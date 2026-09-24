package br.ufal.ic.p2.wepayu.Exception;

/*Indica que a identificação de sindicato já está sendo utilizada por outro empregado.*/

public class IdentificacaoSindicatoJaExisteException extends RuntimeException
{
    public IdentificacaoSindicatoJaExisteException()
    {
        super("Ha outro empregado com esta identificacao de sindicato");
    }
}
