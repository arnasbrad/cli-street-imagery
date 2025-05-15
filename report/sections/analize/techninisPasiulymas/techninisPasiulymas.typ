#set text(lang: "lt", region: "lt")
== Techninis pasiūlymas

_Rašė: Ignas Survila._

=== Sistemos apibrėžimas

Kuriama sistema yra specializuota komandinės eilutės (angl. _Command line interface_) programa, skirta interaktyviam gatvės lygio panoraminių
vaizdų naršymui. Pagrindinė jos funkcija – gauti geografinės vietovės panoraminį vaizdą per išorinę paslaugą (konkrečiai,
planuojama naudoti „Mapillary“ API), apdoroti gautą vaizdinę medžiagą realiu laiku konvertuojant ją į tekstinį
ASCII formatą, ir atvaizduoti šį rezultatą tiesiogiai naudotojo terminalo lange.

Sistema neapsiribos vien statišku vaizdų rodymu. Ji suteiks naudotojui galimybę interaktyviai naviguoti po virtualią
erdvę -- judėti pirmyn ir atgal numanoma kelio kryptimi. Ši navigacija bus valdoma per klaviatūros komandas, pritaikytas
specifinei CLI aplinkai. ASCII konvertavimo procesas bus optimizuotas siekiant ne tik greitaveikos, bet ir kuo aiškesnio
erdvinės informacijos bei objektų kontūrų perteikimo naudojant ribotą simbolių rinkinį.

Be pagrindinių naršymo funkcijų, į sistemą bus integruotas žaidybinis elementas. Programa turės žaidimo režimą funkcionalumu
primenantį populiarų internetinį žaidimą „Geoguessr“ @geoguessr. Šio režimo tikslas – ne tik
pademonstruoti visas programos galimybes (judėjimą, sąveiką), bet ir padaryti pirmąją pažintį su įrankiu įdomesne bei intuityvesne.

Šiame projekte kuriama visa sistema nuo pradžios iki galo -- nuo sąsajos su išoriniu API, vaizdų apdorojimo algoritmo,
ASCII atvaizdavimo logikos iki naudotojo sąsajos ir navigacijos valdymo komandinėje eilutėje. Sistema kuriama kaip
savarankiškas įrankis, nereikalaujantis papildomų grafinių bibliotekų ar aplinkų, išskyrus standartinį terminalą.

=== Bendras veiklos tikslas ir pagrįstumas

Pagrindinis šio projekto veiklos tikslas yra ištirti ir praplėsti komandinės eilutės sąsajos (CLI) taikymo ribas,
demonstruojant, kaip sudėtinga vizualinė ir geografinė informacija gali būti interaktyviai pateikiama ir valdoma
netradicinėje, tekstinėje aplinkoje. Siekiama ne tik įrodyti techninį tokios sistemos įgyvendinamumą, bet ir įvertinti
jos potencialų naudojimo patogumą bei praktiškumą specifinei naudotojų grupei – informacinių technologijų profesionalams
ir entuziastams, kurie dažnai dirba terminalo aplinkoje.

Numatoma nauda yra daugiausia nekomercinė:
- Technologinis eksperimentas ir ribų tyrimas -- projektas praplės supratimą apie CLI galimybes ir ASCII meno potencialą
  atvaizduojant dinamišką vizualinę informaciją, aktyviai ieškant taškų, kur ši technologija pasiekia savo limitus.
- Žmogaus-kompiuterio sąveikos tyrimas -- bus gauta įžvalgų apie naudotojo patirtį sąveikaujant su geografine informacija
  neįprastoje sąsajoje.
- Potencialus nišinis įrankis -- sukurta programa, įskaitant jos žaidimo režimą, galėtų tapti įdomiu ir galbūt net naudingu
  įrankiu tiems, kas vertina galimybę greitai pasiekti informaciją ir pramogauti nepaliekant komandinės eilutės aplinkos.
- Idėjų generavimas -- projektas gali paskatinti naujas idėjas apie alternatyvius duomenų vizualizavimo ir sąveikos būdus.

Nors tiesioginės komercinės naudos ar finansinio atsipirkimo iš šio projekto nėra tikimasi, jo sėkmingas įgyvendinimas
turės reikšmingą vertę kaip koncepcijos įrodymas (angl. _Proof of concept_). Šis projektas veiks kaip praktinis pavyzdys, paneigiantis
nusistovėjusias nuostatas, jog komandinė eilutė tinka tik paprastoms tekstinėms operacijoms ir griežtai struktūruotiems
duomenims. Šis projektas demonstruoja, kad net vizualiai sudėtinga ir interaktyvi užduotis, kaip realaus laiko gatvių vaizdų naršymas,
gali būti sėkmingai realizuota pasitelkiant ASCII reprezentaciją komandinės eilutės aplinkoje.

