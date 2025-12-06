package models;

public class Prestamo {
    private String id;
    private String usuarioId;
    private String libroIsbn;
    private String fechaPrestamo;
    private String fechaDevolucionEsperada;
    private String fechaDevolucionReal;
    private String estado;

    public Prestamo(String id, String usuarioId, String libroIsbn, String fechaPrestamo, String fechaDevolucionEsperada,String fechaDevolucionReal, String estado) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.libroIsbn = libroIsbn;
        this.fechaPrestamo = fechaPrestamo;
        this.fechaDevolucionEsperada = fechaDevolucionEsperada;
        this.fechaDevolucionReal = fechaDevolucionReal;
        this.estado = estado;
    }

    public Prestamo(String id, String usuarioId, String libroIsbn, String fechaPrestamo, String fechaDevolucionEsperada) {
        this(id, usuarioId, libroIsbn, fechaPrestamo, fechaDevolucionEsperada, null, "prestado");
    }

    // Getters
    public String getId() { return id; }
    public String getUsuarioId() { return usuarioId; }
    public String getLibroIsbn() { return libroIsbn; }
    public String getFechaPrestamo() { return fechaPrestamo; }
    public String getFechaDevolucionEsperada() { return fechaDevolucionEsperada; }
    public String getFechaDevolucionReal() { return fechaDevolucionReal; }
    public String getEstado() { return estado; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setUsuarioId(String usuarioId) { this.usuarioId = usuarioId; }
    public void setLibroIsbn(String libroIsbn) { this.libroIsbn = libroIsbn; }
    public void setFechaPrestamo(String fechaPrestamo) { this.fechaPrestamo = fechaPrestamo; }
    public void setFechaDevolucionEsperada(String fechaDevolucionEsperada) { 
        this.fechaDevolucionEsperada = fechaDevolucionEsperada; 
    }
    public void setFechaDevolucionReal(String fechaDevolucionReal) { 
        this.fechaDevolucionReal = fechaDevolucionReal; 
    }
    public void setEstado(String estado) { this.estado = estado; }

    @Override
    public String toString() {
        return String.format("Préstamo %s: Libro %s → Usuario %s (Estado: %s)", id, libroIsbn, usuarioId, estado);
    }
}
