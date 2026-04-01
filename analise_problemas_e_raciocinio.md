# Analise de rotas e backlog de backend para o `pyloto_app-parceiro`

Data da analise: `2026-04-01`

## Atualizacao de execucao - backend `pyloto_atende` - `2026-04-01` - rodada de leitura e nao lidas do chat

As duas proximas rotas mais logicas atacadas nesta rodada foram:

- `POST /chat/{corridaId}/read`
  - implementada para marcar como lidas as mensagens inbound do contato associado a corrida;
  - persiste `partner_read_at`, `partner_read_by` e `partner_read_source`, sem confundir leitura do app com receipt da Meta;
  - devolve `read_count`, `read_at`, `unread_count` residual e `has_unread`.

- `GET /chat/{corridaId}/unread-count`
  - implementada para contar quantas mensagens inbound daquele contato ainda nao foram abertas pelo parceiro;
  - devolve `corrida_id`, `count` e `has_unread`;
  - fecha o minimo decente de badge server-driven para o corredor do chat.

## Atualizacao de execucao - backend `pyloto_atende` - `2026-04-01` - rodada de download seguro e recalc da rota ativa

As duas proximas rotas mais logicas atacadas nesta rodada foram:

- `GET /entregador/contrato/download-url`
  - implementada para resolver `contrato_download_ref` sem despejar `gs://...` cru no colo do app;
  - preserva referencias HTTP publicas quando elas ja existem;
  - converte referencias privadas do GCS em URL assinada temporaria de download;
  - devolve `download_url`, `reference`, `expires_at`, `filename`, `contract_version` e `source`.

- `POST /corridas/rota-ativa/recalcular`
  - implementada para gerar uma preview server-driven da ordem de coleta e entrega da rota ativa;
  - aceita `lat` e `lng` opcionais para o app forcar uma origem mais atual;
  - devolve `source = recalculated_preview`, `origin`, `stops`, `pedido_ids`, `pedido_principal_id`, `total_distance_m`, `total_duration_min` e `recalculated_at`;
  - reidrata tambem os `pedidos` ja ordenados, para o app parar de remendar a fila na unha.

## Atualizacao de execucao - backend `pyloto_atende` - `2026-04-01` - rodada de uploads de auditoria e foto

As duas proximas rotas mais logicas atacadas nesta rodada foram:

- `POST /entregador/auditoria-veiculo/upload-url`
  - implementada para gerar URL assinada da foto exigida em incidente de auditoria do veiculo;
  - valida `incident_id`, tipo, tamanho e se o incidente realmente exige essa evidencia;
  - persiste a intencao em `parceiro_uploads` com `kind = vehicle_audit_photo`.

- `POST /entregador/foto/upload-url`
  - implementada para gerar URL assinada da foto de perfil do parceiro;
  - restringe o upload a imagem e persiste a intencao em `parceiro_uploads` com `kind = profile_photo`;
  - fecha a lacuna de backend que obrigava o app a depender de URL pronta para `foto_url`.

## Atualizacao de execucao - backend `pyloto_atende` - `2026-04-01` - rodada de eventos e assinatura

As duas proximas rotas mais logicas atacadas nesta rodada foram:

- `POST /corridas/{id}/eventos`
  - corrigida para validar `kind` em contrato canonico, em vez de aceitar qualquer ritual mal desenhado;
  - agora devolve `meta.operational_event` com `phase`, `suggested_next_action`, `next_allowed_actions`, `current_phase` e `current_step`;
  - a resposta tambem reidrata o detalhe operacional server-driven da corrida;
  - pedidos de outro parceiro deixam de atravessar o corredor e passam a cair em `404`.

- `POST /entregador/contrato/assinatura/upload-url`
  - implementada para gerar URL assinada de upload da assinatura do contrato;
  - valida tipo, tamanho e existencia da evidencia base do contrato antes de liberar o envio;
  - devolve `upload_id`, `upload_url`, `http_method`, `headers`, `content_type`, `max_size_bytes`, `expires_at` e `reference`;
  - persiste a intencao de upload em `parceiro_uploads`, com expiracao e referencia final controladas pelo backend.

## Atualizacao de execucao - backend `pyloto_atende` - `2026-04-01` - rodada de comprovantes

As duas proximas rotas mais logicas atacadas nesta rodada foram:

- `POST /corridas/{id}/comprovante/upload-url`
  - implementada para gerar URL assinada de upload de comprovante de entrega;
  - devolve `upload_id`, `upload_url`, `http_method`, `headers`, `content_type`, `max_size_bytes`, `expires_at` e `reference`;
  - o backend passou a persistir a intencao de upload em `pedido_uploads`, com expiracao e consumo controlado.

- `POST /corridas/{id}/finalizar`
  - corrigida para aceitar `upload_id` alem de `foto_comprovante_url`;
  - agora exige comprovante de entrega de forma explicita, em vez de deixar a corrida fechar sem prova;
  - o upload so e consumido depois que o pedido realmente cai como `finalizado`, evitando um cemiterio de referencias gastas por engano.

## Atualizacao de execucao - backend `pyloto_atende` - `2026-04-01`

As duas proximas rotas mais urgentes do app parceiro foram atacadas nesta rodada:

- `GET /corridas/disponiveis`
  - agora usa `lat`, `lng` e `raio` de verdade;
  - remove ofertas fora do raio quando ha coordenada de origem;
  - devolve `distancia_ate_coleta_m`, `distancia_ate_coleta_km`, `eta_ate_coleta_min`, `distancia_total_m`, `tempo_total_min`, `rank_dispatch` e `meta.geo_context`;
  - pedidos sem coordenada continuam aparecendo como fallback, mas no fim da fila.

