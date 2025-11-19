# 🛠️ Comandos Úteis - Insanos Server

## Maven

### Compilação
```bash
# Compilar sem executar testes
mvn clean compile

# Compilar e empacotar
mvn clean package

# Pular testes
mvn clean package -DskipTests

# Limpar build
mvn clean
```

### Executar
```bash
# Executar com Maven
mvn spring-boot:run

# Executar JAR compilado
java -jar target/insanos-server-0.0.1-SNAPSHOT.jar

# Executar com perfil específico
mvn spring-boot:run -Dspring-boot.run.profiles=prod

# Executar com porta diferente
mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=9090
```

### Testes
```bash
# Executar todos os testes
mvn test

# Executar teste específico
mvn test -Dtest=InsanosServerApplicationTests

# Executar com cobertura
mvn test jacoco:report
```

### Dependências
```bash
# Baixar/atualizar dependências
mvn dependency:resolve

# Ver árvore de dependências
mvn dependency:tree

# Verificar atualizações
mvn versions:display-dependency-updates
```

## Git

### Básico
```bash
# Inicializar repositório
git init

# Adicionar arquivos
git add .

# Commit
git commit -m "feat: implementar autenticação JWT"

# Push
git push origin main

# Status
git status
```

### Branches
```bash
# Criar branch
git checkout -b feature/nova-funcionalidade

# Mudar de branch
git checkout main

# Listar branches
git branch -a

# Deletar branch
git branch -d feature/antiga-funcionalidade
```

## Docker (Opcional)

### Criar Dockerfile
```dockerfile
FROM eclipse-temurin:25-jdk-alpine
WORKDIR /app
COPY target/insanos-server-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### Comandos Docker
```bash
# Build
docker build -t insanos-server .

# Run
docker run -p 8080:8080 insanos-server

# Com variáveis de ambiente
docker run -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/insanosdb \
  -e JWT_SECRET=seu-secret-aqui \
  insanos-server

# Docker Compose
docker-compose up -d
```

### docker-compose.yml
```yaml
version: '3.8'
services:
  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      - SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/insanosdb
      - SPRING_DATASOURCE_USERNAME=postgres
      - SPRING_DATASOURCE_PASSWORD=postgres
    depends_on:
      - db
  
  db:
    image: postgres:15-alpine
    environment:
      - POSTGRES_DB=insanosdb
      - POSTGRES_USER=postgres
      - POSTGRES_PASSWORD=postgres
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data

volumes:
  postgres_data:
```

## Banco de Dados

### H2 Console
```bash
# Acessar: http://localhost:8080/h2-console
# JDBC URL: jdbc:h2:mem:insanosdb
# Username: sa
# Password: (vazio)
```

### PostgreSQL
```bash
# Instalar
sudo apt install postgresql  # Ubuntu/Debian
brew install postgresql       # macOS

# Criar banco
createdb insanosdb

# Conectar
psql -U postgres -d insanosdb

# Comandos SQL
\dt                  # Listar tabelas
\d users            # Descrever tabela users
SELECT * FROM users;
```

### MySQL (alternativa)
```properties
# application.properties
spring.datasource.url=jdbc:mysql://localhost:3306/insanosdb
spring.datasource.username=root
spring.datasource.password=senha
spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect
```

## Testes de API

### cURL
```bash
# Registrar
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"user","email":"user@email.com","password":"pass123"}'

# Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"user","password":"pass123"}'

# Endpoint protegido
TOKEN="seu_token_aqui"
curl -X GET http://localhost:8080/api/auth/me \
  -H "Authorization: Bearer $TOKEN"
```

### HTTPie (alternativa mais amigável)
```bash
# Instalar
pip install httpie

# Registrar
http POST localhost:8080/api/auth/register \
  username=user email=user@email.com password=pass123

# Login
http POST localhost:8080/api/auth/login \
  username=user password=pass123

# Com token
http GET localhost:8080/api/auth/me \
  "Authorization: Bearer $TOKEN"
```

## Logs

### Ver logs
```bash
# Logs em tempo real
tail -f logs/application.log

# Últimas 100 linhas
tail -100 logs/application.log

# Buscar erro
grep "ERROR" logs/application.log

