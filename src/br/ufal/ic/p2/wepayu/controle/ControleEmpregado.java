package br.ufal.ic.p2.wepayu.controle;

import br.ufal.ic.p2.wepayu.models.empregado.Empregado;
import br.ufal.ic.p2.wepayu.repositorio.RepositorioEmpregados;

// O IntConsumer deixou de ser necessário, porque agora o RepositorioEmpregados controla o próximo id

/*Comando que representa uma mudança em 1 único empregado do mapa de empregados.*/

public class ControleEmpregado extends Controle
{
    private final RepositorioEmpregados empregados;
    private final String id;
    private final Empregado antes;
    private final int proximoIdAntes;
    private Empregado depois;
    private int proximoIdDepois;

    // Monta e registra o ComandoEmpregado
    // o construtor monta com o estado anterior e o HistoricoComandos registra chamando concluir()
    // Guarda como o empregado estava antes da operação (null se ele ainda não existia)

    public ControleEmpregado(RepositorioEmpregados empregados, String id)
    {
        this.empregados = empregados;
        this.id = id;
        this.antes = empregados.clonarSeExistir(id);
        this.proximoIdAntes = empregados.getProximoId();
    }

    // Para a criação: o empregado ainda não existe, então o controle fica ligado ao id que vai ser gerado

    public static ControleEmpregado paraNovoEmpregado(RepositorioEmpregados empregados)
    {
        return new ControleEmpregado(empregados, empregados.consultarProximoId());
    }

    @Override
    public void concluir()
    {
        this.depois = empregados.clonarSeExistir(id);
        this.proximoIdDepois = empregados.getProximoId();
    }

    @Override
    public void desfazer()
    {
        empregados.restaurar(id, antes);
        empregados.definirProximoId(proximoIdAntes);
    }

    @Override
    public void refazer()
    {
        empregados.restaurar(id, depois);
        empregados.definirProximoId(proximoIdDepois);
    }
}
