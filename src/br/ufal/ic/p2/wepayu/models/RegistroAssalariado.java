package br.ufal.ic.p2.wepayu.models;

import java.io.PrintWriter;

public class RegistroAssalariado extends RegistroPagamento
{
    public RegistroAssalariado(String nome, String descricaoMetodoPagamento, ResultadoPagamento resultado)
    {
        super("assalariado", nome, descricaoMetodoPagamento, resultado);
    }

    @Override
    public void imprimirLinha(PrintWriter saida)
    {
        saida.printf("%-48s %13s %9s %15s %s%n", getNome(), Formatador.formatarValor(getSalarioBruto()), Formatador.formatarValor(getDescontos()), Formatador.formatarValor(getSalarioLiquido()), getDescricaoMetodoPagamento());
    }
}