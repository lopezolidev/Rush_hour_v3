package testing ;
import java.util.LinkedList;
import java.util.Queue;

public class Estacionamiento {
    // Matriz 6x6. 
    // -1 = Vacío (Usamos -1 porque el ID 0 es un vehículo válido)
    private int[][] tablero = new int[6][6];
    
    // Cola de vehículos esperando carga (Recurso para Productor/Consumidor)
    private Queue<Vehiculo> colaDeCarga = new LinkedList<>();

    public Estacionamiento() {
        // Inicializar tablero vacío
        for(int i = 0; i < 6; i++) {
            for(int j = 0; j < 6; j++) {
                tablero[i][j] = -1; 
            }
        }
    }

    // -------------------------------------------------
    // INICIALIZACIÓN DE TABLERO
    // -------------------------------------------------

    /**
     * Coloca el vehículo en la matriz inicial según sus coordenadas, longitud y orientación.
     */
    public void inicializarPosicion(Vehiculo v) {
        int filaInicio = v.getFila();
        int colInicio = v.getCol();
        int largo = v.getLargo();
        char orientacion = Character.toLowerCase(v.getOrientacion());
        int id = v.getIdVehiculo();

        // Iterar sobre el largo del vehículo para ocupar todas sus casillas correspondientes
        for (int i = 0; i < largo; i++) {
            int filaActual = filaInicio;
            int colActual = colInicio;

            if (orientacion == 'h') {
                colActual += i; // Aumenta la columna hacia la derecha
            } else if (orientacion == 'v') {
                filaActual += i; // Aumenta la fila hacia abajo
            } else {
                System.err.println("Orientación desconocida para el vehículo " + id);
                return;
            }

            // Validar que no se salga del tablero 6x6
            if (filaActual >= 6 || colActual >= 6 || filaActual < 0 || colActual < 0) {
                System.err.println("Error: El vehículo " + id + " excede los límites del tablero.");
                continue;
            }

            // Validar que la casilla esté vacía antes de colocarlo (previene choques en el TXT inicial)
            if (tablero[filaActual][colActual] != -1) {
                System.err.println("Error: Colisión detectada al inicializar vehículo " + id + " en la celda [" + filaActual + "][" + colActual + "]");
            } else {
                // Posicionar el vehículo en la matriz
                tablero[filaActual][colActual] = id;
            }
        }
    }

    // -------------------------------------------------
    // LÓGICA DE MOVIMIENTO (Vehículos - Consumidores de Espacio)
    // -------------------------------------------------

    public synchronized boolean intentarMover(Vehiculo v, int dFila, int dCol) {
        int id = v.getIdVehiculo();
        int filaActual = v.getFila();
        int colActual = v.getCol();
        int largo = v.getLargo();
        char orientacion = Character.toLowerCase(v.getOrientacion());

        // 1. Calcular nueva posición tentativa
        int nuevaFila = filaActual + dFila;
        int nuevaCol = colActual + dCol;

        // 2. Validar límites del tablero
        if (nuevaFila < 0 || nuevaCol < 0) return false;
        
        // Si es el vehículo 0 y sale por la derecha, permitimos el movimiento (lógica de victoria)
        if (id == 0 && orientacion == 'h' && (nuevaCol + largo - 1) > 5) {
            // El vehículo se retira del tablero para ganar
            limpiarVehiculo(filaActual, colActual, largo, orientacion);
            return true; 
        }

        // Validar límites normales (no salir del 6x6)
        if (orientacion == 'h' && (nuevaCol + largo > 6)) return false;
        if (orientacion == 'v' && (nuevaFila + largo > 6)) return false;

        // 3. Validar colisión
        // Solo necesitamos revisar la celda "nueva" que se va a ocupar
        int filaRevision = (dFila > 0) ? nuevaFila + largo - 1 : nuevaFila;
        int colRevision = (dCol > 0) ? nuevaCol + largo - 1 : nuevaCol;

        if (tablero[filaRevision][colRevision] != -1) {
            return false; // Casilla ocupada, el hilo deberá esperar [cite: 46]
        }

        // 4. Actualizar Tablero (Movimiento exitoso)
        // Borramos la posición vieja y pintamos la nueva
        limpiarVehiculo(filaActual, colActual, largo, orientacion);
        
        for (int i = 0; i < largo; i++) {
            int r = (orientacion == 'h') ? nuevaFila : nuevaFila + i;
            int c = (orientacion == 'h') ? nuevaCol + i : nuevaCol;
            tablero[r][c] = id;
        }

        return true;
    }

/**
 * Método auxiliar para limpiar las casillas actuales antes de mover
 */
private void limpiarVehiculo(int f, int c, int l, char o) {
    for (int i = 0; i < l; i++) {
        if (o == 'h') tablero[f][c + i] = -1;
        else tablero[f + i][c] = -1;
    }
}

    // -------------------------------------------------
    // LÓGICA DE ENERGÍA (Productor - Consumidor)
    // -------------------------------------------------

    public synchronized void pedirCarga(Vehiculo v) {
        System.out.println("Vehículo " + v.getIdVehiculo() + " pide carga.");
        colaDeCarga.add(v);
        notifyAll(); 

        while (v.getBateria() == 0) {
            try {
                wait(); 
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        System.out.println("Vehículo " + v.getIdVehiculo() + " recargado y listo.");
    }

    public synchronized void recargarVehiculo(int idCargador) {
        while (colaDeCarga.isEmpty()) {
            try {
                wait(); 
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        Vehiculo v = colaDeCarga.poll();
        if (v != null) {
            v.recargarBateria(10); 
            System.out.println("Cargador " + idCargador + " recargó al vehículo " + v.getIdVehiculo());
            notifyAll(); 
        }
    }
    
    public synchronized boolean esVictoria() {
        // El vehículo 0 gana si su celda frontal (columna + largo - 1) supera el índice 5
        // Pero en tu lógica de intentarMover, ya lo sacas del tablero.
        // Una forma simple es ver si el ID 0 ya no existe en la matriz:
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 6; j++) {
                if (tablero[i][j] == 0) return false;
            }
        }
        return true; 
    }
    /**
     * Utilidad recomendada para ir viendo el estado del juego en la consola
     */
    public synchronized void imprimirTablero() {
        System.out.println("-----------------");
        for(int i = 0; i < 6; i++) {
            for(int j = 0; j < 6; j++) {
                if(tablero[i][j] == -1) {
                    System.out.print(". ");
                } else {
                    System.out.print(tablero[i][j] + " ");
                }
            }
            System.out.println();
        }
        System.out.println("-----------------");
    }
}
