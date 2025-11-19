# 🚀 Quick Start - Insanos Server

## Início Rápido em 3 Passos

### 1️⃣ Compilar e Executar o Backend

```bash
cd insanos-server
./start.sh
```

Ou manualmente:
```bash
mvn spring-boot:run
```

✅ Servidor rodando em: **http://localhost:8080**

---

## 👤 Usuário Padrão Criado Automaticamente

Quando o servidor iniciar pela primeira vez, os seguintes usuários serão criados:

**Usuário Padrão:**
- **Username**: `insanos`
- **Email**: `user@insanos.com`
- **Password**: `insanos321`

**Usuário Admin:**
- **Username**: `admin`
- **Email**: `admin@insanos.com`
- **Password**: `admin123`

Use essas credenciais para testar o login!

---

### 2️⃣ Testar a API

#### Fazer login com usuário padrão:
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"insanos","password":"insanos321"}'
```

Ou por email:
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"user@insanos.com","password":"insanos321"}'
```

#### Registrar um novo usuário:
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"teste","email":"teste@email.com","password":"senha123"}'
```

**Copie o `token` da resposta!**

#### Testar endpoint protegido:
```bash
curl -X GET http://localhost:8080/api/auth/me \
  -H "Authorization: Bearer SEU_TOKEN_AQUI"
```

---

### 3️⃣ Integrar com Frontend React

No seu projeto React (insanos-app):

#### Instalar dependências:
```bash
npm install axios react-router-dom
```

#### Criar serviço de API (src/services/api.js):
```javascript
import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8080/api',
  headers: { 'Content-Type': 'application/json' },
});

api.interceptors.request.use((config) => {
  const user = JSON.parse(localStorage.getItem('user') || '{}');
  if (user.token) {
    config.headers.Authorization = `Bearer ${user.token}`;
  }
  return config;
});

export default api;
```

#### Criar serviço de autenticação (src/services/authService.js):
```javascript
import api from './api';

class AuthService {
  async login(username, password) {
    const response = await api.post('/auth/login', { username, password });
    if (response.data.token) {
      localStorage.setItem('user', JSON.stringify(response.data));
    }
    return response.data;
  }

  async register(username, email, password) {
    return await api.post('/auth/register', { username, email, password });
  }

  logout() {
    localStorage.removeItem('user');
  }

  getCurrentUser() {
    return JSON.parse(localStorage.getItem('user') || 'null');
  }
}

export default new AuthService();
```

#### Componente de Login (src/pages/Login.jsx):
```javascript
import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import authService from '../services/authService';

export default function Login() {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await authService.login(username, password);
      navigate('/dashboard');
    } catch (err) {
      setError(err.response?.data?.message || 'Erro ao fazer login');
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      <input
        type="text"
        placeholder="Usuário"
        value={username}
        onChange={(e) => setUsername(e.target.value)}
      />
      <input
        type="password"
        placeholder="Senha"
        value={password}
        onChange={(e) => setPassword(e.target.value)}
      />
      {error && <p style={{color: 'red'}}>{error}</p>}
      <button type="submit">Entrar</button>
    </form>
  );
}
```

---

## 📚 Documentação Completa

- **README.md** - Documentação detalhada
- **REACT_INTEGRATION_GUIDE.md** - Guia completo de integração React
- **FRONTEND_INTEGRATION.js** - Exemplos de código completos
- **IMPLEMENTATION_SUMMARY.md** - Resumo da implementação
- **api-tests.http** - Testes de API (use com REST Client)

---

## 🎯 Endpoints Principais

| Método | Endpoint | Descrição | Autenticação |
|--------|----------|-----------|--------------|
| POST | `/api/auth/register` | Registrar usuário | ❌ |
| POST | `/api/auth/login` | Fazer login | ❌ |
| GET | `/api/auth/me` | Dados do usuário | ✅ |
| GET | `/api/auth/check` | Verificar auth | ✅ |
| GET | `/api/test/user` | Conteúdo user | ✅ |
| GET | `/api/test/admin` | Conteúdo admin | ✅ Admin |

---

## ⚙️ Configuração

### Banco de Dados H2 (Desenvolvimento)
- Console: http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:mem:insanosdb`
- Username: `sa`
- Password: (vazio)

### Alterar Configurações
Edite: `src/main/resources/application.properties`

### Usar PostgreSQL em Produção
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/insanosdb
spring.datasource.username=seu_usuario
spring.datasource.password=sua_senha
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
```

---

## 🐛 Problemas Comuns

### Porta 8080 em uso
```bash
lsof -i :8080
kill -9 <PID>
```

### Erro de CORS
Adicione a origem do frontend em `application.properties`:
```properties
cors.allowed.origins=http://localhost:3000,http://localhost:5173
```

### Token inválido
- Verifique o formato: `Authorization: Bearer {token}`
- Token expira em 24h por padrão

---

## ✅ Checklist

- [x] Backend Spring Boot funcionando
- [x] JWT implementado
- [x] CORS configurado
- [x] Documentação completa
- [x] Exemplos de integração React
- [ ] Frontend integrado
- [ ] Deploy em produção

---

## 🎉 Pronto!

Seu backend está funcionando e pronto para integração com o frontend React!

**Próximo passo:** Integre com o [insanos-app](https://github.com/MaxCosta0/insanos-app)

Para dúvidas, consulte a documentação completa nos arquivos mencionados acima.

