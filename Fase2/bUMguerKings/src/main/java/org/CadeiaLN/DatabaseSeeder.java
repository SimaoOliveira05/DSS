package org.CadeiaLN;

import org.CadeiaLN.SubSistemaRestaurante.SubSistemaPedidos.*;
import org.CadeiaLN.SubSistemaRestaurante.SubSistemaTarefas.TipoPosto;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Classe responsável por popular a base de dados com dados de teste
 */
public class DatabaseSeeder {
    
    /**
     * Popula o restaurante com ingredientes, produtos e menus de teste
     * @param facade A facade do sistema
     */
    public static void seedDatabase(CadeiaFacade facade) {
        // Verificar se já existem propostas (BD já foi populada)
        if (!facade.getPropostasDisponiveis().isEmpty()) {
            System.out.println("ℹ️ Base de dados já contém dados. A saltar seed...");
            return;
        }
        
    // Criar ingredientes básicos (posto + tempo estimado)
    Ingrediente pao = new Ingrediente("Pão de Hambúrguer", Arrays.asList("Glúten"), TipoPosto.MONTAGEM, 1);
    Ingrediente carne = new Ingrediente("Carne de Vaca", new ArrayList<>(), TipoPosto.GRILL, 8);
    Ingrediente queijo = new Ingrediente("Queijo Cheddar", Arrays.asList("Lactose"), TipoPosto.MONTAGEM, 1);
    Ingrediente alface = new Ingrediente("Alface", new ArrayList<>(), TipoPosto.MONTAGEM, 1);
    Ingrediente tomate = new Ingrediente("Tomate", new ArrayList<>(), TipoPosto.MONTAGEM, 1);
    Ingrediente cebola = new Ingrediente("Cebola", new ArrayList<>(), TipoPosto.MONTAGEM, 1);
    Ingrediente pickles = new Ingrediente("Pickles", new ArrayList<>(), TipoPosto.MONTAGEM, 1);
    Ingrediente bacon = new Ingrediente("Bacon", new ArrayList<>(), TipoPosto.GRILL, 5);
    Ingrediente molhoEspecial = new Ingrediente("Molho Especial", Arrays.asList("Ovo"), TipoPosto.MONTAGEM, 1);
    Ingrediente ketchup = new Ingrediente("Ketchup", new ArrayList<>(), TipoPosto.MONTAGEM, 1);
    Ingrediente mostarda = new Ingrediente("Mostarda", new ArrayList<>(), TipoPosto.MONTAGEM, 1);
    Ingrediente maionese = new Ingrediente("Maionese", Arrays.asList("Ovo"), TipoPosto.MONTAGEM, 1);

    // Ingredientes para acompanhamentos (simplificado)
    Ingrediente sal = new Ingrediente("Sal", new ArrayList<>(), TipoPosto.MONTAGEM, 1);
    // Componentes base (não removíveis) para gerar tarefas claras
    Ingrediente batatasBase = new Ingrediente("Batatas Fritas", new ArrayList<>(), TipoPosto.FRITURA, 6, false);
    Ingrediente cocaBase = new Ingrediente("Coca-Cola", new ArrayList<>(), TipoPosto.BEBIDAS, 2, false);
    Ingrediente sumoBase = new Ingrediente("Sumo de Laranja", new ArrayList<>(), TipoPosto.BEBIDAS, 2, false);
    Ingrediente aguaBase = new Ingrediente("Água Mineral", new ArrayList<>(), TipoPosto.BEBIDAS, 1, false);
        

        
    // Adicionar ingredientes ao restaurante
    facade.adicionarIngredienteAoRestaurante(pao);
    facade.adicionarIngredienteAoRestaurante(carne);
    facade.adicionarIngredienteAoRestaurante(queijo);
    facade.adicionarIngredienteAoRestaurante(alface);
    facade.adicionarIngredienteAoRestaurante(tomate);
    facade.adicionarIngredienteAoRestaurante(cebola);
    facade.adicionarIngredienteAoRestaurante(pickles);
    facade.adicionarIngredienteAoRestaurante(bacon);
    facade.adicionarIngredienteAoRestaurante(molhoEspecial);
    facade.adicionarIngredienteAoRestaurante(ketchup);
    facade.adicionarIngredienteAoRestaurante(mostarda);
    facade.adicionarIngredienteAoRestaurante(maionese);
    facade.adicionarIngredienteAoRestaurante(sal);
    facade.adicionarIngredienteAoRestaurante(batatasBase);
    facade.adicionarIngredienteAoRestaurante(cocaBase);
    facade.adicionarIngredienteAoRestaurante(sumoBase);
    facade.adicionarIngredienteAoRestaurante(aguaBase);
        
    // Templates deixam de ser necessários: tarefas derivam dos ingredientes
        
        // Criar produtos - Hambúrguers
        Produto hamburgerClassico = new Produto(
            "Hambúrguer Clássico",
            Arrays.asList(pao, carne, queijo, alface, tomate, ketchup, mostarda),
            5.99,
            Arrays.asList(bacon, queijo, pickles, cebola, molhoEspecial, maionese)
        );
        
        Produto cheeseburger = new Produto(
            "Cheeseburger",
            Arrays.asList(pao, carne, queijo, queijo, pickles, cebola, ketchup, mostarda),
            6.49,
            Arrays.asList(bacon, queijo, alface, tomate, molhoEspecial, maionese)
        );
        
        Produto baconBurger = new Produto(
            "Bacon Burger",
            Arrays.asList(pao, carne, queijo, bacon, alface, tomate, molhoEspecial),
            7.99,
            Arrays.asList(bacon, queijo, pickles, cebola, ketchup, mostarda)
        );
        
        Produto burgerEspecial = new Produto(
            "BUMguer Especial",
            Arrays.asList(pao, carne, carne, queijo, bacon, alface, tomate, cebola, pickles, molhoEspecial),
            9.99,
            Arrays.asList(bacon, queijo, carne, maionese, ketchup, mostarda)
        );
        
        // Criar produtos - Acompanhamentos
        Produto batatasFritas = new Produto(
            "Batatas Fritas",
            Arrays.asList(batatasBase),
            2.99,
            Arrays.asList(sal, ketchup, maionese)
        );
        
        Produto batatasFritasGrandes = new Produto(
            "Batatas Fritas Grandes",
            Arrays.asList(batatasBase),
            3.99,
            Arrays.asList(sal, ketchup, maionese)
        );
        
        // Criar produtos - Bebidas
        Produto cocaCola = new Produto(
            "Coca-Cola",
            Arrays.asList(cocaBase),
            2.49,
            new ArrayList<>()
        );
        
        Produto sumoLaranja = new Produto(
            "Sumo de Laranja",
            Arrays.asList(sumoBase),
            2.49,
            new ArrayList<>()
        );
        
        Produto aguaMineral = new Produto(
            "Água Mineral",
            Arrays.asList(aguaBase),
            1.49,
            new ArrayList<>()
        );
        
        // Adicionar produtos ao gestor
    facade.registarPropostaNoRestaurante(hamburgerClassico);
    facade.registarPropostaNoRestaurante(cheeseburger);
    facade.registarPropostaNoRestaurante(baconBurger);
    facade.registarPropostaNoRestaurante(burgerEspecial);
    facade.registarPropostaNoRestaurante(batatasFritas);
    facade.registarPropostaNoRestaurante(batatasFritasGrandes);
    facade.registarPropostaNoRestaurante(cocaCola);
    facade.registarPropostaNoRestaurante(sumoLaranja);
    facade.registarPropostaNoRestaurante(aguaMineral);
        
        // Criar menus (combos)
        Menu menuClassico = new Menu(
            "Menu Clássico",
            Arrays.asList(hamburgerClassico, batatasFritas, cocaCola),
            9.99
        );
        
        Menu menuBacon = new Menu(
            "Menu Bacon",
            Arrays.asList(baconBurger, batatasFritasGrandes, cocaCola),
            12.99
        );
        
        Menu menuEspecial = new Menu(
            "Menu BUMguer Especial",
            Arrays.asList(burgerEspecial, batatasFritasGrandes, cocaCola),
            14.99
        );
        
        Menu menuCheese = new Menu(
            "Menu Cheese",
            Arrays.asList(cheeseburger, batatasFritas, sumoLaranja),
            10.49
        );
        
        // Adicionar menus ao gestor
    facade.registarPropostaNoRestaurante(menuClassico);
    facade.registarPropostaNoRestaurante(menuBacon);
    facade.registarPropostaNoRestaurante(menuEspecial);
    facade.registarPropostaNoRestaurante(menuCheese);
        
        System.out.println("✅ Base de dados populada com sucesso!");
        System.out.println("📦 " + facade.getPropostasDisponiveis().size() + " propostas criadas");
        // Não há API pública para contar ingredientes, apenas populamos silenciosamente.
    }
}
