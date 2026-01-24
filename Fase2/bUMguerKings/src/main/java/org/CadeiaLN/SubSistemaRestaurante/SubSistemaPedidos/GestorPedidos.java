package org.CadeiaLN.SubSistemaRestaurante.SubSistemaPedidos;

import java.util.List;
import java.util.ArrayList;
import java.time.LocalTime;
import java.time.Duration;

import org.CadeiaDL.PedidoDAO;
import org.CadeiaDL.PropostaDAO;
import org.CadeiaDL.IngredienteDAO;
import org.CadeiaDL.TarefaDAO;
import org.CadeiaLN.SubSistemaRestaurante.SubSistemaTarefas.Tarefa;
import org.CadeiaLN.SubSistemaRestaurante.SubSistemaTarefas.EstadoTarefa;

public class GestorPedidos implements IGestPedidos {
    private final int idRestaurante;
    private PedidoDAO pedidos;
    private PropostaDAO propostas;
    private IngredienteDAO ingredientesRestaurante;


    public GestorPedidos(int idRestaurante) {
        this.idRestaurante = idRestaurante;
        this.pedidos = new PedidoDAO(idRestaurante);
        this.propostas = new PropostaDAO(idRestaurante);
        this.ingredientesRestaurante = new IngredienteDAO(idRestaurante);
    }

    public String listarPersonalizacoesPossiveis(String nomeProposta) {
        Proposta proposta = propostas.get(nomeProposta);
        if (proposta == null) {
            return "❌ Proposta não encontrada.";
        }
        return proposta.toPersonalizacaoString();
    }
    
    /**
     * Verifica se a proposta é um Menu
     */
    public boolean isMenu(String nomeProposta) {
        Proposta p = propostas.get(nomeProposta);
        return p instanceof Menu;
    }
    
    /**
     * Obtém os produtos de um menu
     */
    public List<Produto> getProdutosDoMenu(String nomeMenu) {
        Proposta p = propostas.get(nomeMenu);
        if (p instanceof Menu) {
            return ((Menu) p).getProdutos();
        }
        return new ArrayList<>();
    }


    public int iniciarNovoPedido() {
        Pedido novoPedido = new Pedido(new java.util.ArrayList<>(), "", EstadoPedido.EM_CONSTRUCAO);
        // Usar o método específico do DAO para criar pedido com AUTO_INCREMENT
        int idPedido = ((PedidoDAO) pedidos).criarNovoPedido(novoPedido);
        return idPedido;
    }


    public boolean adicionarPropostaAoPedido(int idPedido, String nomeProposta) {
        Pedido pedido = pedidos.get(idPedido);
        if (pedido == null || pedido.getEstado() != EstadoPedido.EM_CONSTRUCAO) {
            return false; // Pedido não existe ou não está em construção
        }
        Proposta proposta = propostas.get(nomeProposta);
        if (proposta == null) {
            return false; // Proposta não existe
        }
        pedido.adicionarProposta(proposta);
        pedidos.put(idPedido, pedido); // Persistir alterações
        return true;
    }

    public boolean adicionarPropostaPersonalizadaAoPedido(int idPedido, 
                                                        String nomeProposta,
                                                        List<String> ingredientesARemover,
                                                        List<String> ingredientesAAdicionar) {
        Pedido pedido = pedidos.get(idPedido);
        if (pedido == null || pedido.getEstado() != EstadoPedido.EM_CONSTRUCAO) {
            return false; // Pedido não existe ou não está em construção
        }
        Proposta personalizado = criarProdutoPersonalizado(nomeProposta, ingredientesARemover, ingredientesAAdicionar);
        if (personalizado == null) {
            return false;
        }
        pedido.adicionarProposta(personalizado);
        pedidos.put(idPedido, pedido); // Persistir alterações
        return true;
    }

