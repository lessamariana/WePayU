package br.ufal.ic.p2.wepayu.models.empregado;

import br.ufal.ic.p2.wepayu.Exception.EmpregadoNaoEhComissionadoException;
import br.ufal.ic.p2.wepayu.Exception.ComissaoInvalidaException;
import br.ufal.ic.p2.wepayu.models.folha.RegistroComissionado;
import br.ufal.ic.p2.wepayu.models.folha.RegistroPagamento;
import br.ufal.ic.p2.wepayu.models.folha.ResultadoPagamento;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/*Empregado que recebe salário + comissão*/

public class EmpregadoComissionado extends Empregado
{
    private BigDecimal comissao;

    // Lista que guarda todas as vendas realizadas pelo empregado.
    private final List<Venda> vendas;

    public EmpregadoComissionado(String nome, String endereco, BigDecimal salario, BigDecimal comissao)
    {
        super(nome, endereco, salario);
        validarComissao(comissao);
        this.comissao = comissao;

        // Inicializa a lista de vendas vazia.
        this.vendas = new ArrayList<>();

        definirAgendaPagamentoPadrao("semanal 2 5");
    }

    public BigDecimal getComissao()
    {
        return comissao;
    }

    @Override
    public String getComissaoPersistencia()
    {
        return comissao.toString();
    }

    // Devolve uma cópia não modificável, assim ninguém altera a lista sem passar pelas validações do modelo

    @Override
    public List<Venda> getVendasPersistencia()
    {
        return List.copyOf(vendas);
    }

    /*Usei o override para sinalizar que pode substituir esse metodo na classe pai*/
    /*Achei relevante por ser POO, então estou tentando usar esse conceito de polimorfismo*/
    /*As classe filhas tem que responder por conta própria*/

    @Override
    protected String getTipoEmpregado()
    {
        return "comissionado";
    }

    /*Comissão é um atributo especifico de EmpregadoComissao, então adicionei*/
    @Override
    protected String getAtributoEspecifico(String atributo)
    {
        if ("comissao".equals(atributo))
        {
            return formatarValor(comissao);
        }
        /*usei o super pra fazer referencia a classe pai Empregado*/
        return super.getAtributoEspecifico(atributo);
    }

    /* Registra uma nova venda para o empregado.*/
    @Override
    public void lancaVenda(Venda venda)
    {
        vendas.add(venda);
    }

    /* Soma as vendas realizadas dentro do intervalo. A data inicial é incluída.A data final não é incluída. */

    @Override
    public BigDecimal getVendasRealizadas(LocalDate dataInicial, LocalDate dataFinal)
    {
        BigDecimal total = BigDecimal.ZERO;

        for(Venda venda : vendas)
        {
            LocalDate dataVenda = venda.getData();

            if(!dataVenda.isBefore(dataInicial) && dataVenda.isBefore(dataFinal))
            {
                total = total.add(venda.getValor());
            }
        }

        return total;
    }

    @Override
    public void alteraComissao(BigDecimal comissao)
    {
        validarComissao(comissao);

        this.comissao = comissao;
    }

    // Extraí a validação para um método, porque agora o construtor também valida a comissão (antes só a Facade validava na criação)

    private void validarComissao(BigDecimal comissao)
    {
        if(comissao == null)
        {
            throw new ComissaoInvalidaException("Comissao nao pode ser nula.");
        }

        if(comissao.compareTo(BigDecimal.ZERO) < 0)
        {
            throw new ComissaoInvalidaException("Comissao deve ser nao-negativa.");
        }
    }

    @Override
    public boolean deveReceber(LocalDate dataPagamento)
    {
        return deveReceberPelaAgenda(dataPagamento);
    }

    @Override
    public BigDecimal calcularPagamento(LocalDate dataPagamento)
    {
        LocalDate inicio = getInicioPeriodoAtual(dataPagamento);

        BigDecimal salarioFixo = calcularSalarioFixoPorAgenda();

        BigDecimal vendas = getVendasRealizadas(inicio, dataPagamento);

        BigDecimal valorComissao = vendas.multiply(getComissao()).setScale(2, RoundingMode.DOWN);

        return salarioFixo.add(valorComissao);
    }

    private BigDecimal calcularSalarioFixoQuinzenal()
    {
        return getSalario().multiply(new BigDecimal("12")).divide(new BigDecimal("52"), 10, RoundingMode.HALF_UP).multiply(new BigDecimal("2")).setScale(2, RoundingMode.DOWN);
    }

    @Override
    public RegistroPagamento gerarRegistro(LocalDate dataPagamento, ResultadoPagamento resultado)
    {
        LocalDate inicio = getInicioPeriodoAtual(dataPagamento);

        BigDecimal salarioFixo = calcularSalarioFixoPorAgenda();

        BigDecimal vendasRealizadas = getVendasRealizadas(inicio, dataPagamento);

        BigDecimal valorComissao = vendasRealizadas.multiply(getComissao()).setScale(2, RoundingMode.DOWN);

        return new RegistroComissionado(getNome(), getDescricaoMetodoPagamento(), resultado, salarioFixo, vendasRealizadas, valorComissao);
    }

    @Override
    public Empregado clonar()
    {
        EmpregadoComissionado clone = new EmpregadoComissionado(getNome(), getEndereco(), getSalario(), this.comissao);
        copiarCampos(clone);
        clone.vendas.addAll(this.vendas); // Venda é imutável, copiar a lista basta
        return clone;
    }

}
