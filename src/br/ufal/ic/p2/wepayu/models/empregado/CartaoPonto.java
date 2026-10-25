package br.ufal.ic.p2.wepayu.models.empregado;

import br.ufal.ic.p2.wepayu.Exception.DataInvalidaException;
import br.ufal.ic.p2.wepayu.Exception.HorasInvalidaException;
import br.ufal.ic.p2.wepayu.utilitarios.ConversorValores;
import br.ufal.ic.p2.wepayu.utilitarios.DataTratamento;

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
    private final LocalDate data;
    private final BigDecimal horas;

    // O próprio cartão valida as horas (antes essa validação ficava espalhada entre Facade e EmpregadoHorista)

    public CartaoPonto(LocalDate data, BigDecimal horas) throws HorasInvalidaException
    {
        if(horas.compareTo(BigDecimal.ZERO) <= 0)
        {
            throw new HorasInvalidaException();
        }

        this.data = data;
        this.horas = horas;
    }

    // Cria o cartão a partir dos valores em texto recebidos pela Facade

    public static CartaoPonto criar(String data, String horas) throws DataInvalidaException, HorasInvalidaException
    {
        LocalDate dataConvertida = DataTratamento.converterData(data, "Data invalida.");
        BigDecimal horasConvertidas = ConversorValores.converterHoras(horas);

        return new CartaoPonto(dataConvertida, horasConvertidas);
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
