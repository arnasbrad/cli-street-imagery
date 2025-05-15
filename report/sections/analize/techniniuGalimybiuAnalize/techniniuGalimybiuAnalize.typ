#set text(lang: "lt", region: "lt")
== Techninių galimybių analizė<techniniu-galimybiu-analize>

_Rašė: Arnas Bradauskas._

Šiame skyriuje analizuojamos techninės kliūtys ir apribojimai, su kuriais susidurta
kuriant komandinės eilutės sąsają „Street View“ tipo platformai, naudojant ASCII meną vaizdams atvaizduoti.
Analizė apima tiek išorinius veiksnius (pavyzdžiui, priklausomybes nuo trečiųjų šalių paslaugų), tiek vidinius
(pavyzdžiui, pasirinktos technologinės aplinkos apribojimus).

*Prieiga prie „Street View“ duomenų ir API kainodara*

Pradinė idėja -- idealus variantas būtų buvęs naudoti plačiausiai paplitusią ir didžiausią aprėptį turinčią
„Google Street View“ platformą.

Kliūtis -- „Google Maps Platform“ programavimo sąsaja, įskaitant „Street View“ prieigą, neturi nemokamo plano,
tinkamo projekto mastui, o mokami planai viršijo projekto finansines galimybes (arba buvo nepraktiški
nekomerciniam bei eksperimentiniam projektui). Tai tapo esminiu finansiniu ir techniniu barjeru realizuoti pradinę
viziją naudojant „Google“ duomenis.

Sprendimas -- siekiant užtikrinti projekto įgyvendinamumą, buvo pasirinkta alternatyvi platforma – „Mapillary“.
„Mapillary“ programavimo sąsaja siūlė nemokamą prieigos modelį.

Liekamasis apribojimas -- nors „Mapillary“ leido tęsti projektą, jos duomenų aprėptis tam tikrose geografinėse vietovėse
gali būti mažesnė nei „Google Street View“, kas yra techninis apribojimas galutinio produkto naudojimo geografijai.
Taip pat, dėl to kad ši sąsaja yra nemokama, ji nėra ypatingai patikima, pavyzdžiui, ribojamos dėžės (angl. _bounding box_)
užklausos dažnai yra atmetamos dėl per didelio bendro užklausų kiekio -- tenka laukti, kol „Mapillary“ serveriai bus mažiau
naudojami ir bandyti programą leisti iš naujo arba tiesiog naudoti kitokį paleidimo režimą.
Todėl reikalingas patikimas klaidų valdymas arba būtų galima pridėti alternatyvus gatvės lygio vaizdų šaltinį.

*Terminalo aplinkos grafiniai apribojimai*

Kliūtis -- standartinė komandinės eilutės (terminalo) aplinka turi esminių grafinių galimybių apribojimų,
lyginant su grafinėmis naudotojo sąsajomis (angl. _graphical user interface_ arba _GUI_). Tai tiesiogiai paveikė
galimybes atvaizduoti „Street View“ vaizdus ir kurti naudotojo sąsają.

Spalvų palaikymas -- daugelis standartinių terminalų emuliatorių ir populiarių terminalo naudotojo sąsajos
(angl. _terminal user interface_ arba _TUI_) bibliotekų dėl atgalinio suderinamumo arba paprastumo dažnai palaiko ribotą
spalvų paletę (pvz., 16 spalvų) arba neleidžia pilnai išnaudoti modernesnių terminalų galimybių
(pavyzdžiui, 256 spalvų palaikymo).
Tai ženkliai apribotų galimybes tiksliai ir detaliai konvertuoti fotografinius vaizdus į ASCII meną,
išlaikant vizualinį aiškumą, jei būtų pasikliauta tik standartiniais įrankiais.

Šrifto dydžio ir stiliaus variacijos -- terminalai natūraliai nepalaiko skirtingų šrifto dydžių ar stilių
naudojimo viename ekrano lange, kas apsunkino intuityvios ir vizualiai struktūruotos naudotojo sąsajos elementų
(pavyzdžiui, antraščių, mygtukų, informacinių blokų) kūrimą.

Sprendimas ir jo poveikis:
- Spalvoms -- siekiant įveikti standartinių bibliotekų apribojimus ir pagerinti ASCII meno kokybę, buvo sukurtas
  nuosavas TUI komponentas, specialiai pritaikytas išnaudoti platesnes modernių terminalų spalvų galimybes
  (ypač 256 spalvų režimą). Tai leido pasiekti detalesnį vaizdą, tačiau pareikalavo papildomų programavimo pastangų.
- Šriftams bei UI Elementams -- UI elementai, kuriems įprastai būtų naudojami skirtingi šrifto dydžiai
  (pavadinimai, meniu punktai), taip pat buvo realizuoti kaip ASCII menas, leidžiantis vizualiai juos atskirti
  ir struktūruoti sąsają, tačiau padidinant generuojamo vaizdo sudėtingumą.

*Vaizdo reprezentacijos tikslumas*

Kliūtis -- pats fotografinio vaizdo konvertavimas į ASCII meną yra techniškai ribotas procesas.
Nepriklausomai nuo algoritmų, ASCII reprezentacija visada bus ženkliai žemesnės raiškos ir detalumo nei pradinis vaizdas.
Tai yra fundamentalus techninis apribojimas, lemiantis, kad galutinis produktas gali perteikti tik apytikslį vaizdą,
o ne tikslią fotografinę kopiją. Projekto įgyvendinamumas apsiriboja būtent tokio aproksimuoto vaizdo pateikimu.

Našumo aspektas -- dinaminis ASCII meno generavimas ir atvaizdavimas terminale, ypač naviguojant
(t.y., dažnai keičiantis vaizdui), gali atrodyti lėtas. Tačiau pagrindinė vėlavimo priežastis dažniausiai yra
ne pats ASCII meno generavimo procesas (kuris yra sąlyginai greitas modernioje technikoje),
o laukimas, kol bus gautas atsakymas iš „Mapillary“ API. Senesniuose kompiuteriuose
ar lėtesniuose terminaluose pats generavimas taip pat gali prisidėti prie nevisiškai
sklandaus veikimo, kas yra techninis naudojimo patirties apribojimas.

Išvada -- nepaisant identifikuotų techninių kliūčių, susijusių su API prieiga ir jos patikimumu, terminalo aplinkos apribojimais ir
vaizdo konversijos prigimtimi, projektas buvo techniškai įgyvendinamas pasirinkus alternatyvius sprendimus
(pavyzdžiui, „Mapillary“ programavimo sąsaja, nuosavas TUI modulis skirtas geresniam spalvų išnaudojimui)
ir pripažįstant neišvengiamus platformos apribojimus
(ASCII meno detalumo lygį, priklausomybę nuo „Mapillary“ atsako laiko).
Šie sprendimai leido sukurti veikiantį prototipą ar produktą, nors galutinis rezultatas ir skiriasi
nuo hipotetinio idealaus varianto, kuris galėtų būti sukurtas neribojant finansų ar
technologinių platformų galimybių.
