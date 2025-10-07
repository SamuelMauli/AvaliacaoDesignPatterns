# Sistema Bancário com Padrões de Projeto e Princípios SOLID

Este projeto implementa um sistema bancário simplificado em Java, com foco na aplicação e demonstração de **Padrões de Projeto** e **Princípios SOLID**. O objetivo é criar um código robusto, extensível, de fácil manutenção e didático, ideal para uma defesa de código, onde cada escolha de design é justificada.

## Sumário
1.  [Introdução](#introdução)
2.  [Estrutura do Projeto](#estrutura-do-projeto)
3.  [Princípios SOLID Aplicados](#princípios-solid-aplicados)
    *   [Princípio da Responsabilidade Única (SRP)](#princípio-da-responsabilidade-única-srp)
    *   [Princípio Aberto/Fechado (OCP)](#princípio-aberto/fechado-ocp)
    *   [Princípio da Substituição de Liskov (LSP)](#princípio-da-substituição-de-liskov-lsp)
    *   [Princípio da Segregação de Interfaces (ISP)](#princípio-da-segregação-de-interfaces-isp)
    *   [Princípio da Inversão de Dependência (DIP)](#princípio-da-inversão-de-dependência-dip)
4.  [Padrões de Projeto Implementados](#padrões-de-projeto-implementados)
    *   [Factory Method](#factory-method)
    *   [Singleton](#singleton)
    *   [Command](#command)
    *   [Facade](#facade)
    *   [Decorator](#decorator)
    *   [Observer](#observer)
    *   [Strategy](#strategy)
5.  [Interface Gráfica do Usuário (GUI)](#interface-gráfica-do-usuário-gui)
6.  [Como Executar o Projeto](#como-executar-o-projeto)
7.  [Testes](#testes)

## 1. Introdução

O sistema bancário é um domínio complexo que se beneficia enormemente da aplicação de boas práticas de engenharia de software. Este projeto serve como um exemplo prático de como os **Princípios SOLID** e **Padrões de Projeto** podem ser utilizados para gerenciar essa complexidade, resultando em um código mais organizado, flexível e fácil de entender e estender. Cada componente foi projetado com a intenção de demonstrar um ou mais desses conceitos.

## 2. Estrutura do Projeto

O projeto segue uma estrutura Maven padrão, com pacotes organizados por funcionalidade e padrão de projeto. Com a adição da GUI, a estrutura foi expandida para incluir componentes JavaFX:

```
AvaliacaoDesignPatterns
├── pom.xml
└── src
    ├── main
    │   ├── java
    │   │   └── com
    │   │       └── bank
    │   │           ├── account             // Classes base de contas e interfaces
    │   │           ├── app                 // Classe principal para execução via console
    │   │           ├── command             // Implementações do padrão Command
    │   │           ├── decorator           // Implementações do padrão Decorator
    │   │           ├── facade              // Implementação do padrão Facade
    │   │           ├── factory             // Implementação do padrão Factory Method
    │   │           ├── gui                 // Componentes da Interface Gráfica (JavaFX)
    │   │           │   ├── controller      // Controladores FXML
    │   │           │   ├── model           // Modelos de dados para a GUI (ex: User, AuthenticationService)
    │   │           │   ├── util            // Classes utilitárias para GUI (validação, UI)
    │   │           │   └── BankingApplication.java // Classe principal da aplicação GUI
    │   │           ├── logger              // Implementação do padrão Singleton
    │   │           ├── observer            // Implementações do padrão Observer
    │   │           └── strategy            // Implementações do padrão Strategy
    │   └── resources
    │       └── fxml                  // Arquivos FXML para as telas da GUI
    └── test
        └── java
            └── com
                └── bank
                    ├── account             // Testes para classes de conta
                    ├── facade              // Testes para BankingFacade
                    ├── factory             // Testes para AccountFactory
                    └── gui                 // Testes para componentes da GUI (ex: AuthenticationServiceTest)
```

## 3. Princípios SOLID Aplicados

Os princípios SOLID são um conjunto de cinco princípios de design de software que visam tornar os designs de software mais compreensíveis, flexíveis e manteníveis. Eles são a base para a construção de sistemas orientados a objetos de alta qualidade.

### Princípio da Responsabilidade Única (SRP)

> *Uma classe deve ter apenas uma razão para mudar.* Isso significa que uma classe deve ter apenas uma responsabilidade.

**Aplicação no Projeto:**
*   **`Account`**: É responsável por gerenciar o estado básico de uma conta (saldo, número, cliente) e a lógica de depósito. Outras responsabilidades, como saque ou cálculo de juros, são delegadas a interfaces específicas ou subclasses.
*   **`TransactionLogger`**: Sua única responsabilidade é registrar eventos em um arquivo de log.
*   **`AuditService`**: Focado exclusivamente na tarefa de auditoria de eventos de conta.
*   **Estratégias de Juros (`SimpleInterestStrategy`, `HighYieldInterestStrategy`)**: Cada uma é responsável apenas por um método específico de cálculo de juros.
*   **`AuthenticationService`**: Responsável exclusivamente pela autenticação e gerenciamento de usuários.
*   **`ValidationUtils` e `UIUtils`**: Cada um com responsabilidades bem definidas de validação e manipulação de UI, respectivamente.

### Princípio Aberto/Fechado (OCP)

> *Entidades de software (classes, módulos, funções, etc.) devem ser abertas para extensão, mas fechadas para modificação.* Isso significa que o comportamento de um módulo pode ser estendido sem alterar seu código-fonte.

**Aplicação no Projeto:**
*   **`Account`**: É uma classe abstrata que pode ser estendida por novos tipos de conta (`CheckingAccount`, `SavingsAccount`) sem modificar o código existente da classe base.
*   **`AccountFactory`**: Permite a adição de novos tipos de conta ao sistema modificando apenas a fábrica, sem alterar o código cliente que solicita a criação de contas.
*   **Padrão Decorator (`AccountDecorator`, `OverdraftProtectionDecorator`)**: Novas funcionalidades podem ser adicionadas a objetos de conta dinamicamente sem modificar as classes de conta existentes.
*   **Padrão Observer (`AccountObserver`, `AuditService`)**: Novos observadores podem ser adicionados sem modificar o código da classe `Account`.
*   **Padrão Strategy (`InterestCalculationStrategy`)**: Novas estratégias de cálculo de juros podem ser adicionadas sem modificar a classe `SavingsAccount`.

### Princípio da Substituição de Liskov (LSP)

> *Objetos em um programa devem ser substituíveis por instâncias de seus subtipos sem alterar a correção do programa.* Em outras palavras, se `S` é um subtipo de `T`, então objetos do tipo `T` em um programa podem ser substituídos por objetos do tipo `S` sem alterar nenhuma das propriedades desejáveis desse programa.

**Aplicação no Projeto:**
*   **`CheckingAccount` e `SavingsAccount`**: Podem ser usadas onde uma `Account` ou uma interface como `Withdrawable` é esperada. Por exemplo, a `BankingFacade` opera com a abstração `Account` e `Withdrawable` para realizar operações, garantindo que qualquer subtipo funcione corretamente.
*   **`AccountDecorator`**: Garante que os decoradores possam ser usados onde uma `Account` ou uma conta que suporta depósito/saque é esperada.

### Princípio da Segregação de Interfaces (ISP)

> *Clientes não devem ser forçados a depender de interfaces que não utilizam.* Isso significa que interfaces grandes devem ser divididas em interfaces menores e mais específicas.

**Aplicação no Projeto:**
*   **`Depositable`, `Withdrawable`, `InterestBearing`**: Em vez de uma única interface `AccountOperations` com todos os métodos, foram criadas interfaces específicas. Uma `SavingsAccount` implementa `Depositable`, `Withdrawable` e `InterestBearing`, enquanto uma `CheckingAccount` implementa `Depositable` e `Withdrawable`. Isso garante que as classes implementem apenas as funcionalidades que realmente precisam.

### Princípio da Inversão de Dependência (DIP)

> *Módulos de alto nível não devem depender de módulos de baixo nível. Ambos devem depender de abstrações. Abstrações não devem depender de detalhes. Detalhes devem depender de abstrações.* Isso promove o acoplamento fraco.

**Aplicação no Projeto:**
*   **`Account`**: Depende da abstração `AccountObserver` em vez de implementações concretas de observadores.
*   **`SavingsAccount`**: Depende da abstração `InterestCalculationStrategy` em vez de uma implementação concreta de cálculo de juros.
*   **`AccountFactory`**: O cliente depende da fábrica e da abstração `Account`, não das classes concretas de conta.
*   **`BankingFacade`**: O cliente interage com a Facade, que por sua vez coordena com as abstrações dos subsistemas (Factory, Command, etc.).
*   **`DepositCommand` e `WithdrawCommand`**: Dependem da abstração `Account` ou `Withdrawable`.
*   **Controladores da GUI**: Dependem da `BankingFacade` e do `AuthenticationService` (abstrações ou serviços de alto nível) para realizar operações, sem se preocupar com os detalhes de implementação dos subsistemas bancários.

## 4. Padrões de Projeto Implementados

Os padrões de projeto são soluções reutilizáveis para problemas comuns no design de software. Eles fornecem um vocabulário comum e uma estrutura para resolver problemas de design de forma eficaz.

### Factory Method

**Propósito:** Define uma interface para criar um objeto, mas permite que as subclasses decidam qual classe instanciar. O Factory Method permite que uma classe adie a instanciação para suas subclasses.

**Aplicação no Projeto:**
*   **`AccountFactory`**: A classe `AccountFactory` possui um método estático `createAccount` que recebe um `AccountType` (CHECKING ou SAVINGS) e outros parâmetros. Com base no tipo, ele retorna uma instância de `CheckingAccount` ou `SavingsAccount`.

**Benefícios:**
*   **Desacoplamento**: O código cliente não precisa saber as classes concretas das contas, apenas a fábrica e a interface `Account`.
*   **Extensibilidade**: Adicionar novos tipos de conta é fácil; basta criar uma nova classe de conta e estender a lógica na `AccountFactory`, sem modificar o código cliente.

### Singleton

**Propósito:** Garante que uma classe tenha apenas uma instância e fornece um ponto de acesso global a ela.

**Aplicação no Projeto:**
*   **`TransactionLogger`**: A classe `TransactionLogger` é implementada como um Singleton. Ela possui um construtor privado e um método estático `getInstance()` que retorna a única instância da classe. Isso garante que todos os logs sejam escritos no mesmo arquivo e evita problemas de concorrência.
*   **`AuthenticationService`**: Também implementado como Singleton para garantir um único ponto de gerenciamento de sessão e autenticação de usuários em toda a aplicação GUI.

**Benefícios:**
*   **Controle de Instância**: Garante que apenas uma instância de um recurso (neste caso, o logger de transações e o serviço de autenticação) seja utilizada, economizando recursos e evitando inconsistências.
*   **Ponto de Acesso Global**: Facilita o acesso ao logger e ao serviço de autenticação de qualquer parte da aplicação.

### Command

**Propósito:** Encapsula uma solicitação como um objeto, permitindo que você parametrize clientes com diferentes solicitações, enfileire ou registre solicitações, e suporte operações desfazíveis.

**Aplicação no Projeto:**
*   **`Command` (interface)**: Define o método `execute()`.
*   **`DepositCommand` e `WithdrawCommand` (classes concretas)**: Implementam a interface `Command` e encapsulam as operações de depósito e saque, respectivamente. Cada comando contém a referência à conta e o valor da transação.
*   **`BankingFacade`**: Utiliza esses comandos para realizar as operações bancárias, invocando o método `execute()` do comando apropriado.

**Benefícios:**
*   **Desacoplamento**: O invocador (a `BankingFacade` ou um controlador da GUI) é desacoplado da implementação da operação. Ele apenas sabe como executar um `Command`.
*   **Extensibilidade**: Novas operações podem ser adicionadas criando novas classes de comando sem modificar o invocador.
*   **Flexibilidade**: Permite a implementação de funcionalidades como histórico de transações, operações desfazíveis ou agendamento de transações.

### Facade

**Propósito:** Fornece uma interface unificada para um conjunto de interfaces em um subsistema. Facade define uma interface de nível superior que torna o subsistema mais fácil de usar.

**Aplicação no Projeto:**
*   **`BankingFacade`**: Atua como uma interface simplificada para o sistema bancário. Ela expõe métodos como `createAccount`, `deposit`, `withdraw`, `transfer` e `getBalance`, que internamente coordenam com `AccountFactory`, `DepositCommand`, `WithdrawCommand` e as classes `Account`.
*   **Controladores da GUI**: Interagem exclusivamente com a `BankingFacade` para realizar todas as operações bancárias, sem precisar conhecer os detalhes complexos dos subsistemas internos.

**Benefícios:**
*   **Simplificação**: Reduz a complexidade do cliente (incluindo a GUI) ao interagir com um subsistema complexo.
*   **Desacoplamento**: O cliente é desacoplado dos detalhes de implementação dos subsistemas internos.
*   **Manutenibilidade**: Mudanças nos subsistemas internos têm menos impacto no código cliente, desde que a interface da Facade permaneça estável.

### Decorator

**Propósito:** Anexa responsabilidades adicionais a um objeto dinamicamente. Decorators fornecem uma alternativa flexível à subclasse para estender a funcionalidade.

**Aplicação no Projeto:**
*   **`AccountDecorator` (classe abstrata)**: Estende `Account` e mantém uma referência à `Account` decorada. Delega a maioria das chamadas para a conta decorada.
*   **`OverdraftProtectionDecorator` (decorador concreto)**: Estende `AccountDecorator` e adiciona a funcionalidade de cheque especial. Ele sobrescreve o método `withdraw` para permitir saques além do saldo, até um limite de cheque especial.

**Benefícios:**
*   **Extensibilidade Dinâmica**: Novas funcionalidades podem ser adicionadas a objetos em tempo de execução, sem modificar suas classes originais.
*   **Flexibilidade**: Evita a explosão de subclasses que ocorreria com a herança para cada combinação de funcionalidades.
*   **OCP**: A classe `Account` é fechada para modificação, mas aberta para extensão através de decoradores.

### Observer

**Propósito:** Define uma dependência um-para-muitos entre objetos para que, quando um objeto muda de estado, todos os seus dependentes sejam notificados e atualizados automaticamente.

**Aplicação no Projeto:**
*   **`Account` (Subject/Observable)**: Mantém uma lista de `AccountObserver`s e notifica-os sobre eventos (depósito, saque, cálculo de juros) através do método `notifyObservers()`.
*   **`AccountObserver` (interface)**: Define o método `update()` que os observadores devem implementar.
*   **`AuditService` (Observer concreto)**: Implementa `AccountObserver` e registra os eventos da conta no `TransactionLogger`.

**Benefícios:**
*   **Acoplamento Fraco**: O Subject (`Account`) não precisa conhecer os detalhes dos Observers. Ele apenas sabe que eles implementam a interface `AccountObserver`.
*   **Reusabilidade**: Observers podem ser reutilizados em diferentes Subjects.
*   **Extensibilidade**: Novos observadores podem ser adicionados facilmente sem modificar o Subject.

### Strategy

**Propósito:** Define uma família de algoritmos, encapsula cada um e os torna intercambiáveis. Strategy permite que o algoritmo varie independentemente dos clientes que o utilizam.

**Aplicação no Projeto:**
*   **`InterestCalculationStrategy` (interface)**: Define o método `calculateInterest()`.
*   **`SimpleInterestStrategy` e `HighYieldInterestStrategy` (estratégias concretas)**: Implementam a interface `InterestCalculationStrategy`, cada uma com sua própria lógica de cálculo de juros.
*   **`SavingsAccount` (Contexto)**: Contém uma referência a uma `InterestCalculationStrategy` e delega a ela o cálculo dos juros. A estratégia pode ser alterada em tempo de execução usando o método `setInterestStrategy()`.

**Benefícios:**
*   **Flexibilidade**: Permite que o algoritmo de cálculo de juros seja alterado dinamicamente.
*   **OCP**: Novas estratégias de juros podem ser adicionadas sem modificar a classe `SavingsAccount`.
*   **SRP**: Cada estratégia é responsável apenas por um algoritmo de cálculo de juros.

## 5. Interface Gráfica do Usuário (GUI)

Para tornar o sistema mais interativo e completo, foi desenvolvida uma Interface Gráfica do Usuário (GUI) utilizando **JavaFX**. A GUI integra todas as funcionalidades do sistema bancário, proporcionando uma experiência de usuário mais rica.

**Componentes da GUI:**
*   **`BankingApplication.java`**: A classe principal da aplicação JavaFX, responsável por carregar as telas e iniciar a aplicação.
*   **`LoginController.java` e `login.fxml`**: Implementam a tela de login, onde os usuários podem autenticar-se no sistema. Utiliza o `AuthenticationService` (Singleton) para gerenciar as credenciais.
*   **`MainController.java` e `main.fxml`**: Representam a janela principal da aplicação após o login. Esta tela permite aos usuários:
    *   Visualizar suas contas e saldos.
    *   Criar novas contas (corrente ou poupança).
    *   Realizar depósitos e saques.
    *   Efetuar transferências entre contas.
    *   Calcular juros para contas poupança.
    *   Visualizar o histórico de transações.
*   **`ValidationUtils.java` e `UIUtils.java`**: Classes utilitárias para fornecer validações de entrada e feedback visual (mensagens de erro/sucesso, formatação de valores) consistentes em toda a GUI, melhorando a usabilidade e a robustez.

**Integração com Padrões de Projeto:**
*   A GUI interage primariamente com a `BankingFacade`, demonstrando o benefício do padrão Facade ao simplificar a complexidade do subsistema bancário para a camada de apresentação.
*   O `AuthenticationService` é um Singleton, garantindo que o estado de autenticação seja consistente em toda a aplicação.
*   As validações e o feedback visual seguem o princípio SRP, com classes dedicadas para essas responsabilidades.

## 6. Como Executar o Projeto

Para compilar e executar este projeto, você precisará ter o **Java Development Kit (JDK) 11 ou superior** e o **Apache Maven** instalados.

1.  **Clone o repositório:**
    ```bash
    git clone git@github.com:SamuelMauli/AvaliacaoDesignPatterns.git
    cd AvaliacaoDesignPatterns
    ```

2.  **Compile o projeto:**
    ```bash
    mvn clean install
    ```

3.  **Execute a aplicação via Console (demonstração dos padrões):**
    ```bash
    mvn exec:java -Dexec.mainClass="com.bank.app.Main"
    ```
    A classe `com.bank.app.Main` demonstra a criação de contas, depósitos, saques e a aplicação de padrões de projeto.

4.  **Execute a aplicação GUI (JavaFX):**
    ```bash
    mvn javafx:run
    ```
    Esta é a aplicação com interface gráfica completa. Você pode usar as seguintes credenciais para login:
    *   **Usuário:** `admin` / **Senha:** `admin123`
    *   **Usuário:** `alice` / **Senha:** `alice123`
    *   **Usuário:** `bob` / **Senha:** `bob123`
    *   **Usuário:** `charlie` / **Senha:** `charlie123`

## 7. Testes

Para executar os testes JUnit do projeto:

```bash
mvn test
```

Os testes estão localizados no diretório `src/test/java` e cobrem as funcionalidades principais das classes de conta, da fábrica de contas, da `BankingFacade` e do `AuthenticationService`, garantindo a correção das implementações dos padrões de projeto e da lógica de negócios.
