# Pet Society Server Emulator
![El emulador con el cliente original en el navegador](image.png)

Emulador de servidor para Pet Society, el juego social que Playfish publicó dentro de Facebook
entre 2008 y 2013. El cliente original, escrito en Flash y ActionScript 3, vuelve a funcionar
en el navegador gracias a Ruffle; este repositorio construye el servidor que ese cliente busca
cuando arranca.

## Qué era Pet Society

Un juego social en el que cada jugador cuidaba una mascota. El sistema permitía elegir nombre,
género, color y rasgos físicos, y la vida diaria consistía en bañarla, cepillarla, acariciarla y
alimentarla. Con las monedas ganadas se compraban muebles, ropa y objetos para la casa, y todo
se compartía con los amigos de la red social.

## Por qué este proyecto

Pet Society cerró sus servidores y, sin ellos, el cliente que jugó millones de personas quedó
inservible: no hay partida que cargar ni mundo que visitar. Preservar solo el archivo del juego
no alcanza, porque el juego no está en el cliente sino en lo que el servidor responde.

Este emulador reemplaza únicamente esa pieza perdida. El objetivo es que un jugador pueda abrir
el cliente original, cargar su partida y jugar como antes, sin modificar un solo gráfico ni un
solo archivo del juego.

## Estado actual

El proyecto tiene la **documentación de arquitectura publicada** y el esqueleto del servidor:
compila, arranca y todavía no implementa juego. Primero se define el alcance, la arquitectura y el
orden de las etapas; el calendario completo está en la documentación del proyecto. No hay versiones
para descargar.

Decisiones ya cerradas:

- El servidor se escribe en Java. Las rutas más exigentes del analizador binario pueden
  resolverse en C++, siempre con una versión equivalente en Java como respaldo.
- La forma elegida es un monolito modular: un proceso, un artefacto y límites internos que se
  verifican con pruebas. No se reparte el sistema en servicios.
- El protocolo se implementa desde cero, con compatibilidad total hacia el cliente original.

## Documentación

La arquitectura se describe con el modelo 4+1: cuatro vistas concurrentes y los escenarios que las
validan. Cada vista vive en su carpeta de `docs/` y responde una sola pregunta.

| Vista | Qué responde | Documento |
|---|---|---|
| Escenarios | Quién usa el emulador y para qué | [Casos de uso](docs/escenarios/casos-uso.md) |
| Lógica | Qué conceptos existen y cómo se relacionan | [Clases del dominio](docs/logica/clases-dominio.md), [modelo de datos](docs/logica/modelo-datos.md) |
| Proceso | Qué ocurre en ejecución y en qué orden | [Ejecución](docs/proceso/ejecucion.md), [protocolo](docs/proceso/protocolo.md), [carga de partida](docs/proceso/carga-partida.md), [tienda](docs/proceso/tienda.md) |
| Desarrollo | Qué artefactos se construyen y qué contrato exponen | [Estructura de módulos](docs/desarrollo/estructura-modulos.md), [flujo de trabajo](docs/desarrollo/flujo-trabajo.md) |
| Física | Qué artefacto corre en qué nodo | [Despliegue](docs/fisica/despliegue.md) |

El índice, el contexto del sistema y las convenciones de los diagramas están en
[docs/README.md](docs/README.md).

## Cómo se jugará

Cuando exista la primera versión, el procedimiento será corto: iniciar el servidor, abrir el
navegador en la dirección del juego y empezar. El puerto previsto es el 8881.

- En Windows, un lanzador junto al archivo de partida, sin instalar nada más.
- En macOS, ese mismo lanzador desde una terminal, en la carpeta de la partida.
- En Linux, el lanzador o el arranque desde el propio proyecto, con Java instalado.

Hasta entonces no hay binarios ni instrucciones que ejecutar, y este documento se actualizará
cuando la primera versión esté disponible.

## Partidas guardadas

La partida vive en un archivo con extensión `.pet` o `.nofil`. El servidor lo busca en su
carpeta y espera que se llame `profile`; si no lo encuentra, crea un perfil nuevo. Para cambiar
de partida basta mover, renombrar o reemplazar ese archivo, sin tocar nada más.

## Cómo funciona el emulador

Los juegos de Playfish no usaban una API convencional. El cliente intercambiaba mensajes
binarios por HTTP: cada petición lleva un tipo de encapsulación, un tipo de mensaje y un
identificador de sesión, y la respuesta repite el mismo tipo de mensaje con su contenido. Los
datos viajan en orden de red, con los bytes más significativos primero, y algunas lecturas
dependen del tipo de mensaje.

El trabajo del emulador es reproducir ese intercambio con exactitud: si cambia el orden de un
byte o el tamaño de un campo, el cliente falla. Por eso la fase de protocolo se cierra con
pruebas que escriben y vuelven a leer cada estructura.

## Etapas

1. Visión, arquitectura, reglas y plan.
2. Estructura del repositorio y arnés de trabajo.
3. Motor del protocolo binario con pruebas de paridad.
4. Dominio del juego, persistencia y carga de perfil.
5. Mensajes de habitación, tienda, inventario y cuidado.
6. Caché, base de datos relacional y módulo nativo opcional.

## Reglas de contribución

- Un commit por entrada visible: cada archivo de la raíz va solo y cada documento de `docs/` va con
  sus diagramas.
- Mensajes en español, en infinitivo, de una línea y sin punto final, y ninguno se repite
  (ejemplo: `Agregar la portada del emulador`).
- Sin comentarios decorativos ni líneas de separación en ningún archivo.
- Nada de credenciales, tokens, partidas reales de jugadores ni datos personales.

## Licencia

El código del emulador se distribuye bajo la GNU General Public License, versión 3. El texto
completo está en el archivo `LICENSE` del repositorio. En resumen: se puede usar, estudiar,
modificar y redistribuir, siempre que las versiones derivadas conserven la misma licencia y
publiquen su código.

Esa licencia cubre únicamente el código escrito para este proyecto. No otorga ningún derecho
sobre el juego: Pet Society, sus gráficos, su cliente y sus marcas no son parte de esta
licencia.

## Aviso legal

Pet Society fue desarrollado por Playfish y publicado por Electronic Arts. El nombre, los
gráficos, el cliente y las marcas pertenecen a sus dueños y se usan aquí con fines de
preservación y estudio. Ruffle se distribuye con licencia MIT.

Este proyecto no modifica el cliente original: solo reemplaza el servidor que dejó de existir.
