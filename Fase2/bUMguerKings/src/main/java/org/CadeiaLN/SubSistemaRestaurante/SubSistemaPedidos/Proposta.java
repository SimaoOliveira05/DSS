package org.CadeiaLN.SubSistemaRestaurante.SubSistemaPedidos;
import java.util.List;

/**
 * Proposta é a abstração de tudo o que pode ser pedido: Produto ou Menu
 * Define o contrato para qualquer item que possa ser incluído num pedido
 */
public abstract class Proposta {
    private String nome;
    private double preco;

    public Proposta(String nome, double preco) {
        this.nome = nome;
        this.preco = preco;
    }

    public String getNome() {
        return nome;
    }

    public double getPreco() {
        return preco;
    }

    protected void setNome(String nome) {
        this.nome = nome;
    }

    protected void setPreco(double preco) {
        this.preco = preco;
    }

    /**
     * Retorna os ingredientes desta proposta
     */
    public abstract List<Ingrediente> getIngredientes();
    
    /**
     * Retorna os ingredientes que podem ser removidos desta proposta
     */
    public abstract List<Ingrediente> getIngredientesRemoviveis();

    public abstract List<Ingrediente> getIngredientesAdicionaveis();
    
    /**
     * Decompõe esta proposta em produtos base para criação de tarefas
     * @return lista de produtos a processar
     */
    public abstract List<Produto> decomporEmProdutos();

    public abstract String toPersonalizacaoString();
    
    /**
     * Cria uma cópia profunda desta proposta
     * @return cópia independente da proposta
     */
    public abstract Proposta clone();
}
