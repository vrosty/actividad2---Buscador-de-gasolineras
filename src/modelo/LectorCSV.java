package modelo;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LectorCSV {

    public static List<Gasolinera> leerGasolineras(String rutaArchivo) {
        List<Gasolinera> lista = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(rutaArchivo))) {
            String linea = br.readLine(); // Leer cabecera
            while ((linea = br.readLine()) != null) {
                String[] campos = linea.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1); // Manejo de comas entre comillas

                if (campos.length < 17) continue; // Evita errores con líneas incompletas

                try {
                    String provincia = campos[0];
                    String municipio = campos[1];
                    String localidad = campos[2];
                    String cp = campos[3];
                    String direccion = campos[4];

                    double gasolina95 = parsearPrecio(campos[9]);
                    double gasolina98 = parsearPrecio(campos[11]);
                    double diesel = parsearPrecio(campos[15]);
                    double dieselPlus = parsearPrecio(campos[16]);

                    Gasolinera g = new Gasolinera(provincia, municipio, localidad, cp, direccion,
                            gasolina95, gasolina98, diesel, dieselPlus);
                    lista.add(g);
                } catch (Exception e) {
                    System.out.println("Error al procesar línea: " + linea);
                }
            }

        } catch (IOException e) {
            System.out.println("Error al leer el archivo CSV: " + e.getMessage());
        }

        return lista;
    }

    private static double parsearPrecio(String campo) {
        try {
            return campo == null || campo.isEmpty() ? Double.NaN : Double.parseDouble(campo);
        } catch (NumberFormatException e) {
            return Double.NaN;
        }
    }
}

