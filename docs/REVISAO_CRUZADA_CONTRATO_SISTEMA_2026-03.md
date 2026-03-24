# Revisao Cruzada Contrato x Sistema x Interface

Data da revisao: 24/03/2026

## Fontes revisadas

- Contrato padrao `PylotoEntregadores.docx` versao `2026.03`
- `Cadastro_Entregador.md`
- `Planos_parceiros`
- Backend `pyloto_atende`
- Painel `pyloto_admin-panel`
- App `pyloto_app-parceiro`

## Divergencias relevantes encontradas e resolvidas

### 1. Cadastro do parceiro

- Contrato vigente: clausula 2.1 determina comparecimento ao escritorio com documentos originais.
- Sistema vigente: cadastro realizado pelo `pyloto_admin-panel`, com contrato preenchido automaticamente e anexos documentais.
- Interface vigente: o app nao oferece mais autocadastro.

Resolucao aplicada:

- `docs/ARQUITETURA-COMUNICACAO.md` passou a refletir cadastro exclusivamente administrativo.
- `docs/API.md` passou a documentar `POST /auth/register` como `403 FORBIDDEN`.
- `docs/SETUP.md` deixou de descrever a camada `auth` como fluxo de registro.

### 2. Dupla assinatura contratual

- Contrato vigente: clausula 2.2 exige assinatura fisica no cadastro e assinatura digital posterior via Gov.br.
- Sistema vigente: o cadastro nasce com `requires_digital_contract_signature = true`, o parceiro baixa a via digital e envia a referencia assinada em `POST /entregador/contrato/assinatura-digital`.
- Interface vigente: o app bloqueia Home e operacao ate concluir a assinatura digital.

Conclusao:

- Contrato, backend, painel e app estao alinhados no fluxo de dupla confirmacao.

### 3. Documentos, bloqueios e elegibilidade

- Contrato vigente: CNH, CPF, comprovante de residencia, CRLV, seguro quando aplicavel, bau minimo e dados bancarios.
- Sistema vigente: cada documento possui status proprio, historico, revisao, validade, evidencias e bloqueio operacional por pendencia/rejeicao/vencimento.
- Interface vigente: o painel exige os anexos no credenciamento e o app exibe onboarding/bloqueios correspondentes.

Conclusao:

- Nao ha regra documental critica em producao sem lastro contratual ou documental interno.

### 4. Capacidade do bau, SLA e agenda

- Lastro documental:
  - `Cadastro_Entregador.md` e anexo contratual fixam a tabela 80L/120L/135L/150L.
  - `Planos_parceiros` registra comum ate 1h para coleta e entrega no mesmo dia ate 19h; prioridade em 15 min para coleta e 30 min apos coleta para entrega.
  - `Planos_parceiros` tambem registra agenda D+1 e D+2.
- Sistema vigente:
  - `regras_capacidade_bau.json` e `src/parceiros/bau_policy.py` usam a mesma tabela.
  - `src/pedidos/sla.py` aplica janelas de comum/prioridade.
  - `src/parceiros/service.py` aplica agenda D+1/D+2, cancelamento com regua de 12h e no-show.
- Interface vigente:
  - app e painel exibem capacidade, SLA, agenda e bloqueios operacionais.

Conclusao:

- As regras operacionais criticas hoje em uso possuem suporte contratual ou documental interno versionado.

### 5. Privacidade e minimizacao de dados

- Decisao arquitetural: comunicacao do parceiro deve usar canais oficiais da Pyloto, sem exposicao indevida do contato bruto do solicitante.
- Sistema vigente: payloads de corrida entregam apenas `cliente_nome` e `cliente_telefone` mascarados, sem `wa_id` no app, com trilha minima de acesso.
- Interface vigente: app orienta uso de canais oficiais; painel exibe governanca LGPD e retencao.

Conclusao:

- O material vigente nao promete ao parceiro acesso irrestrito a dados brutos do solicitante.

## Resultado final da revisao

- Nao foi mantido texto corrente prometendo autocadastro no app.
- Nao foi mantido contrato prometendo funcionalidade operacional inexistente como se ja estivesse pronta.
- As divergencias encontradas nesta rodada foram documentadas e corrigidas nos artefatos de interface e documentacao tecnica.

## Observacao de governanca

- `docs/TODO_integracao_concluido.md` permanece como registro historico de fases antigas e nao deve ser usado como fonte de verdade para o fluxo atual de cadastro/autenticacao.
