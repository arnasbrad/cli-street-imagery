#align(center)[
  = Įvadas<ivadas>
]

_Rašė: 50% Arnas Bradauskas, 50% Ignas Survila._

== Darbo problematika ir aktualumas

// „“

Šiuolaikiniame technologijų pasaulyje gatvių vaizdai ir geografinė informacija paprastai pateikiami per grafines sąsajas,
tačiau komandinės eilutės (angl. _Command line interface_) aplinka išlieka svarbi daugeliui informacinių technologijų profesionalų
ir entuziastų. Šio darbo problematika kyla iš smalsumo ir noro ištirti naujas komandinės eilutės pritaikymo galimybes --
ar įmanoma sukurti interaktyvią sąsają gatvės lygio vaizdams, ir jei taip, ar tokia sąsaja gali būti intuityvi ir
patogi naudoti. Tai yra ne tik techninio įgyvendinamumo klausimas, bet ir žmogaus-kompiuterio sąveikos tyrimas
neįprastoje aplinkoje.

Projekto aktualumas pasireiškia kaip alternatyvių sąsajų tyrinėjimas ir kūrybinis eksperimentas, praplečiantis
komandinės eilutės galimybes. Tokia sąsaja galėtų būti įdomi programuotojams, sistemų administratoriams ir kitiems
specialistams, kurie daug laiko praleidžia terminalo aplinkoje ir vertina galimybę greitai pasiekti informaciją
nepaliekant šios aplinkos. Be to, projektas atskleidžia ASCII stiliaus meno potencialą perteikti sudėtingą vaizdinę
informaciją ir kelia klausimus apie tai, kaip skirtingos sąsajos formos veikia mūsų suvokimą ir sąveiką su
geografine informacija.

Projektas apima kompiuterių mokslų, žmogaus-kompiuterio sąveikos ir kūrybinių technologijų sritis. Praktinė darbo
reikšmė slypi ne tik galimame sukurto įrankio naudojime, bet ir naujų idėjų generavime apie tai, kaip vaizdinis
turinys gali būti pristatomas nestandartiniais būdais.

== Darbo tikslas ir uždaviniai

Darbo pagrindinis tikslas -- sukurti ir įvertinti komandinės eilutės pagrindu veikiantį prototipą,
leidžiantį interaktyviai naršyti gatvės lygio vaizdus (angl. street-level view) ASCII grafikos formatu.
Atliekant sistemingą algoritmų analizę, demonstracinio prototipo kūrimą bei naudotojo patirties vertinimą,
nustatyti tokios sąsajos techninius privalumus ir apribojimus, lyginant su įprasta grafine aplinka.

Uždaviniai:
+ Išanalizuoti algoritmus, skirtus vaizdiniam turiniui konvertuoti į ASCII formatą,
  bei įvertinti jų tinkamumą atvaizduoti gatvės lygio vaizdus.
+ Palyginti ir pasirinkti tinkamiausią „Street View“ API, įvertinti jos galimybes ir apribojimus.
+ Sukurti interaktyvų demonstracinį prototipą su žaidybiniais elementais, leidžiantį pademonstruoti sistemos galimybes.
+ Sukurti patogią navigaciją komandinėje eilutėje, leidžiančią sklandžiai pereiti tarp gatvės lygio vaizdų.
+ Įvertinti komandinės eilutės aplinkos apribojimus ir galimybes, bei aprašyti pagrindines tobulinimo kryptis.
+ Atlikti sukurtos sistemos testavimą, vertinant techninį veikimą, naudotojo sąsajos intuityvumą,
  greitaveiką bei veikimą skirtingose operacinėse sistemose.

== Darbo strukrūra

Dokumentas susideda iš penkių skyrių:

+ Pirmajame skyriuje „Analizė“ pateikiamas kuriamo „Street View“ tipo programos techninis pasiūlymas, apibrėžiantis sistemos architektūrą
  ir pagrindines funkcijas. Atliekama detali egzistuojančių sprendimų analizė, apžvelgiant tiek nuotraukų konvertavimo į ASCII meną įrankius,
  tiek kitas komandinės eilutės sąsajas naudojančias programas. Taip pat nagrinėjamos techninės galimybės ir apribojimai, susiję su „Street View“
  tipo duomenų prieiga ir terminalo aplinkos grafinėmis ypatybėmis.

+ Antrajame skyriuje „Projektas“ detaliai aprašoma reikalavimų specifikacija, pristatomi pasirinkti projektavimo metodai, argumentuojamas „Scala“
  programavimo kalbos ir funkcinio programavimo principų pasirinkimas. Skyrius užbaigiamas sistemos statiniu vaizdu, pateikiant diegimo ir paketų diagramas.

+ Trečiajame skyriuje „Realizacija“ gilinamasi į praktinius komandinės eilutės techninius apribojimus ir jų įtaką projekto sprendimams.
  Aptariamas gatvės vaizdo duomenų tiekėjo („Mapillary“) ir papildomų sąsajų („TravelTime“, „Imgur“) pasirinkimas bei integravimas.
  Detaliai nagrinėjamas naudotojo sąsajos bibliotekos pasirinkimo procesas, lėmęs nuosavo TUI modulio kūrimą. Ypatingas dėmesys skiriamas
  įvairių nuotraukų konvertavimo į ASCII meną algoritmų realizacijai bei ASCII spalvų pritaikymui.

+ Ketvirtajame skyriuje „Testavimas“ pristatomas parengtas testavimo planas, apimantis vienetinius testus, naudotojo sąsajos (TUI) testavimą,
  našumo ir suderinamumo patikrinimus. Nurodomi testavimo kriterijai ir apžvelgiami testuotų programos dalių rezultatai, įskaitant ASCII
  meno generavimo algoritmų našumo palyginimą ir programos resursų naudojimo analizę.

+ Penktajame skyriuje „Dokumentacija naudotojui“ pateikiamas apibendrintas sistemos galimybių aprašymas ir išsamus naudotojo vadovas.
  Jame paaiškinama programos konfigūracija, įskaitant API raktų ir kitų parametrų nustatymą, programos paleidimas skirtingais režimais,
  pagalbos sistemos naudojimas ir pagrindinės valdymo komandos naršant vaizdus.

Galiausiai pateiktose išvadose apibendrinami projekto metu gauti rezultatai, įvertinama,
kaip pavyko pasiekti iškeltus tikslus ir įgyvendinti uždavinius.
Pateikiamos pagrindinės įžvalgos ir rekomendacijos galimam tolimesniam darbo plėtojimui.

== Sistemos apimtis

Kodo eilučių skaičius -- 10391. Iš jų:
- 5276 _.scala_ -- pagrindinis programos kodas.
- 1000 _.sh_ ir _.bat_ -- konfigūraciniai scenarjai.
- 5115 _.scala_ testai -- programos vienetų testai.

