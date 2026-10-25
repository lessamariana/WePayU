package br.ufal.ic.p2.wepayu.repositorio;

import br.ufal.ic.p2.wepayu.Exception.EmpregadoNaoExisteException;
import br.ufal.ic.p2.wepayu.Exception.EmpregadoNaoExistePorNomeException;
import br.ufal.ic.p2.wepayu.Exception.IdentificacaoEmpregadoInvalidaException;
import br.ufal.ic.p2.wepayu.models.empregado.Empregado;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap; /* Estrutura de dados em pares, como uma lista encadeada */
import java.util.Map;

// Responsável por armazenar e localizar os empregados cadastrados
// Antes o mapa de empregados e os métodos adicionarEmpregado, buscarEmpregado, removerEmpregado e getEmpregadoPorNome ficavam na Facade

public class RepositorioEmpregados
{
    // Guarda os empregados cadastrados

    private final Map<String, Empregado> empregados;
    private final GeradorId geradorId;

    public RepositorioEmpregados()
    {
        empregados = new LinkedHashMap<>();
        geradorId = new GeradorId();
    }

    // Adiciona um empregado ao mapa e gera id

    public String adicionar(Empregado empregado)
    {
        String id = geradorId.gerar();
        empregados.put(id, empregado);

        return id;
    }

    // Procura empregado pelo id
    // Agora é o único lugar que checa o id, antes essa checagem era repetida em 7 métodos da Facade

    public Empregado buscar(String id) throws EmpregadoNaoExisteException
    {
        // Precisei ajustar pra verificar se o id foi informado
        // já que o teste da us_2 pede uma mensagem específica para esse caso
        if (id == null || id.trim().isEmpty()) //verificando se a string tá vazia
        {
            throw new IdentificacaoEmpregadoInvalidaException();
        }

        // Depois verifica se existe um empregado com essa id
        Empregado empregado = empregados.get(id);

        if (empregado == null)
        {
            throw new EmpregadoNaoExisteException();
        }

        return empregado;
    }

    /* Adicionei esse metodo para atender o teste us_2 de remover funcionario*/
    /*Usei o containsKey por causa do Map, achei mais simples*/
    // agora usa o buscar, que já faz as validações do id

    public void remover(String id) throws EmpregadoNaoExisteException
    {
        buscar(id);
        empregados.remove(id);
    }

    // Troca o objeto de um empregado (usado quando o tipo do empregado muda)

    public void substituir(String id, Empregado novoEmpregado)
    {
        empregados.put(id, novoEmpregado);
    }

    public boolean existe(String id)
    {
        return empregados.containsKey(id);
    }

    public String buscarIdPorNome(String nome, int indice) throws EmpregadoNaoExisteException
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

        throw new EmpregadoNaoExistePorNomeException();
    }

    public int quantidade()
    {
        return empregados.size();
    }

    // Devolvem visões não modificáveis, quem precisar alterar tem que passar pelo repositório

    public Collection<Empregado> listar()
    {
        return Collections.unmodifiableCollection(empregados.values());
    }

    public Map<String, Empregado> listarComIds()
    {
        return Collections.unmodifiableMap(empregados);
    }

    // Limpa o sistema e reinicia a geração dos id

    public void limpar()
    {
        empregados.clear();
        geradorId.reiniciar();
    }

    // Métodos usados pela persistência e pelo controle de desfazer/refazer

    public int getProximoId()
    {
        return geradorId.getProximoId();
    }

    public String consultarProximoId()
    {
        return geradorId.consultarProximo();
    }

    public void definirProximoId(int proximoId)
    {
        geradorId.definir(proximoId);
    }

    public Empregado clonarSeExistir(String id)
    {
        Empregado empregado = empregados.get(id);
        return (empregado == null) ? null : empregado.clonar();
    }

    // Coloca de volta um empregado salvo, se for null o empregado não existia e é removido

    public void restaurar(String id, Empregado empregado)
    {
        if(empregado == null)
        {
            empregados.remove(id);
        }
        else
        {
            empregados.put(id, empregado);
        }
    }

    public EstadoRepositorio salvarEstado()
    {
        return new EstadoRepositorio(empregados, geradorId.getProximoId());
    }

    public void restaurarEstado(EstadoRepositorio estado)
    {
        empregados.clear();
        empregados.putAll(estado.getEmpregados());
        geradorId.definir(estado.getProximoId());
    }
}
