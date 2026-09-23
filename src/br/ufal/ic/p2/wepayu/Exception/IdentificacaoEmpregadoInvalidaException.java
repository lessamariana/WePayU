package br.ufal.ic.p2.wepayu.Exception;

public class IdentificacaoEmpregadoInvalidaException extends RuntimeException
{

    public IdentificacaoEmpregadoInvalidaException()
    {
        super("Identificacao do empregado não pode ser nula.");
    }
}