package solution;

import java.util.Random;

public class Vehiculo extends Thread {
    private int id, fila, col, largo, bateria;
    private char orientacion;
    private final Estacionamiento estacionamiento;
    private final Random random = new Random();

    public Vehiculo(int id, char orientacion, int fila, int col, int largo, int bateria, Estacionamiento e) {
        this.id = id; this.orientacion = orientacion; this.fila = fila;
        this.col = col; this.largo = largo; this.bateria = bateria;
        this.estacionamiento = e;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            if (this.id == 0 && estacionamiento.esVictoria()) {
                System.out.println(">>> RESULTADO: Victoria. Vehículo 0 escapó.");
                System.exit(0); 
            }

            if (this.bateria <= 0) {
                estacionamiento.pedirCarga(this);
            }

            // Backtracking local de profundidad 1 (Decisión inmediata basada en futuro)
            int direccion = decidirMejorDireccion();

            if (direccion != 0) {
                int dF = (orientacion == 'v') ? direccion : 0;
                int dC = (orientacion == 'h') ? direccion : 0;

                if (estacionamiento.intentarMover(this, dF, dC)) {
                    this.bateria--;
                    this.fila += dF;
                    this.col += dC;
                }
            }
            // Yield para máxima velocidad sin acaparar el kernel
            Thread.yield();
        }
    }

    private int decidirMejorDireccion() {
        int scorePos = evaluar(1);
        int scoreNeg = evaluar(-1);

        if (scorePos > scoreNeg && scorePos > -50) return 1;
        if (scoreNeg > scorePos && scoreNeg > -50) return -1;
        
        // Si hay empate o bloqueo, 15% de azar para romper ciclos
        if (random.nextFloat() < 0.15) return (random.nextBoolean() ? 1 : -1);
        return 0;
    }

    private int evaluar(int dir) {
        int[][] snap = estacionamiento.getSnapshot();
        int nf = fila + (orientacion == 'v' ? dir : 0);
        int nc = col + (orientacion == 'h' ? dir : 0);

        // Límites
        if (nf < 0 || nc < 0 || (orientacion == 'h' && nc + largo > 6) || (orientacion == 'v' && nf + largo > 6)) {
            if (id == 0 && orientacion == 'h' && dir == 1 && nc + largo > 5) return 2000;
            return -100;
        }

        // Colisión
        for (int i = 0; i < largo; i++) {
            int rf = nf + (orientacion == 'v' ? i : 0);
            int rc = nc + (orientacion == 'h' ? i : 0);
            if (snap[rf][rc] != -1 && snap[rf][rc] != id) return -80;
        }

        if (id == 0) return (dir == 1) ? 100 : -20;
        
        // Otros: Priorizar salir de la fila 2
        boolean bloqueaFilaSalida = false;
        for (int i = 0; i < largo; i++) {
            if (nf + (orientacion == 'v' ? i : 0) == 2) bloqueaFilaSalida = true;
        }
        return bloqueaFilaSalida ? -60 : 40;
    }

    public int getIdVehiculo() { return id; }
    public int getBateria() { return bateria; }
    public void recargarBateria(int carga) { this.bateria = carga; }
    public int getFila() { return fila; }
    public int getCol() { return col; }
    public char getOrientacion() { return orientacion; }
    public int getLargo() { return largo; }
}