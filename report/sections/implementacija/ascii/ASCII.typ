#set text(lang: "lt", region: "lt")

== Nuotraukų konvertavimas į ASCII
=== ASCII

Ascii (angl. _American Standard Code for Information interchange_) yra vienas iš populiariausių teksto simbolių kodavimo formatų,
naudojamas atvaizduoti tekstą kompiuterinėse sistemose ir internete @techtarget-ascii. Šis kodavimo
standartas buvo sukurtas 1963 metais siekiant, jog skritingų gamintojų kompiuterių sistemos galėtų dalintis ir apdoroti
informaciją. ASCII simboliai skirstomi į dvi grupes: spausdinamuosius ir nespausdinamuosius. Spausdinamieji simboliai apima
raides, skaičius, skirybos ženklus bei specialius simbolius, tuo tarpu nespausdinamųjų aibė yra sudaryta iš eilučių
pabaigos ženklų, tabuliacijos simbolių ir t.t. Šiame bakalauriniame darbe daugiausiai dėmesio skirsime
spausdinamiesiems simboliams, kadangi tik iš jų gali būti atvaizduojami įvairūs vaizdai. ASCII standartas pasižymi
paprastu ir kompaktišku simbolių kodavimu, kadangi vienam simboliui reprezentuoti užtenka vos 7 arba 8 bitų,
priklausomai ar naudojama išplėstinių ASCII simbolių aibė. Šis paprastumas ir yra vienas iš didžiausių šio formato minusų,
nes palaikomi yra tik 255 unikalūs simboliai. Tai lėmė, jog 2003 metais standartų organizacija
IETF (angl. _Internet Engineering Task Force_) įvedė naująjį „Unicode“ simbolių kodavimo standartą. Šis standartas pakeitė
ASCII, tačiau naujasis formatas pilnai palaiko ASCII atgalinio suderinamumo pagalba. Nors šiomis
dienomis naudojame „Unicode“ standartą, 255 simbolių rinkinys, anksčiau priklausęs ASCII formatui, vis dar vadinamas ASCII.

=== ASCII menas

ASCII menas tai grafinio dizaino technika, kuria vaizdai atvaizduojami pasitelkiant teksto simbolius. Šios meno formos
pirmieji egzemplioriai užfiksuoti dar prieš ASCII standarto sukūrimą (#ref(<typewriter_art>)).

#figure(
  image("/images/typewriter_art.png", width: 8cm),
  caption: [Spausdinimo mašinėles menas, kūrėjas Julius Nelson 1939m.],
) <typewriter_art>

Vaizdų iš simbolių kūrimo pradžia siejama net ne su kompiuteriais, o su XIX amžiuje plačiai naudojamomis rašymo mašinėlėmis.
Vaizdų sudarymas iš simbolių buvo skatinamas rašymo mašinėlių gamintojų rengiamuose turnyruose @ascii-comic-sans.
Antrasis ASCII meno populiarumo šuolis buvo matomas XX amžiaus viduryje, kai vis daugiau žmonių turėjo prieigą prie pirmųjų
kompiuterių. Žinoma, tais laikais kompiuteriai dar neturėjo grafinių sąsajų, todėl vaizdus reprezentuoti buvo galima tik ASCII
simboliais. Spausdinti ir masiškai platinti teksto simbolių meną kompiuterio pagalba buvo žymiai paprasčiau, nei naudojantis
spausdinimo mašinėle. Tačiau sparčiai populiarėjant grafinėms vartotojo sąsajoms, ASCII menas buvo pakeistas rastrinės grafikos.
Šiomis dienomis ASCII menas naudojimas nišiniuose sistemose ir programose dėl savo stilistinių priežasčių ir nostalgijos.

== Pasiruošimas konvertuoti nuotraukas Į ASCII

=== Nuotraukos proporcijų išlaikymas

Siekiant konvertuoti nuotraukos pikselius į ASCII simbolius, susiduriame su proporcijų išlaikymo problema. Kitaip nei
rastrinėje grafikoje, kurioje nuotraukos atvaizduojamos vienodo pločio ir aukščio pikseliais, teksto simboliai yra nevienodų
dimensijų. Todėl tiesiogiai konvertuojant nuotrauką gausime vaizdą ištemptą vertikaliai. Pavyzdžiui, šrifto stiliaus
„Courrier New“ simbolių dimensijos turi santikį 1:0,6, tai yra plotis sudaro 60% aukščio. Žinoma, teigti apie šį santikį
galime tik dėl to, nes visi šio, konsolėms pritaikyto šrifto stiliaus simbolių plotis yra vienodas. Dėl paprastumo ir
minimaliaus poveikio galutiniam rezultatui buvo laikoma, jog šis santykis yra 1:0,5, kitaip tariant aukštis yra du kartus
didesnis už plotį. Siekiant išspręsti šią problemą būtina du kartus sumažinti vertikalią orginalios nuotraukos rezoliuciją,
galimi keli sprendimo būdai:

- Vertikalios rezoliucijos sumažinimas pašalinant kas antrą nuotraukos pikselių eilutę. Šis metodas yra pats greičiausias,
  nereikalaujantis daug kompiuterio resursų. Išlaikomi aiškūs kraštai, tačiau šios kraštinės ne visais atvejais susijungs
  kaip orginaliame vaizde dėl apdorojimo metų prarandamos informacijos.
- Vertikalios rezoliucijos sumažinimas apskaičiuojant vidurkį tarp gretimų pikselių. Šiuo atveju gretimų pikselių
  reikšmių vidurkiai yra naudojami sukurti naują pikselio reikšmę neprarandant informacijos. Tačiau pagrindinis šio metodo
  minusas yra neryškus kraštų atvaizdavimas, kadangi dažnu atveju kelių visiškai skirtingų pikselių reikšmės yra sumaišomos į vieną.

=== ASCII simbolių dydžio pasirinkimas

