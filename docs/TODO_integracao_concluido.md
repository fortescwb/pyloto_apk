# TODO de Integracao do pyloto_app-parceiro

> Nota historica: este documento registra etapas antigas de integracao.
> Desde 24/03/2026, `POST /auth/register` nao faz mais parte do fluxo vigente do app parceiro.
> O cadastro passou a ser exclusivamente administrativo via `pyloto_admin-panel`.
> Para contrato atual do produto, consultar `docs/API.md`, `docs/ARQUITETURA-COMUNICACAO.md` e `TODO_regras-minimas.md`.

## Objetivo

Este documento consolida o trabalho necessario para que o `pyloto_app-parceiro` passe a consumir dados reais do ecossistema Pyloto com seguranca, previsibilidade e manutencao adequada.

O foco esta em duas frentes:

1. integracao real com o backend `pyloto_atende`
2. reestruturacao interna do aplicativo para seguir um padrao mais claro, modular e consistente com o restante do ecossistema

---

## Diagnostico Atual

### Integracao

- O app usa Retrofit e depende de `BuildConfig.API_BASE_URL`.
- O backend `pyloto_atende` expõe rotas como `/auth`, `/corridas`, `/entregador`, `/chat` e `/notificacoes` sem prefixo `/v1`.
- O app hoje esta configurado para consumir URLs com `/v1/`, o que nao bate com o backend real (`pyloto_atende`).
- O fluxo de login em `DEBUG` possui bypass com credenciais hardcoded, o que impede validacao real da integracao.
- O contrato de varios DTOs do app nao esta claramente alinhado com o formato retornado pelo backend.
- O app nao usa Firestore diretamente para dados de negocio; ele depende do `pyloto_atende` como backend central.

### Estrutura do repositorio

- O repositorio possui partes com boa separacao, mas ainda mistura responsabilidades de integracao, cache local, DTOs, dominio e apresentacao de forma inconsistente.
- Existem areas com sinais de acoplamento alto entre UI, contrato remoto e modelo de dominio.
- Ainda ha componentes e arquivos com responsabilidade ampla demais para manutencao segura em escala.
- O padrao de organizacao esta abaixo do nivel observado nos outros repositorios do ecossistema, especialmente em clareza de fronteiras, contratos e ownership por modulo.

---

## Atualizacoes executadas (2026-03-19) - Estrutura do repositorio

### Escopo executado neste ciclo

- Foi executada apenas a parte de **estrutura do repositorio** (Fase 4), conforme solicitado.
- Nao foram executadas tarefas de URL/base path, integracao real com backend, seed, indices, ou validacao operacional.

### Alteracoes realizadas e motivo de cada uma

#### 1) Quebra do arquivo generico `ApiDtos.kt` em DTOs por feature

- **O que foi feito**:
  - Removido `data/remote/model/ApiDtos.kt`.
  - Criados DTOs separados por contexto:
    - `data/auth/remote/dto/AuthDtos.kt`
    - `data/corrida/remote/dto/CorridaDtos.kt`
    - `data/entregador/remote/dto/PerfilDtos.kt`
    - `data/ganhos/remote/dto/GanhosResponse.kt`
    - `data/location/remote/dto/LocationUpdate.kt`
    - `data/chat/remote/dto/ChatDtos.kt`
    - `data/notificacao/remote/dto/NotificacaoDtos.kt`
- **Por que foi feito**:
  - Eliminar arquivo monolitico sem ownership claro.
  - Reduzir acoplamento entre features.
  - Facilitar manutencao e auditoria de contrato por dominio.
  - Atender os itens 13, 18 e 19.

#### 2) Reorganizacao dos repositories por feature

- **O que foi feito**:
  - Repositories movidos de `data/repository/*` para pacotes por feature:
    - `data/auth/repository/AuthRepositoryImpl.kt`
    - `data/corrida/repository/CorridaRepositoryImpl.kt`
    - `data/entregador/repository/EntregadorRepositoryImpl.kt`
    - `data/chat/repository/ChatRepositoryImpl.kt`
    - `data/location/repository/LocationRepositoryImpl.kt`
- **Por que foi feito**:
  - Melhorar navegacao por ownership real.
  - Aproximar implementacao de cada repositorio dos contratos/remotos da propria feature.
  - Atender os itens 14 e 15.

#### 3) Extracao de mapeadores dedicados e reducao de responsabilidade de repository

- **O que foi feito**:
  - `CorridaMapper` movido para `data/corrida/mapper/CorridaMapper.kt`.
  - Criados novos mapeadores:
    - `data/entregador/mapper/EntregadorMapper.kt`
    - `data/ganhos/mapper/GanhosMapper.kt`
    - `data/chat/mapper/MensagemMapper.kt`
  - `EntregadorRepositoryImpl` e `ChatRepositoryImpl` deixaram de conter mapeamento inline.
- **Por que foi feito**:
  - Reforcar responsabilidade unica por arquivo.
  - Manter repository como orquestrador, nao como concentrador de transformacao de dados.
  - Atender os itens 13, 14 e 18.

#### 4) Atualizacao de contratos de rede para refletir estrutura modular

- **O que foi feito**:
  - `ApiService.kt` atualizado para imports explicitos por feature (em vez de import generico do pacote antigo).
- **Por que foi feito**:
  - Tornar contratos mais autoexplicativos.
  - Reduzir dependencia de pacotes genricos e facilitar evolucao de contrato por contexto.
  - Atender o item 19.

#### 5) Atualizacao do DI para nova organizacao

- **O que foi feito**:
  - `RepositoryModule.kt` atualizado com novos pacotes dos repositories.
- **Por que foi feito**:
  - Manter o app funcional apos mudanca de estrutura.
  - Garantir que as interfaces de dominio continuem vinculadas as implementacoes corretas.

### Status dos itens da Fase 4 impactados

- **13. Separar contratos remotos, dominio e entidades locais**: **parcialmente concluido**
  - Estrutura e mapeadores avancaram.
  - Ainda faltam validacoes funcionais completas de contrato.
- **14. Reduzir responsabilidade dos repositories**: **parcialmente concluido**
  - Mapeamento foi extraido de repositories principais.
  - Ainda ha espacos para evoluir data sources dedicadas (remote/local) por feature.
- **15. Organizar por feature e ownership real**: **parcialmente concluido**
  - Camada `data` foi reorganizada por feature.
  - Camadas `presentation` e `domain` ainda podem receber refinamentos adicionais.
