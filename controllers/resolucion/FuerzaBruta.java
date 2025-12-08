package controllers.resolucion;

import models.Libro;
import java.util.ArrayList;
import java.util.List;

/**
 * Algoritmo de fuerza bruta para encontrar combinaciones de libros peligrosas
 * Este algoritmo encuentra todas las combinaciones posibles que exceden
 * el umbral de riesgo de 8 Kg (estantería deficiente).
 */
public class FuerzaBruta {
    
    /**
     * Clase interna para representar una combinación peligrosa
     */
    public static class CombinacionPeligrosa {
        private List<Libro> libros;
        private double pesoTotal;
        private double exceso;
        
        public CombinacionPeligrosa(List<Libro> libros, double pesoTotal, double exceso) {
            this.libros = new ArrayList<>(libros);
            this.pesoTotal = pesoTotal;
            this.exceso = exceso;
        }
        
        public List<Libro> getLibros() { return libros; }
        public double getPesoTotal() { return pesoTotal; }
        public double getExceso() { return exceso; }
        
        @Override
        public String toString() {
            return String.format("Peso: %.2f Kg (Exceso: %.2f Kg) - %d libros", 
                               pesoTotal, exceso, libros.size());
        }
    }
    
    /**
     * Encuentra todas las combinaciones de libros que exceden el peso máximo.
     * 
     * Este algoritmo de fuerza bruta explora TODAS las combinaciones posibles
     * de 'numLibros' libros y retorna aquellas cuyo peso total excede el
     * umbral de riesgo (8 Kg).
     * 
     * @param listaLibros Lista de objetos Libro disponibles
     * @param numLibros Número de libros por combinación (default: 4)
     * @param pesoMaximo Peso máximo permitido en Kg (default: 8.0)
     * @return Lista de combinaciones peligrosas
     */
    public static List<CombinacionPeligrosa> encontrarCombinaciones(
            List<Libro> listaLibros, 
            int numLibros, 
            double pesoMaximo) {
        
        List<CombinacionPeligrosa> combinacionesPeligrosas = new ArrayList<>();
        int[] totalCombinaciones = {0};
        
        System.out.println(String.format("\nExplorando todas las combinaciones de %d libros...", numLibros));
        
        // Generar todas las combinaciones posibles
        List<Libro> combinacionActual = new ArrayList<>();
        generarCombinaciones(listaLibros, numLibros, 0, combinacionActual, 
                           combinacionesPeligrosas, pesoMaximo, totalCombinaciones);
        
        System.out.println(String.format("Exploración completada: %,d combinaciones analizadas", 
                                       totalCombinaciones[0]));
        System.out.println(String.format("Combinaciones peligrosas encontradas: %d", 
                                       combinacionesPeligrosas.size()));
        
        return combinacionesPeligrosas;
    }
    
    /**
     * Sobrecarga con valores por defecto
     */
    public static List<CombinacionPeligrosa> encontrarCombinaciones(List<Libro> listaLibros) {
        return encontrarCombinaciones(listaLibros, 4, 8.0);
    }
    
    /**
     * Genera combinaciones recursivamente
     */
    private static void generarCombinaciones(
            List<Libro> listaLibros,
            int numLibros,
            int inicio,
            List<Libro> combinacionActual,
            List<CombinacionPeligrosa> combinacionesPeligrosas,
            double pesoMaximo,
            int[] totalCombinaciones) {
        
        // Caso base: hemos seleccionado suficientes libros
        if (combinacionActual.size() == numLibros) {
            totalCombinaciones[0]++;
            
            // Calcular peso total
            double pesoTotal = 0;
            for (Libro libro : combinacionActual) {
                pesoTotal += libro.getPeso();
            }
            
            // Si excede el límite, es peligrosa
            if (pesoTotal > pesoMaximo) {
                double exceso = pesoTotal - pesoMaximo;
                combinacionesPeligrosas.add(
                    new CombinacionPeligrosa(combinacionActual, pesoTotal, exceso)
                );
            }
            return;
        }
        
        // Generar combinaciones
        for (int i = inicio; i < listaLibros.size(); i++) {
            combinacionActual.add(listaLibros.get(i));
            generarCombinaciones(listaLibros, numLibros, i + 1, combinacionActual,
                               combinacionesPeligrosas, pesoMaximo, totalCombinaciones);
            combinacionActual.remove(combinacionActual.size() - 1);
        }
    }
    
