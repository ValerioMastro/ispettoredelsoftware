  
Documento di Visione

# Documento di Visione  

## Ispettore dell'Architettura Software  
  

**Corso di Tecniche Avanzate di Programmazione**    
**Project Work n. 4**    
**Data:** Febbraio 2026  
  

---  
  

## Introduzione  
  
Durante il ciclo di vita di un progetto software, il codice accumula debito tecnico tramite anti-pattern strutturali, come classi eccessivamente estese che centralizzano molteplici responsabilità, dipendenze complesse da risolvere e un uso improprio dell’ereditarietà. Tali problemi non si manifestano come bug classici, poiché il software mantiene la funzionalità per l’utente finale, ma rendono sempre più ardua la manutenzione, l’estensione e la comprensione del codice. Il progetto ‘Ispettore dell’Architettura Software’ prevede lo sviluppo di uno strumento  per l'analisi della qualità del codice, che partendo da file .jar o cartelle di .class valuta la qualità del design object-oriented senza necessità di esecuzione.
  

## Caratteristiche  
  

L'obiettivo principale del sistema è identificare automaticamente le violazioni dei principi di buona progettazione orientata agli oggetti analizzando la struttura delle classi, non il loro comportamento a run-time. Il sistema deve essere in grado di adattarsi a contesti eterogenei (microservizi, monoliti legacy, librerie di utility ) tramite profili di analisi con soglie configurabili dall'utente.  
  

Le principali caratteristiche attese sono:  
  

- **Introspezione dinamica** di classi sconosciute a compile-time, mediante riflessione Java, con raccolta di metriche strutturali quali numero di metodi pubblici, numero di attributi, gerarchia di ereditarietà , tipi delle variabili di istanza e lunghezza delle firme dei metodi.  
  
- **Rilevazione configurabile di Anti-Pattern** noti tra cui God Class, No-Polymorphism (Switch mania), Lazy Class (Poltergeist), Broken Singleton, Broken Utility Class, Yo-Yo Problem, Long Parameter List, Refused Bequest, Constant Interface e Vendor Lock-in.  
  
- **Gestione di profili di analisi** con soglie modificabili e attivazione/disattivazione delle singole regole tramite file di configurazione e interfaccia grafica.  
  
- **Generazione di report strutturato** che elenca classi problematiche, violazioni riscontrate con evidenza delle soglie superate e un punteggio globale di salute del progetto.  
  

L'interfaccia utente (Audit Dashboard) dovrà  offrire un selettore del target da analizzare, un pannello delle regole con checkbox e slider per le soglie e un visualizzatore dei risultati con indicatori visivi di gravità  mediante semaforo rosso/giallo/verde.  
  

### Schermi principali (bozze)  
  

**Schermo 1 - Audit Dashboard (principale)**  
  

Schermata a tre zone con layout fisso:  
  

- Barra superiore: titolo "Ispettore Architettura Software", menù minimale (File: Apri progetto, Esporta report; Aiuto: Info).  
- Colonna sinistra: pannello "Target da analizzare" con campo di testo che mostra il percorso selezionato, bottone "Sfoglia..." per scegliere jar o cartella, etichetta di stato che indica il numero di classi caricate.  
- Parte centrale: pannello "Risultati analisi" con tabella a righe in cui ogni riga rappresenta una classe, colonne per nome completo, numero di violazioni e icona semaforo (verde/giallo/rosso) a seconda della gravità .  
- Colonna destra: pannello compatto "Profilo attivo" che mostra il nome del profilo e un pulsante "Configura regole..." che apre lo schermo di configurazione.  
- Barra inferiore: barra di stato che mostra il punteggio globale di salute del progetto (es. "Punteggio: 72/100 - Qualità  discreta").  
  

**Schermo 2 - Pannello Regole e Profili**  
  

Finestra dedicata alla configurazione:  
  

- Parte alta: combo box "Profilo di analisi" (es. "Default", "Microservizio", "Legacy monolitico") con pulsanti "Nuovo", "Duplica", "Elimina" per gestire i profili.  
- Parte centrale: elenco delle regole, una per riga, con checkbox per attivare/disattivare la regola (es. "God Class", "Long Parameter List", "Vendor Lock-in") e uno o più slider o campi di testo per le soglie principali (es. "Metodi > X", "Attributi > Y", "Profondità  ereditarietà  > D").  
- Parte bassa: pulsanti "Ripristina valori di default" (legge i valori dal file di properties), "Annulla" e "Salva profilo".  
- Area descrizione sulla destra: mostra per la regola selezionata una spiegazione breve dell'Anti-Pattern e l'effetto delle soglie sul riconoscimento.  
  