- `GET /corridas/{id}`
  - agora devolve estado operacional server-driven com `current_phase`, `current_step`, `allowed_actions`, `next_action`, `tracking_required`, `proof_of_delivery_required`, `route_session_id`, `route_tracking_active` e `route_status`;
  - detalhe de corrida de outro parceiro deixa de vazar e passa a cair em `404`.

Tambem houve uma correcao estrutural acoplada a isso:

- os controles operacionais canonicos da corrida sairam de `src/corridas_ativas/controls.py` para `src/pedidos/orquestracao/estado/controles_operacionais.py`;
- isso matou o risco de import circular ao reaproveitar o mesmo estado operacional no detalhe da corrida.

## Atualizacao de execucao - backend `pyloto_atende`

Rodada implementada em `2026-03-31`:

- `GET /corridas/meus` implementada no backend.
- `GET /corridas/rota-ativa` implementada no backend.
- As duas rotas agora devolvem snapshot operacional server-driven com `current_phase`, `current_step`, `allowed_actions`, `next_action`, `tracking_required` e `proof_of_delivery_required`.
- `GET /corridas/rota-ativa` faz fallback sintetico a partir de pedidos ativos quando a `rota_ativa` persistida ainda nao estiver marcada como `ativa`.
- Tambem foi corrigida a precedencia de roteamento no backend para impedir que `/{pedido_id}` sequestrasse `/corridas/meus` e `/corridas/rota-ativa`.
- A refatoracao estrutural de `src/parceiros/service.py` foi iniciada no backend.
  - A logica operacional do entregador passou a ser extraida para `src/parceiros/operacao/`.
  - Isso atinge especialmente tracking, rota ativa, elegibilidade operacional, documentos e incidentes.
- A rodada atual aprofundou a refatoracao de `src/parceiros/service.py`.
  - A agenda operacional do parceiro foi extraida para `src/parceiros/agenda/`.
  - Os modulos novos de agenda ficaram todos abaixo de 150 linhas.
- A rodada seguinte continuou a refatoracao estrutural sem alterar contrato HTTP do app.
  - O nucleo financeiro do parceiro foi extraido para `src/parceiros/financeiro/`.
  - Isso isola ganhos, repasses, mensalidades, suspensao financeira e extrato.
- A rodada mais recente continuou a limpeza estrutural sem mudar contrato HTTP do app.
  - O eixo temporal saiu de `src/parceiros/service.py` para `src/parceiros/base/tempo.py`.
  - O financeiro ganhou `src/parceiros/financeiro/estado/`, `src/parceiros/financeiro/consulta/` e `src/parceiros/financeiro/base/plano.py`.
  - Resumo, extrato, periodo de ganhos e sincronizacao financeira do parceiro deixaram de viver espremidos dentro do `service.py`.
- A rodada atual seguiu sem alterar contrato HTTP do app.
  - A agenda operacional ganhou `src/parceiros/agenda/base/` e `src/parceiros/agenda/contexto/`.
  - Janela D+1/D+2, no-show, liberacao remanescente, resposta de agenda e refresh de rollover sairam de `src/parceiros/service.py`.
  - O backend preservou os mesmos fluxos do app e dos testes de agenda enquanto derrubava esse corredor da masmorra.
- A rodada atual continuou sem alterar contrato HTTP do app.
  - A conformidade operacional ganhou `src/parceiros/conformidade/base/` e `src/parceiros/conformidade/contexto/`.
  - Politicas de incidente, thresholds de reputacao, penalidades operacionais e sincronizacao de compliance sairam de `src/parceiros/service.py`.
  - Os fluxos do app, do despacho e do onboarding administrativo seguiram preservados durante a extracao.
- `GET /corridas/disponiveis` foi corrigida no backend.
  - A listagem agora aplica `dispatch_check` por item.
  - Corridas ainda bloqueadas para parceiros nao agendados deixam de aparecer antes da liberacao remanescente.
  - Cada item listado agora embute `dispatch_access`.
- `GET /corridas/{id}/capacidade-check` tambem foi ajustada.
  - Quando o impedimento real e de bucket de distribuicao, a razao retornada prioriza o bloqueio de despacho.
- Proxima prioridade funcional de backend para essa frente: completar `GET /corridas/disponiveis` com filtro geoespacial real usando `lat/lng/raio`.

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

1. O modo mapa continua dependente de `MAPS_API_KEY` embutido no APK e ainda falta uma rota backend-first para Google Routes/legs/polylines reais.
2. O pipeline oficial de upload e download privado ja existe no backend, mas o app ainda nao consome de ponta a ponta os portais novos de onboarding, perfil e comprovantes.
3. `POST /auth/logout` existe, mas ainda nao invalida token nem refresh token.
4. Chat e notificacoes ainda nao fecharam o ciclo operacional; o chat agora ganhou leitura e nao lidas, mas ainda falta sync incremental/tempo real, e notificacoes seguem sem badge e marcacao de leitura.
5. A roteirizacao server-driven ja ganhou uma preview heuristica, mas ainda falta a camada viaria real para o mapa deixar de improvisar.

## Evidencias objetivas encontradas

