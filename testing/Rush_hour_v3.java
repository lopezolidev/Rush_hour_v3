package testing ;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class Rush_hour_v3 {
    public static void main(String[] args) {
        Estacionamiento estacionamiento = new Estacionamiento();
        List<Vehiculo> vehiculos = new ArrayList<>();

        // 1. Vehículo Objetivo (ID 0): Horizontal, fila 2, col 0, largo 2, 5 de información.
        // Necesita moverse a la derecha para ganar.
        Vehiculo v0 = new Vehiculo(0, 'h', 2, 0, 2, 5, estacionamiento);
        
        // 2. Vehículo Obstáculo (ID 1): Vertical, fila 1, col 2, largo 3, 10 de información.
        // Bloquea el paso del ID 0 en la columna 2.
        Vehiculo v1 = new Vehiculo(1, 'v', 1, 2, 3, 10, estacionamiento);

        // 3. Inicializar en el monitor
        vehiculos.add(v0);
        vehiculos.add(v1);
        estacionamiento.inicializarPosicion(v0);
        estacionamiento.inicializarPosicion(v1);

        // 4. Configurar Cargadores (2 según el requerimiento) [cite: 31]
        int numCargadores = 2; 

        // 5. Lanzar Hilos
        for (Vehiculo v : vehiculos) {
            v.start();
        }

        for (int i = 0; i < numCargadores; i++) {
            Cargador c = new Cargador(i, estacionamiento);
            c.start(); // Estos son hilos Daemon
        }
        
        System.out.println("--- Simulación Hardcoded Iniciada ---");
    }
}
