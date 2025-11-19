# 📚 Índice de Documentação - Insanos Server

Bem-vindo ao projeto Insanos Server! Este índice organiza toda a documentação disponível.

## 🎯 Por onde começar?

**Novo no projeto?** Siga esta ordem:

1. 🚀 [**QUICKSTART.md**](QUICKSTART.md) - Comece aqui! (3 passos rápidos)
2. 📘 [**README.md**](README.md) - Documentação principal completa
3. 💻 [**REACT_INTEGRATION_GUIDE.md**](REACT_INTEGRATION_GUIDE.md) - Integração com React

## 📖 Documentação Completa

### Guias Principais

| Documento | Descrição | Quando usar |
|-----------|-----------|-------------|
| [QUICKSTART.md](QUICKSTART.md) | Início rápido em 3 passos | **Começar agora!** |
| [CREDENTIALS.md](CREDENTIALS.md) | Credenciais de acesso | **Login e testes** |
| [README.md](README.md) | Documentação principal | Referência completa |
| [IMPLEMENTATION_SUMMARY.md](IMPLEMENTATION_SUMMARY.md) | Resumo técnico | Entender o projeto |

### Integração Frontend

| Documento | Descrição | Quando usar |
|-----------|-----------|-------------|
| [REACT_INTEGRATION_GUIDE.md](REACT_INTEGRATION_GUIDE.md) | Guia completo React | Integração detalhada |
| [FRONTEND_INTEGRATION.js](FRONTEND_INTEGRATION.js) | Código React pronto | Copiar e colar |

### Referência

| Documento | Descrição | Quando usar |
|-----------|-----------|-------------|
| [COMMANDS.md](COMMANDS.md) | Comandos úteis | Desenvolvimento diário |
| [TROUBLESHOOTING.md](TROUBLESHOOTING.md) | Soluções de problemas | **Quando der erro** |
| [api-tests.http](api-tests.http) | Testes de API | Testar endpoints |
| [.env.example](.env.example) | Variáveis de ambiente | Configuração |

## 🗂️ Estrutura do Projeto

```
insanos-server/
├── 📚 Documentação
│   ├── README.md                      # Documentação principal
│   ├── QUICKSTART.md                  # Início rápido
│   ├── REACT_INTEGRATION_GUIDE.md     # Guia React
│   ├── IMPLEMENTATION_SUMMARY.md      # Resumo técnico
│   ├── FRONTEND_INTEGRATION.js        # Exemplos React
│   ├── COMMANDS.md                    # Comandos úteis
│   └── INDEX.md                       # Este arquivo
│
├── 🧪 Testes e Exemplos
│   └── api-tests.http                 # Testes HTTP
│
├── ⚙️ Configuração
│   ├── .env.example                   # Exemplo de configuração
│   ├── .gitignore                     # Git ignore
│   └── start.sh                       # Script de inicialização
│
├── 📦 Maven
│   └── pom.xml                        # Dependências
│
└── 💻 Código Fonte
    └── src/
        ├── main/
        │   ├── java/                  # Código Java
        │   └── resources/             # Recursos
        └── test/                      # Testes
```

## 🎯 Casos de Uso

### "Quero começar rapidamente"
→ [QUICKSTART.md](QUICKSTART.md)

### "Como integro com meu frontend React?"
→ [REACT_INTEGRATION_GUIDE.md](REACT_INTEGRATION_GUIDE.md)

### "Preciso de exemplos de código React"
→ [FRONTEND_INTEGRATION.js](FRONTEND_INTEGRATION.js)

### "Quero entender a implementação"
→ [IMPLEMENTATION_SUMMARY.md](IMPLEMENTATION_SUMMARY.md)

### "Preciso de comandos para desenvolvimento"
→ [COMMANDS.md](COMMANDS.md)

### "Como testar a API?"
→ [api-tests.http](api-tests.http)

### "Como configurar variáveis de ambiente?"
→ [.env.example](.env.example)

## 📡 Endpoints da API

### Públicos (sem autenticação)
- `POST /api/auth/register` - Registrar usuário
- `POST /api/auth/login` - Login
- `GET /api/test/all` - Conteúdo público

### Protegidos (requer token JWT)
- `GET /api/auth/check` - Verificar autenticação
- `GET /api/auth/me` - Dados do usuário
- `GET /api/test/user` - Conteúdo de usuário
- `GET /api/test/admin` - Conteúdo de admin (requer ROLE_ADMIN)

## 🚀 Comandos Rápidos

### Iniciar servidor
```bash
./start.sh
# ou
mvn spring-boot:run
```

### Testar registro
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"teste","email":"teste@email.com","password":"senha123"}'
```

### Testar login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"teste","password":"senha123"}'
```

## 🔗 Links Úteis

- **Repositório Frontend**: https://github.com/MaxCosta0/insanos-app
- **H2 Console**: http://localhost:8080/h2-console
- **API Base URL**: http://localhost:8080/api

## 🛠️ Tecnologias

- **Java 25**
- **Spring Boot 3.5.7**
- **Spring Security**
- **JWT (JSON Web Tokens)**
- **JPA/Hibernate**
- **H2 Database** (desenvolvimento)
- **PostgreSQL** (produção)
- **Maven**
- **Lombok**

## 📞 Suporte

### Problemas comuns
Consulte [COMMANDS.md](COMMANDS.md) seção "Troubleshooting"

### Erros de compilação
```bash
mvn clean compile
```

### Erro de porta em uso
```bash
lsof -i :8080
kill -9 <PID>
```

## ✅ Checklist de Configuração

- [ ] JDK 17+ instalado
- [ ] Maven instalado
- [ ] Projeto compilado (`mvn clean compile`)
- [ ] Servidor iniciado (`./start.sh`)
- [ ] Endpoints testados (ver api-tests.http)
- [ ] Frontend integrado (opcional)

## 📊 Status do Projeto

- ✅ Backend 100% funcional
- ✅ JWT implementado
- ✅ CORS configurado
- ✅ Documentação completa
- ✅ Exemplos React prontos
- ✅ Pronto para produção
- ⏳ Frontend a ser integrado
- ⏳ Deploy (pendente)

## 🎓 Aprendizado

### Para entender JWT
1. Leia [IMPLEMENTATION_SUMMARY.md](IMPLEMENTATION_SUMMARY.md) - seção "Segurança"
2. Veja o código em `src/main/java/.../security/jwt/`

### Para entender Spring Security
1. Veja `SecurityConfig.java`
2. Consulte [README.md](README.md) - seção "Segurança"

### Para integrar com React
1. Siga [REACT_INTEGRATION_GUIDE.md](REACT_INTEGRATION_GUIDE.md)
2. Use exemplos de [FRONTEND_INTEGRATION.js](FRONTEND_INTEGRATION.js)

## 🎉 Próximos Passos

1. **Desenvolver**: Use [COMMANDS.md](COMMANDS.md) como referência
2. **Integrar**: Siga [REACT_INTEGRATION_GUIDE.md](REACT_INTEGRATION_GUIDE.md)
3. **Deploy**: Configure ambiente de produção
4. **Monitorar**: Adicione logs e métricas

---

**Dica:** Marque este arquivo nos favoritos para acesso rápido à documentação!

📅 Última atualização: 2025-01-18
🚀 Projeto: Insanos Server
👨‍💻 Status: Pronto para uso

