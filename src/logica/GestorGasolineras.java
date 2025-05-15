package logica;

import modelo.Gasolinera;

import java.util.*;
import java.util.stream.Collectors;
import java.text.DecimalFormat; // Para mejorar la visualización de estadísticas

public class GestorGasolineras {
    private List<Gasolinera> gasolineras;
    private static final DecimalFormat df = new DecimalFormat("#.###"); // Para mostrar 3 decimales

    public GestorGasolineras(List<Gasolinera> gasolineras) {
        if (gasolineras == null) {
            // Decidimos inicializar con una lista vacía si es nula, para evitar NullPointerExceptions.
            // Podría también lanzarse un IllegalArgumentException si una lista nula es inaceptable.
            System.err.println("Advertencia: La lista de gasolineras recibida por GestorGasolineras era nula. Se usará una lista vacía.");
            this.gasolineras = new ArrayList<>();
        } else {
            this.gasolineras = gasolineras;
        }
    }

    public List<String> obtenerProvincias() {
        if (this.gasolineras.isEmpty()) {
            return Collections.emptyList();
        }
        return gasolineras.stream()
                .map(Gasolinera::getProvincia)
                .filter(p -> p != null && !p.isBlank()) // Mantener filtro por si algún dato es anómalo
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    public List<String> obtenerTiposCombustible() {
        // Mantenemos la lista original
        return Arrays.asList("gasolina95", "gasolina98", "diesel", "dieselplus");
    }

    public List<Gasolinera> filtrarGasolineras(String tipo, String provincia) {
        // Validación de parámetros
        if (tipo == null || tipo.trim().isEmpty()) {
            throw new IllegalArgumentException("El tipo de combustible no puede ser nulo o vacío para filtrar.");
        }
        if (provincia == null || provincia.trim().isEmpty()) {
            throw new IllegalArgumentException("La provincia no puede ser nula o vacía para filtrar.");
        }
        if (this.gasolineras.isEmpty()) {
            return Collections.emptyList();
        }

        String tipoNormalizado = tipo.trim().toLowerCase(); // Normalizar para la comparación
        String provinciaNormalizada = provincia.trim();

        return gasolineras.stream()
                .filter(g -> g.getProvincia().equalsIgnoreCase(provinciaNormalizada)) // Usar equalsIgnoreCase para provincia
                .filter(g -> !Double.isNaN(g.getPrecioCombustible(tipoNormalizado))) // Mantenemos la lógica original de filtrado
                .collect(Collectors.toList());
    }

    public String obtenerEstadisticas(List<Gasolinera> lista, String tipo) {
        // Validación de parámetros
        if (lista == null) {
            throw new IllegalArgumentException("La lista de gasolineras para estadísticas no puede ser nula.");
        }
        if (tipo == null || tipo.trim().isEmpty()) {
            throw new IllegalArgumentException("El tipo de combustible no puede ser nulo o vacío para obtener estadísticas.");
        }
        if (lista.isEmpty()) {
            return "Estadísticas (" + tipo + "):\nNo hay datos disponibles para los criterios seleccionados.";
        }

        String tipoNormalizado = tipo.trim().toLowerCase();

        DoubleSummaryStatistics stats = lista.stream()
                .mapToDouble(g -> g.getPrecioCombustible(tipoNormalizado)) // Mantenemos la lógica original
                .filter(p -> !Double.isNaN(p)) // Mantenemos la lógica original
                .summaryStatistics();

        if (stats.getCount() == 0) {
            return "Estadísticas (" + tipoNormalizado + "):\nNo hay precios válidos para los criterios seleccionados.";
        }

        return "Estadísticas (" + tipoNormalizado + "):\n" +
                "Gasolineras consideradas: " + stats.getCount() + "\n" +
                "Media: " + (Double.isNaN(stats.getAverage()) ? "N/A" : df.format(stats.getAverage())) + " €\n" +
                "Mínimo: " + (stats.getMin() == Double.POSITIVE_INFINITY ? "N/A" : df.format(stats.getMin())) + " €\n" +
                "Máximo: " + (stats.getMax() == Double.NEGATIVE_INFINITY ? "N/A" : df.format(stats.getMax())) + " €";
    }

    public Gasolinera obtenerGasolineraMasBarata(String tipo, String provincia) {
        // Validación de parámetros
        if (tipo == null || tipo.trim().isEmpty()) {
            throw new IllegalArgumentException("El tipo de combustible no puede ser nulo o vacío para buscar la más barata.");
        }
        if (provincia == null || provincia.trim().isEmpty()) {
            throw new IllegalArgumentException("La provincia no puede ser nula o vacía para buscar la más barata.");
        }
        if (this.gasolineras.isEmpty()) {
            return null;
        }

        String tipoNormalizado = tipo.trim().toLowerCase();
        String provinciaNormalizada = provincia.trim();

        // Reutilizamos el método de filtrado que ya valida y normaliza internamente si es necesario
        List<Gasolinera> filtradas = filtrarGasolineras(tipoNormalizado, provinciaNormalizada);

        return filtradas.stream()
                .min(Comparator.comparingDouble(g -> g.getPrecioCombustible(tipoNormalizado)))
                .orElse(null); // Mantenemos la lógica original
    }
}
