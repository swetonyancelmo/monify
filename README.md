# Monify

API REST de **finanças pessoais**: cada usuário organiza suas contas, categorias de receita e despesa e transações, com o saldo das contas atualizado automaticamente a cada lançamento.

**Java 21 · Spring Boot 4 · Spring Security + JWT · Spring Data JPA · Flyway · PostgreSQL · Swagger · Docker Compose**

## O que faz

- **Cadastro e login** com senha criptografada e token JWT
- **Contas** (por exemplo, carteira e banco) com saldo próprio e nome único por usuário
- **Categorias** de receita (`INCOME`) ou despesa (`EXPENSE`)
- **Transações** que atualizam o saldo da conta: receitas somam, despesas subtraem, e editar ou excluir uma transação desfaz o efeito anterior
- **Documentação interativa** no Swagger

## Regras de negócio

- Cada usuário só enxerga e altera as próprias contas, categorias e transações. O dono é sempre lido do token, nunca do corpo da requisição.
- O tipo da transação precisa bater com o tipo da categoria (uma despesa não entra numa categoria de receita).
- Uma despesa maior que o saldo da conta é recusada com "Saldo insuficiente".
- Criar, editar e excluir uma transação roda dentro de uma transação de banco (`@Transactional`), para o saldo nunca ficar pela metade.

## Decisões técnicas

- **Schema versionado com Flyway** (`V1` a `V6`) e Hibernate em modo `validate`: o banco só muda por migração, nunca pelo ORM.
- **DTOs de entrada e saída** separados das entidades, com Bean Validation nas requisições.
- **Tratamento de erros centralizado** (`GlobalExceptionHandler`), com respostas padronizadas para recurso não encontrado, regra de negócio e e-mail duplicado.
- **Documentação do Swagger separada dos controllers** (interfaces em `controller/docs`), para os controllers ficarem enxutos.
- **Segredo do JWT obrigatório:** sem `JWT_SECRET` a aplicação não sobe, em vez de usar uma chave padrão.

## Endpoints

| Recurso | Base | Operações |
|---|---|---|
| Autenticação | `/api/auth/v1` | `POST /register`, `POST /login` |
| Usuário | `/api/users/v1` | consultar e atualizar o próprio perfil |
| Contas | `/api/account/v1` | listar, criar, atualizar e excluir |
| Categorias | `/api/category/v1` | listar, criar, atualizar e excluir |
| Transações | `/api/transaction/v1` | listar, criar, atualizar e excluir |

Detalhes de cada rota, com exemplos, em `http://localhost:8080/swagger-ui.html`.

## Como rodar

Pré-requisitos: JDK 21, Maven e Docker.

```bash
docker compose up -d                       # PostgreSQL na porta 5433
export JWT_SECRET=$(openssl rand -base64 32)
mvn spring-boot:run                        # API em http://localhost:8080
```

As credenciais do banco podem ser trocadas por `DB_URL`, `DB_USERNAME` e `DB_PASSWORD`. Há um modelo em `.env.example`.

## Estrutura

```
src/main/java/com/swetonyancelmo/monify/
├── controller/   endpoints REST (e docs/ com as anotações do Swagger)
├── service/      regras de negócio
├── repository/   acesso a dados (Spring Data JPA)
├── domain/       entidades e enums
├── dto/          objetos de requisição e resposta
├── config/       segurança, JWT e Swagger
└── exception/    exceções de negócio e handler global
```

## Próximos passos

- Testes unitários dos serviços, começando pelas regras de saldo
- Relatório mensal de receitas e despesas por categoria
- Front-end para a API