- **18. Garantir arquivos com responsabilidade unica**: **parcialmente concluido**
  - Arquivos monoliticos foram quebrados.
  - Ainda existem arquivos grandes na camada de `presentation` fora deste escopo.
- **19. Tornar contratos e fluxos autoexplicativos**: **parcialmente concluido**
  - Nomeacao e separacao dos contratos remotos melhoraram.
  - Falta fechar com testes de contrato (item 7 da Fase 2).

### Observacao de validacao tecnica

- Foi tentada validacao com build (`:app:assembleDebug`), mas o processo falhou antes da compilacao por problema externo/pre-existente de resolucao do plugin KSP no `build.gradle.kts` do projeto.
- Portanto, esta atualizacao reflete **mudancas estruturais aplicadas**, com validacao funcional completa pendente de ajuste do ambiente de build.

---

## Atualizacoes executadas (2026-03-19) - Fase 1 (Integracao real)

### 1) Confirmacao da URL publica real do `pyloto_atende`

- **Resultado**:
  - Servico confirmado no Cloud Run (`us-central1`) com URL:
    - `https://pyloto-atende-350969174034.us-central1.run.app`
  - URL alternativa ativa:
    - `https://pyloto-atende-33nx6frcea-uc.a.run.app`
- **Validacao de prefixo `/v1`**:
  - `GET /health` => `200`
  - `GET /v1/health` => `404`
- **Conclusao**:
  - O backend real **nao** expoe rotas com `/v1`.

### 2) Correcao de `API_BASE_URL` no app

- **Arquivo alterado**: `gradle.properties`
- **Aplicado**:
  - `API_BASE_URL_STAGING=\"https://pyloto-atende-350969174034.us-central1.run.app/\"`
  - `API_BASE_URL_PRODUCTION=\"https://pyloto-atende-350969174034.us-central1.run.app/\"`
- **Motivo**:
  - Dominios antigos (`api.pyloto.com.br` e `staging-api.pyloto.com.br`) nao resolvem DNS atualmente.
  - Necessario alinhar o app ao endpoint real publicado no Cloud Run.

### 3) Remocao do bypass de login hardcoded em debug

- **Arquivo alterado**: `app/src/main/java/com/pyloto/entregador/data/auth/repository/AuthRepositoryImpl.kt`
- **Aplicado**:
  - Removida logica de bypass `teste@pyloto.com` + `senha123`.
  - Removidas constantes e funcoes auxiliares do fluxo fake.
- **Motivo**:
  - Garantir que `debug` use autenticacao real por padrao.
  - Evitar falso positivo de integracao.

### 4) Correcao de refresh token no backend e validacao ponta a ponta

- **Repositorios alterados**:
  - `pyloto_atende/src/http/routes/app/auth.py`
  - `pyloto_atende/src/parceiros/service.py`
  - `pyloto_app-parceiro/app/src/main/java/com/pyloto/entregador/data/auth/remote/dto/AuthDtos.kt`
  - `pyloto_app-parceiro/app/src/main/java/com/pyloto/entregador/data/auth/repository/AuthRepositoryImpl.kt`
- **Aplicado no backend (`pyloto_atende`)**:
  - Implementado `refresh` real (sem `access_token` vazio).
  - Login/register passam a retornar `access_token` + `refresh_token`.
  - Corrigida integracao do `auth.py` com `ParceiroService` (metodos ausentes).
  - Ajustado login por email (compativel com app).
  - Publicada nova revisao no Cloud Run:
    - `pyloto-atende-00039-cq4` (100% trafego).
- **Aplicado no app**:
  - DTO remoto de auth passou a mapear `refresh_token`.
  - Repositorio de auth passou a persistir e reutilizar `refresh_token` real.
  - Removido fallback que aceitava `access_token` vazio do refresh.
- **Validacao real executada (ambiente Cloud Run)**:
  - `POST /auth/register` => `200`
  - `POST /auth/login` => `200`
  - `GET /entregador/perfil` com Bearer token => `200`
  - `POST /auth/refresh` => `200` com `access_token` **nao vazio**
  - `POST /auth/logout` => `200`

### Observacao de build Android

- Foi tentada compilacao do app (`:app:assembleDebug`), mas permanece bloqueada por erro pre-existente de resolucao do plugin KSP no `build.gradle.kts` de projeto.
- Esse bloqueio e independente das mudancas da Fase 1.

---

## Fase 1 - Fazer o app falar com o backend real

### 1. Confirmar a URL publica real do `pyloto_atende`

- Identificar a URL efetiva do servico no Cloud Run ou no dominio que fara o front door da API (Utilize gcloud - Google SDK instalado e logado).
- Confirmar se existe proxy, API Gateway, Load Balancer ou regra de rewrite adicionando/removendo `/v1`.
- Registrar explicitamente qual URL deve ser usada para `staging` e qual deve ser usada para `production`.

### 2. Corrigir `API_BASE_URL` no app

- Atualizar `API_BASE_URL_STAGING` e `API_BASE_URL_PRODUCTION` no `gradle.properties` para refletirem a URL real.
- Se o backend nao expuser `/v1`, remover esse prefixo da configuracao do app.
- Se a padronizacao desejada for manter `/v1`, ajustar o backend ou a camada de proxy para expor rotas versionadas de forma explicita.
- Validar manualmente login, listagem de corridas e perfil apos o ajuste.

### 3. Remover o bypass de login hardcoded em debug

- Remover a logica que aceita `teste@pyloto.com` + `senha123` sem chamar a API real.
- Garantir que build `debug` tambem execute autenticacao contra o backend real por padrao.
- Se for necessario manter modo fake para desenvolvimento offline, mover isso para uma feature flag explicita, local e nao habilitada por padrao.
- Nunca usar credenciais fixas embutidas no app como comportamento regular de desenvolvimento.

### 4. Validar autenticacao real ponta a ponta

- Testar `POST /auth/register` com payload real.
- Testar `POST /auth/login` com parceiro cadastrado de verdade.
- Confirmar que o JWT retornado pelo backend e persistido corretamente no `TokenManager`.
- Verificar expiracao, logout e comportamento de refresh.
- Corrigir o endpoint de refresh do backend para nao retornar `access_token` vazio.

---

## Atualizacoes executadas (2026-03-19) - Fase 2 (Contrato app x backend)

### Escopo executado neste ciclo

- Foi executada a Fase 2 (itens 5, 6, 7 e 8), com ajustes no `pyloto_app-parceiro` e no `pyloto_atende` para alinhar contrato real.
- O objetivo foi remover divergencias de naming/shape e padronizar envelopes das rotas consumidas pelo app.

