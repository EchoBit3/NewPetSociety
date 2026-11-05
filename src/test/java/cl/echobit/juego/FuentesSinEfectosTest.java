package cl.echobit.juego;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Guardian de la etapa H1: el codigo de produccion solo puede usar la biblioteca estandar
 * permitida. Red, base de datos, archivos, reflexion y procesos requieren una etapa abierta y una
 * prueba de contrato; mientras no exista esa etapa, esta prueba falla antes de ejecutar el codigo.
 *
 * <p>Se usa lista blanca, no lista negra: un import que no este permitido rompe la prueba aunque
 * nadie haya previsto ese paquete.</p>
 */
class FuentesSinEfectosTest {

    private static final List<String> IMPORT_PERMITIDO = List.of(
            "java.lang.",
            "java.util.",
            "java.math.",
            "java.time.",
            "java.text.",
            "cl.echobit.juego.");

    private static final List<String> TEXTO_PROHIBIDO = List.of(
            "java.net",
            "java.sql",
            "java.io",
            "java.nio",
            "javax.",
            "jdk.",
            "java.lang.reflect",
            "ProcessBuilder",
            "Runtime.",
            "Class.forName",
            "System.loadLibrary",
            "Socket",
            "DriverManager",
            "FileOutputStream",
            "FileReader");

    @Test
    void elCodigoDeProduccionSoloUsaLaBibliotecaPermitida() throws IOException {
        Path raiz = Path.of("src", "main", "java");
        assertTrue(Files.isDirectory(raiz), "no existe la raiz de fuentes " + raiz.toAbsolutePath());

        try (Stream<Path> fuentes = Files.walk(raiz)) {
            List<Path> archivos = fuentes
                    .filter(archivo -> archivo.toString().endsWith(".java"))
                    .toList();
            assertFalse(archivos.size() < 6,
                    "se esperaban al menos 6 fuentes, se revisaron " + archivos.size());
            for (Path archivo : archivos) {
                assertEquals(List.of(), revisar(archivo), "efecto externo prohibido en " + archivo);
            }
        }
    }

    @Test
    void elRevisorRechazaUnImportFueraDeLaLista(@TempDir Path directorio) throws IOException {
        Path fuente = escribir(directorio, "Mala", """
                package x;

                import java.sql.Connection;

                class Mala {
                }
                """);
        assertEquals(List.of("import java.sql.Connection"), revisar(fuente));
    }

    @Test
    void elRevisorRechazaElComodinDePaquete(@TempDir Path directorio) throws IOException {
        Path fuente = escribir(directorio, "Comodin", """
                package x;

                import java.io.*;

                class Comodin {
                }
                """);
        assertEquals(List.of("import java.io.*"), revisar(fuente));
    }

    @Test
    void elRevisorRechazaElNombreCompletoSinImport(@TempDir Path directorio) throws IOException {
        Path fuente = escribir(directorio, "Completo", """
                package x;

                class Completo {
                    Object conexion = java.sql.DriverManager.class;
                }
                """);
        assertFalse(revisar(fuente).isEmpty(), "el nombre completo sin import tambien debe fallar");
    }

    @Test
    void elRevisorAceptaLaListaBlanca(@TempDir Path directorio) throws IOException {
        Path fuente = escribir(directorio, "Buena", """
                package x;

                import java.util.List;
                import java.time.Instant;

                class Buena {
                }
                """);
        assertEquals(List.of(), revisar(fuente));
    }

    /**
     * Devuelve los hallazgos de un archivo. Es static y de paquete para poder probarlo con fuentes
     * de ejemplo sin tocar el arbol real.
     */
    static List<String> revisar(Path archivo) throws IOException {
        List<String> hallazgos = new ArrayList<>();
        for (String linea : Files.readAllLines(archivo)) {
            String limpia = linea.trim();
            if (limpia.startsWith("import ")) {
                String objetivo = limpia.substring("import ".length()).replace(";", "").trim();
                boolean permitido = IMPORT_PERMITIDO.stream().anyMatch(objetivo::startsWith);
                if (!permitido) {
                    hallazgos.add(limpia.replace(";", ""));
                }
                continue;
            }
            for (String prohibido : TEXTO_PROHIBIDO) {
                if (limpia.contains(prohibido)) {
                    hallazgos.add(prohibido);
                }
            }
        }
        return hallazgos;
    }

    private static Path escribir(Path directorio, String nombre, String contenido) throws IOException {
        Path archivo = directorio.resolve(nombre + ".java");
        Files.writeString(archivo, contenido);
        return archivo;
    }
}
