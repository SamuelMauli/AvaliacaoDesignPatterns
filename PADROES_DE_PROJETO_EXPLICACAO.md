# Explicação dos Padrões de Projeto no Sistema Bancário

Este documento explica detalhadamente todos os padrões de projeto implementados no sistema bancário, como são utilizados e por que são importantes.

---

## 1. Factory Method (AccountFactory)

### O que é?
O **Factory Method** é um padrão criacional que fornece uma interface para criar objetos sem especificar exatamente qual classe será instanciada. A decisão de qual classe criar é delegada para uma fábrica.

### Como é Utilizado no Sistema?
A classe `AccountFactory` é responsável por criar instâncias de diferentes tipos de contas bancárias (`CheckingAccount` ou `SavingsAccount`) baseado no `AccountType` fornecido.

**Exemplo de uso:**
```java
// A BankingFacade usa a factory para criar contas
Account account = AccountFactory.createAccount(
    AccountType.CHECKING, 
    "João Silva", 
    1000.0, 
    500.0  // limite de cheque especial
);
```

**Fluxo de funcionamento:**
1. O cliente (BankingFacade) chama `AccountFactory.createAccount()` passando o tipo de conta
2. A factory verifica o tipo (CHECKING ou SAVINGS)
3. A factory decide qual classe concreta instanciar
4. A factory cria e retorna a instância apropriada
5. O cliente recebe uma referência `Account` sem saber qual classe concreta foi criada

### Por que é Importante?
1. **Encapsulamento**: Esconde a lógica complexa de criação de objetos do cliente
2. **Flexibilidade**: Facilita adicionar novos tipos de conta sem modificar o código cliente
3. **Manutenibilidade**: Centraliza a lógica de criação em um único lugar
4. **Desacoplamento**: O cliente não depende das classes concretas (CheckingAccount, SavingsAccount)
5. **Extensibilidade**: Para adicionar um novo tipo de conta (ex: InvestmentAccount), basta modificar a factory

**Sem o Factory Method:** O cliente teria que conhecer todas as classes concretas e usar múltiplos `if-else` ou `switch` para decidir qual instanciar, aumentando o acoplamento.

---

## 2. Facade (BankingFacade)

### O que é?
O **Facade** é um padrão estrutural que fornece uma interface simplificada para um subsistema complexo. Ele oculta a complexidade interna e fornece um ponto de acesso único.

### Como é Utilizado no Sistema?
A classe `BankingFacade` fornece uma interface simples e unificada para todas as operações bancárias, escondendo a complexidade dos subsistemas (Factory, Command, Observer, etc.).

**Exemplo de uso:**
```java
// Cliente usa a facade de forma simples
BankingFacade facade = new BankingFacade();

// Criar conta - a facade usa AccountFactory internamente
String accountNumber = facade.createAccount(
    AccountType.CHECKING, 
    "Maria", 
    1000.0, 
    500.0
);

// Depositar - a facade usa DepositCommand internamente
facade.deposit(accountNumber, 200.0);

// Sacar - a facade usa WithdrawCommand internamente
facade.withdraw(accountNumber, 100.0);
```

**Fluxo de funcionamento:**
1. Cliente chama métodos simples da facade (createAccount, deposit, withdraw)
2. A facade coordena múltiplos subsistemas:
   - Usa `AccountFactory` para criar contas
   - Usa `DepositCommand`/`WithdrawCommand` para operações
   - Usa `TransactionLogger` para registrar transações
   - Gerencia o mapa de contas
3. Cliente recebe resultado simples sem conhecer a complexidade interna

### Por que é Importante?
1. **Simplicidade**: Cliente não precisa conhecer múltiplos subsistemas
2. **Interface Unificada**: Um único ponto de acesso para todas as operações bancárias
3. **Redução de Acoplamento**: Cliente depende apenas da facade, não dos subsistemas
4. **Facilita Manutenção**: Mudanças internas não afetam o cliente
5. **Melhora Legibilidade**: Código do cliente fica mais limpo e fácil de entender

**Sem o Facade:** O cliente teria que:
- Conhecer AccountFactory para criar contas
- Conhecer DepositCommand/WithdrawCommand para operações
- Gerenciar o mapa de contas manualmente
- Lidar com TransactionLogger diretamente
- Coordenar múltiplos objetos manualmente

---

## 3. Command (DepositCommand, WithdrawCommand)

### O que é?
O **Command** é um padrão comportamental que encapsula uma solicitação como um objeto, permitindo parametrizar clientes com diferentes solicitações, enfileirar operações, registrar comandos e suportar operações desfazíveis.

### Como é Utilizado no Sistema?
As classes `DepositCommand` e `WithdrawCommand` encapsulam operações de depósito e saque como objetos que podem ser executados, registrados e manipulados.

