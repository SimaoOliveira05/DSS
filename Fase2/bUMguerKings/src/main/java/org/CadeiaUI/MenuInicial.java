package org.CadeiaUI;

import java.util.Scanner;
import org.CadeiaLN.CadeiaFacade;

/**
 * Menu principal do sistema - ponto de entrada
 */
public class MenuInicial extends Menu {
    public MenuInicial(Scanner scanner, CadeiaFacade facade) {
        super(scanner, "BEM-VINDO AO BUMGUERKINGS", facade);
    }
    
    @Override
    public void mostrar() {
        mostrarBoasVindas();
        
        while (true) {
            imprimirCabecalho();
            System.out.println("\n🏢 Restaurante atual: " + getFacade().getRestauranteAtualNome());
            System.out.println("\n👥 Selecione o tipo de utilizador:");
            System.out.println("─".repeat(40));
            System.out.println("1. 🍔 Cliente");
            System.out.println("2. 👨‍🍳 Funcionário");
            System.out.println("3. 🚚 Entregador");
            System.out.println("4. 🏢 Selecionar Restaurante");
            System.out.println("5. 🧭 COO");
            System.out.println("0. 🚪 Sair");

            int opcao = lerOpcao();
            cleanTerminal();

            switch (opcao) {
                case 1:
                    abrirMenuCliente();
                    break;
                case 2:
                    abrirMenuFuncionario();
                    break;
                case 3:
                    abrirMenuEntregador();
                    break;
                case 4:
                    selecionarRestaurante();
                    break;
                case 5:
                    abrirMenuCOO();
                    break;
                case 0:
                    despedir();
                    return;
                default:
                    System.out.println("\n❌ Opção inválida!");
                    pausar();
            }
        }
    }
    
    // ══════════════════════════════════════════════════════════
    // Navegação para Submenus
    // ══════════════════════════════════════════════════════════
    
    private void abrirMenuCliente() {
        MenuCliente menuCliente = new MenuCliente(getScanner(), getFacade());
        menuCliente.mostrar();
    }
    
    private void abrirMenuFuncionario() {
        MenuFuncionario menuFuncionario = new MenuFuncionario(getScanner(), getFacade());
        menuFuncionario.mostrar();
    }
    
    private void abrirMenuEntregador() {
        MenuEntregador menuEntregador = new MenuEntregador(getScanner(), getFacade());
        menuEntregador.mostrar();
    }

    private void abrirMenuCOO() {
        MenuCOO menuCOO = new MenuCOO(getScanner(), getFacade());
        menuCOO.mostrar();
    }

    private void selecionarRestaurante() {
        var restaurantes = getFacade().listarRestaurantes();
        if (restaurantes == null || restaurantes.isEmpty()) {
            System.out.println("\n❌ Nenhum restaurante disponível.");
            pausar();
            return;
        }

        System.out.println("\n📋 Restaurantes disponíveis:");
        System.out.println("─".repeat(40));
        restaurantes.forEach(r -> System.out.println("#" + r.getIdRestaurante() + " - " + r.getNome() + " (" + r.getLocalizacao() + ")"));

        int id = lerOpcao();
        if (getFacade().selecionarRestaurante(id)) {
            System.out.println("\n✅ Restaurante selecionado: " + getFacade().getRestauranteAtualNome());
        } else {
            System.out.println("\n❌ ID inválido!");
        }
        pausar();
    }
    
    // ══════════════════════════════════════════════════════════
    // Mensagens de Interface
    // ══════════════════════════════════════════════════════════
    
    private void mostrarBoasVindas() {
        System.out.println("\n" + "═".repeat(50));
        System.out.println("🍔".repeat(25));
        System.out.println("═".repeat(50));
        System.out.println();
        System.out.println("       ██████╗ ██╗   ██╗███╗   ███╗");
        System.out.println("       ██╔══██╗██║   ██║████╗ ████║");
        System.out.println("       ██████╔╝██║   ██║██╔████╔██║");
        System.out.println("       ██╔══██╗██║   ██║██║╚██╔╝██║");
        System.out.println("       ██████╔╝╚██████╔╝██║ ╚═╝ ██║");
        System.out.println("       ╚═════╝  ╚═════╝ ╚═╝     ╚═╝");
        System.out.println();
        System.out.println("    ██████╗ ██╗   ██╗███████╗██████╗ ");
        System.out.println("   ██╔════╝ ██║   ██║██╔════╝██╔══██╗");
        System.out.println("   ██║  ███╗██║   ██║█████╗  ██████╔╝");
        System.out.println("   ██║   ██║██║   ██║██╔══╝  ██╔══██╗");
        System.out.println("   ╚██████╔╝╚██████╔╝███████╗██║  ██║");
        System.out.println("    ╚═════╝  ╚═════╝ ╚══════╝╚═╝  ╚═╝");
        System.out.println();
        System.out.println("         ██╗  ██╗██╗███╗   ██╗ ██████╗ ███████╗");
        System.out.println("         ██║ ██╔╝██║████╗  ██║██╔════╝ ██╔════╝");
        System.out.println("         █████╔╝ ██║██╔██╗ ██║██║  ███╗███████╗");
        System.out.println("         ██╔═██╗ ██║██║╚██╗██║██║   ██║╚════██║");
        System.out.println("         ██║  ██╗██║██║ ╚████║╚██████╔╝███████║");
        System.out.println("         ╚═╝  ╚═╝╚═╝╚═╝  ╚═══╝ ╚═════╝ ╚══════╝");
        System.out.println();
        System.out.println("═".repeat(50));
        System.out.println("     Sistema de Gestão de Pedidos v1.0");
        System.out.println("═".repeat(50));
        System.out.println("🍔".repeat(25));
        System.out.println("═".repeat(50));
        pausar();
    }
    
    private void despedir() {
        System.out.println("\n" + "═".repeat(50));
        System.out.println("✨ Obrigado por usar o BUMguerKings! ✨");
        System.out.println("👋 Até à próxima!");
        System.out.println("═".repeat(50));
    }
}

