package org.CadeiaLN;

import java.util.List;

import org.CadeiaLN.SubSistemaRestaurante.Restaurante;
import org.CadeiaLN.SubSistemaRestaurante.SubSistemaPedidos.Ingrediente;
import org.CadeiaLN.SubSistemaRestaurante.SubSistemaPedidos.Proposta;

/**
 * Interface para a gestão de restaurantes (GestorRestaurante).
 */
public interface IGestRestaurantes {
    int criarRestaurante(String nome, String localizacao);
    boolean selecionarRestaurante(int idRestaurante);
    Restaurante getRestauranteAtual();
    Restaurante getRestaurante(int idRestaurante);
    List<Restaurante> listarRestaurantes();

    // Seeding helpers
    void adicionarIngrediente(Ingrediente ingrediente);
    void registarProposta(Proposta proposta);

    // Delegados para pedidos/tarefas - exposição mínima
    int iniciarNovoPedido();
    boolean adicionarPropostaAoPedido(int idPedido, String nomeProposta);
    boolean adicionarPropostaPersonalizadaAoPedido(int idPedido, String nomeProposta, List<String> ingredientesRemovidos, List<String> ingredientesAdicionados);
    void adicionarNotaAoPedido(int idPedido, String nota);
    boolean finalizarPedido(int idPedido);
    void marcarPedidoComoEntregue(int idPedido);
    String listarPedidos();
    String listarPedidosProntos();
    long getEtaPedidoMinutos(int idPedido);
    List<org.CadeiaLN.SubSistemaRestaurante.SubSistemaTarefas.Tarefa> listarTarefasDoPosto(org.CadeiaLN.SubSistemaRestaurante.SubSistemaTarefas.TipoPosto tipoPosto);
    int marcarTarefaComoConcluidaNoPosto(org.CadeiaLN.SubSistemaRestaurante.SubSistemaTarefas.TipoPosto tipoPosto, int idTarefa);
    boolean adicionarDelayATarefa(int idTarefa, int delayMinutos);

    // Indicadores
    double tempoMedioAtendimentoTodos();
    double tempoMedioAtendimentoRestaurante(int idRestaurante);
    double getFaturacaoTotal();
    double getFaturacaoTotalRestaurante(int idRestaurante);
}
