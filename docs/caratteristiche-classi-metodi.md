# Caratteristiche di classi e metodi (per slide)

## Architettura logica (pacchetti)
- `com.tav.progetto.analysis.ui`: interfaccia utente Swing (selezione target, input soglie, output risultati).
- `com.tav.progetto.analysis.core`: orchestrazione dell'analisi e modello dei risultati (severity, violazioni, health score).
- `com.tav.progetto.analysis.scanner`: scoperta dei file `.java` a partire da un target.
- `com.tav.progetto.analysis.metrics`: calcolo metriche (estrazione informazioni da file Java).
- `com.tav.progetto.analysis.rules`: regole/antipattern applicate alle metriche (con profilo di soglie e abilitazione).

## Flusso dati (input -> output)
- Input: directory con sorgenti `.java` + profilo soglie (`AnalysisProfile`).
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
  - Scopo: raccolta ricorsiva di tutti i file `.java` dentro una directory.
  - Metodo chiave: `findClassFiles(TargetDescriptor target) : List<File>`.
  - Nota: nel prototipo e' gestito solo `TargetType.DIRECTORY`.

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
    - `depthOfInheritance`: placeholder nel prototipo (impostato a 0).
- `MetricCalculator`
  - Scopo: calcolare `ClassMetrics` leggendo un file `.java`.
  - Metodo chiave: `computeMetrics(File javaFile) : ClassMetrics`.
  - Nota per slide: e' un calcolo "lightweight" basato su regole testuali, non su parser/AST.

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
  - Output: una `Violation` con descrizione "Class with too many methods/fields".
- `LongParameterListRule` (`ruleId = LONG_PARAM_LIST`)
  - Trigger: `maxParametersPerMethod > longParamListMaxParams`.
  - Severita': `MEDIUM`.
  - Output: una `Violation` con descrizione "Method with too many parameters".
- `RuleEngine`
  - Scopo: contiene la lista di regole e le applica in sequenza.
  - Nota: nel prototipo la lista e costruita nel costruttore (attualmente 2 regole).

### Soglie e configurazione
- `AnalysisProfile`
  - Campi configurabili: `godClassMaxMethods`, `godClassMaxFields`, `longParamListMaxParams`.
  - Abilitazione regole: `enabledRules : Set<String>` (filtra in `Rule.apply`).

## Dataset di esempio (per demo e test)
- `sample-project`
  - `GodClass.java`: progettata per superare la soglia metodi/campi e generare `GOD_CLASS`.
  - `LongParamClass.java`: progettata per superare la soglia parametri e generare `LONG_PARAM_LIST`.
  - `UtilityClass.java` e `ConstantInterface.java`: incluse come casi "puliti" nel prototipo attuale.

## Nota di allineamento con UML
Nei diagrammi PlantUML compaiono anche regole come `ConstantInterfaceRule` e `BrokenUtilityClassRule`, ma nel codice attuale non risultano implementate/attivate: la demo esegue solo `GOD_CLASS` e `LONG_PARAM_LIST`.
