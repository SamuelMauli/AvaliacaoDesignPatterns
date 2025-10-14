package com.bank.gui;

import com.bank.model.Account;
import com.bank.model.Account.AccountType;
import com.bank.model.Account.InterestStrategyType;
import com.bank.model.AuditLog;
import com.bank.model.Transaction;
import com.bank.model.User;
import com.bank.service.BankingFacade;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class DashboardController {

    @FXML
    private Label welcomeLabel;
    @FXML
    private ListView<Account> accountsListView;
    @FXML
    private Label selectedAccountNumberLabel;
    @FXML
    private Label selectedAccountTypeLabel;
    @FXML
    private Label selectedAccountBalanceLabel;
    @FXML
    private Label selectedAccountInterestStrategyLabel;
    @FXML
    private TextField amountField;
    @FXML
    private Label accountMessageLabel;

    @FXML
    private ListView<Account> fromAccountListView;
    @FXML
    private ListView<Account> toAccountListView;
    @FXML
    private TextField transferAmountField;
    @FXML
    private Label transferMessageLabel;

    @FXML
    private ListView<Account> statementAccountListView;
    @FXML
    private ListView<Transaction> transactionsListView;
    @FXML
    private Label statementMessageLabel;

    @FXML
    private ListView<Account> auditLogAccountListView;
    @FXML
    private ListView<AuditLog> auditLogsListView;
    @FXML
    private Label auditLogMessageLabel;

    @FXML
    private TabPane mainTabPane;

    private User loggedInUser;
    private BankingFacade bankingFacade;
    private ObservableList<Account> userAccounts;

    public DashboardController() {
        this.bankingFacade = new BankingFacade();
        this.userAccounts = FXCollections.observableArrayList();
    }

    public void setLoggedInUser(User user) {
        this.loggedInUser = user;
        welcomeLabel.setText("Bem-vindo, " + loggedInUser.getName() + "!");
        loadUserAccounts();
    }

    @FXML
    public void initialize() {
        accountsListView.setItems(userAccounts);
        fromAccountListView.setItems(userAccounts);
        toAccountListView.setItems(userAccounts);
        statementAccountListView.setItems(userAccounts);
        auditLogAccountListView.setItems(userAccounts);

        accountsListView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                displayAccountDetails(newSelection);
                loadAccountTransactions(newSelection.getId());
                loadAuditLogs(newSelection.getId());
            }
        });

        statementAccountListView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                loadAccountTransactions(newSelection.getId());
            }
        });

        auditLogAccountListView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                loadAuditLogs(newSelection.getId());
            }
        });

        // Listener for tab changes to refresh data
        mainTabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldTab, newTab) -> {
            if (newTab != null) {
                if (newTab.getText().equals("Minhas Contas")) {
                    loadUserAccounts();
                } else if (newTab.getText().equals("Extrato")) {
                    loadUserAccounts(); // Refresh accounts for statement view
                } else if (newTab.getText().equals("Logs de Auditoria")) {
                    loadUserAccounts(); // Refresh accounts for audit log view
                }
            }
        });
    }

    private void loadUserAccounts() {
        if (loggedInUser != null) {
            try {
                List<Account> accounts = bankingFacade.getAccountsByUserId(loggedInUser.getId());
                userAccounts.setAll(accounts);
                if (!accounts.isEmpty()) {
                    accountsListView.getSelectionModel().selectFirst();
                }
            } catch (SQLException e) {
                accountMessageLabel.setText("Erro ao carregar contas: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void displayAccountDetails(Account account) {
        selectedAccountNumberLabel.setText(account.getAccountNumber());
        selectedAccountTypeLabel.setText(account.getAccountType().toString());
        selectedAccountBalanceLabel.setText(account.getBalance().toPlainString());
        selectedAccountInterestStrategyLabel.setText(account.getInterestStrategy() != null ? account.getInterestStrategy().toString() : "N/A");
    }

    private void loadAccountTransactions(UUID accountId) {
        try {
            List<Transaction> transactions = bankingFacade.getAccountTransactions(accountId);
            transactionsListView.setItems(FXCollections.observableArrayList(transactions));
            statementMessageLabel.setText("");
        } catch (SQLException e) {
            statementMessageLabel.setText("Erro ao carregar transações: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadAuditLogs(UUID accountId) {
        try {
            List<AuditLog> auditLogs = bankingFacade.getAuditLogsByAccountId(accountId);
            auditLogsListView.setItems(FXCollections.observableArrayList(auditLogs));
            auditLogMessageLabel.setText("");
        } catch (SQLException e) {
            auditLogMessageLabel.setText("Erro ao carregar logs de auditoria: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCreateCheckingAccount() {
        try {
            bankingFacade.createAccount(loggedInUser.getId(), AccountType.CHECKING, null);
            accountMessageLabel.setText("Conta corrente criada com sucesso!");
            loadUserAccounts();
        } catch (SQLException e) {
            accountMessageLabel.setText("Erro ao criar conta corrente: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCreateSavingsAccount() {
        try {
            bankingFacade.createAccount(loggedInUser.getId(), AccountType.SAVINGS, InterestStrategyType.SIMPLE);
            accountMessageLabel.setText("Conta poupança criada com sucesso!");
            loadUserAccounts();
        } catch (SQLException e) {
            accountMessageLabel.setText("Erro ao criar conta poupança: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDeposit() {
        Account selectedAccount = accountsListView.getSelectionModel().getSelectedItem();
        if (selectedAccount == null) {
            accountMessageLabel.setText("Selecione uma conta para depositar.");
            return;
        }
        try {
            BigDecimal amount = new BigDecimal(amountField.getText());
            bankingFacade.deposit(selectedAccount.getId(), amount);
            accountMessageLabel.setText("Depósito realizado com sucesso!");
            amountField.clear();
            loadUserAccounts();
        } catch (NumberFormatException e) {
            accountMessageLabel.setText("Valor inválido para depósito.");
        } catch (SQLException | IllegalArgumentException e) {
            accountMessageLabel.setText("Erro ao depositar: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleWithdraw() {
        Account selectedAccount = accountsListView.getSelectionModel().getSelectedItem();
        if (selectedAccount == null) {
            accountMessageLabel.setText("Selecione uma conta para sacar.");
            return;
        }
        try {
            BigDecimal amount = new BigDecimal(amountField.getText());
            bankingFacade.withdraw(selectedAccount.getId(), amount);
            accountMessageLabel.setText("Saque realizado com sucesso!");
            amountField.clear();
            loadUserAccounts();
        } catch (NumberFormatException e) {
            accountMessageLabel.setText("Valor inválido para saque.");
        } catch (SQLException | IllegalArgumentException e) {
            accountMessageLabel.setText("Erro ao sacar: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleTransfer() {
        Account fromAccount = fromAccountListView.getSelectionModel().getSelectedItem();
        Account toAccount = toAccountListView.getSelectionModel().getSelectedItem();

        if (fromAccount == null || toAccount == null) {
            transferMessageLabel.setText("Selecione as contas de origem e destino.");
            return;
        }

        try {
            BigDecimal amount = new BigDecimal(transferAmountField.getText());
            bankingFacade.transfer(fromAccount.getId(), toAccount.getId(), amount);
            transferMessageLabel.setText("Transferência realizada com sucesso!");
            transferAmountField.clear();
            loadUserAccounts();
        } catch (NumberFormatException e) {
            transferMessageLabel.setText("Valor inválido para transferência.");
        } catch (SQLException | IllegalArgumentException e) {
            transferMessageLabel.setText("Erro ao transferir: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleApplyInterest() {
        Account selectedAccount = accountsListView.getSelectionModel().getSelectedItem();
        if (selectedAccount == null) {
            accountMessageLabel.setText("Selecione uma conta para aplicar juros.");
            return;
        }
        try {
            bankingFacade.applyInterest(selectedAccount.getId());
            accountMessageLabel.setText("Juros aplicados com sucesso!");
            loadUserAccounts();
        } catch (SQLException | IllegalStateException | IllegalArgumentException e) {
            accountMessageLabel.setText("Erro ao aplicar juros: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogoutButtonAction() {
        try {
            BankingApplication.showLoginView();
        } catch (IOException e) {
            System.err.println("Erro ao retornar para a tela de login: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

