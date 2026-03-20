# TODO — Regras mínimas do módulo de Entregadores / Operação

Baseado nas regras operacionais e contratuais do documento anexado pela Pyloto, este checklist serve para **implementar, revisar ou confirmar** se a lógica mínima já existe no sistema. Onde houver divergência entre regra de negócio informal e documento contratual, a tarefa deve ser tratada como **pendência crítica de alinhamento** antes de marcar como concluída.

---

## 1. Cadastro e qualificação do parceiro

### [ ] 1.1 Garantir campos obrigatórios de cadastro civil e operacional do parceiro
**Descrição:** Confirmar que o cadastro do entregador exige, no mínimo, nome completo, CPF, RG/documento de identidade, data de nascimento, endereço completo, telefone/WhatsApp, e-mail, tipo e placa do veículo, CNH, volume do baú e plano de adesão escolhido.

**Critérios de aceite:**
- O backend possui estrutura de dados para todos os campos exigidos contratualmente.
- O frontend/painel impede ativação de parceiro sem preenchimento dos campos obrigatórios.
- Existe validação mínima de formato para CPF, telefone, e-mail e placa.
- O volume do baú é armazenado como dado estruturado, não apenas texto livre.

### [ ] 1.2 Separar status cadastral de status operacional
**Descrição:** Garantir distinção entre parceiro ativo cadastralmente e parceiro apto a receber corridas naquele momento.

**Critérios de aceite:**
- Existem campos distintos para: status cadastral, online/offline, disponível/indisponível e bloqueio operacional.
- O sistema não usa um único campo genérico para representar tudo.
- Um parceiro pode estar cadastrado e ativo, porém temporariamente inelegível para receber corridas.

### [ ] 1.3 Registrar aceite contratual e versão do contrato
**Descrição:** Armazenar evidência de assinatura física/digital, versão do contrato aceito e timestamp do aceite.

**Critérios de aceite:**
- Existe campo para versão contratual aceita.
- Existe campo para data/hora do aceite.
- Existe referência ao arquivo assinado ou evidência de assinatura eletrônica.
- O parceiro não é ativado sem aceite contratual registrado.

### [ ] 1.4 Registrar treinamento operacional inicial
**Descrição:** Confirmar se o sistema registra que o parceiro recebeu treinamento operacional no ato do cadastro.

**Critérios de aceite:**
- Existe campo ou evento de treinamento concluído.
- Há data/hora e responsável pelo registro.
- O parceiro só pode ser liberado após treinamento marcado como concluído, se essa for a regra adotada.

---

## 2. Documentos e elegibilidade

### [ ] 2.1 Controlar documentos exigidos no credenciamento
**Descrição:** Confirmar armazenamento e status de CNH, CPF, comprovante de residência, documento do veículo (CRLV), seguro do veículo quando aplicável, foto/comprovação do baú e dados bancários/Pix.

**Critérios de aceite:**
- Cada documento possui status próprio: pendente, aprovado, rejeitado ou vencido.
- O sistema registra data de envio, data de aprovação/rejeição e responsável pela validação.
- O sistema permite bloquear operação por documento pendente, inválido ou vencido.
- Há espaço para anexos/URLs de evidência documental.

### [ ] 2.2 Validar vencimento e renovação documental
**Descrição:** Implementar ou confirmar rotina para documentos com validade, especialmente CNH e CRLV.

**Critérios de aceite:**
- Existe campo de validade para documentos aplicáveis.
- O sistema alerta antes do vencimento.
- O sistema impede operação após vencimento, se a regra assim exigir.
- Há trilha de auditoria de reenvio e nova aprovação.

### [ ] 2.3 Bloquear compartilhamento indevido de conta e uso de veículo não cadastrado
**Descrição:** Implementar mecanismo mínimo de prevenção, detecção e resposta a uso de credenciais por terceiros ou veículo divergente do cadastro.

**Critérios de aceite:**
- Existe regra operacional/documentada para auditoria de veículo em uso.
- O sistema permite solicitar foto do veículo com placa visível ou evidência equivalente.
- Existe fluxo de suspensão/bloqueio por uso irregular.
- Há registro de incidentes relacionados a fraude operacional.

---

## 3. Baú, capacidade e limites operacionais

### [ ] 3.1 Tornar obrigatória a informação estruturada do baú
**Descrição:** O volume do baú não pode existir apenas como observação contratual; deve ser dado estruturado, validável e utilizável pelo motor de despacho.

**Critérios de aceite:**
- Existe campo específico para capacidade do baú em litros.
- O sistema aceita apenas valores compatíveis com os planos/regras operacionais adotados.
- Existe prova ou validação administrativa de que o baú foi conferido.
- Parceiro sem baú mínimo exigido não pode operar.

