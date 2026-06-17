# ecom-user-service

> Cadastro e autenticação de usuários para plataforma de e-commerce — gerencia registro, login com JWT e dados de endereço.

[![License](https://img.shields.io/github/license/odevpedro/ecom-user-service?style=flat-square)](./LICENSE)
[![Last Commit](https://img.shields.io/github/last-commit/odevpedro/ecom-user-service?style=flat-square)](https://github.com/odevpedro/ecom-user-service/commits/master)

---

## Sobre o Projeto

API REST responsável pelo gerenciamento de usuários do ecossistema de e-commerce. Fornece cadastro com senha hash (BCrypt), autenticação stateless via JWT e consulta de dados do usuário protegida por token. Opera como fonte única da verdade para dados de usuário, servindo os demais serviços (Cart, Order) via HTTP.

Faz parte de um ecossistema **polyglot** de microserviços (Java/Spring Boot, Python, Go, Node.js, TypeScript).

---

## Stack & Arquitetura

| Camada        | Tecnologia                          |
|---------------|--------------------------------------|
| Runtime       | Java 21 (Temurin)                    |
| Framework     | Spring Boot 3.4                      |
| ORM           | Spring Data JPA / Hibernate          |
| Autenticação  | Spring Security + JJWT 0.12          |
| Validação     | Jakarta Validation                   |
| Banco de dados| PostgreSQL 15                        |
| Build         | Maven                                |
| Infra         | Docker + Docker Compose              |
| CI/CD         | GitHub Actions                       |
| Testes        | JUnit 5 + Mockito                    |

> Padrão arquitetural: **Spring MVC Layered** com `Controller → Service → Repository (JPA)`. Segurança via Filter Chain.

---

## Estrutura de Pastas

```
src/main/java/com/ecom/user/
├── UserApplication.java                     # @SpringBootApplication
├── controller/
│   ├── AuthController.java                  # POST register, POST login
│   ├── UserController.java                  # GET /api/users/{id}
│   └── HealthController.java                # /health, /live, /ready
├── service/
│   ├── UserService.java                     # CRUD de usuários
│   ├── AuthService.java                     # Lógica de autenticação
│   └── JwtService.java                      # Geração/validação de tokens
├── model/
│   ├── User.java                            # JPA Entity
│   └── Address.java                         # JPA Entity
├── repository/
│   ├── UserRepository.java                  # Spring Data JPA
│   └── AddressRepository.java               # Spring Data JPA
├── dto/                                     # CreateUserRequest, LoginRequest, etc.
├── config/
│   ├── SecurityConfig.java                  # Filter chain + CORS
│   ├── JwtAuthFilter.java                   # Bearer token filter
│   ├── RequestIdFilter.java                 # X-Request-ID
│   └── ErrorResponse.java                   # Erro padronizado
└── exception/
    └── GlobalExceptionHandler.java           # @RestControllerAdvice
```

---

## Como Rodar Localmente

### Pré-requisitos

- Docker + Docker Compose
- JDK 21 + Maven

### Setup

```bash
cp .env.example .env
docker compose up -d postgres-user
./mvnw spring-boot:run
```

A API estará disponível em `http://localhost:3007`.

### Variáveis de Ambiente

| Variável              | Descrição                            | Valor padrão (dev)                                          |
|-----------------------|--------------------------------------|-------------------------------------------------------------|
| `PORT`                | Porta do servidor                    | `3007`                                                      |
| `DATABASE_URL`        | URL JDBC do PostgreSQL               | `jdbc:postgresql://localhost:5432/ecom_user`                |
| `DATABASE_USER`       | Usuário do banco                     | `ecom`                                                      |
| `DATABASE_PASSWORD`   | Senha do banco                       | `ecom`                                                      |
| `JWT_SECRET`          | Chave secreta para assinatura JWT    | `change-me-in-production`                                   |
| `JWT_EXPIRATION`      | Tempo de expiração do token (ms)     | `86400000` (24h)                                            |
| `SPRING_PROFILES_ACTIVE` | Perfil ativo do Spring            | `dev`                                                       |

---

## Testes

```bash
./mvnw test
```

**5 cenários:**
| Suite                        | Arquivo                              | Cenários |
|------------------------------|--------------------------------------|----------|
| Integração (AuthController)  | `AuthControllerTest.java`            | 2        |
| Unitários (JwtService)       | `JwtServiceTest.java`                | 3        |

---

## API — Endpoints

| Método | Rota                         | Auth     | Descrição                    |
|--------|------------------------------|----------|------------------------------|
| GET    | `/health`                    | Não      | Health check                 |
| GET    | `/live`                      | Não      | Liveness probe               |
| GET    | `/ready`                     | Não      | Readiness probe              |
| POST   | `/api/users/auth/register`   | Não      | Registro de usuário (201)    |
| POST   | `/api/users/auth/login`      | Não      | Login, retorna JWT           |
| GET    | `/api/users/{id}`            | JWT      | Dados do usuário             |

> Documentação interativa: `http://localhost:3007/swagger-ui.html` (Springdoc OpenAPI)

---

## Documentação Técnica

| Documento                                        | Descrição                                 |
|--------------------------------------------------|-------------------------------------------|
| [Fluxos de Funcionalidades](./docs/system-feature-flows.md) | Fluxo interno de cada feature |
| [Modelo de Dados](./docs/data-model.md)          | Entidades, relacionamentos e enums        |
| [Backlog](./backlog.md)                          | Status de desenvolvimento                 |

---

## Status do Projeto

```
[x] Cadastro de usuário com BCrypt
[x] Autenticação JWT (24h expiry)
[x] Proteção de rotas com Spring Security
[x] JPA + PostgreSQL com ddl-auto=update
[x] Health checks + Request ID + erro padronizado
[ ] Refresh token
[ ] Verificação de e-mail
[ ] Recuperação de senha
```

---

## Licença

Distribuído sob a licença MIT. Veja [LICENSE](./LICENSE) para mais informações.

---

<p align="center">
  Feito com foco em qualidade por <a href="https://github.com/odevpedro">@odevpedro</a>
</p>
