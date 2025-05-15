== Testavimo planas

Šiame skyriuje apibrėžiamas programos testavimo planas, siekiant užtikrinti jos kokybę, funkcionalumą ir patogumą naudoti.
Testavimas bus atliekamas keliais lygiais, apimant vienetinius testus, naudotojo sąsajos testavimą ir našumo
bei suderinamumo patikrinimus.

=== Vienetiniai testai (angl. _unit tests_)

Pagrindinis vienetinių testų tikslas – patikrinti individualių programos komponentų ir funkcijų teisingumą
izoliuotai. Bus siekiama padengti svarbiausias programos dalis:

- Algoritmų testavimas -- bus tikrinamas kiekvieno ASCII meno generavimo algoritmo (pvz., šviesumo, Sobelio, Canny, Brailio) veikimas su
  įvairiais įvesties duomenimis, siekiant užtikrinti teisingą vaizdų konvertavimą ir kraštinių atpažinimą.
- JSON skaitymo (angl. _parsing_) testavimas -- bus tikrinamas atsako iš išorinių API
  („Mapillary“, „Imgur“, „TravelTime“) JSON duomenų formatų teisingas nuskaitymas ir interpretavimas,
  įskaitant ribinius atvejus ir galimas klaidas.
- Konfigūracijos failo apdorojimas -- bus testuojamas konfigūracijos failo nuskaitymas, reikšmių interpretavimas
  ir klaidų valdymas (pvz., trūkstamos reikšmės, neteisingas formatas).
- Pagalbinių funkcijų testavimas -- kitos svarbios loginės dalys, tokios kaip navigacijos logika,
  koordinačių apdorojimas, bus tikrinamos atskirais vienetiniais testais.

=== Naudotojo sąsajos (TUI) testavimas

Naudotojo sąsajos testavimas bus orientuotas į naudojimo patogumą ir intuityvumą komandinės eilutės aplinkoje:

- Intuityvumas ir valdymas -- bus tikrinama, ar programos valdymas klaviatūra yra aiškus, ar komandos yra lengvai
  įsimenamos ir ar naudotojas gali lengvai atlikti norimus veiksmus (pvz., naršyti, keisti nustatymus, gauti pagalbą).
- Pagalbos prieinamumas -- bus užtikrinama, kad pagalbos informacija (_h_ mygtukas) būtų visada pasiekiamamas iš bet kurios
  programos būsenos ir pateiktų aktualią informaciją apie galimus veiksmus.
- Klaidų valdymas ir pranešimai -- bus tikrinama, kaip sistema reaguoja į neteisingas naudotojo įvestis ar išorinių
  sistemų klaidas, ar pateikiami aiškūs ir informatyvūs klaidų pranešimai, neleidžiantys naudotojui „užstrigti“.
- Nuoseklumas -- bus tikrinamas sąsajos elementų ir veiksmų nuoseklumas visoje programoje.

=== Našumo testavimas

Našumo testavimo tikslas – užtikrinti, kad programa veiktų pakankamai greitai ir neapkrautų sistemos resursų nepagrįstai:

- Atsako laikas -- bus stebima, ar atskiri programos etapai (pvz., vaizdo gavimas iš API, ASCII meno generavimas, ekrano atnaujinimas)
  neužtrunka nepagrįstai ilgai, ypač lyginant su kitais etapais. Siekiama, kad vaizdo konvertavimas ir atvaizdavimas terminale
  būtų kuo spartesnis po duomenų gavimo.
- Resursų naudojimas -- bus stebimas atminties ir procesoriaus naudojimas programai veikiant, siekiant išvengti didelio
  resursų sunaudojimo.

=== Suderinamumo testavimas

Suderinamumo testavimas bus atliekamas siekiant užtikrinti programos veikimą skirtingose operacinėse sistemose ir terminalo aplinkose:

- Operacinės sistemos -- programa bus testuojama pagrindinėse operacinėse sistemose: „Windows“, „Linux“ (pagrinde „Debian“) ir „macOS“.
- Terminalų emuliatoriai -- bus tikrinamas programos veikimas populiariuose terminalų emuliatoriuose, ypač atkreipiant dėmesį į
  spalvų palaikymą ir specialių simbolių atvaizdavimą.

Šis testavimo planas padės identifikuoti ir ištaisyti galimas klaidas bei pagerinti bendrą programos kokybę ir naudotojo patirtį.

