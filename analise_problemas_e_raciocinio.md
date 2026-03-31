# Analise de rotas e backlog de backend para o `pyloto_app-parceiro`

Data da analise: `2026-03-31`

## Atualizacao de execucao - backend `pyloto_atende`

Rodada implementada em `2026-03-31`:

- `GET /corridas/meus` implementada no backend.
- `GET /corridas/rota-ativa` implementada no backend.
- As duas rotas agora devolvem snapshot operacional server-driven com `current_phase`, `current_step`, `allowed_actions`, `next_action`, `tracking_required` e `proof_of_delivery_required`.
- `GET /corridas/rota-ativa` faz fallback sintetico a partir de pedidos ativos quando a `rota_ativa` persistida ainda nao estiver marcada como `ativa`.
- Tambem foi corrigida a precedencia de roteamento no backend para impedir que `/{pedido_id}` sequestrasse `/corridas/meus` e `/corridas/rota-ativa`.
- Proxima prioridade de backend mantida: corrigir `GET /corridas/disponiveis` para usar `lat/lng/raio` de verdade.

## Escopo e fontes

- App analisado: `pyloto_app-parceiro`
- Backend app-facing analisado: `pyloto_atende`
- Foco: contrato HTTP entre app parceiro e backend, incluindo lacunas de rota, lacunas de payload e tarefas necessarias para o backend sustentar o fluxo real do entregador
- Fontes principais:
  - `pyloto_app-parceiro/app/src/main/java/com/pyloto/entregador/core/network/ApiService.kt`
  - `pyloto_app-parceiro/app/src/main/java/com/pyloto/entregador/presentation/**`
  - `pyloto_atende/src/http/routes/app/**`
  - `pyloto_atende/src/pedidos/service.py`
  - `pyloto_atende/src/parceiros/service.py`
  - `pyloto_atende/README.md`

## Resumo executivo

O backend atual ja cobre o basico de autenticacao, onboarding, corridas, capacidade, agenda, ganhos, localizacao, chat basico e notificacoes. O problema principal nao e "backend inexistente", e sim "backend ainda incompleto para o fluxo operacional real do parceiro".

As maiores lacunas encontradas sao estas:

1. Falta uma rota para listar corridas aceitas/coletadas/em entrega do parceiro e reidratar a rota ativa apos reinicio do app.
2. `GET /corridas/disponiveis` recebe `lat`, `lng` e `raio`, mas hoje nao usa esses parametros para filtrar nem ordenar as ofertas.
3. O modo mapa continua dependente de `MAPS_API_KEY` embutido no APK e nao existe contrato backend-first para roteirizacao/Google Routes.
4. Falta um fluxo de upload ou URL assinada para comprovante de entrega, assinatura digital, auditoria do veiculo e foto de perfil.
5. `POST /auth/logout` existe, mas ainda nao invalida token nem refresh token.
6. Chat e notificacoes existem apenas em modo basico; faltam leitura, contagem de nao lidas e mecanismo de atualizacao em tempo real.
7. O `README` do `pyloto_atende` ainda promete `GET /corridas/meus`, mas essa rota nao esta exposta no FastAPI atual.

## Evidencias objetivas encontradas

- O contrato atual do app esta centralizado em `ApiService.kt`.
- O app calcula distancia ate a coleta e ordena a lista localmente em `CorridasUiModels.kt`.
- O backend recebe `lat`, `lng` e `raio` em `/corridas/disponiveis`, mas `list_disponiveis()` nao usa esses parametros para filtro ou ranking.
- O `README` do `pyloto_atende` ainda documenta `GET /corridas/meus`.
- A chave `MAPS_API_KEY` esta em `gradle.properties` e entra no manifesto via `com.google.android.geo.API_KEY`.
- `PylotoFirebaseMessagingService.onNewToken()` ainda esta com TODO para enviar o token ao backend.
- `ChatScreen.kt` e `HistoricoScreen.kt` continuam placeholders.
- O backend ja persiste `rota_ativa`, `route_session_id` e `pedido_ids`, mas nao expoe isso em rota app-facing.

## Matriz 1 - rotas que o app ja espera e status no backend

