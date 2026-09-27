package br.ufal.ic.p2.wepayu.Exception;

public class EmpregadoNaoExistePorNomeException extends EmpregadoNaoExisteException
{
    public EmpregadoNaoExistePorNomeException()
    {
        super();
    }

    @Override
    public String getMessage()
    {
        return "Nao ha empregado com esse nome.";
    }
}