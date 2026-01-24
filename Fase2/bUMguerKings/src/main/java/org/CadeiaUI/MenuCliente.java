package org.CadeiaUI;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import org.CadeiaLN.CadeiaFacade;
import org.CadeiaLN.SubSistemaRestaurante.SubSistemaPedidos.Ingrediente;
import org.CadeiaLN.SubSistemaRestaurante.SubSistemaPedidos.Produto;

/**
 * Menu de operações para clientes
 */
public class MenuCliente extends Menu {
    public MenuCliente(Scanner scanner, CadeiaFacade facade) {
        super(scanner, "MENU CLIENTE", facade);
    }
    
    @Override
    public void mostrar() {
        while (true) {
            imprimirCabecalho();
            System.out.println("\n1. 🛒 Criar Novo Pedido");
            System.out.println("2. 📋 Ver Todos os Pedidos");
            System.out.println("0. ⬅️  Voltar");
            
            int opcao = lerOpcao();
            
            cleanTerminal();
            switch (opcao) {
                case 1:
                    criarPedido();
                    break;
                case 2:
                    verTodosPedidos();
                    break;
                case 0:
                    return;
                default:
                    System.out.println("\n❌ Opção inválida!");
            }
        }
    }
    
    // ══════════════════════════════════════════════════════════
    // Criação de Pedido
    // ══════════════════════════════════════════════════════════
    
    private void criarPedido() {
        imprimirSeparador();
        System.out.println("         🛒 CRIAR NOVO PEDIDO         ");
        imprimirSeparador();
        
        int idPedido = getFacade().iniciarNovoPedido();
        System.out.println("\n✅ Pedido #" + idPedido + " iniciado!\n");
        
        // Loop para adicionar propostas
        adicionarPropostasAoPedido(idPedido);
        
        // Adicionar nota opcional
        adicionarNotaOpcional(idPedido);
        
        // Finalizar pedido
        finalizarPedidoComResumo(idPedido);
    }
    
    private void adicionarPropostasAoPedido(int idPedido) {
        boolean continuarAdicionando = true;
        
        while (continuarAdicionando) {
            mostrarPropostasDisponiveis();
            
            int opcao = lerOpcao();
            
            List<String> propostas = getFacade().getPropostasDisponiveis();
            
            if (opcao > 0 && opcao <= propostas.size()) {
                adicionarProposta(idPedido, propostas.get(opcao - 1));
            } else if (opcao == propostas.size() + 1) {
                continuarAdicionando = false;
            } else {
                System.out.println("\n❌ Opção inválida!");
            }
        }
    }
    
    private void mostrarPropostasDisponiveis() {
        System.out.println("\n" + "─".repeat(40));
        System.out.println("📋 PROPOSTAS DISPONÍVEIS");
        System.out.println("─".repeat(40));
        
        List<String> propostas = getFacade().getPropostasDisponiveis();
        for (int i = 0; i < propostas.size(); i++) {
            System.out.println((i + 1) + ". " + propostas.get(i));
        }
        System.out.println((propostas.size() + 1) + ". ✅ Finalizar Pedido");
    }
    
    private void adicionarNotaOpcional(int idPedido) {
        getScanner().nextLine(); // Limpar buffer
        System.out.print("\n📝 Deseja adicionar uma nota ao pedido? (s/n): ");
        String resposta = getScanner().nextLine().trim();
        
        if (resposta.equalsIgnoreCase("s")) {
            System.out.print("💬 Nota: ");
            String nota = getScanner().nextLine();
            getFacade().adicionarNotaAoPedido(idPedido, nota);
            System.out.println("✅ Nota adicionada!");
        }
    }
    
    private void finalizarPedidoComResumo(int idPedido) {
        if (!getFacade().finalizarPedido(idPedido)) {
            System.out.println("\n❌ Pedido não pode ser finalizado (sem itens). Pedido cancelado.");
            pausar();
            return;
        }
        
        imprimirSeparador();
        System.out.println("✅ PEDIDO #" + idPedido + " CONFIRMADO!");
        imprimirSeparador();
        
        long etaMin = getFacade().getEtaPedidoMinutos(idPedido);
        System.out.println("⏱️  Tempo estimado: " + etaMin + " minutos");
        
        System.out.println("\n👍 Obrigado pelo seu pedido!");
        pausar();
    }
    
    // ══════════════════════════════════════════════════════════
    // Adicionar Proposta
    // ══════════════════════════════════════════════════════════

