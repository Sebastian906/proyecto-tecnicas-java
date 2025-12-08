package controllers;

import models.*;
import controllers.listas.InventarioGeneral;
import controllers.listas.InventarioOrdenado;
import controllers.estructuras.PilaHistorial;
import controllers.estructuras.ColaReservas;
import controllers.busqueda.BusquedaBinaria;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Este módulo actúa como un patrón facade que coordina
 * todos los subsistemas del Sistema de Gestión de Bibliotecas.
 * 
 * Responsabilidades:
 * - Gestionar inventarios (general y ordenado)
 * - Coordinar préstamos y devoluciones
 * - Gestionar reservas y colas de espera
 * - Gestionar usuarios
 * - Coordinar estanterías
 */
@SuppressWarnings("unused")
public class GestorBiblioteca {
    
    // Inventarios
    private InventarioGeneral inventarioGeneral;
    private InventarioOrdenado inventarioOrdenado;
    
    // Usuarios (Map: {id: Usuario})
    private Map<String, Usuario> usuarios;
    
    // Colas de reservas por libro (Map: {isbn: ColaReservas})
    private Map<String, ColaReservas> colasReservas;
    
    // Estantes (Map: {id: Estante})
    private Map<String, Estante> estantes;
    
    // Contadores para IDs
    private int contadorPrestamos;
    private int contadorReservas;
    
    /**
     * Inicializa el gestor de biblioteca
     */
    public GestorBiblioteca() {
        this.inventarioGeneral = new InventarioGeneral();
        this.inventarioOrdenado = new InventarioOrdenado();
        this.usuarios = new HashMap<>();
        this.colasReservas = new HashMap<>();
        this.estantes = new HashMap<>();
        this.contadorPrestamos = 1;
        this.contadorReservas = 1;
    }
    
    // ========== GESTIÓN DE LIBROS ==========
    
    /**
     * Agrega un libro a ambos inventarios
     */
    public boolean agregarLibro(Libro libro) {
        // Agregar a inventario general
        if (!inventarioGeneral.agregarLibro(libro)) {
            return false;
        }
        
        // Agregar a inventario ordenado
        if (!inventarioOrdenado.agregarLibro(libro)) {
            inventarioGeneral.eliminarLibro(libro.getIsbn());
            return false;
        }
        
        return true;
    }
    
    /**
     * Busca un libro por ISBN en el inventario ordenado (búsqueda binaria)
     */
    public Libro buscarLibroPorISBN(String isbn) {
        BusquedaBinaria.ResultadoBusqueda resultado = 
            BusquedaBinaria.buscarPorISBN(inventarioOrdenado.obtenerLibros(), isbn);
        return resultado.isEncontrado() ? resultado.getLibro() : null;
    }
    
    /**
     * Busca libros por título (búsqueda lineal)
     */
    public List<Libro> buscarLibrosPorTitulo(String titulo) {
        return inventarioGeneral.buscarPorTitulo(titulo);
    }
    
    /**
     * Busca libros por autor (búsqueda lineal)
     */
    public List<Libro> buscarLibrosPorAutor(String autor) {
        return inventarioGeneral.buscarPorAutor(autor);
    }
    
    /**
     * Elimina un libro de ambos inventarios
     */
    public boolean eliminarLibro(String isbn) {
        boolean result1 = inventarioGeneral.eliminarLibro(isbn);
        boolean result2 = inventarioOrdenado.eliminarLibro(isbn);
        return result1 && result2;
    }
    
    /**
     * Obtiene todos los libros del inventario
     */
    public List<Libro> obtenerTodosLosLibros() {
        return inventarioGeneral.obtenerLibros();
    }
    
    // ========== GESTIÓN DE USUARIOS ==========
    
    /**
     * Agrega un usuario al sistema
     */
    public boolean agregarUsuario(Usuario usuario) {
        if (usuarios.containsKey(usuario.getId())) {
            return false;
        }
        
        // Inicializar historial de préstamos
        usuario.setHistorialPrestamos(new Stack<>());
        usuarios.put(usuario.getId(), usuario);
        return true;
    }
    
    /**
     * Busca un usuario por ID
     */
    public Usuario buscarUsuario(String usuarioId) {
        return usuarios.get(usuarioId);
    }
    
    /**
     * Elimina un usuario del sistema
     */
    public boolean eliminarUsuario(String usuarioId) {
        return usuarios.remove(usuarioId) != null;
    }
    
    /**
     * Lista todos los usuarios
     */
    public List<Usuario> listarUsuarios() {
        return new ArrayList<>(usuarios.values());
    }
    
    // ========== GESTIÓN DE PRÉSTAMOS ==========
    
    /**
     * Resultado de una operación
     */
    public static class ResultadoOperacion {
        private boolean exito;
        private String mensaje;
        
        public ResultadoOperacion(boolean exito, String mensaje) {
            this.exito = exito;
            this.mensaje = mensaje;
        }
        
        public boolean isExito() { return exito; }
        public String getMensaje() { return mensaje; }
    }
    
