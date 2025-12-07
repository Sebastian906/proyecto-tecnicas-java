package controllers.estructuras;

import java.util.LinkedList;
import java.util.Queue;
import java.util.NoSuchElementException;
import java.io.*;

/**
 * Cola para gestionar las reservas de libros agotados (FIFO)
 */
public class ColaReservas {
    private Queue<ReservaUsuario> cola;

    public ColaReservas() {
        this.cola = new LinkedList<>();
    }

    /**
     * Encola una nueva reserva (enqueue)
     */
    public void encolar(String usuarioId, String libroIsbn, String fecha) {
        cola.offer(new ReservaUsuario(usuarioId, libroIsbn, fecha));
    }

    /**
     * Desencola la primera reserva (dequeue)
     */
    public ReservaUsuario desencolar() {
        if (estaVacia()) {
            throw new NoSuchElementException("La cola está vacía");
        }
        return cola.poll();
    }

    /**
     * Consulta el frente de la cola sin removerlo (peek)
     */
    public ReservaUsuario verFrente() {
        if (estaVacia()) {
            throw new NoSuchElementException("La cola está vacía");
        }
        return cola.peek();
    }

    /**
     * Verifica si la cola está vacía
     */
    public boolean estaVacia() {
        return cola.isEmpty();
    }

    /**
     * Retorna el tamaño de la cola
     */
    public int tamanio() {
        return cola.size();
    }

    /**
     * Guarda las reservas en un archivo
     */
    public void guardarEnArchivo(String rutaArchivo) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(rutaArchivo))) {
            for (ReservaUsuario reserva : cola) {
                writer.write(reserva.getUsuarioId() + "," + 
                        reserva.getLibroIsbn() + "," + 
                        reserva.getFecha());
                writer.newLine();
            }
        }
    }

    /**
     * Carga las reservas desde un archivo
     */
    public void cargarDesdeArchivo(String rutaArchivo) throws IOException {
        cola.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader(rutaArchivo))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] partes = linea.split(",");
                if (partes.length == 3) {
                    encolar(partes[0].trim(), partes[1].trim(), partes[2].trim());
                }
            }
        }
    }

    @Override
    public String toString() {
        return "ColaReservas{tamaño=" + tamanio() + ", elementos=" + cola + "}";
    }

    /**
     * Clase interna para representar una reserva de usuario
     */
    public static class ReservaUsuario {
        private String usuarioId;
        private String libroIsbn;
        private String fecha;

        public ReservaUsuario(String usuarioId, String libroIsbn, String fecha) {
            this.usuarioId = usuarioId;
            this.libroIsbn = libroIsbn;
            this.fecha = fecha;
        }

        public String getUsuarioId() { return usuarioId; }
        public String getLibroIsbn() { return libroIsbn; }
        public String getFecha() { return fecha; }

        @Override
        public String toString() {
            return String.format("(%s, %s, %s)", usuarioId, libroIsbn, fecha);
        }
    }
}
