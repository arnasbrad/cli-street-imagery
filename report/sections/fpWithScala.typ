= Funkcinis programavimas su „Scala“

== Apie „Scala“

„Scala“ programavimo kalba, taip pat kaip ir „Java“, yra skirta dirbti su „JVM“
(_Java Virtual Machine_) platforma - tai reiškia, jog programinis kodas yra kompiliuojamas
ne tiesiai į dvejetainį kodą, o į specialų bitų kodą (angl. _bytecode_), kuris
gali veikti bet kokioje operacinėje sistemoje. Taip pat tai reiškia, jog „Scala“
kode galima naudotis ne tik „Scala“ įskiepiais, viskuo, ką mums suteikia „Java“
programavimo kalba, net galime tiesiogiai kreiptis į „Java“ kodą. Tai yra ypač
svarbus privalumas, žinant, jog „Scala“ yra sąlyginai nepopuliari kalba, kurioje
gali trūkti norimų įskiepių.

Ši programavimo kalba išsiskiria nuo kitų funkcinių kalbų tuo, jog ji nėra vien tik
funkcinė. Joje, priešingai nei kalboje kaip „Haskell“, galime kurti kintamas reikšmes,
nors tai nėra rekomenduojama. Taip pat „Scala“ turi klases, bruožus (angl. _trait_) ir
daugybę kitų kodo projektavimo modėlių, kuriuos dažnai matome objektinio stiliaus
kalbose - tai stipriai palengvina darbą žmogui, pirmą kartą bandančiam funkcinį
programavimą.

