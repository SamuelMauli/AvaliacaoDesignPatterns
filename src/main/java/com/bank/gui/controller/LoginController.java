package com.bank.gui.controller;

import com.bank.gui.BankingApplication;
import com.bank.gui.model.AuthenticationService;
import com.bank.gui.util.UIUtils;
import com.bank.gui.util.ValidationUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.io.IOException;

/**
 * Controlador para a tela de login da aplicação bancária.
 * Esta classe implementa o padrão **Model-View-Controller (MVC)**, atuando como
 * o Controller que gerencia a interação entre a View (login.fxml) e o Model
 * (AuthenticationService).
 *
 * <p>Segue o **Princípio da Responsabilidade Única (SRP)**, sendo responsável
 * exclusivamente por gerenciar a lógica da tela de login: validação de entrada,
 * autenticação de credenciais e navegação para a tela principal ou exibição de erros.
 * Ela interage com o {@code AuthenticationService} para realizar a autenticação,
 * e com {@code UIUtils} e {@code ValidationUtils} para feedback visual e validação
 * de forma desacoplada.
 */
public class LoginController {

    /**
     * Campo de texto para o nome de usuário.
     */
    @FXML
    private TextField usernameField;

    /**
     * Campo de senha para a senha do usuário.
     */
    @FXML
    private PasswordField passwordField;

    /**
     * Botão de login que dispara a ação de autenticação.
     */
    @FXML
    private Button loginButton;

    /**
     * Rótulo para exibir mensagens de status (erro ou sucesso) ao usuário.
     */
    @FXML
    private Label statusLabel;

    /**
     * Contêiner principal da tela de login, usado para manipulações de UI se necessário.
     */
    @FXML
    private VBox loginContainer;

    /**
     * Instância do serviço de autenticação, obtida via Singleton para garantir
     * um único ponto de controle de autenticação em toda a aplicação.
     */
    private AuthenticationService authService = AuthenticationService.getInstance();

