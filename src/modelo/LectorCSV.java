package modelo;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LectorCSV {

    public static List<Gasolinera> leerGasolineras(String rutaArchivo) {
        List<Gasolinera> lista = new ArrayList<>();

        // Validación de la ruta del archivo
        if (rutaArchivo == null || rutaArchivo.trim().isEmpty()) {
            throw new CSVLecturaException("La ruta del archivo CSV no puede ser nula o vacía.");
        }

        int numeroLinea = 0; // Contador para el número de línea actual (incluyendo cabecera)

        try (BufferedReader br = new BufferedReader(new FileReader(rutaArchivo))) {
            String linea = br.readLine(); // Leer la línea de cabecera
            numeroLinea++;

            // Procesar cada línea de datos del archivo
            while ((linea = br.readLine()) != null) {
                numeroLinea++;
                // Se mantiene el split original que maneja comas dentro de campos entrecomillados
                String[] campos = linea.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);

                // Comprobar si la línea tiene el número mínimo de campos esperados
                if (campos.length < 17) { // Asumiendo que se necesitan al menos 17 campos (índice 16 para dieselPlus)
                    System.err.println("Línea " + numeroLinea + " omitida: formato incorrecto (campos insuficientes: " + campos.length + "). Contenido: " + linea);
                    continue;
                }

                try {
                    // Extracción de datos de los campos
                    String provincia = campos[0];
                    String municipio = campos[1];
                    String localidad = campos[2];
                    String cp = campos[3];
                    String direccion = campos[4];

                    // Parseo de precios utilizando el método actualizado
                    double gasolina95 = parsearPrecio(campos[9]);
                    double gasolina98 = parsearPrecio(campos[11]);
                    double diesel = parsearPrecio(campos[15]);
                    double dieselPlus = parsearPrecio(campos[16]);

                    // Creación del objeto Gasolinera
                    Gasolinera g = new Gasolinera(provincia, municipio, localidad, cp, direccion,
                            gasolina95, gasolina98, diesel, dieselPlus);
                    lista.add(g);
                } catch (IllegalArgumentException e) {
                    // Error en la validación de datos dentro del constructor de Gasolinera
                    System.err.println("Error al procesar línea " + numeroLinea + " (datos inválidos para Gasolinera): " + e.getMessage() + ". Contenido: " + linea);
                } catch (ArrayIndexOutOfBoundsException e) {
                    // Error si se intenta acceder a un índice de campo que no existe (menos de 17 campos relevantes para precios)
                    System.err.println("Error al procesar línea " + numeroLinea + " (faltan campos esperados, ej. para precios). Contenido: " + linea);
                } catch (Exception e) {
                    // Captura de cualquier otra excepción inesperada durante el procesamiento de la línea
                    System.err.println("Error inesperado al procesar línea " + numeroLinea + ": " + linea + ". Causa: " + e.getMessage());
                }
            }

        } catch (FileNotFoundException e) {
            // Error si el archivo CSV no se encuentra en la ruta especificada
            throw new CSVLecturaException("Archivo CSV no encontrado en la ruta: " + rutaArchivo, e);
        } catch (IOException e) {
            // Error general de entrada/salida durante la lectura del archivo
            throw new CSVLecturaException("Error de E/S al leer el archivo CSV: " + e.getMessage(), e);
        }

        // Mensajes informativos sobre el resultado de la lectura
        if (numeroLinea <= 1 && lista.isEmpty()) { // Solo cabecera leída o archivo vacío
            if (rutaArchivo != null && !rutaArchivo.isEmpty()) {
                System.out.println("Información: El archivo CSV '" + rutaArchivo + "' está vacío o solo contiene la cabecera.");
            }
        } else { // Se procesaron líneas de datos
            int lineasDeDatosIntentadas = numeroLinea - 1; // Restar la cabecera
            System.out.println("Información: Se intentaron procesar " + lineasDeDatosIntentadas + " líneas de datos del CSV '" + rutaArchivo + "'.");
            if (lista.isEmpty()) {
                System.out.println("Advertencia: No se cargaron gasolineras válidas de las " + lineasDeDatosIntentadas + " líneas procesadas.");
            } else {
                System.out.println("Éxito: Se cargaron " + lista.size() + " gasolineras.");
            }
        }

        return lista;
    }

    /**
     * Parsea un campo de texto que representa un precio y lo convierte a double.
     * Maneja dos formatos:
     * 1. Formato "entero" (ej. "1450"): Se interpreta como milésimas de unidad (1.450).
     * 2. Formato "flotante" (ej. "1.21" o "1,21"): Se interpreta directamente.
     * También maneja comillas y reemplaza comas por puntos para el separador decimal.
     *
     * @param campo El string que contiene el precio.
     * @return El precio como double, o Double.NaN si el parseo falla o el campo es inválido.
     */
    private static double parsearPrecio(String campo) {
        if (campo == null) {
            return Double.NaN;
        }

        // Limpiar el campo: quitar espacios, comillas y reemplazar coma decimal por punto
        String valorProcesado = campo.trim().replace("\"", "");
        if (valorProcesado.isEmpty() || valorProcesado.equalsIgnoreCase("null")) {
            return Double.NaN;
        }
        valorProcesado = valorProcesado.replace(',', '.'); // Asegurar punto como separador decimal

        try {
            if (valorProcesado.contains(".")) {
                // El campo ya tiene un punto decimal, se asume formato flotante estándar (ej. "1.21")
                return Double.parseDouble(valorProcesado);
            } else {
                // El campo no tiene punto decimal, se asume formato entero que representa milésimas (ej. "1450" -> 1.450)
                // También maneja enteros simples (ej. "2" -> 0.002, si esa es la intención, o ajustar la división)
                // Si "2" debe ser 2.000, entonces la lógica es correcta.
                long valorEntero = Long.parseLong(valorProcesado);
                return valorEntero / 1000.0;
            }
        } catch (NumberFormatException e) {
            // Si ocurre un error durante la conversión numérica
            // System.err.println("Advertencia: No se pudo parsear el precio '" + campo + "' (procesado como '" + valorProcesado + "'). Se usará NaN. Error: " + e.getMessage());
            return Double.NaN;
        }
    }
}
