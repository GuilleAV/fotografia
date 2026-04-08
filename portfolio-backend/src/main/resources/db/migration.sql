-- Agregar campo 'orden' a la tabla fotos para el carousel
ALTER TABLE fotos ADD COLUMN orden INT NULL;

-- Índices para优化的consultas
CREATE INDEX idx_fotos_orden ON fotos(orden) WHERE orden IS NOT NULL;
CREATE INDEX idx_fotos_destacada ON fotos(destacada) WHERE destacada = TRUE;

-- Ejemplo: actualizar una foto para el carousel (ponerla en posición 1)
-- UPDATE fotos SET orden = 1 WHERE id_foto = 1;

-- Ejemplo: marcar una foto como destacada
-- UPDATE fotos SET destacada = TRUE WHERE id_foto = 1;