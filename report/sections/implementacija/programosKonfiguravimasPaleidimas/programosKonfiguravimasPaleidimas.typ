#set text(lang: "lt", region: "lt")
== Programos konfigūravimas ir paleidimas

Kadangi gatvės lygio navigavimo programa turi daug skirtingų nustatymų, kaip algoritmai, simbolių rinkiniai, spalvų
filtrai ir kiti, kiekviną kartą pasirinkti juos per programos parametrus būtų nepatogu. Todėl buvo priimtas sprendimas
programos nustatymus importuoti iš HOCON konfigūracinio failo. Visa šio failo tipo esmė yra naudoti JSON semantiką,
tačiau atvaiduoti jį lengviau žmogui įskaitomu stiliumi (CCC https://github.com/lightbend/config/blob/main/HOCON.md).
Sekant naudotojo dokumentaciją šį failą tūrėtų būti nesunku keisti rankiniu būdu. Žinoma, taip naudotojai gali pasirinkti
neoptimalius nustatymus. Dėl šios priežasties buvo sukurti konfigūraciniai scenarijai dar labiau palengvinantys konfigūravimą.
Šie scenarijai buvo pritaikyti populiariausioms operacinėms sistemoms: „Windows“, „Macos“, „Linux Debian“. Šie naudoja
„Gum“ TUI biblioteką skirtą pagražinti konfigūracinius vedlio scenarijus (CCC https://github.com/charmbracelet/gum). Naudotojui pačiam instaliuoti jokių papildomų
bibliotekų nereikės, visa tai atliekama automatiškai pirmąkart paleidus scenarijų.

Konfigūraciniai scenarijai veda naudotoją per visus programos nustatymus, kai kurie iš jų yra būtini, kai kurie -- ne.
Juose egzistuoja dviejų tipų įvestys:
- Pasirenkami nustatymai -- tai parinktys, kurias tereikia pasirinkti iš galimų variantų (#ref(<selection-input>)). Tai
  būna sąrašas galimybių, per kurias naviguojama rodyklių pagalba .
  #figure(
    image("/images/selection_input.png", width: 10cm),
    caption: [Pasirenkamas konfigūracinio scenarijaus nustatymas.],
  ) <selection-input>
- Įvedami nustatymai -- naudotojas šiuos nustatymus turi įvesti pats, tai dažniausiai bus skaitinės reikšmės arba API
  raktai (#ref(<manual-input>)). Šie pasirinkimai yra ribojami įvesties validacijos.
  #figure(
    image("/images/manual_input.png", width: 10cm),
    caption: [Įrašomas konfigūracinio scenarijaus nustatymas.],
  ) <manual-input>

Galiausiai, kai pasirenkami visi parametrai -- naudotojui pristatoma konfigūracijos apžvalga ir leidžiama sukurti
konfigūracinį failą (#ref(<config-summary>)). Po šio žingsnio jau esame pasiruošė paleisti pagrindinę programą.

#figure(
  image("/images/config_summary.png", width: 10cm),
  caption: [Konfigūracijos pasirinkimo apžvalga.],
) <config-summary>