| Rota | App usa hoje | Status no backend | Observacao |
|---|---|---|---|
| `POST /auth/login` | Sim | OK | Contrato alinhado ao app |
| `POST /auth/refresh` | Sim | OK | Contrato alinhado ao app |
| `POST /auth/logout` | Sim | OK com ressalvas | Ainda nao invalida token; ha TODO explicito no backend |
| `GET /corridas/disponiveis` | Sim | OK com ressalvas graves | Backend nao usa `lat/lng/raio` para filtro/ranking e devolve contexto geografico insuficiente |
| `GET /corridas/{id}` | Sim | OK com ressalvas | Falta expor estado operacional server-driven para reduzir logica no app |
| `GET /corridas/{id}/capacidade-check` | Sim | OK | Bom contrato para bloqueio de aceite |
| `POST /corridas/{id}/aceitar` | Sim | OK | Fluxo de aceite implementado |
| `POST /corridas/{id}/recusar` | Sim | OK | Fluxo implementado, com categorias e reputacao |
| `POST /corridas/{id}/iniciar` | Sim | OK | Backend inicia coleta ou entrega conforme status atual |
| `POST /corridas/{id}/coletar` | Sim | OK | Fluxo implementado |
| `POST /corridas/{id}/finalizar` | Sim | OK com ressalvas | So aceita `foto_comprovante_url`; falta pipeline de upload |
| `POST /corridas/{id}/cancelar` | Sim | OK | Fluxo implementado |
| `POST /corridas/{id}/eventos` | Sim | OK com ressalvas | Generico demais; app ainda infere parte da maquina de estados |
| `GET /corridas/historico` | Sim | OK | Backend existe, mas a tela ainda e placeholder |
| `POST /entregador/localizacao` | Sim | OK | Contrato implementado |
| `POST /entregador/localizacao/batch` | Sim | OK | Contrato implementado |
| `GET /entregador/perfil` | Sim | OK | Contrato implementado |
| `PUT /entregador/perfil` | Sim | OK com ressalvas | Falta rota de upload para `foto_url` |
| `GET /entregador/onboarding-status` | Sim | OK com ressalvas | Falta download seguro/proxy para referencias de contrato |
| `GET /entregador/capacidade` | Sim | OK | Bom snapshot agregado, mas sem lista navegavel de pedidos ativos |
| `GET /entregador/agenda` | Sim | OK | Contrato implementado |
| `POST /entregador/agenda` | Sim | OK | Contrato implementado |
| `POST /entregador/agenda/{agendaId}/cancelar` | Sim | OK | Contrato implementado |
| `POST /entregador/status` | Sim | OK | Toggle implementado; auditoria e generica via update de perfil |
| `POST /entregador/contrato/assinatura-digital` | Sim | OK com ressalvas | So recebe referencia; falta upload self-service |
| `POST /entregador/auditoria-veiculo` | Sim | OK com ressalvas | So recebe referencia; falta upload self-service |
| `GET /entregador/ganhos` | Sim | OK | Backend entrega ganhos e extrato |
| `GET /chat/{corridaId}/mensagens` | Sim | OK basico | Sem contrato de tempo real, leitura ou cursor incremental |
| `POST /chat/{corridaId}/mensagens` | Sim | OK basico | Funciona para envio, mas a tela do app ainda nao esta pronta |
| `POST /notificacoes/token` | Sim no contrato | Backend pronto, app nao usa de verdade | `onNewToken()` do app ainda esta pendente |
| `GET /notificacoes` | Sim no contrato | Backend pronto, app nao usa de verdade | Nao ha tela/fluxo implementado no app |

## Matriz 2 - rotas necessarias no backend e hoje ausentes

