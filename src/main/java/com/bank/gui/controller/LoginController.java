package com.bank.gui.controller;

import com.bank.gui.model.AuthenticationService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Controlador para a tela de login da aplicação bancária.
 * Esta classe implementa o padrão **Model-View-Controller (MVC)**, atuando como
 * o Controller que gerencia a interação entre a View (login.fxml) e o Model
 * (AuthenticationService).
 *
 * <p>Segue o **Princípio da Responsabilidade Única (SRP)**, sendo responsável
 * exclusivamente por gerenciar a lógica da tela de login.
 */
public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Label statusLabel;

    @FXML
    private VBox loginContainer;

    private AuthenticationService authService;

    /**
     * Método de inicialização chamado automaticamente pelo JavaFX após o carregamento do FXML.
     * Configura os componentes da interface e obtém a instância do serviço de autenticação.
     */
    @FXML
    private void initialize() {
        authService = AuthenticationService.getInstance();
        
        // Configura o botão de login como padrão (ativado com Enter)
        loginButton.setDefaultButton(true);
        
        // Limpa a mensagem de status inicialmente
        statusLabel.setText("");
        
        // Adiciona listener para limpar mensagens de erro quando o usuário digita
        usernameField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!statusLabel.getText().isEmpty()) {
                statusLabel.setText("");
            }
        });
        
        passwordField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!statusLabel.getText().isEmpty()) {
                statusLabel.setText("");
            }
        });
    }

    /**
     * Manipula o evento de clique no botão de login.
     * Valida as credenciais e redireciona para a tela principal se a autenticação for bem-sucedida.
     *
     * @param event O evento de ação do botão.
     */
    @FXML
    private void handleLogin(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        // Validação básica dos campos
        if (username.isEmpty() || password.isEmpty()) {
            showError("Por favor, preencha todos os campos.");
            return;
        }

        // Tenta autenticar o usuário
        if (authService.authenticate(username, password)) {
            showSuccess("Login realizado com sucesso!");
            
            // Redireciona para a tela principal após um breve delay
            new Thread(() -> {
                try {
                    Thread.sleep(1000); // Delay de 1 segundo para mostrar a mensagem de sucesso
                    javafx.application.Platform.runLater(this::openMainWindow);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }).start();
            
        } else {
            showError("Credenciais inválidas. Tente novamente.");
            passwordField.clear();
            passwordField.requestFocus();
        }
    }

    /**
     * Abre a janela principal da aplicação após login bem-sucedido.
     */
    private void openMainWindow() {
        try {
            // Carrega a tela principal
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
            Scene mainScene = new Scene(loader.load(), 1200, 800);
            
            // Obtém a janela atual e substitui a cena
            Stage currentStage = (Stage) loginButton.getScene().getWindow();
            currentStage.setTitle("Sistema Bancário - " + authService.getCurrentUser().getFullName());
            currentStage.setScene(mainScene);
            currentStage.setResizable(true);
            currentStage.centerOnScreen();
            
        } catch (IOException e) {
            showError("Erro ao carregar a tela principal: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Exibe uma mensagem de erro na interface.
     * @param message A mensagem de erro a ser exibida.
     */
    private void showError(String message) {
        statusLabel.setText(message);
        statusLabel.setStyle("-fx-text-fill: #d32f2f; -fx-font-weight: bold;");
    }

    /**
     * Exibe uma mensagem de sucesso na interface.
     * @param message A mensagem de sucesso a ser exibida.
     */
    private void showSuccess(String message) {
        statusLabel.setText(message);
        statusLabel.setStyle("-fx-text-fill: #388e3c; -fx-font-weight: bold;");
    }

    /**
     * Manipula o evento de clique no link "Esqueci minha senha".
     * Em um sistema real, isso abriria uma tela de recuperação de senha.
     */
    @FXML
    private void handleForgotPassword() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Recuperação de Senha");
        alert.setHeaderText("Funcionalidade em Desenvolvimento");
        alert.setContentText("A funcionalidade de recuperação de senha será implementada em uma versão futura.\n\n" +
                "Para demonstração, use as seguintes credenciais:\n" +
                "• admin / admin123\n" +
                "• alice / alice123\n" +
                "• bob / bob123\n" +
                "• charlie / charlie123");
        alert.showAndWait();
    }
}
