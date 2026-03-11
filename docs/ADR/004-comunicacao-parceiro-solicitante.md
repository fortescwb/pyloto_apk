# ADR 004: Ponte de Comunicacao entre Parceiro (APK) e Solicitante (WhatsApp)

## Status
Aceito

## Contexto

O parceiro (entregador ou prestador de servico) opera exclusivamente pelo pyloto_apk.
O solicitante fez o pedido e se comunica exclusivamente pelo WhatsApp.

Precisamos que:
- O parceiro envie mensagens e realize ligacoes de dentro do app, sem expor o proprio numero
- O solicitante receba as mensagens e ligacoes no WhatsApp, sem saber que o parceiro usa um app
- Todo o historico de interacao fique registrado no backend (Firestore)

O numero WhatsApp principal da Pyloto (+55 42 8402-7199) e usado exclusivamente para atendimento IA
e nao pode ser sobrecarregado com comunicacao direta parceiro-solicitante.

## Decisao

Registrar um **segundo numero WhatsApp Business** ("canal parceiro") no pyloto_graph-api,
usado exclusivamente para comunicacao entre parceiros e solicitantes durante a execucao de pedidos.

**Mensagens (Fase 1):**
```
Parceiro (app)
    --> POST /chat/{pedidoId}/mensagens (pyloto_atende)
        --> Salva no Firestore (mensagens/{pedidoId})
        --> POST /v1/send/text com phone_number_id=CANAL_PARCEIRO (graph-api)
            --> Mensagem entregue ao solicitante no WhatsApp
```

**Resposta do solicitante (Fase 1):**
```
Solicitante responde no WhatsApp (para o canal parceiro)
    --> Webhook Meta -> graph-api
        --> Evento publicado no PubSub com channel=parceiro
            --> pyloto_atende identifica pedido pelo wa_id do solicitante
                --> Salva no Firestore
                --> FCM push para o parceiro (APK exibe a mensagem)
```

**Ligacoes (Fase 2 — WebRTC Bridge):**
```
Parceiro toca em "Ligar" no app
    --> POST /chat/{pedidoId}/chamar (pyloto_atende)
        --> Cria sessao WebRTC no servidor de midia
        --> Calls API Meta: inicia chamada WhatsApp do canal parceiro para o solicitante
            --> Parceiro fala pelo app (cliente WebRTC)
            --> Solicitante recebe chamada no WhatsApp
```

## Alternativas Consideradas

### A — Usar o numero principal para tudo
Rejeitada: mistura atendimento IA com comunicacao de execucao, impossibilita diferenciar contextos.

### B — Expor numero real do parceiro / solicitante
Rejeitada: viola privacidade, cria canal fora do controle da Pyloto, impede registro de historico.

### C — Chat in-app puro (sem ponte WhatsApp)
Rejeitada para mensagens: o solicitante precisaria ter o app instalado. Para ligacoes: praticavel
apenas como interim enquanto a WebRTC Bridge nao esta pronta (ver Fase Interim abaixo).

### D — VoIP propria (SIP)
Rejeitada: alto custo de infra, latencia, requer operadora. WhatsApp Calling API ja esta disponivel
e integrada ao ecossistema Meta que ja usamos.

## Fase Interim para Ligacoes (antes da Fase 2)

Enquanto a WebRTC Bridge nao estiver implementada, ao tocar em "Ligar":
1. pyloto_atende envia mensagem template pelo canal parceiro ao solicitante:
   "Seu parceiro esta tentando falar com voce. Retorne a chamada pelo WhatsApp."
2. O app exibe o numero mascarado do solicitante e abre WhatsApp via deep link no dispositivo do parceiro.
3. A chamada acontece diretamente entre os dois WhatsApps, mas o numero do parceiro fica visivel.

Isso e aceitavel apenas no MVP. A Fase 2 elimina essa exposicao.

## Consequencias

**Positivas:**
- Solicitante nunca precisa instalar outro app
- Parceiro nunca expoe o proprio numero (Fase 2)
- Historico completo de interacoes no Firestore
- Auditoria e moderacao possiveis pela Pyloto

**Negativas:**
- Requer segundo numero WhatsApp Business registrado na Meta (custo adicional de WABA)
- Fase 2 (ligacoes) requer servidor WebRTC (mediasoup ou equivalente)
- Latencia adicional de ~200ms no envio de mensagens (via backend)

## Componentes Envolvidos

| Componente | Responsabilidade |
|---|---|
| pyloto_apk | Envia mensagens/acoes, exibe respostas via FCM |
| pyloto_atende | Orquestra o chat, persiste historico, chama graph-api |
| pyloto_graph-api | Envia mensagens/chamadas pelo canal parceiro, processa webhooks |
| Meta WABA (canal parceiro) | Numero secundario exclusivo para comunicacao parceiro-solicitante |
| Firebase FCM | Entrega mensagens em tempo real ao APK |
| WebRTC Server (Fase 2) | Bridge de audio entre APK e WhatsApp Calling API |
