package org.CadeiaDL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Gestão centralizada de conexões à base de dados.
 * Configuração simples para MySQL/MariaDB.
 */
public class ConnectionManager {
    
    private static final String URL = "jdbc:mysql://localhost:3306/bumguerkings";
    private static final String USER = "root";
    private static final String PASSWORD = "root";
    
    private static ConnectionManager instance;
    
    private ConnectionManager() {
        // Singleton
    }
    
    public static synchronized ConnectionManager getInstance() {
        if (instance == null) {
            instance = new ConnectionManager();
        }
        return instance;
    }
    
    /**
     * Obtém uma nova conexão à base de dados.
     */
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
    
    /**
     * Inicializa o schema da base de dados a partir do ficheiro schema.sql
     */
    public void initializeSchema() throws SQLException {
        InputStream is = getClass().getClassLoader().getResourceAsStream("schema.sql");
        
        if (is == null) {
            throw new SQLException("Ficheiro schema.sql não encontrado no classpath!");
        }
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                // Ignorar comentários de linha
                if (!line.trim().startsWith("--")) {
                    sb.append(line).append("\n");
                }
            }
            
            // Executar cada statement separadamente
            String[] statements = sb.toString().split(";");
            int count = 0;
            for (String sql : statements) {
                sql = sql.trim();
                if (!sql.isEmpty()) {
                    try {
                        stmt.execute(sql);
                        count++;
                    } catch (SQLException e) {
                        System.err.println("⚠️ Erro ao executar SQL: " + e.getMessage());
                        System.err.println("   Statement: " + sql.substring(0, Math.min(50, sql.length())) + "...");
                    }
                }
            }
            System.out.println("📊 " + count + " statements SQL executados.");
            
        } catch (Exception e) {
            throw new SQLException("Erro ao inicializar schema: " + e.getMessage(), e);
        }
    }
}
