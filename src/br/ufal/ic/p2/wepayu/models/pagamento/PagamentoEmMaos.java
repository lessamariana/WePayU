package br.ufal.ic.p2.wepayu.models.pagamento;

public class PagamentoEmMaos extends MetodoPagamento
{
    @Override
    public String getTipo()
    {
        return "emMaos";
    }

    @Override
    public String getDescricaoRecibo(String enderecoEmpregado)
    {
        return "Em maos";
    }

}