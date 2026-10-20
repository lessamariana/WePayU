
package br.ufal.ic.p2.wepayu.Exception;

public class AgendaPagamentoNaoDisponivelException extends RuntimeException
{
  public AgendaPagamentoNaoDisponivelException()
  {
    super("Agenda de pagamento nao esta disponivel");
  }
}
