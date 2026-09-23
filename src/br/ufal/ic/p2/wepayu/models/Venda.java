package br.ufal.ic.p2.wepayu.models;

import java.math.BigDecimal;
import java.time.LocalDate;

/* Classe para representar uma venda realizada por um empregado comissionado. Toda venda tem data e valor. */

public class Venda
{
    private final LocalDate data;
    private final BigDecimal valor;

    public Venda(LocalDate data, BigDecimal valor)
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