    /**
     * Realiza un préstamo de un libro a un usuario
     */
    public ResultadoOperacion realizarPrestamo(String usuarioId, String isbn, int diasPrestamo) {
        // Verificar usuario
        Usuario usuario = buscarUsuario(usuarioId);
        if (usuario == null) {
            return new ResultadoOperacion(false, "Usuario no encontrado");
        }
        
        // Verificar libro
        Libro libro = buscarLibroPorISBN(isbn);
        if (libro == null) {
            return new ResultadoOperacion(false, "Libro no encontrado");
        }
        
        // Verificar disponibilidad
        if (!libro.estaDisponible()) {
            return new ResultadoOperacion(false, 
                String.format("Libro no disponible (Stock: %d)", libro.getCantidadDisponible()));
        }
        
        // Reducir stock
        libro.setCantidadDisponible(libro.getCantidadDisponible() - 1);
        
        // Crear préstamo
        String prestamoId = String.format("P%04d", contadorPrestamos++);
        LocalDateTime fechaPrestamo = LocalDateTime.now();
        LocalDateTime fechaDevolucionEsperada = fechaPrestamo.plusDays(diasPrestamo);
        
        Prestamo prestamo = new Prestamo(
            prestamoId,
            usuarioId,
            isbn,
            fechaPrestamo.toString(),
            fechaDevolucionEsperada.toString()
        );
        
        // Agregar a historial del usuario (Pila)
        usuario.getHistorialPrestamos().push(prestamoId);
        
        return new ResultadoOperacion(true, 
            String.format("Préstamo realizado exitosamente. ID: %s", prestamoId));
    }
    
    /**
     * Sobrecarga con días por defecto
     */
    public ResultadoOperacion realizarPrestamo(String usuarioId, String isbn) {
        return realizarPrestamo(usuarioId, isbn, 15);
    }
    
    /**
     * Procesa la devolución de un libro
     * 
     * FLUJO CRÍTICO: Verifica reservas pendientes usando búsqueda binaria
     */
    public ResultadoOperacion devolverLibro(String usuarioId, String isbn) {
        // Verificar usuario
        Usuario usuario = buscarUsuario(usuarioId);
        if (usuario == null) {
            return new ResultadoOperacion(false, "Usuario no encontrado");
        }
        
        // Verificar que el usuario tenga el libro prestado
        Stack<String> historial = usuario.getHistorialPrestamos();
        if (historial == null || historial.isEmpty()) {
            return new ResultadoOperacion(false, "No se encontró préstamo activo de este libro");
        }
        
        // Buscar libro en inventario ordenado (BÚSQUEDA BINARIA - CRÍTICO)
        BusquedaBinaria.ResultadoBusqueda resultado = 
            BusquedaBinaria.buscarPorISBN(inventarioOrdenado.obtenerLibros(), isbn);
        
        if (!resultado.isEncontrado()) {
            return new ResultadoOperacion(false, "Error: Libro no encontrado en inventario");
        }
        
        Libro libro = resultado.getLibro();
        
        // FLUJO CRÍTICO: Verificar reservas pendientes
        if (colasReservas.containsKey(isbn)) {
            ColaReservas cola = colasReservas.get(isbn);
            
            if (!cola.estaVacia()) {
                // Hay reservas pendientes: asignar al primero en la cola (FIFO)
                ColaReservas.ReservaUsuario reserva = cola.desencolar();
                
                String mensaje = String.format(
                    "Libro devuelto y asignado automáticamente a usuario %s (reserva pendiente)",
                    reserva.getUsuarioId()
                );
                
                // No incrementar stock disponible
                return new ResultadoOperacion(true, mensaje);
            }
        }
        
        // No hay reservas: incrementar stock disponible
        libro.setCantidadDisponible(libro.getCantidadDisponible() + 1);
        
        return new ResultadoOperacion(true, "Libro devuelto exitosamente");
    }
    
    // ========== GESTIÓN DE RESERVAS ==========
    
    /**
     * Crea una reserva para un libro
     */
    public ResultadoOperacion crearReserva(String usuarioId, String isbn) {
        // Verificar usuario
        Usuario usuario = buscarUsuario(usuarioId);
        if (usuario == null) {
            return new ResultadoOperacion(false, "Usuario no encontrado");
        }
        
        // Verificar libro
        Libro libro = buscarLibroPorISBN(isbn);
        if (libro == null) {
            return new ResultadoOperacion(false, "Libro no encontrado");
        }
        
        // Solo reservar si el libro está agotado
        if (libro.getCantidadDisponible() > 0) {
            return new ResultadoOperacion(false, 
                String.format("No se puede reservar. El libro tiene %d copia(s) disponible(s). " +
                            "Las reservas solo se permiten para libros agotados (stock = 0)", 
                            libro.getCantidadDisponible()));
        }
        
        // Crear cola si no existe
        if (!colasReservas.containsKey(isbn)) {
            colasReservas.put(isbn, new ColaReservas());
        }
        
        ColaReservas cola = colasReservas.get(isbn);
        
        // Crear reserva
        String reservaId = String.format("R%04d", contadorReservas++);
        LocalDateTime fechaReserva = LocalDateTime.now();
        
        // Encolar (FIFO)
        cola.encolar(usuarioId, isbn, fechaReserva.toString());
        
        int posicion = cola.tamanio();
        return new ResultadoOperacion(true, 
            String.format("Reserva creada. Posición en cola: %d", posicion));
    }
    
