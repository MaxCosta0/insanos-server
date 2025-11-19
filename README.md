# Insanos Server - API de Autenticação

Aplicação Spring Boot para autenticação de usuários com JWT, preparada para integração com frontend React.

> **✨ Novo!** Este projeto está totalmente integrado com o [insanos-app](https://github.com/MaxCosta0/insanos-app) - frontend React.

## 📖 Documentação

- 🚀 **[Quick Start](QUICKSTART.md)** - Comece aqui! Guia rápido em 3 passos
- 📘 **[Guia de Integração React](REACT_INTEGRATION_GUIDE.md)** - Integração completa com frontend
- 📊 **[Guia de Logs](LOGGING_GUIDE.md)** - Sistema de logs estruturados
- 🧪 **[Guia de Testes](TESTING_GUIDE.md)** - Testes unitários e de integração
- 📝 **[Resumo da Implementação](IMPLEMENTATION_SUMMARY.md)** - Visão geral do projeto
- 💻 **[Exemplos de Código](FRONTEND_INTEGRATION.js)** - Código React pronto para usar
- 🧪 **[Testes de API](api-tests.http)** - Exemplos de requisições HTTP

## 🚀 Tecnologias

- Java 25
- Spring Boot 3.5.7
- Spring Security
- JWT (JSON Web Token)
- JPA/Hibernate
- H2 Database (desenvolvimento)
- PostgreSQL (produção)
- Lombok
- Maven

## 📋 Pré-requisitos

- Java 25 ou superior
- Maven 3.6+

## 🔧 Instalação

1. Clone o repositório
```bash
git clone <repository-url>
cd insanos-server
```

2. Instale as dependências
```bash
mvn clean install
```

3. Execute a aplicação
```bash
mvn spring-boot:run
```

A aplicação estará disponível em `http://localhost:8080`

## 👤 Usuário Padrão

Na primeira inicialização, o sistema cria automaticamente os seguintes usuários:

**Usuário Padrão:**
- Username: `insanos`
- Email: `user@insanos.com`
- Password: `insanos321`
- Roles: `ROLE_USER`

**Usuário Admin:**
- Username: `admin`
- Email: `admin@insanos.com`
- Password: `admin123`
- Roles: `ROLE_USER`, `ROLE_ADMIN`

> ⚠️ **Importante**: Altere essas senhas em produção!

## 🔑 Endpoints da API

### Autenticação

#### 1. Login (use as credenciais acima)
```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "insanos",
  "password": "insanos321"
}
```

**Resposta de sucesso:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "id": 1,
  "username": "insanos",
  "email": "user@insanos.com",
  "roles": ["ROLE_USER"]
}
```

#### 2. Registrar novo usuário
```http
POST /api/auth/register
Content-Type: application/json

{
  "username": "usuario",
  "email": "usuario@example.com",
  "password": "senha123",
  "roles": ["user"]  // opcional: "user" ou "admin"
}
```

**Resposta de sucesso:**
```json
{
  "message": "Usuário registrado com sucesso!"
}
```

#### 3. Login (após registro)
```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "usuario",
  "password": "senha123"
}
```

**Resposta de sucesso:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "id": 1,
  "username": "usuario",
  "email": "usuario@example.com",
  "roles": ["ROLE_USER"]
}
```

#### 3. Verificar autenticação
```http
GET /api/auth/check
Authorization: Bearer <token>
```

### Endpoints de Teste

#### Acesso público
```http
GET /api/test/all
```

#### Acesso de usuário (requer autenticação)
```http
GET /api/test/user
Authorization: Bearer <token>
```

#### Acesso de administrador (requer role ADMIN)
```http
GET /api/test/admin
Authorization: Bearer <token>
```

## 🔐 Uso do Token JWT

Após fazer login, você receberá um token JWT. Use-o nas requisições protegidas:

```http
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### Exemplo com cURL:
```bash
curl -X GET http://localhost:8080/api/test/user \
  -H "Authorization: Bearer SEU_TOKEN_AQUI"
```

### Exemplo com JavaScript (Fetch API):
```javascript
const response = await fetch('http://localhost:8080/api/test/user', {
  method: 'GET',
  headers: {
    'Authorization': 'Bearer ' + token,
    'Content-Type': 'application/json'
  }
});
```

### Exemplo com Axios:
```javascript
axios.get('http://localhost:8080/api/test/user', {
  headers: {
    'Authorization': 'Bearer ' + token
  }
});
```

## ⚙️ Configuração

### application.properties

As principais configurações estão em `src/main/resources/application.properties`:

- **Banco de dados H2** (desenvolvimento)
- **JWT Secret**: Configure uma chave segura em produção
- **JWT Expiration**: Tempo de expiração do token (padrão: 24h)
- **CORS**: Origens permitidas para requisições

### Configuração para Produção (PostgreSQL)

Para usar PostgreSQL em produção, atualize o `application.properties`:

```properties
# PostgreSQL Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/insanosdb
spring.datasource.username=seu_usuario
spring.datasource.password=sua_senha
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=update

# Desabilitar H2 Console
spring.h2.console.enabled=false
```

## 🗄️ H2 Console (Desenvolvimento)

Acesse o console H2 em: `http://localhost:8080/h2-console`

- **JDBC URL**: `jdbc:h2:mem:insanosdb`
- **User**: `sa`
- **Password**: (deixe em branco)

## 🛡️ Segurança

### Roles Disponíveis:
- `ROLE_USER` - Usuário padrão
- `ROLE_ADMIN` - Administrador

### Senha
As senhas são criptografadas usando BCrypt antes de serem armazenadas no banco de dados.

## 📦 Estrutura do Projeto

```
src/main/java/br/com/insanos/insanos_server/
├── config/              # Configurações (Security)
├── controller/          # Controllers REST
├── dto/                 # Data Transfer Objects
├── model/               # Entidades JPA
├── repository/          # Repositórios JPA
├── security/            # Segurança (UserDetails, JWT)
│   └── jwt/            # Utilitários JWT
└── service/            # Serviços de negócio
```

## 🧪 Testes

Execute os testes com:
```bash
mvn test
```

## 📝 Exemplos de Uso Completo

### 1. Criar um usuário
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "joao",
    "email": "joao@example.com",
    "password": "senha123"
  }'
```

### 2. Fazer login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "joao",
    "password": "senha123"
  }'
```

### 3. Acessar recurso protegido
```bash
curl -X GET http://localhost:8080/api/test/user \
  -H "Authorization: Bearer SEU_TOKEN_AQUI"
```

## 🌐 CORS

Por padrão, a aplicação aceita requisições de:
- http://localhost:3000 (React)
- http://localhost:5173 (Vite)

Para adicionar mais origens, edite `application.properties`:
```properties
cors.allowed.origins=http://localhost:3000,http://localhost:5173,http://seusite.com
```

## 📄 Licença

Este projeto está sob a licença MIT.

## 👥 Contribuindo

1. Fork o projeto
2. Crie uma branch para sua feature (`git checkout -b feature/AmazingFeature`)
3. Commit suas mudanças (`git commit -m 'Add some AmazingFeature'`)
4. Push para a branch (`git push origin feature/AmazingFeature`)
5. Abra um Pull Request

## 🐛 Problemas Conhecidos

Se você encontrar problemas, por favor abra uma issue no GitHub.

## 📞 Contato

Para dúvidas ou sugestões, entre em contato através das issues do GitHub.

