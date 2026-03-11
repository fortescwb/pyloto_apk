package com.pyloto.entregador.presentation.corrida.ativa

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PinDrop
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

private enum class DeliveryStage(val title: String, val subtitle: String, val action: String) {
    PICKUP_START("Ir para coleta", "Mapa para endereco de coleta.", "Iniciar deslocamento"),
    PICKUP_ARRIVAL("Chegada na coleta", "Confirme ao chegar no ponto de coleta.", "Cheguei na coleta"),
    PICKUP_INFO("Dados da coleta", "Informacoes do solicitante e item.", "Coletei"),
    DROPOFF_START("Ir para destino", "Mapa para endereco de entrega.", "Estou a caminho"),
    DROPOFF_ARRIVAL("Chegada no destino", "Confirme ao chegar no destino.", "Cheguei no destino"),
    DELIVERY_PROOF("Confirmacao", "Numero no cartao + foto do cartao.", "Confirmar entrega")
}

private data class Contact(val name: String, val phone: String, val note: String)
private data class Route(val from: String, val to: String, val km: String, val eta: String)
private data class ActiveOrder(
    val id: String,
    val requester: Contact,
    val pickupContact: Contact,
    val dropoffContact: Contact,
    val itemType: String,
    val itemDescription: String,
    val pickupRoute: Route,
    val dropoffRoute: Route
)

private val DEFAULT_ORDER = ActiveOrder(
    id = "PLT-2026-02481",
    requester = Contact("Ana Martins", "(42) 99123-7788", "Solicitante do pedido"),
    pickupContact = Contact("Carlos Pereira", "(42) 99888-1100", "Recepcao, Laboratorio Leste"),
    dropoffContact = Contact("Renata Souza", "(42) 99911-4455", "Portaria principal, bloco B"),
    itemType = "Medicamento",
    itemDescription = "Envelope lacrado com receita e unidade termica.",
    pickupRoute = Route("Av. Vicente Machado, 1150", "Rua das Palmeiras, 90", "3,2 km", "11 min"),
    dropoffRoute = Route("Rua das Palmeiras, 90", "Rua Monteiro Lobato, 421", "8,6 km", "24 min")
)

