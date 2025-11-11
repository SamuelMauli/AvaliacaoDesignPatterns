# Arquitetura do Sistema Bancário - Diagrama de Padrões

## Visão Geral da Arquitetura

```
┌─────────────────────────────────────────────────────────────────┐
│                         CLIENTE (GUI)                            │
│                    (LoginController, MainController)             │
└──────────────────────────────┬──────────────────────────────────┘
                               │
                               │ Usa interface simples
                               ▼
┌─────────────────────────────────────────────────────────────────┐
│                      BANKING FACADE                              │
│                   (Padrão: Facade)                               │
│  • createAccount()                                               │
│  • deposit()                                                     │
│  • withdraw()                                                    │
│  • transfer()                                                    │
└───────┬──────────────────┬──────────────────┬───────────────────┘
        │                  │                  │
        │ Factory Method   │ Command Pattern  │ Singleton
        ▼                  ▼                  ▼
┌───────────────┐  ┌──────────────┐  ┌─────────────────────┐
│ AccountFactory│  │ DepositCommand│  │ TransactionLogger   │
│               │  │ WithdrawCommand│  │ (Singleton)         │
└───────┬───────┘  └──────┬───────┘  └─────────────────────┘
        │                  │
        │ Cria             │ Usa
        ▼                  ▼
┌─────────────────────────────────────────────────────────────────┐
│                         ACCOUNT                                  │
│            (CheckingAccount, SavingsAccount)                     │
│  • deposit()                                                     │
│  • withdraw()                                                    │
│  • addObserver()                                                 │
│  • notifyObservers()                                             │
└───────┬──────────────────┬──────────────────┬───────────────────┘
        │                  │                  │
        │ Observer         │ Strategy         │ Decorator
        ▼                  ▼                  ▼
┌───────────────┐  ┌──────────────┐  ┌─────────────────────┐
│ AuditService  │  │ InterestCalc │  │ AccountDecorator    │
│ (Observer)    │  │ Strategy     │  │ OverdraftProtection │
└───────┬───────┘  └──────┬───────┘  └─────────────────────┘
        │                  │
        │ Usa              │ Implementações
        ▼                  ▼
┌───────────────┐  ┌──────────────────────────────────────┐
│ Transaction   │  │ SimpleInterestStrategy               │
│ Logger        │  │ HighYieldInterestStrategy            │
│ (Singleton)   │  └──────────────────────────────────────┘
└───────────────┘
```

---

## Fluxo de Dados: Criação de Conta

```
1. Cliente (GUI)
   │
   │ facade.createAccount(CHECKING, "João", 1000.0, 500.0)
   ▼
2. BankingFacade (Facade)
   │
   │ AccountFactory.createAccount(...)
   ▼
3. AccountFactory (Factory Method)
   │
   │ new CheckingAccount(...)
   ▼
4. CheckingAccount
   │
   │ Conta criada com número único (UUID)
   │ Saldo inicial: 1000.0
   │ Limite cheque especial: 500.0
   ▼
5. BankingFacade
   │
   │ accounts.put(accountNumber, account)
   │ Retorna accountNumber
   ▼
6. Cliente (GUI)
   │
   │ Recebe accountNumber
   │ Exibe na interface
```

---

## Fluxo de Dados: Depósito

```
1. Cliente (GUI)
   │
   │ facade.deposit(accountNumber, 500.0)
   ▼
2. BankingFacade (Facade)
   │
   │ Account account = accounts.get(accountNumber)
   │ new DepositCommand(account, 500.0)
   ▼
3. DepositCommand (Command)
   │
   │ account.deposit(500.0)
   │ TransactionLogger.getInstance().log(...)
   ▼
4. Account
   │
   │ adjustBalanceAndNotify(500.0, "deposit")
   │ balance += 500.0
   │ notifyObservers("deposit", 500.0)
   ▼
5. Account.notifyObservers() (Observer)
   │
   │ for (observer : observers) {
   │     observer.update(this, "deposit", 500.0)
   │ }
   ▼
6. AuditService (Observer)
   │
   │ TransactionLogger.getInstance().log("AUDIT: ...")
   ▼
7. TransactionLogger (Singleton)
   │
   │ Escreve no arquivo transactions.log
   │ "2024-01-15T14:30:45 - Deposit: Account XXX, Amount: 500.0"
   │ "2024-01-15T14:30:45 - AUDIT: Account XXX, Event: deposit, Amount: 500.0"
```

---

## Fluxo de Dados: Cálculo de Juros

