//TODO: uncomment font change
#set text(font: "Times New Roman", size: 12pt)
#set par(
    spacing: 10pt,
    justify: true,
    leading: 1.15em
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
#include "mainPages/TableList.typ"
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
#include "sections/implementacija/implementacija.typ"
#pagebreak()
#include "sections/dokumentacija/dokumentacija.typ"

@harry

#bibliography(
    "bibliography.yaml",
    style: "iso-690.csl" 
)

