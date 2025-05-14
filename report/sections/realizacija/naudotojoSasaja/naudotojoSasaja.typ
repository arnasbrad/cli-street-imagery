#set text(lang: "lt", region: "lt")

== Naudotojo sąsajos ir navigacijos projektavimas<ui-navigacijos-projektavimas>

_Rašė: Arnas Bradauskas._

Sukūrus nuosavą TUI modulį, kitas svarbus etapas buvo suprojektuoti naudotojo sąsają (UI) ir sąveikos (UX) modelį,
kuris leistų intuityviai naršyti gatvių vaizdus ASCII formatu komandinės eilutės aplinkoje. Pagrindinis iššūkis –
suderinti poreikį pateikti kuo detalesnį vaizdą su būtinybe suteikti naudotojui valdymo įrankius ir grįžtamąjį ryšį,
visa tai darant tekstinėje aplinkoje be tradicinių grafinių elementų.

=== Pagrindiniai projektavimo principai

Projektuojant sąsają, vadovautasi keliais pagrindiniais principais:

1. Vaizdo prioritetas: svarbiausias tikslas buvo maksimaliai išnaudoti terminalo lango plotą pačiam ASCII gatvės vaizdui.
   Dėl šios priežasties atsisakyta nuolat matomų sąsajos elementų (pvz., meniu juostų, būsenos eilučių), kurie atimtų
   vietą iš vaizdo.
2. Minimalizmas ir paprastumas: valdymas turėjo būti kuo paprastesnis, naudojant nedidelį kiekį lengvai įsimenamų
   komandų (klavišų). Vengta sudėtingų komandų sekų ar daugiapakopių meniu.
3. Tiesioginė sąveika: naudojant „JLine“ bibliotekos „raw“ režimą, siekta, kad sistema reaguotų į kiekvieną
   klavišo paspaudimą nedelsiant, suteikiant tiesioginės kontrolės pojūtį.
4. Kontekstinė informacija: papildoma informacija (pvz., pagalba, navigacijos parinktys) turėjo būti pateikiama tik tada,
   kai jos reikia, laikinai uždengiant pagrindinį vaizdą, o ne būnant matomai nuolat.

=== Sąveikos modelis

Pasirinktas sąveikos modelis yra pagrįstas būsenomis (angl. _state-based_) ir valdomas vieno simbolio komandomis.
Pagrindinė būsena yra ASCII gatvės vaizdo rodymas. Naudotojui paspaudus tam tikrą klavišą, programa pereina į kitą
būseną arba atlieka veiksmą:

- Vaizdo rodymas (pagrindinė būsena): rodydamas vaizdą, programa laukia naudotojo įvesties.
- Veiksmo sužadinimas: klavišo paspaudimas (pvz., _n_, _h_, _g_, _q_) inicijuoja perėjimą.
- Informacijos pateikimas bei parinkčių rodymas: paspaudus pagalbos (_h_) ar navigacijos (_n_) klavišą, ekranas
  išvalomas, ir vietoje pagrindinio vaizdo laikinai parodoma tekstinė informacija arba galimų veiksmų sąrašas
  (pvz., navigacijos krypčių), sugeneruotas kaip ASCII tekstas. Programa pereina į laukimo būseną, kol naudotojas
  pasirenka vieną iš pateiktų parinkčių arba grįžta.
- Navigacija: pasirinkus navigacijos kryptį, inicijuojamas naujo vaizdo duomenų gavimas, po kurio vėl perpiešiamas
  pagrindinis vaizdas su nauja lokacija.
- Kiti veiksmai: kitos komandos (pvz., ekrano perpiešimas _r_, dalinimasis _s_) atlieka atitinkamą veiksmą ir
  dažniausiai grįžta į pagrindinę vaizdo rodymo būseną.
- Išėjimas: paspaudus išėjimo klavišą (_q_), programa baigia darbą.

Šis modelis leidžia išlaikyti švarią pagrindinę sąsają (tik vaizdas) ir pateikti papildomas funkcijas pagal poreikį.

=== Naudotojo sąsajos elementai

