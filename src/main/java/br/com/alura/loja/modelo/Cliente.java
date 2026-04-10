package br.com.alura.loja.modelo;

import javax.persistence.*;

@Entity
@Table(name = "clientes")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Uso de @Embedded e @Embeddable:
     *
     * - @Embeddable marca a classe DadosPessoais como "embutível",
     *   ou seja, seus atributos podem ser incorporados em outras entidades.
     *
     * - @Embedded indica que os campos da classe DadosPessoais
     *   serão mapeados como colunas diretas da tabela "clientes",
     *   sem criar uma tabela separada.
     *
     * Vantagem: permite organizar e reutilizar blocos de atributos
     * (como nome e cpf) em várias entidades, mantendo o código limpo
     * e evitando repetição.
     *
     * Resultado: a tabela de Cliente terá colunas "nome" e "cpf"
     * diretamente, mas no código esses dados ficam agrupados
     * dentro do objeto DadosPessoais.
     */

    @Embedded
    private DadosPessoais dadosPessoais;

    public Cliente(String nome, String cpf) {
        this.dadosPessoais = new DadosPessoais(nome, cpf);
    }

    public String getNome() {
        return this.dadosPessoais.getNome();
    }

    public String getCpf() {
        return this.dadosPessoais.getCpf();
    }

    public Cliente() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DadosPessoais getDadosPessoais() {
        return dadosPessoais;
    }

}
