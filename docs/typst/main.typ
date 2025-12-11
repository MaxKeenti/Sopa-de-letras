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

#title("REPORTE DE PRÁCTICA: SOPA DE LETRAS CON SOCKETS UDP")
#set align(left)

= 1. Objetivo
El estudiante implementará el juego Sopa de letras utilizando hilos, así como sockets de datagrama.

= 2. Competencia Específica
Desarrolla aplicaciones en red, con base en el modelo cliente-servidor y utilizando sockets de datagrama, así como hilos para el envío de datos a cada uno de los clientes.

= 3. Desarrollo
El proyecto consiste en la implementación de un sistema distribuido para el juego de Sopa de Letras, utilizando una arquitectura Cliente-Servidor.

== 3.1. Arquitectura del Sistema
El sistema se compone de tres elementos principales:
+ *Servidor de Juego (UDP)*: Encargado de la lógica del juego, generación de tableros y validación de palabras. Utiliza `DatagramSocket` en Java.
+ *Web API (Spring Boot)*: Actúa como puente (Gateway) entre el cliente web y el servidor UDP. Recibe peticiones HTTP y las transforma en datagramas UDP.
+ *Cliente Web (React)*: Interfaz gráfica de usuario moderna que permite interactuar con el juego desde un navegador.

== 3.2. Implementación del Servidor (Java)
El servidor utiliza un hilo (`Thread`) dedicado para escuchar peticiones en el puerto 9876.
- Al recibir `START_GAME`, genera una matriz de 15x15 con palabras aleatorias.
- Al recibir `VALIDATE_WORD`, verifica la existencia de la palabra.

== 3.3. Implementación del Cliente (React)
El cliente utiliza `fetch` para comunicarse con la API, renderiza el tablero de forma dinámica y gestiona la selección de celdas por parte del usuario.

= 4. Pruebas y Resultados
Se verificó el correcto funcionamiento mediante el despliegue en contenedores Docker.

#figure(
  image("media/foto1_placeholder.jpg", width: 80%),
  caption: [Interfaz del Juego Sopa de Letras],
)

= 5. Conclusiones
Se logró implementar exitosamente la comunicación mediante Sockets UDP, integrando tecnologías modernas como React y Sprint Boot. El uso de Hilos permitió al servidor manejar las peticiones de juego de manera concurrente (simulada en este diseño por la naturaleza de UDP). La arquitectura en contenedores facilita el despliegue y pruebas del sistema.

