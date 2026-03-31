# Analise de rotas e backlog de backend para o `pyloto_app-parceiro`

Data da analise: `2026-03-31`

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
| `GET /corridas/disponiveis` | Sim | OK com ressalvas graves | `dispatch_access` por item e filtro de liberacao remanescente foram corrigidos, mas o backend ainda nao usa `lat/lng/raio` para filtro/ranking geoespacial |
| `GET /corridas/{id}` | Sim | OK com ressalvas | Falta expor estado operacional server-driven para reduzir logica no app |
| `GET /corridas/{id}/capacidade-check` | Sim | OK | Agora prioriza a razao de bloqueio de despacho quando a restricao real nao e capacidade fisica |
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
| `GET /corridas/disponiveis` | Ja aplica `dispatch_access` por item e bucket remanescente, mas ainda nao usa `lat/lng/raio`; tambem nao devolve `distancia_ate_coleta`, `eta_ate_coleta` ou ranking server-driven | Fazer filtro geoespacial real, ordenar por distancia/score de despacho e devolver payload pronto para UI |
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

2. Completar `GET /corridas/disponiveis`.
   - O backend ja aplica `dispatch_access` por item e respeita a liberacao remanescente.
   - Ainda falta usar `lat`, `lng` e `raio`.
   - Ainda falta ordenar por distancia ate a coleta e devolver `distancia_ate_coleta_m`, `eta_ate_coleta_min`, `distancia_total_m`, `tempo_total_min` e score/rank final.

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
2. completar `GET /corridas/disponiveis` com geo de verdade
3. roteirizacao server-side
4. upload/presign para comprovantes e onboarding
5. logout com revogacao

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
