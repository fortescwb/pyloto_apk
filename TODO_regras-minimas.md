# TODO — Regras mínimas do módulo de Entregadores / Operação

Baseado nas regras operacionais e contratuais do documento anexado pela Pyloto, este checklist serve para **implementar, revisar ou confirmar** se a lógica mínima já existe no sistema. Onde houver divergência entre regra de negócio informal e documento contratual, a tarefa deve ser tratada como **pendência crítica de alinhamento** antes de marcar como concluída.

---

## 1. Cadastro e qualificação do parceiro

### [x] 1.1 Garantir campos obrigatórios de cadastro civil e operacional do parceiro
**Descrição:** Confirmar que o cadastro do entregador exige, no mínimo, nome completo, CPF, RG/documento de identidade, data de nascimento, endereço completo, telefone/WhatsApp, e-mail, tipo e placa do veículo, CNH, volume do baú e plano de adesão escolhido.

**Critérios de aceite:**
- O backend possui estrutura de dados para todos os campos exigidos contratualmente.
- O frontend/painel impede ativação de parceiro sem preenchimento dos campos obrigatórios.
- Existe validação mínima de formato para CPF, telefone, e-mail e placa.
- O volume do baú é armazenado como dado estruturado, não apenas texto livre.

**Pequenas provas desta implementação:**
- `pyloto_atende/src/http/routes/admin/entregadores.py`: `CreateEntregadorBody` exige `nome`, `cpf`, `rg`, `data_nascimento`, endereço completo, `veiculo_tipo`, `veiculo_placa`, `cnh_numero`, `bau_capacidade_litros` e `plano_adesao`.
- `pyloto_atende/src/parceiros/models.py`: `Parceiro` modela `rg`, `data_nascimento`, `endereco`, `cnh_numero`, `bau`, `bau_capacidade_litros` e `plano_adesao`; `BauParceiro` guarda o baú como estrutura própria.
- `pyloto_admin-panel/src/components/cadastros/EntregadorCadastroFlow.tsx`: `validateForm()` bloqueia salvar sem os dados mínimos e valida email, telefone, CPF e placa.

### [x] 1.2 Separar status cadastral de status operacional
**Descrição:** Garantir distinção entre parceiro ativo cadastralmente e parceiro apto a receber corridas naquele momento.

**Critérios de aceite:**
- Existem campos distintos para: status cadastral, online/offline, disponível/indisponível e bloqueio operacional.
- O sistema não usa um único campo genérico para representar tudo.
- Um parceiro pode estar cadastrado e ativo, porém temporariamente inelegível para receber corridas.

**Pequenas provas desta implementação:**
- `pyloto_atende/src/parceiros/models.py`: o modelo `Parceiro` possui `status_cadastral`, `status_operacional`, `bloqueio_operacional`, `disponivel` e `online` como campos separados.
- `pyloto_atende/src/parceiros/service.py`: no cadastro administrativo, o parceiro nasce com `status="pendente"`, `status_cadastral="pendente"` e `status_operacional="em_analise"`, sem liberar operação imediata.
- `pyloto_atende/src/http/middleware/auth.py`, `pyloto_atende/src/http/routes/app/corridas.py` e `pyloto_atende/src/http/routes/app/localizacao.py`: corridas e localização passaram a exigir parceiro operacionalmente habilitado.

### [x] 1.3 Registrar aceite contratual e versão do contrato
**Descrição:** Armazenar evidência de assinatura física/digital, versão do contrato aceito e timestamp do aceite.

**Critérios de aceite:**
- Existe campo para versão contratual aceita.
- Existe campo para data/hora do aceite.
- Existe referência ao arquivo assinado ou evidência de assinatura eletrônica.
- O parceiro não é ativado sem aceite contratual registrado.

**Pequenas provas desta implementação:**
- `pyloto_atende/src/parceiros/models.py`: `ContratoParceiro` possui `versao`, `aceito_em`, `evidencia_ref` e os campos de assinatura digital (`assinatura_digital_concluida`, `assinatura_digital_ref`, `assinatura_digital_enviada_em`).
- `pyloto_atende/src/parceiros/service.py`: `create_entregador_admin()` exige `contrato_versao`, `contrato_evidencia_ref` e confirmação de `contrato_assinado`; o parceiro continua `pendente` até a assinatura digital.
- `pyloto_atende/src/http/routes/app/parceiros.py` e `pyloto_app-parceiro/app/src/main/java/com/pyloto/entregador/presentation/onboarding/ContractSignatureScreen.kt`: o app passou a expor o fluxo de baixar o contrato e enviar a referência da via assinada digitalmente.
- `pyloto_atende/tests/integration/test_admin_entregadores_routes.py`: `test_admin_create_entregador_starts_pending_and_finishes_after_digital_signature` cobre o bloqueio inicial e a liberação posterior.

### [x] 1.4 Registrar treinamento operacional inicial
**Descrição:** Confirmar se o sistema registra que o parceiro recebeu treinamento operacional no ato do cadastro.

**Critérios de aceite:**
- Existe campo ou evento de treinamento concluído.
- Há data/hora e responsável pelo registro.
- O parceiro só pode ser liberado após treinamento marcado como concluído, se essa for a regra adotada.

**Pequenas provas desta implementação:**
- `pyloto_atende/src/parceiros/models.py`: `TreinamentoParceiro` possui `concluido`, `concluido_em` e `registrado_por`.
- `pyloto_atende/src/http/routes/admin/entregadores.py`: o body administrativo aceita `treinamento_concluido` e `treinamento_concluido_em`.
- `pyloto_atende/src/parceiros/service.py`: `create_entregador_admin()` rejeita cadastro sem `treinamento_concluido` e grava `TreinamentoParceiro(..., registrado_por=operator_email)`.
- `pyloto_atende/src/parceiros/service.py`: a liberação final após assinatura digital só acontece se o treinamento já estiver concluído.

**Validação executada nesta rodada:**
- `uv run pytest tests/integration/test_admin_entregadores_routes.py tests/integration/test_app_routes.py -q` em `pyloto_atende` -> `8 passed`
- `npm run typecheck` em `pyloto_admin-panel`
- `.\\gradlew.bat :app:compileProductionDebugKotlin` em `pyloto_app-parceiro`

---

## 2. Documentos e elegibilidade

### [x] 2.1 Controlar documentos exigidos no credenciamento
**Descrição:** Confirmar armazenamento e status de CNH, CPF, comprovante de residência, documento do veículo (CRLV), seguro do veículo quando aplicável, foto/comprovação do baú e dados bancários/Pix.

**Critérios de aceite:**
- Cada documento possui status próprio: pendente, aprovado, rejeitado ou vencido.
- O sistema registra data de envio, data de aprovação/rejeição e responsável pela validação.
- O sistema permite bloquear operação por documento pendente, inválido ou vencido.
- Há espaço para anexos/URLs de evidência documental.

**Pequenas provas desta implementação:**
- `pyloto_atende/src/parceiros/models.py`: `DocumentoParceiro` e `DocumentosParceiro` mantêm status individual por documento, referência/URL, `enviado_em`, `revisado_em`, `revisado_por`, `validade_em` e `motivo_rejeicao`; `PixParceiro` passou a usar status alinhado com aprovação documental.
- `pyloto_atende/src/parceiros/service.py`: `create_entregador_admin()` agora exige referências para CPF, RG, CNH, comprovante de residência, CRLV quando aplicável, foto do baú e comprovante Pix; seguro do veículo fica condicional a `seguro_veiculo_obrigatorio`; `_get_document_blockers()` e `ensure_operational_access()` barram a operação por documento pendente, rejeitado ou vencido.
- `pyloto_atende/tests/integration/test_admin_entregadores_routes.py`: o cadastro administrativo cobre documentos aprovados já na criação e há cenário cobrindo bloqueio operacional quando a CNH fica vencida.
- `pyloto_admin-panel/src/components/cadastros/EntregadorCadastroFlow.tsx`: o fluxo de cadastro passou a exigir os anexos/URLs documentais no momento da triagem e só permite salvar depois de gerar o contrato preenchido e anexar a via física assinada.
- `pyloto_admin-panel/src/lib/contracts/partner-contract.ts` e `pyloto_admin-panel/public/contracts/PylotoEntregadores.docx`: o contrato padrão da raiz foi levado para o painel e agora é preenchido automaticamente com os dados do cadastro para download em `.docx`, antes da assinatura manual e da etapa posterior de Gov.br no app.

**Validação executada nesta rodada:**
- `uv run pytest tests/integration/test_admin_entregadores_routes.py tests/integration/test_app_routes.py -q` em `pyloto_atende` -> `10 passed`
- `npm run typecheck` em `pyloto_admin-panel`
- `.\\gradlew.bat :app:compileProductionDebugKotlin` em `pyloto_app-parceiro`

### [x] 2.2 Validar vencimento e renovação documental
**Descrição:** Implementar ou confirmar rotina para documentos com validade, especialmente CNH e CRLV.

**Critérios de aceite:**
- Existe campo de validade para documentos aplicáveis.
- O sistema alerta antes do vencimento.
- O sistema impede operação após vencimento, se a regra assim exigir.
- Há trilha de auditoria de reenvio e nova aprovação.

