## Roadmap de Estruturação - App Entregador Android

---

## FASE 0: Setup Inicial do Repositório (Dia 1)

### **Estrutura de Diretórios**

```
pyloto-entregador-android/
│
├── .github/
│   └── workflows/
│       ├── ci.yml                    # Build + testes automáticos
│       └── release.yml               # Deploy automático
│
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/pyloto/entregador/
│   │   │   │   ├── core/              # Camada base
│   │   │   │   │   ├── di/            # Hilt modules
│   │   │   │   │   ├── network/       # Retrofit, interceptors
│   │   │   │   │   ├── database/      # Room entities, DAOs
│   │   │   │   │   ├── location/      # GPS service, geofencing
│   │   │   │   │   └── util/          # Extensions, constants
│   │   │   │   │
│   │   │   │   ├── data/              # Repositories, data sources
│   │   │   │   │   ├── repository/
│   │   │   │   │   ├── remote/        # API models
│   │   │   │   │   └── local/         # Cache models
│   │   │   │   │
│   │   │   │   ├── domain/            # Business logic
│   │   │   │   │   ├── model/         # Domain entities
│   │   │   │   │   ├── usecase/       # Use cases
│   │   │   │   │   └── repository/    # Repository interfaces
│   │   │   │   │
│   │   │   │   └── presentation/      # UI layer
│   │   │   │       ├── auth/
│   │   │   │       │   ├── login/
│   │   │   │       │   └── register/
│   │   │   │       ├── home/
│   │   │   │       ├── corrida/
│   │   │   │       │   ├── disponivel/
│   │   │   │       │   ├── ativa/
│   │   │   │       │   └── historico/
│   │   │   │       ├── perfil/
│   │   │   │       ├── chat/
│   │   │   │       └── navigation/
│   │   │   │
│   │   │   └── res/
│   │   │       ├── values/
│   │   │       │   ├── colors.xml     # Paleta Pyloto
│   │   │       │   ├── themes.xml
│   │   │       │   └── strings.xml
│   │   │       └── drawable/
│   │   │
│   │   ├── test/                      # Unit tests
│   │   └── androidTest/               # Instrumented tests
│   │
│   └── build.gradle.kts               # Dependencies do app
│
├── buildSrc/                          # Gerenciamento de versões
│   └── src/main/kotlin/
│       ├── Dependencies.kt
│       └── Versions.kt
│
├── docs/
│   ├── ADR/                           # Architecture Decision Records
│   │   ├── 001-arquitetura-mvvm.md
│   │   ├── 002-strategy-gps.md
│   │   └── 003-offline-first.md
│   ├── API.md                         # Contratos de API
│   └── SETUP.md                       # Instruções de dev
│
├── .gitignore
├── build.gradle.kts                   # Config do projeto
├── settings.gradle.kts
├── gradle.properties
└── README.md
```

---

## FASE 1: Fundação (Semana 1 - Dias 1-3)

### **1.1 Configuração do Projeto**

**build.gradle.kts (project level)**
```kotlin
plugins {
    id("com.android.application") version "8.2.0" apply false
    id("org.jetbrains.kotlin.android") version "1.9.20" apply false
    id("com.google.dagger.hilt.android") version "2.48" apply false
    id("com.google.gms.google-services") version "4.4.0" apply false
}
```

**build.gradle.kts (app level) - Dependências Core**
```kotlin
dependencies {
    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.20")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    
    // Android Core
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    
    // Compose
    implementation(platform("androidx.compose:compose-bom:2023.10.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.activity:activity-compose:1.8.1")
    implementation("androidx.navigation:navigation-compose:2.7.5")
    
    // Hilt (DI)
    implementation("com.google.dagger:hilt-android:2.48")
    kapt("com.google.dagger:hilt-compiler:2.48")
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0")
    
    // Network
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    
    // Database
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")
    
    // Location & Maps
    implementation("com.google.android.gms:play-services-location:21.0.1")
    implementation("com.google.android.gms:play-services-maps:18.2.0")
    implementation("com.google.maps.android:maps-compose:4.3.0")
    
    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))
    implementation("com.google.firebase:firebase-messaging-ktx")
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-crashlytics-ktx")
    
    // WorkManager
    implementation("androidx.work:work-runtime-ktx:2.9.0")
    
    // Image Loading
    implementation("io.coil-kt:coil-compose:2.5.0")
    
    // Testing
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.1.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
}
```

