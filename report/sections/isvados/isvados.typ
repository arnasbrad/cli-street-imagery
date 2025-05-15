#align(center)[
  = Išvados
]

_Rašė: 50% Arnas Bradauskas, 50% Ignas Survila._

Atlikus projektinį darbą padarėme išvadas:
1.  Sukurtas interaktyvus demonstracinis prototipas su žaidybiniais elementais padėjo parodyti pagrindinį kuriamo
    įrankio funkcionalumą.
2.  Komandinės eilutės aplinka sėkmingai atvaizduoja gatvės lygio vaizdus ASCII formatu, su keliais apribolimais.
    Vienodo šrifto dydžio apribojimas verčia tekstą atvaizduoti tap pat kaip ASCII meną, ko pasėkmė yra tai, jog
    teksto ekrane turi būti nedaug -- kitaip jis sunkiai įskaitomas. Statinis šrifto dydis terminale taip pat riboja
    gatvės lygio vaizdų detalių raišką.
3.  Tinkamai parinktos duomenų struktūros ASCII meno kūrimo algoritmams bei funkcinis programavimo stilius užtikrino
    pakankamą įrankio spartos lygį be papildomų sudėtingų optimizacijų ir sumažino bendrą klaidų skaičių. Tai leido
    daugiau dėmesio skirti programos funkcionalumui.
4.  Paprasčiausias šviesumo (angl. _luminance_) algoritmas pasirodė greičiausias, lengviausiai pritaikomas ir dažniausiai
    užtikrina geriausią vaizdo atvaizdavimą su įvairiais nustatymais.
5.  Sekos režimas užtikrina geresnę naudotojo patirtį – nuotraukos būna vienodo formato, panašaus apšvietimo
    ir mastelio, vaizdai visuomet būna užfiksuoti vienu laikotarpiu, todėl peržiūra vyksta sklandžiai.
    Tuo tarpu artumo režime to paties objekto kadrai gali smarkiai skirtis – naudotojas dažnai gauna skirtingo
    formato nuotraukas, gali naviguodamas matysi besikeičiantį paros laiką ar net metų laikus.
6.  Mapillary API ribojamo lango (angl. _bounding box_) užklausos kartais neįvykdomos dėl serverių apkrovos, todėl
    koordinačių ir adreso paleidimo režimai, taip pat ir atstumo navigacijos režimas, gali neveikti --
    programai reikalingas patikimas klaidų valdymas arba alternatyvus gatvės lygio vaizdų šaltinis.
7.  Geokodavimas pagal adresą (naudojamas adreso paleidimo režime) dažnai grąžina pastato centro koordinates,
    todėl vietoje gatvės lygio vaizdų gaunamos pastato vidaus nuotraukos arba jų visai nebūna – norint užtikrinti vaizdų
    radimą ribojame lange, dažnai tenka didinti jo dydį, ko pasėkmė yra ilgesnis programos paleidimo laikas bei didesnis šansas
    atsirasti toli nuo norimo objekto.
8.  Suderinamumo testavimo metu pastebėjome, kad programa be didelių sunkumų veikė visose pagrindinėse operacinėse sistemose
    Vienintelė vieta, kur radome skirtumų, buvo tikrinime, ar terminalo emuliatorius palaiko spalvas -- „Windows“ tam
    naudoja kitokius aplinkos kintamuosius nei „Linux“ ar „MacOS“.
9.  Kuriant šį projektą įgyta naudotojo sąsajos projektavimo patirtis padės ateities projektuose parinkti joms tinkamiausią
    terpę. Sudėtingai naudotojo sąsajai su daug elementų rinktumėmės grafinę naudotojo sąsają, tuo tarpu minimalių vizualinių
    reikalavimų programoms puikiai tinka komandinės eilutės sąsaja.
10. Nešališki naudotojai, ištestavę programą, skundėsi nepatogiu sekos navigacijos režimu: nuolat ekrane pasirodantys
    judėjimo pirmyn ir atgal mygtukai atitraukdavo dėmesį nuo gatvės lygio vaizdų. Todėl įdiegėme minimalų sekos navigavimo
    režimą, kuris neuždengia vaizdo spustelėjus.


