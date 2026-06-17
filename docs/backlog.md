# Backlog — ecom-user-service

> Registro vivo do progresso do projeto. Atualizado a cada mudanca de estado de uma funcionalidade.
> **Ultima atualizacao:** 2026-06-16

---

## Sobre o Projeto

Microservico de gerenciamento de usuarios do ecosistema ecom. Responsavel por cadastro, autenticacao JWT e gerenciamento de enderecos.

**Versao atual:** `1.0.0`
**Repositorio:** [github.com/odevpedro/ecom-user-service](https://github.com/odevpedro/ecom-user-service)
**Stack principal:** Java 21, Spring Boot 3.4, JPA + PostgreSQL, JWT (JJWT 0.12.6), Spring Security com BCrypt

---

## Legenda

| Simbolo | Significado |
|---------|-------------|
| `[ ]`   | Pendente |
| `[~]`   | Em andamento |
| `[x]`   | Concluido |
| `P0`    | Critico — bloqueia outras features |
| `P1`    | Alta prioridade |
| `P2`    | Media prioridade |
| `P3`    | Melhoria / nice-to-have |
| `XS` `S` `M` `L` `XL` | Estimativa de complexidade |

---

## Em Andamento

> Features atualmente sendo desenvolvidas. Idealmente, maximo de 2–3 itens simultaneos.

_Nenhum item em andamento no momento._

---

## Pendentes

> Ordenadas por prioridade. Itens de P0 e P1 devem entrar em "Em Andamento" primeiro.

| Prioridade | Item | Estimativa |
|------------|------|------------|
| `P1` | Refresh token — renovacao silenciosa sem reautenticacao | `M` |
| `P1` | Verificacao de email — envio de codigo e confirmacao | `M` |
| `P2` | Recuperacao de senha — fluxo de reset por email | `M` |
| `P3` | CRUD de enderecos — endpoints para criar, listar, atualizar e remover enderecos | `S` |
| `P3` | Paginacao na consulta de usuarios | `XS` |

---

## Concluidas

> Features finalizadas com suas respectivas datas de conclusao e links de referencia.

| Feature | Data | Estimativa | Referencia |
|---------|------|------------|------------|
| Cadastro de usuario com BCrypt | 2026-06-16 | `S` | `AuthController.register()` |
| Autenticacao JWT (login + token 24h) | 2026-06-16 | `M` | `AuthService.login()`, `JwtService` |
| Spring Security filter chain com JwtAuthFilter | 2026-06-16 | `M` | `SecurityConfig`, `JwtAuthFilter` |
| JPA + PostgreSQL (entidades User e Address) | 2026-06-16 | `M` | `User`, `Address`, `UserRepository` |
| Endpoints de health check (/health, /live, /ready) | 2026-06-16 | `XS` | `HealthController` |
| Consulta protegida de usuario por ID | 2026-06-16 | `S` | `UserController.getById()` |
| Request ID tracing (filtro X-Request-ID) | 2026-06-16 | `XS` | `RequestIdFilter` |
| Testes: 2 integracao AuthController + 3 unitarios JwtService | 2026-06-16 | `S` | `AuthControllerTest`, `JwtServiceTest` |

---

## Bugs Conhecidos

> Problemas identificados que ainda nao foram corrigidos.

_Nenhum bug conhecido no momento._

---

## Notas & Decisoes Pendentes

> Pontos em aberto que precisam de decisao antes de serem desenvolvidos.

_Nenhuma decisao pendente no momento._

---

## Historico de Versoes

| Versao | Data | Principais entregas |
|--------|------|---------------------|
| `1.0.0` | 2026-06-16 | Cadastro BCrypt, JWT auth, JPA + PostgreSQL, health checks, consulta protegida |
