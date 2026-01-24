package org.CadeiaLN.SubSistemaRestaurante.SubSistemaPedidos;

/**
 * Estados possíveis de um pedido ao longo do seu ciclo de vida
 */
public enum EstadoPedido {
    EM_CONSTRUCAO("Em Construção"),
    PENDENTE("Pendente"),
    PRONTO("Pronto"),
    ENTREGUE("Entregue");
    
    private final String descricao;
    
    EstadoPedido(String descricao) {
        this.descricao = descricao;
    }
    
    public String getDescricao() {
        return descricao;
    }
    
    @Override
    public String toString() {
        return descricao;
    }
}
