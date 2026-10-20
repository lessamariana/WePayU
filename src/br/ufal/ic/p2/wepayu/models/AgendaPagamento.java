
package br.ufal.ic.p2.wepayu.models;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;

// Representa uma agenda de pagamento e verifica as datas de pagamento

public class AgendaPagamento
{
    private String descricao;

    public AgendaPagamento(String descricao)
    {
        this.descricao = descricao;
    }

    public String getDescricao()
    {
        return descricao;
    }

    public void setDescricao(String descricao)
    {
        this.descricao = descricao;
    }

    //Verifica se a data informada corresponde na agenda

    public boolean deveReceber(LocalDate data, LocalDate contratacao)
    {
        if(data == null)
        {
            return false;
        }

        if("mensal $".equals(descricao))
        {
            if(contratacao != null && data.isBefore(contratacao))
            {
                return false;
            }

            LocalDate ultimoDiaUtil = data.with(TemporalAdjusters.lastDayOfMonth());

            while(ultimoDiaUtil.getDayOfWeek() == DayOfWeek.SATURDAY || ultimoDiaUtil.getDayOfWeek() == DayOfWeek.SUNDAY)
            {
                ultimoDiaUtil = ultimoDiaUtil.minusDays(1);
            }

            return data.equals(ultimoDiaUtil);
        }

        String[] partes = descricao.split(" ");

        if(partes.length < 2 || !"semanal".equals(partes[0]))
        {
            return false;
        }

        int intervaloSemanas;
        int numeroDia;

        if(partes.length == 2)
        {
            intervaloSemanas = 1;
            numeroDia = Integer.parseInt(partes[1]);
        }
        else if(partes.length == 3)
        {
            intervaloSemanas = Integer.parseInt(partes[1]);
            numeroDia = Integer.parseInt(partes[2]);
        }
        else
        {
            return false;
        }

        if(intervaloSemanas <= 0 || numeroDia < 1 || numeroDia > 7)
        {
            return false;
        }

        DayOfWeek diaPagamento = DayOfWeek.of(numeroDia);

        if(data.getDayOfWeek() != diaPagamento)
        {
            return false;
        }

        // Horistas sem data de contratação também podem receber na sexta
        if(contratacao == null)
        {
            return intervaloSemanas == 1;
        }

        if(data.isBefore(contratacao))
        {
            return false;
        }

        LocalDate primeiroPagamento = contratacao;

        while(primeiroPagamento.getDayOfWeek() != diaPagamento)
        {
            primeiroPagamento = primeiroPagamento.plusDays(1);
        }

        long semanas = ChronoUnit.WEEKS.between(primeiroPagamento, data);

        return semanas % intervaloSemanas == intervaloSemanas - 1;
    }

    //Retorna quantas semanas de salário fixo são pagas por período

    public int getQuantidadeSemanas()
    {
        if("semanal 5".equals(descricao))
        {
            return 1;
        }

        if("semanal 2 5".equals(descricao))
        {
            return 2;
        }

        return 0;
    }

    //Indica se a agenda é mensal

    public boolean isMensal()
    {
        return descricao.startsWith("mensal ");
    }

    //Calcula o início do período correspondente ao pagamento informado. Quando não existe pagamento anterior registrado, considera a  ultima data de pagamento prevista na agenda

    public LocalDate getInicioPeriodo(LocalDate dataPagamento, LocalDate contratacao)
    {
        if(contratacao == null)
        {
            return dataPagamento;
        }

        LocalDate dataCandidata = dataPagamento.minusDays(1);

        while(!dataCandidata.isBefore(contratacao))
        {
            if(deveReceber(dataCandidata, contratacao))
            {
                return dataCandidata.plusDays(1);
            }

            dataCandidata = dataCandidata.minusDays(1);
        }

        return contratacao;
    }
}
