package br.ufal.ic.p2.wepayu.utilitarios;

import java.math.BigDecimal;
import java.math.RoundingMode; //Descobri que ao dividir números ou ajustar casas decimais de um BigDecimal e o resultado não for exato, o Java não sabe o que fazer com os números que sobram, então coloquei para auxiliar no arredondamento.

public final class Formatador
{
    private Formatador() { }

    /*Como em Empregado está como protected, coloquei novamente aqui, caso ache que vai ser muito reutilizado, irei criar uma classe só para isso depois para evitar repetição*/
    // a Facade agora usa direto esse método, então a cópia que existia na Facade foi removida

    public static String formatarValor(BigDecimal valor)
    {
        return valor.setScale(2, RoundingMode.HALF_UP).toString().replace('.', ',');
    }

    public static String formatarInteiro(BigDecimal valor)
    {
        return valor.setScale(0, RoundingMode.DOWN).toString();
    }

    // Usado para as horas trabalhadas: mantém as casas decimais que existirem, só trocando ponto por vírgula

    public static String formatarDecimal(BigDecimal valor)
    {
        return valor.toString().replace('.', ',');
    }
}
