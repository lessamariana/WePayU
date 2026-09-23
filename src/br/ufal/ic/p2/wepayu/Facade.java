package br.ufal.ic.p2.wepayu;

import br.ufal.ic.p2.wepayu.Exception.ComissaoInvalidaException;
import br.ufal.ic.p2.wepayu.Exception.EmpregadoNaoExisteException;
import br.ufal.ic.p2.wepayu.Exception.IdentificacaoEmpregadoInvalidaException;
import br.ufal.ic.p2.wepayu.Exception.SalarioInvalidoException;
import br.ufal.ic.p2.wepayu.models.Empregado;
import br.ufal.ic.p2.wepayu.models.EmpregadoFactory;

import java.math.BigDecimal;
import java.util.LinkedHashMap; /* Estrutura de dados em pares, como uma lista encadeada */
import java.util.Map;

/*    Vai receber os comandos dos testes,localizar os objetos necessários e delegar as regras para as classes de negócio. */

public class Facade
{

    /* Guarda os empregados cadastrados. LinkedHashMap mantém a ordem de inserção, o que será útil posteriormente para buscas por nome. */

    private final Map<String, Empregado> empregados;

    private final EmpregadoFactory empregadoFactory;

    /* Criação de Identificador Único (inicie em 1) para cada funcionário criado: Próximo número que será utilizado como identificador.*/
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

    public void removerEmpregado(String id) throws EmpregadoNaoExisteException
    {
        if (!empregados.containsKey(id))
        {
            throw new EmpregadoNaoExisteException();
        }

        empregados.remove(id);
    }
}