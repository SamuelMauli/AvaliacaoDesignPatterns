#!/bin/bash

###############################################################################
# Script de Execução do Sistema Bancário
###############################################################################
# Este script facilita a execução da aplicação JavaFX do Sistema Bancário.
# Ele configura automaticamente o classpath e os módulos JavaFX necessários.
#
# Uso:
#   ./run.sh
#
# Pré-requisitos:
#   - Maven instalado
#   - Java 11 ou superior
#   - JavaFX SDK (será baixado automaticamente pelo Maven)
###############################################################################

echo "========================================="
echo "Sistema Bancário - Iniciando Aplicação"
echo "========================================="
echo ""

# Verifica se o projeto foi compilado
if [ ! -d "target/classes" ]; then
    echo "Projeto não compilado. Compilando..."
    mvn clean compile
    if [ $? -ne 0 ]; then
        echo "Erro na compilação. Abortando."
        exit 1
    fi
fi

echo "Executando aplicação JavaFX..."
echo ""

# Executa a aplicação usando o plugin Maven JavaFX
mvn javafx:run

echo ""
echo "========================================="
echo "Aplicação encerrada"
echo "========================================="
