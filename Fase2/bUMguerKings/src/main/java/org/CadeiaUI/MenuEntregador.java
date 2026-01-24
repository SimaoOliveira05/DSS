package org.CadeiaUI;

import java.util.Scanner;
import org.CadeiaLN.CadeiaFacade;

/**
 * Menu de operações para entregadores
 */
public class MenuEntregador extends Menu {
    
    public MenuEntregador(Scanner scanner, CadeiaFacade facade) {
        super(scanner, "MENU ENTREGADOR", facade);
    }

    @Override
    public void mostrar() {
        while (true) {
            imprimirCabecalho();
            System.out.println("\n🚚 Bem-vindo, Entregador!");
            System.out.println("─".repeat(40));
            System.out.println("1. 📦 Ver Pedidos Prontos para Entrega");
            System.out.println("2. ✅ Marcar Pedido como Entregue");
            System.out.println("0. ⬅️  Voltar");

            int opcao = lerOpcao();
            cleanTerminal();

            switch (opcao) {
                case 1:
                    verPedidosProntos();
                    break;
                case 2:
                    entregarPedido();
                    break;
                case 0:
                    return;
                default:
                    mostrarErro("Opção inválida!");
                    pausar();
            }
        }
    }

    // ══════════════════════════════════════════════════════════
    // Gestão de Entregas
    // ══════════════════════════════════════════════════════════

    private void verPedidosProntos() {
        imprimirSeparador();
        System.out.println("      📦 PEDIDOS PRONTOS PARA ENTREGA      ");
        imprimirSeparador();
        
        String pedidos = getFacade().listarPedidosProntos();
        System.out.println("\n" + pedidos);
        
        pausar();
    }

    private void entregarPedido() {
        imprimirSeparador();
        System.out.println("         ✅ MARCAR COMO ENTREGUE         ");
        imprimirSeparador();
        
        // Mostrar pedidos prontos
        String pedidos = getFacade().listarPedidosProntos();
        System.out.println("\n" + pedidos);
        
        // Pedir ID do pedido
        getScanner().nextLine(); // Limpar buffer
        System.out.print("\n👉 Digite o ID do pedido a entregar (ou ENTER para cancelar): ");
        String input = getScanner().nextLine().trim();
        
        if (input.isEmpty()) {
            mostrarAviso("Operação cancelada.");
            pausar();
            return;
        }
        
        try {
            int idPedido = Integer.parseInt(input);
            getFacade().marcarPedidoComoEntregue(idPedido);
            mostrarSucesso("✅ Pedido #" + idPedido + " marcado como entregue!");
        } catch (NumberFormatException e) {
            mostrarErro("❌ ID inválido! Digite apenas números.");
        }
        
        pausar();
    }

    // ══════════════════════════════════════════════════════════
    // Métodos Auxiliares
    // ══════════════════════════════════════════════════════════
    
    private void imprimirSeparador() {
        System.out.println("\n" + "═".repeat(40));
    }
}
