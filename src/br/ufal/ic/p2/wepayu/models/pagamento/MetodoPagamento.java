package br.ufal.ic.p2.wepayu.models.pagamento;

import br.ufal.ic.p2.wepayu.Exception.EmpregadoNaoRecebeEmBancoException;

public abstract class MetodoPagamento
{
    public abstract String getTipo();

    public String getBanco()
    {
        throw new EmpregadoNaoRecebeEmBancoException();
    }

    public String getAgencia()
    {
        throw new EmpregadoNaoRecebeEmBancoException();
    }

    public String getContaCorrente()
    {
        throw new EmpregadoNaoRecebeEmBancoException();
    }

    public abstract String getDescricaoRecibo(String enderecoEmpregado);
}