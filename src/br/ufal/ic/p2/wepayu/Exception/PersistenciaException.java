package br.ufal.ic.p2.wepayu.Exception;

/*Exceção específica para problemas ao salvarou carregar o estado do sistema.*/
public class PersistenciaException extends RuntimeException
{
    public PersistenciaException(String mensagem)
    {
        super(mensagem);
    }

    public PersistenciaException(String mensagem, Throwable causa)
    {
        super(mensagem, causa);
    }
}