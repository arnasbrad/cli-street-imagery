#set text(lang: "lt", region: "lt")

== Realizacijai keliami reikalavimai<realizacijai-keliami-reikalavimai>

Šiame skyriuje apibrėžiami pagrindiniai nefunkciniai reikalavimai, keliami kuriamai sistemai, apimantys jos naudojimo
patogumą, veikimo charakteristikas, aplinkos sąlygas ir kitus svarbius aspektus.

=== Reikalavimai panaudojamumui<reikalavimai-panaudojamumui>

- Intuityvi navigacija: sistema turi leisti vartotojui naršyti (judėti pirmyn/atgal arba į aplinkines lokacijas)
  naudojant aiškius ir lengvai įsimenamus klaviatūros klavišus, įprastus komandinės eilutės aplinkoje
  (pvz., rodyklių klavišai, WASD ar panašiai).
- Aiškus atsakas: sąsaja turi aiškiai informuoti vartotoją apie dabartinę būseną
  (pvz., vaizdo krovimas, klaida gaunant duomenis iš „Mapillary“ sąsajos).
- Mokymosi paprastumas: bazinis sistemos naudojimas (paleidimas, pagrindinė navigacija) turėtų būti lengvai perprantamas
  tikslinei auditorijai (komandinės eilutės naudotojams), pateikiant trumpą pagalbos informaciją paleidimo metu
  arba per specialią komandą (pvz., `--help`).
- Klaidų apdorojimas: sistema turi korektiškai apdoroti numatomas klaidas (pvz., „Mapillary“ nepasiekiamumas,
  neteisingos koordinatės, interneto ryšio nebuvimas) ir pateikti vartotojui suprantamą klaidos pranešimą,
  neužlūžtant pačiai programai.

=== Reikalavimai vykdymo charakteristikoms<reikalavimai-vykdymo-charakteristikoms>

- Atsako laikas (angl. _response time_): nors bendras atsako laikas priklauso nuo „Mapillary“, pati ASCII vaizdo
  generavimo ir atvaizdavimo terminale operacija turėtų būti pakankamai sparti, kad nesukeltų reikšmingo papildomo
  vėlavimo modernioje techninėje įrangoje po atsakymo gavimo.
- Resursų naudojimas (angl. _resource usage_): programa neturėtų nepagrįstai apkrauti sistemos resursų, veikdama kaip
  tipinė komandinės eilutės aplikacija.

=== Reikalavimai veikimo sąlygoms<reikalavimai-veikimo-sąlygoms>

- Terminalo suderinamumas (angl. _terminal compatibility_): sistema turi siekti veikti populiariuose terminalų
  emuliatoriuose, palaikančiuose bent 256 spalvas (pvz., „GNOME Terminal“, „Konsole“, „iTerm2“, „Windows Terminal“),
  pagrindinėse operacinėse sistemose („Linux“, „macOS“, „Windows“).
- Priklausomybė nuo tinklo (angl. _network dependency_): veikimui būtinas aktyvus interneto ryšys prieigai
  prie „Mapillary“ programavimo sąsajos.
- Programinės įrangos priklausomybės (angl. _software dependencies_): reikalingos priklausomybės (pvz., specifinė „JVM“
  versija, „Docker“ ir panašiai) turi būti aiškiai dokumentuotos.

=== Reikalavimai sistemos išvaizdai<reikalavimai-sistemos-isvaizdai>

- Vizualinis aiškumas (angl. _visual clarity_): ASCII menas, nors ir riboto detalumo, turėtų būti generuojamas
  taip, kad pagrindiniai objektai ir erdvės kryptis būtų bent apytiksliai atpažįstami. Spalvų naudojimas (kai palaikoma)
  turėtų didinti aiškumą.
- Sąsajos konsistencija (angl. _interface consistency_): tekstiniai vartotojo sąsajos elementai 
  (pranešimai, meniu, pagalba) turėtų naudoti nuoseklų formatavimą ir stilių visoje aplikacijoje.

=== Reikalavimai sistemos priežiūrai<reikalavimai-sistemos-prieziurai>

- Kodo struktūra ir skaitymas (angl. _code structure and readability_): kodas turi būti logiškai struktūrizuotas
  (pvz., pagal modulius ar klases) ir parašytas laikantis bendrų programavimo gerosios praktikos principų
  (pvz., prasmingi pavadinimai, komentarai sudėtingesnėse vietose), kad būtų lengviau jį suprasti ir modifikuoti ateityje,
  kas ypač svarbu akademiniam darbui.

=== Reikalavimai saugumui<reikalavimai-saugumui>

- Išorinės sąsajos raktų apsauga (angl. _API key protection_): jei naudojamas „Mapillary“ ar kitokios sąsajos raktas,
  jis neturėtų būti tiesiogiai įkoduotas viešai prieinamame kode. Rekomenduojama naudoti konfigūracijos failą
  ar aplinkos kintamąjį.
- Duomenų privatumas (angl. _data privacy_): sistema neturėtų rinkti, saugoti ar perduoti jokių vartotojo asmeninių 
  duomenų, išskyrus tuos, kurie būtini išorinės sąsajos užklausoms (pvz., geografinės koordinatės).

=== Teisiniai reikalavimai<teisiniai-reikalavimai>

- Išorinės programavimo sąsajos naudojimo sąlygos (angl. _API Terms of Service_): sistemos naudojimas turi 
  nepažeisti „Mapillary“ naudojimo sąlygų ir politikos.
- Bibliotekų licencijos (angl. _library licensing_): Naudojamos trečiųjų šalių bibliotekos turi turėti su
  projekto tikslais (pvz., akademinis, galimai atviras kodas) suderinamas licencijas, ir turi būti laikomasi
  tų licencijų reikalavimų.
