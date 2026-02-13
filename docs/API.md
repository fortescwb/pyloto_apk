# Contratos de API - Pyloto Entregador

## Base URLs
- **Staging:** `https://staging-api.pyloto.com.br/v1/`
- **Production:** `https://api.pyloto.com.br/v1/`

## Autenticação
Todas as requisições (exceto login/register/refresh) requerem header:
```
Authorization: Bearer <access_token>
```

### Refresh Token
Quando o access token expira (HTTP 401), o interceptor tenta automaticamente:
```
POST /auth/refresh
Body: { "refreshToken": "<refresh_token>" }
```

---

## Endpoints

### Auth
| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | `/auth/login` | Login com email/senha |
| POST | `/auth/register` | Cadastro de entregador |
| POST | `/auth/refresh` | Renovar token |
| POST | `/auth/logout` | Logout |

### Corridas
| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/corridas/disponiveis?lat=&lng=&raio=` | Corridas próximas |
| GET | `/corridas/{id}` | Detalhes de uma corrida |
| POST | `/corridas/{id}/aceitar` | Aceitar corrida |
| POST | `/corridas/{id}/iniciar` | Iniciar corrida |
| POST | `/corridas/{id}/coletar` | Marcar coleta |
| POST | `/corridas/{id}/finalizar` | Finalizar entrega |
| POST | `/corridas/{id}/cancelar` | Cancelar corrida |
| GET | `/corridas/historico?page=&size=` | Histórico paginado |

### Entregador
| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/entregador/perfil` | Perfil do entregador |
| PUT | `/entregador/perfil` | Atualizar perfil |
| POST | `/entregador/localizacao` | Atualizar GPS |
| POST | `/entregador/localizacao/batch` | Batch de localizações |
| POST | `/entregador/status` | Online/Offline |
| GET | `/entregador/ganhos?periodo=&dataInicio=&dataFim=` | Ganhos |

### Chat
| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/chat/{corridaId}/mensagens?page=` | Mensagens paginadas |
| POST | `/chat/{corridaId}/mensagem` | Enviar mensagem |

### Notificações
| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | `/notificacoes/token` | Registrar token FCM |
| GET | `/notificacoes?page=&size=` | Listar notificações |

---

## Formato de Resposta Padrão
```json
{
  "success": true,
  "data": { ... },
  "message": "Operação realizada com sucesso",
  "errors": null
}
```

## Formato de Erro
```json
{
  "success": false,
  "data": null,
  "message": "Erro de validação",
  "errors": [
    { "field": "email", "message": "Email inválido" }
  ]
}
```

## Paginação
```json
{
  "content": [...],
  "page": 0,
  "size": 20,
  "totalElements": 150,
  "totalPages": 8,
  "last": false
}
```
