**ISPETTORE DEL SOFTWARE**

Corso di Tecniche Avanzate di Programmazione 
Project work
Febbraio 2026

**Introduzione**

Nel corso del tempo i programmi si ricoprono di problemi strutturali: classi che fanno troppo, dipendenze ingarbugliate, ereditarietà mal condotta. Bug non bloccanti, ma che rendono dolorosa la manutenzione. Questo progetto è uno strumento che analizza file .jar o cartelle di .class per scovare questi anti-pattern nel design object-oriented, senza eseguirli.
L'analisi delle violazioni ai principi di design, tipiche (pensiamo a God Class o Long Parameter List) avviene automaticamente, sulla struttura statica delle classi. E deve valere in progetti diversi (microservizi, vecchi orbi, librerie) mediante profili configurabili, regolabili con soglie personalizzate.

Le Features Principali:

1. Ispezione attraverso riflessione Java: conta metodi pubblici, attributi, profondità ereditaria, variabili di tipo, lunghezza delle firme.
2. Riconoscimento Anti-Pattern: God Class, Switch Mania, Lazy Class, Singleton non benfatto, Utility Class disperatamente utile, Yo-Yo, liste parametri lunghe, Refused Bequest, Constant lnterface, Vendor Lock-in.
3. Profili di ispezione: file config e appunto GUI per attivare/disattivare norme, cambiare soglie.
4. Report chiari: elenco delle classi problematiche, delle violazioni con palesato superamento della soglia, del punteggio di salute (es. fra 90 e 100 "Codice pulito").
L'interfaccia è una Dashboard semplice e direi piacevole: om si ha a che fare con@click di menù.

L'interfaccia è una **Dashboard** semplice: scegli il target, configuri regole (checkbox/slider), vedi risultati con semaforo rosso/giallo/verde.  

### Schermi principali (bozza)  

**Dashboard principale**  
Layout a 3 zone:  
- Sopra: titolo + menu base (Apri, Esporta, Info).  
- Sinistra: percorso jar/cartella, bottone "Sfoglia", contatore classi.  
- Centro: tabella classi con nome, violazioni, semaforo gravità.  
- Destra: profilo attivo + "Configura...".  
- Sotto: "Punteggio: 72/100 - Discreto".  

**Pannello regole**  
- Sopra: seleziona profilo (Default, Microservizi...) + Nuovo/Duplica/Elimina.  
- Centro: lista regole con checkbox + slider (es. "Metodi >20").  
- Sotto: Ripristina default, Salva/Annulla.  
- Destra: spiegazione regola selezionata.
  
**Dettaglio classe**  
- Nome classe + semaforo rischio.  
- Metriche: tabella con metodi, attributi, ereditarietà, parametri max/medio.  
- Violazioni: "God Class: 45 metodi (>20), 30 attr (>15)".  
- Suggerimenti: "Prova a spezzarla in classi più piccole".  

## Processi dove si usa  
Perfetto per revisioni codice e controllo debito tecnico, prima del refactoring. Si integra in iterazioni XP/AUP, lanciandolo a fine sprint per monitorare la qualità architetturale.  

## Alternative  
Simile a SonarQube ma più leggero, solo per anti-pattern specifici.
## Chi lo usa  
- **Sviluppatori**: trovano classi a rischio prima che siano un problema.  
- **Project manager**: hanno un punteggio salute per priorizzare delle fix.  

## Tech stack (indicazioni)  
Architettura modulare: loader classi, metriche, regole, profili, report, GUI. Java con riflessione per introspezione jar/class, Swing per interfaccia, .properties per config. Facile da evolvere aggiungendo regole.  
Standalone .jar con profili default inclusi, no server.  

## Funzionalità riassunte  
- Carica jar/cartella, scopre classi.  
- Calcola metriche strutturali.  
- Applica regole configurabili.  
- Genera report con classi violanti + score.  
- Dashboard: target, regole, risultati.  

## Casi d'uso principali  
### CU1 - Analisi progetto  
**Attore:** Sviluppatore  
Scegli jar/cartella, lancia scan, vedi classi critiche + score salute.  
1. Apri app, seleziona target.  
2. Sistema carica, analizza metriche/regole.  
3. Tabella classi + report.  
### CU2 - Configura profilo  
**Attore:** Sviluppatore  
Modifica soglie/regole via GUI/file.  
1. Apri pannello regole.  
2. Scegli/crea profilo, attiva/disattiva (God Class...), regola slider.  
3. Salva in .properties.  
### CU3 - Dettaglio classe  
**Attore:** Sviluppatore  
Da lista, clicca classe problematica per metriche/violazioni.  
1. Post-analisi (CU1), seleziona riga.  
2. Vedi numeri + regole violate + suggerimenti refactor.  
### CU4 - Esporta report  
**Attore:** Sviluppatore/PM  
Salva risultati in testo/HTML.  
1. Dopo CU1, "Esporta".  
2. File con score + classi violanti.  
## Requisiti URPS  
**Usabilità**: interfaccia semplice, default ok per primo uso.  
**Affidabilità**: gestisce jar rotti senza crash, messaggi chiari.  
**Prestazioni**: analisi media in pochi minuti su PC normale.  
**Supportabilità**: aggiungi regole facili, no dipendenze pazze.  
## Costi e benefici  
Costo: le metriche, il motore regole ed una GUI reattiva. 
Benefici: meno debito tecnico, refactoring mirati, awareness anti-pattern.  
Rischi: se le soglie sono sbagliate c'è la possibilità di falsi positivi. Possibile miitigazione aggiungendo profili e facendo altre iterazioni di calibrazione.  
## Pianificazione ideazione  
Prime settimane: documento visione, casi d'uso, anti-pattern iniziali (God Class, Long Params). Tech: Java/Swing. Prima iterazione su regole facili, poi complesse (es Yo-Yo). Poi verrà  pianificata l' elaborazione.

<!--stackedit_data:
eyJoaXN0b3J5IjpbMTM0MzcxNjM2OF19
-->