- O contrato atual do app esta centralizado em `ApiService.kt`.
- O app calcula distancia ate a coleta e ordena a lista localmente em `CorridasUiModels.kt`.
- O backend agora usa `lat`, `lng` e `raio` em `/corridas/disponiveis` e devolve ranking/metricas geograficas server-driven.
- O backend agora expoe `GET /corridas/meus` e `GET /corridas/rota-ativa`.
- O backend agora expoe `POST /corridas/{id}/comprovante/upload-url` e `POST /corridas/{id}/finalizar` aceita `upload_id`.
- O backend agora expoe `POST /corridas/{id}/eventos` com contrato canonico e `meta.operational_event`.
- O backend agora expoe `POST /chat/{corridaId}/read`.
- O backend agora expoe `GET /chat/{corridaId}/unread-count`.
- O backend agora expoe `POST /entregador/contrato/assinatura/upload-url` para onboarding self-service.
- O backend agora expoe `GET /entregador/contrato/download-url` para resolver contrato privado com URL navegavel.
- O backend agora expoe `POST /entregador/auditoria-veiculo/upload-url`.
- O backend agora expoe `POST /entregador/foto/upload-url`.
- O backend agora expoe `POST /corridas/rota-ativa/recalcular` para preview server-driven da ordem de paradas.
- A chave `MAPS_API_KEY` esta em `gradle.properties` e entra no manifesto via `com.google.android.geo.API_KEY`.
- `PylotoFirebaseMessagingService.onNewToken()` ainda esta com TODO para enviar o token ao backend.
- `ChatScreen.kt` e `HistoricoScreen.kt` continuam placeholders.
- O backend ja persiste `rota_ativa`, `route_session_id` e `pedido_ids` e agora expoe snapshot app-facing para reidratacao.

## Matriz 1 - rotas que o app ja espera e status no backend

| Rota | App usa hoje | Status no backend | Observacao |
|---|---|---|---|
| `POST /auth/login` | Sim | OK | Contrato alinhado ao app |
| `POST /auth/refresh` | Sim | OK | Contrato alinhado ao app |
| `POST /auth/logout` | Sim | OK com ressalvas | Ainda nao invalida token; ha TODO explicito no backend |
| `GET /corridas/disponiveis` | Sim | OK com ressalvas | Agora usa `lat/lng/raio`, devolve distancia e rank server-driven; ainda falta ETA viaria real e score mais sofisticado |
| `GET /corridas/{id}` | Sim | OK com ressalvas leves | Agora expoe estado operacional server-driven; ainda faltam refinamentos como `can_return_home` e eventual `last_location` |
| `GET /corridas/{id}/capacidade-check` | Sim | OK | Agora prioriza a razao de bloqueio de despacho quando a restricao real nao e capacidade fisica |
| `POST /corridas/{id}/aceitar` | Sim | OK | Fluxo de aceite implementado |
| `POST /corridas/{id}/recusar` | Sim | OK | Fluxo implementado, com categorias e reputacao |
| `POST /corridas/{id}/iniciar` | Sim | OK | Backend inicia coleta ou entrega conforme status atual |
| `POST /corridas/{id}/coletar` | Sim | OK | Fluxo implementado |
| `POST /corridas/{id}/finalizar` | Sim | OK | Agora aceita `upload_id` ou `foto_comprovante_url` e exige comprovante de entrega |
| `POST /corridas/{id}/cancelar` | Sim | OK | Fluxo implementado |
| `POST /corridas/{id}/eventos` | Sim | OK | Agora valida `kind` em contrato canonico e devolve `meta.operational_event` com fase e proxima acao |
| `GET /corridas/historico` | Sim | OK | Backend existe, mas a tela ainda e placeholder |
| `POST /entregador/localizacao` | Sim | OK | Contrato implementado |
| `POST /entregador/localizacao/batch` | Sim | OK | Contrato implementado |
| `GET /entregador/perfil` | Sim | OK | Contrato implementado |
| `PUT /entregador/perfil` | Sim | OK com ressalvas leves | Agora ja pode ser combinado com `POST /entregador/foto/upload-url`; falta o app consumir o pipeline novo |
| `GET /entregador/onboarding-status` | Sim | OK com ressalvas leves | Agora pode ser combinado com `GET /entregador/contrato/download-url`; falta o app consumir o fluxo |
| `GET /entregador/capacidade` | Sim | OK | Bom snapshot agregado, mas sem lista navegavel de pedidos ativos |
| `GET /entregador/agenda` | Sim | OK | Contrato implementado |
| `POST /entregador/agenda` | Sim | OK | Contrato implementado |
| `POST /entregador/agenda/{agendaId}/cancelar` | Sim | OK | Contrato implementado |
| `POST /entregador/status` | Sim | OK | Toggle implementado; auditoria e generica via update de perfil |
| `POST /entregador/contrato/assinatura-digital` | Sim | OK com ressalvas leves | Agora ja pode ser combinado com `upload-url`; falta o app consumir o pipeline completo |
| `POST /entregador/auditoria-veiculo` | Sim | OK com ressalvas leves | Agora ja pode ser combinado com `POST /entregador/auditoria-veiculo/upload-url`; falta o app consumir o fluxo novo |
| `GET /entregador/ganhos` | Sim | OK | Backend entrega ganhos e extrato |
| `GET /chat/{corridaId}/mensagens` | Sim | OK com ressalvas | Backend agora cobre leitura e nao lidas em rotas dedicadas; ainda falta sync incremental ou stream |
| `POST /chat/{corridaId}/mensagens` | Sim | OK basico | Funciona para envio, mas a tela do app ainda nao esta pronta |
| `POST /notificacoes/token` | Sim no contrato | Backend pronto, app nao usa de verdade | `onNewToken()` do app ainda esta pendente |
| `GET /notificacoes` | Sim no contrato | Backend pronto, app nao usa de verdade | Nao ha tela/fluxo implementado no app |

## Matriz 2 - rotas necessarias no backend e hoje ausentes

