package cl.echobit.juego;

/**
 * Punto de arranque del servidor.
 *
 * <p>En esta etapa el arranque no hace nada de juego: no abre puertos, no toca base de datos y no
 * lee partidas. Solo comprueba que el artefacto se puede ejecutar y termina con codigo 0.</p>
 */
public final class Main {

    public Main() {
    }

    /**
     * Arranca la aplicacion sin efectos externos.
     *
     * @return 0 si el arranque fue correcto
     */
    public int arrancar() {
        return 0;
    }

    public static void main(String[] args) {
        System.exit(new Main().arrancar());
    }
}
