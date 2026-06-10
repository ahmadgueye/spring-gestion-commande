# Gestion des Commandes — API REST Spring Boot

API REST de gestion de commandes clients développée avec Spring Boot 3.4.x dans le cadre du module Services WEB — Polytech ISI 2025-2026.

## Stack technique

- Java 21
- Spring Boot 3.4.x
- Spring Security + JWT
- Spring Data JPA + MySQL 8
- Docker
- Swagger / OpenAPI 3

## Prérequis

- Java 21
- Maven
- Docker

## Lancement

### 1. Démarrer la base de données

```bash
docker compose up -d
```

### 2. Lancer l'application

```bash
./mvnw spring-boot:run
```

L'application démarre sur `http://localhost:8080` avec le profil `dev` par défaut.

## Profils Spring

| Profil | Usage | Base de données |
|--------|-------|-----------------|
| `dev` | Développement local | MySQL sur Docker |
| `test` | Tests automatisés | H2 en mémoire |
| `prod` | Production | MySQL (variables d'environnement) |

Pour activer un profil spécifique :

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=prod
```

## Variables d'environnement (production)

| Variable | Description |
|----------|-------------|
| `SPRING_PROFILES_ACTIVE` | Profil actif (`prod`) |
| `DB_URL` | URL JDBC MySQL |
| `DB_USERNAME` | Utilisateur base de données |
| `DB_PASSWORD` | Mot de passe base de données |
| `JWT_SECRET` | Clé secrète JWT (min. 32 caractères) |
| `JWT_EXPIRATION` | Durée du token en ms (défaut : 86400000) |

## Accès Swagger
http://localhost:8080/swagger-ui.html

Pour tester les endpoints protégés :
1. Appelle `POST /api/auth/login` pour obtenir un token
2. Clique sur **Authorize** en haut à droite
3. Entre `Bearer <ton_token>`
4. Tous les endpoints sont maintenant accessibles

## Comptes de test (profil dev)

| Username | Password | Rôle |
|----------|----------|------|
| `admin` | `admin123` | ROLE_ADMIN |
| `ali` | `ali123` | ROLE_USER |
| `fatou` | `fatou123` | ROLE_USER |

## Exemples d'appels API

### Authentification

```http
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123"
}
```

### Créer un produit

```http
POST http://localhost:8080/api/produits
Content-Type: application/json
Authorization: Bearer <token>

{
  "nom": "Laptop Dell",
  "prix": 999.99,
  "stock": 50
}
```

### Créer une commande

```http
POST http://localhost:8080/api/commandes
Content-Type: application/json
Authorization: Bearer <token>

{
  "clientId": 1,
  "lignes": [
    { "produitId": 1, "quantite": 1 },
    { "produitId": 2, "quantite": 2 }
  ]
}
```

### Valider une commande

```http
PATCH http://localhost:8080/api/commandes/1/valider
Authorization: Bearer <token>
```

### Commandes entre deux dates

```http
GET http://localhost:8080/api/commandes/entre-dates?debut=2026-01-01T00:00:00&fin=2026-12-31T23:59:59
Authorization: Bearer <token>
```

### Chiffre d'affaires global

```http
GET http://localhost:8080/api/commandes/statistiques/chiffre-affaires
Authorization: Bearer <token>
```