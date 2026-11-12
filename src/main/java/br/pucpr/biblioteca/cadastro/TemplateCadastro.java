package br.pucpr.biblioteca.cadastro;

import java.util.Objects;
import java.util.Optional;

/**
 * Define o fluxo fixo de inclusão de um cadastro.
 *
 * <p>Padrão: R07 Template Method — papel: Classe abstrata.</p>
 */
public abstract class TemplateCadastro<T> {
    public final T incluir(T dados) {
        Objects.requireNonNull(dados, "dados nao podem ser nulos");
        Optional<T> existente = buscarExistente(dados);
        if (existente.isPresent()) {
            return apresentar(existente.get());
        }
        validarDados(dados);
        verificarPreRequisitos(dados);
        T registro = criarRegistro(dados);
        armazenar(registro);
        return apresentar(registro);
    }

    protected abstract Optional<T> buscarExistente(T dados);

    protected abstract void validarDados(T dados);

    protected void verificarPreRequisitos(T dados) {
    }

    protected abstract T criarRegistro(T dados);

    protected abstract void armazenar(T registro);

    protected T apresentar(T registro) {
        return registro;
    }
}
