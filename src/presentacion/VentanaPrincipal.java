package presentacion;

import logica.GestorGasolineras;
import modelo.Gasolinera;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Collections; // Para listas vacías

public class VentanaPrincipal extends JFrame {
    private JComboBox<String> comboProvincias;
    private JComboBox<String> comboTipos;
    private JButton btnFiltrar;
    private JButton btnEstadisticas;
    private JButton btnMasBarata;
    private JTextArea txtEstadisticas;
    private JTable tablaResultados;
    private DefaultTableModel modeloTabla;
    private transient GestorGasolineras gestor;

    public VentanaPrincipal(GestorGasolineras gestor) {
        // Validación del gestor
        if (gestor == null) {
            JOptionPane.showMessageDialog(null,
                    "Error crítico: El gestor de datos no está disponible.\nLa aplicación no puede continuar.",
                    "Error Interno Grave",
                    JOptionPane.ERROR_MESSAGE);
            // Considerar cerrar la aplicación o deshabilitar toda la UI.
            // Por simplicidad, permitimos que continúe, pero la UI podría no funcionar.
            // System.exit(1); // Opción drástica
            this.gestor = new GestorGasolineras(Collections.emptyList()); // Gestor "dummy" para evitar NPEs masivos
            setTitle("Gasolineras - Aplicación (MODO ERROR)");
        } else {
            this.gestor = gestor;
            setTitle("Gasolineras - Aplicación");
        }

        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Panel superior con controles
        JPanel panelControles = new JPanel(new FlowLayout(FlowLayout.LEFT));

        List<String> provincias = this.gestor.obtenerProvincias();
        if (provincias.isEmpty()) {
            comboProvincias = new JComboBox<>(new String[]{"(Sin provincias)"});
            comboProvincias.setEnabled(false);
        } else {
            comboProvincias = new JComboBox<>(provincias.toArray(new String[0]));
        }

        List<String> tipos = this.gestor.obtenerTiposCombustible();
        if (tipos.isEmpty()) { // Aunque GestorGasolineras siempre devuelve 4 tipos
            comboTipos = new JComboBox<>(new String[]{"(Sin tipos)"});
            comboTipos.setEnabled(false);
        } else {
            comboTipos = new JComboBox<>(tipos.toArray(new String[0]));
        }

        btnFiltrar = new JButton("Filtrar");
        btnFiltrar.setToolTipText("Genera una tabla con el resultado del filtro.");
        btnEstadisticas = new JButton("Estadísticas");
        btnEstadisticas.setToolTipText("Obtiene unas estadísticas basicas sobre los precios.");
        btnMasBarata = new JButton("Más barata");
        btnMasBarata.setToolTipText("Gasolinera más barata para los cirterios de filtrado.");

        panelControles.add(new JLabel("Provincia:"));
        panelControles.add(comboProvincias);
        panelControles.add(new JLabel("Combustible:"));
        panelControles.add(comboTipos);
        panelControles.add(btnFiltrar);
        panelControles.add(btnEstadisticas);
        panelControles.add(btnMasBarata);

        add(panelControles, BorderLayout.NORTH);

        // Tabla de resultados con scroll
        String[] columnas = {"Provincia", "Municipio", "Localidad", "Dirección", "Precio"};
        modeloTabla = new DefaultTableModel(columnas, 0);
        tablaResultados = new JTable(modeloTabla);
        JScrollPane scrollTabla = new JScrollPane(tablaResultados);

        // Área de estadísticas con scroll
        txtEstadisticas = new JTextArea(6, 40);
        txtEstadisticas.setEditable(false);
        txtEstadisticas.setLineWrap(true);
        JScrollPane scrollEstadisticas = new JScrollPane(txtEstadisticas);

        JPanel panelCentro = new JPanel();
        panelCentro.setLayout(new BoxLayout(panelCentro, BoxLayout.Y_AXIS));
        panelCentro.add(scrollTabla);
        panelCentro.add(Box.createVerticalStrut(10));
        panelCentro.add(scrollEstadisticas);

        add(panelCentro, BorderLayout.CENTER);

        // Texto inicial
        txtEstadisticas.setText("Seleccione criterios y pulse un botón para ver resultados.");

        // Acción botón Filtrar
        btnFiltrar.addActionListener((ActionEvent e) -> {
            try {
                String provincia = obtenerSeleccionValida(comboProvincias, "una provincia");
                String tipo = obtenerSeleccionValida(comboTipos, "un tipo de combustible");
                mostrarResultadosFiltrados(provincia, tipo);
            } catch (IllegalArgumentException iae) {
                JOptionPane.showMessageDialog(this, iae.getMessage(), "Entrada Inválida", JOptionPane.WARNING_MESSAGE);
            } catch (Exception ex) { // Captura genérica
                JOptionPane.showMessageDialog(this, "Ocurrió un error inesperado al filtrar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                System.err.println("Error en btnFiltrar: " + ex.getMessage());
                // ex.printStackTrace();
            }
        });

        // Acción botón Estadísticas
        btnEstadisticas.addActionListener((ActionEvent e) -> {
            try {
                String provincia = obtenerSeleccionValida(comboProvincias, "una provincia");
                String tipo = obtenerSeleccionValida(comboTipos, "un tipo de combustible");
                mostrarEstadisticas(provincia, tipo);
            } catch (IllegalArgumentException iae) {
                JOptionPane.showMessageDialog(this, iae.getMessage(), "Entrada Inválida", JOptionPane.WARNING_MESSAGE);
            } catch (Exception ex) { // Captura genérica
                JOptionPane.showMessageDialog(this, "Ocurrió un error inesperado al obtener estadísticas: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                System.err.println("Error en btnEstadisticas: " + ex.getMessage());
            }
        });

        // Acción botón Más barata
        btnMasBarata.addActionListener((ActionEvent e) -> {
            try {
                String provincia = obtenerSeleccionValida(comboProvincias, "una provincia");
                String tipo = obtenerSeleccionValida(comboTipos, "un tipo de combustible");
                mostrarGasolineraMasBarata(provincia, tipo);
            } catch (IllegalArgumentException iae) {
                JOptionPane.showMessageDialog(this, iae.getMessage(), "Entrada Inválida", JOptionPane.WARNING_MESSAGE);
            } catch (Exception ex) { // Captura genérica
                JOptionPane.showMessageDialog(this, "Ocurrió un error inesperado al buscar la más barata: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                System.err.println("Error en btnMasBarata: " + ex.getMessage());
            }
        });
    }

    // Método ayudante para validar selección de ComboBox
    private String obtenerSeleccionValida(JComboBox<String> comboBox, String descripcionCampo) {
        if (!comboBox.isEnabled() || comboBox.getSelectedItem() == null) {
            throw new IllegalArgumentException("No hay " + descripcionCampo + " disponible o habilitado para seleccionar.");
        }
        String seleccion = (String) comboBox.getSelectedItem();
        if (seleccion.startsWith("(") || seleccion.trim().isEmpty()) { // Para manejar placeholders como "(Sin...)"
            throw new IllegalArgumentException("Por favor, seleccione " + descripcionCampo + " válido(a).");
        }
        return seleccion;
    }


    private void mostrarResultadosFiltrados(String provincia, String tipo) {
        // Las validaciones de provincia y tipo ya se hicieron en el listener
        // y GestorGasolineras también las hace.
        List<Gasolinera> filtradas = gestor.filtrarGasolineras(tipo, provincia);

        modeloTabla.setRowCount(0); // Limpiar tabla antes de añadir nuevas filas
        if (filtradas.isEmpty()){
            txtEstadisticas.setText("No se han encontrado gasolineras para los criterios:\nProvincia: " + provincia + "\nCombustible: " + tipo);
        } else {
            for (Gasolinera g : filtradas) {
                modeloTabla.addRow(new Object[]{
                        g.getProvincia(), g.getMunicipio(), g.getLocalidad(), g.getDireccion(),
                        // Formatear precio para mejor visualización, si no es NaN
                        Double.isNaN(g.getPrecioCombustible(tipo)) ? "N/A" : String.format("%.3f", g.getPrecioCombustible(tipo))
                });
            }
            txtEstadisticas.setText("Se han encontrado " + filtradas.size() + " resultados para:\nProvincia: " + provincia + "\nCombustible: " + tipo);
        }
    }

    private void mostrarEstadisticas(String provincia, String tipo) {
        List<Gasolinera> filtradas = gestor.filtrarGasolineras(tipo, provincia); // Reutilizar filtro
        String stats = gestor.obtenerEstadisticas(filtradas, tipo);
        txtEstadisticas.setText("Resultados para Provincia: " + provincia + "\n" + stats);
    }

    private void mostrarGasolineraMasBarata(String provincia, String tipo) {
        Gasolinera barata = gestor.obtenerGasolineraMasBarata(tipo, provincia);

        modeloTabla.setRowCount(0); // Limpiar tabla
        if (barata != null) {
            modeloTabla.addRow(new Object[]{
                    barata.getProvincia(), barata.getMunicipio(), barata.getLocalidad(),
                    barata.getDireccion(),
                    Double.isNaN(barata.getPrecioCombustible(tipo)) ? "N/A" : String.format("%.3f", barata.getPrecioCombustible(tipo))
            });

            txtEstadisticas.setText("Gasolinera más barata para " + tipo + " en " + provincia + ":\n" +
                    barata.getDireccion() + ", " + barata.getLocalidad() + "\n" +
                    "Precio: " + (Double.isNaN(barata.getPrecioCombustible(tipo)) ? "N/A" : String.format("%.3f", barata.getPrecioCombustible(tipo))) + " €");
        } else {
            txtEstadisticas.setText("No se encontraron datos de gasolineras para " + tipo + " en " + provincia + ".");
        }
    }
}