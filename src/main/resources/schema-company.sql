-- ============================================================
-- ReparaYa — Schema: company
-- Motor: PostgreSQL (Neon Cloud)
-- Ejecutar antes de levantar company-service
-- ============================================================

-- Crear schema si no existe
CREATE SCHEMA IF NOT EXISTS company;

-- ─── TIPOS ENUMERADOS ────────────────────────────────────────

CREATE TYPE company.empresa_estado_enum AS ENUM (
    'ACTIVA', 'INACTIVA', 'SUSPENDIDA'
);

CREATE TYPE company.categoria_enum AS ENUM (
    'VIALIDAD', 'ALUMBRADO', 'AGUA_POTABLE', 'ALCANTARILLADO', 'OTRO'
);

-- ─── TABLA: empresa_servicio ─────────────────────────────────

CREATE TABLE IF NOT EXISTS company.empresa_servicio (
    id                   UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    nombre               VARCHAR(200) NOT NULL,
    ruc                  VARCHAR(11)  NOT NULL,
    email_coordinador    VARCHAR(255) NOT NULL,
    whatsapp_coordinador VARCHAR(20),
    capacidad_diaria_max INTEGER      NOT NULL DEFAULT 5 CHECK (capacidad_diaria_max >= 1),
    trabajos_hoy         INTEGER      NOT NULL DEFAULT 0 CHECK (trabajos_hoy >= 0),
    estado               company.empresa_estado_enum NOT NULL DEFAULT 'ACTIVA',
    vigencia_contrato    DATE,
    fecha_creacion       TIMESTAMP    NOT NULL DEFAULT NOW(),
    fecha_actualizacion  TIMESTAMP    NOT NULL DEFAULT NOW(),

    CONSTRAINT uk_empresa_ruc UNIQUE (ruc)
);

-- ─── TABLA: empresa_especialidad (colección de especialidades) ─

CREATE TABLE IF NOT EXISTS company.empresa_especialidad (
    empresa_id UUID                    NOT NULL REFERENCES company.empresa_servicio(id) ON DELETE CASCADE,
    categoria  company.categoria_enum  NOT NULL,
    PRIMARY KEY (empresa_id, categoria)
);

-- ─── ÍNDICES ─────────────────────────────────────────────────

CREATE INDEX IF NOT EXISTS idx_empresa_estado
    ON company.empresa_servicio(estado);

CREATE INDEX IF NOT EXISTS idx_empresa_vigencia
    ON company.empresa_servicio(vigencia_contrato);

CREATE INDEX IF NOT EXISTS idx_especialidad_categoria
    ON company.empresa_especialidad(categoria);

-- ─── FUNCIÓN: actualizar fecha_actualizacion automáticamente ──

CREATE OR REPLACE FUNCTION company.update_timestamp()
RETURNS TRIGGER AS $$
BEGIN
    NEW.fecha_actualizacion = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_empresa_update
    BEFORE UPDATE ON company.empresa_servicio
    FOR EACH ROW EXECUTE FUNCTION company.update_timestamp();

-- ─── DATOS INICIALES (seeds para desarrollo) ─────────────────

INSERT INTO company.empresa_servicio
    (id, nombre, ruc, email_coordinador, whatsapp_coordinador,
     capacidad_diaria_max, trabajos_hoy, estado, vigencia_contrato)
VALUES
    ('11111111-1111-1111-1111-111111111111',
     'Constructora Lima SAC', '20789002001',
     'coord@constructorima.pe', '+51987000001',
     15, 0, 'ACTIVA', '2025-12-31'),

    ('22222222-2222-2222-2222-222222222222',
     'Electro Norte SAC', '20131887001',
     'coord@electronorte.pe', '+51987000002',
     10, 0, 'ACTIVA', '2025-12-31'),

    ('33333333-3333-3333-3333-333333333333',
     'AquaTec SRL', '20445621001',
     'coord@aquatec.pe', null,
     10, 0, 'ACTIVA', '2025-06-30'),

    ('44444444-4444-4444-4444-444444444444',
     'ServiRed Trujillo', '20334455001',
     'coord@servired.pe', null,
     12, 0, 'ACTIVA', '2025-09-30')
ON CONFLICT (ruc) DO NOTHING;

INSERT INTO company.empresa_especialidad (empresa_id, categoria)
VALUES
    ('11111111-1111-1111-1111-111111111111', 'VIALIDAD'),
    ('22222222-2222-2222-2222-222222222222', 'ALUMBRADO'),
    ('33333333-3333-3333-3333-333333333333', 'AGUA_POTABLE'),
    ('33333333-3333-3333-3333-333333333333', 'ALCANTARILLADO'),
    ('44444444-4444-4444-4444-444444444444', 'VIALIDAD'),
    ('44444444-4444-4444-4444-444444444444', 'OTRO')
ON CONFLICT DO NOTHING;
