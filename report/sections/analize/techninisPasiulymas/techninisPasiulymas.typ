#set text(lang: "lt", region: "lt")
== Techninis pasiūlymas

=== Sistemos apibrėžimas

Kuriama sistema yra specializuota komandinės eilutės (angl. _Command line interface_) aplikacija, skirta interaktyviam gatvės lygio panoraminių
vaizdų naršymui. Pagrindinė jos funkcija – gauti geografinės vietovės panoraminį vaizdą per išorinę paslaugą (konkrečiai,
planuojama naudoti „Mapillary“ API), apdoroti gautą vaizdinę medžiagą realiu laiku konvertuojant ją į tekstinį
ASCII formatą, ir atvaizduoti šį rezultatą tiesiogiai vartotojo terminalo lange.

Sistema neapsiribos vien statišku vaizdų rodymu. Ji suteiks vartotojui galimybę interaktyviai naviguoti po virtualią
erdvę: judėti pirmyn ir atgal numanoma kelio kryptimi. Ši navigacija bus valdoma per klaviatūros komandas, pritaikytas
specifinei CLI aplinkai. ASCII konvertavimo procesas bus optimizuotas siekiant ne tik greitaveikos, bet ir kuo aiškesnio
erdvinės informacijos bei objektų kontūrų perteikimo naudojant ribotą simbolių rinkinį.

Be pagrindinių naršymo funkcijų, į sistemą bus integruotas žaidybinis elementas. Programa turės žaidimo režimą funkcionalumu
primenantį populiarų internetinį žaidimą „Geoguessr“ (CCC https://www.geoguessr.com). Šio režimo tikslas – ne tik
pademonstruoti visas programos galimybes (judėjimą, sąveiką), bet ir padaryti pirmąją pažintį su įrankiu įdomesne bei intuityvesne.

Šiame projekte kuriama visa sistema nuo pradžios iki galo: nuo sąsajos su išoriniu API, vaizdų apdorojimo algoritmo,
ASCII atvaizdavimo logikos iki vartotojo sąsajos ir navigacijos valdymo komandinėje eilutėje. Sistema kuriama kaip
savarankiškas įrankis, nereikalaujantis papildomų grafinių bibliotekų ar aplinkų, išskyrus standartinį terminalą.

=== Bendras veiklos tikslas ir pagrįstumas

Pagrindinis šio projekto veiklos tikslas yra ištirti ir praplėsti komandinės eilutės sąsajos (CLI) taikymo ribas,
demonstruojant, kaip sudėtinga vizualinė ir geografinė informacija gali būti interaktyviai pateikiama ir valdoma
netradicinėje, tekstinėje aplinkoje. Siekiama ne tik įrodyti techninį tokios sistemos įgyvendinamumą, bet ir įvertinti
jos potencialų naudojimo patogumą bei praktiškumą specifinei vartotojų grupei – informacinių technologijų profesionalams
ir entuziastams, kurie dažnai dirba terminalo aplinkoje.

Numatoma nauda yra daugiausia nekomercinė:
- Technologinis eksperimentas: Projektas praplės supratimą apie CLI galimybes ir ASCII meno potencialą atvaizduojant
  dinamišką vizualinę informaciją.
- Žmogaus-kompiuterio sąveikos tyrimas: Bus gauta įžvalgų apie vartotojo patirtį sąveikaujant su geografine informacija
  neįprastoje sąsajoje.
- Potencialus nišinis įrankis: Sukurta programa galėtų tapti įdomiu ir galbūt net naudingu įrankiu tiems, kas vertina
  galimybę greitai pasiekti informaciją nepaliekant komandinės eilutės aplinkos.
- Idėjų generavimas: Projektas gali paskatinti naujas idėjas apie alternatyvius duomenų vizualizavimo ir sąveikos būdus.

Nors tiesioginės komercinės naudos ar atsipirkimo nesiekiama, sėkmingas projekto įgyvendinimas gali veikti kaip koncepcijos
įrodymas. O tai gali įkvėpti kitus asmenis pasirinkti nestandartinę komandinės eilutės naudotojo sąsają savo projektams.

Nors tiesioginės komercinės naudos ar finansinio atsipirkimo iš šio projekto nėra tikimasi, jo sėkmingas įgyvendinimas
turės reikšmingą vertę kaip koncepcijos įrodymas (angl. _Proof of concept_). Šis projektas veiks kaip praktinis pavyzdys, paneigiantis
nusistovėjusias nuostatas, jog komandinė eilutė tinka tik paprastoms tekstinėms operacijoms ir griežtai struktūruotiems
duomenims. Šis projektas demonstruoja, kad net vizualiai sudėtinga ir interaktyvi užduotis, kaip realaus laiko gatvių vaizdų naršymas,
gali būti sėkmingai realizuota pasitelkiant ASCII reprezentaciją komandinės eilutės aplinkoje.

Toks precedentas turi potencialą įkvėpti platesnę kūrėjų ir technologijų entuziastų bendruomenę permąstyti komandinės
eilutės dizaino galimybes ir jos taikymo sritis. Tai gali pasireikšti įvairiai: nuo
interaktyvesnių duomenų analizės ir vizualizavimo įrankių kūrimo, vaizdingesnių ir informatyvesnių serverių ar procesų
stebėjimo sąsajų iki prieinamesnių alternatyvų vartotojams, dirbantiems riboto pralaidumo tinkluose ar naudojantiems
specializuotą įrangą. Galiausiai, šis projektas, nors ir nišinis, gali prisidėti prie subtilaus komandinės eilutės suvokimo
pokyčio – iš grynai utilitaraus, kartais bauginančio įrankio į lanksčią, galingą ir potencialiai labai kūrybišką platformą inovacijoms.

=== Egzistuojančių sprendimų analizė


