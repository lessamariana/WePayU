package br.ufal.ic.p2.wepayu.models.folha;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import br.ufal.ic.p2.wepayu.Exception.DataInvalidaException;
import br.ufal.ic.p2.wepayu.Exception.EmpregadoNaoEhHoristaException;
import br.ufal.ic.p2.wepayu.models.empregado.Empregado;
import br.ufal.ic.p2.wepayu.utilitarios.DataTratamento;

public class FolhaPagamento
{
    private final LocalDate dataPagamento;

    public FolhaPagamento(LocalDate dataPagamento)
    {
        this.dataPagamento = dataPagamento;
    }

    // Cria a folha a partir da data em texto recebida pela Facade

    public static FolhaPagamento criar(String data) throws DataInvalidaException
    {
        return new FolhaPagamento(DataTratamento.converterData(data, "Data invalida."));
    }

    public ResultadoPagamento calcular(Empregado empregado) throws EmpregadoNaoEhHoristaException, DataInvalidaException
    {
        BigDecimal bruto = empregado.calcularPagamento(dataPagamento);

        BigDecimal descontos = BigDecimal.ZERO;

        LocalDate inicio = empregado.getInicioPeriodoAtual(dataPagamento);

        if(empregado.participaSindicato())
        {
            descontos = descontos.add(empregado.calcularTaxaSindical(inicio, dataPagamento));
        }

        descontos = descontos.add(empregado.calcularTaxasServico(inicio, dataPagamento));

        return new ResultadoPagamento(bruto, descontos);
    }

    // Soma o salário bruto de quem deve receber na data (antes ficava em totalFolha na Facade)

    public BigDecimal calcularTotal(Collection<Empregado> empregados) throws DataInvalidaException, EmpregadoNaoEhHoristaException
    {
        BigDecimal total = BigDecimal.ZERO;

        for(Empregado empregado : empregados)
        {
            if(empregado.deveReceber(dataPagamento))
            {
                total = total.add(calcular(empregado).getSalarioBruto());
            }
        }

        return total;
    }

    // Roda a folha: calcula, escreve o arquivo e registra o pagamento (antes ficava em rodaFolha na Facade)

    public void rodar(Collection<Empregado> empregados, String saida) throws DataInvalidaException, EmpregadoNaoEhHoristaException
    {
        // Monta os registros de quem deve receber nesta data, e já separa quem teve pagamento efetivo (bruto > 0) de quem não teve
        List<RegistroPagamento> registros = new ArrayList<>();
        List<Empregado> empregadosComPagamentoEfetivo = new ArrayList<>();

        for(Empregado empregado : empregados)
        {
            if(empregado.deveReceber(dataPagamento))
            {
                ResultadoPagamento resultado = calcular(empregado);
                registros.add(empregado.gerarRegistro(dataPagamento, resultado));

                if(empregado.recebeuPagamentoEfetivo(resultado))
                {
                    empregadosComPagamentoEfetivo.add(empregado);
                }
            }
        }

        registros.sort(Comparator.comparing(RegistroPagamento::getNome));

        new RelatorioFolha(dataPagamento, registros).escrever(saida);

        // So avancamos a data do ultimo pagamento de quem realmente recebeu algo (bruto > 0).
        // Quem ficou zerado (ex.: horista sem horas suficientes) tem a data mantida,
        // para que a taxa sindical e as horas se acumulem corretamente ate o proximo pagamento de verdade
        for(Empregado empregado : empregadosComPagamentoEfetivo)
        {
            empregado.setDataUltimoPagamento(dataPagamento);
        }
    }
}
