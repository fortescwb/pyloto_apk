# Validacao de Aceite Real do Pronto Minimo 2026-03-24

Data da auditoria: 24/03/2026

Escopo auditado:

- tarefas 1 a 11 de [TODO_regras-minimas.md](/c:/Users/jamis/Documents/Projetos/Pyloto/pyloto_app-parceiro/TODO_regras-minimas.md)
- repositorios `pyloto_atende`, `pyloto_admin-panel` e `pyloto_app-parceiro`

Objetivo:

- confirmar aceite real do fluxo parceiro-entrega com evidencia de codigo, testes executados e revisao de coerencia documental

## Metodologia aplicada

- leitura integral do TODO e conferência das pequenas provas registradas por tarefa;
- inspeção pontual dos pontos centrais de backend, painel, app e documentação ativa;
- reexecução das validações do escopo parceiro-entrega em 24/03/2026;
- execução da suíte completa do backend para distinguir aceite do escopo auditado de dívida legada fora dele;
- varredura textual por divergências ativas de contrato, API e onboarding.

## Resultado consolidado

- Escopo 1 a 11: aceito.
- Divergência ativa conhecida entre contrato, sistema e interface no escopo parceiro-entrega: nao encontrada.
- Saude global do repositorio `pyloto_atende`: nao verde.
- Bloqueio para manter as tarefas 1 a 11 como concluidas: nao.

Interpretacao objetiva:

- o modulo parceiro-entrega implementado neste ciclo atende o pronto minimo definido no TODO;
- o backend inteiro ainda possui falhas legadas em fluxos antigos e modulos fora desse escopo;
- portanto, a conclusao desta auditoria vale para o escopo parceiro-entrega e nao para o repositorio inteiro.

## Validacoes executadas em 24/03/2026

1. Backend do escopo parceiro-entrega:
   `uv run pytest tests/unit/test_pricing_service.py tests/integration/test_admin_entregadores_routes.py tests/integration/test_app_routes.py -q`
   Resultado: `50 passed, 118 warnings`.
2. Painel administrativo:
   `npm run typecheck`
   Resultado: sem erros.
3. App parceiro:
   `.\gradlew.bat :app:compileProductionDebugKotlin`
   Resultado: build bem-sucedido.
4. Suíte completa do backend:
   `uv run pytest -q`
   Resultado: `67 failed, 212 passed, 165 warnings`.

Warnings residuais observados nesta rodada:

- JWT com chave HMAC curta no ambiente de teste;
- `redis.close()` deprecated;
- aviso de `tool.uv.dev-dependencies` deprecated;
- aviso do Android Gradle Plugin sobre `buildConfig=true`;
- aviso de Java native access do Gradle.

Nenhum desses warnings invalidou o aceite do escopo 1 a 11 nesta rodada.

## Matriz de aceite por tarefa

### 1. Cadastro e qualificacao do parceiro

Status: aceito.

Evidencias centrais:

- [entregadores.py](/c:/Users/jamis/Documents/Projetos/Pyloto/pyloto_atende/src/http/routes/admin/entregadores.py) exige os campos civis e operacionais no cadastro administrativo.
- [service.py](/c:/Users/jamis/Documents/Projetos/Pyloto/pyloto_atende/src/parceiros/service.py) separa status cadastral, status operacional, treinamento e assinatura digital.
- [ContractSignatureScreen.kt](/c:/Users/jamis/Documents/Projetos/Pyloto/pyloto_app-parceiro/app/src/main/java/com/pyloto/entregador/presentation/onboarding/ContractSignatureScreen.kt) implementa o onboarding contratual do primeiro login.
- [test_admin_entregadores_routes.py](/c:/Users/jamis/Documents/Projetos/Pyloto/pyloto_atende/tests/integration/test_admin_entregadores_routes.py) cobre o fluxo `pending -> assinatura digital -> habilitacao`.

### 2. Documentos e elegibilidade

Status: aceito.

Evidencias centrais:

