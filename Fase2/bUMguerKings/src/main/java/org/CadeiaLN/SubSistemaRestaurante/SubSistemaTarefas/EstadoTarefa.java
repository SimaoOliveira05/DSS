package org.CadeiaLN.SubSistemaRestaurante.SubSistemaTarefas;

/**
 * Estados possíveis de uma tarefa durante a sua execução
 */
public enum EstadoTarefa {
    PENDENTE("Pendente"),
    EM_EXECUCAO("Em Execução"),
    CONCLUIDA("Concluída");
    
    private final String descricao;
    
    EstadoTarefa(String descricao) {
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
