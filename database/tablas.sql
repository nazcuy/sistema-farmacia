CREATE DATABASE sistema_farmacia;

USE sistema_farmacia;

-- Tabla principal de personas (encuestados)
CREATE TABLE personas (
    id INT PRIMARY KEY AUTO_INCREMENT,
    documento_identidad VARCHAR(50) UNIQUE, -- DNI o similar para identificación única
    nombre VARCHAR(100),
    apellido VARCHAR(100),
    fecha_nacimiento DATE,
    sexo ENUM('varón', 'mujer', 'LGBTIQ+'),
    pais_nacimiento VARCHAR(100),
    telefono VARCHAR(20),
    email VARCHAR(100),
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    activo BOOLEAN DEFAULT TRUE,
    INDEX idx_documento (documento_identidad),
    INDEX idx_nombre_apellido (nombre, apellido)
);

-- Tabla de domicilios (vivienda actual)
CREATE TABLE domicilios (
    id INT PRIMARY KEY AUTO_INCREMENT,
    persona_id INT,
    provincia VARCHAR(100),
    municipio VARCHAR(100),
    localidad VARCHAR(100),
    barrio VARCHAR(100),
    calle VARCHAR(150),
    numero VARCHAR(20),
    piso VARCHAR(10),
    departamento VARCHAR(10),
    manzana INT,
    lote INT,
    frente_manzana CHAR(1), -- Ej: "Frente a la plaza" o "Lado norte"
    entre_calle_1 VARCHAR(150),
    entre_calle_2 VARCHAR(150),
    observaciones_ubicacion TEXT, -- Referencias visuales adicionales
    
    -- Campos sociales
    material_construccion SET('ladrillo', 'madera', 'chapa', 'mixto', 'otros'),
    acceso_agua ENUM('cañería dentro de la vivienda', 'fuera de la vivienda pero en el terreno', 
                     'fuera del terreno', 'No tiene acceso a agua potable'),
    tipo_cocina SET('gas de red', 'gas envasado', 'electricidad', 'leña - carbón', 'otro combustible'),
    conexion_electricidad ENUM('con medidor', 'enganchado', 'no tiene'),
    situacion_ingresos ENUM('No alcanza para necesidades básicas', 'Alcanza para necesidades básicas',
                           'Queda resto para ahorrar'),
    
    fecha_desde DATE,
    actual BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (persona_id) REFERENCES personas(id) ON DELETE CASCADE
);

-- Tabla de encuestas (una persona puede tener múltiples encuestas en el tiempo)
CREATE TABLE encuestas (
    id INT PRIMARY KEY AUTO_INCREMENT,
    persona_id INT,
    fecha_encuesta TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    encuestador_id INT,
    provincia VARCHAR(100),
    municipio VARCHAR(100),
    barrio VARCHAR(100),
    geolocalizacion POINT,
    prueba BOOLEAN DEFAULT FALSE,
    observaciones TEXT,
    participacion_devolucion ENUM('Sí', 'No'),
    FOREIGN KEY (persona_id) REFERENCES personas(id) ON DELETE CASCADE,
    INDEX idx_persona_fecha (persona_id, fecha_encuesta),
    INDEX idx_fecha (fecha_encuesta)
);

-- Tabla de composición familiar (otros habitantes de la vivienda)
CREATE TABLE familiares (
    id INT PRIMARY KEY AUTO_INCREMENT,
    persona_id INT, -- Persona principal (encuestado)
    familiar_persona_id INT NULL, -- Si ya está registrado en la tabla personas
    relacion ENUM('pareja', 'hijo/a', 'padre', 'madre', 'hermano/a', 'abuelo/a', 'otro familiar', 'no familiar'),
    sexo ENUM('varón', 'mujer', 'LGBTIQ+'),
    es_menor BOOLEAN DEFAULT FALSE,
    edad INT,
    pais_nacimiento VARCHAR(100),
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (persona_id) REFERENCES personas(id) ON DELETE CASCADE,
    FOREIGN KEY (familiar_persona_id) REFERENCES personas(id) ON DELETE SET NULL,
    INDEX idx_persona_familia (persona_id)
);

