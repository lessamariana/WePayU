package br.ufal.ic.p2.wepayu.Exception;

public class ArquivoDeSaidaInvalidoException extends RuntimeException
{
    public ArquivoDeSaidaInvalidoException(String caminhoArquivo)
    {
        super("Nao foi possivel escrever o arquivo de saida: " + caminhoArquivo);
    }
}