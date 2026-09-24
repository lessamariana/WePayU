package br.ufal.ic.p2.wepayu;

import br.ufal.ic.p2.wepayu.Exception.EmpregadoNaoEhSindicalizadoException;
import br.ufal.ic.p2.wepayu.Exception.IdentificacaoMembroInvalidaException;
import br.ufal.ic.p2.wepayu.Exception.IdentificacaoSindicatoJaExisteException;
import br.ufal.ic.p2.wepayu.Exception.MembroNaoExisteException;
import br.ufal.ic.p2.wepayu.Exception.ValorInvalidoException;
import br.ufal.ic.p2.wepayu.Exception.EmpregadoNaoEhComissionadoException;
import br.ufal.ic.p2.wepayu.Exception.ValorInvalidoException;
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
import java.math.RoundingMode; //Descobri que ao dividir números ou ajustar casas decimais de um BigDecimal e o resultado não for exato, o Java não sabe o que fazer com os números que sobram, então coloquei para auxiliar no arredondamento.

/*    Vai receber os comandos dos testes,localizar os objetos necessários e delegar as regras para as classes de negócio. */

public class Facade
{

    /* Guarda os empregados cadastrados. LinkedHashMap mantém a ordem de inserção, o que será útil posteriormente para buscas por nome. */

    private final Map<String, Empregado> empregados;
    private final Map<String, String> membrosSindicato;

    private final EmpregadoFactory empregadoFactory;

    /* Criação de Identificador Único (iniciei em 1) para cada funcionário criado: Próximo número que será utilizado como identificador.*/
    private int proximoId;

    public Facade()
    {
        empregados = new LinkedHashMap<>();
        membrosSindicato = new LinkedHashMap<>();
        empregadoFactory = new EmpregadoFactory();
        proximoId = 1;
    }

    /* Limpa o sistema e reinicia a geração dos IDs.*/

