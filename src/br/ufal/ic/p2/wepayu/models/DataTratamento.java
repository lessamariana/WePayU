package br.ufal.ic.p2.wepayu.models;

import br.ufal.ic.p2.wepayu.Exception.DataInvalidaException;

import java.time.DateTimeException;
import java.time.LocalDate;

public class DataTratamento
{

    public static LocalDate converterData(String data, String mensagem) throws DataInvalidaException
    {
        try
        {
            /*Convertendo String em inteiro*/
            String[] partes = data.split("/");

            int dia = Integer.parseInt(partes[0]);
            int mes = Integer.parseInt(partes[1]);
            int ano = Integer.parseInt(partes[2]);

            return LocalDate.of(ano, mes, dia);

        } catch (DateTimeException | NumberFormatException | ArrayIndexOutOfBoundsException e)
        {
            throw new DataInvalidaException(mensagem);
        }
    }
}