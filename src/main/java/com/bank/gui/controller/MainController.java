package com.bank.gui.controller;

import com.bank.account.Account;
import com.bank.account.AccountType;
import com.bank.account.InterestBearing;
import com.bank.account.SavingsAccount;
import com.bank.facade.BankingFacade;
import com.bank.gui.model.AuthenticationService;
import com.bank.gui.model.User;
import com.bank.observer.AuditService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Optional;

/**
 * Controlador principal da aplicação bancária.
 * Esta classe implementa o padrão **Model-View-Controller (MVC)**, gerenciando
 * a interação entre a interface principal e os serviços bancários.
 *
 * <p>Integra a {@code BankingFacade} (que implementa o padrão Facade) com a GUI,
 * demonstrando como os padrões de projeto podem ser combinados para criar
 * uma arquitetura robusta e flexível.
 */
public class MainController {

    // Elementos da interface - Informações do usuário
    @FXML private Label welcomeLabel;
    @FXML private Label userInfoLabel;

    // Elementos da interface - Gestão de contas
    @FXML private TableView<AccountInfo> accountsTable;
    @FXML private TableColumn<AccountInfo, String> accountNumberColumn;
    @FXML private TableColumn<AccountInfo, String> accountTypeColumn;
    @FXML private TableColumn<AccountInfo, String> balanceColumn;

    // Elementos da interface - Operações bancárias
    @FXML private ComboBox<String> operationAccountCombo;
    @FXML private TextField amountField;
    @FXML private TextArea transactionHistoryArea;
    
    // Elementos da interface - Transferências
    @FXML private ComboBox<String> fromAccountCombo;
    @FXML private ComboBox<String> toAccountCombo;
    @FXML private TextField transferAmountField;

    // Elementos da interface - Criação de contas
    @FXML private TextField newAccountNameField;
    @FXML private ComboBox<AccountType> accountTypeCombo;
    @FXML private TextField initialBalanceField;
    @FXML private TextField parameterField;
    @FXML private Label parameterLabel;

    // Serviços
    private BankingFacade bankingFacade;
    private AuthenticationService authService;
    private AuditService auditService;
    private ObservableList<AccountInfo> accountsList;

    // Formatador de moeda
    private NumberFormat currencyFormatter;

    /**
     * Classe interna para representar informações de conta na tabela.
     * Demonstra o uso de classes auxiliares para separar responsabilidades.
     */
    public static class AccountInfo {
        private String accountNumber;
        private String accountType;
        private String balance;

        public AccountInfo(String accountNumber, String accountType, String balance) {
            this.accountNumber = accountNumber;
            this.accountType = accountType;
            this.balance = balance;
        }

        // Getters necessários para o TableView
        public String getAccountNumber() { return accountNumber; }
        public String getAccountType() { return accountType; }
        public String getBalance() { return balance; }
    }

    /**
     * Inicialização do controlador.
     * Configura os serviços, componentes da interface e carrega dados iniciais.
     */
    @FXML
    private void initialize() {
        // Inicializa serviços
        bankingFacade = new BankingFacade();
        authService = AuthenticationService.getInstance();
        auditService = new AuditService();
        
        // Configura formatador de moeda
        currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        
        // Configura a interface
        setupUserInterface();
        setupAccountsTable();
        setupAccountCreation();
        
        // Carrega dados iniciais
        loadUserInfo();
        createDemoAccounts();
        refreshAccountsList();
        refreshTransactionHistory();
    }

    /**
     * Configura os elementos básicos da interface do usuário.
     */
    private void setupUserInterface() {
        User currentUser = authService.getCurrentUser();
        if (currentUser != null) {
            welcomeLabel.setText("Bem-vindo, " + currentUser.getFullName() + "!");
            userInfoLabel.setText("Usuário: " + currentUser.getUsername() + " | Email: " + currentUser.getEmail());
        }
    }

