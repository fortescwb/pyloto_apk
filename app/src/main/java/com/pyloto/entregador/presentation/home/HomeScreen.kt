package com.pyloto.entregador.presentation.home

import android.location.Location
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.Circle
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.pyloto.entregador.domain.model.Corrida
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onCorridaClick: (String) -> Unit,
    onPerfilClick: () -> Unit,
    onHistoricoClick: () -> Unit,
    onNotificacoesClick: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text = "Entregas disponiveis",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = if (uiState.modoVisualizacao == HomeModoVisualizacao.MAPA) {
                                "Mapa de coleta com raio de 200m"
                            } else {
                                "${uiState.corridas.size} corridas no painel padrao"
                            },
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.loadCorridas() }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Atualizar corridas"
                        )
                    }
                    IconButton(onClick = onNotificacoesClick) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notificacoes"
                        )
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = true,
                    onClick = {},
                    icon = { Icon(Icons.AutoMirrored.Filled.ListAlt, contentDescription = null) },
                    label = { Text("Inicio") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onHistoricoClick,
                    icon = { Icon(Icons.Default.History, contentDescription = null) },
                    label = { Text("Historico") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onPerfilClick,
                    icon = { Icon(Icons.Default.Person, contentDescription = null) },
                    label = { Text("Perfil") }
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ModoVisualizacaoToggle(
                modoSelecionado = uiState.modoVisualizacao,
                onModoSelecionado = viewModel::alterarModoVisualizacao
            )

            when (uiState.modoVisualizacao) {
                HomeModoVisualizacao.PADRAO -> {
                    HomePadraoContent(
                        uiState = uiState,
                        onCorridaClick = onCorridaClick,
                        onRetry = viewModel::loadCorridas
                    )
                }

                HomeModoVisualizacao.MAPA -> {
                    HomeMapaContent(
                        uiState = uiState,
                        onCorridaClick = onCorridaClick,
                        onRetry = viewModel::loadCorridas
                    )
                }
            }
        }
    }
}

@Composable
private fun ModoVisualizacaoToggle(
    modoSelecionado: HomeModoVisualizacao,
    onModoSelecionado: (HomeModoVisualizacao) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
            selected = modoSelecionado == HomeModoVisualizacao.PADRAO,
            onClick = { onModoSelecionado(HomeModoVisualizacao.PADRAO) },
            label = { Text("Padrao") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ListAlt,
                    contentDescription = null
                )
            }
        )
        FilterChip(
            selected = modoSelecionado == HomeModoVisualizacao.MAPA,
            onClick = { onModoSelecionado(HomeModoVisualizacao.MAPA) },
            label = { Text("Modo mapa") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Map,
                    contentDescription = null
                )
            }
        )
    }
}

@Composable
private fun HomePadraoContent(
    uiState: HomeUiState,
    onCorridaClick: (String) -> Unit,
    onRetry: () -> Unit
) {
    when {
        uiState.isLoading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        uiState.erro != null -> {
            HomeMensagemEstado(
                titulo = "Falha ao carregar corridas",
                mensagem = uiState.erro,
                actionText = "Tentar novamente",
                onAction = onRetry
            )
        }

        uiState.corridas.isEmpty() -> {
            HomeMensagemEstado(
                titulo = "Nenhuma corrida disponivel",
                mensagem = "Atualize em instantes para encontrar novas coletas.",
                actionText = "Atualizar",
                onAction = onRetry
            )
        }

        else -> {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(uiState.corridas, key = { it.id }) { corrida ->
                    CorridaCard(
                        corrida = corrida,
                        onCorridaClick = onCorridaClick
                    )
                }
            }
        }
    }
}

@Composable
private fun CorridaCard(
    corrida: Corrida,
    onCorridaClick: (String) -> Unit
) {
    val currencyFormatter = remember { NumberFormat.getCurrencyInstance(Locale("pt", "BR")) }

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = corrida.cliente.nome,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                AssistChip(
                    onClick = {},
                    label = { Text(text = formatKm(corrida.distanciaKm)) }
                )
            }

            Text(
                text = "Coleta: ${corrida.origem.enderecoFormatado}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Entrega: ${corrida.destino.enderecoFormatado}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = currencyFormatter.format(corrida.valor),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "${corrida.tempoEstimadoMin} min",
                    style = MaterialTheme.typography.labelLarge
                )
            }

            Button(
                onClick = { onCorridaClick(corrida.id) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Ver detalhes")
            }
        }
    }
}

