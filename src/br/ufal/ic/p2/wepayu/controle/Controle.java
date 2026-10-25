package br.ufal.ic.p2.wepayu.controle;

// Representa uma ação que alterou o estado do sistema e que pode ser desfeita (undo) e refeita (redo).
// Funcionamento: o controle é criado ANTES da operação (guarda o estado anterior)
// HistoricoComandos chama concluir() DEPOIS da operação (guarda o estado novo)

public abstract class Controle
{
    public abstract void concluir();
    public abstract void desfazer();
    public abstract void refazer();
}
