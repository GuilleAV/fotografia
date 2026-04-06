# FotoPortfolio — Plataforma Multi-Fotógrafo

Plataforma donde fotógrafos suben sus fotos, un administrador las modera, y el público las ve en una galería. Las fotos se suben en alta calidad pero se muestran como thumbnails optimizados.

---

## 🏗️ Arquitectura

```
fotografia/
├── portfolio-backend/    ← API REST (Java EE 8 + JAX-RS + JPA + MySQL)
├── frontend/             ← SPA (Angular 17 + TypeScript)
└── docker-compose.yml    ← MySQL + phpMyAdmin
```

### Stack

| Capa | Tecnología |
|------|-----------|
| **Backend** | Java EE 8, JAX-RS, JPA (EclipseLink), MySQL 8 |
| **Frontend** | Angular 17 (standalone), TypeScript, Signals |
| **Infra** | Docker Compose, GlassFish/Payara |

---

## 🚀 Inicio Rápido

### 1. Base de datos

```bash
docker-compose up -d mysql phpmyadmin
```

- **MySQL**: `localhost:3306` — user: `root` / pass: `root123`
- **phpMyAdmin**: `http://localhost:8081`

### 2. Migración de la BD

Ejecutar el script de migración en MySQL:

```bash
docker exec -i foto-mysql mysql -uroot -proot123 portfolio_fotografo < portfolio-backend/src/main/resources/db/migration/V001__multi_photographer.sql
```

O desde phpMyAdmin: importar el archivo `V001__multi_photographer.sql`.

### 3. Backend

1. Abrir `portfolio-backend/` en NetBeans/IntelliJ
2. Configurar el datasource `jdbc/portfolioFT` en GlassFish/Payara apuntando a `localhost:3306/portfolio_fotografo`
3. Deployar el WAR (`mvn clean package` → `target/portfolio-backend.war`)
4. La API queda en: `http://localhost:8080/portfolio-backend/api`

### 4. Frontend

```bash
cd frontend
npm install
ng serve
```

Abrir `http://localhost:4200`

---

## 📡 API Endpoints

### Auth

| Método | Path | Auth | Descripción |
|--------|------|------|-------------|
| `POST` | `/api/auth/login` | No | Login → devuelve JWT |
| `POST` | `/api/auth/logout` | Sí | Invalida sesión |
| `POST` | `/api/auth/recuperar` | No | Solicitar recuperación de password |
| `POST` | `/api/auth/reset` | No | Resetear password con token |

### Fotos (Públicas)

| Método | Path | Auth | Descripción |
|--------|------|------|-------------|
| `GET` | `/api/fotos` | No | Listar fotos aprobadas |
| `GET` | `/api/fotos/{id}` | No | Detalle de foto (solo si está aprobada) |
| `GET` | `/api/fotos/categoria/{idCategoria}` | No | Fotos por categoría (aprobadas) |

### Fotos (Autenticadas)

| Método | Path | Auth | Descripción |
|--------|------|------|-------------|
| `GET` | `/api/fotos/mis-fotos` | Sí | Fotos del usuario logueado |
| `POST` | `/api/fotos/upload` | Sí | Subir foto (multipart/form-data) |
| `PUT` | `/api/fotos/{id}` | Sí | Actualizar metadata (dueño o admin) |
| `DELETE` | `/api/fotos/{id}` | Sí | Eliminar foto (dueño o admin) |
| `GET` | `/api/fotos/{id}/download` | Sí | Descargar versión original |

### Admin

| Método | Path | Auth | Descripción |
|--------|------|------|-------------|
| `GET` | `/api/fotos/admin/todas` | Admin | Todas las fotos (todos los estados) |
| `GET` | `/api/fotos/admin/pendientes` | Admin | Fotos pendientes de aprobación |
| `PATCH` | `/api/fotos/{id}/estado` | Admin | Cambiar estado (APROBADA/RECHAZADA) |

---

## 🔐 Roles del Sistema

| Rol | Permisos |
|-----|----------|
| **FOTOGRAFO** | Subir fotos, ver/editar/eliminar sus propias fotos |
| **ADMIN** | Todo lo de FOTOGRAFO + moderar fotos, ver todas las fotos |
| **SUPER_ADMIN** | Todo lo de ADMIN + gestionar usuarios |

---

## 📸 Flujo de Upload

```
Fotógrafo sube foto (alta calidad, máx 10MB)
        │
        ▼
ImageProcessor genera 3 versiones:
  ├── _original.jpg  → Alta calidad (almacenada)
  ├── _thumb.jpg     → 800px max, calidad 80%
  └── _web.jpg       → 1920px max, calidad 85%
        │
        ▼
Se guarda en BD con estado: PENDIENTE
        │
        ▼
Admin revisa → APROBADA o RECHAZADA
        │
        ▼
Público solo ve fotos APROBADAS (usa _web.jpg)
```

