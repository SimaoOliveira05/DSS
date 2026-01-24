package org.CadeiaLN.SubSistemaRestaurante.SubSistemaTarefas;

import java.util.List;
import org.CadeiaLN.SubSistemaRestaurante.SubSistemaPedidos.*;
import org.CadeiaDL.TarefaDAO;

import java.util.ArrayList;
import java.time.LocalTime;

/**
 * Gestor responsável por decompor pedidos em tarefas e distribuí-las pelos postos
 */
public class GestorTarefas implements IGestTarefas {
    private TarefaDAO tarefas; // TarefaDAO implementa Map<Integer, Tarefa>

    public GestorTarefas(int idRestaurante) {
        this.tarefas = new TarefaDAO(idRestaurante);
    }

    // Acesso ao DAO para métodos específicos
    private TarefaDAO getTarefaDAO() {
        return tarefas;
    }

    // Modelo antigo de tarefas via templates removido. Agora tarefas derivam dos ingredientes.

    public void decomporPedido(Pedido pedido) {
        List<Tarefa> tarefasPedido = new ArrayList<>();
        int idPedido = pedido.getIdPedido();
        
        // Decompor todas as propostas em tarefas
        for (Proposta proposta : pedido.getPropostas()) {
            for (Produto produto : proposta.decomporEmProdutos()) {
                for (Ingrediente ing : produto.getIngredientes()) {
                    Tarefa tarefa = new Tarefa(
                        idPedido,
                        produto.getNome(),
                        ing.getNome(),
                        ing.getTipoPosto(),
                        EstadoTarefa.PENDENTE,
                        ing.getTempoEstimado()
                    );
                    tarefasPedido.add(tarefa);
                    tarefas.put(tarefa.getIdTarefa(), tarefa); // Persistir no DAO
                }
            }
        }
        
        // Atualizar pedido
        pedido.setTarefasAssociadas(tarefasPedido);
        pedido.setEstado(EstadoPedido.PENDENTE);
        
        // Recalcular tempos ANTES de calcular o ETA
        recalcularAgendamento();
        
        // Agora calcular o ETA com os tempos já agendados
        pedido.setEta(calcularEtaPedido(pedido));
    }


    public void recalcularAgendamento() {
        // Versão simplificada: cada posto tem uma fila
        LocalTime agora = LocalTime.now();
        
        // Iterar por cada tipo de posto
        for (TipoPosto tipoPosto : TipoPosto.values()) {
            List<Tarefa> tarefasPosto = getTarefaDAO().getTarefasPorPosto(tipoPosto);
            LocalTime cursor = agora;
            for (Tarefa t : tarefasPosto) {
                if (t.getEstado() == EstadoTarefa.CONCLUIDA) {
                    continue;
                }
                t.reschedule(cursor);
                tarefas.put(t.getIdTarefa(), t); // Persistir alterações
                cursor = t.getScheduledFinish();
            }
        }
    }

    /** Aplica atraso a tarefa e recalcula agendamento */
    public boolean adicionarDelayATarefa(int idTarefa, int delayMinutos) {
        Tarefa t = tarefas.get(idTarefa);
        if (t == null || delayMinutos <= 0) return false;
        if (t.getEstado() == EstadoTarefa.CONCLUIDA) return false;
        t.applyDelayMinutes(delayMinutos);
        tarefas.put(idTarefa, t); // Persistir alterações
        recalcularAgendamento();
        return true;
    }

    public LocalTime calcularEtaPedido(Pedido pedido) {
        List<Tarefa> lista = getTarefaDAO().getTarefasPorPedido(pedido.getIdPedido());
        LocalTime eta = LocalTime.now();
        if (lista == null || lista.isEmpty()) return LocalTime.now();
        
        for (Tarefa t : lista) {
            if (t.getEstado() != EstadoTarefa.CONCLUIDA) {
                LocalTime finish = t.getScheduledFinish();
                if (finish != null && finish.isAfter(eta)) eta = finish;
            }
        }
        return eta;
    }

    public int marcarTarefaComoConcluidaNoPosto(TipoPosto tipoPosto, int idTarefa) {
        // Buscar a tarefa pelo ID
        Tarefa tarefa = tarefas.get(idTarefa);
        if (tarefa == null || tarefa.getTipoPosto() != tipoPosto) {
            return -1; // tarefa não existe ou não pertence a este posto
        }
        
        // Marcar como concluída
        tarefa.setEstado(EstadoTarefa.CONCLUIDA);
        tarefas.put(idTarefa, tarefa); // Persistir alteração
        
        // Recalcular agendamento do posto afetado
        recalcularAgendamento();
        
        return tarefa.getIdPedido();
    }

    public List<Tarefa> listarTarefasDoPosto(TipoPosto tipoPosto) {
        return getTarefaDAO().getTarefasPorPosto(tipoPosto);
    }

}