**Pequenas provas desta implementação:**
- `pyloto_atende/src/parceiros/service.py`: `DOCUMENTS_WITH_VALIDITY`, `_get_document_alerts()`, `_effective_document_status()` e `review_entregador_document_admin()` passaram a tratar validade, alerta pré-vencimento, bloqueio por expiração e histórico de reenvio/aprovação.
- `pyloto_atende/src/parceiros/models.py`: `HistoricoDocumentoParceiro` foi adicionado ao `DocumentoParceiro`, preservando trilha de auditoria com ação, status, validade, motivo, data e responsável.
- `pyloto_admin-panel/src/components/cadastros/EntregadorCadastroFlow.tsx`: o cadastro administrativo passou a coletar `crlv_validade` e `seguro_veiculo_validade` quando aplicável.
- `pyloto_admin-panel/src/components/cadastros/EntregadorAuditoriaModal.tsx`: a equipe Pyloto consegue revisar documento, renovar referência/validade e visualizar histórico recente diretamente no fluxo de auditoria.
- `pyloto_atende/tests/integration/test_admin_entregadores_routes.py`: `test_onboarding_status_warns_before_document_expiry` cobre alerta antes do vencimento e `test_admin_document_review_restores_operational_access_and_keeps_history` cobre renovação + nova aprovação.

### [x] 2.3 Bloquear compartilhamento indevido de conta e uso de veículo não cadastrado
**Descrição:** Implementar mecanismo mínimo de prevenção, detecção e resposta a uso de credenciais por terceiros ou veículo divergente do cadastro.

**Critérios de aceite:**
- Existe regra operacional/documentada para auditoria de veículo em uso.
- O sistema permite solicitar foto do veículo com placa visível ou evidência equivalente.
- Existe fluxo de suspensão/bloqueio por uso irregular.
- Há registro de incidentes relacionados a fraude operacional.

**Pequenas provas desta implementação:**
- `pyloto_atende/src/parceiros/models.py`: `IncidenteOperacionalParceiro` passou a registrar tipo, status, evidência, placa observada, bloqueio aplicado, solicitação de foto do veículo e resolução.
- `pyloto_atende/src/parceiros/service.py`: `register_operational_incident_admin()`, `resolve_operational_incident_admin()`, `_get_incident_blockers()` e `submit_vehicle_audit_evidence()` implementam bloqueio operacional, auditoria de veículo e liberação posterior; `update_parceiro()` também passou a impedir alteração direta de veículo pelo app.
- `pyloto_atende/src/http/routes/admin/entregadores.py` e `pyloto_atende/src/http/routes/app/parceiros.py`: foram criadas rotas administrativas para abrir/resolver incidentes e a rota do app para o parceiro enviar foto do veículo com placa visível.
- `pyloto_admin-panel/src/components/cadastros/EntregadoresTable.tsx`, `pyloto_admin-panel/src/components/cadastros/EntregadorAuditoriaModal.tsx` e `pyloto_admin-panel/src/app/pyloto-entrega-servicos/cadastros/page.tsx`: o painel passou a sinalizar auditorias abertas e a registrar incidentes de compartilhamento de conta, veículo divergente e fraude operacional.
- `pyloto_app-parceiro/app/src/main/java/com/pyloto/entregador/presentation/onboarding/ContractSignatureScreen.kt` e `pyloto_app-parceiro/app/src/main/java/com/pyloto/entregador/presentation/onboarding/ContractSignatureViewModel.kt`: o app agora mostra a pendência de auditoria e permite enviar a evidência do veículo para análise.
- `pyloto_atende/tests/integration/test_admin_entregadores_routes.py`: `test_operational_incident_blocks_partner_until_resolved` cobre abertura de incidente, solicitação de foto, bloqueio do parceiro e reabilitação após resolução administrativa.

**Validação executada nesta rodada:**
- `uv run pytest tests/integration/test_admin_entregadores_routes.py tests/integration/test_app_routes.py -q` em `pyloto_atende` -> `13 passed`
- `npm run typecheck` em `pyloto_admin-panel`
- `.\\gradlew.bat :app:compileProductionDebugKotlin` em `pyloto_app-parceiro`

---

## 3. Baú, capacidade e limites operacionais

### [x] 3.1 Tornar obrigatória a informação estruturada do baú
**Descrição:** O volume do baú não pode existir apenas como observação contratual; deve ser dado estruturado, validável e utilizável pelo motor de despacho.

**Critérios de aceite:**
- Existe campo específico para capacidade do baú em litros.
- O sistema aceita apenas valores compatíveis com os planos/regras operacionais adotados.
- Existe prova ou validação administrativa de que o baú foi conferido.
- Parceiro sem baú mínimo exigido não pode operar.

**Pequenas provas desta implementação:**
- `pyloto_atende/src/parceiros/validators.py`: `normalize_bau_capacity()` passou a validar a litragem contra a tabela versionada central, aceitando apenas as capacidades operacionais vigentes.
- `pyloto_atende/src/parceiros/service.py`: `_get_bau_blockers()` e `_build_operational_state_update()` passaram a bloquear o parceiro quando o baú não estiver informado, não estiver conferido ou estiver fora da tabela operacional.
- `pyloto_admin-panel/src/components/cadastros/EntregadorCadastroFlow.tsx`: o cadastro administrativo continua exigindo `bau_capacidade_litros`, `bau_conferido` e evidência do baú como parte obrigatória da triagem.

### [x] 3.2 Implementar tabela de capacidade operacional por tipo de baú
**Descrição:** Confirmar que o sistema suporta, no mínimo, a tabela contratual de capacidade por litragem do baú.

**Critérios de aceite:**
- O sistema possui tabela/versionamento de capacidade por baú.
- Para 80L, 120L, 135L e 150L há limites parametrizados de volume, peso e valor.
- A tabela usada pelo sistema corresponde à regra vigente aprovada pela empresa.
- Mudança futura da tabela não exige alteração manual dispersa em múltiplos pontos do código.

**Pequenas provas desta implementação:**
- `pyloto_atende/regras_capacidade_bau.json`: a tabela operacional passou a existir como arquivo versionado com `80L`, `120L`, `135L` e `150L`, incluindo limites de volume, peso e valor e margem de alerta de `90%`.
- `pyloto_atende/src/parceiros/bau_policy.py`: o backend centraliza leitura da tabela/versionamento e expõe essa política para validação cadastral e despacho, evitando regra dispersa.
- `pyloto_atende/src/pedidos/capacity.py`: o motor de capacidade passou a consumir essa mesma tabela para snapshot remanescente, projeção de aceite e bloqueios.

### [x] 3.3 Sanear divergência de valor máximo transportado para baú 80L
**Descrição:** Existe divergência entre a regra de negócio informal mencionada anteriormente (R$ 500,00) e o contrato anexado (R$ 1.500,00 para 80L). Essa divergência precisa ser resolvida antes de considerar a lógica correta.

**Critérios de aceite:**
- Existe definição única e formal da regra vigente.
- Contrato, sistema, documentação interna e front exibem o mesmo valor.
- Há registro da decisão e, se necessário, atualização contratual/documental.
- Nenhum cálculo operacional permanece usando valor divergente.

**Pequenas provas desta implementação:**
- `pyloto_atende/regras_capacidade_bau.json`: a regra formal adotada nesta rodada passou a ser a do anexo contratual `2026.03`, com `80L = 20kg / R$ 1.500,00`, `120L = 30kg / R$ 2.000,00`, `135L = 40kg / R$ 2.500,00` e `150L = 50kg / R$ 3.000,00`.
- `Cadastro_Entregador.md`: a documentação interna foi alinhada à mesma tabela contratual, eliminando os valores divergentes que antes apareciam no documento.
- `pyloto_app-parceiro/app/src/main/java/com/pyloto/entregador/presentation/home/NewHomeScreen.kt`, `pyloto_app-parceiro/app/src/main/java/com/pyloto/entregador/presentation/corrida/disponivel/CorridaDetalhesScreen.kt` e `pyloto_admin-panel/src/components/cadastros/EntregadorAuditoriaModal.tsx`: app e painel passaram a exibir a mesma regra/snapshot enviada pelo backend.
- `pyloto_atende/tests/integration/test_admin_entregadores_routes.py`: `test_capacity_snapshot_and_admin_detail_follow_contract_policy_for_80l` valida explicitamente `80L = 20kg / R$ 1.500,00`.

### [x] 3.4 Calcular capacidade remanescente em tempo real
**Descrição:** Confirmar que volume, peso e valor em uso são recalculados conforme coleta, entrega, cancelamento, devolução ou reatribuição.

**Critérios de aceite:**
- O sistema mantém capacidade máxima e capacidade em uso/remanescente.
- A coleta de um pedido consome capacidade automaticamente.
- A entrega/cancelamento libera capacidade automaticamente.
- O parceiro e o motor de despacho recebem informação atualizada sem depender de input manual.

