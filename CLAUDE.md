# CLAUDE.md — Monify

Guia completo para o agente Claude CLI operar neste projeto.

---

## Visão Geral

**Monify** é uma API REST de finanças pessoais construída com Java 21 e Spring Boot 4. Permite que usuários cadastrem contas bancárias, categorizem receitas/despesas e registrem transações, com controle de saldo automático.

- **Porta:** `8080`
- **Swagger UI:** `http://localhost:8080/swagger-ui.html`
- **Banco de dados:** PostgreSQL 15 (porta `5433` via Docker)
- **Gerenciador de dependências:** Maven

---

## Stack Tecnológica

| Tecnologia | Versão | Uso |
|---|---|---|
| Java | 21 | Linguagem |
| Spring Boot | 4.0.5 | Framework principal |
| Spring Security | (Boot managed) | Autenticação/Autorização |
| Spring Data JPA | (Boot managed) | Persistência |
| Flyway | (Boot managed) | Migrations de banco |
| PostgreSQL | 15 | Banco de dados |
| Auth0 Java JWT | 4.4.0 | Geração/validação de tokens JWT |
| SpringDoc OpenAPI | 3.0.2 | Documentação Swagger |
| Lombok | (Boot managed) | Redução de boilerplate |
| BCrypt | (Boot managed) | Hash de senhas |

---

## Como Iniciar o Projeto

```bash
# 1. Subir o banco de dados (PostgreSQL via Docker)
docker-compose up -d

# 2. Iniciar a aplicação
./mvnw spring-boot:run

# 3. (Alternativa) Compilar e executar o JAR
./mvnw clean package -DskipTests
java -jar target/monify-0.0.1-SNAPSHOT.jar
```

### Credenciais do banco (development)

```
URL:      jdbc:postgresql://localhost:5433/monify_db
Usuário:  postgres
Senha:    admin
```

---

## Estrutura de Pacotes

```
src/main/java/com/swetonyancelmo/monify/
├── MonifyApplication.java
├── config/
│   ├── AuthConfig.java          # UserDetailsService bean
│   ├── JWTUserData.java         # record(userId, email) — contexto de segurança
│   ├── SecurityConfig.java      # SecurityFilterChain — CSRF off, stateless
│   ├── SecurityFilter.java      # Extrai Bearer → coloca no SecurityContext
│   ├── SwaggerConfig.java       # OpenAPI com Bearer JWT
│   └── TokenConfig.java         # Gera/valida JWT (HMAC256, 24h)
├── controller/
│   ├── AccountController.java
│   ├── AuthController.java
│   ├── CategoryController.java
│   ├── TransactionController.java
│   ├── UserController.java
│   └── docs/                    # Interfaces OpenAPI (anotações Swagger)
├── domain/
│   ├── Account.java
│   ├── Category.java
│   ├── Transaction.java
│   ├── User.java
│   └── enums/
│       ├── CategoryType.java    # INCOME, EXPENSE
│       └── TransactionType.java # INCOME, EXPENSE
├── dto/
│   ├── request/                 # 9 records de entrada
│   └── response/                # 6 records de saída
├── exception/
│   ├── BusinessException.java         # HTTP 400
│   ├── EmailAlreadyExistsException.java # HTTP 400 (deveria ser 409)
│   ├── ResourceNotFoundException.java # HTTP 404
│   └── GlobalExceptionHandler.java    # @RestControllerAdvice
├── repository/                  # 4 interfaces JPA
└── service/                     # 5 classes de serviço

src/main/resources/
├── application.yml
└── db/migration/                # V1–V6 (Flyway)
```

---

## Modelo de Dados

### Diagrama ER

```
users ||--o{ account : owns
users ||--o{ category : owns
account ||--o{ transactions : has
category ||--o{ transactions : classifies
```

### Entidades

**`users`** — `id` (UUID), `name`, `email` (UNIQUE), `password` (BCrypt)

**`account`** — `id` (UUID), `name`, `balance` (NUMERIC 10,2), `user_id` FK → CASCADE

**`category`** — `id` (UUID), `name`, `type` (INCOME/EXPENSE), `user_id` FK → CASCADE

**`transactions`** — `id` (UUID), `type`, `amount`, `description`, `date`, `account_id` FK, `category_id` FK

> **Nota histórica:** A migration V4 criou a tabela com typo (`transacions`). A V6 renomeia para `transactions`. A entidade JPA usa `@Table(name = "transactions")`.

---

## API — Mapa de Endpoints

### Autenticação (público)

```
POST /api/auth/v1/register    # Cria novo usuário
POST /api/auth/v1/login       # Retorna JWT
```

### Usuários (autenticado)

```
GET  /api/users/v1/{uuid}     # Busca usuário
PUT  /api/users/v1/{uuid}     # Atualiza nome e/ou senha
```

### Contas (autenticado)

