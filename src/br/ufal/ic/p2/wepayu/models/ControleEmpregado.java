package br.ufal.ic.p2.wepayu.models;

import java.util.Map;
import java.util.function.IntConsumer; //Pesquisei e vi que pode ser usado ao invés do int
// para não retornar valor nenhum, como estou usando para definir uma ação achei melhor porque é só pra validar e não quero que seja fixo

/*Comando que representa uma mudança em 1 único empregado do mapa de empregados.*/

public class ControleEmpregado extends Controle
{
    private final Map<String, Empregado> empregados;
    private final String id;
    private final Empregado antes;
    private final Empregado depois;
    private final int proximoIdAntes;
    private final int proximoIdDepois;
    private final IntConsumer definirProximoId;

    public ControleEmpregado(Map<String, Empregado> empregados, String id, Empregado antes, Empregado depois, int proximoIdAntes, int proximoIdDepois, IntConsumer definirProximoId)
    {
        this.empregados = empregados;
        this.id = id;
        this.antes = antes;
        this.depois = depois;
        this.proximoIdAntes = proximoIdAntes;
        this.proximoIdDepois = proximoIdDepois;
        this.definirProximoId = definirProximoId;
    }

    @Override
    public void desfazer()
    {
        if(antes == null)
        {
            empregados.remove(id);
        }
        else
        {
            empregados.put(id, antes);
        }

        definirProximoId.accept(proximoIdAntes);
    }

    @Override
    public void refazer()
    {
        if(depois == null)
        {
            empregados.remove(id);
        }
        else
        {
            empregados.put(id, depois);
        }

        definirProximoId.accept(proximoIdDepois);
    }
}