    /**
     * Cria um novo Produto personalizado a partir de um produto base.
     * Nome final: "<nomeBase> Personalizado"
     * Regra de preço: +1.00 por ingrediente adicionado; remoções não alteram preço.
     */
    public Proposta criarProdutoPersonalizado(String nomeProposta,
                                              List<String> ingredientesARemover,
                                              List<String> ingredientesAAdicionar) {
        Proposta proposta = propostas.get(nomeProposta);
        if (proposta == null || !(proposta instanceof Produto)) {
            return null;
        }
        Produto base = (Produto) proposta;
        List<Ingrediente> lista = new ArrayList<>(base.getIngredientes());

        // Remover
        if (ingredientesARemover != null) {
            for (String nomeIng : ingredientesARemover) {
                lista.removeIf(i -> i.getNome().equalsIgnoreCase(nomeIng));
            }
        }

        double novoPreco = base.getPreco();
        // Adicionar
        if (ingredientesAAdicionar != null) {
            for (String nomeIng : ingredientesAAdicionar) {
                Ingrediente ing = ingredientesRestaurante.get(nomeIng);
                if (ing != null && lista.stream().noneMatch(i -> i.getNome().equalsIgnoreCase(nomeIng))) {
                    lista.add(ing);
                    novoPreco += 1.00; // regra simples
                }
            }
        }

        // Copiar lista de adicionáveis do base (pode continuar para futuras personalizações)
        Produto personalizado = new Produto(base.getNome() + " Personalizado", lista, novoPreco, base.getIngredientesAdicionaveis());
        return personalizado;
    }


    public boolean adicionarNotaAoPedido(int idPedido, String nota) {
        Pedido pedido = pedidos.get(idPedido);
        if (pedido == null) {
            return false;
        }
        pedido.setNota(nota);
        pedidos.put(idPedido, pedido); // Persistir alterações
        return true;
    }


    public boolean finalizarPedido(int idPedido, LocalTime eta) {
        Pedido pedido = pedidos.get(idPedido);
        if (pedido == null) {
            return false; // pedido inexistente
        }
        if (pedido.getPropostas().isEmpty()) {
            return false; // Não pode finalizar pedido vazio
        }
        pedido.setEstado(EstadoPedido.PENDENTE);
        pedido.setEta(eta);
        // timestamp real de finalização
        pedido.setFinalizadoEm(LocalTime.now());
        pedidos.put(idPedido, pedido); // Persistir alterações
        return true;
    }

    public Pedido getPedido(int idPedido) {
        return pedidos.get(idPedido);
        
    }

    public void atualizarPedido(Pedido pedido) {
        pedidos.put(pedido.getIdPedido(), pedido);
    }

    public void removerPedido(int idPedido) {
        pedidos.remove(idPedido);
    }

    public boolean cancelarPedido(int idPedido) {
        Pedido pedido = pedidos.get(idPedido);
        if (pedido == null || pedido.getEstado() != EstadoPedido.EM_CONSTRUCAO) {
            return false;
        }
        pedidos.remove(idPedido);
        return true;
    }

    public List<Proposta> getListaPropostas() {
        return new ArrayList<>(propostas.values());
    }
    


    public void atualizarEstadoPedido(int idPedido, EstadoPedido novoEstado) {
        Pedido pedido = pedidos.get(idPedido);
        if (pedido != null) {
            pedido.setEstado(novoEstado);
            if (novoEstado == EstadoPedido.ENTREGUE) {
                pedido.setEntregueEm(LocalTime.now());
            }
            pedidos.put(idPedido, pedido); // Persistir alterações
        }
    }

