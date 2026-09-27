package br.ufal.ic.p2.wepayu;

import br.ufal.ic.p2.wepayu.Exception.*;
import br.ufal.ic.p2.wepayu.models.*;

import java.beans.ExceptionListener;
import java.beans.XMLEncoder;
import java.beans.XMLDecoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/*Classe responsável pela persistência do sistema.
 * A gravação é feita usando XMLEncoder.
 * A leitura é feita usando XMLDecoder. */

public class Persistencia
{
    //Nome do arquivo onde o estado do sistema será armazenado

    private static final String ARQUIVO = "wepayu.xml";

    // Salvando o estado atual do sistema
    public static void salvar(Map<String, Empregado> empregados, int proximoId)
    {
        /*transforma o estado do sistema em objetos simples*/
        DadosSistema dados = criarDadosSistema(empregados, proximoId);

        // Guarda possíveis erros relatados pelo XMLEncoder

        ErroPersistencia erro = new ErroPersistencia();

        try
        {
            XMLEncoder encoder = new XMLEncoder(new BufferedOutputStream(new FileOutputStream(ARQUIVO)));

            encoder.setExceptionListener(erro);

            // Grava o objeto no XML

            encoder.writeObject(dados);
            encoder.flush();
            encoder.close();
        }
        catch (FileNotFoundException e)
        {
            throw new PersistenciaException("Nao foi possivel salvar o sistema.", e);
        }

        if (erro.getErro() != null)
        {
            throw new PersistenciaException("Nao foi possivel salvar o sistema.", erro.getErro());
        }
    }

    // Carrega o estado que foi salvo antes, se o arquivo não existur retorna null.

    public static DadosSistema carregar()
    {
        try
        {
            XMLDecoder decoder = new XMLDecoder(new BufferedInputStream(new FileInputStream(ARQUIVO)));

            Object objeto = decoder.readObject();

            decoder.close();

            return (DadosSistema) objeto;
        }
        catch(FileNotFoundException e)
        {
            return null;
        }
        catch(RuntimeException e)
        {
            throw new PersistenciaException("Nao foi possivel carregar o sistema.", e);
        }
    }


    // Converte o mapa de empregados para um objeto simples paraser gravado pelo XMLEncoder

    private static DadosSistema criarDadosSistema(Map<String, Empregado> empregados, int proximoId)
    {
        DadosSistema dados = new DadosSistema();

        dados.setProximoId(proximoId);

        for(Map.Entry<String, Empregado> entrada : empregados.entrySet())
        {
            DadosEmpregado dadosEmpregado = criarDadosEmpregado(entrada.getKey(), entrada.getValue());
            dados.getEmpregados().add(dadosEmpregado);
        }

        return dados;
    }

    private static DadosEmpregado criarDadosEmpregado(String id, Empregado empregado)
    {
        DadosEmpregado dados = new DadosEmpregado();

        dados.setId(id);
        dados.setNome(empregado.getNome());
        dados.setEndereco(empregado.getEndereco());
        dados.setTipo(empregado.getTipo());
        dados.setSalario(empregado.getSalario().toString());


        dados.setComissao(empregado.getComissaoPersistencia());
        dados.setSindicalizado(empregado.participaSindicato());
        dados.setIdSindicato(empregado.getIdSindicato());
        dados.setTaxaSindical(empregado.getTaxaSindical().toString());
        dados.setMetodoPagamento(empregado.getMetodoPagamento());
        dados.setDataContratacao(dataParaTexto(empregado.getDataContratacao()));
        dados.setDataUltimoPagamento(dataParaTexto(empregado.getDataUltimoPagamento()));


        // Se o método de pagamento for banco, também precisa salvar os dados

        if("banco".equals(empregado.getMetodoPagamento()))
        {
            dados.setBanco(empregado.getAtributo("banco"));
            dados.setAgencia(empregado.getAtributo("agencia"));
            dados.setContaCorrente(empregado.getAtributo("contaCorrente"));
        }


        // Salva as taxas de serviço
        for(TaxaServico taxa : empregado.getTaxasServicoPersistencia())
        {
            DadosTaxaServico taxaDados = new DadosTaxaServico();
            taxaDados.setData(dataParaTexto(taxa.getData()));
            taxaDados.setValor(taxa.getValor().toString());
            dados.getTaxasServico().add(taxaDados);
        }


        // Salva os cartões de ponto.
        for(CartaoPonto cartao : empregado.getCartoesPontoPersistencia())
        {
            DadosCartaoPonto cartaoDados = new DadosCartaoPonto();
            cartaoDados.setData(dataParaTexto(cartao.getData()));
            cartaoDados.setHoras(cartao.getHoras().toString());
            dados.getCartoesPonto().add(cartaoDados);
        }


        // Salva as vendas.

        for(Venda venda : empregado.getVendasPersistencia())
        {
            DadosVenda vendaDados = new DadosVenda();
            vendaDados.setData(dataParaTexto(venda.getData()));
            vendaDados.setValor(venda.getValor().toString());
            dados.getVendas().add(vendaDados);
        }

        return dados;
    }


