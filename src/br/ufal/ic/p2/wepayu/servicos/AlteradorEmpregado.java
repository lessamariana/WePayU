package br.ufal.ic.p2.wepayu.servicos;

import br.ufal.ic.p2.wepayu.Exception.AtributoNaoExisteException;
import br.ufal.ic.p2.wepayu.Exception.EmpregadoNaoExisteException;
import br.ufal.ic.p2.wepayu.Exception.MetodoPagamentoInvalidoException;
import br.ufal.ic.p2.wepayu.models.empregado.Empregado;
import br.ufal.ic.p2.wepayu.models.empregado.EmpregadoFactory;
import br.ufal.ic.p2.wepayu.models.pagamento.MetodoPagamentoFactory;
import br.ufal.ic.p2.wepayu.repositorio.MembrosSindicato;
import br.ufal.ic.p2.wepayu.repositorio.RepositorioAgendas;
import br.ufal.ic.p2.wepayu.repositorio.RepositorioEmpregados;
import br.ufal.ic.p2.wepayu.utilitarios.ConversorValores;

import java.util.HashMap;
import java.util.Map;

// Responsável por aplicar as alterações de atributos de um empregado (alteraEmpregado)
// Antes esses ifs ficavam na Facade (aplicarAlteracaoSimples, alterarTipo, alterarMetodoPagamento)
//Cada atributo agora é uma entrada no mapa: para aceitar um atributo novo basta adicionar mais uma entrada, sem mexer no resto do código

public class AlteradorEmpregado
{
    // Uma alteração recebe o id, o empregado já encontrado e o novo valor

    @FunctionalInterface
    private interface AlteracaoAtributo
    {
        void aplicar(String id, Empregado empregado, String valor);
    }

    private final RepositorioEmpregados empregados;
    private final MembrosSindicato membrosSindicato;
    private final RepositorioAgendas agendasPagamento;
    private final EmpregadoFactory empregadoFactory;
    private final Map<String, AlteracaoAtributo> alteracoes;

    public AlteradorEmpregado(RepositorioEmpregados empregados, MembrosSindicato membrosSindicato, RepositorioAgendas agendasPagamento, EmpregadoFactory empregadoFactory)
    {
        this.empregados = empregados;
        this.membrosSindicato = membrosSindicato;
        this.agendasPagamento = agendasPagamento;
        this.empregadoFactory = empregadoFactory;
        this.alteracoes = new HashMap<>();

        registrarAlteracoes();
    }

    private void registrarAlteracoes()
    {
        alteracoes.put("nome", (id, empregado, valor) -> empregado.alteraNome(valor));

        alteracoes.put("endereco", (id, empregado, valor) -> empregado.alteraEndereco(valor));

        alteracoes.put("salario", (id, empregado, valor) -> empregado.alteraSalario(ConversorValores.converterSalario(valor)));

        alteracoes.put("tipo", (id, empregado, valor) -> trocarTipo(id, empregado, valor, null));

        alteracoes.put("comissao", (id, empregado, valor) -> empregado.alteraComissao(ConversorValores.converterComissao(valor)));

        alteracoes.put("metodoPagamento", (id, empregado, valor) -> empregado.alteraMetodoPagamento(MetodoPagamentoFactory.criar(valor)));

        // Sem idSindicato: "true" dá erro de identificação e "false" retira do sindicato
        alteracoes.put("sindicalizado", (id, empregado, valor) -> membrosSindicato.alterarSindicalizacao(id, empregado, valor, null, null));

        alteracoes.put("agendaPagamento", this::alterarAgendaPagamento);
    }

    public void alterar(String id, String atributo, String valor) throws EmpregadoNaoExisteException
    {
        Empregado empregado = empregados.buscar(id);

        AlteracaoAtributo alteracao = alteracoes.get(atributo);

        if(alteracao == null)
        {
            throw new AtributoNaoExisteException();
        }

        alteracao.aplicar(id, empregado, valor);
    }

    // Mudança de tipo com valor adicional (salário ou comissão)

    public void alterarTipo(String id, String atributo, String tipo, String valorAdicional) throws EmpregadoNaoExisteException
    {
        Empregado empregado = empregados.buscar(id);

        if (!"tipo".equals(atributo))
        {
            throw new AtributoNaoExisteException();
        }

        trocarTipo(id, empregado, tipo, valorAdicional);
    }

    // Pagamento em banco precisa dos dados da conta

    public void alterarMetodoPagamento(String id, String atributo, String metodo, String banco, String agencia, String contaCorrente) throws EmpregadoNaoExisteException
    {
        Empregado empregado = empregados.buscar(id);

        if(!"metodoPagamento".equals(atributo))
        {
            throw new MetodoPagamentoInvalidoException();
        }

        empregado.alteraMetodoPagamento(MetodoPagamentoFactory.criar(metodo, banco, agencia, contaCorrente));
    }

    // Sindicalização com identificação e taxa sindical

    public void alterarSindicalizacao(String id, String atributo, String valor, String idSindicato, String taxaSindical) throws EmpregadoNaoExisteException
    {
        Empregado empregado = empregados.buscar(id);

        if(!"sindicalizado".equals(atributo))
        {
            throw new AtributoNaoExisteException();
        }

        membrosSindicato.alterarSindicalizacao(id, empregado, valor, idSindicato, taxaSindical);
    }

    // A fábrica cria o empregado do novo tipo e o repositório guarda no lugar do antigo

    private void trocarTipo(String id, Empregado atual, String tipo, String valorAdicional)
    {
        empregados.substituir(id, empregadoFactory.criarComNovoTipo(atual, tipo, valorAdicional));
    }

    //Altera a agenda quando ela está entre as três opções

    private void alterarAgendaPagamento(String id, Empregado empregado, String descricao)
    {
        agendasPagamento.verificarDisponivel(descricao);

        empregado.alteraAgendaPagamento(descricao);
    }
}