private fun orderForCorrida(corridaId: String): ActiveOrder {
    val normalizedId = corridaId.takeIf { it.isNotBlank() } ?: DEFAULT_ORDER.id

    return when (normalizedId) {
        "corrida-001" -> DEFAULT_ORDER.copy(
            id = normalizedId.uppercase(),
            requester = Contact("Restaurante Sabor & Arte", "(42) 3025-1122", "Solicitante do pedido"),
            pickupContact = Contact("Carla Moraes", "(42) 99820-1010", "Balcao principal do restaurante"),
            dropoffContact = Contact("Marcelo Lima", "(42) 99920-3030", "Portaria residencial"),
            itemType = "Vestuário",
            itemDescription = "Sacola lacrada com 2 itens.",
            pickupRoute = Route("Rua Balduino Taques, 300", "Rua Balduino Taques, 300", "0,7 km", "4 min"),
            dropoffRoute = Route("Rua Balduino Taques, 300", "Rua XV de Novembro, 850", "1,2 km", "8 min")
        )
        "corrida-002" -> DEFAULT_ORDER.copy(
            id = normalizedId.uppercase(),
            requester = Contact("Padaria Grao Dourado", "(42) 3026-3344", "Solicitante do pedido"),
            pickupContact = Contact("Vanessa Rocha", "(42) 99915-1200", "Caixa 2, entrada lateral"),
            dropoffContact = Contact("Eduardo Alves", "(42) 99944-2201", "Recepcao torre azul"),
            itemType = "Documento",
            itemDescription = "Pacote pequeno com nota fiscal e comprovante.",
            pickupRoute = Route("Av. Visconde de Taunay, 1500", "Av. Visconde de Taunay, 1500", "2,3 km", "9 min"),
            dropoffRoute = Route("Av. Visconde de Taunay, 1500", "Rua Engenheiro Schamber, 245", "3,8 km", "15 min")
        )
        "corrida-003" -> DEFAULT_ORDER.copy(
            id = normalizedId.uppercase(),
            requester = Contact("Farmacia Saude Total", "(42) 3028-5566", "Solicitante do pedido"),
            pickupContact = Contact("Igor Mendes", "(42) 99812-1700", "Guiche de retirada rapida"),
            dropoffContact = Contact("Patricia Costa", "(42) 99970-2210", "Portaria comercial"),
            itemType = "Medicamento",
            itemDescription = "Envelope termico identificado.",
            pickupRoute = Route("Rua Coronel Dulcidio, 72", "Rua Coronel Dulcidio, 72", "1,0 km", "5 min"),
            dropoffRoute = Route("Rua Coronel Dulcidio, 72", "Rua Benjamin Constant, 410", "0,8 km", "5 min")
        )
        "corrida-004" -> DEFAULT_ORDER.copy(
            id = normalizedId.uppercase(),
            requester = Contact("PetShop Amigo Fiel", "(42) 3029-7788", "Solicitante do pedido"),
            pickupContact = Contact("Luan Ferreira", "(42) 99877-4500", "Loja 1, caixa principal"),
            dropoffContact = Contact("Nathalia Freitas", "(42) 99911-7766", "Residencia, interfone 12"),
            itemType = "Eletrônico",
            itemDescription = "Caixa media com acessorios pet.",
            pickupRoute = Route("Av. Carlos Cavalcanti, 4748", "Av. Carlos Cavalcanti, 4748", "4,9 km", "14 min"),
            dropoffRoute = Route("Av. Carlos Cavalcanti, 4748", "Rua Tiburcio Pedro Ferreira, 60", "6,2 km", "22 min")
        )
        "corrida-005" -> DEFAULT_ORDER.copy(
            id = normalizedId.uppercase(),
            requester = Contact("Acai da Barra", "(42) 99801-2233", "Solicitante do pedido"),
            pickupContact = Contact("Bruna Nogueira", "(42) 99865-3322", "Retirada no caixa frontal"),
            dropoffContact = Contact("Rafael Prado", "(42) 99931-4451", "Condominio, bloco C"),
            itemType = "Outros",
            itemDescription = "Pacote refrigerado com 4 unidades.",
            pickupRoute = Route("Rua Dr. Colares, 180", "Rua Dr. Colares, 180", "1,8 km", "7 min"),
            dropoffRoute = Route("Rua Dr. Colares, 180", "Rua Santos Dumont, 320", "4,5 km", "18 min")
        )
        else -> DEFAULT_ORDER.copy(id = normalizedId.uppercase())
    }
}

