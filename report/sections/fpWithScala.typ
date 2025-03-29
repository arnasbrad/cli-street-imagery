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
naudoti lygiagrečioje aplinkoje, nereikia galvoti apie lenktynių sąlygą.

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
