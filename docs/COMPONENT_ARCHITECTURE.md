# 🏗️ Arquitetura de Componentes - Nova Home Screen

## 📐 Visão Geral da Estrutura

```
┌─────────────────────────────────────────────────────────┐
│                    NewHomeScreen                        │  ← Tela Principal
│  ┌───────────────────────────────────────────────────┐ │
│  │              Scaffold (Material3)                 │ │
│  │                                                   │ │
│  │  ┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓  │ │
│  │  ┃          HomeHeader (FIXO)                 ┃  │ │  ← Componente 1
│  │  ┃  - Logo Pyloto (dourado)                   ┃  │ │
│  │  ┃  - Toggle Online/Offline                   ┃  │ │
│  │  ┃  - Localização atual                       ┃  │ │
│  │  ┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛  │ │
│  │                                                   │ │
│  │  ╔═══════════════════════════════════════════╗  │ │
│  │  ║     LazyColumn (Scroll Container)         ║  │ │
│  │  ║                                           ║  │ │
│  │  ║  ┌─────────────────────────────────────┐ ║  │ │
│  │  ║  │    DailyStatsSection               │ ║  │ │  ← Componente 2
│  │  ║  │  ┌──────┐  ┌──────┐                │ ║  │ │
│  │  ║  │  │Ganhos│  │Entre-│                │ ║  │ │
│  │  ║  │  │(Gold)│  │gas   │                │ ║  │ │
│  │  ║  │  └──────┘  └──────┘                │ ║  │ │
│  │  ║  │  ┌──────┐  ┌──────┐                │ ║  │ │
│  │  ║  │  │Tempo │  │Tempo │                │ ║  │ │
│  │  ║  │  │Online│  │Rest. │                │ ║  │ │
│  │  ║  │  └──────┘  └──────┘                │ ║  │ │
│  │  ║  └─────────────────────────────────────┘ ║  │ │
│  │  ║                                           ║  │ │
│  │  ║  ┌─────────────────────────────────────┐ ║  │ │
│  │  ║  │      DailyGoalCard                  │ ║  │ │  ← Componente 3
│  │  ║  │  🎯 Meta do Dia: R$ 300,00          │ ║  │ │
│  │  ║  │  ████████████░░░░ 81%               │ ║  │ │
│  │  ║  │  Faltam R$ 54,50                    │ ║  │ │
│  │  ║  └─────────────────────────────────────┘ ║  │ │
│  │  ║                                           ║  │ │
│  │  ║  ┌─────────────────────────────────────┐ ║  │ │
│  │  ║  │    CompactMapSection (opcional)     │ ║  │ │  ← Componente 4
│  │  ║  │  [Google Maps integrado]            │ ║  │ │
│  │  ║  │  📍 3 pedidos próximos              │ ║  │ │
│  │  ║  └─────────────────────────────────────┘ ║  │ │
│  │  ║                                           ║  │ │
│  │  ║  ┌─────────────────────────────────────┐ ║  │ │
│  │  ║  │  AvailableOrdersHeader              │ ║  │ │
│  │  ║  │  Pedidos Disponíveis  3 disponíveis │ ║  │ │
│  │  ║  └─────────────────────────────────────┘ ║  │ │
│  │  ║                                           ║  │ │
│  │  ║  ┌─────────────────────────────────────┐ ║  │ │
│  │  ║  │  EnrichedCorridaCard #1             │ ║  │ │  ← Componente 5
│  │  ║  │  🔥 PRIORITÁRIO                     │ ║  │ │
│  │  ║  │  [R$ 18,50] 2.3 km • 25 min        │ ║  │ │
│  │  ║  │  🏪 Sabor Brasileiro                │ ║  │ │
│  │  ║  │  [ACEITAR]     [DETALHES]          │ ║  │ │
│  │  ║  └─────────────────────────────────────┘ ║  │ │
│  │  ║                                           ║  │ │
│  │  ║  ┌─────────────────────────────────────┐ ║  │ │
│  │  ║  │  EnrichedCorridaCard #2             │ ║  │ │
│  │  ║  │  ...                                │ ║  │ │
│  │  ║  └─────────────────────────────────────┘ ║  │ │
│  │  ║                                           ║  │ │
│  │  ╚═══════════════════════════════════════════╝  │ │
│  │                                                   │ │
│  │  ┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓  │ │
│  │  ┃   EnhancedBottomNavigation (FIXO)       ┃  │ │  ← Componente 6
│  │  ┃   🏠 Início │ 📋 Corridas │ 💰 Ganhos   ┃  │ │
│  │  ┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛  │ │
│  └───────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────┘
```

