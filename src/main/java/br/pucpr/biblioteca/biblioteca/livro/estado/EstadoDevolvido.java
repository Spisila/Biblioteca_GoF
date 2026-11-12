package br.pucpr.biblioteca.biblioteca.livro.estado;

import br.pucpr.biblioteca.biblioteca.livro.Livro;
import br.pucpr.biblioteca.biblioteca.livro.ResultadoInspecao;
import br.pucpr.biblioteca.biblioteca.livro.SituacaoLivro;
import java.util.Objects;

/**
 * Estado aguardando a inspeção do livro devolvido.
 *
 * <p>Padrão: R01 State — papel: Estado concreto.</p>
 */
public final class EstadoDevolvido extends EstadoLivroBase {
    @Override
    protected SituacaoLivro situacaoAtual() {
        return SituacaoLivro.DEVOLVIDO;
    }

    @Override
    public void inspecionarLivro(Livro livro, ResultadoInspecao resultado) {
        Objects.requireNonNull(resultado, "resultado nao pode ser nulo");
        switch (resultado) {
            case BOM_ESTADO:
                livro.definirDataDevolucao(null);
                livro.transitarPara(EstadosLivro.de(SituacaoLivro.LIVRE));
                break;
            case DANIFICADO_REPARAVEL:
                livro.transitarPara(EstadosLivro.de(SituacaoLivro.REPARANDO));
                break;
            case IRREPARAVEL:
                livro.transitarPara(EstadosLivro.de(SituacaoLivro.INCINERADO));
                break;
            default:
                throw new IllegalStateException("Resultado de inspeção não suportado");
        }
    }
}
