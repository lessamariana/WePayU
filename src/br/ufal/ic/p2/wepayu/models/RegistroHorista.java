package br.ufal.ic.p2.wepayu.models;

import java.io.PrintWriter;
import java.math.BigDecimal;

public class RegistroHorista extends RegistroPagamento
{
    private final BigDecimal horasNormais;
    private final BigDecimal horasExtras;

    public RegistroHorista(String nome, String descricaoMetodoPagamento, ResultadoPagamento resultado, BigDecimal horasNormais, BigDecimal horasExtras)
    {
        super("horista", nome, descricaoMetodoPagamento, resultado);
        this.horasNormais = horasNormais;
        this.horasExtras = horasExtras;
    }

    @Override
    public BigDecimal getHorasNormais() { return horasNormais; }

    @Override
    public BigDecimal getHorasExtras() { return horasExtras; }

    @Override
    public void imprimirLinha(PrintWriter saida)
    {
        saida.printf("%-36s %5s %5s %13s %9s %15s %s%n", getNome(), Formatador.formatarInteiro(horasNormais), Formatador.formatarInteiro(horasExtras), Formatador.formatarValor(getSalarioBruto()), Formatador.formatarValor(getDescontos()), Formatador.formatarValor(getSalarioLiquido()), getDescricaoMetodoPagamento());
    }
}