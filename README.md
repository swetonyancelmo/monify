# Monify

Monify é uma aplicação Spring Boot desenvolvida em Java para gerenciamento financeiro pessoal. O projeto utiliza autenticação JWT para segurança, e inclui funcionalidades para gerenciar usuários, categorias, contas e transações.

## Tecnologias Utilizadas
- **Java** com **Spring Boot**
- **JWT** para autenticação
- **Flyway** para migrações de banco de dados
- **Swagger** para documentação da API
- **Maven** para gerenciamento de dependências
- **Docker Compose** para orquestração de containers

## Estrutura do Projeto
- **Controllers**: Endpoints para autenticação (AuthController), usuários (UserController) e categorias (CategoryController).
- **Services**: Lógica de negócio para autenticação, usuários e categorias.
- **Repositories**: Acesso aos dados para usuários e categorias.
- **Configurações**: Segurança, JWT, filtros e Swagger.
- **Domínios**: Entidades para usuários, categorias, contas e transações.
- **Migrações**: Scripts SQL para criação e alteração de tabelas (V1__ a V5__).
- **Testes**: Testes unitários básicos.

## Como Executar
1. Certifique-se de ter o Docker e Docker Compose instalados.
2. Execute `docker-compose up -d` para iniciar os serviços necessários (como o banco de dados).
3. Certifique-se de ter o Java e Maven instalados.
4. Execute `mvn spring-boot:run` para iniciar a aplicação.
5. Alternativamente, se preferir usar apenas containers, configure o docker-compose para incluir a aplicação e use `docker-compose up`.

A aplicação roda na porta padrão do Spring Boot (8080) e inclui documentação Swagger acessível via `/swagger-ui.html`.
