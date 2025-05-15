package presentacion;

import logica.GestorGasolineras;
import modelo.Gasolinera;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class VentanaPrincipal extends JFrame {
    private JComboBox<String> comboProvincias;
    private JComboBox<String> comboTipos;
    private JButton btnFiltrar;
    private JButton btnEstadisticas;
    private JButton btnMasBarata;
    private JTextArea txtEstadisticas;
    private JTable tablaResultados;
    private DefaultTableModel modeloTabla;
    private GestorGasolineras gestor;

    public VentanaPrincipal(GestorGasolineras gestor) {
        this.gestor = gestor;
        setTitle("Gasolineras - Aplicación");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Panel superior con controles
        JPanel panelControles = new JPanel(new FlowLayout(FlowLayout.LEFT));
        comboProvincias = new JComboBox<>(gestor.obtenerProvincias().toArray(new String[0]));
        comboTipos = new JComboBox<>(gestor.obtenerTiposCombustible().toArray(new String[0]));
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

        // Panel central con tabla y estadísticas
        JPanel panelCentro = new JPanel();
        panelCentro.setLayout(new BoxLayout(panelCentro, BoxLayout.Y_AXIS));
        panelCentro.add(scrollTabla);
        panelCentro.add(Box.createVerticalStrut(10));
        panelCentro.add(scrollEstadisticas);

        add(panelCentro, BorderLayout.CENTER);

        // Acción botón Filtrar
        btnFiltrar.addActionListener((ActionEvent e) -> {
            mostrarResultadosFiltrados();
        });

        // Acción botón Estadísticas
        btnEstadisticas.addActionListener((ActionEvent e) -> {
            mostrarEstadisticas();
        });

        // Acción botón Más barata
        btnMasBarata.addActionListener((ActionEvent e) -> {
            mostrarGasolineraMasBarata();
        });
    }

    private void mostrarResultadosFiltrados() {
        String provincia = (String) comboProvincias.getSelectedItem();
        String tipo = (String) comboTipos.getSelectedItem();
        List<Gasolinera> filtradas = gestor.filtrarGasolineras(tipo, provincia);

        modeloTabla.setRowCount(0);
        for (Gasolinera g : filtradas) {
            modeloTabla.addRow(new Object[]{
                    g.getProvincia(), g.getMunicipio(), g.getLocalidad(), g.getDireccion(),
                    g.getPrecioCombustible(tipo)
            });
        }

        txtEstadisticas.setText("Se han encontrado " + filtradas.size() + " resultados.");
    }

    private void mostrarEstadisticas() {
        String provincia = (String) comboProvincias.getSelectedItem();
        String tipo = (String) comboTipos.getSelectedItem();
        List<Gasolinera> filtradas = gestor.filtrarGasolineras(tipo, provincia);

        String stats = gestor.obtenerEstadisticas(filtradas, tipo);
        txtEstadisticas.setText(stats);
    }

    private void mostrarGasolineraMasBarata() {
        String provincia = (String) comboProvincias.getSelectedItem();
        String tipo = (String) comboTipos.getSelectedItem();
        Gasolinera barata = gestor.obtenerGasolineraMasBarata(tipo, provincia);

        if (barata != null) {
            modeloTabla.setRowCount(0);
            modeloTabla.addRow(new Object[]{
                    barata.getProvincia(), barata.getMunicipio(), barata.getLocalidad(),
                    barata.getDireccion(), barata.getPrecioCombustible(tipo)
            });

            txtEstadisticas.setText("Gasolinera más barata:\n" + barata.getDireccion() +
                    "\nPrecio: " + barata.getPrecioCombustible(tipo));
        } else {
            txtEstadisticas.setText("No se encontraron datos.");
        }
    }
}
