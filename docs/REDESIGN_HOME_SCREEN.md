# 🎨 Redesign da Tela Inicial - Pyloto Entregador

## 📊 Análise da Situação Atual

### ✅ O que já existe (app atual):
- ✅ Toggle entre modo padrão e mapa
- ✅ Lista de corridas disponíveis com cards
- ✅ Integração com Google Maps
- ✅ Localização em tempo real
- ✅ Bottom navigation
- ✅ Paleta de cores Pyloto implementada

### ❌ O que está faltando (comparado à webapp):
- ❌ **Header com identidade**: Nome "Pyloto" e status Online/Offline
- ❌ **Dashboard de estatísticas do dia**: Ganhos, entregas realizadas, tempo online, tempo restante
- ❌ **Indicadores de performance**: Média por hora, economia de taxas
- ❌ **Meta diária**: Barra de progresso com objetivo configurável
- ❌ **Visual impactante**: Uso forte das cores verde militar e dourado
- ❌ **Informações de localização**: Cidade e região atual
- ❌ **Cards de pedidos mais ricos**: Prioridade, detalhes visuais, valores em destaque

---

## 🎯 Proposta de Redesign

### 1. **Header Premium** (Verde Militar + Dourado)

```kotlin
┌─────────────────────────────────────────┐
│ 🟢 PYLOTO            [● ONLINE] 🟡     │
│ Entregador                              │
│ 📍 Ponta Grossa, PR • Centro           │
└─────────────────────────────────────────┘
```

**Características**:
- Fundo: `Verde Militar (#3D5A40)` - dominante
- Logo "PYLOTO": `Dourado (#D4AF37)` - destaque
- Botão Online/Offline:
  - Online: `Dourado` com texto preto
  - Offline: Cinza com texto branco
- Localização: Fundo branco translúcido 10%, ícone dourado

---

### 2. **Dashboard de Estatísticas** (Logo após o header)

```kotlin
┌─────────────────────────────────────────┐
│ HOJE                                    │
│                                         │
│ ┌─────────┐  ┌─────────┐              │
│ │ 💰 245,50│  │ ✓ 12    │              │
│ │ Ganhos  │  │ Entregas│              │
│ └─────────┘  └─────────┘              │
│                                         │
│ ┌─────────┐  ┌─────────┐              │
│ │ ⏱ 5h 32m│  │ ⏰ 4h 28m│              │
│ │ Online  │  │ Restante│              │
│ └─────────┘  └─────────┘              │
│                                         │
│ ┌─────────────────────────────────────┐│
│ │ 🎯 Meta do Dia: R$ 300,00           ││
│ │ ████████████░░░░ 81%                ││
│ │ Faltam R$ 54,50 para sua meta       ││
│ └─────────────────────────────────────┘│
└─────────────────────────────────────────┘
```

**Cards de Estatísticas**:
1. **Ganhos** (destaque máximo):
   - Fundo: Gradiente `Dourado → Marrom`
   - Texto: Branco
   - Ícone: DollarSign
   - Valor grande e bold

2. **Entregas**:
   - Fundo: Branco
   - Borda: Verde Brasil (20% opacity)
   - Ícone: CheckCircle (verde)
   - Valor em preto

3. **Tempo Online**:
   - Fundo: Branco
   - Borda: Azul Técnico (20% opacity)
   - Ícone: Clock (azul)
   - Valor em preto

4. **Tempo Restante**:
   - Fundo: Branco
   - Borda: Verde Brasil (20% opacity)
   - Ícone: AlertCircle (verde)
   - Valor em preto

5. **Meta Diária** (card expandido):
   - Fundo: Branco
   - Ícone: Target (verde)
   - Barra de progresso: Gradiente `Verde → Dourado`
   - Texto de progresso em verde/dourado

---

### 3. **Mapa Compacto** (Integrado ao fluxo)

```kotlin
┌─────────────────────────────────────────┐
│ ┌───────────────────────────────────┐  │
│ │  [Mapa do Google Maps]            │  │
│ │  • 3 marcadores dourados (pedidos)│  │
│ │  • 1 marcador azul (você)         │  │
│ │  • Toque para expandir           │  │
│ └───────────────────────────────────┘  │
│ 📍 3 pedidos próximos                   │
└─────────────────────────────────────────┘
```

**Características**:
- Altura: 200dp (compacto)
- Marcadores de pedidos: Dourado pulsante
- Marcador do entregador: Azul com animação de ping
- Overlay com gradiente escuro no bottom
- Botão de expandir no canto superior direito
- Ao tocar, abre modal full-screen

---

### 4. **Cards de Pedidos Enriquecidos**

