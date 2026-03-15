# BluePrints API – Parte 2

API REST para el laboratorio ARSW. Endpoints públicos, sin autenticación JWT/OAuth2.

## Endpoints

## Configuración

...existing code...

## Seguridad JWT

### Login

POST /auth/login

### Acceso con token

...existing code...

### Consideraciones importantes

...existing code...

# BluePrints API Backend

API REST para gestión de blueprints construida con Spring Boot 3 y Java 21.
El proyecto implementa seguridad con JWT (OAuth2 Resource Server), persistencia en PostgreSQL, documentación Swagger/OpenAPI y una arquitectura por capas (API, servicios, persistencia y dominio).

## Tabla de contenido
1. [Resumen del proyecto](#resumen-del-proyecto)
2. [Tecnologías](#tecnologías)
3. [Arquitectura](#arquitectura)
4. [Árbol de directorios](#árbol-de-directorios)
5. [Seguridad y autenticación](#seguridad-y-autenticación)
6. [Modelo de datos](#modelo-de-datos)
7. [Endpoints](#endpoints)
8. [Cómo ejecutar localmente](#cómo-ejecutar-localmente)
9. [Ejecución con Docker](#ejecución-con-docker)
10. [Perfiles de filtros](#perfiles-de-filtros)
11. [Pruebas y utilidades](#pruebas-y-utilidades)
12. [Integrantes](#integrantes)

## Resumen del proyecto
- Expone operaciones CRUD sobre blueprints y puntos.
- Usa JWT firmado con RS256 para proteger endpoints.
- Incluye endpoint de login didáctico (`/auth/login`) para emitir tokens en laboratorio.
- Implementa persistencia con JPA + PostgreSQL.
- Incluye estrategia de filtros para transformar los puntos de un blueprint al consultarlo.

## Tecnologías
- Java 21
- Spring Boot 3.3.2
- Spring Web
- Spring Security
- OAuth2 Resource Server
- JWT (Nimbus JOSE + JWT)
- Spring Data JPA
- PostgreSQL
- Springdoc OpenAPI (Swagger UI)
- Maven
- Docker (multi-stage build)

## Arquitectura

### Capas principales
- `api`: controladores REST y contrato HTTP.
- `auth`: autenticación y emisión de token JWT.
- `services`: lógica de negocio de blueprints.
- `persistence`: abstracción e implementaciones de persistencia.
- `model`: entidades del dominio (`Blueprint`, `Point`).
- `dto`: objetos de transferencia y mapeo entidad-DTO.
- `filters`: estrategias para transformar puntos al consultar un blueprint.
- `security`: configuración de seguridad, encoder y llaves JWT.
- `config`: configuración OpenAPI.

### Persistencia
Se define una interfaz `BlueprintPersistence` con dos implementaciones:
- `PostgresBlueprintPersistence` (activa por `@Primary`): usa JPA con `EntityManager`.
- `InMemoryBlueprintPersistence`: útil para pruebas/manual demo.

## Árbol de directorios
```text
backend/
├─ api.http
├─ Dockerfile
├─ pom.xml
├─ README.md
├─ src/
│  └─ main/
│     ├─ java/co/edu/eci/blueprints/
│     │  ├─ BlueprintsApiApplication.java
│     │  ├─ api/
│     │  │  ├─ ApiResponse.java
│     │  │  ├─ BlueprintController.java
│     │  │  └─ BlueprintsAPIController.java
│     │  ├─ auth/
│     │  │  └─ AuthController.java
│     │  ├─ config/
│     │  │  └─ OpenApiConfig.java
│     │  ├─ dto/
│     │  │  ├─ BlueprintDTO.java
│     │  │  ├─ BlueprintMapper.java
│     │  │  └─ PointDTO.java
│     │  ├─ filters/
│     │  │  ├─ BlueprintsFilter.java
│     │  │  ├─ IdentityFilter.java
│     │  │  ├─ RedundancyFilter.java
│     │  │  └─ UndersamplingFilter.java
│     │  ├─ model/
│     │  │  ├─ Blueprint.java
│     │  │  └─ Point.java
│     │  ├─ persistence/
│     │  │  ├─ BlueprintNotFoundException.java
│     │  │  ├─ BlueprintPersistence.java
│     │  │  ├─ BlueprintPersistenceException.java
│     │  │  ├─ InMemoryBlueprintPersistence.java
│     │  │  └─ PostgresBlueprintPersistence.java
│     │  ├─ security/
│     │  │  ├─ InMemoryUserService.java
│     │  │  ├─ JwtKeyProvider.java
│     │  │  ├─ MethodSecurityConfig.java
│     │  │  ├─ RsaKeyProperties.java
│     │  │  └─ SecurityConfig.java
│     │  └─ services/
│     │     └─ BlueprintsServices.java
│     └─ resources/
│        ├─ application.properties
│        └─ application.yml
└─ target/
```

## Seguridad y autenticación

### Flujo
1. Cliente hace login en `POST /auth/login`.
2. El backend valida usuario/clave contra `InMemoryUserService`.
3. Si es válido, genera JWT con scopes y expiración configurada.
4. Cliente envía `Authorization: Bearer <token>` para endpoints protegidos.

### Usuarios de laboratorio
- `student / student123`
- `assistant / assistant123`

### Scopes emitidos
- `blueprints.read`
- `blueprints.write`
- `blueprints.addPoint`

### Consideraciones importantes
- El par de llaves RSA se genera en memoria al iniciar la app.
- Si reinicias el backend, los tokens anteriores dejan de ser válidos.
- CORS habilitado para frontend local en `http://localhost:5173`.

## Modelo de datos

### Blueprint
- `id` (autogenerado)
- `author`
- `name`
- `points` (colección de `Point`)

### Point
- `x`
- `y`

## Endpoints

### Autenticación

#### `POST /auth/login`
Request:
```json
{
  "username": "student",
  "password": "student123"
}
```

Response (200):
```json
{
  "code": 200,
  "message": "Success",
  "data": {
    "access_token": "<jwt>",
    "token_type": "Bearer",
    "expires_in": 1800
  }
}
```

### API principal de blueprints (`/api/v1/blueprints`)

#### `GET /api/v1/blueprints`
Lista todos los blueprints.

#### `GET /api/v1/blueprints/{author}`
Lista blueprints por autor.

#### `GET /api/v1/blueprints/{author}/{bpname}`
Obtiene blueprint específico y aplica filtro activo.

#### `POST /api/v1/blueprints`
Crea blueprint.
Request:
```json
{
  "author": "john",
  "name": "house",
  "points": [
    { "x": 0, "y": 0 },
    { "x": 10, "y": 0 },
    { "x": 10, "y": 10 }
  ]
}
```

#### `PUT /api/v1/blueprints/{author}/{bpname}/points`
Agrega un punto al blueprint.
Request:
```json
{ "x": 5, "y": 5 }
```

#### `PUT /api/v1/blueprints/{author}/{bpname}`
Actualiza blueprint completo (incluye posible cambio de autor/nombre/puntos).

#### `DELETE /api/v1/blueprints/{author}/{bpname}`
Elimina blueprint.

### API de compatibilidad (`/api/blueprints`)
Existe un controlador adicional con endpoints básicos de demostración:
- `GET /api/blueprints`
- `POST /api/blueprints`

### Swagger
- URL: `http://localhost:8080/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## Cómo ejecutar localmente

### Requisitos
- JDK 21
- Maven 3.9+
- Docker (para levantar PostgreSQL rápido)

### 1. Levantar PostgreSQL
```bash
docker run --name mi-postgres -e POSTGRES_USER=admin -e POSTGRES_PASSWORD=admin123 -e POSTGRES_DB=mi_basedatos -p 5432:5432 -d postgres
```

Importante:
- Este backend requiere que PostgreSQL esté disponible para iniciar correctamente.
- Si la base no está corriendo, comandos como `mvn spring-boot:run` (y en algunos entornos `mvn package`) pueden fallar por conexión a BD.
- Asegúrate de tener Docker Desktop encendido antes de ejecutar el comando anterior.

Si el contenedor ya existe pero está detenido:
```bash
docker start mi-postgres
```

Para validar que PostgreSQL está arriba:
```bash
docker ps
```

### 2. Ejecutar backend
```bash
mvn -q -DskipTests spring-boot:run
```

Opcional (solo compilar):
```bash
mvn -B -DskipTests package
```

### 3. Verificar salud
- API: `http://localhost:8080`
- Health: `http://localhost:8080/actuator/health`

### Configuración por defecto
Las propiedades actuales apuntan a:
- DB: `jdbc:postgresql://localhost:5432/mi_basedatos`
- Usuario: `admin`
- Password: `admin123`
- TTL token: `1800` segundos
- Issuer token: `https://decsis-eci/blueprints`

## Ejecución con Docker

### Build de imagen
```bash
docker build -t blueprints-backend .
```

### Run de contenedor
```bash
docker run --name blueprints-backend -p 8080:8080 blueprints-backend
```

Nota:
Si el backend se ejecuta en contenedor y PostgreSQL está fuera del contenedor, ajusta la URL de conexión (`spring.datasource.url`) para que apunte al host correcto.

Si quieres detener y limpiar el contenedor local de PostgreSQL:
```bash
docker stop mi-postgres
docker rm mi-postgres
```

## Perfiles de filtros
Los filtros aplican al consultar blueprint por autor y nombre.

- `default`: `IdentityFilter` (sin cambios)
- `redundancy`: elimina puntos consecutivos repetidos
- `undersampling`: conserva 1 de cada 2 puntos

Para activar un perfil:
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=redundancy
```

## Pruebas y utilidades
- Archivo de ejemplos HTTP: `api.http`
- No se encontraron pruebas automáticas en `src/test` en el estado actual del proyecto.

## Integrantes
- Anderson Fabian Garcia Nieto
- Juana Lozano Chaves

## Licencia
Proyecto educativo con fines académicos.
