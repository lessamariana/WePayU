package br.ufal.ic.p2.wepayu.models;

import java.io.PrintWriter;
import java.math.BigDecimal;

public class RegistroComissionado extends RegistroPagamento
{
    private final BigDecimal salarioFixo;
    private final BigDecimal vendas;
    private final BigDecimal comissao;

    public RegistroComissionado(String nome, String descricaoMetodoPagamento, ResultadoPagamento resultado, BigDecimal salarioFixo, BigDecimal vendas, BigDecimal comissao)
    {
        super("comissionado", nome, descricaoMetodoPagamento, resultado);
        this.salarioFixo = salarioFixo;
        this.vendas = vendas;
        this.comissao = comissao;
    }

    @Override
    public BigDecimal getSalarioFixo() { return salarioFixo; }

    @Override
    public BigDecimal getVendas() { return vendas; }

    @Override
    public BigDecimal getComissao() { return comissao; }

    @Override
    public void imprimirLinha(PrintWriter saida)
    {
        saida.printf("%-21s %8s %8s %8s %13s %9s %15s %s%n", getNome(), Formatador.formatarValor(salarioFixo), Formatador.formatarValor(vendas), Formatador.formatarValor(comissao), Formatador.formatarValor(getSalarioBruto()), Formatador.formatarValor(getDescontos()), Formatador.formatarValor(getSalarioLiquido()), getDescricaoMetodoPagamento());
    }
}