Modernūs fotoaparatai geba sukurti labai aukštos rezoliucijos nuotraukas. Šie vaizdai yra sudaryti iš kelių milijonų pikselių.
Konvertuojant kiekvieną nuotraukos pikselį į atskirą ASCII simbolį, gautas rezultatas nesutips į jokį komerciškai prieinamą
ekraną. Šios problemos sprendimas yra elementarus - sumažinti šrifto dydį. Šis sprendimas turi daug teigiamų savybių,
pavyzdžiui, sumažinus šriftą iki pačio mažiausio leidžiamo dydžio, rezultatas dažnu atveju kokybe neatsiliks nuo orginalaus
rastrinio vaizdo. Taip pat, kuo mažesnis yra gaunamas paveiksliukas, tuo lengviau žmogaus smegenys geba atpažinti jo turinį.
Mažesnį plotą užimantys objektai dažniausiai suvokiami per jų formą arba figūrą, o didesni objetai suprantami kaip fonas
@figure-ground-perception. Dėl to suprasti abstraktų paveikslą žiūrint
iš toli yra lengviau, tas pats gali būti pritaikyta ir ASCII menui. Žinoma, mažesnis šriftas ne visada yra geriau. Iš teksto
simbolių kuriamo vaizdo esmė nėra pati aukščiausia kokybė. ASCII menas yra kuriamas dėl stilistinių tikslų. Taigi sumažinti
šrifto dydį galima tik tiek, kol vis dar bus galima įskaityti individualius simbolius. Norint pasiekti optimalų rezultatą
būtina suderinti abu anksčiau aptartus reikalavimus.

=== Nuotraukos reprezentacija pilkos spalvos tonais

ASCII meną galima skirstyti į 2 grupes: spalvotąjį ir nespalvotąjį. Kadangi visi kadrai gaunami iš gatvės lygio platformų
„Google Maps“ ir „Mapillary“ jau bus spalvoti, pasirūpinti reikės tik konvertavimu iš RGB į pilkus atspalvius. Kovertuoti
turėsime kiekvieną nuotraukos pikselį, tai atlikti galima pasitelkus viena iš trijų galimų formulių:

- Svertinis vidurkis – remiasi žmogaus akies jautrumu skirtingoms spalvoms. Kadangi žalia spalva žmogaus akiai atrodo
  šviesiausia, jos koeficientas yra didžiausias. Toliau mažėjimo tvarka seka raudona ir galiausiai mėlyna spalvos.
#align(center)[Y=0.299×R+0.587×G+0.114×B]
- Vidurkis – ši formulė yra pati paprasčiausia. Visos spalvos turi vienodą svorį skaičiuojant pilkos spalvos reikšmę.
#align(center)[Y=(R+G+B)/3]
- Reliatyvus šviesumas - naujesnė svertinio vidurkio formulės atmaina. Kaip ir ankstesnėje formulėje, koeficientai
  apskaičiuoti remiantis akies jautrumu šviesai. Tačiau šįkart atsižvelgiama į modernių vaizduoklių ir ekranų technologijas
  bei naujus tyrimus apie akies šviesos suvokimą.
#align(center)[Y=0.2126×R+0.7152G+0.0722B]

Čia R – raudonos RGB spalvos reikšmė, G - žalios spalvos reikšmė, o B - mėlynos.

=== ASCII simbolių rinkinio pasirinkimas

Tinkamo simbolių rinkinio pasirinkimas yra vienas iš svarbiausių ASCII meno kūrimo etapų. Šis pasirinkimas daro įtaką galutinio
rezultato detalumui, kontrasto intervalui bei įtakoja žmogaus galimybę atpažinti vaizduojamus objektus. ASCII mene šviesumą
reprezentuoti naudojamas simbolių tankis. Jei ASCII meno fonas yra juodas, o simboliai balti, tai simboliai užimantys mažai
vietos reprezentuos tamsias nuotraukos vietas. Tuo tarpu simboliai užimantys didžiąją simboliui leistiną vietą vaizduos
šviesiasias nuotraukos dalis:

- Tarpo simbolis „ “, tankis 0%.
- Taškas „=“, tankis apie 25%.
- Solidus blokas „█“, tankis 100%.

Vienos simbolių aibės tinkančios kiekvienai nuotraukai atvaizduoti nėra. Šis pasirinkimas dažniausiai bus įtakojamas objektų,
kuriuos yra siekiama atvaizduoti. Kuo didesnė ši aibė, tuo detalesnius objektus bus galima atvaizduoti. Šiame projekte
dažnu atveju teks atvaizduoti medžius, todėl detalūs simbolių rinkiniai bus naudojami siekiant kuo detalesnio rezultato.
Pateiktuose pavyzdžiuose (#ref(<charset_comparison>)) bus naudojami šie, paprastas ir išplėstas, simbolių rinkiniai:

- Paprastas simbolių rinkinys `.:-=+\*\#\%\@`
- Išplėstas simbolių rinkinys `.'^\",:;Il!i~+_-?][}{1)(|\\/*tfjrxnuvczXYUJCLQ0OZmwqpdbkhao*#MW&8%B@$`

Kairėje pusėje matome medžio atvaizdą sugeneruotą su išplėstu simbolių rinkiniu, o dešinėje - paprastu. Naudojant paprastąjį
rinkinį gauname atvaizdą, kuriame subjekto detalės skiriasi ryškiai skirtingais atspalviais. Nors detalumo nuotraukoje yra
nedaug, palyginus su išplėstuoju simbolių rinkiniu. Šiame atspalvių skirtumai yra beveik nematomi, visas detalumo pojūtis
sudaromas iš pačių simbolių. Šalutinis šio rinkinio efektas yra labai didelis nuotraukos triukšmingumas (angl. _noise_).

#figure(
  image("/images/charset_comparison.png", width: 15cm),
  caption: [Palyginimas tarp paprasto ir išplėsto simbolių rinkinio.],
) <charset_comparison>

=== Tinkamos duomenų strukūros pasirinkimas

Pradiniame projekto etape pasirinkome naudoti standartinę sąrašo (angl. _list_) duomenų struktūrą dėl jos patogumo ir
funkcinio programavimo paradigmos atitikimo. Programavimo kalboje „Scala“ sąrašas iš tiesų veikia kaip susieto sąrašo
(angl. _linked list_) tipo duomenų struktūra, kuri puikiai tinka funkcinėms operacijoms @scala-list. Ji taip pat užtikrina
nekintamumą (angl. _immutability_), kas atitiko mūsų pradinį projekto dizainą.

Tačiau konvertuojant didelės apimties nuotraukas į ASCII meną, pastebėjome reikšmingą veikimo efektyvumo sumažėjimą. Tai
ypač išryškėjo dirbant su originalios rezoliucijos nuotraukomis. Naudojant sąrašo duomenų struktūrą elementus tenka
pasiekti nuosekliai einant nuo sąrašo pradžios. Su kai kuriais ASCII konvertavimo algoritmais programa užtrukdavo netgi
keliolika sekundžių, kol būdavo sugeneruotas vaizdas. Toks laukimo laikas buvo nepriimtinas, kadangi nefunkciniai
reikalavimai nurodė maksimalų 5 sekundes trunkantį vaizdų apdorojimą.

