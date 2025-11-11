# Índice de Referência Rápida: Padrões de Projeto no Sistema

## 📚 Documentação Criada

1. **PADROES_DE_PROJETO_EXPLICACAO.md** - Explicação detalhada de cada padrão
2. **EXEMPLO_FLUXO_COMPLETO.md** - Exemplos práticos de como os padrões trabalham juntos
3. **RESUMO_IMPORTANCIA_PADROES.md** - Resumo da importância de cada padrão
4. **ARQUITETURA_SISTEMA.md** - Diagramas de arquitetura e fluxo de dados
5. **INDICE_PADROES.md** - Este arquivo (índice de referência rápida)

---

## 🗂️ Localização dos Padrões no Código

### 1. Factory Method (AccountFactory)

**Arquivo:** `src/main/java/com/bank/factory/AccountFactory.java`

**Uso no Sistema:**
- `BankingFacade.createAccount()` → chama `AccountFactory.createAccount()`
- Cria instâncias de `CheckingAccount` ou `SavingsAccount`

**Classes Relacionadas:**
- `AccountType` (enum) - Define tipos de conta
- `CheckingAccount` - Classe concreta criada pela factory
- `SavingsAccount` - Classe concreta criada pela factory

---

### 2. Facade (BankingFacade)

**Arquivo:** `src/main/java/com/bank/facade/BankingFacade.java`

**Uso no Sistema:**
- `LoginController` - Não usa diretamente
- `MainController` - Usa para todas as operações bancárias
  - `bankingFacade.createAccount()`
  - `bankingFacade.deposit()`
  - `bankingFacade.withdraw()`
  - `bankingFacade.transfer()`
  - `bankingFacade.getTransactionHistory()`

**Classes Relacionadas:**
- `AccountFactory` - Usado para criar contas
- `DepositCommand` - Usado para depósitos
- `WithdrawCommand` - Usado para saques
- `TransactionLogger` - Usado para obter histórico

---

### 3. Command (DepositCommand, WithdrawCommand)

**Arquivos:**
- `src/main/java/com/bank/command/Command.java` (interface)
- `src/main/java/com/bank/command/DepositCommand.java`
- `src/main/java/com/bank/command/WithdrawCommand.java`

**Uso no Sistema:**
- `BankingFacade.deposit()` → cria `DepositCommand` e executa
- `BankingFacade.withdraw()` → cria `WithdrawCommand` e executa
- `BankingFacade.transfer()` → cria `WithdrawCommand` e `DepositCommand`

**Classes Relacionadas:**
- `Account` - Objeto sobre o qual o comando opera
- `TransactionLogger` - Usado para registrar transações

---

### 4. Observer (AccountObserver, AuditService)

**Arquivos:**
- `src/main/java/com/bank/observer/AccountObserver.java` (interface)
- `src/main/java/com/bank/observer/AuditService.java`

**Uso no Sistema:**
- `Account` - Mantém lista de observadores
- `Account.addObserver()` - Registra observador
- `Account.notifyObservers()` - Notifica observadores
- `AuditService` - Observador que registra eventos no log
- `Main.java` - Registra `AuditService` nas contas criadas

**Classes Relacionadas:**
- `Account` - Subject que notifica observadores
- `TransactionLogger` - Usado pelo `AuditService` para registrar eventos

---

### 5. Strategy (InterestCalculationStrategy)

**Arquivos:**
- `src/main/java/com/bank/strategy/InterestCalculationStrategy.java` (interface)
- `src/main/java/com/bank/strategy/SimpleInterestStrategy.java`
- `src/main/java/com/bank/strategy/HighYieldInterestStrategy.java`

**Uso no Sistema:**
- `SavingsAccount` - Usa estratégia para calcular juros
- `SavingsAccount.calculateInterest()` - Chama a estratégia
- `SavingsAccount.setInterestStrategy()` - Troca estratégia dinamicamente
- `MainController.handleCalculateInterest()` - Calcula juros na GUI

**Classes Relacionadas:**
- `SavingsAccount` - Cliente que usa a estratégia
- `InterestBearing` - Interface que define cálculo de juros

---

### 6. Decorator (AccountDecorator, OverdraftProtectionDecorator)

**Arquivos:**
- `src/main/java/com/bank/decorator/AccountDecorator.java` (classe abstrata)
- `src/main/java/com/bank/decorator/OverdraftProtectionDecorator.java`