    /**
     * Demuestra paso a paso la exploración exhaustiva de fuerza bruta.
     * 
     * Imprime el proceso de exploración a la consola con fines educativos.
     * 
     * @param listaLibros Lista de objetos Libro
     * @param numLibros Número de libros (default: 4)
     * @param pesoMaximo Peso máximo en Kg (default: 8.0)
     * @param mostrarPrimeras Cuántas combinaciones mostrar (default: 15)
     * @return Lista de combinaciones peligrosas encontradas
     */
    public static List<CombinacionPeligrosa> demostrarExploracionFuerzaBruta(
            List<Libro> listaLibros,
            int numLibros,
            double pesoMaximo,
            int mostrarPrimeras) {
        
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("Exploración Exhaustiva - Fuerza Bruta");
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("\nParámetros:");
        System.out.println(String.format("  • Total de libros: %d", listaLibros.size()));
        System.out.println(String.format("  • Libros por combinación: %d", numLibros));
        System.out.println(String.format("  • Peso máximo permitido: %.1f Kg", pesoMaximo));
        System.out.println(String.format("  • Umbral de riesgo: > %.1f Kg", pesoMaximo));
        
        long totalPosibles = calcularCombinaciones(listaLibros.size(), numLibros);
        System.out.println(String.format("  • Total de combinaciones a explorar: %,d", totalPosibles));
        System.out.println("\nExplorando TODAS las combinaciones...\n");
        
        List<CombinacionPeligrosa> combinacionesPeligrosas = new ArrayList<>();
        int[] contadores = {0, 0}; // [combinacion_num, peligrosas_encontradas]
        
        List<Libro> combinacionActual = new ArrayList<>();
        explorarConDemo(listaLibros, numLibros, 0, combinacionActual, 
                       combinacionesPeligrosas, pesoMaximo, mostrarPrimeras, contadores);
        
        System.out.println("\n═══════════════════════════════════════════════════════");
        System.out.println("RESULTADO DE LA EXPLORACIÓN:");
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println(String.format("  • Combinaciones exploradas: %,d", contadores[0]));
        System.out.println(String.format("  • Combinaciones PELIGROSAS encontradas: %d", contadores[1]));
        System.out.println(String.format("  • Porcentaje peligrosas: %.2f%%", 
                                       (contadores[1] * 100.0 / contadores[0])));
        
        return combinacionesPeligrosas;
    }
    
    /**
     * Sobrecarga con valores por defecto
     */
    public static List<CombinacionPeligrosa> demostrarExploracionFuerzaBruta(List<Libro> listaLibros) {
        return demostrarExploracionFuerzaBruta(listaLibros, 4, 8.0, 15);
    }
    
    /**
     * Explora combinaciones mostrando el progreso
     */
    private static void explorarConDemo(
            List<Libro> listaLibros,
            int numLibros,
            int inicio,
            List<Libro> combinacionActual,
            List<CombinacionPeligrosa> combinacionesPeligrosas,
            double pesoMaximo,
            int mostrarPrimeras,
            int[] contadores) {
        
        if (combinacionActual.size() == numLibros) {
            contadores[0]++;
            
            double pesoTotal = 0;
            for (Libro libro : combinacionActual) {
                pesoTotal += libro.getPeso();
            }
            
            // Mostrar solo las primeras N combinaciones
            if (contadores[0] <= mostrarPrimeras) {
                List<String> isbns = new ArrayList<>();
                for (Libro libro : combinacionActual) {
                    String isbn = libro.getIsbn();
                    isbns.add(isbn.substring(Math.max(0, isbn.length() - 4)));
                }
                String estado = pesoTotal > pesoMaximo ? "PELIGROSA" : "Segura";
                System.out.println(String.format("  [%3d] ISBNs: %s | Peso: %5.2f Kg | %s",
                                               contadores[0], isbns, pesoTotal, estado));
            } else if (contadores[0] == mostrarPrimeras + 1) {
                long restantes = calcularCombinaciones(listaLibros.size(), numLibros) - mostrarPrimeras;
                System.out.println(String.format("  ... (explorando %,d combinaciones más) ...", restantes));
            }
            
            if (pesoTotal > pesoMaximo) {
                double exceso = pesoTotal - pesoMaximo;
                combinacionesPeligrosas.add(
                    new CombinacionPeligrosa(combinacionActual, pesoTotal, exceso)
                );
                contadores[1]++;
            }
            return;
        }
        
        for (int i = inicio; i < listaLibros.size(); i++) {
            combinacionActual.add(listaLibros.get(i));
            explorarConDemo(listaLibros, numLibros, i + 1, combinacionActual,
                          combinacionesPeligrosas, pesoMaximo, mostrarPrimeras, contadores);
            combinacionActual.remove(combinacionActual.size() - 1);
        }
    }
    
    /**
     * Calcula el número de combinaciones (n choose k)
     */
    private static long calcularCombinaciones(int n, int k) {
        if (k > n) return 0;
        if (k == 0 || k == n) return 1;
        
        long resultado = 1;
        for (int i = 0; i < k; i++) {
            resultado = resultado * (n - i) / (i + 1);
        }
        return resultado;
    }
}