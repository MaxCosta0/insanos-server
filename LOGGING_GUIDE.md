# 📊 Sistema de Logs Estruturados - Insanos Server

## 🎯 Visão Geral

O sistema implementa logs estruturados em todos os componentes principais para facilitar o debug, monitoramento e manutenção.

## 📝 Níveis de Log

### INFO
Usado para eventos importantes do sistema:
- Login/logout de usuários
- Registro de novos usuários
- Geração de tokens JWT
- Acesso a endpoints protegidos

### DEBUG
Usado para informações detalhadas de debug:
- Validações de dados
- Processamento de requisições
- Operações de banco de dados
- Fluxo de autenticação

### WARN
Usado para situações anormais mas não críticas:
- Tentativas de login falhas
- Tokens inválidos ou expirados
- Acessos negados
- Dados duplicados

### ERROR
Usado para erros que requerem atenção:
- Exceções não tratadas
- Falhas de autenticação
- Erros de banco de dados
- Problemas de configuração

### TRACE
Usado para informações muito detalhadas (SQL queries, etc.)

---

## 🔍 Logs por Componente

### 1. AuthController

**Login:**
```
INFO  🔐 Tentativa de login - Username: usuario123
DEBUG Iniciando autenticação para usuário: usuario123
INFO  ✅ Login bem-sucedido - Username: usuario123, ID: 1, Roles: [ROLE_USER]
```

**Falha no Login:**
```
INFO  🔐 Tentativa de login - Username: usuario123
ERROR ❌ Falha no login - Username: usuario123, Erro: Bad credentials
```

**Registro:**
```
INFO  📝 Tentativa de registro - Username: novousuario, Email: novo@email.com
DEBUG Validando dados de registro para: novousuario
DEBUG Processando roles especificadas: [user]
INFO  ✅ Registro bem-sucedido - Username: novousuario, Email: novo@email.com
```

**Verificação de Auth:**
```
DEBUG 🔍 Verificação de autenticação solicitada
INFO  ✅ Usuário autenticado - Username: usuario123, ID: 1
```

### 2. AuthService

**Autenticação:**
```
INFO  🔐 AuthService: Iniciando autenticação - Username: usuario123
DEBUG Criando token de autenticação para: usuario123
DEBUG Autenticação bem-sucedida, configurando contexto de segurança
DEBUG Gerando token JWT
INFO  ✅ Token JWT gerado com sucesso - Username: usuario123, ID: 1, Roles: [ROLE_USER]
```

**Registro:**
```
INFO  📝 AuthService: Iniciando registro - Username: novousuario, Email: novo@email.com
DEBUG Verificando se username 'novousuario' já existe
DEBUG Verificando se email 'novo@email.com' já existe
DEBUG Criando novo usuário - Username: novousuario
DEBUG Criptografando senha para: novousuario
DEBUG Nenhuma role especificada, atribuindo ROLE_USER padrão
DEBUG Salvando usuário no banco de dados: novousuario
INFO  ✅ Usuário registrado com sucesso - Username: novousuario, Email: novo@email.com, Roles: [ROLE_USER]
```

### 3. JwtUtils

**Geração de Token:**
```
DEBUG 🔑 Gerando JWT token para usuário: usuario123
DEBUG Token será válido de Mon Nov 18 23:30:00 2025 até Tue Nov 19 23:30:00 2025
INFO  ✅ JWT token gerado com sucesso para: usuario123 (expira em Tue Nov 19 23:30:00 2025)
```

**Validação de Token:**
```
DEBUG 🔍 Extraindo username do JWT token
DEBUG Username extraído do token: usuario123
```

**Token Inválido:**
```
ERROR Assinatura JWT inválida: JWT signature does not match
```

### 4. AuthTokenFilter

**Token Válido:**
```
DEBUG 🔒 Filtro JWT ativado para: GET /api/test/user
DEBUG Token JWT encontrado na requisição
DEBUG Token JWT válido, extraindo username
DEBUG Carregando UserDetails para: usuario123
INFO  ✅ Usuário autenticado via JWT - Username: usuario123, Path: /api/test/user
```

**Sem Token:**
```
DEBUG 🔒 Filtro JWT ativado para: GET /api/test/all
DEBUG Nenhum token JWT encontrado na requisição para: /api/test/all
```

**Token Inválido:**
```
DEBUG 🔒 Filtro JWT ativado para: GET /api/test/user
DEBUG Token JWT encontrado na requisição
WARN  ⚠️ Token JWT inválido para path: /api/test/user
```

### 5. UserDetailsServiceImpl