```
GET    /api/account/v1?page=0&size=12&direction=asc  # Lista paginada
POST   /api/account/v1                               # Cria conta
PUT    /api/account/v1/{id}                          # Atualiza conta
DELETE /api/account/v1/{id}                          # Remove conta
```

### Categorias (autenticado)

```
GET    /api/category/v1?page=0&size=12&direction=asc
POST   /api/category/v1
PUT    /api/category/v1/{id}
DELETE /api/category/v1/{id}
```

### Transações (autenticado)

```
GET    /api/transaction/v1?page=0&size=12&direction=asc
POST   /api/transaction/v1/{categoryId}/{accountId}
PUT    /api/transaction/v1/{transactionId}/{categoryId}/{accountId}
DELETE /api/transaction/v1/{transactionId}
```

### Documentação

```
GET /swagger-ui.html
GET /v3/api-docs/**
```

---

## Autenticação

1. Fazer `POST /api/auth/v1/login` com `{ "email": "...", "password": "..." }`
2. Receber `{ "token": "eyJ..." }`
3. Enviar em todas as requisições protegidas: `Authorization: Bearer <token>`
4. Token expira em **24 horas** (HMAC256)

> **Problema conhecido:** O JWT secret está hardcoded como `"secret"` em `TokenConfig.java`. Risco crítico de segurança — qualquer pessoa pode forjar tokens.

---

## DTOs de Entrada

| DTO | Campos obrigatórios | Validações |
|---|---|---|
| `CreateUserDto` | name, email, password | `@Email`, password 6–20 chars |
| `UpdateUserDto` | (todos opcionais) | password 6–20 se informado |
| `LoginRequestDto` | email, password | — |
| `CreateAccountDto` | name, balance | name 3–100 chars, balance `@PositiveOrZero` |
| `UpdateAccountDto` | name, balance | ambos `@NotBlank`/`@NotNull` |
| `CreateCategoryDto` | name, type | type: INCOME ou EXPENSE |
| `UpdateCategoryDto` | name, type | — |
| `CreateTransactionDto` | type, amount, description, date | amount `@Positive`, date formato `dd/MM/yyyy` |
| `UpdateTransactionDto` | (todos opcionais) | — |

---

## Regras de Negócio

1. **Isolamento por usuário:** Cada usuário acessa somente suas próprias contas, categorias e transações. O `userId` vem do JWT via `JWTUserData` do `SecurityContextHolder`.

2. **Nome único por usuário:** Nome de conta e nome de categoria devem ser únicos por usuário (verificado via `existsByNameAndUserId`).

3. **Tipo compatível:** O tipo da transação (`INCOME`/`EXPENSE`) deve coincidir com o `CategoryType` da categoria selecionada.

4. **Saldo suficiente:** Transações do tipo `EXPENSE` exigem saldo suficiente na conta. Exceção lançada caso contrário.

5. **Atualização de saldo automática:**
   - Ao **criar** transação: saldo é ajustado (+ para INCOME, - para EXPENSE)
   - Ao **atualizar** transação: saldo anterior é revertido e o novo é aplicado
   - Ao **deletar** transação: saldo é revertido

6. **Senha com BCrypt:** Tanto no cadastro quanto no update, a senha é sempre hasheada via BCrypt antes de persistir.

7. **Email único:** Verificado via `existsByEmail` antes de salvar, além de constraint UNIQUE no banco.

---

## Fluxo de Autenticação

```
POST /register → AuthService.create() → BCrypt(senha) → salvar usuário
POST /login    → AuthenticationManager.authenticate() → TokenConfig.generateToken() → retorna JWT
Requisição     → SecurityFilter.doFilter() → TokenConfig.validateToken() → JWTUserData no SecurityContext
```

---

## Migrações Flyway (V1–V6)

| Versão | Descrição |
|---|---|
| V1 | Cria tabela `users` (usa `pgcrypto` para UUID) |
| V2 | Cria tabela `category` com CHECK em `type` |
| V3 | Cria tabela `account` |
| V4 | Cria tabela `transacions` (typo intencional, corrigido na V6) |
| V5 | Adiciona `UNIQUE` em `users.email` |
| V6 | Renomeia `transacions` → `transactions` |

Para adicionar uma nova migration, criar o arquivo em `src/main/resources/db/migration/` com o padrão `V{n}__{descricao}.sql`.

---

## Configuração (`application.yml`)

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5433/monify_db
    username: postgres
    password: admin
  jpa:
    hibernate:
      ddl-auto: validate   # Flyway gerencia o schema
    show-sql: true
  flyway:
    enabled: true
    baseline-on-migrate: true