    // Reconstrói empregado a partir dos dados lidos do XML

    public static Empregado criarEmpregado(DadosEmpregado dados, EmpregadoFactory factory)
    {
        BigDecimal salario = new BigDecimal(dados.getSalario());
        Empregado empregado;

        if("comissionado".equals(dados.getTipo()))
        {
            empregado = factory.criar(dados.getNome(), dados.getEndereco(), dados.getTipo(), salario, new BigDecimal(dados.getComissao()));
        }
        else
        {
            empregado = factory.criar(dados.getNome(), dados.getEndereco(), dados.getTipo(), salario);
        }

        empregado.setDataContratacao(textoParaData(dados.getDataContratacao()));
        empregado.setDataUltimoPagamento(textoParaData(dados.getDataUltimoPagamento()));

        if("banco".equals(dados.getMetodoPagamento()))
        {
            empregado.alteraMetodoPagamento(new PagamentoBanco(dados.getBanco(), dados.getAgencia(), dados.getContaCorrente()));
        }
        else if("correios".equals(dados.getMetodoPagamento()))
        {
            empregado.alteraMetodoPagamento(new PagamentoCorreios());
        }
        else
        {
            empregado.alteraMetodoPagamento(new PagamentoEmMaos());
        }

        if(dados.isSindicalizado())
        {
            empregado.sindicalizar(dados.getIdSindicato(), new BigDecimal(dados.getTaxaSindical()));

            for(DadosTaxaServico taxaDados : dados.getTaxasServico())
            {
                empregado.lancaTaxaServico(textoParaData(taxaDados.getData()), new BigDecimal(taxaDados.getValor()));
            }
        }

        try
        {
            for(DadosCartaoPonto cartaoDados : dados.getCartoesPonto())
            {
                empregado.lancaCartao(textoParaData(cartaoDados.getData()), new BigDecimal(cartaoDados.getHoras()));
            }

            for(DadosVenda vendaDados : dados.getVendas())
            {
                empregado.lancaVenda(textoParaData(vendaDados.getData()), new BigDecimal(vendaDados.getValor()));
            }
        }
        catch(EmpregadoNaoEhHoristaException | DataInvalidaException | HorasInvalidaException | EmpregadoNaoEhComissionadoException e)
        {
            throw new PersistenciaException("Estado persistido invalido.", e);
        }

        return empregado;
    }

    private static String dataParaTexto(LocalDate data)
    {
        if(data == null)
        {
            return null;
        }

        return data.toString();
    }

    private static LocalDate textoParaData(String data)
    {
        if(data == null || data.isEmpty())
        {
            return null;
        }

        return LocalDate.parse(data);
    }


    // Recebe erros encontrados pelo XMLEncoder

    private static class ErroPersistencia implements ExceptionListener
    {
        private Exception erro;

        @Override
        public void exceptionThrown(Exception e)
        {
            if(erro == null)
            {
                erro = e;
            }
        }

        public Exception getErro()
        {
            return erro;
        }
    }

    public static class DadosSistema
    {
        private int proximoId;
        private List<DadosEmpregado> empregados = new ArrayList<>();

        public DadosSistema()
        {
        }

        public int getProximoId()
        {
            return proximoId;
        }

        public void setProximoId(int proximoId)
        {
            this.proximoId = proximoId;
        }

        public List<DadosEmpregado> getEmpregados()
        {
            return empregados;
        }

        public void setEmpregados(List<DadosEmpregado> empregados)
        {
            this.empregados = empregados;
        }
    }

    public static class DadosEmpregado
    {
        private String id;
        private String nome;
        private String endereco;
        private String tipo;
        private String salario;
        private String comissao;

        private boolean sindicalizado;

        private String idSindicato;
        private String taxaSindical;

        private String metodoPagamento;

        private String banco;
        private String agencia;
        private String contaCorrente;

