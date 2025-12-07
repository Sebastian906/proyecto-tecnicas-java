package controllers.estructuras;

import java.util.Stack;
import java.util.EmptyStackException;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Pila para gestionar el historial de préstamos (LIFO)
 * Cada elemento contiene ISBN y fecha de préstamo
 */
public class PilaHistorial {
    private Stack<RegistroPrestamo> pila;

    public PilaHistorial() {
        this.pila = new Stack<>();
    }

    /**
     * Apila un nuevo préstamo (push)
     */
    public void apilar(String isbn, String fecha) {
        pila.push(new RegistroPrestamo(isbn, fecha));
    }

    /**
     * Desapila el último préstamo (pop)
     */
    public RegistroPrestamo desapilar() {
        if (estaVacia()) {
            throw new EmptyStackException();
        }
        return pila.pop();
    }

    /**
     * Consulta el tope de la pila sin removerlo (peek)
     */
    public RegistroPrestamo verTope() {
        if (estaVacia()) {
            throw new EmptyStackException();
        }
        return pila.peek();
    }

    /**
     * Verifica si la pila está vacía
     */
    public boolean estaVacia() {
        return pila.isEmpty();
    }

    /**
     * Retorna el tamaño de la pila
     */
    public int tamanio() {
        return pila.size();
    }

    /**
     * Guarda el historial en un archivo
     */
    public void guardarEnArchivo(String rutaArchivo) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(rutaArchivo))) {
            // Guardamos de abajo hacia arriba para mantener el orden al cargar
            List<RegistroPrestamo> temporal = new ArrayList<>(pila);
            for (RegistroPrestamo registro : temporal) {
                writer.write(registro.getIsbn() + "," + registro.getFecha());
                writer.newLine();
            }
        }
    }

    /**
     * Carga el historial desde un archivo
     */
    public void cargarDesdeArchivo(String rutaArchivo) throws IOException {
        pila.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader(rutaArchivo))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] partes = linea.split(",");
                if (partes.length == 2) {
                    apilar(partes[0].trim(), partes[1].trim());
                }
            }
        }
    }

    @Override
    public String toString() {
        return "PilaHistorial{tamaño=" + tamanio() + ", elementos=" + pila + "}";
    }

    /**
     * Clase interna para representar un registro de préstamo
     */
    public static class RegistroPrestamo {
        private String isbn;
        private String fecha;

        public RegistroPrestamo(String isbn, String fecha) {
            this.isbn = isbn;
            this.fecha = fecha;
        }

        public String getIsbn() { return isbn; }
        public String getFecha() { return fecha; }

        @Override
        public String toString() {
            return String.format("(%s, %s)", isbn, fecha);
        }
    }
}
