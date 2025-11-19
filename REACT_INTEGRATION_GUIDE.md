# Guia de Integração - Frontend React + Backend Spring Boot

## 🔗 Configuração do Backend

O backend está configurado para aceitar requisições do frontend em:
- **URL Base**: `http://localhost:8080`
- **CORS**: Habilitado para todas as origens (desenvolvimento)
- **Porta**: 8080

## 👤 Usuário Padrão

O sistema cria automaticamente um usuário padrão na primeira inicialização:

**Usuário Padrão:**
- **Username**: `insanos`
- **Email**: `user@insanos.com`
- **Password**: `insanos321`
- **Roles**: `ROLE_USER`

**Usuário Admin (opcional):**
- **Username**: `admin`
- **Email**: `admin@insanos.com`
- **Password**: `admin123`
- **Roles**: `ROLE_USER`, `ROLE_ADMIN`

> **Nota**: Esses usuários são criados automaticamente quando o servidor inicia pela primeira vez.

## 📡 Endpoints Disponíveis

### Autenticação (Públicos)

#### 1. Registrar Usuário
```http
POST http://localhost:8080/api/auth/register
Content-Type: application/json

{
  "username": "usuario",
  "email": "usuario@email.com",
  "password": "senha123"
}
```

**Resposta de Sucesso (201):**
```json
{
  "message": "Usuário registrado com sucesso!"
}
```

**Resposta de Erro (400):**
```json
{
  "error": "Erro no registro",
  "message": "Erro: Username já está em uso!"
}
```

#### 2. Login
```http
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "username": "usuario",
  "password": "senha123"
}
```

**Resposta de Sucesso (200):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "id": 1,
  "username": "usuario",
  "email": "usuario@email.com",
  "roles": ["ROLE_USER"]
}
```

**Resposta de Erro (401):**
```json
{
  "error": "Credenciais inválidas",
  "message": "Usuário ou senha incorretos"
}
```

### Endpoints Protegidos (Requerem Token)

#### 3. Verificar Autenticação
```http
GET http://localhost:8080/api/auth/check
Authorization: Bearer {token}
```

**Resposta (200):**
```json
{
  "id": 1,
  "username": "usuario",
  "email": "usuario@email.com",
  "roles": ["ROLE_USER"],
  "authenticated": true
}
```

#### 4. Obter Dados do Usuário Atual
```http
GET http://localhost:8080/api/auth/me
Authorization: Bearer {token}
```

**Resposta (200):**
```json
{
  "id": 1,
  "username": "usuario",
  "email": "usuario@email.com",
  "roles": ["ROLE_USER"]
}
```

### Endpoints de Teste

#### 5. Conteúdo Público
```http
GET http://localhost:8080/api/test/all
```

#### 6. Conteúdo de Usuário (Requer Autenticação)
```http
GET http://localhost:8080/api/test/user
Authorization: Bearer {token}
```

#### 7. Conteúdo Admin (Requer Role ADMIN)
```http
GET http://localhost:8080/api/test/admin
Authorization: Bearer {token}
```

## 🎯 Integração com React

### 1. Criar Serviço de API (src/services/api.js)

```javascript
import axios from 'axios';