**Pequenas provas desta implementação:**
- `pyloto_atende/src/pedidos/capacity.py`: o backend passou a calcular `reserved`, `in_use`, `committed` e `remaining` em tempo real a partir das corridas do parceiro, usando `aceito/coletando` como reserva e `coletado/em_entrega` como ocupação efetiva do baú.
- `pyloto_atende/src/http/routes/app/parceiros.py`: foi criada a rota `GET /entregador/capacidade` para o app consultar capacidade remanescente atualizada.
- `pyloto_admin-panel/src/components/cadastros/EntregadorAuditoriaModal.tsx` e `pyloto_app-parceiro/app/src/main/java/com/pyloto/entregador/presentation/home/NewHomeScreen.kt`: painel e app passaram a mostrar o snapshot operacional atualizado sem input manual.

### [x] 3.5 Bloquear novas corridas quando limites forem atingidos ou estiverem críticos
**Descrição:** Implementar limitação automática para impedir aceite/atribuição acima da capacidade operacional.

**Critérios de aceite:**
- O sistema impede alocação quando volume máximo, peso máximo ou valor máximo forem excedidos.
- Existe regra clara para “próximo do limite”, se a empresa quiser margem de segurança.
- O app informa o motivo do bloqueio de forma compreensível.
- O log registra a tentativa de alocação recusada por capacidade.

**Pequenas provas desta implementação:**
- `pyloto_atende/src/pedidos/service.py`: `list_disponiveis()` passou a filtrar corridas que não cabem na capacidade restante e `aceitar_pedido()` agora revalida o encaixe antes de atribuir a corrida.
- `pyloto_atende/src/pedidos/capacity.py`: `ensure_order_fits_partner_capacity()` registra `pedido_capacity_rejected` no log e aplica margem crítica configurável via `near_limit_threshold_ratio`.
- `pyloto_atende/src/http/routes/app/corridas.py`: foi criada a rota `GET /corridas/{id}/capacidade-check` para o app explicar o bloqueio antes do aceite.
- `pyloto_app-parceiro/app/src/main/java/com/pyloto/entregador/presentation/corrida/disponivel/CorridaDetalhesViewModel.kt` e `pyloto_app-parceiro/app/src/main/java/com/pyloto/entregador/presentation/corrida/disponivel/CorridaDetalhesScreen.kt`: o app passou a exibir o motivo do bloqueio/próximo do limite e desabilita o botão de aceite quando o pedido não cabe.
- `pyloto_atende/tests/integration/test_admin_entregadores_routes.py`: `test_capacity_check_filters_available_orders_and_blocks_accept_above_limit` cobre filtro da listagem, `capacidade-check` e `422` ao tentar aceitar corrida acima do limite.

**Validação executada nesta rodada:**
- `uv run pytest tests/integration/test_admin_entregadores_routes.py tests/integration/test_app_routes.py -q` em `pyloto_atende` -> `15 passed`
- `npm run typecheck` em `pyloto_admin-panel`
- `.\\gradlew.bat :app:compileProductionDebugKotlin` em `pyloto_app-parceiro`

---

## 4. Tipos de corrida e SLAs

### [x] 4.1 Estruturar modalidade da corrida como campo formal
**Descrição:** Toda corrida deve possuir modalidade explícita: comum (sem prioridade) ou prioridade.

**Critérios de aceite:**
- O pedido/corrida armazena modalidade em campo estruturado.
- Regras de coleta, entrega e despacho leem esse campo.
- O frontend e o backend tratam as modalidades de forma consistente.

**Pequenas provas desta implementação:**
- `pyloto_atende/src/pedidos/models.py`: o documento `Pedido` passou a persistir `modalidade_corrida`, `modalidade_solicitada`, `prioridade_obrigatoria`, deadlines e eventos de SLA como campos formais.
- `pyloto_atende/src/pedidos/sla.py`: `normalize_delivery_mode()`, `initialize_operational_fields()` e `hydrate_pedido_operational_state()` centralizam a modalidade operacional para criação, leitura, despacho e auditoria.
- `pyloto_app-parceiro/app/src/main/java/com/pyloto/entregador/data/corrida/remote/dto/CorridaDtos.kt`, `.../mapper/CorridaMapper.kt` e `.../presentation/corrida/disponivel/CorridaDetalhesScreen.kt`: o app passou a consumir e exibir a modalidade estruturada recebida do backend.
- `pyloto_admin-panel/src/lib/types.ts`, `pyloto_admin-panel/src/lib/api.ts` e `pyloto_admin-panel/src/app/pyloto-entrega-servicos/solicitacoes/page.tsx`: o painel administrativo passou a tipar e renderizar a modalidade/SLA de forma consistente com a API.

### [x] 4.2 Implementar SLA de corridas comuns
**Descrição:** Corridas comuns devem respeitar coleta em até 1 hora após aceitação e entrega no mesmo dia até 19h00; pedidos após 18h00 devem ser processados para o dia seguinte.

**Critérios de aceite:**
- O sistema calcula prazo máximo de coleta para corridas comuns.
- O sistema calcula prazo limite de entrega no mesmo dia, até 19h00.
- Pedidos após 18h00 não entram como entrega do mesmo dia, salvo regra posterior expressa.
- Existe sinalização de atraso de coleta e atraso de entrega.

**Pequenas provas desta implementação:**
- `pyloto_atende/src/pedidos/sla.py`: corridas comuns passaram a usar coleta em `60 min`, entrega até `19h` e processamento `D+1` quando criadas após `18h`, com alerta de aproximação e atraso persistido.
- `pyloto_atende/src/pedidos/service.py`: criação, leitura, listagem e transição de status agora hidratam/persistem `coleta_deadline_at`, `entrega_deadline_at`, `processamento_dia_seguinte` e flags de atraso.
- `pyloto_atende/tests/integration/test_admin_entregadores_routes.py`: `test_common_sla_is_rehydrated_and_after_18_goes_to_next_day` valida a janela de coleta, a virada para o próximo dia e a reidratação segura para pedidos seedados/legados.
- `pyloto_app-parceiro/app/src/main/java/com/pyloto/entregador/presentation/corrida/disponivel/CorridaDetalhesScreen.kt` e `pyloto_admin-panel/src/app/pyloto-entrega-servicos/solicitacoes/page.tsx`: app e painel passaram a mostrar os limites de coleta/entrega e o indicativo de `Janela D+1`.

### [x] 4.3 Implementar SLA de corridas prioritárias
**Descrição:** Corridas prioritárias devem respeitar coleta em até 15 minutos e entrega em até 30 minutos após coleta.

**Critérios de aceite:**
- O sistema calcula e monitora prazo máximo de coleta prioritária.
- O sistema calcula e monitora prazo máximo de entrega prioritária.
- Há alerta operacional quando o parceiro estiver próximo de estourar o SLA.
- A quebra de SLA gera evento auditável.

**Pequenas provas desta implementação:**
- `pyloto_atende/src/pedidos/sla.py`: corridas prioritárias passaram a usar coleta em `15 min`, entrega em `30 min` após coleta, alertas de aproximação e geração automática de eventos `pickup_sla_breached` e `delivery_sla_breached`.
- `pyloto_atende/src/pedidos/service.py`: ao transicionar para `aceito` e `coletado`, o backend recalcula deadlines prioritários e devolve o estado operacional hidratado para app/painel.
- `pyloto_atende/tests/integration/test_admin_entregadores_routes.py`: `test_priority_sla_breach_is_alerted_and_recorded` cobre alerta operacional e evento auditável quando a prioridade estoura o SLA.
- `pyloto_app-parceiro/app/src/main/java/com/pyloto/entregador/presentation/corrida/disponivel/CorridaDetalhesScreen.kt` e `pyloto_admin-panel/src/app/pyloto-entrega-servicos/solicitacoes/page.tsx`: os alertas e eventos de SLA passaram a ficar visíveis nas duas interfaces.

### [x] 4.4 Garantir precedência operacional da prioridade
**Descrição:** Corridas prioritárias têm precedência absoluta sobre corridas comuns em caso de conflito operacional.

**Critérios de aceite:**
- O motor de despacho/routing reconhece prioridade como classe superior.
- O sistema evita atribuir nova carga que inviabilize a prioridade.
- Em conflito de rota, a prioridade prevalece automaticamente ou exige intervenção explícita.
- Há evidência em testes de cenário misto comum + prioridade.

**Pequenas provas desta implementação:**
- `pyloto_atende/src/pedidos/service.py`: `list_disponiveis()` passou a ordenar prioridade antes de comum e a filtrar a oferta pelo aceite operacional; `aceitar_pedido()` revalida a precedência antes da atribuição.
- `pyloto_atende/src/pedidos/sla.py`: `build_operational_acceptance_check()` bloqueia nova corrida comum quando já existe prioridade em andamento e impede segunda prioridade simultânea.
- `pyloto_atende/tests/integration/test_admin_entregadores_routes.py`: `test_priority_orders_are_listed_first_when_partner_is_free` e `test_priority_precedence_blocks_new_common_until_priority_finishes` comprovam ordenação e bloqueio em cenário misto.

### [x] 4.5 Permitir acúmulo de corridas comuns apenas quando viável
**Descrição:** O parceiro pode aceitar múltiplas corridas comuns, mas somente se respeitar capacidade e SLA.

