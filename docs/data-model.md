# Data Model — ecom-user-service

> Documento vivo do modelo de dados. Atualizado sempre que uma entidade for criada, alterada ou removida.
> **Ultima atualizacao:** 2026-06-16

---

## Indice

- [Visao Geral](#visao-geral)
- [Diagrama ER](#diagrama-er)
- [Entidades](#entidades)
- [Indices e Performance](#indices-e-performance)
- [Classificacao de Privacidade](#classificacao-de-privacidade)
- [Decisoes de Modelagem](#decisoes-de-modelagem)

---

## Visao Geral

Duas entidades principais: `User` (usuarios do sistema) e `Address` (enderecos vinculados a cada usuario). O nucleo do dominio e o cadastro de usuarios com autenticacao via JWT.

**Banco de dados:** PostgreSQL 15
**ORM / acesso:** Hibernate via Spring Data JPA
**Extensoes relevantes:** uuid-ossp

---

## Diagrama ER

```mermaid
erDiagram
    USER {
        uuid id PK
        varchar name
        varchar email UK
        varchar password
        varchar document
        varchar phone
        timestamptz created_at
        timestamptz updated_at
    }

    ADDRESS {
        uuid id PK
        uuid user_id FK
        varchar street
        varchar number
        varchar complement
        varchar neighborhood
        varchar city
        varchar state
        varchar zip_code
        boolean is_default
        timestamptz created_at
    }

    USER ||--o{ ADDRESS : "tem muitos"
```

---

## Entidades

---

### User

> Entidade central do dominio. Representa um usuario registrado no sistema, com credenciais de acesso e dados pessoais.

**Tabela:** `users`
**Servico responsavel:** ecom-user-service

| Campo | Tipo SQL | Nullable | Default | Descricao |
|-------|----------|----------|---------|-----------|
| `id` | UUID | Nao | uuid_generate_v4() | Identificador unico do usuario |
| `name` | VARCHAR(255) | Nao | — | Nome completo do usuario |
| `email` | VARCHAR(255) | Nao | — | Email (usado para login) |
| `password` | VARCHAR(255) | Nao | — | Hash BCrypt da senha |
| `document` | VARCHAR(20) | Sim | NULL | CPF ou CNPJ |
| `phone` | VARCHAR(20) | Sim | NULL | Telefone de contato |
| `created_at` | TIMESTAMP | Nao | NOW() | Data de criacao do registro |
| `updated_at` | TIMESTAMP | Nao | NOW() | Data da ultima atualizacao |

**Constraints:**
- `UNIQUE(email)` — email deve ser unico para garantir unicidade de login e evitar duplicatas
- `CHECK (LENGTH(password) >= 60)` — BCrypt hash sempre tem 60 caracteres (validacao implicita via JPA)

**Relacionamentos:**
- Um `User` tem muitos `Address` via `addresses.user_id`

---

### Address

> Entidade que armazena enderecos de entrega ou cobranca associados a um usuario.

**Tabela:** `addresses`
**Servico responsavel:** ecom-user-service

| Campo | Tipo SQL | Nullable | Default | Descricao |
|-------|----------|----------|---------|-----------|
| `id` | UUID | Nao | uuid_generate_v4() | Identificador unico do endereco |
| `user_id` | VARCHAR(36) | Nao | — | FK para o usuario proprietario |
| `street` | VARCHAR(255) | Nao | — | Logradouro |
| `number` | VARCHAR(20) | Nao | — | Numero do imovel |
| `complement` | VARCHAR(255) | Sim | NULL | Complemento (apto, bloco, etc.) |
| `neighborhood` | VARCHAR(255) | Nao | — | Bairro |
| `city` | VARCHAR(255) | Nao | — | Cidade |
| `state` | VARCHAR(2) | Nao | — | UF (sigla de 2 caracteres) |
| `zip_code` | VARCHAR(9) | Nao | — | CEP (formato 00000000 ou 00000-000) |
| `is_default` | BOOLEAN | Nao | false | Indica se e o endereco padrao do usuario |
| `created_at` | TIMESTAMP | Nao | NOW() | Data de criacao do registro |

**Constraints:**
- `FOREIGN KEY (user_id) REFERENCES users(id)` — FK para a tabela `users`
- `CHECK (LENGTH(state) = 2)` — UF deve ter exatamente 2 caracteres

**Relacionamentos:**
- Muitos `Address` pertencem a um `User` via `addresses.user_id`

---

## Indices e Performance

| Indice | Tabela | Campos | Tipo | Motivo |
|--------|--------|--------|------|--------|
| `idx_users_email` | `users` | `email` | UNIQUE BTREE | Consulta de login por email |
| `idx_users_id` | `users` | `id` | PRIMARY KEY BTREE | Consulta de usuario por ID |
| `idx_addresses_user_id` | `addresses` | `user_id` | BTREE | Consulta de enderecos por usuario |
| `idx_addresses_default` | `addresses` | `user_id, is_default` WHERE `is_default = TRUE` | Partial BTREE | Busca rapida do endereco padrao |

---

## Classificacao de Privacidade

> Classifique cada campo sensivelmente de acordo com LGPD / GDPR.
> Campos sensiveis nunca devem ser expostos via API publica ou retornados em listagens.

| Campo | Tabela | Classificacao | Justificativa |
|-------|--------|---------------|---------------|
| `password` | `users` | Critico | Credencial de acesso — nunca sai do banco |
| `email` | `users` | Pessoal | Identificador direto do usuario |
| `document` | `users` | Pessoal | Documento fiscal CPF/CNPJ |
| `phone` | `users` | Pessoal | Dado de contato pessoal |
| `name` | `users` | Publico derivado | Nome de exibicao |
| `street`, `number`, `complement`, `neighborhood`, `city`, `state`, `zip_code` | `addresses` | Pessoal | Dados de localizacao do usuario |

**Regras gerais:**
- Campos marcados como **Critico** nunca saem do banco
- Campos marcados como **Pessoal** so sao retornados ao proprio usuario autenticado
- Campos marcados como **Publico derivado** podem aparecer em respostas de API

---

## Decisoes de Modelagem

### ADR-DM-001 — UUID como chave primaria

| Campo | Detalhe |
|-------|---------|
| **Status** | Aceita |
| **Data** | 2026-06-16 |
| **Contexto** | Necessidade de identificadores unicos nao sequenciais para evitar enumeracao de recursos em APIs publicas |
| **Decisao** | Usar UUID gerado via `GenerationType.UUID` do Hibernate em todas as entidades |
| **Alternativas consideradas** | `SERIAL` (sequencial), `IDENTITY`, `SEQUENCE` |
| **Consequencias** | Chaves nao enumeraveis, sem conflito entre ambientes, mas indices BTREE ligeiramente maiores que `BIGSERIAL` |

### ADR-DM-002 — user_id como VARCHAR(36) sem relacao JPA explicita

| Campo | Detalhe |
|-------|---------|
| **Status** | Aceita |
| **Data** | 2026-06-16 |
| **Contexto** | Address possui FK para User, mas nao ha relacao JPA mapeada com `@ManyToOne` |
| **Decisao** | Manter `userId` como coluna simples com `@Column` e sem `@ManyToOne` — a consistencia referencial e mantida pelo banco via constraint FK |
| **Alternativas consideradas** | `@ManyToOne` com `@JoinColumn` (mais acoplamento JPA) |
| **Consequencias** | Consultas de enderecos exigem joins manuais ou query explicita por `userId`; menor acoplamento entre entidades |
