package models;

import java.util.ArrayList;
import java.util.List;

public class Estante {
    public static final double PESO_MAXIMO = 8.0;

    private String id;
    private int cantidad;
    private double pesoMaximo;
    private double pesoActual;
    private List<Libro> librosAsignados;

    public Estante(String id, int cantidad, Double pesoMaximo) {
        this.id = id;
        this.cantidad = cantidad;
        this.pesoMaximo = pesoMaximo != null ? pesoMaximo : PESO_MAXIMO;
        this.pesoActual = 0.0;
        this.librosAsignados = new ArrayList<>();
    }

    public Estante(String id, int cantidad) {
        this(id, cantidad, null);
    }

    // Getters
    public String getId() { return id; }
    public int getCantidad() { return cantidad; }
    public double getPesoMaximo() { return pesoMaximo; }
    public double getPesoActual() { return pesoActual; }
    public List<Libro> getLibrosAsignados() { return librosAsignados; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
    public void setPesoMaximo(double pesoMaximo) { this.pesoMaximo = pesoMaximo; }
    public void setPesoActual(double pesoActual) { this.pesoActual = pesoActual; }
    public void setLibrosAsignados(List<Libro> librosAsignados) { 
        this.librosAsignados = librosAsignados; 
    }

    @Override
    public String toString() {
        return String.format("Estante %s: %.1f/%.1f kg (%d/%d libros", id, pesoActual, pesoMaximo, librosAsignados.size(), cantidad);
    }
}
