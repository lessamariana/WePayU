package br.ufal.ic.p2.wepayu.repositorio;

import br.ufal.ic.p2.wepayu.Exception.AgendaPagamentoNaoDisponivelException;
import br.ufal.ic.p2.wepayu.Exception.AgendaPagamentosJaExisteException;
import br.ufal.ic.p2.wepayu.models.pagamento.AgendaPagamento;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// Guarda as agendas de pagamento disponíveis no sistema
//Antes o mapa agendasPagamento e o registro das agendas padrão ficavam na Facade

public class RepositorioAgendas
{
    private final Map<String, AgendaPagamento> agendasPagamento;

    public RepositorioAgendas()
    {
        agendasPagamento = new LinkedHashMap<>();
        registrarAgendasPadrao();
    }

    // Registra as três agendas padão

    private void registrarAgendasPadrao()
    {
        agendasPagamento.put("semanal 5", new AgendaPagamento("semanal 5"));

        agendasPagamento.put("mensal $", new AgendaPagamento("mensal $"));

        agendasPagamento.put("semanal 2 5", new AgendaPagamento("semanal 2 5"));
    }

    // Cadastra uma nova agenda de pagamento
    // O construtor de AgendaPagamento já valida a descrição

    public void criar(String descricao)
    {
        AgendaPagamento agenda = new AgendaPagamento(descricao);

        if(agendasPagamento.containsKey(descricao))
        {
            throw new AgendaPagamentosJaExisteException();
        }

        agendasPagamento.put(descricao, agenda);
    }

    // Só verifica se a agenda existe, quem altera é o próprio empregado

    public void verificarDisponivel(String descricao)
    {
        if(!agendasPagamento.containsKey(descricao))
        {
            throw new AgendaPagamentoNaoDisponivelException();
        }
    }

    // Volta para as agendas padrão (usado no zerarSistema)

    public void reiniciar()
    {
        agendasPagamento.clear();
        registrarAgendasPadrao();
    }

    // Restaura as agendas salvas no XML
    // Se o arquivo for antigo e não tiver agendas, mantém as padrão

    public void restaurar(List<String> descricoes)
    {
        if(descricoes != null && !descricoes.isEmpty())
        {
            agendasPagamento.clear();

            for(String descricao : descricoes)
            {
                agendasPagamento.put(descricao, new AgendaPagamento(descricao));
            }
        }
    }

    public List<String> getDescricoes()
    {
        return new ArrayList<>(agendasPagamento.keySet());
    }
}
