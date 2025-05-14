#import "/variables.typ" : *

#page(header: none)[
  #set text(size: 12pt)
  #AuthorName.at(1).
  #ProjectTitleEN.
  #ProjectTypeEN.
  #ProjectSupervisorEN.join(" ").
  #ProjectFacultyEN, Kaunas University of Technology.

  Study field and area: #ProjectStudyFieldAndAreaEN.

  Keywords: #ProjectKeywordsEN.

  #ProjectCity, #ProjectYear.
  #context counter(page).final().at(0) pages.

  #set align(center)
    *Summary*
  
  #set align(start)
  This final bachelor's thesis project introduces an innovative solution: a program designed for interactively navigating global streets using solely a
  command-line interface. At the core of this work lies a fundamental inquiry into the suitability of the command-line environment as a medium
  for visually rich applications. The primary objective of the project is twofold: firstly, to conduct a comprehensive analysis of the technical and
  user interface limitations of the command-line environment within the context of such an application. Secondly, based on this analysis,
  to design and implement a functional application demonstrating street-level image viewing in ASCII format.

  The project delves into the practical challenges encountered during the implementation of the solution, ranging from the limited support for
  colors and graphical elements in the terminal to the necessity of creating an intuitive navigation system without traditional graphical widgets.
  It describes the chosen compromises and creative solutions that allowed these limitations to be circumvented or mitigated. A significant part
  of the work involves the analysis and practical application of integrating external services, such as the Mapillary API for sourcing street-level
  imagery, and other software libraries (JLine for terminal control, Cats Effect for asynchronicity). Particular attention is given to the investigation
  of various ASCII art generation algorithms (luminance-based, edge detection) and their effectiveness in dynamically converting photographic
  images into a textual representation.

  The technical implementation of the project was carried out using the Scala programming language, chosen for its powerful type system, support
  for functional programming paradigms, and good integration with the Java ecosystem. The project report, in turn, was prepared using Typst –
  a modern, code-based document preparation system.
]
