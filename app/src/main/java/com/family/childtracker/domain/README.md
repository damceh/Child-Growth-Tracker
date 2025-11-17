# Domain Layer

This layer contains:
- **Use Cases**: Business logic encapsulated in single-responsibility classes
- **Domain Models**: Core entities and data structures
- **Repository Interfaces**: Contracts for data access

The domain layer is independent of Android framework and external libraries.

## Structure
```
domain/
├── model/          # Domain entities (ChildProfile, GrowthRecord, etc.)
├── repository/     # Repository interfaces
└── usecase/        # Use cases for business logic
```
