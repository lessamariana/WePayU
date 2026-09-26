package br.ufal.ic.p2.wepayu.models;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class Formatador
{
    private Formatador() { }

    public static String formatarValor(BigDecimal valor)
    {
        return valor.setScale(2, RoundingMode.HALF_UP).toString().replace('.', ',');
    }

    public static String formatarInteiro(BigDecimal valor)
    {
        return valor.setScale(0, RoundingMode.DOWN).toString();
    }
}