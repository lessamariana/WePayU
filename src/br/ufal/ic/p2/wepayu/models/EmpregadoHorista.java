package br.ufal.ic.p2.wepayu.models;

/*Adicionando imports*/

import br.ufal.ic.p2.wepayu.Exception.DataInvalidaException;
import br.ufal.ic.p2.wepayu.Exception.EmpregadoNaoEhHoristaException;
import br.ufal.ic.p2.wepayu.Exception.HorasInvalidaException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/*Empregado que recebe por hora trabalhada*/

public class EmpregadoHorista extends Empregado
{
    public EmpregadoHorista(String nome, String endereco, BigDecimal salario)
    {
        super(nome, endereco, salario);
    }

    /*Usei o override para sinalizar que pode substituir esse metodo na classe pai*/
    /*Achei relevante por ser POO, então estou tentando usar esse conceito de polimorfismo*/
    /*As classe filhas tem que responder por conta própria*/

    @Override
    protected String getTipoEmpregado()
    {
        return "horista";
    }

    private List<CartaoPonto> cartoesPonto = new ArrayList<>();

    @Override
    public void lancaCartao(LocalDate data, BigDecimal horas) throws EmpregadoNaoEhHoristaException, DataInvalidaException, HorasInvalidaException
    {
        if(horas.compareTo(BigDecimal.ZERO) <= 0)
        {
            throw new HorasInvalidaException();
        }

        cartoesPonto.add(new CartaoPonto(data, horas));
    }

    @Override
    public BigDecimal getHorasNormaisTrabalhadas(LocalDate dataInicial, LocalDate dataFinal) throws EmpregadoNaoEhHoristaException, DataInvalidaException
    {
        BigDecimal total = BigDecimal.ZERO;

        for(CartaoPonto cartao : cartoesPonto)
        {
            LocalDate data = cartao.getData();

            if(!data.isBefore(dataInicial) && data.isBefore(dataFinal))
            {
                BigDecimal horas = cartao.getHoras();

                if(horas.compareTo(BigDecimal.valueOf(8)) <= 0)
                {
                    total = total.add(horas);
                }else
                {
                    total = total.add(BigDecimal.valueOf(8));
                }
            }
        }

        return total;
    }

    @Override
    public BigDecimal getHorasExtrasTrabalhadas(LocalDate dataInicial, LocalDate dataFinal) throws EmpregadoNaoEhHoristaException, DataInvalidaException
    {
        BigDecimal total = BigDecimal.ZERO;

        for(CartaoPonto cartao : cartoesPonto)
        {
            LocalDate data = cartao.getData();

            if(!data.isBefore(dataInicial) && data.isBefore(dataFinal))
            {
                BigDecimal horas = cartao.getHoras();

                if(horas.compareTo(BigDecimal.valueOf(8)) > 0)
                {
                    total = total.add(horas.subtract(BigDecimal.valueOf(8)));
                }
            }
        }

        return total;
    }
}
