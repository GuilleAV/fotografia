-- ============================================
-- Migración: Multi-Photographer Portfolio
-- Fecha: 2026-04-05
-- Descripción: Agrega soporte para multi-fotógrafo,
--              moderación de fotos y procesamiento de imágenes
-- ============================================

-- 1. Agregar campo 'estado' a la tabla fotos
-- Estados: PENDIENTE, APROBADA, RECHAZADA
ALTER TABLE fotos
    ADD COLUMN estado VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE' AFTER activo;

-- 2. Actualizar fotos existentes a APROBADA (asumimos que las que ya existen están aprobadas)
UPDATE fotos SET estado = 'APROBADA' WHERE activo = TRUE;
UPDATE fotos SET estado = 'RECHAZADA' WHERE activo = FALSE;

-- 3. Agregar columnas para rutas de imágenes procesadas
ALTER TABLE fotos
    ADD COLUMN ruta_thumbnail VARCHAR(500) DEFAULT NULL AFTER ruta_archivo,
    ADD COLUMN ruta_web VARCHAR(500) DEFAULT NULL AFTER ruta_thumbnail;

-- 4. Agregar rol FOTOGRAFO (ya existe ADMIN y SUPER_ADMIN)
-- No se necesita ALTER TABLE, el rol es un VARCHAR(20)
-- Solo documentamos los roles válidos:
-- - SUPER_ADMIN: acceso total
-- - ADMIN: moderación + gestión
-- - FOTOGRAFO: subir y gestionar sus propias fotos

-- 5. Índice para mejorar consultas de moderación
CREATE INDEX idx_foto_estado ON fotos(estado);
CREATE INDEX idx_foto_usuario ON fotos(id_usuario);
CREATE INDEX idx_foto_categoria_estado ON fotos(id_categoria, estado);

-- ============================================
-- Datos de ejemplo (opcional)
-- ============================================

-- Crear un fotógrafo de ejemplo (password: bcrypt de "fotografo123")
-- INSERT INTO usuarios (username, password, email, nombre, apellido, rol, activo, fecha_creacion)
-- VALUES ('fotografo1', '$2a$10$...', 'fotografo@example.com', 'Juan', 'Pérez', 'FOTOGRAFO', TRUE, NOW());
