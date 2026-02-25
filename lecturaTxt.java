// Libs that we'll use to read files

import java.io.*;
import java.util.ArrayList;        
import java.util.List; 

// Para leer los datos de un vehiculo antes de crear sus instancias (hilos)

public class DatosVehiculo {
    int id;
    char orientacion;
    int fila;
    int columna;
    int longitud;
    int bateriaI;

    public DatosVehiculo(int id, char orientacion, int fila, int columna, int longitud, int bateria) {
        this.id = id;
        this.orientacion = orientacion;
        this.fila = fila;
        this.columna = columna;
        this.longitud = longitud;
        this.bateriaI = bateria;
    }
}

// Lectura de la entrada del vehiculo por archivo (txt)

public class LectorConfiguracion {

    public static void cargarConfiguracion(String rutaArchivo) {
        File archivo = null;
        FileReader fr = null;
        BufferedReader br = null;

        List<DatosVehiculo> vehiculos = new ArrayList<>();
        int cantidadCargadores = 0;

        try {
            archivo = new File(rutaArchivo);
            fr = new FileReader(archivo);        // Esta clase tiene métodos que nos permiten leer caracteres
            br = new BufferedReader(fr);        // Nos permitan leer líneas completas

            String linea;
            while ((linea = br.readLine()) != null) {
                linea = linea.trim();
                if (linea.isEmpty()) continue;

                // Si la línea contiene "Cargadores", la siguiente o esa misma indica el número
                if (linea.contains("Cargadores")) {
                    // El número puede estar abajo
                    linea = br.readLine();

                    if (linea != null) {
                        cantidadCargadores = Integer.parseInt(linea.trim());
                    }
                    break; // Fin del archivo según formato
                }

                // Procesar vehículos: ID, Orientación, Fila, Columna, Longitud, Batería 
                String[] partes = linea.split(",");
                if (partes.length >= 6) {
                    int id = Integer.parseInt(partes[0].trim());
                    char orientacion = partes[1].trim().charAt(0);
                    int fila = Integer.parseInt(partes[2].trim());
                    int columna = Integer.parseInt(partes[3].trim());
                    int longitud = Integer.parseInt(partes[4].trim());
                    int bateria = Integer.parseInt(partes[5].trim());

                    vehiculos.add(new DatosVehiculo(id, orientacion, fila, columna, longitud, bateria));
                }
            }
            
            // Inicializamos el Monitor y los Threads
            System.out.println("Vehículos cargados: " + vehiculos.size());
            System.out.println("Cargadores disponibles: " + cantidadCargadores);

        } catch (Exception e) {
            System.err.println("Error leyendo el archivo: " + e.getMessage());
        } finally {
            try {
                if (null != fr) fr.close();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }
}

// Para probar que funciona
public static void main(String[] args) {
    if (args.length < 1) {
        System.out.println("Indica el nombre del archivo TXT como argumento.");
        return;
    }
    
    LectorConfiguracion.cargarConfiguracion(args[0]);
}