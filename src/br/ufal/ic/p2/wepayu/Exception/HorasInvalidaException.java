package br.ufal.ic.p2.wepayu.Exception;

public class HorasInvalidaException extends Exception
{
    public HorasInvalidaException()
    {
        super("Horas devem ser positivas.");
    }
}
