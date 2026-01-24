package org;

import java.util.Scanner;
import org.CadeiaLN.CadeiaFacade;
import org.CadeiaLN.DatabaseSeeder;
import org.CadeiaUI.MenuInicial;
import org.CadeiaDL.ConnectionManager;

/**
 * Classe principal - ponto de entrada da aplicação
 */
public class App {
    public static void main(String[] args) {
        // Inicializar a base de dados (criar tabelas se não existirem)
        try {
            System.out.println("A inicializar base de dados...");
            ConnectionManager.getInstance().initializeSchema();
            System.out.println("Base de dados inicializada com sucesso!");
        } catch (Exception e) {
            System.err.println("Erro ao inicializar base de dados: " + e.getMessage());
            System.err.println("   Verifica se o MySQL está a correr e se a BD 'bumguerkings' existe.");
            System.err.println("   Executa: CREATE DATABASE bumguerkings;");
            return;
        }

        // Inicializa o scanner e a facade
        Scanner scanner = new Scanner(System.in);
        CadeiaFacade facade = new CadeiaFacade();

        // Popula a base de dados com dados de teste
        DatabaseSeeder.seedDatabase(facade);

        // Cria e mostra o menu inicial
        MenuInicial menuInicial = new MenuInicial(scanner, facade);
        menuInicial.mostrar();

        // Fecha o scanner ao sair
        scanner.close();
    }
}
