package br.ufal.ic.p2.wepayu.models.folha;

import br.ufal.ic.p2.wepayu.Exception.ArquivoDeSaidaInvalidoException;
import br.ufal.ic.p2.wepayu.utilitarios.Formatador;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

// Responsável apenas por escrever o arquivo da folha de pagamento
//Os métodos imprimirSecao estavam na Facade, trouxe para cá sem mudar a lógica

public class RelatorioFolha
{
    private final LocalDate dataPagamento;
    private final List<RegistroPagamento> registros;

    public RelatorioFolha(LocalDate dataPagamento, List<RegistroPagamento> registros)
    {
        this.dataPagamento = dataPagamento;
        this.registros = List.copyOf(registros);
    }

    public void escrever(String saida)
    {
        try (PrintWriter arquivo = new PrintWriter(saida))
        {
            arquivo.println("FOLHA DE PAGAMENTO DO DIA " + dataPagamento);
            arquivo.println("====================================");
            arquivo.println();

            BigDecimal totalHoristas = imprimirSecaoHoristas(arquivo, registros);
            arquivo.println();
            BigDecimal totalAssalariados = imprimirSecaoAssalariados(arquivo, registros);
            arquivo.println();
            BigDecimal totalComissionados = imprimirSecaoComissionados(arquivo, registros);

            BigDecimal totalFolha = totalHoristas.add(totalAssalariados).add(totalComissionados);

            arquivo.println();
            arquivo.println("TOTAL FOLHA: " + Formatador.formatarValor(totalFolha));
        }
        catch(FileNotFoundException e)
        {
            throw new ArquivoDeSaidaInvalidoException(saida);
        }
    }

    private BigDecimal imprimirSecaoHoristas(PrintWriter arquivo, List<RegistroPagamento> registros)
    {
        arquivo.println("===============================================================================================================================");
        arquivo.println("===================== HORISTAS ================================================================================================");
        arquivo.println("===============================================================================================================================");
        arquivo.println("Nome                                 Horas Extra Salario Bruto Descontos Salario Liquido Metodo");
        arquivo.println("==================================== ===== ===== ============= ========= =============== ======================================");

        BigDecimal totalHoras = BigDecimal.ZERO, totalExtras = BigDecimal.ZERO;
        BigDecimal totalBruto = BigDecimal.ZERO, totalDescontos = BigDecimal.ZERO, totalLiquido = BigDecimal.ZERO;

        for(RegistroPagamento registro : registros)
        {
            if(!"horista".equals(registro.getTipoEmpregado()))
            {
                continue;
            }

            registro.imprimirLinha(arquivo);

            totalHoras = totalHoras.add(registro.getHorasNormais());
            totalExtras = totalExtras.add(registro.getHorasExtras());
            totalBruto = totalBruto.add(registro.getSalarioBruto());
            totalDescontos = totalDescontos.add(registro.getDescontos());
            totalLiquido = totalLiquido.add(registro.getSalarioLiquido());
        }

        arquivo.println();
        arquivo.printf("%-36s %5s %5s %13s %9s %15s%n", "TOTAL HORISTAS", Formatador.formatarInteiro(totalHoras), Formatador.formatarInteiro(totalExtras), Formatador.formatarValor(totalBruto), Formatador.formatarValor(totalDescontos), Formatador.formatarValor(totalLiquido));

        return totalBruto;
    }

    private BigDecimal imprimirSecaoAssalariados(PrintWriter arquivo, List<RegistroPagamento> registros)
    {
        arquivo.println("===============================================================================================================================");
        arquivo.println("===================== ASSALARIADOS ============================================================================================");
        arquivo.println("===============================================================================================================================");
        arquivo.println("Nome                                             Salario Bruto Descontos Salario Liquido Metodo");
        arquivo.println("================================================ ============= ========= =============== ======================================");

        BigDecimal totalBruto = BigDecimal.ZERO, totalDescontos = BigDecimal.ZERO, totalLiquido = BigDecimal.ZERO;

        for(RegistroPagamento registro : registros)
        {
            if(!"assalariado".equals(registro.getTipoEmpregado()))
            {
                continue;
            }

            registro.imprimirLinha(arquivo);

            totalBruto = totalBruto.add(registro.getSalarioBruto());
            totalDescontos = totalDescontos.add(registro.getDescontos());
            totalLiquido = totalLiquido.add(registro.getSalarioLiquido());
        }

        arquivo.println();
        arquivo.printf("%-48s %13s %9s %15s%n", "TOTAL ASSALARIADOS", Formatador.formatarValor(totalBruto), Formatador.formatarValor(totalDescontos), Formatador.formatarValor(totalLiquido));

        return totalBruto;
    }

    private BigDecimal imprimirSecaoComissionados(PrintWriter arquivo, List<RegistroPagamento> registros)
    {
        arquivo.println("===============================================================================================================================");
        arquivo.println("===================== COMISSIONADOS ===========================================================================================");
        arquivo.println("===============================================================================================================================");
        arquivo.println("Nome                  Fixo     Vendas   Comissao Salario Bruto Descontos Salario Liquido Metodo");
        arquivo.println("===================== ======== ======== ======== ============= ========= =============== ======================================");

        BigDecimal totalFixo = BigDecimal.ZERO, totalVendas = BigDecimal.ZERO, totalComissao = BigDecimal.ZERO;
        BigDecimal totalBruto = BigDecimal.ZERO, totalDescontos = BigDecimal.ZERO, totalLiquido = BigDecimal.ZERO;

        for(RegistroPagamento registro : registros)
        {
            if(!"comissionado".equals(registro.getTipoEmpregado()))
            {
                continue;
            }

            registro.imprimirLinha(arquivo);

            totalFixo = totalFixo.add(registro.getSalarioFixo());
            totalVendas = totalVendas.add(registro.getVendas());
            totalComissao = totalComissao.add(registro.getComissao());
            totalBruto = totalBruto.add(registro.getSalarioBruto());
            totalDescontos = totalDescontos.add(registro.getDescontos());
            totalLiquido = totalLiquido.add(registro.getSalarioLiquido());
        }

        arquivo.println();
        arquivo.printf("%-21s %8s %8s %8s %13s %9s %15s%n", "TOTAL COMISSIONADOS", Formatador.formatarValor(totalFixo), Formatador.formatarValor(totalVendas), Formatador.formatarValor(totalComissao), Formatador.formatarValor(totalBruto), Formatador.formatarValor(totalDescontos), Formatador.formatarValor(totalLiquido));

        return totalBruto;
    }
}
