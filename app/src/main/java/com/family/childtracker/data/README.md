# Data Layer

This layer contains:
- **Repository Implementations**: Concrete implementations of domain repository interfaces
- **Data Sources**: Local (Room) and remote (API) data sources
- **Database**: Room database, DAOs, and entities
- **API**: Retrofit services and data models

## Structure
```
data/
├── local/
│   ├── database/   # Room database and DAOs
│   ├── entity/     # Room entities
│   └── mapper/     # Entity to domain model mappers
├── remote/
│   ├── api/        # Retrofit API services
│   └── dto/        # Data transfer objects
└── repository/     # Repository implementations
```