-- Tabla de cobertura de salud (historial de coberturas)
CREATE TABLE coberturas_salud (
    id INT PRIMARY KEY AUTO_INCREMENT,
    persona_id INT,
    encuesta_id INT,
    tipo_cobertura ENUM('Obra social/Mutual', 'Prepaga', 'PAMI/Incluir Salud', 
                       'Sistema Público de Salud', 'Ninguna'),
    fecha_desde DATE,
    fecha_hasta DATE,
    actual BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (persona_id) REFERENCES personas(id) ON DELETE CASCADE,
    FOREIGN KEY (encuesta_id) REFERENCES encuestas(id),
    INDEX idx_persona_cobertura (persona_id)
);

-- Tabla de lugares de atención preferidos
CREATE TABLE lugares_atencion (
    id INT PRIMARY KEY AUTO_INCREMENT,
    persona_id INT,
    encuesta_id INT,
    lugar_atencion ENUM('Centro de salud o salita pública', 'Hospital público', 
                       'Clínica privada', 'Centro de salud de obra social', 
                       'Posta de salud', 'Otro'),
    otro_lugar TEXT,
    preferencia INT DEFAULT 1, -- 1=primera opción, 2=segunda, etc.
    FOREIGN KEY (persona_id) REFERENCES personas(id) ON DELETE CASCADE,
    FOREIGN KEY (encuesta_id) REFERENCES encuestas(id),
    INDEX idx_persona_lugar (persona_id)
);

-- Tabla de dificultades en atención
CREATE TABLE dificultades_atencion (
    id INT PRIMARY KEY AUTO_INCREMENT,
    persona_id INT,
    encuesta_id INT,
    dificultad ENUM('no tuvo dificultad', 'no conoce efectores de salud', 'pocos horarios de atención',
                    'no conseguir turnos', 'falta de especialidades', 'distancias muy largas',
                    'maltrato', 'no pudo pagar', 'otro'),
    otra_dificultad TEXT,
    fecha_incidente DATE,
    FOREIGN KEY (persona_id) REFERENCES personas(id) ON DELETE CASCADE,
    FOREIGN KEY (encuesta_id) REFERENCES encuestas(id),
    INDEX idx_persona_dificultad (persona_id)
);

-- Tabla de consultas médicas
CREATE TABLE consultas_medicas (
    id INT PRIMARY KEY AUTO_INCREMENT,
    persona_id INT,
    encuesta_id INT,
    fecha_consulta DATE,
    especialidad ENUM('enfermería', 'medicina general', 'clínica médica', 'pediatría', 
                     'ginecología', 'obstetricia', 'psicología', 'psiquiatría',
                     'odontología', 'trabajo social', 'fonoaudiologia', 'psicopedagogía',
                     'otras'),
    motivo TEXT,
    lugar ENUM('Centro de salud público', 'Hospital público', 'Clínica privada',
              'Consultorio privado', 'Centro de obra social', 'Posta de salud',
              'Comunitario', 'Otro'),
    otro_lugar TEXT,
    FOREIGN KEY (persona_id) REFERENCES personas(id) ON DELETE CASCADE,
    FOREIGN KEY (encuesta_id) REFERENCES encuestas(id),
    INDEX idx_persona_consultas (persona_id, fecha_consulta)
);

-- Tabla de salud mental
CREATE TABLE salud_mental (
    id INT PRIMARY KEY AUTO_INCREMENT,
    persona_id INT,
    encuesta_id INT,
    consulto_psicologo_psiquiatra BOOLEAN,
    ultima_consulta DATE,
    acceso_oportuno ENUM('Sí', 'No'),
    motivo_no_acceso TEXT,
    tratamiento_actual BOOLEAN,
    FOREIGN KEY (persona_id) REFERENCES personas(id) ON DELETE CASCADE,
    FOREIGN KEY (encuesta_id) REFERENCES encuestas(id),
    INDEX idx_persona_salud_mental (persona_id)
);

