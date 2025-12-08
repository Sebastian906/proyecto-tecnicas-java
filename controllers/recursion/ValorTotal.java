package controllers.recursion;

import models.Libro;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementación de recursión de pila para calcular el valor total
 * de libros por autor.
 * 
 * La recursión de pila hace el cálculo AL REGRESAR de las llamadas recursivas.
 */
public class ValorTotal {
    
    /**
     * Calcula el valor total de todos los libros de un autor usando recursión de pila.
     * 
     * Esta función demuestra la recursión de pila donde el cálculo se hace
     * cuando se RETORNA de las llamadas recursivas. El resultado se acumula en la pila de llamadas.
     * 
     * @param listaLibros Lista de objetos Libro
     * @param autor Nombre del autor a buscar
     * @param indice Índice actual en la lista (default: 0)
     * @return Valor total acumulado de los libros del autor
     */
    public static double calcularValorTotal(List<Libro> listaLibros, String autor, int indice) {
        // Caso base: llegamos al final de la lista
        if (indice >= listaLibros.size()) {
            return 0.0;
        }
        
        Libro libroActual = listaLibros.get(indice);
        
        // Verificar si el libro es del autor buscado
        if (libroActual.getAutor().toLowerCase().contains(autor.toLowerCase())) {
            // Recursión: obtener el valor del resto de los libros
            double valorResto = calcularValorTotal(listaLibros, autor, indice + 1);
            // El trabajo se hace al regresar: Sumar el valor del libro actual con el resto
            return libroActual.getValor() + valorResto;
        } else {
            // Si no es del autor, solo continuar con el siguiente
            return calcularValorTotal(listaLibros, autor, indice + 1);
        }
    }
    
    /**
     * Sobrecarga con índice por defecto
     */
    public static double calcularValorTotal(List<Libro> listaLibros, String autor) {
        return calcularValorTotal(listaLibros, autor, 0);
    }
    
    /**
     * Versión que demuestra el proceso de recursión de pila en la consola.
     * 
     * Muestra cómo se construye la pila de llamadas y cómo los valores se acumulan al regresar.
     * 
     * @param listaLibros Lista de objetos Libro
     * @param autor Nombre del autor a buscar
     * @param indice Índice actual (default: 0)
     * @param nivel Nivel de profundidad (para indentación) (default: 0)
     * @return Valor total calculado
     */
    public static double calcularValorTotalConDemostracion(
            List<Libro> listaLibros, 
            String autor, 
            int indice, 
            int nivel) {
        
        String margen = "  ".repeat(nivel);
        
        // Caso base
        if (indice >= listaLibros.size()) {
            System.out.println(margen + "[Caso base] Fin de la lista, retornando 0");
            return 0.0;
        }
        
        Libro libroActual = listaLibros.get(indice);
        
        if (libroActual.getAutor().toLowerCase().contains(autor.toLowerCase())) {
            String tituloCorto = libroActual.getTitulo().length() > 40 ? 
                libroActual.getTitulo().substring(0, 40) : libroActual.getTitulo();
            
            System.out.println(String.format("%s→ [%d] Libro: %s", margen, nivel, tituloCorto));
            System.out.println(String.format("%s   Valor: $%,.0f | Llamando recursivamente...", margen, libroActual.getValor()));
            
            // Llamada recursiva (bajando por la pila)
            double valorResto = calcularValorTotalConDemostracion(listaLibros, autor, indice + 1, nivel + 1);
            
            // Trabajo al regresar (subiendo por la pila)
            double valorTotal = libroActual.getValor() + valorResto;
            System.out.println(String.format("%s← [%d] Regresando: $%,.0f + $%,.0f = $%,.0f", margen, nivel, libroActual.getValor(), valorResto, valorTotal));
            return valorTotal;
        } else {
            // No es del autor, seguir buscando
            return calcularValorTotalConDemostracion(listaLibros, autor, indice + 1, nivel);
        }
    }
    
    /**
     * Sobrecarga con valores por defecto
     */
    public static double calcularValorTotalConDemostracion(List<Libro> listaLibros, String autor) {
        return calcularValorTotalConDemostracion(listaLibros, autor, 0, 0);
    }
    
