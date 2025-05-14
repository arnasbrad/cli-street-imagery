#import "/variables.typ": *

#page(header: none)[
  #set text(size: 12pt)
  #AuthorName.at(1).
  #ProjectTitle.
  #ProjectType.
  #ProjectSupervisor.join(" ").
  #ProjectFaculty, Kauno technologijos universitetas.

  Studijų kryptis ir sritis: #ProjectStudyFieldAndArea.

  Reikšminiai žodžiai: #ProjectKeywords.

  #ProjectCity, #ProjectYear.
  #context counter(page).final().at(0) p.

  #set align(center)
    *Santrauka*
  
  #set align(start)

  Šis baigiamasis bakalauro studijų projektas pristato inovatyvų sprendimą – programą, skirtą
  interaktyviai naršyti pasaulio gatves naudojant tik komandinės eilutės sąsają. Darbo epicentre –
  fundamentalus klausimas apie komandinės eilutės, kaip terpės, tinkamumą vizualiai turtingoms programoms.
  Pagrindinis projekto tikslas yra dvejopas: pirma, atlikti išsamią komandinės eilutės aplinkos
  techninių ir vartotojo sąsajos apribojimų analizę tokio tipo programos kontekste. Antra, remiantis šia analize,
  suprojektuoti ir realizuoti veikiančia programą, demonstruojančią gatvės lygio vaizdų peržiūrą ASCII formatu.

  Projekte detaliai gilinamasi į praktinius iššūkius, su kuriais susidurta realizuojant sprendimą:
  pradedant ribotu spalvų ir grafinių elementų palaikymu terminale, baigiant poreikiu sukurti intuityvią
  navigacijos sistemą be tradicinių grafinių valdiklių. Aprašomi pasirinkti kompromisai ir kūrybiniai sprendimai,
  leidusieji šiuos apribojimus apeiti ar sušvelninti. Svarbi darbo dalis yra išorinių paslaugų, tokių kaip
  „Mapillary“ API gatvės vaizdų tiekimui, ir kitų programinės įrangos bibliotekų („JLine“ terminalo valdymui,
  „Cats Effect“ pašalinių efektų valdymui) integravimo analizė ir praktinis pritaikymas. Ypatingas dėmesys
  skiriamas įvairių ASCII meno generavimo algoritmų (šviesumo, kraštinių detekcijos) tyrimui ir jų
  efektyvumui dinamiškai konvertuojant fotografinius vaizdus į tekstinę reprezentaciją.

  Techninė projekto realizacija atlikta naudojant „Scala“ programavimo kalbą, kuri pasirinkta dėl jos galingos
  tipų sistemos, funkcinio programavimo paradigmos palaikymo ir geros integracijos su „Java“ ekosistema.
  Projektinė ataskaita, savo ruožtu, parengta naudojant „Typst“ – modernią, kodu pagrįstą dokumentų rengimo sistemą.
  Siekiant atitikti akademinius reikalavimus ir užtikrinti dokumento kokybę, šiam darbui buvo sukurtas specialus
  „Typst“ šablonas.
]