Sprendžiant šią problemą, nusprendėme naudoti masyvo duomenų struktūrą. „Scala“ masyvas užtikrina O(1) sudėtingumo
prieigą prie bet kurio elemento pagal indeksą. Nors ši duomenų struktūra yra kintama (angl. _mutable_) ir mažiau atitinka
funkcinio programavimo principus, jo našumo privalumai mūsų atveju buvo svarbesni. Perėjimas prie masyvo struktūros
pareikalavo tam tikrų kodo architektūros pakeitimų, tačiau rezultatai buvo įspūdingi.

Po migracijos prie masyvo duomenų struktūros, pastebėjome žymų programos veikimo pagreitėjimą, ypač dirbant su daugiau
skaičiavimų reikalaujančiais algoritmais. Visi algoritmai pradėjo grąžinti rezultatus per mažiau nei vieną sekundę.
Kadangi dėl šio pakeitimo generavimo laikas saugiai tenkino nefunkcinius greitaveikos reikalavimus, buvo nuspręsta
nelygiagretinti nuotraukų konvertavimo į ASCII meną veikimo. Detalius algoritmų greitaveikos rodikliai aprašyti testavimo skyriuje.

== Nuotraukų konvertavimo į ASCII meną algoritmai

=== Įvadas

Ankstesniuose skyriuose aptarėme ASCII standarto pagrindus, ASCII meno istoriją ir svarbiausius pasiruošimo etapus, būtinus
norint kokybiškai konvertuoti skaitmeninę nuotrauką į ASCII meną. Buvo išspręstos proporcijų išlaikymo problemos, aptartas
šrifto dydžio parinkimo klausimas, nuotraukos konvertuotos į pilkų tonų paletę ir pasirinkti tinkami ASCII simbolių rinkiniai,
kurie veikia kaip mūsų „spalvų“ paletė. Dabar pereisime prie pagrindinės konvertavimo proceso dalies – algoritmų, kurie
atlieka faktinį vaizdo duomenų pavertimą teksto simboliais. Pagrindinis iššūkis yra sukurti metodą, kuris kiekvienam nuotraukos
pikseliui (arba pikselių grupei) priskirtų tinkamiausią ASCII simbolį iš pasirinkto rinkinio, atsižvelgiant į to pikselio
šviesumą ar kitas vaizdo savybes. Skirtingi algoritmai naudoja skirtingas strategijas šiam susiejimui atlikti, todėl
gaunami rezultatai gali skirtis savo stiliumi, detalumu ir akcentuojamomis vaizdo ypatybėmis. Šiame skyriuje detaliau
apžvelgsime du pagrindinius metodus, naudojamus nuotraukų konvertavimui į ASCII meną: šviesumo algoritmą, kuris remiasi
tiesioginiu pikselių šviesumo atitikimu simbolių tankiui, ir kraštų atpažinimo algoritmą, kuris siekia išryškinti vaizdo
struktūrą ir kontūrus. Kiekvienas algoritmas turi savo privalumų ir trūkumų, kuriuos aptarsime tolesniuose poskyriuose.

=== Algoritmai
==== Šviesumo algoritmas (angl. _Luminance_)

Šviesumo algoritmas yra vienas pamatinių ir bene dažniausiai taikomų metodų skaitmeninių vaizdų transformavimui į ASCII meną.
Jo pagrindinė idėja yra intuityvi ir tiesiogiai susijusi su tuo, kaip mes vizualiai suvokiame šviesumą ir tamsumą. Algoritmas
veikia remdamasis tiesioginiu atitikimu tarp kiekvieno nuotraukos taško (pikselio) šviesumo lygio ir pasirinkto ASCII
simbolio vizualinio „svorio“ arba „tankio“. Paprastai tariant, tamsesniems vaizdo fragmentams atvaizduoti parenkami simboliai,
kurie užima mažiau vietos arba atrodo „lengvesni“ (pavyzdžiui, taškas „.“, kablelis „,“), tuo tarpu šviesesnės sritys
reprezentuojamos „tankesniais“ ar daugiau ploto padengiančiais simboliais (pvz., dolerių ženklas „\$“, procento ženklas „\%“ ar
net pilnas blokas „█“). Žinoma, šis principas gali būti ir atvirkštinis, jei pasirenkamas šviesus fonas ir tamsūs
simboliai – tuomet tankiausi simboliai atitiks tamsiausias vaizdo dalis.

Norint pritaikyti šį algoritmą, pirmiausia reikia turėti vaizdą, paruoštą pagal anksčiau aptartus principus: konvertuotą
į pilkos spalvos tonų paletę. Tokiame vaizde kiekvienas pikselis nebeturi sudėtingos RGB spalvos informacijos, o yra apibūdinamas
viena skaitine reikšme, nurodančia jo šviesumą. Dažniausiai ši reikšmė svyruoja intervale nuo 0 (visiškai juoda) iki 255
(visiškai balta). Kitas būtinas komponentas yra ASCII simbolių rinkinys, kuris tarnaus kaip mūsų „ASCII paletė“. Svarbu,
kad šis rinkinys būtų iš anksto surikiuotas pagal simbolių vizualinį tankį – nuo mažiausiai tankaus iki tankiausio.
Pavyzdžiui, paprastas rinkinys galėtų būti „.:-=+\*\#\%\@“, kur „.“ yra mažiausio tankio, o „@“ – didžiausio.

Pats konvertavimo procesas vyksta iteruojant per kiekvieną pilkų tonų nuotraukos pikselį. Kiekvienam aplankytam pikseliui
yra nuskaitoma jo šviesumo reikšmė (skaičius tarp 0 ir 255). Ši reikšmė turi būti transformuota į indeksą, atitinkantį
poziciją mūsų surikiuotame ASCII simbolių rinkinyje. Populiariausias ir paprasčiausias būdas tai padaryti yra tiesinis
susiejimas (angl. _linear mapping_) @image-to-ascii. Tarkime, mūsų simbolių rinkinyje yra *N* simbolių. Tuomet visą šviesumo intervalą
[0, 255] galima proporcingai padalinti į *N* dalių. Kiekviena dalis atitiks vieną simbolį. Pikselio šviesumo reikšmę
galima konvertuoti į simbolių rinkinio indeksą naudojant formulę:
#align(center)[`i = floor( L * (N - 1) / 255 ),`]
čia L yra pikselio šviesumo reikšmė, N yra bendras simbolių skaičius pasirinktame ASCII rinkinyje, (N - 1) yra didžiausias
galimas indekso numeris simbolių rinkinyje, dalijymas iš 255 normalizuoja šviesumo reikšmę į intervalą [0, N-1].

