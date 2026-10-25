package br.ufal.ic.p2.wepayu.utilitarios;

import br.ufal.ic.p2.wepayu.Exception.DataInvalidaException;

import java.time.LocalDate;

// Representa um intervalo de datas informado nas consultas (horas, vendas e taxas de serviço)
// Antes a conversão e a validação das datas eram repetidas em 4 métodos da Facade, agora o próprio período se valida ao ser criado

public final class Periodo
{
    private final LocalDate inicio;
    private final LocalDate fim;

    private Periodo(LocalDate inicio, LocalDate fim)
    {
        this.inicio = inicio;
        this.fim = fim;
    }

    public static Periodo criar(String dataInicial, String dataFinal) throws DataInvalidaException
    {
        // Converte a data inicial
        LocalDate inicio = DataTratamento.converterData(dataInicial, "Data inicial invalida.");

        // Converte a data final
        LocalDate fim = DataTratamento.converterData(dataFinal, "Data final invalida.");

        // A data inicial não pode ser posterior a data final
        if(inicio.isAfter(fim))
        {
            throw new DataInvalidaException("Data inicial nao pode ser posterior aa data final."); //nos testes tá assim então deixei
        }

        return new Periodo(inicio, fim);
    }

    public LocalDate getInicio()
    {
        return inicio;
    }

    public LocalDate getFim()
    {
        return fim;
    }
}
