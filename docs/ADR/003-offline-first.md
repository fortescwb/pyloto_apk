# ADR 003: Estratégia Offline-First

## Status
Aceito

## Contexto
Entregadores frequentemente passam por áreas com conectividade instável. O app precisa:
- Funcionar sem conexão para operações críticas
- Sincronizar automaticamente quando a conexão retornar
- Evitar perda de dados e duplicação

## Decisão
Adotar estratégia **Offline-First** com:

### 1. Cache Local (Room)
- Todas as entidades principais têm cache local
- Flag `sincronizado` em cada entidade
- Paginação local para listas grandes

### 2. Sync Queue (Fila de Sincronização)
- Tabela `sync_queue` para operações pendentes
- Prioridade: `CRITICA` (aceitar/finalizar corrida) > `ALTA` (localização) > `NORMAL` (perfil)
- Retry com backoff exponencial (máx. 5 tentativas)

### 3. WorkManager
- `SyncWorker`: Executa a cada 15min para sincronizar pendências
- `LocationSyncWorker`: Batch sync de localizações acumuladas
- `CleanupWorker`: Limpeza periódica de dados expirados

### 4. ConnectivityMonitor
- Monitora estado de rede via Flow
- Dispara sincronização imediata quando conexão retorna

### Fluxo de Escrita
```
Operação → Salvar Local → Enfileirar Sync → Tentar Enviar
                                                ↓
                                          Sucesso? → Marcar sincronizado
                                          Falha?  → Retry via WorkManager
```

## Consequências

**Positivas:**
- App funcional sem internet
- Dados nunca perdidos
- UX fluida (sem spinners de loading para dados em cache)
- Escalável para milhares de operações pendentes

**Negativas:**
- Complexidade de resolução de conflitos
- Storage local maior
- Necessidade de política de expiração de cache
