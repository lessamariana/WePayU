package br.ufal.ic.p2.wepayu;

import br.ufal.ic.p2.wepayu.Exception.*;

import br.ufal.ic.p2.wepayu.controle.ControleEmpregado;
import br.ufal.ic.p2.wepayu.controle.ControleGlobal;
import br.ufal.ic.p2.wepayu.controle.EstadoSistema;
import br.ufal.ic.p2.wepayu.controle.HistoricoComandos;
import br.ufal.ic.p2.wepayu.models.empregado.CartaoPonto;
import br.ufal.ic.p2.wepayu.models.empregado.Empregado;
import br.ufal.ic.p2.wepayu.models.empregado.EmpregadoFactory;
import br.ufal.ic.p2.wepayu.models.empregado.TaxaServico;
import br.ufal.ic.p2.wepayu.models.empregado.Venda;
import br.ufal.ic.p2.wepayu.models.folha.FolhaPagamento;
import br.ufal.ic.p2.wepayu.persistencia.Persistencia;
import br.ufal.ic.p2.wepayu.repositorio.MembrosSindicato;
import br.ufal.ic.p2.wepayu.repositorio.RepositorioAgendas;
import br.ufal.ic.p2.wepayu.repositorio.RepositorioEmpregados;
import br.ufal.ic.p2.wepayu.servicos.AlteradorEmpregado;
import br.ufal.ic.p2.wepayu.utilitarios.Formatador;
import br.ufal.ic.p2.wepayu.utilitarios.Periodo;

// Vai receber os comandos dos testes, localizar os objetos necessários e delegar as regras para as classes de negócio
// a Facade não guarda mais dados nem tem regras de negócio
// Cada responsabilidade ficou em uma classe própria:
//  - RepositorioEmpregados: armazena os empregados e gera os ids (GeradorId)
//  - MembrosSindicato: controla os membros do sindicato
//  - RepositorioAgendas: agendas de pagamento disponíveis
//  - HistoricoComandos: pilhas de desfazer/refazer
//  - EstadoSistema: se o sistema foi encerrado
//  - AlteradorEmpregado: alterações de atributos do empregado
//  - FolhaPagamento / RelatorioFolha: cálculo e escrita da folha

public class Facade
{
    private final RepositorioEmpregados empregados;
    private final MembrosSindicato membrosSindicato;
    private final RepositorioAgendas agendasPagamento;
    private final EmpregadoFactory empregadoFactory;
    private final AlteradorEmpregado alteradorEmpregado;
    private final HistoricoComandos historico;
    private final EstadoSistema estadoSistema;

    public Facade()
    {
        empregados = new RepositorioEmpregados();
        membrosSindicato = new MembrosSindicato(empregados);
        agendasPagamento = new RepositorioAgendas();
        empregadoFactory = new EmpregadoFactory();
        alteradorEmpregado = new AlteradorEmpregado(empregados, membrosSindicato, agendasPagamento, empregadoFactory);
        historico = new HistoricoComandos();
        estadoSistema = new EstadoSistema();

        Persistencia.restaurar(empregados, agendasPagamento, empregadoFactory);
        membrosSindicato.reconstruir();
    }

    // Cadastra uma nova agenda de pagamento

    public void criarAgendaDePagamentos(String descricao)
    {
        estadoSistema.verificarAtivo();

        agendasPagamento.criar(descricao);
    }

    /* Limpa o sistema e reinicia a geração dos id*/

    public void zerarSistema()
    {
        estadoSistema.verificarAtivo();

        ControleGlobal controle = new ControleGlobal(empregados);

        empregados.limpar();
        membrosSindicato.limpar();
        agendasPagamento.reiniciar();

        historico.registrar(controle);
    }

    /*Cria horista ou CLT.*/

    public String criarEmpregado(String nome, String endereco, String tipo, String salario)
    {
        estadoSistema.verificarAtivo();

        return adicionarEmpregado(empregadoFactory.criar(nome, endereco, tipo, salario));
    }

    public String criarEmpregado(String nome, String endereco, String tipo, String salario, String comissao)
    {
        estadoSistema.verificarAtivo();

        return adicionarEmpregado(empregadoFactory.criar(nome, endereco, tipo, salario, comissao));
    }