### **1.2 Paleta de Cores (res/values/colors.xml)**

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <!-- Paleta Pyloto Corp -->
    <color name="primary">#1A1A1A</color>           <!-- Preto elegante -->
    <color name="secondary">#8B4513</color>         <!-- Marrom sépia -->
    <color name="accent">#D4AF37</color>            <!-- Dourado premium -->
    <color name="background">#FFFFFF</color>        <!-- Branco puro (app limpo) -->
    <color name="surface">#F5F1E8</color>           <!-- Bege pergaminho -->
    <color name="text_primary">#2A2A2A</color>      <!-- Cinza escuro -->
    <color name="text_secondary">#6B6B6B</color>
    <color name="military_green">#3D5A40</color>    <!-- Verde militar -->
    <color name="tech_blue">#2C5F7D</color>         <!-- Azul técnico -->
    
    <!-- Estados -->
    <color name="status_approved">#3D5A40</color>   <!-- Verde -->
    <color name="status_pending">#8B4513</color>    <!-- Marrom -->
    <color name="status_rejected">#C4342D</color>   <!-- Vermelho (adicional) -->
    
    <!-- Transparências -->
    <color name="overlay_dark">#80000000</color>
    <color name="overlay_light">#40FFFFFF</color>
</resources>
```

### **1.3 Theme (res/values/themes.xml)**

```xml
<resources>
    <style name="Theme.PylotoEntregador" parent="Theme.Material3.Light.NoActionBar">
        <item name="colorPrimary">@color/tech_blue</item>
        <item name="colorOnPrimary">@color/background</item>
        <item name="colorSecondary">@color/accent</item>
        <item name="colorOnSecondary">@color/primary</item>
        <item name="colorTertiary">@color/military_green</item>
        <item name="android:colorBackground">@color/background</item>
        <item name="colorSurface">@color/surface</item>
        <item name="colorOnSurface">@color/text_primary</item>
    </style>
