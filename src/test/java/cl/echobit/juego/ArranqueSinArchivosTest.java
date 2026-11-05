package cl.echobit.juego;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Verifica que el arranque no deje archivos de partida ni base de datos en el directorio de trabajo.
 * Los puertos y la red no se pueden observar de un proceso que dura milisegundos: de eso se encarga
 * {@link FuentesSinEfectosTest}, que revisa los imports antes de compilar.
 */
class ArranqueSinArchivosTest {

    @Test
    void elArranqueNoCreaArchivosDePartidaNiBase(@TempDir Path directorio) throws Exception {
        Path salida = Files.createTempFile("arranque", ".log");
        Process proceso = new ProcessBuilder(
                        rutaJava(), "-cp", classpath(), Main.class.getName())
                .directory(directorio.toFile())
                .redirectErrorStream(true)
                .redirectOutput(salida.toFile())
                .start();

        try {
            assertTrue(proceso.waitFor(30, TimeUnit.SECONDS), "el arranque debe terminar solo");
            assertEquals(0, proceso.exitValue(), () -> "salida del arranque: " + leer(salida));
            assertTrue(archivosCreados(directorio).isEmpty(),
                    "el arranque no debe crear archivos de partida ni base de datos");
        } finally {
            if (proceso.isAlive()) {
                proceso.destroyForcibly().waitFor();
            }
            Files.deleteIfExists(salida);
        }
    }

    private static String rutaJava() {
        return Path.of(System.getProperty("java.home"), "bin", "java").toString();
    }

    private static String classpath() {
        String deSurefire = System.getProperty("surefire.test.class.path");
        return deSurefire != null ? deSurefire : System.getProperty("java.class.path");
    }

    private static String leer(Path archivo) {
        try {
            return Files.readString(archivo);
        } catch (IOException e) {
            return "no se pudo leer la salida: " + e.getMessage();
        }
    }

    private static List<Path> archivosCreados(Path directorio) throws IOException {
        try (Stream<Path> arbol = Files.walk(directorio)) {
            return arbol.filter(Files::isRegularFile).toList();
        }
    }
}