@Composable
fun CorridaAtivaScreen(
    corridaId: String,
    onCorridaFinalizada: () -> Unit,
    onChatClick: (String) -> Unit
) {
    val order = remember(corridaId) { orderForCorrida(corridaId) }
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var stepIndex by rememberSaveable(corridaId) { mutableIntStateOf(0) }
    var cardNumber by rememberSaveable(corridaId) { mutableStateOf("") }
    var proofCaptured by rememberSaveable(corridaId) { mutableStateOf(false) }
    val step = DeliveryStage.entries[stepIndex]

    @OptIn(ExperimentalMaterial3Api::class)
    Scaffold(
        snackbarHost = { SnackbarHost(snackbar) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Entrega em andamento")
                        Text(order.id, style = MaterialTheme.typography.labelMedium)
                    }
                },
                actions = {
                    IconButton(onClick = { onChatClick(order.id) }) {
                        Icon(Icons.Default.Chat, contentDescription = "Chat")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Spacer(Modifier.height(4.dp))
            ProgressHeader(step = step, index = stepIndex, total = DeliveryStage.entries.size)
            when (step) {
                DeliveryStage.PICKUP_START -> {
                    MapCard("Rota ate a coleta", order.pickupRoute)
                    SlideAction("Deslize para iniciar deslocamento", step.action, Icons.Default.Navigation) { stepIndex += 1 }
                }
                DeliveryStage.PICKUP_ARRIVAL -> {
                    MapCard("Rota ate a coleta", order.pickupRoute)
                    SlideAction("Deslize para confirmar chegada", step.action, Icons.Default.LocationOn) { stepIndex += 1 }
                }
                DeliveryStage.PICKUP_INFO -> {
                    PickupInfoCard(order = order, onChatClick = { onChatClick(order.id) })
                    SlideAction("Deslize apos retirar o item", step.action, Icons.Default.ShoppingBag) { stepIndex += 1 }
                }
                DeliveryStage.DROPOFF_START -> {
                    MapCard("Rota ate o destino", order.dropoffRoute)
                    SlideAction("Deslize para iniciar entrega", step.action, Icons.Default.DirectionsBike) { stepIndex += 1 }
                }
                DeliveryStage.DROPOFF_ARRIVAL -> {
                    MapCard("Rota ate o destino", order.dropoffRoute)
                    SlideAction("Deslize para confirmar chegada", step.action, Icons.Default.PinDrop) { stepIndex += 1 }
                }
                DeliveryStage.DELIVERY_PROOF -> {
                    DeliveryProofCard(
                        order = order,
                        cardNumber = cardNumber,
                        onCardNumberChange = { cardNumber = it },
                        proofCaptured = proofCaptured,
                        onCaptureProof = { proofCaptured = true },
                        onChatClick = { onChatClick(order.id) }
                    )
                    SlideAction("Deslize para concluir", step.action, Icons.Default.Verified) {
                        when {
                            cardNumber.trim().isEmpty() -> scope.launch { snackbar.showSnackbar("Preencha o numero no cartao.") }
                            !proofCaptured -> scope.launch { snackbar.showSnackbar("Tire foto do cartao preenchido.") }
                            else -> onCorridaFinalizada()
                        }
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun ProgressHeader(step: DeliveryStage, index: Int, total: Int) {
    val progress = ((index + 1).toFloat() / total).coerceIn(0f, 1f)
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), elevation = CardDefaults.cardElevation(2.dp)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Etapa ${index + 1} de $total", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelLarge)
            Text(step.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Text(step.subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Box(Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(99.dp)).background(MaterialTheme.colorScheme.surfaceVariant)) {
                Box(Modifier.fillMaxHeight().fillMaxWidth(progress).clip(RoundedCornerShape(99.dp)).background(MaterialTheme.colorScheme.primary))
            }
        }
    }
}

@Composable
private fun MapCard(title: String, route: Route) {
    Card(elevation = CardDefaults.cardElevation(2.dp)) {
        Column {
            Box(
                Modifier.fillMaxWidth().height(210.dp).background(
                    Brush.linearGradient(listOf(MaterialTheme.colorScheme.primary.copy(0.9f), MaterialTheme.colorScheme.tertiary.copy(0.8f)))
                ),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Map, null, tint = Color.White, modifier = Modifier.size(34.dp))
                    Text("Mapa e rota (placeholder)", color = Color.White, fontWeight = FontWeight.SemiBold)
                    Text("${route.km} • ${route.eta}", color = Color.White)
                }
            }
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                RouteLine(Icons.Default.LocationOn, "Saida", route.from)
                RouteLine(Icons.Default.PinDrop, "Destino", route.to)
            }
        }
    }
}

@Composable
private fun RouteLine(icon: ImageVector, label: String, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Icon(icon, null, tint = MaterialTheme.colorScheme.primary)
        Column {
            Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(text, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun PickupInfoCard(order: ActiveOrder, onChatClick: () -> Unit) {
    Card(elevation = CardDefaults.cardElevation(2.dp)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("Informacoes para coleta", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            ContactCard("Solicitante", order.requester)
            ContactCard("Responsavel na coleta", order.pickupContact)
            Surface(shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.45f)) {
                Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Item para transporte", fontWeight = FontWeight.Medium)
                    Text("Categoria: ${order.itemType}")
                    Text(order.itemDescription, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            HorizontalDivider()
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onChatClick) { Icon(Icons.Default.Chat, null) }
                IconButton(onClick = {}) { Icon(Icons.Default.Call, null) }
                Text("Use chat/chamada para validar duvidas na coleta.", modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
private fun ContactCard(title: String, contact: Contact) {
    Surface(shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Default.Person, null, tint = MaterialTheme.colorScheme.primary)
                Text(title, fontWeight = FontWeight.Medium)
            }
            Text(contact.name)
            Text(contact.phone, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(contact.note, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun DeliveryProofCard(
    order: ActiveOrder,
    cardNumber: String,
    onCardNumberChange: (String) -> Unit,
    proofCaptured: Boolean,
    onCaptureProof: () -> Unit,
    onChatClick: () -> Unit
) {
    Card(elevation = CardDefaults.cardElevation(2.dp)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("Responsavel no destino", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            ContactCard("Responsavel na entrega", order.dropoffContact)
            Surface(shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)) {
                Text(
                    "Numero do pedido: ${order.id}\n1) Entregue o cartao.\n2) Preencha no verso o numero.\n3) Tire foto do cartao preenchido.\n4) Cartao fica com responsavel.",
                    modifier = Modifier.padding(12.dp),
                    style = MaterialTheme.typography.bodySmall
                )
            }
            OutlinedTextField(
                value = cardNumber,
                onValueChange = onCardNumberChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Numero preenchido no cartao") },
                singleLine = true
            )
            Button(onClick = onCaptureProof) {
                Icon(Icons.Default.CameraAlt, null)
                Spacer(Modifier.width(8.dp))
                Text(if (proofCaptured) "Foto registrada (placeholder)" else "Tirar foto do cartao")
            }
            Surface(
                Modifier.fillMaxWidth().height(110.dp).border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp)),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        if (proofCaptured) "Comprovante anexado." else "Preview da foto (placeholder).",
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onChatClick) { Icon(Icons.Default.Chat, null) }
                Text("Se houver divergencia, confirme com o solicitante no chat.", modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
private fun SlideAction(label: String, action: String, icon: ImageVector, onCompleted: () -> Unit) {
    val density = LocalDensity.current
    val thumb = 52.dp
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(action, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Medium)
        BoxWithConstraints(Modifier.fillMaxWidth().height(58.dp)) {
            val maxWidthPx = with(density) { maxWidth.toPx() }
            val thumbPx = with(density) { thumb.toPx() }
            val maxOffset = (maxWidthPx - thumbPx).coerceAtLeast(0f)
            var offset by remember(maxOffset) { mutableFloatStateOf(0f) }
            val progress = if (maxOffset == 0f) 0f else (offset / maxOffset).coerceIn(0f, 1f)
            Box(Modifier.matchParentSize().clip(RoundedCornerShape(99.dp)).background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.75f))) {
                Box(Modifier.fillMaxHeight().fillMaxWidth(progress).background(MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)))
                Text(label, Modifier.align(Alignment.Center).padding(horizontal = 56.dp), textAlign = TextAlign.Center)
            }
            Surface(
                modifier = Modifier
                    .offset { IntOffset(offset.roundToInt(), 0) }
                    .size(thumb)
                    .draggable(
                        orientation = Orientation.Horizontal,
                        state = rememberDraggableState { d -> offset = (offset + d).coerceIn(0f, maxOffset) },
                        onDragStopped = {
                            val complete = maxOffset > 0f && progress >= 0.82f
                            offset = 0f
                            if (complete) onCompleted()
                        }
                    ),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary
            ) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Icon(icon, null, tint = MaterialTheme.colorScheme.onPrimary)
                }
            }
        }
    }
}
