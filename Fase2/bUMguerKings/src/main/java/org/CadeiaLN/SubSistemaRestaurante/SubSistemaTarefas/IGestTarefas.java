package org.CadeiaLN.SubSistemaRestaurante.SubSistemaTarefas;

import java.time.LocalTime;
import java.util.List;

import org.CadeiaLN.SubSistemaRestaurante.SubSistemaPedidos.Pedido;

/**
 * Interface para o gestor de tarefas (GestorTarefas).
 */
public interface IGestTarefas {
    void decomporPedido(Pedido pedido);
    void recalcularAgendamento();
    boolean adicionarDelayATarefa(int idTarefa, int delayMinutos);
    LocalTime calcularEtaPedido(Pedido pedido);
    int marcarTarefaComoConcluidaNoPosto(TipoPosto tipoPosto, int idTarefa);
    List<Tarefa> listarTarefasDoPosto(TipoPosto tipoPosto);
}