Dėl pasirinkto minimalistinio požiūrio, sąsajos elementai yra labai paprasti:

- Pagrindinis ASCII vaizdas: užima visą terminalo plotą, atvaizduojamas naudojant spalvotus ANSI valdymo kodus.
- Laikinos tekstinės persidengimo sritys (angl. _overlays_): pagalba, navigacijos parinktys, žaidimo klausimai ar
  kiti pranešimai yra dinamiškai generuojami kaip tekstas ir paverčiami į ASCII meną, laikinai pakeičiantys pagrindinį
  gatvės vaizdą. Tai leidžia pateikti informaciją nenaudojant nuolatinių UI valdiklių.

=== Navigacijos realizacija

Navigacija yra viena pagrindinių interaktyvių programos funkcijų, leidžianti naudotojui virtualiai
judėti po aplinką. Ji realizuota taip, kad suteiktų lankstumo priklausomai nuo turimų duomenų ir naudotojo
pageidavimų. Programa palaiko du pagrindinius navigacijos tipus, kuriuos galima pasirinkti konfigūracijos faile:
navigaciją pagal sekas (angl. _sequence-based_) ir navigaciją pagal atstumą (angl. _proximity-based_ arba _radius-based_).

Bendras navigacijos procesas vyksta taip:
1. Naudotojas inicijuoja navigacijos režimą paspausdamas tam skirtą klavišą (_n_).
2. Sistema, atsižvelgdama į konfigūracijoje nustatytą navigacijos tipą ir esamos „Mapillary“ nuotraukos
   metaduomenis, pateikia galimų judėjimo krypčių sąrašą. Šis sąrašas rodomas kaip
   tekstinis ASCII menas, persidengiantis su pagrindiniu vaizdu.
3. Naudotojas pasirenka vieną iš pateiktų krypčių paspausdamas atitinkamą klavišą (pvz., skaičių ar raidę, nurodytą sąraše).
4. Programa kreipiasi į „Mapillary“ API, prašydama naujos vietos vaizdo duomenų pagal pasirinktą kryptį
   (t.y., naują nuotraukos identifikatorių).
5. Gavus naujus duomenis, ekranas yra perpiešiamas su nauju ASCII vaizdu, atitinkančiu naująją lokaciją.

Detaliau apie kiekvieną navigacijos tipą ir jo realizaciją:

- Navigacija pagal sekas (angl. _Sequence-based navigation_):
    - Logika: šis navigacijos tipas remiasi „Mapillary“ nuotraukų sekomis. Seka – tai eilė nuotraukų, padarytų
      judant tam tikra trajektorija (pvz., važiuojant gatve). Jei dabartinė rodoma nuotrauka priklauso sekai,
      programa bando rasti ankstesnę ir vėlesnę nuotrauką toje pačioje sekoje.
    - Įgyvendinimas:
        1. Pirmiausia kreipiamasi į „Mapillary“ API, prašoma visų nuotraukų identifikatorių (ID), priklausančių
           dabartinės nuotraukos sekai.
        2. Gavus visų sekos nuotraukų ID sąrašą, nustatoma dabartinės nuotraukos pozicija šiame sąraše.
        3. Remiantis šia pozicija, identifikuojamas ankstesnis (jei yra) ir vėlesnis (jei yra) nuotraukos ID sekoje.
        4. Naudotojui pateikiamos parinktys judėti pirmyn (angl. _forwards_) arba atgal (angl. _backwards_) seka.
    - Privalumai: dažniausiai užtikrina sklandų ir logišką judėjimą ta pačia gatve ar keliu. Nuotraukos dydis
      naviguojant visuomet yra toks pats, dienos laikas taip pat pastovus.
    - Trūkumai: veikia tik tada, kai nuotrauka priklauso sekai -- kitu atveju nei pirmyn, nei atgal paeiti nebus galima.
      Neleidžia pasukti į šonines gatves ar tyrinėti aplinkos laisviau, jei nėra tiesioginės sekos jungties.

