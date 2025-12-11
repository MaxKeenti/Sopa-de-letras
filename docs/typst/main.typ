#import "portada-template.typ": portada

#let integrantes = (
  "Garcia Salgado Angel Gabriel",
  "García Zavala Sebastián",
  "Garrido Cázares Ashley Elizabeth",
  "Gonzalez Calzada Maximiliano",
  "Espinosa Esquivias Mauricio Genaro",
)

#portada(
  "UNIDAD DE APRENDIZAJE",
  "PRÁCTICA",
  "EQUIPO",
  "SECUENCIA Y PERIODO",
  "INTEGRANTES",
  "PROFESOR",
  "FECHA DE ENTREGA",
  "Redes y Conectividad",
  "Sopa de letras",
  "Equipo 3",
  "5NM52 2026-1",
  integrantes,
  "Hernández Cerón Ricardo",
  "11 de diciembre de 2025",
)

#set text(
  font: "ITC Avant Garde Gothic",
  lang: "es",
  weight: "semibold",
)

#set page(
  paper: "us-letter",
  margin: (left: 3cm, top: 2.5cm, right: 2.5cm, bottom: 2.5cm),
  numbering: "1",
)

#outline(
  title: "Índice",
  indent: auto,
)

#pagebreak()
#set par(justify: true, leading: 1.4em)
#set heading(numbering: "1.")
#set list(indent: 1.5em)
#v(1cm)

#set align(center)
#title("PRÁCTICA FINAL: SOPA DE LETRAS")
#set align(left)

= Introducción

El juego "Sopa de letras" consiste en un tablero de 15 filas por 15 columnas lleno de letras. Algunas de las filas, columnas o diagonales que forman las letras sobre el tablero dan lugar a palabras sobre un tema común planteado.

El objetivo de esta práctica es implementar dicho juego utilizando una arquitectura Cliente-Servidor mediante Sockets de Datagrama (UDP) y el uso de hilos (threads) para la gestión de la comunicación y la interfaz de usuario.

= Desarrollo

== Arquitectura del Sistema

El sistema se divide en dos componentes principales: el Servidor y el Cliente.

=== Servidor
El servidor es responsable de:
- Generar el tablero de juego de 15x15 con palabras aleatorias.
- Gestionar las sesiones de juego (aunque UDP es sin conexión, se rastrea el estado lógico).
- Validar las palabras encontradas por el cliente.
- Registrar los tiempos de finalización de los jugadores.

El servidor escucha en el puerto UDP 5000 por defecto.

=== Cliente
El cliente es una aplicación de escritorio (Java Swing) que permite al usuario:
- Conectarse al servidor.
- Visualizar el tablero de juego.
- Seleccionar palabras arrastrando el mouse.
- Visualizar el tiempo transcurrido y las palabras encontradas.

== Protocolo de Comunicación

Se diseñó un protocolo simple basado en texto sobre UDP:

- *START:\<Nombre\>* : El cliente solicita iniciar un juego.
- *BOARD:\<Datos\>* : El servidor responde con el tablero serializado.
- *FOUND:\<Palabra\>* : El cliente envía una palabra seleccionada.
- *VALID:\<Palabra\>* / *INVALID* : El servidor confirma si la palabra es correcta.
- *FINISH:\<Nombre\>,\<Tiempo\>* : El cliente notifica que terminó.
- *ACK_FINISH* : El servidor confirma el registro del tiempo.

== Implementación

Se utilizaron las siguientes clases Java:

- `Server.java`: Clase principal del servidor.
- `BoardGenerator.java`: Lógica de generación de sopa de letras.
- `Client.java`: Clase principal del cliente e interfaz gráfica.
- `NetworkClient.java`: Gestión de sockets UDP en el cliente.

= Pruebas

A continuación se muestran las pruebas de funcionamiento:

#figure(
  image("media/foto1_placeholder.jpg", width: 80%),
  caption: [Interfaz del Cliente con el tablero cargado],
)

#figure(
  image("media/foto1_placeholder.jpg", width: 80%),
  caption: [Validación de palabras encontradas],
)

= Conclusión

Se logró implementar existosamente el juego de Sopa de Letras cumpliendo con los requisitos de utilizar Sockets de Datagrama y Hilos. La arquitectura elegida permite una comunicación fluida y la lógica del servidor garantiza que el juego sea justo al validar las palabras centralizadamente.