```kotlin
┌─────────────────────────────────────────┐
│ PEDIDOS DISPONÍVEIS          3 disponív│
│                                         │
│ ┌───────────────────────────────────┐  │
│ │ 🔥 PRIORITÁRIO                    │  │
│ │                                   │  │
│ │ [R$ 18,50] 2.3 km • 25 min       │  │
│ │                                   │  │
│ │ 🏪 Sabor Brasileiro               │  │
│ │ 📍 Rua das Flores, 123            │  │
│ │ 📦 3 itens                         │  │
│ │                                   │  │
│ │ ━━━━━━━━━━━━━━━━━━━━━━━━         │  │
│ │                                   │  │
│ │ 📍 Endereço de entrega            │  │
│ │ Av. Paulista, 1578                │  │
│ │                                   │  │
│ │ [ACEITAR]        [DETALHES]      │  │
│ └───────────────────────────────────┘  │
└─────────────────────────────────────────┘
```

**Características**:
- Badge "Prioritário": Fundo dourado, ícone AlertCircle
- Valor em destaque: Fundo dourado, texto preto, bold, grande
- Ícone de origem (Store): Circle verde com ícone branco
- Ícone de destino (MapPin): Circle azul com ícone branco
- Botão "Aceitar": Fundo verde, texto branco, bold
- Botão "Detalhes": Fundo cinza claro, texto cinza escuro
- Sombra suave no card

---

### 5. **Bottom Navigation** (Melhorado)

```kotlin
┌─────────────────────────────────────────┐
│  🏠      📋      💰      👤            │
│ Início  Corridas Ganhos  Perfil        │
│  ●                                      │
└─────────────────────────────────────────┘
```

**Atualização**:
- Ícone ativo: Verde Brasil
- Texto ativo: Verde Brasil, bold
- Ícone inativo: Cinza 400
- Indicador ativo: Bolinha verde na parte inferior
- Nova aba "Ganhos" (Wallet) para estatísticas detalhadas

---

## 🎨 Aplicação da Paleta Pyloto

### Hierarquia Visual:
- **50% Verde Militar**: Header, FABs, botões primários, bordas, ícones de aprovação
- **25% Dourado**: Valor dos ganhos, badges, CTAs secundários, destaques financeiros
- **15% Azul Técnico**: Informações, links, marcador de localização
- **10% Branco/Bege**: Cards, backgrounds, respiro visual

### Contrastes Importantes:
- ✅ Verde + Branco: 8.5:1 (excelente)
- ✅ Dourado + Preto: 9.8:1 (excelente)
- ✅ Azul + Branco: 6.2:1 (muito bom)

---

## 📱 Estrutura de Componentes

### Novos Componentes Necessários:

1. **`HomeHeader.kt`**
   - Status online/offline toggle
   - Logo Pyloto
   - Informações de localização

2. **`DailyStatsSection.kt`**
   - Grid 2x2 de cards de estatísticas
   - Animações de entrada (fade + slide)
   - Formatação de valores monetários

3. **`DailyGoalCard.kt`**
   - Meta configurável
   - Barra de progresso animada
   - Cálculo de faltante

4. **`CompactMapSection.kt`**
   - Mapa Google Maps compacto
   - Marcadores animados
   - Modal de expansão

5. **`EnrichedCorridaCard.kt`**
   - Badge de prioridade
   - Valor em destaque
   - Ícones de origem/destino
   - Botões de ação

---

## 🔧 Alterações no ViewModel

### HomeUiState (expandido):

```kotlin
data class HomeUiState(
    // Existente
    val isLoading: Boolean = true,
    val modoVisualizacao: HomeModoVisualizacao = HomeModoVisualizacao.PADRAO,
    val corridas: List<Corrida> = emptyList(),
    val localizacaoAtual: HomeLocation? = null,
    val erro: String? = null,

    // NOVO - Status do Entregador
    val isOnline: Boolean = false,
    val cidadeAtual: String = "Ponta Grossa, PR",
    val regiaoAtual: String = "Centro",

    // NOVO - Estatísticas do Dia
    val dailyStats: DailyStats = DailyStats(),
    val dailyGoal: Double = 300.0
)

data class DailyStats(
    val earnings: Double = 0.0,           // Ganhos do dia
    val deliveries: Int = 0,              // Entregas realizadas
    val timeOnlineMinutes: Int = 0,       // Tempo online em minutos
    val maxTimeMinutes: Int = 600,        // Limite máximo (10h)
    val totalFeeSavings: Double = 0.0,    // Economia em taxas
    val averagePerHour: Double = 0.0      // Média por hora
) {
    val timeOnlineFormatted: String
        get() = "${timeOnlineMinutes / 60}h ${timeOnlineMinutes % 60}m"

    val timeRemainingFormatted: String
        get() {
            val remaining = maxTimeMinutes - timeOnlineMinutes
            return "${remaining / 60}h ${remaining % 60}m"
        }

    fun goalProgress(goal: Double): Float {
        return (earnings / goal * 100).coerceIn(0f, 100f)
    }

    fun remainingToGoal(goal: Double): Double {
        return (goal - earnings).coerceAtLeast(0.0)
    }
}
```

