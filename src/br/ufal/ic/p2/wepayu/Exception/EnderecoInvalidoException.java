package br.ufal.ic.p2.wepayu.Exception;

public class EnderecoInvalidoException extends RuntimeException
{

    public EnderecoInvalidoException()
    {
        super("Endereco nao pode ser nulo.");
    }
}