**Exemplo de uso:**
```java
// A BankingFacade cria e executa comandos
Command deposit = new DepositCommand(account, 200.0);
deposit.execute();  // Executa o depósito e registra no log

Command withdraw = new WithdrawCommand(account, account, 100.0);
withdraw.execute();  // Executa o saque e registra no log
```

**Fluxo de funcionamento:**
1. BankingFacade cria um comando (DepositCommand ou WithdrawCommand)
2. O comando armazena:
   - A conta envolvida
   - O valor da operação
   - Referência ao TransactionLogger
3. Quando `execute()` é chamado:
   - O comando chama o método apropriado da conta (deposit/withdraw)
   - O comando registra a transação no TransactionLogger
4. A operação é completada e registrada

### Por que é Importante?
1. **Encapsulamento**: Transforma operações em objetos manipuláveis
2. **Flexibilidade**: Permite enfileirar comandos para execução posterior
3. **Desfazer/Refazer**: Base para implementar undo/redo (não implementado, mas possível)
4. **Logging Automático**: Cada comando registra automaticamente a transação
5. **Separação de Responsabilidades**: A lógica de execução está separada da lógica de negócio
6. **Transações**: Facilita implementar transações atômicas (todos ou nenhum)

**Sem o Command:** Cada operação teria que:
- Ser executada diretamente na conta
- Registrar no log manualmente
- Não poderia ser enfileirada ou desfeita
- Não teria histórico de operações

**Possíveis Extensões:**
- Enfileirar comandos para processamento em lote
- Implementar undo/redo de operações
- Implementar transações atômicas (rollback em caso de erro)
- Executar comandos de forma assíncrona

---

## 4. Observer (AccountObserver, AuditService)

### O que é?
O **Observer** é um padrão comportamental que define uma dependência um-para-muitos entre objetos. Quando um objeto muda de estado, todos os seus dependentes são notificados e atualizados automaticamente.

### Como é Utilizado no Sistema?
A classe `Account` (Subject) mantém uma lista de observadores (`AccountObserver`). Quando eventos ocorrem na conta (depósito, saque, cálculo de juros), todos os observadores registrados são notificados automaticamente.

**Exemplo de uso:**
```java
// Cria uma conta e um serviço de auditoria
Account account = new CheckingAccount("João", 1000.0, 500.0);
AuditService auditService = new AuditService();

// Registra o auditService como observador
account.addObserver(auditService);

// Quando um depósito é feito, o AuditService é notificado automaticamente
account.deposit(200.0);
// O AuditService registra automaticamente no log: 
// "AUDIT: Account XXX, Event: deposit, Amount: 200.0, Current Balance: 1200.0"
```

**Fluxo de funcionamento:**
1. Observador (AuditService) é registrado na conta usando `addObserver()`
2. Quando um evento ocorre na conta (depósito, saque, etc.):
   - A conta chama `adjustBalanceAndNotify()`
   - Este método atualiza o saldo e chama `notifyObservers()`
   - `notifyObservers()` itera sobre todos os observadores
   - Cada observador recebe uma chamada `update()` com os detalhes do evento
3. O AuditService registra o evento no TransactionLogger automaticamente

### Por que é Importante?
1. **Desacoplamento**: A conta não conhece os observadores concretos, apenas a interface
2. **Extensibilidade**: Novos observadores podem ser adicionados sem modificar a Account
3. **Notificação Automática**: Observadores são notificados automaticamente de eventos
4. **Múltiplos Observadores**: Múltiplos serviços podem observar a mesma conta
5. **Separação de Responsabilidades**: A conta não precisa saber sobre auditoria, logging, etc.

**Sem o Observer:** 
- A conta teria que conhecer todos os serviços que precisam ser notificados
- Adicionar um novo serviço (ex: notificação por email) exigiria modificar a classe Account
- Código acoplado e difícil de manter

**Possíveis Extensões:**
- Notificação por email quando saldo fica negativo
- Alertas SMS para transações grandes
- Dashboard em tempo real mostrando transações
- Análise de comportamento do cliente

---

## 5. Strategy (InterestCalculationStrategy)

### O que é?
O **Strategy** é um padrão comportamental que define uma família de algoritmos, encapsula cada um deles e os torna intercambiáveis. O Strategy permite que o algoritmo varie independentemente dos clientes que o utilizam.

### Como é Utilizado no Sistema?
A interface `InterestCalculationStrategy` define o contrato para cálculo de juros. Classes concretas (`SimpleInterestStrategy`, `HighYieldInterestStrategy`) implementam algoritmos diferentes. A `SavingsAccount` usa uma estratégia que pode ser trocada dinamicamente.

