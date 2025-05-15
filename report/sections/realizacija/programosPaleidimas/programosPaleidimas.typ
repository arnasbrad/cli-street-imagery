== Programos paleidimas

_Rašė: Arnas Bradauskas._

Programos paleidimas per komandinę eilutę yra fundamentalus interakcijos būdas, suteikiantis lankstumo ir galimybę automatizuoti
užduotis. Mūsų kuriama „StreetAscii“ programa nėra išimtis – jai reikalinga sistema, leidžianti naudotojui nurodyti
įvairius paleidimo parametrus. Programa palaiko keturis pagrindinius veikimo režimus:
1. Pagal „Mapillary“ nuotraukos identifikatorių.
2. Pagal geografines koordinates.
3. Pagal vietovės adresą.
4. Lokacijos spėliojimo režimu.

Kiekvienas iš šių režimų gali turėti specifinius, jam būdingus argumentus. Pavyzdžiui, visi režimai leidžia nurodyti
kelią iki konfigūracinio failo naudojant _-\-config_ (arba _-c_) parinktį. Be to, naudotojui turi būti prieinama aiški
ir struktūrizuota pagalbos informacija (_-\-help_), detalizuojanti galimas komandas, jų parinktis ir vėliavėles (angl. _flags_).

=== Techninių reikalavimų analizė ir bibliotekos pasirinkimas

Siekiant efektyviai valdyti komandinės eilutės argumentus „Scala“ programoje, patogu naudoti tam skirtą įskiepį.
Pagrindiniai reikalavimai tokiai bibliotekai buvo šie:
- Deklaratyvi sąsaja -- galimybė apibrėžti komandų struktūrą (subkomandas, parinktis, argumentus) aiškiai ir glaustai.
- Tipų saugumas -- biblioteka turėtų užtikrinti, kad išanalizuoti argumentai atitiktų nurodytus tipus
  (pvz., _String_, _Int_, pasirinktinis tipas), taip sumažinant klaidų tikimybę programos vykdymo metu.
- Automatinis pagalbos generavimas -- gebėjimas automatiškai sukurti informatyvius pagalbos pranešimus pagal apibrėžtą komandų struktūrą.
- Klaidingo įvedimo valdymas -- aiškūs ir naudingi pranešimai naudotojui, jei argumentai įvesti neteisingai.
- Kompoziciškumas -- galimybė lengvai derinti ir komponuoti skirtingas komandų dalis, ypač dirbant su subkomandomis.
- Integracija su funkcinio programavimo paradigma -- kadangi projektas kuriamas naudojant „Scala“ ir funkcinio programavimo
  principus (pvz., „Cats Effect“ biblioteką efektams valdyti), pageidautina, kad argumentų analizės biblioteka
  natūraliai įsilietų į šią ekosistemą.

Atsižvelgiant į šiuos kriterijus, projekte buvo pasirinkta „Decline“ @decline-homepage biblioteka. „Decline“ yra kompozicinė
komandinės eilutės argumentų analizės biblioteka „Scala“ kalbai, įkvėpta „Haskell“ bibliotekos „optparse-applicative“.

Pagrindiniai „Decline“ privalumai, lėmę jos pasirinkimą:
1. Funkcinis ir kompozicinis dizainas -- „Decline“ leidžia konstruoti komandų analizatorius (angl. _parsers_) naudojant
   aplikatyvinius funktorius (angl. _applicative functors_) ir kitus funkcinio programavimo konstruktus. Tai užtikrina
   aukštą kompoziciškumo lygį ir leidžia elegantiškai apibrėžti sudėtingas komandų struktūras. Užuot rašius imperatyvų kodą
   argumentams tikrinti ir išrinkinėti, su „Decline“ komandų struktūra aprašoma deklaratyviai, o biblioteka pasirūpina analizės logika.
2. Tipų saugumas -- analizuojant argumentus, „Decline“ grąžina reikšmes su „Scala“ tipais. Jei naudotojas pateikia argumentą,
   neatitinkantį laukiamo tipo (pvz., tekstą vietoj skaičiaus), biblioteka automatiškai sugeneruos klaidą dar prieš programos
   logikai pradedant vykdyti pagrindines užduotis. Tai padeda anksti aptikti klaidas ir užtikrina didesnį programos patikimumą.
3. Automatinis pagalbos meniu -- biblioteka automatiškai generuoja _-\-help_ išvestį, kuri yra nuosekli ir pritaikyta apibrėžtoms
   komandoms bei parinktims. Kaip matyti iš pateiktų naudojimo pavyzdžių programos paleidimo dokumentacijoje, pagalbos meniu
   yra kontekstinis – _street-ascii.jar -\-help_ rodo bendrą informaciją, o _street-ascii.jar guessing -\-help_ rodo specifinę
   informaciją _guessing_ subkomandai. Ši funkcija sutaupo daug laiko, nes nereikia rankiniu būdu kurti ir palaikyti pagalbos tekstų.
