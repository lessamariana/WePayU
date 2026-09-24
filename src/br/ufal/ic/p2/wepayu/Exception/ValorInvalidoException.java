package br.ufal.ic.p2.wepayu.Exception;

/*Exceção usada quando o valor de uma venda mão é válido.*/

public class ValorInvalidoException extends RuntimeException
{
    public ValorInvalidoException()
    {
        super("Valor deve ser positivo.");
    }
}