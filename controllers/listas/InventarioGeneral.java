package controllers.listas;

import models.Libro;
import java.util.ArrayList;
import java.util.List;

/**
 * Gestiona el inventario general de libros (en el orden en que fueron agregados).
 * 
 * Atributos:
 *     libros (List): Lista que contiene los libros en el inventario.
 */
public class InventarioGeneral {
    private List<Libro> libros;
    
    /**
     * Inicializa el inventario general con una lista vacía de libros.
     */
    public InventarioGeneral() {
        this.libros = new ArrayList<>();
    }
    
    /**
     * Agrega un libro al inventario general.
     * 
     * @param libro Objeto Libro a agregar al inventario
     * @return true si el libro fue agregado exitosamente, false si el ISBN ya existe
     */
    public boolean agregarLibro(Libro libro) {
        // Verificar que el ISBN no exista en el inventario
        if (buscarPorISBN(libro.getIsbn()) != null) {
            return false;
        }
        
        libros.add(libro);
        return true;
    }
    
    /**
     * Agrega múltiples libros al inventario general.
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
     * Elimina un libro del inventario general por su ISBN.
     * 
     * @param isbn ISBN del libro a eliminar
     * @return true si el libro fue eliminado, false si no fue encontrado
     */
    public boolean eliminarLibro(String isbn) {
        for (int i = 0; i < libros.size(); i++) {
            if (libros.get(i).getIsbn().equals(isbn)) {
                libros.remove(i);
                return true;
            }
        }
        return false;
    }
    
    /**
     * Busca un libro en el inventario por su ISBN.
     * 
     * @param isbn ISBN del libro a buscar
     * @return El libro si es encontrado, null si no es encontrado
     */
    public Libro buscarPorISBN(String isbn) {
        for (Libro libro : libros) {
            if (libro.getIsbn().equals(isbn)) {
                return libro;
            }
        }
        return null;
    }
    
    /**
     * Busca libros en el inventario por su título.
     * 
     * @param titulo Título del libro a buscar
     * @return Lista de libros que coinciden con el título
     */
    public List<Libro> buscarPorTitulo(String titulo) {
        String tituloLower = titulo.toLowerCase();
        List<Libro> resultados = new ArrayList<>();
        
        for (Libro libro : libros) {
            if (libro.getTitulo().toLowerCase().contains(tituloLower)) {
                resultados.add(libro);
            }
        }
        return resultados;
    }
    
    /**
     * Busca libros en el inventario por su autor.
     * 
     * @param autor Autor del libro a buscar
     * @return Lista de libros que coinciden con el autor
     */
    public List<Libro> buscarPorAutor(String autor) {
        String autorLower = autor.toLowerCase();
        List<Libro> resultados = new ArrayList<>();
        
        for (Libro libro : libros) {
            if (libro.getAutor().toLowerCase().contains(autorLower)) {
                resultados.add(libro);
            }
        }
        return resultados;
    }
    
    /**
     * Obtiene la lista completa de libros en el inventario.
     * 
     * @return Copia de todos los libros en el inventario
     */
    public List<Libro> obtenerLibros() {
        return new ArrayList<>(libros);
    }
    
    /**
     * Obtiene el número total de libros en el inventario.
     * 
     * @return Número total de libros
     */
    public int cantidadLibros() {
        return libros.size();
    }
    
    /**
     * Verifica si el inventario está vacío.
     * 
     * @return true si el inventario está vacío, false en caso contrario
     */
    public boolean estaVacio() {
        return libros.isEmpty();
    }
    
    /**
     * Limpia todos los libros del inventario.
     */
    public void limpiarInventario() {
        libros.clear();
    }
    
    /**
     * Obtiene un libro por su índice en la lista del inventario.
     * 
     * @param indice Índice del libro a obtener
     * @return El libro si el índice es válido, null si es inválido
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
        return String.format("InventarioGeneral con %d libros (orden de carga).", libros.size());
    }
}