package views;

import controllers.GestorBiblioteca;
import controllers.adquisicion.LectorArchivo;
import controllers.ordenamiento.MergeSort;
import controllers.resolucion.FuerzaBruta;
import controllers.resolucion.Backtracking;
import controllers.recursion.ValorTotal;
import controllers.recursion.PesoPromedio;
import models.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.List;

/**
 * Interfaz gráfica principal del Sistema de Gestión de Bibliotecas
 */
@SuppressWarnings("unused")
public class BibliotecaGUI extends JFrame {
    private GestorBiblioteca gestor;
    private JTabbedPane tabbedPane;
    
    // Tablas
    private JTable tablaLibros;
    private DefaultTableModel modeloLibros;
    private JTable tablaUsuarios;
    private DefaultTableModel modeloUsuarios;
    
    public BibliotecaGUI() {
        gestor = new GestorBiblioteca();
        
        setTitle("Sistema de Gestión de Bibliotecas");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        crearInterfaz();
    }
    
    private void crearInterfaz() {
        tabbedPane = new JTabbedPane();
        
        tabbedPane.addTab("Libros", crearPanelLibros());
        tabbedPane.addTab("Usuarios", crearPanelUsuarios());
        tabbedPane.addTab("Préstamos", crearPanelPrestamos());
        tabbedPane.addTab("Reservas", crearPanelReservas());
        tabbedPane.addTab("Estantes", crearPanelEstantes());
        tabbedPane.addTab("Reportes", crearPanelReportes());
        
        add(tabbedPane);
    }
    
    // ========== PANEL LIBROS ==========
    
    private JPanel crearPanelLibros() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Botones superiores
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnCargar = new JButton("Cargar desde archivo");
        JButton btnAgregar = new JButton("Agregar libro");
        JButton btnBuscarISBN = new JButton("Buscar ISBN");
        JButton btnActualizar = new JButton("Actualizar lista");
        
        btnCargar.addActionListener(e -> cargarLibros());
        btnAgregar.addActionListener(e -> agregarLibro());
        btnBuscarISBN.addActionListener(e -> buscarLibroISBN());
        btnActualizar.addActionListener(e -> actualizarTablaLibros());
        
        panelBotones.add(btnCargar);
        panelBotones.add(btnAgregar);
        panelBotones.add(btnBuscarISBN);
        panelBotones.add(btnActualizar);
        
        // Panel de búsqueda
        JPanel panelBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelBusqueda.setBorder(BorderFactory.createTitledBorder("Búsqueda"));
        
        JTextField txtBuscarTitulo = new JTextField(15);
        JButton btnBuscarTitulo = new JButton("Buscar por Título");
        btnBuscarTitulo.addActionListener(e -> {
            List<Libro> libros = gestor.buscarLibrosPorTitulo(txtBuscarTitulo.getText());
            mostrarLibrosEnTabla(libros);
        });
        
        JTextField txtBuscarAutor = new JTextField(15);
        JButton btnBuscarAutor = new JButton("Buscar por Autor");
        btnBuscarAutor.addActionListener(e -> {
            List<Libro> libros = gestor.buscarLibrosPorAutor(txtBuscarAutor.getText());
            mostrarLibrosEnTabla(libros);
        });
        
        panelBusqueda.add(new JLabel("Título:"));
        panelBusqueda.add(txtBuscarTitulo);
        panelBusqueda.add(btnBuscarTitulo);
        panelBusqueda.add(new JLabel("Autor:"));
        panelBusqueda.add(txtBuscarAutor);
        panelBusqueda.add(btnBuscarAutor);
        
        // Tabla
        String[] columnas = {"ISBN", "Título", "Autor", "Peso", "Valor", "Disponibles"};
        modeloLibros = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaLibros = new JTable(modeloLibros);
        JScrollPane scrollPane = new JScrollPane(tablaLibros);
        
        // Ensamblar
        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.add(panelBotones, BorderLayout.NORTH);
        panelSuperior.add(panelBusqueda, BorderLayout.CENTER);
        
