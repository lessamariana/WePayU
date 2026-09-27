package br.ufal.ic.p2.wepayu.models;

import java.util.Map;
import java.util.function.IntConsumer;

/* Controle que afeta o sistema inteiro: zerarSistema (apaga tudo) e rodaFolha (atualiza a data de último pagamento dos empregados). */

public class ControleGlobal extends Controle
{
    private final Map<String, Empregado> empregados;
    private final Map<String, Empregado> antes;
    private final Map<String, Empregado> depois;
    private final int proximoIdAntes;
    private final int proximoIdDepois;
    private final IntConsumer definirProximoId;

    public ControleGlobal(Map<String, Empregado> empregados, Map<String, Empregado> antes, int proximoIdAntes, Map<String, Empregado> depois, int proximoIdDepois, IntConsumer definirProximoId)
    {
        this.empregados = empregados;
        this.antes = antes;
        this.proximoIdAntes = proximoIdAntes;
        this.depois = depois;
        this.proximoIdDepois = proximoIdDepois;
        this.definirProximoId = definirProximoId;
    }

    @Override
    public void desfazer()
    {
        empregados.clear();
        empregados.putAll(antes);
        definirProximoId.accept(proximoIdAntes);
    }

    @Override
    public void refazer()
    {
        empregados.clear();
        empregados.putAll(depois);
        definirProximoId.accept(proximoIdDepois);
    }
}