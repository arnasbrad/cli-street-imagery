#set text(lang: "lt", region: "lt")

= Nuotraukų konvertavimas į ASCII
== ASCII

Ascii (angl. _American Standard Code for Information interchange_) yra vienas iš populiariausių teksto simbolių kodavimo formatų,
naudojamas atvaizduoti tekstą kompiuterinėse sistemose ir internete
(https://www.techtarget.com/whatis/definition/ASCII-American-Standard-Code-for-Information-Interchange). Šis kodavimo
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

== ASCII menas

ASCII menas tai grafinio dizaino technika, kuria vaizdai atvaizduojami pasitelkiant teksto simbolius. Šios meno formos
pirmieji egzemplioriai užfiksuoti dar prieš ASCII standarto sukūrimą (#ref(<typewriter_art>)).

#figure(
  image("/images/typewriter_art.png", width: 8cm),
  caption: [Spausdinimo mašinėles menas, kūrėjas Julius Nelson 1939m.],
) <typewriter_art>

Vaizdų iš simbolių kūrimo pradžia siejama net ne su kompiuteriais, o su XIX amžiuje plačiai naudojamomis rašymo mašinėlėmis.
Vaizdų sudarymas iš simbolių buvo skatinamas rašymo mašinėlių gamintojų rengiamuose turnyruose
(https://direct.mit.edu/books/oa-monograph/5649/From-ASCII-Art-to-Comic-SansTypography-and-Popular).
Antrasis ASCII meno populiarumo šuolis buvo matomas XX amžiaus viduryje, kai vis daugiau žmonių turėjo prieigą prie pirmųjų
kompiuterių. Žinoma, tais laikais kompiuteriai dar neturėjo grafinių sąsajų, todėl vaizdus reprezentuoti buvo galima tik ASCII
simboliais. Spausdinti ir masiškai platinti teksto simbolių meną kompiuterio pagalba buvo žymiai paprasčiau, nei naudojantis
spausdinimo mašinėle. Tačiau sparčiai populiarėjant grafinėms vartotojo sąsajoms, ASCII menas buvo pakeistas rastrinės grafikos.
Šiomis dienomis ASCII menas naudojimas nišiniuose sistemose ir programose dėl savo stilistinių priežasčių ir nostalgijos.

= Pasiruosimas konvertuoti nuotraukas Į ASCII

== Nuotraukos proporcijų išlaikymas

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

== ASCII simbolių dydžio pasirinkimas

Modernūs fotoaparatai geba sukurti labai aukštos rezoliucijos nuotraukas. Šie vaizdai yra sudaryti iš kelių milijonų pikselių.
Konvertuojant kiekvieną nuotraukos pikselį į atskirą ASCII simbolį, gautas rezultatas nesutips į jokį komerciškai prieinamą
ekraną. Šios problemos sprendimas yra elementarus - sumažinti šrifto dydį. Šis sprendimas turi daug teigiamų savybių,
pavyzdžiui, sumažinus šriftą iki pačio mažiausio leidžiamo dydžio, rezultatas dažnu atveju kokybe neatsiliks nuo orginalaus
rastrinio vaizdo. Taip pat, kuo mažesnis yra gaunamas paveiksliukas, tuo lengviau žmogaus smegenys geba atpažinti jo turinį.
Mažesnį plotą užimantys objektai dažniausiai suvokiami per jų formą arba figūrą, o didesni objetai suprantami kaip fonas
(https://link.springer.com/article/10.3758/BF03207416?utm_source=chatgpt.com). Dėl to suprasti abstraktų paveikslą žiūrint
iš toli yra lengviau, tas pats gali būti pritaikyta ir ASCII menui. Žinoma, mažesnis šriftas ne visada yra geriau. Iš teksto
simbolių kuriamo vaizdo esmė nėra pati aukščiausia kokybė. ASCII menas yra kuriamas dėl stilistinių tikslų. Taigi sumažinti
šrifto dydį galima tik tiek, kol vis dar bus galima įskaityti individualius simbolius. Norint pasiekti optimalų rezultatą
būtina suderinti abu anksčiau aptartus reikalavimus.

== Nuotraukos reprezentacija pilkos spalvos tonais

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

== ASCII simbolių rinkinio pasirinkimas

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

= Nuotraukų konvertavimo į ASCII meną algoritmai

== Įvadas

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

== Algoritmai
=== Šviesumo algoritmas (angl. _Luminance_)

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
yra nuskaitoma jo šviesumo reikšmė *L* (skaičius tarp 0 ir 255). Ši reikšmė turi būti transformuota į indeksą, atitinkantį
poziciją mūsų surikiuotame ASCII simbolių rinkinyje. Populiariausias ir paprasčiausias būdas tai padaryti yra tiesinis
susiejimas (angl. _linear mapping_). Tarkime, mūsų simbolių rinkinyje yra *N* simbolių. Tuomet visą šviesumo intervalą
[0, 255] galima proporcingai padalinti į *N* dalių. Kiekviena dalis atitiks vieną simbolį. Pikselio šviesumo reikšmę *L*
galima konvertuoti į simbolių rinkinio indeksą *i* naudojant formulę: `i = floor(L / 256 * N)`. Čia `floor` funkcija
naudojama tam, kad gautume sveikąjį skaičių (indeksą), nes rezultatas gali būti trupmeninis; ji tiesiog nupjauna trupmeninę
dalį, apvalindama žemyn. Svarbu pastebėti, kad daliname iš 256 (o ne 255), kad reikšmė 255 patektų į paskutinio simbolio
intervalą (indeksas N-1), o ne už jo ribų.

Pavyzdžiui, jei naudojame anksčiau minėtą 9 simbolių rinkinį („.:-=+\*\#`\%\@`“, N=11), tuomet pikseliui,
kurio šviesumas L=20 (gana tamsus), apskaičiuotas indeksas būtų „`floor(20 / 256 * 11) = floor(0.859) = 0`“. Tai reiškia, kad
šiam pikseliui bus priskirtas pirmasis simbolis iš rinkinio, t.y., „`.`“. Jei pikselio šviesumas yra L=150 (vidutinis), indeksas
bus „`floor(150 / 256 * 11) = floor(6.44) = 6`“, ir jam bus priskirtas šeštasis simbolis (indeksas 5) – „`#`“. Galiausiai, labai
šviesiam pikseliui su L=250, indeksas būtų „`floor(250 / 256 * 9) = floor(8.78) = 8`“, todėl jam bus priskirtas paskutinis,
tankiausias simbolis „@“.

Kai kiekvienam pikseliui (arba pikselių blokui, jei buvo mažinama rezoliucija) priskiriamas atitinkamas ASCII simbolis,
šie simboliai yra išdėstomi į dvimatę struktūrą, atkartojančią pradinės nuotraukos matmenis. Dažniausiai tai realizuojama
kaip tekstinė eilutė, kurioje eilutės atskiriamos naujos eilutės simboliais („`\n`“), taip suformuojant galutinį ASCII meno
kūrinį, paruoštą atvaizdavimui ekrane ar faile.

Galutinio rezultato kokybė, naudojant šviesumo algoritmą, labai priklauso nuo kelių veiksnių. Esminę įtaką daro pasirinktas
ASCII simbolių rinkinys. Kuo daugiau simbolių jame yra ir kuo tolygiau pasiskirstęs jų vizualinis tankis (t.y., kuo mažesni
"šuoliai" tarp gretimų simbolių tankumo), tuo glotnesnius toninius perėjimus ir detalesnį vaizdą galima išgauti. Prastai
parinktas rinkinys, kuriame simbolių tankis kinta netolygiai arba kuriame yra mažai simbolių, gali lemti grubų, „laiptuotą“
vaizdą su prarastomis detalėmis.

Nepaisant galimų trūkumų, šviesumo algoritmas turi akivaizdžių privalumų. Pirmiausia, jis yra konceptualiai paprastas ir
lengvai įgyvendinamas programuojant. Antra, jis yra skaičiavimų prasme efektyvus, nes kiekvieno pikselio apdorojimas
reikalauja tik kelių paprastų aritmetinių operacijų. Dėl šių savybių jis veikia greitai net ir apdorojant didelės raiškos
nuotraukas. Be to, šis metodas gana gerai perteikia bendrą vaizdo šviesumo pasiskirstymą, kas dažnai yra
pagrindinis ASCII meno tikslas.

Vis dėlto, šis paprastumas turi savo kainą. Algoritmas linkęs prarasti smulkias detales ir ypač aštrius kontūrus, nes jis
neanalizuoja pikselio aplinkos ar formų vaizde – kiekvienas pikselis traktuojamas izoliuotai, atsižvelgiant tik į jo paties
šviesumą. Todėl objektai su sudėtingomis tekstūromis ar ryškiomis ribomis gali atrodyti suplokštinti ar sulieti. Kaip minėta,
rezultato kokybė kritiškai priklauso nuo simbolių rinkinio – netinkamas rinkinys gali visiškai sugadinti vaizdą.

Apibendrinant, šviesumo algoritmas yra fundamentalus ASCII meno generavimo įrankis, puikiai tinkantis kaip atspirties taškas
arba tais atvejais, kai siekiama greitai gauti bendrą vaizdo įspūdį, perteikiant jo toninius perėjimus. Nors jis gali ne
visada išsaugoti visas detales, jo paprastumas ir efektyvumas daro jį populiariu pasirinkimu daugeliui taikymų.

=== Sobel kraštų atpažinimo algoritmas (angl. _Sobel edge detection_)

=== Canny kraštų atpažinimo algoritmas (angl. _Canny edge detection_)