**Critérios de aceite:**
- O sistema valida capacidade física e financeira antes de adicionar nova corrida comum.
- O sistema valida impacto da nova coleta na rota e nos prazos já assumidos.
- Não é permitido aceitar múltiplas corridas comuns apenas porque “cabe no baú”; é necessário manter SLA viável.
- Existe teste de cenário com múltiplas coletas e entregas no mesmo dia.

**Pequenas provas desta implementação:**
- `pyloto_atende/src/pedidos/sla.py`: a aceitação de corridas comuns passou a validar a fila de coleta (`pickup_window`) e a carga restante até os deadlines do dia (`delivery_window`), além da capacidade física/financeira.
- `pyloto_atende/src/pedidos/service.py`: `get_order_capacity_check()` agora devolve um check unificado de capacidade + viabilidade operacional, usado tanto na listagem quanto no aceite final.
- `pyloto_atende/tests/integration/test_admin_entregadores_routes.py`: `test_common_acceptance_blocks_when_pickup_window_would_break` e `test_capacity_check_filters_available_orders_and_blocks_accept_above_limit` cobrem o cenário de múltiplas corridas comuns no mesmo dia.
- `pyloto_app-parceiro/app/src/main/java/com/pyloto/entregador/presentation/corrida/disponivel/CorridaDetalhesScreen.kt`: o app passou a explicar o motivo do bloqueio operacional antes do aceite.

### [x] 4.6 Tratar corretamente prioridade coexistindo com pedidos comuns no baú
**Descrição:** O contrato permite manter pedidos comuns no baú durante uma entrega prioritária, desde que a prioritária seja executada imediatamente e sem desvio de rota.

**Critérios de aceite:**
- A regra existe de forma explícita no sistema ou na camada de despacho.
- O sistema bloqueia comportamento incompatível com execução imediata da prioridade.
- Há definição objetiva do que caracteriza desvio incompatível, ainda que inicialmente simplificada.
- Cenários de coexistência foram testados.

**Pequenas provas desta implementação:**
- `pyloto_atende/src/pedidos/sla.py`: a regra ficou explícita via `coexistence_with_common_load`, permitindo prioridade coexistir apenas com pedidos comuns já `coletado/em_entrega` e bloqueando quando ainda há coleta comum pendente.
- `pyloto_atende/src/pedidos/service.py`: o aceite e o `capacidade-check` passaram a devolver essa decisão operacional junto com o motivo/bloqueio aplicado.
- `pyloto_atende/tests/integration/test_admin_entregadores_routes.py`: `test_priority_can_coexist_with_common_load_but_not_with_common_pickup_pending` cobre o cenário permitido e o cenário bloqueado.

**Validação executada nesta rodada:**
- `uv run pytest tests/integration/test_admin_entregadores_routes.py tests/integration/test_app_routes.py -q` em `pyloto_atende` -> `21 passed`
- `npm run typecheck` em `pyloto_admin-panel`
- `.\\gradlew.bat :app:compileProductionDebugKotlin` em `pyloto_app-parceiro`

---

## 5. Rota, geolocalização e rastreabilidade

### [x] 5.1 Manter localização atual do parceiro como estado operacional
**Descrição:** Confirmar armazenamento de latitude, longitude, timestamp da última posição e metadados úteis como precisão, velocidade e origem da coleta de localização, quando disponível.

**Critérios de aceite:**
- O documento do parceiro mantém localização atual atualizável.
- Existe campo de timestamp da última atualização.
- O sistema sabe distinguir localização recente de localização obsoleta.
- Há política para offline/stale location.

**Pequenas provas desta implementação:**
- `pyloto_atende/src/parceiros/models.py`: o parceiro passou a manter `localizacao_atual` e `rota_ativa` como estado operacional persistido.
- `pyloto_atende/src/parceiros/service.py`: `update_location_payload()` atualiza latitude/longitude/timestamp/metadados e aplica política de stale location com janela mais curta em rota ativa e mais longa em ociosidade.
- `pyloto_atende/src/http/routes/public/localizacao.py`: o tracking público agora diferencia localização disponível de localização obsoleta antes de expor o ponto ao solicitante.
- `pyloto_atende/tests/integration/test_admin_entregadores_routes.py`: `test_public_tracking_stays_available_when_partner_location_is_fresh` e `test_public_tracking_marks_location_as_unavailable_when_stale` cobrem os cenários recente x obsoleta.

### [x] 5.2 Armazenar histórico de localização durante corridas ativas
**Descrição:** Implementar ou confirmar trilha de localização quando houver pedido ativo, rota iniciada ou evento crítico operacional.

**Critérios de aceite:**
- O histórico é salvo em estrutura separada do documento principal.
- Cada ponto registra ao menos parceiro, pedido/rota quando aplicável, latitude, longitude e timestamp.
- O histórico é gravado apenas quando operacionalmente relevante, evitando custo inútil.
- Existe política mínima de retenção ou descarte.

**Pequenas provas desta implementação:**
- `pyloto_atende/src/config/constants.py`: foi criada a coleção `tracking_points` para trilha operacional separada do documento principal do parceiro.
- `pyloto_atende/src/parceiros/service.py`: o histórico só é gravado quando há rota ativa, pedido em curso ou contexto operacional relevante, já com retenção mínima configurada.
- `pyloto_atende/src/http/routes/app/localizacao.py` e `pyloto_app-parceiro/app/src/main/java/com/pyloto/entregador/data/location/repository/LocationRepositoryImpl.kt`: o app passou a enviar `pedido_id`, `route_session_id`, origem e lote de tracking compatíveis com a rota ativa.
- `pyloto_atende/tests/integration/test_admin_entregadores_routes.py`: `test_route_start_location_tracking_and_operational_events_are_persisted` valida a gravação do histórico com parceiro, pedido, sessão de rota e timestamp.

### [x] 5.3 Registrar eventos operacionais críticos
**Descrição:** Além da localização bruta, o sistema deve registrar eventos como aceite, chegada na coleta, coleta confirmada, saída, chegada no destino, entrega, cancelamento, atraso e bloqueio por capacidade.

**Critérios de aceite:**
- Existe trilha de eventos operacionais por parceiro ou corrida.
- Cada evento possui timestamp e referência à corrida/pedido.
- Eventos podem ser usados em suporte, auditoria e disputa.
- A ausência de evento crítico impede conclusão silenciosa do fluxo.

**Pequenas provas desta implementação:**
- `pyloto_atende/src/pedidos/service.py`: a corrida agora registra eventos operacionais estruturados para aceite, bloqueio por capacidade, início de rota, coleta, entrega, cancelamento e violações operacionais.
- `pyloto_atende/src/http/routes/app/corridas.py`: foi exposta a rota `POST /corridas/{pedido_id}/eventos` para registrar eventos críticos explícitos vindos do app.
- `pyloto_admin-panel/src/app/pyloto-entrega-servicos/solicitacoes/page.tsx`: o painel passou a exibir a trilha de eventos operacionais junto do tracking da corrida para suporte e auditoria.
- `pyloto_atende/tests/integration/test_admin_entregadores_routes.py`: `test_route_start_location_tracking_and_operational_events_are_persisted` garante que os eventos ficam persistidos e vinculados à corrida.

### [x] 5.4 Implementar funcionalidade “Iniciar Rota” com efeito real
**Descrição:** Confirmar que a funcionalidade prevista contratualmente não seja apenas visual; ela deve afetar cálculo de rota, acompanhamento ou prova operacional.

**Critérios de aceite:**
- Existe ação explícita de iniciar rota.
- A ação altera o estado operacional do parceiro.
- A partir desse estado, o sistema passa a acompanhar rota/localização/eventos compatíveis.
- Há evidência de uso desse estado no fluxo operacional.

**Pequenas provas desta implementação:**
- `pyloto_app-parceiro/app/src/main/java/com/pyloto/entregador/presentation/corrida/ativa/CorridaAtivaViewModel.kt`: “Iniciar rota” passou a chamar o backend, persistir contexto da rota ativa no app e disparar o tracking operacional.
- `pyloto_app-parceiro/app/src/main/java/com/pyloto/entregador/presentation/corrida/ativa/CorridaAtivaScreen.kt`: a tela deixou de ser apenas visual e agora executa as etapas reais de rota de coleta, coleta, rota de entrega e finalização.
- `pyloto_atende/src/parceiros/service.py`: o backend grava `rota_ativa`, `route_session_id`, fase operacional e atualiza esse estado ao iniciar/finalizar a rota.
- `pyloto_atende/src/pedidos/service.py`: o fluxo usa esse estado para anexar tracking, eventos e encerramento da sessão operacional da corrida.

**Validação executada nesta rodada:**
- `uv run pytest tests/integration/test_admin_entregadores_routes.py tests/integration/test_app_routes.py -q` em `pyloto_atende` -> `24 passed`
- `npm run typecheck` em `pyloto_admin-panel`
- `.\\gradlew.bat :app:compileProductionDebugKotlin` em `pyloto_app-parceiro`

---

## 6. Agenda de trabalho

### [x] 6.1 Implementar agenda com janela D+1 e D+2
**Descrição:** Confirmar a lógica contratual de abertura diária da agenda de trabalho futura.