| Prioridade | Rota sugerida | Motivo |
|---|---|---|
| `P0` | `POST /maps/routes` ou rota de dominio equivalente | Consumir Google Routes API pelo backend para rota, ETA, legs e polyline sem jogar a logica de navegação no app |
| `P1` | `POST /corridas/roteirizacao` | Evoluir a preview heuristica da rota ativa para uma roteirizacao generica e reutilizavel, com suporte a cenarios fora da corrida ativa |
| `P1` | `GET /chat/{corridaId}/stream` ou alternativa de sync incremental | Atualizacao em tempo real ou quase real do chat |
| `P1` | `GET /notificacoes/unread-count` | Badge global de notificacoes |
| `P1` | `POST /notificacoes/{id}/read` | Marcar notificacao individual como lida |
| `P1` | `POST /notificacoes/read-all` | Marcar tudo como lido |

## Matriz 3 - rotas existentes, mas com contrato insuficiente para o fluxo real

| Rota | Problema atual | Tarefa de backend |
|---|---|---|
| `GET /corridas/disponiveis` | Ja usa geo real e devolve ranking, mas a ETA ate coleta ainda e heuristica e a distancia total depende da estimativa persistida | Integrar depois com roteirizacao server-side/Google Routes para metricas viarias reais |
| `GET /corridas/{id}` | Ja devolve estado operacional server-driven, mas ainda nao traz `can_return_home`, `last_location` ou um snapshot mais rico de rota | Evoluir o payload para o modo corrida ativa e reduzir mais ainda a inferencia local do app |
| `POST /corridas/{id}/eventos` | O contrato agora esta canonizado para os eventos operacionais do app, mas ainda pode ganhar novos eventos sem quebrar a trilha atual | Expandir o enum com cuidado e manter a documentacao da maquina operacional alinhada |
| `POST /corridas/{id}/finalizar` | O backend ja aceita `upload_id`, mas o app ainda nao consome o fluxo novo | Ajustar o app para usar `comprovante/upload-url`, enviar o binario e finalizar com `upload_id` |
| `PUT /entregador/perfil` | O backend agora ja tem `upload-url`, mas o app ainda nao encadeia o envio da imagem com a atualizacao do perfil | Ajustar o app para usar `POST /entregador/foto/upload-url` e persistir a referencia retornada |
| `GET /entregador/onboarding-status` | O backend ja cobre o download seguro do contrato, mas o app ainda nao encadeia esse portal no fluxo de onboarding | Ajustar o app para usar `GET /entregador/contrato/download-url` quando houver contrato privado |
| `POST /auth/logout` | Nao ha revogacao real; backend so responde sucesso | Implementar invalidacao de access/refresh token com Redis ou registry equivalente |
| `POST /notificacoes/token` | So recebe o token cru | Passar a receber `device_id`, `platform`, `app_version`, `push_enabled` e suportar multiplos dispositivos |
| `GET /corridas/historico` | Contrato serve para pagina basica, mas nao para tela com filtros/periodos | Adicionar filtros por data, status, tipo e resumos agregados |
| `GET/POST /chat/{corridaId}` | O backend agora cobre leitura e contagem de nao lidas, mas o chat ainda nao tem sync incremental nem stream | Evoluir para cursor incremental, stream ou pull curto sem duplicar logica no app |

## Rotas que o backend ja possui, mas o app ainda nao aproveita bem

Estas nao entram como "falta no backend", mas ajudam a separar o que e backlog do app:

- `GET /corridas/historico`: backend existe, mas `HistoricoScreen.kt` ainda esta placeholder.
- `GET/POST /chat/{corridaId}/mensagens`: backend existe, `ChatRepository` existe, mas `ChatScreen.kt` ainda esta placeholder.
- `POST /chat/{corridaId}/read`: backend existe, mas o app ainda precisa marcar o chat como lido ao abrir/concluir a conversa.
- `GET /chat/{corridaId}/unread-count`: backend existe, mas o app ainda precisa usar a contagem em badge ou lista.
- `POST /notificacoes/token`: backend existe, mas `PylotoFirebaseMessagingService.onNewToken()` ainda nao chama a API.
- `GET /notificacoes`: backend existe, mas nao ha repository/screen usando essa rota.
- `POST /corridas/{id}/localizacao`: backend existe, mas o app atualmente concentra tracking em `/entregador/localizacao` com `pedido_id`.
- `POST /corridas/{id}/comprovante/upload-url`: backend existe, mas o app ainda finaliza corrida sem usar pipeline oficial de upload.
- `POST /entregador/contrato/assinatura/upload-url`: backend existe, mas o app ainda precisa enviar o binario e fechar o onboarding pelo fluxo novo.
- `GET /entregador/contrato/download-url`: backend existe, mas o app ainda precisa parar de depender de `contrato_download_ref` cru.
- `POST /entregador/auditoria-veiculo/upload-url`: backend existe, mas o app ainda precisa enviar a imagem e depois registrar a evidencia do incidente.
- `POST /entregador/foto/upload-url`: backend existe, mas o app ainda precisa usar a referencia retornada na atualizacao de perfil.
- `POST /corridas/rota-ativa/recalcular`: backend existe, mas o app ainda precisa decidir quando pedir recalc server-driven em vez de ordenar localmente no improviso.

## Tarefas priorizadas para o backend

### `P0` - obrigatorio para operacao real

1. Evoluir a roteirizacao server-side.
   - A preview heuristica ja existe em `POST /corridas/rota-ativa/recalcular`.
   - Falta criar a camada viaria real em `POST /maps/routes` e, se necessario, uma rota generica de roteirizacao para multiplos cenarios.
   - O alvo agora e responder com sequencia de paradas, legs, ETA, distancia total, polyline e justificativa da ordem.

2. Fechar o consumo do pipeline oficial de upload.
   - O backend ja esta pronto para:
     - assinatura digital do contrato;
     - foto do veiculo;
     - foto de perfil;
     - comprovante de entrega.
   - Falta o app consumir os `upload-url` novos e registrar as referencias finais nas rotas ja existentes.

