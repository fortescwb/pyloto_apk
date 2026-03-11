# Arquitetura de Integracao: pyloto_apk <-> Ecossistema Pyloto

## Visao Geral

O pyloto_apk e o canal exclusivo dos **parceiros** (entregadores e prestadores de servico cadastrados).
Os **solicitantes** interagem exclusivamente pelo **WhatsApp** da Pyloto.

O backend central (pyloto_atende) e o intermediario de toda a comunicacao entre os dois mundos.

```
[Solicitante]                    [Pyloto]                         [Parceiro]
  WhatsApp  <----  canal parceiro (numero 2)  <----  pyloto_atende <----  pyloto_apk
  WhatsApp  ---->  numero principal (+5542...)  ---->  pyloto_atende ---->  FCM ---->  pyloto_apk
```

---

## 1. Tipos de Parceiro Suportados

O campo `tipo` no cadastro do parceiro determina quais pedidos ele ve na lista de disponiveis.

| tipo | Descricao | GPS obrigatorio durante servico |
|---|---|---|
| `entregador` | Realiza entregas de itens | Sim (rastreamento em tempo real) |
| `diarista` | Servico de limpeza/organizacao | Nao (so no deslocamento) |
| `pedreiro` | Servicos de obras/reformas | Nao |
| `pintor` | Servicos de pintura | Nao |
| `marido_aluguel` | Servicos gerais avulsos | Nao |

O app deve adaptar o fluxo de execucao conforme o tipo de servico do pedido aceito:
- **entregador**: exibe mapa com rota, botao "Coletar", botao "Finalizar" com foto obrigatoria
- **demais**: exibe endereco de servico, botao "Iniciar", botao "Finalizar" com foto opcional

---

## 2. Ciclo de Vida de um Pedido

```
[pendente] -> [pago_aguardando_parceiro] -> [parceiro_aceito] -> [em_execucao] -> [concluido]
                                         \-> [cancelado]      \-> [cancelado]
```

| Status | Quem transiciona | Como o parceiro fica sabendo |
|---|---|---|
| `pago_aguardando_parceiro` | pyloto_atende (webhook pagamento) | FCM push: "Novo pedido disponivel" |
| `parceiro_aceito` | Parceiro via `POST /corridas/{id}/aceitar` | Resposta da API |
| `em_execucao` | Parceiro via `POST /corridas/{id}/iniciar` | Resposta da API |
| `coletado` | Parceiro via `POST /corridas/{id}/coletar` (so entrega) | Resposta da API |
| `concluido` | Parceiro via `POST /corridas/{id}/finalizar` | Resposta da API |
| `cancelado` | Parceiro ou Pyloto | FCM push se cancelado pela Pyloto |

---

## 3. Notificacoes em Tempo Real (FCM)

O app usa Firebase Cloud Messaging para receber eventos sem polling.

### Eventos que o app deve tratar

| event_type | Quando ocorre | Acao no app |
|---|---|---|
| `novo_pedido` | Pedido pago disponivel dentro do raio | Toca som + exibe card do pedido |
| `pedido_cancelado` | Pedido cancelado enquanto o parceiro tinha aceito | Alerta + retorna para home |
| `nova_mensagem` | Solicitante respondeu pelo canal WhatsApp parceiro | Exibe notificacao + atualiza chat |
| `chamada_solicitante` | Solicitante iniciou chamada pelo WhatsApp (Fase 2) | Exibe tela de chamada entrante |

### Payload FCM padrao

```json
{
  "data": {
    "event_type": "novo_pedido",
    "pedido_id": "ped_abc123",
    "tipo_servico": "entrega",
    "valor_parceiro": "25.50",
    "endereco_origem": "Rua X, 123 - Centro"
  },
  "notification": {
    "title": "Novo pedido disponivel!",
    "body": "Entrega - R$ 25,50 - a 1,2km de voce"
  }
}
```

---

## 4. Atualizacao de GPS

O GPS do parceiro serve dois propositos:
1. **Filtrar pedidos disponiveis** por proximidade (raio configuravel)
2. **Mapa de rastreamento** para o solicitante (pagina publica `/acompanhar/{pedidoId}`)

### Estrategia de envio

- **Online e com pedido ativo**: a cada 5 segundos
- **Online sem pedido ativo**: a cada 30 segundos
- **Offline**: nenhum envio

