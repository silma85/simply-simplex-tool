simply-simplex-tool
===================

Tool interattivo per la risoluzione di problemi di Programmazione Lineare con il metodo del Simplesso (max. 7 variabili e 7 vincoli)

La finestra principale permette di inserire un problema di Programmazione Lineare. Questo programma risolve problemi di Programmazione Lineare Continua, di massimo o minimo, con vincoli di maggioranza, minoranza od uguaglianza e termini noti positivi o negativi. Non risolve problemi di Programmazione Lineare Intera o Mista (ma può essere d'aiuto nel risolvere il rilassamento lineare in alcuni algoritmi) e assume tutte le variabili non-negative, per cui dovrai adottare alcuni accorgimenti per inserire un problema in cui le variabili non siano limitate inferiormente o abbiano limite inferiore negativo.
 
Puoi: 
- Selezionare un problema di massimizzazione o minimizzazione, utilizzando l'apposito selettore a destra.
- Aumentare o diminuire il numero di variabili e vincoli, subito sopra. Compariranno ulteriori caselle vuote, da riempire. Sei limitato a 7 variabili e 7 vincoli.
- Inserire i coefficienti delle variabili decisionali nella funzione obiettivo, segnata in giallo. Attento: se il campo diventa bordato di rosso, hai inserito qualche valore illegale. Correggi l'errore. 
- Inserire i coefficienti dei vincoli, nei campi in bianco, e modificarne il verso con gli appositi selettori.
- Inserire i termini noti dei vincoli, nelle caselle in azzurro.
- Per problemi di due variabili, puoi visualizzare il Grafico del problema premendo l'apposito pulsante.
- Selezionare Esporta in AMPL... dal menu File per ottenere i files .dat e .mod da caricare in ampl.exe.
 
Se sei in dubbio, puoi premere il bottone Convalida per far comparire una scrittura più leggibile del problema da te inserito. Prova ora! Dovrai convalidare almeno una volta, per poter accedere al Risolutore. Per resettare le caselle ed i vincoli, premi il tasto Reimposta. 

Infine, quando sei soddisfatto del problema inserito, premi Risolutore per avviare l'esecuzione interattiva del Metodo del Simplesso. Verrai guidato da messaggi e domande; alla fine, riceverai una valutazione indicativa del lavoro svolto. In ogni momento, potrai chiudere il Risolutore e inserire un nuovo problema. 

Buon lavoro! 
