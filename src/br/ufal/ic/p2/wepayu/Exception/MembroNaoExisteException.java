package br.ufal.ic.p2.wepayu.Exception;

/* Indica que não existe empregado associado a id de membro informada.*/

public class MembroNaoExisteException extends RuntimeException
{
  public MembroNaoExisteException()
  {
    super("Membro nao existe.");
  }
}