    public void zerarSistema()
    {
        empregados.clear();
        membrosSindicato.clear();
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

    /* Lança uma venda para um empregado.A venda só pode ser registrada para um empregado comissionado.*/
    public void lancaVenda(String id, String data, String valor) throws EmpregadoNaoExisteException, IdentificacaoEmpregadoInvalidaException, EmpregadoNaoEhComissionadoException, DataInvalidaException, ValorInvalidoException
    {
        // Verifica se o ID foi informado.
        if(id == null || id.trim().isEmpty())
        {
            throw new IdentificacaoEmpregadoInvalidaException();
        }

        // Procura o empregado.
        Empregado empregado = buscarEmpregado(id);

        // Converte e valida a data.
        LocalDate dataConvertida = DataTratamento.converterData(data, "Data invalida.");

        // Converte o valor informado para BigDecimal.
        BigDecimal valorConvertido;

        try
        {
            valorConvertido = new BigDecimal(valor.replace(",", "."));
        }
        catch (NumberFormatException e)
        {
            throw new ValorInvalidoException();
        }

        // O valor da venda precisa ser positivo.
        if (valorConvertido.compareTo(BigDecimal.ZERO) <= 0)
        {
            throw new ValorInvalidoException();
        }

        /* EmpregadoComissionado registra a venda. Outros tipos lançam EmpregadoNaoEhComissionadoException.*/
        empregado.lancaVenda(dataConvertida, valorConvertido);
    }

    /*Retorna o total de vendas realizadas pelo empregadodentro do período informado. */

    public String getVendasRealizadas(String id, String dataInicial, String dataFinal) throws EmpregadoNaoExisteException, IdentificacaoEmpregadoInvalidaException, EmpregadoNaoEhComissionadoException, DataInvalidaException
    {
        if(id == null || id.trim().isEmpty())
        {
            throw new IdentificacaoEmpregadoInvalidaException();
        }

        // Procura o empregado.
        Empregado empregado = buscarEmpregado(id);

        // Converte a data inicial.
        LocalDate inicio = DataTratamento.converterData(dataInicial, "Data inicial invalida.");

        // Converte a data final.
        LocalDate fim = DataTratamento.converterData(dataFinal, "Data final invalida.");

        /*A data inicial não pode ser posterior a data final.*/
        if (inicio.isAfter(fim))
        {
            throw new DataInvalidaException("Data inicial nao pode ser posterior aa data final.");
        }

        BigDecimal total = empregado.getVendasRealizadas(inicio, fim);

        // O EasyAccept espera duas casas decimais e vírgula.
        return formatarValor(total);
    }

    public void lancaTaxaServico(String membro, String data, String valor) throws IdentificacaoMembroInvalidaException, MembroNaoExisteException, DataInvalidaException, ValorInvalidoException
    {
        if(membro == null || membro.trim().isEmpty())
        {
            throw new IdentificacaoMembroInvalidaException();
        }

        String idEmpregado = membrosSindicato.get(membro);

        if(idEmpregado == null)
        {
            throw new MembroNaoExisteException();
        }

        Empregado empregado = empregados.get(idEmpregado);

        if(empregado == null)
        {
            throw new MembroNaoExisteException();
        }

        LocalDate dataConvertida = DataTratamento.converterData(data, "Data invalida.");

        BigDecimal valorConvertido;

        try
        {
            valorConvertido = new BigDecimal(valor.replace(",", "."));
        }
        catch(NumberFormatException e)
        {
            throw new ValorInvalidoException();
        }

        if(valorConvertido.compareTo(BigDecimal.ZERO) <= 0)
        {
            throw new ValorInvalidoException();
        }

        empregado.lancaTaxaServico(dataConvertida, valorConvertido);
    }

    /*
     * Retorna o total das taxas de serviço
     * de um empregado dentro de um período.
     */
    public String getTaxasServico(String id, String dataInicial, String dataFinal) throws EmpregadoNaoExisteException, IdentificacaoEmpregadoInvalidaException, EmpregadoNaoEhSindicalizadoException, DataInvalidaException
    {
        if(id == null || id.trim().isEmpty())
        {
            throw new IdentificacaoEmpregadoInvalidaException();
        }

        Empregado empregado = buscarEmpregado(id);

        LocalDate inicio = DataTratamento.converterData(dataInicial, "Data inicial invalida.");

        LocalDate fim = DataTratamento.converterData(dataFinal, "Data final invalida.");

        if(inicio.isAfter(fim))
        {
            throw new DataInvalidaException("Data inicial nao pode ser posterior aa data final.");
        }

        BigDecimal total = empregado.getTaxasServico(inicio, fim);

        return formatarValor(total);
    }

    /*Como em Empregado está como protected, coloquei novamente aqui, caso ache que vai ser muito reutilizado, irei criar uma classe só para isso depois para evitar repetição*/

    private String formatarValor(BigDecimal valor)
    {
        return valor.setScale(2, RoundingMode.HALF_UP).toString().replace('.', ',');
    }

    /* Retira o empregado do sindicato.*/
    private void removerSindicalizacao(String id, Empregado empregado)
    {
        String idSindicato = empregado.getIdSindicato();

        if (idSindicato != null)
        {
            membrosSindicato.remove(idSindicato);
        }

        empregado.dessindicalizar();
    }

    /*Converte a taxa sindical informada para BigDecimal.*/
    private BigDecimal converterTaxaSindical(String taxaSindical)
    {
        if(taxaSindical == null || taxaSindical.isEmpty())
        {
            return BigDecimal.ZERO;
        }

        try
        {
            BigDecimal valor = new BigDecimal(taxaSindical.replace(',', '.'));

            if(valor.compareTo(BigDecimal.ZERO) < 0)
            {
                throw new ValorInvalidoException();
            }

            return valor;
        }
        catch(NumberFormatException e)
        {
            throw new ValorInvalidoException();
        }
    }

    /* Altera atributo do empregado associado ao vinculo sindical.*/
    public void alteraEmpregado(String id, String atributo, String valor, String idSindicato, String taxaSindical) throws EmpregadoNaoExisteException, IdentificacaoEmpregadoInvalidaException
    {
        Empregado empregado = buscarEmpregado(id);

        if("sindicalizado".equals(atributo))
        {
            if("true".equals(valor))
            {
                BigDecimal taxa = converterTaxaSindical(taxaSindical);

                alterarSindicalizacao(id, empregado, idSindicato, taxa);
            }
            else if("false".equals(valor))
            {
                removerSindicalizacao(id, empregado);
            }
        }
    }

    /*Altera um atributo do empregado quando a alteração não precisa de informações adicionais.
     * Estou usando sobrecarga de métodos nesse caso, pois um dos caso da us_5 usa sindicalizado = false
     * Quando chamo um método sobrecarregado, o Java olha os argumentos que eu passei e decide qual versão exata do método deve ser executada.
     * Achei uma resolução mais eficaz para esse erro e vi que faz parte do polimorfismo, coisa interessante para POO.
     */
    public void alteraEmpregado(String id, String atributo, String valor) throws EmpregadoNaoExisteException, IdentificacaoEmpregadoInvalidaException
    {
        Empregado empregado = buscarEmpregado(id);

        if("sindicalizado".equals(atributo) && "false".equals(valor))
        {
            removerSindicalizacao(id, empregado);
        }
    }

    /*Coloca um empregado no sindicato.*/
    private void alterarSindicalizacao(String id, Empregado empregado, String idSindicato, BigDecimal taxaSindical)
    {
        if(membrosSindicato.containsKey(idSindicato))
        {
            String outroEmpregado = membrosSindicato.get(idSindicato);

            if(!id.equals(outroEmpregado))
            {
                throw new IdentificacaoSindicatoJaExisteException();
            }
        }

        /*Se o empregado já possuía outra identificação de sindicato, ela deixa de ser utilizada.*/
        String sindicatoAnterior = empregado.getIdSindicato();

        if (sindicatoAnterior != null && !sindicatoAnterior.equals(idSindicato))
        {
            membrosSindicato.remove(sindicatoAnterior);
        }

        empregado.sindicalizar(idSindicato, taxaSindical);

        membrosSindicato.put(idSindicato, id);
    }

}