„Scala“ buvo sukurta 2004 metais Martino Odersky (citata https://www.oreilly.com/library/view/scala-and-spark/9781785280849/005ee526-d74c-4d1e-a1b3-34f6213d5ece.xhtml),
siekiant sukurti modernią programavimo kalbą, kuri apjungtų objektinio ir funkcinio
programavimo privalumus. Vienas didžiausių „Scala“ privalumų yra jos išraiškingumas
– dažnai galima parašyti daugiau funkcionalumo su mažiau kodo eilučių, palyginus
su „Java“ ar kitomis tradicinėmis kalbomis. Štai pavyzdys, kaip paprasčiau gali
atrodyti funkcinis kodas. Šių kodo tikslas - išfiltruoti iš skaičių sąrašo tik tuos
skaičius, kurie dalinasi iš dviejų, ir gauti jų kvadratus.

„Java“ pavyzdys:
```java
List<Integer> result = new ArrayList<>();
for (Integer number : numbers) {
    if (number % 2 == 0) {
        result.add(number * number);
    }
}
```

„Scala“ pavydzys:
```scala
val result = numbers.filter(_ % 2 == 0).map(x => x * x)
```

Toks programavimo stilius, bent mūsų nuomone, yra daug lengviau skaitomas žmogui.
Dideliame projekte tai žymiai palengina svetimo žmogaus kodo skaitymą, o
pati projekto apimtis būna žymiai mažesnė, lyginant su tradicinėmis programavimo
kalbomis.
Taip pat, kadangi nėra naudojamos jokios kintamos reikšmės, tokį kodą yra saugu
naudoti lygiagrečioje aplinkoje, nereikia papildomai galvoti apie lenktynių sąlygas.

Statinis tipizavimas yra dar vienas svarbus „Scala“ bruožas. Nors programuotojui
nebūtina nurodyti kintamųjų tipus (dėl automatinio tipo išvedimo mechanizmo),
kompiliatorius vis tiek aptinka tipo nesuderinamumo klaidas kompiliavimo metu,
o ne vykdymo metu. Tai padeda išvengti daugelio klaidų dar prieš paleidžiant programą.

„Scala“ turi turtingą sintaksę, kuri leidžia kurti aiškias, glaustas ir elegantiškas
duomenų struktūras bei algoritmus. Šabloninis atitikimas (angl. _pattern matching_),
aukštesnės eilės funkcijos, tingi (angl _lazy_) inicializacija ir nemutuojamų duomenų
struktūrų palaikymas – tai tik keletas funkcinio programavimo bruožų,
kuriuos egzistuoja „Scala“ kalboje.

Pramonėje „Scala“ dažnai naudojama didelės apimties duomenų apdorojimo sistemose.
„Apache Spark“ (citata https://www.chaosgenius.io/blog/apache-spark-with-scala/),
vienas populiariausių didelių duomenų apdorojimo karkasų,
yra parašytas būtent „Scala“ kalba. Tokios įmonės kaip „X“, „LinkedIn“ ir „Netflix“
(citata https://sysgears.com/articles/how-tech-giants-use-scala/)
naudoja „Scala“ savo pagrindinėse sistemose dėl jos gebėjimo efektyviai valdyti
lygiagrečias užduotis ir didelius duomenų srautus.

„Scala“ ekosistema taip pat siūlo keletą galingų įrankių ir įskiepių,
tokių kaip „Akka“ (citata https://akka.io/) (aktorių modeliu pagrįsta lygiagretumo sistema),
„Play Framework“ (citata https://www.playframework.com/) (tinklalapių kūrimo karkasas)
ir „Cats“ (citata https://typelevel.org/cats/)
(funkcinio programavimo abstrakcijos). Šios bibliotekos padeda programuotojams
kurti tvarų, testuojamą ir lengvai prižiūrimą kodą.

Nors „Scala“ mokymosi kreivė gali būti šiek tiek statesnė nei kai kurių
kitų programavimo kalbų, jos teikiami privalumai – ypač kuriant sudėtingas,
didelio masto sistemas – dažnai atperka pradinį mokymosi laiką.
Tai yra puikus pasirinkimas programuotojams, norintiems išplėsti savo įgūdžius
ir įsisavinti funkcinio programavimo koncepcijas, išlaikant pažįstamą
objektinio programavimo aplinką.

== „Cats-Effect“ karkasas

Kaip minėjome anksčiau, „Scala“ nėra idealiai funkcinė kalba. Vienas pagrindinis funkcionalumas,
kurio nėra šioje programavimo kalboje, kuris dažnai randamas kitose funkcinėse programavimo kalbose - 
efektų valdymas.

Prieš aiškinantis kaip reikia valdyti šalutinius efektus, reikia suprasti, kas tiksliai yra funkcinis programavimas.
Funkcinis programavimas yra pagrįstas matematinėmis funkcijomis, taigi jomis ir galime pasinaudoti apibūdinant
funkcinio programavimo paradigmą. Štai pažiūrėkime į šią funkciją:

#math.equation[
  f(x) = 3x
]

Tokia funkcija yra tarytum sujungimas tarp dviejų skaičių sarašų. Pavyzdžiui, sąrašas (1, 2, 3) patampa sąrašu (3, 6, 9).
Kieviena įvestis turi vieną ir tik vieną išvestį. Nesvarbu kokia yra išvestis, jai visada bus išvestis (nėra jokių išimčių).
Funkcijos rezultatas yra tiesiogiai išvedamas iš įvesties ir iš nieko daugiau (neskaitant žinoma kitokių konstantų, kaip 3).
Funkcija tik apskaičiuoja išvestį ir nieko daugiau - ji nekeičia kažkokių kitų reikšmių, nesiunčia laiško, neperka obuolių - 
ji tik įvestį paverčia išvestimi. Tai ir yra visa esmė funkcinio programavimo.

Svarbi tokių grynų funkcijų savybė yra referencinis skaidrumas (angl. _referential transparency_).
Tai reiškia, kad bet kurį funkcijos iškvietimą su konkrečiomis įvesties reikšmėmis galima mintyse
(ar net kodo pertvarkymo metu) pakeisti jos rezultatu, nepakeičiant programos elgsenos visumos. Pavyzdžiui, jei žinome,
kad mūsų funkcija f(2) visada grąžina 6, mes galime visur programoje, kur matome f(2), įsivaizduoti tiesiog reikšmę 6.
Tai daro kodą daug lengviau suprantamą, testuojamą ir nuspėjamą, nes funkcijos rezultatas nepriklauso nuo jokių paslėptų
faktorių ar ankstesnių įvykių – tik nuo jos argumentų.

Panagrinėkime kelis pavyzdžius.

```scala
def doSomething(value: Int) = value * 3
```

Štai čia matome funkciniame programavime vadinamą gryną (angl. _pure_) funkciją - ji įvestį paverčiame išvestimi
ir nieko daugiau. Ji yra referenciškai skaidri.
Pasižiūrėkime, kokie pavyzdžiai nebūtų grynos funkcijos ir kaip galėtume jas paversti grynomis funkcijomis.

```scala
def doSomething(value: Int) = 5 / value
```

Ši funkcija dalina iš įvesties - tai reiškia, jog ne kiekvienai reikšmei yra išvestis. T.y. reikšmei 0 išvesties nėra -
programoje įvyks dalybos iš nulio klaida. Tai galima išspręsti pridėję papildomą sąlygą, kuri patikrintų įvestį:

```scala
def doSomething(value: Int) = 
    if (value == 0) 0
    else 5 / value
```

Dabar ši funkcija yra gryna. Galima ir kitaip sugadinti funkcijos grynumą: 

```scala
def doSomething(value: Int) = {
    x++ // Šalutinis kintamos reikšmės padidinimas
    println("Šalutinis spausdinimas") // Šalutinis spausdinimas
    value * 3
}
```

Ši funkcija nebėra gryna, nes ji daro daugiau, nei reikia norint gauti išvestį. Ji pažeidžia referencinį skaidrumą,
nes jos iškvietimas ne tik grąžina reikšmę, bet ir turi šalutinį poveikį (pakeičia x reikšmę, išspausdina tekstą),
todėl negalime jos tiesiog pakeisti rezultatu, neprarasdami šių poveikių. Kitaip tariant, turėtų būti aišku ką daro
funkcija vien iš jos įvesties ir išvesties tipų, net neskaitant pačios funkcijos implementacijos.
Tokie šalutiniai efektai žymiai apsunkina programos klaidų ieškojimą ir kodo supratimą.

Tačiau kai kurios funkcijos negali būti idealiai grynos. Pavyzdžiui, spausdinimas į ekraną ar HTTP užklausa -
abi šios funkcijos priklauso nuo išorinės aplinkos. Jei programa neturi kur spausdinti, ji neveiks. Jei
serveris į kurį siunčiame užklausą neegzistuoja ar neveikia, mūsų programa taip pat neveiks. Šiai problemai spręsti
funkcinėse programavimo kalbose paprastai yra kažkokia forma efektų valdymo.

Efektų valdymas funkciniame programavime yra būdas tvarkyti šalutinius efektus (angl. _side effects_) – procesus, kurie keičia
programos būseną už funkcijos aprėpties ribų, pavyzdžiui, duomenų nuskaitymas ar įrašymas, tinklo operacijos, atsitiktinių
skaičių generavimas ir panašios operacijos. Tradicinėse funkcinėse kalbose šalutiniai efektai yra aiškiai apibrėžiami
ir izoliuojami, kas leidžia programuotojams tiksliai žinoti, kokius poveikius gali turėti jų funkcijos. Tai suteikia
geresnes galimybes testuoti kodą, lengviau suprasti programos veikimą, išvengti netikėtų šalutinių pasekmių bei
nesunkiai valdyti programos klaidas. „Cats-Effect“ (citata https://typelevel.org/cats-effect) karkasas „Scala“
programavimo kalbai įveda šią koncepcija per IO monadą
ir kitus abstrakcijos mechanizmus, kurie leidžia programuotojams apibrėžti ir komponuoti efektus deklaratyviu būdu,
kartu išlaikant griežtą tipų saugumą.

Toliau panagrinėsime koks tikslas yra naudoti efektų valdymo karkasą kaip „Cats-Effect“ bei kokias problemas
jis padeda išspręsti.

Esminė šio karkaso abstrakcija yra pluoštai (angl. _Fibers_) (citata https://typelevel.org/cats-effect/docs/concepts#fibers).
Tai yra „Cats-Effect“ paralelizmo pagrindas. Pluoštai yra lengvos gijos, skirtos reprezentuoti seką veiksmų, kurie
programos veikimo metu galiausiai bus realizuoti. Pluoštai yra ypatingai lengvi - vienas pluoštas užima vos 150 baitų atminties.
Tai reiškia, jog mes galima sukurti dešimtis milijonų pluoštų be jokių problemų. Per daug nelendant į technines detales,
galima jų naudą apibendrinti taip - pluoštai leidžia mums lengvai, be papildomo vargo, valdyti paralelizmą bei suteikia
mums galimybę bet kurį skaičiavimo procesą sustabdyti ar atšaukti, net jei jis jau yra vykdomas.

Šio karkaso konteksta efektas (angl. _effect_) (citata https://typelevel.org/cats-effect/docs/concepts#effects) yra veiksmo
(ar veiksmų) apibrėžimas, kuris bus įvykdytas, kai vyks
kodo vertinimas (angl. _evaluation_). Pagrindinis toks efektas yra IO.

```scala
val spausdintuvas: IO[Unit] = IO.println("Labas, pasauli!")
```

Šiame kodo fragmente reikšmė _spausdintuvas_ yra aprašymas veiksmo, kuris atspausdina tekstą į komandinę eilutę.
Nesvarbu, kiek kartų mes iškviesime šią reikšmę, spausdinimas nebus įvykdytas nė karto, pavyzdžiui:

```scala
printer
printer
printer
```

Šis kodas neišspausdins teksto nė karto, nes mes dar nenurodėme, jog efektą reikia įvykdyti.
Jei nurodytume, jog efektas turi būti įvykdytas, tekstas būtų išspausdintas kiekvieną kartą.
Tai mums leidžia dirbti su bet kokiomis reikšmėmis, net tokiomis kaip _Unit_ (kitose kalbose dažniau naudojamas terminas
yra _void_) taip pat, kaip dirbtume su paprastomis reikšmėmis, kaip _Int_, _String_ ar kitomis - jas galime naudoti,
perpanaudoti, grąžinti naują reikšmę ir panašiai. Tai yra galima todėl, nes mes programiniame kode dirbame ne su pačia
šalutine reikšme, o su jos apibūdinimu.

Dažnas IO monados apibūdinimas skamba taip: IO aprašo transformaciją iš vienos pasaulio būsenos į kitą.
Kiekvienas veiksmas IO viduje yra ne pats veiksmas, o receptas naujai pasaulio būsenai, kuri gautųsi įvykdžius
tą veiksmą. Kaip matome, šitoks apibūdinimas nepažeidžia funkcinio programavimo taisyklių - nebuvo jokių kintamų
reikšmių ar tiesioginių šalutinių efektų pačiame aprašyme, tik dvi atskiros, nekintamos koncepcijos - pasaulis prieš
ir po veiksmo aprašymo.

Tuo tarpu „Scala“ paralelizmo monada „Future“ to negali.

```scala
val spausdintuvas: Future[Unit] = Future(println("Labas, pasauli!"))
```

Kad ir kiek kviestume šia reikšmę, ji išspausdins rezultatą vieną ir tiek vieną kartą, vykdydama efektą iš karto
ją sukūrus. Tai nėra intuityvu, neleidžia mums perpanaudoti reikšmės ateityje ir pažeidžia referencinį skaidrumą
(angl. _referential transparency_) – pagrindinį funkcinio programavimo principą, kurio IO laikosi dėl savo
tingumo (angl. laziness).

Anksčiau minėjome klaidų valdymą. „Cats-Effect“ karkasas mums taip pat suteikia paprastas ir intuityvias sąsajas
valdyti klaidoms, įvykusioms IO monados veiksmų metu. Mes galime saugiai dirbti su galimai klaidą sukeliančiais
efektais naudodami metodus kaip _attempt_ (kuris paverčia rezultatą kurio galime negauti dėl klaidos į _Either_ tipą, kuris
saugo arba rezultatą, arba įvykusią klaidą) arba _handleErrorWith_ (kuris leidžia aprašyti, kaip elgtis klaidos atveju).

```scala
val galimaiKlaidingas: IO[Int] = IO(5 / 0) // Efektas, kuris mes klaidą

val apdorotaKlaida: IO[Int] = galimaiKlaidingas.handleErrorWith { klaida =>
  // Jei įvyko klaida, atspausdiname pranešimą ir grąžiname numatytąją reikšmę
  IO.println(s"Įvyko klaida: ${klaida.getMessage}") *> IO.pure(-1)
}
```

Dar vienas ypatingai patogus dalykas, kurį suteikia šis karkasas, yra resursų valdymas. 
Daugelis šalutinių efektų apima darbą su resursais, kuriuos reikia ne tik atidaryti ar įsigyti, bet ir saugiai uždaryti
ar paleisti, nepriklausomai nuo to, ar operacijos su jais pavyko, ar įvyko klaida (pavyzdžiui, failų skaitytuvai,
duomenų bazių prisijungimai, tinklo lizdai). Rankiniu būdu tai užtikrinti sudėtinga ir linkę į klaidas (resursų nutekėjimą).
„Cats-Effect“ siūlo elegantišką sprendimą – _Resource_ duomenų tipą. Jis aprašo, kaip įsigyti (angl. _acquire_) resursą ir kaip
jį paleisti (angl. _release_).

```scala
import cats.effect._
import java.io._

// Aprašome, kaip saugiai gauti ir uždaryti failo skaitytuvą
def failoSkaitytuvas(kelias: String): Resource[IO, BufferedReader] =
  Resource.make {
    IO(new BufferedReader(new FileReader(kelias))) // Kaip įsigyti
  } { skaitytuvas =>
    IO(skaitytuvas.close()).handleErrorWith(_ => IO.unit) // Kaip paleisti (užtikrintai)
  }

// Naudojame resursą saugiai: .use garantuoja, kad release bus iškviestas
val saugusSkaitymas: IO[String] = failoSkaitytuvas("manoFailas.txt").use { skaitytuvas =>
  IO(skaitytuvas.readLine()) // Darbas su resursu
}
```

Tai užtikrina, jog resursai bus paleisti net jei programoje įvyks klaida, ar ji bus nutraukta rankiniu būdu.

Visos šitos abstrakcijos leidžia mums rašyti lengviau suprantamą, pertvarkomą ir patikimesnį programinį kodą.
Žinant, jog šio projekto dydis bus sąlyginai didelis, o jame daug pašalinių efektų dirbant su komandinės eilutės
spausdinimu, konfigūracinių failų nuskaitymu, išorinių sąsajų bendravimu bei daugybe baitų ir kitokių tipų transformacijų,
šios abstrakcijos mums labai padėjo parašyti patikimai veikiančią programą.
