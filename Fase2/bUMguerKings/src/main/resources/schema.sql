-- ══════════════════════════════════════════════════════════════════════════════
-- Schema SQL - bUMguerKings
-- Estratégia: Uma tabela por classe com chaves estrangeiras
-- ══════════════════════════════════════════════════════════════════════════════

-- ─────────────────────────────────────────────────────────────────────────────
-- RESTAURANTE
-- ─────────────────────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS Restaurante (
    idRestaurante   INT PRIMARY KEY AUTO_INCREMENT,
    nome            VARCHAR(100) NOT NULL,
    localizacao     VARCHAR(200) NOT NULL,
    totalPedidos    INT DEFAULT 0,
    faturacaoTotal  DOUBLE DEFAULT 0.0
);

-- ─────────────────────────────────────────────────────────────────────────────
-- INGREDIENTE (por restaurante)
-- ─────────────────────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS Ingrediente (
    nome            VARCHAR(100),
    idRestaurante   INT,
    tipoPosto       VARCHAR(50) NOT NULL,  -- enum TipoPosto como string
    tempoEstimado   INT NOT NULL DEFAULT 1,
    removivel       BOOLEAN DEFAULT TRUE,
    PRIMARY KEY (nome, idRestaurante),
    FOREIGN KEY (idRestaurante) REFERENCES Restaurante(idRestaurante) ON DELETE CASCADE
);

-- Alergénios de um ingrediente (relação 1:N)
CREATE TABLE IF NOT EXISTS Ingrediente_Alergenio (
    nomeIngrediente VARCHAR(100),
    idRestaurante   INT,
    alergenio       VARCHAR(100),
    PRIMARY KEY (nomeIngrediente, idRestaurante, alergenio),
    FOREIGN KEY (nomeIngrediente, idRestaurante) REFERENCES Ingrediente(nome, idRestaurante) ON DELETE CASCADE
);

-- ─────────────────────────────────────────────────────────────────────────────
-- PROPOSTA (superclasse) - com discriminador de tipo
-- ─────────────────────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS Proposta (
    nome            VARCHAR(100),
    idRestaurante   INT,
    preco           DOUBLE NOT NULL,
    tipo            VARCHAR(10) NOT NULL,  -- 'PRODUTO' ou 'MENU' (discriminador)
    PRIMARY KEY (nome, idRestaurante),
    FOREIGN KEY (idRestaurante) REFERENCES Restaurante(idRestaurante) ON DELETE CASCADE
);

-- ─────────────────────────────────────────────────────────────────────────────
-- PRODUTO (subclasse de Proposta)
-- ─────────────────────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS Produto (
    nome            VARCHAR(100),
    idRestaurante   INT,
    PRIMARY KEY (nome, idRestaurante),
    FOREIGN KEY (nome, idRestaurante) REFERENCES Proposta(nome, idRestaurante) ON DELETE CASCADE
);

-- Ingredientes de um produto (relação N:M)
-- removivel: TRUE se pode ser removido do produto, FALSE se é obrigatório
-- adicionavel: TRUE se pode ser adicionado (extra), FALSE se já faz parte do produto
-- quantidade: número de vezes que o ingrediente aparece (ex: duplo queijo = 2)
CREATE TABLE IF NOT EXISTS Produto_Ingrediente (
    nomeProduto       VARCHAR(100),
    idRestaurante     INT,
    nomeIngrediente   VARCHAR(100),
    quantidade        INT DEFAULT 1,
    removivel         BOOLEAN DEFAULT FALSE,
    adicionavel       BOOLEAN DEFAULT FALSE,
    PRIMARY KEY (nomeProduto, idRestaurante, nomeIngrediente, adicionavel),
    FOREIGN KEY (nomeProduto, idRestaurante) REFERENCES Produto(nome, idRestaurante) ON DELETE CASCADE,
    FOREIGN KEY (nomeIngrediente, idRestaurante) REFERENCES Ingrediente(nome, idRestaurante) ON DELETE CASCADE
);