@Composable
private fun HomeMapaContent(
    uiState: HomeUiState,
    onCorridaClick: (String) -> Unit,
    onRetry: () -> Unit
) {
    val fallbackCenter = remember { LatLng(-23.55052, -46.633308) }
    val userCenter = uiState.localizacaoAtual?.let { LatLng(it.latitude, it.longitude) } ?: fallbackCenter
    val zoom = if (uiState.localizacaoAtual != null) 16f else 12f

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(userCenter, zoom)
    }

    val corridasNoRaio = remember(uiState.corridas, uiState.localizacaoAtual) {
        filtrarCorridasNoRaio(
            corridas = uiState.corridas,
            localizacaoAtual = uiState.localizacaoAtual,
            raioMetros = MAPA_RAIO_METROS
        )
    }

    LaunchedEffect(userCenter.latitude, userCenter.longitude, uiState.modoVisualizacao) {
        cameraPositionState.animate(
            update = CameraUpdateFactory.newLatLngZoom(userCenter, zoom)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(MaterialTheme.shapes.large)
    ) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(isMyLocationEnabled = false),
            uiSettings = MapUiSettings(
                myLocationButtonEnabled = false,
                zoomControlsEnabled = false,
                compassEnabled = true
            )
        ) {
            if (uiState.localizacaoAtual != null) {
                Circle(
                    center = userCenter,
                    radius = MAPA_RAIO_METROS.toDouble(),
                    fillColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                    strokeColor = MaterialTheme.colorScheme.primary,
                    strokeWidth = 4f
                )
                Marker(
                    state = MarkerState(position = userCenter),
                    title = "Sua localizacao"
                )
            }

            corridasNoRaio.forEach { corrida ->
                val origem = LatLng(corrida.corrida.origem.latitude, corrida.corrida.origem.longitude)
                Marker(
                    state = MarkerState(position = origem),
                    title = "Coleta: ${corrida.corrida.cliente.nome}",
                    snippet = "${corrida.distanciaMetros.toInt()}m de voce"
                )
            }
        }

        Surface(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(12.dp),
            tonalElevation = 6.dp,
            shadowElevation = 6.dp,
            shape = MaterialTheme.shapes.medium
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Raio de busca: 200m",
                    style = MaterialTheme.typography.labelLarge
                )
                Text(
                    text = "${corridasNoRaio.size} coletas proximas",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        when {
            uiState.isLoading -> {
                Surface(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp),
                    tonalElevation = 8.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.height(20.dp),
                            strokeWidth = 2.dp
                        )
                        Text("Atualizando mapa...")
                    }
                }
            }

            uiState.erro != null -> {
                Surface(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp),
                    color = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(uiState.erro)
                        OutlinedButton(onClick = onRetry) {
                            Text("Tentar novamente")
                        }
                    }
                }
            }

            uiState.localizacaoAtual == null -> {
                Surface(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp),
                    tonalElevation = 8.dp
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Nao foi possivel obter sua localizacao.",
                            style = MaterialTheme.typography.titleSmall
                        )
                        Text(
                            text = "Ative o GPS para visualizar coletas em ate 200m.",
                            style = MaterialTheme.typography.bodySmall
                        )
                        OutlinedButton(onClick = onRetry) {
                            Text("Atualizar")
                        }
                    }
                }
            }

            corridasNoRaio.isEmpty() -> {
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                        .navigationBarsPadding(),
                    tonalElevation = 8.dp
                ) {
                    Text(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        text = "Sem coletas disponiveis no raio de 200m.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            else -> {
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(16.dp)
                        .navigationBarsPadding(),
                    tonalElevation = 10.dp,
                    shadowElevation = 10.dp,
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.98f)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Coletas proximas",
                            style = MaterialTheme.typography.titleMedium
                        )
                        corridasNoRaio.take(3).forEach { corrida ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(2.dp)
                                ) {
                                    Text(
                                        text = corrida.corrida.cliente.nome,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                    Text(
                                        text = "${corrida.distanciaMetros.toInt()}m - ${corrida.corrida.origem.bairro}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                OutlinedButton(onClick = { onCorridaClick(corrida.corrida.id) }) {
                                    Text("Detalhes")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeMensagemEstado(
    titulo: String,
    mensagem: String,
    actionText: String,
    onAction: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = titulo,
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = mensagem,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            OutlinedButton(onClick = onAction) {
                Text(actionText)
            }
        }
    }
}

private data class CorridaComDistancia(
    val corrida: Corrida,
    val distanciaMetros: Float
)

private fun filtrarCorridasNoRaio(
    corridas: List<Corrida>,
    localizacaoAtual: HomeLocation?,
    raioMetros: Int
): List<CorridaComDistancia> {
    val currentLocation = localizacaoAtual ?: return emptyList()
    val userLatLng = LatLng(currentLocation.latitude, currentLocation.longitude)

    return corridas.mapNotNull { corrida ->
        val coleta = LatLng(corrida.origem.latitude, corrida.origem.longitude)
        val distancia = calcularDistanciaMetros(userLatLng, coleta)
        if (distancia <= raioMetros) {
            CorridaComDistancia(corrida = corrida, distanciaMetros = distancia)
        } else {
            null
        }
    }.sortedBy { it.distanciaMetros }
}

private fun calcularDistanciaMetros(origem: LatLng, destino: LatLng): Float {
    val resultado = FloatArray(1)
    Location.distanceBetween(
        origem.latitude,
        origem.longitude,
        destino.latitude,
        destino.longitude,
        resultado
    )
    return resultado.first()
}

private fun formatKm(distanciaKm: Double): String {
    return String.format(Locale("pt", "BR"), "%.1f km", distanciaKm)
}

private const val MAPA_RAIO_METROS = 200
