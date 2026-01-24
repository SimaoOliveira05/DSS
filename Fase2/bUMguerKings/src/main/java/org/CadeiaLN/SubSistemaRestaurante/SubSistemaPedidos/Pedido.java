package org.CadeiaLN.SubSistemaRestaurante.SubSistemaPedidos;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalTime;
import org.CadeiaLN.SubSistemaRestaurante.SubSistemaTarefas.Tarefa;
import org.CadeiaLN.SubSistemaRestaurante.SubSistemaTarefas.EstadoTarefa;

public class Pedido {

    private static int contadorId = 1;
    
    private int idPedido;
    private List<Proposta> propostas;
    private String nota;
    private double precoTotal;
    private EstadoPedido estado;
    private LocalTime eta; // horário previsto de conclusão do pedido
    private List<Tarefa> tarefasAssociadas;
    // Indicadores: timestamps reais
    private LocalTime finalizadoEm; // quando o pedido saiu de EM_CONSTRUCAO para PENDENTE
    private LocalTime entregueEm;   // quando o pedido foi marcado como ENTREGUE

    public Pedido(List<Proposta> propostas, String nota, EstadoPedido estado) {
        this.idPedido = contadorId++;
        this.propostas = propostas;
        this.nota = nota;
        for (Proposta p : propostas) {
            this.precoTotal += p.getPreco();
        }
        this.estado = estado;
        this.eta = LocalTime.now().plusMinutes(0);
        this.tarefasAssociadas = new ArrayList<>();
    }

    /**
     * Construtor para reconstrução a partir da base de dados (com ID conhecido).
     */
    public Pedido(int id, List<Proposta> propostas, String nota, EstadoPedido estado) {
        this.idPedido = id;
        this.propostas = propostas;
        this.nota = nota;
        for (Proposta p : propostas) {
            this.precoTotal += p.getPreco();
        }
        this.estado = estado;
        this.eta = LocalTime.now().plusMinutes(0);
        this.tarefasAssociadas = new ArrayList<>();
        // Atualizar contador para evitar colisões de ID
        if (id >= contadorId) {
            contadorId = id + 1;
        }
    }

    public int getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(int idPedido) {
        this.idPedido = idPedido;
    }


    public List<Proposta> getPropostas() {
        return new ArrayList<>(propostas);
    }

    public void setPropostas(List<Proposta> propostas) {
        this.propostas = new ArrayList<>(propostas);
    }

    public String getNota() {
        return nota;
    }

    public void setNota(String nota) {
        this.nota = nota;
    }

    public double getPrecoTotal() {
        return precoTotal;
    }

    public void setPrecoTotal(double precoTotal) {
        this.precoTotal = precoTotal;
    }

    public void incrementarPrecoTotal(double valor) {
        this.precoTotal += valor;
    }

    /**
     * Adiciona uma proposta ao pedido, criando uma cópia independente
     * para evitar que alterações em pedidos diferentes afetem uns aos outros
     */
    public void adicionarProposta(Proposta proposta) {
        Proposta copia = proposta.clone();
        this.propostas.add(copia);
        this.incrementarPrecoTotal(copia.getPreco());
    }

    public EstadoPedido getEstado() {
        return estado;
    }

    public void setEstado(EstadoPedido estado) {
        this.estado = estado;
    }

    public LocalTime getEta() {
        return eta;
    }

    public void setEta(LocalTime eta) {
        this.eta = eta;
    }
    
    public LocalTime getFinalizadoEm() {
        return finalizadoEm;
    }

    public void setFinalizadoEm(LocalTime finalizadoEm) {
        this.finalizadoEm = finalizadoEm;
    }

    public LocalTime getEntregueEm() {
        return entregueEm;
    }

    public void setEntregueEm(LocalTime entregueEm) {
        this.entregueEm = entregueEm;
    }

    public void setTarefasAssociadas(List<Tarefa> tarefas) {
        this.tarefasAssociadas = new ArrayList<>(tarefas);
    }

    public List<Tarefa> getTarefasAssociadas() {
        return new ArrayList<>(tarefasAssociadas);
    }
    
    public boolean isReadyForDelivery() {
        for (Tarefa tarefa : tarefasAssociadas) {
            if (tarefa.getEstado() != EstadoTarefa.CONCLUIDA) {
                return false;
            }
        }
        return true;
    }

}   
