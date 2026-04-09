# Caratteristiche di classi e metodi (per slide)

## Architettura logica (pacchetti)
- `com.tav.progetto.analysis.ui`: interfaccia utente Swing (selezione target, input soglie, output risultati).
- `com.tav.progetto.analysis.core`: orchestrazione dell'analisi e modello dei risultati (severity, violazioni, health score).
- `com.tav.progetto.analysis.scanner`: scoperta dei file `.java` a partire da un target.
- `com.tav.progetto.analysis.metrics`: calcolo metriche (estrazione informazioni da file Java).
- `com.tav.progetto.analysis.rules`: regole/antipattern applicate alle metriche (con profilo di soglie e abilitazione).

## Flusso dati (input -> output)
- Input: directory con `.class`, singolo file `.class` oppure `.jar` + profilo soglie (`AnalysisProfile`).
- Output: `ProjectAnalysisResult` composto da:
  - lista di `ClassAnalysisResult` (una entry per ogni file Java analizzato),
  - `healthScore` (0..100).

## Classi principali e responsabilita (con metodi chiave)

### Avvio e UI
- `App`
  - Scopo: bootstrap dell'applicazione Swing.
  - Metodo chiave: `main(String[] args)` (crea e mostra `MainFrame` su EDT).
- `MainFrame`
  - Scopo: UI per selezione target, set soglie e visualizzazione output.
  - Metodi chiave:
    - `onBrowse(ActionEvent e)`: selezione directory tramite `JFileChooser`.
    - `onRun(ActionEvent e)`: costruisce `AnalysisProfile`, invoca `ProjectAnalyzer.analyze(...)`, popola tabella e health score.

### Core (orchestrazione + risultati)
- `ProjectAnalyzer`
  - Scopo: pipeline completa su un target; aggrega severita' e `healthScore`.
  - Metodo chiave: `analyze(TargetDescriptor target, AnalysisProfile profile) : ProjectAnalysisResult`.
  - Calcolo `healthScore`:
    - parte da 100,
    - sottrae penalita per violazioni (HIGH=20, MEDIUM=10, LOW=2),
    - clamp finale a minimo 0.
- `TargetDescriptor`
  - Scopo: descrive il target di analisi.
  - Dati: `path`, `TargetType` (nel prototipo usato `DIRECTORY`).
- `ProjectAnalysisResult`
  - Dati: `classResults : List<ClassAnalysisResult>`, `healthScore : int`.
- `ClassAnalysisResult`
  - Dati: `metrics : ClassMetrics`, `violations : List<Violation>`, `overallSeverity : Severity`.
  - `overallSeverity` = severita' massima tra le violazioni della classe, o `LOW` se nessuna.
- `Violation`
  - Dati: `className`, `ruleId`, `description`, `severity`.
- `Severity`
  - Valori: `LOW`, `MEDIUM`, `HIGH`.

### Scanner
- `ClassPathScanner`
  - Scopo: raccolta delle classi analizzabili a partire da directory di `.class`, singoli `.class` o `.jar`.
  - Metodo chiave: `findClasses(TargetDescriptor target) : List<ScannedClass>`.
  - Nota: il flusso corrente supporta `DIRECTORY`, `CLASS_FILE` e `JAR`.

### Metriche (classi/metodi)
- `ClassMetrics`
  - Dati estratti (per classe):
    - `className`: nome file senza `.java`.
    - `totalMethods`: numero di metodi rilevati nel file (match su riga con visibilita + `(...)`).
    - `publicMethods`: numero di metodi che contengono `public`.
    - `fields`: conteggio di righe che finiscono con `;` e non contengono `(` (proxy per campi).
    - `maxParametersPerMethod`: massimo numero di parametri su un metodo rilevato (conteggio virgole).
    - `outgoingDependencies`: numero di righe `import` (proxy per dipendenze in uscita).
    - `isInterface`: true se viene trovato `interface`.
    - `hasOnlyConstants`: true se `isInterface` e tutti i campi trovati sono `static` e `final`.
    - `depthOfInheritance`: alias legacy della profondita' ereditaria per compatibilita' con il codice storico.
    - `inheritanceDepth`: profondita' ereditaria calcolata via reflection.
    - `superClassName`: nome della superclasse diretta.
    - `methodsCount` / `totalMethods`: alias per il conteggio metodi.
    - `totalFieldsCount` / `fields`: alias per il conteggio campi.
    - `publicStaticFinalFieldsCount`: conteggio dei campi `public static final`.
