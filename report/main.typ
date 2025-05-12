#set text(lang: "lt", region: "lt")
#set text(font: "Times New Roman", size: 12pt)
#set par(
    spacing: 10pt,
    justify: true,
    //leading: 1.15em
    leading: 0.65em
)
#set page(
    paper: "a4",
    margin: (
        left: 30mm,
        right: 10mm,
        top: 20mm,
        bottom: 20mm
    )
)

#set figure(
  supplement: none,
  numbering: n => {
    [#n pav.]
  }
)
#show figure.caption: it => {
    set text(size: 10pt)
    if it.kind == image {
        [#text(weight: "bold")[#context it.counter.display(it.numbering)] #it.body]
    } else {
        [#it.supplement #context it.counter.display(it.numbering): #it.body]
    }
}

#show raw: it => block(
  fill: rgb("#f0ede6"),
  inset: 8pt,
  radius: 2pt,
  text(it)
)

#import "variables.typ": *
#include "mainPages/TitlePage.typ"

#set page(
    numbering: "1",
    number-align: right
)

#include "mainPages/SecondPage.typ"
#include "mainPages/ThirdPage.typ"
#include "mainPages/PageSummaryLT.typ"
#include "mainPages/PageSummaryEN.typ"

#include "mainPages/TableOfContents.typ"
//#include "mainPages/TableList.typ"
#include "mainPages/ImageList.typ"
#include "mainPages/TermsList.typ"

//#include "sections/testtext.typ"

#include "sections/ivadas.typ"

#set heading(
    numbering: "1."
)

#pagebreak()
#include "sections/typst/typst.typ" 
#pagebreak()
#include "sections/analize/analize.typ" 
#pagebreak()
#include "sections/projektas/projektas.typ"
#pagebreak()
#include "sections/realizacija/realizacija.typ"
#pagebreak()
#include "sections/testavimas/testavimas.typ"
#pagebreak()
#include "sections/dokumentacija/dokumentacija.typ"
#pagebreak()
#bibliography(
    "bibliography.yaml",
    title: "Literatūros sąrašas",
    style: "iso-690.csl"
)

