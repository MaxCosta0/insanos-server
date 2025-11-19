#!/bin/bash

# Script para liberar a porta 8080 antes de iniciar o servidor

echo "🔍 Verificando porta 8080..."

# Verificar se existe algum processo usando a porta 8080
PID=$(lsof -ti :8080 2>/dev/null)

if [ -z "$PID" ]; then
    echo "✅ Porta 8080 está livre!"
else
    echo "⚠️  Porta 8080 está em uso pelo processo PID: $PID"
    echo "🔨 Finalizando processo..."
    kill -9 $PID 2>/dev/null

    # Aguardar um momento
    sleep 1

    # Verificar novamente
    PID_CHECK=$(lsof -ti :8080 2>/dev/null)
    if [ -z "$PID_CHECK" ]; then
        echo "✅ Processo finalizado! Porta 8080 agora está livre."
    else
        echo "❌ Erro: Não foi possível liberar a porta 8080"
        exit 1
    fi
fi

echo ""
echo "✅ Pronto para iniciar o servidor!"

