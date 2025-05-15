#set text(lang: "lt", region: "lt")
== Naudotojo sąsajos bibliotekos pasirinkimas<tui-bibliotekos-pasirinkimas>

_Rašė: Arnas Bradauskas._

Kuriant komandinės eilutės programą, ypač interaktyvią, svarbus sprendimas yra naudotojo sąsajos
(angl. _Terminal User Interface_ – TUI) realizavimo būdas. Nors projekto pagrindinis tikslas buvo atvaizduoti
gatvių vaizdus ASCII formatu, reikėjo mechanizmo naudotojo įvesties (navigacijos komandų) apdorojimui ir
informacijos (pvz., pagalbos, būsenos pranešimų) pateikimui. Šiame skyriuje aptariamas TUI sprendimo pasirinkimo
procesas. Kaip minėta ankstesniuose skyriuose, pagrindiniu programos karkasu buvo pasirinktas
„Cats Effect“, todėl TUI sprendimas turėjo derėti prie šios ekosistemos.

=== Pradinis bandymas -- „tui-scala“

Pradiniame etape buvo svarstoma galimybė naudoti egzistuojančią TUI biblioteką, siekiant paspartinti
kūrimo procesą ir pasinaudoti paruoštais naudotojo sąsajos komponentais (langais, lentelėmis, sąrašais).
Buvo pasirinkta išbandyti biblioteką „tui-scala“ @tui-scala-repo. Tai yra „Scala“
kalbai skirta sąsaja (angl. _wrapper_) populiariai „Rust“ kalbos bibliotekai „tui-rs“, kuri siūlo deklaratyvų
būdą kurti sudėtingas terminalo sąsajas.

Tikėtasi, kad „tui-scala“ leis lengvai sukurti struktūrizuotą naudotojo sąsają, galbūt su atskirais langais
informacijai ar navigacijos parinktims.

=== „tui-scala“ apribojimai ir iššūkiai

Tęsiant įgyvendinimą naudojant „tui-scala“, paaiškėjo keletas esminių trūkumų, kurie trukdė pasiekti projekto tikslus:

1. Spalvų palaikymo apribojimai -- svarbiausia problema buvo susijusi su spalvų atvaizdavimu. Norint kuo tiksliau perteikti
   fotografinį vaizdą ASCII formatu, būtina išnaudoti platesnes terminalo spalvų galimybes
   (idealiu atveju – 256 spalvas arba 24 bitų „True Color“). Atliekant bandymus paaiškėjo, kad „tui-scala“
   (arba jos sąsaja su „tui-rs“ tuo metu) efektyviai apribojo spalvų naudojimą iki standartinės 16 spalvų paletės.
   Net bandant nurodyti specifines RGB reikšmes, jos dažnai buvo konvertuojamos į artimiausią atitikmenį iš 16 spalvų rinkinio.
   Tai ženkliai sumažino generuojamo ASCII vaizdo detalumą ir vizualinį patrauklumą.

2. Sąsajos sudėtingumas ir ekrano ploto poreikis -- projekto eigoje tapo aišku, kad pagrindinis prioritetas yra maksimaliai
   išnaudoti terminalo ekrano plotą pačiam ASCII vaizdui. Kuo didesnė skiriamoji geba (simbolių skaičius), tuo detalesnį
   vaizdą galima pavaizduoti. Sudėtingesni „tui-scala“ komponentai (pvz., rėmeliai, atskiri langai meniu) būtų užėmę
   brangų ekrano plotą, kuris galėjo būti panaudotas pačiam vaizdui. Kadangi pagrindinė programos funkcija –
   vaizdo atvaizdavimas, o naudotojo sąveika apsiriboja keliomis paprastomis komandomis (navigacija, pagalba, išėjimas),
   pilnavertės TUI bibliotekos teikiamos galimybės tapo nebereikalingos ir netgi trukdančios.

=== Sprendimas -- nuosavas TUI modulis

Atsižvelgiant į „tui-scala“ apribojimus, buvo priimtas sprendimas atsisakyti išorinės TUI bibliotekos ir sukurti nuosavą,
minimalų TUI modulį, pritaikytą specifiniams projekto poreikiams. Šis modulis remiasi keliomis pagrindinėmis
technologijomis ir principais:

1. Tiesioginis terminalo valdymas su „JLine“ -- buvo panaudota „Java“ biblioteka „JLine“ @jline3-repo.
   Ji suteikia galimybę žemu lygiu sąveikauti su terminalu:
   - Įjungti „raw“ režimą, kuris leidžia nuskaityti kiekvieną klavišo paspaudimą iš karto,
     neatliekant standartinio eilutės buferizavimo ar redagavimo.
   - Tiesiogiai nuskaityti naudotojo įvestį.
   - Valdyti terminalo būseną, pavyzdžiui, išvalyti ekraną naudojant terminalo galimybes.
2. Tiesioginis ANSI spalvų kodų generavimas -- siekiant įveikti 16 spalvų apribojimą, buvo realizuota funkcija,
   kuri tiesiogiai generuoja ANSI escape sekas 24 bitų „True Color“ spalvoms
   (pvz., _\u001B[38;2;r;g;bm_). Tai leido perduoti terminalui tikslią RGB informaciją kiekvienam ASCII simboliui,
   jei terminalo emuliatorius palaiko šį režimą.
3. Efektyvus išvedimas su _BufferedWriter_ -- siekiant optimizuoti viso ekrano perpiešimą (kas vyksta keičiant vaizdą),
   išvedimui į terminalą buvo naudojamas _java.io.BufferedWriter_. Tai leidžia sukaupti visą perpiešiamo ekrano turinį į
   buferį (angl. _buffer_) ir išvesti jį vienu kartu, kas yra efektyviau nei rašyti kiekvieną simbolį ar eilutę atskirai.
4. Minimalizmas -- nuosavas modulis realizuoja tik būtiniausią funkcionalumą -- spalvoto ASCII tinklelio atvaizdavimą,
   ekrano valymą ir klavišų nuskaitymą. Neapkraunama papildomais komponentais, kurių projektui nereikia.

=== Išvada

Nors išorinės TUI bibliotekos, tokios kaip „tui-scala“, siūlo patogius įrankius standartinėms terminalo sąsajoms kurti,
šio projekto specifiniai reikalavimai – ypač poreikis tiksliai valdyti spalvas (daugiau nei 16) ir maksimaliai išnaudoti
ekrano plotą vaizdui – atskleidė jų trūkumus. Sprendimas sukurti nuosavą, minimalų TUI modulį naudojant „JLine“
ir tiesioginį ANSI kodų generavimą, nors ir pareikalavo daugiau pradinio programavimo pastangų,
leido įveikti šiuos apribojimus ir pasiekti norimą rezultatą – spalvotą ASCII gatvės vaizdą, užimantį visą terminalo langą,
su paprastu klaviatūros valdymu.
