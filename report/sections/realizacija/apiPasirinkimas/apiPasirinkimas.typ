#set text(lang: "lt", region: "lt")
== Gatvės vaizdo sąsajos pasirinkimas<api-pasirinkimas>

Vienas iš esminių šio projekto reikalavimų – prieiga prie gatvės lygio panoraminės vaizdinės medžiagos.
Tokie duomenys leidžia naudotojui virtualiai „keliauti“ ir tyrinėti aplinką. Rinkoje egzistuoja keletas platformų,
teikiančių tokius duomenis per programavimo sąsajas (API), tačiau jų prieigos modeliai, duomenų aprėptis ir kainodara
ženkliai skiriasi. Šiame skyriuje aptariamas sąsajos pasirinkimo procesas ir pagrindiniai motyvai, lėmę galutinį sprendimą.

=== Pirminis kandidatas: „Google Street View“

Natūralus pirmasis pasirinkimas daugeliui projektų, susijusių su gatvių vaizdais, yra „Google Street View“.
Ši platforma yra plačiausiai žinoma ir pasižymi bene didžiausia geografine duomenų aprėptimi pasaulyje.
„Google“ teikia prieigą prie šių duomenų per „Google Maps Platform“ paslaugų rinkinį, įskaitant „Street View Static API“
@gmaps-sv-overview.

Pagrindiniai „Google Street View“ privalumai projektui būtų buvę:
- Didžiulė aprėptis: galimybė naudotis vaizdais iš daugybės vietovių visame pasaulyje.
- Duomenų kokybė ir aktualumas: dažnai atnaujinami ir aukštos raiškos vaizdai.
- Išplėtota sąsaja: palyginti gerai dokumentuota ir plačiai naudojama sąsaja.

Tačiau pagrindinė kliūtis, sutrukdžiusi pasirinkti „Google Street View“, buvo jos kainodaros modelis.
Nors „Google Maps Platform“ galbūt gali suteikti nemokamo naudojimo galimybes, pats kainodaros modelis
(angl. _pay-as-you-go_) ir jo stebėjimas gali būti painus akademinio projekto kontekste, kur biudžetas yra itin ribotas
arba jo nėra visai. Projektui, kurio metu vykdomas intensyvus kūrimas, eksperimentavimas ir testavimas
(ypač naršant ir nuolat keičiant vaizdus), egzistavo reali rizika
netikėtai patirti išlaidų. Kadangi mokami planai viršijo projekto finansines galimybes @gmaps-sv-billing,
o dalinai nemokamas modelis kėlė
neapibrėžtumo, buvo nuspręsta ieškoti alternatyvos, kuri siūlytų visiškai nemokamą ir aiškesnę prieigą prie duomenų.
Šis poreikis išvengti bet kokios finansinės rizikos ir sudėtingumo tapo esminiu veiksniu ieškant kitos platformos.

=== Alternatyvų paieška ir „Mapillary“ pasirinkimas

Susidūrus su „Google Street View“ kainodaros apribojimais, buvo pradėta ieškoti alternatyvių platformų,
kurios teiktų prieigą prie gatvės lygio vaizdų per programavimo sąsają ir turėtų projektui priimtinesnį prieigos modelį.
Po analizės buvo pasirinkta platforma „Mapillary“ @mapillary-homepage.

„Mapillary“, kurią 2020 metais įsigijo „Meta“ (anksčiau „Facebook“) @mapillary-joins-fb-blog,
yra bendruomenės principais paremta platforma, skirta gatvės lygio vaizdams rinkti ir dalintis. Jos pasirinkimą
lėmė keli pagrindiniai veiksniai:

1. Nemokama progamavimo sąsajos prieiga: „Mapillary“ teikia programavimo sąsają („Mapillary API v4“), kuri leidžia nemokamai
  gauti prieigą prie vaizdų sekų, metaduomenų ir pačių vaizdų @mapillary-api-docs.
  Nors ir egzistuoja tam tikri naudojimo limitai bei, kaip pastebėta techninių galimybių analizėje, kartais pasitaiko patikimumo
  problemų dėl didelio serverių apkrovimo, nemokamas prieigos modelis buvo esminis veiksnys, leidęs tęsti projektą
  neperžengiant biudžeto ribų.

2. Atvirų duomenų aspektai ir bendruomenės indėlis: nors pati „Mapillary“ platforma ir jos pagrindinė programinė įranga
  nėra atviro kodo (angl. _open source_), jos veikimo principas remiasi bendruomenės kuriamais duomenimis. Didelė dalis
  į „Mapillary“ įkeltų vaizdų yra licencijuojami pagal atvirą „Creative Commons Attribution-ShareAlike 4.0 International“
  (CC BY-SA) licenciją @mapillary-terms @cc-by-sa-4.
  Tai reiškia, kad duomenys gali būti laisvai naudojami (nurodant autorystę ir platinant išvestinius kūrinius ta pačia licencija),
  kas atitinka akademinio projekto dvasią. Be to, „Mapillary“ aktyviai integruojasi su „OpenStreetMap“ projektu,
  papildydama jį gatvių vaizdais.

3. Galimybė prisidėti prie duomenų rinkimo: „Mapillary“ leidžia bet kam įkelti savo surinktus gatvių vaizdus naudojant
  išmanųjį telefoną ar kitas kameras @mapillary-uploader-guide.
  Tai suteikia potencialią galimybę patiems projekto autoriams ar kitiems entuziastams papildyti duomenų bazę tose vietovėse,
  kurios projektui yra aktualios, bet „Mapillary“ aprėptis yra nepakankama. Šis aspektas ypač svarbus nišiniams
  ar lokaliems projektams.

4. Pakankamas funkcionalumas projektui: nors „Mapillary“ sąsaja galbūt nėra tokia išplėtota ar turinti tiek pagalbinių
  funkcijų kaip „Google Maps Platform“, ji suteikė visas projektui būtinas pagrindines galimybes: gauti vaizdus pagal
  geografines koordinates, naršyti vaizdų sekas (judėti pirmyn ir atgal) ir gauti reikalingus metaduomenis (pvz., vaizdo kryptį).

=== Išvada

Nors „Google Street View“ iš pradžių atrodė kaip technologiškai pranašesnis variantas dėl savo aprėpties ir brandumo,
jos kainodara buvo nepriimtina šiam akademiniam projektui. „Mapillary“ buvo pasirinkta kaip tinkamiausia alternatyva
dėl savo nemokamo programavimo sąsajos prieigos modelio, bendruomenės kuriamų ir dažnai atviromis licencijomis prieinamų
duomenų bei galimybės patiems prisidėti prie duomenų bazės pildymo. Nors teko susitaikyti su potencialiai mažesne
geografine aprėptimi tam tikrose vietovėse ir kartais pasitaikančiais programavimo sąsajos patikimumo svyravimais,
šie kompromisai leido įgyvendinti projekto tikslus laikantis nustatytų resursų ribų.