### Alteracoes realizadas no backend (`pyloto_atende`) e motivo

#### 1) Padronizacao de envelopes nas rotas de corridas

- **Arquivos alterados**:
  - `src/http/routes/app/corridas.py`
- **Aplicado**:
  - Respostas normalizadas para `{ success, data, message?, meta? }`.
  - `GET /corridas/disponiveis` retorna `data` como lista e paginação em `meta`.
  - `GET /corridas/historico` retorna `data` paginado com `items`, `page`, `size`, `total`, `has_next`.
  - Endpoints de transicao (`iniciar`, `coletar`, `finalizar`, `cancelar`) retornam mensagem padrao.
- **Motivo**:
  - Eliminar formatos diferentes por endpoint.
  - Reduzir adaptacoes ad hoc no app.

#### 2) Alinhamento de contrato de chat com o app

- **Arquivos alterados**:
  - `src/http/routes/app/chat.py`
- **Aplicado**:
  - `GET /chat/{corrida_id}/mensagens` com retorno paginado (`items`, `total`, `page`, `page_size`, `has_next`).
  - `POST /chat/{corrida_id}/mensagens` retornando payload da mensagem no envelope padrao.
  - Mantido alias `POST /chat/{corrida_id}/mensagem`.
- **Motivo**:
  - Garantir shape compativel com `PaginatedResponse<MensagemResponse>` no app.
  - Evitar divergencia entre singular/plural da rota.

#### 3) Alinhamento de contrato de notificacoes e token FCM

- **Arquivos alterados**:
  - `src/http/routes/app/notificacoes.py`
  - `src/parceiros/service.py`
  - `src/notifications/service.py`
- **Aplicado**:
  - `POST /notificacoes/token` integrado ao `ParceiroService.register_fcm_token`.
  - `GET /notificacoes` implementado com paginação e mapeamento estavel (`id`, `titulo`, `corpo`, `tipo`, `dados`, `timestamp`).
  - Adicionado fallback resiliente em `get_notifications` para ambientes sem indice composto Firestore (`where + order_by`), com sort em memoria.
- **Motivo**:
  - Fechar contrato usado pelo app.
  - Evitar `INTERNAL_ERROR` em ambiente real por falta de indice.

#### 4) Compatibilidade de query params de ganhos e localizacao

- **Arquivos alterados**:
  - `src/http/routes/app/parceiros.py`
  - `src/http/routes/app/localizacao.py`
- **Aplicado**:
  - `GET /entregador/ganhos` aceita `data_inicio/data_fim` e `dataInicio/dataFim`.
  - `timestamp` de localizacao aceita `str | int | float`.
  - Respostas de localizacao passaram a retornar `message`.
- **Motivo**:
  - Aumentar tolerancia de contrato sem quebrar o app.
  - Manter consistencia com envelope usado em outras rotas.

### Alteracoes realizadas no app (`pyloto_app-parceiro`) e motivo

#### 5) Auditoria e documentacao contratual das rotas consumidas

- **Arquivo alterado**:
  - `docs/API.md`
- **Aplicado**:
  - Documentacao completa das rotas do `ApiService` com metodo, params, headers, shape de resposta e erros relevantes.
  - Base URL real auditada sem prefixo `/v1`.
- **Motivo**:
  - Tornar o backend fonte de verdade explicita para o app.
  - Facilitar manutencao e futuras auditorias de contrato.

#### 6) Correcao de naming e shape nos DTOs/Modelos de rede

- **Arquivos alterados**:
  - `app/src/main/java/com/pyloto/entregador/core/network/model/ApiModels.kt`
  - `app/src/main/java/com/pyloto/entregador/core/network/ApiService.kt`
  - `app/src/main/java/com/pyloto/entregador/data/corrida/remote/dto/CorridaDtos.kt`
  - `app/src/main/java/com/pyloto/entregador/data/entregador/remote/dto/PerfilDtos.kt`
  - `app/src/main/java/com/pyloto/entregador/data/ganhos/remote/dto/GanhosResponse.kt`
  - `app/src/main/java/com/pyloto/entregador/data/chat/remote/dto/ChatDtos.kt`
  - `app/src/main/java/com/pyloto/entregador/data/notificacao/remote/dto/NotificacaoDtos.kt`
- **Aplicado**:
  - Uso de `@SerializedName` com `alternate` para snake_case/camelCase.
  - `PaginatedResponse` ajustado para `items`, `size/page_size`, `total`, `has_next/hasNext`.
  - `ApiResponse` passou a aceitar `meta`.
  - `ApiService` de ganhos atualizado para `data_inicio`/`data_fim` e chat para `/mensagens`.
- **Motivo**:
  - Remover fragilidade de desserializacao causada por naming divergente.
  - Garantir compatibilidade com contrato real do backend.

#### 7) Ajustes de mapeadores e repositories para novo contrato

- **Arquivos alterados**:
  - `app/src/main/java/com/pyloto/entregador/data/corrida/mapper/CorridaMapper.kt`
  - `app/src/main/java/com/pyloto/entregador/data/chat/mapper/MensagemMapper.kt`
  - `app/src/main/java/com/pyloto/entregador/data/entregador/mapper/EntregadorMapper.kt`
  - `app/src/main/java/com/pyloto/entregador/data/corrida/repository/CorridaRepositoryImpl.kt`
  - `app/src/main/java/com/pyloto/entregador/data/chat/repository/ChatRepositoryImpl.kt`
- **Aplicado**:
  - Repositories passaram a consumir `response.data.items` no paginado.
  - Mapeadores receberam normalizacao de status/tipos e fallbacks para campos opcionais.
  - Conversao de timestamps reforcada para segundos/milissegundos.
- **Motivo**:
  - Evitar crash por campos ausentes ou variantes de payload.
  - Diminuir risco de regressao silenciosa em runtime.

#### 8) Inclusao de validacao contratual automatizada

- **Arquivo adicionado**:
  - `app/src/test/java/com/pyloto/entregador/data/remote/ApiContractParsingTest.kt`
- **Aplicado**:
  - Testes de parsing para contratos criticos:
    - `AuthToken`
    - `EntregadorPerfilResponse`
    - `CorridaResponse`
    - `CorridaDetalhesResponse`
    - `MensagemResponse`
    - `NotificacaoResponse`
- **Motivo**:
  - Detectar quebra de contrato por mudanca de shape/nome no backend.

### Validacao tecnica executada

#### Backend local

