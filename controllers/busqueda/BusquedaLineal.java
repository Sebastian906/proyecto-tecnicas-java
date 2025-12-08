package controllers.busqueda;

import models.Libro;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación de Búsqueda Lineal
 * Se utiliza para buscar en el Inventario General (lista desordenada)
 */
public class BusquedaLineal {
    
    /**
     * Busca libros por título en una lista desordenada
     * Búsqueda parcial (coincidencia de subcadena, case-insensitive)
     * 
     * @param libros Lista de libros donde buscar
     * @param titulo Título a buscar
     * @return Lista de libros que coinciden con el título
     */
    public static List<Libro> buscarPorTitulo(List<Libro> libros, String titulo) {
        List<Libro> resultados = new ArrayList<>();
        String tituloBusqueda = titulo.toLowerCase().trim();
        
        for (Libro libro : libros) {
            if (libro.getTitulo().toLowerCase().contains(tituloBusqueda)) {
                resultados.add(libro);
            }
        }
        
        return resultados;
    }
    
    /**
     * Busca libros por autor en una lista desordenada
     * Búsqueda parcial (coincidencia de subcadena, case-insensitive)
     * 
     * @param libros Lista de libros donde buscar
     * @param autor Autor a buscar
     * @return Lista de libros que coinciden con el autor
     */
    public static List<Libro> buscarPorAutor(List<Libro> libros, String autor) {
        List<Libro> resultados = new ArrayList<>();
        String autorBusqueda = autor.toLowerCase().trim();
        
        for (Libro libro : libros) {
            if (libro.getAutor().toLowerCase().contains(autorBusqueda)) {
                resultados.add(libro);
            }
        }
        
        return resultados;
    }
    
    /**
     * Busca libros por género en una lista desordenada
     * 
     * @param libros Lista de libros donde buscar
     * @param genero Género a buscar
     * @return Lista de libros que coinciden con el género
     */
    public static List<Libro> buscarPorGenero(List<Libro> libros, String genero) {
        List<Libro> resultados = new ArrayList<>();
        String generoBusqueda = genero.toLowerCase().trim();
        
        for (Libro libro : libros) {
            if (libro.getGenero().toLowerCase().contains(generoBusqueda)) {
                resultados.add(libro);
            }
        }
        
        return resultados;
    }
    
    /**
     * Busca un libro por ISBN en una lista desordenada
     * Búsqueda exacta
     * 
     * @param libros Lista de libros donde buscar
     * @param isbn ISBN a buscar
     * @return El libro encontrado o null si no existe
     */
    public static Libro buscarPorISBN(List<Libro> libros, String isbn) {
        String isbnBusqueda = isbn.trim();
        
        for (Libro libro : libros) {
            if (libro.getIsbn().equals(isbnBusqueda)) {
                return libro;
            }
        }
        
        return null;
    }
    
    /**
     * Busca libros con múltiples criterios
     * 
     * @param libros Lista de libros donde buscar
     * @param titulo Título a buscar (puede ser null)
     * @param autor Autor a buscar (puede ser null)
     * @param genero Género a buscar (puede ser null)
     * @return Lista de libros que coinciden con todos los criterios especificados
     */
    public static List<Libro> buscarMultiple(List<Libro> libros, String titulo, String autor, String genero) {
        List<Libro> resultados = new ArrayList<>();
        
        for (Libro libro : libros) {
            boolean coincide = true;
            
            if (titulo != null && !titulo.isEmpty()) {
                if (!libro.getTitulo().toLowerCase().contains(titulo.toLowerCase().trim())) {
                    coincide = false;
                }
            }
            
            if (autor != null && !autor.isEmpty()) {
                if (!libro.getAutor().toLowerCase().contains(autor.toLowerCase().trim())) {
                    coincide = false;
                }
            }
            
            if (genero != null && !genero.isEmpty()) {
                if (!libro.getGenero().toLowerCase().contains(genero.toLowerCase().trim())) {
                    coincide = false;
                }
            }
            
            if (coincide) {
                resultados.add(libro);
            }
        }
        
        return resultados;
    }
    
    /**
     * Cuenta el número de ocurrencias de un libro por título
     * 
     * @param libros Lista de libros donde buscar
     * @param titulo Título a buscar
     * @return Número de libros encontrados
     */
    public static int contarPorTitulo(List<Libro> libros, String titulo) {
        return buscarPorTitulo(libros, titulo).size();
    }
    
    /**
     * Cuenta el número de libros de un autor
     * 
     * @param libros Lista de libros donde buscar
     * @param autor Autor a buscar
     * @return Número de libros encontrados
     */
    public static int contarPorAutor(List<Libro> libros, String autor) {
        return buscarPorAutor(libros, autor).size();
    }
}