**Critérios de aceite:**
- No dia corrente, o parceiro consegue visualizar/agendar os dias previstos pela regra vigente.
- A renovação diária da agenda é automática.
- O sistema preserva/cancela agendamentos conforme a lógica adotada.
- Há testes cobrindo virada diária da janela.

**Pequenas provas desta implementação:**
- `pyloto_atende/src/parceiros/service.py`: a agenda passou a ser calculada com janela móvel D+1/D+2, sem cron, via `agenda_trabalho`, `get_work_schedule()` e `create_work_schedule_entry()`.
- `pyloto_atende/src/http/routes/app/parceiros.py`: o app ganhou `GET /entregador/agenda`, `POST /entregador/agenda` e `POST /entregador/agenda/{agenda_id}/cancelar`.
- `pyloto_app-parceiro/app/src/main/java/com/pyloto/entregador/presentation/home/NewHomeScreen.kt`: a Home passou a exibir o card de agenda com os dois dias abertos para reserva/cancelamento.
- `pyloto_atende/tests/integration/test_admin_entregadores_routes.py`: `test_work_schedule_window_supports_d_plus_1_and_d_plus_2_booking` cobre a abertura D+1/D+2 e a criação do agendamento.

### [x] 6.2 Priorizar parceiros com agendamento prévio
**Descrição:** O sistema deve considerar o agendamento como critério de prioridade de acesso/operação.

**Critérios de aceite:**
- Existe lógica de prioridade para parceiros previamente agendados.
- A regra não depende de decisão manual ad hoc.
- Há distinção entre parceiro agendado, não agendado e remanescente.

**Pequenas provas desta implementação:**
- `pyloto_atende/src/parceiros/service.py`: a fila operacional passou a distinguir `agendado`, `nao_agendado` e `remanescente`, com liberação remanescente automática após a janela prioritária.
- `pyloto_atende/src/pedidos/service.py`: `list_disponiveis()` e `get_order_capacity_check()` agora combinam agenda + capacidade + SLA antes de listar/permitir aceite.
- `pyloto_app-parceiro/app/src/main/java/com/pyloto/entregador/presentation/home/HomeViewModel.kt`: o app consulta a agenda operacional real para refletir o bucket atual do parceiro na Home.
- `pyloto_atende/tests/integration/test_admin_entregadores_routes.py`: `test_scheduled_priority_and_residual_release_are_enforced_in_dispatch` cobre prioridade do agendado e liberação via remanescente.

### [x] 6.3 Aplicar penalidade por não comparecimento sem cancelamento prévio
**Descrição:** Parceiro que agendar e não comparecer deve sofrer restrição na abertura seguinte, conforme regra contratual.

**Critérios de aceite:**
- Existe detecção de no-show baseada em agendamento + ausência de online/atividade.
- Existe registro do no-show.
- A penalidade afeta a abertura seguinte da agenda, conforme regra definida.
- O parceiro consegue voltar a operar por vagas remanescentes, se for o caso.

**Pequenas provas desta implementação:**
- `pyloto_atende/src/parceiros/service.py`: o comparecimento passou a ser marcado por `status_online`, localizacao e aceite/inicio de corrida; ausências viram `no_show` com penalidade na abertura seguinte.
- `pyloto_admin-panel/src/components/cadastros/EntregadorAuditoriaModal.tsx`: o painel passou a exibir agenda operacional, histórico recente e penalidades ativas do parceiro.
- `pyloto_atende/tests/integration/test_admin_entregadores_routes.py`: `test_no_show_blocks_next_opening_but_keeps_remanescente_operation` valida no-show, bloqueio da abertura seguinte e manutenção da operação por remanescente.

### [x] 6.4 Implementar cancelamento sem penalidade com antecedência mínima de 12 horas
**Descrição:** O sistema deve validar a antecedência mínima de 12h para cancelamento sem penalidade.

**Critérios de aceite:**
- Cancelamento dentro do prazo não gera penalidade.
- Cancelamento fora do prazo aplica a consequência prevista.
- O sistema calcula a antecedência corretamente com base no horário agendado.
- Há evidência de teste de borda próximo às 12h.

**Pequenas provas desta implementação:**
- `pyloto_atende/src/parceiros/service.py`: `cancel_work_schedule_entry()` passou a usar uma jornada fixa `08:00-20:00` e a régua de `12h` para distinguir `cancelado` de `cancelado_tardio`.
- `pyloto_app-parceiro/app/src/main/java/com/pyloto/entregador/data/entregador/remote/dto/AgendaDtos.kt` e `EntregadorRepositoryImpl.kt`: o app ganhou contrato de API e ações reais para reservar/cancelar jornada.
- `pyloto_atende/tests/integration/test_admin_entregadores_routes.py`: `test_schedule_cancellation_respects_twelve_hour_boundary` cobre o cenário sem penalidade e a borda de cancelamento tardio.

**Validação executada nesta rodada:**
- `uv run pytest tests/integration/test_admin_entregadores_routes.py tests/integration/test_app_routes.py -q` em `pyloto_atende` -> `28 passed`
- `npm run typecheck` em `pyloto_admin-panel`
- `.\\gradlew.bat :app:compileProductionDebugKotlin` em `pyloto_app-parceiro`

---

## 7. Pagamentos, saldo e repasses

### [x] 7.1 Registrar conta/chave Pix de recebimento do parceiro
**Descrição:** O parceiro precisa ter meio de recebimento estruturado e validável.

**Critérios de aceite:**
- Existe armazenamento estruturado de conta ou chave Pix.
- Existe status de confirmação/validação dessa informação.
- Parceiro sem dado financeiro válido não pode ser liberado para repasse.

**Pequenas provas desta implementação:**
- `pyloto_atende/src/parceiros/models.py`: `PixParceiro`, `RepasseParceiro` e `FinanceiroParceiro` passaram a estruturar chave Pix, status de validação e trilha financeira do parceiro.
- `pyloto_atende/src/parceiros/service.py`: `_has_valid_payout_destination()` e `_build_finance_summary()` validam `tipo`, `chave`, aprovação do Pix e do comprovante documental antes de liberar repasse.
- `pyloto_admin-panel/src/components/cadastros/EntregadorAuditoriaModal.tsx`: o modal administrativo passou a exibir o status do Pix de recebimento e as pendências financeiras do parceiro.
- `pyloto_app-parceiro/app/src/main/java/com/pyloto/entregador/presentation/ganhos/GanhosScreen.kt`: o app passou a mostrar o status do Pix e o motivo da pendência financeira na tela de ganhos.

### [x] 7.2 Implementar regra de saldo D+1 após confirmação de entrega
**Descrição:** O contrato prevê que o pagamento do parceiro é contabilizado na confirmação da entrega e transferido em até 1 dia via Pix.

**Critérios de aceite:**
- A entrega confirmada gera crédito interno do parceiro.
- O saldo fica visível no app/painel.
- O repasse obedece a janela D+1 definida pela regra vigente.
- Há trilha de status: pendente, disponível, transferido, falhou.

**Pequenas provas desta implementação:**
- `pyloto_atende/src/pedidos/service.py`: `finalizar_pedido()` passou a chamar `register_partner_delivery_credit()` e gerar o crédito financeiro da corrida no fechamento da entrega.
- `pyloto_atende/src/parceiros/service.py`: `_refresh_partner_finance()` promove repasses de `pendente` para `disponivel` em D+1, mantém a trilha `pendente | disponivel | transferido | falhou` e recalcula os saldos operacionais.
- `pyloto_atende/src/http/routes/admin/entregadores.py`: foram criadas rotas administrativas para atualizar o status do repasse (`disponibilizar`, `transferido`, `falhou`).
- `pyloto_admin-panel/src/components/cadastros/EntregadorAuditoriaModal.tsx` e `pyloto_app-parceiro/app/src/main/java/com/pyloto/entregador/presentation/ganhos/GanhosScreen.kt`: painel e app passaram a exibir saldo pendente, disponível, transferido, falho e o extrato operacional.
- `pyloto_atende/tests/integration/test_admin_entregadores_routes.py`: `test_financial_credit_promotes_to_available_and_transfer_status_is_visible` cobre geração do crédito, promoção para disponível e marcação posterior como transferido.

### [x] 7.3 Separar financeiramente valor da corrida, taxa da plataforma e mensalidade do plano
**Descrição:** A taxa de serviço da Pyloto é cobrada do solicitante; o parceiro recebe o valor integral da corrida. A mensalidade do plano é outra lógica e não pode contaminar o cálculo da corrida.

**Critérios de aceite:**
- O cálculo do parceiro não desconta indevidamente taxa da plataforma sobre a corrida, se a regra vigente for essa.
- A mensalidade é tratada separadamente do repasse operacional.
- O extrato deixa claro origem e natureza de cada valor.
- Há testes cobrindo corrida + mensalidade + repasse.

