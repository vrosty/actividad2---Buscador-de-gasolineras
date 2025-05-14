package presentacion;

import javax.swing.*;
import logica.GestorGasolineras;
import modelo.Gasolinera;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class VentanaPrincipal extends JFrame {
    private JComboBox<String> comboCombustibles;
    private JComboBox<String> comboProvincias;
    private JTextArea areaResultados;
    private GestorGasolineras gestor;

    public VentanaPrincipal(GestorGasolineras gestor) {
        this.gestor = gestor;
        setTitle("Consulta de Gasolineras");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        inicializarComponentes();
    }

    private void inicializarComponentes() {
        JPanel panelSuperior = new JPanel(new GridLayout(2, 2));
        comboCombustibles = new JComboBox<>(gestor.obtenerTiposCombustible().toArray(new String[0]));
        comboProvincias = new JComboBox<>(gestor.obtenerProvincias().toArray(new String[0]));

        JButton btnFiltrar = new JButton("Filtrar");
        JButton btnEstadisticas = new JButton("Estadísticas");
        JButton btnMasBarata = new JButton("Más barata");

        panelSuperior.add(new JLabel("Combustible:"));
        panelSuperior.add(comboCombustibles);
        panelSuperior.add(new JLabel("Provincia:"));
        panelSuperior.add(comboProvincias);

        JPanel panelBotones = new JPanel();
        panelBotones.add(btnFiltrar);
        panelBotones.add(btnEstadisticas);
        panelBotones.add(btnMasBarata);

        areaResultados = new JTextArea();
        areaResultados.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(areaResultados);

        getContentPane().add(panelSuperior, BorderLayout.NORTH);
        getContentPane().add(panelBotones, BorderLayout.CENTER);
        getContentPane().add(scrollPane, BorderLayout.SOUTH);

        btnFiltrar.addActionListener(e -> {
            String combustible = comboCombustibles.getSelectedItem().toString();
            String provincia = comboProvincias.getSelectedItem().toString();
            List<Gasolinera> lista = gestor.filtrarGasolineras(combustible, provincia);
            mostrarGasolineras(lista);
        });

        btnEstadisticas.addActionListener(e -> {
            String combustible = comboCombustibles.getSelectedItem().toString();
            String provincia = comboProvincias.getSelectedItem().toString();
            List<Gasolinera> lista = gestor.filtrarGasolineras(combustible, provincia);
            areaResultados.setText(gestor.obtenerEstadisticas(lista, combustible));
        });

        btnMasBarata.addActionListener(e -> {
            String combustible = comboCombustibles.getSelectedItem().toString();
            String provincia = comboProvincias.getSelectedItem().toString();
            Gasolinera g = gestor.obtenerGasolineraMasBarata(combustible, provincia);
            if (g != null) {
                areaResultados.setText("Gasolinera más barata:\n" + g.toString());
            } else {
                areaResultados.setText("No se encontraron resultados.");
            }
        });
    }

    private void mostrarGasolineras(List<Gasolinera> gasolineras) {
        StringBuilder sb = new StringBuilder();
        for (Gasolinera g : gasolineras) {
            sb.append(g.toString()).append("\n");
        }
        areaResultados.setText(sb.toString());
    }
}
