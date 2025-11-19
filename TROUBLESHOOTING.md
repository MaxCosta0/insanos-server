# 🔧 Troubleshooting - Insanos Server

Soluções para problemas comuns ao executar o servidor.

---

## ❌ Erro: "Endereço já em uso" (BindException)

### Erro Completo:
```
Caused by: java.net.BindException: Endereço já em uso
```

### Causa:
Outro processo já está usando a porta 8080.

### Soluções:

#### ✅ Solução 1: Script Automático (RECOMENDADO)
```bash
./kill-port-8080.sh
```

Este script verifica e libera a porta automaticamente.

#### ✅ Solução 2: Usar o start.sh atualizado
```bash
./start.sh
```

O script start.sh foi atualizado para verificar e liberar a porta automaticamente antes de iniciar.

#### ✅ Solução 3: Liberar manualmente

**Passo 1: Encontrar o processo**
```bash
# Opção 1
lsof -i :8080

# Opção 2
ss -tulpn | grep :8080

# Opção 3
netstat -tulpn | grep :8080
```

**Passo 2: Matar o processo**
```bash
# Substitua <PID> pelo número do processo encontrado
kill -9 <PID>
```

**Exemplo completo:**
```bash
# Encontrar PID
PID=$(lsof -ti :8080)
echo "Processo encontrado: $PID"

# Matar processo
kill -9 $PID

# Verificar se foi liberado
lsof -i :8080 || echo "Porta 8080 está livre!"
```

#### ✅ Solução 4: Usar porta diferente

**Temporariamente:**
```bash
mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=9090
```

**Permanentemente (application.properties):**
```properties
server.port=9090
```

---

## ❌ Erro: Maven não encontrado

### Erro:
```
mvn: command not found
```

### Solução:
```bash
# Ubuntu/Debian
sudo apt update
sudo apt install maven

# Fedora
sudo dnf install maven

# macOS
brew install maven

# Verificar instalação
mvn --version
```

---

## ❌ Erro: Java não encontrado

### Erro:
```
java: command not found
```

### Solução:
```bash
# Verificar se está instalado
java -version

# Se não estiver, instalar (Ubuntu/Debian)
sudo apt install openjdk-17-jdk

# Ou usar SDKMAN (recomendado)
curl -s "https://get.sdkman.io" | bash
source "$HOME/.sdkman/bin/sdkman-init.sh"
sdk install java 17.0.8-oracle
```

---

## ❌ Erro: Dependências Maven não baixadas

### Sintomas:
- Erros de compilação
- "Cannot resolve symbol"
- Classes não encontradas

### Solução:
```bash
# Limpar cache e recompilar
mvn clean install -U

# Forçar atualização de dependências
mvn dependency:purge-local-repository
mvn clean install
```

---

## ❌ Erro: Lombok não está funcionando

### Sintomas:
- "Cannot resolve method 'setUsername'"
- "Cannot resolve method 'getEmail'"

### Solução:

**IntelliJ IDEA:**
1. Instale o plugin Lombok:
   - File → Settings → Plugins
   - Pesquise "Lombok"
   - Instale e reinicie

2. Habilite annotation processing:
   - File → Settings → Build, Execution, Deployment → Compiler → Annotation Processors
   - Marque "Enable annotation processing"

**Eclipse:**
1. Baixe lombok.jar
2. Execute: `java -jar lombok.jar`
3. Aponte para a instalação do Eclipse

**Via Maven:**
```bash
mvn clean compile
```

---

## ❌ Erro: H2 Console não abre

### Problema:
http://localhost:8080/h2-console retorna 404

### Solução:

**Verificar configuração (application.properties):**
```properties
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
```

**Verificar se o servidor está rodando:**
```bash
curl http://localhost:8080/h2-console
```

---

## ❌ Erro: JWT Token inválido

### Erro:
```
401 Unauthorized
```

### Causas e Soluções:

**1. Token expirado (24h por padrão)**
```bash
# Faça login novamente para obter novo token
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"insanos","password":"insanos321"}'
```

**2. Formato do header incorreto**
```bash
# ERRADO
Authorization: eyJhbG...

# CORRETO
Authorization: Bearer eyJhbG...
```

**3. Token não sendo enviado**
```javascript
// Verificar se o token está sendo adicionado
console.log(localStorage.getItem('user'));
```

---

## ❌ Erro: CORS

### Erro:
```
Access to XMLHttpRequest at 'http://localhost:8080/api/auth/login' 
from origin 'http://localhost:3000' has been blocked by CORS policy
```

