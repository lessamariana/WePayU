package br.ufal.ic.p2.wepayu.controle;

import br.ufal.ic.p2.wepayu.Exception.NenhumComandoException;

import java.util.ArrayDeque; // to usando pra fazer de forma dinamica, pesquisei e vi que é melhor que usar LinkedLista ou Stack
import java.util.Deque;

//Ainda us_8 mas associado a questão das pilhas/filas organização

// Responsável apenas pelas pilhas de desfazer/refazer
//Antes os atributos pilhaDesfazer e pilhaRefazer e os métodos registrarComando, undo e redo ficavam na Facade

public class HistoricoComandos
{
    private final Deque<Controle> pilhaDesfazer = new ArrayDeque<>();
    private final Deque<Controle> pilhaRefazer = new ArrayDeque<>();

    // Chamado depois que a operação deu certo: guarda o estado final e empilha

    public void registrar(Controle comando)
    {
        comando.concluir();
        pilhaDesfazer.push(comando);
        pilhaRefazer.clear();
    }

    public void desfazer()
    {
        if(pilhaDesfazer.isEmpty())
        {
            throw new NenhumComandoException("Nao ha comando a desfazer.");
        }

        Controle comando = pilhaDesfazer.pop();
        comando.desfazer();
        pilhaRefazer.push(comando);
    }

    public void refazer()
    {
        if(pilhaRefazer.isEmpty())
        {
            throw new NenhumComandoException("Nao ha comando a refazer.");
        }

        Controle comando = pilhaRefazer.pop();
        comando.refazer();
        pilhaDesfazer.push(comando);
    }
}