# Logs coloridos com Maven
mvn spring-boot:run | grep --color=always "ERROR\|WARN\|INFO"
```

### Configurar logging (application.properties)
```properties
# Nível de log
logging.level.root=INFO
logging.level.br.com.insanos=DEBUG
logging.level.org.springframework.security=DEBUG

# Arquivo de log
logging.file.name=logs/application.log
logging.file.max-size=10MB
logging.file.max-history=30
```

## Performance

### Monitorar memória
```bash
# Ver uso de memória
jps -lvm

# Dump de memória
jmap -dump:live,format=b,file=heap.bin <PID>

# Analisar heap
jhat heap.bin
```

### Configurar JVM
```bash
# Executar com opções JVM
java -Xms512m -Xmx1024m -jar target/insanos-server-0.0.1-SNAPSHOT.jar

# Com garbage collector otimizado
java -XX:+UseG1GC -jar target/insanos-server-0.0.1-SNAPSHOT.jar
```

## Produção

### Build para produção
```bash
# Compilar com perfil de produção
mvn clean package -Pprod -DskipTests

# Otimizar JAR
mvn clean package -DskipTests spring-boot:repackage
```

### Variáveis de ambiente
```bash
# Linux/Mac
export JWT_SECRET="seu-secret-muito-seguro"
export SPRING_DATASOURCE_URL="jdbc:postgresql://localhost:5432/insanosdb"

# Windows
set JWT_SECRET=seu-secret-muito-seguro
set SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/insanosdb

# Executar com variáveis
java -jar target/insanos-server-0.0.1-SNAPSHOT.jar
```

### Systemd Service (Linux)
```ini
# /etc/systemd/system/insanos-server.service
[Unit]
Description=Insanos Server
After=syslog.target network.target

[Service]
User=www-data
ExecStart=/usr/bin/java -jar /opt/insanos-server/app.jar
SuccessExitStatus=143
Environment="JAVA_OPTS=-Xms512m -Xmx1024m"
Environment="JWT_SECRET=seu-secret"

[Install]
WantedBy=multi-user.target
```

```bash
# Comandos systemd
sudo systemctl start insanos-server
sudo systemctl stop insanos-server
sudo systemctl restart insanos-server
sudo systemctl status insanos-server
sudo systemctl enable insanos-server  # Iniciar no boot
```

## Troubleshooting

### Porta em uso (Erro: "Endereço já em uso")
```bash
# Erro comum: java.net.BindException: Endereço já em uso

# Solução 1: Use o script automatizado
./kill-port-8080.sh

# Solução 2: Encontrar e matar o processo manualmente
lsof -i :8080
# ou
ss -tulpn | grep :8080
# ou
netstat -tulpn | grep :8080

# Matar o processo (substitua PID pelo número encontrado)
kill -9 <PID>

# Solução 3: Usar porta diferente
mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=9090
```

### Limpar cache Maven
```bash
# Limpar cache local
mvn dependency:purge-local-repository

# Forçar atualização
mvn clean install -U
```

### Recompilar completamente
```bash
# Limpar tudo e recompilar
rm -rf target/
mvn clean compile
```

### Debug remoto
```bash
# Iniciar com debug
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005"

# Conectar IDE na porta 5005
```

## Atualizações

### Atualizar Spring Boot
```bash
# Ver versão atual
mvn help:evaluate -Dexpression=project.parent.version -q -DforceStdout

# Atualizar no pom.xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.5.7</version>  <!-- Atualizar aqui -->
</parent>
```

### Verificar vulnerabilidades
```bash
# OWASP Dependency Check
mvn org.owasp:dependency-check-maven:check

# Snyk
npm install -g snyk
snyk test --all-projects
```

## Backup

### Backup do banco H2
```bash
# Exportar dados
java -cp target/classes org.h2.tools.Script \
  -url jdbc:h2:mem:insanosdb -user sa -script backup.sql

# Importar dados
java -cp target/classes org.h2.tools.RunScript \
  -url jdbc:h2:mem:insanosdb -user sa -script backup.sql
```

### Backup PostgreSQL
```bash
# Backup
pg_dump -U postgres insanosdb > backup.sql

# Restaurar
psql -U postgres insanosdb < backup.sql
```

---

**Dica:** Salve este arquivo como referência para comandos frequentes!