Toks precedentas turi potencialą įkvėpti platesnę kūrėjų ir technologijų entuziastų bendruomenę permąstyti komandinės
eilutės dizaino galimybes ir jos taikymo sritis. Tai gali pasireikšti įvairiai: nuo
interaktyvesnių duomenų analizės ir vizualizavimo įrankių kūrimo, vaizdingesnių ir informatyvesnių serverių ar procesų
stebėjimo sąsajų iki prieinamesnių alternatyvų naudotojams, dirbantiems riboto pralaidumo tinkluose ar naudojantiems
specializuotą įrangą. Galiausiai, šis projektas, nors ir nišinis, gali prisidėti prie subtilaus komandinės eilutės suvokimo
pokyčio – iš grynai utilitaraus, kartais bauginančio įrankio į lanksčią, galingą ir potencialiai labai kūrybišką platformą inovacijoms.

=== Egzistuojančių sprendimų analizė

Šiame skyriuje apžvelgiami egzistuojantys sprendimai, susiję su projekto tikslais. Analizė padalinta į dvi dalis: pirmojoje
nagrinėjami kiti vaizdų į ASCII meną konvertavimo įrankiai, kurie sudaro technologinį pagrindą vizualinės informacijos
pateikimui tekstinėje aplinkoje. Antrojoje dalyje bus analizuojami populiarios egzistuojančios programos, kurių alternatyvios
versijos buvo išleistos išskirtinai naudojant komandinės eilutės naudotojo sąsajas.

*Nuotraukų konvertavimo į ASCII įrankiai*

Vaizdo konvertavimas į ASCII meną yra nusistovėjusi technika, leidžianti apytiksliai atkurti vaizdinę informaciją naudojant
standartinius spausdinamus simbolius. Egzistuoja įvairių įgyvendinimų, kurie skiriasi prieinamumu, lankstumu ir pritaikymo sritimis.

Internetiniai konvertavimo įrankiai -- tai labiausiai paplitę ir naudotojui draugiškiausi įrankiai, skirti greitam ir paprastam
vienkartiniam vaizdų konvertavimui. Jie nereikalauja jokios techninės konfigūracijos ar diegimo, sugeneruotą rezultatą
naudotojas gali nusikopijuoti į iškarpinę. Šių įrankių pavyzdžiai:

- „Ascii-art-generator.org“ @ascii-art-generator -- ši svetainė yra tipiškas pavyzdys, leidžiantis naudotojui įkelti
  paveikslėlį (pvz., JPG, PNG, GIF) arba pateikti jo URL. Naudotojas gali pasirinkti keletą pagrindinių parametrų:
    - Išvesties dydis -- nurodomas pasirenkant norimą rezultato plotį, kas lemia detalumo lygį.
    - Simbolių rinkinys -- nėra simbolių rinkinio pasirinkimo.
    - Algoritmai -- nėra algoritmų pasirinkimo galimybės.
    - Spalvos -- įrankis palaiko spalvoto ir monochromatinio ASCII generavimą, naudojant HTML spalvas fone ar pačius simbolius.
    - Taikymas -- tinka greitam vizualiniam efektui gauti, socialinių tinklų įrašams ar kaip pramoga.
- „Asciiart.eu“ @asciiart-eu-image-converter -- veikia panašiai kaip ankstesnis pavyzdys, tačiau šįkart daug
  dėmesio sutelkiama į rezultato sudedamųjų dalių modifikavimą. Įkėlus vaizdą puslapis leidžia eksperimentuoti su plačiu nustatymų pasirinkimu.
    - Išvesties dydis -- nurodomas pasirenkant norimą rezultato plotį, kas lemia detalumo lygį.
    - Simbolių rinkinys -- platus simbolių aibių pasirinkimas.
    - Algoritmai -- puslapis leidžia pasirinkti spalvų maišymo ir kraštų atpažinimo algoritmus.
    - Spalvos -- platus spalvų reprezentavimo nustatymai, leidžiantys keisti kontrastą, atspalvį, invertuoti spalvas.
    - Taikymas -- paprastas įrankis atliekantis ASCII konvertaciją, tačiau pažengusiems naudotojams suteikiama didelė konfigūravimo laisvė.
