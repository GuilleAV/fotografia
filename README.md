# FotoPortfolio — Plataforma Multi-Fotógrafo

Plataforma donde fotógrafos suben sus fotos, un administrador las modera, y el público las ve en una galería. Las fotos se suben en alta calidad pero se muestran como thumbnails optimizados.

---

## 📚 Documentación disponible

### Manuales funcionales

- **Manual de usuario (PDF):** [`MANUAL_USUARIO_SISTEMA.pdf`](./MANUAL_USUARIO_SISTEMA.pdf)
- **Capacitación interna (PDF):** [`CAPACITACION_INTERNA.pdf`](./CAPACITACION_INTERNA.pdf)

### Documentación técnica

- **Doc para desarrolladores (PDF):** [`DOCUMENTACION_DESARROLLADORES.pdf`](./DOCUMENTACION_DESARROLLADORES.pdf)
- **Doc para desarrolladores (MD):** [`DOCUMENTACION_DESARROLLADORES.md`](./DOCUMENTACION_DESARROLLADORES.md)
- **Guía de deploy (local + producción):** [`DEPLOYMENT.md`](./DEPLOYMENT.md)

### Fuente editable de manuales

- [`MANUAL_USUARIO_SISTEMA.md`](./MANUAL_USUARIO_SISTEMA.md)
- [`CAPACITACION_INTERNA.md`](./CAPACITACION_INTERNA.md)

> Para regenerar PDFs desde los `.md`: `python generate_manual_pdf.py -i <entrada.md> -o <salida.pdf>`

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

> Para configuración completa **local + producción (DonWeb + NIC.ar + Nginx + GlassFish + MySQL)**, ver [`DEPLOYMENT.md`](./DEPLOYMENT.md).

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
| `PATCH` | `/api/fotos/admin/estado/lote` | Admin | Cambiar estado por lote (resumen: procesadas/omitidas/errores) |

---

## 🔐 Roles del Sistema

| Rol | Permisos |
|-----|----------|
| **FOTOGRAFO** | Subir fotos, ver/editar/eliminar sus propias fotos (no puede cambiar categoría post-subida) |
| **ADMIN** | Todo lo de FOTOGRAFO + moderar fotos, ver todas las fotos |
| **SUPER_ADMIN** | Todo lo de ADMIN + gestionar usuarios |

> **Regla de negocio vigente:** el cambio de `idCategoria` en fotos existentes es **solo admin/super-admin** (validado también en backend).

---

## 📸 Flujo de Upload

```
Fotógrafo sube foto (alta calidad, máx 30MB)
        │
        ▼
ImageProcessor genera 3 versiones:
  ├── _original.{ext}  → Alta calidad (almacenada)
  ├── _thumb.webp      → 800px max, calidad 80% (fallback .jpg)
  └── _web.webp        → 1920px max, calidad 85% (fallback .jpg)
        │
        ▼
Se guarda en BD con estado: PENDIENTE
        │
        ▼
Admin revisa → APROBADA o RECHAZADA
        │
        ▼
Público solo ve fotos APROBADAS (usa _web.webp o fallback .jpg)
```

### Formatos soportados
- JPG, JPEG, PNG, WebP
- Tamaño máximo: 30MB (límite de API)

### Almacenamiento
- Directorio: `APP_UPLOAD_DIR/{username}/`
- Si `APP_UPLOAD_DIR` no está definido: `user.home/portfolio-uploads/{username}/`
- Cada foto genera 3 archivos en disco
- La tabla `fotos` guarda **metadata y nombres de archivo** (no BLOB)

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

### Funcionalidades destacadas de operación

- **Dashboard:** subida rápida y subida por lote con progreso visual (contador, porcentaje, barra, éxitos/fallos, archivo actual).
- **Admin/Pendientes:** moderación individual por card + moderación por lote (selección visibles / todas pendientes).
- **Admin/Todas:** filtros por estado/categoría, paginación (10/20/50), edición de categoría admin-only, moderación por lote filtrada.
- **Responsive:** paneles adaptados para móvil (acciones y filtros optimizados para touch).

---

## ⚙️ Configuración

### Backend (persistence.xml)

```xml
jdbc:mysql://localhost:3306/portfolio_fotografo
user: root / password: root
```

### Frontend (environments)

```ts
// development (environment.ts)
apiUrl: 'http://localhost:8080/portfolio-backend/api'

// production (environment.prod.ts)
apiUrl: '/api'
```

> El build de producción usa `fileReplacements` en `frontend/angular.json` para reemplazar `environment.ts` por `environment.prod.ts`.

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

## 🛠️ Troubleshooting (producción)

### 1) `/api/*` devuelve `index.html` en vez de JSON

Falta (o está mal) el proxy de Nginx. Debe existir un bloque como:

```nginx
location ^~ /api/ {
    proxy_pass http://127.0.0.1:8080/<context-root>/api/;
}
```

> Verificá el context root real en Payara:

```bash
asadmin get applications.application.portfolio-backend.context-root
```

### 2) Login falla con `Illegal base64 character: '_'`

`APP_JWT_SECRET` inválido para la librería JWT actual. Configurar uno fuerte (Base64) por JVM option y reiniciar dominio.

### 3) Hay fotos en BD pero no se ven

Normalmente faltan archivos en disco. Verificar:

- que existan en `APP_UPLOAD_DIR/{username}/`
- que `ruta_archivo`, `ruta_thumbnail`, `ruta_web` sean solo nombres de archivo
- que la foto esté `estado='APROBADA'` y `activo=1`

---

## 📝 Licencia

Todos los derechos reservados.
