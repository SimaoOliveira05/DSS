package org.CadeiaLN.SubSistemaRestaurante.SubSistemaTarefas;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalTime;

/**
 * Representa uma tarefa concreta a ser executada para preparar parte de um pedido
 */
public class Tarefa {
    private int idTarefa;
    private int idPedido;
    private String nomeProduto;
    private String descricao;
    private TipoPosto tipoPosto;
    private EstadoTarefa estado;
    private List<String> instrucoes; // Instruções adicionais (ex: "SEM cebola", "COM bacon extra")
    private int tempoEstimado;
    private LocalTime scheduledStart; // horário previsto de início
    private LocalTime scheduledFinish; // horário previsto de conclusão
    private int delayMinutes; // atraso manual aplicado pelo funcionário (minutos)
    
    public Tarefa(int idPedido, String nomeProduto, String descricao, TipoPosto tipoPosto, EstadoTarefa estado, int tempoEstimado) {
        this.idPedido = idPedido;
        this.nomeProduto = nomeProduto;
        this.descricao = descricao;
        this.tipoPosto = tipoPosto;
        this.estado = estado;
        this.instrucoes = new ArrayList<>();
        this.tempoEstimado = tempoEstimado;
        this.scheduledStart = LocalTime.now();
        this.scheduledFinish = this.scheduledStart.plusMinutes(tempoEstimado);
        this.delayMinutes = 0;
    }
    
    /**
     * Construtor para reconstrução a partir da base de dados.
     * Não altera o contador estático - usa o ID existente.
     */
    public Tarefa(int idTarefa, int idPedido, String nomeProduto, String descricao, TipoPosto tipoPosto, EstadoTarefa estado, int tempoEstimado) {
        this.idTarefa = idTarefa;
        this.idPedido = idPedido;
        this.nomeProduto = nomeProduto;
        this.descricao = descricao;
        this.tipoPosto = tipoPosto;
        this.estado = estado;
        this.instrucoes = new ArrayList<>();
        this.tempoEstimado = tempoEstimado;
        this.scheduledStart = null;
        this.scheduledFinish = null;
        this.delayMinutes = 0;
    }

    
    public void adicionarInstrucao(String instrucao) {
        this.instrucoes.add(instrucao);
    }
    
    public int getIdTarefa() {
        return idTarefa;
    }
    
    public void setIdTarefa(int idTarefa) {
        this.idTarefa = idTarefa;
    }

    public int getIdPedido() {
        return idPedido;
    }
    
    public String getNomeProduto() {
        return nomeProduto;
    }
    
    public String getDescricao() {
        return descricao;
    }
    
    public TipoPosto getTipoPosto() {
        return tipoPosto;
    }
    
    public EstadoTarefa getEstado() {
        return estado;
    }
    
    public void setEstado(EstadoTarefa estado) {
        this.estado = estado;
    }
    
    public List<String> getInstrucoes() {
        return new ArrayList<>(instrucoes);
    }
    
    public int getTempoEstimado() {
        return tempoEstimado;
    }
    
    public void setTempoEstimado(int tempoEstimado) {
        this.tempoEstimado = tempoEstimado;
    }

    public LocalTime getScheduledStart() { return scheduledStart; }
    public LocalTime getScheduledFinish() { return scheduledFinish; }
    public int getDelayMinutes() { return delayMinutes; }
    
    /**
     * Define o delay em minutos diretamente (usado para restaurar da BD)
     */
    public void setDelayMinutes(int delayMinutes) {
        this.delayMinutes = delayMinutes;
    }

    public void applyDelayMinutes(int extraDelay) {
        if (extraDelay <= 0) return;
        this.delayMinutes += extraDelay;
        recomputeFinish();
    }

    public void reschedule(LocalTime newStart) {
        this.scheduledStart = newStart;
        recomputeFinish();
    }

    public void recomputeFinish() {
        if (this.scheduledStart != null) {
            this.scheduledFinish = this.scheduledStart.plusMinutes(tempoEstimado + delayMinutes);
        }
    }
    
    public void iniciarExecucao() {
        this.estado = EstadoTarefa.EM_EXECUCAO;
    }
    
    public void concluir() {
        this.estado = EstadoTarefa.CONCLUIDA;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Tarefa{")
          .append("id='").append(idTarefa).append('\'')
          .append(", pedido='").append(idPedido).append('\'')
          .append(", produto='").append(nomeProduto).append('\'')
          .append(", descricao='").append(descricao).append('\'')
          .append(", posto=").append(tipoPosto)
          .append(", estado='").append(estado).append('\'');
        
        if (!instrucoes.isEmpty()) {
            sb.append(", instrucoes=").append(instrucoes);
        }
                                sb.append(", start=").append(scheduledStart)
                                    .append(", finish=").append(scheduledFinish)
                                    .append(", delayMin=").append(delayMinutes);
        
        sb.append('}');
        return sb.toString();
    }
    
    /**
     * Formata a tarefa para exibição ao funcionário com tempo restante calculado
     */
    public String toDisplayString(String notaPedido) {
        StringBuilder sb = new StringBuilder();
        
        // Linha principal com ícone de estado
        String iconeEstado = obterIconeEstado();
        sb.append(iconeEstado).append(" Pedido #").append(idPedido)
          .append(" | Tarefa #").append(idTarefa).append("\n");
        
        // Descrição da tarefa
        sb.append("   📋 ").append(descricao);
        if (!nomeProduto.isEmpty()) {
            sb.append(" (").append(nomeProduto).append(")");
        }
        sb.append("\n");
        
        // Estado
        sb.append("   🔄 Estado: ").append(estado).append("\n");
        
        // Tempo restante
        long restanteMin = Math.max(0, java.time.Duration.between(
                java.time.LocalTime.now(), scheduledFinish).toMinutes());
        sb.append("   ⏱️  Tempo restante: ").append(restanteMin).append(" min");
        
        if (delayMinutes > 0) {
            sb.append(" (⚠️ +").append(delayMinutes).append(" min atraso)");
        }
        sb.append("\n");
        
        // Nota do pedido
        if (notaPedido != null && !notaPedido.isEmpty()) {
            sb.append("   💬 Nota: ").append(notaPedido).append("\n");
        }
        
        // Instruções especiais
        if (!instrucoes.isEmpty()) {
            sb.append("   📝 Instruções especiais:\n");
            for (String instrucao : instrucoes) {
                sb.append("      → ").append(instrucao).append("\n");
            }
        }
        
        return sb.toString();
    }
    
    /**
     * Retorna ícone apropriado para o estado da tarefa
     */
    private String obterIconeEstado() {
        switch (estado) {
            case PENDENTE:
                return "⏳";
            case EM_EXECUCAO:
                return "🔨";
            case CONCLUIDA:
                return "✅";
            default:
                return "❓";
        }
    }
}
