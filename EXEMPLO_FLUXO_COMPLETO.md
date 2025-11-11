# Exemplo Prático: Como os Padrões Trabalham Juntos

Este documento demonstra como todos os padrões de projeto trabalham juntos em uma operação completa do sistema bancário.

---

## Cenário: Depósito de R$ 500,00 em uma Conta Corrente

Vamos rastrear todo o fluxo de uma operação de depósito, mostrando como cada padrão é utilizado.

### 1. Inicialização do Sistema

```java
// Singleton: AuthenticationService
AuthenticationService authService = AuthenticationService.getInstance();
// Uma única instância gerencia toda a autenticação

// Singleton: TransactionLogger
TransactionLogger logger = TransactionLogger.getInstance();
// Uma única instância registra todas as transações no mesmo arquivo

// Facade: BankingFacade
BankingFacade facade = new BankingFacade();
// Interface simplificada para todas as operações bancárias
```

---

### 2. Criação de uma Conta (Factory Method + Facade)

```java
// Cliente usa a Facade de forma simples
String accountNumber = facade.createAccount(
    AccountType.CHECKING,      // Tipo de conta
    "João Silva",              // Nome do cliente
    1000.0,                    // Saldo inicial
    500.0                      // Limite de cheque especial
);
```

**O que acontece internamente:**

1. **Facade recebe a solicitação**
   ```java
   // BankingFacade.createAccount()
   Account account = AccountFactory.createAccount(...);  // Factory Method
   accounts.put(account.getAccountNumber(), account);     // Armazena no mapa
   ```

2. **Factory Method cria a conta apropriada**
   ```java
   // AccountFactory.createAccount()
   switch (type) {
       case CHECKING:
           return new CheckingAccount(customerName, initialBalance, overdraftLimit);
       // A factory decide qual classe instanciar
   }
   ```

3. **Conta é criada e registrada**
   - Conta é criada com número único (UUID)
   - Saldo inicial é definido
   - Limite de cheque especial é configurado
   - Conta é armazenada no mapa da facade

---

### 3. Registro de Observador (Observer Pattern)

```java
// Observer: AuditService é registrado como observador
Account account = facade.getAccount(accountNumber);
AuditService auditService = new AuditService();
account.addObserver(auditService);
// A partir de agora, o AuditService será notificado de todos os eventos
```

**O que acontece:**
- A conta mantém uma lista de observadores
- AuditService é adicionado à lista
- Qualquer evento na conta notificará o AuditService automaticamente

---

### 4. Depósito de R$ 500,00 (Command + Observer + Singleton)

```java
// Cliente usa a Facade para depositar
facade.deposit(accountNumber, 500.0);
```

**Fluxo completo da operação:**

#### Passo 1: Facade recebe a solicitação
```java
// BankingFacade.deposit()
Account account = accounts.get(accountNumber);  // Busca a conta
Command deposit = new DepositCommand(account, 500.0);  // Command Pattern
deposit.execute();  // Executa o comando
```

#### Passo 2: Command encapsula a operação
```java
// DepositCommand.execute()
account.deposit(500.0);  // Chama o método da conta
logger.log("Deposit: Account XXX, Amount: 500.0, New Balance: 1500.0");  // Singleton
```

#### Passo 3: Conta processa o depósito
```java
// Account.deposit()
if (amount > 0) {
    adjustBalanceAndNotify(500.0, "deposit");  // Atualiza saldo e notifica observadores
}
```

#### Passo 4: Observer Pattern notifica observadores
```java
// Account.adjustBalanceAndNotify()
this.balance += 500.0;  // Atualiza saldo: 1000.0 + 500.0 = 1500.0
notifyObservers("deposit", 500.0);  // Notifica todos os observadores
```

#### Passo 5: Observadores são notificados
```java
// Account.notifyObservers()
for (AccountObserver observer : observers) {
    observer.update(this, "deposit", 500.0);  // Notifica cada observador
}
```

#### Passo 6: AuditService registra a auditoria
```java
// AuditService.update()
logger.log("AUDIT: Account XXX, Event: deposit, Amount: 500.0, Current Balance: 1500.0");
// Singleton: TransactionLogger registra no arquivo
```

---

### 5. Resultado Final

**Arquivo de Log (transactions.log):**
```
2024-01-15T14:30:45 - Deposit: Account abc-123, Amount: 500.0, New Balance: 1500.0
2024-01-15T14:30:45 - AUDIT: Account abc-123, Event: deposit, Amount: 500.0, Current Balance: 1500.0
```

**Estado da Conta:**
- Saldo anterior: R$ 1.000,00
- Depósito: R$ 500,00
- Saldo atual: R$ 1.500,00
- Observadores notificados: AuditService
- Transação registrada: Sim

---

## Cenário 2: Cálculo de Juros em Conta Poupança (Strategy Pattern)

```java
// Cria conta poupança
String savingsAccount = facade.createAccount(
    AccountType.SAVINGS,
    "Maria Santos",
    2000.0,    // Saldo inicial
    0.05       // Taxa de juros (5%)
);

// Obtém a conta
Account account = facade.getAccount(savingsAccount);
SavingsAccount savings = (SavingsAccount) account;

// Registra observador
AuditService audit = new AuditService();
savings.addObserver(audit);

// Calcula juros com estratégia padrão (SimpleInterestStrategy)
savings.calculateInterest();
// Juros = 2000.0 * 0.05 = 100.0
// Novo saldo = 2000.0 + 100.0 = 2100.0

// Troca para estratégia de alto rendimento (Strategy Pattern)
savings.setInterestStrategy(new HighYieldInterestStrategy());

// Calcula juros com nova estratégia
savings.calculateInterest();
// Juros = 2000.0 * (0.05 + 0.01) = 120.0
// Novo saldo = 2100.0 + 120.0 = 2220.0
```

