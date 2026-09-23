package br.ufal.ic.p2.wepayu.models;

import br.ufal.ic.p2.wepayu.Exception.TipoInvalidoException;
import br.ufal.ic.p2.wepayu.Exception.TipoNaoAplicavelException;

import java.math.BigDecimal;

/*Cria os diferentes tipos de empregado*/
/*Estudando para fazer o trabalho descobri que existe Factory Method*/
/*É um padrão para a lógica de criação de objetos e serve para centralizar
a parte de criação em um lugar só, achei que fazia sentido já que são vários tipos de empregado */

public class EmpregadoFactory
{
    //Empregados sem comissão

    public Empregado criar(String nome, String endereco, String tipo, BigDecimal salario)
    {

        switch (tipo)
        {

            case "horista":
                return new EmpregadoHorista(nome, endereco, salario);

            case "assalariado":
                return new EmpregadoAssalariado(nome, endereco, salario);

            case "comissionado":
                // Comissionado precisa obrigatoriamente receber comissão
                throw new TipoNaoAplicavelException();

            default:
                throw new TipoInvalidoException();
        }
    }

    //Empregado com comissão
    public Empregado criar(String nome, String endereco, String tipo, BigDecimal salario, BigDecimal comissao)
    {

        switch (tipo)
        {

            case "comissionado":
                return new EmpregadoComissionado(nome, endereco, salario, comissao);

            case "horista":
            case "assalariado":
                // Esses tipos não aceitam comissão.
                throw new TipoNaoAplicavelException();

            default:
                throw new TipoInvalidoException();
        }
    }
}