3. Resolver o problema de mapa/Google no desenho de backend.
   - Criar rota backend-first para Google Routes API.
   - Se a exigencia for "nenhuma credencial Google no APK", o desenho atual com Google Maps SDK precisa ser substituido, nao apenas escondido.

4. Implementar logout com revogacao.
   - Bloquear refresh token reutilizado.
   - Invalidar access token conforme politica definida.
   - Registrar logout por dispositivo/sessao.

### `P1` - necessario para maturidade operacional

5. Evoluir o detalhe operacional da corrida.
   - `can_return_home`
   - `last_location`
   - `route_snapshot` parcial para a tela ativa

6. Fechar o contrato de chat.
   - O backend ja cobre marcar como lida e contar nao lidas.
   - Falta sync incremental ou stream.
   - Falta o app efetivamente consumir as rotas novas.

7. Fechar o contrato de notificacoes.
   - Contagem de nao lidas.
   - Marcar individualmente.
   - Marcar todas.

8. Fechar o consumo do download seguro de contrato.
   - O backend ja resolve `gs://...` com `GET /entregador/contrato/download-url`.
   - Falta o app usar esse portal no onboarding e parar de depender de referencia crua.

9. Enriquecer `POST /notificacoes/token`.
   - Receber contexto do dispositivo e tratar troca/rotacao de tokens.

### `P2` - qualidade de plataforma e manutencao

12. Alinhar documentacao do `pyloto_atende`.
   - Revisar tabelas de rotas para refletir o contrato real do app.
   - Documentar explicitamente o fluxo novo de `upload_id` para comprovante de entrega.

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

- operar multi-entrega;
- suportar mapa e roteirizacao sem logica critica no app;
- fechar upload e seguranca de arquivos;
- tratar logout, notificacoes e chat como produto operacional, nao apenas como stub.

Se eu tivesse que priorizar so o backend em uma ordem curta, faria assim:

1. roteirizacao server-side
2. consumo do pipeline de upload e download seguro de ativos privados
3. logout com revogacao
4. download seguro de contrato e ativos privados
5. chat/notificacoes com leitura e nao lidas

## Atualizacao complementar 18 - `2026-04-01`

O backend `pyloto_atende` fechou duas lacunas que ainda obrigavam o app parceiro a operar no improviso.

- `GET /corridas/disponiveis`
  - ganhou filtro geoespacial real por `lat/lng/raio`;
  - passou a devolver `distancia_ate_coleta_m`, `distancia_ate_coleta_km`, `eta_ate_coleta_min`, `distancia_total_m`, `tempo_total_min` e `rank_dispatch`;
  - passou a expor `meta.geo_context` para o app saber com qual raio a vitrine foi montada.

- `GET /corridas/{id}`
  - agora devolve `current_phase`, `current_step`, `allowed_actions`, `next_action`, `tracking_required`, `proof_of_delivery_required`, `route_session_id`, `route_tracking_active` e `route_status`;
  - tambem fecha o vazamento de detalhe de corrida de outro parceiro, respondendo `404`.

- Ajuste estrutural que sustentou a rodada:
  - `src/pedidos/orquestracao/estado/controles_operacionais.py` virou a fonte canonica do estado operacional da corrida;
  - `src/corridas_ativas/controls.py` ficou so como adapter de compatibilidade.

## Atualizacao complementar 19 - `2026-04-01`

O backend `pyloto_atende` fechou mais duas lacunas que ainda deixavam o onboarding privado e a multi-entrega presos em remendo local.

- `GET /entregador/contrato/download-url`
  - agora resolve contrato base privado com URL assinada de download quando a referencia vier de `gs://...`;
  - se a referencia ja for HTTP publica, preserva o link sem teatro desnecessario;
  - devolve tambem `filename`, `contract_version`, `expires_at` e `source`.

- `POST /corridas/rota-ativa/recalcular`
  - agora gera preview heuristica server-driven da ordem das paradas da rota ativa;
  - aceita `lat` e `lng` para o app informar a origem mais recente;
  - devolve `origin`, `stops`, `pedido_ids`, `pedido_principal_id`, `total_distance_m`, `total_duration_min` e `recalculated_at`.

- Ajuste estrutural que sustentou a rodada:
  - `src/corridas_ativas/snapshot.py` passou a concentrar a montagem do snapshot persistido;
  - `src/corridas_ativas/service.py` voltou a ser so fachada curta;
  - `src/parceiros/onboarding/autosservico/downloads/` virou o corredor proprio de downloads privados.

## Atualizacao complementar 20 - `2026-04-01`

O backend `pyloto_atende` fechou o pacote minimo de leitura server-driven do chat do parceiro.

- `POST /chat/{corridaId}/read`
  - agora marca como lidas as mensagens inbound do contato, persistindo `partner_read_at`, `partner_read_by` e `partner_read_source`;
  - devolve `read_count`, `read_at`, `unread_count` e `has_unread`.

- `GET /chat/{corridaId}/unread-count`
  - agora devolve `count` e `has_unread` para o app montar badge ou resumo sem inventar estado local.

- Ajuste estrutural que sustentou a rodada:
  - `src/http/routes/app/chat.py` foi enxugada;
  - `src/http/routes/app/chat_contrato.py` e `src/http/routes/app/chat_contexto.py` ficaram responsaveis por contrato e contexto;
  - `src/mensagens/repository.py` virou fachada curta, enquanto consulta, persistencia e leitura foram separadas em modulos proprios.

## Atualizacao de execucao - `2026-03-31`

Nesta rodada nao houve mudanca de contrato HTTP para o app parceiro. O backend `pyloto_atende` foi refatorado internamente para tirar do monolito de `src/parceiros/service.py` o bloco de reputacao, penalidades e sincronizacao de conformidade.

