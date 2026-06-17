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
│   ├── AuthController.java                  # POST register, login, refresh, verify-email, resend-verification, forgot-password, reset-password
│   ├── UserController.java                  # GET /api/users/{id}
│   └── HealthController.java                # /health, /live, /ready
├── service/
│   ├── UserService.java                     # CRUD de usuários
│   ├── AuthService.java                     # Lógica de autenticação
│   ├── JwtService.java                      # Geração/validação de tokens
│   ├── RefreshTokenService.java             # Refresh token com rotação
│   ├── EmailVerificationService.java        # Verificação de email (scaffold)
│   └── PasswordResetService.java            # Recuperação de senha (scaffold)
├── model/
│   ├── User.java                            # JPA Entity
│   ├── Address.java                         # JPA Entity
│   ├── RefreshToken.java                    # JPA Entity — refresh tokens
│   ├── EmailVerification.java               # JPA Entity — verificação de email
│   └── PasswordReset.java                   # JPA Entity — reset de senha
├── repository/
│   ├── UserRepository.java                  # Spring Data JPA
│   ├── AddressRepository.java               # Spring Data JPA
│   ├── RefreshTokenRepository.java          # Spring Data JPA
│   ├── EmailVerificationRepository.java     # Spring Data JPA
│   └── PasswordResetRepository.java         # Spring Data JPA
├── dto/                                     # CreateUserRequest, LoginRequest, RefreshTokenRequest, etc.
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

**11 cenários:**
| Suite                        | Arquivo                              | Cenários |
|------------------------------|--------------------------------------|----------|
| Integração (AuthController)  | `AuthControllerTest.java`            | 2        |
| Unitários (JwtService)       | `JwtServiceTest.java`                | 3        |
| Unitários (RefreshToken)     | `RefreshTokenServiceTest.java`       | 6        |

---

## API — Endpoints

| Método | Rota                               | Auth     | Descrição                                |
|--------|------------------------------------|----------|------------------------------------------|
| GET    | `/health`                          | Não      | Health check                             |
| GET    | `/live`                            | Não      | Liveness probe                           |
| GET    | `/ready`                           | Não      | Readiness probe                          |
| POST   | `/api/users/auth/register`         | Não      | Registro de usuário (201)                |
| POST   | `/api/users/auth/login`            | Não      | Login, retorna JWT + refresh token       |
| POST   | `/api/users/auth/refresh`          | Não      | Renova access token via refresh token    |
| POST   | `/api/users/auth/verify-email`     | Não      | Verifica email com token                 |
| POST   | `/api/users/auth/resend-verification` | Não   | Reenvia token de verificação (stub)      |
| POST   | `/api/users/auth/forgot-password`  | Não      | Solicita reset de senha (stub)           |
| POST   | `/api/users/auth/reset-password`   | Não      | Executa reset de senha com token         |
| GET    | `/api/users/{id}`                  | JWT      | Dados do usuário                         |

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
[x] Refresh token com rotação (7 dias de validade)
[x] Verificação de e-mail — estrutura e endpoints
[x] Recuperação de senha — estrutura e endpoints
```

---

## Licença

Distribuído sob a licença MIT. Veja [LICENSE](./LICENSE) para mais informações.

---

<p align="center">
  Feito com foco em qualidade por <a href="https://github.com/odevpedro">@odevpedro</a>
</p>