- [models.py](/c:/Users/jamis/Documents/Projetos/Pyloto/pyloto_atende/src/parceiros/models.py) modela status, validade, historico, retention e evidencias por documento.
- [service.py](/c:/Users/jamis/Documents/Projetos/Pyloto/pyloto_atende/src/parceiros/service.py) bloqueia operacao por documento pendente, rejeitado, vencido ou incidente operacional.
- [EntregadorCadastroFlow.tsx](/c:/Users/jamis/Documents/Projetos/Pyloto/pyloto_admin-panel/src/components/cadastros/EntregadorCadastroFlow.tsx) exige anexos/URLs e gera o contrato preenchido.
- [EntregadorAuditoriaModal.tsx](/c:/Users/jamis/Documents/Projetos/Pyloto/pyloto_admin-panel/src/components/cadastros/EntregadorAuditoriaModal.tsx) permite revisar documentos, incidentes e renovacoes.

### 3. Bau, capacidade e limites operacionais

Status: aceito.

Evidencias centrais:

- [regras_capacidade_bau.json](/c:/Users/jamis/Documents/Projetos/Pyloto/pyloto_atende/regras_capacidade_bau.json) virou a tabela unica e versionada do bau.
- [capacity.py](/c:/Users/jamis/Documents/Projetos/Pyloto/pyloto_atende/src/pedidos/capacity.py) calcula capacidade remanescente e recusa excessos.
- [NewHomeScreen.kt](/c:/Users/jamis/Documents/Projetos/Pyloto/pyloto_app-parceiro/app/src/main/java/com/pyloto/entregador/presentation/home/NewHomeScreen.kt) e [CorridaDetalhesScreen.kt](/c:/Users/jamis/Documents/Projetos/Pyloto/pyloto_app-parceiro/app/src/main/java/com/pyloto/entregador/presentation/corrida/disponivel/CorridaDetalhesScreen.kt) exibem snapshot e bloqueios vindos do backend.

### 4. Tipologia de corridas, SLA e prioridades

Status: aceito.

Evidencias centrais:

- [sla.py](/c:/Users/jamis/Documents/Projetos/Pyloto/pyloto_atende/src/pedidos/sla.py) centraliza modalidade, precedencia e deadlines.
- [service.py](/c:/Users/jamis/Documents/Projetos/Pyloto/pyloto_atende/src/pedidos/service.py) aplica a viabilidade operacional no despacho e aceite.
- [page.tsx](/c:/Users/jamis/Documents/Projetos/Pyloto/pyloto_admin-panel/src/app/pyloto-entrega-servicos/solicitacoes/page.tsx) mostra modalidade, SLA e alertas no painel.

### 5. Rota, localizacao e prova operacional

Status: aceito.

Evidencias centrais:

- [service.py](/c:/Users/jamis/Documents/Projetos/Pyloto/pyloto_atende/src/parceiros/service.py) persiste localizacao, rota ativa e tracking historico.
- [CorridaAtivaViewModel.kt](/c:/Users/jamis/Documents/Projetos/Pyloto/pyloto_app-parceiro/app/src/main/java/com/pyloto/entregador/presentation/corrida/ativa/CorridaAtivaViewModel.kt) e [CorridaAtivaScreen.kt](/c:/Users/jamis/Documents/Projetos/Pyloto/pyloto_app-parceiro/app/src/main/java/com/pyloto/entregador/presentation/corrida/ativa/CorridaAtivaScreen.kt) executam o inicio/encerramento real da rota.
- [EntregadorAuditoriaModal.tsx](/c:/Users/jamis/Documents/Projetos/Pyloto/pyloto_admin-panel/src/components/cadastros/EntregadorAuditoriaModal.tsx) expõe tracking e rota ativa para auditoria.

### 6. Agenda operacional

Status: aceito.

Evidencias centrais:

