package org.CadeiaLN.SubSistemaRestaurante.SubSistemaPedidos;

import java.util.ArrayList;
import java.util.List;

public class Menu extends Proposta {
    private List<Produto> produtos;

    public Menu(String nome, List<Produto> produtos, double preco) {
        super(nome, preco);
        this.produtos = produtos != null ? produtos : new ArrayList<>();
    }

    /**
     * Retorna uma string formatada com as opções de personalização dos produtos do menu.
     */
    public String toPersonalizacaoString() {
        StringBuilder sb = new StringBuilder();
        sb.append("🍽️  Menu: ").append(getNome()).append("\n");
        sb.append("─".repeat(40)).append("\n");
        sb.append("\n📦 Produtos incluídos:\n");
        List<Produto> produtos = getProdutos();
        for (int i = 0; i < produtos.size(); i++) {
            Produto p = produtos.get(i);
            sb.append("\n").append(i + 1).append(". ").append(p.toPersonalizacaoString());
        }
        sb.append("\n\n" + "─".repeat(40));
        sb.append("\n💡 Pode personalizar cada produto individualmente\n");
        return sb.toString();
    }

    /**
     * Retorna os ingredientes que podem ser removidos (os atuais do menu)
     */
    public List<Ingrediente> getIngredientesRemoviveis() {
        List<Ingrediente> ingredientes = new ArrayList<>();
        for (Produto produto : produtos) {
            ingredientes.addAll(produto.getIngredientes());
        }
        return ingredientes;
    }

    public List<Ingrediente> getIngredientesAdicionaveis() {
        List<Ingrediente> ingredientes = new ArrayList<>();
        for (Produto produto : produtos) {
            ingredientes.addAll(produto.getIngredientesAdicionaveis());
        }
        return ingredientes;
    }

    public List<Produto> getProdutos() {
        return new ArrayList<>(produtos);
    }

    public void setProdutos(List<Produto> produtos) {
        this.produtos = produtos;
    }

    public void adicionarProduto(Produto produto) {
        this.produtos.add(produto);
    }

    public void removerProduto(Produto produto) {
        this.produtos.remove(produto);
    }

    @Override
    public List<Ingrediente> getIngredientes() {
        List<Ingrediente> ingredientes = new ArrayList<>();
        for (Produto produto : produtos) {
            ingredientes.addAll(produto.getIngredientes());
        }
        return ingredientes;
    }
    
    @Override
    public List<Produto> decomporEmProdutos() {
        return new ArrayList<>(produtos);
    }
    
    @Override
    public Proposta clone() {
        // Copiar cada produto do menu
        List<Produto> produtosCopia = new ArrayList<>();
        for (Produto p : produtos) {
            produtosCopia.add((Produto) p.clone());
        }
        return new Menu(getNome(), produtosCopia, getPreco());
    }

    @Override
    public String toString() {
        return "Menu{" +
                "nome='" + getNome() + '\'' +
                ", preco=" + getPreco() +
                ", produtos=" + produtos +
                '}';
    }
}
