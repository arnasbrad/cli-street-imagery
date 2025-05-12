#set text(lang: "lt", region: "lt")
= Išvados

Atlikus projektinį darbą padarėme išvadas:
- Komandinės eilutės aplinka, nors dažnai suvokiama kaip ribota vartotojo sąsajos atžvilgiu, tyrimo metu atskleidė
  netikėtai plačias galimybes vizualiniam atvaizdavimui. Nors pradinė hipotezė buvo, kad tokia aplinka bus nepajėgi
  parodyti sudėtingą vaizdinį turinį. Tačiau įgyvendintas projektas pademonstravo, jog tinkamai panaudojant ASCII
  simbolius ir spalvų kodus, galima sukurti gana detalius vaizdus ir interaktyvius naudotojo sąsajos elementus. Tai
  leido programai išlaikyti paprastumą ir universalumą, kartu užtikrinant pakankamą funkcionalumą, kad vartotojo patirtis
  būtų intuityvi ir efektyvi.
- Pagrindinis apribojimas, su kuriuo susidūrėme dirbdami komandinėje eilutėje, buvo statinis šrifto dydis, kuris ribojo
  gatvės vaizdų ir sąsajos atvaizdavimo galimybes. Ši problema tapo aktuali bandant atvaizduoti didesnio detalumo nuotraukas.
  Siekiant, kad ekrane tilptų visas vaizdas reikėjo stipriai mažinti terminalo šriftą, ko pasekoje kiti sąsajos elementai
  tapo nebeįskaitomi. Tačiau ir šį apribojimą galima apeiti, tai buvo padaryta sąsajos elementus atvaizduojant atskirai
  nuo gatvės lygio fotografijų. Sąsajos elementai taip pat buvo paversti nuotraukomis, kurioms būdavo pritaikomas tas pats
  ASCII konvertavimo algoritmas kaip ir pačiam gatvės vaizdui. Tai leido atvaizduoti aukštos rezoliucijos nuotraukas terminalo
  sąsajoje ir tuo pačiu suvienodino programos stilių.
- Tyrimo metu lyginome keletos skirtingų algoritmų efektyvumą, atvaizduojant įvairias nuotraukas terminale. Paprasčiausias
  šviesumo (angl. luminance) algoritmas, kuris parenka ASCII simbolį pagal orginalios nuotraukos pikselių šviesumą,
  pasirodė esąs universaliausias. Šis algoritmas geriausiai veikė su įvairiais terminalo nustatymais, spalvų schemomis
  ir skirtingo apšvietimo nuotraukomis, užtikrindamas optimalų kontrastą ir detalių išsaugojimą. Naudojant jį nebuvo
  susidurta su problemomis kaip: detalumo trūkumas, spalvų nepalaikymas, priklausomybė nuo ryškių nuotraukos kraštų kiekio,
  kurie darė neigiamą įtaką kitų algoritmų rezultatams.
- Nuotraukų apdorojimo laikas buvo iškeltas, kaip vienas iš pagrindinių nefunkcinių programos reikalavimų. Projekto
  pradžioje programa užtrukdavo net iki keliolikos sekundžių kovertuoti nuotrauką į ASCII meną. Lėto veikimo priežastis
  buvo netinkamų duomenų struktūrų naudojimas. Vietoje sąrašo duomenų struktūros pradėjus naudoti masyvus programa tapo
  ženkliai efektyvesnė. Konvertavimo laikas vis dar priklausė nuo originalios nuotraukos raiškos, tačiau šis apdorojimas
  įvykdavo greičiau nei per sekundę. Šis greitaveikos rodiklis mus tenkino ir buvo nuspręsta nelygiagretinti programos.
- Įgyvendinus ir išbandžius du skirtingus navigacijos režimus, sekos (angl. sequence) navigacija akivaizdžiai pranoko
  atstumo (angl. proximity) navigaciją dėl kelių priežasčių. Sekos režime visos nuotraukos, užfiksuotos vieno fotografo
  vienu metu, yra vienodos orientacijos, panašaus apšvietimo ir vaizdo kokybės, todėl perėjimas tarp jų yra nuoseklesnis
  ir mažiau trikdantis. Be to, sekos režimas veikia patikimai nepriklausomai nuo aplink esančių fotografijų kiekio, todėl
  vartotojui užtikrinamas nuoseklus judėjimas net ir menką žemėlapio padengimą turinčiose vietovėse.
- Projekto metu taikytas funkcinis programavimo stilius, vengiant šalutinių efektų, pasirodė esąs itin naudingas kuriant
  stabilią ir prognozuojamą programą. Kodas, kuriame funkcijos nepakeičia išorinių būsenų ir grąžina naują reikšmę vietoj
  esamos modifikavimo, buvo žymiai lengviau testuojamas ir derinamas. Tai leido išvengti daugelio įprastų programavimo
  klaidų, susijusių su būsenos pasikeitimu netikėtose vietose. Programos veikimas viso projekto metu išliko patikimas
  net sudėtingesnėse situacijose, o tai sumažino kritinių kodo klaidų skaičių iki minimumo.
- Dirbant su „Mapillary“ API, buvo pastebėti ženklūs patikimumo iššūkiai, ypač naudojant ribojamo lango
  (angl. _bounding box_) užklausas geografinėms teritorijoms. Serverių apkrovos metu API dažnai grąžindavo klaidos
  pranešimus arba visiškai neatsakydavo į užklausas. Šios problemos tapo ypač akivaizdžios dienos metu, kai serveriai
  būdavo labiau apkrauti. Kai „Mapillary“ serveriai susidurdavo su problemomis -- programa neveikdavo koordinačių ir
  adresų paieškos režimuose, kadangi jų veikimas yra paremtas atstumo paieška pagal koordinates. Kiti sekos ir spėliojimo
  režimai šių trikdžių nebuvo įtakojami ir veikdavo įprastai.
- Programos funkcija, leidžianti paleisti ją nurodant gatvės adresą vietoje tikslių koordinačių, pasirodė esanti ne tokia
  naudinga praktikoje, kaip buvo tikėtasi projekto pradžioje. Pagrindinė problema kyla dėl geokodavimo paslaugų
  specifikos -- jos paprastai grąžina koordinates, atitinkančias pastato ar objekto centrą, o ne gatvės vaizdą. Dėl to
  „Mapillary“ duomenų bazėje dažnai nebūdavo nuotraukų tiksliai tose koordinatėse, nes nuotraukos paprastai fiksuojamos
  iš gatvės perspektyvos. Tai reikalavo papildomos logikos, kad būtų rastos artimiausios nuotraukos aplink nurodytą
  adresą, tačiau šis sprendimas nėra tikslus ir ne visada grąžindavo norimą rezultatą.