O `LocationService` (foreground service) deve respeitar essa cadencia para economizar bateria.
Usar `POST /entregador/localizacao/batch` quando houver acumulo de pontos offline.

---

## 5. Bridge de Comunicacao: Mensagens

### 5.1 Parceiro envia mensagem para o solicitante

```
APK
  POST /chat/{pedidoId}/mensagens
  { "conteudo": "Cheguei no local de coleta!", "tipo": "texto" }

pyloto_atende
  1. Valida JWT do parceiro
  2. Busca pedido no Firestore -> obtem wa_id do solicitante
  3. Salva mensagem: Firestore/mensagens/{pedidoId}/{msgId}
     { de: "parceiro", parceiro_id, conteudo, tipo, created_at }
  4. Chama graph-api: POST /v1/send/text
     { phone_number_id: CANAL_PARCEIRO_PHONE_ID, to: wa_id_solicitante, text: conteudo }

solicitante recebe no WhatsApp:
  [Numero canal parceiro] "Cheguei no local de coleta!"
```

### 5.2 Solicitante responde para o parceiro

```
Solicitante digita no WhatsApp (para o canal parceiro):
  "Ok, pode subir!"

Meta -> graph-api (webhook do canal parceiro)
  graph-api normaliza -> publica evento:
  { channel: "parceiro", from: wa_id_solicitante, text: "Ok, pode subir!" }

pyloto_atende (consumer PubSub)
  1. Identifica o pedido ativo do wa_id_solicitante (status em_execucao ou parceiro_aceito)
  2. Salva no Firestore: { de: "solicitante", conteudo, created_at }
  3. Envia FCM push para o fcm_token do parceiro do pedido
     { event_type: "nova_mensagem", pedido_id, conteudo, de: "solicitante" }

APK recebe FCM -> exibe notificacao -> abre ChatScreen se em foreground
```

### 5.3 Formato das mensagens no Firestore

Colecao: `mensagens/{pedidoId}/msgs`

```json
{
  "id": "msg_xyz",
  "de": "parceiro",
  "parceiro_id": "par_abc",
  "conteudo": "Cheguei no local de coleta!",
  "tipo": "texto",
  "status": "entregue",
  "created_at": 1741290000
}
```

---

## 6. Bridge de Comunicacao: Ligacoes

### 6.1 Fase Interim (MVP)

Enquanto o servidor WebRTC nao estiver disponivel:

```
Parceiro toca em "Ligar"
  POST /chat/{pedidoId}/chamar

pyloto_atende
  1. Envia mensagem template pelo canal parceiro ao solicitante:
     "Seu parceiro [Nome] esta tentando falar com voce. Responda esta mensagem ou ligue de volta."
  2. Retorna { interim: true, solicitante_wa: "+55XXXXXXXXXXX_MASCARADO" }

APK
  Exibe dialog: "Solicitante avisado. Aguarde o retorno ou..."
  Botao "Abrir WhatsApp" -> deep link: whatsapp://send?phone=<numero_canal_parceiro>
    (parceiro liga do proprio WhatsApp usando o numero do canal como referencia)
```

Nessa fase o numero real do parceiro fica visivel para o solicitante na chamada. Aceitavel apenas no MVP.

### 6.2 Fase 2 (WebRTC Bridge)

```
Parceiro toca em "Ligar"
  POST /chat/{pedidoId}/chamar

pyloto_atende
  1. Solicita sessao WebRTC ao servidor de midia (mediasoup)
     -> retorna { session_id, ice_servers, sdp_offer }
  2. Chama Meta Calls API: inicia chamada WhatsApp do canal parceiro para wa_id_solicitante
  3. Retorna ao APK: { session_id, ice_servers, sdp_offer }

APK
  1. Conecta ao servidor WebRTC com as credenciais recebidas
  2. Exibe tela de chamada ativa (microfone/speaker)

Meta -> servidor de midia
  Streams de audio do WhatsApp do solicitante chegam pela Calls API
  -> servidor de midia repassa para a sessao WebRTC do parceiro

Resultado:
  Parceiro ouve/fala pelo app
  Solicitante ouve/fala pelo WhatsApp (recebeu chamada do canal parceiro)
```

---

## 7. Rastreamento em Tempo Real (Mapa Publico)

