package controllers.ordenamiento;

import models.Libro;
import java.util.ArrayList;
import java.util.List;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Implementación del algoritmo Merge Sort
 * Se utiliza para generar reportes ordenados por Valor (COP)
 */
public class MergeSort {
    /**
     * Ordena una lista de libros por valor usando Merge Sort
     * 
     * @param libros Lista de libros a ordenar
     * @return Nueva lista ordenada por valor
     */
    public static List<Libro> ordenarPorValor(List<Libro> libros) {
        if (libros.size() <= 1) {
            return new ArrayList<>(libros);
        }
        
        return mergeSortRecursivo(libros);
    }
    
    /**
     * Implementación recursiva de Merge Sort
     */
    private static List<Libro> mergeSortRecursivo(List<Libro> libros) {
        if (libros.size() <= 1) {
            return libros;
        }
        
        // Dividir la lista en dos mitades
        int medio = libros.size() / 2;
        List<Libro> izquierda = new ArrayList<>(libros.subList(0, medio));
        List<Libro> derecha = new ArrayList<>(libros.subList(medio, libros.size()));
        
        // Ordenar recursivamente cada mitad
        izquierda = mergeSortRecursivo(izquierda);
        derecha = mergeSortRecursivo(derecha);
        
        // Mezclar las mitades ordenadas
        return merge(izquierda, derecha);
    }
    
    /**
     * Mezcla dos listas ordenadas en una sola lista ordenada
     */
    private static List<Libro> merge(List<Libro> izquierda, List<Libro> derecha) {
        List<Libro> resultado = new ArrayList<>();
        int i = 0, j = 0;
        
        // Comparar y mezclar elementos
        while (i < izquierda.size() && j < derecha.size()) {
            if (izquierda.get(i).getValor() <= derecha.get(j).getValor()) {
                resultado.add(izquierda.get(i));
                i++;
            } else {
                resultado.add(derecha.get(j));
                j++;
            }
        }
        
        // Agregar elementos restantes de la izquierda
        while (i < izquierda.size()) {
            resultado.add(izquierda.get(i));
            i++;
        }
        
        // Agregar elementos restantes de la derecha
        while (j < derecha.size()) {
            resultado.add(derecha.get(j));
            j++;
        }
        
        return resultado;
    }
    
    /**
     * Ordena libros por cualquier criterio usando Merge Sort
     * 
     * @param libros Lista de libros a ordenar
     * @param comparador Comparador personalizado
     * @return Nueva lista ordenada
     */
    public static List<Libro> ordenarConComparador(List<Libro> libros, java.util.Comparator<Libro> comparador) {
        if (libros.size() <= 1) {
            return new ArrayList<>(libros);
        }
        
        return mergeSortConComparador(libros, comparador);
    }
    
    private static List<Libro> mergeSortConComparador(List<Libro> libros, java.util.Comparator<Libro> comparador) {
        if (libros.size() <= 1) {
            return libros;
        }
        
        int medio = libros.size() / 2;
        List<Libro> izquierda = new ArrayList<>(libros.subList(0, medio));
        List<Libro> derecha = new ArrayList<>(libros.subList(medio, libros.size()));
        
        izquierda = mergeSortConComparador(izquierda, comparador);
        derecha = mergeSortConComparador(derecha, comparador);
        
        return mergeConComparador(izquierda, derecha, comparador);
    }
    
    private static List<Libro> mergeConComparador(List<Libro> izquierda, List<Libro> derecha, java.util.Comparator<Libro> comparador) {
        List<Libro> resultado = new ArrayList<>();
        int i = 0, j = 0;
        
        while (i < izquierda.size() && j < derecha.size()) {
            if (comparador.compare(izquierda.get(i), derecha.get(j)) <= 0) {
                resultado.add(izquierda.get(i));
                i++;
            } else {
                resultado.add(derecha.get(j));
                j++;
            }
        }
        
        while (i < izquierda.size()) {
            resultado.add(izquierda.get(i));
            i++;
        }
        
        while (j < derecha.size()) {
            resultado.add(derecha.get(j));
            j++;
        }
        
        return resultado;
    }
    
    /**
     * Genera un reporte de inventario ordenado por valor y lo guarda en un archivo
     * 
     * @param libros Lista de libros
     * @param rutaArchivo Ruta del archivo donde guardar el reporte
     * @throws IOException Si hay error al escribir el archivo
     */
    public static void generarReportePorValor(List<Libro> libros, String rutaArchivo) 
            throws IOException {
        // Ordenar libros por valor
        List<Libro> librosOrdenados = ordenarPorValor(libros);
        
        // Escribir reporte en archivo
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(rutaArchivo))) {
            writer.write("  REPORTE GLOBAL DE INVENTARIO\n");
            writer.write("  Ordenado por Valor (COP)\n");
            
            double valorTotal = 0;
            int contador = 1;
            
            for (Libro libro : librosOrdenados) {
                writer.write(String.format("%d. %s\n", contador, libro.getTitulo()));
                writer.write(String.format("   ISBN: %s\n", libro.getIsbn()));
                writer.write(String.format("   Autor: %s\n", libro.getAutor()));
                writer.write(String.format("   Valor: $%,.2f COP\n", libro.getValor()));
                writer.write(String.format("   Peso: %.2f kg\n", libro.getPeso()));
                writer.write(String.format("   Género: %s\n", libro.getGenero()));
                writer.write(String.format("   Disponibles: %d/%d\n", 
                    libro.getCantidadDisponible(), libro.getCantidadTotal()));
                writer.write("\n");
                
                valorTotal += libro.getValor() * libro.getCantidadTotal();
                contador++;
            }
            
            writer.write(String.format("Total de libros: %d\n", librosOrdenados.size()));
            writer.write(String.format("Valor total del inventario: $%,.2f COP\n", valorTotal));
        }
    }
}
