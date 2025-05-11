== Papildomai naudotos sąsajos<extra-apis>

Taip pat naudojome dvi papildomas išorines sąsajas, siekiant suteikti papildomo funkcionalumo mūsų programai.

=== „TravelTime“: Geokodavimo ir kelionės laiko analizės platforma

„TravelTime“ @traveltime-homepage yra specializuota platforma, teikianti pažangius įrankius darbui su geografine informacija,
ypač orientuotus į kelionės laikų skaičiavimą ir maršrutizavimą įvairiomis transporto priemonėmis.
Platforma leidžia ne tik nustatyti kelionės trukmę tarp taškų, bet ir generuoti pasiekiamumo zonas, analizuoti maršrutus
bei atlikti kitas su lokacija susijusias operacijas.

Šiame projekte pagrindinis „TravelTime“ platformos panaudojimas yra susijęs su jos teikiama geokodavimo
(angl. _geocoding_) paslauga. Geokodavimas – tai procesas, kurio metu tekstinis adresas (pvz., gatvė, namo numeris, miestas)
yra paverčiamas tiksliomis geografinėmis koordinatėmis (platuma ir ilguma). Ši funkcija yra prieinama per „TravelTime“ programavimo
sąsają (API) @traveltime-api-docs. Mūsų kuriamoje programoje šis funkcionalumas yra kritiškai svarbus vartotojo patogumui:
jis leidžia naudotojui tiesiog įvesti norimos vietos adresą, o programa, pasinaudodama „TravelTime“ paslauga, automatiškai
nustato tos vietos koordinates. Šios koordinatės vėliau naudojamos kitoms programos funkcijoms, pavyzdžiui,
pradinio taško nustatymui žemėlapyje ar gatvės vaizdų paieškai aplink nurodytą adresą.

Sprendimą integruoti būtent „TravelTime“ platformą lėmė keli esminiai veiksniai:
1. Komandos nario patirtis ir prieiga: vienas iš projekto komandos narių (Arnas Bradauskas) dirba „TravelTime“ įmonėje.
   Ši aplinkybė suteikė unikalią galimybę gauti tiesioginę techninę pagalbą, gilesnį API veikimo principų supratimą ir greitesnį
   galimų problemų sprendimą. Tai ypač vertinga vykdant projektą su ribotais laiko ištekliais.
2. Funkcionalumo tikslumas ir patikimumas: „TravelTime“ geokodavimo paslauga pasižymi aukštu tikslumu ir patikimumu, kas
   yra būtina sąlyga norint užtikrinti kokybišką programos veikimą ir teigiamą vartotojo patirtį. Netikslus adresų konvertavimas
   galėtų lemti klaidingą programos elgseną ir prastus rezultatus.
3. Specializuota paskirtis: nors egzistuoja ir kitų geokodavimo paslaugų teikėjų, „TravelTime“ specializacija lokacijos analizės
   srityje ir jų įsipareigojimas teikti kokybiškus geografinius duomenis padarė šią platformą patraukliu pasirinkimu projektui,
   kurio viena iš kerninių funkcijų yra darbas su adresais ir koordinatėmis.

Nors projekte tiesiogiai neišnaudojamos visos „TravelTime“ kelionių laikų skaičiavimo galimybės, jos geokodavimo funkcionalumas tapo
svarbia programos dalimi, užtikrinančia sklandų adresų pavertimą koordinatėmis.

=== „Imgur“: Vaizdų talpinimo ir dalinimosi paslauga

„Imgur“ @imgur-homepage yra plačiai žinoma ir viena populiariausių internetinių platformų, skirta vaizdinei medžiagai
– nuotraukoms, GIF animacijoms ir trumpiems vaizdo klipams – talpinti, saugoti bei ja dalintis su kitais interneto
vartotojais. Platforma pasižymi paprastu naudojimu ir didele vartotojų bendruomene.

Projekto kontekste „Imgur“ platforma yra pasitelkiama kaip išorinė paslauga, leidžianti automatizuoti nuotraukų
įkėlimo procesą ir gauti stabilias, viešai prieinamas nuorodas į įkeltus vaizdus. Šiam tikslui naudojama „Imgur“
programavimo sąsaja (API) @imgur-api-docs, kuri suteikia galimybę programiškai valdyti nuotraukų įkėlimą be tiesioginio
vartotojo įsikišimo į „Imgur“ svetainę. Mūsų programoje ši funkcija veikia taip: sugeneruota ar apdorota nuotrauka
yra automatiškai įkeliama į „Imgur“ serverius. Sėkmingai įkėlus, „Imgur“ API grąžina unikalią, tiesioginę nuorodą į tą nuotrauką.

Gauta nuoroda yra itin svarbi įgyvendinant programos socialinės medijos pasidalinimo funkcionalumą. Kai vartotojas nori
pasidalinti programos turiniu (pvz., įdomiu rastu vaizdu) socialiniuose tinkluose, būtina turėti viešai prieinamą nuorodą į
vaizdą. „Imgur“ suteikta nuoroda užtikrina, kad dalinantis informacija socialiniuose tinkluose būtų korektiškai
atvaizduojama nuotraukos peržiūra (angl. _rich preview_), taip padidinant įrašo patrauklumą ir informatyvumą.

„Imgur“ platformos pasirinkimą šiam tikslui lėmė šie pagrindiniai privalumai:
1. Nemokamas naudojimas ir prieinamumas: „Imgur“ leidžia nemokamai talpinti didelį kiekį vaizdų ir naudotis jos API
   (su tam tikrais užklausų limitais), kas yra ypač aktualu akademinio ar asmeninio projekto kontekste,
   kur biudžetas dažnai yra ribotas arba jo nėra.
2. Paprasta ir gerai dokumentuota API: „Imgur“ API yra palyginti nesudėtinga integruoti į įvairias programavimo kalbas ir platformas.
   Jos dokumentacija yra aiški, o pats integracijos procesas nereikalauja didelių laiko sąnaudų.

Atsižvelgiant į šiuos veiksnius, „Imgur“ buvo pasirinkta kaip efektyvus ir praktiškas sprendimas projekto poreikiams,
susijusiems su automatizuotu nuotraukų talpinimu ir jų nuorodų panaudojimu socialinės medijos integracijose.
