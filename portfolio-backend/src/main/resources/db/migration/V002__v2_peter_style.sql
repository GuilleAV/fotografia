-- ============================================
-- Migración: V2 Peter Style - UI Redesign
-- Fecha: 2026-04-09
-- Descripción: Agrega categorías por defecto y redes sociales
-- ============================================

-- 1. Agregar campo es_default a categorias (categorías fijas del sistema)
ALTER TABLE categorias 
ADD COLUMN es_default BOOLEAN DEFAULT FALSE AFTER slug;

-- 2. Agregar campos de redes sociales a usuarios
ALTER TABLE usuarios 
ADD COLUMN social_youtube VARCHAR(255) DEFAULT NULL AFTER foto_perfil,
ADD COLUMN social_instagram VARCHAR(255) DEFAULT NULL AFTER social_youtube,
ADD COLUMN social_threads VARCHAR(255) DEFAULT NULL AFTER social_instagram;

-- 3. Seed de 5 categorías por defecto (solo si no existen)
-- Verificamos que no existan antes de insertar
INSERT INTO categorias (nombre, descripcion, slug, icono, color, orden, activo, es_default, fecha_creacion)
SELECT 'Retratos', 'Fotografías de personas, headshots, retratos familiares', 'retratos', 'fa-user', '#e74c3c', 1, TRUE, TRUE, NOW()
WHERE NOT EXISTS (SELECT 1 FROM categorias WHERE slug = 'retratos');

INSERT INTO categorias (nombre, descripcion, slug, icono, color, orden, activo, es_default, fecha_creacion)
SELECT 'Lugares', 'Paisajes, arquitectura, travel photography', 'lugares', 'fa-map-marker', '#3498db', 2, TRUE, TRUE, NOW()
WHERE NOT EXISTS (SELECT 1 FROM categorias WHERE slug = 'lugares');

INSERT INTO categorias (nombre, descripcion, slug, icono, color, orden, activo, es_default, fecha_creacion)
SELECT 'Productos', 'Food photography, productos, still life', 'productos', 'fa-box', '#9b59b6', 3, TRUE, TRUE, NOW()
WHERE NOT EXISTS (SELECT 1 FROM categorias WHERE slug = 'productos');

INSERT INTO categorias (nombre, descripcion, slug, icono, color, orden, activo, es_default, fecha_creacion)
SELECT 'Eventos', 'Bodas, quinceañeras, cumpleaños, eventos sociales', 'eventos', 'fa-calendar', '#f39c12', 4, TRUE, TRUE, NOW()
WHERE NOT EXISTS (SELECT 1 FROM categorias WHERE slug = 'eventos');

INSERT INTO categorias (nombre, descripcion, slug, icono, color, orden, activo, es_default, fecha_creacion)
SELECT 'Comercial', 'Trabajos para marcas, publicidad', 'comercial', 'fa-briefcase', '#2ecc71', 5, TRUE, TRUE, NOW()
WHERE NOT EXISTS (SELECT 1 FROM categorias WHERE slug = 'comercial');

-- 4. Índices para mejor rendimiento
CREATE INDEX idx_categoria_es_default ON categorias(es_default);
CREATE INDEX idx_usuario_redes ON usuarios(social_youtube, social_instagram, social_threads);
