package solution;

public class Cargador extends Thread {
    private final int id;
    private final Estacionamiento estacionamiento;

    public Cargador(int id, Estacionamiento e) {
        this.id = id;
        this.estacionamiento = e;
        this.setDaemon(true); 
    }

    @Override
    public void run() {
        while (true) {
            estacionamiento.recargarVehiculo(this.id);
            // Sin sleep para recarga instantánea
            Thread.yield();
        }
    }
}