package com.bank.gui.util;

import java.util.regex.Pattern;

/**
 * Classe utilitária {@code ValidationUtils} fornece métodos estáticos para validação
 * de dados de entrada na interface gráfica do usuário.
 *
 * <p>Esta classe segue o **Princípio da Responsabilidade Única (SRP)**, sendo
 * responsável exclusivamente por validações de dados. Também demonstra o uso
 * de métodos estáticos para funcionalidades utilitárias que não requerem estado.
 */
public class ValidationUtils {

    // Padrões de validação
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    );
    
    private static final Pattern NAME_PATTERN = Pattern.compile(
        "^[a-zA-ZÀ-ÿ\\s]{2,50}$"
    );

    /**
     * Construtor privado para prevenir instanciação.
     * Esta é uma classe utilitária com métodos estáticos.
     */
    private ValidationUtils() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * Valida se uma string não é nula nem vazia.
     * @param value O valor a ser validado.
     * @return true se o valor é válido, false caso contrário.
     */
    public static boolean isNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }

    /**
     * Valida se um valor numérico é positivo.
     * @param value O valor a ser validado.
     * @return true se o valor é positivo, false caso contrário.
     */
    public static boolean isPositive(double value) {
        return value > 0;
    }

    /**
     * Valida se um valor numérico é não-negativo (zero ou positivo).
     * @param value O valor a ser validado.
     * @return true se o valor é não-negativo, false caso contrário.
     */
    public static boolean isNonNegative(double value) {
        return value >= 0;
    }

    /**
     * Valida se uma string representa um número válido.
     * @param value A string a ser validada.
     * @return true se a string representa um número válido, false caso contrário.
     */
    public static boolean isValidNumber(String value) {
        if (!isNotEmpty(value)) {
            return false;
        }
        try {
            Double.parseDouble(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Valida se uma string representa um número positivo válido.
     * @param value A string a ser validada.
     * @return true se a string representa um número positivo válido, false caso contrário.
     */
    public static boolean isValidPositiveNumber(String value) {
        if (!isValidNumber(value)) {
            return false;
        }
        try {
            double number = Double.parseDouble(value);
            return isPositive(number);
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Valida se uma string representa um número não-negativo válido.
     * @param value A string a ser validada.
     * @return true se a string representa um número não-negativo válido, false caso contrário.
     */
    public static boolean isValidNonNegativeNumber(String value) {
        if (!isValidNumber(value)) {
            return false;
        }
        try {
            double number = Double.parseDouble(value);
            return isNonNegative(number);
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Valida se um email tem formato válido.
     * @param email O email a ser validado.
     * @return true se o email é válido, false caso contrário.
     */
    public static boolean isValidEmail(String email) {
        if (!isNotEmpty(email)) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * Valida se um nome tem formato válido.
     * Aceita apenas letras, espaços e acentos, com 2 a 50 caracteres.
     * @param name O nome a ser validado.
     * @return true se o nome é válido, false caso contrário.
     */
    public static boolean isValidName(String name) {
        if (!isNotEmpty(name)) {
            return false;
        }
        return NAME_PATTERN.matcher(name.trim()).matches();
    }

    /**
     * Valida se uma senha atende aos critérios mínimos de segurança.
     * Critérios: pelo menos 6 caracteres.
     * @param password A senha a ser validada.
     * @return true se a senha é válida, false caso contrário.
     */
    public static boolean isValidPassword(String password) {
        return isNotEmpty(password) && password.length() >= 6;
    }

    /**
     * Valida se um nome de usuário é válido.
     * Critérios: 3 a 20 caracteres, apenas letras, números e underscore.
     * @param username O nome de usuário a ser validado.
     * @return true se o nome de usuário é válido, false caso contrário.
     */
    public static boolean isValidUsername(String username) {
        if (!isNotEmpty(username)) {
            return false;
        }
        String trimmed = username.trim();
        return trimmed.length() >= 3 && trimmed.length() <= 20 && 
               trimmed.matches("^[a-zA-Z0-9_]+$");
    }

    /**
     * Valida se um valor está dentro de um intervalo específico.
     * @param value O valor a ser validado.
     * @param min O valor mínimo (inclusivo).
     * @param max O valor máximo (inclusivo).
     * @return true se o valor está no intervalo, false caso contrário.
     */
    public static boolean isInRange(double value, double min, double max) {
        return value >= min && value <= max;
    }

    /**
     * Valida se uma taxa de juros é válida (entre 0% e 100%).
     * @param interestRate A taxa de juros a ser validada.
     * @return true se a taxa é válida, false caso contrário.
     */
    public static boolean isValidInterestRate(double interestRate) {
        return isInRange(interestRate, 0.0, 1.0); // 0% a 100% em decimal
    }

    /**
     * Valida se um limite de cheque especial é válido (não-negativo).
     * @param overdraftLimit O limite a ser validado.
     * @return true se o limite é válido, false caso contrário.
     */
    public static boolean isValidOverdraftLimit(double overdraftLimit) {
        return isNonNegative(overdraftLimit);
    }

    /**
     * Formata uma mensagem de erro para campo obrigatório.
     * @param fieldName O nome do campo.
     * @return A mensagem de erro formatada.
     */
    public static String getRequiredFieldMessage(String fieldName) {
        return "O campo '" + fieldName + "' é obrigatório.";
    }

    /**
     * Formata uma mensagem de erro para valor inválido.
     * @param fieldName O nome do campo.
     * @param expectedFormat O formato esperado.
     * @return A mensagem de erro formatada.
     */
    public static String getInvalidFormatMessage(String fieldName, String expectedFormat) {
        return "O campo '" + fieldName + "' deve ter o formato: " + expectedFormat;
    }

    /**
     * Formata uma mensagem de erro para valor fora do intervalo.
     * @param fieldName O nome do campo.
     * @param min O valor mínimo.
     * @param max O valor máximo.
     * @return A mensagem de erro formatada.
     */
    public static String getOutOfRangeMessage(String fieldName, double min, double max) {
        return "O campo '" + fieldName + "' deve estar entre " + min + " e " + max + ".";
    }
}
