package org.CadeiaUI;

import java.util.Scanner;
import org.CadeiaLN.CadeiaFacade;
import org.CadeiaLN.SubSistemaRestaurante.SubSistemaTarefas.TipoPosto;

/**
 * Menu de operações para funcionários
 */
public class MenuFuncionario extends Menu {
    private TipoPosto postoSelecionado = null;

    public MenuFuncionario(Scanner scanner, CadeiaFacade facade) {
        super(scanner, "MENU FUNCIONÁRIO", facade);
    }

    @Override
    public void mostrar() {
        selecionarPosto();
        
        while (true) {
            imprimirCabecalho();
            System.out.println("\n🏪 Posto: " + postoSelecionado.getDescricao());
            System.out.println("─".repeat(40));
            System.out.println("1. 📋 Ver Tarefas Pendentes");
            System.out.println("2. ✅ Concluir Tarefa");
            System.out.println("3. ⏱️  Adicionar Atraso");
            System.out.println("0. ⬅️  Voltar");

            int opcao = lerOpcao();
            cleanTerminal();
            switch (opcao) {
                case 1:
                    verTarefasPendentes();
                    break;
                case 2:
                    concluirTarefa();
                    break;
                case 3:
                    adicionarAtraso();
                    break;
                case 0:
                    return;
                default:
                    System.out.println("\n❌ Opção inválida!");
            }
        }
    }
    
    // ══════════════════════════════════════════════════════════
    // Seleção de Posto
    // ══════════════════════════════════════════════════════════

    private void selecionarPosto() {
        while (postoSelecionado == null) {
            imprimirCabecalho();
            System.out.println("\n🏪 Selecione o seu posto de trabalho:");
            System.out.println("─".repeat(40));
            
            TipoPosto[] postos = TipoPosto.values();
            for (int i = 0; i < postos.length; i++) {
                System.out.println((i + 1) + ". " + postos[i].getDescricao());
            }
            
            int opcao = lerOpcao();
            
            if (opcao > 0 && opcao <= postos.length) {
                postoSelecionado = postos[opcao - 1];
                System.out.println("\n✅ Posto selecionado: " + postoSelecionado.getDescricao());
                pausar();
            } else {
                System.out.println("\n❌ Opção inválida!");
                pausar();
            }
        }
    }
    
    // ══════════════════════════════════════════════════════════
    // Gestão de Tarefas
    // ══════════════════════════════════════════════════════════

    private void verTarefasPendentes() {
        imprimirSeparador();
        System.out.println("         📋 TAREFAS PENDENTES         ");
        imprimirSeparador();
        
        String tarefas = getFacade().listarTarefasDoPosto(postoSelecionado);
        System.out.println("\n" + tarefas);
        
        pausar();
    }
    
    private void concluirTarefa() {
        imprimirSeparador();
        System.out.println("         ✅ CONCLUIR TAREFA         ");
        imprimirSeparador();
        
        String tarefas = getFacade().listarTarefasDoPosto(postoSelecionado);
        System.out.println("\n" + tarefas);
        
        getScanner().nextLine(); // Limpar buffer
        System.out.print("\n👉 Digite o id da tarefa a concluir (ou ENTER para cancelar): ");
        String input = getScanner().nextLine().trim();
        
        if (input.isEmpty()) {
            System.out.println("\n❌ Operação cancelada.");
            pausar();
            return;
        }
        
        try {
            int idTarefa = Integer.parseInt(input);
            getFacade().marcarTarefaComoConcluidaNoPosto(postoSelecionado, idTarefa);
            System.out.println("\n✅ Tarefa concluída com sucesso!");
        } catch (NumberFormatException e) {
            System.out.println("\n❌ Número inválido!");
        }
        
        pausar();
    }

    private void adicionarAtraso() {
        imprimirSeparador();
        System.out.println("         ⏱️  ADICIONAR ATRASO         ");
        imprimirSeparador();
        
        String tarefas = getFacade().listarTarefasDoPosto(postoSelecionado);
        System.out.println("\n" + tarefas);
        
        getScanner().nextLine(); // Limpar buffer
        System.out.print("\n👉 ID da tarefa (ou ENTER para cancelar): ");
        String input = getScanner().nextLine().trim();
        
        if (input.isEmpty()) {
            System.out.println("\n❌ Operação cancelada.");
            pausar();
            return;
        }
        
        try {
            int idTarefa = Integer.parseInt(input);
            
            System.out.print("⏱️  Minutos de atraso a adicionar: ");
            String minutosStr = getScanner().nextLine().trim();
            int minutos = Integer.parseInt(minutosStr);
            
            if (minutos <= 0) {
                System.out.println("\n❌ O atraso deve ser positivo!");
            } else if (getFacade().adicionarDelayATarefa(idTarefa, minutos)) {
                System.out.println("\n✅ Atraso de " + minutos + " minutos adicionado!");
                System.out.println("🔄 Agendamento recalculado.");
            } else {
                System.out.println("\n❌ Erro ao adicionar atraso. Verifique o ID da tarefa.");
            }
        } catch (NumberFormatException e) {
            System.out.println("\n❌ Valor inválido!");
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

