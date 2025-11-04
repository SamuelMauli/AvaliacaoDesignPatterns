# Sistema Bancário - Instruções de Execução

## 📋 Visão Geral

Este documento fornece instruções detalhadas para compilar, executar e testar o Sistema Bancário desenvolvido em Java com JavaFX.

## 🔧 Pré-requisitos

Antes de executar a aplicação, certifique-se de ter instalado:

- **Java Development Kit (JDK) 11 ou superior**
  - Verifique com: `java -version`
- **Apache Maven 3.6 ou superior**
  - Verifique com: `mvn -version`
- **JavaFX SDK 17.0.2** (será baixado automaticamente pelo Maven)

## 🚀 Compilação do Projeto

### Opção 1: Compilação Simples

```bash
mvn clean compile
```

### Opção 2: Compilação com Testes

```bash
mvn clean test
```

### Opção 3: Compilação Completa (com empacotamento)

```bash
mvn clean package
```

## ▶️ Executando a Aplicação

### Método 1: Usando o Script de Execução (Recomendado)

```bash
./run.sh
```

### Método 2: Usando Maven diretamente

```bash
mvn javafx:run
```

### Método 3: Executando o JAR (após compilação completa)

```bash
java --module-path /caminho/para/javafx-sdk/lib \
     --add-modules javafx.controls,javafx.fxml \
     -jar target/banking-system-1.0-SNAPSHOT.jar
```

## 🧪 Executando Testes

### Executar todos os testes

```bash
mvn test
```

### Executar testes de uma classe específica

```bash
mvn test -Dtest=AccountFactoryTest
```

### Executar testes com relatório detalhado

```bash
mvn test -X
```

## 👤 Credenciais de Acesso

Para fazer login na aplicação, use uma das seguintes credenciais:

| Usuário | Senha | Perfil |
|---------|-------|--------|
| admin | admin123 | Administrador |
| alice | alice123 | Usuário |
| bob | bob123 | Usuário |
| charlie | charlie123 | Usuário |

## 📚 Funcionalidades Disponíveis

### 1. Gerenciamento de Contas
- ✅ Criar conta corrente com limite de cheque especial
- ✅ Criar conta poupança com taxa de juros
- ✅ Visualizar todas as contas cadastradas
- ✅ Consultar saldo de contas

### 2. Operações Bancárias
- ✅ Realizar depósitos em contas
- ✅ Realizar saques de contas
- ✅ Transferir valores entre contas
- ✅ Calcular juros para contas poupança

### 3. Histórico e Auditoria
- ✅ Visualizar histórico completo de transações
- ✅ Acompanhar todas as operações realizadas
- ✅ Logs detalhados de cada transação

## 🏗️ Arquitetura e Padrões de Projeto

O sistema implementa diversos padrões de projeto:

### Padrões Criacionais
- **Factory Method**: `AccountFactory` para criação de contas
- **Singleton**: `TransactionLogger` e `AuthenticationService`

### Padrões Estruturais
- **Facade**: `BankingFacade` simplifica operações complexas
- **Decorator**: `OverdraftProtectionDecorator` adiciona funcionalidades

### Padrões Comportamentais
- **Command**: `DepositCommand` e `WithdrawCommand` encapsulam operações
- **Observer**: Sistema de notificação de eventos em contas
- **Strategy**: `InterestCalculationStrategy` para cálculo de juros

### Padrões Arquiteturais
- **MVC (Model-View-Controller)**: Separação clara entre lógica de negócio e interface

## 📁 Estrutura do Projeto

```
src/
├── main/
│   ├── java/com/bank/
│   │   ├── account/          # Classes de contas bancárias
│   │   ├── command/          # Padrão Command para operações
│   │   ├── decorator/        # Padrão Decorator para funcionalidades adicionais
│   │   ├── facade/           # Padrão Facade para simplificação de API
│   │   ├── factory/          # Padrão Factory para criação de objetos
│   │   ├── gui/              # Interface gráfica JavaFX
│   │   │   ├── controller/   # Controladores MVC
│   │   │   ├── model/        # Modelos de dados da GUI
│   │   │   └── util/         # Utilitários de UI
│   │   ├── logger/           # Sistema de logging (Singleton)
│   │   ├── observer/         # Padrão Observer para notificações
│   │   └── strategy/         # Padrão Strategy para cálculos
│   └── resources/
│       └── fxml/             # Arquivos de layout JavaFX
└── test/                     # Testes unitários
```

## 🐛 Resolução de Problemas

### Erro: "JavaFX runtime components are missing"

**Solução**: Certifique-se de que o JavaFX SDK está configurado corretamente. Use `mvn javafx:run` em vez de executar diretamente com `java`.

### Erro: "Unable to open DISPLAY"

**Solução**: Este erro ocorre em ambientes sem interface gráfica. Para testar em servidores, use Xvfb:

```bash
Xvfb :99 -screen 0 1024x768x24 &
export DISPLAY=:99
mvn javafx:run
```

### Erro de compilação

**Solução**: Limpe o cache do Maven e recompile:

```bash
mvn clean
mvn compile
```

### Testes falhando

**Solução**: Verifique se todas as dependências foram baixadas:

```bash
mvn dependency:resolve
mvn test
```

## 📊 Relatórios de Teste

Após executar os testes, os relatórios são gerados em:

```
target/surefire-reports/
```

Para visualizar um relatório HTML detalhado:

```bash
mvn surefire-report:report
```

## 🔍 Análise de Código

### Verificar cobertura de testes

```bash
mvn jacoco:prepare-agent test jacoco:report
```

### Análise estática de código

```bash
mvn checkstyle:check
```

## 📝 Logs da Aplicação

Os logs da aplicação são exibidos no console durante a execução. Para salvar em arquivo:

```bash
mvn javafx:run > application.log 2>&1
```

## 🤝 Contribuindo

Para contribuir com melhorias:

1. Faça um fork do repositório
2. Crie uma branch para sua feature (`git checkout -b feature/MinhaFeature`)
3. Commit suas mudanças (`git commit -m 'Adiciona MinhaFeature'`)
4. Push para a branch (`git push origin feature/MinhaFeature`)
5. Abra um Pull Request

## 📄 Licença

Este projeto é um sistema educacional para demonstração de padrões de projeto e boas práticas de desenvolvimento.

## 📞 Suporte

Para questões ou problemas, consulte a documentação no código-fonte ou entre em contato com o desenvolvedor.

---

**Última atualização**: Novembro 2025