- [service.py](/c:/Users/jamis/Documents/Projetos/Pyloto/pyloto_atende/src/parceiros/service.py) implementa agenda D+1/D+2, cancelamento 12h e penalidade de no-show.
- [parceiros.py](/c:/Users/jamis/Documents/Projetos/Pyloto/pyloto_atende/src/http/routes/app/parceiros.py) expõe a agenda ao app.
- [HomeViewModel.kt](/c:/Users/jamis/Documents/Projetos/Pyloto/pyloto_app-parceiro/app/src/main/java/com/pyloto/entregador/presentation/home/HomeViewModel.kt) e [NewHomeScreen.kt](/c:/Users/jamis/Documents/Projetos/Pyloto/pyloto_app-parceiro/app/src/main/java/com/pyloto/entregador/presentation/home/NewHomeScreen.kt) refletem o estado operacional real.

### 7. Fluxo financeiro do parceiro

Status: aceito.

Evidencias centrais:

- [service.py](/c:/Users/jamis/Documents/Projetos/Pyloto/pyloto_atende/src/parceiros/service.py) centraliza Pix, repasse D+1, mensalidade, extrato e bloqueio financeiro.
- [entregadores.py](/c:/Users/jamis/Documents/Projetos/Pyloto/pyloto_atende/src/http/routes/admin/entregadores.py) expõe as ações administrativas de repasse e mensalidade.
- [GanhosScreen.kt](/c:/Users/jamis/Documents/Projetos/Pyloto/pyloto_app-parceiro/app/src/main/java/com/pyloto/entregador/presentation/ganhos/GanhosScreen.kt) e [EntregadorAuditoriaModal.tsx](/c:/Users/jamis/Documents/Projetos/Pyloto/pyloto_admin-panel/src/components/cadastros/EntregadorAuditoriaModal.tsx) exibem o estado financeiro nas duas pontas.

### 8. Penalidades, incidentes e reputacao

Status: aceito.

Evidencias centrais:

- [service.py](/c:/Users/jamis/Documents/Projetos/Pyloto/pyloto_atende/src/parceiros/service.py) implementa gradação de penalidades, incidentes criticos e rebaixamento por recusa reiterada.
- [service.py](/c:/Users/jamis/Documents/Projetos/Pyloto/pyloto_atende/src/pedidos/service.py) registra recusa, motivo e impacto em distribuicao futura.
- [CorridaDetalhesViewModel.kt](/c:/Users/jamis/Documents/Projetos/Pyloto/pyloto_app-parceiro/app/src/main/java/com/pyloto/entregador/presentation/corrida/disponivel/CorridaDetalhesViewModel.kt) e [CorridaDetalhesScreen.kt](/c:/Users/jamis/Documents/Projetos/Pyloto/pyloto_app-parceiro/app/src/main/java/com/pyloto/entregador/presentation/corrida/disponivel/CorridaDetalhesScreen.kt) enviam categoria e motivo reais de recusa.

### 9. LGPD, seguranca e retencao

Status: aceito.

Evidencias centrais:

- [service.py](/c:/Users/jamis/Documents/Projetos/Pyloto/pyloto_atende/src/parceiros/service.py) registra base legal, snapshot de retencao e metadados `retention_until_at`.
- [service.py](/c:/Users/jamis/Documents/Projetos/Pyloto/pyloto_atende/src/pedidos/service.py) minimiza o payload entregue ao app e audita acesso.
- [CorridaMapper.kt](/c:/Users/jamis/Documents/Projetos/Pyloto/pyloto_app-parceiro/app/src/main/java/com/pyloto/entregador/data/corrida/mapper/CorridaMapper.kt) e [CorridaEntity.kt](/c:/Users/jamis/Documents/Projetos/Pyloto/pyloto_app-parceiro/app/src/main/java/com/pyloto/entregador/core/database/entity/CorridaEntity.kt) nao persistem telefone bruto do solicitante.

### 10. Auditoria, observabilidade e testes

Status: aceito.

Evidencias centrais:

