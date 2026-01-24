package org.CadeiaLN.SubSistemaRestaurante;

import org.CadeiaLN.IGestRestaurantes;

import java.util.List;
import java.util.ArrayList;
import java.time.Duration;
import java.time.LocalTime;

import org.CadeiaLN.SubSistemaRestaurante.SubSistemaPedidos.*;
import org.CadeiaLN.SubSistemaRestaurante.SubSistemaTarefas.*;
import org.CadeiaDL.RestauranteDAO;

/**
 * Gestão de múltiplos restaurantes na cadeia.
 * Mantém o restaurante atual (contexto) e permite criar/listar/selecionar.
 */
public class GestorRestaurante implements IGestRestaurantes {
	private final RestauranteDAO restaurantes;
	private Restaurante restauranteAtual;

	public GestorRestaurante() {
		this.restaurantes = RestauranteDAO.getInstance();
		this.restauranteAtual = null;
		
		// Se já existirem restaurantes na BD, selecionar o primeiro como atual
		if (!restaurantes.isEmpty()) {
			this.restauranteAtual = restaurantes.values().iterator().next();
		}
	}

	/** Cria um novo restaurante e devolve o seu ID */
	public int criarRestaurante(String nome, String localizacao) {
		Restaurante r = new Restaurante(nome, localizacao);
		// Usar o método específico do DAO para inserir e obter o ID gerado
		int idGerado = ((RestauranteDAO) restaurantes).insert(r);
		// Recarregar o restaurante com o ID correto
		Restaurante restauranteCriado = restaurantes.get(idGerado);
		if (restauranteAtual == null) {
			restauranteAtual = restauranteCriado;
		}
		return idGerado;
	}

	/** Seleciona restaurante atual pelo ID */
	public boolean selecionarRestaurante(int idRestaurante) {
		if (restaurantes.containsKey(idRestaurante)) {
			restauranteAtual = restaurantes.get(idRestaurante);
			return true;
		}
		return false;
	}

	/** Obtém o restaurante atual, ou null se não definido */
	public Restaurante getRestauranteAtual() {
		return restauranteAtual;
	}

	/** Obtém restaurante por ID */
	public Restaurante getRestaurante(int idRestaurante) {
		return restaurantes.get(idRestaurante);
	}

	/** Lista todos os restaurantes */
	public List<Restaurante> listarRestaurantes() {
		return new ArrayList<>(restaurantes.values());
	}

	// ══════════════════════════════════════════════════════════
	// Operações delegadas ao restaurante atual (encapsulamento)
	// ══════════════════════════════════════════════════════════

	// Seeding
	public void adicionarIngrediente(Ingrediente ingrediente) { restauranteAtual.adicionarIngrediente(ingrediente); }
	public void registarProposta(Proposta proposta) { restauranteAtual.adicionarProposta(proposta); }

	// Propostas / Menus
	public List<Proposta> getListaPropostas() { return restauranteAtual.getListaPropostas(); }
	public String listarPersonalizacoesPossiveis(String nomeProposta) { return restauranteAtual.listarPersonalizacoesPossiveis(nomeProposta); }
	public boolean isMenu(String nomeProposta) { return restauranteAtual.isMenu(nomeProposta); }
	public List<Produto> getProdutosDoMenu(String nomeMenu) { return restauranteAtual.getProdutosDoMenu(nomeMenu); }

	// Pedidos
	public int iniciarNovoPedido() { return restauranteAtual.iniciarNovoPedido(); }
	public boolean adicionarPropostaAoPedido(int idPedido, String nomeProposta) { return restauranteAtual.adicionarPropostaAoPedido(idPedido, nomeProposta); }
	public boolean adicionarPropostaPersonalizadaAoPedido(int idPedido, String nomeProposta, List<String> ingredientesRemovidos, List<String> ingredientesAdicionados) {
		return restauranteAtual.adicionarPropostaPersonalizadaAoPedido(idPedido, nomeProposta, ingredientesRemovidos, ingredientesAdicionados);
	}
    
	public void adicionarNotaAoPedido(int idPedido, String nota) { restauranteAtual.adicionarNotaAoPedido(idPedido, nota); }
	
    public boolean finalizarPedido(int idPedido) {
		return restauranteAtual.finalizarPedido(idPedido);
	}

	public void marcarPedidoComoEntregue(int idPedido) { 
		restauranteAtual.atualizarEstadoPedido(idPedido, EstadoPedido.ENTREGUE); 
		restauranteAtual.adicionarFaturacaoPedido(idPedido);
		restaurantes.put(restauranteAtual.getIdRestaurante(), restauranteAtual);
	}

	public String listarPedidos() { return restauranteAtual.listarTodosPedidos(); }
	public String listarPedidosProntos() { return restauranteAtual.listarPedidosEstado(EstadoPedido.PRONTO); }
	public long getEtaPedidoMinutos(int idPedido) {
		LocalTime eta = restauranteAtual.getPedido(idPedido).getEta();
		return Math.max(0, Duration.between(LocalTime.now(), eta).toMinutes());
	}
	public String getNotaPedido(int idPedido) { return restauranteAtual.getNotaPedido(idPedido); }

	// Tarefas
	public List<Tarefa> listarTarefasDoPosto(TipoPosto tipoPosto) { return restauranteAtual.listarTarefasDoPosto(tipoPosto); }
	public int marcarTarefaComoConcluidaNoPosto(TipoPosto tipoPosto, int idTarefa) { return restauranteAtual.marcarTarefaComoConcluidaNoPosto(tipoPosto, idTarefa); }
	public boolean pedidoConcluido(int idPedido) { return restauranteAtual.pedidoConcluido(idPedido); }
	public boolean adicionarDelayATarefa(int idTarefa, int delayMinutos) { return restauranteAtual.adicionarDelayATarefa(idTarefa, delayMinutos); }
	public void atualizarEstadoPedido(int idPedido, EstadoPedido estado) { restauranteAtual.atualizarEstadoPedido(idPedido, estado); }

	// ================================
	// Indicadores
	// ================================


	/** Tempo médio de atendimento (min) de um restaurante específico */
	public double tempoMedioAtendimentoRestaurante(int idRestaurante) {
		Restaurante r = restaurantes.get(idRestaurante);
		return r == null ? 0.0 : r.tempoMedioAtendimentoMinutos();
	}

	/** Tempo médio de atendimento (min) agregado em todos os restaurantes */
	public double tempoMedioAtendimentoTodos() {
		double sum = 0.0;
		for (Restaurante r : restaurantes.values()) {
			double sc = r.tempoMedioAtendimentoMinutos();
			sum += sc;
		}
		return sum;
	}

	public double getFaturacaoTotalRestaurante(int idRestaurante) {
		Restaurante r = restaurantes.get(idRestaurante);
		return r == null ? 0 : r.getFaturacaoTotal();
	}

	public double getFaturacaoTotal() {
		double total = 0;
		for (Restaurante r : restaurantes.values()) {
			total += r.getFaturacaoTotal();
		}
		return total;
	}
}
