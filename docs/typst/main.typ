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
#title("COMPROBACIÓN LÓGICA DE CABLEADO Y CONECTIVIDAD DE UNA RED DE ÁREA LOCAL")
#set align(left)

= Sección

== Subsección
#lorem(15)

=== Subsubsección
#lorem(15)

==== Subsubsubsección
#lorem(15)

== Subsección
#lorem(15)

=== Subsubsección
#lorem(15)

==== Subsubsubsección
#lorem(15)

== Subsección
#lorem(15)

=== Subsubsección
#lorem(15)

==== Subsubsubsección
#lorem(15)

#figure(
  image("media/foto1_placeholder.jpg", width: 40%),
  caption: [Placeholder],
)

