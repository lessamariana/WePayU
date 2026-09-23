package br.ufal.ic.p2.wepayu;

import br.ufal.ic.p2.wepayu.Exception.DataInvalidaException;
import br.ufal.ic.p2.wepayu.Exception.EmpregadoNaoEhHoristaException;
import br.ufal.ic.p2.wepayu.Exception.HorasInvalidaException;
import br.ufal.ic.p2.wepayu.Exception.ComissaoInvalidaException;
import br.ufal.ic.p2.wepayu.Exception.EmpregadoNaoExisteException;
import br.ufal.ic.p2.wepayu.Exception.IdentificacaoEmpregadoInvalidaException;
import br.ufal.ic.p2.wepayu.Exception.SalarioInvalidoException;
import br.ufal.ic.p2.wepayu.models.Empregado;
import br.ufal.ic.p2.wepayu.models.EmpregadoFactory;
import br.ufal.ic.p2.wepayu.models.DataTratamento;

import java.time.LocalDate;
import java.math.BigDecimal;
import java.util.LinkedHashMap; /* Estrutura de dados em pares, como uma lista encadeada */
import java.util.Map;

/*    Vai receber os comandos dos testes,localizar os objetos necessários e delegar as regras para as classes de negócio. */

public class Facade
{

    /* Guarda os empregados cadastrados. LinkedHashMap mantém a ordem de inserção, o que será útil posteriormente para buscas por nome. */

    private final Map<String, Empregado> empregados;

    private final EmpregadoFactory empregadoFactory;

    /* Criação de Identificador Único (iniciei em 1) para cada funcionário criado: Próximo número que será utilizado como identificador.*/
    private int proximoId;

    public Facade()
    {
        empregados = new LinkedHashMap<>();
        empregadoFactory = new EmpregadoFactory();
        proximoId = 1;
    }

    /* Limpa o sistema e reinicia a geração dos IDs.*/

    public void zerarSistema()
    {
        empregados.clear();
        proximoId = 1;
    }

    /*Cria horista ou CLT.*/

    public String criarEmpregado(String nome, String endereco, String tipo, String salario)
    {

        BigDecimal salarioConvertido = converterSalario(salario);
        Empregado empregado = empregadoFactory.criar(nome, endereco, tipo, salarioConvertido);

        return adicionarEmpregado(empregado);
    }

    /*Cria empregado com comissão.*/

    public String criarEmpregado(String nome, String endereco, String tipo, String salario, String comissao)
    {

        BigDecimal salarioConvertido = converterSalario(salario);
        BigDecimal comissaoConvertida = converterComissao(comissao);

        Empregado empregado = empregadoFactory.criar(nome, endereco, tipo, salarioConvertido, comissaoConvertida);

        return adicionarEmpregado(empregado);
    }

    /*Retorna um atributo do empregado.*/

    public String getAtributoEmpregado(String emp, String atributo) throws EmpregadoNaoExisteException
    {
        return buscarEmpregado(emp).getAtributo(atributo);
    }

    public void encerrarSistema()
    {
        // Coloquei para testar, não tá funcionando ainda.
    }

    /*Adicionar um empregado ao mapa e gerar seu o ID.*/

    private String adicionarEmpregado(Empregado empregado)
    {
        String id = String.valueOf(proximoId++);
        empregados.put(id, empregado);

        return id;
    }

    /*Procurar empregado pelo ID.*/

    private Empregado buscarEmpregado(String id) throws EmpregadoNaoExisteException
    {

        if (id == null || id.isEmpty())
        {
            throw new IdentificacaoEmpregadoInvalidaException();
        }

        Empregado empregado = empregados.get(id);

        if (empregado == null)
        {
            throw new EmpregadoNaoExisteException();
        }

        return empregado;
    }

    /* Conversão  do salário recebido para BigDecimal.*/

    private BigDecimal converterSalario(String salario)
    {

        if (salario == null || salario.isEmpty())
        {
            throw new SalarioInvalidoException("Salario nao pode ser nulo.");
        }

        try
        {
            BigDecimal valor = new BigDecimal(salario.replace(',', '.'));

            if (valor.compareTo(BigDecimal.ZERO) < 0)
            {
                throw new SalarioInvalidoException("Salario deve ser nao-negativo.");
            }

            return valor;

        } catch (NumberFormatException e)
        {
            throw new SalarioInvalidoException("Salario deve ser numerico.");
        }
    }

