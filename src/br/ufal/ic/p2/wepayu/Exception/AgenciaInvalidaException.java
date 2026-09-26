package br.ufal.ic.p2.wepayu.Exception;

public class AgenciaInvalidaException extends RuntimeException
{
    public AgenciaInvalidaException()
    {
        super("Agencia nao pode ser nulo.");
    }
}