# Contratos de API - pyloto_apk

Backend: **pyloto_atende** (FastAPI)
Documentacao de arquitetura: [ARQUITETURA-COMUNICACAO.md](./ARQUITETURA-COMUNICACAO.md)

## Base URLs

| Ambiente | URL |
|---|---|
| Staging | `https://staging-api.pyloto.com.br/v1/` |
| Production | `https://api.pyloto.com.br/v1/` |

## Autenticacao

Todas as rotas (exceto `/auth/login` e `/auth/register`) exigem:
```
Authorization: Bearer <access_token>
```

O `AuthInterceptor` tenta renovar automaticamente com `POST /auth/refresh` ao receber HTTP 401.

---

## Auth

| Metodo | Endpoint | Descricao |
|---|---|---|
| POST | `/auth/login` | Login com email + senha |
| POST | `/auth/register` | Cadastro de novo parceiro |
| POST | `/auth/refresh` | Renovar access token |
| POST | `/auth/logout` | Logout (invalida token no Redis) |

### POST /auth/register

```json
// Request
{
  "nome": "Carlos Silva",
  "email": "carlos@email.com",
  "senha": "senha123",
  "telefone": "42999991111",
  "cpf": "123.456.789-00",
  "tipo": "entregador",
  "tipo_veiculo": "moto",
  "placa": "ABC-1234"
}

// tipo: entregador | diarista | pedreiro | pintor | marido_aluguel
// tipo_veiculo e placa: obrigatorios apenas para tipo=entregador
```

```json
// Response 201
{
  "success": true,
  "data": {
    "access_token": "<jwt>",
    "token_type": "bearer",
    "parceiro": {
      "id": "par_abc123",
      "nome": "Carlos Silva",
      "tipo": "entregador",
      "status": "pendente"
    }
  }
}
// status "pendente": aguarda aprovacao manual da Pyloto. Exibir tela "Cadastro em Analise".
// status "ativo": pode receber pedidos.
```

### POST /auth/refresh

```json
// Request
{ "refresh_token": "<refresh_token>" }

// Response 200
{
  "success": true,
  "data": { "access_token": "<novo_jwt>", "token_type": "bearer" }
}
```

---

## Pedidos (alias: Corridas no APK)

"Corrida" e o nome interno do APK para qualquer tipo de pedido/servico.

| Metodo | Endpoint | Descricao |
|---|---|---|
| GET | `/corridas/disponiveis` | Pedidos proximos ao parceiro |
| GET | `/corridas/{id}` | Detalhes de um pedido |
| POST | `/corridas/{id}/aceitar` | Parceiro aceita o pedido |
| POST | `/corridas/{id}/iniciar` | Parceiro inicia a execucao |
| POST | `/corridas/{id}/coletar` | Parceiro confirma coleta (so entregador) |
| POST | `/corridas/{id}/finalizar` | Parceiro finaliza o pedido |
| POST | `/corridas/{id}/cancelar` | Parceiro cancela o pedido |
| GET | `/corridas/historico` | Historico paginado do parceiro |

### GET /corridas/disponiveis

```
Query params:
  lat: float (obrigatorio)
  lng: float (obrigatorio)
  raio: int (metros, default 5000, min 100, max 50000)
  tipo: string (opcional — filtra pelo tipo_servico do pedido)
```

```json
// Response 200
{
  "success": true,
  "data": {
    "items": [
      {
        "id": "ped_xyz",
        "tipo_servico": "entrega",
        "endereco_origem": { "logradouro": "Rua A, 100", "bairro": "Centro" },
        "endereco_destino": { "logradouro": "Rua B, 200", "bairro": "Jardim" },
        "distancia_km": 3.2,
        "tempo_estimado_min": 15,
        "valor_parceiro": 22.50,
        "criado_ha": "5 min"
      }
    ],
    "total": 3
  }
}
```

O filtro por `tipo` respeita o tipo do parceiro autenticado: um parceiro do tipo `diarista`
so ve pedidos de `diarista`, independente do filtro enviado.

### GET /corridas/{id}

```json
// Response 200
{
  "success": true,
  "data": {
    "id": "ped_xyz",
    "tipo_servico": "entrega",
    "status": "pago_aguardando_parceiro",
    "wa_id": "5542999990000",
    "solicitante_nome": "Joao P.",
    "endereco_origem": {
      "logradouro": "Rua A, 100", "bairro": "Centro",
      "lat": -25.38, "lng": -49.26
    },
    "endereco_destino": {
      "logradouro": "Rua B, 200", "bairro": "Jardim",
      "lat": -25.40, "lng": -49.28
    },
    "endereco_servico": {},
    "valor_parceiro": 22.50,
    "dados": {},
    "etapas": [],
    "parceiro_id": null,
    "created_at": 1741290000
  }
}
```

