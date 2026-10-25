package br.ufal.ic.p2.wepayu.models.empregado;

import br.ufal.ic.p2.wepayu.Exception.DataInvalidaException;
import br.ufal.ic.p2.wepayu.Exception.ValorInvalidoException;
import br.ufal.ic.p2.wepayu.utilitarios.ConversorValores;
import br.ufal.ic.p2.wepayu.utilitarios.DataTratamento;

import java.math.BigDecimal;
import java.time.LocalDate;

/* Taxa de serviço cobrada pelo sindicato em uma determinada data.
 */

public class TaxaServico
{
    private final LocalDate data;
    private final BigDecimal valor;

    // A própria taxa garante que o valor é positivo (antes era validado na Facade)

    public TaxaServico(LocalDate data, BigDecimal valor)
    {
        if(valor.compareTo(BigDecimal.ZERO) <= 0)
        {
            throw new ValorInvalidoException();
        }

        this.data = data;
        this.valor = valor;
    }

    // Cria a taxa a partir dos valores em texto recebidos pela Facade

    public static TaxaServico criar(String data, String valor) throws DataInvalidaException
    {
        LocalDate dataConvertida = DataTratamento.converterData(data, "Data invalida.");
        BigDecimal valorConvertido = ConversorValores.converterValor(valor);

        return new TaxaServico(dataConvertida, valorConvertido);
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
