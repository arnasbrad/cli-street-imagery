=== Konfigūracija<config-docs>

Norint naudoti programą, būtina turėti paruoštą konfigūracinį failą. Kaip minėta praeitame skyriuje šis failas leidžia
mums ne tik pasirinkti įvairius ASCII vaizdų generavimo nustatymus, bet ir nurodyti reikiamus programavimo sąsajų
raktus (angl. _API keys_).

```hocon
api {
  mapillary-key = "MLY|1111111111111111|22222222222222222222222222222222"
  imgur-client-id = "Disabled"
  traveltime-app-id = "aaaaaa"
  traveltime-key = "bbbbbbb"
}

processing {
  navigation-type = "Sequence Navigation"
  algorithm = "Edge Detection Sobel"
  charset = "Blocks Extended"
  down-sampling-rate = 3
}

colors {
  color = true
  color-filter = "No Filter"
}
```

Toliau aptariamos konfigūracinio failo reikšmės, pradedant nuo API nustatymų:
- _mapillary-key_ -- įvedama „Mapillary“ API rakto reikšmė, ji yra būtina programos veikimui.
- _imgur-client-id_ -- įvedama „Imgur“ naudotojo įdentifikacinė reikšmė. Funkcija yra nebūtina ir gali būti išjungta
  konfigūracijoje nustačius reikšmę _Disabled_.
- _traveltime-app-id_ -- įvedama „TravelTime“ programos įdentifikacinė reikšmė. Funkcija yra nebūtina ir gali būti išjungta
  konfigūracijoje nustačius reikšmę _Disabled_.
- _traveltime-key_ -- įvedama „TravelTime“ API rakto reikšmė. Funkcija yra nebūtina ir gali būti išjungta konfigūracijoje
  nustačius reikšmę _Disabled_.

Nuotraukos konvertavimo į ASCII meną nustatymai:
- _navigation-type_ -- pasirenkamas gatvės lygio navigacijos tipas:
  - _Sequence Navigation_ -- navigacijos tipas leidžiantis naudotojams vaikščioti pirmyn ir atgal vienoje nuoseklioje
    vaizdų sekoje. Navigacija leidžiama, tol kol vaizdų sekoje yra naujų nuotraukų.
  - _Proximity Navigation_ -- navigacijos tipas leidžiantis vaikščioti į 8 skirtingas kryptis, pasirenkant vieną iš 5 galimų
    sekančių lokacijų. Šiame režime galima judėti tarp skirtingų sekų.
- _algorithm_ -- pasirenkamas algoritmas ASCII konvertavimui:
  - _Luminance_ -- naudojamas šviesumo algoritmas.
  - _Edge Detection Sobel_ -- naudojamas Sobelio kraštų atpažinimo algoritmas.
  - _Edge Detection Canny_ -- naudojamas Canny kraštų atpažinimo algoritmas.
  - _Braille_ -- naudojamas Brailio taškų metodas.
  - _No Algorithm_ -- naudojamas vieno simbolio užpildymo metodas.
- _charset_ -- pasirenkamas tekstinių simbolių rinkinys:
  - _Default_ `.:-=+*#%@`
  - _Extended_ `.'^\",:;Il!i~+_-?][}{1)(|\\/*tfjrxnuvczXYUJCLQ0OZmwqpdbkhao*#MW&8%B@$`
  - _Braille_ `⠁⠉⠋⠛⠟⠿⡿⢿⣻⣽⣾⣷⣟⣯⣿`
  - _Blocks_ `░▒▓█`
  - _Blocks_ Extended `·░▒▓▄▌▐▀█`
  - _Blank_ `█`
  - _Braille Patterns_ -- visi iš 8 taškų sudaryti Brailio simboliai.
- _down-sampling-rate_ -- įvedamas skaičius, kuris nurodo nuotraukos rezoliucijos mažinimo koeficientą, rekomenduojama nuo
  1 iki 20.

Spalvų nustatymai:
- _color_ -- nustato ar rezultatas bus spalvotas, galimos reiškmės: _true_ arba _false_.
- _color-filter_ -- pasirenkamas ASCII menui pritaikomas spalvų filtras:
  - _No Filter_ -- nenaudojamas joks spalvų filtras.
  - _Contrast_ -- naudojamas padidinto kontrasto filtras.
  - _Tritanopia_ -- naudojamas Tritanopijos spalvų filtras.
  - _Protanopia_ -- naudojamas Protanopijos spalvų filtras.
  - _Deuteranopia_ -- naudojamas Deuteranopijos spalvų filtras.

Ne visos nustatymų kombinacijos grąžins kokybišką rezultatą. Todėl siekiant palengvinti konfigūracinio failo sukūrimą
buvo sukurti konfigūraciniai scenarijai, kurie veda naudotoją per per programos nustatymų pasirinkimus. Pirmąkart paleidus
scenarijų jis susiinstaliuos reikiamus įskiepius, todėl svarbu pasirinkti tinkamą scenarijaus versiją priklausomai nuo
naudojamos operacinės sistemos:
- Windows operacinei sistemai -- _windowsScript.bat_
- Linux operacinei sistemai -- _linuxScript.sh_
- MacOS operacinei sistemai -- _macosScript.sh_

Nors šių scenarijų technologijos skiriasi, bendras veikimas atrodys panašiai. Iš eilės bus prašoma įvesti nustatymų
reikšmes, scenarijus pasirūpins, jog pasirinkti nustatymai gerai veiktų tarpusavyje.
