# 🔑 Credenciais de Acesso - Insanos Server

## Usuários Criados Automaticamente

O sistema cria automaticamente os seguintes usuários na **primeira inicialização**:

---

## 👤 Usuário Padrão

**Para testes e uso geral:**

```
Username:  insanos
Email:     user@insanos.com
Password:  insanos321
Roles:     ROLE_USER
```

### Login via API:

**Por username:**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"insanos","password":"insanos321"}'
```

**Por email:**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"user@insanos.com","password":"insanos321"}'
```

---

## 👨‍💼 Usuário Administrador

**Para funcionalidades administrativas:**

```
Username:  admin
Email:     admin@insanos.com
Password:  admin123
Roles:     ROLE_USER, ROLE_ADMIN
```

### Login via API:

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

---

## 🔒 Segurança

### ⚠️ Importante para Produção

**NUNCA use essas credenciais em produção!**

Para produção:
1. Desabilite a criação automática de usuários
2. Crie usuários manualmente via API
3. Use senhas fortes e únicas
4. Configure variáveis de ambiente

### Desabilitar criação automática:

Comente ou remova o arquivo:
```
src/main/java/br/com/insanos/insanos_server/config/DataInitializer.java
```

Ou adicione uma propriedade condicional:
```properties
# application.properties
app.init.create-default-users=false
```

---

## 📋 Verificação

### Verificar se os usuários foram criados:

Ao iniciar o servidor, você verá no log:

```
✓ Usuário padrão criado com sucesso!
  Username: insanos
  Email: user@insanos.com
  Password: insanos321

✓ Usuário admin criado com sucesso!
  Username: admin
  Email: admin@insanos.com
  Password: admin123
```

### Ou, se já existirem:

```
✓ Usuário padrão já existe no banco de dados
✓ Usuário admin já existe no banco de dados
```

---

## 🧪 Testes Rápidos

### 1. Testar login do usuário padrão:

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"insanos","password":"insanos321"}'
```

**Resposta esperada:**
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

### 2. Usar o token para acessar endpoint protegido:

```bash
# Copie o token da resposta anterior
TOKEN="seu_token_aqui"

curl -X GET http://localhost:8080/api/auth/me \
  -H "Authorization: Bearer $TOKEN"
```

**Resposta esperada:**
```json
{
  "id": 1,
  "username": "insanos",
  "email": "user@insanos.com",
  "roles": ["ROLE_USER"]
}
```

---

## 🌐 Integração com Frontend

### React/JavaScript:

```javascript
// Login com usuário padrão
const response = await fetch('http://localhost:8080/api/auth/login', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json',
  },
  body: JSON.stringify({
    username: 'insanos',
    password: 'insanos321'
  })
});

const data = await response.json();
console.log('Token:', data.token);

// Salvar no localStorage
localStorage.setItem('user', JSON.stringify(data));
```

### Com Axios:

```javascript
import axios from 'axios';

const login = async () => {
  try {
    const response = await axios.post('http://localhost:8080/api/auth/login', {
      username: 'insanos',
      password: 'insanos321'
    });
    
    localStorage.setItem('user', JSON.stringify(response.data));
    console.log('Login bem-sucedido!', response.data);
  } catch (error) {
    console.error('Erro no login:', error.response?.data);
  }
};
```

---

## 📝 Notas

1. **Primeiro Login**: Use as credenciais acima para fazer o primeiro login
2. **H2 Console**: Acesse http://localhost:8080/h2-console para ver os usuários no banco
   - JDBC URL: `jdbc:h2:mem:insanosdb`
   - Username: `sa`
   - Password: (vazio)
3. **Criar novos usuários**: Use o endpoint `/api/auth/register`
4. **Alteração de senha**: Implemente endpoint de alteração de senha conforme necessário

---

## ✅ Checklist de Login

- [ ] Servidor iniciado (`mvn spring-boot:run`)
- [ ] Usuários criados (verificar logs)
- [ ] Teste de login realizado
- [ ] Token JWT recebido
- [ ] Token funcionando em endpoints protegidos
- [ ] Integração com frontend testada

---

**Última atualização:** 2025-01-18  
**Versão:** 1.0  
**Status:** ✅ Funcional

