package br.ufal.ic.p2.wepayu.models.empregado;

import br.ufal.ic.p2.wepayu.models.folha.RegistroAssalariado;
import br.ufal.ic.p2.wepayu.models.folha.RegistroPagamento;
import br.ufal.ic.p2.wepayu.models.folha.ResultadoPagamento;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

/*Empregado que recebe salário fixo*/

public class EmpregadoAssalariado extends Empregado
{
    public EmpregadoAssalariado(String nome, String endereco, BigDecimal salario)
    {
        super(nome, endereco, salario);
        definirAgendaPagamentoPadrao("mensal $");
    }

    /*Usei o override para sinalizar que pode substituir esse metodo na classe pai*/
    /*Achei relevante por ser POO, então estou tentando usar esse conceito de polimorfismo*/
    /*As classe filhas tem que responder por conta própria*/
    @Override
    protected String getTipoEmpregado()
    {
        return "assalariado";
    }

    @Override
    public boolean deveReceber(LocalDate dataPagamento)
    {
        return deveReceberPelaAgenda(dataPagamento);
    }

    @Override
    public BigDecimal calcularPagamento(LocalDate dataPagamento)
    {
        return calcularSalarioFixoPorAgenda();
    }

    @Override
    public RegistroPagamento gerarRegistro(LocalDate dataPagamento, ResultadoPagamento resultado)
    {
        return new RegistroAssalariado(getNome(), getDescricaoMetodoPagamento(), resultado);
    }

    @Override
    public LocalDate getInicioPeriodoAtual(LocalDate dataPagamento)
    {
        return super.getInicioPeriodoAtual(dataPagamento);
    }

    @Override
    public Empregado clonar()
    {
        EmpregadoAssalariado clone = new EmpregadoAssalariado(getNome(), getEndereco(), getSalario());
        copiarCampos(clone);
        return clone;
    }

}