    private void adicionarProposta(int idPedido, String nomeProposta) {
        System.out.print("\n🔧 Deseja personalizar '" + nomeProposta + "'? (s/n): ");
        getScanner().nextLine(); // Limpar buffer
        String resposta = getScanner().nextLine().trim();
        
        if (resposta.equalsIgnoreCase("s")) {
            personalizarEAdicionar(idPedido, nomeProposta);
        } else {
            adicionarPropostaSemPersonalizacao(idPedido, nomeProposta);
        }
    }
    
    private void adicionarPropostaSemPersonalizacao(int idPedido, String nomeProposta) {
        if (getFacade().adicionarPropostaAoPedido(idPedido, nomeProposta)) {
            System.out.println("✅ '" + nomeProposta + "' adicionado ao pedido!");
        } else {
            System.out.println("❌ Erro ao adicionar proposta!");
        }
    }
    
    // ══════════════════════════════════════════════════════════
    // Personalização
    // ══════════════════════════════════════════════════════════
    
    private void personalizarEAdicionar(int idPedido, String nomeProposta) {
        if (getFacade().isMenu(nomeProposta)) {
            personalizarMenu(idPedido, nomeProposta);
        } else {
            personalizarProduto(idPedido, nomeProposta);
        }
    }
    
    private void personalizarProduto(int idPedido, String nomeProduto) {
        System.out.println("\n" + getFacade().listarPersonalizacoesPossiveis(nomeProduto));
        
        List<String> ingredientesRemover = perguntarIngredientes(
            "\n➖ Ingredientes a remover (separados por vírgula, ou ENTER para nenhum):"
        );
        
        List<String> ingredientesAdicionar = perguntarIngredientes(
            "➕ Ingredientes a adicionar (separados por vírgula, ou ENTER para nenhum):"
        );
        
        if (getFacade().adicionarPropostaPersonalizadaAoPedido(idPedido, nomeProduto, 
                                                                ingredientesRemover, 
                                                                ingredientesAdicionar)) {
            System.out.println("\n✅ '" + nomeProduto + "' personalizado adicionado ao pedido!");
        } else {
            System.out.println("\n❌ Erro ao adicionar produto personalizado!");
        }
    }
    
    // ══════════════════════════════════════════════════════════
    // Personalização de Menu
    // ══════════════════════════════════════════════════════════
    
    private void personalizarMenu(int idPedido, String nomeMenu) {
        System.out.println("\n" + getFacade().listarPersonalizacoesPossiveis(nomeMenu));
        
        List<Produto> produtos = getFacade().getProdutosDoMenu(nomeMenu);
        List<Produto> produtosAIncluir = new ArrayList<>(produtos);
        
        // Passo 1: Remover produtos indesejados
        produtosAIncluir = removerProdutosDoMenu(produtosAIncluir);
        
        if (produtosAIncluir.isEmpty()) {
            System.out.println("\n❌ Nenhum produto restante no menu. Operação cancelada.");
            pausar();
            return;
        }
        
        // Passo 2: Personalizar produtos individuais
        List<String> produtosPersonalizados = personalizarProdutosDoMenu(idPedido, produtosAIncluir);
        
        // Passo 3: Adicionar produtos restantes sem personalização
        adicionarProdutosRestantes(idPedido, produtosAIncluir, produtosPersonalizados);
        
        System.out.println("\n✅ Menu processado com sucesso!");
        pausar();
    }
    
    private List<Produto> removerProdutosDoMenu(List<Produto> produtos) {
        System.out.print("\n🗑️  Deseja remover algum produto do menu? (s/n): ");
        String resposta = getScanner().nextLine().trim();
        
        if (!resposta.equalsIgnoreCase("s")) {
            return produtos;
        }
        
        List<Produto> produtosRestantes = new ArrayList<>(produtos);
        boolean continuarRemovendo = true;
        
        while (continuarRemovendo && !produtosRestantes.isEmpty()) {
            System.out.println("\n📦 Produtos no menu:");
            for (int i = 0; i < produtosRestantes.size(); i++) {
                System.out.println("  " + (i + 1) + ". " + produtosRestantes.get(i).getNome());
            }
            
            int escolha = lerOpcao("\n👉 Número do produto a remover (0 para continuar): ");
            getScanner().nextLine(); // Limpar buffer
            
            if (escolha == 0) {
                continuarRemovendo = false;
            } else if (escolha > 0 && escolha <= produtosRestantes.size()) {
                Produto removido = produtosRestantes.remove(escolha - 1);
                System.out.println("❌ '" + removido.getNome() + "' removido.");
            } else {
                System.out.println("❌ Opção inválida!");
            }
        }
        
        return produtosRestantes;
    }
    
