package br.com.alura.loja.modelo;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pedidos")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "valor_total")
    private BigDecimal valorTotal = BigDecimal.ZERO;
    private LocalDate data = LocalDate.now();

    // Associação ManyToOne: cada Pedido está ligado a um Cliente.
    // Por padrão, o fetch em @ManyToOne costuma ser EAGER,
    // ou seja, ao carregar um Pedido o JPA já traz junto o Cliente.
    // Isso é útil quando quase sempre precisamos dos dados do cliente,
    // mas pode gerar consultas mais pesadas se não for necessário.
    @ManyToOne(fetch = FetchType.LAZY)
    private Cliente cliente;

    // Associação OneToMany: um Pedido possui vários itens.
    // Aqui usamos mappedBy para indicar que a relação é controlada pelo ItemPedido.
    // O fetch em @OneToMany por padrão é LAZY,
    // ou seja, os itens só são carregados quando realmente acessados.
    // Isso evita sobrecarga de memória e consultas desnecessárias,
    // mas exige cuidado para não gerar LazyInitializationException
    // se o acesso ocorrer fora do contexto da sessão.
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL)
    private List<ItemPedido> itens = new ArrayList<>();

    public Pedido() {
    }

    public Pedido(Cliente cliente) {
        this.cliente = cliente;
    }

    public void adicionarItem(ItemPedido item) {
        item.setPedido(this);
        this.itens.add(item);
        this.valorTotal = this.valorTotal.add(item.getValor());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public List<ItemPedido> getItens() {
        return itens;
    }

    public void setItens(List<ItemPedido> itens) {
        this.itens = itens;
    }
}
