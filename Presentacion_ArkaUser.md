
# Presentación Técnica Integral — Arka **User Service**

> **Versión:** 1.0.0 — **Fecha:** 10-Nov-2025  
> **Stack:** Java 21 · Spring Boot 3.5.2 · Spring Security (JWT) · Spring Data JPA · PostgreSQL · Docker/Compose · GitHub Actions · OpenAPI/Swagger

---

## 1. Resumen Ejecutivo
El **User Service** es el microservicio responsable de la **gestión de usuarios** dentro del ecosistema **Arka**. Expone una API REST segura para **crear**, **consultar**, **actualizar** y **eliminar** usuarios; integra **JWT** para autenticación/autorización por **roles**; persiste en **PostgreSQL**; y está preparado para contenedores (**Docker**) y **CI/CD** (GitHub Actions).  
La documentación interactiva se publica con **OpenAPI/Swagger** y se habilitan **endpoints de observabilidad** vía Actuator.

**Beneficios clave**
- Frontera clara de responsabilidades (Single Responsibility) y bajo acoplamiento.
- Seguridad end-to-end con JWT HS256 y **scopes/roles**.
- Infraestructura lista: Dockerfile, docker-compose, healthchecks, CI/CD.
- Documentación viva con OpenAPI, ejemplos y contratos de entrada/salida.

---

## 2. Objetivos y Requisitos
### 2.1. Objetivos
- Exponer **CRUD de usuarios** con validaciones y esquema de roles.
- Garantizar **seguridad** y trazabilidad básica (health, logs).
- Facilitar el **despliegue reproducible** (Docker/Compose) y **automatizado** (CI/CD).

### 2.2. Requisitos Funcionales
- RF-01: Listar usuarios (`GET /api/users`).
- RF-02: Consultar usuario por id (`GET /api/users/{id}`).
- RF-03: Crear usuario (`POST /api/users`) con `username`, `email`, `fullName`, `role`.
- RF-04: Actualizar usuario (`PUT /api/users/{id}`).
- RF-05: Eliminar usuario (`DELETE /api/users/{id}`).

### 2.3. Requisitos No Funcionales
- RNF-01: Autenticación y autorización con **JWT** HS256.
- RNF-02: Disponibilidad local mediante **docker-compose** (Postgres + servicio).
- RNF-03: Observabilidad básica con **Actuator**.
- RNF-04: Documentación **OpenAPI** pública (UI y JSON).
- RNF-05: Pipeline **CI/CD** con build, test y publicación de imagen Docker.

---

## 3. Arquitectura

### 3.1. Vista Lógica (Capa Aplicativa)
- **Controller**: expone REST y define contratos (DTOs).  
- **Service**: orquestación de casos de uso y validaciones.  
- **Repository (JPA)**: acceso a datos en PostgreSQL.  
- **Security**: `SecurityFilterChain` con reglas por **roles** y **recursos**.

### 3.2. Vista de Datos
**Entidad:** `UserEntity(id, username, email, fullName, role)`  
**Reglas:**  
- `username` y `email` **únicos**.  
- `role` ∈ {`ROLE_ADMIN`, `ROLE_USER`} (recomendación).

### 3.3. Vista de Despliegue
- Imagen construida vía **Gradle bootJar** (multistage) → **Temurin JRE 21**.  
- Orquestación local por **docker-compose**: `postgres` + `user-service`.  
- **Healthcheck** HTTP al Actuator.

---

## 4. Seguridad

### 4.1. Autenticación
- **JWT** firmado HS256, secreto definido por `SECURITY_JWT_SECRET`.
- Recomendación: rotación periódica del secreto y manejo vía **vault**/secrets.

### 4.2. Autorización (Reglas)
- **Público**: `/actuator/**`, `/v3/api-docs/**`, `/swagger-ui.html`, `/swagger-ui/**`.
- **Protegido**:
  - `GET /api/users/**` → `ROLE_ADMIN` o `ROLE_USER`.
  - `POST|PUT|DELETE /api/users/**` → `ROLE_ADMIN`.

### 4.3. Claims sugeridos
`sub`, `iss`, `roles` (array) y/o `scope` con los prefijos `ROLE_`.

---

## 5. API (OpenAPI/Swagger)
**UI**: `/swagger-ui.html` · **Especificación**: `/v3/api-docs`

### 5.1. Endpoints
- `GET /api/users` — Lista usuarios (200).
- `GET /api/users/{id}` — Obtiene usuario (200 / 404).
- `POST /api/users` — Crea usuario (201 / 400).
- `PUT /api/users/{id}` — Actualiza (200 / 400 / 404).
- `DELETE /api/users/{id}` — Elimina (200 / 404).

### 5.2. DTOs
- **UserRequest**: `username`, `email`, `fullName`, `role`.
- **UserEntity**: añade `id` (autogenerado).

---

## 6. Flujos Clave (Secuencias)
1) **Creación de Usuario**  
   Cliente → (JWT `ROLE_ADMIN`) → `POST /api/users` → Service valida → Repo guarda → `201 Created` con `Location`.

2) **Lectura de Usuario**  
   Cliente → (JWT `ROLE_ADMIN/USER`) → `GET /api/users/{id}` → Service obtiene → `200 OK` con JSON.

3) **Eliminación**  
   Cliente → (JWT `ROLE_ADMIN`) → `DELETE /api/users/{id}` → Repo borra → `200 OK`.

---

## 7. Persistencia y Configuración
- `ddl-auto: update` en **local** para acelerar iteración (en producción: **migraciones** con Flyway/Liquibase).
- Variables de entorno: `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD`, `SERVER_PORT`, `SECURITY_JWT_SECRET`, `SECURITY_JWT_ISSUER`.

---

## 8. Observabilidad
- **Health** (`/actuator/health`), **Info**, **Metrics** mínimos expuestos.
- Logs de arranque y consultas JPA (configurables).

---

## 9. Despliegue

### 9.1. Local (docker-compose)
```bash
docker compose up -d --build
```

### 9.2. CI/CD (GitHub Actions)
- **build-test**: JDK 21, `gradlew test`, empaquetado.  
- **docker**: buildx + push a Docker Hub (`DOCKERHUB_USERNAME`, `DOCKERHUB_TOKEN`).

---

## 10. Calidad y Pruebas
- Unit tests para Service y Controller (pendientes de ampliar).  
- Recomendado: **tests de integración** con profile `testcontainers` y **contratos**.

---

## 11. Operación (Runbook)
- **Reinicio**: reiniciar contenedor `user-service`.  
- **Variables**: revisar secrets/vars en orquestador.  
- **Incidentes comunes**: errores de validación, 401/403 por roles, 500 por integridad (únicos).  
- **KPIs**: latencia P95, tasa de errores 5xx, disponibilidad, throughput.

---

## 12. Riesgos y Mitigación
- **Secreto JWT expuesto** → rotación y vault.
- **Esquema de DB mutable** → migraciones controladas (Flyway).
- **Sobrecarga** → escalado horizontal, caché de lectura.

---

## 13. Roadmap
- Integración con **Eureka/Gateway**.
- **Auditoría** (creadoPor/fecha, actualizadoPor/fecha).
- **Observabilidad** avanzada (OTel/Zipkin).
- **Hardening** de security headers y rate-limiting.
- **Pruebas** E2E y de contrato.

---

## 14. Anexos
- **Diagrama de Arquitectura/Flujos (imagen)**: `docs/diagramas/flujo_arkauser.png`
- **Mermaid de respaldo** 

https://markdownlivepreview.com/