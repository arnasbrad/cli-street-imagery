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

==== Konfigūracinio failo nurodymas

Programai veikti reikalingas konfigūracinis failas, kuriam sukurti yra pateikti specialūs
scenarijai, aprašyti konfigūracijos dokumentacijoje. Paleidžiant programą, naudotojas
gali pateikti kelią iki konfigūracinio failo.

```bash
sbt "run guessing --config ./mano_configuracija.conf"

sbt "run guessing -c ./mano_configuracija.conf"
```

Jei _--config_ arba _-c_ parametras nėra nurodytas, bus naudojama numatytoji (angl. _default_) reikšmė
_./config.conf_.

=== Pagalba

Jei naudototojas nežino kaip paleisti programą ar kokie argumentai yra galimi, paleidimo metu
galima iškviesti pagalbos meniu naudojant _--help_ vėliavėlę (angl. _flag_).
Klaidingo paleidimo metu (pavyzdžiui, bandant paleisti programą su klaidingais parametrais)
pagalbos meniu bus pateiktas automatiškai.

- Komanda _sbt "run --help"_
  
  Išvestis:

  ```bash
  Usage:
      StreetAscii id
      StreetAscii coordinates
      StreetAscii address
      StreetAscii guessing

  Street imagery in your terminal

  Options and flags:
      --help
          Display this help text.
      --version, -v
          Print the version number and exit.

  Subcommands:
      id
          Start with a Mapillary image ID
      coordinates
          Start with geographic coordinates
      address
          Start with a street address
      guessing
          Start in guessing mode
  ```

- Komanda _sbt "run guessing --help"_

  Išvestis:

  ```bash
  Usage: StreetAscii guessing [--config <string>]

  Start in guessing mode

  Options and flags:
      --help
          Display this help text.
      --config <string>, -c <string>
          Path to configuration file
  ```

Analogiškai galima gauti pagalbos meniu visoms kitoms komandoms.


