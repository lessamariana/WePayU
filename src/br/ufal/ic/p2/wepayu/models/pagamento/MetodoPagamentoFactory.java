package br.ufal.ic.p2.wepayu.models.pagamento;

import br.ufal.ic.p2.wepayu.Exception.MetodoPagamentoInvalidoException;

// Cria o método de pagamento a partir do texto recebido
// É a mesma ideia do EmpregadoFactory, centralizar a criação em um lugar só
// Antes essa escolha ficava na Facade (alterarMetodoPagamento e alteraEmpregado com banco)

public final class MetodoPagamentoFactory
{
    private MetodoPagamentoFactory() { }

    public static MetodoPagamento criar(String tipo)
    {
        if("emMaos".equals(tipo))
        {
            return new PagamentoEmMaos();
        }

        if("correios".equals(tipo))
        {
            return new PagamentoCorreios();
        }

        throw new MetodoPagamentoInvalidoException();
    }

    // Só o pagamento em banco precisa dos dados da conta

    public static MetodoPagamento criar(String tipo, String banco, String agencia, String contaCorrente)
    {
        if(!"banco".equals(tipo))
        {
            throw new MetodoPagamentoInvalidoException();
        }

        return new PagamentoBanco(banco, agencia, contaCorrente);
    }
}
