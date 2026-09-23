package br.ufal.ic.p2.wepayu.Exception;

public class SalarioInvalidoException extends RuntimeException
{

    public SalarioInvalidoException(String mensagem)
    {
        super(mensagem);
    }
}