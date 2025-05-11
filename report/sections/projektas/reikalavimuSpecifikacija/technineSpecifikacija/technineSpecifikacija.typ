=== Techninė specifikacija

Šiame skyriuje apibrėžiama techninė aplinka ir išoriniai resursai, būtini kuriamos programos veikimui. Programos
paleidimui ir naudojimui reikalinga komandinės eilutės sąsaja, kuri yra prieinama daugumoje šiuolaikinių operacinių
sistemų, tokių kaip „Windows“, „Linux“, „MacOS“. Terminalo aplinka turi palaikyti UTF-8 koduotę, kadangi ASCII menui
generuoti gali būti naudojami ne tik standartiniai ASCII simboliai. Taip pat programos veikimui bus reikalinga „Java
Runtime Environment“ vykdymo aplinka bei kelios bibliotekos, kurios bus automatiškai įdiegiamos pirmąkart paleidus programą.

Kalbant apie techninės įrangos reikalavimus, programa yra sukurta taip, kad nebūtų reikli sistemos resursams. Ji turėtų
sklandžiai veikti praktiškai bet kokioje šiuolaikinėje kompiuterinėje sistemoje, neturinčioje išskirtinai didelių
skaičiavimo pajėgumų. Naudotojams nereikia rūpintis specifiniais procesoriaus greičio ar operatyviosios atminties kiekio
reikalavimais, nes sistemos našumo poveikis programai yra minimalus.

Vis dėlto, yra keli esminiai išoriniai reikalavimai. Pirma, programos veikimui būtinas aktyvus interneto ryšys. Tai
reikalinga todėl, kad programa sąveikauja su išorinėmis paslaugomis internetu, visų pirma gaudama nuotraukų duomenis.
Antra, norint naudotis pagrindine programos funkcija – gatvės lygio vaizdų navigavimu – privaloma turėti galiojantį
„Mapillary“ API raktą. Be šio rakto, programa negalės gauti pradinių nuotraukų duomenų.

Programa taip pat palaiko sąsajas su keliomis papildomomis, tačiau nebūtinomis, išorinėmis paslaugomis. Naudotojai,
norintys naudotis galimybe automatiškai įkelti sugeneruotą ASCII meną į „Imgur“ platformą, turės pateikti „Imgur“ API
raktą. O norint naudotis geolokacijos paslaugomis, gali būti reikalingas „TravelTime“ API raktas. Šių papildomų raktų
nebuvimas netrukdys pagrindiniam nuotraukų konvertavimo procesui, bet apribos prieigą prie atitinkamų išplėstinių funkcijų.
Naudotojai patys atsakingi už visų reikalingų API raktų gavimą ir naudojimą pagal paslaugų teikėjų sąlygas.

