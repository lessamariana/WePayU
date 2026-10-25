package br.ufal.ic.p2.wepayu.repositorio;

import br.ufal.ic.p2.wepayu.models.empregado.Empregado;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

// Foto (snapshot) do repositório em um determinado momento, é a cópia dos empregados e o próximo id
// Usado pelo undo/redo das operações que afetam o sistema inteiro
// Antes isso era feito na Facade com o metodo clonarMapa

public final class EstadoRepositorio
{
    private final Map<String, Empregado> empregados;
    private final int proximoId;

    EstadoRepositorio(Map<String, Empregado> empregadosAtuais, int proximoId)
    {
        Map<String, Empregado> copia = new LinkedHashMap<>();

        for(Map.Entry<String, Empregado> entrada : empregadosAtuais.entrySet())
        {
            copia.put(entrada.getKey(), entrada.getValue().clonar());
        }

        this.empregados = Collections.unmodifiableMap(copia);
        this.proximoId = proximoId;
    }

    Map<String, Empregado> getEmpregados()
    {
        return empregados;
    }

    int getProximoId()
    {
        return proximoId;
    }
}