- „Manytools.org“ @manytools-ascii-converter -- šis įrankis dažnai siūlo šiek tiek daugiau techninių parinkčių nei kiti internetiniai konverteriai:
    - Išvesties dydis -- nurodomas pasirenkant norimą rezultato plotį, kas lemia detalumo lygį.
    - Simbolių rinkinys -- nėra simbolių rinkinio pasirinkimo.
    - Algoritmai -- puslapis leidžia pasirinkti spalvų maišymo ir kraštų atpažinimo algoritmus.
    - Spalvos -- svarbi funkcija – galimybė generuoti ne tik vienspalvį, bet ir spalvotą ASCII meną, naudojant ANSI valdymo
      kodus (angl. _ANSII escape codes_), kurie leidžia atvaizduoti spalvas standartiniuose terminaluose.
    - Taikymas -- paprastas įrankis atliekantis ASCII konvertaciją, pasižymintis minimaliomis konfigūravimo galimybėmis

Komandinės eilutės konvertavimo įrankiai -- šie įrankiai yra sukurti veikti tiesiogiai terminalo aplinkoje, todėl yra žymiai
lankstesni ir tinkamesni automatizavimui bei integracijai į kitas programas. Šie įrankiai lengvai įdiegiami per paketų
tvarkykles. Šių įrankių pavyzdžiai:

- „jp2a“ -- vienas iš senesnių ir plačiai žinomų CLI įrankių, parašytas C kalba @jp2a.
    - Funkcionalumas -- specializuojasi JPEG konvertavime, nors dažnai palaiko ir kitus formatus per išorines bibliotekas,
      pavyzdžiui, „libpng“. Konvertuoja vaizdą į ASCII simbolius, atsižvelgdamas į pikselių šviesumą.
    - Parinktys -- leidžia nurodyti išvesties plotį, aukštį, naudoti ANSI spalvas, pasirinkti kraštinių išryškinimo algoritmus, invertuoti išvestį.
    - Taikymas -- greitas vaizdų peržiūrėjimas terminale, sistemų stebėjimo įrankių papildymas, pavyzdžiui, rodant logotipo ASCII versiją.
    - Trūkumai projekto kontekste -- sukurtas konvertuoti pavienius failus. Nors teoriškai galima nukreipti vaizdo srautą,
      jis nėra optimizuotas realaus laiko interaktyviam atvaizdavimui.
- „libcaca“ -- tai ne tik įrankis, bet ir galinga C biblioteka, skirta pažangiam tekstiniam vaizdavimui @libcaca.
    - Funkcionalumas -- šis įrankis daro daugiau nei paprastas ASCII konvertavimas. Jis palaiko ne tik ASCII ar ANSI, bet
      ir „Unicode“ simbolius, įvairius spalvų maišymo algoritmus, kad pagerintų vaizdo kokybę ribotoje spalvų paletėje.
      Yra galimybė vaizdo įrašams pritaikyti ASCII simbolių filtrą.
    - Parinktys -- leidžia pasirinkti šriftą, spalvų maišymo algoritmą, spalvų režimą, išvesties formatą (ANSI, HTML ir kt.).
    - Taikymas -- aukštesnės kokybės spalvoto ASCII meno generavimas, vaizdo įrašų peržiūra terminale, demonstracinės programos.
    - Trūkumai projekto kontekste -- pati biblioteka yra labai galinga, bet ji yra orientuotas į failų konvertavimą.
      Nors biblioteka suteiktų reikiamus primityvus interaktyvumui, jį reikėtų programuoti papildomai. Realizuoti sudėtingą
      interaktyvią sąsają (kaip gatvių vaizdų naršymas) vien „libcaca“ pagalba būtų nemenkas iššūkis.
- „ascii\_magic“ -- modernesnis sprendimas, parašytas „Python“ kalba, lengvai integruojamas į „Python“ projektus @ascii-magic.
    - Funkcionalumas -- veikia kaip „Python“ biblioteka ir kaip CLI įrankis. Leidžia konvertuoti vaizdus iš failų, URL adresų.
      Palaiko spalvotą ANSI išvestį.
    - Parinktys -- galima nurodyti išvesties stulpelių skaičių, simbolių rinkinį, spalvų režimą.
    - Taikymas -- lengvai integruojamas į „Python“ programas, greitas prototipavimas, automatizuotos užduotys.
    - Trūkumai projekto kontekste -- kaip ir kiti CLI įrankiai, pats savaime nesuteikia interaktyvios sąsajos. Tai labiau
      statinio konvertavimo biblioteka. Interaktyvumas (naršymas, žaidimas) reikalautų papildomos logikos, naudojant šią
      biblioteką kaip vieną iš komponentų.