- `MetricCalculator`
  - Scopo: calcolare `ClassMetrics` leggendo un file `.java` oppure ispezionando una `Class<?>`.
  - Metodi chiave:
    - `computeMetrics(File javaFile) : ClassMetrics`
    - `computeMetrics(Class<?> clazz) : ClassMetrics`
  - Nota per slide: il path reflection-based e' quello piu' fedele; il path file-based resta lightweight e serve soprattutto alla demo.

## Regole implementate (antipattern)
Le regole sono incapsulate dall'interfaccia:
- `Rule`
  - `getId() : String`
  - `getName() : String`
  - `apply(ClassMetrics metrics, AnalysisProfile profile) : List<Violation>`

### Regole attive nel prototipo
- `GodClassRule` (`ruleId = GOD_CLASS`)
  - Trigger: `totalMethods > godClassMaxMethods` OR `fields > godClassMaxFields`.
  - Severita': `HIGH`.
  - Output: una `Violation` con valori osservati e soglie superate.
- `LongParameterListRule` (`ruleId = LONG_PARAM_LIST`)
  - Trigger: `maxParametersPerMethod > longParamListMaxParams`.
  - Severita': `MEDIUM`.
  - Output: una `Violation` con valore osservato e soglia superata.
- `LazyClassRule` (`ruleId = LAZY_CLASS`)
  - Trigger: classe concreta con `methods <= lazyClassMaxMethods` AND `fields <= lazyClassMaxFields`.
  - Severita': `LOW`.
- `UtilityClassRule` (`ruleId = UTILITY_CLASS`)
  - Trigger: classe concreta con rapporto `staticMethods / totalMethods >= utilityMinStaticMethodRatio` e `instanceFields <= utilityMaxInstanceFields`.
  - Severita': `LOW`.
- `YoyoRule` (`ruleId = YOYO`)
  - Trigger: `inheritanceDepth > yoyoMaxInheritanceDepth`.
  - Severita': `MEDIUM`.
- `ConstantInterfaceRule` (`ruleId = CONSTANT_INTERFACE`)
  - Trigger: elemento con almeno `constantInterfaceMinConstantFields` campi `public static final`, prevalenza di costanti sui campi totali e `methods <= constantInterfaceMaxMethods`.
  - Severita': `MEDIUM`.
  - Output: una `Violation` che distingue tra interfaccia costante e classe costante.
- `BrokenUtilityClassRule` (`ruleId = BROKEN_UTILITY_CLASS`)
  - Trigger: classe concreta con almeno un membro, soli membri statici e almeno un costruttore non `private`.
  - Severita': `LOW`.
  - Output: una `Violation` con conteggio dei costruttori non privati.
  - Nota: con sola reflection e' un'euristica minima; non distingue tutte le classi helper "legittime", ma intercetta bene le utility class istanziabili.
- `RuleEngine`
  - Scopo: contiene la lista di regole e le applica in sequenza.
  - Nota: il costruttore di default registra le regole standard, ma esiste anche un costruttore per iniettare una lista custom di regole.

### Soglie e configurazione
- `AnalysisProfile`
  - Campi configurabili: `godClassMaxMethods`, `godClassMaxFields`, `longParamListMaxParams`, `lazyClassMaxMethods`, `lazyClassMaxFields`, `utilityMinStaticMethodRatio`, `utilityMaxInstanceFields`, `yoyoMaxInheritanceDepth`, `constantInterfaceMinConstantFields`, `constantInterfaceMaxMethods`.
  - Abilitazione regole: `enabledRules : Set<String>` (filtra in `Rule.apply`).
  - La UI espone tutte le soglie delle regole configurabili; `BrokenUtilityClassRule` non ha soglie dedicate.

## Dataset di esempio (per demo e test)
- `sample-project`
  - `GodClass.java`: progettata per superare la soglia metodi/campi e generare `GOD_CLASS`.
  - `LongParamClass.java`: progettata per superare la soglia parametri e generare `LONG_PARAM_LIST`.
  - `ConstantInterface.java`: progettata per generare `CONSTANT_INTERFACE`.
  - `UtilityClass.java`: progettata per generare `BROKEN_UTILITY_CLASS` con euristica minima.

## Nota di allineamento con UML
Le regole attive nel motore sono ora `GOD_CLASS`, `LONG_PARAM_LIST`, `LAZY_CLASS`, `UTILITY_CLASS`, `YOYO`, `CONSTANT_INTERFACE` e `BROKEN_UTILITY_CLASS`.
