package experiments ;
//java:src/Vehiculo.java
import java.util.Random;

public class Vehiculo extends Thread {
    private int id;
    private char orientacion; // 'h' o 'v'
    private int fila, col;
    private int largo;
    private int bateria;
    private Estacionamiento estacionamiento;
    private Random random = new Random();

    public Vehiculo(int id, char orientacion, int fila, int col, int largo, int bateria, Estacionamiento e) {
        this.id = id;
        this.orientacion = orientacion;
        this.fila = fila;
        this.col = col;
        this.largo = largo;
        this.bateria = bateria;
        this.estacionamiento = e;
    }

    @Override
    public void run() {
        while (true) {
            // 0. Verificar condición de victoria (Si soy el 0 y salí)
            if (this.id == 0 && estacionamiento.esVictoria()) {
                System.out.println("¡Vehículo Objetivo ha salido! Fin de simulación.");
                System.exit(0); // Terminar todo el programa
            }

            // 1. Verificar Batería
            if (this.bateria <= 0) {
                estacionamiento.pedirCarga(this);
                // Al volver de este método, la batería ya será > 0
            }

            // 2. Intentar Moverse
            // Heurística simple: Aleatorio adelante o atrás
            int direccion = random.nextBoolean() ? 1 : -1; 
            // Si es horizontal, cambio columna; si es vertical, cambio fila
            int dFila = (orientacion == 'v') ? direccion : 0;
            int dCol = (orientacion == 'h') ? direccion : 0;

            boolean seMovio = estacionamiento.intentarMover(this, dFila, dCol);

            if (seMovio) {
                this.bateria--;
                // Actualizar mis coordenadas internas si el monitor confirmó el movimiento
                this.fila += dFila;
                this.col += dCol;
            } else {
                // Si no se pudo mover, espera un poco para no saturar CPU (Spin-wait mitigado)
                try { Thread.sleep(100); } catch (InterruptedException e) {}
            }
            
            // Simular velocidad del movimiento
            try { Thread.sleep(200); } catch (InterruptedException e) {}
        }
    }

    // Getters y Setters necesarios para el Monitor
    public int getIdVehiculo() { return id; }
    public int getBateria() { return bateria; }
    public void recargarBateria(int carga) { this.bateria = carga; }
    // ... más getters de posición
}

