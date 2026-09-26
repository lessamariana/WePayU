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
        this.salarioBruto = salarioBruto.setScale(2, RoundingMode.HALF_UP);
        this.descontos = descontos.setScale(2, RoundingMode.HALF_UP);

        BigDecimal liquido = salarioBruto.subtract(descontos);

        if(liquido.compareTo(BigDecimal.ZERO) < 0)
        {
            liquido = BigDecimal.ZERO;
        }

        this.salarioLiquido = liquido.setScale(2, RoundingMode.HALF_UP);
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