Atlikta egzistuojančių vaizdo į ASCII konvertavimo sprendimų analizė rodo, kad technologija yra gerai išvystyta ir prieinama
įvairiomis formomis – nuo paprastų internetinių įrankių iki galingų programavimo bibliotekų. Internetiniai įrankiai yra
patogūs vienkartiniams konvertavimams, tačiau visiškai netinka šio projekto tikslams dėl savo statinio pobūdžio,
interaktyvumo stokos ir neįmanomos integracijos į CLI darbo eigas. Tuo tarpu komandinės eilutės įrankiai yra žingsnis
arčiau, nes veikia terminale ir gali būti automatizuojami. Jie demonstruoja potencialą vaizdinei informacijai pateikti
komandinėje eilutėje, įskaitant spalvotą ANSI meną. Tačiau jie vis dar yra orientuoti į statinių failų konvertavimą.
Jų panaudojimas projekte reikalautų papildomų įrankių interaktyvumui valdyti. Vis dėlto, nė vienas iš analizuotų sprendimų
tiesiogiai nesiūlo pilnai integruotos sistemos, kuri leistų interaktyviai naršyti gatvių vaizdus vien tik komandinės
eilutės sąsajoje, naudojant ASCII reprezentaciją. Egzistuojantys įrankiai sprendžia tik vaizdo konvertavimo problemą,
bet ne interaktyvios, dinamiškos, į gatvės vaizdo reprezentavimą orientuotos komandinės eilutės programos kūrimo iššūkį.
Šis projektas siekia užpildyti šią nišą, sujungdamas ASCII vizualizavimo technikas su interaktyviu valdymu ir specifiniu
geografiniu turiniu, taip praplečiant suvokimą apie komandinės eilutės galimybes.

*Programos naudojančios komandinės eilutės sąsają*

Pirmojoje dalyje išnagrinėjus specifinius vaizdo konvertavimo į ASCII meną įrankius, antrojoje dalyje dėmesys krypsta į
platesnį kontekstą – egzistuojančias komandinės eilutės (angl. _Command-line interface_) alternatyvas plačiai naudojamoms paslaugoms,
kurios tradiciškai pasiekiamos per grafines naudotojo sąsajas (angl. _Graphical user interfaces_) arba interneto naršykles.
Šios analizės tikslas – įvertinti, kaip sudėtingos, interaktyvios ir dažnai vizualiai turtingos paslaugos adaptuojamos
ribotai, tekstinei komandinės eilutės aplinkai, kokie yra tokių sprendimų privalumai, trūkumai ir pritaikymo sritys.
Tai padės geriau suprasti šio projekto (interaktyvaus gatvių vaizdų naršymo komandinėje eilutėje) potencialą ir iššūkius,
lyginant jį su jau egzistuojančiais komandinės eilutės sąsajų principais. Analizei pasirinkti du gerai žinomi pavyzdžiai:
el. pašto paslauga (konkrečiai „Gmail“) ir muzikos transliavimo platforma („Spotify“).

„Gmail“, kaip ir dauguma modernių el. pašto paslaugų, pirmiausia yra pasiekiama per naršyklės sąsają arba specializuotas
grafines programas („Outlook“, „Thunderbird“, mobiliąsias programėles). Šios sąsajos siūlo vizualiai patrauklų laiškų
atvaizdavimą, lengvą priedų valdymą, WYSIWYG redaktorius ir integruotas kalendoriaus bei kontaktų funkcijas. Tačiau
egzistuoja ir komandinės eilutės alternatyvos, skirtos el. pašto valdymui tiesiogiai iš terminalo:
- „mutt“ -- klasikinis, itin konfigūruojama komandinės eilutės el. pašto klientinė programa, dažnai naudojama su „Gmail“ per IMAP ar
  SMTP protokolus. Nors senas, jis vis dar populiarus tarp programuotojų ir sistemų administratorių dėl savo efektyvumo ir lankstumo.
- „himalaya“ -- modernus, Rust kalba parašyta el. pašto klientinė programa, palaikanti „Gmail“ ir kitas IMAP paslaugas,
  galinti pasiūlyti patogesnę naudotojo patirtį nei tradiciniai įrankiai.
- „lieer“ -- įrankis, skirtas „Gmail“ sinchronizavimui ir darbui neprisijungus, integruojamas su kitais komandinės eilutės įrankiais.

