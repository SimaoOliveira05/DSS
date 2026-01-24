# bUMger Kings

> Sistema integrado de gestão para uma cadeia de restaurantes de fast-food, desde o pedido do cliente até à confecão e entrega.

## Contexto Académico
| | |
| :--- | :--- |
| **Cadeira** | Desenvolvimento de Sistemas de Software |
| **Curso** | Licenciatura em Engenharia Informática |
| **Ano Letivo** | 2025 / 2026 |
| **Nota Final** | **XX** / 20 |

## Sobre o Projeto
Este projeto consiste num sistema de gestão para uma cadeia de restaurantes (bUMger Kings), desenvolvido para automatizar o funcionamento dos restaurantes e fornecer indicadores de gestão. O objetivo principal era implementar uma arquitetura em camadas (Lógica de Negócio, Dados, Interface) capaz de suportar todo o ciclo de vida de um pedido, bem como a gestão operacional.

O sistema permite que clientes façam pedidos em quiosques digitais, que são depois decompostos em tarefas específicas para diferentes postos de cozinha (grelha, fritura, montagem, etc.). Simultaneamente, oferece ferramentas para gestores e administradores (COO) monitorizarem stocks, tempos de espera e faturação.

### Funcionalidades Principais
* **Gestão de Pedidos:** Criação de pedidos personalizados, gestão de pagamentos e acompanhamento de estados (Em Preparação, Pronto, Entregue).
* **Sistema de Produção (Cozinha):** Decomposição automática de pedidos em tarefas distribuídas por postos de trabalho; cálculo de tempos estimados (ETA) dinâmicos.
* **Gestão e Administração:** Monitorização de indicadores de desempenho (KPIs) por restaurante ou cadeia completa, gestão de stocks e envio de mensagens para funcionários.

## Tecnologias
* **Linguagem:** Java (JDK 21)
* **Build Tool:** Maven
* **Base de Dados:** MySQL (suportado via JDBC/Connector)
* **Conceitos:** Arquitetura em Camadas, Padrão DAO, Singleton, Facade

## Como Correr
Para compilar e executar o projeto, certifica-te que tens o Java e o Maven instalados.

```bash
# Compilar o projeto
mvn clean install

# Executar a aplicação (exemplo via plugin exec ou jar gerado)
java -cp target/bUMguerKings-1.0-SNAPSHOT-jar-with-dependencies.jar org.App