- [constants.py](/c:/Users/jamis/Documents/Projetos/Pyloto/pyloto_atende/src/config/constants.py) define `audit_trails` como colecao persistida.
- [service.py](/c:/Users/jamis/Documents/Projetos/Pyloto/pyloto_atende/src/parceiros/service.py) e [service.py](/c:/Users/jamis/Documents/Projetos/Pyloto/pyloto_atende/src/pedidos/service.py) gravam auditoria critica administrativa e operacional.
- [test_admin_entregadores_routes.py](/c:/Users/jamis/Documents/Projetos/Pyloto/pyloto_atende/tests/integration/test_admin_entregadores_routes.py) cobre os cenarios minimos obrigatorios e leitura da trilha persistida.

### 11. Pendencias criticas de alinhamento antes de producao

Status: aceito.

Evidencias centrais:

- [ALINHAMENTO_PRE_PRODUCAO_2026-03.md](/c:/Users/jamis/Documents/Projetos/Pyloto/pyloto_app-parceiro/docs/ALINHAMENTO_PRE_PRODUCAO_2026-03.md) consolidou definicoes unicas de bau, disponibilidade e tracking.
- [POLITICA_PRECIFICACAO_ENTREGAS_2026-03.md](/c:/Users/jamis/Documents/Projetos/Pyloto/POLITICA_PRECIFICACAO_ENTREGAS_2026-03.md) formalizou a governanca da precificacao.
- [test_pricing_service.py](/c:/Users/jamis/Documents/Projetos/Pyloto/pyloto_atende/tests/unit/test_pricing_service.py) valida a versao oficial `2026.03`.

## Revisao de divergencia documental

Resultado da varredura textual:

- os documentos ativos do produto permanecem alinhados com o fluxo administrativo + dupla assinatura;
- `docs/API.md` documenta `POST /auth/register` como `403 FORBIDDEN`;
- `docs/ARQUITETURA-COMUNICACAO.md` descreve o onboarding contratual vigente com `requires_digital_contract_signature`;
- referencias antigas a `POST /auth/register` continuam apenas em [TODO_integracao_concluido.md](/c:/Users/jamis/Documents/Projetos/Pyloto/pyloto_app-parceiro/docs/TODO_integracao_concluido.md), que agora traz banner explicito de documento historico e nao foi tratado como fonte de verdade;
- nao foi encontrada divergencia ativa bloqueante entre contrato, sistema e operacao no escopo parceiro-entrega.

## Falhas fora do escopo 1 a 11

A suíte completa do backend ainda falha em modulos legados nao cobertos por este TODO, principalmente:

- `tests/integration/test_flow_e2e.py`
- `tests/integration/test_public_routes.py`
- `tests/unit/test_constants.py`
- `tests/unit/test_event_router.py`
- `tests/unit/test_exchange.py`
- `tests/unit/test_flows.py`
- `tests/unit/test_fsm.py`
- `tests/unit/test_message_handler.py`
- `tests/unit/test_models.py`
- `tests/unit/test_screen_handlers.py`
- `tests/unit/test_security.py`
- `tests/unit/test_types.py`

Padrao predominante dessas falhas:

- contratos antigos de flow/router que nao batem mais com a implementacao atual;
- testes esperando atributos, funcoes ou telas legadas;
- expectativas antigas de JWT e tipos Pydantic;
- rotas publicas e fluxos de WhatsApp antigos fora do modulo parceiro-entrega.

Essas falhas devem virar um stream separado de saneamento do repositorio, mas nao invalidam o aceite do escopo 1 a 11 deste TODO.

## Conclusao

Conclusao desta auditoria:

- as tarefas 1 a 11 permanecem legitimamente marcadas como concluidas;
- a Tarefa 12 pode ser considerada atendida como auditoria efetiva do pronto minimo do modulo parceiro-entrega;
- o repositorio `pyloto_atende` ainda nao pode ser tratado como totalmente verde em nivel global ate a remediacao das falhas legadas fora do escopo.
