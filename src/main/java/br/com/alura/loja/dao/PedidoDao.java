package br.com.alura.loja.dao;

import br.com.alura.loja.dto.RelatorioDeVendasDTO;
import br.com.alura.loja.modelo.Pedido;
import br.com.alura.loja.modelo.Produto;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.List;

public class PedidoDao {

	private EntityManager em;

	public PedidoDao(EntityManager em) {
		this.em = em;
	}

	public void cadastrar(Pedido pedido) {
		this.em.persist(pedido);
	}

	public BigDecimal valorTotalVendido(){
		String jpql = "SELECT SUM(p.valorTotal) FROM Pedido p";
		 return em.createQuery(jpql, BigDecimal.class).getSingleResult();
	}

	public List<RelatorioDeVendasDTO> relatorioDeVendas(){
		/**
		 * Relatório de vendas via JPQL.
		 *
		 * Monta uma consulta JPQL que:
		 *  - Faz JOIN entre Pedido, seus itens e os respectivos produtos
		 *  - Agrupa os resultados pelo nome do produto
		 *  - Calcula a soma das quantidades vendidas
		 *  - Obtém a data mais recente de venda (MAX)
		 *  - Ordena pela quantidade vendida em ordem decrescente
		 *
		 * A consulta já instancia diretamente a classe RelatorioDeVendasDTO
		 * dentro do JPQL, convertendo os dados do banco em objetos prontos
		 * para uso na aplicação sem precisar de mapeamento manual.
		 *
		 * Resultado: lista de DTOs com nome do produto, total vendido
		 * e última data de venda.
		 */
String jpql = "SELECT new br.com.alura.loja.dto.RelatorioDeVendasDTO("
		+ "produto.nome, "
		+ "SUM(item.quantidade), "
		+ "MAX(pedido.data))"
		+ "FROM Pedido pedido "
		+ "JOIN pedido.itens item "
		+ "JOIN item.produto produto "
		+ "GROUP BY produto.nome "
		+ "ORDER BY item.quantidade DESC";
return em.createQuery(jpql, RelatorioDeVendasDTO.class).getResultList();
	}

}
