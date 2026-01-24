package org.CadeiaLN.SubSistemaRestaurante;

import java.util.List;
import java.time.LocalTime;

import org.CadeiaLN.SubSistemaRestaurante.SubSistemaPedidos.*;
import org.CadeiaLN.SubSistemaRestaurante.SubSistemaTarefas.*;

/**
 * Entidade Restaurante: cada instância possui os seus próprios gestores
 * de pedidos e tarefas, bem como indicadores de desempenho.
 */
public class Restaurante {
    private static int contadorId = 1;

    private final int idRestaurante;
    private String nome;
    private String localizacao;

    // Gestores por restaurante
    private final GestorPedidos gestorPedidos;
    private final GestorTarefas gestorTarefas;

    // Indicadores simples
    private int totalPedidos;
    private double faturacaoTotal;

    public Restaurante(String nome, String localizacao) {
        this.idRestaurante = contadorId++;
        this.nome = nome;
        this.localizacao = localizacao;
        this.gestorPedidos = new GestorPedidos(this.idRestaurante);
        this.gestorTarefas = new GestorTarefas(this.idRestaurante);
        this.totalPedidos = 0;
        this.faturacaoTotal = 0.0;
    }

    /**
     * Construtor para reconstrução a partir da base de dados (com ID conhecido).
     */
    public Restaurante(int id, String nome, String localizacao) {
        this.idRestaurante = id;
        this.nome = nome;
        this.localizacao = localizacao;
        this.gestorPedidos = new GestorPedidos(this.idRestaurante);
        this.gestorTarefas = new GestorTarefas(this.idRestaurante);
        this.totalPedidos = 0;
        this.faturacaoTotal = 0.0;
        // Atualizar contador para evitar colisões de ID
        if (id >= contadorId) {
            contadorId = id + 1;
        }
    }

    public int getIdRestaurante() { return idRestaurante; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getLocalizacao() { return localizacao; }
    public void setLocalizacao(String localizacao) { this.localizacao = localizacao; }

    public GestorPedidos getGestorPedidos() { return gestorPedidos; }
    public GestorTarefas getGestorTarefas() { return gestorTarefas; }

    public int getTotalPedidos() { return totalPedidos; }
    public void incrementaTotalPedidos() { this.totalPedidos++; }
    public double getFaturacaoTotal() { return faturacaoTotal; }
    public void adicionaFaturacao(double valor) { this.faturacaoTotal += valor; }

    @Override
    public String toString() {
        return "Restaurante #" + idRestaurante + " - " + nome + " (" + localizacao + ")";
    }

    // ════════════════════════════════════════════════════════
    // Delegações para GestorPedidos (encapsulamento)
    // ════════════════════════════════════════════════════════

    public void adicionarIngrediente(Ingrediente ingrediente) {
        gestorPedidos.adicionarIngrediente(ingrediente);
    }

    public void adicionarProposta(Proposta proposta) {
        gestorPedidos.adicionarProposta(proposta);
    }

    public List<Proposta> getListaPropostas() {
        return gestorPedidos.getListaPropostas();
    }

    public String listarPersonalizacoesPossiveis(String nomeProposta) {
        return gestorPedidos.listarPersonalizacoesPossiveis(nomeProposta);
    }

    public boolean isMenu(String nomeProposta) {
        return gestorPedidos.isMenu(nomeProposta);
    }

    public List<Produto> getProdutosDoMenu(String nomeMenu) {
        return gestorPedidos.getProdutosDoMenu(nomeMenu);
    }

    public int iniciarNovoPedido() {
        return gestorPedidos.iniciarNovoPedido();
    }

    public boolean adicionarPropostaAoPedido(int idPedido, String nomeProposta) {
        return gestorPedidos.adicionarPropostaAoPedido(idPedido, nomeProposta);
    }

    public boolean adicionarPropostaPersonalizadaAoPedido(int idPedido, String nomeProposta,
                                                          List<String> ingredientesRemovidos,
                                                          List<String> ingredientesAdicionados) {
        return gestorPedidos.adicionarPropostaPersonalizadaAoPedido(idPedido, nomeProposta,
                ingredientesRemovidos, ingredientesAdicionados);
    }

    public void adicionarNotaAoPedido(int idPedido, String nota) {
        gestorPedidos.adicionarNotaAoPedido(idPedido, nota);
    }

    public Pedido getPedido(int idPedido) { return gestorPedidos.getPedido(idPedido); }
    public void removerPedido(int idPedido) { gestorPedidos.removerPedido(idPedido); }

    public boolean finalizarPedido(int idPedido) {
        Pedido pedido = gestorPedidos.getPedido(idPedido);
        if (pedido == null) 
            return false;

        if (pedido.getPropostas().isEmpty()) {
            gestorPedidos.removerPedido(idPedido);
            return false;
        }
        
        // Decompor em tarefas (isto também calcula o ETA)
        gestorTarefas.decomporPedido(pedido);
        
        // Agora sim, finalizar o pedido (define finalizadoEm e estado)
        LocalTime eta = gestorTarefas.calcularEtaPedido(pedido);
        return gestorPedidos.finalizarPedido(idPedido, eta);
    }

    public void atualizarEstadoPedido(int idPedido, EstadoPedido novoEstado) {
        gestorPedidos.atualizarEstadoPedido(idPedido, novoEstado);
    }

    public void adicionarFaturacaoPedido(int idPedido) {
        Pedido pedido = gestorPedidos.getPedido(idPedido);
        if (pedido != null) {
            double faturacao = pedido.getPrecoTotal();
            this.faturacaoTotal += faturacao;
        }
    }

    public String listarTodosPedidos() { return gestorPedidos.listarTodosPedidos(); }
    public String listarPedidosEstado(EstadoPedido estado) { return gestorPedidos.listarPedidosEstado(estado); }

    public String getNotaPedido(int idPedido) {
        Pedido pedido = gestorPedidos.getPedido(idPedido);
        if (pedido != null) {
            return pedido.getNota();
        }
        return null;
    }   
    // ════════════════════════════════════════════════════════
    // Delegações para GestorTarefas (encapsulamento)
    // ════════════════════════════════════════════════════════

    public List<Tarefa> listarTarefasDoPosto(TipoPosto tipoPosto) {
        return gestorTarefas.listarTarefasDoPosto(tipoPosto);
    }

    public int marcarTarefaComoConcluidaNoPosto(TipoPosto tipoPosto, int idTarefa) {
        return gestorTarefas.marcarTarefaComoConcluidaNoPosto(tipoPosto, idTarefa);
    }

    public boolean pedidoConcluido(int idPedido) { return gestorPedidos.pedidoConcluido(idPedido); }

    public boolean adicionarDelayATarefa(int idTarefa, int delayMinutos) {
        return gestorTarefas.adicionarDelayATarefa(idTarefa, delayMinutos);
    }

    // ================================
    // Indicadores por restaurante
    // ================================

    /** Tempo médio de atendimento (min) = finalização -> entrega */
    public double tempoMedioAtendimentoMinutos() {
        return gestorPedidos.atendimentoSumAndCountMinutes();
    }

    
}
