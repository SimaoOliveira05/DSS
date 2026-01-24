package org.CadeiaDL;

import org.CadeiaLN.SubSistemaRestaurante.SubSistemaPedidos.Ingrediente;
import org.CadeiaLN.SubSistemaRestaurante.SubSistemaTarefas.TipoPosto;

import java.sql.*;
import java.util.*;

/**
 * DAO para Ingrediente que implementa Map<String, Ingrediente>.
 * Cada instância está associada a um restaurante específico.
 */
public class IngredienteDAO implements Map<String, Ingrediente> {
    
    private final int idRestaurante;
    
    public IngredienteDAO(int idRestaurante) {
        this.idRestaurante = idRestaurante;
    }
    
    private Connection getConnection() throws SQLException {
        return ConnectionManager.getInstance().getConnection();
    }
    
    // ════════════════════════════════════════════════════════════════════════
    // Implementação de Map<String, Ingrediente>
    // ════════════════════════════════════════════════════════════════════════
    
    @Override
    public int size() {
        String sql = "SELECT COUNT(*) FROM Ingrediente WHERE idRestaurante = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idRestaurante);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao contar ingredientes: " + e.getMessage(), e);
        }
        return 0;
    }
    
    @Override
    public boolean isEmpty() {
        return size() == 0;
    }
    
    @Override
    public boolean containsKey(Object key) {
        if (!(key instanceof String)) return false;
        String sql = "SELECT 1 FROM Ingrediente WHERE nome = ? AND idRestaurante = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, (String) key);
            stmt.setInt(2, idRestaurante);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao verificar ingrediente: " + e.getMessage(), e);
        }
    }
    
    @Override
    public boolean containsValue(Object value) {
        if (!(value instanceof Ingrediente)) return false;
        return containsKey(((Ingrediente) value).getNome());
    }
    
    @Override
    public Ingrediente get(Object key) {
        if (!(key instanceof String)) return null;
        String sql = "SELECT nome, tipoPosto, tempoEstimado, removivel FROM Ingrediente WHERE nome = ? AND idRestaurante = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, (String) key);
            stmt.setInt(2, idRestaurante);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return ingredienteFromResultSet(conn, rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao obter ingrediente: " + e.getMessage(), e);
        }
        return null;
    }
    
    @Override
    public Ingrediente put(String key, Ingrediente ingrediente) {
        Ingrediente old = get(key);
        if (old == null) {
            insert(ingrediente);
        } else {
            update(ingrediente);
        }
        return old;
    }
    
    @Override
    public Ingrediente remove(Object key) {
        if (!(key instanceof String)) return null;
        Ingrediente old = get(key);
        if (old != null) {
            String sql = "DELETE FROM Ingrediente WHERE nome = ? AND idRestaurante = ?";
            try (Connection conn = getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, (String) key);
                stmt.setInt(2, idRestaurante);
                stmt.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException("Erro ao remover ingrediente: " + e.getMessage(), e);
            }
        }
        return old;
    }
    
    @Override
    public void putAll(Map<? extends String, ? extends Ingrediente> m) {
        for (Entry<? extends String, ? extends Ingrediente> entry : m.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }
    
    @Override
    public void clear() {
        String sql = "DELETE FROM Ingrediente WHERE idRestaurante = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idRestaurante);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao limpar ingredientes: " + e.getMessage(), e);
        }
    }
    
    @Override
    public Set<String> keySet() {
        Set<String> keys = new HashSet<>();
        String sql = "SELECT nome FROM Ingrediente WHERE idRestaurante = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idRestaurante);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    keys.add(rs.getString("nome"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar nomes de ingredientes: " + e.getMessage(), e);
        }
        return keys;
    }
    
    @Override
    public Collection<Ingrediente> values() {
        List<Ingrediente> list = new ArrayList<>();
        String sql = "SELECT nome, tipoPosto, tempoEstimado, removivel FROM Ingrediente WHERE idRestaurante = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idRestaurante);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(ingredienteFromResultSet(conn, rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar ingredientes: " + e.getMessage(), e);
        }
        return list;
    }
    
    @Override
    public Set<Entry<String, Ingrediente>> entrySet() {
        Set<Entry<String, Ingrediente>> entries = new HashSet<>();
        for (Ingrediente ing : values()) {
            entries.add(new AbstractMap.SimpleEntry<>(ing.getNome(), ing));
        }
        return entries;
    }
    
    // ════════════════════════════════════════════════════════════════════════
    // Métodos auxiliares
    // ════════════════════════════════════════════════════════════════════════
    
    private void insert(Ingrediente ingrediente) {
        String sql = "INSERT INTO Ingrediente (nome, idRestaurante, tipoPosto, tempoEstimado, removivel) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, ingrediente.getNome());
            stmt.setInt(2, idRestaurante);
            stmt.setString(3, ingrediente.getTipoPosto().name());
            stmt.setInt(4, ingrediente.getTempoEstimado());
            stmt.setBoolean(5, ingrediente.isRemovivel());
            stmt.executeUpdate();
            
            // Inserir alergénios
            insertAlergenios(conn, ingrediente);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir ingrediente: " + e.getMessage(), e);
        }
    }
    
    private void update(Ingrediente ingrediente) {
        String sql = "UPDATE Ingrediente SET tipoPosto = ?, tempoEstimado = ?, removivel = ? WHERE nome = ? AND idRestaurante = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, ingrediente.getTipoPosto().name());
            stmt.setInt(2, ingrediente.getTempoEstimado());
            stmt.setBoolean(3, ingrediente.isRemovivel());
            stmt.setString(4, ingrediente.getNome());
            stmt.setInt(5, idRestaurante);
            stmt.executeUpdate();
            
            // Atualizar alergénios (apagar e reinserir)
            deleteAlergenios(conn, ingrediente.getNome());
            insertAlergenios(conn, ingrediente);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar ingrediente: " + e.getMessage(), e);
        }
    }
    
    private void insertAlergenios(Connection conn, Ingrediente ingrediente) throws SQLException {
        if (ingrediente.getAlergenios() == null || ingrediente.getAlergenios().isEmpty()) return;
        String sql = "INSERT INTO Ingrediente_Alergenio (nomeIngrediente, idRestaurante, alergenio) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (String alergenio : ingrediente.getAlergenios()) {
                stmt.setString(1, ingrediente.getNome());
                stmt.setInt(2, idRestaurante);
                stmt.setString(3, alergenio);
                stmt.addBatch();
            }
            stmt.executeBatch();
        }
    }
    
    private void deleteAlergenios(Connection conn, String nomeIngrediente) throws SQLException {
        String sql = "DELETE FROM Ingrediente_Alergenio WHERE nomeIngrediente = ? AND idRestaurante = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nomeIngrediente);
            stmt.setInt(2, idRestaurante);
            stmt.executeUpdate();
        }
    }
    
    private List<String> getAlergenios(Connection conn, String nomeIngrediente) throws SQLException {
        List<String> alergenios = new ArrayList<>();
        String sql = "SELECT alergenio FROM Ingrediente_Alergenio WHERE nomeIngrediente = ? AND idRestaurante = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nomeIngrediente);
            stmt.setInt(2, idRestaurante);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    alergenios.add(rs.getString("alergenio"));
                }
            }
        }
        return alergenios;
    }
    
    private Ingrediente ingredienteFromResultSet(Connection conn, ResultSet rs) throws SQLException {
        String nome = rs.getString("nome");
        TipoPosto tipoPosto = TipoPosto.valueOf(rs.getString("tipoPosto"));
        int tempoEstimado = rs.getInt("tempoEstimado");
        boolean removivel = rs.getBoolean("removivel");
        List<String> alergenios = getAlergenios(conn, nome);
        
        return new Ingrediente(nome, alergenios, tipoPosto, tempoEstimado, removivel);
    }
}
