package models;

import java.util.Stack;

public class Usuario {
    private String id;
    private String nombre;
    private String apellidos;
    private String direccion;
    private Stack<String> historialPrestamos;

    public Usuario(String id, String nombre, String apellidos, String direccion) {
        this.id = id;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.direccion = direccion;
        this.historialPrestamos = null;
    }

    // Getters
    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public String getApellidos() { return apellidos; }
    public String getDireccion() { return direccion; }
    public Stack<String> getHistorialPrestamos() { return historialPrestamos; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public void setHistorialPrestamos(Stack<String> historialPrestamos) { 
        this.historialPrestamos = historialPrestamos; 
    }

    @Override
    public String toString() {
        return String.format("Usuario: %s %s (ID: %s)", nombre, apellidos, id);
    }
}