</resources>
```

---

## FASE 2: Camada Core (Semana 1 - Dias 4-5)

### **2.1 Network Setup**

**core/network/ApiService.kt**
```kotlin
interface ApiService {
    
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): ApiResponse<AuthToken>
    
    @GET("corridas/disponiveis")
    suspend fun getCorridasDisponiveis(
        @Query("lat") lat: Double,
        @Query("lng") lng: Double,
        @Query("raio") raio: Int = 5000
    ): ApiResponse<List<CorridaResponse>>
    
    @POST("corridas/{id}/aceitar")
    suspend fun aceitarCorrida(@Path("id") corridaId: String): ApiResponse<CorridaDetalhes>
    
    @POST("corridas/{id}/iniciar")
    suspend fun iniciarCorrida(@Path("id") corridaId: String): ApiResponse<Unit>
    
    @POST("corridas/{id}/finalizar")
    suspend fun finalizarCorrida(
        @Path("id") corridaId: String,
        @Body foto: FinalizacaoRequest
    ): ApiResponse<Unit>
    
    @POST("entregador/localizacao")
    suspend fun atualizarLocalizacao(@Body location: LocationUpdate): ApiResponse<Unit>
    
    @GET("entregador/perfil")
    suspend fun getPerfil(): ApiResponse<EntregadorPerfil>
}
```

**core/network/NetworkModule.kt**
```kotlin
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    
    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor,
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }
    
    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }
}
```

### **2.2 Database Setup**

**core/database/AppDatabase.kt**
```kotlin
@Database(
    entities = [
        CorridaEntity::class,
        LocationEntity::class,
        EntregadorEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun corridaDao(): CorridaDao
    abstract fun locationDao(): LocationDao
    abstract fun entregadorDao(): EntregadorDao
}
```

**core/database/entities/CorridaEntity.kt**
```kotlin
@Entity(tableName = "corridas")
data class CorridaEntity(
    @PrimaryKey val id: String,
    val clienteNome: String,
    val clienteTelefone: String,
    val enderecoOrigem: String,
    val enderecoDestino: String,
    val latOrigem: Double,
    val lngOrigem: Double,
    val latDestino: Double,
    val lngDestino: Double,
    val valorEntrega: Double,
    val distanciaKm: Double,
    val status: String, // DISPONIVEL, ACEITA, EM_ANDAMENTO, FINALIZADA
    val aceitaEm: Long?,
    val iniciadaEm: Long?,
    val finalizadaEm: Long?,
    val sincronizado: Boolean = false
)
```

### **2.3 Location Service**

**core/location/LocationService.kt**
```kotlin
@AndroidEntryPoint
class LocationService : Service() {
    
    @Inject lateinit var fusedLocationClient: FusedLocationProviderClient
    @Inject lateinit var repository: LocationRepository
    
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            result.lastLocation?.let { location ->
                CoroutineScope(Dispatchers.IO).launch {
                    repository.saveLocation(location)
                    repository.syncLocationToServer(location)
                }
            }
        }
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, buildNotification())
        startLocationUpdates()
        return START_STICKY
    }
    
    private fun startLocationUpdates() {
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            10_000L // 10 segundos
        ).apply {
            setMinUpdateIntervalMillis(5_000L)
            setMaxUpdateDelayMillis(15_000L)
        }.build()
        
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        }
    }
    
    private fun buildNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Pyloto - Você está online")
            .setContentText("Rastreando sua localização")
            .setSmallIcon(R.drawable.ic_location)
            .setColor(getColor(R.color.tech_blue))
            .setOngoing(true)
            .build()
    }
    
    companion object {
        const val CHANNEL_ID = "location_service"
        const val NOTIFICATION_ID = 1001
    }
}
```

---

## FASE 3: Domain Layer (Semana 2 - Dias 1-2)

### **3.1 Domain Models**

**domain/model/Corrida.kt**
```kotlin
data class Corrida(
    val id: String,
    val cliente: Cliente,
    val origem: Endereco,
    val destino: Endereco,
    val valor: BigDecimal,
    val distanciaKm: Double,
    val tempoEstimadoMin: Int,
    val status: CorridaStatus,
    val timestamps: CorridaTimestamps
)

data class Cliente(
    val nome: String,
    val telefone: String,
    val foto: String?
)

data class Endereco(
    val logradouro: String,
    val numero: String,
    val complemento: String?,
    val bairro: String,
    val cidade: String,
    val cep: String,
    val latitude: Double,
    val longitude: Double
)

