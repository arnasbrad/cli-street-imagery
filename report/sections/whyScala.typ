#set text(lang: "lt", region: "lt")
= Kodėl „Scala“?<why-scala>

== Įvadas
Programavimo kalbos yra pagrindinis tarpininkas tarp žmogiškos logikos ir mašininio
kodo - jos leidžia programuotojams paversti abstrakčias idėjas
ir problemų sprendimo metodus į instrukcijas, kurias kompiuteriai gali vykdyti.
Tinkamos programavimo kalbos pasirinkimas projektui yra kritinis sprendimas,
kuris daro įtaką kūrimo efektyvumui, sistemos veikimui, priežiūrai ir galiausiai
projekto sėkmei. Šiame skyriuje pateikiamas kontekstas „Scala“ pasirinkimui kaip šios
disertacijos įgyvendinimo kalbai, nagrinėjant platesnį programavimo kalbų kraštovaizdį,
jų evoliuciją ir įvairias paradigmas, kurias jos atstovauja.

== Istorinė programavimo kalbų raida

Manome, jog galima išskirti kelis tipus programavimo kalbų, priklausomai nuo jų
sukūrimo laiko bei paskirties.

=== Mašininis kodas
Ankstyviausi kompiuteriai reikalavo programavimo dvejetainiu
mašininiu kodu – 1 ir 0 sekomis, tiesiogiai atitinkančiomis procesoriaus instrukcijas.
Šis metodas, nors ir tiesiogiai vykdomas aparatinės įrangos, žmogui programuotojui
buvo labai varginantis ir linkęs į klaidas.

Taigi šis programavimo metodas yra sudėtingas, juo parašytas programinis kodas yra
praktiškai kalbant neįskaitomas, bei jį naudojant yra labai lengva padaryti klaidų.
Ar yra bent vienas tikslas programuoti mašininiu kodu? Teoriškai, taip - mašininis kodas
suteikia programuotojui paties žemiausio lygio prieigą prie procesoriaus instrukcijų.
Tai leidžia patyrusiam programuotojui pasiekti didesnę greitaveiką, tikslesnį veikimą
bei mažesnį galutinio failo dydį. Visa tai yra labai naudinga specifinėse situacijose,
kai resursai yra ypatingai riboti. Tačiau net tokiu atveju, dauguma profesionalų
rinktūsi įrankį, suteikiantį šiek tiek daugiau abstrakcijos.

=== Asemblerio kalbos

Asemblerio kalbos pristatė simbolinius mašininių instrukcijų atvaizdavimus,
leidžiančius programuotojams naudoti žmogui suprantamą kodą vietoje dvejetainių
procesoriaus instruktijų.
Nors vis dar glaudžiai susietos su aparatinės įrangos architektūra,
asemblerio kalbos buvo pirmoji abstrakcija nuo mašininio kodo.

Šiais laikais kompiuteriai yra taip stipriai pažengę, jog programinio kodo rašymas
Asemblerio kalbomis dažniausiai yra visiškai nepraktiškas sprendimas. Kai egzistuoja
tiek daug aukštesnio lygio kalbų, Asemblerio kalbos naudojamos tik esant 