    /**
     * Método de inicialização chamado automaticamente pelo JavaFX após o carregamento do FXML.
     * Configura os componentes da interface, obtém a instância do serviço de autenticação
     * e define listeners para limpar mensagens de status ao digitar.
     */
    @FXML
    private void initialize() {
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
     * Manipula o evento de login quando o botão é clicado ou Enter é pressionado.
     * Realiza a validação dos campos de entrada, tenta autenticar o usuário
     * usando o {@code AuthenticationService} e, em caso de sucesso, carrega
     * a tela principal da aplicação. Em caso de falha, exibe mensagens de erro.
     * @param event O evento de ação que disparou este método.
     */
    @FXML
    private void handleLogin(ActionEvent event) {
        // Limpa estilos de validação anteriores e mensagens de status
        UIUtils.clearValidationStyles(usernameField, passwordField);
        statusLabel.setText("");

        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        // Validação aprimorada dos campos de entrada usando ValidationUtils
        boolean isValid = true;
        StringBuilder errorMessage = new StringBuilder();

        if (!ValidationUtils.isNotEmpty(username)) {
            UIUtils.applyErrorStyle(usernameField);
            errorMessage.append("Nome de usuário é obrigatório. ");
            isValid = false;
        } else if (!ValidationUtils.isValidUsername(username)) {
            UIUtils.applyErrorStyle(usernameField);
            errorMessage.append("Nome de usuário deve ter 3-20 caracteres (apenas letras, números e _). ");
            isValid = false;
        }

        if (!ValidationUtils.isNotEmpty(password)) {
            UIUtils.applyErrorStyle(passwordField);
            errorMessage.append("Senha é obrigatória. ");
            isValid = false;
        } else if (!ValidationUtils.isValidPassword(password)) {
            UIUtils.applyErrorStyle(passwordField);
            errorMessage.append("Senha deve ter pelo menos 6 caracteres. ");
            isValid = false;
        }

        // Se a validação inicial falhar, exibe o erro e retorna
        if (!isValid) {
            showError(errorMessage.toString().trim());
            return;
        }

        // Aplica estilo de sucesso aos campos válidos antes de tentar a autenticação
        UIUtils.applySuccessStyle(usernameField);
        UIUtils.applySuccessStyle(passwordField);

        // Tenta autenticar o usuário através do AuthenticationService
        if (authService.authenticate(username, password)) {
            showSuccess("Login realizado com sucesso!");
            
            // Redireciona para a tela principal após um breve delay para mostrar a mensagem de sucesso
            new Thread(() -> {
                try {
                    Thread.sleep(1000); // Delay de 1 segundo
                    javafx.application.Platform.runLater(this::openMainWindow);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt(); // Restaura o status de interrupção
                }
            }).start();
            
        } else {
            // Em caso de falha na autenticação, exibe erro e limpa o campo de senha
            UIUtils.applyErrorStyle(usernameField);
            UIUtils.applyErrorStyle(passwordField);
            showError("Credenciais inválidas. Tente novamente.");
            passwordField.clear();
            passwordField.requestFocus(); // Foca no campo de senha para nova tentativa
        }
    }

    /**
     * Abre a janela principal da aplicação bancária após um login bem-sucedido.
     * Este método é chamado em uma thread da UI para garantir que as atualizações
     * da interface sejam feitas corretamente.
     */
    private void openMainWindow() {
        try {
            // Carrega o arquivo FXML da tela principal
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
            Scene mainScene = new Scene(loader.load(), 1200, 800);
            
            // Obtém o Stage atual (janela de login) e o substitui pela nova cena
            Stage currentStage = (Stage) loginButton.getScene().getWindow();
            currentStage.setTitle("Sistema Bancário - " + authService.getCurrentUser().getFullName());
            currentStage.setScene(mainScene);
            currentStage.setResizable(true);
            currentStage.centerOnScreen();

            // Passa o AuthenticationService para o MainController para manter o estado do usuário logado
            MainController mainController = loader.getController();
            mainController.setAuthenticationService(authService);
            mainController.initializeData(); // Inicializa os dados específicos do usuário logado

            currentStage.show();
            
        } catch (IOException e) {
            // Em caso de erro ao carregar a tela principal, exibe uma mensagem de erro
            showError("Erro ao carregar a tela principal: " + e.getMessage());
            e.printStackTrace(); // Imprime o stack trace para depuração
        }
    }

    /**
     * Exibe uma mensagem de erro na interface do usuário através do statusLabel.
     * Aplica estilo de erro e animação de fade-in.
     * @param message A mensagem de erro a ser exibida.
     */
    private void showError(String message) {
        statusLabel.setText(message);
        UIUtils.applyErrorStyle(statusLabel);
        UIUtils.fadeInAnimation(statusLabel);
    }

    /**
     * Exibe uma mensagem de sucesso na interface do usuário através do statusLabel.
     * Aplica estilo de sucesso e animação de fade-in.
     * @param message A mensagem de sucesso a ser exibida.
     */
    private void showSuccess(String message) {
        statusLabel.setText(message);
        UIUtils.applySuccessStyle(statusLabel);
        UIUtils.fadeInAnimation(statusLabel);
    }

    /**
     * Manipula o evento de clique no link "Esqueci minha senha".
     * Para fins de demonstração, exibe um alerta com informações de login.
     * Em um sistema real, isso abriria uma tela de recuperação de senha ou enviaria um e-mail.
     */
    @FXML
    private void handleForgotPassword() {
        UIUtils.showInfoAlert("Recuperação de Senha", "Funcionalidade em Desenvolvimento",
                "A funcionalidade de recuperação de senha será implementada em uma versão futura.\n\n" +
                "Para demonstração, use as seguintes credenciais:\n" +
                "• admin / admin123\n" +
                "• alice / alice123\n" +
                "• bob / bob123\n" +
                "• charlie / charlie123");
    }
}

