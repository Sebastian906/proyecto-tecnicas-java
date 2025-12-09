package controllers.adquisicion;

import models.Libro;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase responsable de cargar datos de libros desde archivos .csv o .json
 */
public class LectorArchivo {
    
    /**
     * Carga libros desde un archivo CSV
     * El archivo CSV debe tener los mismos atributos que la clase Libro
     * 
     * @param rutaArchivo Ruta del archivo CSV a cargar
     * @return Lista de objetos Libro cargados desde el CSV
     * @throws FileNotFoundException Si el archivo no existe
     * @throws IOException Si hay error de lectura
     * @throws IllegalArgumentException Si el formato del archivo no coincide
     */
    public static List<Libro> cargarCSV(String rutaArchivo) 
            throws FileNotFoundException, IOException, IllegalArgumentException {
        
        File archivo = new File(rutaArchivo);
        if (!archivo.exists()) {
            throw new FileNotFoundException("El archivo " + rutaArchivo + " no existe");
        }
        
        List<Libro> libros = new ArrayList<>();
        
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(archivo), StandardCharsets.UTF_8))) {
            
            // Leer encabezado
            String lineaEncabezado = br.readLine();
            if (lineaEncabezado == null) {
                throw new IllegalArgumentException("El archivo CSV está vacío");
            }
            
            String[] encabezados = lineaEncabezado.split(",");
            int lineaNum = 2; // Empezamos en 2 porque la línea 1 es el encabezado
            
            String linea;
            while ((linea = br.readLine()) != null) {
                try {
                    String[] valores = parsearLineaCSV(linea);
                    
                    if (valores.length < 6) {
                        throw new IllegalArgumentException(
                            "Columnas insuficientes en línea " + lineaNum
                        );
                    }
                    
                    // Crear mapa de columnas
                    String isbn = valores[getIndiceColumna(encabezados, "isbn")].trim();
                    String titulo = valores[getIndiceColumna(encabezados, "titulo")].trim();
                    String autor = valores[getIndiceColumna(encabezados, "autor")].trim();
                    double peso = Double.parseDouble(valores[getIndiceColumna(encabezados, "peso")].trim());
                    double valor = Double.parseDouble(valores[getIndiceColumna(encabezados, "valor")].trim());
                    String genero = valores[getIndiceColumna(encabezados, "genero")].trim();
                    
                    // Campos opcionales
                    int cantidadDisponible = 1;
                    int cantidadTotal = 1;
                    String estanteId = null;
                    
                    try {
                        int idxCantDisp = getIndiceColumna(encabezados, "cantidad_disponible");
                        if (idxCantDisp >= 0 && idxCantDisp < valores.length && !valores[idxCantDisp].trim().isEmpty()) {
                            cantidadDisponible = Integer.parseInt(valores[idxCantDisp].trim());
                        }
                    } catch (Exception e) {
                        // Usar valor por defecto
                    }
                    
                    try {
                        int idxCantTotal = getIndiceColumna(encabezados, "cantidad_total");
                        if (idxCantTotal >= 0 && idxCantTotal < valores.length && !valores[idxCantTotal].trim().isEmpty()) {
                            cantidadTotal = Integer.parseInt(valores[idxCantTotal].trim());
                        }
                    } catch (Exception e) {
                        // Usar valor por defecto
                    }
                    
                    try {
                        int idxEstante = getIndiceColumna(encabezados, "estante_id");
                        if (idxEstante >= 0 && idxEstante < valores.length && !valores[idxEstante].trim().isEmpty()) {
                            estanteId = valores[idxEstante].trim();
                        }
                    } catch (Exception e) {
                        // Usar valor por defecto
                    }
                    
                    Libro libro = new Libro(isbn, titulo, autor, peso, valor, genero, 
                                          cantidadDisponible, cantidadTotal, estanteId);
                    libros.add(libro);
                    
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException(
                        "Error en línea " + lineaNum + ": Formato numérico inválido - " + e.getMessage()
                    );
                } catch (Exception e) {
                    throw new IllegalArgumentException(
                        "Error en línea " + lineaNum + ": " + e.getMessage()
                    );
                }
                lineaNum++;
            }
        }
        
        System.out.println("Se cargaron " + libros.size() + " libros desde " + rutaArchivo);
        return libros;
    }
    
    /**
     * Carga libros desde un archivo JSON
     * 
     * @param rutaArchivo Ruta del archivo JSON a cargar
     * @return Lista de objetos Libro cargados desde el JSON
     * @throws FileNotFoundException Si el archivo no existe
     * @throws IOException Si hay error de lectura
     * @throws IllegalArgumentException Si el formato del archivo no coincide
     */
    public static List<Libro> cargarJSON(String rutaArchivo) 
            throws FileNotFoundException, IOException, IllegalArgumentException {
        
        File archivo = new File(rutaArchivo);
        if (!archivo.exists()) {
            throw new FileNotFoundException("El archivo " + rutaArchivo + " no existe");
        }
        
        List<Libro> libros = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        
        try {
            JsonNode rootNode = mapper.readTree(archivo);
            
            // Verificar si es un arreglo o un objeto con propiedad "libros"
            JsonNode librosNode = rootNode;
            if (rootNode.isObject() && rootNode.has("libros")) {
                librosNode = rootNode.get("libros");
            }
            
            if (!librosNode.isArray()) {
                throw new IllegalArgumentException(
                    "El archivo JSON debe contener un arreglo de libros o un objeto con propiedad 'libros'"
                );
            }
            
            for (int idx = 0; idx < librosNode.size(); idx++) {
                try {
                    JsonNode item = librosNode.get(idx);
                    
                    // Campos obligatorios
                    String isbn = getJsonField(item, "isbn", "").trim();
                    String titulo = getJsonField(item, "titulo", "").trim();
                    String autor = getJsonField(item, "autor", "").trim();
                    String pesoStr = getJsonField(item, "peso", "0").trim();
                    String valorStr = getJsonField(item, "valor", "0").trim();
                    String genero = getJsonField(item, "genero", "").trim();
                    
                    // Validaciones
                    if (isbn.isEmpty() || titulo.isEmpty() || autor.isEmpty()) {
                        System.err.println("Advertencia: Elemento " + idx + " incompleto, saltando...");
                        continue;
                    }
                    
                    double peso = Double.parseDouble(pesoStr);
                    double valor = Double.parseDouble(valorStr);
                    
                    // Campos opcionales
                    int cantidadDisponible = item.has("cantidad_disponible") && !item.get("cantidad_disponible").isNull() ? 
                        item.get("cantidad_disponible").asInt() : 1;
                    int cantidadTotal = item.has("cantidad_total") && !item.get("cantidad_total").isNull() ? 
                        item.get("cantidad_total").asInt() : 1;
                    String estanteId = item.has("estante_id") && !item.get("estante_id").isNull() ? 
                        item.get("estante_id").asText() : null;
                    
                    Libro libro = new Libro(isbn, titulo, autor, peso, valor, genero,
                                          cantidadDisponible, cantidadTotal, estanteId);
                    libros.add(libro);
                    
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException(
                        "Error en formato numérico en elemento " + idx + ": " + e.getMessage()
                    );
                } catch (Exception e) {
                    throw new IllegalArgumentException(
                        "Error en el elemento " + idx + ": " + e.getMessage()
                    );
                }
            }
            
        } catch (IOException e) {
            throw new IOException("Error al leer el archivo JSON: " + e.getMessage());
        }
        
        System.out.println("Se cargaron " + libros.size() + " libros desde " + rutaArchivo);
        return libros;
    }
    
    /**
     * Método auxiliar para obtener campos del JSON de forma segura
     */
    private static String getJsonField(JsonNode node, String fieldName, String defaultValue) {
        if (node.has(fieldName) && !node.get(fieldName).isNull()) {
            return node.get(fieldName).asText();
        }
        return defaultValue;
    }
    
    /**
     * Carga libros detectando automáticamente el formato del archivo
     * 
     * @param rutaArchivo Ruta del archivo a cargar
     * @return Lista de objetos Libro cargados
     * @throws IllegalArgumentException Si el formato no es soportado
     */
    public static List<Libro> cargarLibros(String rutaArchivo) 
            throws FileNotFoundException, IOException, IllegalArgumentException {
        
        String extension = obtenerExtension(rutaArchivo).toLowerCase();
        
        switch (extension) {
            case ".csv":
                return cargarCSV(rutaArchivo);
            case ".json":
                return cargarJSON(rutaArchivo);
            default:
                throw new IllegalArgumentException(
                    "Formato de archivo no soportado: " + extension + 
                    ". Solo se soportan .csv y .json"
                );
        }
    }
    
    /**
     * Guarda una lista de libros en formato CSV
     * 
     * @param libros Lista de libros a guardar
     * @param rutaArchivo Ruta donde guardar el archivo CSV
     * @throws IOException Si hay error de escritura
     */
    public static void guardarCSV(List<Libro> libros, String rutaArchivo) 
            throws IOException {
        
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(rutaArchivo), StandardCharsets.UTF_8))) {
            
            // Escribir encabezado
            writer.write("isbn,titulo,autor,peso,valor,genero,cantidad_disponible,cantidad_total,estante_id");
            writer.newLine();
            
            // Escribir libros
            for (Libro libro : libros) {
                writer.write(String.format("%s,%s,%s,%.2f,%.2f,%s,%d,%d,%s",
                    escaparCSV(libro.getIsbn()),
                    escaparCSV(libro.getTitulo()),
                    escaparCSV(libro.getAutor()),
                    libro.getPeso(),
                    libro.getValor(),
                    escaparCSV(libro.getGenero()),
                    libro.getCantidadDisponible(),
                    libro.getCantidadTotal(),
                    libro.getEstanteId() != null ? libro.getEstanteId() : ""
                ));
                writer.newLine();
            }
        }
        
        System.out.println("Se guardaron " + libros.size() + " libros en " + rutaArchivo);
    }
    
    /**
     * Guarda una lista de libros en formato JSON
     * 
     * @param libros Lista de libros a guardar
     * @param rutaArchivo Ruta donde guardar el archivo JSON
     * @throws IOException Si hay error de escritura
     */
    public static void guardarJSON(List<Libro> libros, String rutaArchivo) 
            throws IOException {
        
        ObjectMapper mapper = new ObjectMapper();
        List<java.util.Map<String, Object>> datos = new ArrayList<>();
        
        for (Libro libro : libros) {
            java.util.Map<String, Object> libroMap = new java.util.LinkedHashMap<>();
            libroMap.put("isbn", libro.getIsbn());
            libroMap.put("titulo", libro.getTitulo());
            libroMap.put("autor", libro.getAutor());
            libroMap.put("peso", libro.getPeso());
            libroMap.put("valor", libro.getValor());
            libroMap.put("genero", libro.getGenero());
            libroMap.put("cantidad_disponible", libro.getCantidadDisponible());
            libroMap.put("cantidad_total", libro.getCantidadTotal());
            libroMap.put("estante_id", libro.getEstanteId());
            datos.add(libroMap);
        }
        
        mapper.writerWithDefaultPrettyPrinter().writeValue(new File(rutaArchivo), datos);
        System.out.println("Se guardaron " + libros.size() + " libros en " + rutaArchivo);
    }
    
    // Métodos auxiliares
    
    private static String obtenerExtension(String rutaArchivo) {
        int ultimoPunto = rutaArchivo.lastIndexOf('.');
        return ultimoPunto > 0 ? rutaArchivo.substring(ultimoPunto) : "";
    }
    
    private static int getIndiceColumna(String[] encabezados, String nombreColumna) {
        for (int i = 0; i < encabezados.length; i++) {
            if (encabezados[i].trim().equalsIgnoreCase(nombreColumna)) {
                return i;
            }
        }
        return -1;
    }
    
    private static String[] parsearLineaCSV(String linea) {
        List<String> resultado = new ArrayList<>();
        StringBuilder campo = new StringBuilder();
        boolean entreComillas = false;
        
        for (int i = 0; i < linea.length(); i++) {
            char c = linea.charAt(i);
            
            if (c == '"') {
                entreComillas = !entreComillas;
            } else if (c == ',' && !entreComillas) {
                resultado.add(campo.toString());
                campo = new StringBuilder();
            } else {
                campo.append(c);
            }
        }
        resultado.add(campo.toString());
        
        return resultado.toArray(new String[0]);
    }
    
    private static String escaparCSV(String valor) {
        if (valor == null) return "";
        if (valor.contains(",") || valor.contains("\"") || valor.contains("\n")) {
            return "\"" + valor.replace("\"", "\"\"") + "\"";
        }
        return valor;
    }
}