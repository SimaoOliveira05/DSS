package org.CadeiaDL;

import org.CadeiaLN.SubSistemaRestaurante.SubSistemaPedidos.*;

import java.sql.*;
import java.util.*;

/**
 * DAO para Proposta (Produto/Menu) que implementa Map<String, Proposta>.
 * Usa estratégia de uma tabela por classe com discriminador de tipo.
 * Cada instância está associada a um restaurante específico.
 */
public class PropostaDAO implements Map<String, Proposta> {
    
    private final int idRestaurante;
    private final IngredienteDAO ingredienteDAO;
    
    public PropostaDAO(int idRestaurante) {
        this.idRestaurante = idRestaurante;
        this.ingredienteDAO = new IngredienteDAO(idRestaurante);
    }
    
    private Connection getConnection() throws SQLException {
        return ConnectionManager.getInstance().getConnection();
    }
    
    // ════════════════════════════════════════════════════════════════════════
    // Implementação de Map<String, Proposta>
    // ════════════════════════════════════════════════════════════════════════
    
    @Override
    public int size() {
        String sql = "SELECT COUNT(*) FROM Proposta WHERE idRestaurante = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idRestaurante);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao contar propostas: " + e.getMessage(), e);
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
        String sql = "SELECT 1 FROM Proposta WHERE nome = ? AND idRestaurante = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, (String) key);
            stmt.setInt(2, idRestaurante);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao verificar proposta: " + e.getMessage(), e);
        }
    }
    
    @Override
    public boolean containsValue(Object value) {
        if (!(value instanceof Proposta)) return false;
        return containsKey(((Proposta) value).getNome());
    }
    
    @Override
    public Proposta get(Object key) {
        if (!(key instanceof String)) return null;
        String nome = (String) key;
        
        // Primeiro obter tipo da proposta
        String sqlTipo = "SELECT tipo, preco FROM Proposta WHERE nome = ? AND idRestaurante = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sqlTipo)) {
            stmt.setString(1, nome);
            stmt.setInt(2, idRestaurante);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String tipo = rs.getString("tipo");
                    double preco = rs.getDouble("preco");
                    
                    if ("PRODUTO".equals(tipo)) {
                        return getProduto(conn, nome, preco);
                    } else if ("MENU".equals(tipo)) {
                        return getMenu(conn, nome, preco);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao obter proposta: " + e.getMessage(), e);
        }
        return null;
    }
    
    @Override
    public Proposta put(String key, Proposta proposta) {
        Proposta old = get(key);
        if (old == null) {
            insert(proposta);
        } else {
            update(proposta);
        }
        return old;
    }
    
    @Override
    public Proposta remove(Object key) {
        if (!(key instanceof String)) return null;
        Proposta old = get(key);
        if (old != null) {
            // CASCADE irá apagar das tabelas filhas
            String sql = "DELETE FROM Proposta WHERE nome = ? AND idRestaurante = ?";
            try (Connection conn = getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, (String) key);
                stmt.setInt(2, idRestaurante);
                stmt.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException("Erro ao remover proposta: " + e.getMessage(), e);
            }
        }
        return old;
    }
    
    @Override
    public void putAll(Map<? extends String, ? extends Proposta> m) {
        for (Entry<? extends String, ? extends Proposta> entry : m.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }
    
    @Override
    public void clear() {
        String sql = "DELETE FROM Proposta WHERE idRestaurante = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idRestaurante);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao limpar propostas: " + e.getMessage(), e);
        }
    }
    
    @Override
    public Set<String> keySet() {
        Set<String> keys = new HashSet<>();
        String sql = "SELECT nome FROM Proposta WHERE idRestaurante = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idRestaurante);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    keys.add(rs.getString("nome"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar nomes de propostas: " + e.getMessage(), e);
        }
        return keys;
    }
    
    @Override
    public Collection<Proposta> values() {
        List<Proposta> list = new ArrayList<>();
        String sql = "SELECT nome, tipo, preco FROM Proposta WHERE idRestaurante = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idRestaurante);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String nome = rs.getString("nome");
                    String tipo = rs.getString("tipo");
                    double preco = rs.getDouble("preco");
                    
                    if ("PRODUTO".equals(tipo)) {
                        list.add(getProduto(conn, nome, preco));
                    } else if ("MENU".equals(tipo)) {
                        list.add(getMenu(conn, nome, preco));
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar propostas: " + e.getMessage(), e);
        }
        return list;
    }
    
    @Override
    public Set<Entry<String, Proposta>> entrySet() {
        Set<Entry<String, Proposta>> entries = new HashSet<>();
        for (Proposta p : values()) {
            entries.add(new AbstractMap.SimpleEntry<>(p.getNome(), p));
        }
        return entries;
    }
    
    // ════════════════════════════════════════════════════════════════════════
    // Métodos auxiliares - INSERT
    // ════════════════════════════════════════════════════════════════════════
    
    private void insert(Proposta proposta) {
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            try {
                // 1. Inserir na tabela Proposta (superclasse)
                String tipo = (proposta instanceof Produto) ? "PRODUTO" : "MENU";
                insertProposta(conn, proposta.getNome(), proposta.getPreco(), tipo);
                
                // 2. Inserir na tabela específica
                if (proposta instanceof Produto) {
                    insertProduto(conn, (Produto) proposta);
                } else if (proposta instanceof Menu) {
                    insertMenu(conn, (Menu) proposta);
                }
                
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir proposta: " + e.getMessage(), e);
        }
    }
    
    private void insertProposta(Connection conn, String nome, double preco, String tipo) throws SQLException {
        String sql = "INSERT INTO Proposta (nome, idRestaurante, preco, tipo) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nome);
            stmt.setInt(2, idRestaurante);
            stmt.setDouble(3, preco);
            stmt.setString(4, tipo);
            stmt.executeUpdate();
        }
    }
    
    private void insertProduto(Connection conn, Produto produto) throws SQLException {
        // Inserir na tabela Produto
        String sql = "INSERT INTO Produto (nome, idRestaurante) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, produto.getNome());
            stmt.setInt(2, idRestaurante);
            stmt.executeUpdate();
        }
        
        // Inserir todos os ingredientes (base + adicionáveis) numa única tabela
        insertProdutoIngredientes(conn, produto);
    }
    
    private void insertProdutoIngredientes(Connection conn, Produto produto) throws SQLException {
        String sql = "INSERT INTO Produto_Ingrediente (nomeProduto, idRestaurante, nomeIngrediente, quantidade, removivel, adicionavel) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            // Agrupar ingredientes base por nome e contar quantidade
            Map<String, Integer> quantidadesPorIngrediente = new LinkedHashMap<>();
            Map<String, Ingrediente> ingredientesPorNome = new HashMap<>();
            
            for (Ingrediente ing : produto.getIngredientes()) {
                String nome = ing.getNome();
                quantidadesPorIngrediente.merge(nome, 1, Integer::sum);
                ingredientesPorNome.put(nome, ing);
            }
            
            // Inserir ingredientes base com quantidade
            for (Map.Entry<String, Integer> entry : quantidadesPorIngrediente.entrySet()) {
                String nomeIng = entry.getKey();
                int quantidade = entry.getValue();
                Ingrediente ing = ingredientesPorNome.get(nomeIng);
                
                stmt.setString(1, produto.getNome());
                stmt.setInt(2, idRestaurante);
                stmt.setString(3, nomeIng);
                stmt.setInt(4, quantidade);
                stmt.setBoolean(5, ing.isRemovivel());
                stmt.setBoolean(6, false); // não é adicionável
                stmt.addBatch();
            }
            
            // Ingredientes adicionáveis (extras) - sem duplicados
            Set<String> adicionaveisInseridos = new HashSet<>();
            for (Ingrediente ing : produto.getIngredientesAdicionaveis()) {
                if (!adicionaveisInseridos.contains(ing.getNome())) {
                    stmt.setString(1, produto.getNome());
                    stmt.setInt(2, idRestaurante);
                    stmt.setString(3, ing.getNome());
                    stmt.setInt(4, 1);
                    stmt.setBoolean(5, false);
                    stmt.setBoolean(6, true); // é adicionável
                    stmt.addBatch();
                    adicionaveisInseridos.add(ing.getNome());
                }
            }
            stmt.executeBatch();
        }
    }
    
    private void insertMenu(Connection conn, Menu menu) throws SQLException {
        // Inserir na tabela Menu
        String sql = "INSERT INTO Menu (nome, idRestaurante) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, menu.getNome());
            stmt.setInt(2, idRestaurante);
            stmt.executeUpdate();
        }
        
        // Inserir produtos do menu
        insertMenuProdutos(conn, menu.getNome(), menu.getProdutos());
    }
    
    private void insertMenuProdutos(Connection conn, String nomeMenu, List<Produto> produtos) throws SQLException {
        if (produtos == null || produtos.isEmpty()) return;
        String sql = "INSERT INTO Menu_Produto (nomeMenu, idRestaurante, nomeProduto) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (Produto p : produtos) {
                stmt.setString(1, nomeMenu);
                stmt.setInt(2, idRestaurante);
                stmt.setString(3, p.getNome());
                stmt.addBatch();
            }
            stmt.executeBatch();
        }
    }
    
    // ════════════════════════════════════════════════════════════════════════
    // Métodos auxiliares - UPDATE
    // ════════════════════════════════════════════════════════════════════════
    
    private void update(Proposta proposta) {
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            try {
                // Atualizar preço na tabela Proposta
                String sql = "UPDATE Proposta SET preco = ? WHERE nome = ? AND idRestaurante = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setDouble(1, proposta.getPreco());
                    stmt.setString(2, proposta.getNome());
                    stmt.setInt(3, idRestaurante);
                    stmt.executeUpdate();
                }
                
                // Atualizar tabela específica
                if (proposta instanceof Produto) {
                    updateProduto(conn, (Produto) proposta);
                } else if (proposta instanceof Menu) {
                    updateMenu(conn, (Menu) proposta);
                }
                
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar proposta: " + e.getMessage(), e);
        }
    }
    
    private void updateProduto(Connection conn, Produto produto) throws SQLException {
        // Apagar ingredientes antigos e reinserir
        String sqlDel = "DELETE FROM Produto_Ingrediente WHERE nomeProduto = ? AND idRestaurante = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sqlDel)) {
            stmt.setString(1, produto.getNome());
            stmt.setInt(2, idRestaurante);
            stmt.executeUpdate();
        }
        
        // Reinserir todos os ingredientes com a nova estrutura
        insertProdutoIngredientes(conn, produto);
    }
    
    private void updateMenu(Connection conn, Menu menu) throws SQLException {
        // Apagar produtos antigos e reinserir
        String sqlDel = "DELETE FROM Menu_Produto WHERE nomeMenu = ? AND idRestaurante = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sqlDel)) {
            stmt.setString(1, menu.getNome());
            stmt.setInt(2, idRestaurante);
            stmt.executeUpdate();
        }
        
        insertMenuProdutos(conn, menu.getNome(), menu.getProdutos());
    }
    
    // ════════════════════════════════════════════════════════════════════════
    // Métodos auxiliares - GET (reconstrução de objetos)
    // ════════════════════════════════════════════════════════════════════════
    
    private Produto getProduto(Connection conn, String nome, double preco) throws SQLException {
        List<Ingrediente> ingredientes = getProdutoIngredientes(conn, nome);
        List<Ingrediente> adicionaveis = getProdutoIngredientesAdicionaveis(conn, nome);
        return new Produto(nome, ingredientes, preco, adicionaveis);
    }
    
    private List<Ingrediente> getProdutoIngredientes(Connection conn, String nomeProduto) throws SQLException {
        List<Ingrediente> list = new ArrayList<>();
        // Obter ingredientes base (adicionavel = FALSE) com o campo removivel e quantidade
        String sql = "SELECT nomeIngrediente, removivel, quantidade FROM Produto_Ingrediente WHERE nomeProduto = ? AND idRestaurante = ? AND adicionavel = FALSE";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nomeProduto);
            stmt.setInt(2, idRestaurante);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String nomeIng = rs.getString("nomeIngrediente");
                    boolean removivel = rs.getBoolean("removivel");
                    int quantidade = rs.getInt("quantidade");
                    Ingrediente ing = ingredienteDAO.get(nomeIng);
                    if (ing != null) {
                        // Adicionar N cópias do ingrediente conforme a quantidade
                        for (int i = 0; i < quantidade; i++) {
                            Ingrediente ingCopia = new Ingrediente(
                                ing.getNome(), 
                                ing.getAlergenios(),
                                ing.getTipoPosto(), 
                                ing.getTempoEstimado(), 
                                removivel
                            );
                            list.add(ingCopia);
                        }
                    }
                }
            }
        }
        return list;
    }
    
    private List<Ingrediente> getProdutoIngredientesAdicionaveis(Connection conn, String nomeProduto) throws SQLException {
        List<Ingrediente> list = new ArrayList<>();
        // Obter ingredientes adicionáveis (adicionavel = TRUE)
        String sql = "SELECT nomeIngrediente FROM Produto_Ingrediente WHERE nomeProduto = ? AND idRestaurante = ? AND adicionavel = TRUE";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nomeProduto);
            stmt.setInt(2, idRestaurante);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String nomeIng = rs.getString("nomeIngrediente");
                    Ingrediente ing = ingredienteDAO.get(nomeIng);
                    if (ing != null) list.add(ing);
                }
            }
        }
        return list;
    }
    
    private Menu getMenu(Connection conn, String nome, double preco) throws SQLException {
        List<Produto> produtos = getMenuProdutos(conn, nome);
        return new Menu(nome, produtos, preco);
    }
    
    private List<Produto> getMenuProdutos(Connection conn, String nomeMenu) throws SQLException {
        List<Produto> list = new ArrayList<>();
        String sql = "SELECT nomeProduto FROM Menu_Produto WHERE nomeMenu = ? AND idRestaurante = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nomeMenu);
            stmt.setInt(2, idRestaurante);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String nomeProduto = rs.getString("nomeProduto");
                    // Obter o produto (que já existe na BD)
                    Proposta p = get(nomeProduto);
                    if (p instanceof Produto) {
                        list.add((Produto) p);
                    }
                }
            }
        }
        return list;
    }
}
