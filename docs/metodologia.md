# Metodologia (Progetto "Ispettore del Software" - Motore di analisi)

## Obiettivo
Realizzare un prototipo minimale (demo) di un "motore di analisi" capace di:
- scansionare un target di sorgenti Java (directory),
- estrarre metriche semplici su classi/metodi,
- applicare regole (antipattern/code smell) basate su soglie configurabili,
- produrre un report sintetico (tabella + health score) tramite UI Java Swing.

## Vincoli e scelte progettuali
- Runtime senza dipendenze esterne: solo JDK (Swing) + Maven per build.
- Analisi leggera basata su scansione testuale dei file `.java` (non AST): scelta coerente con "prototype/minimum viable demo".
- Architettura a componenti/pacchetti per separare responsabilita' e rendere estendibili metriche e regole.

## Processo di lavoro adottato (in pratica)
- Sviluppo incrementale: prima pipeline end-to-end (scan -> metriche -> regole -> risultati), poi raffinamenti.
- "Contract-first" sulle regole: definizione di un'interfaccia (`Rule`) e di un profilo di analisi (`AnalysisProfile`) per tenere separate logica e configurazione.
- Validazione tramite test automatici:
  - unit test per le singole regole (soglie e casi limite),
  - integration test sul progetto di esempio (`sample-project`) per verificare il flusso completo.
- Documentazione tecnica tramite UML (diagrammi PlantUML) per fissare componenti e relazioni.

## Pipeline di analisi (end-to-end)
1. L'utente seleziona una directory target dalla UI e imposta le soglie.
2. Lo `scanner` raccoglie ricorsivamente tutti i file `.java` nella directory.
3. Il `MetricCalculator` legge ogni file e calcola le metriche disponibili (conteggi e flag).
4. Il `RuleEngine` applica le regole abilitate nel profilo e genera violazioni con severita'.
5. Il `ProjectAnalyzer` aggrega i risultati per classe e calcola un `healthScore` complessivo.
6. La UI mostra una riga per ogni classe (metriche essenziali + violazioni + severita') e l'health score finale.

## Parametrizzazione (soglie)
Le soglie sono centralizzate in `AnalysisProfile` e possono essere modificate dalla UI:
- `godClassMaxMethods` (default 20)
- `godClassMaxFields` (default 15)
- `longParamListMaxParams` (default 4)
- `enabledRules` (regole abilitate di default: `GOD_CLASS`, `LONG_PARAM_LIST`)

## Modalita' di esecuzione e verifica
- Esecuzione demo: `mvn compile exec:java`
- Test: `mvn test`
- Target consigliato per la demo: cartella `sample-project`

## Limiti noti (da citare in slide)
- L'analisi e' basata su parsing testuale: non copre bene firme multi-linea, annotazioni, casi complessi di Java, ecc.
- `depthOfInheritance` e' attualmente un placeholder (0) nel prototipo.
- `TargetType.JAR` e' definito nel modello, ma lo scanner del prototipo gestisce solo `TargetType.DIRECTORY`.
- Nel codice attuale sono operative solo 2 regole (God Class, Long Parameter List).
