package br.ufal.ic.p2.wepayu.Exception;

public class IdentificacaoSindicatoInvalidaException extends RuntimeException
{
    public IdentificacaoSindicatoInvalidaException()
    {
        super("Identificacao do sindicato nao pode ser nula.");
    }
}