package br.ufal.ic.p2.wepayu.Exception;

public class ContaCorrenteInvalidaException extends RuntimeException
{
    public ContaCorrenteInvalidaException()
    {
        super("Conta corrente nao pode ser nulo.");
    }
}