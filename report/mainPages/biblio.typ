#let sources(title) = {
  show heading: it => {
    set align(center)
    set text(font: "Times New Roman", size: 12pt)
    it
  }

  heading(title)
  
  bibliography(
    title: none,
    "/bibliography.yaml",
    style: "/iso-690.csl"
  )
}

#sources([Literatūros sąrašas])