- Criada a pasta `src/parceiros/conformidade/` no backend, com modulos pequenos para:
  - reputacao operacional;
  - penalidades operacionais;
  - aceite e recusa de corrida;
  - criacao e resolucao administrativa de penalidades;
  - sincronizacao de compliance.
- O efeito para o app e indireto, mas importante:
  - menos risco de regressao silenciosa em elegibilidade de corrida;
  - menos acoplamento entre despacho, agenda e reputacao;
  - base mais segura para continuar as proximas rotas e correcoes do parceiro.
- O contrato observado pelo app segue igual nesta rodada.

## Atualizacao complementar - `2026-03-31`

Outra rodada de faxina caiu sobre `src/pedidos/service.py` no backend `pyloto_atende`.

- Criadas as pastas:
  - `src/pedidos/parceiro/`
  - `src/pedidos/operacao/`
- O que saiu do monolito de pedidos:
  - payload minimizado para o app do entregador;
  - auditoria de acesso aos dados do solicitante;
  - regra de recusa e visibilidade de corridas;
  - eventos operacionais;
  - refresh de SLA operacional;
  - notificacao de mudanca de status.
- Efeito pratico para o app:
  - menos acoplamento entre oferta de corrida e infraestrutura de evento;
  - menos risco de quebrar serializacao minimizada ao tocar status ou SLA;
  - base melhor para evoluir `corridas/disponiveis`, historico e eventos da corrida.
- O contrato HTTP observado pelo app continua o mesmo nesta rodada.

## Atualizacao complementar 2 - `2026-03-31`

Mais uma parte de `src/pedidos/service.py` foi arrancada para fora do monolito no backend `pyloto_atende`.

- Criada a pasta:
  - `src/pedidos/transicoes/`
- O que saiu do service:
  - atualizacao generica de status;
  - aceite atomico da corrida;
  - ida para aguardando aprovacao;
  - liberacao do pedido apos pagamento aprovado.
- Efeito pratico para o app:
  - menos risco de regressao silenciosa no fluxo que leva uma corrida de disponivel para aceito;
- menos acoplamento entre pagamento e estado operacional;
- base melhor para continuar as correcoes de aceite, cancelamento e historico.
- O contrato HTTP observado pelo app continua o mesmo nesta rodada.

## Atualizacao complementar 3 - `2026-03-31`

O backend `pyloto_atende` levou mais um golpe de machado em `src/pedidos/service.py`.

- Criados os modulos:
  - `src/pedidos/criacao.py`
  - `src/pedidos/localizacao.py`
  - `src/pedidos/precificacao.py`
- O que saiu do monolito:
  - criacao de pedido e extracao de endereco;
  - atualizacao de localizacao geral;
  - tracking de rota;
  - precificacao manual.
- Efeito pratico para o app:
- menos risco de regressao acidental ao mexer em criacao e tracking no mesmo arquivo;
- base melhor para evoluir mapa, historico e reidratacao da corrida ativa sem cavar mais fundo no lamaçal;
- terreno mais seguro para atacar depois as consultas de pedido e o bloco de onboarding do parceiro.
- O contrato HTTP observado pelo app continua o mesmo nesta rodada.

## Atualizacao complementar 4 - `2026-03-31`

O backend `pyloto_atende` moveu o bloco de onboarding do parceiro para um feudo proprio.

- Criada a pasta:
  - `src/parceiros/onboarding/`
- O que saiu de `src/parceiros/service.py`:
  - consulta de onboarding;
  - detalhe administrativo do entregador;
  - revisao e upload administrativo de documentos;
  - edicao administrativa de cadastro;
  - registro e resolucao de incidentes operacionais;
  - envio de evidencia de auditoria de veiculo;
  - assinatura digital do contrato pelo parceiro.
- Efeito pratico para o app:
  - menos risco de quebrar onboarding, assinatura digital e auditoria de veiculo ao tocar outras partes do parceiro;
  - base melhor para continuar a evolucao de onboarding documental e politicas operacionais;
  - nenhuma mudanca de contrato HTTP nesta rodada.
- Observacao importante:
  - dois testes de integracao que aceitam corridas seedadas em `2026-03-24` continuam falhando por um problema antigo de SLA contra o relogio real em `2026-03-31`;
  - isso nao aponta regressao do bloco de onboarding refatorado.

## Atualizacao complementar 5 - `2026-03-31`

O backend `pyloto_atende` perdeu mais um corredor podre de `src/parceiros/service.py`.

- Criados os modulos:
  - `src/parceiros/onboarding/admin_payload.py`
  - `src/parceiros/onboarding/admin_creation.py`
  - `src/parceiros/onboarding/admin_partner_record.py`
  - `src/parceiros/onboarding/document_building.py`
- O que saiu do monolito:
  - normalizacao do payload de cadastro administrativo do entregador;
  - criacao administrativa e persistencia do parceiro;
  - montagem do registro de parceiro para onboarding;
  - construcao e validacao dos documentos obrigatorios.
- Efeito pratico para o ecossistema do app:
  - menos risco de quebrar onboarding administrativo ao tocar regras operacionais do parceiro;
  - base melhor para evoluir upload, revisao documental e bloqueios cadastrais sem cavar no mesmo pantano;
  - nenhuma mudanca de contrato HTTP nesta rodada.

## Atualizacao complementar 6 - `2026-03-31`

O backend `pyloto_atende` moveu as consultas administrativas de onboarding para um subfeudo proprio.

- Criada a subpasta:
  - `src/parceiros/onboarding/consulta/`
- O que saiu de `src/parceiros/service.py`:
  - resumo administrativo do entregador;
  - status de onboarding com `pending_reason`;
  - detalhe administrativo expandido do parceiro.
