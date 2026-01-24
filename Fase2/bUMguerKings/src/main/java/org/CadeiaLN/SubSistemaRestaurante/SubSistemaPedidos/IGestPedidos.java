package org.CadeiaLN.SubSistemaRestaurante.SubSistemaPedidos;

import java.time.LocalTime;
import java.util.List;

/**
 * Interface para as operações de gestão de pedidos (GestorPedidos).
 */
public interface IGestPedidos {
    String listarPersonalizacoesPossiveis(String nomeProposta);
    boolean isMenu(String nomeProposta);
    List<Produto> getProdutosDoMenu(String nomeMenu);
    int iniciarNovoPedido();
    boolean adicionarPropostaAoPedido(int idPedido, String nomeProposta);
    boolean adicionarPropostaPersonalizadaAoPedido(int idPedido, String nomeProposta, List<String> ingredientesARemover, List<String> ingredientesAAdicionar);
    Proposta criarProdutoPersonalizado(String nomeProposta, List<String> ingredientesARemover, List<String> ingredientesAAdicionar);
    boolean adicionarNotaAoPedido(int idPedido, String nota);
    boolean finalizarPedido(int idPedido, LocalTime eta);
    Pedido getPedido(int idPedido);
    void atualizarPedido(Pedido pedido);
    void removerPedido(int idPedido);
    boolean cancelarPedido(int idPedido);
    List<Proposta> getListaPropostas();
    void atualizarEstadoPedido(int idPedido, org.CadeiaLN.SubSistemaRestaurante.SubSistemaPedidos.EstadoPedido novoEstado);
    String listarTodosPedidos();
    String listarPedidosEstado(org.CadeiaLN.SubSistemaRestaurante.SubSistemaPedidos.EstadoPedido estado);
    void adicionarIngrediente(Ingrediente ingrediente);
    void adicionarProposta(Proposta proposta);
    boolean pedidoConcluido(int idPedido);
    double atendimentoSumAndCountMinutes();
}