    public String listarTodosPedidos() {
        if (pedidos.isEmpty()) {
            return "📭 Não há pedidos registados.";
        }
        
        StringBuilder sb = new StringBuilder();
        for (Pedido pedido : pedidos.values()) {
            String iconeEstado = obterIconeEstado(pedido.getEstado());
            
            sb.append("🆔 Pedido #").append(pedido.getIdPedido()).append("\n");
            sb.append("   ").append(iconeEstado).append(" Estado: ").append(pedido.getEstado()).append("\n");
            sb.append("   💶 Total: €").append(String.format("%.2f", pedido.getPrecoTotal())).append("\n");
            
            if (!pedido.getPropostas().isEmpty()) {
                sb.append("   📦 Itens: ").append(pedido.getPropostas().size()).append("\n");
            }
            if (pedido.getEta() != null) {
                LocalTime agora = LocalTime.now();
                boolean atrasado = pedido.getEta().isBefore(agora);
                sb.append("   ⏱️ ETA: ").append(pedido.getEta().toString());
                if (atrasado) {
                    sb.append("  ⚠️ (atrasado)");
                }
                sb.append("\n");
            }
            if (pedido.getNota() != null && !pedido.getNota().isEmpty()) {
                sb.append("   💬 Nota: ").append(pedido.getNota()).append("\n");
            }
            
            sb.append("\n");
        }
        return sb.toString();
    }

    public String listarPedidosEstado(EstadoPedido estado) {
        StringBuilder sb = new StringBuilder();
        boolean encontrouPedidos = false;
        
        for (Pedido pedido : pedidos.values()) {
            if (pedido.getEstado() == estado) {
                encontrouPedidos = true;
                String iconeEstado = obterIconeEstado(pedido.getEstado());
                
                sb.append("🆔 Pedido #").append(pedido.getIdPedido());
                sb.append(" | ").append(iconeEstado).append(" ").append(estado);
                sb.append(" | 💶 €").append(String.format("%.2f", pedido.getPrecoTotal()));
                sb.append("\n");
            }
        }
        
        if (!encontrouPedidos) {
            sb.append("📭 Não há pedidos no estado: ").append(estado);
        }
        
        return sb.toString();
    }
    
    /**
     * Retorna um ícone apropriado para cada estado de pedido
     */
    private String obterIconeEstado(EstadoPedido estado) {
        switch (estado) {
            case EM_CONSTRUCAO:
                return "🔨";
            case PENDENTE:
                return "⏳";
            case PRONTO:
                return "✅";
            case ENTREGUE:
                return "📦";
            default:
                return "❓";
        }
    }

    public void adicionarIngrediente(Ingrediente ingrediente) {
        ingredientesRestaurante.put(ingrediente.getNome(), ingrediente);
    }
    

    public void adicionarProposta(Proposta proposta) {
        propostas.put(proposta.getNome(), proposta);
    }
    

    
    public boolean pedidoConcluido(int idPedido) {
        Pedido pedido = pedidos.get(idPedido);
        if (pedido == null) {
            return false;
        }
        
        // Consultar diretamente a base de dados para obter o estado atual das tarefas
        TarefaDAO tarefaDAO = new TarefaDAO(idRestaurante);
        List<Tarefa> tarefas = tarefaDAO.getTarefasPorPedido(idPedido);
        
        // Se não houver tarefas, o pedido não pode estar concluído
        if (tarefas.isEmpty()) {
            return false;
        }
        
        // Verificar se todas as tarefas estão concluídas
        for (Tarefa tarefa : tarefas) {
            if (tarefa.getEstado() != EstadoTarefa.CONCLUIDA) {
                return false;
            }
        }
        return true;
    }

    // ================================
    // Indicadores
    // ================================



    /**
     * Retorna a média do tempo de atendimento em minutos.
     * Se não houver pedidos entregues, retorna 0.0.
     */
    public double atendimentoSumAndCountMinutes() {
        double sum = 0.0;
        int count = 0;
        
        for (Pedido p : pedidos.values()) {
            if (p.getFinalizadoEm() != null && p.getEntregueEm() != null) {
                long minutes = Duration.between(p.getFinalizadoEm(), p.getEntregueEm()).toMinutes();
                if (minutes >= 0) {
                    sum += minutes;
                    count++;
                }
            }
        }
        // Evitar divisão por zero que resulta em NaN
        return count > 0 ? sum / count : 0.0;
    }
}