const API_URL = 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Interceptor para adicionar token em todas as requisições
api.interceptors.request.use(
  (config) => {
    const user = JSON.parse(localStorage.getItem('user'));
    if (user && user.token) {
      config.headers.Authorization = `Bearer ${user.token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Interceptor para tratar erros de autenticação
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('user');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export default api;
```

### 2. Criar Serviço de Autenticação (src/services/authService.js)

```javascript
import api from './api';

const API_URL = '/auth';

class AuthService {
  async login(username, password) {
    const response = await api.post(`${API_URL}/login`, {
      username,
      password,
    });
    
    if (response.data.token) {
      localStorage.setItem('user', JSON.stringify(response.data));
    }
    
    return response.data;
  }

  async register(username, email, password) {
    const response = await api.post(`${API_URL}/register`, {
      username,
      email,
      password,
    });
    
    return response.data;
  }

  logout() {
    localStorage.removeItem('user');
  }

  getCurrentUser() {
    const user = localStorage.getItem('user');
    return user ? JSON.parse(user) : null;
  }

  async checkAuth() {
    try {
      const response = await api.get(`${API_URL}/check`);
      return response.data;
    } catch (error) {
      return null;
    }
  }

  async getMe() {
    try {
      const response = await api.get(`${API_URL}/me`);
      return response.data;
    } catch (error) {
      return null;
    }
  }
}

export default new AuthService();
```

### 3. Criar Context de Autenticação (src/contexts/AuthContext.jsx)

```javascript
import { createContext, useState, useContext, useEffect } from 'react';
import authService from '../services/authService';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const loadUser = async () => {
      const currentUser = authService.getCurrentUser();
      
      if (currentUser) {
        // Verificar se o token ainda é válido
        const userData = await authService.checkAuth();
        if (userData && userData.authenticated) {
          setUser(currentUser);
        } else {
          authService.logout();
        }
      }
      
      setLoading(false);
    };

    loadUser();
  }, []);

  const login = async (username, password) => {
    const userData = await authService.login(username, password);
    setUser(userData);
    return userData;
  };

  const register = async (username, email, password) => {
    return await authService.register(username, email, password);
  };

  const logout = () => {
    authService.logout();
    setUser(null);
  };

  const value = {
    user,
    login,
    logout,
    register,
    loading,
    isAuthenticated: !!user,
    isAdmin: user?.roles?.includes('ROLE_ADMIN'),
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth deve ser usado dentro de um AuthProvider');
  }
  return context;
}
```

### 4. Componente de Login (src/pages/Login.jsx)

```javascript
import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';

export default function Login() {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  
  const { login } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      await login(username, password);
      navigate('/');
    } catch (err) {
      setError(err.response?.data?.message || 'Erro ao fazer login');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="login-container">
      <h2>Login</h2>
      <form onSubmit={handleSubmit}>
        <div>
          <label>Usuário</label>
          <input
            type="text"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            required
            disabled={loading}
          />
        </div>
        
        <div>
          <label>Senha</label>
          <input
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
            disabled={loading}
          />
        </div>

        {error && <div className="error">{error}</div>}

        <button type="submit" disabled={loading}>
          {loading ? 'Entrando...' : 'Entrar'}
        </button>
      </form>
      
      <p>
        Não tem conta? <Link to="/register">Registre-se</Link>
      </p>
    </div>
  );
}
```

### 5. Componente de Registro (src/pages/Register.jsx)

```javascript
import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';

export default function Register() {
  const [formData, setFormData] = useState({
    username: '',
    email: '',
    password: '',
    confirmPassword: '',
  });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  
  const { register } = useAuth();
  const navigate = useNavigate();

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value,
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');

    if (formData.password !== formData.confirmPassword) {
      setError('As senhas não coincidem');
      return;
    }

    setLoading(true);

    try {
      await register(formData.username, formData.email, formData.password);
      alert('Cadastro realizado com sucesso!');
      navigate('/login');
    } catch (err) {
      setError(err.response?.data?.message || 'Erro ao registrar');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="register-container">
      <h2>Cadastro</h2>
      <form onSubmit={handleSubmit}>
        <div>
          <label>Usuário</label>
          <input
            type="text"
            name="username"
            value={formData.username}
            onChange={handleChange}
            required
            disabled={loading}
          />
        </div>
        
        <div>
          <label>Email</label>
          <input
            type="email"
            name="email"
            value={formData.email}
            onChange={handleChange}
            required
            disabled={loading}
          />
        </div>
        
        <div>
          <label>Senha</label>
          <input
            type="password"
            name="password"
            value={formData.password}
            onChange={handleChange}
            required
            disabled={loading}
          />
        </div>
        
        <div>
          <label>Confirmar Senha</label>
          <input
            type="password"
            name="confirmPassword"
            value={formData.confirmPassword}
            onChange={handleChange}
            required
            disabled={loading}
          />
        </div>

        {error && <div className="error">{error}</div>}

        <button type="submit" disabled={loading}>
          {loading ? 'Cadastrando...' : 'Cadastrar'}
        </button>
      </form>
      
      <p>
        Já tem conta? <Link to="/login">Faça login</Link>
      </p>
    </div>
  );
}
```

### 6. Componente de Rota Protegida (src/components/PrivateRoute.jsx)

```javascript
import { Navigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';

export default function PrivateRoute({ children, requireAdmin = false }) {
  const { user, loading, isAdmin } = useAuth();

  if (loading) {
    return <div>Carregando...</div>;
  }

  if (!user) {
    return <Navigate to="/login" replace />;
  }

  if (requireAdmin && !isAdmin) {
    return <Navigate to="/" replace />;
  }

  return children;
}
```

### 7. Configurar Rotas no App (src/App.jsx)

```javascript
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import { AuthProvider } from './contexts/AuthContext';
import PrivateRoute from './components/PrivateRoute';
import Login from './pages/Login';
import Register from './pages/Register';
import Home from './pages/Home';
import Dashboard from './pages/Dashboard';

function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          
          <Route
            path="/"
            element={
              <PrivateRoute>
                <Home />
              </PrivateRoute>
            }
          />
          
          <Route
            path="/dashboard"
            element={
              <PrivateRoute>
                <Dashboard />
              </PrivateRoute>
            }
          />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
}

export default App;
```

## 🚀 Iniciar o Projeto

### Backend (Spring Boot)
```bash
cd insanos-server
mvn spring-boot:run
```

### Frontend (React + Vite)
```bash
cd insanos-app
npm install
npm run dev
```

## 🔒 Segurança

- **Tokens JWT**: Expiração de 24 horas (configurável em `application.properties`)
- **Senhas**: Criptografadas com BCrypt
- **CORS**: Configurado para aceitar requisições do frontend

## 📝 Notas Importantes

1. **LocalStorage**: O token é armazenado no localStorage do navegador
2. **Interceptores**: Axios adiciona automaticamente o token em todas as requisições
3. **Redirecionamento**: Em caso de 401, o usuário é redirecionado para o login
4. **Validação**: Tanto frontend quanto backend validam os dados de entrada

## 🐛 Troubleshooting

### CORS Error
Se você receber erros de CORS:
- Verifique se o backend está rodando em `http://localhost:8080`
- Verifique as configurações de CORS em `SecurityConfig.java`

### Token Inválido
Se o token não está sendo aceito:
- Verifique se o header `Authorization` está sendo enviado
- Confirme que o formato é `Bearer {token}`
- Verifique se o token não expirou

### Erro 401 (Unauthorized)
- Verifique se você está fazendo login corretamente
- Confirme que o token está sendo salvo no localStorage
- Verifique se o token está sendo enviado nas requisições

## 📚 Recursos Adicionais

- [Spring Security Documentation](https://spring.io/projects/spring-security)
- [JWT.io](https://jwt.io/)
- [React Router Documentation](https://reactrouter.com/)
- [Axios Documentation](https://axios-http.com/)