**Pequenas provas desta implementação:**
- `pyloto_atende/src/parceiros/service.py`: `_extract_order_financials()` passou a separar `valor_corrida`, `valor_repasse` e `taxa_plataforma`; `_build_financial_statement_entries()` gera extrato distinto para `corrida_credito` e `mensalidade_plano`.
- `pyloto_atende/src/parceiros/models.py`: `RepasseParceiro` e `MensalidadePlanoParceiro` mantêm repasse operacional e cobrança de plano em estruturas independentes.
- `pyloto_app-parceiro/app/src/main/java/com/pyloto/entregador/data/ganhos/mapper/GanhosMapper.kt` e `pyloto_app-parceiro/app/src/main/java/com/pyloto/entregador/presentation/ganhos/GanhosScreen.kt`: o app passou a consumir e renderizar o extrato financeiro com natureza e origem de cada lançamento.
- `pyloto_admin-panel/src/components/cadastros/EntregadorAuditoriaModal.tsx`: o painel passou a separar visualmente repasses, mensalidades e histórico financeiro.
- `pyloto_atende/tests/integration/test_admin_entregadores_routes.py`: `test_financial_credit_promotes_to_available_and_transfer_status_is_visible` e `test_overdue_plan_suspends_partner_but_keeps_active_order_until_payment` cobrem corrida, repasse e mensalidade no mesmo fluxo financeiro.

### [x] 7.4 Implementar suspensão por inadimplência do plano quando aplicável
**Descrição:** O contrato prevê suspensão do acesso em caso de atraso no pagamento da mensalidade e até descredenciamento em hipóteses mais graves.

**Critérios de aceite:**
- Existe controle de vencimento da mensalidade.
- Existe regra de suspensão por inadimplência.
- Existe histórico de cobranças, suspensão e reativação.
- A suspensão não afeta silenciosamente corridas já em andamento sem tratamento definido.

**Pequenas provas desta implementação:**
- `pyloto_atende/src/parceiros/service.py`: `_refresh_partner_finance()` passou a gerar mensalidades a partir do 3º mês do plano, marcar competências vencidas e ativar `suspensao_ativa` com histórico financeiro.
- `pyloto_atende/src/parceiros/service.py`: `_build_operational_state_update()`, `ensure_operational_access()` e `ensure_dispatch_access()` aplicam suspensão financeira para novas ofertas sem derrubar silenciosamente corridas já em andamento.
- `pyloto_atende/src/http/routes/admin/entregadores.py`: foi criada a rota administrativa para registrar pagamento da mensalidade e reativar o parceiro após regularização.
- `pyloto_admin-panel/src/components/cadastros/EntregadorAuditoriaModal.tsx` e `pyloto_admin-panel/src/components/cadastros/EntregadoresTable.tsx`: o painel passou a sinalizar `financial_hold`, exibir mensalidades em aberto e permitir baixa administrativa da cobrança.
- `pyloto_atende/tests/integration/test_admin_entregadores_routes.py`: `test_overdue_plan_suspends_partner_but_keeps_active_order_until_payment` cobre vencimento, suspensão, preservação da corrida ativa e reativação após pagamento.

**Validação executada nesta rodada:**
- `uv run pytest tests/integration/test_admin_entregadores_routes.py tests/integration/test_app_routes.py -q` em `pyloto_atende` -> `31 passed`
- `npm run typecheck` em `pyloto_admin-panel`
- `.\\gradlew.bat :app:compileProductionDebugKotlin` em `pyloto_app-parceiro`

---

## 8. Penalidades, qualidade e reputação

### [x] 8.1 Implementar gradação mínima de penalidades
**Descrição:** O sistema deve suportar advertência, suspensão temporária, suspensão do agendamento prioritário e descredenciamento.

**Critérios de aceite:**
- Há estrutura para registrar penalidade e motivo.
- Há distinção entre tipos e gravidade.
- Penalidades geram efeitos operacionais reais no sistema.
- Existe histórico auditável por parceiro.

**Pequenas provas desta implementação:**
- `pyloto_atende/src/parceiros/models.py`: `PenalidadeOperacionalParceiro` foi adicionada ao parceiro com `tipo`, `gravidade`, `status`, motivo, referências e trilha completa de criação/resolução.
- `pyloto_atende/src/parceiros/service.py`: `create_operational_penalty_admin()`, `resolve_operational_penalty_admin()` e `_build_operational_state_update()` passaram a suportar `advertencia`, `suspensao_temporaria`, `suspensao_agendamento_prioritario` e `descredenciamento`, com efeito real sobre bloqueio operacional, agenda e status cadastral.
- `pyloto_atende/src/http/routes/admin/entregadores.py` e `pyloto_admin-panel/src/components/cadastros/EntregadorAuditoriaModal.tsx`: o painel ganhou fluxo administrativo para registrar e resolver penalidades, inclusive com prazo para suspensão temporária e vínculo opcional com incidente/pedido.
- `pyloto_admin-panel/src/components/cadastros/EntregadoresTable.tsx`: a listagem passou a destacar descredenciamento, suspensão por penalidade, suspensão do agendamento prioritário e distribuição rebaixada.
- `pyloto_atende/tests/integration/test_admin_entregadores_routes.py`: `test_manual_operational_penalties_apply_real_effects` cobre advertência sem bloqueio, suspensão temporária bloqueando corridas, suspensão do agendamento prioritário bloqueando reserva de jornada e descredenciamento tornando o parceiro inativo.

### [x] 8.2 Implementar hipóteses de bloqueio/descredenciamento imediato
**Descrição:** Fraude, uso ilícito, violação grave de prazo, dano doloso, uso indevido de dados e adulteração de informações devem ter resposta operacional correspondente.

**Critérios de aceite:**
- As hipóteses críticas podem ser registradas como incidente grave.
- O sistema permite bloqueio imediato.
- O bloqueio é auditável e reversível apenas por fluxo administrativo apropriado.
- Há rastreabilidade da decisão de bloqueio.

**Pequenas provas desta implementação:**
- `pyloto_atende/src/parceiros/service.py`: `CRITICAL_INCIDENT_TYPES` passou a incluir fraude, uso ilícito, violação grave de prazo, dano doloso, uso indevido de dados e adulteração; `register_operational_incident_admin()` agora aceita `acao_imediata` e cria penalidade automática ligada ao incidente crítico.
- `pyloto_atende/src/http/routes/admin/entregadores.py` e `pyloto_admin-panel/src/components/cadastros/EntregadorAuditoriaModal.tsx`: o fluxo administrativo ganhou seleção de incidente crítico com ação imediata (`advertencia`, `suspensao_temporaria` ou `descredenciamento`) e resolução posterior em trilha separada.
- `pyloto_atende/tests/integration/test_admin_entregadores_routes.py`: `test_critical_incident_creates_immediate_block_and_requires_admin_resolution` cobre incidente crítico com descredenciamento imediato, bloqueio real do parceiro e reversão apenas depois de resolver incidente e penalidade.

### [x] 8.3 Tratar recusa reiterada de corridas como fator de reputação/distribuição
**Descrição:** O contrato prevê rebaixamento de prioridade por recusa injustificada e reiterada de corridas.

**Critérios de aceite:**
- O sistema registra aceite, recusa e motivo da recusa.
- É possível distinguir recusa legítima por capacidade/indisponibilidade de recusa injustificada.
- Existe regra de impacto na distribuição futura.
- Essa lógica não depende apenas de análise manual.

**Pequenas provas desta implementação:**
- `pyloto_atende/src/pedidos/service.py`: `recusar_pedido()` passou a registrar categoria, motivo, classificação justificável/injustificada, impedir nova oferta da mesma corrida ao mesmo parceiro e gerar evento operacional `pedido_recusado`.
- `pyloto_atende/src/parceiros/service.py`: `register_partner_order_acceptance()`, `register_partner_order_refusal()` e `_distribution_reputation_context()` passaram a consolidar aceites/recusas, manter histórico de recusas e aplicar rebaixamento automático de distribuição após 3 recusas injustificadas na janela de 7 dias.
- `pyloto_app-parceiro/app/src/main/java/com/pyloto/entregador/core/network/ApiService.kt`, `.../data/corrida/repository/CorridaRepositoryImpl.kt`, `.../presentation/corrida/disponivel/CorridaDetalhesViewModel.kt` e `.../CorridaDetalhesScreen.kt`: o app deixou de tratar “Recusar” como simples voltar e passou a enviar categoria + motivo reais para o backend.
- `pyloto_admin-panel/src/components/cadastros/EntregadorAuditoriaModal.tsx` e `pyloto_admin-panel/src/components/cadastros/EntregadoresTable.tsx`: o painel passou a exibir histórico de recusas, janela de recusas injustificadas, nível de distribuição e a consequência operacional dessa reputação.
- `pyloto_atende/tests/integration/test_admin_entregadores_routes.py`: `test_unjustified_refusals_reduce_distribution_priority_but_legitimate_refusals_do_not` cobre a distinção entre recusa legítima e injustificada, o bloqueio temporário da distribuição futura e a liberação posterior.

**Validação executada nesta rodada:**
- `uv run pytest tests/integration/test_admin_entregadores_routes.py tests/integration/test_app_routes.py -q` em `pyloto_atende` -> `34 passed`
- `npm run typecheck` em `pyloto_admin-panel`
- `.\\gradlew.bat :app:compileProductionDebugKotlin` em `pyloto_app-parceiro`