    /*Converte a comissão para BigDecimal.*/

    private BigDecimal converterComissao(String comissao)
    {

        if (comissao == null || comissao.isEmpty())
        {
            throw new ComissaoInvalidaException("Comissao nao pode ser nula.");
        }

        try
        {

            BigDecimal valor = new BigDecimal(comissao.replace(',', '.'));

            if (valor.compareTo(BigDecimal.ZERO) < 0)
            {
                throw new ComissaoInvalidaException("Comissao deve ser nao-negativa.");
            }

            return valor;

        } catch (NumberFormatException e)
        {

            throw new ComissaoInvalidaException("Comissao deve ser numerica.");
        }
    }

    /* Adicionei esse metodo para atender o teste us_2 de remover funcionario*/
    /*Usei o containsKey por causa do Map, achei mais simples*/

    public void removerEmpregado(String id) throws EmpregadoNaoExisteException, IdentificacaoEmpregadoInvalidaException
    {

        // Precisei ajustar pra verificar se o id foi informado
        // já que o teste da us_2 pede uma mensagem específica para esse caso.

        if (id == null || id.trim().isEmpty()) //verificando se a string tá vazia
        {
            throw new IdentificacaoEmpregadoInvalidaException();
        }

        // Depois verifica se existe um empregado com essa id
        if (!empregados.containsKey(id))
        {
            throw new EmpregadoNaoExisteException();
        }

        // Se passou pelas duas validações, remove o empregado.
        empregados.remove(id);
    }

    //Adicionando metodos para tratamento de cartoes de ponto pros testes de us_3

    public void lancaCartao(String id, String data, String horas) throws EmpregadoNaoExisteException, IdentificacaoEmpregadoInvalidaException, EmpregadoNaoEhHoristaException, DataInvalidaException, HorasInvalidaException
    {
        if(id == null || id.trim().isEmpty())
        {
            throw new IdentificacaoEmpregadoInvalidaException();
        }

        Empregado empregado = buscarEmpregado(id);

        LocalDate dataConvertida = DataTratamento.converterData(data, "Data invalida.");

        BigDecimal horasConvertidas;

        try
        {
            horasConvertidas = new BigDecimal(horas.replace(",", "."));
        } catch (NumberFormatException e)
        {
            throw new HorasInvalidaException();
        }

        empregado.lancaCartao(dataConvertida, horasConvertidas);
    }

    //Estava usando BigDecimal, mas estava dando erro na formatação, então achei melhor mudar para String

    public String getHorasNormaisTrabalhadas(String id, String dataInicial, String dataFinal) throws EmpregadoNaoExisteException, IdentificacaoEmpregadoInvalidaException, EmpregadoNaoEhHoristaException, DataInvalidaException
    {

        if(id == null || id.trim().isEmpty())
        {
            throw new IdentificacaoEmpregadoInvalidaException();
        }

        Empregado empregado = buscarEmpregado(id);

        LocalDate inicio = DataTratamento.converterData(dataInicial, "Data inicial invalida.");

        LocalDate fim = DataTratamento.converterData(dataFinal, "Data final invalida.");

        if(!inicio.isBefore(fim))
        {
            if(inicio.equals(fim))
            {
                return "0";
            }

            throw new DataInvalidaException("Data inicial nao pode ser posterior aa data final.");
        }

        return empregado.getHorasNormaisTrabalhadas(inicio, fim).toString().replace('.', ',');
    }

    public String getHorasExtrasTrabalhadas(String id, String dataInicial, String dataFinal) throws EmpregadoNaoExisteException, IdentificacaoEmpregadoInvalidaException, EmpregadoNaoEhHoristaException, DataInvalidaException
    {
        if(id == null || id.trim().isEmpty())
        {
            throw new IdentificacaoEmpregadoInvalidaException();
        }

        Empregado empregado = buscarEmpregado(id);

        LocalDate inicio = DataTratamento.converterData(dataInicial, "Data inicial invalida.");

        LocalDate fim = DataTratamento.converterData(dataFinal, "Data final invalida.");

        if(!inicio.isBefore(fim))
        {
            if(inicio.equals(fim))
            {
                return "0";
            }

            throw new DataInvalidaException("Data inicial nao pode ser posterior aa data final."); //nos testes tá assim então deixei
        }

        return empregado.getHorasExtrasTrabalhadas(inicio, fim).toString().replace('.', ',');
    }


}