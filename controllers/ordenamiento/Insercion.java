package controllers.ordenamiento;

import models.Libro;
import java.util.List;

/**
 * Implementación del algoritmo de Ordenamiento por Inserción
 * Se utiliza para mantener el Inventario Ordenado por ISBN
 */
public class Insercion {
    
    /**
     * Ordena una lista de libros por ISBN usando el algoritmo de inserción
     * Este método modifica la lista original
     * 
     * @param libros Lista de libros a ordenar
     */
    public static void ordenarPorISBN(List<Libro> libros) {
        int n = libros.size();
        
        for (int i = 1; i < n; i++) {
            Libro libroActual = libros.get(i);
            int j = i - 1;
            
            // Mover elementos mayores una posición adelante
            while (j >= 0 && libros.get(j).getIsbn().compareTo(libroActual.getIsbn()) > 0) {
                libros.set(j + 1, libros.get(j));
                j--;
            }
            
            // Insertar el libro en su posición correcta
            libros.set(j + 1, libroActual);
        }
    }
    
    /**
     * Inserta un libro en una lista ya ordenada por ISBN
     * Mantiene el orden de la lista usando inserción ordenada
     * 
     * @param libros Lista ordenada de libros
     * @param nuevoLibro Libro a insertar
     */
    public static void insertarOrdenado(List<Libro> libros, Libro nuevoLibro) {
        int posicion = 0;
        
        // Buscar la posición correcta
        while (posicion < libros.size() && libros.get(posicion).getIsbn().compareTo(nuevoLibro.getIsbn()) < 0) {
            posicion++;
        }
        
        // Insertar en la posición correcta
        libros.add(posicion, nuevoLibro);
    }
    
    /**
     * Ordena una lista de libros por cualquier criterio usando inserción
     * 
     * @param libros Lista de libros a ordenar
     * @param comparador Comparador personalizado
     */
    public static void ordenarConComparador(List<Libro> libros, java.util.Comparator<Libro> comparador) {
        int n = libros.size();
        
        for (int i = 1; i < n; i++) {
            Libro libroActual = libros.get(i);
            int j = i - 1;
            
            while (j >= 0 && comparador.compare(libros.get(j), libroActual) > 0) {
                libros.set(j + 1, libros.get(j));
                j--;
            }
            
            libros.set(j + 1, libroActual);
        }
    }
}