        private String dataContratacao;
        private String dataUltimoPagamento;

        private List<DadosTaxaServico> taxasServico = new ArrayList<>();
        private List<DadosCartaoPonto> cartoesPonto = new ArrayList<>();
        private List<DadosVenda> vendas = new ArrayList<>();


        public DadosEmpregado()
        {
        }

        public String getId()
        {
            return id;
        }

        public void setId(String id)
        {
            this.id = id;
        }

        public String getNome()
        {
            return nome;
        }

        public void setNome(String nome)
        {
            this.nome = nome;
        }

        public String getEndereco()
        {
            return endereco;
        }

        public void setEndereco(String endereco)
        {
            this.endereco = endereco;
        }

        public String getTipo()
        {
            return tipo;
        }

        public void setTipo(String tipo)
        {
            this.tipo = tipo;
        }

        public String getSalario()
        {
            return salario;
        }

        public void setSalario(String salario)
        {
            this.salario = salario;
        }

        public String getComissao()
        {
            return comissao;
        }

        public void setComissao(String comissao)
        {
            this.comissao = comissao;
        }

        public boolean isSindicalizado()
        {
            return sindicalizado;
        }

        public void setSindicalizado(boolean sindicalizado)
        {
            this.sindicalizado = sindicalizado;
        }

        public String getIdSindicato()
        {
            return idSindicato;
        }
        public void setIdSindicato(String idSindicato)
        {
            this.idSindicato = idSindicato;
        }


        public String getTaxaSindical()
        {
            return taxaSindical;
        }

        public void setTaxaSindical(String taxaSindical)
        {
            this.taxaSindical = taxaSindical;
        }


        public String getMetodoPagamento()
        {
            return metodoPagamento;
        }

        public void setMetodoPagamento(String metodoPagamento)
        {
            this.metodoPagamento = metodoPagamento;
        }

        public String getBanco()
        {
            return banco;
        }

        public void setBanco(String banco)
        {
            this.banco = banco;
        }


        public String getAgencia()
        {
            return agencia;
        }

        public void setAgencia(String agencia)
        {
            this.agencia = agencia;
        }

        public String getContaCorrente()
        {
            return contaCorrente;
        }

        public void setContaCorrente(String contaCorrente)
        {
            this.contaCorrente = contaCorrente;
        }

        public String getDataContratacao()
        {
            return dataContratacao;
        }

        public void setDataContratacao(String dataContratacao)
        {
            this.dataContratacao = dataContratacao;
        }

        public String getDataUltimoPagamento()
        {
            return dataUltimoPagamento;
        }

        public void setDataUltimoPagamento(String dataUltimoPagamento)
        {
            this.dataUltimoPagamento = dataUltimoPagamento;
        }

        public List<DadosTaxaServico> getTaxasServico()
        {
            return taxasServico;
        }

        public void setTaxasServico(List<DadosTaxaServico> taxasServico)
        {
            this.taxasServico = taxasServico;
        }

        public List<DadosCartaoPonto> getCartoesPonto()
        {
            return cartoesPonto;
        }

        public void setCartoesPonto(List<DadosCartaoPonto> cartoesPonto)
        {
            this.cartoesPonto = cartoesPonto;
        }

        public List<DadosVenda> getVendas()
        {
            return vendas;
        }

        public void setVendas(List<DadosVenda> vendas)
        {
            this.vendas = vendas;
        }
    }


    public static class DadosTaxaServico
    {
        private String data;
        private String valor;

        public DadosTaxaServico()
        {
        }

        public String getData()
        {
            return data;
        }

        public void setData(String data)
        {
            this.data = data;
        }

        public String getValor()
        {
            return valor;
        }

        public void setValor(String valor)
        {
            this.valor = valor;
        }
    }

    public static class DadosCartaoPonto
    {
        private String data;
        private String horas;

        public DadosCartaoPonto()
        {
        }

        public String getData()
        {
            return data;
        }

        public void setData(String data)
        {
            this.data = data;
        }

        public String getHoras()
        {
            return horas;
        }

        public void setHoras(String horas)
        {
            this.horas = horas;
        }
    }


    public static class DadosVenda
    {
        private String data;
        private String valor;

        public DadosVenda()
        {
        }

        public String getData()
        {
            return data;
        }

        public void setData(String data)
        {
            this.data = data;
        }

        public String getValor()
        {
            return valor;
        }

        public void setValor(String valor)
        {
            this.valor = valor;
        }
    }
}