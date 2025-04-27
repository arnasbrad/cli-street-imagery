#set text(lang: "lt", region: "lt")

== ASCII

=== Nuotraukų konvertavimas į ASCII
==== ASCII

Ascii (angl. _American Standard Code for Information interchange_) yra vienas iš populiariausių teksto simbolių kodavimo formatų,
naudojamas atvaizduoti tekstą kompiuterinėse sistemose ir internete
(CCC https://www.techtarget.com/whatis/definition/ASCII-American-Standard-Code-for-Information-Interchange). Šis kodavimo
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

==== ASCII menas

ASCII menas tai grafinio dizaino technika, kuria vaizdai atvaizduojami pasitelkiant teksto simbolius. Šios meno formos
pirmieji egzemplioriai užfiksuoti dar prieš ASCII standarto sukūrimą (#ref(<typewriter_art>)).

#figure(
  image("/images/typewriter_art.png", width: 8cm),
  caption: [Spausdinimo mašinėles menas, kūrėjas Julius Nelson 1939m.],
) <typewriter_art>

Vaizdų iš simbolių kūrimo pradžia siejama net ne su kompiuteriais, o su XIX amžiuje plačiai naudojamomis rašymo mašinėlėmis.
Vaizdų sudarymas iš simbolių buvo skatinamas rašymo mašinėlių gamintojų rengiamuose turnyruose
(CCC https://direct.mit.edu/books/oa-monograph/5649/From-ASCII-Art-to-Comic-SansTypography-and-Popular).
Antrasis ASCII meno populiarumo šuolis buvo matomas XX amžiaus viduryje, kai vis daugiau žmonių turėjo prieigą prie pirmųjų
kompiuterių. Žinoma, tais laikais kompiuteriai dar neturėjo grafinių sąsajų, todėl vaizdus reprezentuoti buvo galima tik ASCII
simboliais. Spausdinti ir masiškai platinti teksto simbolių meną kompiuterio pagalba buvo žymiai paprasčiau, nei naudojantis
spausdinimo mašinėle. Tačiau sparčiai populiarėjant grafinėms vartotojo sąsajoms, ASCII menas buvo pakeistas rastrinės grafikos.
Šiomis dienomis ASCII menas naudojimas nišiniuose sistemose ir programose dėl savo stilistinių priežasčių ir nostalgijos.

=== Pasiruošimas konvertuoti nuotraukas Į ASCII

==== Nuotraukos proporcijų išlaikymas

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

==== ASCII simbolių dydžio pasirinkimas

Modernūs fotoaparatai geba sukurti labai aukštos rezoliucijos nuotraukas. Šie vaizdai yra sudaryti iš kelių milijonų pikselių.
Konvertuojant kiekvieną nuotraukos pikselį į atskirą ASCII simbolį, gautas rezultatas nesutips į jokį komerciškai prieinamą
ekraną. Šios problemos sprendimas yra elementarus - sumažinti šrifto dydį. Šis sprendimas turi daug teigiamų savybių,
pavyzdžiui, sumažinus šriftą iki pačio mažiausio leidžiamo dydžio, rezultatas dažnu atveju kokybe neatsiliks nuo orginalaus
rastrinio vaizdo. Taip pat, kuo mažesnis yra gaunamas paveiksliukas, tuo lengviau žmogaus smegenys geba atpažinti jo turinį.
Mažesnį plotą užimantys objektai dažniausiai suvokiami per jų formą arba figūrą, o didesni objetai suprantami kaip fonas
(CCC https://link.springer.com/article/10.3758/BF03207416?utm_source=chatgpt.com). Dėl to suprasti abstraktų paveikslą žiūrint
iš toli yra lengviau, tas pats gali būti pritaikyta ir ASCII menui. Žinoma, mažesnis šriftas ne visada yra geriau. Iš teksto
simbolių kuriamo vaizdo esmė nėra pati aukščiausia kokybė. ASCII menas yra kuriamas dėl stilistinių tikslų. Taigi sumažinti
šrifto dydį galima tik tiek, kol vis dar bus galima įskaityti individualius simbolius. Norint pasiekti optimalų rezultatą
būtina suderinti abu anksčiau aptartus reikalavimus.

==== Nuotraukos reprezentacija pilkos spalvos tonais

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

==== ASCII simbolių rinkinio pasirinkimas

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

- Paprastas simbolių rinkinys „.:-=+\*\#\%\@“.
- Išplėstas simbolių rinkinys „ .\'\`^\",:;Il!i~+\_-?\]\[}{1)(|\\tfjrxnuvczXYUJCLQ0OZmwqpdbkhao#8\%B\@\$“.

Kairėje pusėje matome medžio atvaizdą sugeneruotą su išplėstu simbolių rinkiniu, o dešinėje - paprastu. Naudojant paprastąjį
rinkinį gauname atvaizdą, kuriame subjekto detalės skiriasi ryškiai skirtingais atspalviais. Nors detalumo nuotraukoje yra
nedaug, palyginus su išplėstuoju simbolių rinkiniu. Šiame atspalvių skirtumai yra beveik nematomi, visas detalumo pojūtis
sudaromas iš pačių simbolių. Šalutinis šio rinkinio efektas yra labai didelis nuotraukos triukšmingumas (angl. _noise_).

#figure(
  image("/images/charset_comparison.png", width: 15cm),
  caption: [Palyginimas tarp paprasto ir išplėsto simbolių rinkinio.],
) <charset_comparison>

=== Nuotraukų konvertavimo į ASCII meną algoritmai

==== Įvadas

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

==== Algoritmai
===== Šviesumo algoritmas (angl. _Luminance_)

Šviesumo algoritmas yra vienas pamatinių ir bene dažniausiai taikomų metodų skaitmeninių vaizdų transformavimui į ASCII meną.
Jo pagrindinė idėja yra intuityvi ir tiesiogiai susijusi su tuo, kaip mes vizualiai suvokiame šviesumą ir tamsumą. Algoritmas
veikia remdamasis tiesioginiu atitikimu tarp kiekvieno nuotraukos taško (pikselio) šviesumo lygio ir pasirinkto ASCII
simbolio vizualinio „svorio“ arba „tankio“. Paprastai tariant, tamsesniems vaizdo fragmentams atvaizduoti parenkami simboliai,
kurie užima mažiau vietos arba atrodo „lengvesni“ (pavyzdžiui, taškas „.“, kablelis „,“), tuo tarpu šviesesnės sritys
reprezentuojamos „tankesniais“ ar daugiau ploto padengiančiais simboliais (pvz., dolerių ženklas „`$`“, procento ženklas „%“ ar
net pilnas blokas „█“). Žinoma, šis principas gali būti ir atvirkštinis, jei pasirenkamas šviesus fonas ir tamsūs
simboliai – tuomet tankiausi simboliai atitiks tamsiausias vaizdo dalis.

Norint pritaikyti šį algoritmą, pirmiausia reikia turėti vaizdą, paruoštą pagal anksčiau aptartus principus: konvertuotą
į pilkos spalvos tonų paletę. Tokiame vaizde kiekvienas pikselis nebeturi sudėtingos RGB spalvos informacijos, o yra apibūdinamas
viena skaitine reikšme, nurodančia jo šviesumą. Dažniausiai ši reikšmė svyruoja intervale nuo 0 (visiškai juoda) iki 255
(visiškai balta). Kitas būtinas komponentas yra ASCII simbolių rinkinys, kuris tarnaus kaip mūsų „ASCII paletė“. Svarbu,
kad šis rinkinys būtų iš anksto surikiuotas pagal simbolių vizualinį tankį – nuo mažiausiai tankaus iki tankiausio.
Pavyzdžiui, paprastas rinkinys galėtų būti „.:-=+\*\#`\%\@`“, kur „.“ yra mažiausio tankio, o „@“ – didžiausio.

Pats konvertavimo procesas vyksta iteruojant per kiekvieną pilkų tonų nuotraukos pikselį. Kiekvienam aplankytam pikseliui
yra nuskaitoma jo šviesumo reikšmė (skaičius tarp 0 ir 255). Ši reikšmė turi būti transformuota į indeksą, atitinkantį
poziciją mūsų surikiuotame ASCII simbolių rinkinyje. Populiariausias ir paprasčiausias būdas tai padaryti yra tiesinis
susiejimas (angl. _linear mapping_) (CCC https://asciieverything.com/ascii-tips/how-does-image-to-ascii-work/). Tarkime, mūsų simbolių rinkinyje yra *N* simbolių. Tuomet visą šviesumo intervalą
[0, 255] galima proporcingai padalinti į *N* dalių. Kiekviena dalis atitiks vieną simbolį. Pikselio šviesumo reikšmę
galima konvertuoti į simbolių rinkinio indeksą naudojant formulę:
#align(center)[`i = floor( L * (N - 1) / 255 ),`]
čia L yra pikselio šviesumo reikšmė, N yra bendras simbolių skaičius pasirinktame ASCII rinkinyje, (N - 1) yra didžiausias
galimas indekso numeris simbolių rinkinyje, dalijymas iš 255 normalizuoja šviesumo reikšmę į intervalą [0, N-1].

Kai kiekvienam pikseliui priskiriamas atitinkamas ASCII simbolis, šie simboliai yra išdėstomi į dvimatę struktūrą,
atkartojančią pradinės nuotraukos matmenis. Dažniausiai tai realizuojama kaip tekstinė eilutė, kurioje eilutės atskiriamos
naujos eilutės simboliais („`\n`“), taip suformuojant galutinį ASCII meno kūrinį, paruoštą atvaizdavimui ekrane ar faile.

Galutinio rezultato kokybė, naudojant šviesumo algoritmą, labai priklauso nuo kelių veiksnių. Esminę įtaką daro pasirinktas
ASCII simbolių rinkinys. Kuo daugiau simbolių jame yra ir kuo tolygiau pasiskirstęs jų vizualinis tankis (t.y., kuo mažesni
„šuoliai“ tarp gretimų simbolių tankumo), tuo glotnesnius toninius perėjimus ir detalesnį vaizdą galima išgauti. Prastai
parinktas rinkinys, kuriame simbolių tankis kinta netolygiai arba kuriame yra mažai simbolių, gali lemti grubų, „laiptuotą“
vaizdą su prarastomis detalėmis.

Nepaisant galimų trūkumų, šviesumo algoritmas turi akivaizdžių privalumų. Pirmiausia, jis yra konceptualiai paprastas ir
lengvai įgyvendinamas programuojant. Antra, jis yra efektyvus skaičiavimų prasme, nes kiekvieno pikselio apdorojimas
reikalauja tik kelių paprastų aritmetinių operacijų. Dėl šių savybių jis veikia greitai net ir apdorojant didelės raiškos
nuotraukas. Be to, šis metodas gana gerai perteikia bendrą vaizdo šviesumo pasiskirstymą, kas dažnai yra
pagrindinis ASCII meno tikslas.

Vis dėlto, šis paprastumas turi savo kainą. Algoritmas linkęs prarasti smulkias detales ir ypač aštrius kontūrus, nes jis
neanalizuoja pikselio aplinkos ar formų vaizde – kiekvienas pikselis traktuojamas izoliuotai, atsižvelgiant tik į jo paties
šviesumą (CCC https://publications.lib.chalmers.se/records/fulltext/215545/local_215545.pdf). Todėl objektai su sudėtingomis tekstūromis ar ryškiomis ribomis gali atrodyti sulieti. Kaip minėta,
rezultato kokybė kritiškai priklauso nuo simbolių rinkinio – netinkamas rinkinys gali visiškai sugadinti vaizdą.

Apibendrinant, šviesumo algoritmas yra fundamentalus ASCII meno generavimo įrankis, puikiai tinkantis kaip atspirties taškas
arba tais atvejais, kai siekiama greitai gauti bendrą vaizdo įspūdį, perteikiant jo toninius perėjimus. Nors jis gali ne
visada išsaugoti visas detales, jo paprastumas ir efektyvumas daro jį populiariu pasirinkimu daugeliui taikymų.

===== Sobelio kraštų atpažinimo algoritmas (angl. _Sobel edge detection_)

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
simbolių rinkinys yra labai svarbus – jis lemia, kaip bus atvaizduojami skirtingo stiprumo kontūrai.

Apibendrinant, kontūrų išryškinimo algoritmas yra vertinga ASCII meno generavimo technika, ypač tinkama, kai norima pabrėžti
vaizdo struktūrą ir formas, o ne fotorealistišką šviesumo atvaizdavimą. Šis algoritmas gali išryškinti detales, kurios būtų
prarandamos naudojant šviesumo atvaizdavimo algoritmą, ypač jei vaizde yra daug panašaus šviesumo, bet aiškių ribų turinčių plotų.

===== Canny kraštų atpažinimo algoritmas (angl. _Canny edge detection_)

Canny kontūrų išryškinimo algoritmas yra laikomas vienu iš efektyviausių ir plačiausiai naudojamų metodų kontūrams aptikti
skaitmeniniuose vaizduose. Lyginant su paprastesniais metodais, pavyzdžiui, pagrįstais tik Sobelio operatoriumi, Canny
algoritmas siekia ne tik identifikuoti šviesumo pokyčius, bet ir optimizuoti rezultatus pagal tris pagrindinius kriterijus:
tikslų aptikimą (kuo daugiau realių kontūrų aptinkama, kuo mažiau klaidingų), tikslią lokalizaciją (aptikti kontūrai turi
būti kuo arčiau tikrųjų kontūrų vaizde) ir minimalistinį efektą (vienas realus kontūras turi generuoti tik vieną aptiktą
kontūrą). Pritaikytas ASCII meno generavimui, šis algoritmas leidžia sukurti detalius, plonų linijų, „eskizą“ primenančius
vaizdus, potencialiai atvaizduojant ir kontūrų kryptį.

Algoritmo veikimas susideda iš kelių nuoseklių etapų, kurių kiekvienas remiasi ankstesnio etapo rezultatais:
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