**Schermo 3 - Dettaglio Classe Analizzata**  
  

Finestra per approfondire una singola classe:  
  

- Intestazione con nome completo della classe (package + nome) e indicatore sintetico di rischio (icona rossa/gialla/verde).  
- Sezione "Metriche": tabella con valori numerici principali quali numero di metodi totali e pubblici, numero di attributi, profondità  della gerarchia di ereditarietà , numero medio e massimo di parametri, numero di dipendenze in uscita.  
- Sezione "Violazioni rilevate": elenco delle regole violate con messaggi del tipo "God Class: metodi=45 > soglia 20, attributi=30 > soglia 15", "Long Parameter List: 3 metodi con più di 6 parametri", "Constant Interface: interfaccia contenente solo costanti".  
- Riquadro "Suggerimenti" opzionale con consigli di refactoring di alto livello (es. "Valuta estrazione di sottoclassi", "Sposta la logica in classi collaboratrici").  
  

## Processi coinvolti  
  

Il tool si inserisce principalmente nei processi di sviluppo e quality assurance, in particolare:  
  

- **Revisione del codice e gestione del debito tecnico**, fornendo una fotografia strutturale del progetto prima di attività  di refactoring programmate.  
- **Integrazione in iterazioni AUP o XP-like**, con esecuzioni periodiche  al termine di ogni iterazione per monitorare l'evoluzione della qualità  architetturale nel tempo.  
  

L'introduzione dell'Auditor Automatico modifica i processi interni dei team di sviluppo, rendendo sistematiche le attività di valutazione del design e di pianificazione tecnica basata su metriche quantitative e Anti-Pattern rilevati. In contesto didattico, il tool entra anche nel processo di apprendimento degli studenti, che possono osservare direttamente come scelte progettuali si riflettano nelle violazioni rilevate dall'analisi.  
  

## Posizionamento e alternative  
  

Lo strumento si colloca nello spazio degli analyzer di qualità  del codice, a fianco di strumenti consolidati come SonarQube e plugin per IDE, ma con obiettivi più limitati e mirati ad alcuni Anti-Pattern tipici della programmazione a oggetti. A differenza delle soluzioni enterprise, è pensato per essere più leggero, configurabile via semplici file di properties e utilizzabile facilmente in contesti didattici o in piccoli team di sviluppo senza necessità  di infrastrutture complesse.  
  

Alternative oggi disponibili sono tool esterni completi ma complessi da installare e personalizzare, oppure l'assenza di strumenti strutturali con valutazioni affidate esclusivamente all'esperienza del singolo sviluppatore. Il progetto mira a coprire questo spazio intermedio, offrendo un supporto automatico e configurabile senza introdurre eccessiva burocrazia nel processo di sviluppo.  
  

## Parti interessate  
  

**Parti interessate principali:**  
  

- **Sviluppatori e team leader** che vogliono monitorare la qualità  del proprio codice e individuare classi ad alto rischio prima che i problemi esplodano in produzione.  
- **Architetti software e responsabili di qualità ** che necessitano di indicatori sintetici e di elenchi di classi critiche su cui concentrare le attività  di refactoring.  
  

**Parti interessate secondarie:**  
  

- **Docenti e studenti** di corsi di ingegneria del software e tecniche avanzate di programmazione che utilizzano il tool come supporto didattico per riconoscere Anti-Pattern e ragionare sui trade-off architetturali.  
- **Responsabili di progetto o committenti interni** interessati ad avere un indice di "salute" del software per valutare rischi e priorità  di intervento.  
  

## Architettura e tecnologia (indicazioni preliminari)  
  

A livello preliminare si prevede un'architettura modulare che separa caricamento del target, calcolo delle metriche, applicazione delle regole, gestione dei profili, generazione del report e interfaccia grafica. Ogni modulo puÃ² evolvere in modo relativamente indipendente, supportando l'approccio iterativo e l'eventuale introduzione di nuove regole o nuovi formati di report.  
  