### Novos métodos no ViewModel:

```kotlin
fun toggleOnlineStatus() {
    _uiState.update { it.copy(isOnline = !it.isOnline) }
    // TODO: Implementar lógica de sincronização com backend
}

fun loadDailyStats() {
    viewModelScope.launch {
        // TODO: Buscar estatísticas do dia do repositório
        _uiState.update {
            it.copy(
                dailyStats = DailyStats(
                    earnings = 245.50,
                    deliveries = 12,
                    timeOnlineMinutes = 332,
                    totalFeeSavings = 24.55,
                    averagePerHour = 44.51
                )
            )
        }
    }
}

fun updateDailyGoal(newGoal: Double) {
    _uiState.update { it.copy(dailyGoal = newGoal) }
    // TODO: Persistir no SharedPreferences ou backend
}
```

---

## 🎭 Experiência do Usuário

### Mensagem Humanizada

Este redesign foi pensado para **pessoas humildes que passam por necessidades básicas**:

1. **Transparência Financeira Total**:
   - Ganhos do dia sempre visíveis e em destaque
   - Meta clara e motivadora
   - Economia de taxas evidenciada (vs outros apps)

2. **Motivação Constante**:
   - Progresso visual da meta
   - Estatísticas positivas
   - Feedback imediato

3. **Clareza e Simplicidade**:
   - Informações importantes acima da dobra
   - Cards grandes e legíveis
   - Cores que transmitem confiança (verde) e valor (dourado)

4. **Empoderamento**:
   - Controle total (toggle online/offline)
   - Visibilidade dos pedidos
   - Cálculos transparentes

---

## 📐 Layout Responsivo

### Estrutura de Scroll:

```
┌─────────────────────────────────────────┐
│ [HEADER FIXO - Verde]                   │ ← Sempre visível
├─────────────────────────────────────────┤
│ [Scroll Container]                      │
│                                         │
│ 1. Dashboard de Estatísticas           │ ← Primeira dobra
│    - 4 cards (2x2)                      │
│    - Meta do dia                        │
│                                         │
│ 2. Mapa Compacto                        │ ← Segunda dobra
│    - 200dp altura                       │
│                                         │
│ 3. Pedidos Disponíveis                  │ ← Terceira dobra+
│    - LazyColumn                         │
│    - Cards de corridas                  │
│                                         │
│ [Espaço bottom navigation]              │
└─────────────────────────────────────────┘
│ [BOTTOM NAV FIXO - Branco]              │ ← Sempre visível
└─────────────────────────────────────────┘
```

---

## 🚀 Implementação Sugerida

### Fase 1: Infraestrutura (1-2 dias)
1. ✅ Atualizar `HomeUiState` com novos campos
2. ✅ Criar `DailyStats` data class
3. ✅ Adicionar métodos ao `HomeViewModel`
4. ✅ Configurar repositório de estatísticas (mock inicial)

### Fase 2: Componentes Visuais (2-3 dias)
1. ✅ Criar `HomeHeader` com toggle online/offline
2. ✅ Criar `DailyStatsSection` com 4 cards
3. ✅ Criar `DailyGoalCard` com barra de progresso
4. ✅ Adaptar `CompactMapSection`
5. ✅ Enriquecer `CorridaCard`

### Fase 3: Integração (1 dia)
1. ✅ Montar nova `HomeScreen` com todos os componentes
2. ✅ Adicionar animações de entrada
3. ✅ Testar scroll e performance

### Fase 4: Refinamento (1 dia)
1. ✅ Ajustar espaçamentos e tamanhos
2. ✅ Validar acessibilidade
3. ✅ Testar em diferentes tamanhos de tela
4. ✅ Validar contraste de cores

---

## 💡 Próximos Passos

1. **Revisar e aprovar este documento**
2. **Confirmar a hierarquia de informações**
3. **Definir prioridades de implementação**
4. **Preparar dados mockados para desenvolvimento**
5. **Iniciar implementação por fases**

---

**Objetivo Final**: Criar uma tela inicial que não só mostre as corridas disponíveis, mas que **motive, inspire e empodere** os entregadores, mostrando de forma clara e transparente seus ganhos, progresso e valor que a Pyloto proporciona.

🇧🇷 **Pyloto - Transformando vidas através da tecnologia**
