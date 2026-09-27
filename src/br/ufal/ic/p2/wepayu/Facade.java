package br.ufal.ic.p2.wepayu;

import br.ufal.ic.p2.wepayu.Exception.*;


import br.ufal.ic.p2.wepayu.models.*;

import java.util.Deque;
import java.util.ArrayDeque; // to usando pra fazer de forma dinamica, pesquisei e vi que é melhor que usar LinkedLista ou Stack
import java.io.PrintWriter;
import java.io.FileNotFoundException;
import java.util.Comparator;
import java.util.ArrayList;
import java.util.List;
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

    private final Deque<Controle> pilhaDesfazer = new ArrayDeque<>();
    private final Deque<Controle> pilhaRefazer = new ArrayDeque<>();
    private boolean sistemaEncerrado = false;

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
        verificarSistemaAtivo();

        Map<String, Empregado> antes = clonarMapa(empregados);
        int proximoIdAntes = proximoId;

        empregados.clear();
        membrosSindicato.clear();
        proximoId = 1;

        Map<String, Empregado> depois = clonarMapa(empregados);

        registrarComando(new ControleGlobal(empregados, antes, proximoIdAntes, depois, proximoId, novoValor -> proximoId = novoValor));
    }

    /*Cria horista ou CLT.*/

    public String criarEmpregado(String nome, String endereco, String tipo, String salario)
    {
        verificarSistemaAtivo();

        BigDecimal salarioConvertido = converterSalario(salario);
        Empregado empregado = empregadoFactory.criar(nome, endereco, tipo, salarioConvertido);

        int proximoIdAntes = proximoId;
        String id = adicionarEmpregado(empregado);

        registrarComandoEmpregado(id, null, proximoIdAntes);
        return id;
    }

    public String criarEmpregado(String nome, String endereco, String tipo, String salario, String comissao)
    {
        verificarSistemaAtivo();

        BigDecimal salarioConvertido = converterSalario(salario);
        BigDecimal comissaoConvertida = converterComissao(comissao);
        Empregado empregado = empregadoFactory.criar(nome, endereco, tipo, salarioConvertido, comissaoConvertida);

        int proximoIdAntes = proximoId;
        String id = adicionarEmpregado(empregado);

        registrarComandoEmpregado(id, null, proximoIdAntes);
        return id;
    }

    /*Retorna um atributo do empregado.*/

    public String getAtributoEmpregado(String emp, String atributo) throws EmpregadoNaoExisteException
    {
        return buscarEmpregado(emp).getAtributo(atributo);
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
        //Atualizei para us_8
        verificarSistemaAtivo();

        if (id == null || id.trim().isEmpty()) //verificando se a string tá vazia
        {
            throw new IdentificacaoEmpregadoInvalidaException();
        }

        // Depois verifica se existe um empregado com essa id
        if (!empregados.containsKey(id))
        {
            throw new EmpregadoNaoExisteException();
        }

        Empregado antes = clonarSeExistir(id);
        int proximoIdAntes = proximoId;

        empregados.remove(id);

        registrarComandoEmpregado(id, antes, proximoIdAntes);
    }

    //Adicionando metodos para tratamento de cartoes de ponto pros testes de us_3
    //Atualizado para us_8

    public void lancaCartao(String id, String data, String horas) throws EmpregadoNaoExisteException, IdentificacaoEmpregadoInvalidaException, EmpregadoNaoEhHoristaException, DataInvalidaException, HorasInvalidaException
    {
        verificarSistemaAtivo();

        if(id == null || id.trim().isEmpty())
        {
            throw new IdentificacaoEmpregadoInvalidaException();
        }

        Empregado empregado = buscarEmpregado(id);
        Empregado antes = empregado.clonar();
        int proximoIdAntes = proximoId;

        LocalDate dataConvertida = DataTratamento.converterData(data, "Data invalida.");

        BigDecimal horasConvertidas;
        try
        {
            horasConvertidas = new BigDecimal(horas.replace(",", "."));
        }
        catch (NumberFormatException e)
        {
            throw new HorasInvalidaException();
        }

        empregado.lancaCartao(dataConvertida, horasConvertidas);

        registrarComandoEmpregado(id, antes, proximoIdAntes);
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
    //Atualizado para us_8
    public void lancaVenda(String id, String data, String valor) throws EmpregadoNaoExisteException, IdentificacaoEmpregadoInvalidaException, EmpregadoNaoEhComissionadoException, DataInvalidaException, ValorInvalidoException
    {
        verificarSistemaAtivo();

        if(id == null || id.trim().isEmpty())
        {
            throw new IdentificacaoEmpregadoInvalidaException();
        }

        Empregado empregado = buscarEmpregado(id);
        Empregado antes = empregado.clonar();
        int proximoIdAntes = proximoId;

        LocalDate dataConvertida = DataTratamento.converterData(data, "Data invalida.");

        BigDecimal valorConvertido;
        try
        {
            valorConvertido = new BigDecimal(valor.replace(",", "."));
        }
        catch (NumberFormatException e)
        {
            throw new ValorInvalidoException();
        }

        if (valorConvertido.compareTo(BigDecimal.ZERO) <= 0)
        {
            throw new ValorInvalidoException();
        }

        empregado.lancaVenda(dataConvertida, valorConvertido);

        registrarComandoEmpregado(id, antes, proximoIdAntes);
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

    //Atualizado para us_8
    public void lancaTaxaServico(String membro, String data, String valor) throws IdentificacaoMembroInvalidaException, MembroNaoExisteException, DataInvalidaException, ValorInvalidoException
    {
        verificarSistemaAtivo();

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

        Empregado antes = empregado.clonar();
        int proximoIdAntes = proximoId;

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

        registrarComandoEmpregado(idEmpregado, antes, proximoIdAntes);
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
            throw new TaxaSindicalNulaException();
        }

        try
        {
            BigDecimal valor = new BigDecimal(taxaSindical.replace(',', '.'));

            if(valor.compareTo(BigDecimal.ZERO) < 0)
            {
                throw new TaxaSindicalNegativaException();
            }

            return valor;
        }
        catch(NumberFormatException e)
        {
            throw new TaxaSindicalNaoNumericaException();
        }
    }


    /*Altera um atributo do empregado quando a alteração não precisa de informações adicionais.
     * Estou usando sobrecarga de métodos nesse caso, pois um dos caso da us_5 usa sindicalizado = false
     * Quando chamo um método sobrecarregado, o Java olha os argumentos que eu passei e decide qual versão exata do método deve ser executada.
     * Achei uma resolução mais eficaz para esse erro e vi que faz parte do polimorfismo, coisa interessante para POO.
     *Tirei porque fiz sobrecarga com outras e essa tava dando erro/

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

    private void alterarMetodoPagamento(Empregado empregado, String valor)
    {
        if("emMaos".equals(valor))
        {
            empregado.alteraMetodoPagamento(new PagamentoEmMaos());
            return;
        }

        if("correios".equals(valor))
        {
            empregado.alteraMetodoPagamento(new PagamentoCorreios());
            return;
        }

        throw new MetodoPagamentoInvalidoException();
    }

    private void alterarTipo(String id, Empregado atual, String tipo, String valorAdicional)
    {
        Empregado novoEmpregado;

        if("horista".equals(tipo))
        {
            BigDecimal salario = valorAdicional == null ? atual.getSalario() : converterSalario(valorAdicional);

            novoEmpregado = empregadoFactory.criar(atual.getNome(), atual.getEndereco(), "horista", salario);
        }
        else if("assalariado".equals(tipo))
        {
            BigDecimal salario = valorAdicional == null ? atual.getSalario() : converterSalario(valorAdicional);

            novoEmpregado = empregadoFactory.criar(atual.getNome(), atual.getEndereco(), "assalariado", salario);
        }
        else if("comissionado".equals(tipo))
        {
            if(valorAdicional == null)
            {
                throw new TipoNaoAplicavelException();
            }

            BigDecimal comissao = converterComissao(valorAdicional);

            novoEmpregado = empregadoFactory.criar(atual.getNome(), atual.getEndereco(), "comissionado", atual.getSalario(), comissao);
        }
        else
        {
            throw new TipoInvalidoException();
        }

        novoEmpregado.copiarDadosAlteraveis(atual);

        empregados.put(id, novoEmpregado);
    }

    // sobrecarda de metodo pois precisa de parametros diferentes para us_6

    public String totalFolha(String data) throws DataInvalidaException, EmpregadoNaoEhHoristaException
    {
        LocalDate dataPagamento = DataTratamento.converterData(data, "Data invalida.");
        FolhaPagamento folha = new FolhaPagamento(dataPagamento);

        BigDecimal total = BigDecimal.ZERO;

        for(Empregado empregado : empregados.values())
        {
            if(empregado.deveReceber(dataPagamento))
            {
                total = total.add(folha.calcular(empregado).getSalarioBruto());
            }
        }

        return formatarValor(total);
    }

    private String formatarInteiro(BigDecimal valor)
    {
        return valor.setScale(0, RoundingMode.DOWN).toString();
    }

    public void rodaFolha(String data, String saida) throws DataInvalidaException, EmpregadoNaoEhHoristaException
    {
        verificarSistemaAtivo();

        LocalDate dataPagamento = DataTratamento.converterData(data, "Data invalida.");
        Map<String, Empregado> antes = clonarMapa(empregados);
        int proximoIdAntes = proximoId;

        FolhaPagamento folha = new FolhaPagamento(dataPagamento);

        // Monta os registros de quem deve receber nesta data, e já separa quem teve pagamento efetivo (bruto > 0) de quem não teve.
        List<RegistroPagamento> registros = new ArrayList<>();
        List<Empregado> empregadosComPagamentoEfetivo = new ArrayList<>();

        for(Empregado empregado : empregados.values())
        {
            if(empregado.deveReceber(dataPagamento))
            {
                ResultadoPagamento resultado = folha.calcular(empregado);
                registros.add(empregado.gerarRegistro(dataPagamento, resultado));

                if(empregado.recebeuPagamentoEfetivo(resultado))
                {
                    empregadosComPagamentoEfetivo.add(empregado);
                }
            }
        }

        registros.sort(Comparator.comparing(RegistroPagamento::getNome));

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

        // So avancamos a data do ultimo pagamento de quem realmente recebeualgo (bruto > 0). Quem ficou zerado (ex.: horista sem horassuficientes) tem a data mantida, para que a taxa sindical e as
        //horas se acumulem corretamente ate o proximo pagamento de verdade.
        for(Empregado empregado : empregadosComPagamentoEfetivo)
        {
            empregado.setDataUltimoPagamento(dataPagamento);
        }

        Map<String, Empregado> depois = clonarMapa(empregados);
        registrarComando(new ControleGlobal(empregados, antes, proximoIdAntes, depois, proximoId, novoValor -> proximoId = novoValor));
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

    //Ainda us_8 mas associado a questão das pilhas/filas organização

    private void verificarSistemaAtivo()
    {
        if(sistemaEncerrado)
        {
            throw new SistemaEncerradoException();
        }
    }

    private void registrarComando(Controle comando)
    {
        pilhaDesfazer.push(comando);
        pilhaRefazer.clear();
    }

    private Empregado clonarSeExistir(String id)
    {
        Empregado empregado = empregados.get(id);
        return (empregado == null) ? null : empregado.clonar();
    }

    // Monta e registra o ComandoEmpregado

    private void registrarComandoEmpregado(String id, Empregado antes, int proximoIdAntes)
    {
        Empregado depois = clonarSeExistir(id);
        registrarComando(new ControleEmpregado(empregados, id, antes, depois, proximoIdAntes, proximoId, novoValor -> proximoId = novoValor));
    }

    private Map<String, Empregado> clonarMapa(Map<String, Empregado> original)
    {
        Map<String, Empregado> copia = new LinkedHashMap<>();

        for(Map.Entry<String, Empregado> entrada : original.entrySet())
        {
            copia.put(entrada.getKey(), entrada.getValue().clonar());
        }

        return copia;
    }

    private void reconstruirMembrosSindicato()
    {
        membrosSindicato.clear();

        for(Map.Entry<String, Empregado> entrada : empregados.entrySet())
        {
            Empregado empregado = entrada.getValue();

            if(empregado.participaSindicato())
            {
                membrosSindicato.put(empregado.getIdSindicato(), entrada.getKey());
            }
        }
    }

    public void undo()
    {
        verificarSistemaAtivo();

        if(pilhaDesfazer.isEmpty())
        {
            throw new NenhumComandoException("Nao ha comando a desfazer.");
        }

        Controle comando = pilhaDesfazer.pop();
        comando.desfazer();
        reconstruirMembrosSindicato();
        pilhaRefazer.push(comando);
    }

    public void redo()
    {
        verificarSistemaAtivo();

        if(pilhaRefazer.isEmpty())
        {
            throw new NenhumComandoException("Nao ha comando a refazer.");
        }

        Controle comando = pilhaRefazer.pop();
        comando.refazer();
        reconstruirMembrosSindicato();
        pilhaDesfazer.push(comando);
    }

    public int getNumeroDeEmpregados()
    {
        return empregados.size();
    }

    public String getEmpregadoPorNome(String nome, int indice) throws EmpregadoNaoExisteException
    {
        int alvo = indice - 1; // "indice" é 1-based no script de testes
        int contador = 0;

        for(Map.Entry<String, Empregado> entrada : empregados.entrySet())
        {
            if(entrada.getValue().getNome().equals(nome))
            {
                if(contador == alvo)
                {
                    return entrada.getKey();
                }

                contador++;
            }
        }

        throw new EmpregadoNaoExisteException();
    }

    public void encerrarSistema()
    {
        sistemaEncerrado = true;
    }

    //Sobrecarga de método


    //Tive que usar sobrecarda de metodo e alteraEmpregado ficou meio espalhado, então centralizei aqui

    public void alteraEmpregado(String id, String atributo, String valor) throws EmpregadoNaoExisteException
    {
        verificarSistemaAtivo();

        Empregado empregado = buscarEmpregado(id);
        Empregado antes = empregado.clonar();
        int proximoIdAntes = proximoId;

        aplicarAlteracaoSimples(id, empregado, atributo, valor);

        registrarComandoEmpregado(id, antes, proximoIdAntes);
    }

    private void aplicarAlteracaoSimples(String id, Empregado empregado, String atributo, String valor)
    {
        if("nome".equals(atributo))
        {
            empregado.alteraNome(valor);
            return;
        }

        if("endereco".equals(atributo))
        {
            empregado.alteraEndereco(valor);
            return;
        }

        if("salario".equals(atributo))
        {
            empregado.alteraSalario(converterSalario(valor));
            return;
        }

        if("tipo".equals(atributo))
        {
            alterarTipo(id, empregado, valor, null);
            return;
        }

        if("comissao".equals(atributo))
        {
            empregado.alteraComissao(converterComissao(valor));
            return;
        }

        if("metodoPagamento".equals(atributo))
        {
            alterarMetodoPagamento(empregado, valor);
            return;
        }

        if("sindicalizado".equals(atributo))
        {
            if("false".equals(valor))
            {
                removerSindicalizacao(id, empregado);
                return;
            }

            if("true".equals(valor))
            {
                throw new IdentificacaoSindicatoInvalidaException();
            }

            throw new ValorBooleanoInvalidoException();
        }

        throw new AtributoNaoExisteException();
    }

    public void alteraEmpregado(String id, String atributo, String valor, String valorAdicional) throws EmpregadoNaoExisteException
    {
        verificarSistemaAtivo();

        Empregado empregado = buscarEmpregado(id);
        Empregado antes = empregado.clonar();
        int proximoIdAntes = proximoId;

        if (!"tipo".equals(atributo))
        {
            throw new AtributoNaoExisteException();
        }

        alterarTipo(id, empregado, valor, valorAdicional);

        registrarComandoEmpregado(id, antes, proximoIdAntes);
    }

    public void alteraEmpregado(String id, String atributo, String valor1, String banco, String agencia, String contaCorrente) throws EmpregadoNaoExisteException
    {
        verificarSistemaAtivo();

        Empregado empregado = buscarEmpregado(id);
        Empregado antes = empregado.clonar();
        int proximoIdAntes = proximoId;

        if(!("metodoPagamento".equals(atributo) && "banco".equals(valor1)))
        {
            throw new MetodoPagamentoInvalidoException();
        }

        empregado.alteraMetodoPagamento(new PagamentoBanco(banco, agencia, contaCorrente));

        registrarComandoEmpregado(id, antes, proximoIdAntes);
    }

    public void alteraEmpregado(String id, String atributo, String valor, String idSindicato, String taxaSindical) throws EmpregadoNaoExisteException
    {
        verificarSistemaAtivo();

        Empregado empregado = buscarEmpregado(id);
        Empregado antes = empregado.clonar();
        int proximoIdAntes = proximoId;

        aplicarAlteracaoSindicalizacao(id, empregado, atributo, valor, idSindicato, taxaSindical);

        registrarComandoEmpregado(id, antes, proximoIdAntes);
    }

    private void aplicarAlteracaoSindicalizacao(String id, Empregado empregado, String atributo, String valor, String idSindicato, String taxaSindical)
    {
        if(!"sindicalizado".equals(atributo))
        {
            throw new AtributoNaoExisteException();
        }

        if("true".equals(valor))
        {
            if(idSindicato == null || idSindicato.isEmpty())
            {
                throw new IdentificacaoSindicatoInvalidaException();
            }

            BigDecimal taxa = converterTaxaSindical(taxaSindical);
            alterarSindicalizacao(id, empregado, idSindicato, taxa);
            return;
        }

        if ("false".equals(valor))
        {
            removerSindicalizacao(id, empregado);
            return;
        }

        throw new ValorBooleanoInvalidoException();
    }

}