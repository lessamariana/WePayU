package br.ufal.ic.p2.wepayu.models.empregado;

import br.ufal.ic.p2.wepayu.Exception.DataInvalidaException;
import br.ufal.ic.p2.wepayu.Exception.ValorInvalidoException;
import br.ufal.ic.p2.wepayu.utilitarios.ConversorValores;
import br.ufal.ic.p2.wepayu.utilitarios.DataTratamento;

import java.math.BigDecimal;
import java.time.LocalDate;

/* Classe para representar uma venda realizada por um empregado comissionado. Toda venda tem data e valor. */

public class Venda
{
    private final LocalDate data;
    private final BigDecimal valor;

    // A própria venda garante que o valor é positivo (antes era validado na Facade)

    public Venda(LocalDate data, BigDecimal valor)
    {
        if(valor.compareTo(BigDecimal.ZERO) <= 0)
        {
            throw new ValorInvalidoException();
        }

        this.data = data;
        this.valor = valor;
    }

    // Cria a venda a partir dos valores em texto recebidos pela Facade

    public static Venda criar(String data, String valor) throws DataInvalidaException
    {
        LocalDate dataConvertida = DataTratamento.converterData(data, "Data invalida.");
        BigDecimal valorConvertido = ConversorValores.converterValor(valor);

        return new Venda(dataConvertida, valorConvertido);
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