        panel.add(panelSuperior, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void cargarLibros() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
            "Archivos CSV/JSON", "csv", "json"));
        fileChooser.setCurrentDirectory(new File("data"));
        
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                File archivo = fileChooser.getSelectedFile();
                String rutaArchivo = archivo.getAbsolutePath();
                
                // Validar que el archivo existe
                if (!archivo.exists()) {
                    JOptionPane.showMessageDialog(this, 
                        "El archivo no existe: " + rutaArchivo, 
                        "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Validar extensión
                String extension = rutaArchivo.substring(rutaArchivo.lastIndexOf('.')).toLowerCase();
                if (!extension.equals(".csv") && !extension.equals(".json")) {
                    JOptionPane.showMessageDialog(this, 
                        "Formato no soportado: " + extension + "\nSolo se aceptan .csv y .json", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                System.out.println("Cargando archivo: " + rutaArchivo);
                List<Libro> libros = LectorArchivo.cargarLibros(rutaArchivo);
                
                if (libros == null || libros.isEmpty()) {
                    JOptionPane.showMessageDialog(this, 
                        "El archivo no contiene libros", 
                        "Advertencia", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                int agregados = 0;
                int duplicados = 0;
                for (Libro libro : libros) {
                    if (gestor.agregarLibro(libro)) {
                        agregados++;
                    } else {
                        duplicados++;
                    }
                }
                
                StringBuilder msg = new StringBuilder();
                msg.append(String.format("Se agregaron %d libros\n", agregados));
                if (duplicados > 0) {
                    msg.append(String.format("%d libros duplicados no se agregaron", duplicados));
                }
                
                JOptionPane.showMessageDialog(this, msg.toString(), 
                    "Carga completada", JOptionPane.INFORMATION_MESSAGE);
                actualizarTablaLibros();
            } catch (Exception ex) {
                System.err.println("Error al cargar archivo:");
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, 
                    "Error al cargar el archivo:\n" + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void agregarLibro() {
        JDialog dialog = new JDialog(this, "Agregar Libro", true);
        dialog.setLayout(new GridLayout(8, 2, 5, 5));
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(this);
        
        JTextField txtISBN = new JTextField();
        JTextField txtTitulo = new JTextField();
        JTextField txtAutor = new JTextField();
        JTextField txtPeso = new JTextField();
        JTextField txtValor = new JTextField();
        JTextField txtGenero = new JTextField();
        JTextField txtCantidad = new JTextField("1");
        
        dialog.add(new JLabel("ISBN:"));
        dialog.add(txtISBN);
        dialog.add(new JLabel("Título:"));
        dialog.add(txtTitulo);
        dialog.add(new JLabel("Autor:"));
        dialog.add(txtAutor);
        dialog.add(new JLabel("Peso (Kg):"));
        dialog.add(txtPeso);
        dialog.add(new JLabel("Valor (COP):"));
        dialog.add(txtValor);
        dialog.add(new JLabel("Género:"));
        dialog.add(txtGenero);
        dialog.add(new JLabel("Cantidad:"));
        dialog.add(txtCantidad);
        
        JButton btnGuardar = new JButton("Guardar");
        btnGuardar.addActionListener(e -> {
            try {
                // Validar campos vacíos
                String isbn = txtISBN.getText().trim();
                String titulo = txtTitulo.getText().trim();
                String autor = txtAutor.getText().trim();
                String pesoStr = txtPeso.getText().trim();
                String valorStr = txtValor.getText().trim();
                String genero = txtGenero.getText().trim();
                String cantidadStr = txtCantidad.getText().trim();
                
                if (isbn.isEmpty() || titulo.isEmpty() || autor.isEmpty() || 
                    pesoStr.isEmpty() || valorStr.isEmpty() || genero.isEmpty() || cantidadStr.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, 
                        "Todos los campos son obligatorios", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Validar valores numéricos
                double peso = Double.parseDouble(pesoStr);
                double valor = Double.parseDouble(valorStr);
                int cantidad = Integer.parseInt(cantidadStr);
                
                if (peso <= 0 || valor <= 0 || cantidad <= 0) {
                    JOptionPane.showMessageDialog(dialog, 
                        "Peso, Valor y Cantidad deben ser mayores a 0", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                Libro libro = new Libro(
                    isbn,
                    titulo,
                    autor,
                    peso,
                    valor,
                    genero,
                    cantidad,
                    cantidad,
                    null
                );
                
                if (gestor.agregarLibro(libro)) {
                    JOptionPane.showMessageDialog(dialog, "Libro agregado");
                    dialog.dispose();
                    actualizarTablaLibros();
                } else {
                    JOptionPane.showMessageDialog(dialog, "El libro ya existe", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, 
                    "Peso y Valor deben ser números. Cantidad debe ser un número entero", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Datos inválidos: " + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        dialog.add(btnGuardar);
        dialog.setVisible(true);
    }
    
    private void buscarLibroISBN() {
        String isbn = JOptionPane.showInputDialog(this, "Ingrese el ISBN:");
        if (isbn != null && !isbn.trim().isEmpty()) {
            Libro libro = gestor.buscarLibroPorISBN(isbn);
            if (libro != null) {
                String msg = String.format(
                    "Título: %s\nAutor: %s\nPeso: %.2f Kg\nValor: $%,.0f\nDisponibles: %d/%d",
                    libro.getTitulo(), libro.getAutor(), libro.getPeso(), 
                    libro.getValor(), libro.getCantidadDisponible(), libro.getCantidadTotal()
                );
                JOptionPane.showMessageDialog(this, msg, "Libro Encontrado", 
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Libro no encontrado", 
                    "No encontrado", JOptionPane.WARNING_MESSAGE);
            }
        }
    }
    
    private void actualizarTablaLibros() {
        mostrarLibrosEnTabla(gestor.obtenerTodosLosLibros());
    }
    
    private void mostrarLibrosEnTabla(List<Libro> libros) {
        modeloLibros.setRowCount(0);
        for (Libro libro : libros) {
            modeloLibros.addRow(new Object[]{
                libro.getIsbn(),
                libro.getTitulo().length() > 30 ? libro.getTitulo().substring(0, 30) + "..." : libro.getTitulo(),
                libro.getAutor().length() > 25 ? libro.getAutor().substring(0, 25) + "..." : libro.getAutor(),
                String.format("%.2f", libro.getPeso()),
                String.format("$%,.0f", libro.getValor()),
                String.format("%d/%d", libro.getCantidadDisponible(), libro.getCantidadTotal())
            });
        }
    }
    
    // ========== PANEL USUARIOS ==========
    
    private JPanel crearPanelUsuarios() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnAgregar = new JButton("Agregar usuario");
        JButton btnVerHistorial = new JButton("Ver historial");
        JButton btnActualizar = new JButton("Actualizar lista");
        
        btnAgregar.addActionListener(e -> agregarUsuario());
        btnVerHistorial.addActionListener(e -> verHistorial());
        btnActualizar.addActionListener(e -> actualizarTablaUsuarios());
        
        panelBotones.add(btnAgregar);
        panelBotones.add(btnVerHistorial);
        panelBotones.add(btnActualizar);
        
        // Tabla
        String[] columnas = {"ID", "Nombre", "Apellidos", "Dirección"};
        modeloUsuarios = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaUsuarios = new JTable(modeloUsuarios);
        JScrollPane scrollPane = new JScrollPane(tablaUsuarios);
        
        panel.add(panelBotones, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void agregarUsuario() {
        JDialog dialog = new JDialog(this, "Agregar Usuario", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(500, 250);
        dialog.setLocationRelativeTo(this);
        
        // Panel central con GridLayout para los campos
        JPanel panelCentral = new JPanel(new GridLayout(4, 2, 10, 10));
        panelCentral.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JTextField txtID = new JTextField();
        JTextField txtNombre = new JTextField();
        JTextField txtApellidos = new JTextField();
        JTextField txtDireccion = new JTextField();
        
        panelCentral.add(new JLabel("ID:"));
        panelCentral.add(txtID);
        panelCentral.add(new JLabel("Nombre:"));
        panelCentral.add(txtNombre);
        panelCentral.add(new JLabel("Apellidos:"));
        panelCentral.add(txtApellidos);
        panelCentral.add(new JLabel("Dirección:"));
        panelCentral.add(txtDireccion);
        
        // Panel inferior centrado para el botón
        JPanel panelBoton = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton btnGuardar = new JButton("Guardar");
        btnGuardar.addActionListener(e -> {
            try {
                // Validar campos vacíos
                String id = txtID.getText().trim();
                String nombre = txtNombre.getText().trim();
                String apellidos = txtApellidos.getText().trim();
                String direccion = txtDireccion.getText().trim();
                
                if (id.isEmpty() || nombre.isEmpty() || apellidos.isEmpty() || direccion.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, 
                        "Todos los campos son obligatorios", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                Usuario usuario = new Usuario(id, nombre, apellidos, direccion);
                
                if (gestor.agregarUsuario(usuario)) {
                    JOptionPane.showMessageDialog(dialog, "Usuario agregado");
                    dialog.dispose();
                    actualizarTablaUsuarios();
                } else {
                    JOptionPane.showMessageDialog(dialog, "El usuario ya existe", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        panelBoton.add(btnGuardar);
        
        dialog.add(panelCentral, BorderLayout.CENTER);
        dialog.add(panelBoton, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
    
    private void verHistorial() {
        String id = JOptionPane.showInputDialog(this, "ID del usuario:");
        if (id != null && !id.trim().isEmpty()) {
            Usuario usuario = gestor.buscarUsuario(id);
            if (usuario != null) {
                StringBuilder msg = new StringBuilder();
                msg.append("Historial de ").append(usuario.getNombre()).append(" ").append(usuario.getApellidos()).append(":\n\n");
                
                if (usuario.getHistorialPrestamos() != null && !usuario.getHistorialPrestamos().isEmpty()) {
                    msg.append("Préstamos: ").append(usuario.getHistorialPrestamos().size());
                } else {
                    msg.append("Sin préstamos");
                }
                
                JOptionPane.showMessageDialog(this, msg.toString(), 
                    "Historial", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Usuario no encontrado", 
                    "Error", JOptionPane.WARNING_MESSAGE);
            }
        }
    }
    
    private void actualizarTablaUsuarios() {
        modeloUsuarios.setRowCount(0);
        for (Usuario usuario : gestor.listarUsuarios()) {
            modeloUsuarios.addRow(new Object[]{
                usuario.getId(),
                usuario.getNombre(),
                usuario.getApellidos(),
                usuario.getDireccion()
            });
        }
    }
    
    // ========== PANEL PRÉSTAMOS ==========
    
    private JPanel crearPanelPrestamos() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Panel préstamo
        JPanel panelPrestamo = new JPanel(new GridLayout(3, 2, 5, 5));
        panelPrestamo.setBorder(BorderFactory.createTitledBorder("Realizar Préstamo"));
        
        JTextField txtPrestamoUsuario = new JTextField(15);
        JTextField txtPrestamoISBN = new JTextField(15);
        
        panelPrestamo.add(new JLabel("ID Usuario:"));
        panelPrestamo.add(txtPrestamoUsuario);
        panelPrestamo.add(new JLabel("ISBN Libro:"));
        panelPrestamo.add(txtPrestamoISBN);
        
        JButton btnPrestar = new JButton("Prestar");
        btnPrestar.addActionListener(e -> {
            GestorBiblioteca.ResultadoOperacion resultado = gestor.realizarPrestamo(
                txtPrestamoUsuario.getText(),
                txtPrestamoISBN.getText()
            );
            JOptionPane.showMessageDialog(this, resultado.getMensaje(), 
                resultado.isExito() ? "Éxito" : "Error",
                resultado.isExito() ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
            
            if (resultado.isExito()) {
                txtPrestamoUsuario.setText("");
                txtPrestamoISBN.setText("");
            }
        });
        panelPrestamo.add(btnPrestar);
        
        // Panel devolución
        JPanel panelDevolucion = new JPanel(new GridLayout(3, 2, 5, 5));
        panelDevolucion.setBorder(BorderFactory.createTitledBorder("Devolver Libro"));
        
        JTextField txtDevolucionUsuario = new JTextField(15);
        JTextField txtDevolucionISBN = new JTextField(15);
        
        panelDevolucion.add(new JLabel("ID Usuario:"));
        panelDevolucion.add(txtDevolucionUsuario);
        panelDevolucion.add(new JLabel("ISBN Libro:"));
        panelDevolucion.add(txtDevolucionISBN);
        
        JButton btnDevolver = new JButton("Devolver");
        btnDevolver.addActionListener(e -> {
            GestorBiblioteca.ResultadoOperacion resultado = gestor.devolverLibro(
                txtDevolucionUsuario.getText(),
                txtDevolucionISBN.getText()
            );
            JOptionPane.showMessageDialog(this, resultado.getMensaje(), 
                resultado.isExito() ? "Éxito" : "Error",
                resultado.isExito() ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
            
            if (resultado.isExito()) {
                txtDevolucionUsuario.setText("");
                txtDevolucionISBN.setText("");
            }
        });
        panelDevolucion.add(btnDevolver);
        
        // Agregar paneles
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(panelPrestamo, gbc);
        
        gbc.gridy = 1;
        panel.add(panelDevolucion, gbc);
        
        gbc.gridy = 2;
        JButton btnVerActivos = new JButton("Ver Préstamos Activos");
        btnVerActivos.addActionListener(e -> verPrestamosActivos());
        panel.add(btnVerActivos, gbc);
        
        return panel;
    }
    
    private void verPrestamosActivos() {
        StringBuilder msg = new StringBuilder("PRÉSTAMOS ACTIVOS:\n\n");
        int total = 0;
        
        for (Usuario u : gestor.listarUsuarios()) {
            if (u.getHistorialPrestamos() != null && !u.getHistorialPrestamos().isEmpty()) {
                msg.append(u.getNombre()).append(" ").append(u.getApellidos()).append(":\n");
                msg.append("  Préstamos: ").append(u.getHistorialPrestamos().size()).append("\n\n");
                total += u.getHistorialPrestamos().size();
            }
        }
        
        msg.append("\nTotal: ").append(total);
        JOptionPane.showMessageDialog(this, msg.toString(), "Préstamos Activos", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    // ========== PANEL RESERVAS ==========
    
    private JPanel crearPanelReservas() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JPanel panelReserva = new JPanel(new GridLayout(3, 2, 5, 5));
        panelReserva.setBorder(BorderFactory.createTitledBorder("Gestión de Reservas"));
        
        JTextField txtUsuario = new JTextField(15);
        JTextField txtISBN = new JTextField(15);
        
        panelReserva.add(new JLabel("ID Usuario:"));
        panelReserva.add(txtUsuario);
        panelReserva.add(new JLabel("ISBN Libro:"));
        panelReserva.add(txtISBN);
        
        JButton btnCrear = new JButton("Crear Reserva");
        btnCrear.addActionListener(e -> {
            GestorBiblioteca.ResultadoOperacion resultado = gestor.crearReserva(
                txtUsuario.getText(), txtISBN.getText());
            JOptionPane.showMessageDialog(this, resultado.getMensaje());
        });
        
        JButton btnCancelar = new JButton("Cancelar Reserva");
        btnCancelar.addActionListener(e -> {
            GestorBiblioteca.ResultadoOperacion resultado = gestor.cancelarReserva(
                txtUsuario.getText(), txtISBN.getText());
            JOptionPane.showMessageDialog(this, resultado.getMensaje());
        });
        
        panelReserva.add(btnCrear);
        panelReserva.add(btnCancelar);
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(panelReserva, gbc);
        
        return panel;
    }
    
    // ========== PANEL ESTANTES ==========
    
    private JPanel crearPanelEstantes() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Panel de botones superiores
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnAgregar = new JButton("Agregar Estante");
        JButton btnAsignar = new JButton("Asignar Libro");
        JButton btnListar = new JButton("Listar Estantes");
        JButton btnFuerzaBruta = new JButton("Análisis Peligroso");
        JButton btnBacktracking = new JButton("Optimización");
        
        btnAgregar.addActionListener(e -> agregarEstante());
        btnAsignar.addActionListener(e -> asignarLibroAEstante());
        btnListar.addActionListener(e -> listarEstantes());
        btnFuerzaBruta.addActionListener(e -> analisisPeligroso());
        btnBacktracking.addActionListener(e -> optimizacionEstanteria());
        
        panelBotones.add(btnAgregar);
        panelBotones.add(btnAsignar);
        panelBotones.add(btnListar);
        panelBotones.add(btnFuerzaBruta);
        panelBotones.add(btnBacktracking);
        
        // Tabla para listar estantes
        String[] columnas = {"ID Estante", "Espacios Disponibles", "Libros Asignados"};
        DefaultTableModel modeloEstantes = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable tablaEstantes = new JTable(modeloEstantes);
        JScrollPane scrollPane = new JScrollPane(tablaEstantes);
        
        panel.add(panelBotones, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void agregarEstante() {
        JDialog dialog = new JDialog(this, "Agregar Estante", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(450, 200);
        dialog.setLocationRelativeTo(this);
        
        // Panel central con GridLayout para los campos
        JPanel panelCentral = new JPanel(new GridLayout(2, 2, 10, 10));
        panelCentral.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JTextField txtID = new JTextField();
        JTextField txtEspacios = new JTextField();
        
        panelCentral.add(new JLabel("ID Estante:"));
        panelCentral.add(txtID);
        panelCentral.add(new JLabel("Espacios (cantidad de libros):"));
        panelCentral.add(txtEspacios);
        
        // Panel inferior centrado para el botón
        JPanel panelBoton = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton btnGuardar = new JButton("Guardar");
        btnGuardar.addActionListener(e -> {
            try {
                String id = txtID.getText().trim();
                String espaciosStr = txtEspacios.getText().trim();
                
                if (id.isEmpty() || espaciosStr.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Todos los campos son obligatorios", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                int espacios = Integer.parseInt(espaciosStr);
                if (espacios <= 0) {
                    JOptionPane.showMessageDialog(dialog, "Los espacios deben ser mayores a 0", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                Estante estante = new Estante(id, espacios);
                if (gestor.agregarEstante(estante)) {
                    JOptionPane.showMessageDialog(dialog, "Estante agregado correctamente");
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "El estante ya existe", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Los espacios deben ser un número entero", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        panelBoton.add(btnGuardar);
        
        dialog.add(panelCentral, BorderLayout.CENTER);
        dialog.add(panelBoton, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
    
    private void asignarLibroAEstante() {
        JDialog dialog = new JDialog(this, "Asignar Libro a Estante", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(450, 200);
        dialog.setLocationRelativeTo(this);
        
        // Panel central con GridLayout para los campos
        JPanel panelCentral = new JPanel(new GridLayout(2, 2, 10, 10));
        panelCentral.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JTextField txtISBN = new JTextField();
        JTextField txtIDEstante = new JTextField();
        
        panelCentral.add(new JLabel("ISBN del Libro:"));
        panelCentral.add(txtISBN);
        panelCentral.add(new JLabel("ID del Estante:"));
        panelCentral.add(txtIDEstante);
        
        // Panel inferior centrado para el botón
        JPanel panelBoton = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton btnAsignar = new JButton("Asignar");
        btnAsignar.addActionListener(e -> {
            try {
                String isbn = txtISBN.getText().trim();
                String idEstante = txtIDEstante.getText().trim();
                
                if (isbn.isEmpty() || idEstante.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Todos los campos son obligatorios", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                GestorBiblioteca.ResultadoOperacion resultado = gestor.asignarLibroAEstante(isbn, idEstante);
                if (resultado.isExito()) {
                    JOptionPane.showMessageDialog(dialog, resultado.getMensaje());
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, resultado.getMensaje(), 
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        panelBoton.add(btnAsignar);
        
        dialog.add(panelCentral, BorderLayout.CENTER);
        dialog.add(panelBoton, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
    
    private void listarEstantes() {
        List<Estante> estantes = gestor.listarEstantes();
        
        StringBuilder msg = new StringBuilder("LISTA DE ESTANTES\n\n");
        
        if (estantes.isEmpty()) {
            msg.append("No hay estantes registrados");
        } else {
            for (Estante estante : estantes) {
                msg.append(String.format("ID: %s | Espacios: %d | Libros: %d | Peso: %.2f / %.2f Kg\n",
                    estante.getId(),
                    estante.getCantidad(),
                    estante.getLibrosAsignados().size(),
                    estante.getPesoActual(),
                    estante.getPesoMaximo()));
            }
        }
        
        JOptionPane.showMessageDialog(this, msg.toString(), "Estantes", 
            JOptionPane.INFORMATION_MESSAGE);
    }

    
    private void analisisPeligroso() {
        List<Libro> libros = gestor.obtenerTodosLosLibros();
        if (libros.size() < 4) {
            JOptionPane.showMessageDialog(this, "Se necesitan al menos 4 libros");
            return;
        }
        
        List<FuerzaBruta.CombinacionPeligrosa> peligrosas = 
            FuerzaBruta.encontrarCombinaciones(libros, 4, 8.0);
        
        StringBuilder msg = new StringBuilder("FUERZA BRUTA - COMBINACIONES PELIGROSAS\n\n");
        msg.append("Total encontradas: ").append(peligrosas.size()).append("\n\n");
        
        for (int i = 0; i < Math.min(5, peligrosas.size()); i++) {
            FuerzaBruta.CombinacionPeligrosa comb = peligrosas.get(i);
            msg.append(String.format("[%d] %.2f Kg (Exceso: %.2f Kg)\n", 
                i+1, comb.getPesoTotal(), comb.getExceso()));
        }
        
        JOptionPane.showMessageDialog(this, msg.toString());
    }
    
    private void optimizacionEstanteria() {
        List<Libro> libros = gestor.obtenerTodosLosLibros();
        if (libros.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay libros");
            return;
        }
        
        Backtracking.SolucionEstanteria mejor = 
            Backtracking.optimizarEstanteria(libros, 8.0, false, 50);
        
        StringBuilder msg = new StringBuilder("BACKTRACKING - SOLUCIÓN ÓPTIMA\n\n");
        msg.append(String.format("Libros: %d\n", mejor.getLibros().size()));
        msg.append(String.format("Peso: %.2f / 8.0 Kg\n", mejor.getPesoTotal()));
        msg.append(String.format("Valor: $%,.0f COP\n", mejor.getValorTotal()));
        
        JOptionPane.showMessageDialog(this, msg.toString());
    }
    
    // ========== PANEL REPORTES ==========
    
    private JPanel crearPanelReportes() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        JButton btnEstadisticas = new JButton("Estadísticas");
        JButton btnReporte = new JButton("Generar Reporte");
        JButton btnValorAutor = new JButton("Valor por Autor");
        JButton btnPesoAutor = new JButton("Peso por Autor");
        
        btnEstadisticas.addActionListener(e -> mostrarEstadisticas());
        btnReporte.addActionListener(e -> generarReporte());
        btnValorAutor.addActionListener(e -> valorPorAutor());
        btnPesoAutor.addActionListener(e -> pesoPorAutor());
        
        panel.add(btnEstadisticas);
        panel.add(btnReporte);
        panel.add(btnValorAutor);
        panel.add(btnPesoAutor);
        
        return panel;
    }
    
    private void mostrarEstadisticas() {
        GestorBiblioteca.Estadisticas stats = gestor.obtenerEstadisticas();
        JOptionPane.showMessageDialog(this, stats.toString(), "Estadísticas", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void generarReporte() {
        try {
            // Crear carpeta reports si no existe
            File carpetaReports = new File("reports");
            if (!carpetaReports.exists()) {
                if (!carpetaReports.mkdir()) {
                    JOptionPane.showMessageDialog(this, 
                        "No se pudo crear la carpeta reports", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            
            // Obtener estadísticas
            GestorBiblioteca.Estadisticas stats = gestor.obtenerEstadisticas();
            
            // Crear nombre del archivo con timestamp
            String timestamp = new java.text.SimpleDateFormat("yyyyMMdd_HHmmss").format(new java.util.Date());
            String nombreArchivo = "reports/Reporte_Estadisticas_" + timestamp + ".txt";
            
            // Obtener todos los libros
            List<Libro> libros = gestor.obtenerTodosLosLibros();
            
            // Crear contenido del reporte
            StringBuilder contenido = new StringBuilder();
            contenido.append("================================================\n");
            contenido.append("REPORTE DE ESTADÍSTICAS DEL SISTEMA\n");
            contenido.append("================================================\n\n");
            contenido.append("Fecha y hora: ").append(new java.util.Date()).append("\n\n");
            
            contenido.append("--- ESTADÍSTICAS GENERALES ---\n");
            contenido.append("Total de Libros: ").append(stats.totalLibros).append("\n");
            contenido.append("Total de Usuarios: ").append(stats.totalUsuarios).append("\n");
            contenido.append("Préstamos Activos: ").append(stats.prestamosActivos).append("\n");
            contenido.append("Total de Reservas: ").append(stats.totalReservas).append("\n");
            contenido.append("Total de Estantes: ").append(stats.totalEstantes).append("\n\n");
            
            contenido.append("--- DETALLES DE LIBROS ---\n");
            if (libros.isEmpty()) {
                contenido.append("No hay libros registrados en el sistema.\n");
            } else {
                contenido.append(String.format("%-15s %-30s %-20s %-10s %-10s\n", 
                    "ISBN", "Título", "Autor", "Peso(kg)", "Valor($)"));
                contenido.append("=".repeat(85)).append("\n");
                
                for (Libro libro : libros) {
                    contenido.append(String.format("%-15s %-30s %-20s %-10.2f %-10.0f\n",
                        libro.getIsbn(),
                        libro.getTitulo().length() > 29 ? libro.getTitulo().substring(0, 29) : libro.getTitulo(),
                        libro.getAutor().length() > 19 ? libro.getAutor().substring(0, 19) : libro.getAutor(),
                        libro.getPeso(),
                        libro.getValor()));
                }
            }
            
            contenido.append("\n================================================\n");
            contenido.append("FIN DEL REPORTE\n");
            contenido.append("================================================\n");
            
            // Escribir archivo
            java.nio.file.Files.write(
                java.nio.file.Paths.get(nombreArchivo),
                contenido.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8)
            );
            
            JOptionPane.showMessageDialog(this, 
                "Reporte generado exitosamente en:\n" + nombreArchivo, 
                "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Error al generar reporte: " + ex.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void valorPorAutor() {
        String autor = JOptionPane.showInputDialog(this, "Nombre del autor:");
        if (autor != null && !autor.trim().isEmpty()) {
            List<Libro> libros = gestor.obtenerTodosLosLibros();
            double valorTotal = ValorTotal.calcularValorTotal(libros, autor);
            int cantidad = ValorTotal.contarLibrosAutor(libros, autor);
            
            String msg = String.format("RECURSIÓN DE PILA\n\nAutor: %s\nLibros: %d\nValor Total: $%,.0f COP",
                autor, cantidad, valorTotal);
            JOptionPane.showMessageDialog(this, msg);
        }
    }
    
    private void pesoPorAutor() {
        String autor = JOptionPane.showInputDialog(this, "Nombre del autor:");
        if (autor != null && !autor.trim().isEmpty()) {
            List<Libro> libros = gestor.obtenerTodosLosLibros();
            double pesoPromedio = PesoPromedio.calcularPesoPromedio(libros, autor);
            
            String msg = String.format("RECURSIÓN DE COLA\n\nAutor: %s\nPeso Promedio: %.2f Kg",
                autor, pesoPromedio);
            JOptionPane.showMessageDialog(this, msg);
        }
    }
    
    // ========== MAIN ==========
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            BibliotecaGUI gui = new BibliotecaGUI();
            gui.setVisible(true);
        });
    }
}