### [ ] 3.2 Implementar tabela de capacidade operacional por tipo de baú
**Descrição:** Confirmar que o sistema suporta, no mínimo, a tabela contratual de capacidade por litragem do baú.

**Critérios de aceite:**
- O sistema possui tabela/versionamento de capacidade por baú.
- Para 80L, 120L, 135L e 150L há limites parametrizados de volume, peso e valor.
- A tabela usada pelo sistema corresponde à regra vigente aprovada pela empresa.
- Mudança futura da tabela não exige alteração manual dispersa em múltiplos pontos do código.

### [ ] 3.3 Sanear divergência de valor máximo transportado para baú 80L
**Descrição:** Existe divergência entre a regra de negócio informal mencionada anteriormente (R$ 500,00) e o contrato anexado (R$ 1.500,00 para 80L). Essa divergência precisa ser resolvida antes de considerar a lógica correta.

**Critérios de aceite:**
- Existe definição única e formal da regra vigente.
- Contrato, sistema, documentação interna e front exibem o mesmo valor.
- Há registro da decisão e, se necessário, atualização contratual/documental.
- Nenhum cálculo operacional permanece usando valor divergente.

### [ ] 3.4 Calcular capacidade remanescente em tempo real
**Descrição:** Confirmar que volume, peso e valor em uso são recalculados conforme coleta, entrega, cancelamento, devolução ou reatribuição.

**Critérios de aceite:**
- O sistema mantém capacidade máxima e capacidade em uso/remanescente.
- A coleta de um pedido consome capacidade automaticamente.
- A entrega/cancelamento libera capacidade automaticamente.
- O parceiro e o motor de despacho recebem informação atualizada sem depender de input manual.

### [ ] 3.5 Bloquear novas corridas quando limites forem atingidos ou estiverem críticos
**Descrição:** Implementar limitação automática para impedir aceite/atribuição acima da capacidade operacional.

**Critérios de aceite:**
- O sistema impede alocação quando volume máximo, peso máximo ou valor máximo forem excedidos.
- Existe regra clara para “próximo do limite”, se a empresa quiser margem de segurança.
- O app informa o motivo do bloqueio de forma compreensível.
- O log registra a tentativa de alocação recusada por capacidade.

---

## 4. Tipos de corrida e SLAs

### [ ] 4.1 Estruturar modalidade da corrida como campo formal
**Descrição:** Toda corrida deve possuir modalidade explícita: comum (sem prioridade) ou prioridade.

**Critérios de aceite:**
- O pedido/corrida armazena modalidade em campo estruturado.
- Regras de coleta, entrega e despacho leem esse campo.
- O frontend e o backend tratam as modalidades de forma consistente.

### [ ] 4.2 Implementar SLA de corridas comuns
**Descrição:** Corridas comuns devem respeitar coleta em até 1 hora após aceitação e entrega no mesmo dia até 19h00; pedidos após 18h00 devem ser processados para o dia seguinte.

**Critérios de aceite:**
- O sistema calcula prazo máximo de coleta para corridas comuns.
- O sistema calcula prazo limite de entrega no mesmo dia, até 19h00.
- Pedidos após 18h00 não entram como entrega do mesmo dia, salvo regra posterior expressa.
- Existe sinalização de atraso de coleta e atraso de entrega.

### [ ] 4.3 Implementar SLA de corridas prioritárias
**Descrição:** Corridas prioritárias devem respeitar coleta em até 15 minutos e entrega em até 30 minutos após coleta.

**Critérios de aceite:**
- O sistema calcula e monitora prazo máximo de coleta prioritária.
- O sistema calcula e monitora prazo máximo de entrega prioritária.
- Há alerta operacional quando o parceiro estiver próximo de estourar o SLA.
- A quebra de SLA gera evento auditável.

### [ ] 4.4 Garantir precedência operacional da prioridade
**Descrição:** Corridas prioritárias têm precedência absoluta sobre corridas comuns em caso de conflito operacional.

**Critérios de aceite:**
- O motor de despacho/routing reconhece prioridade como classe superior.
- O sistema evita atribuir nova carga que inviabilize a prioridade.
- Em conflito de rota, a prioridade prevalece automaticamente ou exige intervenção explícita.
- Há evidência em testes de cenário misto comum + prioridade.

### [ ] 4.5 Permitir acúmulo de corridas comuns apenas quando viável
**Descrição:** O parceiro pode aceitar múltiplas corridas comuns, mas somente se respeitar capacidade e SLA.

