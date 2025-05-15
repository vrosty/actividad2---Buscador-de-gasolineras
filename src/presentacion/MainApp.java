package presentacion;

import java.util.*;
import modelo.LectorCSV;
import modelo.Gasolinera;
import modelo.CSVLecturaException; // Importar la excepción personalizada
import logica.GestorGasolineras;

import javax.swing.*;
import java.io.File;

public class MainApp {
    public static void main(String[] args) {
        try {
            // Configurar Look and Feel para una apariencia más moderna (opcional)
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Advertencia: No se pudo establecer el Look and Feel del sistema: " + e.getMessage());
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Selecciona el archivo CSV de gasolineras");

        int seleccion = fileChooser.showOpenDialog(null);
        if (seleccion != JFileChooser.APPROVE_OPTION) {
            JOptionPane.showMessageDialog(null,
                    "No se seleccionó ningún archivo.\nLa aplicación se cerrará.",
                    "Operación Cancelada",
                    JOptionPane.INFORMATION_MESSAGE);
            System.out.println("No se seleccionó ningún archivo.");
            // System.exit(0); // No es necesario si la app no continúa
            return;
        }

        File archivoSeleccionado = fileChooser.getSelectedFile();

        try {
            String rutaCSV = archivoSeleccionado.getAbsolutePath();
            //System.out.println("Intentando leer CSV desde: " + rutaCSV); // Log para depuración
            List<Gasolinera> gasolineras = LectorCSV.leerGasolineras(rutaCSV);

            if (gasolineras.isEmpty()) {
                JOptionPane.showMessageDialog(null,
                        "El archivo CSV está vacío o no contiene datos de gasolineras válidos.\nLa aplicación se cerrará.",
                        "Archivo Sin Datos",
                        JOptionPane.WARNING_MESSAGE);
                System.out.println("Advertencia: No se cargaron gasolineras válidas desde el CSV.");
                // System.exit(0); // No es necesario si la app no continúa
                return;
            }
            //System.out.println("Gasolineras cargadas: " + gasolineras.size()); // Log para depuración

            GestorGasolineras gestor = new GestorGasolineras(gasolineras);

            SwingUtilities.invokeLater(() -> {
                VentanaPrincipal ventana = new VentanaPrincipal(gestor);
                ventana.setVisible(true);
            });

        } catch (CSVLecturaException e) {
            JOptionPane.showMessageDialog(null,
                    "Error al leer o procesar el archivo CSV:\n" + e.getMessage() + "\nLa aplicación se cerrará.",
                    "Error de Archivo",
                    JOptionPane.ERROR_MESSAGE);
            System.err.println("CSVLecturaException: " + e.getMessage());
            // e.printStackTrace(); // Para depuración
        } catch (IllegalArgumentException e) { // Captura de errores de validación del gestor o gasolinera
            JOptionPane.showMessageDialog(null,
                    "Error en los datos o configuración interna:\n" + e.getMessage() + "\nLa aplicación se cerrará.",
                    "Error de Datos",
                    JOptionPane.ERROR_MESSAGE);
            System.err.println("IllegalArgumentException: " + e.getMessage());
        } catch (Exception e) { // Captura genérica para otros errores inesperados
            JOptionPane.showMessageDialog(null,
                    "Ocurrió un error inesperado al iniciar la aplicación:\n" + e.getMessage() + "\nLa aplicación se cerrará.",
                    "Error Crítico",
                    JOptionPane.ERROR_MESSAGE);
            System.err.println("Error inesperado en MainApp: " + e.getClass().getName() + " - " + e.getMessage());
            e.printStackTrace(); // Para depuración
        }
    }
}