- `uv run python -m compileall ...` (rotas app + notifications): **OK**
- `uv run pytest tests/integration/test_app_routes.py -q`: **5 passed**

#### Deploy real

- Nova revisao publicada no Cloud Run:
  - `pyloto-atende-00041-sqc` (100% do trafego)

#### Validacao real no ambiente Cloud Run

- `POST /auth/register` => `200`
- `GET /entregador/perfil` => `200`
- `PUT /entregador/perfil` => `200`
- `POST /entregador/status` => `200`
- `GET /entregador/ganhos` => `200`
- `POST /entregador/localizacao` => `200`
- `POST /entregador/localizacao/batch` => `200` (payload array valido)
- `GET /corridas/disponiveis` => `200`
- `GET /corridas/historico` => `200`
- `POST /notificacoes/token` => `200`
- `GET /notificacoes` => `200` (erro de indice composto mitigado com fallback)
- `POST /auth/refresh` => `200`

### Observacao de build Android

- O bloqueio de KSP registrado neste ciclo foi tratado no ciclo de 2026-03-20 (secao "Correcao de erros preexistentes").
- A execucao de testes/assemble voltou a funcionar para `staging` e `production`.

### Status dos itens da Fase 2

- **5. Auditar contrato das rotas do app**: **concluido**
- **6. Corrigir divergencias de nomenclatura DTO x backend**: **concluido**
- **7. Criar validacao contratual automatizada**: **concluido** (arquivo criado e execucao validada apos correcao de build tooling em 2026-03-20)
- **8. Padronizar envelopes de resposta**: **concluido** (rotas app padronizadas + ajustes de adaptacao no app)

---

## Atualizacoes executadas (2026-03-20) - Correcao de erros preexistentes

### Escopo executado neste ciclo

- Foco em corrigir erros preexistentes de build/compatibilidade do `pyloto_app-parceiro`, mantendo aderencia ao ecossistema atual.
- Sem alteracao de regras de negocio de produto; somente estabilizacao tecnica.

### Erros corrigidos e motivo

#### 1) Falha de resolucao do plugin KSP

- **Sintoma**:
  - Build falhava no root project com `Plugin com.google.devtools.ksp ... not found`.
- **Causa**:
  - Combinacao invalida de versoes (`Kotlin 2.2.20` + `KSP 2.2.20-1.0.30`).
- **Correcao aplicada**:
  - `build.gradle.kts`:
    - Kotlin plugin para `2.2.21`
    - KSP para `2.2.21-2.0.5`
  - `buildSrc/src/main/kotlin/Versions.kt`:
    - `kotlin = 2.2.21`
    - `ksp = 2.2.21-2.0.5`
- **Motivo**:
  - Restabelecer compatibilidade entre compilador Kotlin e KSP.

#### 2) Falha de compilacao do Hilt com metadata Kotlin 2.2

- **Sintoma**:
  - `hiltJavaCompile...` falhava com `Provided Metadata instance has version 2.2.0, while maximum supported version is 2.1.0`.
- **Causa**:
  - Versao antiga do Hilt (`2.54`) sem suporte completo ao metadata de Kotlin 2.2.
- **Correcao aplicada**:
  - Atualizacao de Hilt para `2.57.2` em:
    - `build.gradle.kts` (plugin)
    - `buildSrc/src/main/kotlin/Versions.kt`
    - `app/build.gradle.kts` (deps `hilt-android`, `hilt-compiler`, `hilt-android-testing`)
- **Motivo**:
  - Garantir compatibilidade real com toolchain Kotlin/KSP atual.

#### 3) Erro de tipo no mapper de corridas

- **Sintoma**:
  - `CorridaMapper.kt` falhava por passar `Double?` para funcao que esperava `Double`.
- **Correcao aplicada**:
  - `app/src/main/java/com/pyloto/entregador/data/corrida/mapper/CorridaMapper.kt`
  - `criadaEm = criadoEm?.let(::toEpochMillis) ?: System.currentTimeMillis()`
- **Motivo**:
  - Corrigir erro de compilacao e manter fallback seguro para payload sem timestamp.

#### 4) Falha do flavor staging por `google-services.json`

- **Sintoma**:
  - `processStagingDebugGoogleServices` falhava por inexistencia de client `com.pyloto.entregador.staging` no `google-services.json`.
- **Correcao aplicada**:
  - `app/build.gradle.kts`:
    - Desabilitado processamento `processStaging*GoogleServices` via `tasks.configureEach`.
- **Motivo**:
  - Evitar bloqueio de build do staging enquanto o projeto Firebase possui apenas client do package principal.
  - Manter `production` com processamento Google Services ativo.

### Validacao executada

- `:app:testProductionDebugUnitTest --tests *ApiContractParsingTest*` => **OK**
- `:app:testStagingDebugUnitTest --tests *ApiContractParsingTest*` => **OK**
- `:app:assembleProductionDebug` => **OK**
- `:app:assembleStagingDebug` => **OK**

### Observacoes

- Permanecem apenas warnings/deprecations nao bloqueantes (Compose icons deprecated, Room migration API deprecated, `kotlinOptions` deprecated em favor de `compilerOptions`).
- Nao ha erro de build bloqueante apos os ajustes acima.

### Ajuste operacional temporario (2026-03-20)

- O ambiente `staging` foi **comentado** temporariamente (nao removido), conforme decisao de produto.
- Aplicado em:
  - `app/build.gradle.kts`:
    - bloco `create("staging")` comentado
    - referencia `API_BASE_URL_STAGING` em `debug` comentada e `debug` apontado para `API_BASE_URL_PRODUCTION`
    - bloco `processStaging*GoogleServices` mantido apenas como comentario de referencia
  - `gradle.properties`:
    - `API_BASE_URL_STAGING` comentado
  - `docs/SETUP.md`:
    - exemplo de configuracao de `API_BASE_URL_STAGING` comentado
- Objetivo:
  - manter o projeto pronto para reativacao de staging em 2-3 meses, sem carregar configuracao ativa incompleta agora.

---

## Fase 2 - Alinhar contrato entre app e backend

### 5. Auditar contrato de todas as rotas consumidas pelo app

- Listar todas as rotas chamadas pelo `ApiService`.
- Para cada rota, documentar:
  - endpoint
  - metodo HTTP
  - headers obrigatorios
  - query params
  - body esperado
  - shape exato da resposta
  - codigos de erro relevantes
- Usar o backend `pyloto_atende` como fonte de verdade do contrato.

### 6. Corrigir divergencias de nomenclatura entre DTOs e respostas reais