    /*Adicionar um empregado ao mapa e gerar seu o ID*/
    // O repositório gera o id, aqui só registra a criação no histórico

    private String adicionarEmpregado(Empregado empregado)
    {
        ControleEmpregado controle = ControleEmpregado.paraNovoEmpregado(empregados);

        String id = empregados.adicionar(empregado);

        historico.registrar(controle);
        return id;
    }

    /*Retorna um atributo do empregado.*/

    public String getAtributoEmpregado(String emp, String atributo) throws EmpregadoNaoExisteException
    {
        return empregados.buscar(emp).getAtributo(atributo);
    }

    /* Adicionei esse metodo para atender o teste us_2 de remover funcionario*/
    //Atualizei para us_8

    public void removerEmpregado(String id) throws EmpregadoNaoExisteException, IdentificacaoEmpregadoInvalidaException
    {
        estadoSistema.verificarAtivo();

        ControleEmpregado controle = new ControleEmpregado(empregados, id);

        empregados.remover(id);

        historico.registrar(controle);
    }

    //Adicionando metodos para tratamento de cartoes de ponto pros testes de us_3
    //Atualizado para us_8

    public void lancaCartao(String id, String data, String horas) throws EmpregadoNaoExisteException, IdentificacaoEmpregadoInvalidaException, EmpregadoNaoEhHoristaException, DataInvalidaException, HorasInvalidaException
    {
        estadoSistema.verificarAtivo();

        ControleEmpregado controle = new ControleEmpregado(empregados, id);

        empregados.buscar(id).lancaCartao(CartaoPonto.criar(data, horas));

        historico.registrar(controle);
    }

    //Estava usando BigDecimal, mas estava dando erro na formatação, então achei melhor mudar para String

    public String getHorasNormaisTrabalhadas(String id, String dataInicial, String dataFinal) throws EmpregadoNaoExisteException, IdentificacaoEmpregadoInvalidaException, EmpregadoNaoEhHoristaException, DataInvalidaException
    {
        Empregado empregado = empregados.buscar(id);
        Periodo periodo = Periodo.criar(dataInicial, dataFinal);

        return Formatador.formatarDecimal(empregado.getHorasNormaisTrabalhadas(periodo.getInicio(), periodo.getFim()));
    }

    public String getHorasExtrasTrabalhadas(String id, String dataInicial, String dataFinal) throws EmpregadoNaoExisteException, IdentificacaoEmpregadoInvalidaException, EmpregadoNaoEhHoristaException, DataInvalidaException
    {
        Empregado empregado = empregados.buscar(id);
        Periodo periodo = Periodo.criar(dataInicial, dataFinal);

        return Formatador.formatarDecimal(empregado.getHorasExtrasTrabalhadas(periodo.getInicio(), periodo.getFim()));
    }

    /* Lança uma venda para um empregado.A venda só pode ser registrada para um empregado comissionado.*/
    //Atualizado para us_8
    public void lancaVenda(String id, String data, String valor) throws EmpregadoNaoExisteException, IdentificacaoEmpregadoInvalidaException, EmpregadoNaoEhComissionadoException, DataInvalidaException, ValorInvalidoException
    {
        estadoSistema.verificarAtivo();

        ControleEmpregado controle = new ControleEmpregado(empregados, id);

        empregados.buscar(id).lancaVenda(Venda.criar(data, valor));

        historico.registrar(controle);
    }

    /*Retorna o total de vendas realizadas pelo empregado dentro do período informado. */

    public String getVendasRealizadas(String id, String dataInicial, String dataFinal) throws EmpregadoNaoExisteException, IdentificacaoEmpregadoInvalidaException, EmpregadoNaoEhComissionadoException, DataInvalidaException
    {
        // Procura o empregado
        Empregado empregado = empregados.buscar(id);
        Periodo periodo = Periodo.criar(dataInicial, dataFinal);

        // O EasyAccept espera duas casas decimais e vírgula
        return Formatador.formatarValor(empregado.getVendasRealizadas(periodo.getInicio(), periodo.getFim()));
    }

