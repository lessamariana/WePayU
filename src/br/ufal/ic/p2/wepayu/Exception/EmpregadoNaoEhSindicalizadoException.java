package br.ufal.ic.p2.wepayu.Exception;

/* Indica que uma operação exclusiva paraempregados sindicalizados foi realizadapara um empregado que não pertence ao sindicato.*/

public class EmpregadoNaoEhSindicalizadoException extends RuntimeException
{
    public EmpregadoNaoEhSindicalizadoException()
    {
        super("Empregado nao eh sindicalizado.");
    }
}
