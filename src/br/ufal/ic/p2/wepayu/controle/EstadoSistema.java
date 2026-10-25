package br.ufal.ic.p2.wepayu.controle;

import br.ufal.ic.p2.wepayu.Exception.SistemaEncerradoException;

// Guarda se o sistema já foi encerrado
//Antes o atributo sistemaEncerrado e o metodo verificarSistemaAtivo ficavam na Facade

public class EstadoSistema
{
    private boolean sistemaEncerrado = false;

    public void verificarAtivo()
    {
        if(sistemaEncerrado)
        {
            throw new SistemaEncerradoException();
        }
    }

    public void encerrar()
    {
        sistemaEncerrado = true;
    }
}
