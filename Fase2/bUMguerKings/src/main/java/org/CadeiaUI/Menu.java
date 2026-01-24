package org.CadeiaUI;

import java.util.Scanner;
import org.CadeiaLN.CadeiaFacade;


/**
 * Classe abstrata base para todos os menus do sistema
 * Fornece estrutura comum para navegação e interação
 */
public abstract class Menu {
    private Scanner scanner;
    private String titulo;
    private CadeiaFacade facade;

    public Menu(Scanner scanner, String titulo, CadeiaFacade facade) {
        this.scanner = scanner;
        this.titulo = titulo;
        this.facade = facade;
    }
    
    // ══════════════════════════════════════════════════════════
    // Métodos Abstratos
    // ══════════════════════════════════════════════════════════
    
    /**
     * Exibe o menu e processa as opções do usuário
     */
    public abstract void mostrar();
    
    // ══════════════════════════════════════════════════════════
    // Formatação de Interface
    // ══════════════════════════════════════════════════════════
    
    /**
     * Imprime o cabeçalho do menu
     */
    public void imprimirCabecalho() {
        System.out.println("\n╔════════════════════════════════════╗");
        System.out.println("║  " + centralizarTexto(titulo, 34) + "║");
        System.out.println("╚════════════════════════════════════╝");
    }

        /**
     * Limpa o terminal.
     */
    public void cleanTerminal() {
        try {
            if (System.getProperty("os.name").toLowerCase().contains("win")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                new ProcessBuilder("clear").inheritIO().start().waitFor();
            }
        } catch (Exception e) {
            System.out.println("Erro ao limpar o terminal: " + e.getMessage());
        }
    }

    
    /**
     * Centraliza um texto em um espaço de tamanho especificado
     */
    private String centralizarTexto(String texto, int tamanho) {
        if (texto.length() >= tamanho) {
            return texto.substring(0, tamanho);
        }
        int espacos = (tamanho - texto.length()) / 2;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < espacos; i++) {
            sb.append(" ");
        }
        sb.append(texto);
        while (sb.length() < tamanho) {
            sb.append(" ");
        }
        return sb.toString();
    }
    
    // ══════════════════════════════════════════════════════════
    // Entrada de Dados
    // ══════════════════════════════════════════════════════════
    
    /**
     * Lê uma opção numérica do usuário com prompt padrão
     */
    protected int lerOpcao() {
        return lerOpcao("👉 Escolha uma opção: ");
    }
    
    /**
     * Lê uma opção numérica do usuário com prompt customizado
     */
    protected int lerOpcao(String prompt) {
        try {
            System.out.print(prompt);
            return scanner.nextInt();
        } catch (Exception e) {
            scanner.nextLine(); // Limpar buffer
            return -1;
        }
    }
    
    /**
     * Lê uma string do usuário
     */
    protected String lerString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }
    
    /**
     * Confirma uma ação (s/n)
     */
    protected boolean confirmar(String mensagem) {
        System.out.print(mensagem + " (s/n): ");
        String resposta = scanner.nextLine().trim().toLowerCase();
        return resposta.equals("s") || resposta.equals("sim");
    }
    
    // ══════════════════════════════════════════════════════════
    // Controle de Fluxo
    // ══════════════════════════════════════════════════════════
    
    /**
     * Pausa para o usuário ler a mensagem
     */
    protected void pausar() {
        System.out.print("\n⏸️  Pressione ENTER para continuar...");
        try {
            // Se há algo no buffer, limpa
            if (scanner.hasNextLine()) {
                scanner.nextLine();
            }
        } catch (Exception e) {
            // Ignora
        }
    }
    
    /**
     * Exibe uma mensagem de sucesso
     */
    protected void mostrarSucesso(String mensagem) {
        System.out.println("\n✅ " + mensagem);
    }
    
    /**
     * Exibe uma mensagem de erro
     */
    protected void mostrarErro(String mensagem) {
        System.out.println("\n❌ " + mensagem);
    }
    
    /**
     * Exibe uma mensagem de aviso
     */
    protected void mostrarAviso(String mensagem) {
        System.out.println("\n⚠️  " + mensagem);
    }
    
    /**
     * Exibe uma mensagem informativa
     */
    protected void mostrarInfo(String mensagem) {
        System.out.println("\nℹ️  " + mensagem);
    }

    // ══════════════════════════════════════════════════════════
    // Getters
    // ══════════════════════════════════════════════════════════

    public Scanner getScanner() {
        return scanner;
    }

    public CadeiaFacade getFacade() {
        return facade;
    }


}
