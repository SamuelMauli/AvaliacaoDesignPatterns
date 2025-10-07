package com.bank.gui.controller;

import com.bank.account.Account;
import com.bank.account.AccountType;
import com.bank.account.CheckingAccount;
import com.bank.account.SavingsAccount;
import com.bank.facade.BankingFacade;
import com.bank.gui.model.AuthenticationService;
import com.bank.gui.model.User;
import com.bank.gui.util.UIUtils;
import com.bank.gui.util.ValidationUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controlador para a janela principal da aplicação bancária.
 * Esta classe gerencia a interação do usuário com as funcionalidades bancárias
 * após o login, como criação de contas, depósitos, saques, transferências e
 * visualização de histórico.
 *
 * <p>Implementa o padrão **Model-View-Controller (MVC)**, atuando como o Controller
 * que coordena a View (main.fxml) com o Model (BankingFacade, AuthenticationService).
 *
 * <p>Demonstra o **Princípio da Responsabilidade Única (SRP)** ao focar na lógica
 * de apresentação e interação da janela principal, delegando as operações de negócio
 * para a {@code BankingFacade}.
 */
public class MainController {

    // --- Componentes FXML para a criação de contas ---
    @FXML
    private TextField customerNameField;
    @FXML
    private ComboBox<AccountType> accountTypeCombo;
    @FXML
    private TextField initialBalanceField;
    @FXML
    private TextField overdraftLimitField;
    @FXML
    private TextField interestRateField;
    @FXML
    private Label createAccountStatusLabel;

    // --- Componentes FXML para operações de depósito/saque ---
    @FXML
    private ComboBox<String> operationAccountCombo;
    @FXML
    private TextField amountField;
    @FXML
    private Label operationStatusLabel;

    // --- Componentes FXML para transferências ---
    @FXML
    private ComboBox<String> fromAccountCombo;
    @FXML
    private ComboBox<String> toAccountCombo;
    @FXML
    private TextField transferAmountField;
    @FXML
    private Label transferStatusLabel;

    // --- Componentes FXML para a lista de contas ---
    @FXML
    private TableView<AccountDisplay> accountsTable;
    @FXML
    private TableColumn<AccountDisplay, String> colAccountNumber;
    @FXML
    private TableColumn<AccountDisplay, String> colCustomerName;
    @FXML
    private TableColumn<AccountDisplay, String> colAccountType;
    @FXML
    private TableColumn<AccountDisplay, String> colBalance;

    // --- Componentes FXML para o histórico de transações ---
    @FXML
    private TableView<TransactionDisplay> transactionHistoryTable;
    @FXML
    private TableColumn<TransactionDisplay, String> colTimestamp;
    @FXML
    private TableColumn<TransactionDisplay, String> colAccount;
    @FXML
    private TableColumn<TransactionDisplay, String> colType;
    @FXML
    private TableColumn<TransactionDisplay, String> colAmount;
    @FXML
    private TableColumn<TransactionDisplay, String> colNewBalance;

    // --- Outros componentes FXML ---
    @FXML
    private Label welcomeLabel;
    @FXML
    private AnchorPane rootPane;

    // --- Serviços e Facades ---
    private BankingFacade bankingFacade = new BankingFacade();
    private AuthenticationService authenticationService;

    /**
     * Classe interna para exibir contas na TableView.
     * Utiliza o padrão **Adapter** implicitamente, adaptando o objeto {@code Account}
     * para ser exibido na tabela JavaFX.
     */
    public static class AccountDisplay {
        private String accountNumber;
        private String customerName;
        private String accountType;
        private String balance;

        public AccountDisplay(String accountNumber, String customerName, String accountType, double balance) {
            this.accountNumber = accountNumber;
            this.customerName = customerName;
            this.accountType = accountType;
            this.balance = UIUtils.formatCurrency(balance);
        }

        // Getters para PropertyValueFactory
        public String getAccountNumber() { return accountNumber; }
        public String getCustomerName() { return customerName; }
        public String getAccountType() { return accountType; }
        public String getBalance() { return balance; }
    }

    /**
     * Classe interna para exibir histórico de transações na TableView.
     * Adapta os dados de transação para exibição na tabela.
     */
    public static class TransactionDisplay {
        private String timestamp;
        private String accountNumber;
        private String type;
        private String amount;
        private String newBalance;

