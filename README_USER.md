# 🧩 Arka User Service

Microservicio **User** del ecosistema **Arka**, encargado de la gestión de usuarios, autenticación con **JWT**, y asignación de roles dentro de la plataforma.  
Desarrollado con **Spring Boot 3.5**, **Java 21**, **Gradle**, **PostgreSQL** y **Eureka Discovery**.

## 🚀 Características principales
- Registro y autenticación de usuarios mediante **JWT**  
- Roles y permisos definidos (ADMIN, USER, etc)  
- Integración con **Eureka** para descubrimiento de servicios  
- Documentación con **Swagger / OpenAPI 3.0**  
- Configuración multi-perfil (`local` y `docker`)  
- Soporte para despliegue con **Docker** y **Docker Compose**  
- Compatible con CI/CD vía GitHub Actions  

## 🧱 Tecnologías
| Componente | Versión / Descripción |
|-------------|------------------------|
| Java | 21 |
| Spring Boot | 3.5.x |
| Gradle | 8.x |
| Spring Security | Autenticación con JWT |
| SpringDoc OpenAPI | Swagger UI |
| PostgreSQL | Base de datos principal |
| Eureka Client | Registro de servicios |
| Docker / Compose | Contenedorización |
| Lombok | Reducción de boilerplate |

## ⚙️ Configuración
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/usersdb
    username: postgres
    password: 0921
```
Variables de entorno recomendadas:
```bash
export SPRING_DATASOURCE_USERNAME=postgres
export SPRING_DATASOURCE_PASSWORD=0921
export SECURITY_JWT_SECRET="short_secret_key_32_chars_len!!!"
export EUREKA_SERVER_URL="http://localhost:8761/eureka/"
```

## 🔐 Seguridad JWT
Encabezado necesario para endpoints protegidos:
```
Authorization: Bearer <token>
```

## 📘 Endpoints principales
| Método | Endpoint | Descripción |
|--------|-----------|-------------|
| POST | `/api/users/register` | Crea un nuevo usuario |
| POST | `/api/users/login` | Autentica un usuario y genera token JWT |
| GET | `/api/users` | Lista todos los usuarios (ADMIN) |
| GET | `/api/users/{id}` | Obtiene los datos de un usuario |
| GET | `/api/users/{id}/email` | Devuelve el correo del usuario |
| PUT | `/api/users/{id}` | Actualiza datos de usuario |
| DELETE | `/api/users/{id}` | Elimina un usuario (ADMIN) |

## 🧾 Swagger / OpenAPI
Swagger UI: `http://localhost:8084/swagger-ui.html`  
Archivo: `src/main/java/com/example/arkauser/config/OpenApiConfig.java`

## 🐳 Docker
Dockerfile multi-stage y docker-compose disponibles.

## 📦 Estructura del proyecto
```
src/
 ├─ main/java/com/example/arkauser/
 │   ├─ application/
 │   ├─ domain/
 │   ├─ infraestructure/
 │   ├─ config/
 │   └─ ArkaUserApplication.java
 └─ resources/application.yml
```

## 🧪 Ejecución local
```bash
./gradlew clean bootRun
```
Swagger: [http://localhost:8084/swagger-ui.html](http://localhost:8084/swagger-ui.html)

## 🛠️ CI/CD
Workflow `.github/workflows/ci.yml` para build, test y deploy automático.

## 📄 Licencia
Proyecto bajo licencia **Apache 2.0**  
© 2025 Ecosistema Arka – Todos los derechos reservados.
