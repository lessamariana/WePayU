package br.ufal.ic.p2.wepayu.repositorio;

// Responsável pela geração dos id dos empregados
// Antes o atributo proximoId ficava direto na Facade

public class GeradorId
{
    private static final int VALOR_INICIAL = 1;

    // Criação de Identificador Único (iniciei em 1) para cada funcionário criado
    // Próximo número que será utilizado como id
    private int proximoId;

    public GeradorId()
    {
        this.proximoId = VALOR_INICIAL;
    }

    public String gerar()
    {
        return String.valueOf(proximoId++);
    }

    // Mostra qual id será gerado, sem consumir ele (usado pelo controle de desfazer na criação)

    public String consultarProximo()
    {
        return String.valueOf(proximoId);
    }

    public int getProximoId()
    {
        return proximoId;
    }

    // Usado pelo undo/redo e pela persistência para voltar a um valor salvo

    public void definir(int proximoId)
    {
        this.proximoId = proximoId;
    }

    public void reiniciar()
    {
        this.proximoId = VALOR_INICIAL;
    }
}
