package br.ufal.ic.p2.wepayu.utilitarios;

import br.ufal.ic.p2.wepayu.Exception.ComissaoInvalidaException;
import br.ufal.ic.p2.wepayu.Exception.HorasInvalidaException;
import br.ufal.ic.p2.wepayu.Exception.SalarioInvalidoException;
import br.ufal.ic.p2.wepayu.Exception.TaxaSindicalNaoNumericaException;
import br.ufal.ic.p2.wepayu.Exception.TaxaSindicalNulaException;
import br.ufal.ic.p2.wepayu.Exception.ValorInvalidoException;

import java.math.BigDecimal;
import java.util.function.Supplier;

// Converte os valores em texto que chegam dos testes para BigDecimal.
//Essa classe só cuida da conversão (texto vazio ou que não é número).
//As regras de negócio, como "salário não pode ser negativo" ou "horas devem ser positivas", ficam nos próprios objetos de modelo
// (Empregado, CartaoPonto, Venda, TaxaServico)
// Antes esses métodos ficavam na Facade (converterSalario, converterComissao, converterTaxaSindical)

public final class ConversorValores
{
    private ConversorValores() { }

    /* Conversão  do salário recebido para BigDecimal.*/

    public static BigDecimal converterSalario(String salario)
    {
        return converter(salario, () -> new SalarioInvalidoException("Salario nao pode ser nulo."), () -> new SalarioInvalidoException("Salario deve ser numerico."));
    }

    //  Converte a comissão para BigDecimal

    public static BigDecimal converterComissao(String comissao)
    {
        return converter(comissao, () -> new ComissaoInvalidaException("Comissao nao pode ser nula."), () -> new ComissaoInvalidaException("Comissao deve ser numerica."));
    }

    // Converte a taxa sindical para BigDecimal

    public static BigDecimal converterTaxaSindical(String taxaSindical)
    {
        return converter(taxaSindical, TaxaSindicalNulaException::new, TaxaSindicalNaoNumericaException::new);
    }

    public static BigDecimal converterHoras(String horas) throws HorasInvalidaException
    {
        return converter(horas, HorasInvalidaException::new, HorasInvalidaException::new);
    }

    // Valor de venda ou de taxa de serviço

    public static BigDecimal converterValor(String valor)
    {
        return converter(valor, ValorInvalidoException::new, ValorInvalidoException::new);
    }

    // Metodo genérico cada conversão só informa qual exceção deve ser lançada em cada caso

    private static <E extends Exception> BigDecimal converter(String texto, Supplier<E> erroVazio, Supplier<E> erroNaoNumerico) throws E
    {
        if(texto == null || texto.isEmpty())
        {
            throw erroVazio.get();
        }

        try
        {
            return new BigDecimal(texto.replace(',', '.'));
        }
        catch(NumberFormatException e)
        {
            throw erroNaoNumerico.get();
        }
    }
}
