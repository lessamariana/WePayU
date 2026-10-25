package br.ufal.ic.p2.wepayu.controle;

import br.ufal.ic.p2.wepayu.repositorio.EstadoRepositorio;
import br.ufal.ic.p2.wepayu.repositorio.RepositorioEmpregados;

// Controle que afeta o sistema inteiro: zerarSistema (apaga tudo) e rodaFolha (atualiza a data de último pagamento dos empregados)

public class ControleGlobal extends Controle
{
    private final RepositorioEmpregados empregados;
    private final EstadoRepositorio antes;
    private EstadoRepositorio depois;

    public ControleGlobal(RepositorioEmpregados empregados)
    {
        this.empregados = empregados;
        this.antes = empregados.salvarEstado();
    }

    @Override
    public void concluir()
    {
        this.depois = empregados.salvarEstado();
    }

    @Override
    public void desfazer()
    {
        empregados.restaurarEstado(antes);
    }

    @Override
    public void refazer()
    {
        empregados.restaurarEstado(depois);
    }
}