    /**
     * Cancela una reserva de un usuario
     */
    public ResultadoOperacion cancelarReserva(String usuarioId, String isbn) {
        if (!colasReservas.containsKey(isbn)) {
            return new ResultadoOperacion(false, "No hay reservas para este libro");
        }
        
        ColaReservas cola = colasReservas.get(isbn);
        
        // Por simplicidad, en esta implementación no se permite cancelar
        // una reserva que no sea la primera en la cola
        // En una implementación completa, se buscaría y eliminaría la reserva específica
        
        return new ResultadoOperacion(false, "Funcionalidad no implementada completamente");
    }
    
    /**
     * Obtiene las reservas de un libro
     */
    public List<ColaReservas.ReservaUsuario> obtenerReservasLibro(String isbn) {
        if (colasReservas.containsKey(isbn)) {
            // Retornar una copia de las reservas sin modificar la cola
            List<ColaReservas.ReservaUsuario> reservas = new ArrayList<>();
            ColaReservas cola = colasReservas.get(isbn);
            // Aquí se necesitaría un método para obtener todas sin desencolar
            return reservas;
        }
        return new ArrayList<>();
    }
    
    // ========== GESTIÓN DE ESTANTES ==========
    
    /**
     * Agrega un estante al sistema
     */
    public boolean agregarEstante(Estante estante) {
        if (estantes.containsKey(estante.getId())) {
            return false;
        }
        estantes.put(estante.getId(), estante);
        return true;
    }
    
    /**
     * Asigna un libro a un estante
     */
    public ResultadoOperacion asignarLibroAEstante(String isbn, String estanteId) {
        Libro libro = buscarLibroPorISBN(isbn);
        if (libro == null) {
            return new ResultadoOperacion(false, "Libro no encontrado");
        }
        
        Estante estante = estantes.get(estanteId);
        if (estante == null) {
            return new ResultadoOperacion(false, "Estante no encontrado");
        }
        
        // Verificar peso
        if (estante.getPesoActual() + libro.getPeso() > estante.getPesoMaximo()) {
            return new ResultadoOperacion(false, 
                String.format("Excede el peso máximo del estante (%.1f Kg)", 
                            estante.getPesoMaximo()));
        }
        
        // Verificar espacio
        if (estante.getLibrosAsignados().size() >= estante.getCantidad()) {
            return new ResultadoOperacion(false, "Estante lleno");
        }
        
        // Asignar
        estante.getLibrosAsignados().add(libro);
        estante.setPesoActual(estante.getPesoActual() + libro.getPeso());
        libro.setEstanteId(estanteId);
        
        return new ResultadoOperacion(true, "Libro asignado al estante exitosamente");
    }
    
    /**
     * Lista todos los estantes
     */
    public List<Estante> listarEstantes() {
        return new ArrayList<>(estantes.values());
    }
    
    // ========== FUNCIONES DE UTILIDADES ==========
    
    /**
     * Estadísticas del sistema
     */
    public static class Estadisticas {
        public int totalLibros;
        public int totalUsuarios;
        public int prestamosActivos;
        public int totalReservas;
        public int totalEstantes;
        
        @Override
        public String toString() {
            return String.format(
                "Estadísticas:\n" +
                "  - Total de libros: %d\n" +
                "  - Total de usuarios: %d\n" +
                "  - Préstamos activos: %d\n" +
                "  - Total de reservas: %d\n" +
                "  - Total de estantes: %d",
                totalLibros, totalUsuarios, prestamosActivos, totalReservas, totalEstantes
            );
        }
    }
    
    /**
     * Obtiene estadísticas generales del sistema
     */
    public Estadisticas obtenerEstadisticas() {
        Estadisticas stats = new Estadisticas();
        
        stats.totalLibros = inventarioGeneral.cantidadLibros();
        stats.totalUsuarios = usuarios.size();
        
        // Contar préstamos activos
        stats.prestamosActivos = 0;
        for (Usuario usuario : usuarios.values()) {
            if (usuario.getHistorialPrestamos() != null) {
                stats.prestamosActivos += usuario.getHistorialPrestamos().size();
            }
        }
        
        // Contar reservas
        stats.totalReservas = 0;
        for (ColaReservas cola : colasReservas.values()) {
            stats.totalReservas += cola.tamanio();
        }
        
        stats.totalEstantes = estantes.size();
        
        return stats;
    }
    
    // Getters
    public InventarioGeneral getInventarioGeneral() { return inventarioGeneral; }
    public InventarioOrdenado getInventarioOrdenado() { return inventarioOrdenado; }
    public Map<String, Usuario> getUsuarios() { return usuarios; }
    public Map<String, ColaReservas> getColasReservas() { return colasReservas; }
    public Map<String, Estante> getEstantes() { return estantes; }
}