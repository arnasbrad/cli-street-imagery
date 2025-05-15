=== Realizacijai keliami nefunkciniai reikalavimai<realizacijai-keliami-reikalavimai>

_Rašė: Arnas Bradauskas._

Šiame skyriuje apibrėžiami pagrindiniai nefunkciniai reikalavimai, keliami kuriamai sistemai, apimantys jos naudojimo
patogumą, veikimo charakteristikas, aplinkos sąlygas ir kitus svarbius aspektus.

*Reikalavimai panaudojamumui*

- Intuityvi navigacija -- sistema turi leisti naudotojui naršyti (judėti pirmyn/atgal arba į aplinkines lokacijas)
  naudojant aiškius ir lengvai įsimenamus klaviatūros klavišus, įprastus komandinės eilutės aplinkoje
  (pvz., rodyklių klavišai, WASD ar panašiai).
- Aiškus atsakas -- sąsaja turi aiškiai informuoti naudotoją apie dabartinę būseną
  (pvz., vaizdo krovimas, klaida gaunant duomenis iš „Mapillary“ sąsajos).
- Mokymosi paprastumas -- bazinis sistemos naudojimas (paleidimas, pagrindinė navigacija) turėtų būti lengvai perprantamas
  tikslinei auditorijai (komandinės eilutės naudotojams), pateikiant trumpą pagalbos informaciją paleidimo metu
  arba per specialią komandą (pvz., _-\-help_).
- Klaidų apdorojimas -- sistema turi korektiškai apdoroti numatomas klaidas (pvz., „Mapillary“ nepasiekiamumas,
  neteisingos koordinatės, interneto ryšio nebuvimas) ir pateikti naudotojui suprantamą klaidos pranešimą,
  neužlūžtant pačiai programai.

*Reikalavimai vykdymo charakteristikoms*

- Atsako laikas (angl. _response time_) -- nors bendras atsako laikas priklauso nuo „Mapillary“, pati ASCII vaizdo
  generavimo ir atvaizdavimo terminale operacija neturėtų užtrukti ilgiau nei 5 sekundės, kad nesukeltų reikšmingo papildomo
  vėlavimo modernioje techninėje įrangoje po atsakymo gavimo.
- Resursų naudojimas (angl. _resource usage_) -- programa neturėtų nepagrįstai apkrauti sistemos resursų, veikdama kaip
  tipinė komandinės eilutės programa.

*Reikalavimai veikimo sąlygoms*

- Terminalo suderinamumas (angl. _terminal compatibility_) -- sistema turi veikti populiariuose terminalų
  emuliatoriuose, palaikančiuose bent 256 spalvas (pvz., „GNOME Terminal“, „Konsole“, „iTerm2“, „Windows Terminal“),
  pagrindinėse operacinėse sistemose („Linux“, „macOS“, „Windows“).
- Priklausomybė nuo tinklo (angl. _network dependency_) -- veikimui būtinas aktyvus interneto ryšys prieigai
  prie „Mapillary“ programavimo sąsajos.
- Programinės įrangos priklausomybės (angl. _software dependencies_) -- reikalingos priklausomybės (pvz., specifinė JVM
  versija ir panašiai) turi būti aiškiai dokumentuotos.

*Reikalavimai sistemos išvaizdai*

- Vizualinis aiškumas (angl. _visual clarity_) -- ASCII menas, nors ir riboto detalumo, turėtų būti generuojamas
  taip, kad pagrindiniai objektai būtų bent atpažįstami. Spalvų naudojimas (kai palaikoma)
  turėtų didinti aiškumą.
- Sąsajos nuoseklumas (angl. _interface consistency_) -- tekstiniai naudotojo sąsajos elementai
  (pranešimai, meniu, pagalba) turėtų naudoti nuoseklų formatavimą ir stilių visoje programoje.

*Reikalavimai sistemos priežiūrai*

- Kodo struktūra ir skaitymas (angl. _code structure and readability_) -- kodas turi būti logiškai struktūrizuotas
  (pvz., pagal modulius ar klases) ir parašytas laikantis bendrų programavimo gerosios praktikos principų
  (pvz., prasmingi pavadinimai, komentarai sudėtingesnėse vietose), kad būtų lengviau jį suprasti ir modifikuoti ateityje,
  kas ypač svarbu akademiniam darbui.

*Reikalavimai saugumui*

- Išorinės sąsajos raktų apsauga (angl. _API key protection_) -- jei naudojamas „Mapillary“ ar kitokios sąsajos raktas,
  jis neturėtų būti tiesiogiai įkoduotas viešai prieinamame kode. Rekomenduojama naudoti konfigūracijos failą
  ar aplinkos kintamąjį.
- Duomenų privatumas (angl. _data privacy_) -- sistema neturėtų rinkti, saugoti ar perduoti jokių naudotojo asmeninių 
  duomenų, išskyrus tuos, kurie būtini išorinės sąsajos užklausoms (pvz., geografinės koordinatės).

*Teisiniai reikalavimai*

- Išorinės programavimo sąsajos naudojimo sąlygos (angl. _API Terms of Service_) -- sistemos naudojimas turi 
  nepažeisti „Mapillary“ naudojimo sąlygų ir politikos.
- Bibliotekų licencijos (angl. _library licensing_) -- naudojamos trečiųjų šalių bibliotekos turi turėti su
  projekto tikslais (pvz., akademinis, galimai atviras kodas) suderinamas licencijas, ir turi būti laikomasi
  tų licencijų reikalavimų.

Baigiant nefunkcinių reikalavimų apžvalgą, svarbu pabrėžti jų įtaką galutiniam produktui. Reikalavimai panaudojamumui
ir ASCII meno kokybei tiesiogiai lemia naudotojo patirtį. Vykdymo charakteristikų reikalavimai užtikrina, kad nuotraukų
apdorojimas vyktų per priimtiną laiką, neapkraunant sistemos resursų. Tuo tarpu reikalavimai priežiūrai, saugumui ir
veikimo sąlygoms garantuoja programos ilgaamžiškumą, patikimumą ir pritaikomumą skirtingose aplinkose. Visų šių
aspektų visuma formuoja galutinio produkto kokybę ir praktinę vertę.
