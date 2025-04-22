=== Projektavimo valdymas ir eiga

Atsižvelgiant į projekto tiriamąjį ir eksperimentinį pobūdį bei pradinį neapibrėžtumą dėl galutinio sprendimo
techninio įgyvendinamumo, nebuvo taikomas griežtas, iš anksto suplanuotas programinės įrangos kūrimo modelis,
pavyzdžiui, krioklio (angl. _waterfall_). Vietoj to, buvo pasirinktas lankstus, iteracinis ir inkrementinis
(angl. _iterative and incremental_) kūrimo procesas, turintis prototipavimo (angl. _prototyping_) ir eksperimentinio kūrimo
(angl. _exploratory development_) bruožų.

Projekto eiga buvo valdoma dinamiškai, reaguojant į kylančius iššūkius ir atradimus:

1. Pradinis tyrimas ir analizė: pirmiausia buvo atlikta esamų technologijų analizė
  (ASCII generavimo metodai, „Street View“ tipo sąsajų galimybės ir apribojimai), siekiant įvertinti bendrą
  idėjos įgyvendinamumą.
2. Komponentų identifikavimas: pagrindinės sistemos dalys (pvz., prieiga prie „Mapillary“, vaizdo konvertavimas į ASCII,
  terminalo vartotojo sąsajos (TUI) modulis, navigacijos logika) buvo identifikuotos kaip atskiri funkciniai blokai.
3. Iteracinis kūrimas ir integravimas:
  - Buvo kuriamos ir testuojamos nedidelės, atskiros funkcionalumo dalys (pvz., pirminis užklausų siuntimas, 
    bazinis ASCII generavimas).
  - Veikiantys komponentai buvo palaipsniui integruojami tarpusavyje.
  - Kiekvienos iteracijos pabaigoje buvo vertinamas rezultatas, sprendžiami iškilę techniniai sunkumai
    (pvz., „Mapillary“ patikimumo problemos, terminalo spalvų palaikymo iššūkiai).
4. Adaptacija ir krypties koregavimas: remiantis iteracijų rezultatais ir techninių galimybių analize,
  buvo priimami sprendimai dėl tolimesnės eigos. Pavyzdžiui, paaiškėjus standartinių TUI bibliotekų apribojimams,
  buvo nuspręsta kurti nuosavą TUI komponentą. Susidūrus su „Google Street View“ „API“ kainodaros kliūtimis, 
  buvo pereita prie „Mapillary“.
5. Funkcionalumo plėtra: įsitikinus pagrindinių dalių veikimu, buvo pridedamos papildomos funkcijos 
  (pvz., navigacijos patobulinimai, žaidybinis elementas).

Šis lankstus požiūris leido nuolat tikrinti technines hipotezes ir prisitaikyti prie realių apribojimų, kas buvo
būtina tokio pobūdžio eksperimentiniam projektui. Darbai nebuvo skirstomi pagal griežtą grafiką,
o prioritetai buvo nustatomi pagal einamųjų iteracijų poreikius ir techninę būtinybę.