**Exemplo de uso:**
```java
// Cria uma conta poupança com estratégia de juros simples
SavingsAccount account = new SavingsAccount("Maria", 1000.0, 0.05);
// Por padrão, usa SimpleInterestStrategy

// Calcula juros com estratégia simples: 1000 * 0.05 = 50
account.calculateInterest();

// Troca para estratégia de alto rendimento
account.setInterestStrategy(new HighYieldInterestStrategy());
// Agora calcula: 1000 * (0.05 + 0.01) = 60
account.calculateInterest();
```

**Fluxo de funcionamento:**
1. SavingsAccount mantém uma referência a `InterestCalculationStrategy`
2. Por padrão, usa `SimpleInterestStrategy`
3. Quando `calculateInterest()` é chamado:
   - Chama `strategy.calculateInterest(balance, interestRate)`
   - A estratégia retorna o valor dos juros calculados
   - Os juros são adicionados ao saldo
4. A estratégia pode ser trocada em tempo de execução usando `setInterestStrategy()`

### Por que é Importante?
1. **Flexibilidade**: Algoritmo pode ser trocado em tempo de execução
2. **Extensibilidade**: Novos algoritmos podem ser adicionados sem modificar SavingsAccount
3. **Reutilização**: Estratégias podem ser reutilizadas em outras contas
4. **Testabilidade**: Cada estratégia pode ser testada independentemente
5. **Manutenibilidade**: Lógica de cálculo está isolada em classes separadas

**Sem o Strategy:**
- SavingsAccount teria múltiplos métodos (calculateSimpleInterest, calculateCompoundInterest, etc.)
- Adicionar novo algoritmo exigiria modificar SavingsAccount
- Código com muitos if-else ou switch
- Difícil de testar e manter

**Possíveis Extensões:**
- Juros compostos (compound interest)
- Juros progressivos (maior saldo = maior taxa)
- Juros com bônus por tempo de permanência
- Juros personalizados por cliente VIP

---

## 6. Decorator (AccountDecorator, OverdraftProtectionDecorator)

### O que é?
O **Decorator** é um padrão estrutural que permite adicionar novos comportamentos a objetos dinamicamente, envolvendo-os com objetos decoradores. Fornece uma alternativa flexível à herança para estender funcionalidades.

### Como é Utilizado no Sistema?
A classe abstrata `AccountDecorator` serve como base para decoradores. `OverdraftProtectionDecorator` adiciona funcionalidade de proteção contra cheque especial a qualquer conta, envolvendo-a com comportamento adicional.

**Exemplo de uso:**
```java
// Cria uma conta básica
Account basicAccount = new CheckingAccount("Carlos", 500.0, 0.0);

// Aplica o decorator de proteção contra cheque especial
OverdraftProtectionDecorator protectedAccount = 
    new OverdraftProtectionDecorator(basicAccount, 200.0);

// Agora a conta tem proteção adicional de cheque especial
protectedAccount.withdraw(600.0);  // Permite saque mesmo sem saldo suficiente
// Saldo: 500 - 600 = -100 (dentro do limite de 200)
```

**Fluxo de funcionamento:**
1. Cria uma conta básica (Account)
2. Envolve a conta com um decorador (OverdraftProtectionDecorator)
3. O decorador mantém referência à conta original (decoratedAccount)
4. Quando uma operação é chamada:
   - O decorador intercepta a chamada
   - Adiciona comportamento adicional (verificação de limite)
   - Delega para a conta original se necessário
5. O cliente usa o decorador como se fosse uma Account normal

### Por que é Importante?
1. **Flexibilidade**: Funcionalidades podem ser adicionadas/removidas dinamicamente
2. **Composição sobre Herança**: Evita explosão de classes (CheckingAccount, CheckingAccountWithOverdraft, etc.)
3. **Reutilização**: Mesmo decorador pode ser aplicado a diferentes tipos de conta
4. **Extensibilidade**: Novos decoradores podem ser criados sem modificar classes existentes
5. **Separação de Responsabilidades**: Cada decorador adiciona uma responsabilidade específica

**Sem o Decorator:**
- Seria necessário criar múltiplas subclasses:
  - CheckingAccount
  - CheckingAccountWithOverdraft
  - CheckingAccountWithInsurance
  - CheckingAccountWithOverdraftAndInsurance
  - etc.
- Explosão combinatória de classes
- Dificuldade para combinar funcionalidades

**Possíveis Extensões:**
- InsuranceDecorator (seguro da conta)
- RewardsDecorator (programa de pontos)
- NotificationDecorator (notificações por email/SMS)
- FeeWaiverDecorator (isenção de taxas)

---

## 7. Singleton (TransactionLogger, AuthenticationService)

