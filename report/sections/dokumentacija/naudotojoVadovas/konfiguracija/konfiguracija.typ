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
- „mapillary-key“ - įvedama „Mapillary“ API rakto reikšmė, ji yra būtina programos veikimui.
- „imgur-client-id“ - įvedama „Imgur“ naudotojo įdentifikacinė reikšmė. Funkcija yra nebūtina ir gali būti išjungta
  konfigūracijoje nustačius reikšmę „Disabled“.
- „traveltime-app-id“ - įvedama „TravelTime“ aplikacijos įdentifikacinė reikšmė. Funkcija yra nebūtina ir gali būti išjungta
  konfigūracijoje nustačius reikšmę „Disabled“.
- „traveltime-key“ - įvedama „TravelTime“ API rakto reikšmė. Funkcija yra nebūtina ir gali būti išjungta konfigūracijoje
  nustačius reikšmę „Disabled“.

Nuotraukos konvertavimo į ASCII meną nustatymai:
- „navigation-type“ - pasirenkamas gatvės lygio navigacijos tipas:
  - „Sequence Navigation“ - navigacijos tipas leidžiantis naudotojams vaikščioti pirmyn ir atgal vienoje nuoseklioje
    vaizdų sekoje. Navigacija leidžiama, tol kol vaizdų sekoje yra naujų nuotraukų.
  - „Proximity Navigation“ - navigacijos tipas leidžiantis vaikščioti į 8 skirtingas kryptis, pasirenkant vieną iš 5 galimų
    sekančių lokacijų. Šiame režime galima judėti tarp skirtingų sekų.
- „algorithm“ - pasirenkamas algoritmas ASCII konvertavimui:
  - „Luminance“ - naudojamas šviesumo algoritmas.
  - „Edge Detection Sobel“ - naudojamas Sobelio kraštų atpažinimo algoritmas.
  - „Edge Detection Canny“ - naudojamas Canny kraštų atpažinimo algoritmas.
  - „Braille“ - naudojamas Brailio taškų metodas.
  - „No Algorithm“ - naudojamas vieno simbolio užpildymo metodas.
- „charset“ - pasirenkamas tekstinių simbolių rinkinys:
  - „Default“ `.:-=+*#%@`
  - „Extended“ `.'^\",:;Il!i~+_-?][}{1)(|\\/*tfjrxnuvczXYUJCLQ0OZmwqpdbkhao*#MW&8%B@$`
  - „Braille“ `⠁⠉⠋⠛⠟⠿⡿⢿⣻⣽⣾⣷⣟⣯⣿`
  - „Blocks“ `░▒▓█`
  - „Blocks“ Extended `·░▒▓▄▌▐▀█`
  - „Blank“ `█`
  - „Braille Patterns“ - visi iš 8 taškų sudaryti Brailio simboliai.
- „down-sampling-rate“ - įvedamas skaičius, kuris nurodo nuotraukos rezoliucijos mažinimo koeficientą, rekomenduojama nuo
  1 iki 20.

Spalvų nustatymai:
- „color“ - nustato ar rezultatas bus spalvotas, galimos reiškmės: „true“ arba „false“.
- „color-filter“ - pasirenkamas ASCII menui pritaikomas spalvų filtras:
  - „No Filter“ - nenaudojamas joks spalvų filtras.
  - „Contrast“ - naudojamas padidinto kontrasto filtras.
  - „Tritanopia“ - naudojamas Tritanopijos spalvų filtras.
  - „Protanopia“ - naudojamas Protanopijos spalvų filtras.
  - „Deuteranopia“ - naudojamas Deuteranopijos spalvų filtras.

Ne visos nustatymų kombinacijos grąžins kokybišką rezultatą. Todėl siekiant palengvinti konfigūracinio failo sukūrimą
buvo sukurti konfigūraciniai scenarijai, kurie veda naudotoją per per programos nustatymų pasirinkimus. Pirmąkart paleidus
scenarijų jis susiinstaliuos reikiamus įskiepius, todėl svarbu pasirinkti tinkamą scenarijaus versiją priklausomai nuo
naudojamos operacinės sistemos:
- Windows operacinei sistemai - „windowsScript.bat“
- Linux operacinei sistemai - „linuxScript.sh“
- MacOS operacinei sistemai - „macosScript.sh“

Nors šių scenarijų technologijos skiriasi, bendras veikimas atrodys panašiai. Iš eilės bus prašoma įvesti nustatymų
reikšmes, scenarijus pasirūpins, jog pasirinkti nustatymai gerai veiktų tarpusavyje.