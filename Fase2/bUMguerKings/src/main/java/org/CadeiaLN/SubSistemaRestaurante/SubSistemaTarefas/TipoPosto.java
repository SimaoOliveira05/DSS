package org.CadeiaLN.SubSistemaRestaurante.SubSistemaTarefas;

/**
 * Tipos de postos de trabalho na cozinha
 */
public enum TipoPosto {
    GRILL("Grelha"),
    FRITURA("Fritura"),
    MONTAGEM("Montagem"),
    BEBIDAS("Bebidas"),
    SOBREMESAS("Sobremesas");
    
    private final String descricao;
    
    TipoPosto(String descricao) {
        this.descricao = descricao;
    }
    
    public String getDescricao() {
        return descricao;
    }
}
