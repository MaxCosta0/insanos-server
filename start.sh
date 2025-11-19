#!/bin/bash

echo "🚀 Iniciando Insanos Server..."
echo ""

# Verificar se o Java está instalado
if ! command -v java &> /dev/null
then
    echo "❌ Java não encontrado. Por favor, instale o Java 17 ou superior."
    exit 1
fi

# Verificar versão do Java
JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | cut -d'.' -f1)
echo "✓ Java versão detectada: $JAVA_VERSION"

# Verificar e liberar porta 8080 se estiver em uso
echo ""
echo "🔍 Verificando porta 8080..."
PID=$(lsof -ti :8080 2>/dev/null)
if [ ! -z "$PID" ]; then
    echo "⚠️  Porta 8080 está em uso pelo processo PID: $PID"
    echo "🔨 Finalizando processo anterior..."
    kill -9 $PID 2>/dev/null
    sleep 1
    echo "✓ Porta 8080 liberada!"
else
    echo "✓ Porta 8080 disponível"
fi

# Compilar o projeto se necessário
if [ ! -f "target/insanos-server-0.0.1-SNAPSHOT.jar" ]; then
    echo ""
    echo "📦 Compilando o projeto..."
    mvn clean package -DskipTests

    if [ $? -ne 0 ]; then
        echo "❌ Erro ao compilar o projeto"
        exit 1
    fi
fi

echo ""
echo "✓ Compilação concluída"
echo ""
echo "🌐 Servidor iniciando em http://localhost:8080"
echo "📊 H2 Console disponível em http://localhost:8080/h2-console"
echo ""
echo "Pressione Ctrl+C para parar o servidor"
echo ""

# Iniciar o servidor
mvn spring-boot:run

