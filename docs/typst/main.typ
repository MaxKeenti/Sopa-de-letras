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

=== Servidor (Game Server)
El servidor (componente Java puro) mantiene la lógica central:
- Escucha en el puerto UDP 5000.
- Genera tableros y validad palabras.
- Es desplegado en un contenedor Docker (`game-server`).

=== Web Client (Bridge + Frontend)
Dado que los navegadores no soportan sockets UDP directamente, se implementó una arquitectura de capas:

1. *Frontend (React + Vite)*: Interfaz de usuario moderna y responsiva. Se comunica via HTTP con el Backend.
2. *Backend (Spring Boot BFF)*: Actúa como "Bridge" (Backend For Frontend). Recibe peticiones HTTP del frontend y las traduce a datagramas UDP hacia el Game Server.

Ambos componentes se empaquetan en un único contenedor Docker (`web-client`), donde Spring Boot sirve tanto la API como los archivos estáticos de React.

== Protocolo de Comunicación

El flujo de información es:
`Browser (React) <--(HTTP)--> Spring Boot <--(UDP)--> Game Server`

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

= Despliegue con Docker

El sistema ha sido "dockerizado" para facilitar su ejecución y asegurar que el entorno sea consistente.

Se definieron dos contenedores:
1. `Dockerfile.server`: Compila y ejecuta el servidor Java UDP utilizando OpenJDK 17.
2. `Dockerfile.web`: Utiliza un enfoque *multi-stage build*:
  - Stage 1 (Node.js): Compila el frontend React.
  - Stage 2 (Maven): Compila el backend Spring Boot e incrusta los estáticos de React.
  - Stage 3 (JRE): Ejecuta el JAR final optimizado.

Para iniciar el sistema basta con ejecutar:

```bash
docker compose up
```

La aplicación web estará disponible en `http://localhost:8080`.

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

Se logró implementar existosamente el juego de Sopa de Letras cumpliendo con los requisitos de utilizar Sockets de Datagrama e Hilos. La arquitectura elegida permite una comunicación fluida y la lógica del servidor garantiza que el juego sea justo al validar las palabras centralizadamente.

