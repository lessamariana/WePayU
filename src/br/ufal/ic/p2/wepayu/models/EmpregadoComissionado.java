package br.ufal.ic.p2.wepayu.models;

import br.ufal.ic.p2.wepayu.Exception.EmpregadoNaoEhComissionadoException;
import br.ufal.ic.p2.wepayu.Exception.ComissaoInvalidaException;

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
        this.comissao = comissao;

        // Inicializa a lista de vendas vazia.
        this.vendas = new ArrayList<>();
    }

    public BigDecimal getComissao()
    {
        return comissao;
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
    public void lancaVenda(LocalDate data, BigDecimal valor)
    {
        vendas.add(new Venda(data, valor));
    }

    /*
     * Soma as vendas realizadas dentro do intervalo.
     * A data inicial é incluída.
     * A data final não é incluída.
     */

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
        if(comissao == null)
        {
            throw new ComissaoInvalidaException("Comissao nao pode ser nula.");
        }

        if(comissao.compareTo(BigDecimal.ZERO) < 0)
        {
            throw new ComissaoInvalidaException("Comissao deve ser nao-negativa.");
        }

        this.comissao = comissao;
    }

    @Override
    public boolean deveReceber(LocalDate dataPagamento)
    {
        if(dataPagamento.getDayOfWeek() != DayOfWeek.FRIDAY)
        {
            return false;
        }

        LocalDate contratacao = getDataContratacao();

        long semanas = ChronoUnit.WEEKS.between(contratacao, dataPagamento);

        return semanas % 2 == 1;
    }

    @Override
    public BigDecimal calcularPagamento(LocalDate dataPagamento)
    {
        LocalDate inicio;

        if(getDataUltimoPagamento() == null)
        {
            inicio = getDataContratacao();
        }
        else
        {
            inicio = getDataUltimoPagamento().plusDays(1);
        }

        /* O salário fixo do comissionado corresponde aduas semanas do salário mensal.
         * salário anual = salário mensal * 12
         * salário semanal = salário anual / 52
         * duas semanas = salário semanal * 2
         */
        BigDecimal salarioFixo = getSalario().multiply(new BigDecimal("12")).divide(new BigDecimal("52"), 10, RoundingMode.HALF_UP).multiply(new BigDecimal("2")).setScale(2, RoundingMode.DOWN);
        BigDecimal vendas = getVendasRealizadas(inicio, dataPagamento);
        BigDecimal valorComissao = vendas.multiply(getComissao()).setScale(2, RoundingMode.DOWN);

        return salarioFixo.add(valorComissao);
    }
}
