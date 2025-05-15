#let figs(title, target, outlined: true, headings: false) = {
  show heading: it => {
    set align(center)
    set text(font: "Times New Roman", size: 12pt)
    it
  }

  show outline.entry: it => link(
    it.element.location(),
    it.indented([*#it.prefix()*], it.inner()),
  )

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

#figs([Paveikslų sąrašas], figure.where(kind: image))