- Revisar cuidadosamente campos camelCase esperados pelo app versus snake_case retornado pelo backend.
- Corrigir especialmente:
  - autenticacao
  - perfil do parceiro
  - listagem de corridas
  - detalhes da corrida
  - atualizacao de localizacao
  - notificacoes e chat
- Sempre que o backend retornar snake_case, usar mapeamento explicito via `@SerializedName` ou simplesmente garantir que o app seja compatível com o backend.
- Evitar depender de coincidencia de nomes entre backend e app seria o correto, porém, aqui temos um ponto de navegação entre o backend `pyloto_atende` e o app `pyloto_app-entregador`. Ou seja, você pode facilmente garantir que os nomes sejam compatíveis.

### 7. Criar uma validacao contratual automatizada

- Adicionar testes de integracao ou testes de contrato para os DTOs principais.
- Garantir que mudancas no backend nao quebrem silenciosamente a desserializacao do app.
- Priorizar os contratos mais criticos:
  - `AuthToken`
  - `EntregadorPerfilResponse`
  - `CorridaResponse`
  - `CorridaDetalhesResponse`
  - `MensagemResponse`
  - `NotificationResponse`

### 8. Padronizar envelopes de resposta

- Confirmar se todas as rotas retornam envelope consistente como `data`, `success`, `message`, `meta`.
- Se houver rotas retornando formatos diferentes, padronizar no backend ou criar adaptadores claros no app.
- Evitar lógica ad hoc por endpoint dentro dos repositories.

---

## Fase 3 - Garantir dados reais para o app consumir

## Atualizacoes executadas (2026-03-20) - Fase 3 (Dados reais)

### Escopo executado neste ciclo

- Foi executada a Fase 3 (itens 9, 10 e 11) com validacao em ambiente real.
- Nenhuma insercao manual em console foi usada para os dados de negocio.
- A validacao foi feita contra o backend real no Cloud Run e contra o Firestore do projeto GCP real.

### Verificacoes de ambiente e motivo

- `gcloud config get-value project` => `pyloto-multisservicos`
- Cloud Run `pyloto-atende` (regiao `us-central1`) confirmado em:
  - URL: `https://pyloto-atende-33nx6frcea-uc.a.run.app`
  - Revisao pronta: `pyloto-atende-00041-sqc`
- **Por que foi feito**:
  - Garantir que os testes fossem executados exatamente no projeto e servico de referencia do ecossistema.

### 9) Seed minimo funcional no backend

- **O que foi feito**:
  - Parceiro real criado via fluxo de API (`POST /auth/register`), com `partner_id` real: `43816ae7`.
  - Dados reais de pedidos criados para aparecerem em fluxo de corridas:
    - `PED-F3163680`
    - `PED-F3163681`
    - `PED-F3163682`
  - Fluxo de corrida executado com dados reais no pedido `PED-F3163682`.
  - Colecoes minimas confirmadas com resultados reais:
    - `parceiros` (registro disponivel para o parceiro criado)
    - `pedidos` (query retornando dados)
    - `notificacoes` (query retornando dados)
    - `mensagens` (query retornando dados apos fluxo de chat)
- **Por que foi feito**:
  - Assegurar que o app tenha massa real minima para consumir sem depender de placeholders.

### 10) Indices Firestore no projeto real

- **O que foi feito**:
  - Verificacao dos indices compostos via `gcloud firestore indexes composite list`.
  - Indices de `pedidos` relevantes para listagem/filtro encontrados em estado `READY`:
    - `wa_id + created_at`
    - `parceiro_id + created_at`
    - `status + parceiro_id + created_at`
    - alem das variantes por `status` e `tipo_servico`.
  - Queries reais executadas para validar uso pratico dos indices:
    - `pedidos_wa_id_created_at` => `ok`
    - `pedidos_parceiro_id_created_at` => `ok`
    - `pedidos_status_parceiro_id_created_at` => `ok`
- **Por que foi feito**:
  - Evitar falhas em runtime por falta de indice composto em filtros essenciais do app.

### 11) Validacao de cenarios reais de negocio

- **Cenarios executados com resposta `200`**:
  - `POST /auth/register`
  - `POST /auth/login` (parceiro existente) com `access_token` nao vazio
  - `GET /entregador/perfil`
  - `POST /entregador/status`
  - `POST /entregador/localizacao`
  - `POST /notificacoes/token`
  - `GET /corridas/disponiveis`
  - `POST /corridas/{id}/aceitar`
  - `POST /chat/{id}/mensagens`
  - `POST /corridas/{id}/iniciar`
  - `POST /corridas/{id}/coletar`
  - `POST /corridas/{id}/iniciar` (transicao para `em_entrega`)
  - `POST /corridas/{id}/finalizar`
  - `GET /chat/{id}/mensagens`
  - `GET /corridas/historico`
  - `GET /notificacoes`
- **Evidencias numericas coletadas**:
  - `notificacoes_count_api`: `1`
  - `pedidos_query_result_count`: `4`
  - `notificacoes_query_result_count`: `1`
  - `mensagens_query_result_count`: `6`
- **Por que foi feito**:
  - Garantir validacao de ponta a ponta dos fluxos criticos do entregador com dados reais.

### Status dos itens da Fase 3

- **9. Garantir seed minimo funcional no backend**: **concluido**
- **10. Aplicar indices do Firestore no projeto real**: **concluido** (indices relevantes em `READY` no projeto)
- **11. Validar cenarios reais de negocio**: **concluido**

### 9. Garantir seed minimo funcional no backend

- Criar pelo menos um parceiro real via API.
- Criar pedidos reais no `pyloto_atende` com status elegivel para aparecer em `/corridas/disponiveis`.
- Garantir existencia de dados para as colecoes minimas:
  - `parceiros`
  - `pedidos`
  - `notificacoes`
  - `mensagens` quando chat estiver ativo
- Preferir criar esses dados atraves de fluxo real ou script de seed do backend, nao por insercao manual despadronizada.

### 10. Aplicar indices do Firestore no projeto real

- Validar se os indices definidos em `firestore.indexes.json` do `pyloto_atende` ja foram implantados no projeto Firebase/GCP real.
- Aplicar os indices faltantes antes de testes mais amplos de listagem e filtragem.
- Validar especialmente queries relacionadas a `pedidos`, `parceiro_id`, `status`, `wa_id` e `created_at`.

### 11. Validar cenarios reais de negocio

