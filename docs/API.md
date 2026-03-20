# Contratos de API - pyloto_app-parceiro

Backend de referencia: `pyloto_atende` (FastAPI)

## Base URL auditada

| Ambiente | URL |
|---|---|
| Staging | `https://pyloto-atende-350969174034.us-central1.run.app/` |
| Production | `https://pyloto-atende-350969174034.us-central1.run.app/` |

Observacao: o backend real nao usa prefixo `/v1`.

## Envelope padrao

As rotas do app usam envelope padrao:

```json
{
  "success": true,
  "data": {},
  "message": "opcional",
  "meta": {}
}
```

Em erro, o backend responde no formato de `AppError`:

```json
{
  "ok": false,
  "code": "UNAUTHORIZED",
  "message": "Credenciais invalidas",
  "retryable": false
}
```

## Headers obrigatorios

- Rotas protegidas: `Authorization: Bearer <access_token>`
- `Content-Type: application/json`

## Contrato por rota (fonte de verdade: pyloto_atende)

### Auth

#### POST `/auth/login`

Request:

```json
{
  "email": "entregador@pyloto.com",
  "senha": "Senha@123"
}
```

Response `200`:

```json
{
  "success": true,
  "data": {
    "access_token": "<jwt>",
    "refresh_token": "<jwt>",
    "token_type": "bearer",
    "parceiro": {
      "id": "par-123",
      "nome": "Entregador",
      "email": "entregador@pyloto.com"
    }
  }
}
```

#### POST `/auth/register`

Request:

```json
{
  "nome": "Entregador Teste",
  "email": "entregador@pyloto.com",
  "senha": "Senha@123",
  "telefone": "11999999999",
  "cpf": "12345678900",
  "tipo_veiculo": "MOTO",
  "placa": "ABC1234"
}
```

Response `200`: mesmo shape de `/auth/login`.

#### POST `/auth/refresh`

Request:

```json
{ "refresh_token": "<jwt>" }
```

Response `200`:

```json
{
  "success": true,
  "data": {
    "access_token": "<jwt>",
    "refresh_token": "<jwt>",
    "token_type": "bearer"
  }
}
```

Erros relevantes: `401` quando token ausente/invalido/expirado.

#### POST `/auth/logout`

Response `200`:

```json
{ "success": true, "data": null }
```

### Corridas

#### GET `/corridas/disponiveis`

Query params:

- `lat` (obrigatorio, number)
- `lng` (obrigatorio, number)
- `raio` (opcional, default `5000`)
- `tipo` (opcional)

Response `200`:

```json
{
  "success": true,
  "data": [
    {
      "id": "PED-001",
      "status": "disponivel",
      "valor_parceiro": 22.5,
      "endereco_origem": { "rua": "Rua A", "lat": -23.55, "lng": -46.63 },
      "endereco_destino": { "rua": "Rua B", "lat": -23.56, "lng": -46.64 },
      "dados": { "nome": "Cliente" },
      "created_at": 1741290000
    }
  ],
  "meta": { "total": 1, "page": 0, "size": 1, "has_next": false }
}
```

#### GET `/corridas/{id}`

Response `200`:

```json
{
  "success": true,
  "data": {
    "id": "PED-001",
    "status": "aceito",
    "wa_id": "5511999999999",
    "valor_parceiro": 22.5,
    "endereco_origem": {},
    "endereco_destino": {},
    "dados": {},
    "created_at": 1741290000,
    "aceito_at": 1741290100
  }
}
```

#### POST `/corridas/{id}/aceitar`

Response `200`: envelope com `data` do pedido atualizado.

#### POST `/corridas/{id}/iniciar`

Response `200`:

```json
{ "success": true, "data": null, "message": "Corrida iniciada" }
```

#### POST `/corridas/{id}/coletar`

Response `200`:

```json
{ "success": true, "data": null, "message": "Coleta registrada" }
```

#### POST `/corridas/{id}/finalizar`

Request:

```json
{ "foto_comprovante_url": "https://..." }
```

Response `200`:

```json
{ "success": true, "data": null, "message": "Corrida finalizada" }
```

#### POST `/corridas/{id}/cancelar`

Request:

```json
{ "motivo": "nao foi possivel concluir" }
```

Response `200`:

```json
{ "success": true, "data": null, "message": "Corrida cancelada" }
```

#### GET `/corridas/historico`

Query params:

- `page` (default `0`)
- `size` (default `20`)

