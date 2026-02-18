package experiments ;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class Rush_hour_v3 {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Uso: java Main <archivo_entrada.txt>");
            // Por defecto para pruebas si no hay argumentos
            // return; 
        }

        // 1. Inicializar el Monitor (Recurso Compartido)
        Estacionamiento estacionamiento = new Estacionamiento();
        List<Vehiculo> vehiculos = new ArrayList<>();
        List<Cargador> cargadores = new ArrayList<>();

        // 2. Leer archivo (Pseudocódigo de lectura)
        /*
         * Estructura esperada del archivo:
         * ID, Orientacion(h/v), Fila, Col, Largo, Bateria
         * ...
         * NumCargadores
         */
        
        // TODO: Implementar lectura real del archivo TXT
        // Ejemplo manual para probar el esqueleto:
        // Vehiculo objetivo (ID 0)
        Vehiculo v0 = new Vehiculo(0, 'h', 2, 0, 2, 10, estacionamiento);
        vehiculos.add(v0);
        // Agregar al tablero inicial
        estacionamiento.inicializarPosicion(v0);

        int numCargadores = 2; // Leído del archivo

        // 3. Crear e iniciar Hilos
        for (Vehiculo v : vehiculos) {
            v.start();
        }

        for (int i = 0; i < numCargadores; i++) {
            Cargador c = new Cargador(i, estacionamiento);
            cargadores.add(c);
            c.start();
        }
        
        System.out.println("Simulación Iniciada...");
    }
}