        public TransactionDisplay(String timestamp, String accountNumber, String type, double amount, double newBalance) {
            this.timestamp = timestamp;
            this.accountNumber = UIUtils.formatAccountNumber(accountNumber);
            this.type = type;
            this.amount = UIUtils.formatCurrency(amount);
            this.newBalance = UIUtils.formatCurrency(newBalance);
        }

        // Getters para PropertyValueFactory
        public String getTimestamp() { return timestamp; }
        public String getAccountNumber() { return accountNumber; }
        public String getType() { return type; }
        public String getAmount() { return amount; }
        public String getNewBalance() { return newBalance; }
    }

    /**
     * Define o serviço de autenticação para o controlador.
     * Utilizado para passar o usuário logado da tela de login.
     * @param authenticationService O serviço de autenticação.
     */
    public void setAuthenticationService(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    /**
     * Método de inicialização chamado automaticamente pelo JavaFX após o carregamento do FXML.
     * Configura os componentes da interface, como ComboBoxes e TableViews.
     */
    @FXML
    private void initialize() {
        // Configura ComboBox de tipos de conta
        accountTypeCombo.setItems(FXCollections.observableArrayList(AccountType.values()));
        accountTypeCombo.getSelectionModel().selectedItemProperty().addListener((obs, oldType, newType) -> {
            overdraftLimitField.setDisable(true);
            interestRateField.setDisable(true);
            if (newType == AccountType.CHECKING) {
                overdraftLimitField.setDisable(false);
                interestRateField.setText(""); // Limpa campo de juros para conta corrente
            } else if (newType == AccountType.SAVINGS) {
                interestRateField.setDisable(false);
                overdraftLimitField.setText(""); // Limpa campo de cheque especial para conta poupança
            }
        });
        accountTypeCombo.getSelectionModel().selectFirst(); // Seleciona o primeiro tipo por padrão

        // Configura as colunas da tabela de contas
        colAccountNumber.setCellValueFactory(new PropertyValueFactory<>("accountNumber"));
        colCustomerName.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        colAccountType.setCellValueFactory(new PropertyValueFactory<>("accountType"));
        colBalance.setCellValueFactory(new PropertyValueFactory<>("balance"));

        // Configura as colunas da tabela de histórico de transações
        colTimestamp.setCellValueFactory(new PropertyValueFactory<>("timestamp"));
        colAccount.setCellValueFactory(new PropertyValueFactory<>("accountNumber"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        colNewBalance.setCellValueFactory(new PropertyValueFactory<>("newBalance"));

        // Adiciona listeners para limpar mensagens de status ao digitar
        customerNameField.textProperty().addListener((obs, oldVal, newVal) -> createAccountStatusLabel.setText(""));
        initialBalanceField.textProperty().addListener((obs, oldVal, newVal) -> createAccountStatusLabel.setText(""));
        overdraftLimitField.textProperty().addListener((obs, oldVal, newVal) -> createAccountStatusLabel.setText(""));
        interestRateField.textProperty().addListener((obs, oldVal, newVal) -> createAccountStatusLabel.setText(""));

        amountField.textProperty().addListener((obs, oldVal, newVal) -> operationStatusLabel.setText(""));
        transferAmountField.textProperty().addListener((obs, oldVal, newVal) -> transferStatusLabel.setText(""));
    }

    /**
     * Inicializa os dados específicos do usuário logado após o login.
     * Deve ser chamado após {@code setAuthenticationService}.
     */
    public void initializeData() {
        if (authenticationService != null && authenticationService.isUserLoggedIn()) {
            User currentUser = authenticationService.getCurrentUser();
            welcomeLabel.setText("Bem-vindo(a), " + currentUser.getFullName() + "!");
        }
        refreshAccountsList();
        refreshTransactionHistory();
    }

    /**
     * Atualiza a lista de contas exibida na tabela e nos ComboBoxes.
     */
    private void refreshAccountsList() {
        ObservableList<AccountDisplay> accountDisplays = FXCollections.observableArrayList();
        ObservableList<String> accountNumbers = FXCollections.observableArrayList();

        for (Map.Entry<String, Account> entry : bankingFacade.getAllAccounts().entrySet()) {
            Account acc = entry.getValue();
            accountDisplays.add(new AccountDisplay(
                    acc.getAccountNumber(),
                    acc.getCustomerName(),
                    acc.getAccountType(),
                    acc.getBalance()
            ));
            accountNumbers.add(acc.getAccountNumber());
        }
        accountsTable.setItems(accountDisplays);
        operationAccountCombo.setItems(accountNumbers);
        fromAccountCombo.setItems(accountNumbers);
        toAccountCombo.setItems(accountNumbers);
    }

    /**
     * Atualiza o histórico de transações exibido na tabela.
     */
    private void refreshTransactionHistory() {
        ObservableList<TransactionDisplay> transactionDisplays = FXCollections.observableArrayList();
        List<String> logs = bankingFacade.getTransactionHistory();

        // Formato esperado: [TIMESTAMP] [ACCOUNT_NUMBER] [TYPE] [AMOUNT] [NEW_BALANCE]
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        for (String log : logs) {
            try {
                String[] parts = log.split(" ", 5); // Divide em 5 partes: [TIMESTAMP] [ACCOUNT_NUMBER] [TYPE] [AMOUNT] [NEW_BALANCE]
                if (parts.length >= 5) {
                    String timestampStr = parts[0].substring(1, parts[0].length() - 1); // Remove colchetes
                    String accountNumber = parts[1];
                    String type = parts[2];
                    double amount = Double.parseDouble(parts[3]);
                    double newBalance = Double.parseDouble(parts[4]);

                    transactionDisplays.add(new TransactionDisplay(
                            timestampStr,
                            accountNumber,
                            type,
                            amount,
                            newBalance
                    ));
                }
            } catch (Exception e) {
                System.err.println("Erro ao parsear log: " + log + " - " + e.getMessage());
            }
        }
        transactionHistoryTable.setItems(transactionDisplays);
    }

    /**
     * Manipula a criação de uma nova conta.
     * Realiza validações de entrada e utiliza a BankingFacade para criar a conta.
     * @param event O evento de ação.
     */
    @FXML
    private void handleCreateAccount(ActionEvent event) {
        // Limpa estilos de validação anteriores
        UIUtils.clearValidationStyles(customerNameField, initialBalanceField, overdraftLimitField, interestRateField);
        createAccountStatusLabel.setText("");

        String customerName = customerNameField.getText().trim();
        AccountType accountType = accountTypeCombo.getValue();
        String initialBalanceText = initialBalanceField.getText().trim();
        String overdraftLimitText = overdraftLimitField.getText().trim();
        String interestRateText = interestRateField.getText().trim();

        boolean isValid = true;
        StringBuilder errorMessage = new StringBuilder();

        // Validação do nome do cliente
        if (!ValidationUtils.isNotEmpty(customerName)) {
            UIUtils.applyErrorStyle(customerNameField);
            errorMessage.append("Nome do cliente é obrigatório. ");
            isValid = false;
        }

        // Validação do saldo inicial
        if (!ValidationUtils.isValidPositiveNumber(initialBalanceText)) {
            UIUtils.applyErrorStyle(initialBalanceField);
            errorMessage.append("Saldo inicial deve ser um número positivo. ");
            isValid = false;
        }

        // Validações específicas por tipo de conta
        if (accountType == AccountType.CHECKING) {
            if (!ValidationUtils.isValidNonNegativeNumber(overdraftLimitText)) {
                UIUtils.applyErrorStyle(overdraftLimitField);
                errorMessage.append("Limite de cheque especial deve ser um número não negativo. ");
                isValid = false;
            }
        } else if (accountType == AccountType.SAVINGS) {
            if (!ValidationUtils.isValidPositiveNumber(interestRateText)) {
                UIUtils.applyErrorStyle(interestRateField);
                errorMessage.append("Taxa de juros deve ser um número positivo. ");
                isValid = false;
            }
        }

        if (!isValid) {
            UIUtils.showErrorAlert("Erro de Criação de Conta", errorMessage.toString().trim());
            return;
        }

        try {
            double initialBalance = Double.parseDouble(initialBalanceText);
            String newAccountNumber;

            if (accountType == AccountType.CHECKING) {
                double overdraftLimit = Double.parseDouble(overdraftLimitText);
                newAccountNumber = bankingFacade.createAccount(accountType, customerName, initialBalance, overdraftLimit);
            } else {
                double interestRate = Double.parseDouble(interestRateText);
                newAccountNumber = bankingFacade.createAccount(accountType, customerName, initialBalance, interestRate);
            }

            if (newAccountNumber != null) {
                UIUtils.showSuccessAlert("Sucesso", "Conta " + UIUtils.formatAccountNumber(newAccountNumber) + " criada com sucesso!");
                clearCreateAccountFields();
                refreshAccountsList();
                refreshTransactionHistory();
            } else {
                UIUtils.showErrorAlert("Erro", "Falha ao criar conta.");
            }

        } catch (NumberFormatException e) {
            UIUtils.showErrorAlert("Erro", "Valores numéricos inválidos. Verifique o saldo, limite ou taxa.");
        } catch (Exception e) {
            UIUtils.showErrorAlert("Erro", "Ocorreu um erro inesperado: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Limpa os campos do formulário de criação de conta.
     */
    private void clearCreateAccountFields() {
        customerNameField.clear();
        initialBalanceField.clear();
        overdraftLimitField.clear();
        interestRateField.clear();
        accountTypeCombo.getSelectionModel().selectFirst();
        UIUtils.clearValidationStyles(customerNameField, initialBalanceField, overdraftLimitField, interestRateField);
        createAccountStatusLabel.setText("");
    }

    /**
     * Manipula operação de depósito ou saque.
     * @param operation O tipo de operação ("deposit" ou "withdraw").
     */
    private void performTransaction(String operation) {
        // Limpa estilos anteriores
        UIUtils.clearValidationStyles(amountField);
        operationStatusLabel.setText("");

        String accountNumber = operationAccountCombo.getValue();
        String amountText = amountField.getText().trim();

        // Validações
        if (accountNumber == null || accountNumber.isEmpty()) {
            UIUtils.showErrorAlert("Erro", "Por favor, selecione uma conta.");
            return;
        }

        if (!ValidationUtils.isValidPositiveNumber(amountText)) {
            UIUtils.applyErrorStyle(amountField);
            UIUtils.showErrorAlert("Erro", "O valor deve ser um número positivo.");
            return;
        }

        try {
            double amount = Double.parseDouble(amountText);
            
            // Validações específicas para saque
            if ("withdraw".equals(operation)) {
                Account account = bankingFacade.getAccount(accountNumber);
                if (account != null) {
                    double availableBalance = account.getBalance();
                    if (account instanceof CheckingAccount) {
                        CheckingAccount checking = (CheckingAccount) account;
                        availableBalance += checking.getOverdraftLimit();
                    }
                    
                    if (amount > availableBalance) {
                        UIUtils.applyErrorStyle(amountField);
                        UIUtils.showErrorAlert("Saldo Insuficiente", 
                            "Saldo disponível: " + UIUtils.formatCurrency(availableBalance));
                        return;
                    }
                }
            }
            
            // Confirma a operação
            String operationName = "deposit".equals(operation) ? "depósito" : "saque";
            String preposition = "deposit".equals(operation) ? "na" : "da";
            
            if (UIUtils.showConfirmationAlert("Confirmar " + UIUtils.capitalizeWords(operationName), 
                "Confirma o " + operationName + " de " + UIUtils.formatCurrency(amount) + 
                " " + preposition + " conta " + UIUtils.formatAccountNumber(accountNumber) + "?")) {
                
                UIUtils.applySuccessStyle(amountField);
                
                if ("deposit".equals(operation)) {
                    bankingFacade.deposit(accountNumber, amount);
                } else if ("withdraw".equals(operation)) {
                    bankingFacade.withdraw(accountNumber, amount);
                }
                
                UIUtils.showSuccessAlert("Sucesso", 
                    UIUtils.capitalizeWords(operationName) + " de " + UIUtils.formatCurrency(amount) + " realizado com sucesso!");
                
                amountField.clear();
                UIUtils.clearValidationStyles(amountField);
                refreshAccountsList();
                refreshTransactionHistory();
            }

        } catch (NumberFormatException e) {
            UIUtils.applyErrorStyle(amountField);
            UIUtils.showErrorAlert("Erro", "Erro ao processar o valor inserido.");
        } catch (Exception e) {
            UIUtils.showErrorAlert("Erro", "Ocorreu um erro inesperado: " + e.getMessage());
            e.printStackTrace();
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
     * Manipula o cálculo de juros para contas poupança.
     */
    @FXML
    private void handleCalculateInterest(ActionEvent event) {
        String accountNumber = operationAccountCombo.getValue();
        operationStatusLabel.setText("");

        if (accountNumber == null || accountNumber.isEmpty()) {
            UIUtils.showErrorAlert("Erro", "Por favor, selecione uma conta para calcular juros.");
            return;
        }

        Account account = bankingFacade.getAccount(accountNumber);
        if (account instanceof SavingsAccount) {
            if (UIUtils.showConfirmationAlert("Confirmar Cálculo de Juros", 
                "Confirma o cálculo de juros para a conta " + UIUtils.formatAccountNumber(accountNumber) + "?")) {
                
                ((SavingsAccount) account).calculateInterest();
                UIUtils.showSuccessAlert("Sucesso", "Juros calculados para a conta " + UIUtils.formatAccountNumber(accountNumber) + ".");
                refreshAccountsList();
                refreshTransactionHistory();
            }
        } else {
            UIUtils.showErrorAlert("Erro", "Esta conta não suporta cálculo de juros.");
        }
    }

    /**
     * Manipula operação de transferência entre contas.
     */
    @FXML
    private void handleTransfer(ActionEvent event) {
        // Limpa estilos anteriores
        UIUtils.clearValidationStyles(transferAmountField);
        transferStatusLabel.setText("");

        String fromAccount = fromAccountCombo.getValue();
        String toAccount = toAccountCombo.getValue();
        String amountText = transferAmountField.getText().trim();

        // Validações
        if (fromAccount == null || fromAccount.isEmpty()) {
            UIUtils.showErrorAlert("Erro", "Por favor, selecione a conta de origem.");
            return;
        }

        if (toAccount == null || toAccount.isEmpty()) {
            UIUtils.showErrorAlert("Erro", "Por favor, selecione a conta de destino.");
            return;
        }

        if (fromAccount.equals(toAccount)) {
            UIUtils.showErrorAlert("Erro", "A conta de origem e destino devem ser diferentes.");
            return;
        }

        if (!ValidationUtils.isValidPositiveNumber(amountText)) {
            UIUtils.applyErrorStyle(transferAmountField);
            UIUtils.showErrorAlert("Erro", "O valor da transferência deve ser um número positivo.");
            return;
        }

        try {
            double amount = Double.parseDouble(amountText);
            
            // Verifica saldo disponível na conta de origem
            Account sourceAccount = bankingFacade.getAccount(fromAccount);
            if (sourceAccount != null) {
                double availableBalance = sourceAccount.getBalance();
                if (sourceAccount instanceof CheckingAccount) {
                    CheckingAccount checking = (CheckingAccount) sourceAccount;
                    availableBalance += checking.getOverdraftLimit();
                }
                
                if (amount > availableBalance) {
                    UIUtils.applyErrorStyle(transferAmountField);
                    UIUtils.showErrorAlert("Saldo Insuficiente", 
                        "Saldo disponível na conta de origem: " + UIUtils.formatCurrency(availableBalance));
                    return;
                }
            }
            
            // Confirma a operação
            if (UIUtils.showConfirmationAlert("Confirmar Transferência", 
                "Confirma a transferência de " + UIUtils.formatCurrency(amount) + 
                "\nDe: " + UIUtils.formatAccountNumber(fromAccount) + 
                "\nPara: " + UIUtils.formatAccountNumber(toAccount) + "?")) {
                
                UIUtils.applySuccessStyle(transferAmountField);
                
                boolean success = bankingFacade.transfer(fromAccount, toAccount, amount);
                if (success) {
                    UIUtils.showSuccessAlert("Sucesso", 
                        "Transferência de " + UIUtils.formatCurrency(amount) + " realizada com sucesso!");
                    
                    transferAmountField.clear();
                    UIUtils.clearValidationStyles(transferAmountField);
                    refreshAccountsList();
                    refreshTransactionHistory();
                } else {
                    UIUtils.applyErrorStyle(transferAmountField);
                    UIUtils.showErrorAlert("Erro", "Falha na transferência. Verifique o saldo e tente novamente.");
                }
            }

        } catch (NumberFormatException e) {
            UIUtils.applyErrorStyle(transferAmountField);
            UIUtils.showErrorAlert("Erro", "Erro ao processar o valor inserido.");
        } catch (Exception e) {
            UIUtils.showErrorAlert("Erro", "Ocorreu um erro inesperado: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Manipula o evento de logout, retornando para a tela de login.
     * @param event O evento de ação.
     * @throws IOException Se houver um erro ao carregar o arquivo FXML da tela de login.
     */
    @FXML
    private void handleLogout(ActionEvent event) throws IOException {
        if (UIUtils.showConfirmationAlert("Confirmar Logout", "Tem certeza que deseja sair?")) {
            authenticationService.logout();
            Stage stage = (Stage) rootPane.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 800, 600);
            stage.setTitle("Sistema Bancário - Login");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.centerOnScreen();
            stage.show();
        }
    }
}

