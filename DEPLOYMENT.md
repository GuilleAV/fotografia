# Deploy y Configuración (Local + Producción)

Este proyecto está preparado para correr en ambos entornos:

- **Local**: Angular (`ng serve`) + GlassFish/Payara + MySQL local/docker.
- **Producción (DonWeb VPS)**: Angular estático en **Nginx** + API Java EE en **GlassFish** + **MySQL**.

---

## 1) Variables de configuración backend

El backend lee configuración con esta prioridad:

1. `-DAPP_*` (JVM options en GlassFish)
2. Variables de entorno `APP_*`
3. `app.local.properties` (opcional, local, no versionado)
4. `app.properties`

Variables clave:

- `APP_PUBLIC_API_BASE_URL` (ej: `https://sentirfotografico.com.ar/api`)
- `APP_FRONTEND_BASE_URL` (ej: `https://sentirfotografico.com.ar`)
- `APP_UPLOAD_DIR` (ej: `/var/portfolio-uploads`)
- `APP_CORS_ALLOWED_ORIGINS` (CSV)
- `APP_JWT_SECRET` (**obligatorio en prod**)
- `APP_JWT_EXPIRATION_MS`

SMTP (email):

- `APP_SMTP_HOST`, `APP_SMTP_PORT`, `APP_SMTP_AUTH`
- `APP_SMTP_SSL_ENABLE`, `APP_SMTP_STARTTLS_ENABLE`, `APP_SMTP_SSL_TRUST`
- `APP_SMTP_USER`, `APP_SMTP_PASSWORD`

> Local opcional: copiar `app.local.properties.example` y `email.local.properties.example` a `*.local.properties`.

---

## 2) Base de datos

### Local

```bash
docker-compose up -d mysql phpmyadmin
```

Importar estructura completa de tu dump SQL y luego migraciones del repo si aplican.

### Producción

1. Instalar MySQL en VPS.
2. Crear DB y usuario dedicado (no root):

```sql
CREATE DATABASE portfolio_fotografo CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'portfolio_user'@'localhost' IDENTIFIED BY 'CAMBIAR_PASSWORD_FUERTE';
GRANT ALL PRIVILEGES ON portfolio_fotografo.* TO 'portfolio_user'@'localhost';
FLUSH PRIVILEGES;
```

3. Importar estructura SQL completa (la que compartiste).

---

## 3) GlassFish/Payara (local y prod)

El `persistence.xml` usa JNDI: `jdbc/portfolioFT`.

### Crear pool y recurso (asadmin)

```bash
asadmin create-jdbc-connection-pool \
  --datasourceclassname=com.mysql.cj.jdbc.MysqlDataSource \
  --restype=javax.sql.DataSource \
  --property user=portfolio_user:password=CAMBIAR_PASSWORD:DatabaseName=portfolio_fotografo:ServerName=localhost:port=3306 \
  PortfolioPool

asadmin create-jdbc-resource --connectionpoolid PortfolioPool jdbc/portfolioFT
```

### JVM options recomendadas (producción)

```bash
asadmin create-jvm-options "-DAPP_PUBLIC_API_BASE_URL=https://sentirfotografico.com.ar/api"
asadmin create-jvm-options "-DAPP_FRONTEND_BASE_URL=https://sentirfotografico.com.ar"
asadmin create-jvm-options "-DAPP_UPLOAD_DIR=/var/portfolio-uploads"
asadmin create-jvm-options "-DAPP_CORS_ALLOWED_ORIGINS=https://sentirfotografico.com.ar,https://www.sentirfotografico.com.ar"
asadmin create-jvm-options "-DAPP_JWT_SECRET=CAMBIAR_SECRET_LARGO_Y_RANDOM"
asadmin create-jvm-options "-DAPP_SMTP_USER=tu_usuario_smtp"
asadmin create-jvm-options "-DAPP_SMTP_PASSWORD=tu_password_smtp"
```

Deploy del backend:

```bash
cd portfolio-backend
mvn clean package
# deployar target/portfolio-backend.war en GlassFish/Payara
```

---

## 4) Frontend en producción (la parte que faltaba)

No se deploya dentro de GlassFish. Se builda y se sirve estático con Nginx.

```bash
cd frontend
npm install
npm run build -- --configuration production
```

Salida: `frontend/dist/frontend/browser/`

Copiar ese contenido a: `/var/www/sentirfotografico/frontend/browser/` en el VPS.

Usar config Nginx de ejemplo: `deploy/nginx/sentirfotografico.com.ar.conf`.

---

## 5) Dominio (NIC.ar -> DonWeb)

1. Registrar `sentirfotografico.com.ar` en NIC.ar.
2. En NIC, delegar DNS a DonWeb **o** configurar:
   - `A @` -> IP del VPS
   - `CNAME www` -> `@`
3. Esperar propagación.

---

## 6) SSL

En VPS:

```bash
sudo apt update
sudo apt install certbot python3-certbot-nginx -y
sudo certbot --nginx -d sentirfotografico.com.ar -d www.sentirfotografico.com.ar
```

---

## 7) Flujo operativo

### Modo Local

1. MySQL (docker/local)
2. GlassFish con `jdbc/portfolioFT`
3. Backend WAR deployado
4. Frontend: `ng serve`

### Modo Producción

1. MySQL servidor
2. GlassFish con JVM options `APP_*`
3. Backend WAR deployado
4. Frontend build estático en Nginx
5. DNS + SSL activo

---

## 8) Checklist rápido de salida a producción

- [ ] `APP_JWT_SECRET` cambiado
- [ ] SMTP configurado por variables (sin secretos en git)
- [ ] `jdbc/portfolioFT` apunta al MySQL de prod
- [ ] Frontend build servido por Nginx
- [ ] `/api/*` responde por proxy
- [ ] HTTPS válido en dominio y www
- [ ] Upload dir con permisos de escritura para usuario de GlassFish