---

## 9. LGPD, segurança e retenção

### [x] 9.1 Registrar base de consentimento/uso de dados do parceiro
**Descrição:** Confirmar que o cadastro do parceiro contém aceite para tratamento de dados nas finalidades operacionais previstas.

**Critérios de aceite:**
- Existe registro do aceite LGPD ou base legal operacional equivalente, conforme desenho jurídico adotado.
- O sistema vincula esse aceite à versão do texto aplicável.
- Há data/hora do registro.

**Pequenas provas desta implementação:**
- `pyloto_atende/src/parceiros/models.py`: `ConsentimentoDadosParceiro` foi adicionado ao modelo persistido do parceiro.
- `pyloto_atende/src/parceiros/service.py`: `create_entregador_admin()` passou a registrar `base_legal=execucao_de_contrato`, `texto_versao`, finalidades operacionais, ator e timestamp do registro.
- `pyloto_atende/src/http/routes/admin/entregadores.py` e `pyloto_admin-panel/src/components/cadastros/EntregadorCadastroFlow.tsx`: o cadastro administrativo ganhou o campo `tratamento_dados_texto_versao` para versionar o texto jurídico aplicável no momento do credenciamento.
- `pyloto_admin-panel/src/components/cadastros/EntregadorAuditoriaModal.tsx`: o painel passou a exibir base legal, versão do texto, finalidades, meio e data do registro.
- `pyloto_atende/tests/integration/test_admin_entregadores_routes.py`: `test_admin_create_entregador_records_data_consent_and_retention_policy` valida persistência e leitura do consentimento/base legal.

### [x] 9.2 Restringir uso dos dados do solicitante ao estritamente necessário
**Descrição:** O parceiro só deve acessar dados do cliente necessários para executar a corrida.

**Critérios de aceite:**
- O app do parceiro não expõe dados além do necessário.
- Não há campos desnecessários persistidos localmente sem justificativa.
- Existe diretriz técnica para evitar armazenamento indevido de dados do solicitante.
- Há mecanismo mínimo para apurar uso indevido de dados.

**Pequenas provas desta implementação:**
- `pyloto_atende/src/pedidos/service.py`: `build_partner_order_payload()` passou a remover `wa_id`, cortar `dados` para um payload mínimo, mascarar nome/telefone do solicitante e anexar diretriz técnica de uso restrito.
- `pyloto_atende/src/http/routes/app/corridas.py` e `pyloto_atende/src/http/routes/app/chat.py`: listagem, detalhe, aceite, eventos e chat passaram a responder com dados minimizados e registrar auditoria em `data_access_audits`.
- `pyloto_app-parceiro/app/src/main/java/com/pyloto/entregador/data/corrida/mapper/CorridaMapper.kt` e `.../core/database/entity/CorridaEntity.kt`: o app deixou de persistir telefone do solicitante no cache local e não volta a extrair telefone bruto de `dados`.
- `pyloto_app-parceiro/app/src/main/java/com/pyloto/entregador/presentation/corrida/disponivel/CorridaDetalhesScreen.kt`: a UI passou a explicitar que o contato do solicitante é minimizado e deve seguir os canais oficiais da Pyloto.
- `pyloto_atende/tests/integration/test_admin_entregadores_routes.py`: `test_partner_order_payload_is_minimized_and_access_is_audited` cobre mascaramento, remoção de campos brutos e trilha mínima de acesso.

### [x] 9.3 Definir política de retenção para localização, eventos e documentos
**Descrição:** O sistema não deve armazenar indefinidamente tudo sem critério.

**Critérios de aceite:**
- Existe definição formal de retenção por tipo de dado.
- Localização histórica possui prazo ou regra de descarte/arquivamento.
- Documentos e eventos têm tratamento compatível com exigência operacional e jurídica.
- A política é aplicável tecnicamente, não apenas textual.

**Pequenas provas desta implementação:**
- `pyloto_atende/src/parceiros/service.py`: `_build_retention_policy_snapshot()` formalizou a política com versão, prazo, estratégia, finalidade e aplicação técnica para tracking, eventos, documentos e auditoria de acesso.
- `pyloto_atende/src/parceiros/service.py`: documentos e histórico documental passaram a carregar `retention_until_at`; o tracking já mantido no parceiro continua com prazo explícito de 30 dias.
- `pyloto_atende/src/pedidos/service.py`: eventos operacionais e auditorias de acesso passaram a gravar `retention_until_at` no documento persistido.
- `pyloto_admin-panel/src/components/cadastros/EntregadorAuditoriaModal.tsx`: o painel passou a exibir a política de retenção vigente e amostras reais de retenção documental.
- `pyloto_atende/tests/integration/test_admin_entregadores_routes.py`: `test_document_and_operational_event_retention_metadata_is_persisted` valida retenção em documentos e eventos operacionais.

**Validação executada nesta rodada:**
- `uv run pytest tests/integration/test_admin_entregadores_routes.py tests/integration/test_app_routes.py -q` em `pyloto_atende` -> `37 passed`
- `npm run typecheck` em `pyloto_admin-panel`
- `.\\gradlew.bat :app:compileProductionDebugKotlin` em `pyloto_app-parceiro`

---

## 10. Auditoria, observabilidade e testes

### [x] 10.1 Criar trilha de auditoria mínima para ações críticas
**Descrição:** Toda ação relevante deve poder ser reconstruída depois: cadastro, aprovação documental, aceite de contrato, aceite/recusa de corrida, coleta, entrega, bloqueio, alteração de veículo, alteração de plano e repasse.

**Critérios de aceite:**
- Existe trilha de auditoria com timestamp e ator responsável.
- A trilha cobre operações administrativas e operacionais.
- O histórico não depende apenas de logs efêmeros de aplicação.

**Pequenas provas desta implementação:**
- `pyloto_atende/src/config/constants.py`: foi criada a coleção persistida `audit_trails`, separada de logs efêmeros.
- `pyloto_atende/src/parceiros/service.py`: `record_partner_audit_trail()` e `list_partner_audit_trail()` passaram a registrar/expor cadastro, revisão documental, assinatura digital, incidentes, penalidades, atualização de perfil e repasses.
- `pyloto_atende/src/pedidos/service.py`: aceite, recusa, início, coleta, entrega e cancelamento de corrida agora também geram trilha persistida por parceiro.
- `pyloto_admin-panel/src/lib/api.ts`, `pyloto_admin-panel/src/lib/types.ts` e `pyloto_admin-panel/src/components/cadastros/EntregadorAuditoriaModal.tsx`: o painel passou a consumir e exibir a auditoria crítica consolidada no detalhe do entregador.
- `pyloto_atende/tests/integration/test_admin_entregadores_routes.py`: `test_admin_detail_exposes_persisted_critical_audit_trail` valida timestamp, ator, source e ações administrativas + operacionais reais no detalhe do parceiro.

### [x] 10.2 Testar cenários mínimos obrigatórios de operação
**Descrição:** Não basta modelar; é necessário provar que a lógica resiste aos cenários mais prováveis e perigosos.

**Critérios de aceite:**
- Existem testes cobrindo corrida comum simples.
- Existem testes cobrindo múltiplas corridas comuns dentro da capacidade.
- Existem testes cobrindo bloqueio por excesso de volume/peso/valor.
- Existem testes cobrindo coexistência de comum + prioridade.
- Existem testes cobrindo atraso, cancelamento, no-show de agenda e bloqueio por documento vencido.

**Pequenas provas desta implementação:**
- `pyloto_atende/tests/integration/test_admin_entregadores_routes.py`: `test_route_start_location_tracking_and_operational_events_are_persisted` cobre a corrida comum simples ponta a ponta.
- `pyloto_atende/tests/integration/test_admin_entregadores_routes.py`: `test_multiple_common_orders_within_capacity_can_be_reserved_and_carried` cobre múltiplas corridas comuns dentro da capacidade do baú.
- `pyloto_atende/tests/integration/test_admin_entregadores_routes.py`: `test_capacity_check_filters_available_orders_and_blocks_accept_above_limit` cobre bloqueio por excesso de volume/peso/valor.
- `pyloto_atende/tests/integration/test_admin_entregadores_routes.py`: `test_priority_can_coexist_with_common_load_but_not_with_common_pickup_pending` cobre coexistência de comum + prioridade.
- `pyloto_atende/tests/integration/test_admin_entregadores_routes.py`: `test_priority_sla_breach_is_alerted_and_recorded`, `test_schedule_cancellation_respects_twelve_hour_boundary`, `test_no_show_blocks_next_opening_but_keeps_remanescente_operation` e `test_partner_operational_access_is_blocked_by_expired_document` cobrem atraso, cancelamento, no-show e bloqueio por documento vencido.

### [x] 10.3 Validar coerência entre contrato, sistema e interface
**Descrição:** O maior risco aqui é regra existir no contrato e não existir no produto, ou existir de forma divergente.

**Critérios de aceite:**
- Existe revisão cruzada entre contrato, backend, frontend e documentação interna.
- Toda divergência relevante foi registrada e resolvida.
- Não há texto contratual prometendo funcionalidade inexistente como se já estivesse pronta.
- Não há regra crítica em produção sem lastro contratual/documental adequado.