- Navigacija pagal atstumą (angl. _Proximity-based navigation_):
    - Logika: šis tipas leidžia naudotojui judėti į artimiausias aplinkines nuotraukas, nepriklausomai nuo sekų.
      Paieška atliekama tam tikru spinduliu aplink dabartinę naudotojo poziciją.
    - Įgyvendinimas:
        1. Kreipiamasi į „Mapillary“ API, pateikiant dabartinės nuotraukos koordinates ir paieškos spindulį.
        2. API grąžina sąrašą nuotraukų, esančių nurodytame spindulyje. Iš šio sąrašo pašalinama pati dabartinė
           nuotrauka, kad nebūtų siūloma judėti į tą pačią vietą.
        3. Kiekvienai likusiai aplinkinei nuotraukai, taikant Haversine @haversine-wiki formulę, apskaičiuojamas
           atstumas nuo dabartinės pozicijos.
        4. Nuotraukos surūšiuojamos pagal atstumą (nuo artimiausios iki tolimiausios).
        5. Paimamas ribotas kiekis (5) artimiausių nuotraukų.
        6. Naudotojui pateikiamas šių artimiausių lokacijų sąrašas, dažnai nurodant atstumą ir apytikslę kryptį.
           Kryptis apskaičiuojama lyginant dabartinį kompaso kampą su guoliu (angl. _bearing_) į tikslinę koordinatę.
    - Privalumai: suteikia daugiau laisvės tyrinėti aplinką, leidžia pereiti į šonines gatves ar kitus objektus,
      kurie nėra tiesiogiai sujungti seka.
    - Trūkumai: kartais gali pasiūlyti judėjimą, kuris nėra logiškas kelio atžvilgiu (pvz., peršokti į kitą gatvės pusę
      ar į paralelinę gatvę). Rezultatų kokybė priklauso nuo „Mapillary“ padengimo tankumo toje vietoje. Galima gauti
      kitokio dydžio nuotrauką ar vaizdą kitokiu dienos laiku (pvz. iš dienos perėjimas į naktį).

Pasirinkus navigacijos kryptį, programa gauna naujosios nuotraukos identifikatorių ir inicijuoja naujo vaizdo
gavimo bei atvaizdavimo procesą, kaip aprašyta bendroje navigacijos eigoje.

Taip pat patogesniam demonstravimui sukūrėme greitesnį sekos navigacijos režimą, kuris kaskart nerodo
pasirinkimų judėti pirmyn ar atgal. Taip padarėme todėl, nes naviguojant kaskart matyti šiuos du pasirinkimus
yra nebūtina bei sunkiau matyti pasikeitimus tarp dabartinio ir praeito paveiksliuko.

=== Grįžtamasis ryšys naudotojui

Grįžtamasis ryšys tekstinėje sąsajoje yra ribotas, bet užtikrinamas keliais būdais:

- Ekrano pokyčiai: ekrano išvalymas ir naujo turinio (vaizdo ar tekstinės informacijos) atvaizdavimas aiškiai parodo,
  kad įvyko perėjimas tarp būsenų ar įvykdytas veiksmas.
- Tiesioginis atsakas: dėl „raw“ režimo, naudotojas mato greitą reakciją į klavišų paspaudimus
  (nors duomenų gavimas iš „Mapillary“ gali užtrukti).
- Klaidų pranešimai: įvykus klaidai (pvz., nepavykus gauti duomenų iš API), pateikiamas tekstinis klaidos pranešimas.

=== Išvada

Projektuojant šios ASCII „Street View“ programos naudotojo sąsają ir navigaciją, pagrindinis dėmesys skirtas
balansui tarp maksimalaus informatyvumo (detalaus ASCII vaizdo) ir naudojimo paprastumo komandinės eilutės aplinkoje.
Pasirinktas minimalistinis, būsenomis paremtas sąveikos modelis su laikinomis tekstinėmis persidengimo sritimis leido
įgyvendinti pagrindines naršymo funkcijas, neaukojant ekrano ploto pagrindiniam vaizdui. Nors toks sprendimas
reikalauja naudotojo adaptacijos prie neįprastos sąsajos, jis atspindi komandinės eilutės aplinkos specifiką ir galimybes.
