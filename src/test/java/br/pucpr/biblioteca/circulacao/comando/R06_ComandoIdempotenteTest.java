package br.pucpr.biblioteca.circulacao.comando;

import static org.junit.jupiter.api.Assertions.assertEquals;

import br.pucpr.biblioteca.biblioteca.livro.Livro;
import br.pucpr.biblioteca.biblioteca.movimentacao.Emprestimo;
import br.pucpr.biblioteca.circulacao.facade.FachadaCirculacao;
import br.pucpr.biblioteca.comum.relogio.RelogioFixo;
import br.pucpr.biblioteca.persistencia.fabrica.FabricaPersistencia;
import br.pucpr.biblioteca.persistencia.memoria.FabricaPersistenciaMemoria;
import java.time.Instant;
import java.time.LocalDate;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.junit.jupiter.api.Test;

// Cenário R6: validar a execução idempotente de comandos de circulação.
class R06_ComandoIdempotenteTest {
    private static final LocalDate DATA = LocalDate.of(2026, 11, 12);

    @Test
    void R06_renovacaoDuplicadaNaoAlteraPrazoMaisDeUmaVez() throws Exception {
        FabricaPersistencia fabrica = prepararEmprestimo();
        FachadaCirculacao fachada = fachada(fabrica);
        ComandoRenovacao comando = new ComandoRenovacao(
                "cmd-renovacao-1",
                "atendente-1",
                Instant.parse("2026-11-12T17:30:00Z"),
                fachada,
                "l1",
                "t1");
        ExecutorComando executor = new ExecutorComando();

        ExecutorService threads = Executors.newFixedThreadPool(2);
        try {
            Future<Emprestimo> primeira = threads.submit(() -> executor.executar(comando));
            Future<Emprestimo> segunda = threads.submit(() -> executor.executar(comando));

            assertEquals(1, primeira.get().numRenovacoes());
            assertEquals(1, segunda.get().numRenovacoes());
        } finally {
            threads.shutdownNow();
        }

        Emprestimo persistido = buscarEmprestimo(fabrica);
        assertEquals(1, persistido.numRenovacoes());
        assertEquals(DATA.plusDays(60), persistido.dataVencimento());
    }

    private static FabricaPersistencia prepararEmprestimo() {
        FabricaPersistencia fabrica = new FabricaPersistenciaMemoria();
        var unidade = fabrica.criarUnidadeDeTrabalho();
        Livro livro = new Livro("l1", "Livro");
        livro.solicitarReserva("e1");
        Emprestimo emprestimo = new Emprestimo("t1", "l1", "e1", DATA);
        livro.realizarEmprestimo(emprestimo);
        fabrica.criarRepositorioLivro(unidade).salvar(livro);
        fabrica.criarRepositorioMovimentacao(unidade).salvar(emprestimo);
        unidade.commit();
        unidade.close();
        return fabrica;
    }

    private static FachadaCirculacao fachada(FabricaPersistencia fabrica) {
        return new FachadaCirculacao(
                fabrica, new RelogioFixo(DATA, Instant.parse("2026-11-12T17:30:00Z")));
    }

    private static Emprestimo buscarEmprestimo(FabricaPersistencia fabrica) {
        var unidade = fabrica.criarUnidadeDeTrabalho();
        Emprestimo emprestimo = (Emprestimo) fabrica.criarRepositorioMovimentacao(unidade)
                .buscarPorId("t1").orElseThrow();
        unidade.rollback();
        return emprestimo;
    }
}

