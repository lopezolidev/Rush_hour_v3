package testing ;

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
            // 0. Verificar condición de victoria
            if (this.id == 0 && estacionamiento.esVictoria()) {
                System.out.println("Vehículo Objetivo ha salido.");
                System.exit(0); 
            }

            // 1. Verificar Batería
            if (this.bateria <= 0) {
                estacionamiento.pedirCarga(this);
            }

            // 2. Intentar Moverse
            int direccion = random.nextBoolean() ? 1 : -1; 
            int dFila = (orientacion == 'v') ? direccion : 0;
            int dCol = (orientacion == 'h') ? direccion : 0;

            boolean seMovio = estacionamiento.intentarMover(this, dFila, dCol);

            if (seMovio) {
                this.bateria--;
                this.fila += dFila;
                this.col += dCol;
                
                // Imprimir el tablero después de un movimiento exitoso
                estacionamiento.imprimirTablero();
            } else {
                try { Thread.sleep(100); } catch (InterruptedException e) {}
            }
            
            try { Thread.sleep(200); } catch (InterruptedException e) {}
        }
    }

    // -----------------------------------------------------------------
    // Getters y Setters 
    // (Añadidos los que faltaban para que Estacionamiento pueda leerlos)
    // -----------------------------------------------------------------
    
    public int getIdVehiculo() { return id; }
    public int getBateria() { return bateria; }
    public void recargarBateria(int carga) { this.bateria = carga; }
    
    // Nuevos métodos necesarios para inicializarPosicion() e intentarMover()
    public int getFila() { return fila; }
    public int getCol() { return col; }
    public char getOrientacion() { return orientacion; }
    public int getLargo() { return largo; }
}