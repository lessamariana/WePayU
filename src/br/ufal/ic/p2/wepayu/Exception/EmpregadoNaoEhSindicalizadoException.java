package br.ufal.ic.p2.wepayu.Exception;

// Indica que uma operação exclusiva para empregados sindicalizados foi realizada para um empregado que não pertence ao sindicato

public class EmpregadoNaoEhSindicalizadoException extends RuntimeException
{
    public EmpregadoNaoEhSindicalizadoException()
    {
        super("Empregado nao eh sindicalizado.");
    }
}
