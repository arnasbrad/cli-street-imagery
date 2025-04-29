=== Paleidimas

// TODO: replace sbt run commands with jar commands, maybe docker if exits

Paleisti programą galima keturiais būdais.

==== Pagal koordinates:

nurodžius konkrečias koordinates koordinates programa ieškos esamų paveiksliukų „Mapillary“
platformoje, nubrėždama nedidelį stačiakampį aplink nurodytas koordinates.

```bash
sbt "run coordinates 54.901300696008846,11.892028147030498"
```

Sėkmingai radus paveiksliuką, programoje matysite ASCII atvaizduotą jo vaizdą. Įvykus
klaidai, ektrane matysite atitinkamą klaidos tekstą, pavyzdžiui:
- Autentifikacijos klaida
  ```
  Origin image parsing failed with error: Authentication failed with status 401: Unauthorized
  ```
- Klaidingų koordinačių klaida
  ```
  Invalid coordinates: LongitudeOutOfRangeError(1111.8920281470305)
  ```
- Paveiksliukų radimo klaida
  ```
  Origin image parsing failed with error: No images found for coordinates = Coordinates(54.901300696008846,11.892028147030498), radius = 15m. Try a different location or increasing the radius.
  ```
Ir panašios klaidos.

==== Pagal vietovės adresą:

nurodžius vietovės adresą programa kreipsis į „TravelTime“ programavimo sąsają
ir bandys paversti adresą į to adreso koordinates. Jei koordinatės bus rastos,
toliau pagal jas bus ieškomas paveiksliukas taip pat, kaip ir paleidime
naudojant koordinates.

Šiam funkcionalumui būtini „TravelTime“ sąsajos raktai.

```bash
sbt 'run address "Kauno Pilis"'
```

Sėkmingai radus paveiksliuką, programoje matysite ASCII atvaizduotą jo vaizdą. Įvykus
klaidai, ektrane matysite atitinkamą klaidos tekstą, pavyzdžiui:
- Autentifikacijos klaida
  ```
  Error during address geocoding: Cannot use geocoding API, TravelTime credentials were not set
  ```
- Nepavykusio koordinačių radimo klaida
  ```
  Geocoding did not return any results for address: Neegzistuojantis adresas 12341234
  ```
Ir panašios klaidos.

==== Pagal „Mapillary“ paveksliuko unikalų identifikatorių (angl. _id_):

nurodžius konkretų identifikatorių, programa suras atitinkampą paveiksliuka
„Mapillary“ platformoje ir jį matysite ekrane. Šis funkcionalumas užtikrina,
jog kiekvieną kartą bus gautas tas pats paveiksliukas.

```bash
sbt "run id 1688256144933335"
```

Sėkmingai radus nurodytą paveiksliuką, programoje matysite ASCII atvaizduotą jo vaizdą. Įvykus
klaidai, ektrane matysite atitinkamą klaidos tekstą.

==== Lokacijos spėliojimo režimu

Programą galima paleisti lokacijos spėliojimo režimu. Taip paleidus, programa
atsitiktinai parinks lokaciją, o naudotojas turės atspėti, kokioje šalyje jis
yra.

```bash
sbt "run guessing"
```

Sėkmingai radus nurodytą paveiksliuką, programoje matysite ASCII atvaizduotą jo vaizdą bei
galėsite spėti šalį. Daugiau apie tai, kaip spėlioti, bus aprašyta vėlesniuose dokumentacijos
skyriuose.


