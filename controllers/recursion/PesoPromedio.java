package controllers.recursion;

import models.Libro;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementación de recursión de cola para calcular el peso promedio
 * de libros por autor.
 * 
 * La recursión de cola hace el cálculo ANTES de las llamadas recursivas
 * usando acumuladores.
 */
public class PesoPromedio {
    /**
     * Calcula el peso promedio de libros de un autor usando recursión de cola.
     * 
     * Esta función demuestra la recursión de cola donde el cálculo se hace
     * ANTES de la llamada recursiva. Usa acumuladores para mantener el estado.
     * 
     * @param listaLibros Lista de objetos Libro
     * @param autor Nombre del autor a buscar
     * @param indice Índice actual en la lista (default: 0)
     * @param pesoAcumulado Peso acumulado hasta ahora (default: 0.0)
     * @param cantidadLibros Cantidad de libros encontrados (default: 0)
     * @return Peso promedio de los libros del autor
     */
    public static double calcularPesoPromedio(
            List<Libro> listaLibros,
            String autor,
            int indice,
            double pesoAcumulado,
            int cantidadLibros) {
        
        // Caso base: llegamos al final de la lista
        if (indice >= listaLibros.size()) {
            // Calcular promedio final
            if (cantidadLibros > 0) {
                return pesoAcumulado / cantidadLibros;
            }
            return 0.0;
        }
        
        Libro libroActual = listaLibros.get(indice);
        
        // Verificar si el libro es del autor buscado
        if (libroActual.getAutor().toLowerCase().contains(autor.toLowerCase())) {
            // Antes de la recursión: actualizar acumuladores
            double nuevoPeso = pesoAcumulado + libroActual.getPeso();
            int nuevaCantidad = cantidadLibros + 1;
            
            // Llamada recursiva de cola (última operación)
            return calcularPesoPromedio(listaLibros, autor, indice + 1, nuevoPeso, nuevaCantidad);
        } else {
            // Si no es del autor, continuar sin actualizar acumuladores
            return calcularPesoPromedio(listaLibros, autor, indice + 1, pesoAcumulado, cantidadLibros);
        }
    }
    
    /**
     * Sobrecarga con valores por defecto
     */
    public static double calcularPesoPromedio(List<Libro> listaLibros, String autor) {
        return calcularPesoPromedio(listaLibros, autor, 0, 0.0, 0);
    }
    
    /**
     * Versión que demuestra el proceso de recursión de cola en la consola.
     * 
     * Muestra cómo los acumuladores se actualizan ANTES de cada llamada recursiva
     * (característica clave de la recursión de cola).
     */
    public static double calcularPesoPromedioConDemostracion(
            List<Libro> listaLibros,
            String autor,
            int indice,
            double pesoAcumulado,
            int cantidadLibros,
            int nivel) {
        
        String margen = "  ".repeat(nivel);
        
        // Caso base
        if (indice >= listaLibros.size()) {
            double promedio = cantidadLibros > 0 ? pesoAcumulado / cantidadLibros : 0.0;
            System.out.println(margen + "[Caso base] Fin de la lista");
            System.out.println(String.format("%s  Peso acumulado: %.2f Kg", margen, pesoAcumulado));
            System.out.println(String.format("%s  Cantidad de libros: %d", margen, cantidadLibros));
            System.out.println(String.format("%s  Promedio final: %.2f Kg", margen, promedio));
            return promedio;
        }
        
        Libro libroActual = listaLibros.get(indice);
        
        if (libroActual.getAutor().toLowerCase().contains(autor.toLowerCase())) {
            // Actualizar acumuladores ANTES de la llamada recursiva
            double nuevoPeso = pesoAcumulado + libroActual.getPeso();
            int nuevaCantidad = cantidadLibros + 1;
            
            String tituloCorto = libroActual.getTitulo().length() > 40 ? 
                libroActual.getTitulo().substring(0, 40) : libroActual.getTitulo();
            
            System.out.println(String.format("%s[%d] Libro #%d: %s", margen, nivel, indice + 1, tituloCorto));
            System.out.println(String.format("%s    Peso: %.2f Kg", margen, libroActual.getPeso()));
            System.out.println(String.format("%s    → Acumulando: %.2f + %.2f = %.2f Kg", margen, pesoAcumulado, libroActual.getPeso(), nuevoPeso));
            System.out.println(String.format("%s    → Libros contados: %d", margen, nuevaCantidad));
            System.out.println(String.format("%s    → Llamada recursiva (TAIL CALL)...", margen));
            
            // Llamada recursiva de cola (última operación)
            return calcularPesoPromedioConDemostracion(
                listaLibros, autor, indice + 1, nuevoPeso, nuevaCantidad, nivel + 1
            );
        } else {
            // No es del autor, continuar sin modificar acumuladores
            return calcularPesoPromedioConDemostracion(
                listaLibros, autor, indice + 1, pesoAcumulado, cantidadLibros, nivel
            );
        }
    }
    
    /**
     * Sobrecarga con valores por defecto
     */
    public static double calcularPesoPromedioConDemostracion(List<Libro> listaLibros, String autor) {
        return calcularPesoPromedioConDemostracion(listaLibros, autor, 0, 0.0, 0, 0);
    }
    
