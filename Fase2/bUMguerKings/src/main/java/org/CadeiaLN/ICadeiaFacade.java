package org.CadeiaLN;

import java.util.List;

import org.CadeiaLN.SubSistemaRestaurante.Restaurante;
import org.CadeiaLN.SubSistemaRestaurante.SubSistemaPedidos.Ingrediente;
import org.CadeiaLN.SubSistemaRestaurante.SubSistemaPedidos.Proposta;
import org.CadeiaLN.SubSistemaRestaurante.SubSistemaPedidos.Produto;
import org.CadeiaLN.SubSistemaRestaurante.SubSistemaTarefas.TipoPosto;

public interface ICadeiaFacade {
    // Restaurantes
    int criarRestaurante(String nome, String localizacao);
    boolean selecionarRestaurante(int idRestaurante);
    List<Restaurante> listarRestaurantes();
    String getRestauranteAtualNome();

    // Pedidos
    int iniciarNovoPedido();
    boolean adicionarPropostaAoPedido(int idPedido, String nomeProposta);
    boolean adicionarPropostaPersonalizadaAoPedido(int idPedido, String nomeProposta, List<String> ingredientesRemovidos, List<String> ingredientesAdicionados);
    void adicionarNotaAoPedido(int idPedido, String nota);
    boolean finalizarPedido(int idPedido);
    void marcarPedidoComoEntregue(int idPedido);
    List<Produto> getProdutosDoMenu(String nomeMenu);
    List<String> getPropostasDisponiveis();
    String listarPersonalizacoesPossiveis(String nomeProposta);
    boolean isMenu(String nomeProposta);
    String listarPedidos();
    String listarPedidosProntos();
    long getEtaPedidoMinutos(int idPedido);

    // Tarefas
    String listarTarefasDoPosto(TipoPosto tipoPosto);
    void marcarTarefaComoConcluidaNoPosto(TipoPosto tipoPosto, int idTarefa);
    boolean adicionarDelayATarefa(int idTarefa, int delayMinutos);

    // Seeding
    void adicionarIngredienteAoRestaurante(Ingrediente ingrediente);
    void registarPropostaNoRestaurante(Proposta proposta);

    // Indicadores
    double tempoMedioAtendimentoTodos();
    double tempoMedioAtendimentoRestaurante(int idRestaurante);
    double faturacaoTotalTodos();
    double faturacaoTotalRestaurante(int idRestaurante);
}
