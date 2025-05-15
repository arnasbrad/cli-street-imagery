== Vienetų testavimas

=== Testuotos programos dalys

Vienetų testai yra esminė programinės įrangos kūrimo dalis, skirta patikrinti mažiausias logines programos dalis – atskiras
funkcijas ar metodus – izoliuotai nuo likusios sistemos. Pagrindinis tikslas yra anksti aptikti klaidas,
užtikrinti kodo korektiškumą ir palengvinti kodo priežiūrą bei refaktorizavimą ateityje.

Atsižvelgiant į šio projekto specifiką, vienetų testai buvo sutelkti į tas sritis, kurios teikia didžiausią
vertę ir gali būti efektyviai testuojamos izoliuotai.

- JSON duomenų apdorojimas (angl. _parsing_)
  - Buvo kruopščiai testuojamas JSON atsakymų iš išorinių API („Mapillary“, „Imgur“, „TravelTime“) konvertavimas į vidines
    programos duomenų struktūras.
  - Testai apėmė tiek sėkmingus scenarijus su pilnais ir teisingais duomenimis, tiek atvejus su trūkstamais laukais
    ar netikėtais duomenų formatais, siekiant užtikrinti patikimą klaidų valdymą.

- Konfigūracijos failo skaitymas
  - Testuotas _config.conf_ failo nuskaitymas, įskaitant teisingą visų parametrų interpretavimą pagal jų tipus
    (pvz., eilutės, skaičiai, loginės reikšmės, specifinės enumeracijos).
  - Patikrinti scenarijai, kai konfigūracijos faile trūksta privalomų parametrų arba kai parametrų
    reikšmės yra neteisingo formato, užtikrinant, kad programa elgtųsi nuspėjamai.

- ASCII meno generavimo algoritmų išvesties tikrinimas
  - ASCII meno generavimo algoritmai (pvz., šviesumo, Sobelio, Canny, Brailio) kelia unikalų iššūkį
    vienetų testavimui, nes jų „teisingumas“ dažnai yra subjektyvus ir pirmiausia vertinamas vizualiai.
  - Testavimo strategija
    - Etaloninio rezultato sukūrimas -- naudojant programą su konkrečiu, žinomu įvesties vaizdu, vizualiai
      patikrinamas ir atrenkamas algoritmiškai sugeneruotas ASCII rezultatas, kuris laikomas „geru“ arba „teisingu“.
    - Rezultato išsaugojimas -- šis etaloninis ASCII rezultatas (arba jo reprezentatyvi, lengvai patikrinama dalis)
      yra išsaugomas kaip testo duomenų dalis.
    - Automatinis palyginimas -- vienetų testas tada vykdo atitinkamą algoritmą su ta pačia pradine įvestimi ir
      lygina gautą ASCII išvestį su išsaugotu etaloniniu rezultatu. Sutapimas laikomas sėkmingu testu.
    - Ši strategija leidžia automatizuoti algoritmo veikimo patikrinimą po kodo pakeitimų, nors pirminis
      etalono nustatymas remiasi vizualiu vertinimu.
  - Papildomai, buvo testuojami algoritmai su ribiniais atvejais, pavyzdžiui, visiškai juodu ar baltu
    įvesties vaizdu, kur ASCII išvestis yra lengvai nuspėjama ir be vizualaus patvirtinimo.

- Pagalbinės funkcijos ir vidinė logika -- testuotos įvairios mažesnės, grynos funkcijos (angl. _pure functions_),
  atsakingos už duomenų transformacijas,
  koordinačių skaičiavimus, navigacijos logikos dalis (pvz., artimiausių taškų radimas, sekos elementų identifikavimas)
  ir kitus vidinius skaičiavimus.

=== Ribojimai ir netaikyti vienetų testai

Kai kurios programos dalys yra sudėtingos arba mažiau naudingos testuoti tradiciniais vienetų testais dėl jų
prigimties ir sąsajų su išorine aplinka.

- Tiesioginis terminalo išvedimas (TUI)
  - Naudotojo sąsajos elementų atvaizdavimas tiesiogiai terminale, įskaitant ASCII meno spausdinimą ir interaktyvius meniu,
    nėra tikrinamas vienetų testais. Tokio testavimo automatizavimas yra sudėtingas ir dažnai trapus.
  - Vietoj to, vienetų testais tikrinama logika, kuri paruošia duomenis atvaizdavimui (pvz., teisingų simbolių
    sekų ir spalvų ANSI kodų generavimas). Pats vizualinis pateikimas ir sąveika tikrinami rankinio testavimo
    ir naudotojo sąsajos testavimo metu.

- Tiesioginės API užklausos ir atsakymai
  - Vienetų testai neturėtų priklausyti nuo išorinių tinklo paslaugų (pvz., „Mapillary“ API serverių pasiekiamumo
    ar atsako laiko). Tokia priklausomybė padarytų testus nestabilius ir lėtus.
  - Todėl API klientų sąsajos (angl. _traits_) vienetų testuose yra imituojamos (angl. _mocked_). Testuojama, ar pagrindinė
    programos logika teisingai sąveikauja su šiomis imitacijomis – t.y., ar ji teisingai formuoja užklausas imitacijai
    ir ar teisingai apdoroja imituotus atsakymus (tiek sėkmingus, tiek klaidingus). Patys API klientai (jų realizacijos) ir jų
    reali sąveika su išorinėmis API yra labiau integracinių testų objektas, nors šio projekto rėmuose tokie testai
    nebuvo formaliai rašomi.

Šis selektyvus požiūris į vienetų testus leidžia sutelkti pastangas į programos branduolio logikos patikimumą,
tuo pačiu pripažįstant tam tikrų sąveikos su išorine aplinka aspektų testavimo sudėtingumą vienetų lygmenyje.
