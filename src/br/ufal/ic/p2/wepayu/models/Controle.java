package br.ufal.ic.p2.wepayu.models;

/* Representa uma ação que alterou o estado do sistema e que pode ser desfeita (undo) e refeita (redo). */

public abstract class Controle
{
    public abstract void desfazer();
    public abstract void refazer();
}