**Uso no Sistema:**
- `Main.java` - Demonstra uso do decorator
- `OverdraftProtectionDecorator` - Adiciona proteção contra cheque especial
- Não é usado diretamente na GUI (exemplo de uso no Main.java)

**Classes Relacionadas:**
- `Account` - Classe base que é decorada
- `Withdrawable` - Interface necessária para o decorator

---

### 7. Singleton (TransactionLogger, AuthenticationService)

#### 7.1 TransactionLogger

**Arquivo:** `src/main/java/com/bank/logger/TransactionLogger.java`

**Uso no Sistema:**
- `DepositCommand.execute()` → `TransactionLogger.getInstance().log()`
- `WithdrawCommand.execute()` → `TransactionLogger.getInstance().log()`
- `AuditService.update()` → `TransactionLogger.getInstance().log()`
- `BankingFacade.getTransactionHistory()` → `TransactionLogger.getInstance().getLogs()`
- `MainController.refreshTransactionHistory()` → Lê logs para exibir na GUI

**Classes Relacionadas:**
- `DepositCommand` - Registra depósitos
- `WithdrawCommand` - Registra saques
- `AuditService` - Registra auditoria

#### 7.2 AuthenticationService

**Arquivo:** `src/main/java/com/bank/gui/model/AuthenticationService.java`

**Uso no Sistema:**
- `LoginController` - Usa para autenticar usuários
- `MainController` - Usa para obter usuário logado
- `BankingApplication` - Não usa diretamente

**Classes Relacionadas:**
- `User` - Representa um usuário do sistema
- `LoginController` - Interface de login

---

## 📍 Mapa de Referências no Código

### LoginController
- ✅ **Singleton**: `AuthenticationService.getInstance()`
- ❌ Factory Method
- ❌ Facade
- ❌ Command
- ❌ Observer
- ❌ Strategy
- ❌ Decorator

### MainController
- ✅ **Singleton**: `AuthenticationService.getInstance()`
- ✅ **Facade**: `BankingFacade` (criação de contas, operações)
- ❌ Factory Method (usado indiretamente via Facade)
- ❌ Command (usado indiretamente via Facade)
- ❌ Observer (usado indiretamente via Account)
- ✅ **Strategy**: `SavingsAccount.calculateInterest()` (cálculo de juros)
- ❌ Decorator

### BankingFacade
- ✅ **Factory Method**: `AccountFactory.createAccount()`
- ✅ **Command**: `DepositCommand`, `WithdrawCommand`
- ✅ **Singleton**: `TransactionLogger.getInstance().getLogs()`
- ❌ Observer (usado indiretamente via Account)
- ❌ Strategy
- ❌ Decorator

### Account
- ✅ **Observer**: `addObserver()`, `notifyObservers()`
- ❌ Factory Method
- ❌ Facade
- ❌ Command
- ❌ Strategy
- ❌ Decorator

### SavingsAccount
- ✅ **Strategy**: `InterestCalculationStrategy`
- ✅ **Observer**: Herda de `Account`
- ❌ Factory Method (usado para criar)
- ❌ Facade
- ❌ Command
- ❌ Decorator

### DepositCommand / WithdrawCommand
- ✅ **Command**: Implementam interface `Command`
- ✅ **Singleton**: `TransactionLogger.getInstance()`
- ❌ Factory Method
- ❌ Facade
- ❌ Observer
- ❌ Strategy
- ❌ Decorator

### AuditService
- ✅ **Observer**: Implementa `AccountObserver`
- ✅ **Singleton**: `TransactionLogger.getInstance()`
- ❌ Factory Method
- ❌ Facade
- ❌ Command
- ❌ Strategy
- ❌ Decorator

### OverdraftProtectionDecorator
- ✅ **Decorator**: Estende `AccountDecorator`
- ✅ **Observer**: Delega para conta decorada
- ❌ Factory Method
- ❌ Facade
- ❌ Command
- ❌ Strategy

---

## 🔍 Como Encontrar Cada Padrão no Código

### Para encontrar Factory Method:
```
grep -r "AccountFactory" src/main/java
grep -r "createAccount" src/main/java
```

### Para encontrar Facade:
```
grep -r "BankingFacade" src/main/java
```

### Para encontrar Command:
```
grep -r "DepositCommand\|WithdrawCommand" src/main/java
grep -r "implements Command" src/main/java
```