- Efeito pratico para o ecossistema do app:
  - menos risco de quebrar leitura administrativa ao mexer em criacao, revisao documental ou autosservico;
  - base melhor para evoluir onboarding, bloqueios e painel administrativo sem reabrir o mesmo cadaver estrutural;
  - nenhuma mudanca de contrato HTTP nesta rodada.

## Atualizacao complementar 7 - `2026-03-31`

O backend `pyloto_atende` abriu um feudo proprio para a conta do parceiro.

- Criada a pasta:
  - `src/parceiros/conta/`
- O que saiu de `src/parceiros/service.py`:
  - auto registro minimo do parceiro;
  - login/autenticacao;
  - leitura do proprio perfil;
  - atualizacao do perfil publico com auditoria.
- Efeito pratico para o ecossistema do app:
  - menos risco de quebrar login e perfil ao tocar agenda, onboarding ou bloqueios operacionais;
  - base melhor para evoluir autenticacao, perfil e contratos do parceiro sem reabrir o mesmo tumulo;
  - nenhuma mudanca de contrato HTTP nesta rodada.

## Atualizacao complementar 8 - `2026-03-31`

O backend `pyloto_atende` abriu um feudo proprio para consulta de pedidos.

- Criada a pasta:
  - `src/pedidos/consulta/`
- O que saiu de `src/pedidos/service.py`:
  - busca de pedido por ID;
  - busca de pedido por numero legivel;
  - listagem paginada com filtros.
- Efeito pratico para o ecossistema do app:
  - menos risco de quebrar leitura de pedido ao mexer em aceite, transicoes ou notificacoes;
  - base melhor para evoluir historico, detalhe de corrida e tracking sem continuar cavando no mesmo pantano;
  - nenhuma mudanca de contrato HTTP nesta rodada.

## Atualizacao complementar 9 - `2026-03-31`

O backend `pyloto_atende` moveu tambem a interacao do parceiro com a corrida para um feudo proprio.

- Criados os modulos:
  - `src/pedidos/parceiro/consulta.py`
  - `src/pedidos/parceiro/decisao_guard.py`
  - `src/pedidos/parceiro/aceite.py`
  - `src/pedidos/parceiro/recusa.py`
- O que saiu de `src/pedidos/service.py`:
  - listagem de corridas disponiveis;
  - historico do parceiro;
  - aceite de corrida;
  - recusa de corrida com reflexo reputacional.
- Efeito pratico para o ecossistema do app:
  - menos risco de quebrar oferta, aceite e historico ao mexer em tracking, notificacao ou transicoes;
  - base melhor para evoluir filtros de oferta, regras de recusa e fluxo de corrida ativa sem continuar reabrindo o mesmo tumulo;
  - nenhuma mudanca de contrato HTTP nesta rodada.

## Atualizacao complementar 10 - `2026-03-31`

O backend `pyloto_atende` moveu a jornada operacional da corrida para um subsubfeudo proprio.

- Criada a subpasta:
  - `src/pedidos/parceiro/jornada/`
- O que saiu de `src/pedidos/service.py`:
  - inicio da rota;
  - confirmacao de coleta;
  - finalizacao com comprovante;
  - registro de evento operacional com tracking vinculado.
- Efeito pratico para o ecossistema do app:
  - menos risco de quebrar execucao da corrida ao mexer em consulta, oferta ou notificacao;
  - base melhor para evoluir corrida ativa, tracking e telemetria sem continuar abrindo o mesmo sarcofago;
  - nenhuma mudanca de contrato HTTP nesta rodada.

## Atualizacao complementar 11 - `2026-03-31`

O backend `pyloto_atende` concluiu a queda estrutural de `src/pedidos/service.py` e separou o que e fluxo normal da corrida do que e bisturi manual do painel administrativo.

- Criada a pasta:
  - `src/pedidos/intervencoes_manuais/`
- O que foi isolado nesse feudo:
  - aprovacao manual de pagamento;
  - envio para aguardando aprovacao;
  - ajuste manual de precificacao;
  - mudanca administrativa de status com auditoria e fechamento de rota.
- A pasta `src/pedidos/orquestracao/` foi reorganizada em subpastas semanticas:
  - `admin/`
  - `entrada/`
  - `estado/`
  - `parceiro/`
  - `rest/`
- Efeito pratico para o ecossistema do app:
  - menos risco de quebrar fluxo do entregador ao mexer em correcao manual feita pelo painel;
  - base melhor para evoluir aceite, corrida ativa, tracking e correcoes administrativas sem reabrir o mesmo tumulo;
  - nenhuma mudanca de contrato HTTP nesta rodada.

## Atualizacao complementar 12 - `2026-03-31`

O backend `pyloto_atende` moveu o bloco de acesso operacional e despacho do parceiro para modulos proprios.

- Criados os modulos:
  - `src/parceiros/operacao/access_control.py`
  - `src/parceiros/operacao/dispatch_runtime.py`
- O que saiu de `src/parceiros/service.py`:
  - validacao de acesso operacional;
  - validacao de acesso a despacho;
  - contexto de despacho do parceiro;
  - cheque de elegibilidade por corrida;
  - filtro de parceiros disponiveis sob regra operacional real.
- Efeito pratico para o ecossistema do app:
  - menos risco de quebrar oferta e disponibilidade ao mexer em perfil, onboarding ou financeiro;
  - base melhor para continuar a extracao de rota ativa e localizacao do parceiro;
  - nenhuma mudanca de contrato HTTP nesta rodada.

## Atualizacao complementar 13 - `2026-03-31`

O backend `pyloto_atende` moveu o bloco de deslocamento do parceiro para um subfeudo proprio.

