#import "@preview/subpar:0.2.2": *
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

= Objetivo
Implementar un sistema distribuido para el juego "Sopa de Letras", aplicando los fundamentos de la programación en red. El objetivo principal es establecer una comunicación efectiva mediante *Sockets de Datagrama (UDP)* y gestionar la concurrencia a través de *Hilos (Threads)*, cumpliendo con los requisitos de identificación de usuarios, validación lógica y persistencia de datos.

= Competencia Específica
Desarrollar aplicaciones en red robustas bajo el modelo cliente-servidor, integrando mecanismos de comunicación no orientados a conexión (UDP) y procesamiento concurrente para la atención múltiple de clientes.

= Desarrollo
El proyecto materializa una arquitectura moderna y desacoplada, dividida en contenedores Docker para garantizar la portabilidad y escalabilidad.

== Arquitectura del Sistema
El sistema opera bajo un esquema híbrido que combina la robustez de Java con la interactividad de React:

+ *Cliente Web (Frontend)*: Desarrollado en *React* con *Vite* y estilizado mediante *Tailwind CSS*. Es responsable de la presentación, la captura de eventos del usuario (selección de celdas) y la gestión del estado visual (temas claro/oscuro).
+ *Web API Gateway (Backend)*: Implementado en *Spring Boot*. Expone endpoints REST (`/start`, `/validate`, `/scores`) que actúan como una fachada. Internamente, este componente traduce las peticiones HTTP en paquetes UDP.
+ *Servidor de Juego (UDP/Logic)*: Un componente crítico escrito en Java puro que extiende la clase `Thread`. Escucha en el puerto `9876` y procesa comandos (`START`, `VALIDATE`, `END`) de forma asíncrona.

== Características Técnicas Implementadas

=== Comunicación por Sockets UDP
La comunicación interna no utiliza TCP, sino `DatagramSocket`, lo que permite un intercambio de mensajes ligero ("Fire and forget").
- *Protocolo Definido*: Se diseñó un protocolo de texto simple: `COMANDO:DATOS:JUGADOR`.
- *Ejemplo*: `VALIDATE_WORD:JAVA:Max` envía la palabra "JAVA" intentada por el jugador "Max".

=== Concurrencia y Manejo de Sesiones
El servidor mantiene un `Map<String, List<String>>` para gestionar el estado de cada jugador de forma aislada.
- *Aleatoriedad por Hilo*: Cada vez que un jugador inicia, se seleccionan 10 palabras únicas de un diccionario maestro (que incluye términos como `DOCKER`, `ANGULAR`, `PYTHON`), asegurando que cada partida sea única.
- *Validación Contextual*: El servidor valida la palabra contra la lista específica de *ese* jugador, previniendo condiciones de carrera o validaciones cruzadas incorrectas.

=== Persistencia de Datos y Docker Volumes
Uno de los retos principales fue la persistencia en un entorno efímero como Docker.
- Se implementó la escritura en un archivo plano `scores.txt` (`FileWriter` en modo append).
- *Solución de Infraestructura*: Se configuró un *volumen* en `docker-compose.yml` (`./scores.txt:/scores.txt`) para mapear el archivo del contenedor al sistema de archivos del anfitrión (Host), garantizando que los puntajes históricos ("Leaderboard") sobrevivan al reinicio de los contenedores.

=== Interfaz de Usuario (UI/UX)
Se priorizó la experiencia de usuario con:
- *Tema Dinámico*: Soporte para modo oscuro/claro automático basado en preferencias del sistema.
- *Feedback Visual*: Retroalimentación inmediata (colores verde/rojo) al validar palabras.
- *Leaderboard*: Una ventana modal que consume el endpoint `/scores` para mostrar los tiempos históricos.

= Pruebas y Resultados
El despliegue se realiza mediante `docker compose up --build`.

*Pruebas de Integración* @fig:pruebas:
+ *Inicio*: El cliente envía el nombre, el servidor reserva las 10 palabras y retorna la matriz.
+ *Juego*: El usuario selecciona "JAVA". El cliente envía la petición, el servidor confirma y actualiza el puntaje en memoria.
+ *Finalización*: Al encontrar las 10 palabras, el cliente envía el tiempo total. El servidor escribe en `scores.txt` de manera sincronizada (`synchronized`) para evitar corrupción por escritura concurrente.

#figure(
  grid(
    columns: 2,
    // Creates two auto-sized columns
    gutter: 5mm,
    // Space between columns
    image("media/foto2.png", width: 100%),
    // Content for the first cell
    image("media/foto3.png", width: 100%),
    // Content for the second cell
    image("media/foto4.png", width: 100%),
    // Content for the third cell
    image("media/foto5.png", width: 100%),
    // Content for the fourth cell
  ),
  caption: [Aplicación funcionando], // Caption for the entire figure
) <fig:pruebas>


#figure(
  image("media/foto1.png", width: 100%),
  caption: [Arquitectura de Contenedores y Flujo de Datos],
)

= Conclusiones
Como equipo, este proyecto nos permitió consolidar conocimientos clave en sistemas distribuidos. Aprendimos que:
- *UDP vs TCP*: Aunque UDP no garantiza entrega, su simplicidad es ideal para mensajes cortos y rápidos dentro de una red controlada (como la red interna de Docker).
- *Gestión de Estado*: Mantener el estado del juego en el servidor (palabras por jugador) es crucial para la seguridad y consistencia lógica.
- *Dockerización*: La contenedorización no es solo "empaquetar", sino entender cómo interactúa el sistema de archivos del contenedor con el mundo exterior (Volumes), un punto donde inicialmente tuvimos dificultades pero logramos resolver eficazmente.
- *Full Stack*: La integración de un Frontend reactivo con un Backend robusto en Java demuestra la versatilidad necesaria en el desarrollo de software moderno.

El resultado final es una aplicación resiliente, estéticamente agradable y funcionalmente completa que cumple con todos los requerimientos académicos planteados.