**Critérios de aceite:**
- O sistema valida capacidade física e financeira antes de adicionar nova corrida comum.
- O sistema valida impacto da nova coleta na rota e nos prazos já assumidos.
- Não é permitido aceitar múltiplas corridas comuns apenas porque “cabe no baú”; é necessário manter SLA viável.
- Existe teste de cenário com múltiplas coletas e entregas no mesmo dia.

### [ ] 4.6 Tratar corretamente prioridade coexistindo com pedidos comuns no baú
**Descrição:** O contrato permite manter pedidos comuns no baú durante uma entrega prioritária, desde que a prioritária seja executada imediatamente e sem desvio de rota.

**Critérios de aceite:**
- A regra existe de forma explícita no sistema ou na camada de despacho.
- O sistema bloqueia comportamento incompatível com execução imediata da prioridade.
- Há definição objetiva do que caracteriza desvio incompatível, ainda que inicialmente simplificada.
- Cenários de coexistência foram testados.

---

## 5. Rota, geolocalização e rastreabilidade

### [ ] 5.1 Manter localização atual do parceiro como estado operacional
**Descrição:** Confirmar armazenamento de latitude, longitude, timestamp da última posição e metadados úteis como precisão, velocidade e origem da coleta de localização, quando disponível.

**Critérios de aceite:**
- O documento do parceiro mantém localização atual atualizável.
- Existe campo de timestamp da última atualização.
- O sistema sabe distinguir localização recente de localização obsoleta.
- Há política para offline/stale location.

### [ ] 5.2 Armazenar histórico de localização durante corridas ativas
**Descrição:** Implementar ou confirmar trilha de localização quando houver pedido ativo, rota iniciada ou evento crítico operacional.

**Critérios de aceite:**
- O histórico é salvo em estrutura separada do documento principal.
- Cada ponto registra ao menos parceiro, pedido/rota quando aplicável, latitude, longitude e timestamp.
- O histórico é gravado apenas quando operacionalmente relevante, evitando custo inútil.
- Existe política mínima de retenção ou descarte.

### [ ] 5.3 Registrar eventos operacionais críticos
**Descrição:** Além da localização bruta, o sistema deve registrar eventos como aceite, chegada na coleta, coleta confirmada, saída, chegada no destino, entrega, cancelamento, atraso e bloqueio por capacidade.

**Critérios de aceite:**
- Existe trilha de eventos operacionais por parceiro ou corrida.
- Cada evento possui timestamp e referência à corrida/pedido.
- Eventos podem ser usados em suporte, auditoria e disputa.
- A ausência de evento crítico impede conclusão silenciosa do fluxo.

### [ ] 5.4 Implementar funcionalidade “Iniciar Rota” com efeito real
**Descrição:** Confirmar que a funcionalidade prevista contratualmente não seja apenas visual; ela deve afetar cálculo de rota, acompanhamento ou prova operacional.

**Critérios de aceite:**
- Existe ação explícita de iniciar rota.
- A ação altera o estado operacional do parceiro.
- A partir desse estado, o sistema passa a acompanhar rota/localização/eventos compatíveis.
- Há evidência de uso desse estado no fluxo operacional.

---

## 6. Agenda de trabalho

### [ ] 6.1 Implementar agenda com janela D+1 e D+2
**Descrição:** Confirmar a lógica contratual de abertura diária da agenda de trabalho futura.

**Critérios de aceite:**
- No dia corrente, o parceiro consegue visualizar/agendar os dias previstos pela regra vigente.
- A renovação diária da agenda é automática.
- O sistema preserva/cancela agendamentos conforme a lógica adotada.
- Há testes cobrindo virada diária da janela.

### [ ] 6.2 Priorizar parceiros com agendamento prévio
**Descrição:** O sistema deve considerar o agendamento como critério de prioridade de acesso/operação.

**Critérios de aceite:**
- Existe lógica de prioridade para parceiros previamente agendados.
- A regra não depende de decisão manual ad hoc.
- Há distinção entre parceiro agendado, não agendado e remanescente.

### [ ] 6.3 Aplicar penalidade por não comparecimento sem cancelamento prévio
**Descrição:** Parceiro que agendar e não comparecer deve sofrer restrição na abertura seguinte, conforme regra contratual.

**Critérios de aceite:**
- Existe detecção de no-show baseada em agendamento + ausência de online/atividade.
- Existe registro do no-show.
- A penalidade afeta a abertura seguinte da agenda, conforme regra definida.
- O parceiro consegue voltar a operar por vagas remanescentes, se for o caso.

### [ ] 6.4 Implementar cancelamento sem penalidade com antecedência mínima de 12 horas
**Descrição:** O sistema deve validar a antecedência mínima de 12h para cancelamento sem penalidade.

