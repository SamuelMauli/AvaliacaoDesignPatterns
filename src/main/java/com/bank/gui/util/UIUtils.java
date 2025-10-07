package com.bank.gui.util;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.util.Duration;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Optional;

/**
 * Classe utilitária {@code UIUtils} fornece métodos estáticos para melhorar
 * a experiência do usuário na interface gráfica, incluindo animações,
 * formatação e feedback visual.
 *
 * <p>Esta classe segue o **Princípio da Responsabilidade Única (SRP)**, sendo
 * responsável exclusivamente por utilitários de interface do usuário.
 */
public class UIUtils {

    // Formatador de moeda brasileiro
    private static final NumberFormat CURRENCY_FORMATTER = 
        NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

    // Estilos CSS para diferentes estados
    private static final String ERROR_STYLE = 
        "-fx-border-color: #d32f2f; -fx-border-width: 2px; -fx-border-radius: 4px;";
    private static final String SUCCESS_STYLE = 
        "-fx-border-color: #388e3c; -fx-border-width: 2px; -fx-border-radius: 4px;";
    private static final String NORMAL_STYLE = 
        "-fx-border-color: #ddd; -fx-border-width: 1px; -fx-border-radius: 4px;";

    /**
     * Construtor privado para prevenir instanciação.
     * Esta é uma classe utilitária com métodos estáticos.
     */
    private UIUtils() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * Formata um valor monetário para exibição.
     * @param amount O valor a ser formatado.
     * @return O valor formatado como string.
     */
    public static String formatCurrency(double amount) {
        return CURRENCY_FORMATTER.format(amount);
    }

    /**
     * Exibe um alerta de informação.
     * @param title O título do alerta.
     * @param message A mensagem do alerta.
     */
    public static void showInfoAlert(String title, String headerText, String contentText) {
        showAlert(title, headerText, contentText, Alert.AlertType.INFORMATION);
    }

    public static void showInfoAlert(String title, String message) {
        showAlert(title, null, message, Alert.AlertType.INFORMATION);
    }

    /**
     * Exibe um alerta de erro.
     * @param title O título do alerta.
     * @param message A mensagem do alerta.
     */
    public static void showErrorAlert(String title, String message) {
        showAlert(title, null, message, Alert.AlertType.ERROR);
    }

    /**
     * Exibe um alerta de sucesso.
     * @param title O título do alerta.
     * @param message A mensagem do alerta.
     */
    public static void showSuccessAlert(String title, String message) {
        showAlert(title, null, message, Alert.AlertType.INFORMATION);
    }

    /**
     * Exibe um alerta de confirmação.
     * @param title O título do alerta.
     * @param message A mensagem do alerta.
     * @return true se o usuário confirmou, false caso contrário.
     */
    public static boolean showConfirmationAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    /**
     * Método auxiliar para exibir alertas.
     * @param title O título do alerta.
     * @param message A mensagem do alerta.
     * @param type O tipo do alerta.
     */
    private static void showAlert(String title, String headerText, String contentText, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
    }

    /**
     * Aplica estilo de erro a um campo de entrada.
     * @param field O campo a ser estilizado.
     */
    public static void applyErrorStyle(TextField field) {
        field.setStyle(ERROR_STYLE);
        shakeAnimation(field);
    }

    /**
     * Aplica estilo de sucesso a um campo de entrada.
     * @param field O campo a ser estilizado.
     */
    public static void applySuccessStyle(TextField field) {
        field.setStyle(SUCCESS_STYLE);
    }

    /**
     * Remove estilos especiais de um campo de entrada.
     * @param field O campo a ser resetado.
     */
    public static void clearFieldStyle(TextField field) {
        field.setStyle(NORMAL_STYLE);
    }

    /**
     * Aplica estilo de erro a um label.
     * @param label O label a ser estilizado.
     */
    public static void applyErrorStyle(Label label) {
        label.setStyle("-fx-text-fill: #d32f2f; -fx-font-weight: bold;");
    }

    /**
     * Aplica estilo de sucesso a um label.
     * @param label O label a ser estilizado.
     */
    public static void applySuccessStyle(Label label) {
        label.setStyle("-fx-text-fill: #388e3c; -fx-font-weight: bold;");
    }