---

## 🧩 Componentes Detalhados

### 1. HomeHeader

**Arquivo**: `components/HomeHeader.kt`
**Responsabilidade**: Header premium com identidade Pyloto

```kotlin
HomeHeader(
    isOnline: Boolean,           // Estado online/offline
    cidade: String,              // "Ponta Grossa, PR"
    regiao: String,              // "Centro"
    onToggleOnline: () -> Unit   // Callback para toggle
)
```

**Sub-componentes**:
- `OnlineToggleButton()` - Botão de toggle
- `LocationChip()` - Chip de localização

**Cores**:
- Fundo: Verde Militar (#3D5A40)
- Logo: Dourado (#D4AF37)
- Toggle online: Dourado
- Toggle offline: Cinza

**Altura**: ~120dp

---

### 2. DailyStatsSection

**Arquivo**: `components/DailyStatsSection.kt`
**Responsabilidade**: Dashboard de estatísticas do dia

```kotlin
DailyStatsSection(
    earnings: Double,        // 245.50
    deliveries: Int,         // 12
    timeOnline: String,      // "5h 32m"
    timeRemaining: String    // "4h 28m"
)
```

**Sub-componentes**:
- `EarningsCard()` - Card de ganhos (gradiente dourado)
- `DeliveriesCard()` - Card de entregas (branco + borda verde)
- `TimeOnlineCard()` - Card de tempo online (branco + borda azul)
- `TimeRemainingCard()` - Card de tempo restante (branco + borda verde)
- `StatCard()` - Componente genérico reutilizável

**Layout**: Grid 2x2
**Altura**: ~220dp (incluindo título "Hoje")

---

### 3. DailyGoalCard

**Arquivo**: `components/DailyGoalCard.kt`
**Responsabilidade**: Meta diária com progresso

```kotlin
DailyGoalCard(
    currentEarnings: Double,  // 245.50
    goalAmount: Double        // 300.0
)
```

**Sub-componentes**:
- `ProgressBar()` - Barra de progresso com gradiente
- `GoalStatusMessage()` - Mensagem de status (faltante ou atingido)

**Alternativa**:
- `CompactDailyGoalCard()` - Versão mais compacta (80dp vs 110dp)

**Cores**:
- Barra de progresso: Gradiente Verde → Dourado
- Ícone: Verde (em progresso) ou Dourado (atingido)

**Altura**: ~110dp

---

### 4. CompactMapSection (Opcional)

**Arquivo**: `components/CompactMapSection.kt` (a criar)
**Responsabilidade**: Mapa compacto integrado

```kotlin
CompactMapSection(
    location: HomeLocation,
    availableOrders: Int,
    onExpand: () -> Unit
)
```

**Features**:
- Integração com Google Maps
- Marcadores de pedidos (dourado)
- Marcador do entregador (azul)
- Botão de expandir para fullscreen

**Altura**: 200dp

---

### 5. EnrichedCorridaCard

**Arquivo**: `components/EnrichedCorridaCard.kt`
**Responsabilidade**: Card de pedido enriquecido

```kotlin
EnrichedCorridaCard(
    corrida: Corrida,
    onAccept: (String) -> Unit,
    onViewDetails: (String) -> Unit
)
```

**Sub-componentes**:
- `ValueAndInfoRow()` - Valor + distância + tempo
- `LocationSection()` - Origem ou destino com ícone
- `ActionButtonsRow()` - Botões Aceitar e Detalhes
- `PriorityBadge()` - Badge prioritário (se aplicável)

**Cores**:
- Valor: Fundo dourado (#D4AF37)
- Ícone origem: Circle verde (#3D5A40)
- Ícone destino: Circle azul (#2C5F7D)
- Botão aceitar: Verde
- Badge prioritário: Dourado

**Altura**: ~280dp (varia com conteúdo)

---

### 6. EnhancedBottomNavigation

**Arquivo**: `components/EnhancedBottomNavigation.kt` (ou inline)
**Responsabilidade**: Navegação inferior melhorada

```kotlin
EnhancedBottomNavigation(
    selectedTab: String,
    onHomeClick: () -> Unit,
    onCorridasClick: () -> Unit,
    onGanhosClick: () -> Unit,
    onPerfilClick: () -> Unit
)
```

**Abas**:
1. 🏠 Início
2. 📋 Corridas
3. 💰 Ganhos (NOVA)
4. 👤 Perfil

**Cores**:
- Ícone ativo: Verde Militar
- Texto ativo: Verde Militar
- Indicador: Verde com 10% opacity

**Altura**: 80dp

---

## 🔄 Fluxo de Dados

```
┌─────────────────────────────────────────┐
│           HomeViewModel                  │
│                                         │
│  uiState: StateFlow<HomeUiState>       │
│  ├─ isOnline: Boolean                   │
│  ├─ dailyStats: DailyStats              │
│  │   ├─ earnings: Double                │
│  │   ├─ deliveries: Int                 │
│  │   └─ timeOnlineMinutes: Int          │
│  ├─ dailyGoal: Double                   │
│  ├─ corridas: List<Corrida>             │
│  └─ localizacaoAtual: HomeLocation?     │
│                                         │
│  Methods:                                │
│  ├─ toggleOnlineStatus()                │
│  ├─ loadDailyStats()                    │
│  ├─ loadCorridas()                      │
│  └─ aceitarCorrida(id)                  │
└─────────────────────────────────────────┘
            ↓
┌─────────────────────────────────────────┐
│           NewHomeScreen                  │
│                                         │
│  collectAsState() → uiState             │
│                                         │
│  Passa dados para componentes:         │
│  ├─ HomeHeader                          │
│  ├─ DailyStatsSection                   │
│  ├─ DailyGoalCard                       │
│  ├─ CompactMapSection                   │
│  └─ EnrichedCorridaCard (lista)         │
└─────────────────────────────────────────┘
```

---

## 🎨 Hierarquia de Cores por Componente

| Componente | Cor Primária | Cor Secundária | Uso |
|------------|--------------|----------------|-----|
| **HomeHeader** | Verde Militar (fundo) | Dourado (logo + toggle) | Identidade forte |
| **EarningsCard** | Dourado (gradiente) | Branco (texto) | Destaque máximo |
| **DeliveriesCard** | Branco (fundo) | Verde (borda + ícone) | Aprovação |
| **TimeOnlineCard** | Branco (fundo) | Azul (borda + ícone) | Informação |
| **TimeRemainingCard** | Branco (fundo) | Verde (borda + ícone) | Status |
| **DailyGoalCard** | Branco (fundo) | Verde→Dourado (barra) | Motivação |
| **EnrichedCorridaCard** | Branco (fundo) | Dourado (valor) | Destaque financeiro |
| **PriorityBadge** | Dourado (fundo) | Preto (texto) | Urgência |
| **BottomNav** | Branco (fundo) | Verde (ativo) | Navegação |

---

## 📏 Especificações de Layout

### Espaçamentos Padrão:
```kotlin
// Padding externo
horizontal = 16.dp
vertical = 12.dp

// Espaçamento entre seções
verticalArrangement = Arrangement.spacedBy(16.dp)

// Espaçamento entre cards no grid
grid spacing = 12.dp

// Border radius padrão
card shape = RoundedCornerShape(16.dp)
```

### Elevações:
```kotlin
Header: 4.dp
Cards: 4.dp
Bottom Nav: 8.dp
Modal: 16.dp
```

### Tipografia:
```kotlin
Header logo: headlineMedium (bold, dourado)
Section titles: titleLarge (semibold, preto)
Card labels: bodyMedium (regular, cinza)
Card values: headlineMedium (bold, preto ou branco)
```

---

## 🔧 Dependências entre Componentes

```
NewHomeScreen (root)
├── Scaffold
│   ├── topBar: HomeHeader ✓ Independente
│   ├── content: LazyColumn
│   │   ├── DailyStatsSection ✓ Independente
│   │   ├── DailyGoalCard
│   │   │   └── Depende de: dailyStats.earnings
│   │   ├── CompactMapSection
│   │   │   └── Depende de: localizacaoAtual
│   │   ├── AvailableOrdersHeader
│   │   │   └── Depende de: corridas.size
│   │   └── items(corridas)
│   │       └── EnrichedCorridaCard ✓ Independente
│   └── bottomBar: EnhancedBottomNavigation ✓ Independente
```

**Legenda**:
- ✓ Independente = Não tem dependências complexas, funciona isoladamente
- Depende de = Precisa de dados específicos do state

---

## 🧪 Estratégia de Testes

### Unit Tests:
```kotlin
// DailyStats calculations
DailyStatsTest.kt
├─ test_timeOnlineFormatted()
├─ test_timeRemainingFormatted()
└─ test_goalProgress()

// ViewModel logic
HomeViewModelTest.kt
├─ test_toggleOnlineStatus()
├─ test_loadDailyStats()
└─ test_aceitarCorrida()
```

### UI Tests (Compose):
```kotlin
// Component tests
HomeHeaderTest.kt
├─ test_toggleButton_online()
├─ test_toggleButton_offline()
└─ test_locationChip_display()

DailyStatsSectionTest.kt
├─ test_earningsCard_displays_correctly()
├─ test_grid_layout_2x2()
└─ test_animations_play()

EnrichedCorridaCardTest.kt
├─ test_priorityBadge_shows_when_priority()
├─ test_value_highlighted()
└─ test_actionButtons_clickable()
```

---

## 🚀 Ordem de Implementação Recomendada

### Fase 1: Fundação (Dia 1-2)
1. ✅ Atualizar `HomeUiState` com `DailyStats`
2. ✅ Atualizar `HomeViewModel` com métodos
3. ✅ Criar pasta `components/`

### Fase 2: Componentes Visuais (Dia 3-5)
4. ✅ Implementar `HomeHeader`
5. ✅ Implementar `DailyStatsSection`
6. ✅ Implementar `DailyGoalCard`
7. ✅ Implementar `EnrichedCorridaCard`

### Fase 3: Integração (Dia 6-7)
8. ✅ Montar nova `HomeScreen` com todos os componentes
9. ✅ Adicionar animações de entrada
10. ✅ Implementar `CompactMapSection` (opcional)

### Fase 4: Refinamento (Dia 8-9)
11. ✅ Ajustar espaçamentos e responsividade
12. ✅ Validar acessibilidade
13. ✅ Testes e QA

---

## 📦 Estrutura de Arquivos Final

```
presentation/home/
├── HomeScreen.kt              ← Atualizar com novo layout
├── HomeViewModel.kt           ← Adicionar métodos
├── HomeUiState.kt             ← Adicionar DailyStats
└── components/                ← CRIAR ESTA PASTA
    ├── HomeHeader.kt
    ├── DailyStatsSection.kt
    ├── DailyGoalCard.kt
    ├── EnrichedCorridaCard.kt
    ├── CompactMapSection.kt   (opcional)
    └── README.md              (documentação local)
```

---

## 🎯 Checklist de Completude

### Por Componente:

#### HomeHeader
- [ ] Logo Pyloto dourado implementado
- [ ] Toggle online/offline funcional
- [ ] Chip de localização
- [ ] Cores corretas (verde + dourado)
- [ ] Preview funcionando

#### DailyStatsSection
- [ ] Grid 2x2 responsivo
- [ ] Card de ganhos com gradiente
- [ ] 4 cards implementados
- [ ] Animações de entrada
- [ ] Formatação de moeda

#### DailyGoalCard
- [ ] Barra de progresso animada
- [ ] Cálculo automático
- [ ] Mensagens motivacionais
- [ ] Cores corretas (verde → dourado)

#### EnrichedCorridaCard
- [ ] Badge prioritário
- [ ] Valor em destaque
- [ ] Ícones coloridos
- [ ] Botões de ação
- [ ] Layout responsivo

#### Integration
- [ ] Todos os componentes integrados
- [ ] LazyColumn otimizada
- [ ] Estados de loading/error/empty
- [ ] Bottom navigation atualizado

---

## 🌟 Conclusão

Esta arquitetura foi projetada para ser:

- **Modular**: Cada componente funciona independentemente
- **Reutilizável**: Componentes genéricos (StatCard, LocationSection)
- **Testável**: Lógica separada da UI
- **Escalável**: Fácil adicionar novos componentes
- **Performática**: LazyColumn + animações otimizadas
- **Acessível**: Contrastes validados, TalkBack compatível

**Resultado**: Uma tela inicial que **empodera, motiva e valoriza** os entregadores da Pyloto. 🚀🇧🇷

---

_Para implementação, siga: [QUICK_START.md](./QUICK_START.md)_
