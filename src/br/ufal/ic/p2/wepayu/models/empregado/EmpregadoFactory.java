package br.ufal.ic.p2.wepayu.models.empregado;

import br.ufal.ic.p2.wepayu.Exception.TipoInvalidoException;
import br.ufal.ic.p2.wepayu.Exception.TipoNaoAplicavelException;
import br.ufal.ic.p2.wepayu.utilitarios.ConversorValores;

import java.math.BigDecimal;

/*Cria os diferentes tipos de empregado*/
/*Estudando para fazer o trabalho descobri que existe Factory Method*/
/*É um padrão para a lógica de criação de objetos e serve para centralizar
a parte de criação em um lugar só, achei que fazia sentido já que são vários tipos de empregado */

public class EmpregadoFactory
{
    /*Cria horista ou CLT.*/
    // Recebe os valores em texto vindos da Facade, converte e delega para a criação normal

    public Empregado criar(String nome, String endereco, String tipo, String salario)
    {
        BigDecimal salarioConvertido = ConversorValores.converterSalario(salario);

        return criar(nome, endereco, tipo, salarioConvertido);
    }

    public Empregado criar(String nome, String endereco, String tipo, String salario, String comissao)
    {
        BigDecimal salarioConvertido = ConversorValores.converterSalario(salario);
        BigDecimal comissaoConvertida = ConversorValores.converterComissao(comissao);

        return criar(nome, endereco, tipo, salarioConvertido, comissaoConvertida);
    }

    //Empregados sem comissão

    public Empregado criar(String nome, String endereco, String tipo, BigDecimal salario)
    {

        switch (tipo)
        {

            case "horista":
                return new EmpregadoHorista(nome, endereco, salario);

            case "assalariado":
                return new EmpregadoAssalariado(nome, endereco, salario);

            case "comissionado":
                // Comissionado precisa obrigatoriamente receber comissão
                throw new TipoNaoAplicavelException();

            default:
                throw new TipoInvalidoException();
        }
    }

    //Empregado com comissão
    public Empregado criar(String nome, String endereco, String tipo, BigDecimal salario, BigDecimal comissao)
    {

        switch (tipo)
        {

            case "comissionado":
                return new EmpregadoComissionado(nome, endereco, salario, comissao);

            case "horista":
            case "assalariado":
                // Esses tipos não aceitam comissão.
                throw new TipoNaoAplicavelException();

            default:
                throw new TipoInvalidoException();
        }
    }

    // Cria um novo empregado do tipo informado, mantendo os dados do empregado atual
    // Antes ficava na Facade (alterarTipo), mas criar empregados é responsabilidade da fábrica

    public Empregado criarComNovoTipo(Empregado atual, String tipo, String valorAdicional)
    {
        Empregado novoEmpregado;

        if("horista".equals(tipo))
        {
            BigDecimal salario = valorAdicional == null ? atual.getSalario() : ConversorValores.converterSalario(valorAdicional);

            novoEmpregado = criar(atual.getNome(), atual.getEndereco(), "horista", salario);
        }
        else if("assalariado".equals(tipo))
        {
            BigDecimal salario = valorAdicional == null ? atual.getSalario() : ConversorValores.converterSalario(valorAdicional);

            novoEmpregado = criar(atual.getNome(), atual.getEndereco(), "assalariado", salario);
        }
        else if("comissionado".equals(tipo))
        {
            if(valorAdicional == null)
            {
                throw new TipoNaoAplicavelException();
            }

            BigDecimal comissao = ConversorValores.converterComissao(valorAdicional);

            novoEmpregado = criar(atual.getNome(), atual.getEndereco(), "comissionado", atual.getSalario(), comissao);
        }
        else
        {
            throw new TipoInvalidoException();
        }

        novoEmpregado.copiarDadosAlteraveis(atual);

        return novoEmpregado;
    }
}
