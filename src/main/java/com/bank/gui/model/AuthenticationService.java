package com.bank.gui.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Classe {@code AuthenticationService} implementa o padrão **Singleton** para gerenciar
 * a autenticação de usuários no sistema bancário.
 *
 * <p>A escolha do padrão Singleton é justificada pela necessidade de um ponto único
 * de controle para autenticação em toda a aplicação, garantindo consistência
 * no gerenciamento de sessões de usuário.
 *
 * <p>Esta classe também demonstra o **Princípio da Responsabilidade Única (SRP)**,
 * sendo responsável exclusivamente pela autenticação e gerenciamento de usuários.
 */
public class AuthenticationService {
    private static AuthenticationService instance;
    private Map<String, User> users;
    private User currentUser;

    /**
     * Construtor privado para implementar o padrão Singleton.
     * Inicializa alguns usuários de demonstração.
     */
    private AuthenticationService() {
        users = new HashMap<>();
        initializeDefaultUsers();
    }

    /**
     * Retorna a única instância do AuthenticationService.
     * @return A instância única do AuthenticationService.
     */
    public static synchronized AuthenticationService getInstance() {
        if (instance == null) {
            instance = new AuthenticationService();
        }
        return instance;
    }

    /**
     * Inicializa usuários padrão para demonstração.
     * Em um sistema real, estes dados viriam de um banco de dados.
     */
    private void initializeDefaultUsers() {
        users.put("admin", new User("admin", "admin123", "Administrador do Sistema", "admin@bank.com"));
        users.put("alice", new User("alice", "alice123", "Alice Smith", "alice@email.com"));
        users.put("bob", new User("bob", "bob123", "Bob Johnson", "bob@email.com"));
        users.put("charlie", new User("charlie", "charlie123", "Charlie Brown", "charlie@email.com"));
    }

    /**
     * Autentica um usuário com base no nome de usuário e senha.
     * @param username Nome de usuário.
     * @param password Senha do usuário.
     * @return true se a autenticação for bem-sucedida, false caso contrário.
     */
    public boolean authenticate(String username, String password) {
        User user = users.get(username);
        if (user != null && user.validatePassword(password)) {
            user.setLoggedIn(true);
            currentUser = user;
            return true;
        }
        return false;
    }

    /**
     * Realiza o logout do usuário atual.
     */
    public void logout() {
        if (currentUser != null) {
            currentUser.setLoggedIn(false);
            currentUser = null;
        }
    }

    /**
     * Retorna o usuário atualmente logado.
     * @return O usuário atual ou null se nenhum usuário estiver logado.
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * Verifica se há um usuário logado no sistema.
     * @return true se há um usuário logado, false caso contrário.
     */
    public boolean isUserLoggedIn() {
        return currentUser != null && currentUser.isLoggedIn();
    }

    /**
     * Registra um novo usuário no sistema.
     * @param user O usuário a ser registrado.
     * @return true se o registro foi bem-sucedido, false se o nome de usuário já existe.
     */
    public boolean registerUser(User user) {
        if (users.containsKey(user.getUsername())) {
            return false; // Usuário já existe
        }
        users.put(user.getUsername(), user);
        return true;
    }

    /**
     * Verifica se um nome de usuário já existe no sistema.
     * @param username Nome de usuário a ser verificado.
     * @return true se o usuário existe, false caso contrário.
     */
    public boolean userExists(String username) {
        return users.containsKey(username);
    }
}
