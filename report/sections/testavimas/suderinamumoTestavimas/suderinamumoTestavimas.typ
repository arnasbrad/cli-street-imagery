== Suderinamumo testavimas

Suderinamumo testavimo tikslas – užtikrinti, kad kuriama programa veiktų korektiškai ir nuosekliai skirtingose
operacinėse sistemose bei įvairiose terminalo aplinkose. Atsižvelgiant į projekto prigimtį, suderinamumo
testavimas buvo atliekamas rankiniu būdu, vykdant programą ir tikrinant jos pagrindines funkcijas šiose platformose:

- Operacinės sistemos:
  - „Linux“: programa buvo testuojama „Debian“ pagrindu veikiančioje distribucijoje.
  - „Windows“: testavimas atliktas standartinėje „Windows“ aplinkoje (pvz., „Windows 10“ / „Windows 11“ naudojant „Command Prompt“ arba „PowerShell“).
  - „macOS“: programa buvo testuojama „macOS“ operacinėje sistemoje.

- Terminalų emuliatoriai:
  - Nors specifiniai emuliatoriai nebuvo sistemingai dokumentuojami, testavimo metu naudoti standartiniai kiekvienos operacinės sistemos
    terminalai bei keli populiarūs alternatyvūs emuliatoriai (pvz., „Windows Terminal“, „iTerm2“ „macOS“ sistemai, „GNOME Terminal“ ir
    „kitty“ „Linux“ sistemai).
  - Pagrindinis dėmesys buvo skiriamas ANSI spalvų kodų palaikymui ir teisingam specialių simbolių (naudojamų ASCII mene) atvaizdavimui.

Testavimo rezultatai:

- Bendras funkcionalumas: pagrindinės programos funkcijos – vaizdų gavimas iš „Mapillary“ API, ASCII meno generavimas pagal
pasirinktus algoritmus, navigacija tarp vaizdų, pagalbos meniu veikimas, konfigūracijos failo nuskaitymas –
veikė stabiliai ir teisingai visose testuotose operacinėse sistemose. Nebuvo pastebėta kritinių klaidų ar programos „lūžių“,
susijusių su operacinės sistemos specifika.

- Spalvų palaikymo tikrinimas: testavimo metu išryškėjo vienintelis reikšmingesnis suderinamumo iššūkis – automatinis terminalo
spalvų palaikymo nustatymas. Skirtingos operacinės sistemos naudoja skirtingus aplinkos kintamuosius (angl. _environment variables_)
arba metodus terminalo savybėms nustatyti. Pavyzdžiui, kintamieji kaip _TERM_ ar _COLORTERM_ yra labiau paplitę „Linux“ ir „macOS“
aplinkose, tuo tarpu „Windows“ terminalai gali reikalauti kitokių patikrinimų. Dėl šios priežasties, nors programa stengiasi automatiškai
nustatyti spalvų palaikymą, galutinis spalvoto ASCII meno atvaizdavimas priklauso nuo konkretaus terminalo emuliatoriaus ir jo
konfigūracijos. Vartotojui suteikiama galimybė konfigūracijos faile rankiniu būdu įjungti arba išjungti spalvotą išvestį.

- Simbolių atvaizdavimas: specialūs simboliai, naudojami išplėstiniuose ASCII rinkiniuose ir Brailio rašte,
  buvo atvaizduojami korektiškai terminaluose, palaikančiuose UTF-8 koduotę (kas yra standartas daugumoje modernių terminalų).

Apibendrinant, suderinamumo testavimas parodė, kad programa yra gerai perkeliama tarp pagrindinių operacinių sistemų.
Automatinis spalvų palaikymo tikrinimas pasirodė nevertingas, dėl to buvo pasirinkta suteikti rankinį spalvų
įjungimo bei išjungimo valdymą per konfigūracijos failą naudotojui.
