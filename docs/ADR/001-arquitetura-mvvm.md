# ADR 001: Arquitetura MVVM com Clean Architecture

## Status
Aceito

## Contexto
Precisamos de uma arquitetura que:
- Facilite testes unitários
- Separe concerns (UI, lógica, dados)
- Seja escalável para 10+ devs simultâneos
- Suporte crescimento de features sem acoplamento

## Decisão
Adotar MVVM + Clean Architecture com as seguintes camadas:
- **Presentation** (Compose + ViewModel) — UI e estado
- **Domain** (Use Cases + Entities) — Lógica de negócio pura
- **Data** (Repository + Data Sources) — Acesso a dados (API, DB, Cache)

### Regras de Dependência
```
Presentation → Domain → Data
     ↓             ↓         ↓
  ViewModel     UseCase   Repository
  Screen        Model     ApiService
  Navigation    Interface RoomDAO
```

## Consequências

**Positivas:**
- Testabilidade alta (use cases testáveis sem Android framework)
- Separação clara de responsabilidades
- Facilita onboarding de novos devs
- Permite trabalho paralelo em diferentes camadas
- Domain layer portátil (pode ser usada em KMM futuramente)

**Negativas:**
- Boilerplate inicial maior
- Curva de aprendizado para júniors
- Mais arquivos para features simples
