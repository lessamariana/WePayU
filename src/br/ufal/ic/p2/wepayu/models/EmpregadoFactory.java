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

            case "Horista":
                return new EmpregadoPorHora(nome, endereco, salario);

            case "CLT":
                return new EmpregadoCLT(nome, endereco, salario);

            case "Comissionado":
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

            case "Comissionado":
                return new EmpregadoComissao(nome, endereco, salario, comissao);

            case "Horista":
            case "CLT":
                // Esses tipos não aceitam comissão.
                throw new TipoNaoAplicavelException();

            default:
                throw new TipoInvalidoException();
        }
    }
}