    private List<String> personalizarProdutosDoMenu(int idPedido, List<Produto> produtos) {
        System.out.print("\n🔧 Deseja personalizar algum produto? (s/n): ");
        String resposta = getScanner().nextLine().trim();
        
        List<String> produtosPersonalizados = new ArrayList<>();
        
        if (!resposta.equalsIgnoreCase("s")) {
            return produtosPersonalizados;
        }
        
        boolean continuarPersonalizando = true;
        
        while (continuarPersonalizando && !produtos.isEmpty()) {
            System.out.println("\n📦 Produtos disponíveis:");
            for (int i = 0; i < produtos.size(); i++) {
                System.out.println("  " + (i + 1) + ". " + produtos.get(i).getNome());
            }
            
            int escolha = lerOpcao("\n👉 Número do produto a personalizar (0 para terminar): ");
            
            if (escolha == 0) {
                continuarPersonalizando = false;
            } else if (escolha > 0 && escolha <= produtos.size()) {
                Produto produtoEscolhido = produtos.get(escolha - 1);
                personalizarProdutoDoMenu(idPedido, produtoEscolhido, produtosPersonalizados);
            } else {
                System.out.println("❌ Opção inválida!");
            }
        }
        
        return produtosPersonalizados;
    }
    
    private void personalizarProdutoDoMenu(int idPedido, Produto produto, List<String> produtosPersonalizados) {
        String nomeProduto = produto.getNome();
        
        System.out.println("\n🔧 Personalizando: " + nomeProduto);
        mostrarOpcoesPersonalizacao(produto);
        
        getScanner().nextLine(); // Limpar buffer
        
        List<String> ingredientesRemover = perguntarIngredientes(
            "\n➖ Ingredientes a remover (separados por vírgula, ou ENTER para nenhum):"
        );
        
        List<String> ingredientesAdicionar = perguntarIngredientes(
            "➕ Ingredientes a adicionar (separados por vírgula, ou ENTER para nenhum):"
        );
        
        if (getFacade().adicionarPropostaPersonalizadaAoPedido(idPedido, nomeProduto, 
                                                                ingredientesRemover, 
                                                                ingredientesAdicionar)) {
            System.out.println("✅ '" + nomeProduto + "' personalizado adicionado!");
            produtosPersonalizados.add(nomeProduto);
        } else {
            System.out.println("❌ Erro ao personalizar produto!");
        }
    }
    
    private void mostrarOpcoesPersonalizacao(Produto produto) {
        System.out.print("  ➖ Removíveis: ");
        if (produto.getIngredientesRemoviveis().isEmpty()) {
            System.out.print("(nenhum)");
        } else {
            for (Ingrediente ing : produto.getIngredientesRemoviveis()) {
                System.out.print(ing.getNome() + ", ");
            }
        }
        
        System.out.print("\n  ➕ Adicionáveis: ");
        if (produto.getIngredientesAdicionaveis().isEmpty()) {
            System.out.print("(nenhum)");
        } else {
            for (Ingrediente ing : produto.getIngredientesAdicionaveis()) {
                System.out.print(ing.getNome() + ", ");
            }
        }
        System.out.println();
    }
    
    private void adicionarProdutosRestantes(int idPedido, List<Produto> produtos, List<String> produtosPersonalizados) {
        for (Produto p : produtos) {
            if (!produtosPersonalizados.contains(p.getNome())) {
                if (getFacade().adicionarPropostaAoPedido(idPedido, p.getNome())) {
                    System.out.println("✅ '" + p.getNome() + "' adicionado!");
                }
            }
        }
    }
    
    // ══════════════════════════════════════════════════════════
    // Métodos Auxiliares
    // ══════════════════════════════════════════════════════════
    
    private List<String> perguntarIngredientes(String mensagem) {
        System.out.print(mensagem + " ");
        String input = getScanner().nextLine().trim();
        
        List<String> ingredientes = new ArrayList<>();
        if (!input.isEmpty()) {
            for (String ing : input.split(",")) {
                ingredientes.add(ing.trim());
            }
        }
        return ingredientes;
    }
    
    private void verTodosPedidos() {
        imprimirSeparador();
        System.out.println("         📋 TODOS OS PEDIDOS         ");
        imprimirSeparador();
        System.out.println(getFacade().listarPedidos());
        pausar();
    }
    
    private void imprimirSeparador() {
        System.out.println("\n" + "═".repeat(40));
    }
}