**Critérios de aceite:**
- Cancelamento dentro do prazo não gera penalidade.
- Cancelamento fora do prazo aplica a consequência prevista.
- O sistema calcula a antecedência corretamente com base no horário agendado.
- Há evidência de teste de borda próximo às 12h.

---

## 7. Pagamentos, saldo e repasses

### [ ] 7.1 Registrar conta/chave Pix de recebimento do parceiro
**Descrição:** O parceiro precisa ter meio de recebimento estruturado e validável.

**Critérios de aceite:**
- Existe armazenamento estruturado de conta ou chave Pix.
- Existe status de confirmação/validação dessa informação.
- Parceiro sem dado financeiro válido não pode ser liberado para repasse.

### [ ] 7.2 Implementar regra de saldo D+1 após confirmação de entrega
**Descrição:** O contrato prevê que o pagamento do parceiro é contabilizado na confirmação da entrega e transferido em até 1 dia via Pix.

**Critérios de aceite:**
- A entrega confirmada gera crédito interno do parceiro.
- O saldo fica visível no app/painel.
- O repasse obedece a janela D+1 definida pela regra vigente.
- Há trilha de status: pendente, disponível, transferido, falhou.

### [ ] 7.3 Separar financeiramente valor da corrida, taxa da plataforma e mensalidade do plano
**Descrição:** A taxa de serviço da Pyloto é cobrada do solicitante; o parceiro recebe o valor integral da corrida. A mensalidade do plano é outra lógica e não pode contaminar o cálculo da corrida.

**Critérios de aceite:**
- O cálculo do parceiro não desconta indevidamente taxa da plataforma sobre a corrida, se a regra vigente for essa.
- A mensalidade é tratada separadamente do repasse operacional.
- O extrato deixa claro origem e natureza de cada valor.
- Há testes cobrindo corrida + mensalidade + repasse.

### [ ] 7.4 Implementar suspensão por inadimplência do plano quando aplicável
**Descrição:** O contrato prevê suspensão do acesso em caso de atraso no pagamento da mensalidade e até descredenciamento em hipóteses mais graves.

**Critérios de aceite:**
- Existe controle de vencimento da mensalidade.
- Existe regra de suspensão por inadimplência.
- Existe histórico de cobranças, suspensão e reativação.
- A suspensão não afeta silenciosamente corridas já em andamento sem tratamento definido.

---

## 8. Penalidades, qualidade e reputação

### [ ] 8.1 Implementar gradação mínima de penalidades
**Descrição:** O sistema deve suportar advertência, suspensão temporária, suspensão do agendamento prioritário e descredenciamento.

**Critérios de aceite:**
- Há estrutura para registrar penalidade e motivo.
- Há distinção entre tipos e gravidade.
- Penalidades geram efeitos operacionais reais no sistema.
- Existe histórico auditável por parceiro.

### [ ] 8.2 Implementar hipóteses de bloqueio/descredenciamento imediato
**Descrição:** Fraude, uso ilícito, violação grave de prazo, dano doloso, uso indevido de dados e adulteração de informações devem ter resposta operacional correspondente.

**Critérios de aceite:**
- As hipóteses críticas podem ser registradas como incidente grave.
- O sistema permite bloqueio imediato.
- O bloqueio é auditável e reversível apenas por fluxo administrativo apropriado.
- Há rastreabilidade da decisão de bloqueio.

### [ ] 8.3 Tratar recusa reiterada de corridas como fator de reputação/distribuição
**Descrição:** O contrato prevê rebaixamento de prioridade por recusa injustificada e reiterada de corridas.

**Critérios de aceite:**
- O sistema registra aceite, recusa e motivo da recusa.
- É possível distinguir recusa legítima por capacidade/indisponibilidade de recusa injustificada.
- Existe regra de impacto na distribuição futura.
- Essa lógica não depende apenas de análise manual.

---

## 9. LGPD, segurança e retenção

### [ ] 9.1 Registrar base de consentimento/uso de dados do parceiro
**Descrição:** Confirmar que o cadastro do parceiro contém aceite para tratamento de dados nas finalidades operacionais previstas.

**Critérios de aceite:**
- Existe registro do aceite LGPD ou base legal operacional equivalente, conforme desenho jurídico adotado.
- O sistema vincula esse aceite à versão do texto aplicável.
- Há data/hora do registro.

### [ ] 9.2 Restringir uso dos dados do solicitante ao estritamente necessário
**Descrição:** O parceiro só deve acessar dados do cliente necessários para executar a corrida.

