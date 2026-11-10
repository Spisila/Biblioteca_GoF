package br.pucpr.biblioteca.comum.dinheiro;

import java.util.Objects;

public final class Dinheiro implements Comparable<Dinheiro> {
    public static final Dinheiro ZERO = new Dinheiro(0);

    private final long centavos;

    private Dinheiro(long centavos) {
        this.centavos = centavos;
    }

    public static Dinheiro ofCentavos(long centavos) {
        if (centavos < 0) {
            throw new IllegalArgumentException("Dinheiro nao pode ser negativo");
        }
        return centavos == 0 ? ZERO : new Dinheiro(centavos);
    }

    public static Dinheiro ofReais(long reais, int centavos) {
        if (reais < 0 || centavos < 0 || centavos > 99) {
            throw new IllegalArgumentException("Reais ou centavos invalidos");
        }
        return ofCentavos(Math.addExact(Math.multiplyExact(reais, 100), centavos));
    }

    public Dinheiro somar(Dinheiro outro) {
        return ofCentavos(Math.addExact(centavos, Objects.requireNonNull(outro).centavos));
    }

    public Dinheiro subtrair(Dinheiro outro) {
        return ofCentavos(Math.subtractExact(centavos, Objects.requireNonNull(outro).centavos));
    }

    public Dinheiro multiplicar(int fator) {
        if (fator < 0) {
            throw new IllegalArgumentException("Fator nao pode ser negativo");
        }
        return ofCentavos(Math.multiplyExact(centavos, fator));
    }

    public Dinheiro minimo(Dinheiro outro) {
        return compareTo(outro) <= 0 ? this : outro;
    }

    public Dinheiro maximo(Dinheiro outro) {
        return compareTo(outro) >= 0 ? this : outro;
    }

    public long centavos() {
        return centavos;
    }

    public boolean ehZero() {
        return centavos == 0;
    }

    @Override
    public int compareTo(Dinheiro outro) {
        return Long.compare(centavos, Objects.requireNonNull(outro).centavos);
    }

    @Override
    public boolean equals(Object objeto) {
        if (this == objeto) {
            return true;
        }
        if (!(objeto instanceof Dinheiro)) {
            return false;
        }
        Dinheiro outro = (Dinheiro) objeto;
        return centavos == outro.centavos;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(centavos);
    }

    @Override
    public String toString() {
        long reais = centavos / 100;
        long resto = centavos % 100;
        return "R$ " + reais + "," + (resto < 10 ? "0" : "") + resto;
    }
}