**Pequenas provas desta implementação:**
- `pyloto_app-parceiro/docs/REVISAO_CRUZADA_CONTRATO_SISTEMA_2026-03.md`: consolidou a revisão entre contrato padrão, backend, painel, app e documentação interna.
- `pyloto_app-parceiro/docs/ARQUITETURA-COMUNICACAO.md`: a seção de cadastro foi corrigida para o fluxo vigente, administrativo + dupla assinatura.
- `pyloto_app-parceiro/docs/API.md`: `/auth/register` passou a constar como `403 FORBIDDEN`, o onboarding contratual foi documentado e os payloads de corrida foram atualizados para dados minimizados, sem `wa_id`.
- `pyloto_app-parceiro/docs/SETUP.md` e `pyloto_app-parceiro/docs/TODO_integracao_concluido.md`: a camada `auth` deixou de ser descrita como fluxo de registro e o documento histórico passou a indicar que não é fonte de verdade para autocadastro.
- `pyloto_admin-panel/public/contracts/PylotoEntregadores.docx`, `Cadastro_Entregador.md`, `Planos_parceiros` e `pyloto_atende/regras_capacidade_bau.json`: a revisão cruzada confirmou o mesmo lastro documental para cadastro presencial, dupla assinatura, tabela do baú, SLA e agenda operacional.

**Validação executada nesta rodada:**
- `uv run pytest tests/integration/test_admin_entregadores_routes.py tests/integration/test_app_routes.py -q` em `pyloto_atende` -> `39 passed`
- `npm run typecheck` em `pyloto_admin-panel`
- `.\\gradlew.bat :app:compileProductionDebugKotlin` em `pyloto_app-parceiro`

---

## 11. Pendências críticas de alinhamento antes de produção

### [x] 11.1 Definir valor máximo real por litragem do baú
**Descrição:** Resolver formalmente o conflito entre regras mencionadas informalmente e a tabela contratual anexada.

**Critérios de aceite:**
- Existe tabela final aprovada.
- Contrato e sistema usam a mesma tabela.
- Time sabe qual regra está valendo.

**Pequenas provas desta implementação:**
- `pyloto_app-parceiro/docs/ALINHAMENTO_PRE_PRODUCAO_2026-03.md`: consolidou a tabela final aprovada de 80L/120L/135L/150L como matriz operacional unica.
- `pyloto_atende/regras_capacidade_bau.json` e `pyloto_atende/src/parceiros/bau_policy.py`: o backend segue a mesma tabela versionada `2026.03-contract-annex`.
- `Cadastro_Entregador.md` e `pyloto_admin-panel/public/contracts/PylotoEntregadores.docx`: o lastro documental ficou alinhado com os mesmos valores maximos de volume/peso/valor.

### [x] 11.2 Definir se a lógica de cálculo de corrida ficará em anexo contratual/documentação técnica versionada
**Descrição:** O contrato sugere transparência maior sobre cálculo das corridas. Isso afeta suporte, disputas e previsibilidade ao parceiro.

**Critérios de aceite:**
- Foi decidido se haverá anexo contratual, política pública ou documentação interna versionada.
- A fórmula vigente está documentada de forma inequívoca.
- O sistema usa a mesma versão documentada.

**Pequenas provas desta implementação:**
- `POLITICA_PRECIFICACAO_ENTREGAS_2026-03.md`: formalizou a decisao de governanca por documentacao tecnica interna versionada.
- `pyloto_atende/regras_precos_entregas.json`: a versao oficial passou a ser `2026.03`, com `fonte_oficial` e metadados de governanca.
- `pyloto_atende/src/flows/pricing_service.py`: o backend agora devolve `politica_precificacao_versao`, `politica_precificacao_fonte`, `taxa_plataforma_percentual`, `modalidade_multiplicador` e `ajuste_climatico_percentual`.
- `pyloto_atende/tests/unit/test_pricing_service.py`: a suite unitária passou a validar a versao `2026.03` e os metadados da politica aplicada.

### [x] 11.3 Confirmar se “disponível” significa apenas livre para receber corridas ou se depende de agenda, documentos, capacidade e localização recente
**Descrição:** O campo disponível costuma ser mal usado. Precisa de definição operacional única.

**Critérios de aceite:**
- Existe definição única de disponibilidade.
- Backend e frontend usam essa definição.
- Não há despacho baseado em campo ambíguo.

**Pequenas provas desta implementação:**
- `pyloto_app-parceiro/docs/ALINHAMENTO_PRE_PRODUCAO_2026-03.md`: definiu oficialmente a diferenca entre `disponivel` manual, `dispatch_eligible` e restricoes por pedido.
- `pyloto_atende/src/parceiros/service.py`: `_build_dispatch_status()` virou a fonte unica da elegibilidade de despacho e `ensure_dispatch_access()` passou a usar essa definicao.
- `pyloto_admin-panel/src/components/cadastros/EntregadoresTable.tsx` e `pyloto_admin-panel/src/components/cadastros/EntregadorAuditoriaModal.tsx`: o frontend passou a exibir a elegibilidade real de despacho separada do toggle manual.
- `pyloto_atende/tests/integration/test_admin_entregadores_routes.py`: `test_dispatch_access_requires_manual_online_status_after_enablement` prova que parceiro habilitado, mas pausado, nao entra no fluxo de despacho.

### [x] 11.4 Confirmar política de frequência de rastreamento e retenção de localização
**Descrição:** Sem isso você ou grava pouco e perde prova operacional, ou grava demais e cria custo/passivo desnecessário.

**Critérios de aceite:**
- Existe regra mínima de frequência/eventos de gravação.
- Existe política de retenção compatível com suporte, disputa e LGPD.
- A implementação segue a política definida.

**Pequenas provas desta implementação:**
- `pyloto_app-parceiro/docs/ALINHAMENTO_PRE_PRODUCAO_2026-03.md`: consolidou a politica oficial de tracking e retencao.
- `pyloto_app-parceiro/app/src/main/java/com/pyloto/entregador/core/location/LocationService.kt`: o cliente segue intervalo alvo de `10s`, `fastest interval` de `5s`, `batch max delay` de `15s` e distancia minima de `10m`.
- `pyloto_atende/src/parceiros/service.py`: o backend passou a expor `tracking_policy`, manter `30 dias` de retencao e recusar gravacao historica redundante antes de `10s` no mesmo contexto operacional.
- `pyloto_atende/tests/integration/test_admin_entregadores_routes.py`: `test_tracking_history_respects_min_record_interval_and_policy_snapshot` garante a politica de frequencia e o snapshot exposto no detalhe administrativo.

**Validação executada nesta rodada:**
- `uv run pytest tests/unit/test_pricing_service.py tests/integration/test_admin_entregadores_routes.py tests/integration/test_app_routes.py -q` em `pyloto_atende` -> `50 passed`
- `npm run typecheck` em `pyloto_admin-panel`
- `.\\gradlew.bat :app:compileProductionDebugKotlin` em `pyloto_app-parceiro`

---

## 12. Definição de pronto mínimo

Uma tarefa deste documento **só pode ser marcada como concluída** quando houver, cumulativamente:

- implementação real **ou** confirmação técnica objetiva de que a lógica já existe;
- evidência verificável no código, banco, painel ou fluxo do app;
- critério de aceite atendido integralmente;
- ausência de divergência conhecida entre contrato, sistema e operação real.

Se existir regra “mais ou menos”, mock, campo solto sem uso real, lógica manual fora do sistema ou divergência documental, **não está concluído**.

### [x] 12.1 Executar auditoria efetiva de aceite do escopo 1 a 11
**Descrição:** Validar de forma cumulativa se as tarefas já marcadas como concluídas possuem implementação real, evidência verificável, testes executados e coerência documental ativa.

**Resultado da auditoria de 24/03/2026:**
- O escopo parceiro-entrega das tarefas `1` a `11` foi **confirmado como aceito** nesta rodada.
- A validação formal ficou consolidada em `pyloto_app-parceiro/docs/VALIDACAO_ACEITE_REAL_PRONTO_MINIMO_2026-03-24.md`.
- `uv run pytest tests/unit/test_pricing_service.py tests/integration/test_admin_entregadores_routes.py tests/integration/test_app_routes.py -q` em `pyloto_atende` -> `50 passed`.
- `npm run typecheck` em `pyloto_admin-panel` -> sem erros.
- `.\\gradlew.bat :app:compileProductionDebugKotlin` em `pyloto_app-parceiro` -> build bem-sucedido.
- A suíte completa do backend (`uv run pytest -q`) **não** está totalmente verde: `67 failed, 212 passed`; as falhas remanescentes concentram-se em módulos legados fora do escopo deste TODO (`flow_e2e`, `public_routes`, `exchange`, `fsm`, `message_handler`, `security`, `types`).
- A varredura documental não encontrou divergência ativa bloqueante no fluxo vigente; referências antigas a `POST /auth/register` permanecem apenas no documento histórico `docs/TODO_integracao_concluido.md`, que já traz aviso explícito de que não é fonte de verdade.