    /**
     * Configura a tabela de contas.
     */
    private void setupAccountsTable() {
        accountsList = FXCollections.observableArrayList();
        accountsTable.setItems(accountsList);
        
        // Configura as colunas da tabela
        accountNumberColumn.setCellValueFactory(new PropertyValueFactory<>("accountNumber"));
        accountTypeColumn.setCellValueFactory(new PropertyValueFactory<>("accountType"));
        balanceColumn.setCellValueFactory(new PropertyValueFactory<>("balance"));
        
        // Configura seleção da tabela para atualizar combo de operações
        accountsTable.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> {
                if (newValue != null) {
                    operationAccountCombo.setValue(newValue.getAccountNumber());
                }
            }
        );
    }

    /**
     * Configura os elementos de criação de conta.
     */
    private void setupAccountCreation() {
        // Popula combo de tipos de conta
        accountTypeCombo.setItems(FXCollections.observableArrayList(AccountType.values()));
        accountTypeCombo.setValue(AccountType.CHECKING);
        
        // Configura listener para atualizar label do parâmetro
        accountTypeCombo.valueProperty().addListener((observable, oldValue, newValue) -> {
            updateParameterLabel(newValue);
        });
        
        updateParameterLabel(AccountType.CHECKING);
    }

    /**
     * Atualiza o label do parâmetro baseado no tipo de conta selecionado.
     */
    private void updateParameterLabel(AccountType accountType) {
        if (accountType == AccountType.CHECKING) {
            parameterLabel.setText("Limite de Cheque Especial:");
            parameterField.setPromptText("Ex: 500.00");
        } else if (accountType == AccountType.SAVINGS) {
            parameterLabel.setText("Taxa de Juros (%):");
            parameterField.setPromptText("Ex: 0.05 (5%)");
        }
    }

    /**
     * Carrega informações do usuário logado.
     */
    private void loadUserInfo() {
        User currentUser = authService.getCurrentUser();
        if (currentUser == null) {
            // Se não há usuário logado, volta para a tela de login
            handleLogout();
        }
    }

    /**
     * Cria contas de demonstração para o usuário atual.
     */
    private void createDemoAccounts() {
        User currentUser = authService.getCurrentUser();
        if (currentUser != null) {
            // Cria uma conta corrente de demonstração
            String checkingAccount = bankingFacade.createAccount(
                AccountType.CHECKING, 
                currentUser.getFullName(), 
                1000.0, 
                500.0
            );
            
            // Cria uma conta poupança de demonstração
            String savingsAccount = bankingFacade.createAccount(
                AccountType.SAVINGS, 
                currentUser.getFullName(), 
                2000.0, 
                0.05
            );
            
            // Adiciona observador de auditoria às contas
            Account checking = bankingFacade.getAccount(checkingAccount);
            Account savings = bankingFacade.getAccount(savingsAccount);
            
            if (checking != null) checking.addObserver(auditService);
            if (savings != null) savings.addObserver(auditService);
        }
    }

    /**
     * Atualiza a lista de contas na tabela.
     */
    private void refreshAccountsList() {
        accountsList.clear();
        operationAccountCombo.getItems().clear();
        
        // Limpa os combos de transferência se existirem
        if (fromAccountCombo != null) fromAccountCombo.getItems().clear();
        if (toAccountCombo != null) toAccountCombo.getItems().clear();
        
        // Em um sistema real, isso viria de um serviço que filtra contas por usuário
        // Para demonstração, vamos mostrar todas as contas
        for (String accountNumber : getAllAccountNumbers()) {
            Account account = bankingFacade.getAccount(accountNumber);
            if (account != null) {
                String formattedBalance = currencyFormatter.format(account.getBalance());
                accountsList.add(new AccountInfo(
                    accountNumber,
                    account.getAccountType(),
                    formattedBalance
                ));
                operationAccountCombo.getItems().add(accountNumber);
                
                // Adiciona aos combos de transferência se existirem
                if (fromAccountCombo != null) fromAccountCombo.getItems().add(accountNumber);
                if (toAccountCombo != null) toAccountCombo.getItems().add(accountNumber);
            }
        }
    }

    /**
     * Método auxiliar para obter todos os números de conta.
     * Agora utiliza o método público da BankingFacade.
     */
    private java.util.List<String> getAllAccountNumbers() {
        return bankingFacade.getAllAccountNumbers();
    }

    /**
     * Manipula a criação de uma nova conta.
     */
    @FXML
    private void handleCreateAccount(ActionEvent event) {
        try {
            String customerName = newAccountNameField.getText().trim();
            AccountType accountType = accountTypeCombo.getValue();
            double initialBalance = Double.parseDouble(initialBalanceField.getText());
            double parameter = Double.parseDouble(parameterField.getText());

            if (customerName.isEmpty()) {
                showAlert("Erro", "Por favor, preencha o nome do titular da conta.", Alert.AlertType.ERROR);
                return;
            }

            if (initialBalance < 0) {
                showAlert("Erro", "O saldo inicial não pode ser negativo.", Alert.AlertType.ERROR);
                return;
            }

            String accountNumber = bankingFacade.createAccount(accountType, customerName, initialBalance, parameter);
            Account newAccount = bankingFacade.getAccount(accountNumber);
            if (newAccount != null) {
                newAccount.addObserver(auditService);
            }

            showAlert("Sucesso", "Conta criada com sucesso!\nNúmero da conta: " + accountNumber, Alert.AlertType.INFORMATION);
            
            // Limpa os campos
            newAccountNameField.clear();
            initialBalanceField.clear();
            parameterField.clear();
            
            // Atualiza a lista de contas
            refreshAccountsList();
            refreshTransactionHistory();

        } catch (NumberFormatException e) {
            showAlert("Erro", "Por favor, insira valores numéricos válidos.", Alert.AlertType.ERROR);
        }
    }

    /**
     * Manipula operação de depósito.
     */
    @FXML
    private void handleDeposit(ActionEvent event) {
        performTransaction("deposit");
    }

    /**
     * Manipula operação de saque.
     */
    @FXML
    private void handleWithdraw(ActionEvent event) {
        performTransaction("withdraw");
    }

    /**
     * Realiza uma transação (depósito ou saque).
     */
    private void performTransaction(String operation) {
        try {
            String accountNumber = operationAccountCombo.getValue();
            double amount = Double.parseDouble(amountField.getText());

            if (accountNumber == null || accountNumber.isEmpty()) {
                showAlert("Erro", "Por favor, selecione uma conta.", Alert.AlertType.ERROR);
                return;
            }

            if (amount <= 0) {
                showAlert("Erro", "O valor deve ser positivo.", Alert.AlertType.ERROR);
                return;
            }

            if ("deposit".equals(operation)) {
                bankingFacade.deposit(accountNumber, amount);
                showAlert("Sucesso", "Depósito realizado com sucesso!", Alert.AlertType.INFORMATION);
            } else if ("withdraw".equals(operation)) {
                bankingFacade.withdraw(accountNumber, amount);
                showAlert("Sucesso", "Saque realizado com sucesso!", Alert.AlertType.INFORMATION);
            }

            amountField.clear();
            refreshAccountsList();
            refreshTransactionHistory();

        } catch (NumberFormatException e) {
            showAlert("Erro", "Por favor, insira um valor numérico válido.", Alert.AlertType.ERROR);
        }
    }

    /**
     * Manipula o cálculo de juros para contas poupança.
     */
    @FXML
    private void handleCalculateInterest(ActionEvent event) {
        String accountNumber = operationAccountCombo.getValue();
        if (accountNumber == null || accountNumber.isEmpty()) {
            showAlert("Erro", "Por favor, selecione uma conta.", Alert.AlertType.ERROR);
            return;
        }

        Account account = bankingFacade.getAccount(accountNumber);
        if (account instanceof InterestBearing) {
            ((InterestBearing) account).calculateInterest();
            showAlert("Sucesso", "Juros calculados e aplicados com sucesso!", Alert.AlertType.INFORMATION);
            refreshAccountsList();
            refreshTransactionHistory();
        } else {
            showAlert("Erro", "Esta conta não suporta cálculo de juros.", Alert.AlertType.ERROR);
        }
    }

    /**
     * Manipula operação de transferência entre contas.
     */
    @FXML
    private void handleTransfer(ActionEvent event) {
        try {
            String fromAccount = fromAccountCombo.getValue();
            String toAccount = toAccountCombo.getValue();
            double amount = Double.parseDouble(transferAmountField.getText());

            if (fromAccount == null || fromAccount.isEmpty()) {
                showAlert("Erro", "Por favor, selecione a conta de origem.", Alert.AlertType.ERROR);
                return;
            }

            if (toAccount == null || toAccount.isEmpty()) {
                showAlert("Erro", "Por favor, selecione a conta de destino.", Alert.AlertType.ERROR);
                return;
            }

            if (fromAccount.equals(toAccount)) {
                showAlert("Erro", "A conta de origem e destino devem ser diferentes.", Alert.AlertType.ERROR);
                return;
            }

            if (amount <= 0) {
                showAlert("Erro", "O valor da transferência deve ser positivo.", Alert.AlertType.ERROR);
                return;
            }

            boolean success = bankingFacade.transfer(fromAccount, toAccount, amount);
            if (success) {
                showAlert("Sucesso", "Transferência realizada com sucesso!", Alert.AlertType.INFORMATION);
                transferAmountField.clear();
                refreshAccountsList();
                refreshTransactionHistory();
            } else {
                showAlert("Erro", "Falha na transferência. Verifique o saldo e tente novamente.", Alert.AlertType.ERROR);
            }

        } catch (NumberFormatException e) {
            showAlert("Erro", "Por favor, insira um valor numérico válido.", Alert.AlertType.ERROR);
        }
    }

    /**
     * Atualiza o histórico de transações.
     * Em um sistema real, isso viria de um serviço de histórico.
     */
    private void refreshTransactionHistory() {
        // Para demonstração, vamos mostrar informações básicas das contas
        StringBuilder history = new StringBuilder();
        history.append("=== HISTÓRICO DE TRANSAÇÕES ===\n\n");
        
        for (String accountNumber : getAllAccountNumbers()) {
            Account account = bankingFacade.getAccount(accountNumber);
            if (account != null) {
                history.append("Conta: ").append(accountNumber).append("\n");
                history.append("Tipo: ").append(account.getAccountType()).append("\n");
                history.append("Titular: ").append(account.getCustomerName()).append("\n");
                history.append("Saldo: ").append(currencyFormatter.format(account.getBalance())).append("\n");
                history.append("---\n");
            }
        }
        
        transactionHistoryArea.setText(history.toString());
    }

    /**
     * Manipula o logout do usuário.
     */
    @FXML
    private void handleLogout() {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmar Logout");
        confirmation.setHeaderText("Deseja realmente sair do sistema?");
        confirmation.setContentText("Você será redirecionado para a tela de login.");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            authService.logout();
            
            try {
                // Carrega a tela de login
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
                Scene loginScene = new Scene(loader.load(), 800, 600);
                
                // Obtém a janela atual e substitui a cena
                Stage currentStage = (Stage) welcomeLabel.getScene().getWindow();
                currentStage.setTitle("Sistema Bancário - Login");
                currentStage.setScene(loginScene);
                currentStage.setResizable(false);
                currentStage.centerOnScreen();
                
            } catch (IOException e) {
                showAlert("Erro", "Erro ao carregar a tela de login: " + e.getMessage(), Alert.AlertType.ERROR);
                e.printStackTrace();
            }
        }
    }

    /**
     * Exibe um alerta para o usuário.
     */
    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
