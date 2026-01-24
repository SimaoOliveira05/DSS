package org.CadeiaLN;

import org.CadeiaLN.SubSistemaRestaurante.GestorRestaurante;
import org.CadeiaLN.SubSistemaRestaurante.Restaurante;
import org.CadeiaLN.SubSistemaRestaurante.SubSistemaPedidos.*;
import org.CadeiaLN.SubSistemaRestaurante.SubSistemaTarefas.EstadoTarefa;
import org.CadeiaLN.SubSistemaRestaurante.SubSistemaTarefas.Tarefa;
import org.CadeiaLN.SubSistemaRestaurante.SubSistemaTarefas.TipoPosto;

import java.util.ArrayList;
import java.util.List;

public class CadeiaFacade implements ICadeiaFacade {
    private final GestorRestaurante gestorRestaurante;

    public CadeiaFacade() {
        this.gestorRestaurante = new GestorRestaurante();
        // Criar restaurantes iniciais apenas se não existir nenhum na BD
        if (gestorRestaurante.listarRestaurantes().isEmpty()) {
            gestorRestaurante.criarRestaurante("bUMguerKings Braga", "Campus de Gualtar");
            gestorRestaurante.criarRestaurante("bUMguerKings Ponte de Lima", "Praça da República");
        }
    }

    // ================================
    // Gestão de Restaurantes
    // ================================
    public int criarRestaurante(String nome, String localizacao) {
        return gestorRestaurante.criarRestaurante(nome, localizacao);
    }

    public boolean selecionarRestaurante(int idRestaurante) {
        return gestorRestaurante.selecionarRestaurante(idRestaurante);
    }
    
    public List<Restaurante> listarRestaurantes() {
        return gestorRestaurante.listarRestaurantes();
    }

    public String getRestauranteAtualNome() {
        Restaurante r = gestorRestaurante.getRestauranteAtual();
        return r == null ? "(nenhum)" : r.getNome();
    }

    // ================================
    // Gestão de Pedidos
    // ================================
    public int iniciarNovoPedido() {
        return gestorRestaurante.iniciarNovoPedido();
    }

    public boolean adicionarPropostaAoPedido(int idPedido, String nomeProposta) {
        return gestorRestaurante.adicionarPropostaAoPedido(idPedido, nomeProposta);
    }

    public boolean adicionarPropostaPersonalizadaAoPedido(int idPedido, String nomeProposta,
                                                          List<String> ingredientesRemovidos,
                                                          List<String> ingredientesAdicionados) {
        return gestorRestaurante.adicionarPropostaPersonalizadaAoPedido(
                idPedido, nomeProposta, ingredientesRemovidos, ingredientesAdicionados);
    }

    public void adicionarNotaAoPedido(int idPedido, String nota) {
        gestorRestaurante.adicionarNotaAoPedido(idPedido, nota);
    }

    public boolean finalizarPedido(int idPedido) {
        return gestorRestaurante.finalizarPedido(idPedido);
    }

    public void marcarPedidoComoEntregue(int idPedido) {
        gestorRestaurante.marcarPedidoComoEntregue(idPedido);
    }

    // ================================
    // Consulta de Menu e Personalizações
    // ================================
    public List<String> getPropostasDisponiveis() {
        List<String> nomes = new ArrayList<>();
        for (Proposta p : gestorRestaurante.getListaPropostas()) {
            nomes.add(p.getNome());
        }
        return nomes;
    }

    public String listarPersonalizacoesPossiveis(String nomeProposta) {
        return gestorRestaurante.listarPersonalizacoesPossiveis(nomeProposta);
    }

    public boolean isMenu(String nomeProposta) {
        return gestorRestaurante.isMenu(nomeProposta);
    }

    public List<Produto> getProdutosDoMenu(String nomeMenu) {
        return gestorRestaurante.getProdutosDoMenu(nomeMenu);
    }

