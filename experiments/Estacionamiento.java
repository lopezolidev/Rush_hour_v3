package experiments ;
//java:src/Estacionamiento.java
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
        for(int i=0; i<6; i++) {
            for(int j=0; j<6; j++) {
                tablero[i][j] = -1; 
            }
        }
    }

    // Método auxiliar para colocar vehículos al inicio (sin sincronización estricta necesaria si es antes de start())
    public void inicializarPosicion(Vehiculo v) {
        // TODO: Marcar las celdas del tablero con el ID del vehículo según su largo y orientación
        // tablero[v.getFila()][v.getCol()] = v.getId();
    }

    // -------------------------------------------------
    // LÓGICA DE MOVIMIENTO (Vehículos - Consumidores de Espacio)
    // -------------------------------------------------

    public synchronized boolean intentarMover(Vehiculo v, int dFila, int dCol) {
        // 1. Calcular nuevas coordenadas según orientación y largo.
        // 2. Verificar límites del tablero (0-5).
        // 3. Verificar colisiones: ¿Están las celdas destino vacías (o ocupadas por el mismo v)?
        
        boolean movimientoValido = false; 
        // TODO: Implementar lógica de validación de matriz

        if (movimientoValido) {
            // Borrar posición anterior
            // Escribir nueva posición
            // Imprimir estado del tablero
            return true;
        }
        
        return false; // No se pudo mover, el hilo deberá intentar otra cosa o esperar
    }

    // -------------------------------------------------
    // LÓGICA DE ENERGÍA (Productor - Consumidor)
    // -------------------------------------------------

    /**
     * Llamado por el VEHÍCULO cuando batería == 0.
     * El vehículo se bloquea aquí hasta ser recargado.
     */
    public synchronized void pedirCarga(Vehiculo v) {
        System.out.println("Vehículo " + v.getIdVehiculo() + " pide carga.");
        colaDeCarga.add(v);
        notifyAll(); // Despertar a los cargadores que podrían estar durmiendo

        while (v.getBateria() == 0) {
            try {
                wait(); // El vehículo duerme esperando que recarguen su batería
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        System.out.println("Vehículo " + v.getIdVehiculo() + " recargado y listo.");
    }

    /**
     * Llamado por el CARGADOR.
     * Si no hay vehículos, el cargador duerme.
     */
    public synchronized void recargarVehiculo(int idCargador) {
        while (colaDeCarga.isEmpty()) {
            try {
                wait(); // Esperar a que alguien pida carga
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        // Sacar vehículo de la cola (FIFO)
        Vehiculo v = colaDeCarga.poll();
        if (v != null) {
            // Simular tiempo de carga
            v.recargarBateria(10); // Valor fijo según PDF
            System.out.println("Cargador " + idCargador + " recargó al vehículo " + v.getIdVehiculo());
            
            // IMPORTANTE: Notificar a todos (especialmente al vehículo que espera en pedirCarga)
            notifyAll(); 
        }
    }
    
    // Método para verificar si el ID 0 salió (condición de parada)
    public synchronized boolean esVictoria() {
        // TODO: Verificar si el vehículo 0 cruzó la columna 5
        return false;
    }
}
