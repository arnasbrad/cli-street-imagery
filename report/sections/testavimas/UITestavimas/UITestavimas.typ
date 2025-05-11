== Naudotojo sąsajos (TUI) testavimas

Terminalo naudotojo sąsajos (angl. _Terminal User Interface_ – TUI) testavimas šiame projekte yra itin svarbus, nes
programa yra skirta naudoti išskirtinai komandinėje eilutėje. Pagrindinis tikslas – užtikrinti, kad sąveika
su programa būtų intuityvi, efektyvi ir neklaidintų naudotojo, nepaisant tekstinės aplinkos ribojimų.
TUI testavimas daugiausia buvo atliekamas rankiniu būdu, imituojant realius naudotojo veiksmus ir scenarijus.

Testavimo metu dėmesys buvo kreipiamas į šiuos aspektus:

- Valdymo ir navigacijos patikimumas
  - Kruopščiai tikrintas kiekvienos programos būsenos valdymas naudojant klaviatūros komandas (pvz., _n_ – navigacija,
    _g_ – spėliojimas (jei aktyvus), _h_ – pagalba, _q_ – išėjimas, _r_ – perpiešti, _s_ – dalintis, _c_ – išsaugoti).
  - Tikrinta, ar visos komandos veikia kaip numatyta ir ar perėjimai tarp skirtingų programos būsenų (pvz., pagrindinis vaizdas,
    pagalbos langas, navigacijos parinktys) yra logiški ir sklandūs.
  - Atkreiptas dėmesys į tai, ar naudotojas gali lengvai suprasti, kokioje būsenoje jis yra ir kokie veiksmai jam prieinami.

- Pagalbos sistemos efektyvumas
  - Tikrinta, ar pagalbos informacija, iškviečiama _h_ mygtuku, yra pasiekiama iš visų pagrindinių programos būsenų.
  - Vertinta, ar pateikiama pagalbos informacija yra aiški, tiksli ir atitinka esamą programos kontekstą, padedant
    naudotojui suprasti galimas komandas ir jų poveikį.

- Klaidų apdorojimas ir naudotojo informavimas
  - Imituotos įvairios klaidingos situacijos, pavyzdžiui, neteisingų komandų įvedimas, bandymas atlikti veiksmą netinkamoje
    būsenoje, ar išorinių API (pvz., „Mapillary“) pateiktos klaidos (naudojant imituotus atsakymus arba testuojant realias, bet retas klaidas).
  - Tikrinta, ar programoje rodomi klaidų pranešimai yra suprantami, informatyvūs ir ar jie padeda naudotojui ištaisyti klaidą arba tęsti darbą.
  - Ypatingas dėmesys skirtas tam, kad programa „neužstrigtų“ ir nesugriūtų dėl netikėtų įvykių ar klaidų, o leistų naudotojui grįžti
    į stabilią būseną arba tvarkingai užbaigti darbą.

- Vizualinio pateikimo nuoseklumas ir aiškumas
  - Vertintas ASCII meno (tiek pagrindinio gatvės vaizdo, tiek informacinių persidengiančių langų) atvaizdavimo aiškumas ir įskaitomumas.
  - Stebėta, ar nėra vizualinių artefaktų, tokių kaip neteisingas simbolių išsidėstymas, persidengimai ar ekrano „šiukšlės“ po ekrano perpiešimo.
  - Tikrintas programos pranešimų, meniu ir kitų tekstinių elementų pateikimo stiliaus nuoseklumas.

Šie TUI testavimo aspektai padėjo identifikuoti galimus naudojimo sunkumus, vizualinius netikslumus ir logines klaidas, leidžiant juos ištaisyti ir pagerinti bendrą programos kokybę bei naudotojo patirtį.
