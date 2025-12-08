package controllers.resolucion;

import models.Libro;
import java.util.ArrayList;
import java.util.List;

/**
 * Algoritmo de backtracking para optimizar la estantería
 * Este algoritmo encuentra la combinación de libros que maximiza el valor
 * total sin exceder el peso máximo de 8 kg.
 */
public class Backtracking {
    /**
     * Clase para almacenar una solución de estantería
     */
    public static class SolucionEstanteria {
        private List<Libro> libros;
        private double pesoTotal;
        private double valorTotal;
        
        public SolucionEstanteria() {
            this.libros = new ArrayList<>();
            this.pesoTotal = 0.0;
            this.valorTotal = 0.0;
        }
        
        public SolucionEstanteria(List<Libro> libros, double pesoTotal, double valorTotal) {
            this.libros = new ArrayList<>(libros);
            this.pesoTotal = pesoTotal;
            this.valorTotal = valorTotal;
        }
        
        public void agregarLibro(Libro libro) {
            libros.add(libro);
            pesoTotal += libro.getPeso();
            valorTotal += libro.getValor();
        }
        
        public Libro quitarLibro() {
            if (!libros.isEmpty()) {
                Libro libro = libros.remove(libros.size() - 1);
                pesoTotal -= libro.getPeso();
                valorTotal -= libro.getValor();
                return libro;
            }
            return null;
        }
        
        public SolucionEstanteria copia() {
            return new SolucionEstanteria(this.libros, this.pesoTotal, this.valorTotal);
        }
        
        public List<Libro> getLibros() { return libros; }
        public double getPesoTotal() { return pesoTotal; }
        public double getValorTotal() { return valorTotal; }
        
        @Override
        public String toString() {
            return String.format("Solución: %d libros | Peso: %.2f Kg | Valor: $%,.0f",
                               libros.size(), pesoTotal, valorTotal);
        }
    }
    
    /**
     * Encuentra la combinación óptima de libros que maximiza el valor total
     * sin exceder el peso máximo usando backtracking.
     * 
     * Explora recursivamente dos opciones para cada libro: incluirlo o no incluirlo.
     * Mantiene la mejor solución encontrada y la retorna después de la exploración.
     * 
     * @param listaLibros Lista de objetos Libro disponibles
     * @param pesoMaximo Peso máximo en Kg (default: 8.0)
     * @param mostrarExploracion Si es true, imprime la exploración (default: false)
     * @param limiteOutput Número máximo de líneas de exploración a mostrar (default: 50)
     * @return La mejor solución encontrada
     */
    public static SolucionEstanteria optimizarEstanteria(
            List<Libro> listaLibros,
            double pesoMaximo,
            boolean mostrarExploracion,
            int limiteOutput) {
        
        SolucionEstanteria[] mejorSolucion = {new SolucionEstanteria()};
        SolucionEstanteria solucionActual = new SolucionEstanteria();
        int[] nodosExplorados = {0};
        int[] lineasImpresas = {0};
        
        System.out.println("\nBuscando combinación óptima con backtracking...");
        
        backtrack(listaLibros, 0, solucionActual, mejorSolucion, pesoMaximo,
                 mostrarExploracion, limiteOutput, nodosExplorados, lineasImpresas);
        
        System.out.println(String.format("Exploración completada: %,d nodos explorados", 
                                       nodosExplorados[0]));
        System.out.println(String.format("Solución óptima encontrada: %d libros", 
                                       mejorSolucion[0].getLibros().size()));
        
        return mejorSolucion[0];
    }
    
    /**
     * Sobrecarga con valores por defecto
     */
    public static SolucionEstanteria optimizarEstanteria(List<Libro> listaLibros) {
        return optimizarEstanteria(listaLibros, 8.0, false, 50);
    }
    
