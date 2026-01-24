package org.CadeiaLN.SubSistemaRestaurante.SubSistemaPedidos;
import java.util.List;
import java.util.ArrayList;

public class Produto extends Proposta {
    private List<Ingrediente> ingredientes;
    private List<Ingrediente> ingredientesAdicionaveis;

    public Produto(String nome, List<Ingrediente> ingredientes, double preco, List<Ingrediente> ingredientesAdicionaveis) {
        super(nome, preco);
        this.ingredientes = ingredientes != null ? ingredientes : new ArrayList<>();
        this.ingredientesAdicionaveis = ingredientesAdicionaveis != null ? ingredientesAdicionaveis : new ArrayList<>();
    }

    /**
     * Retorna uma string formatada com as opções de personalização do produto.
     */
    public String toPersonalizacaoString() {
        StringBuilder sb = new StringBuilder();
        sb.append("🍔 Produto: ").append(getNome()).append("\n");
        sb.append("─".repeat(40)).append("\n");
        sb.append("\n➖ Ingredientes removíveis:\n   ");
        if (getIngredientesRemoviveis().isEmpty()) {
            sb.append("(nenhum)");
        } else {
            boolean primeiro = true;
            for (Ingrediente ing : getIngredientesRemoviveis()) {
                if (!primeiro) sb.append(", ");
                sb.append(ing.getNome());
                primeiro = false;
            }
        }
        sb.append("\n\n➕ Ingredientes adicionáveis:\n   ");
        if (getIngredientesAdicionaveis().isEmpty()) {
            sb.append("(nenhum)");
        } else {
            boolean primeiro = true;
            for (Ingrediente ing : getIngredientesAdicionaveis()) {
                if (!primeiro) sb.append(", ");
                sb.append(ing.getNome());
                primeiro = false;
            }
        }
        sb.append("\n" + "─".repeat(40));
        return sb.toString();
    }

    /**
     * Retorna os ingredientes que podem ser removidos (os atuais do produto)
     */
    public List<Ingrediente> getIngredientesRemoviveis() {
        List<Ingrediente> removiveis = new ArrayList<>();
        for (Ingrediente i : ingredientes) {
            if (i.isRemovivel()) removiveis.add(i);
        }
        return removiveis;
    }

    public List<Ingrediente> getIngredientesAdicionaveis() {
        return new ArrayList<>(ingredientesAdicionaveis);
    }


    public void setIngredientes(List<Ingrediente> ingredientes) {
        this.ingredientes = ingredientes;
    }

    @Override
    public List<Ingrediente> getIngredientes() {
        return new ArrayList<>(ingredientes);
    }
    
    // Tarefas deixam de estar acopladas ao produto; derivam dos ingredientes

    @Override
    public List<Produto> decomporEmProdutos() {
        List<Produto> produtos = new ArrayList<>();
        produtos.add(this);
        return produtos;
    }
    
    @Override
    public Proposta clone() {
        // Copiar listas de ingredientes
        List<Ingrediente> ingredientesCopia = new ArrayList<>(this.ingredientes);
        List<Ingrediente> adicionaveisCopia = new ArrayList<>(this.ingredientesAdicionaveis);
        return new Produto(getNome(), ingredientesCopia, getPreco(), adicionaveisCopia);
    }

    @Override
    public String toString() {
    return "Produto{" +
        "nome='" + getNome() + '\'' +
        ", preco=" + getPreco() +
        ", ingredientes=" + ingredientes +
        '}';
    }
}
