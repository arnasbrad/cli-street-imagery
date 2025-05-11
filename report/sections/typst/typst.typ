#set text(lang: "lt", region: "lt")

= Darbo rengimo įrankis „Typst“<irankio-pasirinkimas-typst>

Baigiamojo darbo rengimas yra sudėtingas procesas, reikalaujantis ne tik turinio kūrimo, bet
ir nuoseklaus jo formatavimo bei struktūrizavimo. Tradiciškai akademiniuose darbuose dominuoja
du pagrindiniai įrankiai: teksto redaktoriai, tokie kaip „Microsoft Word“ (ar jo atitikmenys, pvz.,
„LibreOffice Writer“), ir tipografinė sistema „LaTeX“. Šiame darbe, siekiant efektyvesnio ir
lankstesnio rengimo proceso, buvo pasirinktas alternatyvus, modernus įrankis – „Typst“ @typst-homepage.
Šiame skyriuje argumentuojamas šis pasirinkimas, lyginant
wwjį su labiau įprastomis alternatyvomis.

== Tradicinių Įrankių Apžvalga ir Jų Trūkumai Projekto Kontekste

1. WYSIWYG (angl. _What you see is what you get_) ( redaktoriai („Microsoft Word“ ir kt.): šie
  įrankiai yra populiarūs dėl savo vizualios sąsajos („ką matai, tą ir gauni“) ir palyginti žemo
  pradinio naudojimo slenksčio. Jie tinka paprastesniems dokumentams, tačiau rengiant sudėtingos
  struktūros mokslinį darbą, ypač informacinių technologijų srityje, išryškėja jų trūkumai:
  - Formatavimo konsistencijos išlaikymas: didelės apimties darbe rankiniu būdu užtikrinti vienodą
    stilių antraštėms, citatoms, kodų pavyzdžiams, paveikslėliams ir lentelėms yra sudėtinga ir
    atima daug laiko. Stilių sistemos padeda, bet dažnai reikalauja nuolatinės priežiūros.
  - Struktūros valdymas: dokumento dalių pertvarkymas, skyrių pernumeravimas, kryžminių nuorodų
    ir turinio automatinis atnaujinimas gali tapti komplikuotas.
  - Versijų valdymas ir bendradarbiavimas: šių redaktorių naudojami dvejetainiai failų formatai
    sunkiai integruojasi su versijų kontrolės sistemomis (pvz., „Git“), kurios yra esminės programinės
    įrangos kūrimo praktikoje ir naudingos rašant bet kokį ilgą tekstą. Pakeitimų sekimas ir sujungimas
    yra ribotas. Tai mums ypač svarbu todėl, nes šį bakalaurinį darbą rašome dviese.
  - Automatizavimas: galimybės automatizuoti pasikartojančias užduotis ar integruoti programinį kodą
    dokumento generavimui yra labai ribotos.
2. „LaTeX“: tai ilgametis akademinių publikacijų standartas, ypač tiksliuosiuose moksluose ir informatikoje.
  „LaTeX“ yra tipografinė sistema, pagrįsta ženklinimo kalba (angl. _markup language_), leidžianti autoriui
  sutelkti dėmesį į turinį, o formatavimą patikėti sistemai. Jos privalumai sprendžia daugelį „Word“ problemų:
  - Puiki tipografinė kokybė: ypač matematinių formulių ir sudėtingų maketų atveju.
  - Struktūra ir konsistencija: griežta struktūra ir stilių valdymas užtikrina dokumento vientisumą.
  - Automatizavimas: bibliografijos, turinio, paveikslų sąrašų, kryžminių nuorodų generavimas yra
    standartinė funkcijos dalis.
  - Tekstinis formatas: _.tex_ failai yra paprasto teksto, todėl puikiai tinka versijų kontrolei su „Git“.
  - Plati ekosistema: daugybė paketų ir šablonų įvairiems poreikiams.

  Tačiau „LaTeX“ taip pat turi trūkumų, ypač šiuolaikiniam kūrėjui:
  - Sintaksės sudėtingumas: „LaTeX“ sintaksė, nors ir galinga, dažnai yra gana sudėtinga,
    reikalaujanti daug specialiųjų simbolių (_\\_, _{_, _}_) ir gali būti sunkiai įskaitoma.
  - Mokymosi kreivė: įvaldyti „LaTeX“ iki lygio, leidžiančio laisvai kurti ir modifikuoti
    sudėtingus dokumentus, reikalauja nemažai laiko ir pastangų.
  - Klaidų pranešimai: gali būti sunkiai suprantami, ypač pradedantiesiems.
  - Kompiliavimo greitis: didelių dokumentų su daug paketų kompiliavimas gali užtrukti.
  - Programavimo galimybės: nors „TeX“ yra Turingo užbaigta (angl. _Turing-Complete_) kalba,
    jos makro sistema yra gana specifinė ir neprilygsta modernių skriptų kalbų lankstumui.

== Kodėl „Typst“? Argumentai Pasirinkimui

Atsižvelgiant į norą naudoti kodu pagrįstą dokumentų rengimo sistemą (dėl versijavimo, automatizavimo
ir struktūros privalumų), tačiau siekiant išvengti kai kurių „LaTeX“ sudėtingumų, buvo pasirinkta „Typst“.
Tai palyginti nauja, bet sparčiai populiarėjanti, kodu pagrįsta tipografinė sistema, sukurta su tikslu suderinti
„LaTeX“ galią su modernesne ir paprastesne sintakse bei naudojimo patirtimi. Pagrindiniai „Typst“
privalumai šio darbo kontekste:

1. Moderni ir paprasta sintaksė: „Typst“ sintaksė yra įkvėpta „Markdown“ ir modernių programavimo kalbų.
  Ji yra žymiai glaustesnė ir intuityvesnė nei „LaTeX“. Paprastiems formatavimo veiksmams (pvz., paryškinimas,
  kursyvas, antraštės, sąrašai) naudojama lengvai įsimenama sintaksė, panaši į „Markdown“, o sudėtingesniems
  elementams (pvz., puslapio konfigūracija, funkcijos) naudojama aiški funkcinė sintaksė.

  ```typst
  // Pavyzdys: Typst sintaksė paprasta
  = Skyriaus Antraštė
  Čia yra *paryškintas* ir _kursyvu_ parašytas tekstas.

  #figure(
    image("images/logo.png", width: 4cm),
    caption: [Logotipas],
  )
  ```
2. Nuožulnesnė mokymosi kreivė: pradėti naudotis „Typst“ ir pasiekti gerų rezultatų galima žymiai
  greičiau nei naudojant „LaTeX“. Pagrindinės funkcijos yra lengvai perprantamos, o sudėtingesni
  aspektai yra logiškai struktūrizuoti.
3. Puiki dokumentacija: „Typst“ turi išsamią, interaktyvią ir lengvai naršomą oficialią dokumentaciją
  su gausiais pavyzdžiais @typst-docs-homepage. Tai labai palengvina mokymąsi ir problemų sprendimą.
4. Greitas kompiliavimas: „Typst“ yra sukurtas su dideliu dėmesiu našumui. Kompiliavimas, ypač inkrementinis
  (kai keičiama tik dalis dokumento), yra ženkliai greitesnis nei daugeliu atvejų su „LaTeX“ @typst-guide-latex.
  Tai leidžia matyti pakeitimų rezultatus beveik akimirksniu, kas pagerina rašymo ir taisymo procesą.
5. Integruotos galingos programavimo galimybės: skirtingai nuo „LaTeX“ makro sistemos, „Typst“ turi
  integruotą, modernią skriptų kalbą. Galima lengvai apibrėžti kintamuosius, funkcijas, naudoti ciklus
  ir sąlygas tiesiogiai dokumento kode. Tai atveria plačias galimybes automatizacijai, duomenų
  vizualizavimui ar nestandartinių elementų kūrimui be būtinybės ieškoti ar kurti sudėtingus išorinius paketus (įskiepius).

    ```typst
    // Pavyzdys: Typst programavimas
    #let project_name = "ASCII Street View CLI"
    Šiame darbe aprašoma sistema #project_name.

    #for i in range(1, 4) {
      [Punktas #i]
    }
    ```
6. Geras įrankių palaikymas: „Typst“ turi puikų „Language Server Protocol“ (LSP) palaikymą, kas reiškia,
  kad populiarūs kodų redaktoriai (pvz., „Visual Studio Code“, „NeoVim“ ar net „Intellij IDEA“) gali
  teikti sintaksės paryškinimą, automatinį raktažodžių užbaigimą, klaidų tikrinimą realiu laiku ir
  kitas pagalbos funkcijas, kurios ženkliai padidina produktyvumą.
7. Tekstinis formatas ir „Git“ suderinamumas: kaip ir „LaTeX“, „Typst“ naudoja paprasto teksto _.typ_
  failus, kurie idealiai tinka versijų kontrolei su „Git“.
8. Dokument konfigūracija: sistema leidžia lengvai keisti viso dokumento stilių ir įvairius parametrus vienoje vietoje.

== Galimi trūkumai ir kompromisai

Nors „Typst“ siūlo daug privalumų, kaip palyginti naujas įrankis, jis turi ir tam tikrų aspektų,
į kuriuos reikėjo atsižvelgti:

- Ekosistema ir bendruomenė: „Typst“ paketų ir šablonų ekosistema bei naudotojų bendruomenė
  yra mažesnė nei „LaTeX“. Tai reiškia, kad kai kuriems labai specifiniams poreikiams gali
  nebūti paruošto sprendimo (nors integruotas programavimas dažnai leidžia jį sukurti).
- Institucijų įpratimas: kai kuriose akademinėse institucijose ar leidyklose „LaTeX“ gali
  būti labiau įprastas ar net reikalaujamas formatas. Tačiau šio darbo kontekste lankstumas
  ir kūrimo efektyvumas buvo laikomi svarbesniais veiksniais. Taip pat mums buvo įdomu
  išbandyti mažiau naudojamą įrankį, įvertinti jo galimybes ir galbūt palikti veikiantį
  bei reikalavimus atitinkantį šabloną kitoms kartoms.
- Produktas dar nebaigtas: šios ataskaitos rašymo metu, naujausia „Typst“ versija yra 0.13.1 --
  tai reiškia, jog įrankis gali būti nepilnai realizuotas bei gali turėti spragų.

== Problemos, su kuriomis susidūrėme

Kadangi „Typst“ yra sąlyginai naujas įrankis ir jis nėra plačiai naudojamas „KTU“ rašto darbų
kūrime, kai kurie reikalavimai gali kelti iššūkių.

=== Tarpai tarp eilučių

Pagal rašto darbų metodinius nurodymus, tarpai tarp eilučių turėtų būti 1,15 (matavimo vienetas nėra
nurodytas). „Typst“ įrankyje egzistuoja _leading_ nustatymas, kuris būtent tai ir nurodo.
Tačiau su 1,15 reikšme, tarpai tarp eilučių tapo žymiai per dideli. Tai reiškia, jog
šis įrankis naudoja kitokį tarpų skaičiavimą, nei, pavyzdžiui, „MS Word“.

Rankiniu būdu testuojant, mūsų nuomone 0,65 tarpas atrodo panašus į kitus pavyzdžius.

```typ
#set par(
    leading: 0.65em
)
```

=== Bibliografija

„KTU“ rašto darbų reikalavimai nurodo, jog bibliografija turi būti _ISO-690:2021_ formato.
„Typst“ įrankis naudoja specialius _.csl_ formato failus bibliografijos formatavimui.

Deja, bet šiuo metu neegzistuoja (arba mes nesugebėjome rasti) _.csl_ failo _ISO-690:2021_ formatui,
todėl teko patiems adaptuoti esančias konfigūracijas, kad daugmaž atitikti reikiamą formatą.

== Išvada

Apibendrinant, „Typst“ pasirinkimas šiam baigiamajam darbui buvo sąmoningas ir pagrįstas sprendimas.
Jis leido pasinaudoti kodu pagrįsto dokumentų rengimo privalumais (struktūra, versijų valdymas, automatizavimas),
kartu išvengiant „LaTeX“ sudėtingumo ir lėtumo. Moderni sintaksė, greitas kompiliavimas, puiki dokumentacija,
integruotas programavimas ir geras įrankių palaikymas padarė darbo rašymo procesą efektyvesnį,
sklandesnį ir malonesnį. Nors įrankis yra naujesnis nei „LaTeX“, jo teikiami privalumai nusvėrė
galimus ekosistemos dydžio trūkumus, ypač IT srities projektui, kur modernių įrankių įvaldymas
ir taikymas yra aktualus.
