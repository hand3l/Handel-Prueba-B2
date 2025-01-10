import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class GestionCalificacionesView extends JFrame {
    private JTextField cedulaField, nombreField;
    private JTextField[] notaFields;
    private JLabel promedioLabel, estadoLabel;

    public GestionCalificacionesView() {
        setTitle("Gestión de Calificaciones");
        setSize(400, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Crear los JLabels y JTextFields para los campos
        JLabel cedulaLabel = new JLabel("Cédula:");
        JLabel nombreLabel = new JLabel("Nombre:");
        cedulaField = new JTextField();
        nombreField = new JTextField();

        // Establecer un número adecuado de columnas para los JTextFields
        cedulaField.setColumns(15);
        nombreField.setColumns(15);

        // Crear los campos de notas
        notaFields = new JTextField[5];
        for (int i = 0; i < 5; i++) {
            notaFields[i] = new JTextField();
            notaFields[i].setColumns(15); // Establecer el mismo tamaño para los campos de nota
        }

        // Etiquetas para el promedio y el estado
        promedioLabel = new JLabel("Promedio: ");
        estadoLabel = new JLabel("Estado: ");

        // Botones para calcular y guardar
        JButton calculateButton = new JButton("Calcular Promedio");
        JButton saveButton = new JButton("Guardar");

        // Usar GridBagLayout para organizar los componentes
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Espaciado entre componentes
        gbc.gridx = 0;
        gbc.gridy = 0;

        // Cédula
        add(cedulaLabel, gbc);
        gbc.gridx = 1;
        add(cedulaField, gbc);

        // Nombre
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(nombreLabel, gbc);
        gbc.gridx = 1;
        add(nombreField, gbc);

        // Notas
        for (int i = 0; i < 5; i++) {
            gbc.gridx = 0;
            gbc.gridy = i + 2;
            add(new JLabel("Nota " + (i + 1) + ":"), gbc);
            gbc.gridx = 1;
            add(notaFields[i], gbc);
        }

        // Promedio
        gbc.gridx = 0;
        gbc.gridy = 7;
        add(promedioLabel, gbc);

        // Estado
        gbc.gridx = 0;
        gbc.gridy = 8;
        add(estadoLabel, gbc);

        // Calcular Promedio y Guardar
        gbc.gridx = 0;
        gbc.gridy = 9;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(calculateButton, gbc);

        gbc.gridy = 10;
        add(saveButton, gbc);

        // Acciones de los botones
        calculateButton.addActionListener((ActionEvent e) -> calcularPromedio());
        saveButton.addActionListener((ActionEvent e) -> saveStudent());

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void calcularPromedio() {
        try {
            double suma = 0;
            for (JTextField field : notaFields) {
                double nota = Double.parseDouble(field.getText());
                if (nota < 0 || nota > 20) {
                    JOptionPane.showMessageDialog(null, "Las notas deben estar entre 0 y 20.");
                    return;
                }
                suma += nota;
            }
            double promedio = suma / notaFields.length;
            promedioLabel.setText("Promedio: " + String.format("%.2f", promedio));

            estadoLabel.setText(promedio >= 14 ? "Estado: Aprobado" : "Estado: Reprobado");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Por favor, ingrese valores válidos.");
        }
    }

    private void saveStudent() {
        try {
            String cedula = cedulaField.getText().trim();
            String nombre = nombreField.getText().trim();
            if (cedula.isEmpty() || nombre.isEmpty()) {
                JOptionPane.showMessageDialog(null, "La cédula y el nombre son obligatorios.");
                return;
            }

            try (Connection conn = DatabaseConnection.getConnection()) {
                String query = "INSERT INTO estudiantes (cedula, nombre, nota1, nota2, nota3, nota4, nota5, promedio) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(query);

                stmt.setString(1, cedula);
                stmt.setString(2, nombre);

                double suma = 0;
                for (int i = 0; i < 5; i++) {
                    double nota = Double.parseDouble(notaFields[i].getText());
                    stmt.setDouble(i + 3, nota);
                    suma += nota;
                }
                double promedio = suma / 5;
                stmt.setDouble(8, promedio);

                stmt.executeUpdate();
                JOptionPane.showMessageDialog(null, "Estudiante guardado correctamente.");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al guardar el estudiante: " + ex.getMessage());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Por favor, ingrese valores válidos.");
        }
    }

    public static void main(String[] args) {
        new GestionCalificacionesView();
    }
}



