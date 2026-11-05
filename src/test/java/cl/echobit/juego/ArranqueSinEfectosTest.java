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
 * Verifica que el arranque no deje rastro fuera del proceso: ni archivos de partida, ni base, ni
 * puerto escuchando.
 */
class ArranqueSinEfectosTest {

    @Test
    void elArranqueNoCreaArchivosNiAbrePuertos(@TempDir Path directorio) throws Exception {
        Process proceso = new ProcessBuilder(
                        javaBin(), "-cp", System.getProperty("java.class.path"), Main.class.getName())
                .directory(directorio.toFile())
                .redirectErrorStream(true)
                .start();

        assertTrue(proceso.waitFor(30, TimeUnit.SECONDS), "el arranque debe terminar solo");
        assertEquals(0, proceso.exitValue(), "el arranque debe terminar con codigo 0");
        assertTrue(archivosCreados(directorio).isEmpty(),
                "el arranque no debe crear archivos de partida ni base de datos");
    }

    private static String javaBin() {
        return Path.of(System.getProperty("java.home"), "bin", "java").toString();
    }

    private static List<Path> archivosCreados(Path directorio) throws IOException {
        try (Stream<Path> arbol = Files.walk(directorio)) {
            return arbol.filter(Files::isRegularFile).toList();
        }
    }
}
