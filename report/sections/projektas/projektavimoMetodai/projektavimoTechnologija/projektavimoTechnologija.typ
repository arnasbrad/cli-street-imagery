=== Projektavimo technologija

Dėl anksčiau minėto iteracinio ir eksperimentinio projekto pobūdžio, nebuvo naudojamos specifinės formalios
projektavimo technologijos ar griežti grafiniai žymėjimo standartai (notacijos), tokie kaip UML
(angl. _Unified Modeling Language_) diagramos, visai sistemai aprašyti iš anksto. Sistemos projektas ir architektūra
formavosi palaipsniui, vykstant kūrimo procesui.

Pagrindiniai projektavimo sprendimai ir sistemos struktūra buvo įtvirtinti ir dokumentuoti šiais būdais:
- Kodas kaip dokumentacija -- pati programos kodo struktūra (moduliai, klasės, funkcijos), parinkti pavadinimai 
  ir vidiniai komentarai tarnavo kaip pagrindinis techninio projekto artefaktas. Buvo stengiamasi laikytis
  bendrų programavimo gerosios praktikos principų, kad kodas būtų kuo aiškesnis ir lengviau suprantamas.
- Tekstinė dokumentacija -- esminiai projektavimo sprendimai, ypač susiję su techniniais apribojimais
  ir pasirinktomis alternatyvomis (pvz., „Street View“ sąsajos pasirinkimas, TUI realizacija), yra aprašyti šiame
  baigiamajame darbe.
- Prototipavimas -- funkcionalumo dalys buvo greitai prototipuojamos ir testuojamos tiesiogiai terminalo aplinkoje,
  kas leido empiriškai patikrinti projektavimo idėjas.

Nors formalūs projektavimo įrankiai nebuvo naudojami, kūrimo procese pasitelkti šie standartiniai programinės
įrangos kūrimo įrankiai:
- Programavimo kalba -- „Scala“ (su „Java“ virtualia mašina).
- Kūrimo aplinka (angl. _Integrated development environment_ arba _IDE_) -- „IntelliJ IDEA“ su „Scala“ įskiepiu.
- Versijų kontrolės sistema -- „Git“, kodui saugoti ir versijuoti, naudojant platformą „GitHub“.
- Bibliotekų valdymas ir projekto kompiliavimas -- „sbt“ („Scala Build Tool“) – standartinis įrankis „Scala“ projektams.
- Tikslinė aplinka -- įvairūs terminalų emuliatoriai („Linux“, „macOS“, „Windows“ sistemose), palaikantys 256 spalvas.

Apibendrinant, projektavimo technologija šiame darbe buvo labiau orientuota į praktinį įgyvendinimą ir
laipsnišką sprendimo formavimą, o ne į išankstinį formalų modeliavimą.
