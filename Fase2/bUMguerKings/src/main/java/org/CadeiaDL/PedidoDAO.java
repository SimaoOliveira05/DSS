package org.CadeiaDL;

import org.CadeiaLN.SubSistemaRestaurante.SubSistemaPedidos.*;

import java.sql.*;
import java.time.LocalTime;
import java.util.*;

/**
 * DAO para Pedido que implementa Map<Integer, Pedido>.
 * Cada instância está associada a um restaurante específico.
 */
public class PedidoDAO implements Map<Integer, Pedido> {
    
    private final int idRestaurante;
    private final IngredienteDAO ingredienteDAO;
    private final PropostaDAO propostaDAO;
    
    public PedidoDAO(int idRestaurante) {
        this.idRestaurante = idRestaurante;
        this.ingredienteDAO = new IngredienteDAO(idRestaurante);
        this.propostaDAO = new PropostaDAO(idRestaurante);
    }
    
    private Connection getConnection() throws SQLException {
        return ConnectionManager.getInstance().getConnection();
    }
    
    // ════════════════════════════════════════════════════════════════════════
    // Implementação de Map<Integer, Pedido>
    // ════════════════════════════════════════════════════════════════════════
    
    @Override
    public int size() {
        String sql = "SELECT COUNT(*) FROM Pedido WHERE idRestaurante = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idRestaurante);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao contar pedidos: " + e.getMessage(), e);
        }
        return 0;
    }
    
    @Override
    public boolean isEmpty() {
        return size() == 0;
    }
    
    @Override
    public boolean containsKey(Object key) {
        if (!(key instanceof Integer)) return false;
        String sql = "SELECT 1 FROM Pedido WHERE idPedido = ? AND idRestaurante = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, (Integer) key);
            stmt.setInt(2, idRestaurante);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao verificar pedido: " + e.getMessage(), e);
        }
    }
    
    @Override
    public boolean containsValue(Object value) {
        if (!(value instanceof Pedido)) return false;
        return containsKey(((Pedido) value).getIdPedido());
    }
    
    @Override
    public Pedido get(Object key) {
        if (!(key instanceof Integer)) return null;
        int idPedido = (Integer) key;
        
        String sql = "SELECT idPedido, nota, precoTotal, estado, eta, finalizadoEm, entregueEm FROM Pedido WHERE idPedido = ? AND idRestaurante = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idPedido);
            stmt.setInt(2, idRestaurante);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return pedidoFromResultSet(conn, rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao obter pedido: " + e.getMessage(), e);
        }
        return null;
    }
    
    @Override
    public Pedido put(Integer key, Pedido pedido) {
        Pedido old = get(key);
        if (old == null) {
            insert(pedido);
        } else {
            update(pedido);
        }
        return old;
    }
    
    @Override
    public Pedido remove(Object key) {
        if (!(key instanceof Integer)) return null;
        Pedido old = get(key);
        if (old != null) {
            String sql = "DELETE FROM Pedido WHERE idPedido = ? AND idRestaurante = ?";
            try (Connection conn = getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, (Integer) key);
                stmt.setInt(2, idRestaurante);
                stmt.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException("Erro ao remover pedido: " + e.getMessage(), e);
            }
        }
        return old;
    }
    
    @Override
    public void putAll(Map<? extends Integer, ? extends Pedido> m) {
        for (Entry<? extends Integer, ? extends Pedido> entry : m.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }
    
    @Override
    public void clear() {
        String sql = "DELETE FROM Pedido WHERE idRestaurante = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idRestaurante);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao limpar pedidos: " + e.getMessage(), e);
        }
    }
    
    @Override
    public Set<Integer> keySet() {
        Set<Integer> keys = new HashSet<>();
        String sql = "SELECT idPedido FROM Pedido WHERE idRestaurante = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idRestaurante);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    keys.add(rs.getInt("idPedido"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar IDs de pedidos: " + e.getMessage(), e);
        }
        return keys;
    }
    
    @Override
    public Collection<Pedido> values() {
        List<Pedido> list = new ArrayList<>();
        String sql = "SELECT idPedido, nota, precoTotal, estado, eta, finalizadoEm, entregueEm FROM Pedido WHERE idRestaurante = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idRestaurante);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(pedidoFromResultSet(conn, rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar pedidos: " + e.getMessage(), e);
        }
        return list;
    }
    
    @Override
    public Set<Entry<Integer, Pedido>> entrySet() {
        Set<Entry<Integer, Pedido>> entries = new HashSet<>();
        for (Pedido p : values()) {
            entries.add(new AbstractMap.SimpleEntry<>(p.getIdPedido(), p));
        }
        return entries;
    }
    
    // ════════════════════════════════════════════════════════════════════════
    // Métodos auxiliares - INSERT
    // ════════════════════════════════════════════════════════════════════════
    
    /**
     * Cria um novo pedido na base de dados e retorna o ID gerado (AUTO_INCREMENT).
     * Utilizar este método em vez de put() para novos pedidos.
     */
    public int criarNovoPedido(Pedido pedido) {
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            try {
                // Inserir pedido sem especificar idPedido (usa AUTO_INCREMENT)
                String sql = "INSERT INTO Pedido (idRestaurante, nota, precoTotal, estado, eta, finalizadoEm, entregueEm) VALUES (?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                    stmt.setInt(1, idRestaurante);
                    stmt.setString(2, pedido.getNota());
                    stmt.setDouble(3, pedido.getPrecoTotal());
                    stmt.setString(4, pedido.getEstado().name());
                    stmt.setTime(5, pedido.getEta() != null ? Time.valueOf(pedido.getEta()) : null);
                    stmt.setTime(6, pedido.getFinalizadoEm() != null ? Time.valueOf(pedido.getFinalizadoEm()) : null);
                    stmt.setTime(7, pedido.getEntregueEm() != null ? Time.valueOf(pedido.getEntregueEm()) : null);
                    stmt.executeUpdate();
                    
                    // Obter o ID gerado
                    try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            int novoId = generatedKeys.getInt(1);
                            pedido.setIdPedido(novoId);
                        }
                    }
                }
                
                // Inserir propostas do pedido (se houver)
                if (!pedido.getPropostas().isEmpty()) {
                    insertPropostasPedido(conn, pedido);
                }
                
                conn.commit();
                return pedido.getIdPedido();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao criar novo pedido: " + e.getMessage(), e);
        }
    }
    
    private void insert(Pedido pedido) {
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            try {
                // Inserir pedido
                String sql = "INSERT INTO Pedido (idPedido, idRestaurante, nota, precoTotal, estado, eta, finalizadoEm, entregueEm) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setInt(1, pedido.getIdPedido());
                    stmt.setInt(2, idRestaurante);
                    stmt.setString(3, pedido.getNota());
                    stmt.setDouble(4, pedido.getPrecoTotal());
                    stmt.setString(5, pedido.getEstado().name());
                    stmt.setTime(6, pedido.getEta() != null ? Time.valueOf(pedido.getEta()) : null);
                    stmt.setTime(7, pedido.getFinalizadoEm() != null ? Time.valueOf(pedido.getFinalizadoEm()) : null);
                    stmt.setTime(8, pedido.getEntregueEm() != null ? Time.valueOf(pedido.getEntregueEm()) : null);
                    stmt.executeUpdate();
                }
                
                // Inserir propostas do pedido
                insertPropostasPedido(conn, pedido);
                
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir pedido: " + e.getMessage(), e);
        }
    }
    
    private void insertPropostasPedido(Connection conn, Pedido pedido) throws SQLException {
        List<Proposta> propostas = pedido.getPropostas();
        for (int i = 0; i < propostas.size(); i++) {
            Proposta p = propostas.get(i);
            String tipo = (p instanceof Produto) ? "PRODUTO" : "MENU";
            
            String sql = "INSERT INTO Pedido_Proposta (idPedido, ordemProposta, nomeProposta, tipoProposta, precoUnitario) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, pedido.getIdPedido());
                stmt.setInt(2, i);
                stmt.setString(3, p.getNome());
                stmt.setString(4, tipo);
                stmt.setDouble(5, p.getPreco());
                stmt.executeUpdate();
            }
            
            // Se for produto, guardar ingredientes (para personalizações)
            if (p instanceof Produto) {
                insertIngredientesProposta(conn, pedido.getIdPedido(), i, (Produto) p);
            }
        }
    }
    
    private void insertIngredientesProposta(Connection conn, int idPedido, int ordem, Produto produto) throws SQLException {
        // Agrupar ingredientes por nome e contar quantidade
        Map<String, Integer> quantidadesPorIngrediente = new LinkedHashMap<>();
        for (Ingrediente ing : produto.getIngredientes()) {
            quantidadesPorIngrediente.merge(ing.getNome(), 1, Integer::sum);
        }
        
        String sql = "INSERT INTO Pedido_Proposta_Ingrediente (idPedido, ordemProposta, nomeIngrediente, quantidade) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (Map.Entry<String, Integer> entry : quantidadesPorIngrediente.entrySet()) {
                stmt.setInt(1, idPedido);
                stmt.setInt(2, ordem);
                stmt.setString(3, entry.getKey());
                stmt.setInt(4, entry.getValue());
                stmt.addBatch();
            }
            stmt.executeBatch();
        }
    }
    
    // ════════════════════════════════════════════════════════════════════════
    // Métodos auxiliares - UPDATE
    // ════════════════════════════════════════════════════════════════════════
    
    private void update(Pedido pedido) {
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            try {
                String sql = "UPDATE Pedido SET nota = ?, precoTotal = ?, estado = ?, eta = ?, finalizadoEm = ?, entregueEm = ? WHERE idPedido = ? AND idRestaurante = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, pedido.getNota());
                    stmt.setDouble(2, pedido.getPrecoTotal());
                    stmt.setString(3, pedido.getEstado().name());
                    stmt.setTime(4, pedido.getEta() != null ? Time.valueOf(pedido.getEta()) : null);
                    stmt.setTime(5, pedido.getFinalizadoEm() != null ? Time.valueOf(pedido.getFinalizadoEm()) : null);
                    stmt.setTime(6, pedido.getEntregueEm() != null ? Time.valueOf(pedido.getEntregueEm()) : null);
                    stmt.setInt(7, pedido.getIdPedido());
                    stmt.setInt(8, idRestaurante);
                    stmt.executeUpdate();
                }
                
                // Atualizar propostas (apagar e reinserir)
                String sqlDel = "DELETE FROM Pedido_Proposta WHERE idPedido = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sqlDel)) {
                    stmt.setInt(1, pedido.getIdPedido());
                    stmt.executeUpdate();
                }
                insertPropostasPedido(conn, pedido);
                
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar pedido: " + e.getMessage(), e);
        }
    }
    
    // ════════════════════════════════════════════════════════════════════════
    // Métodos auxiliares - GET (reconstrução de objetos)
    // ════════════════════════════════════════════════════════════════════════
    
    private Pedido pedidoFromResultSet(Connection conn, ResultSet rs) throws SQLException {
        int idPedido = rs.getInt("idPedido");
        String nota = rs.getString("nota");
        double precoTotal = rs.getDouble("precoTotal");
        EstadoPedido estado = EstadoPedido.valueOf(rs.getString("estado"));
        Time etaTime = rs.getTime("eta");
        Time finalizadoTime = rs.getTime("finalizadoEm");
        Time entregueTime = rs.getTime("entregueEm");
        
        LocalTime eta = etaTime != null ? etaTime.toLocalTime() : null;
        LocalTime finalizadoEm = finalizadoTime != null ? finalizadoTime.toLocalTime() : null;
        LocalTime entregueEm = entregueTime != null ? entregueTime.toLocalTime() : null;
        
        // Obter propostas do pedido
        List<Proposta> propostas = getPropostasPedido(conn, idPedido);
        
        // Criar pedido com construtor que aceita ID
        Pedido pedido = new Pedido(idPedido, propostas, nota, estado);
        pedido.setPrecoTotal(precoTotal);
        pedido.setEta(eta);
        pedido.setFinalizadoEm(finalizadoEm);
        pedido.setEntregueEm(entregueEm);
        
        return pedido;
    }
    
    private List<Proposta> getPropostasPedido(Connection conn, int idPedido) throws SQLException {
        List<Proposta> propostas = new ArrayList<>();
        String sql = "SELECT ordemProposta, nomeProposta, tipoProposta, precoUnitario FROM Pedido_Proposta WHERE idPedido = ? ORDER BY ordemProposta";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idPedido);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int ordem = rs.getInt("ordemProposta");
                    String nome = rs.getString("nomeProposta");
                    String tipo = rs.getString("tipoProposta");
                    double preco = rs.getDouble("precoUnitario");
                    
                    if ("PRODUTO".equals(tipo)) {
                        // Reconstruir produto com ingredientes do pedido
                        List<Ingrediente> ingredientes = getIngredientesProposta(conn, idPedido, ordem);
                        propostas.add(new Produto(nome, ingredientes, preco, new ArrayList<>()));
                    } else {
                        // Para menus, obter o menu original com os seus produtos
                        Proposta menuOriginal = propostaDAO.get(nome);
                        if (menuOriginal instanceof Menu) {
                            // Usar o menu original que tem os produtos carregados
                            Menu menuComProdutos = (Menu) menuOriginal;
                            // Criar cópia com o preço do pedido (pode ter sido diferente)
                            propostas.add(new Menu(nome, menuComProdutos.getProdutos(), preco));
                        } else {
                            // Fallback: menu não encontrado, criar vazio
                            propostas.add(new Menu(nome, new ArrayList<>(), preco));
                        }
                    }
                }
            }
        }
        return propostas;
    }
    
    private List<Ingrediente> getIngredientesProposta(Connection conn, int idPedido, int ordem) throws SQLException {
        List<Ingrediente> list = new ArrayList<>();
        String sql = "SELECT nomeIngrediente, quantidade FROM Pedido_Proposta_Ingrediente WHERE idPedido = ? AND ordemProposta = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idPedido);
            stmt.setInt(2, ordem);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String nomeIng = rs.getString("nomeIngrediente");
                    int quantidade = rs.getInt("quantidade");
                    Ingrediente ing = ingredienteDAO.get(nomeIng);
                    if (ing != null) {
                        // Adicionar o ingrediente tantas vezes quanto a quantidade
                        for (int i = 0; i < quantidade; i++) {
                            list.add(ing);
                        }
                    }
                }
            }
        }
        return list;
    }
}