### O que é?
O **Singleton** é um padrão criacional que garante que uma classe tenha apenas uma instância e fornece um ponto de acesso global a essa instância.

### Como é Utilizado no Sistema?

#### 7.1 TransactionLogger
Garante que todas as transações sejam registradas no mesmo arquivo de log, evitando múltiplas instâncias que poderiam causar conflitos.

**Exemplo de uso:**
```java
// Obtém a instância única do logger
TransactionLogger logger = TransactionLogger.getInstance();

// Qualquer parte do sistema usa a mesma instância
logger.log("Deposit: Account XXX, Amount: 100.0");

// Outra parte do sistema usa a mesma instância
TransactionLogger logger2 = TransactionLogger.getInstance();
// logger e logger2 são a mesma instância
```

#### 7.2 AuthenticationService
Garante que apenas uma instância gerencie a autenticação em toda a aplicação, mantendo consistência no estado de login.

**Exemplo de uso:**
```java
// Obtém a instância única do serviço de autenticação
AuthenticationService authService = AuthenticationService.getInstance();

// Autentica um usuário
authService.authenticate("admin", "admin123");

// Qualquer parte da aplicação pode acessar o usuário logado
User currentUser = authService.getCurrentUser();
```

**Fluxo de funcionamento:**
1. Primeira chamada a `getInstance()` cria a instância
2. Chamadas subsequentes retornam a mesma instância
3. Construtor é privado, impedindo criação direta
4. Sincronização garante segurança em ambientes multi-threaded

### Por que é Importante?

#### TransactionLogger:
1. **Consistência**: Todas as transações são registradas no mesmo arquivo
2. **Evita Conflitos**: Múltiplas instâncias poderiam causar problemas de escrita
3. **Recursos Compartilhados**: Arquivo de log é um recurso compartilhado que deve ser único
4. **Acesso Global**: Qualquer parte do sistema pode registrar transações facilmente

#### AuthenticationService:
1. **Estado Único**: Apenas um usuário pode estar logado por vez
2. **Consistência**: Estado de autenticação é consistente em toda a aplicação
3. **Segurança**: Evita múltiplas instâncias com estados diferentes
4. **Acesso Global**: Qualquer parte da aplicação pode verificar o usuário logado

**Sem o Singleton:**
- TransactionLogger: Múltiplas instâncias poderiam escrever em arquivos diferentes ou causar conflitos
- AuthenticationService: Diferentes partes da aplicação poderiam ter estados de autenticação diferentes
- Inconsistência de dados e comportamentos imprevisíveis

**Quando NÃO usar Singleton:**
- Quando múltiplas instâncias são necessárias
- Quando o estado precisa ser isolado por thread/requisição
- Quando testabilidade é crítica (singletons são difíceis de testar)

---

## Resumo: Por que esses Padrões são Importantes?

### Benefícios Gerais:
1. **Código Limpo**: Código mais organizado, legível e fácil de entender
2. **Manutenibilidade**: Fácil de modificar e estender
3. **Testabilidade**: Componentes podem ser testados independentemente
4. **Reutilização**: Componentes podem ser reutilizados em outros contextos
5. **Desacoplamento**: Baixo acoplamento entre componentes
6. **Flexibilidade**: Sistema pode evoluir sem quebrar código existente

### Princípios SOLID Aplicados:
- **SRP (Single Responsibility Principle)**: Cada classe tem uma responsabilidade única
- **OCP (Open/Closed Principle)**: Aberto para extensão, fechado para modificação
- **LSP (Liskov Substitution Principle)**: Subtipos podem substituir seus tipos base
- **ISP (Interface Segregation Principle)**: Interfaces específicas (Depositable, Withdrawable)
- **DIP (Dependency Inversion Principle)**: Dependências de abstrações, não implementações

### Escalabilidade:
O sistema pode crescer facilmente:
- Novos tipos de conta (Factory Method)
- Novos tipos de operações (Command)
- Novos observadores (Observer)
- Novos algoritmos de juros (Strategy)
- Novas funcionalidades (Decorator)
- Novos serviços compartilhados (Singleton)

---

## Conclusão

Os padrões de projeto implementados neste sistema bancário fornecem:
- **Arquitetura Robusta**: Sistema bem estruturado e organizado
- **Facilidade de Manutenção**: Fácil de modificar e estender
- **Qualidade de Código**: Código limpo, testável e reutilizável
- **Flexibilidade**: Sistema pode evoluir sem grandes refatorações
- **Best Practices**: Segue princípios SOLID e boas práticas de desenvolvimento

Cada padrão resolve um problema específico e contribui para a qualidade geral do sistema, tornando-o profissional, escalável e fácil de manter.