**O que acontece:**

1. **Strategy Pattern**: A estratégia de cálculo de juros pode ser trocada dinamicamente
2. **Observer Pattern**: AuditService é notificado de ambos os cálculos de juros
3. **Singleton**: TransactionLogger registra todas as operações
4. **Facade**: Cliente usa interface simples, complexidade é escondida

---

## Cenário 3: Transferência entre Contas (Command + Facade)

```java
// Cria duas contas
String account1 = facade.createAccount(AccountType.CHECKING, "João", 1000.0, 500.0);
String account2 = facade.createAccount(AccountType.SAVINGS, "Maria", 500.0, 0.05);

// Registra observadores
Account acc1 = facade.getAccount(account1);
Account acc2 = facade.getAccount(account2);
AuditService audit = new AuditService();
acc1.addObserver(audit);
acc2.addObserver(audit);

// Realiza transferência de R$ 300,00
facade.transfer(account1, account2, 300.0);
```

**O que acontece internamente:**

1. **Facade valida a transferência**
   ```java
   // BankingFacade.transfer()
   // Valida contas, saldo, etc.
   ```

2. **Command Pattern executa saque**
   ```java
   Command withdraw = new WithdrawCommand(account1, account1, 300.0);
   withdraw.execute();
   // Saldo da conta1: 1000.0 - 300.0 = 700.0
   // Observer notifica AuditService
   // TransactionLogger registra a transação
   ```

3. **Command Pattern executa depósito**
   ```java
   Command deposit = new DepositCommand(account2, 300.0);
   deposit.execute();
   // Saldo da conta2: 500.0 + 300.0 = 800.0
   // Observer notifica AuditService
   // TransactionLogger registra a transação
   ```

4. **Resultado**
   - Conta1: R$ 700,00 (era R$ 1.000,00)
   - Conta2: R$ 800,00 (era R$ 500,00)
   - 2 transações registradas (saque + depósito)
   - 2 notificações de auditoria (uma para cada conta)

---

## Cenário 4: Aplicação de Decorator (Decorator Pattern)

```java
// Cria conta básica sem cheque especial
Account basicAccount = new CheckingAccount("Carlos", 500.0, 0.0);

// Aplica decorator de proteção contra cheque especial
OverdraftProtectionDecorator protectedAccount = 
    new OverdraftProtectionDecorator(basicAccount, 200.0);

// Registra observador na conta decorada
AuditService audit = new AuditService();
protectedAccount.addObserver(audit);

// Tenta sacar R$ 600,00 (mais que o saldo)
protectedAccount.withdraw(600.0);
// Decorator verifica: 500.0 (saldo) + 200.0 (limite) >= 600.0? Sim!
// Saque permitido: saldo = 500.0 - 600.0 = -100.0
// Observer notifica AuditService
// TransactionLogger registra a transação
```

**O que acontece:**

1. **Decorator Pattern**: Adiciona funcionalidade de cheque especial dinamicamente
2. **Observer Pattern**: Decorator delega notificações para a conta original
3. **Singleton**: TransactionLogger registra a operação
4. **Flexibilidade**: Funcionalidade adicionada sem modificar a classe original

---

## Diagrama de Interação dos Padrões

```
Cliente
  |
  | 1. createAccount()
  v
BankingFacade (Facade)
  |
  | 2. AccountFactory.createAccount() (Factory Method)
  v
AccountFactory
  |
  | 3. new CheckingAccount()
  v
CheckingAccount
  |
  | 4. addObserver(AuditService) (Observer)
  v
Account (mantém lista de observadores)
  |
  |
  | 5. deposit()
  v
BankingFacade
  |
  | 6. new DepositCommand() (Command)
  v
DepositCommand
  |
  | 7. account.deposit()
  | 8. TransactionLogger.getInstance().log() (Singleton)
  v
Account
  |
  | 9. adjustBalanceAndNotify()
  | 10. notifyObservers() (Observer)
  v
AccountObserver (AuditService)
  |
  | 11. update()
  | 12. TransactionLogger.getInstance().log() (Singleton)
  v
TransactionLogger (Singleton)
  |
  | 13. Escreve no arquivo transactions.log
  v
Arquivo de Log
```

---

## Benefícios da Integração dos Padrões

### 1. **Separação de Responsabilidades**
- Cada padrão tem uma responsabilidade específica
- Código organizado e fácil de entender

### 2. **Baixo Acoplamento**
- Componentes são independentes
- Mudanças em um componente não afetam outros

### 3. **Alta Coesão**
- Componentes relacionados estão juntos
- Lógica relacionada está agrupada

### 4. **Flexibilidade**
- Fácil adicionar novos tipos de conta (Factory)
- Fácil adicionar novos observadores (Observer)
- Fácil adicionar novos algoritmos (Strategy)
- Fácil adicionar novas funcionalidades (Decorator)

### 5. **Manutenibilidade**
- Código fácil de modificar
- Fácil de testar
- Fácil de estender

### 6. **Escalabilidade**
- Sistema pode crescer sem grandes refatorações
- Novos recursos podem ser adicionados facilmente

---

## Conclusão

Os padrões de projeto trabalham juntos para criar um sistema:
- **Bem Estruturado**: Cada padrão resolve um problema específico
- **Flexível**: Fácil de modificar e estender
- **Manutenível**: Código limpo e organizado
- **Escalável**: Pode crescer sem grandes refatorações
- **Profissional**: Segue best practices da indústria

A combinação de todos esses padrões resulta em um sistema bancário robusto, flexível e fácil de manter, demonstrando o poder dos padrões de projeto quando aplicados corretamente.

