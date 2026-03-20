# Homologacao - App Parceiro x Backend Real

## Contexto

- App: `pyloto_app-parceiro`
- Backend: `pyloto_atende`
- Ambiente: `production` (staging comentado temporariamente)

## Pre-condicoes

- Build atualizada instalada no dispositivo
- Conta de parceiro valida para login
- Conexao de internet ativa
- Localizacao do dispositivo habilitada
- Projeto GCP correto selecionado para auditoria operacional

## Checklist funcional

1. Login real com parceiro existente
2. Cadastro real de parceiro novo
3. Consulta de perfil (`/entregador/perfil`)
4. Atualizacao de status online/offline (`/entregador/status`)
5. Envio de localizacao unitario (`/entregador/localizacao`)
6. Envio de localizacao em lote (`/entregador/localizacao/batch`)
7. Listagem de corridas disponiveis (`/corridas/disponiveis`)
8. Aceite de corrida (`/corridas/{id}/aceitar`)
9. Transicao de status de corrida (`iniciar`, `coletar`, `finalizar`)
10. Consulta de historico (`/corridas/historico`)
11. Chat de corrida (`GET/POST /chat/{corridaId}/mensagens`)
12. Registro de token FCM (`/notificacoes/token`)
13. Consulta de notificacoes (`/notificacoes`)
14. Logout e novo login

## Checklist de observabilidade

1. Em debug, logs HTTP detalhados habilitados
2. `X-Trace-Id` presente nas requisicoes
3. Nenhum token de autorizacao exposto em logs
4. Falhas de rede 4xx/5xx registradas com metodo, path, status e tempo
5. Falhas com fallback de cache registradas na camada de repository
6. Erros de parsing registrados com identificador de operacao

## Resultado esperado para aprovacao

- 100% dos itens funcionais executados sem bloqueio critico
- Sem crash em fluxos criticos
- Sem inconsistencias de contrato entre app e backend
- Logs suficientes para rastrear falhas sem exposicao de dados sensiveis

