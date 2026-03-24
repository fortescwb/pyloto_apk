# Setup de Desenvolvimento - Pyloto Entregador Android

## Pré-requisitos

- **Android Studio** Hedgehog (2023.1.1) ou superior
- **JDK 17**
- **Android SDK 34**
- **Kotlin 1.9.20**
- Conta Google Cloud (para Maps API Key)
- Conta Firebase (para FCM, Crashlytics)

## Setup Inicial

### 1. Clonar o repositório
```bash
git clone https://github.com/pyloto/pyloto-entregador-android.git
cd pyloto-entregador-android
```

### 2. Configurar variáveis de ambiente

Copie o arquivo `gradle.properties` e configure:
```properties
# STAGING desativado temporariamente (planejado para retorno em 2-3 meses)
# API_BASE_URL_STAGING="https://staging-api.pyloto.com.br/v1/"
API_BASE_URL_PRODUCTION="https://api.pyloto.com.br/v1/"
```

### 3. Google Maps API Key

Adicione ao `local.properties` (NÃO commitar):
```
MAPS_API_KEY=sua_api_key_aqui
```

### 4. Firebase

1. Crie um projeto no [Firebase Console](https://console.firebase.google.com)
2. Baixe o `google-services.json`
3. Coloque em `app/google-services.json`

### 5. Build

```bash
./gradlew assembleDebug
```

## Estrutura do Projeto

```
app/src/main/java/com/pyloto/entregador/
├── core/           # Infraestrutura base
│   ├── di/         # Módulos Hilt (DI)
│   ├── network/    # Retrofit, interceptors, modelos de API
│   ├── database/   # Room (entities, DAOs, converters)
│   ├── location/   # GPS service
│   └── util/       # Extensions, constantes, token manager
├── data/           # Implementações de dados
│   ├── repository/ # Implementações dos repositórios
│   ├── remote/     # DTOs da API
│   └── local/      # Mappers, cache
├── domain/         # Lógica de negócio pura
│   ├── model/      # Entidades de domínio
│   ├── usecase/    # Use cases
│   └── repository/ # Interfaces de repositórios
└── presentation/   # UI (Compose)
    ├── auth/       # Login e onboarding contratual
    ├── home/       # Tela principal
    ├── corrida/    # Corrida disponível, ativa, histórico
    ├── perfil/     # Perfil do entregador
    ├── chat/       # Chat com cliente
    ├── navigation/ # NavGraph, Routes
    └── theme/      # Tema Material3, cores
```

## Convenções

### Branches
- `main` — Produção
- `develop` — Desenvolvimento
- `feature/nome-da-feature` — Features
- `bugfix/nome-do-bug` — Correções
- `hotfix/nome-do-hotfix` — Correções urgentes em produção

### Commits
Seguir [Conventional Commits](https://www.conventionalcommits.org/):
```
feat: adicionar tela de histórico
fix: corrigir crash ao aceitar corrida
refactor: extrair mapper de corrida
docs: atualizar API.md
```

### Code Review
- PRs requerem pelo menos 1 aprovação
- CI deve passar (lint + testes)
- Squash merge para main
