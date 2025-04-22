#set text(lang: "lt", region: "lt")
== Techninis pasiūlymas

=== Sistemos apibrėžimas

Kuriama sistema yra specializuota komandinės eilutės (angl. _Command line interface_) aplikacija, skirta interaktyviam gatvės lygio panoraminių
vaizdų naršymui. Pagrindinė jos funkcija – gauti geografinės vietovės panoraminį vaizdą per išorinę paslaugą (konkrečiai,
planuojama naudoti „Mapillary“ API), apdoroti gautą vaizdinę medžiagą realiu laiku konvertuojant ją į tekstinį
ASCII formatą, ir atvaizduoti šį rezultatą tiesiogiai vartotojo terminalo lange.

Sistema neapsiribos vien statišku vaizdų rodymu. Ji suteiks vartotojui galimybę interaktyviai naviguoti po virtualią
erdvę: judėti pirmyn ir atgal numanoma kelio kryptimi. Ši navigacija bus valdoma per klaviatūros komandas, pritaikytas
specifinei CLI aplinkai. ASCII konvertavimo procesas bus optimizuotas siekiant ne tik greitaveikos, bet ir kuo aiškesnio
erdvinės informacijos bei objektų kontūrų perteikimo naudojant ribotą simbolių rinkinį.

Be pagrindinių naršymo funkcijų, į sistemą bus integruotas žaidybinis elementas. Programa turės žaidimo režimą funkcionalumu
primenantį populiarų internetinį žaidimą „Geoguessr“ (CCC https://www.geoguessr.com). Šio režimo tikslas – ne tik
pademonstruoti visas programos galimybes (judėjimą, sąveiką), bet ir padaryti pirmąją pažintį su įrankiu įdomesne bei intuityvesne.

Šiame projekte kuriama visa sistema nuo pradžios iki galo: nuo sąsajos su išoriniu API, vaizdų apdorojimo algoritmo,
ASCII atvaizdavimo logikos iki vartotojo sąsajos ir navigacijos valdymo komandinėje eilutėje. Sistema kuriama kaip
savarankiškas įrankis, nereikalaujantis papildomų grafinių bibliotekų ar aplinkų, išskyrus standartinį terminalą.

=== Bendras veiklos tikslas ir pagrįstumas

Pagrindinis šio projekto veiklos tikslas yra ištirti ir praplėsti komandinės eilutės sąsajos (CLI) taikymo ribas,
demonstruojant, kaip sudėtinga vizualinė ir geografinė informacija gali būti interaktyviai pateikiama ir valdoma
netradicinėje, tekstinėje aplinkoje. Siekiama ne tik įrodyti techninį tokios sistemos įgyvendinamumą, bet ir įvertinti
jos potencialų naudojimo patogumą bei praktiškumą specifinei vartotojų grupei – informacinių technologijų profesionalams
ir entuziastams, kurie dažnai dirba terminalo aplinkoje.

Numatoma nauda yra daugiausia nekomercinė:
- Technologinis eksperimentas ir ribų tyrimas: projektas praplės supratimą apie CLI galimybes ir ASCII meno potencialą
  atvaizduojant dinamišką vizualinę informaciją, aktyviai ieškant taškų, kur ši technologija pasiekia savo limitus.
- Žmogaus-kompiuterio sąveikos tyrimas: bus gauta įžvalgų apie vartotojo patirtį sąveikaujant su geografine informacija
  neįprastoje sąsajoje.
- Potencialus nišinis įrankis: sukurta programa, įskaitant jos žaidimo režimą, galėtų tapti įdomiu ir galbūt net naudingu
  įrankiu tiems, kas vertina galimybę greitai pasiekti informaciją ir pramogauti nepaliekant komandinės eilutės aplinkos.
- Idėjų generavimas: projektas gali paskatinti naujas idėjas apie alternatyvius duomenų vizualizavimo ir sąveikos būdus.

Nors tiesioginės komercinės naudos ar finansinio atsipirkimo iš šio projekto nėra tikimasi, jo sėkmingas įgyvendinimas
turės reikšmingą vertę kaip koncepcijos įrodymas (angl. _Proof of concept_). Šis projektas veiks kaip praktinis pavyzdys, paneigiantis
nusistovėjusias nuostatas, jog komandinė eilutė tinka tik paprastoms tekstinėms operacijoms ir griežtai struktūruotiems
duomenims. Šis projektas demonstruoja, kad net vizualiai sudėtinga ir interaktyvi užduotis, kaip realaus laiko gatvių vaizdų naršymas,
gali būti sėkmingai realizuota pasitelkiant ASCII reprezentaciją komandinės eilutės aplinkoje.

Toks precedentas turi potencialą įkvėpti platesnę kūrėjų ir technologijų entuziastų bendruomenę permąstyti komandinės
eilutės dizaino galimybes ir jos taikymo sritis. Tai gali pasireikšti įvairiai: nuo
interaktyvesnių duomenų analizės ir vizualizavimo įrankių kūrimo, vaizdingesnių ir informatyvesnių serverių ar procesų
stebėjimo sąsajų iki prieinamesnių alternatyvų vartotojams, dirbantiems riboto pralaidumo tinkluose ar naudojantiems
specializuotą įrangą. Galiausiai, šis projektas, nors ir nišinis, gali prisidėti prie subtilaus komandinės eilutės suvokimo
pokyčio – iš grynai utilitaraus, kartais bauginančio įrankio į lanksčią, galingą ir potencialiai labai kūrybišką platformą inovacijoms.

=== Egzistuojančių sprendimų analizė

Šiame skyriuje apžvelgiami egzistuojantys sprendimai, susiję su projekto tikslais. Analizė padalinta į dvi dalis: pirmojoje
nagrinėjami kiti vaizdų į ASCII meną konvertavimo įrankiai, kurie sudaro technologinį pagrindą vizualinės informacijos
pateikimui tekstinėje aplinkoje. Antrojoje dalyje bus analizuojami populiarios egzistuojančios programos, kurių alternatyvios
versijos buvo išleistos išskirtinai naudojant komandinės eilutės vartotojo sąsajas.

==== Nuotraukų konvertavimo į ASCII įrankiai

Vaizdo konvertavimas į ASCII meną yra nusistovėjusi technika, leidžianti apytiksliai atkurti vaizdinę informaciją naudojant
standartinius spausdinamus simbolius. Egzistuoja įvairių įgyvendinimų, kurie skiriasi prieinamumu, lankstumu ir pritaikymo sritimis.

Internetiniai konvertavimo įrankiai - tai labiausiai paplitę ir vartotojui draugiškiausi įrankiai, skirti greitam ir paprastam
vienkartiniam vaizdų konvertavimui. Jie nereikalauja jokios techninės konfigūracijos ar diegimo, sugeneruotą rezultatą
naudotojas gali nusikopijuoti į iškarpinę. Šių įrankių pavyzdžiai:

- „Ascii-art-generator.org“ (CCC https://www.ascii-art-generator.org/): Ši svetainė yra tipiškas pavyzdys, leidžiantis vartotojui įkelti
  paveikslėlį (pvz., JPG, PNG, GIF) arba pateikti jo URL. Vartotojas gali pasirinkti keletą pagrindinių parametrų:
    - Išvesties dydis: nurodomas pasirenkant norimą rezultato plotį, kas lemia detalumo lygį.
    - Simbolių rinkinys: nėra simbolių rinkinio pasirinkimo.
    - Algoritmai: nėra algoritmų pasirinkimo galimybės.
    - Spalvos: įrankis palaiko spalvoto ir monochromatinio ASCII generavimą, naudojant HTML spalvas fone ar pačius simbolius.
    - Taikymas: tinka greitam vizualiniam efektui gauti, socialinių tinklų įrašams ar kaip pramoga.
- „Asciiart.eu“ (CCC https://www.asciiart.eu/image-to-ascii): Veikia panašiai kaip ankstesnis pavyzdys, tačiau šįkart daug
  dėmesio sutelkiama į rezultato sudedamųjų dalių modifikavimą. Įkėlus vaizdą puslapis leidžia eksperimentuoti su plačiu nustatymų pasirinkimu.
    - Išvesties dydis: nurodomas pasirenkant norimą rezultato plotį, kas lemia detalumo lygį.
    - Simbolių rinkinys: platus simbolių aibių pasirinkimas.
    - Algoritmai: puslapis leidžia pasirinkti spalvų maišymo ir kraštų atpažinimo algoritmus.
    - Spalvos: platus spalvų reprezentavimo nustatymai, leidžiantys keisti kontrastą, atspalvį, invertuoti spalvas.
    - Taikymas: paprastas įrankis atliekantis ASCII konvertaciją, tačiau pažengusiems naudotojams suteikiama didelė konfigūravimo laisvė.
- „Manytools.org“ (CCC https://manytools.org/hacker-tools/convert-images-to-ascii-art/): Šis įrankis dažnai siūlo šiek tiek daugiau techninių parinkčių nei kiti internetiniai konverteriai:
    - Išvesties dydis: nurodomas pasirenkant norimą rezultato plotį, kas lemia detalumo lygį.
    - Simbolių rinkinys: nėra simbolių rinkinio pasirinkimo.
    - Algoritmai: puslapis leidžia pasirinkti spalvų maišymo ir kraštų atpažinimo algoritmus.
    - Spalvos: svarbi funkcija – galimybė generuoti ne tik vienspalvį, bet ir spalvotą ASCII meną, naudojant ANSI valdymo kodus (angl. _ANSII escape codes_), kurie leidžia atvaizduoti spalvas standartiniuose terminaluose.
    - Taikymas: paprastas įrankis atliekantis ASCII konvertaciją, pasižymintis minimaliomis konfigūravimo galimybėmis

Komandinės eilutės konvertavimo įrankiai: Šie įrankiai yra sukurti veikti tiesiogiai terminalo aplinkoje, todėl yra žymiai
lankstesni ir tinkamesni automatizavimui bei integracijai į kitas programas. Šie įrankiai lengvai įdiegiami per paketų
tvarkykles. Šių įrankių pavyzdžiai:

- „jp2a“: Vienas iš senesnių ir plačiai žinomų CLI įrankių, parašytas C kalba.

Funkcionalumas: Specializuojasi JPEG konvertavime (nors dažnai palaiko ir kitus formatus per išorines bibliotekas, pvz., libpng). Konvertuoja vaizdą į ASCII simbolius, atsižvelgdamas į pikselių šviesumą.

Parinktys: Leidžia nurodyti išvesties plotį (--width=), aukštį (--height=), naudoti ANSI spalvas (--color), pasirinkti kraštinių išryškinimo algoritmus (--border), invertuoti išvestį (--invert).

Taikymas: Greitas vaizdų peržiūrėjimas terminale, skriptų dalis, sistemų stebėjimo įrankių papildymas (pvz., rodant logotipo ASCII versiją).

Trūkumai projekto kontekste: Sukurtas konvertuoti pavienius failus. Nors teoriškai galima nukreipti vaizdo srautą (pvz., iš kameros per ffmpeg), jis nėra optimizuotas realaus laiko interaktyviam atvaizdavimui. Interaktyvumo funkcijos (pvz., priartinimas, slinkimas per ASCII vaizdą) turėtų būti implementuotos išorinėmis priemonėmis.

Šaltinis (Debian paketo aprašas): https://packages.debian.org/stable/graphics/jp2a

Šaltinis (GitHub veidrodis/fork'as): https://github.com/cslarsen/jp2a

libcaca (ir jos įrankis img2txt): Tai ne tik įrankis, bet ir galinga C biblioteka, skirta pažangiam tekstiniam vaizdavimui.

Funkcionalumas: libcaca eina toliau nei paprastas ASCII konvertavimas. Ji palaiko ne tik ASCII/ANSI, bet ir Unicode simbolius, įvairius ditheringo (spalvų maišymo) algoritmus, kad pagerintų vaizdo kokybę ribotoje spalvų paletėje. Ji gali netgi "leisti" vaizdo įrašus (pvz., per MPlayer su caca išvesties tvarkykle). img2txt yra įrankis, kuris naudoja libcaca biblioteką failų konvertavimui.

Parinktys (img2txt): Leidžia pasirinkti šriftą, ditheringo algoritmą, spalvų režimą, išvesties formatą (ANSI, HTML ir kt.).

Taikymas: Aukštesnės kokybės spalvoto ASCII meno generavimas, vaizdo įrašų peržiūra terminale (su MPlayer/FFplay), demonstracinės programos.

Trūkumai projekto kontekste: Pati libcaca biblioteka yra labai galinga, bet jos standartinis img2txt įrankis vis dar orientuotas į failų konvertavimą. Nors biblioteka suteiktų reikiamus primityvus interaktyvumui, jį reikėtų programuoti papildomai. Realizuoti sudėtingą interaktyvią sąsają (kaip gatvių vaizdų naršymas) vien libcaca pagalba būtų nemenkas iššūkis.

Šaltinis (Oficiali svetainė): http://caca.zoy.org/wiki/libcaca

ascii_magic (Python): Modernesnis sprendimas, parašytas Python kalba, kas palengvina integraciją į Python projektus.

Funkcionalumas: Veikia kaip Python biblioteka ir kaip CLI įrankis. Leidžia konvertuoti vaizdus iš failų, URL adresų ar tiesiogiai iš Pillow objektų. Palaiko spalvotą ANSI išvestį.

Parinktys: Galima nurodyti išvesties stulpelių skaičių, simbolių rinkinį (chars=), spalvų režimą (mode=).

Taikymas: Lengvai integruojamas į Python skriptus ir programas, greitas prototipavimas, automatizuotos užduotys.

Trūkumai projekto kontekste: Kaip ir kiti CLI įrankiai, pats savaime nesuteikia interaktyvios sąsajos. Tai labiau statinio konvertavimo įrankis/biblioteka. Interaktyvumas (naršymas, žaidimas) reikalautų papildomos logikos, naudojant šią biblioteką kaip vieną iš komponentų.

Šaltinis (PyPI): https://pypi.org/project/ascii-magic/