4. Minimalios priklausomybės -- „Decline“ turi nedaug išorinių priklausomybių, kas yra privalumas siekiant išlaikyti projekto priklausomybių
   medį kuo mažesnį ir išvengti galimų konfliktų.
5. Geras integravimasis su „Cats“ ekosistema -- nors „Decline“ tiesiogiai nepriklauso nuo „Cats“, jos funkcinis stilius ir naudojami
   modeliai (pvz., _Validated_ klaidų kaupimui) gerai dera su „Cats“ ir „Cats Effect“ naudojamais principais.

=== Argumentų struktūros įgyvendinimas su „Decline“

Programoje „StreetAscii“ „Decline“ biblioteka naudojama apibrėžti pagrindinę komandų struktūrą. Yra viena pagrindinė komanda
(_StreetAscii_), kuri gali būti iškviesta su viena iš keturių subkomandų: _id_, _coordinates_, _address_ arba _guessing_.
Kiekviena subkomanda gali turėti savo specifines parinktis. Pavyzdžiui, _coordinates_ ir _address_ subkomandos papildomai priima _-\-radius_
parinktį paieškos spinduliui nurodyti, o visos subkomandos priima neprivalomą _-\-config_ (arba _-c_)
parinktį konfigūracijos failo kelio nurodymui.

Biblioteka leidžia apibrėžti kiekvieną parinktį (pvz., _-\-config_), argumentą (pvz., konkretus ID _id_ subkomandai)
ar vėliavėlę (pvz., _-\-help_) kaip atskirą analizatorių. Šie individualūs analizatoriai vėliau jungiami (komponuojami)
į sudėtingesnes struktūras, atitinkančias subkomandas, o galiausiai – į vieną bendrą komandos analizatorių.

Šis deklaratyvus būdas aprašyti komandinės eilutės sąsają ne tik supaprastina patį argumentų analizės logikos kūrimą,
bet ir užtikrina, kad naudotojui pateikiama pagalbos informacija bei klaidų pranešimai būtų nuoseklūs ir automatiškai
atnaujinami pasikeitus komandų struktūrai.

Naudojant „Decline“, programos pagrindinė paleidimo logika gali tiesiog perduoti komandinės eilutės argumentus
(_args: Array[String]_) bibliotekai. „Decline“ atlieka analizę ir, sėkmės atveju, grąžina tipizuotą objektą, reprezentuojantį
pasirinktą veikimo režimą ir jo parametrus (pvz., objektą, nurodantį, kad pasirinktas _guessing_ režimas ir pateiktas
konkretus konfigūracijos failo kelias). Nesėkmės atveju (pvz., trūksta privalomo argumento arba pateiktas neteisingas formatas),
„Decline“ grąžina klaidą, kurią programa gali tvarkingai apdoroti ir parodyti naudotojui informatyvų pranešimą, dažnai kartu su
pasiūlymu pasinaudoti _-\-help_.

Apibendrinant, „Decline“ pasirinkimas leido sukurti tvirtą, tipų saugią ir naudotojui draugišką komandinės eilutės sąsają,
minimaliomis pastangomis įgyvendinant sudėtingus reikalavimus dėl skirtingų paleidimo režimų ir jų parametrų.
Tai leido kūrėjams labiau susikoncentruoti į pagrindinę programos funkcionalumą, o ne į komandinės eilutės argumentų analizės subtilybes.

=== Išvados

Praktikoje, kuriant _address_ subkomandą, paaiškėjo tam tikras jos naudojimo ypatumas. Nors galimybė paleisti programą nurodant
vietovės adresą yra patogi, geokodavimo paslauga (šiuo atveju „TravelTime“ API) paprastai grąžina koordinates, atitinkančias
objekto ar pastato centrą. Kadangi „Mapillary“ gatvės lygio vaizdų padengimas retai būna pačiame pastato centre (dažniau aplinkinėse
gatvėse), tiesioginis šių koordinačių naudojimas su numatytuoju mažu paieškos spinduliu dažnai neduoda rezultatų. Dėl šios
priežasties, naudojant paleidimą pagal adresą, naudotojui dažnai tenka rankiniu būdu padidinti paieškos spindulį per _-\-radius_
parinktį, kad programa rastų artimiausius padengtus gatvės vaizdus aplink geokoduotą tašką. Tai šiek tiek sumažina pradinį
šios funkcijos patogumą, tačiau galimybė reguliuoti spindulį išlieka svarbi.

Be šio vieno nepatogumo, visos kitos subkomandos veikė kaip tikėtasi, nes naudotojas turi nurodyti arba konkrečias koordinates,
arba nuotraukos identifikatorių -- tokiu atveju naudotojas turi galimybę tiksliai valdyti, ką jis nori pamatyti, nepriklausomai
nuo geokodavimo rezultato.