    //Atualizado para us_8
    public void lancaTaxaServico(String membro, String data, String valor) throws IdentificacaoMembroInvalidaException, MembroNaoExisteException, DataInvalidaException, ValorInvalidoException, EmpregadoNaoExisteException
    {
        estadoSistema.verificarAtivo();

        String idEmpregado = membrosSindicato.buscarIdEmpregado(membro);
        ControleEmpregado controle = new ControleEmpregado(empregados, idEmpregado);

        empregados.buscar(idEmpregado).lancaTaxaServico(TaxaServico.criar(data, valor));

        historico.registrar(controle);
    }

    // Retorna o total das taxas de serviço de um empregado dentro de um período

    public String getTaxasServico(String id, String dataInicial, String dataFinal) throws EmpregadoNaoExisteException, IdentificacaoEmpregadoInvalidaException, EmpregadoNaoEhSindicalizadoException, DataInvalidaException
    {
        Empregado empregado = empregados.buscar(id);
        Periodo periodo = Periodo.criar(dataInicial, dataFinal);

        return Formatador.formatarValor(empregado.getTaxasServico(periodo.getInicio(), periodo.getFim()));
    }

    // sobrecarda de metodo pois precisa de parametros diferentes para us_6

    public String totalFolha(String data) throws DataInvalidaException, EmpregadoNaoEhHoristaException
    {
        return Formatador.formatarValor(FolhaPagamento.criar(data).calcularTotal(empregados.listar()));
    }

    public void rodaFolha(String data, String saida) throws DataInvalidaException, EmpregadoNaoEhHoristaException
    {
        estadoSistema.verificarAtivo();

        FolhaPagamento folha = FolhaPagamento.criar(data);
        ControleGlobal controle = new ControleGlobal(empregados);

        folha.rodar(empregados.listar(), saida);

        historico.registrar(controle);
    }

    // Depois de desfazer/refazer o mapa do sindicato é montado de novo a partir dos empregados

    public void undo()
    {
        estadoSistema.verificarAtivo();

        historico.desfazer();
        membrosSindicato.reconstruir();
    }

    public void redo()
    {
        estadoSistema.verificarAtivo();

        historico.refazer();
        membrosSindicato.reconstruir();
    }

    public int getNumeroDeEmpregados()
    {
        return empregados.quantidade();
    }

    public String getEmpregadoPorNome(String nome, int indice) throws EmpregadoNaoExisteException
    {
        return empregados.buscarIdPorNome(nome, indice);
    }

    public void encerrarSistema()
    {
        Persistencia.salvar(empregados, agendasPagamento);

        estadoSistema.encerrar();
    }

    //Sobrecarga de método


    //Tive que usar sobrecarda de metodo e alteraEmpregado ficou meio espalhado, então centralizei aqui
    // Quem aplica as alterações agora é o AlteradorEmpregado

    public void alteraEmpregado(String id, String atributo, String valor) throws EmpregadoNaoExisteException
    {
        estadoSistema.verificarAtivo();

        ControleEmpregado controle = new ControleEmpregado(empregados, id);

        alteradorEmpregado.alterar(id, atributo, valor);

        historico.registrar(controle);
    }

    public void alteraEmpregado(String id, String atributo, String valor, String valorAdicional) throws EmpregadoNaoExisteException
    {
        estadoSistema.verificarAtivo();

        ControleEmpregado controle = new ControleEmpregado(empregados, id);

        alteradorEmpregado.alterarTipo(id, atributo, valor, valorAdicional);

        historico.registrar(controle);
    }

    public void alteraEmpregado(String id, String atributo, String valor1, String banco, String agencia, String contaCorrente) throws EmpregadoNaoExisteException
    {
        estadoSistema.verificarAtivo();

        ControleEmpregado controle = new ControleEmpregado(empregados, id);

        alteradorEmpregado.alterarMetodoPagamento(id, atributo, valor1, banco, agencia, contaCorrente);

        historico.registrar(controle);
    }

    public void alteraEmpregado(String id, String atributo, String valor, String idSindicato, String taxaSindical) throws EmpregadoNaoExisteException
    {
        estadoSistema.verificarAtivo();

        ControleEmpregado controle = new ControleEmpregado(empregados, id);

        alteradorEmpregado.alterarSindicalizacao(id, atributo, valor, idSindicato, taxaSindical);

        historico.registrar(controle);
    }

}
