package controllers.busqueda;

import models.Libro;
import java.util.List;

/**
 * Implementación de Búsqueda Binaria
 * Se utiliza para buscar en el Inventario Ordenado (lista ordenada por ISBN)
 * Esta función es CRÍTICA para verificar reservas pendientes
 */
public class BusquedaBinaria {
    /**
     * Resultado de una búsqueda binaria
     */
    public static class ResultadoBusqueda {
        private boolean encontrado;
        private int posicion;
        private Libro libro;
        
        public ResultadoBusqueda(boolean encontrado, int posicion, Libro libro) {
            this.encontrado = encontrado;
            this.posicion = posicion;
            this.libro = libro;
        }
        
        public boolean isEncontrado() { return encontrado; }
        public int getPosicion() { return posicion; }
        public Libro getLibro() { return libro; }
        
        @Override
        public String toString() {
            if (encontrado) {
                return String.format("Encontrado en posición %d: %s", posicion, libro);
            } else {
                return String.format("No encontrado (se insertaría en posición %d)", posicion);
            }
        }
    }
    
    /**
     * Busca un libro por ISBN en una lista ordenada usando búsqueda binaria
     * CRÍTICO: Utilizar para verificar si hay reservas pendientes
     * 
     * @param libros Lista de libros ORDENADA por ISBN
     * @param isbn ISBN a buscar
     * @return ResultadoBusqueda con información de la búsqueda
     */
    public static ResultadoBusqueda buscarPorISBN(List<Libro> libros, String isbn) {
        int izquierda = 0;
        int derecha = libros.size() - 1;
        String isbnBusqueda = isbn.trim();
        
        while (izquierda <= derecha) {
            int medio = izquierda + (derecha - izquierda) / 2;
            Libro libroMedio = libros.get(medio);
            int comparacion = libroMedio.getIsbn().compareTo(isbnBusqueda);
            
            if (comparacion == 0) {
                // Libro encontrado
                return new ResultadoBusqueda(true, medio, libroMedio);
            } else if (comparacion < 0) {
                // El ISBN buscado está en la mitad derecha
                izquierda = medio + 1;
            } else {
                // El ISBN buscado está en la mitad izquierda
                derecha = medio - 1;
            }
        }
        
        // Libro no encontrado
        // La posición 'izquierda' indica dónde debería insertarse
        return new ResultadoBusqueda(false, izquierda, null);
    }
    
    /**
     * Verifica si un libro existe en la lista ordenada
     * 
     * @param libros Lista de libros ORDENADA por ISBN
     * @param isbn ISBN a verificar
     * @return true si el libro existe, false en caso contrario
     */
    public static boolean existeLibro(List<Libro> libros, String isbn) {
        return buscarPorISBN(libros, isbn).isEncontrado();
    }
    
    /**
     * Obtiene un libro de la lista ordenada por ISBN
     * 
     * @param libros Lista de libros ORDENADA por ISBN
     * @param isbn ISBN del libro a obtener
     * @return El libro si existe, null en caso contrario
     */
    public static Libro obtenerLibro(List<Libro> libros, String isbn) {
        ResultadoBusqueda resultado = buscarPorISBN(libros, isbn);
        return resultado.isEncontrado() ? resultado.getLibro() : null;
    }
    
    /**
     * Busca múltiples libros por sus ISBNs
     * 
     * @param libros Lista de libros ORDENADA por ISBN
     * @param isbns Lista de ISBNs a buscar
     * @return Lista de libros encontrados
     */
    public static List<Libro> buscarMultiples(List<Libro> libros, List<String> isbns) {
        List<Libro> resultados = new java.util.ArrayList<>();
        
        for (String isbn : isbns) {
            Libro libro = obtenerLibro(libros, isbn);
            if (libro != null) {
                resultados.add(libro);
            }
        }
        
        return resultados;
    }
    
    /**
     * Busca el rango de posiciones donde están todos los libros de un rango de ISBNs
     * 
     * @param libros Lista de libros ORDENADA por ISBN
     * @param isbnInicio ISBN inicial del rango
     * @param isbnFin ISBN final del rango
     * @return Lista de libros en el rango
     */
    public static List<Libro> buscarRango(List<Libro> libros, String isbnInicio, String isbnFin) {
        List<Libro> resultados = new java.util.ArrayList<>();
        
        ResultadoBusqueda inicioResult = buscarPorISBN(libros, isbnInicio);
        int posInicio = inicioResult.isEncontrado() ? inicioResult.getPosicion() : inicioResult.getPosicion();
        
        for (int i = posInicio; i < libros.size(); i++) {
            Libro libro = libros.get(i);
            if (libro.getIsbn().compareTo(isbnFin) <= 0) {
                resultados.add(libro);
            } else {
                break;
            }
        }
        
        return resultados;
    }
    
    /**
     * Búsqueda binaria recursiva (implementación alternativa)
     * 
     * @param libros Lista de libros ORDENADA por ISBN
     * @param isbn ISBN a buscar
     * @param izquierda Índice izquierdo del rango
     * @param derecha Índice derecho del rango
     * @return ResultadoBusqueda con información de la búsqueda
     */
    public static ResultadoBusqueda buscarRecursivo(List<Libro> libros, String isbn, int izquierda, int derecha) {
        if (izquierda > derecha) {
            return new ResultadoBusqueda(false, izquierda, null);
        }
        
        int medio = izquierda + (derecha - izquierda) / 2;
        Libro libroMedio = libros.get(medio);
        int comparacion = libroMedio.getIsbn().compareTo(isbn.trim());
        
        if (comparacion == 0) {
            return new ResultadoBusqueda(true, medio, libroMedio);
        } else if (comparacion < 0) {
            return buscarRecursivo(libros, isbn, medio + 1, derecha);
        } else {
            return buscarRecursivo(libros, isbn, izquierda, medio - 1);
        }
    }
    
    /**
     * Versión simplificada de búsqueda recursiva
     * 
     * @param libros Lista de libros ORDENADA por ISBN
     * @param isbn ISBN a buscar
     * @return ResultadoBusqueda con información de la búsqueda
     */
    public static ResultadoBusqueda buscarRecursivo(List<Libro> libros, String isbn) {
        return buscarRecursivo(libros, isbn, 0, libros.size() - 1);
    }
}
