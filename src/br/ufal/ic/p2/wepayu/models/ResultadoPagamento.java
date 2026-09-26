package br.ufal.ic.p2.wepayu.models;

import java.math.BigDecimal;
import java.math.RoundingMode;

/*Guarda o resultado do pagamento de empregado, onde:
 * salarioBruto = valor antes dos descontos
 * descontos = taxas/descontos aplicados
 * salarioLiquido = valor efetivamente recebido
 */

public class ResultadoPagamento
{
    private final BigDecimal salarioBruto;
    private final BigDecimal descontos;
    private final BigDecimal salarioLiquido;

    public ResultadoPagamento(BigDecimal salarioBruto, BigDecimal descontos)
    {
        BigDecimal brutoArredondado = salarioBruto.setScale(2, RoundingMode.HALF_UP);
        BigDecimal descontosArredondados = descontos.setScale(2, RoundingMode.HALF_UP);

        if(descontosArredondados.compareTo(brutoArredondado) > 0)
        {
            descontosArredondados = brutoArredondado;
        }

        this.salarioBruto = brutoArredondado;
        this.descontos = descontosArredondados;
        this.salarioLiquido = brutoArredondado.subtract(descontosArredondados);
    }

    public BigDecimal getSalarioBruto()
    {
        return salarioBruto;
    }

    public BigDecimal getDescontos()
    {
        return descontos;
    }

    public BigDecimal getSalarioLiquido()
    {
        return salarioLiquido;
    }
}