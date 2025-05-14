package logica;

import modelo.Gasolinera;

import java.util.*;
import java.util.stream.Collectors;

public class GestorGasolineras {
    private List<Gasolinera> gasolineras;

    public GestorGasolineras(List<Gasolinera> gasolineras) {
        this.gasolineras = gasolineras;
    }

    public List<String> obtenerProvincias() {
        return gasolineras.stream()
                .map(Gasolinera::getProvincia)
                .filter(p -> p != null && !p.isBlank())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    public List<String> obtenerTiposCombustible() {
        return Arrays.asList("gasolina95", "gasolina98", "diesel", "dieselplus");
    }

    public List<Gasolinera> filtrarGasolineras(String tipo, String provincia) {
        return gasolineras.stream()
                .filter(g -> g.getProvincia().equalsIgnoreCase(provincia))
                .filter(g -> !Double.isNaN(g.getPrecioCombustible(tipo)))
                .collect(Collectors.toList());
    }

    public String obtenerEstadisticas(List<Gasolinera> lista, String tipo) {
        DoubleSummaryStatistics stats = lista.stream()
                .mapToDouble(g -> g.getPrecioCombustible(tipo))
                .filter(p -> !Double.isNaN(p))
                .summaryStatistics();
        return "Estadísticas (" + tipo + "):\n" +
                "Media: " + stats.getAverage() + "\n" +
                "Mínimo: " + stats.getMin() + "\n" +
                "Máximo: " + stats.getMax();
    }

    public Gasolinera obtenerGasolineraMasBarata(String tipo, String provincia) {
        return filtrarGasolineras(tipo, provincia).stream()
                .min(Comparator.comparingDouble(g -> g.getPrecioCombustible(tipo)))
                .orElse(null);
    }
}

