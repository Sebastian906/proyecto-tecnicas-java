package controllers.listas;

import models.Libro;
import java.util.ArrayList;
import java.util.List;

/**
 * Gestiona el inventario ordenado de libros (según ISBN).
 * 
 * Cuando se agrega un libro, se utiliza un algoritmo de inserción para mantener el orden por ISBN.
 * 
 * Atributos:
 *     libros (List): Lista que contiene los libros ordenados por ISBN.
 */
public class InventarioOrdenado {
    private List<Libro> libros;
    
    /**
     * Inicializa el inventario ordenado con una lista vacía de libros.
     */
    public InventarioOrdenado() {
        this.libros = new ArrayList<>();
    }
    
    /**
     * Agrega un libro al inventario ordenado por ISBN.
     * 
     * Se utiliza un algoritmo de ordenamiento por inserción para mantener la lista ordenada.
     * 
     * @param libro Objeto Libro a agregar al inventario
     * @return true si el libro fue agregado exitosamente, false si el ISBN ya existe
     */
    public boolean agregarLibro(Libro libro) {
        // Verificar que el ISBN no exista en el inventario
        if (buscarPorISBN(libro.getIsbn()) != null) {
            return false;
        }
        
        // Agregar al final
        libros.add(libro);
        
        // Insertar el libro en la posición correcta para mantener el orden por ISBN
        int i = libros.size() - 1;
        while (i > 0 && libros.get(i).getIsbn().compareTo(libros.get(i - 1).getIsbn()) < 0) {
            // Intercambiar con el elemento anterior
            Libro temp = libros.get(i);
            libros.set(i, libros.get(i - 1));
            libros.set(i - 1, temp);
            i--;
        }
        return true;
    }
    
    /**
     * Agrega múltiples libros al inventario ordenado por ISBN.
     * 
     * @param listaLibros Lista de objetos Libro a agregar
     * @return Número de libros agregados exitosamente
     */
    public int agregarLibros(List<Libro> listaLibros) {
        int agregados = 0;
        for (Libro libro : listaLibros) {
            if (agregarLibro(libro)) {
                agregados++;
            }
        }
        return agregados;
    }
    
    /**
     * Elimina un libro del inventario ordenado por su ISBN.
     * 
     * @param isbn ISBN del libro a eliminar
     * @return true si el libro fue eliminado, false si no fue encontrado
     */
    public boolean eliminarLibro(String isbn) {
        // Se usa búsqueda binaria para encontrar el libro
        int indice = buscarIndiceBinario(isbn);
        if (indice != -1) {
            libros.remove(indice);
            return true;
        }
        return false;
    }
    
    /**
     * Busca un libro en el inventario por su ISBN usando búsqueda binaria.
     * 
     * @param isbn ISBN del libro a buscar
     * @return Objeto Libro si es encontrado, null si no es encontrado
     */
    public Libro buscarPorISBN(String isbn) {
        int indice = buscarIndiceBinario(isbn);
        if (indice != -1) {
            return libros.get(indice);
        }
        return null;
    }
    
    /**
     * Busca el índice de un libro en la lista usando búsqueda binaria.
     * 
     * @param isbn ISBN del libro a buscar
     * @return Índice del libro si es encontrado, -1 si no es encontrado
     */
    public int buscarIndiceBinario(String isbn) {
        int izquierda = 0;
        int derecha = libros.size() - 1;
        
        while (izquierda <= derecha) {
            int medio = (izquierda + derecha) / 2;
            String isbnMedio = libros.get(medio).getIsbn();
            
            if (isbnMedio.equals(isbn)) {
                return medio;
            } else if (isbnMedio.compareTo(isbn) < 0) {
                izquierda = medio + 1;
            } else {
                derecha = medio - 1;
            }
        }
        return -1; // No encontrado
    }
    
    /**
     * Obtiene la lista completa de libros en el inventario ordenado.
     * 
     * @return Lista de objetos Libro en el inventario
     */
    public List<Libro> obtenerLibros() {
        return libros;
    }
    
    /**
     * Obtiene el número total de libros en el inventario ordenado.
     * 
     * @return Número de libros en el inventario
     */
    public int cantidadLibros() {
        return libros.size();
    }
    
    /**
     * Verifica si el inventario ordenado está vacío.
     * 
     * @return true si el inventario está vacío, false en caso contrario
     */
    public boolean estaVacio() {
        return libros.isEmpty();
    }
    
    /**
     * Limpia todos los libros del inventario ordenado.
     */
    public void limpiarInventario() {
        libros.clear();
    }
    
    /**
     * Verifica si la lista de libros está ordenada por ISBN.
     * 
     * @return true si la lista está ordenada, false en caso contrario
     */
    public boolean verificarOrden() {
        for (int i = 1; i < libros.size(); i++) {
            if (libros.get(i).getIsbn().compareTo(libros.get(i - 1).getIsbn()) < 0) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Obtiene un libro por su índice en la lista ordenada.
     * 
     * @param indice Índice del libro a obtener
     * @return Objeto Libro si el índice es válido, null en caso contrario
     */
    public Libro obtenerPorIndice(int indice) {
        if (indice >= 0 && indice < libros.size()) {
            return libros.get(indice);
        }
        return null;
    }
    
    /**
     * Retorna el número de libros en el inventario.
     * 
     * @return Número total de libros
     */
    public int size() {
        return libros.size();
    }
    
    @Override
    public String toString() {
        return String.format("InventarioOrdenado con %d libros ordenados por ISBN.", libros.size());
    }
}