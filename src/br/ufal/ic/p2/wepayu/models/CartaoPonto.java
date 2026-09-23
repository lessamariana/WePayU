package br.ufal.ic.p2.wepayu.models;

import java.math.BigDecimal;
import java.time.LocalDate; /* Para representar data (ano/mês/dia)*/


/* Representa o cartão de ponto de um empregado.
 * Cada cartão registra a data em que o empregado trabalhou e a quantidade de horas trabalhadas naquele dia.
 * Criei separadamente como um objeto, porque achei que guardar de forma separada daria muito trabalho.
 * Fica mais organizado, já que são diferentes empregados.
 * Mas ainda é difícil desvincular da programação estruturada então não tenho certeza se faz tanto sentido.
 * */

public class CartaoPonto
{
    private LocalDate data;
    private BigDecimal horas;

    public CartaoPonto(LocalDate data, BigDecimal horas)
    {
        this.data = data;
        this.horas = horas;
    }

    public LocalDate getData()
    {
        return data;
    }

    // Função que calcula ou recupera uma quantidade de horas
    public BigDecimal getHoras()
    {
        return horas;
    }
}