# Criterios de Pronto - Publicacao de Build Externa

## Gate 1 - Integracao real

- Sem bypass hardcoded ativo em runtime normal
- Base URL alinhada ao backend real
- Fluxos de `auth`, `perfil`, `corridas`, `chat` e `notificacoes` funcionando

## Gate 2 - Contrato e dados

- DTOs compativeis com o contrato atual do backend
- Testes de parsing/contrato executados com sucesso
- Indices Firestore necessarios em estado `READY`
- Seed minimo funcional disponivel para homologacao

## Gate 3 - Qualidade tecnica

- `:app:compileProductionDebugKotlin` com sucesso
- `:app:testProductionDebugUnitTest` (ou suite critica definida) com sucesso
- `:app:assembleProductionDebug` com sucesso
- Sem erro bloqueante conhecido em toolchain

## Gate 4 - Observabilidade

- Logs HTTP detalhados apenas em debug
- Correlacao de chamadas por `X-Trace-Id`
- Sem exposicao de tokens/sessao em logs
- Falhas de rede e parsing registradas de forma acionavel

## Gate 5 - Operacao

- Checklist de homologacao concluido
- Principais riscos mapeados no TODO
- Escopo pendente nao bloqueante registrado com data e owner

## Decisao final

A build pode ser promovida para distribuicao externa apenas quando todos os gates acima estiverem aprovados.

