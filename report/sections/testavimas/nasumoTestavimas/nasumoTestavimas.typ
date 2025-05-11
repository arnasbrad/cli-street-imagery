== Našumo testavimas

Našumo testavimo tikslas – įvertinti, kaip programa naudoja sistemos resursus ir koks yra jos atsako laikas atliekant
pagrindines operacijas. Siekta užtikrinti, kad programa veiktų efektyviai ir nesukeltų nepagrįstos apkrovos naudotojo kompiuteriui.

Testavimo metu buvo stebimi šie našumo aspektai:

- Resursų naudojimas
  - Operatyvioji atmintis (RAM): testuojant programą, nustatyta, kad veikiantis „Java“ procesas vidutiniškai naudoja iki 1.1–1.3 GB
    operatyviosios atminties (naujai paleista programa -- apie 500MB). Nors ši reikšmė gali atrodyti didelė paprastai komandinės
    eilutės programai, ji iš dalies paaiškinama „Java“ virtualios mašinos (JVM) ypatumais ir naudojamomis bibliotekomis.
    Tolimesnėje plėtroje būtų galima ieškoti optimizavimo galimybių, tačiau esamoje stadijoje šis atminties naudojimas laikomas priimtinu,
    atsižvelgiant į programos funkcionalumą. Taip pat JVM šiukšlių surinkėjo (angl. _garbage collector_) nustatymai galėtų pakeisti šiuos
    rezultatus.
  - Centrinio procesoriaus (CPU) apkrova: stebėta, kad, išskyrus pradinį programos paleidimo etapą, kuris natūraliai reikalauja daugiau
    resursų, įprasto programos veikimo metu (pvz., naršant vaizdus, generuojant ASCII meną) CPU apkrova išlieka nedidelė. Net ir
    senesnės kartos procesoriuje (pvz., „Intel(R) Core(TM) i7-6700 CPU \@ 3.40GHz“) CPU apkrova paprastai neviršijo 10%. Tai rodo,
    kad pati programos logika nėra itin reikli procesoriaus resursams.

- Atsako laikas
  - Bendras operacijų greitis: dauguma programos operacijų, tokių kaip ASCII meno generavimas iš gautų vaizdo duomenų, ekrano perpiešimas,
    navigacijos parinkčių rodymas ar pagalbos meniu iškvietimas, yra atliekamos pakankamai greitai, paprastai per mažiau nei 1–2 sekundes.
    Tokio greičio operacijos mūsų tikslui yra priimtinos.

    Štai, pavyzdžiui, palyginimas kiek užtrunka kiekvienas ASCII generavimo algoritmas:
    ```
    No Algorithm         - 22ms
    Luminance            - 32ms
    Braille              - 125ms
    Sobel Edge Detection - 636ms
    Canny Edge Detection - 943ms
    ```

    Matome, jog net ir ilgiausias algoritmas užtrunka mažiau nei sekundę. Taip pat šie testai buvo daromi su
    didžiausia mūsų programos leidžiama nuotraukos kokybe. Tikėtina, kad naudotojas naudos bent 3 kartus sumažintą
    nuotrauką, kas taip pat žymiai sumažins konvertavimo laiką.

  - Priklausomybė nuo išorinių API: pastebėta, kad bendras programos atsako laikas, ypač keičiant lokaciją ar pirmą kartą
    užkraunant vaizdą, labiausiai priklauso nuo „Mapillary“ API atsako greičio. Esant normalioms tinklo sąlygoms ir serverio apkrovai,
    atsakas gaunamas pakankamai greitai, tačiau retais atvejais, kai „Mapillary“ atsakas vėluoja, tai tiesiogiai įtakoja ir bendrą
    programos reakcijos laiką. Šis aspektas yra už programos tiesioginės kontrolės ribų.
  - Naudotojo sąsajos reakcija: tiesioginė sąveika su naudotojo sąsaja, pvz., klavišų paspaudimų registravimas,
    yra momentinė, suteikiant gerą tiesioginės kontrolės pojūtį.

Apibendrinant, našumo testavimas parodė, kad programa, neįskaitant išorinių API priklausomybių, veikia pakankamai efektyviai ir
greitai standartinėje techninėje įrangoje. Operatyviosios atminties naudojimas yra didžiausias pastebėtas resursų aspektas,
tačiau jis neturėtų kelti problemų daugumai šiuolaikinių kompiuterių.
