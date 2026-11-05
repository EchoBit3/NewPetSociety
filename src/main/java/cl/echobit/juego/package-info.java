/**
 * Raiz de los paquetes del servidor.
 *
 * <p>Contiene un subpaquete por modulo: {@code protocolo}, {@code dominio}, {@code aplicacion},
 * {@code infraestructura} y {@code herramientas}. El artefacto es unico y los limites entre modulos
 * se verifican con pruebas, no con modulos separados de compilacion.</p>
 *
 * <p>El arranque vive en {@link cl.echobit.juego.Main} y no contiene logica de juego.</p>
 */
package cl.echobit.juego;