### Formatos soportados
- JPG, JPEG, PNG, WebP
- Tamaño máximo: 10MB

### Almacenamiento
- Directorio: `~/portfolio-uploads/{username}/`
- Cada foto genera 3 archivos en disco

---

## 🗄️ Base de Datos

### Tablas principales

| Tabla | Descripción |
|-------|-------------|
| `usuarios` | Usuarios del sistema (fotógrafos, admins) |
| `fotos` | Fotos con estado, rutas y metadata |
| `categorias` | Categorías de fotos |
| `etiquetas` | Tags para fotos |
| `fotos_etiquetas` | Relación many-to-many foto-etiqueta |
| `sesiones_activas` | Sesiones JWT activas |
| `recuperacion_password` | Tokens de recuperación |
| `configuracion` | Configuración del sistema |
| `estadisticas` | Estadísticas diarias |

### Campos nuevos en `fotos` (migración V001)

```sql
estado VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE'  -- PENDIENTE, APROBADA, RECHAZADA
ruta_thumbnail VARCHAR(500)                        -- Ruta del thumbnail
ruta_web VARCHAR(500)                              -- Ruta de la versión web
```

---

## 🎨 Frontend (Angular)

### Estructura

```
frontend/src/app/
├── core/
│   ├── models/              ← Interfaces TypeScript
│   ├── services/            ← Auth, Foto, Categoria
│   ├── guards/              ← authGuard, adminGuard
│   └── interceptors/        ← JWT + Error handling
├── shared/components/
│   ├── header/              ← Navbar reactivo al auth
│   ├── footer/
│   ├── photo-card/          ← Card reutilizable
│   └── skeleton/            ← Loading skeleton
└── pages/
    ├── home/                ← Hero + fotos + categorías
    ├── galeria/             ← Grid filtrable por categoría
    ├── foto-detail/         ← Detalle + lightbox
    ├── login/               ← Login con backend real
    ├── dashboard/           ← Panel del fotógrafo + upload
    ├── admin/               ← Moderación + tabla
    └── unauthorized/        ← Acceso denegado
```

### Rutas

| Ruta | Componente | Guard |
|------|-----------|-------|
| `/` | Home | — |
| `/galeria` | Galeria | — |
| `/foto/:id` | FotoDetail | — |
| `/login` | Login | — |
| `/dashboard` | Dashboard | authGuard |
| `/admin` | Admin | adminGuard |
| `/unauthorized` | Unauthorized | — |

### State Management

Se usan **Angular Signals** (no NgRx):
- `AuthService`: token, user, role como signals computados
- Persistencia en `localStorage`
- HTTP interceptor auto-agrega JWT a cada request

---

## ⚙️ Configuración

### Backend (persistence.xml)

```xml
jdbc:mysql://localhost:3306/portfolio_fotografo
user: root / password: root
```

### Frontend (environment.ts)

```ts
apiUrl: 'http://localhost:8080/portfolio-backend/api'
```

### Docker Compose

| Servicio | Puerto | Credenciales |
|----------|--------|-------------|
| MySQL | 3306 | root / root123 |
| phpMyAdmin | 8081 | root / root123 |

---

## 📋 Comandos Útiles

```bash
# Levantar infraestructura
docker-compose up -d

# Ver logs
docker-compose logs -f mysql

# Detener todo
docker-compose down

# Frontend dev
cd frontend && ng serve

# Frontend build
cd frontend && ng build

# Frontend build producción
cd frontend && ng build --configuration production

# Backend build
cd portfolio-backend && mvn clean package
```

---

## 🔑 Crear Usuario Inicial

Insertar directamente en MySQL (password debe estar hasheada con bcrypt):

```sql
-- Super Admin
INSERT INTO usuarios (username, password, email, nombre, apellido, rol, activo, fecha_creacion)
VALUES ('admin', '$2a$10$...', 'admin@fotoportfolio.com', 'Admin', 'Sistema', 'SUPER_ADMIN', TRUE, NOW());

-- Fotógrafo de prueba
INSERT INTO usuarios (username, password, email, nombre, apellido, rol, activo, fecha_creacion)
VALUES ('fotografo1', '$2a$10$...', 'foto@test.com', 'Juan', 'Pérez', 'FOTOGRAFO', TRUE, NOW());
```

> **Nota**: Generar el hash bcrypt con una herramienta online o desde el backend.

---

## 📝 Licencia

Todos los derechos reservados.
