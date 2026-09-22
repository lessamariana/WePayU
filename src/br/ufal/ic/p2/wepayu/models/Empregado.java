/*Classe pai para os tipos de empregados*/

package br.ufal.ic.p2.wepayu.models;

import br.ufal.ic.p2.wepayu.Exception.AtributoNaoExisteException;
import br.ufal.ic.p2.wepayu.Exception.EnderecoInvalidoException;
import br.ufal.ic.p2.wepayu.Exception.NomeInvalidoException;
import br.ufal.ic.p2.wepayu.Exception.SalarioInvalidoException;

import java.math.BigDecimal;
import java.math.RoundingMode;

public abstract class Empregado
{
    private final String nome;
    private final String endereco;
    private final BigDecimal salario;

    /*Participa do sindicato ou não?*/
    private boolean sindicato;

    /*Usei protected para as classes filhas conseguirem acessar*/

    protected Empregado(String nome, String endereco, BigDecimal salario)
    {
        validarNome(nome);
        validarEndereco(endereco);
        validarSalario(salario);

        this.nome = nome;
        this.endereco = endereco;
        this.salario = salario.setScale(2, RoundingMode.HALF_UP);
        this.sindicato = false;
    }

    /*Validações para verificar possíveis erros*/

    private void validarNome(String nome)
    {
        if (nome == null || nome.isEmpty())
        {
            throw new NomeInvalidoException();
        }
    }

    private void validarEndereco(String endereco)
    {
        if (endereco == null || endereco.isEmpty())
        {
            throw new EnderecoInvalidoException();
        }
    }

    private void validarSalario(BigDecimal salario)
    {
        if (salario == null || salario.compareTo(BigDecimal.ZERO) < 0)
        {
            throw new SalarioInvalidoException("O salário não pode ser negativo.");
        }
    }

    /*Se não der erro, coletar o dados*/

    public String getNome()
    {
        return nome;
    }

    public String getEndereco()
    {
        return endereco;
    }

    public BigDecimal getSalario()
    {
        return salario;
    }

    public boolean participaSindicato()
    {
        return sindicato;
    }


    /*Define o tipo de empregado, já que tem tipos diferentes*/

    public String getTipo()
    {
        return getTipoEmpregado();
    }

    /*Verificação dos atributos*/

    public String getAtributo(String atributo)
    {

        switch (atributo)
        {
            case "nome":
                return nome;

            case "endereco":
                return endereco;

            case "tipo":
                return getTipo();

            case "salario":
                return formatarValor(salario);

            case "sindicato":
                return String.valueOf(sindicato);

            default:
                return getAtributoEspecifico(atributo);
        }
    }

    /*Verificação associada a subclasse, para que possa verificar atributos*/

    protected String getAtributoEspecifico(String atributo)
    {
        throw new AtributoNaoExisteException();
    }

    /*Valores com vírgula e duas casas decimais*/

    protected abstract String getTipoEmpregado();

    protected String formatarValor(BigDecimal valor)
    {
        return valor.setScale(2, RoundingMode.HALF_UP).toString().replace('.', ',');
    }
}
