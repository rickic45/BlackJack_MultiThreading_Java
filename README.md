# Multithread BlackJack Simulator

## Descrizione del progetto
Questo progetto implementa un simulatore di BlackJack in Java, con supporto multithread per giocatori e banco controllato dal computer.  
Il sistema rispetta le regole classiche del gioco: gestione di assi (1 o 11), blackjack, sballo, puntate e logica del banco.  

Caratteristiche principali:  
- Supporto da 1 a 4 giocatori.  
- Banco automatico con regole standard.  
- Thread separati per ogni giocatore e per il banco, con gestione dei turni tramite `GameManager`.  
- Mazzo sincronizzato per evitare accessi concorrenti non sicuri.  
- Gestione del denaro dei giocatori, puntate e condizioni di fine partita.  
- Architettura modulare facilmente estendibile per interfaccia grafica, split, raddoppio o assicurazione.  

## Struttura del progetto
- `Carta`: rappresenta singole carte con seme e valore.  
- `Mazzo`: gestisce il mazzo di carte, pesca e mescolamento.  
- `Mano`: gestisce le carte di un giocatore e calcola il valore della mano.  
- `Giocatore`: thread che rappresenta un giocatore, gestisce input e mosse.  
- `Banco`: thread che gestisce il comportamento del banco.  
- `GameManager`: coordina la partita, i turni e le condizioni di fine gioco.


# Come avviare il gioco (Linux)

Il gioco è diviso in due parti:

Server → gestisce la partita

Client → rappresenta un giocatore

Il server va avviato una sola volta.
Ogni giocatore avvia un client.

Cosa serve

Un computer

Java installato (versione 8 o più recente)

Un terminale

Per verificare Java:

java -version

1. Compilare il progetto

Apri il terminale.
Entra nella cartella src del progetto.

cd src


Compila tutti i file:

javac blackjack/gioco/*.java


Se non vedi errori, la compilazione è riuscita.

2. Avviare il server

Dal terminale, sempre nella cartella src:

java blackjack.gioco.ServerMain


Se tutto funziona, vedrai:

Server started...


Lascia questo terminale aperto.

3. Avviare un client (giocatore)

Apri un nuovo terminale.
Entra di nuovo nella cartella src.

cd src


Avvia il client:

java blackjack.gioco.ClientMain


Ora il client è collegato al server.

Per più giocatori, apri altri terminali e ripeti questo comando.

Regole importanti

Il server deve essere avviato prima dei client

Ogni client usa un terminale diverso

Non entrare nella cartella gioco per eseguire i comandi

I comandi vanno lanciati sempre dalla cartella src

Problemi comuni

Errore: Could not find or load main class
Vuol dire che il comando java è stato lanciato dalla cartella sbagliata.

Torna in src e riprova.