**Carregar Usuário:**
```
DEBUG 👤 Carregando UserDetails para: usuario123
INFO  ✅ UserDetails carregado com sucesso - Username: usuario123, ID: 1, Roles: [ROLE_USER]
```

**Usuário Não Encontrado:**
```
DEBUG 👤 Carregando UserDetails para: usuarioInexistente
ERROR ❌ Usuário não encontrado: usuarioInexistente
```

### 6. TestController

**Acesso Público:**
```
INFO  🌐 Acesso público solicitado - /api/test/all
DEBUG Retornando conteúdo público
```

**Acesso de Usuário:**
```
INFO  👤 Acesso de usuário solicitado - /api/test/user - Username: usuario123
DEBUG Retornando conteúdo de usuário para: usuario123
```

**Acesso Admin:**
```
INFO  👨‍💼 Acesso de admin solicitado - /api/test/admin - Username: admin
DEBUG Retornando conteúdo de admin para: admin
```

### 7. DataInitializer

**Criação de Usuários:**
```
INFO  ✓ Usuário padrão criado com sucesso!
INFO    Username: insanos
INFO    Email: user@insanos.com
INFO    Password: insanos321
```

---

## ⚙️ Configuração de Logs

### application.properties

```properties
# Níveis de Log
logging.level.root=INFO
logging.level.br.com.insanos=DEBUG
logging.level.org.springframework.security=INFO
logging.level.org.springframework.web=INFO
logging.level.org.hibernate.SQL=DEBUG

# Padrão de Log no Console
logging.pattern.console=%d{yyyy-MM-dd HH:mm:ss.SSS} %highlight(%-5level) %cyan([%thread]) %yellow(%logger{36}) - %msg%n

# Arquivo de Log
logging.file.name=logs/insanos-server.log
logging.file.max-size=10MB
logging.file.max-history=30
logging.file.total-size-cap=100MB
```

---

## 📁 Localização dos Logs

### Console
Logs são exibidos no console durante execução com cores:
- **ERROR** - Vermelho
- **WARN** - Amarelo
- **INFO** - Verde
- **DEBUG** - Branco
- **TRACE** - Cinza

### Arquivo
Logs são salvos em: `logs/insanos-server.log`

**Rotação de Logs:**
- Tamanho máximo por arquivo: 10MB
- Histórico: 30 arquivos
- Tamanho total máximo: 100MB

---

## 🔍 Como Usar os Logs

### 1. Monitorar em Tempo Real

```bash
# Ver logs em tempo real
tail -f logs/insanos-server.log

# Com cores
tail -f logs/insanos-server.log | grep --color=always "ERROR\|WARN"

# Filtrar por nível
tail -f logs/insanos-server.log | grep "INFO"
```

### 2. Buscar Logs Específicos

```bash
# Buscar tentativas de login
grep "🔐 Tentativa de login" logs/insanos-server.log

# Buscar erros
grep "ERROR" logs/insanos-server.log

# Buscar por usuário específico
grep "usuario123" logs/insanos-server.log

# Buscar registros bem-sucedidos
grep "✅" logs/insanos-server.log
```

### 3. Analisar Logs

```bash
# Contar logins por usuário
grep "Login bem-sucedido" logs/insanos-server.log | cut -d'-' -f2 | sort | uniq -c

# Ver últimos erros
grep "ERROR" logs/insanos-server.log | tail -20

# Estatísticas de hoje
grep "$(date +%Y-%m-%d)" logs/insanos-server.log | wc -l
```

---

## 🎨 Emojis nos Logs

Os emojis facilitam identificação visual rápida:

- 🔐 - Login/Autenticação
- 📝 - Registro
- ✅ - Sucesso
- ❌ - Erro
- ⚠️ - Aviso
- 🔍 - Verificação/Busca
- 🔑 - Token JWT
- 🔒 - Filtro de Segurança
- 👤 - Usuário
- 👨‍💼 - Admin
- 🌐 - Acesso Público
- 📊 - Dados/Estatísticas

---

## 🛠️ Customização

### Alterar Nível de Log

**Apenas para produção (menos verbose):**
```properties
logging.level.br.com.insanos=INFO
logging.level.org.hibernate.SQL=WARN
```

**Para debug intensivo:**
```properties
logging.level.br.com.insanos=TRACE
logging.level.org.springframework.security=DEBUG
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE
```

### Desabilitar Cores no Console

```properties
spring.output.ansi.enabled=never
```

### Formato JSON (para ferramentas de análise)

Adicione dependência:
```xml
<dependency>
    <groupId>net.logstash.logback</groupId>
    <artifactId>logstash-logback-encoder</artifactId>
    <version>7.4</version>
</dependency>
```