-- Tabla de chequeos preventivos
CREATE TABLE chequeos_preventivos (
    id INT PRIMARY KEY AUTO_INCREMENT,
    persona_id INT,
    encuesta_id INT,
    tipo_chequeo ENUM('general', 'mujer', 'varón', 'niño/a'),
    fecha_ultimo DATE,
    periodicidad ENUM('anual', 'bianual', 'cada 5 años', 'nunca'),
    FOREIGN KEY (persona_id) REFERENCES personas(id) ON DELETE CASCADE,
    FOREIGN KEY (encuesta_id) REFERENCES encuestas(id),
    INDEX idx_persona_chequeos (persona_id)
);

-- Tabla de vacunación
CREATE TABLE vacunaciones (
    id INT PRIMARY KEY AUTO_INCREMENT,
    persona_id INT,
    encuesta_id INT,
    calendario_completo BOOLEAN,
    fecha_ultima DATE,
    acceso_vacunacion ENUM('Sí, siempre', 'Si, a veces', 'No, nunca'),
    motivo_no_acceso TEXT,
    fuente_informacion SET('En la escuela', 'En el centro de salud', 'Por promotoras de salud',
                          'En el barrio', 'Otro'),
    FOREIGN KEY (persona_id) REFERENCES personas(id) ON DELETE CASCADE,
    FOREIGN KEY (encuesta_id) REFERENCES encuestas(id),
    INDEX idx_persona_vacunacion (persona_id)
);

-- Tabla de diagnósticos (enfermedades)
CREATE TABLE diagnosticos (
    id INT PRIMARY KEY AUTO_INCREMENT,
    persona_id INT,
    encuesta_id INT,
    enfermedad ENUM('Cardiovasculares', 'Respiratorios', 'Tuberculosis', 'En la piel', 
                   'En la vista', 'Gastrointestinales', 'Ginecológicos', 'Alergias', 
                   'En los huesos/articulaciones', 'Salud mental', 'Diabetes', 
                   'Hipertensión', 'Problemas de tiroides', 'Odontológicos', 'Cáncer', 
                   'Infección de transmisión sexual', 'Chagas', 'Dengue, zika o chikungunya',
                   'Otra'),
    otra_enfermedad TEXT,
    fecha_diagnostico DATE,
    en_tratamiento BOOLEAN,
    controlado BOOLEAN,
    FOREIGN KEY (persona_id) REFERENCES personas(id) ON DELETE CASCADE,
    FOREIGN KEY (encuesta_id) REFERENCES encuestas(id),
    INDEX idx_persona_diagnosticos (persona_id)
);

-- Tabla de consumo de sustancias
CREATE TABLE consumo_sustancias (
    id INT PRIMARY KEY AUTO_INCREMENT,
    persona_id INT,
    encuesta_id INT,
    sustancia ENUM('cocaína', 'marihuana', 'crack', 'paco', 'psicofármacos', 
                  'alcohol', 'tabaco', 'otra'),
    otra_sustancia TEXT,
    consumo_actual BOOLEAN,
    frecuencia ENUM('diario', 'semanal', 'mensual', 'ocasional', 'ex-consumidor'),
    fecha_inicio DATE,
    fecha_fin DATE,
    FOREIGN KEY (persona_id) REFERENCES personas(id) ON DELETE CASCADE,
    FOREIGN KEY (encuesta_id) REFERENCES encuestas(id),
    INDEX idx_persona_consumo (persona_id)
);

-- Tabla de situaciones de violencia
CREATE TABLE situaciones_violencia (
    id INT PRIMARY KEY AUTO_INCREMENT,
    persona_id INT,
    encuesta_id INT,
    tipo_violencia ENUM('física', 'psicológica', 'económica', 'sexual', 'institucional'),
    contexto ENUM('doméstica', 'laboral', 'institucional', 'comunitaria'),
    fecha_incidente DATE,
    denunciado BOOLEAN,
    en_seguimiento BOOLEAN,
    FOREIGN KEY (persona_id) REFERENCES personas(id) ON DELETE CASCADE,
    FOREIGN KEY (encuesta_id) REFERENCES encuestas(id),
    INDEX idx_persona_violencia (persona_id)
);

