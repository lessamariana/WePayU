package br.ufal.ic.p2.wepayu.models;
import java.math.BigDecimal;

/*Empregado que recebe salário + comissão*/

public class EmpregadoComissionado extends Empregado
{
    private final BigDecimal comissao;

    public EmpregadoComissionado(String nome, String endereco, BigDecimal salario, BigDecimal comissao)
    {
        super(nome, endereco, salario);

        this.comissao = comissao;
    }

    public BigDecimal getComissao()
    {
        return comissao;
    }

    /*Usei o override para sinalizar que pode substituir esse metodo na classe pai*/
    /*Achei relevante por ser POO, então estou tentando usar esse conceito de polimorfismo*/
    /*As classe filhas tem que responder por conta própria*/
    @Override
    protected String getTipoEmpregado()
    {
        return "comissionado";
    }

    /*Comissão é um atributo especifico de EmpregadoComissao, então adicionei*/
    @Override
    protected String getAtributoEspecifico(String atributo)
    {
        if ("comissao".equals(atributo))
        {
            return formatarValor(comissao);
        }
        /*usei o super pra fazer referencia a classe pai Empregado*/
        return super.getAtributoEspecifico(atributo);
    }
}