    /**
     * Calcula estadísticas comprehensivas de peso usando recursión de cola.
     */
    public static Map<String, Object> calcularEstadisticasPeso(List<Libro> listaLibros, String autor) {
        // Peso total
        double pesoTotal = calcularPesoTotal(listaLibros, autor, 0, 0.0);
        
        // Cantidad
        int cantidad = contarLibros(listaLibros, autor, 0, 0);
        
        // Promedio
        double pesoPromedio = cantidad > 0 ? pesoTotal / cantidad : 0.0;
        
        // Mínimo
        double pesoMinimo = calcularPesoMinimo(listaLibros, autor, 0, Double.MAX_VALUE);
        if (pesoMinimo == Double.MAX_VALUE) pesoMinimo = 0.0;
        
        // Máximo
        double pesoMaximo = calcularPesoMaximo(listaLibros, autor, 0, 0.0);
        
        Map<String, Object> resultado = new HashMap<>();
        resultado.put("autor", autor);
        resultado.put("cantidad_libros", cantidad);
        resultado.put("peso_total", pesoTotal);
        resultado.put("peso_promedio", pesoPromedio);
        resultado.put("peso_minimo", pesoMinimo);
        resultado.put("peso_maximo", pesoMaximo);
        
        return resultado;
    }
    
    // Funciones auxiliares con recursión de cola
    private static double calcularPesoTotal(List<Libro> libros, String autor, int indice, double acumulado) {
        if (indice >= libros.size()) return acumulado;
        
        Libro libro = libros.get(indice);
        if (libro.getAutor().toLowerCase().contains(autor.toLowerCase())) {
            return calcularPesoTotal(libros, autor, indice + 1, acumulado + libro.getPeso());
        }
        return calcularPesoTotal(libros, autor, indice + 1, acumulado);
    }
    
    private static int contarLibros(List<Libro> libros, String autor, int indice, int contador) {
        if (indice >= libros.size()) return contador;
        
        Libro libro = libros.get(indice);
        if (libro.getAutor().toLowerCase().contains(autor.toLowerCase())) {
            return contarLibros(libros, autor, indice + 1, contador + 1);
        }
        return contarLibros(libros, autor, indice + 1, contador);
    }
    
    private static double calcularPesoMinimo(List<Libro> libros, String autor, int indice, double minimo) {
        if (indice >= libros.size()) return minimo;
        
        Libro libro = libros.get(indice);
        if (libro.getAutor().toLowerCase().contains(autor.toLowerCase())) {
            double nuevoMinimo = Math.min(minimo, libro.getPeso());
            return calcularPesoMinimo(libros, autor, indice + 1, nuevoMinimo);
        }
        return calcularPesoMinimo(libros, autor, indice + 1, minimo);
    }
    
    private static double calcularPesoMaximo(List<Libro> libros, String autor, int indice, double maximo) {
        if (indice >= libros.size()) return maximo;
        
        Libro libro = libros.get(indice);
        if (libro.getAutor().toLowerCase().contains(autor.toLowerCase())) {
            double nuevoMaximo = Math.max(maximo, libro.getPeso());
            return calcularPesoMaximo(libros, autor, indice + 1, nuevoMaximo);
        }
        return calcularPesoMaximo(libros, autor, indice + 1, maximo);
    }
    
    /**
     * Demuestra visualmente el proceso de recursión de cola.
     */
    public static double demostrarRecursionCola(List<Libro> listaLibros, String autor) {
        System.out.println("Recursión de Cola - Cálculo de Peso Promedio");
        System.out.println(String.format("\nAutor buscado: %s", autor));
        System.out.println(String.format("Total de libros en el inventario: %d", listaLibros.size()));
        System.out.println("\nProceso de recursión de cola (con acumuladores):");
        
        double pesoPromedio = calcularPesoPromedioConDemostracion(listaLibros, autor);
        
        System.out.println(String.format("\nRESULTADO FINAL: %.2f Kg", pesoPromedio));
        
        // Estadísticas adicionales
        Map<String, Object> stats = calcularEstadisticasPeso(listaLibros, autor);
        System.out.println(String.format("\nEstadísticas completas de %s:", autor));
        System.out.println(String.format("  • Libros encontrados: %d", stats.get("cantidad_libros")));
        System.out.println(String.format("  • Peso total: %.2f Kg", (Double)stats.get("peso_total")));
        System.out.println(String.format("  • Peso promedio: %.2f Kg", (Double)stats.get("peso_promedio")));
        System.out.println(String.format("  • Peso mínimo: %.2f Kg", (Double)stats.get("peso_minimo")));
        System.out.println(String.format("  • Peso máximo: %.2f Kg", (Double)stats.get("peso_maximo")));
        
        System.out.println("EXPLICACIÓN:");
        System.out.println("  • Recursión de COLA: El cálculo se hace ANTES de llamar");
        System.out.println("  • Usa acumuladores para mantener el estado");
        System.out.println("  • La llamada recursiva es lo ÚLTIMO que se ejecuta");
        System.out.println("  • Puede ser optimizada (Tail Call Optimization)");
        System.out.println("  • No acumula en la pila de llamadas");
        
        return pesoPromedio;
    }
}