### POST /corridas/{id}/aceitar

Sem body. Retorna o pedido atualizado com `status: "parceiro_aceito"`.
Ao aceitar, o solicitante recebe mensagem automatica via WhatsApp (numero principal):
"Seu pedido foi aceito por [Nome do Parceiro]! Acompanhe em: pyloto.com.br/acompanhar/{id}"

### POST /corridas/{id}/iniciar

Sem body. Muda status para `em_execucao`. Para entregadores: inicia rastreamento GPS ativo.

### POST /corridas/{id}/coletar

Sem body. Apenas para `tipo_servico=entrega`. Registra etapa de coleta.

### POST /corridas/{id}/finalizar

```json
// Request (opcional)
{ "foto_comprovante_url": "https://storage.googleapis.com/pyloto/comprovante_xyz.jpg" }
```

Muda status para `concluido`. Dispara avaliacao pelo WhatsApp ao solicitante.

### POST /corridas/{id}/cancelar

```json
// Request
{ "motivo": "Nao consigo chegar ao local" }
```

### GET /corridas/historico

```
Query params:
  page: int (default 0)
  size: int (default 20, max 100)
```

```json
// Response 200
{
  "success": true,
  "data": {
    "items": [ /* lista de pedidos concluidos/cancelados */ ],
    "page": 0,
    "size": 20,
    "total": 47,
    "has_next": true
  }
}
```

---

## Parceiro

| Metodo | Endpoint | Descricao |
|---|---|---|
| GET | `/entregador/perfil` | Perfil do parceiro autenticado |
| PUT | `/entregador/perfil` | Atualizar dados do perfil |
| POST | `/entregador/localizacao` | Enviar ponto GPS |
| POST | `/entregador/localizacao/batch` | Enviar lote de pontos GPS (offline-first) |
| POST | `/entregador/status` | Ficar online/offline |
| GET | `/entregador/ganhos` | Ganhos por periodo |

### GET /entregador/perfil

```json
// Response 200
{
  "success": true,
  "data": {
    "id": "par_abc123",
    "nome": "Carlos Silva",
    "email": "carlos@email.com",
    "telefone": "42999991111",
    "tipo": "entregador",
    "status": "ativo",
    "disponivel": true,
    "online": true,
    "veiculo_tipo": "moto",
    "veiculo_placa": "ABC-1234",
    "nota_media": 4.8,
    "total_corridas": 152,
    "total_ganho": 3850.00,
    "foto_url": "https://...",
    "created_at": 1700000000
  }
}
```

### POST /entregador/localizacao

```json
// Request
{
  "latitude": -25.3845,
  "longitude": -49.2654,
  "timestamp": "2026-03-06T14:30:00Z",
  "accuracy": 8.5,
  "speed": 12.3,
  "bearing": 270.0
}
```

### POST /entregador/localizacao/batch

```json
// Request — array de LocationUpdate
[
  { "latitude": -25.38, "longitude": -49.26, "timestamp": "2026-03-06T14:30:00Z" },
  { "latitude": -25.39, "longitude": -49.27, "timestamp": "2026-03-06T14:30:10Z" }
]
```

### POST /entregador/status

```json
// Request
{ "disponivel": true }
// disponivel=true: parceiro esta online e aceitando pedidos
// disponivel=false: parceiro esta offline
```

### GET /entregador/ganhos

```
Query params:
  periodo: DIARIO | SEMANAL | MENSAL | PERSONALIZADO (default DIARIO)
  data_inicio: ISO date (obrigatorio quando periodo=PERSONALIZADO)
  data_fim: ISO date (obrigatorio quando periodo=PERSONALIZADO)
```

```json
// Response 200
{
  "success": true,
  "data": {
    "total_bruto": 150.00,
    "taxa_pyloto": 22.50,
    "total_liquido": 127.50,
    "total_corridas": 8,
    "nota_media": 4.9,
    "extrato": [
      {
        "pedido_id": "ped_xyz",
        "tipo_servico": "entrega",
        "valor": 22.50,
        "concluido_at": 1741290000
      }
    ]
  }
}
```

---

## Chat (Comunicacao Parceiro <-> Solicitante)

