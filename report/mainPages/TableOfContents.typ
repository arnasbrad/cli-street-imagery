#let toc(title, target, outlined: true) = {
  show heading: it => {
    set align(center)
    set text(font: "Times New Roman", size: 12pt)
    it
  }
  show outline.entry.where(level: 1): it => [
    *#it*
  ]
  show outline.entry.where(level: 4): it => []

  outline(
    title: {
      set heading(outlined: outlined)
      heading(title)
    },
    indent: 0cm,
    target: target,

  )
  pagebreak()
}

#toc([Turinys], heading, outlined: false)
