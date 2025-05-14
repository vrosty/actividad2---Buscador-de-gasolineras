package presentacion;

import java.util.*;
import modelo.LectorCSV;
import modelo.Gasolinera;
import logica.GestorGasolineras;

import javax.swing.*;
import java.io.File;

public class MainApp {
    public static void main(String[] args) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Selecciona el archivo CSV de gasolineras");

        int seleccion = fileChooser.showOpenDialog(null);
        if (seleccion != JFileChooser.APPROVE_OPTION) {
            System.out.println("No se seleccionó ningún archivo.");
            System.exit(0);
        }

        File archivoSeleccionado = fileChooser.getSelectedFile();
        String rutaCSV = archivoSeleccionado.getAbsolutePath();
        List<Gasolinera> gasolineras = LectorCSV.leerGasolineras(rutaCSV);

        GestorGasolineras gestor = new GestorGasolineras(gasolineras);
        SwingUtilities.invokeLater(() -> {
            VentanaPrincipal ventana = new VentanaPrincipal(gestor);
            ventana.setVisible(true);
        });
    }
}