- Criada a subpasta:
  - `src/parceiros/operacao/deslocamento/`
- O que saiu de `src/parceiros/service.py`:
  - armacao da rota ativa;
  - finalizacao da rota ativa;
  - update de localizacao simples;
  - update de payload de localizacao;
  - leitura da ultima localizacao;
  - wrappers correlatos da `ParceiroService`.
- Efeito pratico para o ecossistema do app:
  - menos risco de quebrar rota ativa e tracking ao mexer em despacho, onboarding ou financeiro;
  - base melhor para seguir limpando a camada REST do parceiro;
  - nenhuma mudanca de contrato HTTP nesta rodada.

## Atualizacao complementar 14 - `2026-03-31`

O backend `pyloto_atende` arrancou de `src/parceiros/service.py` a camada publica inteira do parceiro e espalhou cada fluxo no seu proprio feudo.

- Criados os modulos:
  - `src/parceiros/conta/acesso_publico.py`
  - `src/parceiros/agenda/operacao_publica.py`
  - `src/parceiros/conformidade/corridas_publicas.py`
  - `src/parceiros/financeiro/credito_corrida.py`
  - `src/parceiros/financeiro/administracao_publica.py`
  - `src/parceiros/operacao/acesso_publico.py`
  - `src/parceiros/operacao/deslocamento_publico.py`
- Criadas as subpastas:
  - `src/parceiros/onboarding/administracao/`
  - `src/parceiros/onboarding/autosservico/`
- O que saiu de `src/parceiros/service.py`:
  - cadastro, login e perfil publico;
  - agenda e disponibilidade do parceiro;
  - onboarding administrativo e autosservico;
  - aceite e recusa com reflexo reputacional;
  - credito de corrida, repasses e mensalidades administrativas;
  - acesso operacional, despacho, rota ativa e tracking.
- Efeito pratico para o ecossistema do app:
  - menos risco de quebrar login, onboarding, disponibilidade, tracking e corrida ativa ao mexer em um fluxo vizinho;
  - base melhor para continuar a extracao dos helpers internos do parceiro sem reabrir o mesmo sarcofago;
  - nenhuma mudanca de contrato HTTP nesta rodada.

## Atualizacao complementar 15 - `2026-03-31`

O backend `pyloto_atende` domesticou a estrutura das pastas `financeiro`, `onboarding` e `operacao`, que ja estavam virando taberna de trolls sem mapa.

- Nova topologia de `financeiro`:
  - `src/parceiros/financeiro/base/`
  - `src/parceiros/financeiro/ciclo/`
  - `src/parceiros/financeiro/publico/`
- Nova topologia de `onboarding`:
  - `src/parceiros/onboarding/cadastro/`
  - `src/parceiros/onboarding/consulta/`
  - `src/parceiros/onboarding/documentos/`
  - `src/parceiros/onboarding/incidentes/`
  - `src/parceiros/onboarding/autosservico/`
  - `src/parceiros/onboarding/administracao/`
- Nova topologia de `operacao`:
  - `src/parceiros/operacao/despacho/`
  - `src/parceiros/operacao/compliance/`
  - `src/parceiros/operacao/localizacao/`
- Efeito pratico para o ecossistema do app:
  - menos risco de quebrar onboarding, despacho, disponibilidade e tracking ao mexer em arquivos sem relacao direta;
  - base muito melhor para continuar a queda de `src/parceiros/service.py` com imports limpos e dominios visiveis;
  - nenhuma mudanca de contrato HTTP nesta rodada.

## Atualizacao complementar 16 - `2026-04-01`

O backend `pyloto_atende` praticamente esvaziou o resto utilitario de `src/parceiros/service.py` e distribuiu o que sobrava em feudos contextuais menores.

- Criados os modulos:
  - `src/parceiros/base/politicas.py`
  - `src/parceiros/base/auditoria.py`
  - `src/parceiros/conta/registro_base.py`
  - `src/parceiros/onboarding/cadastro/consentimento_dados.py`
  - `src/parceiros/onboarding/documentos/contexto_documental.py`
  - `src/parceiros/onboarding/consulta/contexto_admin.py`
  - `src/parceiros/onboarding/administracao/parametros.py`
- Criadas as subpastas:
  - `src/parceiros/operacao/compliance/base/`
  - `src/parceiros/operacao/compliance/contexto/`
  - `src/parceiros/operacao/localizacao/contexto/`
- O que saiu de `src/parceiros/service.py`:
  - politicas de retencao e tracking;
  - auditoria do parceiro;
  - unicidade de cadastro e hash de senha;
  - consentimento de dados e construcao documental;
  - resumo, status e detalhe administrativo do onboarding;
  - contexto de localizacao e elegibilidade operacional.
- Efeito pratico para o ecossistema do app:
  - menos risco de quebrar login, onboarding, disponibilidade, tracking e acesso operacional ao mexer em uma regra vizinha;
  - base muito melhor para evoluir o backend do entregador sem reabrir a mesma tumba em `service.py`;
  - nenhuma mudanca de contrato HTTP nesta rodada.

## Atualizacao complementar 17 - `2026-04-01`

O backend `pyloto_atende` ganhou grimorios estruturais em `src/parceiros/` e em todas as subpastas desse dominio.

- Cada pasta agora tem `grimorio.md` com:
  - proposito da pasta;
  - criterio curto para abrir subpasta;
  - tree local que deve ser mantida atualizada.
- Efeito pratico para o ecossistema do app:
  - menor chance de a estrutura de parceiros voltar a virar amontoado opaco;
  - onboarding, operacao, financeiro, conta, agenda e conformidade ficaram mais faceis de navegar;
  - nenhuma mudanca de contrato HTTP nesta rodada.
