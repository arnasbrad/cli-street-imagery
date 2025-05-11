#set text(lang: "lt", region: "lt")

== Vartotojo sąsajos ir navigacijos projektavimas<ui-navigacijos-projektavimas>

Sukūrus nuosavą TUI modulį, kitas svarbus etapas buvo suprojektuoti naudotojo sąsają (UI) ir sąveikos (UX) modelį,
kuris leistų intuityviai naršyti gatvių vaizdus ASCII formatu komandinės eilutės aplinkoje. Pagrindinis iššūkis –
suderinti poreikį pateikti kuo detalesnį vaizdą su būtinybe suteikti naudotojui valdymo įrankius ir grįžtamąjį ryšį,
visa tai darant tekstinėje aplinkoje be tradicinių grafinių elementų.

=== Pagrindiniai projektavimo principai

Projektuojant sąsają, vadovautasi keliais pagrindiniais principais:

1. Vaizdo Prioritetas: svarbiausias tikslas buvo maksimaliai išnaudoti terminalo lango plotą pačiam ASCII gatvės vaizdui.
  Dėl šios priežasties atsisakyta nuolat matomų sąsajos elementų (pvz., meniu juostų, būsenos eilučių), kurie atimtų
  vietą iš vaizdo.
2. Minimalizmas ir Paprastumas: valdymas turėjo būti kuo paprastesnis, naudojant nedidelį kiekį lengvai įsimenamų
  komandų (klavišų). Vengta sudėtingų komandų sekų ar daugiapakopių meniu.
3. Tiesioginė Sąveika: naudojant „JLine“ bibliotekos „raw“ režimą, siekta, kad sistema reaguotų į kiekvieną
  klavišo paspaudimą nedelsiant, suteikiant tiesioginės kontrolės pojūtį.
4. Kontekstinė Informacija: papildoma informacija (pvz., pagalba, navigacijos parinktys) turėjo būti pateikiama tik tada,
  kai jos reikia, laikinai uždengiant pagrindinį vaizdą, o ne būnant matomai nuolat.

=== Sąveikos modelis

Pasirinktas sąveikos modelis yra pagrįstas būsenomis (angl. _state-based_) ir valdomas vieno simbolio komandomis.
Pagrindinė būsena yra ASCII gatvės vaizdo rodymas. Vartotojui paspaudus tam tikrą klavišą, programa pereina į kitą
būseną arba atlieka veiksmą:

- Vaizdo rodymas (pagrindinė būsena): rodydamas vaizdą, programa laukia naudotojo įvesties.
- Veiksmo sužadinimas: klavišo paspaudimas (pvz., _n_, _h_, _g_, _q_) inicijuoja perėjimą.
- Informacijos pateikimas bei parinkčių rodymas: paspaudus pagalbos (_h_) ar navigacijos (_n_) klavišą, ekranas
  išvalomas, ir vietoje pagrindinio vaizdo laikinai parodoma tekstinė informacija arba galimų veiksmų sąrašas
  (pvz., navigacijos krypčių), sugeneruotas kaip ASCII tekstas. Programa pereina į laukimo būseną, kol naudotojas
  pasirenka vieną iš pateiktų parinkčių arba grįžta.
- Navigacija: pasirinkus navigacijos kryptį, inicijuojamas naujo vaizdo duomenų gavimas, po kurio vėl perpiešiamas
  pagrindinis vaizdas su nauja lokacija.
- Kiti Veiksmai: kitos komandos (pvz., ekrano perpiešimas _r_, dalinimasis _s_) atlieka atitinkamą veiksmą ir
  dažniausiai grįžta į pagrindinę vaizdo rodymo būseną.
- Išėjimas: paspaudus išėjimo klavišą (_q_), programa baigia darbą.

Šis modelis leidžia išlaikyti švarią pagrindinę sąsają (tik vaizdas) ir pateikti papildomas funkcijas pagal poreikį.

=== Vartotojo sąsajos elementai

Dėl pasirinkto minimalistinio požiūrio, sąsajos elementai yra labai paprasti:

- Pagrindinis ASCII Vaizdas: užima visą terminalo plotą, atvaizduojamas naudojant spalvotus ANSI valdymo kodus.
- Laikinos tekstinės persidengimo sritys (angl. _overlays_): pagalba, navigacijos parinktys, žaidimo klausimai ar
  kiti pranešimai yra dinamiškai generuojami kaip tekstas ir paverčiami į ASCII meną, laikinai pakeičiantys pagrindinį
  gatvės vaizdą. Tai leidžia pateikti informaciją nenaudojant nuolatinių UI valdiklių.

=== Navigacijos realizacija

Navigacija yra viena pagrindinių interaktyvių funkcijų. Ji realizuota taip:

1. Vartotojas inicijuoja navigacijos režimą paspausdamas tam skirtą klavišą (_n_).
2. Sistema, priklausomai nuo konfigūracijos ar aptiktų „Mapillary“ duomenų tipo, pateikia galimų judėjimo krypčių sąrašą
  (kaip tekstinį ASCII vaizdą).
3. Vartotojas pasirenka vieną iš krypčių paspausdamas atitinkamą klavišą (pvz., skaičių ar raidę).
4. Programa kreipiasi į „Mapillary“, gauna naujos vietos vaizdo duomenis ir perpiešia ekraną su nauju ASCII vaizdu.

=== Grįžtamasis ryšys naudotojui

Grįžtamasis ryšys tekstinėje sąsajoje yra ribotas, bet užtikrinamas keliais būdais:

- Ekrano pokyčiai: ekrano išvalymas ir naujo turinio (vaizdo ar tekstinės informacijos) atvaizdavimas aiškiai parodo,
  kad įvyko perėjimas tarp būsenų ar įvykdytas veiksmas.
- Tiesioginis atsakas: dėl „raw“ režimo, naudotojas mato greitą reakciją į klavišų paspaudimus
  (nors duomenų gavimas iš „Mapillary“ gali užtrukti).
- Klaidų pranešimai: įvykus klaidai (pvz., nepavykus gauti duomenų iš „API“), pateikiamas tekstinis klaidos pranešimas.

=== Išvada

Projektuojant šios ASCII „Street View“ aplikacijos naudotojo sąsają ir navigaciją, pagrindinis dėmesys skirtas
balansui tarp maksimalaus informatyvumo (detalaus ASCII vaizdo) ir naudojimo paprastumo komandinės eilutės aplinkoje.
Pasirinktas minimalistinis, būsenomis paremtas sąveikos modelis su laikinomis tekstinėmis persidengimo sritimis leido
įgyvendinti pagrindines naršymo funkcijas, neaukojant ekrano ploto pagrindiniam vaizdui. Nors toks sprendimas
reikalauja naudotojo adaptacijos prie neįprastos sąsajos, jis atspindi komandinės eilutės aplinkos specifiką ir galimybes.
