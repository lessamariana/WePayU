package br.ufal.ic.p2.wepayu.Exception;

public class ValorBooleanoInvalidoException extends RuntimeException
{
    public ValorBooleanoInvalidoException()
    {
        super("Valor deve ser true ou false.");
    }
}