| Prioridade | Rota sugerida | Motivo |
|---|---|---|
| `P0` | `GET /corridas/meus` ou `GET /corridas/ativas` | Listar corridas do parceiro em `aceito`, `coletando`, `coletado` e `em_entrega`; rota ja prometida no `README`, mas nao exposta |
| `P0` | `GET /corridas/rota-ativa` | Reidratar sessao operacional do parceiro apos reinicio do app, logout/login, reinstalacao parcial ou perda de cache local |
| `P0` | `POST /corridas/roteirizacao` ou `POST /corridas/rota-ativa/recalcular` | Calcular sequencia otima de coleta/entrega para multiplos pedidos, com ETA, distancia total, ordem das paradas e proxima acao |
| `P0` | `POST /corridas/{id}/comprovante/upload-url` | Gerar upload seguro para comprovante de entrega; hoje a UI depende de colar URL manualmente |
| `P0` | `POST /entregador/contrato/assinatura/upload-url` | Permitir onboarding sem colar URL/ref manualmente |
| `P0` | `POST /entregador/auditoria-veiculo/upload-url` | Permitir envio real da foto do veiculo sem depender de referencia externa |
| `P0` | `POST /entregador/foto/upload-url` | Permitir foto de perfil real; hoje `foto_url` depende de URL pronta |
| `P0` | `POST /maps/routes` ou rota de dominio equivalente | Consumir Google Routes API pelo backend para rota, ETA, legs e polyline sem jogar a logica de navegação no app |
| `P1` | `GET /entregador/contrato/download-url` ou `GET /entregador/contrato/download` | Resolver `contrato_download_ref` quando a referencia nao for HTTP publica |
| `P1` | `POST /chat/{corridaId}/read` | Marcar mensagens como lidas no servidor, nao apenas localmente |
| `P1` | `GET /chat/{corridaId}/unread-count` | Badge e contagem de nao lidas server-driven |
| `P1` | `GET /chat/{corridaId}/stream` ou alternativa de sync incremental | Atualizacao em tempo real ou quase real do chat |
| `P1` | `GET /notificacoes/unread-count` | Badge global de notificacoes |
| `P1` | `POST /notificacoes/{id}/read` | Marcar notificacao individual como lida |
| `P1` | `POST /notificacoes/read-all` | Marcar tudo como lido |

## Matriz 3 - rotas existentes, mas com contrato insuficiente para o fluxo real

| Rota | Problema atual | Tarefa de backend |
|---|---|---|
| `GET /corridas/disponiveis` | Recebe `lat/lng/raio`, mas nao usa esses parametros; tambem nao devolve `distancia_ate_coleta`, `eta_ate_coleta` ou ranking server-driven | Fazer filtro geoespacial real, ordenar por distancia/score de despacho e devolver payload pronto para UI |
| `GET /corridas/{id}` | O app ainda infere `currentStep` localmente a partir de `status` | Devolver `current_step`, `current_phase`, `allowed_actions`, `route_session_id`, `tracking_required`, `proof_of_delivery_required` |
| `POST /corridas/{id}/eventos` | Contrato muito generico para um fluxo que depende de etapas conhecidas | Validar `kind` com enum canonico, devolver `next_allowed_actions` e documentar claramente cada evento |
| `POST /corridas/{id}/finalizar` | Backend aceita so URL/ref de comprovante; nao existe upload guiado | Integrar com fluxo de upload seguro e aceitar `upload_id` ou referencia emitida pelo proprio backend |
| `PUT /entregador/perfil` | Foto de perfil so funciona se o app ja tiver uma URL valida | Acrescentar fluxo oficial de upload e validacao de tipo/tamanho |
| `GET /entregador/onboarding-status` | Pode devolver `contrato_download_ref` nao navegavel pelo app, como `gs://...` | Devolver URL assinada, proxy HTTP ou acao explicita de download |
| `POST /auth/logout` | Nao ha revogacao real; backend so responde sucesso | Implementar invalidacao de access/refresh token com Redis ou registry equivalente |
| `POST /notificacoes/token` | So recebe o token cru | Passar a receber `device_id`, `platform`, `app_version`, `push_enabled` e suportar multiplos dispositivos |
| `GET /corridas/historico` | Contrato serve para pagina basica, mas nao para tela com filtros/periodos | Adicionar filtros por data, status, tipo e resumos agregados |

## Rotas que o backend ja possui, mas o app ainda nao aproveita bem

Estas nao entram como "falta no backend", mas ajudam a separar o que e backlog do app:

- `GET /corridas/historico`: backend existe, mas `HistoricoScreen.kt` ainda esta placeholder.
- `GET/POST /chat/{corridaId}/mensagens`: backend existe, `ChatRepository` existe, mas `ChatScreen.kt` ainda esta placeholder.
- `POST /notificacoes/token`: backend existe, mas `PylotoFirebaseMessagingService.onNewToken()` ainda nao chama a API.
- `GET /notificacoes`: backend existe, mas nao ha repository/screen usando essa rota.
- `POST /corridas/{id}/localizacao`: backend existe, mas o app atualmente concentra tracking em `/entregador/localizacao` com `pedido_id`.

## Tarefas priorizadas para o backend

### `P0` - obrigatorio para operacao real

1. Implementar `GET /corridas/meus` ou `GET /corridas/ativas`.
   - Deve devolver corridas em progresso do parceiro.
   - Deve incluir `route_session_id`, `fase`, `pedido_principal_id`, `pedido_ids`, `route_started_at`, `next_action` e deadlines.
   - Deve ser a rota usada para reidratar sessao apos restart do app.

