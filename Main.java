import views.BibliotecaGUI;
import javax.swing.SwingUtilities;

/**
 * Library Management System (LMS)
 * 
 * Main entry point of the system.
 * 
 * This system implements:
 * - Inventory management (general and ordered)
 * - Data structures (Stack and Queue)
 * - Sorting algorithms (Insertion Sort, Merge Sort)
 * - Search algorithms (Linear, Binary)
 * - Resolution algorithms (Brute Force, Backtracking)
 * - Recursion (Stack and Queue)
 * 
 * Usage:
 *     java Main [--cli|--gui]
 *     
 *     --cli: Start command line interface (default)
 *     --gui: Start graphical interface
 */
public class Main {
    
    /**
     * Main method of the system.
     * 
     * @param args Command line arguments: --cli or --gui
     */
    public static void main(String[] args) {
        System.out.println("Sistema de Gestión de Bibliotecas");
        System.out.println();
        
        // Determine execution mode
        String modo = "--gui"; // Default mode in Java version
        
        if (args.length > 0) {
            modo = args[0].toLowerCase();
        }
        
        try {
            if (modo.equals("--gui")) {
                // Start graphical interface
                System.out.println("Iniciando interfaz gráfica...");
                iniciarInterfazGrafica();
            } else if (modo.equals("--cli")) {
                // Start command line interface
                System.out.println("Iniciando interfaz de línea de comandos...");
                System.out.println("Nota: La interfaz CLI está en desarrollo.");
                System.out.println("Se abrirá la interfaz gráfica como alternativa...");
                iniciarInterfazGrafica();
            } else {
                System.out.println("Argumento inválido: " + modo);
                System.out.println("Uso: java Main [--cli|--gui]");
                System.exit(1);
            }
        } catch (Exception e) {
            System.err.println("\nError fatal: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    /**
     * Initializes and starts the graphical interface.
     */
    private static void iniciarInterfazGrafica() {
        SwingUtilities.invokeLater(() -> {
            try {
                BibliotecaGUI frame = new BibliotecaGUI();
                frame.setVisible(true);
            } catch (Exception e) {
                System.err.println("Error al cargar la interfaz gráfica: " + e.getMessage());
                e.printStackTrace();
                System.exit(1);
            }
        });
    }
}