    /**
     * Función recursiva de backtracking
     * 
     * Explora dos ramas para cada libro:
     * 1. Incluir el libro si cabe dentro del peso máximo
     * 2. No incluir el libro (siempre se explora)
     */
    private static void backtrack(
            List<Libro> listaLibros,
            int indice,
            SolucionEstanteria solucionActual,
            SolucionEstanteria[] mejorSolucion,
            double pesoMaximo,
            boolean mostrarExploracion,
            int limiteOutput,
            int[] nodosExplorados,
            int[] lineasImpresas) {
        
        nodosExplorados[0]++;
        
        // Caso base: hemos considerado todos los libros
        if (indice == listaLibros.size()) {
            if (solucionActual.getValorTotal() > mejorSolucion[0].getValorTotal()) {
                mejorSolucion[0] = solucionActual.copia();
                if (mostrarExploracion && lineasImpresas[0] < limiteOutput) {
                    System.out.println(String.format("  → Mejor: %d libros, $%,.0f",
                                                   mejorSolucion[0].getLibros().size(),
                                                   mejorSolucion[0].getValorTotal()));
                    lineasImpresas[0]++;
                }
            }
            return;
        }
        
        Libro libroActual = listaLibros.get(indice);
        
        // Opción 1: Incluir el libro si cabe en el peso máximo
        if (solucionActual.getPesoTotal() + libroActual.getPeso() <= pesoMaximo) {
            solucionActual.agregarLibro(libroActual);
            backtrack(listaLibros, indice + 1, solucionActual, mejorSolucion, pesoMaximo,
                     mostrarExploracion, limiteOutput, nodosExplorados, lineasImpresas);
            solucionActual.quitarLibro();
        }
        
        // Opción 2: No incluir el libro actual (siempre se explora)
        backtrack(listaLibros, indice + 1, solucionActual, mejorSolucion, pesoMaximo,
                 mostrarExploracion, limiteOutput, nodosExplorados, lineasImpresas);
    }
    
    /**
     * Demuestra paso a paso el proceso de backtracking para optimización de estantería.
     * 
     * Ejecuta el algoritmo de backtracking que explora recursivamente todas las
     * combinaciones de libros para encontrar la que maximiza el valor total
     * sin exceder el peso máximo.
     * 
     * @param listaLibros Lista de objetos Libro
     * @param pesoMaximo Peso máximo en Kg (default: 8.0)
     * @return La mejor solución encontrada
     */
    public static SolucionEstanteria demostrarBacktracking(
            List<Libro> listaLibros,
            double pesoMaximo) {
        
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("Backtracking - Optimización de Estantería");
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("\nParámetros:");
        System.out.println(String.format("  • Total de libros disponibles: %d", listaLibros.size()));
        System.out.println(String.format("  • Peso máximo del estante: %.1f Kg", pesoMaximo));
        System.out.println("  • Objetivo: Maximizar valor total (COP)");
        System.out.println("  • Método: Backtracking (exploración recursiva)\n");
        
        SolucionEstanteria mejor = optimizarEstanteria(listaLibros, pesoMaximo, true, 50);
        
        System.out.println("\n═══════════════════════════════════════════════════════");
        System.out.println("SOLUCIÓN ÓPTIMA ENCONTRADA:");
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println(String.format("  • Número de libros: %d", mejor.getLibros().size()));
        System.out.println(String.format("  • Peso total: %.2f Kg / %.1f Kg", 
                                       mejor.getPesoTotal(), pesoMaximo));
        System.out.println(String.format("  • Valor total: $%,.0f COP", mejor.getValorTotal()));
        System.out.println(String.format("  • Espacio disponible: %.2f Kg", 
                                       pesoMaximo - mejor.getPesoTotal()));
        
        if (!mejor.getLibros().isEmpty()) {
            System.out.println("\nLibros seleccionados:");
            int i = 1;
            for (Libro libro : mejor.getLibros()) {
                System.out.println(String.format("  %d. %s", i, libro.getTitulo()));
                System.out.println(String.format("     ISBN: %s | Peso: %.2f Kg | Valor: $%,.0f",
                                               libro.getIsbn(), libro.getPeso(), libro.getValor()));
                i++;
            }
        }
        
        return mejor;
    }
    
    /**
     * Sobrecarga con valor por defecto
     */
    public static SolucionEstanteria demostrarBacktracking(List<Libro> listaLibros) {
        return demostrarBacktracking(listaLibros, 8.0);
    }
}
