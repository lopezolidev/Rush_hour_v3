package testing ;
// java:src/Cargador.java
public class Cargador extends Thread {
    private int id;
    private Estacionamiento estacionamiento;

    public Cargador(int id, Estacionamiento e) {
        this.id = id;
        this.estacionamiento = e;
        // Hacerlo Daemon asegura que si el main y vehículos terminan, estos hilos no impidan cerrar la JVM
        this.setDaemon(true); 
    }

    @Override
    public void run() {
        while (true) {
            // El cargador es pasivo: va al monitor y espera trabajo
            estacionamiento.recargarVehiculo(this.id);
            
            // Pequeña pausa para simular el proceso físico
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
