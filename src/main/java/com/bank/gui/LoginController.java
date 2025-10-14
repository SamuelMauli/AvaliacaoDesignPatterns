package com.bank.gui;

import com.bank.model.User;
import com.bank.service.BankingFacade;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

public class LoginController {

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label messageLabel;

    private BankingFacade bankingFacade;

    public LoginController() {
        this.bankingFacade = new BankingFacade();
    }

    @FXML
    private void handleLoginButtonAction() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Usuário e senha não podem ser vazios.");
            return;
        }

        try {
            Optional<User> user = bankingFacade.loginUser(username, password);
            if (user.isPresent()) {
                messageLabel.setText("Login bem-sucedido!");
                BankingApplication.showDashboardView(user.get());
            } else {
                messageLabel.setText("Usuário ou senha inválidos.");
            }
        } catch (SQLException | IOException e) {
            messageLabel.setText("Erro ao tentar fazer login: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRegisterButtonAction() {
        try {
            BankingApplication.showRegisterView();
        } catch (IOException e) {
            messageLabel.setText("Erro ao abrir tela de registro: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

