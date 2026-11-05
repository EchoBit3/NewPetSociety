package cl.echobit.juego;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class MainTest {

    @Test
    void elArranqueTerminaEnCero() {
        assertEquals(0, new Main().arrancar(), "el arranque sin logica de juego debe terminar en 0");
    }
}
