package models;

public class Reserva {
    private String id;
    private String usuarioId;
    private String libroIsbn;
    private String fechaReserva;
    private String estado;

    public Reserva(String id, String usuarioId, String libroIsbn, String fechaReserva, String estado) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.libroIsbn = libroIsbn;
        this.fechaReserva = fechaReserva;
        this.estado = estado;
    }

    public Reserva(String id, String usuarioId, String libroIsbn, String fechaReserva) {
        this(id, usuarioId, libroIsbn, fechaReserva, "pendiente");
    }

    // Getters
    public String getId() { return id; }
    public String getUsuarioId() { return usuarioId; }
    public String getLibroIsbn() { return libroIsbn; }
    public String getFechaReserva() { return fechaReserva; }
    public String getEstado() { return estado; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setUsuarioId(String usuarioId) { this.usuarioId = usuarioId; }
    public void setLibroIsbn(String libroIsbn) { this.libroIsbn = libroIsbn; }
    public void setFechaReserva(String fechaReserva) { this.fechaReserva = fechaReserva; }
    public void setEstado(String estado) { this.estado = estado; }

    @Override
    public String toString() {
        return String.format("Reserva %s: Libro %s → Usuario %s (Estado: %s)", id, libroIsbn, usuarioId, estado);
    }
}
