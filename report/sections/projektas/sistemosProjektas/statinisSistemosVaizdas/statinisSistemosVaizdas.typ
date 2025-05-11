=== Statinis sistemos vaizdas

*Diegimo diagrama*

#figure(
  image("/images/deploymentDiagram.png"),
  caption: [Diegimo diagrama],
) <deployment-diagram>

Diegimo diagramos iliustracijoje (@deployment-diagram) matome, jog mūsų programos
diegimas nėra sudėtingas.

- Programa turi _.jar_ failą, kurio paleidimui reikia JRE („Java Runtime Environment“) @jre-download.
- Programos įvesčiai / išvesčiai yra reikalingas terminalo emuliatorius, pageidautinai palaikantis bent
  256 spalvas.
- Programa taip pat priklauso nuo konfigūracinio _\file_name.conf_ failo, kurime yra nurodyti įvairių
  išorinių sąsajų raktai bei kiti programos nustatymai.

#pagebreak()

*Paketų diagrama*

#figure(
  image("/images/packageDiagram.png"),
  caption: [Paketų diagrama],
) <package-diagram>

Paketų diagramos iliustracijoje (@package-diagram) matome, jog mūsų programa yra suskirstyta į įvairius
paketus, kiekvienas jų yra atsakingas už skirtingą funkcionalumą. Ryšiai tarp paketų nusako, kaip paketai bendrauja
tarpusavyje, t.y. kokie paketai naudoja kitus paketus.

- _Main_ -- pagrindinis paketas, iš kurio yra paleidžiama programa.
- _Config_ -- konfigūracinio failo pavertimo „Scala“ tipais logika.
- _CLI Handler_ -- programos paleidimo naudojant komandinės eilutės argumentus logika.
- _Terminal UI_ -- terminalo naudotojo sąsajos modulis, suteikiantis ASCII teksto spausdinimo bei
  naudotojo įvesties (valdymo klaviatūros klavišais) funkcionalumą.
- _ASCII Art Engine_ -- nuotraukos baitų pavertimas ASCII menu naudojant įvairius algoritmus logika.
- _Navigation Logic_ -- navigacijos tarp nuotraukų (pvz., atstumo bei krypties skaičiavimo iki arti
  esančių nuotraukų) logika.
- _Guessing Game_ -- nuotraukos šalies spėliojimo logika bei išsaugotos lokacijos.
- _Runner_ -- programos orkestratorius, apjungiantis kelių paketų logiką į paprastas, lengvai naudojamas
  funkcijas.
- _Image Utilities_ -- teksto pavertimo paveikslėliu logika bei įvairių naudotojui skirtų pranešimų saugykla.
- _Social Media Integration_ -- socialinės medijos pasidalinimo nuorodų formavimo logika.
- _External Clients_ -- komunikavimo su išorinėmis sąsajomis logika.
  - _Mapillary Client_ -- komunikacijos su „Mapillary“ @mapillary-api-docs API logika nuotraukų gavimui.
  - _TravelTime Client_ -- komunikacijos su „TravelTime“ @traveltime-api-docs API logika geokodavimo (angl. _geocoding_) -- 
    adreso pavertimo koordinatėmis -- logika.
  - _Imgur Client_ -- komunikacijos su „Imgur“ @imgur-api-docs API logika nuotraukų išoriniam saugojimui.

Visi šie paketai yra kiek įmanoma izoliuoti, tiesiogiai nepriklausantys nuo kitų. Toks programavimo stilius
mums labai padėjo lengvai daryti įvairius progamos pakeitimus bei netrukdyti vienas kitam dirbant komandoje.
