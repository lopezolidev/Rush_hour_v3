# Proyecto #3: Programación Concurrente - The Electric Rush Hour

## 1. Análisis Teórico del Problema

La implementación de esta simulación se fundamenta en la gestión coordinada de procesos concurrentes que compiten por recursos limitados. A continuación, se detallan los elementos clave identificados:

### Procesos e Hilos (Threads)

- **Hilos de Consumo (Vehículos):** Entidades autónomas que ejecutan un ciclo de toma de decisiones, movimiento y consumo de energía. Operan de forma asíncrona respecto a los demás vehículos.
    
- **Hilos de Producción (Cargadores):** Actúan como proveedores en un modelo Productor-Consumidor. Su ciclo de vida es pasivo; permanecen suspendidos hasta que se detecta una demanda de energía en el monitor.
    
- **Hilo Guardián (Watchdog):** Proceso de control en el `main` encargado de monitorizar el tiempo global de ejecución para detectar estados de irresolubilidad o interbloqueo (_deadlock_).
    

### Recursos Críticos

1. **Matriz de Estacionamiento (**$6 \times 6$**):** Recurso compartido fundamental. Debe garantizarse que ninguna celda sea ocupada por más de un identificador de proceso simultáneamente.
    
2. **Cola de Carga:** Estructura de datos compartida donde los vehículos varados registran su necesidad de asistencia.
    
3. **Contador de Movimientos:** Variable compartida que registra el progreso global para fines estadísticos y de control.
    

### Condiciones de Sincronización y Exclusión Mutua

La exclusión mutua se garantiza a través del uso de un **Monitor (`Estacionamiento.java`)**. Todas las operaciones que alteran el estado de los recursos críticos están marcadas como `synchronized`.

- **`wait()` / `notifyAll()`:** Se utilizan para gestionar la suspensión de vehículos sin energía y la activación de cargadores, evitando la espera activa (_busy-waiting_) en la lógica de producción.
    
- **Snapshot (Lectura no bloqueante):** Se implementó una función de clonado de matriz para que los hilos realicen cálculos de heurística sobre copias locales, minimizando el tiempo de bloqueo del monitor principal.
    

## 2. Decisiones de Diseño y Optimizaciones

### Uso de `Thread.yield()` frente a `Thread.sleep()`

Para maximizar el rendimiento en la simulación de alta velocidad, se ha sustituido el uso de pausas temporales fijas (`.sleep()`) por sugerencias al planificador del sistema operativo (`.yield()`). Mientras que `sleep` fuerza una suspensión de duración mínima (que puede ser excesiva para la velocidad de la CPU), `yield` permite que el hilo ceda el paso solo si hay otros procesos listos para ejecutar, optimizando el aprovechamiento de los núcleos del procesador sin añadir latencia artificial.

### Heurística de Backtracking Local

Cada vehículo posee una lógica de evaluación interna de profundidad 1. Antes de realizar un movimiento, el hilo solicita un _snapshot_ del tablero y calcula un puntaje de beneficio:

- **Vehículo 0:** Prioriza movimientos incrementales hacia la columna 5.
    
- **Obstáculos:** Detectan si su posición intersecta la Fila 2 (ruta de escape) y priorizan desplazamientos que despejen dicho eje.
    
- **Factor Estocástico:** Se mantiene un 15% de probabilidad de movimiento aleatorio para romper ciclos de oscilación entre dos estados de igual puntaje.
    

### Detección de Irresolubilidad

Dado que el espacio de estados de Rush Hour es finito pero complejo, se ha implementado un límite de tiempo de 5 segundos. Superar este umbral en una simulación de alta velocidad indica con alta probabilidad que la configuración inicial es físicamente imposible de resolver o que el sistema ha entrado en un interbloqueo irrecuperable.

## 3. Estructura del Proyecto

Para la entrega, el archivo comprimido debe mantener la siguiente jerarquía para asegurar la correcta resolución de paquetes y la compilación vía Makefile:

```
<26272957>-<Lopez>-<CEDULA_MARIA>-<Delgado>-Proyecto_3/
├── README.md              # Este archivo
├── Makefile               # Script de compilación
├── nivel1.txt             # Casos de prueba
├── nivel2.txt
└── solution/              # Carpeta del paquete java
    ├── Estacionamiento.java
    ├── Vehiculo.java
    ├── Cargador.java
    └── Rush_hour_v3.java
```

## 4. Instrucciones de Uso

### Compilación

Desde la raíz del proyecto (donde se encuentra el Makefile), ejecute:

```bash
make
```

Este comando generará los archivos `.class` respetando la estructura de paquetes.

### Ejecución

Para iniciar la simulación con un archivo de configuración específico:

```bash
java solution.Rush_hour_v3 nivel1.txt
```

### Limpieza

Para eliminar los binarios generados:

```bash
make clean
```


```makefile: Makefile de Entrega:Makefile
# Makefile para el proyecto Electric Rush Hour (LdP 2025)

# Definiciones
JAVAC = javac
BIN_DIR = .
SOURCES = solution/Estacionamiento.java \
          solution/Vehiculo.java \
          solution/Cargador.java \
          solution/Rush_hour_v3.java

# Regla principal
all: compile

# Compilación de todas las clases dentro del paquete experiments
compile:
	$(JAVAC) -d $(BIN_DIR) $(SOURCES)

# Limpiar los archivos .class generados
clean:
	rm -rf solution/*.class

# Ejemplo de ejecución rápida
run:
	java solution.Rush_hour_v3 nivel1.txt

.PHONY: all compile clean run

```