enum class CorridaStatus {
    DISPONIVEL,
    ACEITA,
    A_CAMINHO_COLETA,
    COLETADA,
    A_CAMINHO_ENTREGA,
    FINALIZADA,
    CANCELADA
}
```

### **3.2 Use Cases**

**domain/usecase/corrida/AceitarCorridaUseCase.kt**
```kotlin
class AceitarCorridaUseCase @Inject constructor(
    private val repository: CorridaRepository
) {
    suspend operator fun invoke(corridaId: String): Result<Corrida> {
        return try {
            val corrida = repository.aceitarCorrida(corridaId)
            Result.success(corrida)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
```

**domain/usecase/corrida/ObterCorridasDisponiveisUseCase.kt**
```kotlin
class ObterCorridasDisponiveisUseCase @Inject constructor(
    private val repository: CorridaRepository,
    private val locationRepository: LocationRepository
) {
    operator fun invoke(): Flow<List<Corrida>> = flow {
        locationRepository.getLastLocation().collect { location ->
            val corridas = repository.getCorridasDisponiveis(
                lat = location.latitude,
                lng = location.longitude
            )
            emit(corridas)
        }
    }
}
```

---

## FASE 4: Data Layer (Semana 2 - Dias 3-5)

### **4.1 Repository Implementation**

**data/repository/CorridaRepositoryImpl.kt**
```kotlin
class CorridaRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val corridaDao: CorridaDao,
    private val mapper: CorridaMapper
) : CorridaRepository {
    
    override suspend fun getCorridasDisponiveis(
        lat: Double,
        lng: Double
    ): List<Corrida> {
        return try {
            val response = apiService.getCorridasDisponiveis(lat, lng)
            val corridas = response.data.map { mapper.toDomain(it) }
            
            // Cache local
            corridaDao.insertAll(corridas.map { mapper.toEntity(it) })
            
            corridas
        } catch (e: Exception) {
            // Fallback para cache
            corridaDao.getDisponiveis().map { mapper.toDomain(it) }
        }
    }
    
    override suspend fun aceitarCorrida(corridaId: String): Corrida {
        val response = apiService.aceitarCorrida(corridaId)
        val corrida = mapper.toDomain(response.data)
        
        corridaDao.update(mapper.toEntity(corrida))
        
        return corrida
    }
    
    override fun observarCorridaAtiva(): Flow<Corrida?> {
        return corridaDao.observarCorridaAtiva()
            .map { it?.let { mapper.toDomain(it) } }
    }
}
```

---

## FASE 5: Presentation Layer (Semana 3)

### **5.1 Navigation**

**presentation/navigation/NavGraph.kt**
```kotlin
@Composable
fun PylotoNavGraph(
    navController: NavHostController,
    startDestination: String
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Routes.HOME) {
            HomeScreen(
                onCorridaClick = { corridaId ->
                    navController.navigate("${Routes.CORRIDA_DETALHES}/$corridaId")
                }
            )
        }
        
        composable(
            route = "${Routes.CORRIDA_DETALHES}/{corridaId}",
            arguments = listOf(navArgument("corridaId") { type = NavType.StringType })
        ) {
            CorridaDetalhesScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(Routes.CORRIDA_ATIVA) {
            CorridaAtivaScreen()
        }
        
        composable(Routes.HISTORICO) {
            HistoricoScreen()
        }
        
        composable(Routes.PERFIL) {
            PerfilScreen()
        }
    }
}

object Routes {
    const val LOGIN = "login"
    const val HOME = "home"
    const val CORRIDA_DETALHES = "corrida_detalhes"
    const val CORRIDA_ATIVA = "corrida_ativa"
    const val HISTORICO = "historico"
    const val PERFIL = "perfil"
}
```

### **5.2 ViewModel Exemplo**

**presentation/home/HomeViewModel.kt**
```kotlin
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val obterCorridasUseCase: ObterCorridasDisponiveisUseCase,
    private val aceitarCorridaUseCase: AceitarCorridaUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    init {
        loadCorridas()
    }
    
    private fun loadCorridas() {
        viewModelScope.launch {
            obterCorridasUseCase()
                .catch { e ->
                    _uiState.value = HomeUiState.Error(e.message ?: "Erro desconhecido")
                }
                .collect { corridas ->
                    _uiState.value = HomeUiState.Success(corridas)
                }
        }
    }
    
    fun aceitarCorrida(corridaId: String) {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            aceitarCorridaUseCase(corridaId)
                .onSuccess {
                    // Navegar para tela de corrida ativa
                }
                .onFailure { e ->
                    _uiState.value = HomeUiState.Error(e.message ?: "Erro ao aceitar corrida")
                }
        }
    }
}

