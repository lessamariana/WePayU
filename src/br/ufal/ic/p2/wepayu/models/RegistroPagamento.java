package br.ufal.ic.p2.wepayu.models;

import java.io.PrintWriter;
import java.math.BigDecimal;

public abstract class RegistroPagamento
{
    private final String tipoEmpregado;

    private final String nome;
    private final String descricaoMetodoPagamento;
    private final BigDecimal salarioBruto;
    private final BigDecimal descontos;
    private final BigDecimal salarioLiquido;

    protected RegistroPagamento(String tipoEmpregado, String nome, String descricaoMetodoPagamento, ResultadoPagamento resultado)
    {
        this.tipoEmpregado = tipoEmpregado;
        this.nome = nome;
        this.descricaoMetodoPagamento = descricaoMetodoPagamento;
        this.salarioBruto = resultado.getSalarioBruto();
        this.descontos = resultado.getDescontos();
        this.salarioLiquido = resultado.getSalarioLiquido();
    }

    public String getTipoEmpregado()
    {
        return tipoEmpregado;
    }

    public String getNome()
    {
        return nome;
    }

    public String getDescricaoMetodoPagamento()
    {
        return descricaoMetodoPagamento;
    }

    public BigDecimal getSalarioBruto()
    {
        return salarioBruto;
    }

    public BigDecimal getDescontos()
    {
        return descontos;
    }

    public BigDecimal getSalarioLiquido()
    {
        return salarioLiquido;
    }

    public BigDecimal getHorasNormais() { return BigDecimal.ZERO; }
    public BigDecimal getHorasExtras()  { return BigDecimal.ZERO; }
    public BigDecimal getSalarioFixo()  { return BigDecimal.ZERO; }
    public BigDecimal getVendas()       { return BigDecimal.ZERO; }
    public BigDecimal getComissao()     { return BigDecimal.ZERO; }

    public abstract void imprimirLinha(PrintWriter saida);
}