Kai kiekvienam pikseliui priskiriamas atitinkamas ASCII simbolis, šie simboliai yra išdėstomi į dvimatę struktūrą,
atkartojančią pradinės nuotraukos matmenis. Eilutės atskiriamos naujos eilutės simboliais („_\\n_“), taip suformuojant
galutinį ASCII meno kūrinį, paruoštą atvaizdavimui ekrane ar faile.

Galutinio rezultato kokybė, naudojant šviesumo algoritmą, labai priklauso nuo kelių veiksnių. Esminę įtaką daro pasirinktas
ASCII simbolių rinkinys. Kuo daugiau simbolių jame yra ir kuo tolygiau pasiskirstęs jų vizualinis tankis tai yra, kuo mažesni
„šuoliai“ tarp gretimų simbolių tankumo, tuo glotnesnius atspalvių perėjimus ir detalesnį vaizdą galima išgauti. Prastai
parinktas rinkinys, kuriame simbolių tankis kinta netolygiai arba kuriame yra mažai simbolių, gali lemti grubų, „laiptuotą“
vaizdą su prarastomis detalėmis. Toliau pateikiamas šviesumo algoritmo pavyzdys naudojant Brailio simbolių rinkinį (#ref(<luminance_example>)).

#figure(
  image("/images/luminance_example.png", width: 15cm),
  caption: [Šviesumo algoritmo pavyzdys naudojant Brailio simbolių rinkinį.],
) <luminance_example>

Šiuo būdu atvaizduojant nuotraukas mažiau tankūs Brailio simboliai padaro spalvų skirtumus ryškesnius, kadangi daugiau
juodo komandinės eilutės fono yra matoma. Toliau bandome konvertuoti naudojant anksčiau aprašytą išplėstąją simbolių aibę (#ref(<luminance_example2>)).

#figure(
  image("/images/luminance_example2.png", width: 15cm),
  caption: [Šviesumo algoritmo pavyzdys naudojant išplėstąjį simbolių rinkinį.],
) <luminance_example2>

Nepaisant galimų trūkumų, šviesumo algoritmas turi akivaizdžių privalumų. Pirmiausia, jis yra konceptualiai paprastas ir
lengvai įgyvendinamas programuojant. Antra, jis yra efektyvus skaičiavimų prasme, nes kiekvieno pikselio apdorojimas
reikalauja tik kelių paprastų aritmetinių operacijų. Dėl šių savybių jis veikia greitai net ir apdorojant didelės raiškos
nuotraukas. Be to, šis metodas gana gerai perteikia bendrą vaizdo šviesumo pasiskirstymą, kas dažnai yra
pagrindinis ASCII meno tikslas.

Vis dėlto, šis paprastumas turi savo kainą. Algoritmas linkęs prarasti smulkias detales ir ypač aštrius kontūrus, nes jis
neanalizuoja pikselio aplinkos ar formų vaizde – kiekvienas pikselis traktuojamas izoliuotai, atsižvelgiant tik į jo paties
šviesumą @ascii-mosaic-rendering. Todėl objektai su sudėtingomis tekstūromis ar ryškiomis ribomis gali atrodyti sulieti. Kaip minėta,
rezultato kokybė kritiškai priklauso nuo simbolių rinkinio – netinkamas rinkinys gali visiškai sugadinti vaizdą.

Apibendrinant, šviesumo algoritmas yra fundamentalus ASCII meno generavimo įrankis, puikiai tinkantis kaip atspirties taškas
arba tais atvejais, kai siekiama greitai gauti bendrą vaizdo įspūdį, perteikiant jo toninius perėjimus. Nors jis gali ne
visada išsaugoti visas detales, jo paprastumas ir efektyvumas daro jį populiariu pasirinkimu daugeliui taikymų.

==== Sobelio kraštų atpažinimo algoritmas (angl. _Sobel edge detection_)

Kontūrų atpažinimo algoritmas siūlo alternatyvų būdą vaizdo konvertavimui į ASCII meną, lyginant su šviesumo atvaizdavimu.
Užuot tiesiogiai konvertavus pikselių šviesumą į simbolius, šis metodas pirmiausia siekia identifikuoti ir pabrėžti vaizdo
kontūrus – linijas ir ribas tarp skirtingų objektų ar sričių. Galutinis ASCII kūrinys tokiu būdu primena eskizą ar linijinį
piešinį, išryškinantį formas, o ne toninius perėjimus. Šis metodas remiasi standartinėmis skaitmeninio vaizdų apdorojimo
technikomis, dažniausiai naudojant filtrus, tokius kaip Sobelio operatorius, siekiant aptikti staigius šviesumo pokyčius vaizde.

Pagrindinė algoritmo idėja yra ta, kad kontūrai vaizde atsiranda ten, kur gretimų pikselių šviesumo reikšmės smarkiai
skiriasi. Algoritmas analizuoja kiekvieno pikselio kaimynystę, kad įvertintų šio šviesumo pokyčio stiprumą arba gradientą.
Ten, kur pokytis yra didelis, laikoma, kad yra kontūras; kur pokytis mažas, pavyzdžiui, lygiuose, vientisos spalvos plotuose - kontūro nėra.

Algoritmo veikimas prasideda, kaip ir šviesumo algoritmo atveju, nuo vaizdo paruošimo – konvertavimo į pilkų atspalvių
paletę. Kiekvienas pikselis čia taip pat apibūdinamas viena šviesumo reikšme. Toliau vykdomi šie žingsniai, siekiant
rasti kraštus nuotraukoje:
- Pasiruošimas ir kraštinių pikselių apdorojimas: pirmiausia patikrinami vaizdo matmenys. Kadangi kontūrų aptikimui naudojamas
  3x3 dydžio filtras (Sobelio operatorius), vaizdas turi būti bent 3 pikselių aukščio ir pločio. Jei vaizdas per mažas,
  algoritmas grąžina originalų vaizdą. Svarbu pažymėti, kad kontūrų skaičiavimas atliekamas tik vidiniams vaizdo pikseliams,
  aplink kuriuos galima suformuoti pilną 3x3 matricą. Pats kraštinis vieno pikselio pločio rėmelis dažniausiai lieka
  neapdorotas – jo pikseliai išlaiko pradinę pilko tono reikšmę.
