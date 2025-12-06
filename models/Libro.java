package models;

public class Libro {
    private String isbn;
    private String titulo;
    private String autor;
    private double peso;
    private double valor;
    private String genero;
    private int cantidadDisponible;
    private int cantidadTotal;
    private String estanteId;

    public Libro(String isbn, String titulo, String autor, double peso, double valor, String genero, int cantidadDisponible, int cantidadTotal, String estanteId) {
        this.isbn = isbn;
        this.titulo = titulo;
        this.autor = autor;
        this.peso = peso;
        this.valor = valor;
        this.genero = genero;
        this.cantidadDisponible = cantidadDisponible;
        this.cantidadTotal = cantidadTotal;
        this.estanteId = estanteId;
    }

    public Libro(String isbn, String titulo, String autor, double peso, double valor, String genero) {
        this(isbn, titulo, autor, peso, valor, genero, 1, 1, null);
    }

    public boolean estaDisponible() {
        return this.cantidadDisponible > 0;
    }

    // Getters
    public String getIsbn() { return isbn; }
    public String getTitulo() { return titulo; }
    public String getAutor() { return autor; }
    public double getPeso() { return peso; }
    public double getValor() { return valor; }
    public String getGenero() { return genero; }
    public int getCantidadDisponible() { return cantidadDisponible; }
    public int getCantidadTotal() { return cantidadTotal; }
    public String getEstanteId() { return estanteId; }

    // Setters
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public void setAutor(String autor) { this.autor = autor; }
    public void setPeso(double peso) { this.peso = peso; }
    public void setValor(double valor) { this.valor = valor; }
    public void setGenero(String genero) { this.genero = genero; }
    public void setCantidadDisponible(int cantidadDisponible) { this.cantidadDisponible = cantidadDisponible; }
    public void setCantidadTotal(int cantidadTotal) { this.cantidadTotal = cantidadTotal; }
    public void setEstanteId(String estanteId) { this.estanteId = estanteId; }

    @Override
    public String toString() {
        return String.format("Libro: %s (ISBN: %s)", titulo, isbn);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Libro libro = (Libro) o;
        return isbn.equals(libro.isbn);
    }

    @Override
    public int hashCode() {
        return isbn.hashCode();
    }
}