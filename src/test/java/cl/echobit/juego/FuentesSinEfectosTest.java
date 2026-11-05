package cl.echobit.juego;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

/**
 * Guardian de la etapa H1: el codigo de produccion no puede tocar red, base de datos ni archivos.
 * Si aparece un import prohibido, esta prueba falla antes de que el defecto llegue a ejecutarse.
 */
class FuentesSinEfectosTest {

    private static final List<String> PROHIBIDOS = List.of(
            "import java.net.",
            "import javax.net.",
            "import java.sql.",
            "import javax.sql.",
            "import java.nio.file.",
            "import java.io.File",
            "import java.io.FileInputStream",
            "import java.io.FileOutputStream");

    @Test
    void elCodigoDeProduccionNoImportaRedBaseNiArchivos() throws IOException {
        try (Stream<Path> fuentes = Files.walk(Path.of("src", "main", "java"))) {
            List<String> culpables = fuentes
                    .filter(archivo -> archivo.toString().endsWith(".java"))
                    .flatMap(FuentesSinEfectosTest::prohibidosEn)
                    .toList();
            assertTrue(culpables.isEmpty(), "import prohibido en H1: " + culpables);
        }
    }

    private static Stream<String> prohibidosEn(Path archivo) {
        try {
            List<String> lineas = Files.readAllLines(archivo);
            return PROHIBIDOS.stream()
                    .filter(prohibido -> lineas.stream()
                            .map(String::trim)
                            .anyMatch(linea -> linea.startsWith(prohibido)))
                    .map(prohibido -> archivo + " -> " + prohibido);
        } catch (IOException e) {
            return Stream.of("no se pudo leer " + archivo + ": " + e.getMessage());
        }
    }
}