-- Tabla de discapacidades
CREATE TABLE discapacidades (
    id INT PRIMARY KEY AUTO_INCREMENT,
    persona_id INT,
    encuesta_id INT,
    tipo_discapacidad ENUM('psicosocial/mental', 'intelectual', 'física', 'sensorial',
                          'del desarrollo/neurodivergencia', 'Otra'),
    otro_tipo TEXT,
    certificado_unico BOOLEAN,
    fecha_certificado DATE,
    fecha_vencimiento DATE,
    necesita_ayuda_tecnica TEXT,
    FOREIGN KEY (persona_id) REFERENCES personas(id) ON DELETE CASCADE,
    FOREIGN KEY (encuesta_id) REFERENCES encuestas(id),
    INDEX idx_persona_discapacidad (persona_id)
);

-- Tabla de embarazos
CREATE TABLE embarazos (
    id INT PRIMARY KEY AUTO_INCREMENT,
    persona_id INT,
    encuesta_id INT,
    embarazo_actual BOOLEAN,
    fecha_ultima_regla DATE,
    fecha_probable_parto DATE,
    trimestre ENUM('Primer trimestre', 'Segundo trimestre', 'Tercer trimestre'),
    numero_controles INT DEFAULT 0,
    lugar_controles SET('Centro de salud público', 'Hospital público', 'Clínica privada',
                       'Centro de obra social', 'Posta de salud', 'Otro'),
    otro_lugar TEXT,
    FOREIGN KEY (persona_id) REFERENCES personas(id) ON DELETE CASCADE,
    FOREIGN KEY (encuesta_id) REFERENCES encuestas(id),
    INDEX idx_persona_embarazos (persona_id)
);

-- Tabla de medicaciones
CREATE TABLE medicaciones (
    id INT PRIMARY KEY AUTO_INCREMENT,
    persona_id INT,
    encuesta_id INT,
    medicamento VARCHAR(255),
    indicacion TEXT,
    fecha_inicio DATE,
    fecha_fin DATE,
    acceso_regular BOOLEAN,
    forma_conseguir ENUM('Centro de salud público', 'Hospital público', 'Posta/operativo',
                        'Obra social', 'Compra', 'Otro'),
    continuidad_correcta BOOLEAN,
    motivo_discontinuidad TEXT,
    FOREIGN KEY (persona_id) REFERENCES personas(id) ON DELETE CASCADE,
    FOREIGN KEY (encuesta_id) REFERENCES encuestas(id),
    INDEX idx_persona_medicaciones (persona_id)
);

-- Tabla de estudios ginecológicos/preventivos
CREATE TABLE estudios_preventivos (
    id INT PRIMARY KEY AUTO_INCREMENT,
    persona_id INT,
    encuesta_id INT,
    tipo_estudio ENUM('PAP', 'test ITS', 'mamografía', 'otro'),
    fecha_ultimo DATE,
    resultado TEXT,
    proximo_control DATE,
    FOREIGN KEY (persona_id) REFERENCES personas(id) ON DELETE CASCADE,
    FOREIGN KEY (encuesta_id) REFERENCES encuestas(id),
    INDEX idx_persona_estudios (persona_id)
);

-- Tabla de métodos anticonceptivos
CREATE TABLE metodos_anticonceptivos (
    id INT PRIMARY KEY AUTO_INCREMENT,
    persona_id INT,
    encuesta_id INT,
    metodo ENUM('Pastillas', 'Inyección', 'Chip/implante', 'DIU', 'SIU',
               'Preservativo', 'Ligadura', 'Anillo vaginal', 'Vasectomía', 'Otro'),
    otro_metodo TEXT,
    fecha_inicio DATE,
    acceso_regular BOOLEAN,
    lugar_conseguir ENUM('Centro de salud público', 'Hospital público', 'Posta/operativo',
                        'Obra Social', 'Compra', 'Otro'),
    dificultad_conseguir BOOLEAN,
    metodo_deseado TEXT,
    FOREIGN KEY (persona_id) REFERENCES personas(id) ON DELETE CASCADE,
    FOREIGN KEY (encuesta_id) REFERENCES encuestas(id),
    INDEX idx_persona_anticonceptivos (persona_id)
);