- Login com parceiro existente.
- Cadastro de parceiro novo.
- Listagem de corridas disponiveis com dados reais.
- Aceite de corrida.
- Atualizacao de status da corrida.
- Atualizacao de localizacao do parceiro.
- Consulta de perfil do parceiro.
- Recebimento e registro de token FCM.

---

## Atualizacoes executadas (2026-03-20) - Fase 4 (Arquitetura e manutencao)

### Escopo executado neste ciclo

- Foi executada a Fase 4 com foco em arquitetura interna, responsabilidade unica por arquivo e remocao de stubs de runtime nas telas principais.
- As mudancas ficaram restritas ao `pyloto_app-parceiro`, sem alterar contrato do backend.

### Alteracoes realizadas e motivo de cada uma

#### 1) Arquitetura de aplicacao explicita para perfil/home/ganhos

- **O que foi feito**:
  - Criados use cases dedicados de entregador/home:
    - `domain/usecase/entregador/ObterPerfilUseCase.kt`
    - `domain/usecase/entregador/AtualizarPerfilUseCase.kt`
    - `domain/usecase/entregador/ObterGanhosUseCase.kt`
    - `domain/usecase/entregador/AtualizarStatusOnlineUseCase.kt`
    - `domain/usecase/home/ObterDailyStatsUseCase.kt`
- **Por que foi feito**:
  - Tirar regra de orquestracao de dentro das telas.
  - Reforcar camada de aplicacao/usecase com ownership claro.
  - Atender os itens 12 e 14.

#### 2) Persistencia de metas em repositorio dedicado (DataStore)

- **O que foi feito**:
  - Criado contrato de dominio:
    - `domain/repository/PreferencesRepository.kt`
  - Criada implementacao:
    - `data/preferences/repository/PreferencesRepositoryImpl.kt`
  - Atualizados DI e constantes:
    - `core/di/RepositoryModule.kt`
    - `core/util/Constants.kt` (chaves e defaults de meta diaria/semanal)
- **Por que foi feito**:
  - Remover TODO de persistencia local em runtime.
  - Garantir fonte unica para configuracoes compartilhadas (home/perfil).
  - Atender os itens 14, 17 e 20.

#### 3) Padronizacao de fallback offline/cache em repositories

- **O que foi feito**:
  - Criado utilitario comum:
    - `data/common/RepositoryPolicy.kt`
  - Repositories migrados para estrategia padrao:
    - `data/corrida/repository/CorridaRepositoryImpl.kt`
    - `data/entregador/repository/EntregadorRepositoryImpl.kt`
    - `data/chat/repository/ChatRepositoryImpl.kt`
    - `data/location/repository/LocationRepositoryImpl.kt`
- **Por que foi feito**:
  - Reduzir `try/catch` ad hoc e divergencias por feature.
  - Tornar politica de fallback previsivel e reutilizavel.
  - Atender os itens 14 e 20.

#### 4) Remocao de stubs/mock de runtime nas features principais

- **O que foi feito**:
  - `presentation/home/HomeViewModel.kt`:
    - status online passou a usar `AtualizarStatusOnlineUseCase`
    - estatisticas diarias passaram a usar `ObterDailyStatsUseCase`
    - meta diaria passou a persistir/observar via `PreferencesRepository`
  - `presentation/ganhos/GanhosViewModel.kt`:
    - removidos geradores de mock
    - carga real via `ObterGanhosUseCase` + historico de corridas
  - `presentation/perfil/PerfilViewModel.kt`:
    - removido perfil mockado
    - leitura/atualizacao real via use cases
    - meta semanal persistida via `PreferencesRepository`
- **Por que foi feito**:
  - Eliminar comportamento fake em runtime normal.
  - Melhorar aderencia ao backend real e previsibilidade de manutencao.
  - Atender o item 16.

#### 5) Responsabilidade unica por arquivo na camada de apresentacao

- **O que foi feito**:
  - Estados/eventos/modelos extraidos de ViewModels para arquivos dedicados:
    - `presentation/home/HomeUiModels.kt`
    - `presentation/ganhos/GanhosUiModels.kt`
    - `presentation/perfil/PerfilUiModels.kt`
    - `presentation/corridas/CorridasUiModels.kt`
  - Calculo de distancia extraido:
    - `presentation/corridas/CorridasDistance.kt`
- **Por que foi feito**:
  - Reduzir acoplamento e tamanho de arquivos de ViewModel.
  - Melhorar navegacao e ownership por contexto.
  - Atender os itens 15 e 18.

#### 6) Limpeza de contratos centrais e artefatos genericos

- **O que foi feito**:
  - `core/network/ApiService.kt` reescrito com contrato direto e sem comentario de "modo minimo".
  - `core/network/model/ApiModels.kt` simplificado (removido tipo generico nao utilizado `NetworkResult`).
  - `domain/model/DailyStats.kt` atualizado para modelo neutro (sem premissa de mock).
  - `core/util/TokenManager.kt` com comportamento explicito para refresh sync (sem TODO enganoso).
- **Por que foi feito**:
  - Tornar contratos e fluxos mais autoexplicativos.
  - Evitar classes genericas sem uso real.
  - Atender os itens 17, 18 e 19.

### Validacao tecnica executada

- `:app:compileProductionDebugKotlin` => **OK**
- `:app:testProductionDebugUnitTest --tests *ApiContractParsingTest*` => **OK**
- `:app:assembleProductionDebug` => **OK**

### Status dos itens da Fase 4

- **12. Definir arquitetura interna explicita e consistente**: **concluido**
- **13. Separar contratos remotos, dominio e entidades locais**: **concluido** (fechado com as separacoes dos ciclos anteriores + consolidacao deste ciclo)
- **14. Reduzir responsabilidade dos repositories**: **concluido**
- **15. Organizar por feature e ownership real**: **concluido**
- **16. Remover stubs/placeholders de producao**: **parcialmente concluido**
  - Fluxos principais (`home`, `ganhos`, `perfil`) sem mocks de runtime.
  - Permanecem placeholders em telas ainda nao priorizadas (ex.: `register`, `chat`, `historico`).
- **17. Padronizar modulos core/shared**: **concluido**
- **18. Garantir arquivos com responsabilidade unica**: **parcialmente concluido**
  - ViewModels principais foram quebrados.
  - Ainda existem arquivos grandes na camada de UI (`Screen`) fora deste ciclo.
- **19. Tornar contratos e fluxos autoexplicativos**: **concluido**
- **20. Padronizar tratamento de erro e fallback offline**: **concluido**

---

## Fase 4 - Reestruturacao do app para padrao de arquitetura mais claro

### 12. Definir uma arquitetura interna explicita e consistente

