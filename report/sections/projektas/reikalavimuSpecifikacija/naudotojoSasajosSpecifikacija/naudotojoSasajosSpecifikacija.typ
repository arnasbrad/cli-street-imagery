=== Naudotojo sąsajos specifikacija

Šiame skyriuje pristatysime kuriamos komandinės eilutės programos vartotojo sąsajos prototipą. Prototipas parodo
pagrindines sistemos dalis: pagrindinį „Street View" vaizdą, „Config" nustatymų langą ir „Help" pagalbos skiltį. Šie
prototipai padeda perteikti esminę programos struktūrą, navigacijos elementus ir bendrą stilių, kurį matysime galutiniame
produkte. Prototipo dizainas yra pritaikytas komandinei eilutei: minimalistinis, tekstinis, su vienspalve spalvų gama.

Bendra langų struktūra:
- Viršutiniai skirtukai: ekrano viršuje yra trys pagrindiniai navigacijos žymekliai: „CONFIG“, „MAIN“ ir „HELP“, naudojantis
  jais bus galima naviguoti tarp skirtingų langų. Dabartinis langas vizualiai išsiskirs iš kitų.
- Lango turinys: didžiąją lango dalį užims pagrindinė lankomo lango funkcija, ar tai gatvės lygio atvaizdavimas, ar pagalbinė
  informacija su programos valdymu.
- Valdymo mygtukai: lango apačioje bus matomi aktualūs konkrečiam langui mygtukai. Tai gali būti įvesties langai, saugojimo,
  grįžimo atgal mygtukai ir t.t.

Programos prototipo langai:
- Pagrindinis langas „Street View“ (#ref(<main_page>))
  #figure(
    image("/images/main_page.png", width: 15cm),
    caption: [Pagrindinio „Street View“ lango prototipas.],
  ) <main_page>
  Paskirtis: tai pagrindinis programos langas, kuriame vartotojas matys ir sąveikaus su pagrindiniu programos funkcionalumu --
  navigacija gatvės lygio vaizduose. Per apačioje esantį įvesties langą naudotojas galės įvesti norimos vietos koordimates
  arba adresą. Taip pat apačioje bus rodomi galimi įvesties mygtukai leidžiantys naudotojui naviguoti.
- Konfigūracijos langas „Config“ (#ref(<config_page>))
  #figure(
    image("/images/config_page.png", width: 15cm),
    caption: [Konfigūracijos „Config“ lango prototipas.],
  ) <config_page>
  Paskirtis: šis langas, leidžia vartotojui keisti įvairius programos nustatymus. Čia bus galima konfigūruoti programą:
  įvesti API raktus, pasirinkti programos vizualinį stilių bei įjungti kitas programos funkcijas. Apačioje esantys
  mygtukai leis išsaugoti arba sugrąžinti konfigūraciją į pradinį tašką.
- Pagalbos langas „Help“ (#ref(<help_page>))
  #figure(
    image("/images/help_page.png", width: 15cm),
    caption: [Pagalbos „Help“ lango prototipas.],
  ) <help_page>
  Paskirtis: šis langas suteikia vartotojui informaciją apie programos naudojimą ir valdymą.

Programos stilius yra specifiškai pritaikytas naudojimui komandinėje eilutėje, kur svarbiau funkcionalumas ir našumas, o
ne moderni vizualinė estetika. Visi trys langų prototipai demonstruoja nuoseklų dizaino sprendimą:
- Minimalizmas: sąsaja yra labai paprasta, be grafinių perteklinių elementų. Tai užtikrina greitą veikimą ir aiškumą.
- Tekstinė sąsaja: didžioji dalis informacijos ir valdymo pateikiama tekstu ir standartiniais formų elementais.
- Vienspalvė schema: naudojamas didelio kontrasto tamsus režimas leidžia lengvai įžiūrėti visus programos elementus.
- Aiški struktūra ir navigacija: pagrindinės programos dalys lengvai pasiekiamos per nuolatinę viršutinę navigacijos juostą.

Šis programos vaizdas buvo suplanuotas įgyvendinti projekto pradžioje. Tačiau dėl terminalo naudotojo sąsajos apribojimų,
projekto eigo buvo pakankamai žymiai nukrypta nuo pradinės vizijos. Plačiau problemos, su kuriomis buvo susidurta aprašomos
skyriuje „Komandinės eilutės techniniai apriborimai“.
