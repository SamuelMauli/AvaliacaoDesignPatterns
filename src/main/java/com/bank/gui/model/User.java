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

    /**
     * Retorna o nome de usuário.
     * @return O nome de usuário.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Define o nome de usuário.
     * @param username O novo nome de usuário.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Retorna a senha do usuário.
     * @return A senha do usuário.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Define a senha do usuário.
     * @param password A nova senha do usuário.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Retorna o nome completo do usuário.
     * @return O nome completo do usuário.
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * Define o nome completo do usuário.
     * @param fullName O novo nome completo do usuário.
     */
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    /**
     * Retorna o email do usuário.
     * @return O email do usuário.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Define o email do usuário.
     * @param email O novo email do usuário.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Verifica se o usuário está logado.
     * @return true se o usuário está logado, false caso contrário.
     */
    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    /**
     * Define o status de login do usuário.
     * @param loggedIn O status de login.
     */
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

    /**
     * Retorna uma representação em String do objeto User.
     * @return Uma String contendo os detalhes do usuário.
     */
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

