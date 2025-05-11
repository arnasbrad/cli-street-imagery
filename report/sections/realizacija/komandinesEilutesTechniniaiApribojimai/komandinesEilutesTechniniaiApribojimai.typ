#set text(lang: "lt", region: "lt")
== Komandinės eilutės techniniai apribojimai

Kuriant gatvės lygio vaizdų peržiūros programą komandinės eilutės sąsajoje, susiduriama su tam tikrais techniniais
apribojimais, kurie būdingi šiai aplinkai ir daro įtaką tiek programos funkcionalumui, tiek naudotojo patirčiai. Nors
komandinės eilutės pasirinkimas gali turėti savų privalumų, pavyzdžiui, resursų taupymas, automatizavimo galimybės,
prieinamumas specifinėse aplinkose, svarbu aiškiai įvardinti šios technologijos trūkumus kuriamos programos kontekste.

Pagrindinės techninės limitacijos yra šios:
- Apribota įvestis tik klaviatūra:
  - Visa sąveika su programa vyksta tik per klaviatūros komandas ir tekstinę įvestį.
  - Didžioji dalis terminalų nepalaiko pelės ar liečiamojo ekrano įvesties komandų, kurios leistų interaktyvų programos
    valdymą, pavyzdžiui, paslinkti, priartinti tam tikrą tašką paspaudimu. Tai apsunkina intuityvų naršymą ir geografinės
    vietos pasirinkimą, įvedimas reikalauja tikslaus adreso ar koordinačių įrašymo.
- Ribotas spalvų palaikymas:
  - Skirtingi terminalų emuliatoriai ir operacinės sistemos palaiko skirtingą spalvų skaičių, nuo monochrominio vaizdo
    iki 8, 16 ar 256 skirtingų spalvų @terminal-colors.
  - Kuo mažiau spalvų palaiko naudojamas terminalo emuliatorius, tuo daugiau informacijos prarandama atvaizduojant gatvės
    lygio vaizdus komandinėje eilutėje. Pateikto pavyzdžio (#ref(<color_comparison>)) kairėje nuotrauka atvaizduojama
    pasitelkiant tik 16 pagrindinių ANSI spalvų, tuo tarpu dešinėje matomos viso 16,7 milijono RGB spektro spalvų.

    #figure(
      image("/images/color_comparison.png", width: 15cm),
      caption: [ASCII rezultato palyginimas naudojant skirtingą galimų spalvų kiekį.],
    ) <color_comparison>

- Vienodas teksto dydis:
  - Standartinėse komandinės eilutės aplinkose nėra paprasto būdo vienu metu tame pačiame ekrane atvaizduoti tekstą
    keliais skirtingais dydžiais kaip įprasta grafinėse sąsajose.
  - Norint sutalpintį didelės raiškos ASCII vaizdą terminale reikia žymiai sumažinti teksto dydį, tačiau tada sumažės ir
    bus neįskaitomi visi kiti tekstiniai programos elementai. Dėl to projekto kūrimo metu buvo nuspręsta vienu metu
    atvaizduoti arba sugeneruotą ASCII meną, arba pagalbinį, paaiškinamąjį tekstą.
- Tekstinis vaizdų reprezentavimas:
  - Žemėlapiai ir maršrutai turi būti atvaizduojami naudojant tekstinius ASCII ar Unicode simbolius.
  - Toks vaizdavimas yra gerokai mažiau detalus nei vektoriniai ar rastriniai vaizdai grafinėse sąsajose. Sudėtingų gatvės
    lygio fotografijų atvaizdavimas yra labai supaprastintas ir informatyvumu niekada neprilygs originalioms rastrinėms nuotraukoms.
- Grafinių elementų trūkumas:
  - Skirtingi terminalų emuliatoriai nepalaiko įprastų grafinių elementų, tokių kaip jaustukai (angl. _emoji_), krypties
    rodyklės @terminal-unicode-test. Tai apsunkina informacijos perteikimo galimybes ir tenka ieškoti alternatyvų arba išvis atsisakyti tam
    tikrų sprendimų.
- Sudėtingesnė naudotojo sąsaja:
  - Naudotojai turi išmokti specifines programos komandas, jų sintaksę ir parametrus. Nėra vizualių meniu, mygtukų ar
    užuominų, kurios padėtų atrasti funkcijas.
  - Navigacijos užduotys, kurios grafinės sąsajos aplinkoje būtų atliekamos keliais paspaudimais, komandinėje eilutėje
    dažnai reikalauja net kelių komandų įvedimo.
- Ribotos dinaminio atnaujinimo galimybės:
  - Nors įmanoma atnaujinti ekrano turinį iš naujo užkraunant programos turinį, sklandus, realaus laiko vaizdo
    atnaujinimas yra techniškai sudėtingesnis ir dažnai sukelia ekrano mirgėjimą.
  - Tai padaro perėjimą tarp skirtingų gatvės lygio vaizdų mažiau malonų akiai, žinoma, anksčiau minėtas ekrano mirgėjimas
    gali neigiamai įtakoti epilepsija turinčių naudotojų patirtį.

Kaip matome, pasirinkimas kurti komandinės eilutės programą verčia apsvartyti daug naujų aspektų, į kuriuos neturėtume
kreipti dėmesio naudojant grafinę sąsają. Programos veikimas priklauso nuo didelio kiekio kintamųjų, kurių dalis net
neprikauso nuo mūsų -- programos kūrėjų. Programos išvaizda ir elgsena gali nežymiai skirtis priklausomai nuo naudojamo
terminalo emuliatoriaus ir jo konfigūracijos (šriftai, spalvų schemos, Unicode palaikymas). Užtikrinti visiškai vienodą
patirtį visose platformose ir terminaluose yra sudėtinga, tačiau būtina, jog programa galėtų būti paleista ir jos bazinės
funkcijos veiktų teisingai.