-- ─────────────────────────────────────────────────────────────────────────────
-- MENU (subclasse de Proposta)
-- ─────────────────────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS Menu (
    nome            VARCHAR(100),
    idRestaurante   INT,
    PRIMARY KEY (nome, idRestaurante),
    FOREIGN KEY (nome, idRestaurante) REFERENCES Proposta(nome, idRestaurante) ON DELETE CASCADE
);

-- Produtos de um menu (relação N:M)
CREATE TABLE IF NOT EXISTS Menu_Produto (
    nomeMenu        VARCHAR(100),
    idRestaurante   INT,
    nomeProduto     VARCHAR(100),
    PRIMARY KEY (nomeMenu, idRestaurante, nomeProduto),
    FOREIGN KEY (nomeMenu, idRestaurante) REFERENCES Menu(nome, idRestaurante) ON DELETE CASCADE,
    FOREIGN KEY (nomeProduto, idRestaurante) REFERENCES Produto(nome, idRestaurante) ON DELETE CASCADE
);

-- ─────────────────────────────────────────────────────────────────────────────
-- PEDIDO
-- ─────────────────────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS Pedido (
    idPedido        INT PRIMARY KEY AUTO_INCREMENT,
    idRestaurante   INT NOT NULL,
    nota            VARCHAR(500),
    precoTotal      DOUBLE DEFAULT 0.0,
    estado          VARCHAR(20) NOT NULL,  -- enum EstadoPedido como string
    eta             TIME,
    finalizadoEm    TIME,
    entregueEm      TIME,
    FOREIGN KEY (idRestaurante) REFERENCES Restaurante(idRestaurante) ON DELETE CASCADE
);

-- Propostas de um pedido (cópia da proposta no momento do pedido)
-- Como as propostas podem ser personalizadas, guardamos uma cópia serializada
CREATE TABLE IF NOT EXISTS Pedido_Proposta (
    idPedido          INT,
    ordemProposta     INT,  -- ordem da proposta no pedido
    nomeProposta      VARCHAR(100),
    tipoProposta      VARCHAR(10),  -- 'PRODUTO' ou 'MENU'
    precoUnitario     DOUBLE,
    PRIMARY KEY (idPedido, ordemProposta),
    FOREIGN KEY (idPedido) REFERENCES Pedido(idPedido) ON DELETE CASCADE
);

-- Ingredientes da proposta num pedido (para produtos personalizados)
-- quantidade: permite ingredientes duplicados (ex: duplo queijo = 2)
CREATE TABLE IF NOT EXISTS Pedido_Proposta_Ingrediente (
    idPedido          INT,
    ordemProposta     INT,
    nomeIngrediente   VARCHAR(100),
    quantidade        INT DEFAULT 1,
    PRIMARY KEY (idPedido, ordemProposta, nomeIngrediente),
    FOREIGN KEY (idPedido, ordemProposta) REFERENCES Pedido_Proposta(idPedido, ordemProposta) ON DELETE CASCADE
);

-- ─────────────────────────────────────────────────────────────────────────────
-- TAREFA
-- ─────────────────────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS Tarefa (
    idTarefa        INT PRIMARY KEY AUTO_INCREMENT,
    idRestaurante   INT NOT NULL,
    idPedido        INT NOT NULL,
    nomeProduto     VARCHAR(100),
    descricao       VARCHAR(500),
    tipoPosto       VARCHAR(50) NOT NULL,  -- enum TipoPosto como string
    estado          VARCHAR(20) NOT NULL,  -- enum EstadoTarefa como string
    tempoEstimado   INT DEFAULT 1,
    scheduledStart  TIME,
    scheduledFinish TIME,
    delayMinutes    INT DEFAULT 0,
    FOREIGN KEY (idRestaurante) REFERENCES Restaurante(idRestaurante) ON DELETE CASCADE,
    FOREIGN KEY (idPedido) REFERENCES Pedido(idPedido) ON DELETE CASCADE
);

-- Instruções de uma tarefa (relação 1:N)
CREATE TABLE IF NOT EXISTS Tarefa_Instrucao (
    idTarefa        INT,
    ordemInstrucao  INT,
    instrucao       VARCHAR(200),
    PRIMARY KEY (idTarefa, ordemInstrucao),
    FOREIGN KEY (idTarefa) REFERENCES Tarefa(idTarefa) ON DELETE CASCADE
);
