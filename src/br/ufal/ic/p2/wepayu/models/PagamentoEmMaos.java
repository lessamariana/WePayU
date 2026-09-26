package br.ufal.ic.p2.wepayu.models;

public class PagamentoEmMaos extends MetodoPagamento
{
    @Override
    public String getTipo()
    {
        return "emMaos";
    }
}