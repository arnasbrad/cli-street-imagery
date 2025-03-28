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
Y=0.299×R+0.587×G+0.114×B
- Vidurkis – ši formulė yra pati paprasčiausia. Visos spalvos turi vienodą svorį skaičiuojant pilkos spalvos reikšmę.
Y=(R+G+B)/3
- Reliatyvus šviesumas - naujesnė svertinio vidurkio formulės atmaina. Kaip ir ankstesnėje formulėje, koeficientai
  apskaičiuoti remiantis akies jautrumu šviesai. Tačiau šįkart atsižvelgiama į modernių vaizduoklių ir ekranų technologijas
  bei naujus tyrimus apie akies šviesos suvokimą.
Y=0.2126×R+0.7152G+0.0722B

Čia R – raudonos RGB spalvos reikšmė, G - žalios spalvos reikšmė, o B - mėlynos. 