sealed class HomeUiState {
    object Loading : HomeUiState()
    data class Success(val corridas: List<Corrida>) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}
```

---

## FASE 6: CI/CD (Paralelo às fases anteriores)

### **6.1 GitHub Actions - CI**

**.github/workflows/ci.yml**
```yaml
name: Android CI

on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main, develop ]

jobs:
  build:
    runs-on: ubuntu-latest
    
    steps:
    - uses: actions/checkout@v3
    
    - name: Set up JDK 17
      uses: actions/setup-java@v3
      with:
        java-version: '17'
        distribution: 'temurin'
    
    - name: Grant execute permission for gradlew
      run: chmod +x gradlew
    
    - name: Run tests
      run: ./gradlew test
    
    - name: Build debug APK
      run: ./gradlew assembleDebug
    
    - name: Upload APK
      uses: actions/upload-artifact@v3
      with:
        name: app-debug
        path: app/build/outputs/apk/debug/app-debug.apk
```

---

## FASE 7: Documentação (Contínua)

### **7.1 ADR Template**

**docs/ADR/001-arquitetura-mvvm.md**
```markdown
# ADR 001: Arquitetura MVVM com Clean Architecture

## Status
Aceito

## Contexto
Precisamos de uma arquitetura que:
- Facilite testes unitários
- Separe concerns (UI, lógica, dados)
- Seja escalável para 10+ devs futuros

## Decisão
Adotar MVVM + Clean Architecture com as seguintes camadas:
- Presentation (Compose + ViewModel)
- Domain (Use Cases + Entities)
- Data (Repository + Data Sources)

## Consequências
**Positivas:**
- Testabilidade alta (use cases testáveis sem Android framework)
- Separação clara de responsabilidades
- Facilita onboarding de novos devs

**Negativas:**
- Boilerplate inicial maior
- Curva de aprendizado para júniors
```

---

## CRONOGRAMA EXECUTIVO

| Semana | Dias | Entregas |
|--------|------|----------|
| **1** | 1-3 | Setup projeto, paleta cores, dependências, CI básico |
| **1** | 4-5 | Network layer, Database, Location Service |
| **2** | 1-2 | Domain models, Use Cases principais |
| **2** | 3-5 | Repositories, Mappers, Data sources |
| **3** | 1-3 | Telas principais (Login, Home, Corrida Ativa) |
| **3** | 4-5 | Navegação, integração GPS/Maps |
| **4** | 1-2 | Firebase (FCM, Crashlytics), WorkManager |
| **4** | 3-4 | Chat, Câmera, Histórico |
| **4** | 5 | Testes finais, build release |

---

## MÉTRICAS DE QUALIDADE

### **Obrigatórias antes do MVP**
- [ ] Cobertura de testes >50% em Use Cases
- [ ] Zero memory leaks (LeakCanary)
- [ ] Tempo de build <3min
- [ ] APK size <25MB
- [ ] Crash-free rate >99% (após 1 semana de beta)

### **Monitoramento Contínuo**
- Crashlytics configurado (crashes, ANRs)
- Analytics de fluxos críticos (aceitar corrida, finalizar)
- Performance monitoring (tempo de resposta API)

---

## PRÓXIMOS PASSOS IMEDIATOS

1. **Você define:**
   - URL base da API (staging/production)
   - Estrutura de autenticação (JWT? OAuth?)
   - Estratégia de real-time (WebSocket? Firebase Realtime?)

2. **Dev sênior executa:**
   - Cria repo no GitHub
   - Configura projeto base (FASE 0)
   - Documenta ADRs das decisões técnicas

3. **Você + Dev alinham:**
   - Contratos de API (endpoints, payloads)
   - Fluxo completo de corrida (estado, transições)
   - Regras de negócio (raio de busca, timeout aceitação)

**Quer que eu detalhe alguma FASE específica ou partimos para definição dos contratos de API?**