- Sobelio operatoriaus taikymas: kiekvienam vidiniam pikseliui (x, y) yra išskiriama jo 3x3 matrica. Šiai matricai yra
  pritaikomi du Sobelio filtrai (angl. _kernels_) (CCC https://www.projectrhea.org/rhea/index.php/An_Implementation_of_Sobel_Edge_Detection):
  - sobelX = $mat(-1, 0, 1; -2, 0, 2; -1, 0, 1)$ – aptinka vertikalius kontūrus (pokyčius horizontalia kryptimi).
  - sobelY = $mat(-1, -2, -1; 0, 0, 0; 1, 2, 1)$ – aptinka horizontalius kontūrus (pokyčius vertikalia kryptimi).
- Filtro reikšmių sumavimas: kiekvienas 3x3 matricos pikselio šviesumo reikšmė padauginama iš atitinkamo
  Sobelio filtro elemento, ir visi rezultatai sumuojami. Taip gaunamos dvi reikšmės: gx (gradiento X kryptimi įvertis)
  ir gy (gradiento Y kryptimi įvertis).
- Gradiento stiprumo skaičiavimas: gautos gx ir gy reikšmės parodo, koks stiprus yra šviesumo pokytis atitinkamai
  horizontalia ir vertikalia kryptimis. Bendra kontūro stiprumo reikšmė, apskaičiuojama naudojant Pitagoro teoremą (CCC https://proceedings.informingscience.org/InSITE2009/InSITE09p097-107Vincent613.pdf).
  Gauta reikšmė normalizuojama, kad tilptų į [0, 255] intervalą. Ši reikšmė parodo, kontūro ryškumą tame taške.
- Kraštų invertavimas: algoritmas numato galimybę rezultatą invertuoti. Tai reiškia, kad ryškūs kontūrai gaus mažą reikšmę
  ir bus atvaizduojami tamsiai, o lygūs plotai gaus didelę reikšmę ir bus šviesūs. Tai dažnai yra pageidaujamas efektas
  ASCII mene, nes kontūrai gali būti prastai matomi priklausomai nuo komandinės eilutės fono spalvos.
- Rezultato formavimas: po šių žingsnių gaunamas naujas dvimatis masyvas, kurio kiekvienas elementas atitinka apskaičiuotą
  kontūro stiprumo reikšmę normalizuotą intervale [0, 255].

Galiausiai, programa naudoja šį kontūrų stiprumo masyvą ir konvertuoja jį į ASCII simbolius. Šis konvertavimo etapas yra
identiškas tam, kuris naudojamas šviesumo algoritme, vienintelis skirtumas, jog šįkart formulėje pridėta ir kontūro stiprumo reikšmė:
#align(center)[`i = floor( E * (N - 1) / 255 ),`]
čia E yra pikselio kontūro stiprumo reikšmė (0-255), N yra simbolių skaičius rinkinyje. Kiekvienam pikseliui parenkamas
atitinkamas simbolis, suformuojant galutinį ASCII meno kūrinį.

Rezultato kokybė, naudojant šį kraštų atpažinimo algoritmą, priklauso nuo kelių veiksnių. Sobelio operatorius yra gana
paprastas ir jautrus triukšmui vaizde – atsitiktiniai maži šviesumo svyravimai gali būti klaidingai interpretuojami kaip
kontūrai. Gauti kontūrai taip pat gali būti storesni nei tikėtasi. Kaip ir šviesumo algoritmo atveju, pasirinktas ASCII
simbolių rinkinys yra labai svarbus – jis lemia, kaip bus atvaizduojami skirtingo stiprumo kontūrai. Toliau pateikiamas
rezultatas naudojant išplėstinį simbolių rinkinį (#ref(<sobel_example>)).

#figure(
  image("/images/sobel_example.png", width: 15cm),
  caption: [Sobelio algoritmo pavyzdys naudojant Brailio simbolių rinkinį.],
) <sobel_example>

Kaip matome šis algoritmas išryškino ryškiausius kraštus lyginant su šviesumo algoritmu. Pagrindiniai išryškinti kraštai
yra tarp dangaus ir žemės taip pat aprink suolelį. Tačiau šioje nuotraukoje labai didelio šio algoritmo efekto nesimato,
pabandykime konvertuoti miesto nuotrauką su daugiau ryškių kraštinių (#ref(<sobel_example2>)). Šiame pavyzdyje geriau
atsiskleidžia algoritmo privalumai, žymiai labiau išryškinamos pastatų detalės.

#figure(
  image("/images/sobel_example2.png", width: 15cm),
  caption: [Optimalus Sobelio algoritmo pavyzdys.],
) <sobel_example2>

Apibendrinant, kontūrų išryškinimo algoritmas yra vertinga ASCII meno generavimo technika, ypač tinkama, kai norima pabrėžti
vaizdo struktūrą ir formas, o ne fotorealistišką šviesumo atvaizdavimą. Šis algoritmas gali išryškinti detales, kurios būtų
prarandamos naudojant šviesumo atvaizdavimo algoritmą, ypač jei vaizde yra daug panašaus šviesumo, bet aiškių ribų turinčių plotų.

==== Canny kraštų atpažinimo algoritmas (angl. _Canny edge detection_)

Canny kontūrų išryškinimo algoritmas yra laikomas vienu iš efektyviausių ir plačiausiai naudojamų metodų kontūrams aptikti
skaitmeniniuose vaizduose. Lyginant su paprastesniais metodais, pavyzdžiui, pagrįstais tik Sobelio operatoriumi, Canny
algoritmas siekia ne tik identifikuoti šviesumo pokyčius, bet ir optimizuoti rezultatus pagal tris pagrindinius kriterijus:
tikslų aptikimą (kuo daugiau realių kontūrų aptinkama, kuo mažiau klaidingų), tikslią lokalizaciją (aptikti kontūrai turi
būti kuo arčiau tikrųjų kontūrų vaizde) ir minimalistinį efektą (vienas realus kontūras turi generuoti tik vieną aptiktą
kontūrą). Pritaikytas ASCII meno generavimui, šis algoritmas leidžia sukurti detalius, plonų linijų, „eskizą“ primenančius
vaizdus, potencialiai atvaizduojant ir kontūrų kryptį.

Algoritmo veikimas susideda iš kelių nuoseklių etapų (CCC https://www.educative.io/answers/what-is-canny-edge-detection), kurių kiekvienas remiasi ankstesnio etapo rezultatais:
- Pradinis paruošimas ir triukšmo mažinimas:
  - Kaip ir kitiems vaizdo apdorojimo algoritmams, pirmiausia reikalingas pilkų atspalvių vaizdas, kur kiekvienas pikselis
    turi reikšmę tarp 0 ir 255.
  - Prieš ieškant kontūrų, vaizdas yra apdorojamas 5x5 Gauso filtru. Šio žingsnio tikslas yra sumažinti vaizdo triukšmą,
    kurie galėtų būti klaidingai interpretuojami kaip kontūrai vėlesniuose etapuose. Gauso filtras „sušvelnina“ vaizdą,
    pakeisdamas kiekvieno pikselio reikšmę svertiniu jo ir kaimyninių reikšmių vidurkiu. Didesnis 5x5 dydžio filtras
    leidžia efektyviau sumažinti triukšmą, nors ir šiek tiek labiau sulieja vaizdą. Kraštiniai 2 pikselių pločio
    rėmeliai lieka neapdoroti.
- Gradiento intensyvumo ir krypties radimas:
  - Šiame žingsnyje naudojami 3x3 Sobelio operatoriai (sobelX ir sobelY), kad būtų apskaičiuotas šviesumo pokyčio
    (gradiento) stiprumas ir kryptis kiekvienam sulieto vaizdo pikseliui.
  - Stiprumas parodo, kiek stiprus yra kontūras tame taške. Ji normalizuojama intervale [0, 255].
  - Kryptis parodo kontūro orientaciją. Ši kryptis yra esminė Canny algoritmo dalis, nes ji naudojama vėlesniame etapuose
    siekiant atvaizduoti kraštinių kryptį ASCII simboliais. Kryptis yra supaprastinama į vieną iš keturių pagrindinių
    krypčių: 0° (horizontali), 45° (linkstanti į dešinę), 90° (vertikali) arba 135° (linkstanti į kairę).
  - Rezultatas yra du masyvai: vienas su gradiento reikšmėmis ir kitas su supaprastintomis kryptimis.
- Ne maksimumų slopinimas:
  - Kontūrai, gauti po gradiento skaičiavimo, dažnai būna storesni nei vienas pikselis. Šio etapo tikslas yra suploninti
    šiuos kontūrus iki vieno pikselio pločio linijų.
  - Kiekvienam pikseliui tikrinama jo gradiento reikšmė. Ji lyginama su dviejų kaimyninių pikselių reikšmėmis išilgai
    gradiento krypties, nustatytos ankstesniame žingsnyje. Pavyzdžiui, jei kryptis yra 90° (vertikali), pikselis lyginamas
    su kaimynais viršuje ir apačioje. Tik tie pikseliai, kurių reikšmė yra lokaliai didžiausia (t.y., didesnė arba lygi
    abiejų kaimynų išilgai gradiento krypties reikšmėms), išlaiko savo reikšmę. Visų kitų pikseliai nustatomi į 0. Taip
    užtikrinama, kad kontūro linija būtų kuo plonesnė.
- Trūkumų taisymas:
  - Tai paskutinis ir vienas svarbiausių Canny algoritmo žingsnių, skirtas atskirti tikrus kontūrus nuo triukšmo sukeltų
    artefaktų ir sujungti nutrūkusius kontūrų segmentus.
  - Naudojamos dvi slenkstinės reikšmės: aukšta  ir žema ribos, pikseliai, kurių reikšmė viršija aukštą ribą, iš karto
    laikomi "stipriais" kontūrų taškais ir pažymimi galutine kontūro reikšme. Pikseliai, kurių reikšmė yra tarp žemos ir
    aukštos ribų, laikomi "silpnais" kontūrų taškais. Jie potencialiai gali būti kontūro dalis, bet tik jei yra susiję
    su stipriu kontūru. Pikseliai, kurių reikšmė yra mažesnė už žemąją ribą, atmetami kaip triukšmas.
  - Toliau rekursyviai vykdomas kontūrų sekimas: pradedant nuo stiprių kontūrų taškų, ieškoma greta esančių silpnų taškų.
    Visi silpni taškai, kurie tiesiogiai ar netiesiogiai jungiasi prie stipraus taško taip pat tampa galutinio kontūro dalimi.
    Silpni taškai, kurie neprisijungia prie jokio stipraus kontūro, galiausiai atmetami.
  - Rezultatas: gaunamas galutinis kontūrų žemėlapis, kuriame kontūrai yra ploni, geriau sujungti ir mažiau paveikti triukšmo.
- Galutinis apdorojimas ir konvertavimas į ASCII meną:
  - Gautas kontūrų žemėlapis gali būti invertuojamas, jei norima, kad kontūrai būtų tamsūs šviesiame fone.
  - Tikrinama kiekvieno pikselio kontūro reikšmė. Jei ji pakankamai didelė, kad būtų laikoma kontūru - programa parenka
    specialų ASCII simbolį, atspindintį kontūro kryptį: „-, |, /, \“ arba stipresnius jų variantus „═, ║, ╱, ╲“. Jei
    pikselis nelaikomas kontūru ir yra fono dalis - jis paliekamas tuščias.

Sugeneravus tą patį vaizdą su išplėstiniu simbolių rinkinių gauname (#ref(<canny_example>)) pateiktą rezultatą. Vėlgi
kadangi nuotrauka neturėjo labai daug ryškių kraštinių, rezultatas vaizdo detalumu stipriai atsilieka nuo kitų algoritmų.

#figure(
  image("/images/canny_example.png", width: 15cm),
  caption: [Canny algoritmo pavyzdys naudojant išplėstąjį simbolių rinkinį.],
) <canny_example>

Tačiau, kaip ir su Sobelio algoritmu, tereikia pasirinkti tinkamą nuotrauką su dideliu kiekiu kraštinių, kad šis algoritmas
sužibėtų. Žemiau pateikiama nuotrauka yra optimali pasirinktam algoritmui (#ref(<canny_example2>)).

#figure(
  image("/images/canny_example2.png", width: 15cm),
  caption: [Optimalus Canny algoritmo pavyzdys.],
) <canny_example2>

Canny kraštų atpažinimo algoritmo rezultatas labai priklauso nuo parinktų parametrų: Gauso filtro dydžio ir nuo
slenkstinių ribų reikšmių. Per aukšti slenksčiai gali praleisti svarbius kontūrus, per žemi – įtraukti daug triukšmo.
Pagrindinis šio algoritmo pranašumas, lyginant su Sobelio algortimu, yra geresnis triukšmo valdymas, dėl pradinio Gauso
filtravimo algoritmas yra atsparesnis triukšmui. Algoritmas natūraliai apskaičiuoja kontūro kryptį, kurią galima panaudoti
stilizuotam ASCII atvaizdavimui. Tačiau šie pranašumai yra pasiekiami skaičiavimo laiko kaina, Canny algoritmas yra
gerokai sudėtingesnis ir reikalauja daugiau skaičiavimų nei paprastas šviesumo ar Sobelio algoritmai. Taip pat kaip ir
kiti kontūrų aptikimo metodai, jis praranda informaciją apie lygius plotus ir švelnius atspalvių perėjimus. Apibendrinant,
Canny algoritmas yra pažangus ir galingas įrankis kontūrams išgauti, leidžiantis generuoti detalius ir struktūriškai
tikslius vaizdus iš ASCII simbolių, ypač kai norima pabrėžti formas ir linijas, o ne tik bendrą šviesumą. O galimybė
naudoti kryptinę informaciją suteikia rezultatui unikalumo

==== Papildomi vaizdų konvertavimo į ASCII metodai

Be šviesumo ir kontūrų atpažinimo algoritmų, kurie yra pamatiniai ir plačiausiai taikomi metodai generuojant ASCII meną
iš skaitmeninių vaizdų, egzistuoja ir kiti, netradiciniai ir labiau eksperimentiniai, būdai atlikti šią transformaciją.
Šie metodai dažnai nukrypsta nuo tiesioginio pikselių šviesumo ar aiškiai identifikuojamų struktūrinių linijų atvaizdavimo,
vietoj to siūlydami alternatyvius vaizdo informacijos kodavimo principus. Nors kai kurie iš šių metodų gali turėti įdomių
pritaikymų arba sukurti unikalius vizualinius rezultatus, jie paprastai nėra tokie universalūs kaip anksčiau aptartieji
ir dažnai turi specifinių apribojimų ar geriausiai tinka tik tam tikro tipo vaizdams apdoroti. Jų analizė vis dėlto yra
naudinga, nes praplečia supratimą apie galimas vaizdo konvertavimo į tekstą strategijas ir iššūkius. Šiame skyriuje
apžvelgsime keletą tokių papildomų konvertavimo būdų, kurie gali būti laikomi labiau eksperimentiniais ar nišiniais.

*Brailio rašto algoritmas* yra dar viena technika skaitmeniniams vaizdams konvertuoti į tekstinį meną, tačiau ji veikia iš
esmės skirtingai nei šviesumo ar kontūrų aptikimo algoritmai. Užuot kiekvieną pikselį atvaizdavus vienu ASCII simboliu,
šis metodas grupuoja originalaus vaizdo pikselius į mažus blokus (šiuo atveju, 2x4 pikselių) ir kiekvieną tokį bloką
atitinka vienas specialus Brailio rašto simbolis (CCC https://www.pharmabraille.com/pharmaceutical-braille/the-braille-alphabet/). Brailio simboliai yra sudaryti iš 8 taškų matricos (2 stulpeliai, 4
eilutės). Kiekvienas iš šių 8 taškų gali būti matomas arba nematomas, leidžiant sukurti 2⁸ = 256 skirtingas kombinacijas.
Algoritmas išnaudoja šią savybę, susiedamas kiekvieno taško būseną su atitinkamo pikselio šviesumu 2x4 bloke.

Pagrindinė algoritmo idėja yra tokia:
- Pradinis paruošimas ir slenksčio nustatymas:
  - Kaip įprasta, algoritmas pradeda darbą su vaizdu konvertuotu į pilkus atspalvius. Apskaičiuojamas viso vaizdo
    vidutinis šviesumas.
  - Nustatomas globalus šviesumo slenkstis. Šis slenkstis bus naudojamas sprendžiant, ar konkretus pikselis yra
    pakankamai tamsus, kad atitinkamas Brailio taškas būtų matomas.
- Vaizdo padalijimas į blokus:
  - Originalus vaizdas yra padalijamas į 2 pikselių pločio ir 4 pikselių aukščio blokus. Kadangi kiekvienas toks blokas
    bus atvaizduotas vienu Brailio simboliu, galutinio ASCII vaizdo matmenys bus maždaug perpus mažesni pločio ir keturis
    kartus mažesni aukščio atžvilgiu .
- Brailio simbolio generavimas kiekvienam blokui:
  - Iteruojama per kiekvieną 2x4 pikselių bloką. Kiekvienam blokui apibrėžiamas 8 Brailio taškų išdėstymas.
  - Toliau kiekvienam iš 8 pikselių tame 2x4 bloke tikrinamas jo šviesumas. Jei pikselio šviesumo reikšmė yra mažesnė už
    anksčiau apskaičiuotą globalų slenkstį, laikoma, kad šis pikselis yra tamsus. Tokiu atveju, atitinkamas Brailio taškas
    tampa matomu. Jei pikselio šviesumas yra didesnis ar lygus slenksčiui, atitinkamas Brailio taškas lieka nematomas.
  - Po visų 8 pikselių patikrinimo, gaunamas 8 bitų skaičius, kuris unikaliai atspindi tamsių pikselių išsidėstymą 2x4
    bloke. Šis skaičius tarnauja kaip indeksas.
- Simbolių priskyrimas ir rezultato formavimas:
  - Apskaičiuotas indeksas naudojamas parenkant konkretų Brailio simbolį iš specialiai paruošto simbolių rinkinio. Šis
    rinkinys turi būti sudarytas iš 256 Brailio simbolių.
  - Parinktas Brailio simbolis įrašomas į atitinkamą vietą galutiniame dvimačiame simbolių masyve. Procesas kartojamas
    visiems 2x4 blokams, kol suformuojamas visas Brailio ASCII vaizdas

Nors Brailio algoritmas gali pasiekti didesnį efektyvų detalumą nei šviesumo algoritmas, jis turi reikšmingų apribojimų (#ref(<braille_example>)),
ypač dirbant su sudėtingomis fotografijomis, tokiomis kaip gatvės lygio vaizdai:
- Globalus slenkstis: didžiausias trūkumas yra vieno globalaus slenksčio naudojimas visam vaizdui. Vaizdai su dideliais
  šviesumo skirtumais, pavyzdžiui, ryškus dangus ir tamsūs pastatų šešėliai gatvės scenoje bus prastai atvaizduoti.
  Slenkstis, parinktas pagal vidutinį šviesumą, gali būti per aukštas tamsioms sritims (prarandamos detalės šešėliuose)
  ir per žemas šviesioms sritims (viskas tampa baltais taškais).
- Dvejetainis atvaizdavimas: šis metodas gali reprezentuoti vaizdą kiekviename 2x4 bloke tik dviejose stadijose –
  pikselis yra arba tamsus, arba šviesus. Tai reiškia, kad prarandamas labai didelis kiekis informacijos. Švelnūs
  perėjimai, tekstūros ir šešėliai, būdingi realioms scenoms, negali būti perteikti.
- Nesuderinamumas su spalvotu atvaizdavimu: kadangi vienas šiuo metodu sugeneruoto vaizdo simbolis talpina 8 pikselius,
  prarandame ir spalvų informaciją. Žinoma, galima būtų naudoti šių pikselių spalvų visdurkį, tačiau toks kiekis sumaišytų
  spalvų ne pagerins, o pakenks galutinio rezultato kokybei.

#figure(
  image("/images/braille_example.png", width: 15cm),
  caption: [Canny algoritmo pavyzdys atvaizduojant gatvės lygio vaizdus.],
) <braille_example>

Dėl šių priežasčių, Brailio konvertavimo metodas nėra tinkamas atvaizduoti sudėtingus, daug atspalvių turinčius vaizdus,
tokius kaip peizažai ar gatvių fotografijos. Jis geriausiai tinka monochromatiniams vaizdams, turintiems aiškius kraštus.
Rezultatas bus dar geresnis jei yra aiškus atskyrimas tarp tamsių ir šviesių sričių. Žemiau pateikiama ideali sutuacija,
kurioje būtų naudingas šis konvertavimo metodas (#ref(<ideal_braille_example>)).

#figure(
  image("/images/ideal_braille_example.png", width: 15cm),
  caption: [Idealus vaizdas konvertavimui su Brailio metodu.],
) <ideal_braille_example>

Brailio metodas yra nišinis ASCII reprezentavimo būdas, turintis unikalių privalumų. Tačiau jo pritaikymas šio projekto
ribose yra ribotas dėl ypatingai didelio prarandamos informacijos kiekio atvaizduojant chaotiškas gatvės fotografijas.

*Vieno simbolio užpildymo metodas* yra bene pats minimalistiškiausias. Jo veikimo principas radikaliai skiriasi nuo anksčiau
aptartų algoritmų kadangi šis algoritmas visiškai ignoruoja originalaus vaizdo turinį, išskyrus jo matmenis.

Veikimo Principas:
- Nuskaitomi įvesties vaizdo matmenys – aukštis ir plotis.
- Sukuriama naujas dvimatis simbolių masyvas, turintis lygiai tokius pačius matmenis kaip ir įvesties vaizdas.
- Visa šis masyvas yra užpildomas vienu ir tuo pačiu solidaus bloko „█“ simboliu, kuris pilnai užpildo vienam simboliui
  skirtą vietą.

Tai reiškia, kad nepriklausomai nuo to, kas buvo pavaizduota originalioje nuotraukoje, šio metodo rezultatas visada bus
vientisas stačiakampis, sudarytas iš identiškų simbolių. Iš pirmo žvilgsnio gali atrodyti, kad toks algoritmas yra
bevertis, nes jis neperteikia jokios vizualinės informacijos iš pradinio vaizdo per simbolių variaciją. Tačiau jo tikroji
paskirtis atsiskleidžia specifiniame kontekste - spalvoto ASCII meno generavime. Anksčiau išvardinti veiksmai naudojami
kaip paruošiamasis žingsnis, sukuriant tekstinį „drobės“ pagrindą. Nors patys simboliai yra vienodi, spausdinimo į
komandinę eilutę etape kiekvienam simboliui bus priskiriama spalva, paimta iš atitinkamos originalaus vaizdo vietos.
Tokiu būdu, nors tekstūra yra visiškai vienoda, spalvų variacijos sukuria galutinį vaizdą. Rezultatas primena pikselių
meną (angl. _pixel art_), tik vietoj spalvotų kvadratėlių naudojami spalvoti ASCII simboliai (#ref(<blank_filled_example>)).

#figure(
  image("/images/blank_filled_example.png", width: 15cm),
  caption: [Vieno simbolio užpildymo metodo rezultato pavyzdys.],
) <blank_filled_example>

Privalumai:
- Itin paprastas ir greitas: pats greičiausias ir paprasčiausias šiame projekte naudotas konvertavimo metodas.
- Pilnas vaizdo padengimas: bloko simboliai pilnai užpildo vaizdą, matomas tik minimalus kiekis vienspalvio fono.

Trūkumai:
- Visiškai neinformatyvus be spalvų: pats savaime, be papildomo spalvinimo etapo, algoritmas nesukuria jokio atpažįstamo vaizdo.
- Neišnaudoja ASCII simbolių įvairovės: priešingai nei tradiciniai algoritmai, šis nepasinaudoja skirtingų simbolių vizualiniu
  svoriu ar forma detalių ar tekstūrų perteikimui.

Apibendrinant, vieno simbolio užpildymo metodas yra netradicinis ASCII meno generavimo metodas, kuris pats nesukuria
vaizdo iš šviesumo ar kontūrų, bet tarnauja kaip fundamentalus žingsnis kuriant spalvotą tekstinį meną, kur vizualinė
informacija perteikiama ne per simbolių formą, o per jiems priskiriamas spalvas. Dėl šios priklausomybės nuo vėlesnio
spalvinimo ir visiško pradinio vaizdo tekstūrinės informacijos ignoravimo, jis pagrįstai priskiriamas prie eksperimentinių
ar specializuotų konvertavimo metodų.
