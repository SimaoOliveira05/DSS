package org.CadeiaLN.SubSistemaRestaurante.SubSistemaPedidos;

import java.util.List;

import org.CadeiaLN.SubSistemaRestaurante.SubSistemaTarefas.TipoPosto;

/**
 * Ingrediente possui:
 *  - nome e lista de alergénios
 *  - tipoPosto: onde é preparado/manipulado
 *  - tempoEstimado: minutos necessários para preparação (para agendamento)
 */
public class Ingrediente {
    private String nome;
    private List<String> alergenios;
    private TipoPosto tipoPosto;
    private int tempoEstimado;
    private boolean removivel;



    public Ingrediente(String nome, List<String> alergenios, TipoPosto tipoPosto, int tempoEstimado) {
        this(nome, alergenios, tipoPosto, tempoEstimado, true);
    }

    public Ingrediente(String nome, List<String> alergenios, TipoPosto tipoPosto, int tempoEstimado, boolean removivel) {
        this.nome = nome;
        this.alergenios = alergenios;
        this.tipoPosto = tipoPosto;
        this.tempoEstimado = tempoEstimado > 0 ? tempoEstimado : 1;
        this.removivel = removivel;
    }

    public void setNome(String nome) { this.nome = nome; }
    public void setAlergenios(List<String> alergenios) { this.alergenios = alergenios; }
    public void setTipoPosto(TipoPosto tipoPosto) { this.tipoPosto = tipoPosto; }
    public void setTempoEstimado(int tempoEstimado) { this. tempoEstimado = tempoEstimado; }
    public void setRemovivel(boolean removivel) { this.removivel = removivel; }

    public String getNome() { return nome; }
    public List<String> getAlergenios() { return alergenios; }
    public TipoPosto getTipoPosto() { return tipoPosto; }
    public int getTempoEstimado() { return tempoEstimado; }
    public boolean isRemovivel() { return removivel; }

    @Override
    public String toString() {
        if (alergenios == null || alergenios.isEmpty()) {
            return nome;
        }
        return nome + " ⚠️ (" + String.join(", ", alergenios) + ")";
    }
}