-- Tabla de situación barrial (compartida por personas del mismo barrio)
CREATE TABLE situacion_barrial (
    id INT PRIMARY KEY AUTO_INCREMENT,
    provincia VARCHAR(100),
    municipio VARCHAR(100),
    barrio VARCHAR(100),
    servicios SET('Ambulancia', 'Patrullero', 'Recolección de residuos', 'Transporte público',
                 'Electricidad', 'Gas corriente', 'Agua corriente', 'Otros'),
    otros_servicios TEXT,
    acumulacion_basura ENUM('mucha', 'poca', 'no hay'),
    inundaciones ENUM('frecuentes', 'a veces', 'nunca'),
    agua_estancada ENUM('frecuente', 'a veces', 'no hay'),
    malos_olores_humo ENUM('frecuentes', 'a veces', 'nunca'),
    roedores_alacranes ENUM('muchos', 'pocos', 'no hay'),
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_barrio (provincia, municipio, barrio)
);

-- Tabla de relación persona-barrio
CREATE TABLE persona_barrio (
    persona_id INT,
    situacion_barrial_id INT,
    fecha_desde DATE,
    fecha_hasta DATE,
    PRIMARY KEY (persona_id, situacion_barrial_id),
    FOREIGN KEY (persona_id) REFERENCES personas(id) ON DELETE CASCADE,
    FOREIGN KEY (situacion_barrial_id) REFERENCES situacion_barrial(id)
);



-- FARMACIA
-- 1. MEDICAMENTOS (catálogo principal)
CREATE TABLE medicamentos (
    id INT PRIMARY KEY AUTO_INCREMENT,
    codigo_barras VARCHAR(50) UNIQUE,
    nombre_comercial VARCHAR(150) NOT NULL,
    nombre_generico VARCHAR(150) NOT NULL,
    concentracion VARCHAR(100), -- Ej: 500mg, 20mg/ml
    forma_farmaceutica VARCHAR(50), -- Comprimidos, jarabe, crema, etc.
    via_administracion VARCHAR(50), -- Oral, tópico, intramuscular
    presentacion VARCHAR(100), -- Ej: Caja x 20 comprimidos
    stock_total INT DEFAULT 0, -- Stock calculado automáticamente
    stock_minimo INT DEFAULT 10,
    stock_maximo INT DEFAULT 100,
    precio_referencia DECIMAL(10,2), -- Para control interno
    requiere_receta BOOLEAN DEFAULT FALSE,
    categoria VARCHAR(50), -- Analgésico, antibiótico, antihipertensivo
    laboratorio VARCHAR(100),
    principio_activo VARCHAR(150),
    condiciones_almacenamiento VARCHAR(100),
    activo BOOLEAN DEFAULT TRUE,
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_nombre_generico (nombre_generico),
    INDEX idx_categoria (categoria),
    INDEX idx_activo (activo)
);

-- 2. LOTES (control por fecha de vencimiento)
CREATE TABLE lotes (
    id INT PRIMARY KEY AUTO_INCREMENT,
    medicamento_id INT NOT NULL,
    codigo_lote VARCHAR(50) NOT NULL,
    fecha_fabricacion DATE,
    fecha_vencimiento DATE NOT NULL,
    fecha_ingreso DATE DEFAULT (CURRENT_DATE),
    cantidad_inicial INT NOT NULL,
    cantidad_actual INT NOT NULL,
    precio_costo DECIMAL(10,2),
    proveedor VARCHAR(100),
    numero_remito VARCHAR(50),
    ubicacion_almacen VARCHAR(50), -- Estante, refrigerador, etc.
    estado ENUM('activo', 'vencido', 'agotado', 'retirado') DEFAULT 'activo',
    FOREIGN KEY (medicamento_id) REFERENCES medicamentos(id) ON DELETE CASCADE,
    INDEX idx_fecha_vencimiento (fecha_vencimiento),
    INDEX idx_estado (estado),
    INDEX idx_medicamento_lote (medicamento_id, estado)
);

