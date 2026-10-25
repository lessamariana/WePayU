package br.ufal.ic.p2.wepayu.repositorio;

import br.ufal.ic.p2.wepayu.Exception.IdentificacaoMembroInvalidaException;
import br.ufal.ic.p2.wepayu.Exception.IdentificacaoSindicatoInvalidaException;
import br.ufal.ic.p2.wepayu.Exception.IdentificacaoSindicatoJaExisteException;
import br.ufal.ic.p2.wepayu.Exception.MembroNaoExisteException;
import br.ufal.ic.p2.wepayu.Exception.ValorBooleanoInvalidoException;
import br.ufal.ic.p2.wepayu.models.empregado.Empregado;
import br.ufal.ic.p2.wepayu.utilitarios.ConversorValores;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

// Controla quais empregados são membros do sindicato
// Antes o atributo membrosSindicato e os métodos alterarSindicalizacao, removerSindicalizacao e reconstruirMembrosSindicato ficavam na Facade

public class MembrosSindicato
{
    private final Map<String, String> membrosSindicato;
    private final RepositorioEmpregados empregados;

    public MembrosSindicato(RepositorioEmpregados empregados)
    {
        this.membrosSindicato = new LinkedHashMap<>();
        this.empregados = empregados;
    }

    // Encontra o id do empregado a partir da identificação dele no sindicato

    public String buscarIdEmpregado(String membro)
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

        if(!empregados.existe(idEmpregado))
        {
            throw new MembroNaoExisteException();
        }

        return idEmpregado;
    }

    // "true" sindicaliza o empregado e "false" retira do sindicato

    public void alterarSindicalizacao(String id, Empregado empregado, String valor, String idSindicato, String taxaSindical)
    {
        if("true".equals(valor))
        {
            if(idSindicato == null || idSindicato.isEmpty())
            {
                throw new IdentificacaoSindicatoInvalidaException();
            }

            BigDecimal taxa = ConversorValores.converterTaxaSindical(taxaSindical);
            associar(id, empregado, idSindicato, taxa);
            return;
        }

        if ("false".equals(valor))
        {
            removerSindicalizacao(empregado);
            return;
        }

        throw new ValorBooleanoInvalidoException();
    }

    //Altera um atributo do empregado quando a alteração não precisa de informações adicionais.
    // Estou usando sobrecarga de métodos nesse caso, pois um dos caso da us_5 usa sindicalizado = false
    // Quando chamo um método sobrecarregado, o Java olha os argumentos que eu passei e decide qual versão exata do método deve ser executada
    // Achei uma resolução mais eficaz para esse erro e vi que faz parte do polimorfismo, coisa interessante para POO.
    //Tirei porque fiz sobrecarga com outras e essa tava dando erro

    // Coloca um empregado no sindicato
    private void associar(String id, Empregado empregado, String idSindicato, BigDecimal taxaSindical)
    {
        if(membrosSindicato.containsKey(idSindicato))
        {
            String outroEmpregado = membrosSindicato.get(idSindicato);

            if(!id.equals(outroEmpregado))
            {
                throw new IdentificacaoSindicatoJaExisteException();
            }
        }

        //Se o empregado já possuía outra identificação de sindicato, ela deixa de ser utilizada
        String sindicatoAnterior = empregado.getIdSindicato();

        if (sindicatoAnterior != null && !sindicatoAnterior.equals(idSindicato))
        {
            membrosSindicato.remove(sindicatoAnterior);
        }

        empregado.sindicalizar(idSindicato, taxaSindical);

        membrosSindicato.put(idSindicato, id);
    }

    // Retira o empregado do sindicato
    private void removerSindicalizacao(Empregado empregado)
    {
        String idSindicato = empregado.getIdSindicato();

        if (idSindicato != null)
        {
            membrosSindicato.remove(idSindicato);
        }

        empregado.dessindicalizar();
    }

    // Monta o mapa de novo a partir dos empregados (usado depois do undo/redo e ao carregar o XML)

    public void reconstruir()
    {
        membrosSindicato.clear();

        for(Map.Entry<String, Empregado> entrada : empregados.listarComIds().entrySet())
        {
            Empregado empregado = entrada.getValue();

            if(empregado.participaSindicato())
            {
                membrosSindicato.put(empregado.getIdSindicato(), entrada.getKey());
            }
        }
    }

    public void limpar()
    {
        membrosSindicato.clear();
    }
}