O solicitante recebe um link de rastreamento via WhatsApp quando o parceiro aceita o pedido:
`https://pyloto.com.br/acompanhar/{pedidoId}`

Essa pagina (pyloto_website ou pyloto_admin-panel) chama periodicamente:
```
GET /public/pedidos/{pedidoId}/location
-> { location: { lat, lng, updated_at }, available: true }
```

A localizacao e a ultima enviada pelo parceiro via `POST /entregador/localizacao`.
Nao ha WebSocket — o rastreamento e por polling a cada 5 segundos na pagina publica.

---

## 8. Cadastro de Parceiro

O cadastro ocorre dentro do proprio app (tela de registro).
O parceiro informa tipo de servico e documentos (foto, CNH se entregador, documento).

Para parceiros do tipo `entregador`:
- Campo `tipo_veiculo` obrigatorio (moto | carro | bicicleta | van)
- Campo `placa` obrigatorio

Para os demais tipos:
- `tipo_veiculo` e `placa` nao se aplicam

O cadastro passa por aprovacao manual da Pyloto antes de o parceiro poder receber pedidos.
Status inicial: `pendente`. Apos aprovacao: `ativo`.

Enquanto `pendente`, o parceiro ve tela informativa no app ("Cadastro em analise").

---

## 9. Segundo Numero WhatsApp Business (Canal Parceiro)

### Configuracao necessaria no pyloto_graph-api

Variavel de ambiente adicional:
```
PARCEIRO_PHONE_NUMBER_ID=<Meta WABA Phone Number ID do canal parceiro>
```

O graph-api precisa registrar um webhook separado para o canal parceiro, ou identificar
pelo `metadata.phone_number_id` dentro do mesmo webhook, roteando eventos com
`channel: "parceiro"` para o PubSub.

### Identificacao do pedido ativo quando solicitante responde

O pyloto_atende usa o `from` (wa_id do solicitante) para encontrar o pedido:
```
Firestore: pedidos onde wa_id == from AND status in [parceiro_aceito, em_execucao]
```

Se houver mais de um pedido ativo (improvavel mas possivel), a mensagem e associada
ao pedido mais recente. Regra a revisar conforme crescimento.

---

## 10. Componentes a Implementar

### pyloto_graph-api
- [ ] Suporte a `PARCEIRO_PHONE_NUMBER_ID` na env + config
- [ ] Roteamento de webhook por `phone_number_id` (canal principal vs canal parceiro)
- [ ] Evento canonico com campo `channel: "principal" | "parceiro"`
- [ ] `POST /v1/send/text` com parametro opcional `channel` para selecionar numero de envio

### pyloto_atende
- [ ] `POST /chat/{pedidoId}/mensagens` — implementacao completa (salvar + enviar via graph-api)
- [ ] `GET /chat/{pedidoId}/mensagens` — busca paginada no Firestore
- [ ] `POST /chat/{pedidoId}/chamar` — fase interim + fase 2
- [ ] Consumer de eventos do canal parceiro (mensagens do solicitante -> FCM para parceiro)
- [ ] Campo `canal_parceiro_wa_id` no Pedido para identificacao reversa

### pyloto_apk
- [ ] `ChatScreen` — implementacao completa com polling ou SSE
- [ ] `ChatViewModel` — envio de mensagens, recepcao via FCM
- [ ] Tela de chamada (Fase 2): UI WebRTC client
- [ ] `CorridaDetalhesScreen` — implementacao (atualmente stub)
- [ ] Suporte a todos os tipos de parceiro no fluxo de execucao
- [ ] Tela "Cadastro em Analise" para parceiros pendentes
- [ ] Logica de cadencia GPS (5s com pedido ativo, 30s sem pedido)

---

## 11. Dependencias entre Repositorios

```
pyloto_apk
  depende de: pyloto_atende (todas as rotas /app/*)

pyloto_atende
  depende de: pyloto_graph-api (envio de mensagens e chamadas)
  depende de: Firestore (persistencia)
  depende de: FCM (push para APK)
  depende de: Redis (cache de localizacao dos parceiros)

pyloto_graph-api
  depende de: Meta WABA (dois numeros: principal e canal parceiro)
  depende de: Redis (dedupe, rate limit)
  depende de: PubSub (publicacao de eventos)
```
