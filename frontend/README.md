# Frontend (Angular 17)

## Desarrollo local

```bash
npm install
ng serve
```

URL local: `http://localhost:4200`

En local el frontend consume `environment.ts`:

- `apiUrl: 'http://localhost:8080/portfolio-backend/api'`

## Build para producción

```bash
npm run build -- --configuration production
```

Salida estática:

- `dist/frontend/browser/`

En producción usa `environment.prod.ts`:

- `apiUrl: '/api'`

Eso permite que Nginx reverse-proxyee `/api/*` hacia GlassFish sin hardcodear dominio en el frontend.

## Deploy recomendado (VPS)

1. Copiar `dist/frontend/browser/*` al servidor (ej: `/var/www/sentirfotografico/frontend/browser/`).
2. Configurar Nginx con fallback SPA (`try_files ... /index.html`).
3. Configurar proxy `/api/` -> `http://127.0.0.1:8080/portfolio-backend/api/`.

Ver guía completa: [`../DEPLOYMENT.md`](../DEPLOYMENT.md).
