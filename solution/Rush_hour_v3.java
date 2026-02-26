package solution;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class Rush_hour_v3 {
    public static void main(String[] args) {
        if (args.length < 1) return;

        Estacionamiento monitor = new Estacionamiento();
        List<Vehiculo> vehiculos = new ArrayList<>();
        int cargadoresCount = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(new File(args[0])))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                linea = linea.trim();
                if (linea.isEmpty()) continue;
                if (linea.toLowerCase().contains("cargadores")) {
                    String[] p = linea.split(",");
                    cargadoresCount = Integer.parseInt(p[p.length - 1].trim());
                    break;
                }
                String[] p = linea.split(",");
                if (p.length >= 6) {
                    Vehiculo v = new Vehiculo(
                        Integer.parseInt(p[0].trim()), p[1].trim().charAt(0),
                        Integer.parseInt(p[2].trim()), Integer.parseInt(p[3].trim()),
                        Integer.parseInt(p[4].trim()), Integer.parseInt(p[5].trim()),
                        monitor
                    );
                    vehiculos.add(v);
                    monitor.inicializarPosicion(v);
                }
            }
        } catch (Exception e) { return; }

        for (Vehiculo v : vehiculos) v.start();
        for (int i = 0; i < cargadoresCount; i++) new Cargador(i, monitor).start();

        // --- WATCHDOG: Determinar si el juego es irresoluble ---
        final long MAX_WAIT_MS = 5000; // 5 segundos es una eternidad a esta velocidad
        long startTime = System.currentTimeMillis();
        
        try {
            while (true) {
                Thread.sleep(500);
                long elapsed = System.currentTimeMillis() - startTime;
                
                if (elapsed > MAX_WAIT_MS) {
                    System.out.println(">>> RESULTADO: Irresoluble o estancado (Tiempo límite excedido).");
                    System.exit(1);
                }
                // Si el vehículo 0 escapó, el programa ya habrá llamado a System.exit(0)
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}