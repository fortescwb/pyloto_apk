# ADR 002: Estratégia de GPS e Localização

## Status
Aceito

## Contexto
O app precisa rastrear a localização do entregador em tempo real durante as entregas, com:
- Precisão alta para navegação
- Eficiência de bateria para uso prolongado
- Resiliência a falhas de conexão

## Decisão
Utilizar **FusedLocationProviderClient** com Foreground Service.

### Parâmetros de Localização
| Parâmetro | Valor | Justificativa |
|-----------|-------|---------------|
| Intervalo | 10s | Equilíbrio precisão/bateria |
| Intervalo mínimo | 5s | Movimentação rápida |
| Delay máximo | 15s | Batch para economia |
| Distância mínima | 10m | Evitar updates parado |
| Prioridade | HIGH_ACCURACY | GPS + WiFi + Cell |

### Estratégia de Sync
1. Atualização em tempo real via API REST
2. Fallback para batch sync (WorkManager) quando sem conexão
3. Cache local em Room com flag `sincronizado`
4. Cleanup automático de dados antigos já sincronizados

## Consequências

**Positivas:**
- Precisão alta para roteamento
- Batching reduz consumo de rede
- Funciona offline (cache local)

**Negativas:**
- Consumo de bateria moderado-alto
- Requer permissão FOREGROUND_SERVICE_LOCATION (Android 14+)
- Complexidade no gerenciamento do ciclo de vida do Service
