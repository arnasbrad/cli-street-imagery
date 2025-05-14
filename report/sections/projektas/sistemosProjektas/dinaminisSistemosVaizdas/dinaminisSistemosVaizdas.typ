=== Dinaminis sistemos vaizdas

*Pagrindinio funkcionalumo veiklos diagrama*

Šiame skyriuje aprašomas pagrindinis kuriamos programos veikimo ciklas. Pateiktoje veiklos diagramoje vaizduojama
supaprastinta naudojimosi programa eiga (#ref(<activity-diagram>)). Diagramoje nevaizduojamas programos paleidimas ir
konfigūracinio failo kūrimas pasitelkiant vedlio scenarijus.

#figure(
  image("/images/activityDiagram.png"),
  caption: [Pagrindinio programos ciklo veiklos diagrama.],
) <activity-diagram>

Paleidus programą:
- Iškart atvaizduojama pirmoji sugeneruota gatvės lygio nuotrauka.
- Toliau programa pereina į „Laukti naudotojo komandos“ būseną. Toliau, priklausomai nuo naudotojo veiksmų, galimi keli
  keliai:
  - _r_ -- parodyti dabartinę lokaciją dar kartą.
  - _h_ -- parodyti pagalbinį tekstą.
  - _n_ -- parodyti navigavimo pasirinkimus, priklausomai nuo navigavimo tipo:
  - _c_ -- išsaugoti matomą vaizdą kompiuteryje.
  - _s_ -- sugeneruoti paveiksliuko pasidalinimo soc. medijoje nuorodą.
  - _g_ -- parodyti galimus valstybės spėjimo variantus.
  - _q_ -- procesas baigiasi "Pabaiga" taške

Jei naudotojas pasirenka naviguoti gatvės lygio vaizduose, galimi du sąsajų variantai:
- Sėkos -- parodomos galimos judėjimo kryptis, toliau reikia pasirinkti vieną iš jų.
- Atstumo -- parodyti penkias artimiausias lokacijas ir atlikti pasirinkimą.

Jei naudotojas pasirenka spėti šiuo metu atvaizduojamą šalį:
- Parodomos galimos valstybės.
- Naudotojas pasirenkama vieną iš penkių valstybių.
- Priklausomai nuo atsakymo teisingumo:
  - Teisingas spėjimas - rodomas sėkmės pranešimas, galima pasirinkti žaisti toliau.
  - Neteisingas spėjimas - parodomas teisingas atsakymas, programa baigia darbą.
