package solution;

import java.util.LinkedList;
import java.util.Queue;

public class Estacionamiento {
    private final int[][] tablero = new int[6][6];
    private final Queue<Vehiculo> colaDeCarga = new LinkedList<>();
    private long movimientosTotales = 0;

    public Estacionamiento() {
        for(int i = 0; i < 6; i++) {
            for(int j = 0; j < 6; j++) {
                tablero[i][j] = -1; 
            }
        }
    }

    public synchronized int[][] getSnapshot() {
        int[][] copia = new int[6][6];
        for (int i = 0; i < 6; i++) {
            System.arraycopy(tablero[i], 0, copia[i], 0, 6);
        }
        return copia;
    }

    public synchronized void inicializarPosicion(Vehiculo v) {
        int f = v.getFila(), c = v.getCol(), l = v.getLargo();
        char o = Character.toLowerCase(v.getOrientacion());
        int id = v.getIdVehiculo();
        for (int i = 0; i < l; i++) {
            int rf = f + (o == 'v' ? i : 0);
            int rc = c + (o == 'h' ? i : 0);
            if (rf >= 0 && rf < 6 && rc >= 0 && rc < 6) tablero[rf][rc] = id;
        }
    }

    public synchronized boolean intentarMover(Vehiculo v, int dFila, int dCol) {
        int id = v.getIdVehiculo(), f = v.getFila(), c = v.getCol(), l = v.getLargo();
        char o = Character.toLowerCase(v.getOrientacion());
        int nf = f + dFila, nc = c + dCol;

        // Victoria
        if (id == 0 && o == 'h' && (nc + l - 1) > 5) {
            limpiarVehiculo(f, c, l, o);
            return true; 
        }

        if (nf < 0 || nc < 0 || (o == 'h' && nc + l > 6) || (o == 'v' && nf + l > 6)) return false;

        int checkF = (dFila > 0) ? nf + l - 1 : nf;
        int checkC = (dCol > 0) ? nc + l - 1 : nc;

        if (tablero[checkF][checkC] != -1) return false;

        limpiarVehiculo(f, c, l, o);
        for (int i = 0; i < l; i++) {
            tablero[nf + (o == 'v' ? i : 0)][nc + (o == 'h' ? i : 0)] = id;
        }
        movimientosTotales++;
        return true;
    }

    private void limpiarVehiculo(int f, int c, int l, char o) {
        for (int i = 0; i < l; i++) {
            tablero[f + (o == 'v' ? i : 0)][c + (o == 'h' ? i : 0)] = -1;
        }
    }

    public synchronized void pedirCarga(Vehiculo v) {
        colaDeCarga.add(v);
        notifyAll(); 
        while (v.getBateria() == 0) {
            try { wait(); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }
    }

    public synchronized void recargarVehiculo(int idCargador) {
        while (colaDeCarga.isEmpty()) {
            try { wait(); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }
        Vehiculo v = colaDeCarga.poll();
        if (v != null) {
            v.recargarBateria(10);
            notifyAll(); 
        }
    }
    
    public synchronized boolean esVictoria() {
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 6; j++) {
                if (tablero[i][j] == 0) return false;
            }
        }
        return true; 
    }

    public synchronized long getMovimientosTotales() { return movimientosTotales; }
}