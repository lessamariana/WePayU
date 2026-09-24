package br.ufal.ic.p2.wepayu.Exception;

/* A id do membro do sindicato não foi informada. */
public class IdentificacaoMembroInvalidaException extends RuntimeException
{
    public IdentificacaoMembroInvalidaException()
    {
        super("Identificacao do membro nao pode ser nula.");
    }
}