### Para encontrar Observer:
```
grep -r "AccountObserver" src/main/java
grep -r "addObserver\|notifyObservers" src/main/java
```

### Para encontrar Strategy:
```
grep -r "InterestCalculationStrategy" src/main/java
grep -r "setInterestStrategy" src/main/java
```

### Para encontrar Decorator:
```
grep -r "AccountDecorator\|OverdraftProtectionDecorator" src/main/java
```

### Para encontrar Singleton:
```
grep -r "getInstance" src/main/java
grep -r "TransactionLogger\|AuthenticationService" src/main/java
```

---

## 📊 Estatísticas de Uso dos Padrões

| Padrão | Arquivos que Usam | Frequência de Uso |
|--------|-------------------|-------------------|
| **Singleton** | 8 arquivos | ⭐⭐⭐⭐⭐ Muito Alta |
| **Facade** | 2 arquivos | ⭐⭐⭐⭐ Alta |
| **Factory Method** | 2 arquivos | ⭐⭐⭐ Média |
| **Command** | 3 arquivos | ⭐⭐⭐ Média |
| **Observer** | 4 arquivos | ⭐⭐⭐⭐ Alta |
| **Strategy** | 2 arquivos | ⭐⭐ Baixa |
| **Decorator** | 1 arquivo | ⭐ Muito Baixa |

---

## 🎯 Resumo de Uso por Componente

### GUI (LoginController, MainController)
- ✅ Singleton (AuthenticationService)
- ✅ Facade (BankingFacade)
- ✅ Strategy (cálculo de juros na GUI)

### Lógica de Negócio (BankingFacade)
- ✅ Factory Method
- ✅ Command
- ✅ Singleton (TransactionLogger)

### Contas (Account, CheckingAccount, SavingsAccount)
- ✅ Observer
- ✅ Strategy (apenas SavingsAccount)

### Operações (DepositCommand, WithdrawCommand)
- ✅ Command
- ✅ Singleton (TransactionLogger)

### Auditoria (AuditService)
- ✅ Observer
- ✅ Singleton (TransactionLogger)

### Funcionalidades Adicionais (Decorator)
- ✅ Decorator (uso opcional)

---

## 📖 Como Estudar os Padrões no Código

### 1. Comece com Singleton
- Leia `TransactionLogger.java`
- Veja como é usado em `DepositCommand.java`
- Veja como é usado em `AuditService.java`

### 2. Depois Factory Method
- Leia `AccountFactory.java`
- Veja como é usado em `BankingFacade.java`

### 3. Depois Facade
- Leia `BankingFacade.java`
- Veja como é usado em `MainController.java`

### 4. Depois Command
- Leia `Command.java` (interface)
- Leia `DepositCommand.java`
- Veja como é usado em `BankingFacade.java`

### 5. Depois Observer
- Leia `AccountObserver.java` (interface)
- Leia `AuditService.java`
- Veja como é usado em `Account.java`

### 6. Depois Strategy
- Leia `InterestCalculationStrategy.java` (interface)
- Leia `SimpleInterestStrategy.java`
- Veja como é usado em `SavingsAccount.java`

### 7. Por último Decorator
- Leia `AccountDecorator.java`
- Leia `OverdraftProtectionDecorator.java`
- Veja exemplo de uso em `Main.java`

---

## ✅ Checklist de Compreensão

Marque quando entender cada padrão:

- [ ] **Singleton**: Entendi como garante uma única instância
- [ ] **Factory Method**: Entendi como encapsula criação de objetos
- [ ] **Facade**: Entendi como simplifica interface complexa
- [ ] **Command**: Entendi como encapsula operações
- [ ] **Observer**: Entendi como notifica mudanças
- [ ] **Strategy**: Entendi como troca algoritmos
- [ ] **Decorator**: Entendi como adiciona funcionalidades

---

## 🚀 Próximos Passos

1. **Leia a documentação**: Comece com `PADROES_DE_PROJETO_EXPLICACAO.md`
2. **Estude os exemplos**: Veja `EXEMPLO_FLUXO_COMPLETO.md`
3. **Analise a importância**: Leia `RESUMO_IMPORTANCIA_PADROES.md`
4. **Entenda a arquitetura**: Veja `ARQUITETURA_SISTEMA.md`
5. **Explore o código**: Use este índice para encontrar padrões no código

---

**Boa sorte no estudo dos padrões de projeto! 🎓**

