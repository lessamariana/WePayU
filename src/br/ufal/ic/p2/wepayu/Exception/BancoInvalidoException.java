package br.ufal.ic.p2.wepayu.Exception;

public class BancoInvalidoException extends RuntimeException
{
    public BancoInvalidoException()
    {
        super("Banco nao pode ser nulo.");
    }
}