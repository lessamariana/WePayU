package br.ufal.ic.p2.wepayu.models;

import java.math.BigDecimal;
import java.time.LocalDate;

/* Taxa de serviço cobrada pelo sindicato em uma determinada data.
 */

public class TaxaServico
{
    private final LocalDate data;
    private final BigDecimal valor;

    public TaxaServico(LocalDate data, BigDecimal valor)
    {
        this.data = data;
        this.valor = valor;
    }

    public LocalDate getData()
    {
        return data;
    }

    public BigDecimal getValor()
    {
        return valor;
    }
}
