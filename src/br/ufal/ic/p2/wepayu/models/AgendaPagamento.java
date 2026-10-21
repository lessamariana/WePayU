
package br.ufal.ic.p2.wepayu.models;
import br.ufal.ic.p2.wepayu.Exception.DescricaoAgendaInvalidaException;

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
        validarDescricao(descricao);
        this.descricao = descricao;
    }

    public String getDescricao()
    {
        return descricao;
    }

    public void setDescricao(String descricao)
    {
        validarDescricao(descricao);
        this.descricao = descricao;
    }

    public static void validarDescricao(String descricao)
    {
        if(descricao == null || descricao.trim().isEmpty())
        {
            throw new DescricaoAgendaInvalidaException();
        }

        String[] partes = descricao.trim().split("\\s+");

        if("mensal".equals(partes[0]))
        {
            if(partes.length != 2)
            {
                throw new DescricaoAgendaInvalidaException();
            }

            if("$".equals(partes[1]))
            {
                return;
            }

            try
            {
                int dia = Integer.parseInt(partes[1]);

                if(dia >= 1 && dia <= 28)
                {
                    return;
                }
            }
            catch(NumberFormatException e)
            {
                throw new DescricaoAgendaInvalidaException();
            }

            throw new DescricaoAgendaInvalidaException();
        }

        if("semanal".equals(partes[0]))
        {
            if(partes.length == 2)
            {
                validarNumero(partes[1], 1, 7);
                return;
            }

            if(partes.length == 3)
            {
                validarNumero(partes[1], 1, 52);
                validarNumero(partes[2], 1, 7);
                return;
            }
        }

        throw new DescricaoAgendaInvalidaException();
    }

    private static void validarNumero(String valor, int minimo, int maximo)
    {
        try
        {
            int numero = Integer.parseInt(valor);

            if(numero < minimo || numero > maximo)
            {
                throw new DescricaoAgendaInvalidaException();
            }
        }
        catch(NumberFormatException e)
        {
            throw new DescricaoAgendaInvalidaException();
        }
    }

    //Verifica se a data informada corresponde na agenda

    public boolean deveReceber(LocalDate data, LocalDate contratacao)
    {
        if(data == null)
        {
            return false;
        }

        if(contratacao != null && data.isBefore(contratacao))
        {
            return false;
        }

        String[] partes = descricao.split(" ");

        if("mensal".equals(partes[0]))
        {
            if("$".equals(partes[1]))
            {
                LocalDate ultimoDiaUtil = data.with(TemporalAdjusters.lastDayOfMonth());

                while(ultimoDiaUtil.getDayOfWeek() == DayOfWeek.SATURDAY || ultimoDiaUtil.getDayOfWeek() == DayOfWeek.SUNDAY)
                {
                    ultimoDiaUtil = ultimoDiaUtil.minusDays(1);
                }

                return data.equals(ultimoDiaUtil);
            }

            int diaPagamento = Integer.parseInt(partes[1]);

            return data.getDayOfMonth() == diaPagamento;
        }

        int intervaloSemanas;
        int numeroDia;

        if(partes.length == 2)
        {
            intervaloSemanas = 1;
            numeroDia = Integer.parseInt(partes[1]);
        }
        else
        {
            intervaloSemanas = Integer.parseInt(partes[1]);
            numeroDia = Integer.parseInt(partes[2]);
        }

        DayOfWeek diaPagamento = DayOfWeek.of(numeroDia);

        if(data.getDayOfWeek() != diaPagamento)
        {
            return false;
        }

        LocalDate primeiroPagamento = contratacao;

        if(primeiroPagamento == null)
        {
            // Para empregados sem data de contratação, usa o primeiro dia da semana correspondente na própria semana da data
            primeiroPagamento = data.with(TemporalAdjusters.previousOrSame(diaPagamento));
        }
        else
        {
            while(primeiroPagamento.getDayOfWeek() != diaPagamento)
            {
                primeiroPagamento = primeiroPagamento.plusDays(1);
            }
        }

        long semanas = ChronoUnit.WEEKS.between(primeiroPagamento, data);

        return semanas % intervaloSemanas == intervaloSemanas - 1;
    }

    //Retorna quantas semanas de salário fixo são pagas por período

    public int getQuantidadeSemanas()
    {
        if(isMensal())
        {
            return 0;
        }

        String[] partes = descricao.split(" ");

        if(partes.length == 2)
        {
            return 1;
        }

        return Integer.parseInt(partes[1]);
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