Configure `logback-spring.xml`:
```xml
<encoder class="net.logstash.logback.encoder.LogstashEncoder"/>
```

---

## 📈 Monitoramento em Produção

### 1. Ferramentas Recomendadas

- **ELK Stack** (Elasticsearch, Logstash, Kibana)
- **Splunk**
- **Datadog**
- **New Relic**
- **Grafana Loki**

### 2. Integração com ELK

```properties
# application-prod.properties
logging.file.name=/var/log/insanos-server/application.log
```

Configure Logstash para ler o arquivo de log.

### 3. Alertas

Configure alertas para:
- Taxa alta de erros (>10/min)
- Falhas de autenticação repetidas
- Exceções não tratadas
- Uso de memória/CPU elevado

---

## 🔒 Segurança nos Logs

### ❌ NUNCA logar:
- Senhas (mesmo criptografadas)
- Tokens JWT completos (apenas primeiros caracteres)
- Dados sensíveis de usuários
- Informações de cartão de crédito
- Chaves de API completas

### ✅ SEMPRE logar:
- Username (não senha)
- Timestamp
- Nível de severidade
- Contexto da operação
- IDs de objetos (não dados completos)

---

## 📊 Exemplos de Fluxos Completos

### Fluxo de Login Bem-Sucedido

```
2025-11-18 23:30:15.123 INFO  [http-nio-8080-exec-1] AuthController - 🔐 Tentativa de login - Username: usuario123
2025-11-18 23:30:15.125 DEBUG [http-nio-8080-exec-1] AuthController - Iniciando autenticação para usuário: usuario123
2025-11-18 23:30:15.127 INFO  [http-nio-8080-exec-1] AuthService - 🔐 AuthService: Iniciando autenticação - Username: usuario123
2025-11-18 23:30:15.130 DEBUG [http-nio-8080-exec-1] UserDetailsServiceImpl - 👤 Carregando UserDetails para: usuario123
2025-11-18 23:30:15.145 INFO  [http-nio-8080-exec-1] UserDetailsServiceImpl - ✅ UserDetails carregado com sucesso - Username: usuario123, ID: 1, Roles: [ROLE_USER]
2025-11-18 23:30:15.150 DEBUG [http-nio-8080-exec-1] JwtUtils - 🔑 Gerando JWT token para usuário: usuario123
2025-11-18 23:30:15.165 INFO  [http-nio-8080-exec-1] JwtUtils - ✅ JWT token gerado com sucesso para: usuario123
2025-11-18 23:30:15.167 INFO  [http-nio-8080-exec-1] AuthService - ✅ Token JWT gerado com sucesso - Username: usuario123, ID: 1, Roles: [ROLE_USER]
2025-11-18 23:30:15.170 INFO  [http-nio-8080-exec-1] AuthController - ✅ Login bem-sucedido - Username: usuario123, ID: 1, Roles: [ROLE_USER]
```

### Fluxo de Acesso a Endpoint Protegido

```
2025-11-18 23:31:00.100 DEBUG [http-nio-8080-exec-2] AuthTokenFilter - 🔒 Filtro JWT ativado para: GET /api/test/user
2025-11-18 23:31:00.102 DEBUG [http-nio-8080-exec-2] AuthTokenFilter - Token JWT encontrado na requisição
2025-11-18 23:31:00.105 DEBUG [http-nio-8080-exec-2] JwtUtils - 🔍 Extraindo username do JWT token
2025-11-18 23:31:00.108 DEBUG [http-nio-8080-exec-2] UserDetailsServiceImpl - 👤 Carregando UserDetails para: usuario123
2025-11-18 23:31:00.112 INFO  [http-nio-8080-exec-2] UserDetailsServiceImpl - ✅ UserDetails carregado com sucesso - Username: usuario123, ID: 1, Roles: [ROLE_USER]
2025-11-18 23:31:00.115 INFO  [http-nio-8080-exec-2] AuthTokenFilter - ✅ Usuário autenticado via JWT - Username: usuario123, Path: /api/test/user
2025-11-18 23:31:00.118 INFO  [http-nio-8080-exec-2] TestController - 👤 Acesso de usuário solicitado - /api/test/user - Username: usuario123
```

---

## ✅ Benefícios do Sistema de Logs

1. **Debug Facilitado** - Rastreamento completo de requisições
2. **Auditoria** - Registro de todas as ações de usuários
3. **Monitoramento** - Identificação rápida de problemas
4. **Segurança** - Detecção de tentativas de acesso não autorizado
5. **Performance** - Identificação de gargalos
6. **Manutenção** - Facilita correção de bugs

---

**Última atualização:** 2025-11-18

