package org.CadeiaDL;

import org.CadeiaLN.SubSistemaRestaurante.SubSistemaTarefas.*;

import java.sql.*;
import java.time.LocalTime;
import java.util.*;

/**
 * DAO para Tarefa que implementa Map<Integer, Tarefa>.
 * Cada instância está associada a um restaurante específico.
 */
public class TarefaDAO implements Map<Integer, Tarefa> {
    
    private final int idRestaurante;
    
    public TarefaDAO(int idRestaurante) {
        this.idRestaurante = idRestaurante;
    }
    
    private Connection getConnection() throws SQLException {
        return ConnectionManager.getInstance().getConnection();
    }
    
    // ════════════════════════════════════════════════════════════════════════
    // Implementação de Map<Integer, Tarefa>
    // ════════════════════════════════════════════════════════════════════════
    
    @Override
    public int size() {
        String sql = "SELECT COUNT(*) FROM Tarefa WHERE idRestaurante = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idRestaurante);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao contar tarefas: " + e.getMessage(), e);
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
        String sql = "SELECT 1 FROM Tarefa WHERE idTarefa = ? AND idRestaurante = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, (Integer) key);
            stmt.setInt(2, idRestaurante);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao verificar tarefa: " + e.getMessage(), e);
        }
    }
    
    @Override
    public boolean containsValue(Object value) {
        if (!(value instanceof Tarefa)) return false;
        return containsKey(((Tarefa) value).getIdTarefa());
    }
    
    @Override
    public Tarefa get(Object key) {
        if (!(key instanceof Integer)) return null;
        int idTarefa = (Integer) key;
        
        String sql = "SELECT idTarefa, idPedido, nomeProduto, descricao, tipoPosto, estado, tempoEstimado, scheduledStart, scheduledFinish, delayMinutes FROM Tarefa WHERE idTarefa = ? AND idRestaurante = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idTarefa);
            stmt.setInt(2, idRestaurante);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return tarefaFromResultSet(conn, rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao obter tarefa: " + e.getMessage(), e);
        }
        return null;
    }
    
    @Override
    public Tarefa put(Integer key, Tarefa tarefa) {
        Tarefa old = get(key);
        if (old == null) {
            insert(tarefa);
        } else {
            update(tarefa);
        }
        return old;
    }
    
    @Override
    public Tarefa remove(Object key) {
        if (!(key instanceof Integer)) return null;
        Tarefa old = get(key);
        if (old != null) {
            String sql = "DELETE FROM Tarefa WHERE idTarefa = ? AND idRestaurante = ?";
            try (Connection conn = getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, (Integer) key);
                stmt.setInt(2, idRestaurante);
                stmt.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException("Erro ao remover tarefa: " + e.getMessage(), e);
            }
        }
        return old;
    }
    
    @Override
    public void putAll(Map<? extends Integer, ? extends Tarefa> m) {
        for (Entry<? extends Integer, ? extends Tarefa> entry : m.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }
    
    @Override
    public void clear() {
        String sql = "DELETE FROM Tarefa WHERE idRestaurante = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idRestaurante);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao limpar tarefas: " + e.getMessage(), e);
        }
    }
    
    @Override
    public Set<Integer> keySet() {
        Set<Integer> keys = new HashSet<>();
        String sql = "SELECT idTarefa FROM Tarefa WHERE idRestaurante = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idRestaurante);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    keys.add(rs.getInt("idTarefa"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar IDs de tarefas: " + e.getMessage(), e);
        }
        return keys;
    }
    
    @Override
    public Collection<Tarefa> values() {
        List<Tarefa> list = new ArrayList<>();
        String sql = "SELECT idTarefa, idPedido, nomeProduto, descricao, tipoPosto, estado, tempoEstimado, scheduledStart, scheduledFinish, delayMinutes FROM Tarefa WHERE idRestaurante = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idRestaurante);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(tarefaFromResultSet(conn, rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar tarefas: " + e.getMessage(), e);
        }
        return list;
    }
    
    @Override
    public Set<Entry<Integer, Tarefa>> entrySet() {
        Set<Entry<Integer, Tarefa>> entries = new HashSet<>();
        for (Tarefa t : values()) {
            entries.add(new AbstractMap.SimpleEntry<>(t.getIdTarefa(), t));
        }
        return entries;
    }
    
    // ════════════════════════════════════════════════════════════════════════
    // Métodos auxiliares específicos para Tarefas
    // ════════════════════════════════════════════════════════════════════════
    
    /**
     * Obtém todas as tarefas de um pedido específico.
     */
    public List<Tarefa> getTarefasPorPedido(int idPedido) {
        List<Tarefa> list = new ArrayList<>();
        String sql = "SELECT idTarefa, idPedido, nomeProduto, descricao, tipoPosto, estado, tempoEstimado, scheduledStart, scheduledFinish, delayMinutes FROM Tarefa WHERE idPedido = ? AND idRestaurante = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idPedido);
            stmt.setInt(2, idRestaurante);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(tarefaFromResultSet(conn, rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar tarefas do pedido: " + e.getMessage(), e);
        }
        return list;
    }
    
    /**
     * Obtém todas as tarefas de um posto específico.
     */
    public List<Tarefa> getTarefasPorPosto(TipoPosto tipoPosto) {
        List<Tarefa> list = new ArrayList<>();
        String sql = "SELECT idTarefa, idPedido, nomeProduto, descricao, tipoPosto, estado, tempoEstimado, scheduledStart, scheduledFinish, delayMinutes FROM Tarefa WHERE tipoPosto = ? AND idRestaurante = ? AND estado != 'CONCLUIDA'";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, tipoPosto.name());
            stmt.setInt(2, idRestaurante);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(tarefaFromResultSet(conn, rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar tarefas do posto: " + e.getMessage(), e);
        }
        return list;
    }
    
    // ════════════════════════════════════════════════════════════════════════
    // Métodos auxiliares - INSERT/UPDATE
    // ════════════════════════════════════════════════════════════════════════
    
    private void insert(Tarefa tarefa) {
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            try {
                String sql = "INSERT INTO Tarefa (idRestaurante, idPedido, nomeProduto, descricao, tipoPosto, estado, tempoEstimado, scheduledStart, scheduledFinish, delayMinutes) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                    stmt.setInt(1, idRestaurante);
                    stmt.setInt(2, tarefa.getIdPedido());
                    stmt.setString(3, tarefa.getNomeProduto());
                    stmt.setString(4, tarefa.getDescricao());
                    stmt.setString(5, tarefa.getTipoPosto().name());
                    stmt.setString(6, tarefa.getEstado().name());
                    stmt.setInt(7, tarefa.getTempoEstimado());
                    stmt.setTime(8, tarefa.getScheduledStart() != null ? Time.valueOf(tarefa.getScheduledStart()) : null);
                    stmt.setTime(9, tarefa.getScheduledFinish() != null ? Time.valueOf(tarefa.getScheduledFinish()) : null);
                    stmt.setInt(10, tarefa.getDelayMinutes());
                    stmt.executeUpdate();
                    // Se quiseres obter o id gerado:
                    try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            int generatedId = generatedKeys.getInt(1);
                            tarefa.setIdTarefa(generatedId); // Se o modelo Tarefa permitir
                        }
                    }
                }
                // Inserir instruções
                insertInstrucoes(conn, tarefa);
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir tarefa: " + e.getMessage(), e);
        }
    }
    
    private void update(Tarefa tarefa) {
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            try {
                String sql = "UPDATE Tarefa SET nomeProduto = ?, descricao = ?, tipoPosto = ?, estado = ?, tempoEstimado = ?, scheduledStart = ?, scheduledFinish = ?, delayMinutes = ? WHERE idTarefa = ? AND idRestaurante = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, tarefa.getNomeProduto());
                    stmt.setString(2, tarefa.getDescricao());
                    stmt.setString(3, tarefa.getTipoPosto().name());
                    stmt.setString(4, tarefa.getEstado().name());
                    stmt.setInt(5, tarefa.getTempoEstimado());
                    stmt.setTime(6, tarefa.getScheduledStart() != null ? Time.valueOf(tarefa.getScheduledStart()) : null);
                    stmt.setTime(7, tarefa.getScheduledFinish() != null ? Time.valueOf(tarefa.getScheduledFinish()) : null);
                    stmt.setInt(8, tarefa.getDelayMinutes());
                    stmt.setInt(9, tarefa.getIdTarefa());
                    stmt.setInt(10, idRestaurante);
                    stmt.executeUpdate();
                }
                
                // Atualizar instruções (apagar e reinserir)
                deleteInstrucoes(conn, tarefa.getIdTarefa());
                insertInstrucoes(conn, tarefa);
                
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar tarefa: " + e.getMessage(), e);
        }
    }
    
    private void insertInstrucoes(Connection conn, Tarefa tarefa) throws SQLException {
        List<String> instrucoes = tarefa.getInstrucoes();
        if (instrucoes == null || instrucoes.isEmpty()) return;
        
        String sql = "INSERT INTO Tarefa_Instrucao (idTarefa, ordemInstrucao, instrucao) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (int i = 0; i < instrucoes.size(); i++) {
                stmt.setInt(1, tarefa.getIdTarefa());
                stmt.setInt(2, i);
                stmt.setString(3, instrucoes.get(i));
                stmt.addBatch();
            }
            stmt.executeBatch();
        }
    }
    
    private void deleteInstrucoes(Connection conn, int idTarefa) throws SQLException {
        String sql = "DELETE FROM Tarefa_Instrucao WHERE idTarefa = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idTarefa);
            stmt.executeUpdate();
        }
    }
    
    private List<String> getInstrucoes(Connection conn, int idTarefa) throws SQLException {
        List<String> instrucoes = new ArrayList<>();
        String sql = "SELECT instrucao FROM Tarefa_Instrucao WHERE idTarefa = ? ORDER BY ordemInstrucao";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idTarefa);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    instrucoes.add(rs.getString("instrucao"));
                }
            }
        }
        return instrucoes;
    }
    
    // ════════════════════════════════════════════════════════════════════════
    // Métodos auxiliares - GET (reconstrução de objetos)
    // ════════════════════════════════════════════════════════════════════════
    
    private Tarefa tarefaFromResultSet(Connection conn, ResultSet rs) throws SQLException {
        int idTarefa = rs.getInt("idTarefa");
        int idPedido = rs.getInt("idPedido");
        String nomeProduto = rs.getString("nomeProduto");
        String descricao = rs.getString("descricao");
        TipoPosto tipoPosto = TipoPosto.valueOf(rs.getString("tipoPosto"));
        EstadoTarefa estado = EstadoTarefa.valueOf(rs.getString("estado"));
        int tempoEstimado = rs.getInt("tempoEstimado");
        Time startTime = rs.getTime("scheduledStart");
        int delayMinutes = rs.getInt("delayMinutes");
        
        // Se não há scheduledStart na BD, usar hora atual como fallback
        LocalTime scheduledStart = startTime != null ? startTime.toLocalTime() : LocalTime.now();
        
        // Obter instruções
        List<String> instrucoes = getInstrucoes(conn, idTarefa);
        
        // Criar tarefa com construtor que aceita ID
        Tarefa tarefa = new Tarefa(idTarefa, idPedido, nomeProduto, descricao, tipoPosto, estado, tempoEstimado);
        for (String instrucao : instrucoes) {
            tarefa.adicionarInstrucao(instrucao);
        }
        
        // Restaurar delay ANTES de reschedule para que o delay seja considerado
        tarefa.setDelayMinutes(delayMinutes);
        
        // Agora reschedule com o delay já definido
        tarefa.reschedule(scheduledStart);
        
        return tarefa;
    }
}