    // ================================
    // Consulta de Estado - Pedidos
    // ================================
    public String listarPedidos() {
        return gestorRestaurante.listarPedidos();
    }


    public String listarPedidosProntos() {
        return gestorRestaurante.listarPedidosProntos();
    }

    public long getEtaPedidoMinutos(int idPedido) {
        return gestorRestaurante.getEtaPedidoMinutos(idPedido);
    }

    // ================================
    // Gestão de Tarefas - Postos de Trabalho
    // ================================
    public String listarTarefasDoPosto(TipoPosto tipoPosto) {
        List<Tarefa> tarefas = gestorRestaurante.listarTarefasDoPosto(tipoPosto);
        if (tarefas == null || tarefas.isEmpty()) {
            return "📭 Nenhuma tarefa pendente!\n🎉 Pode descansar enquanto aguarda novos pedidos.";
        }
        StringBuilder sb = new StringBuilder();
        int pendentes = 0;
        int emExecucao = 0;
        for (Tarefa t : tarefas) {
            switch (t.getEstado()) {
                case PENDENTE -> pendentes++;
                case EM_EXECUCAO -> emExecucao++;
                default -> {}
            }
        }
        sb.append("📊 Resumo: ").append(pendentes).append(" pendente(s), ");
        sb.append("─".repeat(40)).append("\n\n");
        for (Tarefa t : tarefas) {
            if (t.getEstado() == EstadoTarefa.PENDENTE || t.getEstado() == EstadoTarefa.EM_EXECUCAO) {
                sb.append(t.toDisplayString(gestorRestaurante.getNotaPedido(t.getIdPedido()))).append("\n");
            }
        }
        if (pendentes == 0 && emExecucao == 0) {
            sb.append("✅ Todas as tarefas concluídas!\n");
        }
        return sb.toString();
    }

    public void marcarTarefaComoConcluidaNoPosto(TipoPosto tipoPosto, int idTarefa) {
        int idPedido = gestorRestaurante.marcarTarefaComoConcluidaNoPosto(tipoPosto, idTarefa);
        if (gestorRestaurante.pedidoConcluido(idPedido)) {
            gestorRestaurante.atualizarEstadoPedido(idPedido, EstadoPedido.PRONTO);
        }
    }

    public boolean adicionarDelayATarefa(int idTarefa, int delayMinutos) {
        return gestorRestaurante.adicionarDelayATarefa(idTarefa, delayMinutos);
    }

    // ================================
    // Seeding helpers via facade
    // ================================
    public void adicionarIngredienteAoRestaurante(Ingrediente ingrediente) {
        for(Restaurante r : gestorRestaurante.listarRestaurantes()) {
            gestorRestaurante.selecionarRestaurante(r.getIdRestaurante());
            gestorRestaurante.adicionarIngrediente(ingrediente);
        }
    }

    public void registarPropostaNoRestaurante(Proposta proposta) {
        for(Restaurante r : gestorRestaurante.listarRestaurantes()) {
            gestorRestaurante.selecionarRestaurante(r.getIdRestaurante());
            gestorRestaurante.registarProposta(proposta);
        }
    }

    // ================================
    // Indicadores (para COO/Gerente)
    // ================================

    /** Tempo médio de atendimento (min) agregado em toda a cadeia */
    public double tempoMedioAtendimentoTodos() {
        return gestorRestaurante.tempoMedioAtendimentoTodos();
    }

    /** Tempo médio de atendimento (min) de um restaurante específico */
    public double tempoMedioAtendimentoRestaurante(int idRestaurante) {
        return gestorRestaurante.tempoMedioAtendimentoRestaurante(idRestaurante);
    }

    public double faturacaoTotalTodos(){
        return gestorRestaurante.getFaturacaoTotal();
    }

    public double faturacaoTotalRestaurante(int idRestaurante){
        return gestorRestaurante.getFaturacaoTotalRestaurante(idRestaurante);
    }
}