**Critérios de aceite:**
- O app do parceiro não expõe dados além do necessário.
- Não há campos desnecessários persistidos localmente sem justificativa.
- Existe diretriz técnica para evitar armazenamento indevido de dados do solicitante.
- Há mecanismo mínimo para apurar uso indevido de dados.

### [ ] 9.3 Definir política de retenção para localização, eventos e documentos
**Descrição:** O sistema não deve armazenar indefinidamente tudo sem critério.

**Critérios de aceite:**
- Existe definição formal de retenção por tipo de dado.
- Localização histórica possui prazo ou regra de descarte/arquivamento.
- Documentos e eventos têm tratamento compatível com exigência operacional e jurídica.
- A política é aplicável tecnicamente, não apenas textual.

---

## 10. Auditoria, observabilidade e testes

### [ ] 10.1 Criar trilha de auditoria mínima para ações críticas
**Descrição:** Toda ação relevante deve poder ser reconstruída depois: cadastro, aprovação documental, aceite de contrato, aceite/recusa de corrida, coleta, entrega, bloqueio, alteração de veículo, alteração de plano e repasse.

**Critérios de aceite:**
- Existe trilha de auditoria com timestamp e ator responsável.
- A trilha cobre operações administrativas e operacionais.
- O histórico não depende apenas de logs efêmeros de aplicação.

### [ ] 10.2 Testar cenários mínimos obrigatórios de operação
**Descrição:** Não basta modelar; é necessário provar que a lógica resiste aos cenários mais prováveis e perigosos.

**Critérios de aceite:**
- Existem testes cobrindo corrida comum simples.
- Existem testes cobrindo múltiplas corridas comuns dentro da capacidade.
- Existem testes cobrindo bloqueio por excesso de volume/peso/valor.
- Existem testes cobrindo coexistência de comum + prioridade.
- Existem testes cobrindo atraso, cancelamento, no-show de agenda e bloqueio por documento vencido.

### [ ] 10.3 Validar coerência entre contrato, sistema e interface
**Descrição:** O maior risco aqui é regra existir no contrato e não existir no produto, ou existir de forma divergente.

**Critérios de aceite:**
- Existe revisão cruzada entre contrato, backend, frontend e documentação interna.
- Toda divergência relevante foi registrada e resolvida.
- Não há texto contratual prometendo funcionalidade inexistente como se já estivesse pronta.
- Não há regra crítica em produção sem lastro contratual/documental adequado.

---

## 11. Pendências críticas de alinhamento antes de produção

### [ ] 11.1 Definir valor máximo real por litragem do baú
**Descrição:** Resolver formalmente o conflito entre regras mencionadas informalmente e a tabela contratual anexada.

**Critérios de aceite:**
- Existe tabela final aprovada.
- Contrato e sistema usam a mesma tabela.
- Time sabe qual regra está valendo.

### [ ] 11.2 Definir se a lógica de cálculo de corrida ficará em anexo contratual/documentação técnica versionada
**Descrição:** O contrato sugere transparência maior sobre cálculo das corridas. Isso afeta suporte, disputas e previsibilidade ao parceiro.

**Critérios de aceite:**
- Foi decidido se haverá anexo contratual, política pública ou documentação interna versionada.
- A fórmula vigente está documentada de forma inequívoca.
- O sistema usa a mesma versão documentada.

### [ ] 11.3 Confirmar se “disponível” significa apenas livre para receber corridas ou se depende de agenda, documentos, capacidade e localização recente
**Descrição:** O campo disponível costuma ser mal usado. Precisa de definição operacional única.

**Critérios de aceite:**
- Existe definição única de disponibilidade.
- Backend e frontend usam essa definição.
- Não há despacho baseado em campo ambíguo.

### [ ] 11.4 Confirmar política de frequência de rastreamento e retenção de localização
**Descrição:** Sem isso você ou grava pouco e perde prova operacional, ou grava demais e cria custo/passivo desnecessário.

**Critérios de aceite:**
- Existe regra mínima de frequência/eventos de gravação.
- Existe política de retenção compatível com suporte, disputa e LGPD.
- A implementação segue a política definida.

---

## 12. Definição de pronto mínimo

Uma tarefa deste documento **só pode ser marcada como concluída** quando houver, cumulativamente:

- implementação real **ou** confirmação técnica objetiva de que a lógica já existe;
- evidência verificável no código, banco, painel ou fluxo do app;
- critério de aceite atendido integralmente;
- ausência de divergência conhecida entre contrato, sistema e operação real.

Se existir regra “mais ou menos”, mock, campo solto sem uso real, lógica manual fora do sistema ou divergência documental, **não está concluído**.
