=== Sistemos funkcijos

_Rašė: Ignas Survila._

Ši UML panaudojimo atvejų diagrama (#ref(<use_cases>)) vaizduoja kuriamos programos funkcinius reikalavimus ir pagrindines sąveikas tarp naudotojo
ir programos bei išorinių sistemų: „Mapillary“ API, „Imgur“ API, „TravelTime“ API. Sistema yra padalinta į dvi pagrindines
posistemes -- gatvės vaizdo ir konfigūracijos.

#figure(
  image("/images/useCases.png"),
  caption: [Panaudojimo atvejų diagrama],
) <use_cases>

Aktoriai:
- Naudotojas -- pagrindinis sistemos aktorius – žmogus, kuris sąveikauja su programa per komandinę eilutę, norėdamas
  peržiūrėti gatvės vaizdus, žaisti spėliojimo žaidimą ar konfigūruoti programą.
- „Mapillary“ API -- išorinė sistema, kuri yra naudojama gatvės lygio vaizdams gauti pagal indeksą, koordinates ar adresą.
- „Imgur“ API -- išorinė sistema, naudojama dalinimuisi sugeneruotais ASCII paveiksliukais socialinėje medijoje.
- „TravelTime“ API -- išorinė sistema, naudojama kartu su „Mapillary“ API suteikia naudotojui galimybę ieškoti vietovių pagal
  adresą ar žymias vietas.

Gatvės vaizdo posistemė -- atsakinga už pagrindinį programos veikimą, gatvės vaizdų gavimą bei atvaizdavimą ir programos valdymą.

Pagrindiniai programos paleidimo būdai (tiesiogiai kviečiami naudotojo):
- Paleisti programą naudojant paveiksliuko ID -- naudotojas paleidžia programą nurodydamas konkretų „Mapillary“ vaizdo indeksą,
  taip pasirinkdamas pradinį navigacijos tašką.
- Paleisti programą naudojant koordinatėmis -- naudotojas paleidžia programą nurodydamas geografines koordinates.
- Paleisti programą naudojant lokacijos pavadinimą arba adresą -- naudotojas paleidžia programą nurodydamas vietos pavadinimą
  ar adresą. „TravelTime“ API naudojamas įvestos vietovės geolokacijai.
- Paleisti programą šalies spėliojimo režimu -- naudotojas paleidžia programą specialiu režimu, skirtu žaisti šalies atpažinimo
  žaidimą. Šis režimas papildomai suteikia šalies spėjimo funkciją.

*Pagrindinis panaudojimo atvejis*

Gauti gatvės vaizdą -- šis atvejis be išimties yra naudojamas paleidus programą su bet kuriuo iš keturių paleidimo būdų.
Tai reiškia, kad norint paleisti programą su indeksu, koordinatėmis, adresu ar žaidimo režime, būtinai reikia gauti
gatvės vaizdą. Šis atvejis sąveikauja su „Mapillary“ API.

Veiksmai, papildantys gatvės vaizdo gavimo panaudojimo atvejį (galimi po vaizdo gavimo):
- Naviguoti į šalia esančias vietas -- naudotojas gali judėti į gretimus vaizdus.
- Išsaugoti ASCII paveiksliuką PNG formatu.
- Dalintis paveiksliuku socialinėje medijoje -- naudotojas gali pasidalinti vaizdu socialinėje medijoje naudodamas
  sugeneruotą nuorodą.
- Atidaryti pagalbos meniu.
- Išeiti iš programos.

*Žaidimo režimo funkcionalumas*

Bandyti atspėti šalį -- šis atvejis yra galimas jei programa yra šalies spėliojimo režime. Tai reiškia, kad paleidus žaidimo
režimą, naudotojas gali bandyti atspėti šalį, kurios vaizdas yra pateikiamas programos.

Konfigūracijos posistemė yra atsakinga už programos nustatymų valdymą ir konfigūracinio failo sukūrimą.

*Pagrindinis inicijavimo būdas (tiesiogiai kviečiamas naudotojo)*

Paleisti konfigūravimo scenarijų -- naudotojas inicijuoja programos konfigūravimo procesą. Pirmąkart paleidus konfigūravimo
scenarijų, programa automatiškai įdiegs trūkstamus paketus, priklausomai nuo naudojamos operacinės sistemos.

Konfigūravimo veiksmai (vykdomi konfigūravimo scenarijaus metu):
- „Mapillary“ API rakto įvedimas -- naudotojo prašoma įvesti „Mapillary“ raktą, tai yra standartinė ir būtina konfigūravimo dalis.
- „Imgur“ API ir „TravelTime“ API raktų įvedimas -- naudotojo klausiama, ar šis nori įvesti papildomus raktus. Šie raktai
  nėra būtini programos veikimui.

Parametrų pasirinkimas:
- Pasirinkti ASCII konvertavimo algoritmą -- naudotojas turi pasirinkti norimą vaizdų konvertavimo algoritmą.
- Pasirinkti rezoliucijos mažinimo koeficientą -- naudotojas turi pasirinkti koeficientą, kurio reikšme yra sumažinama
  pateikiamų ASCII vaizdų rezoliucija. Šis veiksmas yra būtinas, nes kitu atveju vaizdas gali netipti į ekraną.
- Pasirinkti spalvų filtrą -- naudotojas gali pasirinkti spalvų filtrą, variantai apima kontrasto didinimo, pagalbos spalvų
  neskiriantiems žmonėms filtrus.
- Pasirinkti simbolių rinkinį -- naudotojas privalo pasirinkti simbolių rinkinį, iš kurio bus sudarytas galutinis ASCII vaizdas.

Konfigūracijos failo valdymas (papildo konfigūravimo scenarijaus paleidimo panaudojimo atvejį):
- Išsaugoti suformuotą konfigūracinį failą -- naudotojas gali išsaugoti nustatymus į CONF failą.
- Pamatyti suformuotą konfigūracinį failą -- naudotojas gali peržiūrėti esamus nustatymus konfigūraciniame faile.

Diagrama aiškiai parodo, kaip naudotojas gali sąveikauti su sistema paleidžiant ją skirtingais režimais (peržiūros, žaidimo,
konfigūravimo, žaidimo), kokios pagrindinės funkcijos yra prieinamos kiekviename režime (vaizdo gavimas, navigacija,
spėjimas) ir kaip sistema sąveikauja su išorinėmis API paslaugomis („Mapillary“, „Imgur“, „TravelTime“) šioms funkcijoms įgyvendinti.
Ryšiai _<\<Include>>_ ir _<\<Extend>>_ parodo privalomas ir pasirenkamas funkcionalumo dalis.
