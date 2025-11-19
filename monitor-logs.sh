#!/bin/bash

# Script de monitoramento de logs em tempo real

echo "📊 Insanos Server - Monitoramento de Logs"
echo "=========================================="
echo ""

# Função para mostrar menu
show_menu() {
    echo "Escolha uma opção:"
    echo ""
    echo "1) 📝 Ver todos os logs em tempo real"
    echo "2) ✅ Ver apenas sucessos (INFO)"
    echo "3) ❌ Ver apenas erros (ERROR)"
    echo "4) ⚠️  Ver avisos (WARN)"
    echo "5) 🔐 Ver tentativas de login"
    echo "6) 👤 Ver atividades de um usuário específico"
    echo "7) 📊 Estatísticas de hoje"
    echo "8) 🔍 Buscar texto específico"
    echo "9) 📁 Ver últimas 50 linhas"
    echo "0) 🚪 Sair"
    echo ""
    echo -n "Opção: "
}

# Verificar se arquivo de log existe
LOG_FILE="logs/insanos-server.log"

if [ ! -f "$LOG_FILE" ]; then
    echo "⚠️  Arquivo de log não encontrado: $LOG_FILE"
    echo "💡 Inicie o servidor primeiro com: ./start.sh"
    exit 1
fi

# Loop principal
while true; do
    show_menu
    read option

    case $option in
        1)
            echo ""
            echo "📝 Monitorando todos os logs (Ctrl+C para sair)..."
            echo "---------------------------------------------------"
            tail -f "$LOG_FILE"
            ;;
        2)
            echo ""
            echo "✅ Monitorando apenas sucessos (Ctrl+C para sair)..."
            echo "---------------------------------------------------"
            tail -f "$LOG_FILE" | grep --line-buffered "INFO\|✅"
            ;;
        3)
            echo ""
            echo "❌ Monitorando apenas erros (Ctrl+C para sair)..."
            echo "---------------------------------------------------"
            tail -f "$LOG_FILE" | grep --line-buffered --color=always "ERROR\|❌"
            ;;
        4)
            echo ""
            echo "⚠️  Monitorando avisos (Ctrl+C para sair)..."
            echo "---------------------------------------------------"
            tail -f "$LOG_FILE" | grep --line-buffered --color=always "WARN\|⚠️"
            ;;
        5)
            echo ""
            echo "🔐 Monitorando tentativas de login (Ctrl+C para sair)..."
            echo "---------------------------------------------------"
            tail -f "$LOG_FILE" | grep --line-buffered --color=always "login\|Login\|🔐"
            ;;
        6)
            echo ""
            echo -n "Digite o username para monitorar: "
            read username
            echo ""
            echo "👤 Monitorando atividades de: $username (Ctrl+C para sair)..."
            echo "---------------------------------------------------"
            tail -f "$LOG_FILE" | grep --line-buffered --color=always "$username"
            ;;
        7)
            echo ""
            echo "📊 Estatísticas de hoje ($(date +%Y-%m-%d)):"
            echo "---------------------------------------------------"
            TODAY=$(date +%Y-%m-%d)
            TOTAL=$(grep "$TODAY" "$LOG_FILE" | wc -l)
            LOGINS=$(grep "$TODAY" "$LOG_FILE" | grep "Tentativa de login" | wc -l)
            LOGINS_OK=$(grep "$TODAY" "$LOG_FILE" | grep "Login bem-sucedido" | wc -l)
            REGISTROS=$(grep "$TODAY" "$LOG_FILE" | grep "Usuário registrado com sucesso" | wc -l)
            ERRORS=$(grep "$TODAY" "$LOG_FILE" | grep "ERROR" | wc -l)

            echo "Total de logs: $TOTAL"
            echo "Tentativas de login: $LOGINS"
            echo "Logins bem-sucedidos: $LOGINS_OK"
            echo "Novos registros: $REGISTROS"
            echo "Erros: $ERRORS"
            echo ""
            echo "Pressione Enter para continuar..."
            read
            ;;
        8)
            echo ""
            echo -n "Digite o texto para buscar: "
            read search_text
            echo ""
            echo "🔍 Resultados para '$search_text':"
            echo "---------------------------------------------------"
            grep --color=always "$search_text" "$LOG_FILE" | tail -20
            echo ""
            echo "Pressione Enter para continuar..."
            read
            ;;
        9)
            echo ""
            echo "📁 Últimas 50 linhas do log:"
            echo "---------------------------------------------------"
            tail -50 "$LOG_FILE"
            echo ""
            echo "Pressione Enter para continuar..."
            read
            ;;
        0)
            echo ""
            echo "👋 Até logo!"
            exit 0
            ;;
        *)
            echo ""
            echo "❌ Opção inválida!"
            echo ""
            ;;
    esac
done

