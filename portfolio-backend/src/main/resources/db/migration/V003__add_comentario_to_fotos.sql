-- ============================================
-- Migración: V3 - Comentario en fotos
-- Fecha: 2026-04-12
-- Descripción: agrega campo comentario para carga/edición de fotos
-- ============================================

ALTER TABLE fotos
    ADD COLUMN comentario TEXT NULL AFTER descripcion;
