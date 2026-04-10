package br.com.alura.loja.dao;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import br.com.alura.loja.modelo.Produto;

public class ProdutoDao {

	private EntityManager em;

	public ProdutoDao(EntityManager em) {
		this.em = em;
	}

	public void cadastrar(Produto produto) {
		this.em.persist(produto);
	}

	public void atualizar(Produto produto) {
		this.em.merge(produto);
	}

	public void remover(Produto produto) {
		produto = em.merge(produto);
		this.em.remove(produto);
	}
	
	public Produto buscarPorId(Long id) {
		return em.find(Produto.class, id);
	}
	
	public List<Produto> buscarTodos() {
		String jpql = "SELECT p FROM Produto p";
		return em.createQuery(jpql, Produto.class).getResultList();
	}
	
	public List<Produto> buscarPorNome(String nome) {
		String jpql = "SELECT p FROM Produto p WHERE p.nome = :nome";
		return em.createQuery(jpql, Produto.class)
				.setParameter("nome", nome)
				.getResultList();
	}
	
	public List<Produto> buscarPorNomeDaCategoria(String nome) {
			return em.createNamedQuery("Produto.produtosPorCategoria", Produto.class)
				.setParameter("nome", nome)
				.getResultList();
	}
	
	public BigDecimal buscarPrecoDoProdutoComNome(String nome) {
		String jpql = "SELECT p.preco FROM Produto p WHERE p.nome = :nome";
		return em.createQuery(jpql, BigDecimal.class)
				.setParameter("nome", nome)
				.getSingleResult();
	}

	// Monta uma consulta JPQL dinâmica para buscar produtos.
	// A ideia é começar com uma cláusula base "WHERE 1=1"
	// e ir adicionando filtros conforme os parâmetros recebidos.
	//
	// - Se 'nome' não for nulo nem vazio, adiciona filtro por nome.
	// - Se 'preco' não for nulo, adiciona filtro por preço.
	// - Se 'dataCadastro' não for nulo, adiciona filtro por data de cadastro.
	//
	// Depois de montar a string JPQL, cria-se um TypedQuery
	// e são setados apenas os parâmetros que realmente foram informados.
	//
	// Vantagem: flexibilidade para aplicar filtros opcionais
	// sem precisar escrever várias queries diferentes.
	// Atenção: é importante concatenar corretamente as cláusulas
	// (no exemplo, faltou manter o "SELECT p FROM Produto p WHERE 1=1"
	// e ir adicionando os "AND ..." em vez de sobrescrever a string).
	//
	// Resultado: retorna a lista de produtos que atendem aos filtros
	// passados pelo usuário.
	public List<Produto> buscarPorParametros(String nome,
											 BigDecimal preco, LocalDate dataCadastro) {
		String jpql = "SELECT p FROM Produto p WHERE 1=1 ";
		if (nome != null && !nome.trim().isEmpty()) {
			jpql = " AND p.nome = :nome ";
		}
		if (preco != null) {
			jpql = " AND p.preco = :preco ";
		}
		if (dataCadastro != null) {
			jpql = " AND p.dataCadastro = :dataCadastro ";
		}
		TypedQuery<Produto> query = em.createQuery(jpql, Produto.class);
		if (nome != null && !nome.trim().isEmpty()) {
			query.setParameter("nome", nome);
		}
		if (preco != null) {
			query.setParameter("preco", preco);
		}
		if (dataCadastro != null) {
			query.setParameter("dataCadastro", dataCadastro);
		}

		return query.getResultList();
	}

	/**
	 * Busca produtos aplicando filtros opcionais usando a API Criteria.
	 *
	 * - O CriteriaBuilder é utilizado para construir a query de forma programática,
	 *   evitando concatenar strings manualmente como no JPQL dinâmico.
	 *
	 * - A consulta parte da entidade Produto (Root<Produto>).
	 *
	 * - Um objeto Predicate é criado para acumular os filtros.
	 *   Cada parâmetro (nome, preço, dataCadastro) é verificado:
	 *     -> Se não for nulo/vazio, adiciona um filtro correspondente.
	 *
	 * - O método builder.and() vai compondo os critérios de forma incremental,
	 *   garantindo que apenas os filtros informados sejam aplicados.
	 *
	 * - Por fim, a query é executada e retorna a lista de produtos
	 *   que atendem aos parâmetros fornecidos.
	 *
	 * Vantagens do Criteria:
	 *   - Evita problemas de concatenação de strings em JPQL.
	 *   - Permite construir consultas dinâmicas de forma mais segura e legível.
	 *   - Facilita manutenção e refatoração, já que é fortemente tipado.
	 */
	/**
	 * O trecho `query.from(Produto.class)` define a "raiz" da consulta.
	 *
	 * - Root<Produto> from = query.from(Produto.class);
	 *
	 * O objeto Root representa a entidade principal que será consultada.
	 * É como se fosse o "alias" da tabela na consulta JPQL:
	 *   SELECT p FROM Produto p
	 *
	 * A partir desse Root, conseguimos acessar os atributos da entidade
	 * (como nome, preço, dataCadastro) para construir os filtros.
	 *
	 * Em outras palavras:
	 *   - `Produto.class` indica qual entidade será usada como base.
	 *   - `from` é o ponto de partida da query, equivalente ao "p" no JPQL.
	 *   - Todos os Predicates (condições) se referem a esse Root.
	 *
	 * Exemplo prático:
	 *   builder.equal(from.get("nome"), nome)
	 *   -> traduzido seria algo como "p.nome = :nome"
	 *
	 * Assim, o Criteria API mantém a tipagem forte e evita erros de sintaxe,
	 * já que não estamos manipulando strings diretamente.
	 */

	public List<Produto> buscarPorParametrosComCriteria(String nome, BigDecimal preco, LocalDate dataCadastro) {

		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Produto> query = builder.createQuery(Produto.class);
		Root<Produto> from = query.from(Produto.class);

		Predicate filtros = builder.and();
		if (nome != null && !nome.trim().isEmpty()) {
			filtros = builder.and(filtros, builder.equal(from.get("nome"), nome));
		}
		if (preco != null) {
			filtros = builder.and(filtros, builder.equal(from.get("preco"), preco));
		}
		if (dataCadastro != null) {
			filtros = builder.and(filtros, builder.equal(from.get("dataCadastro"), dataCadastro));
		}
		query.where(filtros);

		return em.createQuery(query).getResultList();
	}

}