„Spotify“ paslauga yra neatsiejama nuo vizualiai turtingos grafinės sąsajos – albumų viršeliai, atlikėjų nuotraukos,
kuruojami grojaraščiai su paveikslėliais, dinamiškos rekomendacijos. Atrodytų, kad tokia paslauga sunkiai įsivaizduojama
tekstinėje aplinkoje, tačiau egzistuoja keletas populiarių tekstinės naudotojo sąsajos klientinių programų:
- „spotify-tui“ -- populiari klientinė programa, veikianti terminale bei siūlanti į grafinę panašią sąsają, kuri yra valdomą
  klaviatūra. Reikalauja oficialaus „Spotify“ demono (angl. _daemon_) veikimui fone.
- „ncspot“ -- panašus į „spotify-tui“, naudojantis „ncurses“ biblioteką ir siūlantis grojaraščių naršymo, paieškos ir grojimo valdymo funkcijas.
- „spotifyd“ -- ne interaktyvus klientas, o demonas, leidžiantis transliuoti „Spotify“ muziką įrenginyje be oficialios
  grafinės programos, dažnai naudojamas kartu su paprastesniais komandinės eilutės valdymo įrankiais.

Palyginimas su grafinėmis „Gmail“ ir „Spotify“ sąsajomis:
- Naudotojo sąsaja ir patirtis -- šios klientinės programos naudoja tekstinę sąsają, valdomą klaviatūra. Tai reikalauja
  išmokti komandas ir klavišų kombinacijas, tačiau patyrusiems naudotojams leidžia dirbti labai greitai ir efektyviai.
  Trūksta vizualinio patrauklumo, sudėtinga atvaizduoti HTML formato turinį ar peržiūrėti įterptus paveikslėlius. Tuo tarpu grafinė
  naudotojo sąsaja siūlo intuityvią, pelės valdomą sąsają, lengvai suprantamą pradedantiesiems, ir pilną vizualinį turinio atvaizdavimą.
- Funkcionalumas -- pagrindinės funkcijos originalių programų funkcijos yra prieinamos komandinės eilutės alternatyvose.
  Tačiau pažangesnės funkcijos dažnai paremtos sudėtingomis grafinėmis sąsajomis gali būti neprieinamos arba sunkiau naudojamos.
- Našumas ir resursų naudojimas -- lyginant su grafinių sąsajų programomis, komandinės eilutės alternatyvos naudoja minimaliai
  sistemos resursų, veikia greitai net ir naudojant senesnes kompiuterių sistemas ar itin lėtą tinklo ryšį.
- Automatizavimas ir integracija -- programos lengvai integruojamos į scenarijus ir automatizuotas darbo eigas, pavyzdžiui,
  automatinis laiškų apdorojimas, pranešimai. Tai yra didelis privalumas programuotojams ir sistemos administratoriams.

Analizė rodo, kad net sudėtingos, į grafines varotojo sąsajas orientuotos paslaugos kaip „Gmail“ ir „Spotify“ gali būti
sėkmingai adaptuotos komandinei eilutei. Šios alternatyvos dažniausiai siūlo didesnį našumą, mažesnį resursų naudojimą,
geresnes automatizavimo galimybes ir klaviatūra paremtą naudojimą. Tačiau tai pasiekiama aukojant vizualinį patrauklumą,
intuityvumą pradedantiesiems naudotojams ir kartais dalį grafinės sąsajos siūlomo funkcionalumo, ypač susijusio su įvairiu
medijos turiniu ar sudėtingomis vizualinėmis sąveikomis. Šie pavyzdžiai yra svarbūs šio projekto kontekste, nes jie įrodo,
jog interaktyvios ir funkcionalios patirtys yra įmanomos komandinėje eilutėje net ir toms užduotims, kurios atrodo
neatsiejamos nuo grafinių sąsajų. Nors gatvių vaizdų naršymas yra itin vizuali užduotis, egzistuojantys komandinės eilutės
sprendimai rodo, kad tekstinė reprezentacija (šiuo atveju, ASCII menas) kartu su gerai apgalvota interaktyvia navigacija
gali sukurti veikiančią ir potencialiai naudingą alternatyvą grafinėmis sąsajomis pagrįstoms sistemoms, užpildant nišą
naudotojams, vertinantiems komandinės eilutės privalumus. Iššūkis lieka efektyviai perteikti vizualinę informaciją ir
sukurti intuityvią navigacijos sistemą tekstinėje aplinkoje.
