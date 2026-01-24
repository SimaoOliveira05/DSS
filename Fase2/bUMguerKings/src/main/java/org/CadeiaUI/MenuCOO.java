package org.CadeiaUI;

import java.util.List;
import java.util.Scanner;
import org.CadeiaLN.CadeiaFacade;
import org.CadeiaLN.SubSistemaRestaurante.Restaurante;

/**
 * Menu para COO: ver indicadores por restaurante ou agregados.
 */
public class MenuCOO extends Menu {
    public MenuCOO(Scanner scanner, CadeiaFacade facade) {
        super(scanner, "MENU COO", facade);
    }

    @Override
    public void mostrar() {
        while (true) {
            imprimirCabecalho();
            System.out.println("\n📊 Indicadores - Escolha uma opção:");
            System.out.println("─".repeat(40));
            System.out.println("1. ⏱️  Tempo médio de atendimento - TODOS os restaurantes");
            System.out.println("2. ⏱️  Tempo médio de atendimento - Por restaurante");
            System.out.println("3. 💰 Faturação total - TODOS os restaurantes");
            System.out.println("4. 💰 Faturação total - Por restaurante");
            System.out.println("0. ⬅️  Voltar");

            int opcao = lerOpcao();
            cleanTerminal();
            switch (opcao) {
                case 1:
                    verTempoMedioTodos();
                    break;
                case 2:
                    verTempoMedioPorRestaurante();
                    break;
                case 3:
                    verFaturacaoTodos();
                    break;
                case 4:
                    verFaturacaoPorRestaurante();
                    break;
                case 0:
                    return;
                default:
                    System.out.println("\n❌ Opção inválida!");
                    pausar();
            }
        }
    }

    private void verTempoMedioTodos() {
        imprimirSeparador();
        System.out.println("   ⏱️  TEMPO MÉDIO DE ATENDIMENTO (CADEIA)   ");
        imprimirSeparador();
        double media = getFacade().tempoMedioAtendimentoTodos();
        System.out.printf("\nTempo médio: %.2f minutos\n", media);
        pausar();
    }

    private void verTempoMedioPorRestaurante() {
        List<Restaurante> restaurantes = getFacade().listarRestaurantes();
        if (restaurantes == null || restaurantes.isEmpty()) {
            System.out.println("\n❌ Nenhum restaurante disponível.");
            pausar();
            return;
        }
        System.out.println("\n📋 Restaurantes:");
        System.out.println("─".repeat(40));
        restaurantes.forEach(r -> System.out.println("#" + r.getIdRestaurante() + " - " + r.getNome() + " (" + r.getLocalizacao() + ")"));
        int id = lerOpcao();
        double media = getFacade().tempoMedioAtendimentoRestaurante(id);
        System.out.printf("\n⏱️  Tempo médio de atendimento (rest. #%d): %.2f minutos\n", id, media);
        pausar();
    }

    private void verFaturacaoTodos() {
        imprimirSeparador();
        System.out.println("   💰 FATURAÇÃO TOTAL (CADEIA)   ");
        imprimirSeparador();
        double total = getFacade().faturacaoTotalTodos();
        System.out.printf("\nFaturação total: %.2f €\n", total);
        pausar();
    }

    private void verFaturacaoPorRestaurante() {
        List<Restaurante> restaurantes = getFacade().listarRestaurantes();
        if (restaurantes == null || restaurantes.isEmpty()) {
            System.out.println("\n❌ Nenhum restaurante disponível.");
            pausar();
            return;
        }
        System.out.println("\n📋 Restaurantes:");
        System.out.println("─".repeat(40));
        restaurantes.forEach(r -> System.out.println("#" + r.getIdRestaurante() + " - " + r.getNome() + " (" + r.getLocalizacao() + ")"));
        int id = lerOpcao();
        double faturacao = getFacade().faturacaoTotalRestaurante(id);
        System.out.printf("\n💰 Faturação total (rest. #%d): %.2f €\n", id, faturacao);
        pausar();
    }

    private void imprimirSeparador() {
        System.out.println("\n" + "═".repeat(40));
    }
}
