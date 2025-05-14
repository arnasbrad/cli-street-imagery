#set text(lang: "lt", region: "lt")
== ASCII spalvų filtrai

ASCII menas, paremtas tekstiniais simboliais vaizdui kurti, nebūtinai reikalauja spalvų. Monochromatinės jo variacijos,
kuriose šviesumo lygiai perteikiami skirtingais simboliais ar jų tankiu, išlieka populiarios ir efektyvios. Vis dėlto,
spalvų integravimas į ASCII meną suteikia papildomą dimensiją, leidžiančią lengviau atpažinti nuotraukoje vaizduojamus
objektus ir detales. Norint užtikrinti optimalų vaizdo aiškumą ir pritaikyti vaizdą platesniam naudotojų ratui,
įskaitant tuos, kurie susiduria su spalvų matymo iššūkiais, į pagalbą pasitelkiami spalvų filtrai.

Vienas iš esminių vaizdo aiškumo aspektų yra kontrastas. Padidinto kontrasto filtras būtent tam ir skirtas. Jis
analizuoja viso vaizdo spalvų pasiskirstymą ir dinamiškai ištempia spalvų gamą kiekviename iš pagrindinių spalvų kanalų
(raudonos, žalios, mėlynos). Tai reiškia, kad tamsiausios spalvos tampa dar tamsesnės, o šviesiausios – šviesesnės. Šis
filtras ypač naudingas tais atvejais, kai originalus vaizdas yra blankus ar jo spalvos pernelyg susiliejusios.

Kita itin reikšminga filtrų grupė yra skirta spalvinio matymo sutrikimų (daltonizmo) kompensavimui. Tokie sutrikimai
kaip deuteranopija (sumažėjęs jautrumas žaliai šviesai), protanopija (sumažėjęs jautrumas raudonai šviesai) ir
tritanopija (sumažėjęs jautrumas mėlynai šviesai) ženkliai paveikia tai, kaip žmogus suvokia spalvas @color-blindness-types.
Dėl šių sutrikimų tam tikros spalvų poros tampa sunkiai atskiriamos, kas gali lemti prastesnę naudotojo patirtį.

Realizuoti daltonizmo filtrai siekia sušvelninti šias problemas. Jų tikslas nėra tiesiog atkurti trūkstamas spalvas,
nes tai yra neįmanoma. Vietoj to, jie modifikuoja esamas vaizdo spalvas taip, kad probleminės spalvų poros būtų
transformuotos į tokias, kurias asmuo su konkrečiu sutrikimu galėtų lengviau atskirti. Tai dažnai pasiekiama keliais būdais:
- Perkeliant informaciją: pavyzdžiui, raudonos ir žalios spalvų skirtumai gali būti subtiliai perkelti į mėlynus ir
  geltonus atspalvius, kuriuos paveikti asmenys paprastai regi geriau.
- Didinant skirtumus: esami spalvų skirtumai gali būti dirbtinai padidinti. Tai veikia kaip kontrasto didinimo filtras,
  tačiau jis pritaikomas tik kelioms spalvoms.

Šie korekciniai filtrai ne tik pagerina vaizdo informatyvumą, bet ir leidžia platesniam žmonių ratui naudotis programa.
Svarbu paminėti, kad visi filtrai, ypač korekciniai, turi intensyvumo parametrą. Tai leidžia naudotojui pačiam reguliuoti
filtro poveikio stiprumą, nes per stipri korekcija kartais gali atrodyti nenatūraliai ir dar labiau pabloginti matomumą
(#ref(<color_filter>)).

#figure(
  image("/images/color_filter.png", width: 15cm),
  caption: [Palyginimas tarp originalių spalvų ir stipraus protanopijos filtro.],
) <color_filter>