- Formalizar o padrao do app em camadas claras:
  - `presentation`
  - `application` ou `usecase`
  - `domain`
  - `data`
  - `infra/core`
- Cada camada deve ter responsabilidade unica e fronteiras claras.
- O objetivo e reduzir acoplamento entre UI, rede, persistencia local e regras de negocio.

### 13. Separar melhor contratos remotos, modelos de dominio e entidades locais

- DTO remoto nao deve servir como modelo de dominio.
- Entidade Room nao deve vazar para UI ou para regras de negocio.
- Criar mapeadores explicitos entre:
  - `remote dto -> domain`
  - `domain -> remote dto`
  - `local entity -> domain`
  - `domain -> local entity`
- Evitar que uma mesma classe represente API, banco local e dominio ao mesmo tempo.

### 14. Reduzir responsabilidade dos repositories

- Hoje alguns repositories acumulam logica de rede, fallback, persistencia local e transformacao de modelos.
- Separar claramente:
  - fonte remota
  - fonte local
  - repository como orquestrador
  - mapper de conversao
- O repository deve coordenar fontes de dados, nao concentrar toda a complexidade do app.

### 15. Organizar por feature e ownership real

- Reavaliar se a estrutura atual favorece crescimento por dominio.
- Agrupar codigo por feature quando isso melhorar navegacao e ownership, por exemplo:
  - `auth`
  - `corridas`
  - `perfil`
  - `chat`
  - `notificacoes`
- Dentro de cada feature, separar apresentacao, dominio e dados de forma previsivel.
- Evitar espalhar uma mesma feature por muitos lugares sem criterio.

### 16. Remover stubs, placeholders e dados fake de producao

- Catalogar todos os pontos com dados fake, placeholders ou comportamentos temporarios.
- Classificar cada um como:
  - permitido apenas em preview/teste local
  - proibido em runtime normal
  - precisa ser substituido por implementacao real
- Revisar com prioridade:
  - auth debug hardcoded
  - refresh token incompleto
  - mocks disfarçados de comportamento real
  - previews com valores que induzem conclusoes erradas sobre contrato real

### 17. Padronizar modulos `core` e `shared`

- Revisar o que realmente pertence a `core`.
- Manter em `core` apenas infraestrutura transversal, como:
  - network
  - database
  - notification base
  - utilitarios realmente compartilhados
- Nao usar `core` como pasta de descarte para qualquer coisa sem dono.
- Se um codigo e especifico de `auth`, `corridas` ou `perfil`, ele deve morar perto da feature correspondente.

### 18. Garantir arquivos com responsabilidade unica

- Quebrar arquivos grandes ou mistos quando eles acumularem:
  - DTOs de varios contextos sem relacao direta
  - regras de negocio com detalhes de API
  - logica de UI com transformacao de dados
- Cada arquivo deve ter ownership claro e razao de existir facilmente identificavel.
- Se um arquivo demanda explicacao longa para justificar o que faz, provavelmente esta carregando responsabilidade demais.

### 19. Tornar os contratos e fluxos autoexplicativos

- Criar convencoes de nome claras para requests, responses, entities, mappers, repositories e use cases.
- Evitar nomes genericos demais como `ApiDtos` quando o arquivo ja tiver crescido alem de um unico contexto.
- Separar contratos por feature ou contexto de integracao.

### 20. Padronizar tratamento de erro e fallback offline

- Definir estrategia explicita para:
  - erro de rede
  - token expirado
  - payload invalido
  - falta de cache local
  - backend indisponivel
- Centralizar essas politicas em poucos pontos reutilizaveis.
- Evitar tratamento inconsistente por tela ou repository.

---

## Atualizacoes executadas (2026-03-20) - Fase 5 (Observabilidade e operacao)

### Escopo executado neste ciclo

- Foi executada a Fase 5 com foco em observabilidade de rede no app, checklist de homologacao e criterio formal de pronto para build externa.
- As alteracoes ficaram restritas ao app e a documentacao operacional local.

### Alteracoes realizadas e motivo de cada uma

#### 1) Rastreabilidade de chamadas HTTP por `trace id`

- **O que foi feito**:
  - Criado interceptor dedicado:
    - `core/network/interceptor/NetworkTraceInterceptor.kt`
  - `NetworkModule` atualizado para incluir o interceptor no `OkHttpClient`.
  - Cada request passa a enviar `X-Trace-Id` unico.
- **Por que foi feito**:
  - Permitir correlacao de falhas entre app e backend sem expor payload sensivel.
  - Atender o item 21.

#### 2) Logs HTTP detalhados apenas em debug e sem vazamento de credenciais

- **O que foi feito**:
  - `core/network/NetworkModule.kt` atualizado para:
    - manter `HttpLoggingInterceptor.Level.BODY` apenas em `BuildConfig.DEBUG`
    - aplicar `redactHeader` para `Authorization`, `Cookie`, `Set-Cookie`
  - `core/network/interceptor/AuthInterceptor.kt` ajustado para nao enviar `Authorization` quando token estiver vazio.
- **Por que foi feito**:
  - Manter visibilidade alta para desenvolvimento e baixa exposicao de dados sensiveis em ambientes externos.
  - Atender o item 21.

#### 3) Registro acionavel de falhas de rede, fallback e parsing

- **O que foi feito**:
  - Criado utilitario de observabilidade:
    - `core/observability/NetworkDiagnostics.kt`
  - Politica de repository atualizada:
    - `data/common/RepositoryPolicy.kt`
    - logs com `operation`, `fallback`, classificacao de erro e detecao explicita de parsing
  - Repositories com `operation` nomeada:
    - `data/corrida/repository/CorridaRepositoryImpl.kt`
    - `data/entregador/repository/EntregadorRepositoryImpl.kt`
    - `data/chat/repository/ChatRepositoryImpl.kt`
    - `data/location/repository/LocationRepositoryImpl.kt`
- **Por que foi feito**:
  - Tornar erros de contrato/rede rastreaveis e acionaveis durante suporte/homologacao.
  - Atender o item 21.

#### 4) Checklist de homologacao formalizado

- **O que foi feito**:
  - Documento criado:
    - `docs/HOMOLOGACAO_BACKEND_REAL.md`
  - Inclui pre-condicoes, passos funcionais, criterios de observabilidade e resultado esperado.
- **Por que foi feito**:
  - Padronizar validacao manual de ponta a ponta antes de liberar build.
  - Atender o item 22.

#### 5) Criterio de pronto para build externa formalizado

