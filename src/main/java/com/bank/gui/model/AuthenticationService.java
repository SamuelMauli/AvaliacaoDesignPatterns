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
 * 
 * <p><b>COMO FUNCIONA A LÓGICA DO SISTEMA:</b>
 * <ol>
 *     <li><b>Singleton:</b> Garante que apenas uma instância do serviço de autenticação
 *         exista em toda a aplicação. Obtida através de getInstance().</li>
 *     <li><b>Usuários:</b> Mantém um mapa de usuários (username -> User) com usuários
 *         pré-cadastrados para demonstração. Em um sistema real, viria de um banco de dados.</li>
 *     <li><b>Autenticação:</b> O método authenticate() verifica se o username existe
 *         e se a senha está correta. Se sim, define o usuário como logado e armazena
 *         em currentUser.</li>
 *     <li><b>Sessão:</b> Mantém o usuário atual logado em currentUser até que logout()
 *         seja chamado.</li>
 * </ol>
 */
public class AuthenticationService {
    /** Instância única do AuthenticationService (Singleton) */
    private static AuthenticationService instance;
    
    /** Mapa de usuários do sistema (username -> User).
     *  Em um sistema real, viria de um banco de dados. */
    private Map<String, User> users;
    
    /** Usuário atualmente logado no sistema. Null se nenhum usuário estiver logado. */
    private User currentUser;

    /**
     * Construtor privado para implementar o padrão Singleton.
     * Inicializa alguns usuários de demonstração.
     * <p><b>LÓGICA:</b> O construtor é privado para implementar o padrão Singleton.
     * Inicializa o mapa de usuários e cria usuários padrão para demonstração.
     */
    private AuthenticationService() {
        // Inicializa o mapa vazio de usuários
        users = new HashMap<>();
        // Cria usuários padrão para demonstração
        // Em um sistema real, estes dados viriam de um banco de dados
        initializeDefaultUsers();
    }

    /**
     * Retorna a única instância do AuthenticationService.
     * <p><b>LÓGICA:</b> Implementa o padrão Singleton - cria a instância na primeira
     * chamada e retorna a mesma instância em chamadas subsequentes. Usa sincronização
     * para garantir segurança em ambientes multi-threaded.
     * 
     * @return A instância única do AuthenticationService.
     */
    public static synchronized AuthenticationService getInstance() {
        // Verifica se a instância já foi criada
        if (instance == null) {
            // Se não foi criada, cria uma nova instância (chama o construtor privado)
            instance = new AuthenticationService();
        }
        // Retorna a instância (criada agora ou já existente)
        return instance;
    }

    /**
     * Inicializa usuários padrão para demonstração.
     * Em um sistema real, estes dados viriam de um banco de dados.
     * <p><b>LÓGICA:</b> Cria usuários pré-cadastrados com username, senha, nome completo
     * e email. Estes usuários são usados para demonstração do sistema. Em um sistema real,
     * estes dados seriam carregados de um banco de dados ou sistema de autenticação externo.
     */
    private void initializeDefaultUsers() {
        // Cria usuários padrão para demonstração
        // Formato: username, senha, nome completo, email
        users.put("admin", new User("admin", "admin123", "Administrador do Sistema", "admin@bank.com"));
        users.put("alice", new User("alice", "alice123", "Alice Smith", "alice@email.com"));
        users.put("bob", new User("bob", "bob123", "Bob Johnson", "bob@email.com"));
        users.put("charlie", new User("charlie", "charlie123", "Charlie Brown", "charlie@email.com"));
    }

    /**
     * Autentica um usuário com base no nome de usuário e senha.
     * <p><b>LÓGICA DE FUNCIONAMENTO:</b>
     * <ol>
     *     <li>Busca o usuário no mapa usando o username</li>
     *     <li>Se o usuário existir, valida a senha usando user.validatePassword()</li>
     *     <li>Se a senha estiver correta:
     *         <ul>
     *             <li>Define o usuário como logado (setLoggedIn(true))</li>
     *             <li>Armazena o usuário em currentUser</li>
     *             <li>Retorna true (autenticação bem-sucedida)</li>
     *         </ul>
     *     </li>
     *     <li>Se o usuário não existir ou a senha estiver incorreta, retorna false</li>
     * </ol>
     * 
     * @param username Nome de usuário.
     * @param password Senha do usuário.
     * @return true se a autenticação for bem-sucedida, false caso contrário.
     */
    public boolean authenticate(String username, String password) {
        // Busca o usuário no mapa usando o username
        User user = users.get(username);
        
        // Verifica se o usuário existe e se a senha está correta
        if (user != null && user.validatePassword(password)) {
            // Se a autenticação for bem-sucedida:
            // 1. Define o usuário como logado
            user.setLoggedIn(true);
            // 2. Armazena o usuário em currentUser para acesso posterior
            currentUser = user;
            // 3. Retorna true (autenticação bem-sucedida)
            return true;
        }
        
        // Se o usuário não existir ou a senha estiver incorreta, retorna false
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

