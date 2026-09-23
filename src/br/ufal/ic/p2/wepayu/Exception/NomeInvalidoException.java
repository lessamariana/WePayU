package br.ufal.ic.p2.wepayu.Exception;

public class NomeInvalidoException extends RuntimeException
{
    public NomeInvalidoException()
    {
        super("Nome não pode ser nulo.");
    }
}