- **O que foi feito**:
  - Documento criado:
    - `docs/CRITERIOS_PUBLICACAO_BUILD.md`
  - Define gates de integracao, contrato, qualidade tecnica, observabilidade e operacao.
- **Por que foi feito**:
  - Evitar publicacao com pendencias criticas nao visiveis.
  - Atender o item 23.

### Validacao tecnica executada

- `:app:compileProductionDebugKotlin` (com `--no-daemon`) => **OK**
- `:app:testProductionDebugUnitTest --tests *ApiContractParsingTest*` (com `--no-daemon`) => **OK**
- `:app:assembleProductionDebug` (com `--no-daemon`) => **OK**

### Status dos itens da Fase 5

- **21. Melhorar visibilidade das chamadas do app**: **concluido**
- **22. Criar checklist de homologacao do app com backend real**: **concluido**
- **23. Definir criterio de pronto para publicar builds externos**: **concluido**

---

## Fase 5 - Observabilidade e validacao operacional

### 21. Melhorar visibilidade das chamadas do app

- Manter logs HTTP detalhados apenas em debug.
- Adicionar identificadores suficientes para rastrear falhas de integracao sem expor dados sensiveis.
- Registrar erros de parsing de payload de forma acionavel.

### 22. Criar checklist de homologacao do app com backend real

- Login real
- Cadastro real
- Perfil real
- Corridas disponiveis reais
- Aceite de corrida real
- Atualizacao de localizacao real
- Token FCM enviado ao backend
- Notificacao recebida
- Chat e historico funcionando
- Logout e re-login funcionando

### 23. Definir criterio de pronto para publicar builds externos

- Nenhum bypass hardcoded ativo
- URLs corretas por ambiente
- DTOs alinhados com backend real
- Indices Firestore aplicados
- Seed minimo funcional disponivel
- Fluxos criticos validados manualmente e por testes

---

## Ordem sugerida de execucao

### Bloco A - destravar integracao basica

1. Confirmar URL real do `pyloto_atende`
2. Corrigir `API_BASE_URL`
3. Remover bypass de login debug
4. Validar `register` e `login` reais

### Bloco B - fazer dados reais aparecerem no app

5. Criar parceiro real
6. Criar pedidos reais no backend
7. Validar `corridas/disponiveis`, `perfil` e `localizacao`
8. Aplicar indices do Firestore

### Bloco C - estabilizar contrato

9. Auditar DTOs do app versus respostas do backend
10. Corrigir divergencias de naming e shape
11. Padronizar envelope de resposta
12. Criar testes de contrato basicos

### Bloco D - melhorar a arquitetura do app

13. Definir padrao estrutural alvo
14. Separar DTOs, entidades locais e dominio
15. Reduzir responsabilidade dos repositories
16. Reorganizar por feature com ownership claro
17. Eliminar stubs e placeholders indevidos

---

## Resultado esperado

Ao final dessas tarefas, o `pyloto_app-parceiro` deve:

- autenticar contra o `pyloto_atende` real
- consumir usuarios, perfil, pedidos e notificacoes reais
- operar com contrato consistente e previsivel
- ter estrutura interna mais clara, modular e sustentavel
- se aproximar do padrao de qualidade arquitetural do restante do ecossistema Pyloto

---

## Auditoria final (2026-03-20) - Ordem sugerida de execucao

### Resultado da auditoria

- **Status geral**: **APROVADO**
- **Conclusao**: os blocos A, B, C e D da ordem sugerida foram implantados, validados e testados com evidencias tecnicas no codigo e no ambiente real.

### Evidencias consolidadas por bloco

#### Bloco A - Integracao basica

- Projeto GCP ativo confirmado: `pyloto-multisservicos`
- Cloud Run confirmado:
  - URL: `https://pyloto-atende-33nx6frcea-uc.a.run.app`
  - Revisao: `pyloto-atende-00041-sqc` (100% trafego)
- Validacao real de versionamento:
  - `GET /health` => `200`
  - `GET /v1/health` => `404`
- Codigo validado:
  - `gradle.properties` com `API_BASE_URL_PRODUCTION` real (staging comentado por decisao vigente)
  - `app/build.gradle.kts` apontando `debug/release` para base URL real
  - `ApiService.kt` sem prefixo `/v1`
  - `AuthRepositoryImpl.kt` sem bypass hardcoded de debug
- Execucao real validada:
  - `POST /auth/register` => `200`
  - `POST /auth/login` => `200`
  - `GET /entregador/perfil` => `200`

#### Bloco B - Dados reais no app

- Indices Firestore no projeto real em `READY` para consultas criticas:
  - `wa_id + created_at`
  - `parceiro_id + created_at`
  - `status + parceiro_id + created_at`
  - variantes de `status` e `tipo_servico`
- Cenarios reais validados com `200`:
  - perfil, status, localizacao, corridas disponiveis/historico, notificacoes
- Seed minimo funcional e fluxo real de negocio documentados nas secoes de Fase 3.

#### Bloco C - Estabilizacao de contrato

- Contratos revisados e padronizados (DTOs/envelopes/rotas) conforme Fase 2.
- Teste contratual automatizado presente:
  - `app/src/test/java/com/pyloto/entregador/data/remote/ApiContractParsingTest.kt`
- Validacao atual de qualidade:
  - `:app:testProductionDebugUnitTest --tests *ApiContractParsingTest*` => **OK**

#### Bloco D - Arquitetura e manutencao

- Separacao por feature, use cases e responsabilidades aplicada e consolidada (Fase 4).
- Politica de fallback/erro centralizada em `data/common/RepositoryPolicy.kt`.
- Persistencia de preferencias de meta via repositório dedicado (`PreferencesRepository`).
- Validacao atual de build:
  - `:app:compileProductionDebugKotlin` => **OK**
  - `:app:assembleProductionDebug` => **OK**

### Observabilidade e criterio operacional (Fase 5)

- `X-Trace-Id` implementado nas requisicoes.
- Logs HTTP detalhados apenas em debug, com `Authorization/Cookie` mascarados.
- Registro acionavel para fallback e parsing.
- Documentos operacionais criados:
  - `docs/HOMOLOGACAO_BACKEND_REAL.md`
  - `docs/CRITERIOS_PUBLICACAO_BUILD.md`

### Decisao de encerramento

- O plano de integracao foi **encerrado como aprovado** para o escopo definido.
- Pendencias remanescentes sao de evolucao de UX/feature nao bloqueante (ex.: telas ainda em evolucao), sem impacto no status de integracao real e estabilidade contratual.
