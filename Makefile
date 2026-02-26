# Makefile para el proyecto Electric Rush Hour

# Directorio de salida
BIN_DIR = .

# Archivos fuente
SOURCES = solution/Estacionamiento.java \
          solution/Vehiculo.java \
          solution/Cargador.java \
          solution/Rush_hour_v3.java

# Regla por defecto
all: compile

# Compilación
compile:
	javac -d $(BIN_DIR) $(SOURCES)

# Limpiar archivos compilados
clean:
	rm -rf experiments/*.class

# Ejemplo de ejecución
run:
	java experiments.Rush_hour_v3 nivel1.txt

.PHONY: all compile clean run