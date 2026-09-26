package br.ufal.ic.p2.wepayu.models;

public class PagamentoCorreios extends MetodoPagamento
{
    @Override
    public String getTipo()
    {
        return "correios";
    }

    @Override
    public String getDescricaoRecibo(String enderecoEmpregado)
    {
        return "Correios, " + enderecoEmpregado;
    }


}