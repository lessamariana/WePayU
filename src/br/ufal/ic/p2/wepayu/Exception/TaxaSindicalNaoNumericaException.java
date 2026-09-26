package br.ufal.ic.p2.wepayu.Exception;
// Criei três exceções de taxa sindical pois são mensagens diferentes
public class TaxaSindicalNaoNumericaException extends RuntimeException
{
    public TaxaSindicalNaoNumericaException()
    {
        super("Taxa sindical deve ser numerica.");
    }
}