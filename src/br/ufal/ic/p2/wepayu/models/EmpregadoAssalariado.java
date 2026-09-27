package br.ufal.ic.p2.wepayu.models;

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
        LocalDate ultimoDia =
                dataPagamento.with(TemporalAdjusters.lastDayOfMonth());

        while (
                ultimoDia.getDayOfWeek() == DayOfWeek.SATURDAY ||
                        ultimoDia.getDayOfWeek() == DayOfWeek.SUNDAY
        )
        {
            ultimoDia = ultimoDia.minusDays(1);
        }

        return dataPagamento.equals(ultimoDia);
    }

    @Override
    public BigDecimal calcularPagamento(LocalDate dataPagamento)
    {
        return getSalario();
    }

    @Override
    public RegistroPagamento gerarRegistro(LocalDate dataPagamento, ResultadoPagamento resultado)
    {
        return new RegistroAssalariado(getNome(), getDescricaoMetodoPagamento(), resultado);
    }

    @Override
    public LocalDate getInicioPeriodoAtual(LocalDate dataPagamento)
    {
        LocalDate inicio = ultimoDiaUtilDoMesAnterior(dataPagamento).plusDays(1);

        return inicio.isBefore(getDataContratacao()) ? getDataContratacao() : inicio;
    }

    private LocalDate ultimoDiaUtilDoMesAnterior(LocalDate dataPagamento)
    {
        LocalDate ultimoDia = dataPagamento.minusMonths(1).with(TemporalAdjusters.lastDayOfMonth());

        while(ultimoDia.getDayOfWeek() == DayOfWeek.SATURDAY || ultimoDia.getDayOfWeek() == DayOfWeek.SUNDAY)
        {
            ultimoDia = ultimoDia.minusDays(1);
        }

        return ultimoDia;
    }

    @Override
    public Empregado clonar()
    {
        EmpregadoAssalariado clone = new EmpregadoAssalariado(getNome(), getEndereco(), getSalario());
        copiarCampos(clone);
        return clone;
    }

}
