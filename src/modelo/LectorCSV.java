package modelo;

import java.io.BufferedReader;
import java.io.FileNotFoundException; // Importar para ser más específico
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LectorCSV {

    public static List<Gasolinera> leerGasolineras(String rutaArchivo) {
        List<Gasolinera> lista = new ArrayList<>();

        // Validación de rutaArchivo
        if (rutaArchivo == null || rutaArchivo.trim().isEmpty()) {
            // System.err.println("Error: La ruta del archivo CSV no puede ser nula o vacía.");
            // Lanzar una excepción que MainApp pueda capturar
            throw new CSVLecturaException("La ruta del archivo CSV no puede ser nula o vacía.");
        }

        int numeroLinea = 0; // Para posible log de errores por línea

        try (BufferedReader br = new BufferedReader(new FileReader(rutaArchivo))) {
            String linea = br.readLine(); // Leer cabecera
            numeroLinea++; // Contar cabecera

            // Mantenemos la lógica original de lectura de líneas y split
            while ((linea = br.readLine()) != null) {
                numeroLinea++;
                String[] campos = linea.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);

                if (campos.length < 17) {
                    // System.err.println("Línea " + numeroLinea + " omitida: formato incorrecto (campos insuficientes).");
                    continue; // Mantenemos el continue
                }

                try {
                    String provincia = campos[0];
                    String municipio = campos[1];
                    String localidad = campos[2];
                    String cp = campos[3];
                    String direccion = campos[4];

                    // Mantenemos la llamada original a parsearPrecio
                    double gasolina95 = parsearPrecio(campos[9]);
                    double gasolina98 = parsearPrecio(campos[11]);
                    double diesel = parsearPrecio(campos[15]);
                    double dieselPlus = parsearPrecio(campos[16]);

                    Gasolinera g = new Gasolinera(provincia, municipio, localidad, cp, direccion,
                            gasolina95, gasolina98, diesel, dieselPlus);
                    lista.add(g);
                } catch (IllegalArgumentException e) { // Captura errores de validación del constructor de Gasolinera
                    System.err.println("Error al procesar línea " + numeroLinea + " (datos inválidos para Gasolinera): " + e.getMessage() + " Contenido: " + linea);
                } catch (Exception e) { // Mantenemos la captura genérica para otros problemas en la línea
                    System.err.println("Error al procesar línea " + numeroLinea + ": " + linea + ". Causa: " + e.getMessage());
                }
            }

        } catch (FileNotFoundException e) { // Ser más específico aquí
            // System.err.println("Error al leer el archivo CSV: " + e.getMessage());
            throw new CSVLecturaException("Archivo CSV no encontrado en la ruta: " + rutaArchivo, e);
        } catch (IOException e) {
            // System.err.println("Error al leer el archivo CSV: " + e.getMessage());
            throw new CSVLecturaException("Error de E/S al leer el archivo CSV: " + e.getMessage(), e);
        }

        return lista;
    }

    // Mantenemos el método parsearPrecio original
    private static double parsearPrecio(String campo) {
        try {
            return campo == null || campo.isEmpty() ? Double.NaN : Double.parseDouble(campo);
        } catch (NumberFormatException e) {
            return Double.NaN;
        }
    }
}