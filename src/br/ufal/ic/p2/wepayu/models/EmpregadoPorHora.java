package br.ufal.ic.p2.wepayu.models;

import java.math.BigDecimal;

/*Empregado que recebe por hora trabalhada*/

public class EmpregadoPorHora extends Empregado {
    public EmpregadoPorHora(
            String nome,
            String endereco,
            BigDecimal salario) {

        super(nome, endereco, salario);
    }

    /*Usei o override para sinalizar que pode substituir esse metodo na classe pai*/
    /*Achei relevante por ser POO, então estou tentando usar esse conceito de polimorfismo*/
    /*As classe filhas tem que responder por conta própria*/

    @Override
    protected String getTipoEmpregado() {
        return "Horista";
    }
}
