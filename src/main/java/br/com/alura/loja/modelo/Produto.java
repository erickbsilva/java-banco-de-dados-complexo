package br.com.alura.loja.modelo;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.persistence.*;

@Entity
@Table(name = "produtos")
@NamedQuery(name = "Produto.produtosPorCategoria",
query = "SELECT p FROM Produto p WHERE p.categoria.id.nome = :nome")
/**
 * @Inheritance(strategy = InheritanceType.SINGLE_TABLE)
 *
 * Essa anotação define como a herança entre entidades será mapeada no banco.
 *
 * - SINGLE_TABLE: todas as classes da hierarquia de herança
 *   (classe pai e subclasses) são armazenadas em uma única tabela.
 *
 * - O JPA cria uma coluna discriminadora (normalmente chamada "DTYPE")
 *   para identificar qual tipo de entidade cada linha representa.
 *
 * Vantagens:
 *   - Simples e eficiente em termos de performance (uma tabela só).
 *   - Consultas mais rápidas, sem necessidade de JOINs.
 *
 * Desvantagens:
 *   - A tabela pode ficar com muitas colunas nulas,
 *     já que cada subclasse pode ter atributos específicos.
 *
 * Em resumo: é uma estratégia prática quando queremos manter
 * todas as entidades da hierarquia em uma única tabela,
 * aceitando a trade-off de possíveis colunas vazias.
 */
//@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
/**
 * @Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
 *
 * Essa estratégia de herança no JPA cria uma tabela separada
 * para cada classe concreta da hierarquia.
 *
 * - Cada subclasse terá sua própria tabela, contendo
 *   tanto os atributos herdados da classe pai quanto os específicos.
 *
 * - Não existe uma tabela única para a classe base,
 *   então consultas polimórficas (ex.: buscar todos os tipos de entidade)
 *   podem gerar UNION entre várias tabelas.
 *
 * Vantagens:
 *   - Estrutura mais limpa: cada tabela reflete exatamente os atributos da classe.
 *   - Evita colunas nulas, diferente do SINGLE_TABLE.
 *
 * Desvantagens:
 *   - Consultas polimórficas podem ser mais pesadas,
 *     pois exigem UNION entre tabelas.
 *   - Pode ser menos performático em cenários com muitas subclasses.
 *
 * Em resumo: TABLE_PER_CLASS é útil quando queremos
 * evitar colunas nulas e manter tabelas mais específicas,
 * aceitando o custo de consultas mais complexas.
 */
@Inheritance(strategy = InheritanceType.JOINED)
public class Produto {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String nome;
	private String descricao;
	private BigDecimal preco;
	private LocalDate dataCadastro = LocalDate.now();

	@ManyToOne(fetch = FetchType.LAZY)
	private Categoria categoria;
	
	public Produto() {
	}
	
	public Produto(String nome, String descricao, BigDecimal preco, Categoria categoria) {
		this.nome = nome;
		this.descricao = descricao;
		this.preco = preco;
		this.categoria = categoria;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public BigDecimal getPreco() {
		return preco;
	}

	public void setPreco(BigDecimal preco) {
		this.preco = preco;
	}

	public LocalDate getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(LocalDate dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public Categoria getCategoria() {
		return categoria;
	}

	public void setCategoria(Categoria categoria) {
		this.categoria = categoria;
	}

}
