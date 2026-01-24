package org.CadeiaDL;

import org.CadeiaLN.SubSistemaRestaurante.Restaurante;

import java.sql.*;
import java.util.*;

/**
 * DAO para Restaurante que implementa Map<Integer, Restaurante>.
 * Permite integração transparente com o código existente.
 */
public class RestauranteDAO implements Map<Integer, Restaurante> {
    
    private static RestauranteDAO instance;
    
    public static synchronized RestauranteDAO getInstance() {
        if (instance == null) {
            instance = new RestauranteDAO();
        }
        return instance;
    }
    
    private Connection getConnection() throws SQLException {
        return ConnectionManager.getInstance().getConnection();
    }
    
    // ════════════════════════════════════════════════════════════════════════
    // Implementação de Map<Integer, Restaurante>
    // ════════════════════════════════════════════════════════════════════════
    
    @Override
    public int size() {
        String sql = "SELECT COUNT(*) FROM Restaurante";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao contar restaurantes: " + e.getMessage(), e);
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
        String sql = "SELECT 1 FROM Restaurante WHERE idRestaurante = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, (Integer) key);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao verificar restaurante: " + e.getMessage(), e);
        }
    }
    
    @Override
    public boolean containsValue(Object value) {
        if (!(value instanceof Restaurante)) return false;
        Restaurante r = (Restaurante) value;
        return containsKey(r.getIdRestaurante());
    }
    
    @Override
    public Restaurante get(Object key) {
        if (!(key instanceof Integer)) return null;
        String sql = "SELECT idRestaurante, nome, localizacao, totalPedidos, faturacaoTotal FROM Restaurante WHERE idRestaurante = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, (Integer) key);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return restauranteFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao obter restaurante: " + e.getMessage(), e);
        }
        return null;
    }
    
    @Override
    public Restaurante put(Integer key, Restaurante restaurante) {
        Restaurante old = get(key);
        if (old == null) {
            insert(restaurante);
        } else {
            update(restaurante);
        }
        return old;
    }
    
    @Override
    public Restaurante remove(Object key) {
        if (!(key instanceof Integer)) return null;
        Restaurante old = get(key);
        if (old != null) {
            String sql = "DELETE FROM Restaurante WHERE idRestaurante = ?";
            try (Connection conn = getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, (Integer) key);
                stmt.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException("Erro ao remover restaurante: " + e.getMessage(), e);
            }
        }
        return old;
    }
    
    @Override
    public void putAll(Map<? extends Integer, ? extends Restaurante> m) {
        for (Entry<? extends Integer, ? extends Restaurante> entry : m.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }
    
    @Override
    public void clear() {
        String sql = "DELETE FROM Restaurante";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao limpar restaurantes: " + e.getMessage(), e);
        }
    }
    
    @Override
    public Set<Integer> keySet() {
        Set<Integer> keys = new HashSet<>();
        String sql = "SELECT idRestaurante FROM Restaurante";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                keys.add(rs.getInt("idRestaurante"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar IDs: " + e.getMessage(), e);
        }
        return keys;
    }
    
    @Override
    public Collection<Restaurante> values() {
        List<Restaurante> list = new ArrayList<>();
        String sql = "SELECT idRestaurante, nome, localizacao, totalPedidos, faturacaoTotal FROM Restaurante";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(restauranteFromResultSet(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar restaurantes: " + e.getMessage(), e);
        }
        return list;
    }
    
    @Override
    public Set<Entry<Integer, Restaurante>> entrySet() {
        Set<Entry<Integer, Restaurante>> entries = new HashSet<>();
        for (Restaurante r : values()) {
            entries.add(new AbstractMap.SimpleEntry<>(r.getIdRestaurante(), r));
        }
        return entries;
    }
    
    // ════════════════════════════════════════════════════════════════════════
    // Métodos auxiliares
    // ════════════════════════════════════════════════════════════════════════
    
    /**
     * Insere um novo restaurante e retorna o ID gerado.
     */
    public int insert(Restaurante restaurante) {
        String sql = "INSERT INTO Restaurante (nome, localizacao, totalPedidos, faturacaoTotal) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, restaurante.getNome());
            stmt.setString(2, restaurante.getLocalizacao());
            stmt.setInt(3, restaurante.getTotalPedidos());
            stmt.setDouble(4, restaurante.getFaturacaoTotal());
            stmt.executeUpdate();
            
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir restaurante: " + e.getMessage(), e);
        }
        return -1;
    }
    
    /**
     * Atualiza um restaurante existente.
     */
    public void update(Restaurante restaurante) {
        String sql = "UPDATE Restaurante SET nome = ?, localizacao = ?, totalPedidos = ?, faturacaoTotal = ? WHERE idRestaurante = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, restaurante.getNome());
            stmt.setString(2, restaurante.getLocalizacao());
            stmt.setInt(3, restaurante.getTotalPedidos());
            stmt.setDouble(4, restaurante.getFaturacaoTotal());
            stmt.setInt(5, restaurante.getIdRestaurante());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar restaurante: " + e.getMessage(), e);
        }
    }
    
    /**
     * Constrói um Restaurante a partir de um ResultSet.
     * Nota: Os gestores internos (GestorPedidos, GestorTarefas) são recriados vazios;
     * os dados de pedidos/tarefas vêm das suas próprias DAOs.
     */
    private Restaurante restauranteFromResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("idRestaurante");
        String nome = rs.getString("nome");
        String localizacao = rs.getString("localizacao");
        int totalPedidos = rs.getInt("totalPedidos");
        double faturacao = rs.getDouble("faturacaoTotal");
        
        // Criar restaurante com construtor que aceita ID (precisamos adicionar)
        Restaurante r = new Restaurante(id, nome, localizacao);
        // Restaurar indicadores
        for (int i = 0; i < totalPedidos; i++) r.incrementaTotalPedidos();
        r.adicionaFaturacao(faturacao);
        return r;
    }
}