```
1. Cliente (GUI)
   │
   │ savingsAccount.calculateInterest()
   ▼
2. SavingsAccount
   │
   │ interestStrategy.calculateInterest(balance, interestRate)
   ▼
3. InterestCalculationStrategy (Strategy)
   │
   │ SimpleInterestStrategy: balance * interestRate
   │ HighYieldInterestStrategy: balance * (interestRate + 0.01)
   ▼
4. SavingsAccount
   │
   │ adjustBalanceAndNotify(interest, "interest_calculation")
   │ balance += interest
   │ notifyObservers("interest_calculation", interest)
   ▼
5. Account.notifyObservers() (Observer)
   │
   │ for (observer : observers) {
   │     observer.update(this, "interest_calculation", interest)
   │ }
   ▼
6. AuditService (Observer)
   │
   │ TransactionLogger.getInstance().log("AUDIT: ...")
   ▼
7. TransactionLogger (Singleton)
   │
   │ Escreve no arquivo transactions.log
```

---

## Fluxo de Dados: Aplicação de Decorator

```
1. Cliente
   │
   │ Account basicAccount = new CheckingAccount(...)
   │ OverdraftProtectionDecorator decorated = 
   │     new OverdraftProtectionDecorator(basicAccount, 200.0)
   ▼
2. OverdraftProtectionDecorator (Decorator)
   │
   │ Mantém referência a basicAccount (decoratedAccount)
   │ Adiciona funcionalidade de cheque especial
   ▼
3. Cliente
   │
   │ decorated.withdraw(600.0)
   ▼
4. OverdraftProtectionDecorator
   │
   │ Verifica: decoratedAccount.getBalance() + overdraftLimit >= 600.0
   │ Se sim: decoratedAccount.adjustBalanceAndNotify(-600.0, "withdraw")
   ▼
5. CheckingAccount (decoratedAccount)
   │
   │ balance -= 600.0
   │ notifyObservers("withdraw", 600.0)
   ▼
6. Account.notifyObservers() (Observer)
   │
   │ Notifica observadores (AuditService)
   ▼
7. AuditService (Observer)
   │
   │ TransactionLogger.getInstance().log("AUDIT: ...")
   ▼
8. TransactionLogger (Singleton)
   │
   │ Escreve no arquivo transactions.log
```

---

## Diagrama de Classes Simplificado

```
┌─────────────────────────────────────────────────────────────┐
│                        CLIENTE                               │
│                  (LoginController, MainController)           │
└────────────────────────────┬────────────────────────────────┘
                             │
                             │ usa
                             ▼
┌─────────────────────────────────────────────────────────────┐
│                     BANKING FACADE                           │
│  - accounts: Map<String, Account>                           │
│  + createAccount(...): String                               │
│  + deposit(accountNumber, amount): void                     │
│  + withdraw(accountNumber, amount): void                    │
│  + transfer(from, to, amount): boolean                      │
└──────┬──────────────┬──────────────┬────────────────────────┘
       │              │              │
       │ cria         │ cria         │ obtém
       ▼              ▼              ▼
┌─────────────┐  ┌──────────┐  ┌──────────────────┐
│ AccountFactory│ │ Command  │  │ TransactionLogger│
│ + createAccount│ │ + execute│  │ (Singleton)      │
└──────┬──────┘  └────┬─────┘  └──────────────────┘
       │              │
       │ cria         │ usa
       ▼              ▼
┌─────────────────────────────────────────────────────────────┐
│                       ACCOUNT                                │
│  - accountNumber: String                                    │
│  - balance: double                                          │
│  - customerName: String                                     │
│  - observers: List<AccountObserver>                         │
│  + deposit(amount): void                                    │
│  + addObserver(observer): void                              │
│  + notifyObservers(eventType, amount): void                 │
└──────┬──────────────┬──────────────┬────────────────────────┘
       │              │              │
       │              │              │
       ▼              ▼              ▼
┌─────────────┐  ┌──────────┐  ┌──────────────────┐
│ CheckingAccount│ │ SavingsAccount│ │ AccountDecorator│
│ - overdraftLimit│ │ - interestRate│ │ - decoratedAccount│
│ + withdraw()    │ │ - interestStrategy│ │ + withdraw()     │
└─────────────┘  └────┬─────┘  └──────────────────┘
                      │
                      │ usa
                      ▼
            ┌─────────────────────┐
            │ InterestCalculation │
            │ Strategy            │
            │ + calculateInterest │
            └──────┬──────────────┘
                   │
                   │ implementa
                   ▼
    ┌──────────────────────────────────┐
    │ SimpleInterestStrategy           │
    │ HighYieldInterestStrategy        │
    └──────────────────────────────────┘
```

---

## Diagrama de Sequência: Operação Completa