2. Corrigir de verdade `GET /corridas/disponiveis`.
   - Usar `lat`, `lng` e `raio`.
   - Ordenar por distancia ate a coleta e regras de despacho.
   - Devolver `distancia_ate_coleta_m`, `eta_ate_coleta_min`, `distancia_total_m`, `tempo_total_min` e score/rank final.

3. Implementar roteirizacao server-side.
   - Criar rota de calculo de rota otima para multiplas corridas.
   - Responder com sequencia de paradas, legs, ETA, distancia total, polyline e justificativa da ordem.

4. Criar pipeline oficial de upload.
   - Comprovante de entrega.
   - Assinatura digital do contrato.
   - Foto do veiculo.
   - Foto de perfil.
   - Opcao preferivel: URLs assinadas ou `upload_id` emitido pelo backend.

5. Resolver o problema de mapa/Google no desenho de backend.
   - Criar rota backend-first para Google Routes API.
   - Se a exigencia for "nenhuma credencial Google no APK", o desenho atual com Google Maps SDK precisa ser substituido, nao apenas escondido.

6. Implementar logout com revogacao.
   - Bloquear refresh token reutilizado.
   - Invalidar access token conforme politica definida.
   - Registrar logout por dispositivo/sessao.

### `P1` - necessario para maturidade operacional

7. Expor o estado operacional da corrida no backend.
   - `current_step`
   - `allowed_actions`
   - `tracking_required`
   - `proof_of_delivery_required`
   - `can_return_home`

8. Fechar o contrato de chat.
   - Marcar como lida.
   - Contar nao lidas.
   - Sync incremental ou stream.

9. Fechar o contrato de notificacoes.
   - Contagem de nao lidas.
   - Marcar individualmente.
   - Marcar todas.

10. Resolver download de contrato no onboarding.
   - Nao depender de `gs://...` ou referencia interna nao navegavel.

11. Enriquecer `POST /notificacoes/token`.
   - Receber contexto do dispositivo e tratar troca/rotacao de tokens.

### `P2` - qualidade de plataforma e manutencao

12. Alinhar documentacao do `pyloto_atende`.
   - Implementar `GET /corridas/meus` de fato, ou remover a promessa do `README`.
   - Revisar tabelas de rotas para refletir o contrato real do app.

13. Atualizar testes de integracao do backend para o contrato atual.
   - Os testes de `test_app_routes.py` estao desatualizados em relacao ao app real.
   - O alvo deve ser o contrato de `ApiService.kt`.

14. Melhorar `GET /corridas/historico`.
   - Filtros por periodo/status.
   - Sumarios agregados para a tela.

## Decisao tecnica importante sobre o Google Maps

O problema do "token exposto no app" precisa ser tratado com clareza tecnica:

- Se o app continuar usando o Google Maps SDK nativo para Android, sempre havera uma API key no APK.
- O que pode e deve sair do app e a chamada para APIs server-side do Google, como Routes, Directions, Geocoding e Places.
- Se o requisito for realmente "nenhuma credencial Google no app", entao a arquitetura precisa mudar:
  - remover o uso direto do Google Maps SDK no app; ou
  - trocar a estrategia de renderizacao para outra base de mapa/tiles.

Em resumo: esconder a key sem mudar o desenho nao resolve. O backend precisa assumir as chamadas de roteirizacao e o produto precisa decidir se aceita uma key restrita no app ou se vai para um modelo 100% backend-first.

## Conclusao objetiva

O ecossistema ja tem uma base boa para operar o app parceiro, mas ainda faltam contratos centrais para transformar o backend em fonte unica de verdade da operacao.

O maior gap hoje nao e "aceitar corrida" ou "transicionar status". Isso ja existe. O maior gap e o que vem depois:

- recuperar corridas ja aceitas;
- operar multi-entrega;
- reidratar rota ativa;
- suportar mapa e roteirizacao sem logica critica no app;
- fechar upload e seguranca de arquivos;
- tratar logout, notificacoes e chat como produto operacional, nao apenas como stub.

Se eu tivesse que priorizar so o backend em uma ordem curta, faria assim:

1. `GET /corridas/meus` + `GET /corridas/rota-ativa`
2. corrigir `GET /corridas/disponiveis` para usar geo de verdade
3. roteirizacao server-side
4. upload/presign para comprovantes e onboarding
5. logout com revogacao
