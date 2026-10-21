
package br.ufal.ic.p2.wepayu.Exception;

public class AgendaPagamentosJaExisteException extends RuntimeException
{
    public AgendaPagamentosJaExisteException()
    {
        super("Agenda de pagamentos ja existe");
    }
}