### Solução:

**1. Verificar application.properties:**
```properties
cors.allowed.origins=http://localhost:3000,http://localhost:5173
```

**2. Adicionar sua origem:**
```properties
cors.allowed.origins=http://localhost:3000,http://localhost:5173,http://localhost:4200
```

**3. Verificar SecurityConfig.java:**
```java
configuration.setAllowedOrigins(Arrays.asList(allowedOrigins));
```

---

## ❌ Erro: Banco de dados não criado

### Problema:
Tabelas não são criadas automaticamente

### Solução:

**Verificar application.properties:**
```properties
spring.jpa.hibernate.ddl-auto=update
```

**Valores possíveis:**
- `none` - Não faz nada
- `validate` - Apenas valida o schema
- `update` - Atualiza o schema (RECOMENDADO para dev)
- `create` - Cria novo schema (APAGA dados existentes!)
- `create-drop` - Cria e apaga ao finalizar

---

## ❌ Erro: Usuário padrão não criado

### Problema:
Login com user@insanos.com não funciona

### Solução:

**1. Verificar logs:**
```bash
mvn spring-boot:run | grep "Usuário padrão"
```

**Saída esperada:**
```
✓ Usuário padrão criado com sucesso!
  Username: insanos
  Email: user@insanos.com
  Password: insanos321
```

**2. Se não aparecer, verificar DataInitializer.java:**
```bash
ls -la src/main/java/br/com/insanos/insanos_server/config/DataInitializer.java
```

**3. Recompilar:**
```bash
mvn clean compile
mvn spring-boot:run
```

**4. Verificar no H2 Console:**
```sql
SELECT * FROM users;
```

---

## ❌ Erro: Compilação falha

### Erro:
```
[ERROR] Failed to execute goal ... compilation failure
```

### Solução:

**1. Limpar e recompilar:**
```bash
mvn clean compile
```

**2. Se persistir, deletar cache local:**
```bash
rm -rf ~/.m2/repository/br/com/insanos
mvn clean install
```

**3. Verificar versão Java:**
```bash
java -version
# Deve ser Java 17 ou superior
```

---

## ❌ Erro: OutOfMemoryError

### Erro:
```
java.lang.OutOfMemoryError: Java heap space
```

### Solução:

**Aumentar memória heap:**
```bash
export MAVEN_OPTS="-Xmx1024m -Xms512m"
mvn spring-boot:run
```

**Ou ao executar JAR:**
```bash
java -Xmx1024m -Xms512m -jar target/insanos-server-0.0.1-SNAPSHOT.jar
```

---

## ❌ Erro: Senha não criptografada corretamente

### Problema:
Login não funciona mesmo com senha correta

### Solução:

**1. Verificar se BCrypt está funcionando:**
```java
// No DataInitializer.java
user.setPassword(passwordEncoder.encode("insanos321"));
```

**2. Testar senha manualmente:**
```bash
# Fazer login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"insanos","password":"insanos321"}' -v
```

---

## 🆘 Scripts de Ajuda

### Script: Verificar Saúde do Servidor
```bash
#!/bin/bash
echo "Verificando servidor..."
curl -f http://localhost:8080/api/test/all && echo "✅ Servidor OK" || echo "❌ Servidor não responde"
```

### Script: Reset Completo
```bash
#!/bin/bash
echo "Fazendo reset completo..."
./kill-port-8080.sh
mvn clean
rm -rf target/
mvn clean compile
echo "✅ Reset completo!"
```

### Script: Teste de Login Rápido
```bash
#!/bin/bash
echo "Testando login..."
RESPONSE=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"insanos","password":"insanos321"}')

if echo "$RESPONSE" | grep -q "token"; then
    echo "✅ Login funcionando!"
    echo "$RESPONSE" | jq .
else
    echo "❌ Erro no login"
    echo "$RESPONSE"
fi
```

---

## 📞 Ainda com problemas?

1. **Verifique os logs completos:**
   ```bash
   mvn spring-boot:run 2>&1 | tee server.log
   ```

2. **Execute com debug:**
   ```bash
   mvn spring-boot:run -Dspring-boot.run.arguments=--debug
   ```

3. **Verifique a documentação:**
   - INDEX.md - Índice geral
   - README.md - Documentação principal
   - COMMANDS.md - Comandos úteis

4. **Teste os endpoints:**
   - Use o arquivo api-tests.http
   - Teste com curl ou Postman

---

**Última atualização:** 2025-11-18