La piattaforma di riferimento è Java SE, sfruttando le API di riflessione per l'introspezione delle classi, le librerie standard per la gestione di file `.jar` e `.class` e una tecnologia GUI (es. Swing o JavaFX) per l'Audit Dashboard. La configurazione dei profili di analisi e dei valori di default potrà  essere gestita tramite file `.properties`, in linea con le pratiche leggere suggerite da AUP.  
  

## Distribuzione e licenze  
  

Il sistema è pensato inizialmente come applicazione desktop standalone, distribuita come archivio eseguibile (`.jar`) contenente tutti i moduli e un file di configurazione con i profili di analisi standard. Potrà  essere utilizzato localmente dagli sviluppatori senza necessità  di infrastrutture server dedicate.  
  

In contesto accademico si potrà  adottare una licenza che favorisca la condivisione del codice sorgente tra studenti e la possibilità di estendere il set di regole, nel rispetto delle linee guida dell'Ateneo. In fasi successive si potrà  valutare un'integrazione con pipeline di build o strumenti di continuous integration, ma ciò non è vincolante per la fase di ideazione.  
  

## Riassunto delle funzionalità   
  

Funzionalità  principali previste:  
  

- Caricamento di un progetto target (file `.jar` o cartella di `.class`) e scoperta automatica delle classi da analizzare.  
- Calcolo di metriche strutturali sulle classi tramite riflessione: metodi pubblici, attributi, ereditarietà , dipendenze, firme dei metodi.  
- Applicazione di un insieme configurabile di regole di controllo per la rilevazione di Anti-Pattern.  
- Gestione di profili di analisi con soglie e regole attive, configurabili via file di properties e GUI.  
- Produzione di un report strutturato con elenco delle classi problematiche, dettaglio delle violazioni e punteggio di salute complessivo.  
- Audit Dashboard con selettore del target, pannello regole (checkbox e slider) e visualizzatore risultati (lista classi con semaforo e dettaglio).  
  

## Casi d'uso principali  
  

Di seguito si riportano alcuni casi d'uso di alto livello che sintetizzano il comportamento atteso del sistema nella fase di ideazione.  
  

### CU1 - Esegui analisi su progetto  
  

**Attore principale:** Sviluppatore.  
  

Breve descrizione: Lo sviluppatore sceglie un file .jar o una directory di .class, lancia lo scan completo e riceve un report con l’elenco delle classi critiche e il punteggio complessivo di salute architetturale.
  

**Scenario principale:**  
  

1. L'utente avvia l'applicazione e apre l'Audit Dashboard.  
2. L'utente seleziona il target (file `.jar` o directory) tramite il selettore.  
3. Il sistema carica le classi, calcola le metriche strutturali tramite riflessione e applica le regole attive.  
4. Il sistema mostra la lista delle classi analizzate con indicatori di gravità  e rende disponibile il report strutturato.  
  

### CU2 - Configura profilo di analisi  
  

**Attore principale:** Sviluppatore.  
  

**Breve descrizione:** l'utente definisce o modifica un profilo per analizzare, impostando soglie e regole attive tramite il pannello delle regole e il file di configurazione.  
  

**Scenario principale:**  
  

1. L'utente apre il Pannello Regole dalla Dashboard.  
2. L'utente seleziona un profilo esistente o ne crea uno nuovo.  
3. L'utente attiva o disattiva singole regole (es. God Class, Long Parameter List, Vendor Lock-in).  
4. L'utente modifica le soglie numeriche e salva il profilo, che viene memorizzato in un file di properties e diventa utilizzabile nelle valutazioni successive.  
  

### CU3 - Esamina dettagli di una classe  
  

**Attore principale:** Sviluppatore.  
  

**Breve descrizione:** dopo un controllo, l'utente seleziona una classe e visualizza il dettaglio delle metriche e delle violazioni riscontrate.  
  

**Scenario principale:**  
  

1. L'utente ha eseguito un controllo del codice (CU1) e sta visualizzando la lista delle classi.  
2. L'utente seleziona una classe marcata come problematica.  
3. Il sistema mostra le metriche calcolate (numero di metodi, attributi, profondità  gerarchia, numero di parametri) e l'elenco delle regole violate con le soglie superate.  
4. L'utente utilizza queste informazioni per valutare la necessità  di interventi di refactoring.  
  

