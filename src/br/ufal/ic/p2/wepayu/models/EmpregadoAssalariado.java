package br.ufal.ic.p2.wepayu.models;
import java.math.BigDecimal;

/*Empregado que recebe salário fixo*/

public class EmpregadoAssalariado extends Empregado
{
    public EmpregadoAssalariado(String nome, String endereco, BigDecimal salario)
    {
        super(nome, endereco, salario);
    }

    /*Usei o override para sinalizar que pode substituir esse metodo na classe pai*/
    /*Achei relevante por ser POO, então estou tentando usar esse conceito de polimorfismo*/
    /*As classe filhas tem que responder por conta própria*/
    @Override
    protected String getTipoEmpregado()
    {
        return "assalariado";
    }

}