O chat usa o **canal parceiro** (segundo numero WhatsApp Business) como ponte.
Ver [ARQUITETURA-COMUNICACAO.md](./ARQUITETURA-COMUNICACAO.md) para fluxo completo.

| Metodo | Endpoint | Descricao |
|---|---|---|
| GET | `/chat/{pedidoId}/mensagens` | Historico de mensagens do pedido |
| POST | `/chat/{pedidoId}/mensagens` | Enviar mensagem ao solicitante |
| POST | `/chat/{pedidoId}/chamar` | Iniciar chamada com o solicitante |

### GET /chat/{pedidoId}/mensagens

```
Query params:
  page: int (default 0)
  size: int (default 50)
```

```json
// Response 200
{
  "success": true,
  "data": {
    "items": [
      {
        "id": "msg_abc",
        "de": "parceiro",
        "conteudo": "Cheguei no local de coleta!",
        "tipo": "texto",
        "status": "entregue",
        "created_at": 1741290000
      },
      {
        "id": "msg_def",
        "de": "solicitante",
        "conteudo": "Ok, pode subir!",
        "tipo": "texto",
        "status": "lido",
        "created_at": 1741290060
      }
    ],
    "total": 2,
    "has_next": false
  }
}
```

### POST /chat/{pedidoId}/mensagens

```json
// Request
{
  "conteudo": "Cheguei no local de coleta!",
  "tipo": "texto"
}
// tipo: texto | imagem (Fase 2)
```

```json
// Response 201
{
  "success": true,
  "data": {
    "message_id": "msg_abc",
    "status": "enviado"
  }
}
```

A mensagem e entregue ao solicitante pelo WhatsApp (canal parceiro).

### POST /chat/{pedidoId}/chamar

Sem body.

```json
// Response 200 — Fase interim (MVP)
{
  "success": true,
  "data": {
    "interim": true,
    "mensagem": "Solicitante avisado pelo WhatsApp. Aguarde o retorno."
  }
}

// Response 200 — Fase 2 (WebRTC)
{
  "success": true,
  "data": {
    "interim": false,
    "session_id": "sess_xyz",
    "ice_servers": [{ "urls": "stun:stun.pyloto.com.br:3478" }],
    "sdp_offer": "<sdp>"
  }
}
```

O APK verifica o campo `interim`:
- `true`: exibe dialog de aviso e botao para abrir WhatsApp (chamada direta no Fase interim)
- `false`: abre tela de chamada com cliente WebRTC

---

## Notificacoes

| Metodo | Endpoint | Descricao |
|---|---|---|
| POST | `/notificacoes/token` | Registrar/atualizar token FCM |
| GET | `/notificacoes` | Listar notificacoes do parceiro |

### POST /notificacoes/token

Deve ser chamado sempre que o app obter um novo token FCM (ex: apos login, apos renovacao).

```json
// Request
{ "token": "<firebase_fcm_token>", "plataforma": "android" }
```

### Eventos FCM recebidos (nao endpoints — chegam via push)

| event_type | Dados extras | Acao recomendada no app |
|---|---|---|
| `novo_pedido` | `pedido_id`, `tipo_servico`, `valor_parceiro`, `endereco_origem` | Som + card na HomeScreen |
| `pedido_cancelado` | `pedido_id`, `motivo` | Alerta + retorno a HomeScreen |
| `nova_mensagem` | `pedido_id`, `conteudo`, `de` | Notificacao + atualiza ChatScreen |
| `chamada_solicitante` | `pedido_id`, `session_id` (Fase 2) | Tela de chamada entrante |

---

## Formato de Resposta Padrao

```json
{
  "success": true,
  "data": { },
  "message": "Operacao realizada com sucesso",
  "errors": null
}
```

## Formato de Erro

```json
{
  "success": false,
  "data": null,
  "message": "Descricao do erro",
  "errors": [
    { "field": "email", "message": "Email ja cadastrado" }
  ]
}
```

## Codigos HTTP relevantes

| Codigo | Significado |
|---|---|
| 200 | OK |
| 201 | Criado |
| 400 | Dados invalidos |
| 401 | Token invalido ou expirado (interceptor renova automaticamente) |
| 403 | Sem permissao (parceiro inativo ou suspenso) |
| 404 | Recurso nao encontrado |
| 409 | Conflito (ex: pedido ja foi aceito por outro parceiro) |
| 422 | Erro de validacao Pydantic |
| 500 | Erro interno |

## Paginacao

```json
{
  "items": [ ],
  "page": 0,
  "size": 20,
  "total": 150,
  "has_next": true
}
```