### CU4 - Esporta report di qualità   
  

**Attori principali:** Sviluppatore, Project Manager.  
  

**Breve descrizione:** l'utente esporta il risultato dell'analisi in un formato condivisibile (es. file di testo o HTML) contenente punteggio globale e classi problematiche.  
  

**Scenario principale:**  
  

1. L'utente esegue un'analisi del progetto (CU1).  
2. L'utente seleziona il comando "Esporta report".  
3. Il sistema genera un file di report con elenco di classi problematiche, tipo di violazione e punteggio di salute complessivo, salvandolo in una posizione scelta dall'utente.  
  

## Altri requisiti (URPS)  
  

Dal punto di vista dell'**usabilità **, l'interfaccia deve essere semplice e auto-esplicativa, con pochi schermi principali, etichette chiare e valori di default ragionevoli per i profili di valutazione. L'utente non deve essere costretto a conoscere nel dettaglio tutte le regole per eseguire una prima check del progetto.  
  

In termini di **affidabilità **, lo strumento deve gestire in modo robusto errori di caricamento delle classi (classi mancanti, dipendenze non trovate), producendo messaggi d'errore comprensibili senza interrompere bruscamente l'analisi. Inoltre  è desiderabile che il sistema mantenga consistenti i risultati anche in presenza di progetti parziali o incompleti.  
  

Per quanto riguarda le **prestazioni**, l'analisi di un progetto di dimensioni medie deve completarsi in tempi ragionevoli su una postazione di sviluppo standard, in modo da poter essere eseguita frequentemente durante il ciclo di sviluppo. Non è richiesto il supporto a progetti di dimensioni enterprise, ma l'architettura dovrà evitare elaborazioni inutilmente ridondanti.  
  

Quanto alla **supportabilità **, la configurazione tramite file di properties e la struttura modulare delle regole devono facilitare l'aggiunta di nuovi Anti-Pattern e la modifica delle soglie senza interventi invasivi sul resto del sistema. La scelta di tecnologie standard (Java SE, GUI tradizionale) contribuisce a rendere il tool facilmente installabile e utilizzabile in ambienti diversi.  
  

## Costi e benefici  
  

I costi principali riguardano la valutazione iniziale delle metriche e la progettazione del motore di regole, oltre alla realizzazione di una GUI sufficientemente chiara e reattiva. Dal punto di vista tecnologico il progetto rimane contenuto, basandosi su funzionalità  standard e senza richiedere infrastrutture esterne complesse.  
  

I benefici attesi sono la riduzione del rischio di degrado architetturale, il supporto alle decisioni di refactoring e la sensibilizzazione degli sviluppatori sugli Anti-Pattern comuni della programmazione a oggetti. Rischi principali sono la scelta di soglie non adeguate e la complessità  di alcune regole (es. Yo-Yo Problem, Vendor Lock-in), che possono portare a falsi positivi o falsi negativi; tali rischi saranno mitigati tramite profili di esame configurabili e iterazioni successive di calibrazione.  
  

## Pianificazione della fase di ideazione  
  

La fase di ideazione del progetto "Ispettore dell'Architettura Software" ha lo scopo di chiarire la visione del sistema, gli obiettivi principali, i processi coinvolti e le prime decisioni tecnologiche, verificando se il progetto è realistico rispetto a tempi e risorse disponibili. In questa fase si produce il documento di visione, si definisce un primo elenco di casi d'uso e si individuano i principali Anti-Pattern da supportare nella prima iterazione.  
  

Nel contesto del project work la fase di ideazione ha durata limitata (equivalente alle prime settimane di lavoro), durante le quali vengono scelti il linguaggio e la piattaforma (Java SE), le tecnologie di base (riflessione, gestione di jar e class, GUI) e il nucleo iniziale di regole da implementare. Al termine dell'ideazione si pianifica la prima iterazione di elaborazione, concentrandosi sui casi d'uso a maggior rischio e valore, come l'analisi di God Class e Long Parameter List, rinviando a iterazioni successive l'introduzione di regole più complesse come Yo-Yo Problem e Vendor Lock-in.  
  

<!--stackedit_data:
eyJoaXN0b3J5IjpbMTM0MzcxNjM2OF19
-->
