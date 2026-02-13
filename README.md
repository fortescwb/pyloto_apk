# Pyloto Entregador - Android

App Android nativo para entregadores da plataforma Pyloto.

## Tecnologias

- **Kotlin** 1.9.20
- **Jetpack Compose** (Material3)
- **Hilt** (Injeção de dependência)
- **Retrofit + OkHttp** (API REST)
- **Room** (Database local)
- **Google Maps + FusedLocationProvider** (GPS)
- **Firebase** (FCM, Crashlytics, Analytics)
- **WorkManager** (Tarefas em background)
- **Coroutines + Flow** (Assincronismo reativo)

## Arquitetura

**MVVM + Clean Architecture** com 3 camadas:

| Camada | Responsabilidade | Componentes |
|--------|------------------|-------------|
| **Presentation** | UI e estado | Composables, ViewModels, Navigation |
| **Domain** | Lógica de negócio | Use Cases, Models, Repository Interfaces |
| **Data** | Acesso a dados | Repository Impls, API DTOs, Room DAOs |

## Setup

Consulte [docs/SETUP.md](docs/SETUP.md) para instruções detalhadas.

### Quick Start
```bash
git clone https://github.com/pyloto/pyloto-entregador-android.git
cd pyloto-entregador-android
# Configurar google-services.json e local.properties
./gradlew assembleDebug
```

## Funcionalidades Principais

- Login/Cadastro de entregador
- Visualização de corridas disponíveis (mapa + lista)
- Aceitar e gerenciar corridas
- Rastreamento GPS em tempo real
- Chat com cliente
- Histórico e ganhos
- Notificações push (FCM)
- Modo offline-first

## Documentação

- [Contratos de API](docs/API.md)
- [Setup de Desenvolvimento](docs/SETUP.md)
- [Decision Records](docs/ADR/)

## Licença

Propriedade de Pyloto Corp. Todos os direitos reservados.
