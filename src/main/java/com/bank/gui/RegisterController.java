package com.bank.gui;

import com.bank.model.User;
import com.bank.service.BankingFacade;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.sql.SQLException;

public class RegisterController {

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField nameField;
    @FXML
    private TextField emailField;
    @FXML
    private Label messageLabel;

    private BankingFacade bankingFacade;

    public RegisterController() {
        this.bankingFacade = new BankingFacade();
    }

    @FXML
    private void handleRegisterButtonAction() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String name = nameField.getText();
        String email = emailField.getText();

        if (username.isEmpty() || password.isEmpty() || name.isEmpty() || email.isEmpty()) {
            messageLabel.setText("Todos os campos são obrigatórios.");
            return;
        }

        try {
            User newUser = bankingFacade.registerUser(username, password, name, email);
            if (newUser != null) {
                messageLabel.setText("Registro bem-sucedido! Faça login agora.");
                // Optionally clear fields or navigate to login
                usernameField.clear();
                passwordField.clear();
                nameField.clear();
                emailField.clear();
                BankingApplication.showLoginView();
            } else {
                messageLabel.setText("Erro ao registrar usuário.");
            }
        } catch (SQLException | IOException e) {
            messageLabel.setText("Erro de registro: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBackToLoginButtonAction() {
        try {
            BankingApplication.showLoginView();
        } catch (IOException e) {
            messageLabel.setText("Erro ao voltar para o login: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

