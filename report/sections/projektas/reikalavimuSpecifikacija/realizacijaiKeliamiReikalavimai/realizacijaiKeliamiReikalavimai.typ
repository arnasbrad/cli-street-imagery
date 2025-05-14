=== Realizacijai keliami nefunkciniai reikalavimai

Be funkcinių reikalavimų, kurie apibrėžia, ką sistema turi atlikti, lygiai taip pat svarbu apibrėžti, kaip sistema tai
darys ir kokie kokybiniai kriterijai jai keliami. Nefunkciniai reikalavimai nusako esminius programos atributus, tokius
kaip jos naudojimo patogumas, veikimo greitis, patikimumas ir išvaizda. Šiame skyriuje detalizuojami kokybiniai
reikalavimai, keliami kuriamos programos realizacijai, siekiant užtikrinti gerą naudotojo patirtį ir sklandų veikimą.

- Reikalavimai sistemos išvaizdai:
  - Nors programa veikia terminale ir neturi grafinės sąsajos, jos išvesties – ASCII meno – vizualinė kokybė yra svarbi.
    Sugeneruotas ASCII paveikslėlis turi vizualiai atpažįstamai atspindėti pagrindinius originalios nuotraukos kontūrus
    ir šviesumo lygius.
  - Programos pateikiami pranešimai turi būti aiškiai matomi, vizualiai išsiskirti iš bendro programos vaizdo.
- Reikalavimai panaudojamumui:
  - Programos paleidimas bus valdomas per komandinės eilutės argumentus. Argumentų sintaksė turi būti aiški ir logiška.
  - Pagalbos funkcija turi būti prieinama bet kuriuo programos veikimo momentu.
  - Klaidų pranešimai turi būti informatyvūs, nurodantys problemos priežastį (pvz., "Failas nerastas", "Nepalaikomas nuotraukos formatas", "Neteisingas argumentas").
  - Programa turi turėti numatytuosius nustatymus, kad ją būtų galima naudoti pateikiant minimalų argumentų skaičių.
- Reikalavimai programos našumui:
  - Programa turi konvertuoti tipinės raiškos 1920x1080 pikselių gatvės lygio nuotrauką per priimtiną laiką, ne ilgiau
    nei 5 sekundės, nepriklausomai nuo naudotojo naudojamos techninės įrangos.
  - Programa neturi naudoti neproporcingai daug operatyviosios atminties (angl. _random access memory_) ar procesoriaus
    resursų. Atminties naudojimas neturėtų drastiškai augti naudojantis programa ilgą laiką.
- Reikalavimai veikimo sąlygoms:
  - Programa turi veikti visose populiariose operacinėse sistemose: „Windows“, „Linux“, „MacOS“.
  - Programos veikimui reikalingos bibliotekos turi būti aiškiai nurodytos dokumentacijoje.
- Reikalavimai sistemos priežiūrai:
  - Programos kodas turi būti pakankamai komentuotas, kad būtų suprantama jo logika ir struktūra.
  - Programos struktūra turėtų būti modulinė, atskiriant skirtingas funkcijas, pavyzdžiui, argumentų apdorojimas,
    nuotraukos nuskaitymas, konvertavimo logika, išvesties formavimas).
  - Programos vystymui turi būti naudojama versijų kontrolės sistema, pavyzdžiui, „Git“.
- Reikalavimai saugumui:
  - Programa turi saugiai apdoroti naudotojo pateiktus API raktus, vengiant jų paviešinimo trečiosioms šalims.
  - Komandinės eilutės argumentai turi būti tinkamai validuojami, siekiant išvengti neplanuotų programos panaudojimo atvejų.
  - Naudojamos išorinės bibliotekos turėtų būti reguliariai atnaujinamos, siekiant ištaisyti žinomas saugumo spragas.
  - Programai paleisti neturi būti reikalingos administratoriaus teisės.
- Kultūriniai-politiniai reikalavimai:
  - Programa turi veikti nepriklausomai nuo sistemos lokalizacijos nustatymų (angl. _locale_).

Baigiant nefunkcinių reikalavimų apžvalgą, svarbu pabrėžti jų įtaką galutiniam produktui. Nors kuriama programa yra
terminalo įrankis, reikalavimai panaudojamumui ir ASCII meno kokybei tiesiogiai lemia naudotojo patirtį. Vykdymo
charakteristikų reikalavimai užtikrina, kad nuotraukų apdorojimas vyktų per priimtiną laiką, neapkraunant sistemos resursų.
Tuo tarpu reikalavimai priežiūrai, saugumui ir veikimo sąlygoms garantuoja programos ilgaamžiškumą, patikimumą ir
pritaikomumą skirtingose aplinkose. Visų šių aspektų visuma formuoja galutinio produkto kokybę ir praktinę vertę.