    /**
     * Aplica estilo normal a um label.
     * @param label O label a ser estilizado.
     */
    public static void applyNormalStyle(Label label) {
        label.setStyle("-fx-text-fill: #333333;");
    }

    /**
     * Cria uma animação de tremor para indicar erro.
     * @param node O nó a ser animado.
     */
    public static void shakeAnimation(Node node) {
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(100), node);
        scaleTransition.setFromX(1.0);
        scaleTransition.setToX(1.05);
        scaleTransition.setFromY(1.0);
        scaleTransition.setToY(1.05);
        scaleTransition.setCycleCount(4);
        scaleTransition.setAutoReverse(true);
        scaleTransition.play();
    }

    /**
     * Cria uma animação de fade in para um nó.
     * @param node O nó a ser animado.
     */
    public static void fadeInAnimation(Node node) {
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(500), node);
        fadeTransition.setFromValue(0.0);
        fadeTransition.setToValue(1.0);
        fadeTransition.play();
    }

    /**
     * Cria uma animação de fade out para um nó.
     * @param node O nó a ser animado.
     */
    public static void fadeOutAnimation(Node node) {
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(500), node);
        fadeTransition.setFromValue(1.0);
        fadeTransition.setToValue(0.0);
        fadeTransition.play();
    }

    /**
     * Valida e estiliza um campo de entrada com base na validação.
     * @param field O campo a ser validado.
     * @param isValid Se o campo é válido.
     * @param errorMessage A mensagem de erro (se aplicável).
     * @param statusLabel O label para exibir mensagens de status.
     */
    public static void validateAndStyleField(TextField field, boolean isValid, 
                                           String errorMessage, Label statusLabel) {
        if (isValid) {
            applySuccessStyle(field);
            if (statusLabel != null) {
                statusLabel.setText("");
            }
        } else {
            applyErrorStyle(field);
            if (statusLabel != null && errorMessage != null) {
                statusLabel.setText(errorMessage);
                applyErrorStyle(statusLabel);
            }
        }
    }

    /**
     * Limpa todos os estilos de validação de um conjunto de campos.
     * @param fields Os campos a serem limpos.
     */
    public static void clearValidationStyles(TextField... fields) {
        for (TextField field : fields) {
            clearFieldStyle(field);
        }
    }

    /**
     * Formata um número de conta para exibição.
     * Mostra apenas os últimos 4 dígitos para privacidade.
     * @param accountNumber O número completo da conta.
     * @return O número formatado para exibição.
     */
    public static String formatAccountNumber(String accountNumber) {
        if (accountNumber == null || accountNumber.length() < 4) {
            return accountNumber;
        }
        String lastFour = accountNumber.substring(accountNumber.length() - 4);
        return "****-" + lastFour;
    }

    /**
     * Trunca um texto para um comprimento máximo, adicionando "..." se necessário.
     * @param text O texto a ser truncado.
     * @param maxLength O comprimento máximo.
     * @return O texto truncado.
     */
    public static String truncateText(String text, int maxLength) {
        if (text == null || text.length() <= maxLength) {
            return text;
        }
        return text.substring(0, maxLength - 3) + "...";
    }

    /**
     * Capitaliza a primeira letra de cada palavra em uma string.
     * @param text O texto a ser capitalizado.
     * @return O texto capitalizado.
     */
    public static String capitalizeWords(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        
        String[] words = text.toLowerCase().split("\\s+");
        StringBuilder result = new StringBuilder();
        
        for (int i = 0; i < words.length; i++) {
            if (i > 0) {
                result.append(" ");
            }
            if (!words[i].isEmpty()) {
                result.append(Character.toUpperCase(words[i].charAt(0)));
                if (words[i].length() > 1) {
                    result.append(words[i].substring(1));
                }
            }
        }
        
        return result.toString();
    }

    /**
     * Cria uma mensagem de status formatada com timestamp.
     * @param message A mensagem base.
     * @return A mensagem formatada com timestamp.
     */
    public static String createTimestampedMessage(String message) {
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        java.time.format.DateTimeFormatter formatter = 
            java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return "[" + now.format(formatter) + "] " + message;
    }
}