Response `200`:

```json
{
  "success": true,
  "data": {
    "items": [
      { "id": "PED-001", "status": "finalizado" }
    ],
    "page": 0,
    "size": 20,
    "total": 1,
    "has_next": false
  }
}
```

### Entregador

#### POST `/entregador/localizacao`

Request:

```json
{
  "latitude": -23.55,
  "longitude": -46.63,
  "timestamp": 1741290300000,
  "accuracy": 8.5,
  "speed": 10.0,
  "bearing": 90.0
}
```

Response `200`:

```json
{ "success": true, "data": null, "message": "Localizacao atualizada" }
```

#### POST `/entregador/localizacao/batch`

Request: array de `LocationUpdate`.

Response `200`:

```json
{ "success": true, "data": null, "message": "Lote de localizacao atualizado" }
```

#### GET `/entregador/perfil`

Response `200`:

```json
{
  "success": true,
  "data": {
    "id": "par-123",
    "nome": "Entregador",
    "email": "entregador@pyloto.com",
    "telefone": "11999999999",
    "cpf": "12345678900",
    "foto_url": "https://...",
    "veiculo_tipo": "moto",
    "veiculo_placa": "ABC1234",
    "nota_media": 4.9,
    "total_corridas": 120,
    "online": true
  }
}
```

#### PUT `/entregador/perfil`

Request:

```json
{
  "nome": "Novo Nome",
  "telefone": "11999999999",
  "tipo_veiculo": "MOTO",
  "placa": "ABC1234",
  "foto_url": "https://..."
}
```

Response `200`: mesmo shape de `/entregador/perfil`.

#### POST `/entregador/status`

Request:

```json
{ "disponivel": true }
```

Response `200`:

```json
{ "success": true, "data": { "disponivel": true } }
```

#### GET `/entregador/ganhos`

Query params:

- `periodo`
- `data_inicio` (opcional)
- `data_fim` (opcional)

Response `200`:

```json
{
  "success": true,
  "data": {
    "periodo": "DIARIO",
    "totalBruto": 100.0,
    "totalLiquido": 100.0,
    "totalCorridas": 4,
    "mediaValorCorrida": 25.0,
    "corridasPorDia": {},
    "dataInicio": null,
    "dataFim": null
  }
}
```

### Chat

#### GET `/chat/{corridaId}/mensagens`

Query params:

- `page` (default `0`)
- `size` (default `20`)

Response `200`:

```json
{
  "success": true,
  "data": {
    "items": [
      {
        "id": "msg-1",
        "corrida_id": "PED-001",
        "remetente_id": "par-123",
        "remetente_tipo": "ENTREGADOR",
        "conteudo": "Cheguei",
        "tipo_mensagem": "TEXTO",
        "timestamp": 1741290300000
      }
    ],
    "total": 1,
    "page": 0,
    "page_size": 20,
    "has_next": false
  }
}
```

#### POST `/chat/{corridaId}/mensagens`

Request:

```json
{ "conteudo": "Cheguei", "tipo": "texto" }
```

Response `200`:

```json
{
  "success": true,
  "data": {
    "id": "msg-1",
    "corrida_id": "PED-001",
    "remetente_id": "par-123",
    "remetente_tipo": "ENTREGADOR",
    "conteudo": "Cheguei",
    "tipo_mensagem": "TEXTO",
    "timestamp": 1741290300000
  }
}
```

### Notificacoes

#### POST `/notificacoes/token`

Request:

```json
{ "token": "<fcm-token>" }
```

Response `200`:

```json
{ "success": true, "data": null, "message": "Token FCM registrado" }
```

#### GET `/notificacoes`

Query params:

- `page` (default `0`)
- `size` (default `20`)

Response `200`:

```json
{
  "success": true,
  "data": {
    "items": [
      {
        "id": "notif-1",
        "titulo": "NOVO_PEDIDO",
        "corpo": "Pedido disponivel",
        "tipo": "NOVO_PEDIDO",
        "dados": { "pedido_id": "PED-001", "status": "disponivel" },
        "timestamp": 1741290300000
      }
    ],
    "total": 1,
    "page": 0,
    "page_size": 20,
    "has_next": false
  }
}
```

## Codigos de erro relevantes

- `401`: JWT ausente/invalido/expirado
- `404`: recurso nao encontrado
- `409`: conflito de negocio (ex: cadastro duplicado)
- `422`: erro de validacao de payload
- `500`: erro interno
