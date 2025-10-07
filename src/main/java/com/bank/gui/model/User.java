package com.bank.gui.model;

/**
 * Classe {@code User} representa um usuário do sistema bancário.
 * Esta classe é utilizada para autenticação e gerenciamento de sessão na GUI.
 *
 * <p>Segue o **Princípio da Responsabilidade Única (SRP)**, sendo responsável
 * apenas por armazenar e gerenciar informações básicas do usuário.
 */
public class User {
    private String username;
    private String password;
    private String fullName;
    private String email;
    private boolean isLoggedIn;

    /**
     * Construtor para criar um novo usuário.
     * @param username Nome de usuário único.
     * @param password Senha do usuário.
     * @param fullName Nome completo do usuário.
     * @param email Email do usuário.
     */
    public User(String username, String password, String fullName, String email) {
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.email = email;
        this.isLoggedIn = false;
    }

    // Getters e Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        isLoggedIn = loggedIn;
    }

    /**
     * Valida as credenciais do usuário.
     * @param inputPassword Senha fornecida para validação.
     * @return true se a senha estiver correta, false caso contrário.
     */
    public boolean validatePassword(String inputPassword) {
        return this.password.equals(inputPassword);
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", isLoggedIn=" + isLoggedIn +
                '}';
    }
}
