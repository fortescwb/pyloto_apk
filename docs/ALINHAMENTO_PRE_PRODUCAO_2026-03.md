# Alinhamento Pre-Producao 2026-03

Data da consolidacao: 24/03/2026

## 11.1 Tabela final do bau

Tabela operacional aprovada para uso no sistema, contrato e documentacao interna:

| Bau | Volume maximo | Peso maximo | Valor maximo |
|---|---:|---:|---:|
| 80L | 80L | 20kg | R$ 1.500,00 |
| 120L | 120L | 30kg | R$ 2.000,00 |
| 135L | 135L | 40kg | R$ 2.500,00 |
| 150L | 150L | 50kg | R$ 3.000,00 |

Fontes de verdade:

- contrato `PylotoEntregadores.docx`, anexo operacional;
- `Cadastro_Entregador.md`;
- `pyloto_atende/regras_capacidade_bau.json`;
- `src/parceiros/bau_policy.py`.

Regra vigente:

- nao existe tabela paralela valida fora desta matriz.
- qualquer mudanca futura exige nova versao documental + nova versao no JSON.

## 11.2 Onde fica a logica de calculo da corrida

Decisao oficial:

- a logica vigente de precificacao fica em documentacao tecnica interna versionada, nao em anexo contratual detalhando formula fechada por item.
- a fonte humana oficial e [POLITICA_PRECIFICACAO_ENTREGAS_2026-03.md](/c:/Users/jamis/Documents/Projetos/Pyloto/POLITICA_PRECIFICACAO_ENTREGAS_2026-03.md).
- a fonte tecnica executavel e `pyloto_atende/regras_precos_entregas.json`, versao `2026.03`.

## 11.3 Definicao unica de disponibilidade

O termo `disponivel` sozinho passa a ter significado restrito:

- `disponivel` no parceiro = toggle manual do app para receber novas ofertas.
- `online` = sessao ativa/estado online do parceiro no app.
- `dispatch_eligible` = parceiro apto a entrar no fluxo de despacho.

Definicao de `dispatch_eligible`:

- toggle manual ativo (`disponivel = true`);
- parceiro online;
- pronto para operacao;
- sem suspensao financeira ativa.

Importante:

- agenda, capacidade do bau e SLA nao sao atributos permanentes do parceiro; eles sao restricoes por pedido/oferta.
- localizacao fresca tambem nao e pre-requisito para abrir oferta inicial no app; ela e exigencia de prova operacional e tracking durante rota.

Portanto:

- disponibilidade manual != elegibilidade de despacho;
- elegibilidade de despacho != aceite garantido da corrida;
- aceite de corrida depende cumulativamente de:
  - `dispatch_eligible`;
  - bucket de agenda liberado para aquele pedido;
  - capacidade do bau;
  - viabilidade operacional/SLA da modalidade.

## 11.4 Politica oficial de rastreamento e retencao

Politica tecnica vigente:

- coleta no app com intervalo alvo de `10s`;
- `fastest interval` de `5s`;
- `batch max delay` de `15s`;
- distancia minima de `10m`;
- backend atualiza localizacao corrente a cada envio valido;
- backend grava historico somente quando houver contexto operacional:
  - rota ativa;
  - pedido ativo;
  - `pedido_id` explicito no payload;
- backend nao grava ponto historico novo no mesmo contexto antes de `10s`;
- localizacao fica stale apos:
  - `120s` em rota ativa;
  - `600s` em ociosidade;
- retencao dos pontos historicos: `30 dias`.

Fontes de verdade:

- `pyloto_app-parceiro/app/src/main/java/com/pyloto/entregador/core/location/LocationService.kt`
- `pyloto_atende/src/parceiros/service.py`
- `pyloto_admin-panel` no detalhe administrativo do entregador

## Resultado operacional consolidado

- o time passa a ter uma unica tabela do bau;
- a precificacao tem fonte versionada e identificavel;
- `disponivel` deixa de ser termo ambiguo no fluxo de despacho;
- a politica de tracking e retencao deixa de depender de inferencia informal.