```

---

## Padrões de Código do Projeto

### Services
- Usam `@RequiredArgsConstructor` + campos `final` (constructor injection via Lombok)
- Anotados com `@Service` e `@Transactional` quando necessário
- Retornam DTOs de resposta (sem expor entidades)

### Controllers
- Atualmente ainda usam `@Autowired` em campo (inconsistência com os services)
- Têm interfaces em `controller/docs/` para anotações OpenAPI separadas
- Usam `@PreAuthorize("isAuthenticated()")` ou sem anotação (filtro JWT protege tudo exceto rotas públicas)

### Exceções
- `BusinessException` → HTTP 400 (regras de negócio violadas)
- `ResourceNotFoundException` → HTTP 404 (entidade não encontrada)
- `EmailAlreadyExistsException` → HTTP 400 (semanticamente deveria ser 409)
- Todas capturadas pelo `GlobalExceptionHandler` que retorna `ErrorResponseDto { message }`

### Repositórios
- Interfaces que estendem `JpaRepository`
- Queries customizadas via `@Query` JPQL ou method naming

---

## Débitos Técnicos Conhecidos

### Críticos
- **JWT secret hardcoded:** `"secret"` em `TokenConfig.java` — deve ir para variável de ambiente via `@Value("${security.jwt.secret}")`
- **UserController sem validação de ownership:** Qualquer usuário autenticado pode buscar/editar outro usuário pelo UUID

### Qualidade
- `springdoc-openapi` duplicado no `pom.xml` — remover uma entrada
- Controllers com `@Autowired` em campo — padronizar para constructor injection
- Sem campos de auditoria (`createdAt`, `updatedAt`) nas entidades
- Mapeamento entity→DTO manual em cada service (candidato ao MapStruct)
- Sem Spring Profiles (dev/prod)

### Testes
- Apenas um teste vazio (`contextLoads`) — sem cobertura de services, controllers ou repositórios

### Banco
- Sem índices nas colunas de FK (`user_id`, `account_id`, `category_id`, `date`)
- Problema N+1 potencial nas queries de transações (sem `JOIN FETCH`)
- `CategoryRepository.deleteByIdAndUserId` declarado mas não usado pelos services
- `category_id` era `nullable` na V4 mas a entidade JPA declara `nullable = false`

---

## Roadmap de Melhorias (Priorizado)

### Fase 1 — Correções imediatas
- Mover JWT secret para variável de ambiente
- Adicionar validação de ownership no `UserController`
- Remover dependência duplicada do `pom.xml`
- Padronizar injeção por construtor nos controllers

### Fase 2 — Testes
- Testes unitários com JUnit 5 + Mockito nos services
- Testes de repositório com `@DataJpaTest` + Testcontainers
- Testes de integração com `MockMvc`

### Fase 3 — Qualidade e Arquitetura
- `BaseEntity` com `createdAt`/`updatedAt` (`@MappedSuperclass` + `@EnableJpaAuditing`)
- MapStruct para mapeamentos entity→DTO
- Índices no banco via nova migration Flyway (V7)
- Resolver N+1 com `JOIN FETCH` nas queries de transações
- Soft delete (`@SQLDelete` + `@SQLRestriction`)

### Fase 4 — Segurança
- Spring Profiles (dev/prod) com `application-dev.yml`
- CORS explícito com `CorsConfigurationSource`
- Refresh token
- Rate limiting no `/login` com Bucket4j

### Fase 5 — Observabilidade
- Spring Boot Actuator
- Logging estruturado com `@Slf4j` nos services
- HikariCP configurado no `application.yml`
- Correlation ID via `OncePerRequestFilter`

### Fase 6 — Novas funcionalidades
- Dashboard financeiro (`GET /api/dashboard`)
- Filtros em transações por período, tipo, categoria
- Transferência entre contas
- Relatórios mensais/anuais

### Fase 7 — DevOps
- Dockerfile multi-stage
- CI/CD com GitHub Actions
- JaCoCo (cobertura mínima 70%)
- SonarCloud

---

## Comandos Úteis

```bash
# Compilar sem testes
./mvnw clean package -DskipTests

# Executar testes
./mvnw test

# Subir banco
docker-compose up -d

# Parar banco
docker-compose down

# Ver logs do banco
docker logs monify

# Conectar ao banco via psql
docker exec -it monify psql -U postgres -d monify_db
```

---

## Convenções ao Modificar o Código

1. **Novos endpoints** devem ter a interface de documentação correspondente em `controller/docs/`
2. **Novos campos** nas entidades precisam de uma migration Flyway (`V{n}__descricao.sql`)
3. **Novas exceções de negócio** devem estender `RuntimeException` e ser registradas no `GlobalExceptionHandler`
4. **Queries customizadas** devem ser adicionadas ao repositório via `@Query` JPQL, não no service
5. **Nunca** usar `ddl-auto: create` ou `ddl-auto: update` — o Flyway gerencia o schema
6. **Sempre** hashear senhas com `passwordEncoder.encode()` antes de persistir