    /**
     * Cuenta cuántos libros tiene un autor (helper).
     */
    public static int contarLibrosAutor(List<Libro> listaLibros, String autor, int indice) {
        if (indice >= listaLibros.size()) {
            return 0;
        }
        
        Libro libroActual = listaLibros.get(indice);
        
        if (libroActual.getAutor().toLowerCase().contains(autor.toLowerCase())) {
            return 1 + contarLibrosAutor(listaLibros, autor, indice + 1);
        } else {
            return contarLibrosAutor(listaLibros, autor, indice + 1);
        }
    }
    
    public static int contarLibrosAutor(List<Libro> listaLibros, String autor) {
        return contarLibrosAutor(listaLibros, autor, 0);
    }
    
    /**
     * Obtiene todos los libros de un autor usando recursión de pila.
     */
    public static List<Libro> obtenerLibrosAutor(List<Libro> listaLibros, String autor, int indice) {
        if (indice >= listaLibros.size()) {
            return new ArrayList<>();
        }
        
        Libro libroActual = listaLibros.get(indice);
        List<Libro> librosResto = obtenerLibrosAutor(listaLibros, autor, indice + 1);
        
        if (libroActual.getAutor().toLowerCase().contains(autor.toLowerCase())) {
            List<Libro> resultado = new ArrayList<>();
            resultado.add(libroActual);
            resultado.addAll(librosResto);
            return resultado;
        } else {
            return librosResto;
        }
    }
    
    public static List<Libro> obtenerLibrosAutor(List<Libro> listaLibros, String autor) {
        return obtenerLibrosAutor(listaLibros, autor, 0);
    }
    
    /**
     * Análisis completo del valor de libros por autor usando recursión.
     */
    public static Map<String, Object> analizarValorPorAutor(List<Libro> listaLibros, String autor) {
        double valorTotal = calcularValorTotal(listaLibros, autor);
        int cantidadLibros = contarLibrosAutor(listaLibros, autor);
        List<Libro> librosEncontrados = obtenerLibrosAutor(listaLibros, autor);
        
        double valorPromedio = cantidadLibros > 0 ? valorTotal / cantidadLibros : 0;
        
        Map<String, Object> resultado = new HashMap<>();
        resultado.put("autor", autor);
        resultado.put("cantidad_libros", cantidadLibros);
        resultado.put("valor_total", valorTotal);
        resultado.put("valor_promedio", valorPromedio);
        resultado.put("libros", librosEncontrados);
        
        return resultado;
    }
    
    /**
     * Demuestra visualmente el proceso de recursión de pila.
     */
    public static double demostrarRecursionPila(List<Libro> listaLibros, String autor) {
        System.out.println("Recursión de Pila - Cálculo de Valor Total");
        System.out.println(String.format("\nAutor buscado: %s", autor));
        System.out.println(String.format("Total de libros en el inventario: %d", listaLibros.size()));
        System.out.println("\nProceso de recursión (se muestra la pila de llamadas):");
        
        double valorTotal = calcularValorTotalConDemostracion(listaLibros, autor);
        
        System.out.println(String.format("\nRESULTADO FINAL: $%,.0f COP", valorTotal));
        
        // Análisis adicional
        Map<String, Object> analisis = analizarValorPorAutor(listaLibros, autor);
        System.out.println("\nEstadísticas:");
        System.out.println(String.format("  • Libros encontrados: %d", analisis.get("cantidad_libros")));
        System.out.println(String.format("  • Valor total: $%,.0f COP", (Double)analisis.get("valor_total")));
        System.out.println(String.format("  • Valor promedio por libro: $%,.0f COP", (Double)analisis.get("valor_promedio")));
        
        @SuppressWarnings("unchecked")
        List<Libro> libros = (List<Libro>)analisis.get("libros");
        System.out.println(String.format("\nLibros de %s:", autor));
        for (int i = 0; i < libros.size(); i++) {
            Libro libro = libros.get(i);
            System.out.println(String.format("  %d. %s - $%,.0f COP", i + 1, libro.getTitulo(), libro.getValor()));
        }
        
        System.out.println("EXPLICACIÓN:");
        System.out.println("  • Recursión de PILA: El cálculo se hace AL REGRESAR");
        System.out.println("  • Cada llamada espera el resultado de la siguiente");
        System.out.println("  • Se acumula en la pila de llamadas del sistema");
        System.out.println("  • No es tail-recursive (no optimizable por el compilador)");
        
        return valorTotal;
    }
}