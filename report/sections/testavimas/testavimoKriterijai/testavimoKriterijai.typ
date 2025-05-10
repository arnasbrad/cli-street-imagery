== Testavimo kriterijai

Šiame skyriuje apibrėžiami pagrindiniai kriterijai, kuriais bus vadovaujamasi vertinant programos testavimo rezultatus ir bendrą jos kokybę.
Sėkmingas testavimas bus laikomas tada, kai programa atitiks šiuos kriterijus:

- Funkcinis korektiškumas
  - Visi programos komponentai, ypač ASCII konvertavimo algoritmai ir JSON duomenų skaitymas, veikia teisingai pagal numatytą
    logiką ir pateikia tikslius rezultatus.
  - Navigacijos funkcijos (judėjimas pirmyn/atgal, tarp artimų taškų) veikia patikimai.
  - Spėliojimo žaidimo logika yra korektiška.
  - Pagalbos funkcija pateikia teisingą ir aktualią informaciją.
- Vartotojo sąsajos kokybė
  - TUI yra intuityvi ir lengvai perprantama tikslinei auditorijai (komandinės eilutės naudotojams).
  - Pagalbos informacija (_h_ mygtukas) yra visada pasiekiama ir aiški.
  - Naudotojas negali „užstrigti“ programoje -- visada yra aiškus kelias tęsti veiksmus arba išeiti.
  - Klaidų pranešimai yra informatyvūs ir padeda suprasti problemos priežastį.
- Našumas
  - ASCII meno generavimas ir ekrano atvaizdavimas po duomenų gavimo iš API įvyksta per priimtiną laiką
    (pvz., mažiau nei 1-2 sekundes modernioje techninėje įrangoje), nesukeliant naudotojui pastebimo diskomforto.
  - Bendras atsako laikas į naudotojo komandas (išskyrus API užklausas) yra greitas.
  - Programa nenaudoja neproporcingai daug sistemos resursų (CPU, atminties).
- Stabilumas ir patikimumas
  - Programa veikia stabiliai, be netikėtų „lūžių“ ar užstrigimų normaliomis naudojimo sąlygomis.
  - Programa korektiškai tvarko numatomas klaidas (pvz., API atsako klaidos, neteisinga įvestis, tinklo problemos).
- Suderinamumas
  - Programa sėkmingai veikia ir yra vizualiai korektiška pagrindinėse tikslinėse operacinėse sistemose („Windows“, „Linux“, „macOS“).
  - Programa tinkamai atvaizduoja ASCII meną ir spalvas populiariuose terminalų emuliatoriuose, palaikančiuose bent 256 spalvas.

Šie kriterijai padės sistemiškai įvertinti programos parengtį ir identifikuoti sritis, kurioms gali prireikti papildomo dėmesio ar tobulinimo.
