# Backend On IA

Backend em Java com Spring Boot e LangChain4j para sustentar o frontend descrito no arquivo colado.

## Stack

- Java 21
- Spring Boot 3.5
- Spring Web + Validation + JPA
- PostgreSQL
- LangChain4j com integração Anthropic

## Rodando

Defina as variáveis de ambiente antes de subir a aplicação.

Exemplo no PowerShell:

```powershell
$env:ANTHROPIC_API_KEY="sua-chave"
$env:ANTHROPIC_MODEL="claude-sonnet-4-20250514"
mvn spring-boot:run
```

O schema é criado/atualizado automaticamente com `spring.jpa.hibernate.ddl-auto=update`.

## Endpoints principais

- `GET /me`
- `GET|POST|PATCH /categories`
- `GET|POST|PATCH /users`
- `GET|POST|PATCH|DELETE /templates`
- `GET|POST|PATCH /documents`
- `GET /documents/recent-created`
- `GET /documents/recent-updated`
- `GET /documents/metrics`
- `GET|POST|DELETE /chat/sessions`
- `GET|POST /chat/sessions/{id}/messages`
- `POST /uploads/files`
- `POST /knowledge/urls`
- `POST /knowledge/audio/transcribe`
- `GET /dashboard/home`
- `POST /api/chat`
- `POST /api/approve`

## Fluxo principal da IA

1. O frontend envia `POST /api/chat` com o histórico de mensagens e, opcionalmente, texto extraído de arquivo.
2. A IA decide entre responder normalmente ou devolver uma proposta estruturada.
3. Quando retorna `type: "proposal"`, o frontend entra em modo de revisão.
4. O frontend envia `POST /api/approve` com `acao: "APPROVE"` ou `acao: "REJECT"`.
5. Em aprovação, o backend grava um markdown em `knowledge-base/wiki/kb`, atualiza `knowledge-base/index.md` e `knowledge-base/CLAUDE.md`.

## Documentação da API

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON automático: `http://localhost:8080/v3/api-docs`
- Coleção Postman: `docs/backend-on-ia.postman_collection.json`
- OpenAPI YAML do repositório: `docs/openapi.yaml`

## Observações

- O controle de administrador para templates foi deixado simples via header `X-Admin: true`.
- O backend ainda mantém os fluxos de chat `CREATE`, `UPDATE` e `CONSULT`, mas o fluxo principal do produto agora é `POST /api/chat` + `POST /api/approve`.
- O banco sobe vazio, sem dados mockados ou carga inicial automática.
