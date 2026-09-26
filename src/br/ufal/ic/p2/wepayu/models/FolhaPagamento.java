package br.ufal.ic.p2.wepayu.models;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import br.ufal.ic.p2.wepayu.Exception.DataInvalidaException;
import br.ufal.ic.p2.wepayu.Exception.EmpregadoNaoEhHoristaException;

public class FolhaPagamento
{
    private final LocalDate dataPagamento;

    public FolhaPagamento(LocalDate dataPagamento)
    {
        this.dataPagamento = dataPagamento;
    }

    public ResultadoPagamento calcular(Empregado empregado) throws EmpregadoNaoEhHoristaException, DataInvalidaException
    {
        BigDecimal bruto = empregado.calcularPagamento(dataPagamento);

        BigDecimal descontos = BigDecimal.ZERO;

        LocalDate inicio = empregado.getInicioPeriodoAtual(dataPagamento);

        if(empregado.participaSindicato())
        {
            descontos = descontos.add(empregado.calcularTaxaSindical(inicio, dataPagamento));
        }

        descontos = descontos.add(empregado.calcularTaxasServico(inicio, dataPagamento));

        return new ResultadoPagamento(bruto, descontos);
    }
}