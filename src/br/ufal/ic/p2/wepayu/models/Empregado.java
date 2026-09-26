/*Classe pai para os tipos de empregados*/

package br.ufal.ic.p2.wepayu.models;

import br.ufal.ic.p2.wepayu.Exception.EmpregadoNaoEhSindicalizadoException;
import br.ufal.ic.p2.wepayu.Exception.EmpregadoNaoEhComissionadoException;
import br.ufal.ic.p2.wepayu.Exception.DataInvalidaException;
import br.ufal.ic.p2.wepayu.Exception.EmpregadoNaoEhHoristaException;
import br.ufal.ic.p2.wepayu.Exception.HorasInvalidaException;
import br.ufal.ic.p2.wepayu.Exception.AtributoNaoExisteException;
import br.ufal.ic.p2.wepayu.Exception.EnderecoInvalidoException;
import br.ufal.ic.p2.wepayu.Exception.NomeInvalidoException;
import br.ufal.ic.p2.wepayu.Exception.SalarioInvalidoException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public abstract class Empregado
{
    private String nome;
    private String endereco;
    private BigDecimal salario;

    private boolean sindicalizado;
    private String idSindicato;
    private BigDecimal taxaSindical;
    private MetodoPagamento metodoPagamento;

    private final List<TaxaServico> taxasServico;


    /*Usei protected para as classes filhas conseguirem acessar
    * Tive que atualizar para us_5, mesma coisa em cima*/

    protected Empregado(String nome, String endereco, BigDecimal salario)
    {
        validarNome(nome);
        validarEndereco(endereco);
        validarSalario(salario);

        this.nome = nome;
        this.endereco = endereco;
        this.salario = salario.setScale(2, RoundingMode.HALF_UP);

        this.sindicalizado = false;
        this.idSindicato = null;
        this.taxaSindical = BigDecimal.ZERO;
        this.taxasServico = new ArrayList<>();
        this.metodoPagamento = new PagamentoEmMaos();
    }

    /*Validações para verificar possíveis erros*/

    private void validarNome(String nome)
    {
        if (nome == null || nome.isEmpty())
        {
            throw new NomeInvalidoException();
        }
    }

    private void validarEndereco(String endereco)
    {
        if (endereco == null || endereco.isEmpty())
        {
            throw new EnderecoInvalidoException();
        }
    }

    private void validarSalario(BigDecimal salario)
    {
        if (salario == null || salario.compareTo(BigDecimal.ZERO) < 0)
        {
            throw new SalarioInvalidoException("Salario deve ser nao-negativo.");
        }
    }

    /*Se não der erro, coletar o dados*/

    public String getNome()
    {
        return nome;
    }

    public String getEndereco()
    {
        return endereco;
    }

    public BigDecimal getSalario()
    {
        return salario;
    }

    public boolean participaSindicato()
    {
        return sindicalizado;
    }


    /*Define o tipo de empregado, já que tem tipos diferentes*/

    public String getTipo()
    {
        return getTipoEmpregado();
    }

    /*Verificação dos atributos*/

    public String getAtributo(String atributo)
    {

        switch (atributo)
        {
            case "nome":
                return nome;

            case "endereco":
                return endereco;

            case "tipo":
                return getTipo();

            case "salario":
                return formatarValor(salario);

            case "sindicalizado":
                return String.valueOf(sindicalizado);

            default:
                return getAtributoEspecifico(atributo);
        }
    }

    /*Verificação associada a subclasse, para que possa verificar atributos
    * Tive que atualizar por causa dos novos metodos de pagamento*/

    protected String getAtributoEspecifico(String atributo)
    {
        if("comissao".equals(atributo))
        {
            throw new EmpregadoNaoEhComissionadoException();
        }

        if("metodoPagamento".equals(atributo))
        {
            return metodoPagamento.getTipo();
        }

        if("banco".equals(atributo))
        {
            return metodoPagamento.getBanco();
        }

        if("agencia".equals(atributo))
        {
            return metodoPagamento.getAgencia();
        }

        if("contaCorrente".equals(atributo))
        {
            return metodoPagamento.getContaCorrente();
        }

        if("idSindicato".equals(atributo))
        {
            if(!sindicalizado)
            {
                throw new EmpregadoNaoEhSindicalizadoException();
            }

            return idSindicato;
        }

        if("taxaSindical".equals(atributo))
        {
            if(!sindicalizado)
            {
                throw new EmpregadoNaoEhSindicalizadoException();
            }

            return formatarValor(taxaSindical);
        }

        throw new AtributoNaoExisteException();
    }

    /*Valores com vírgula e duas casas decimais*/

    protected abstract String getTipoEmpregado();

    protected String formatarValor(BigDecimal valor)
    {
        return valor.setScale(2, RoundingMode.HALF_UP).toString().replace('.', ',');
    }

    /*Adicionei na classe empregado porque foi enfatizado que é interessante evitar usar instanceof
    * e tentar fazer o polimorfismo de forma correta e foi o jeito que achei que faz sentido*/

    public void lancaCartao(LocalDate data, BigDecimal horas) throws EmpregadoNaoEhHoristaException, DataInvalidaException, HorasInvalidaException
    {
        throw new EmpregadoNaoEhHoristaException();
    }

    public BigDecimal getHorasNormaisTrabalhadas(LocalDate dataInicial, LocalDate dataFinal) throws EmpregadoNaoEhHoristaException, DataInvalidaException
    {
        throw new EmpregadoNaoEhHoristaException();
    }

    public BigDecimal getHorasExtrasTrabalhadas(LocalDate dataInicial, LocalDate dataFinal) throws EmpregadoNaoEhHoristaException, DataInvalidaException
    {
        throw new EmpregadoNaoEhHoristaException();
    }

    /*Preparando o terreno para us_4, associado a vendas
    * Colocando os metodos aqui como fiz com outros*/

    public void lancaVenda(LocalDate data, BigDecimal valor) throws EmpregadoNaoEhComissionadoException
    {
        throw new EmpregadoNaoEhComissionadoException();
    }

    public BigDecimal getVendasRealizadas(LocalDate dataInicial, LocalDate dataFinal) throws EmpregadoNaoEhComissionadoException
    {
        throw new EmpregadoNaoEhComissionadoException();
    }

    /*Metódos associados a us_5, fazer sindicalizado, desfazer, coletar dados e associados a taxa de associação*/

    public void sindicalizar(
            String idSindicato,
            BigDecimal taxaSindical)
    {
        this.sindicalizado = true;
        this.idSindicato = idSindicato;
        this.taxaSindical = taxaSindical;
    }

    public void dessindicalizar()
    {
        this.sindicalizado = false;
        this.idSindicato = null;
        this.taxaSindical = BigDecimal.ZERO;
    }

    public String getIdSindicato()
    {
        return idSindicato;
    }

    public BigDecimal getTaxaSindical()
    {
        return taxaSindical;
    }

    /*Registra a taxa de serviço. Só empregados sindicalizados recebem esse tipo de cobrança.*/

    public void lancaTaxaServico(
            LocalDate data,
            BigDecimal valor)
            throws EmpregadoNaoEhSindicalizadoException
    {
        if (!sindicalizado)
        {
            throw new EmpregadoNaoEhSindicalizadoException();
        }

        taxasServico.add(new TaxaServico(data, valor));
    }

    public BigDecimal getTaxasServico(LocalDate dataInicial, LocalDate dataFinal) throws EmpregadoNaoEhSindicalizadoException
    {
        if(!sindicalizado)
        {
            throw new EmpregadoNaoEhSindicalizadoException();
        }

        BigDecimal total = BigDecimal.ZERO;

        for(TaxaServico taxa : taxasServico)
        {
            LocalDate dataTaxa = taxa.getData();

            if(!dataTaxa.isBefore(dataInicial) && dataTaxa.isBefore(dataFinal))
            {
                total = total.add(taxa.getValor());
            }
        }

        return total;

    }

    public void alteraMetodoPagamento(MetodoPagamento metodoPagamento)
    {
        this.metodoPagamento = metodoPagamento;
    }

    public String getMetodoPagamento()
    {
        return metodoPagamento.getTipo();
    }

    // Metodos para permitir edição de atributos que podem ser alterados
    public void alteraNome(String nome)
    {
        validarNome(nome);
        this.nome = nome;
    }

    public void alteraEndereco(String endereco)
    {
        validarEndereco(endereco);
        this.endereco = endereco;
    }

    public void alteraComissao(BigDecimal comissao)
    {
        throw new EmpregadoNaoEhComissionadoException();
    }

    // Copia para não sumir com objeto
    public void copiarDadosAlteraveis(Empregado outro)
    {
        this.metodoPagamento = outro.metodoPagamento;

        this.sindicalizado = outro.sindicalizado;

        this.idSindicato = outro.idSindicato;

        this.taxaSindical = outro.taxaSindical;

        this.taxasServico.addAll(outro.taxasServico);
    }


}
