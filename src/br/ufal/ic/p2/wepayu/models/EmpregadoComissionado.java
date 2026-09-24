package br.ufal.ic.p2.wepayu.models;

import br.ufal.ic.p2.wepayu.Exception.EmpregadoNaoEhComissionadoException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/*Empregado que recebe salário + comissão*/

public class EmpregadoComissionado extends Empregado
{
    private final BigDecimal comissao;

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
}