```
Cliente    BankingFacade    AccountFactory    Account    DepositCommand    AuditService    TransactionLogger
   │            │                 │             │             │                │                  │
   │──createAccount()────────────>│             │             │                │                  │
   │            │                 │──createAccount()─────────>│                │                │
   │            │                 │<───────────Account────────│                │                │
   │            │<────accountNumber─────────────│             │                │                │
   │            │                 │             │             │                │                │
   │──addObserver()───────────────┼────────────┼────────────>│                │                │
   │            │                 │             │             │                │                │
   │──deposit()──────────────────>│             │             │                │                │
   │            │──new DepositCommand()────────┼─────────────>│                │                │
   │            │                 │             │             │──execute()─────>│                │
   │            │                 │             │             │──deposit()─────>│                │
   │            │                 │             │──notifyObservers()───────────>│                │
   │            │                 │             │             │                │──update()──────>│
   │            │                 │             │             │                │──log()─────────>│
   │            │                 │             │             │                │                │──log()
   │            │                 │             │             │<───────────────│                │
   │            │                 │             │<────────────│                │                │
   │<───────────│                 │             │             │                │                │
```

---

## Mapa de Dependências entre Padrões

```
┌──────────────────────────────────────────────────────────────┐
│                    PADRÕES E SUAS DEPENDÊNCIAS               │
└──────────────────────────────────────────────────────────────┘

1. SINGLETON (TransactionLogger, AuthenticationService)
   │
   │ Usado por: Command, Observer, Facade
   │
   │ Não depende de outros padrões
   │

2. FACTORY METHOD (AccountFactory)
   │
   │ Usado por: Facade
   │
   │ Depende de: Account (classes concretas)
   │

3. COMMAND (DepositCommand, WithdrawCommand)
   │
   │ Usado por: Facade
   │
   │ Depende de: Account, Singleton (TransactionLogger)
   │

4. OBSERVER (AccountObserver, AuditService)
   │
   │ Usado por: Account
   │
   │ Depende de: Account, Singleton (TransactionLogger)
   │

5. STRATEGY (InterestCalculationStrategy)
   │
   │ Usado por: SavingsAccount
   │
   │ Depende de: Nenhum (interface independente)
   │

6. DECORATOR (AccountDecorator, OverdraftProtectionDecorator)
   │
   │ Usado por: Cliente (opcional)
   │
   │ Depende de: Account
   │

7. FACADE (BankingFacade)
   │
   │ Usado por: Cliente (GUI)
   │
   │ Depende de: Factory Method, Command, Singleton
   │

┌──────────────────────────────────────────────────────────────┐
│                    HIERARQUIA DE PADRÕES                     │
└──────────────────────────────────────────────────────────────┘

Nível 1 (Fundamental):
  - Singleton (recursos compartilhados)

Nível 2 (Criação):
  - Factory Method (criação de objetos)

Nível 3 (Estrutural):
  - Decorator (adicionar funcionalidades)
  - Strategy (algoritmos intercambiáveis)

Nível 4 (Comportamental):
  - Command (operaciones encapsuladas)
  - Observer (notificações)

Nível 5 (Orquestração):
  - Facade (interface unificada)
```

---

## Benefícios da Arquitetura em Camadas

### 🎯 Camada 1: Fundamentos (Singleton)
- **TransactionLogger**: Recursos compartilhados
- **AuthenticationService**: Estado único

### 🏗️ Camada 2: Criação (Factory Method)
- **AccountFactory**: Criação de objetos

### 🔧 Camada 3: Estrutural (Decorator, Strategy)
- **Decorator**: Funcionalidades dinâmicas
- **Strategy**: Algoritmos intercambiáveis

### 🔄 Camada 4: Comportamental (Command, Observer)
- **Command**: Operações encapsuladas
- **Observer**: Notificações automáticas

### 🎨 Camada 5: Orquestração (Facade)
- **BankingFacade**: Interface unificada

---

## Conclusão da Arquitetura

### ✅ Vantagens:
1. **Separação de Responsabilidades**: Cada camada tem responsabilidade clara
2. **Baixo Acoplamento**: Camadas superiores não dependem de detalhes de implementação
3. **Alta Coesão**: Componentes relacionados estão juntos
4. **Flexibilidade**: Fácil modificar ou estender cada camada
5. **Testabilidade**: Cada camada pode ser testada independentemente

### 🚀 Resultado:
Uma arquitetura **robusta, escalável e manutenível** que demonstra o poder dos padrões de projeto quando aplicados em conjunto de forma estruturada.

---

**Esta arquitetura garante que o sistema seja:**
- ✅ Fácil de entender
- ✅ Fácil de modificar
- ✅ Fácil de testar
- ✅ Fácil de estender
- ✅ Fácil de manter