-- 3. PROVEEDORES (si quieres llevar control)
CREATE TABLE proveedores (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(150) NOT NULL,
    contacto VARCHAR(100),
    telefono VARCHAR(20),
    email VARCHAR(100),
    direccion TEXT,
    activo BOOLEAN DEFAULT TRUE
);

-- 4. ENTREGAS/RECETAS (ahora más completa)
CREATE TABLE recetas (
    id INT PRIMARY KEY AUTO_INCREMENT,
    persona_id INT NOT NULL,
    profesional_salud VARCHAR(150), -- Nombre del médico que recetó
    matricula_profesional VARCHAR(50),
    diagnostico TEXT,
    fecha_prescripcion DATE,
    fecha_entrega TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    tipo_receta ENUM('comun', 'cronica', 'oncologica', 'psicotropica'),
    estado ENUM('pendiente', 'completa', 'parcial', 'cancelada') DEFAULT 'pendiente',
    observaciones TEXT,
    FOREIGN KEY (persona_id) REFERENCES personas(id) ON DELETE CASCADE,
    INDEX idx_persona_fecha (persona_id, fecha_entrega),
    INDEX idx_estado_receta (estado)
);

-- 5. DETALLE DE ENTREGA (qué medicamentos se entregaron en cada receta)
CREATE TABLE entregas_detalle (
    id INT PRIMARY KEY AUTO_INCREMENT,
    receta_id INT NOT NULL,
    medicamento_id INT NOT NULL,
    lote_id INT NOT NULL,
    cantidad_prescrita INT NOT NULL,
    cantidad_entregada INT NOT NULL,
    posologia TEXT, -- Ej: "1 comprimido cada 8 horas por 7 días"
    via_administracion VARCHAR(50),
    duracion_tratamiento INT, -- En días
    observaciones TEXT,
    FOREIGN KEY (receta_id) REFERENCES recetas(id) ON DELETE CASCADE,
    FOREIGN KEY (medicamento_id) REFERENCES medicamentos(id),
    FOREIGN KEY (lote_id) REFERENCES lotes(id),
    INDEX idx_receta_detalle (receta_id)
);

-- 6. MOVIMIENTOS DE STOCK (auditoría completa)
CREATE TABLE movimientos_stock (
    id INT PRIMARY KEY AUTO_INCREMENT,
    fecha_movimiento TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    tipo_movimiento ENUM('ingreso', 'egreso', 'ajuste', 'vencimiento', 'merma'),
    medicamento_id INT NOT NULL,
    lote_id INT,
    cantidad INT NOT NULL,
    stock_anterior INT,
    stock_nuevo INT,
    motivo TEXT,
    usuario_id INT,
    receta_id INT,
    FOREIGN KEY (medicamento_id) REFERENCES medicamentos(id),
    FOREIGN KEY (lote_id) REFERENCES lotes(id),
    FOREIGN KEY (receta_id) REFERENCES recetas(id),
    INDEX idx_fecha_tipo (fecha_movimiento, tipo_movimiento),
    INDEX idx_medicamento_movimiento (medicamento_id)
);

-- 7. ALERTAS (para vencimientos y stock bajo)
CREATE TABLE alertas_farmacia (
    id INT PRIMARY KEY AUTO_INCREMENT,
    tipo_alerta ENUM('vencimiento', 'stock_minimo', 'stock_agotado', 'lote_vencido'),
    medicamento_id INT,
    lote_id INT,
    mensaje TEXT,
    fecha_alerta TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    leida BOOLEAN DEFAULT FALSE,
    fecha_leida TIMESTAMP NULL,
    FOREIGN KEY (medicamento_id) REFERENCES medicamentos(id),
    FOREIGN KEY (lote_id) REFERENCES lotes(id),
    INDEX idx_alerta_no_leida (leida)
);

-- 8. USUARIOS FARMACIA (si necesitas control de quién hace qué)
CREATE TABLE usuarios_farmacia (
    id INT PRIMARY KEY AUTO_INCREMENT,
    persona_id INT UNIQUE,
    usuario VARCHAR(50) UNIQUE,
    rol ENUM('farmaceutico', 'auxiliar', 'administrador'),
    activo BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (persona_id) REFERENCES personas(id)
);

