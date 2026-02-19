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
-Server → gestisce la partita
-Client → rappresenta un giocatore

Il server va avviato una sola volta.
Ogni giocatore avvia un client.

## Cosa serve?

-Java installato (versione 8 o più recente)
-Un terminale

Per verificare Java:
java -version

## 1. Compilare il progetto
Apri il terminale.
Entra nella cartella src del progetto.

cd src

Compila tutti i file:

javac blackjack/gioco/*.java

Se non vedi errori, la compilazione è riuscita.

## 2. Avviare il server
Dal terminale, sempre nella cartella src:

java blackjack.gioco.ServerMain

Se tutto funziona, vedrai:

Server started...

Lascia questo terminale aperto.

## 3. Avviare un client (giocatore)
Apri un nuovo terminale.
Entra di nuovo nella cartella src.

cd src

Avvia il client:

java blackjack.gioco.ClientMain

Ora il client è collegato al server.
Per più giocatori, apri altri terminali e ripeti questo comando.

## Regole importanti
-Il server deve essere avviato prima dei client
-Ogni client usa un terminale diverso
-Non entrare nella cartella gioco per eseguire i comandi
-I comandi vanno lanciati sempre dalla cartella src

# Come avviare il gioco (Windows 10/11)

## 1. Verificare che Java sia installato
Apri Prompt dei comandi.
Scrivi:

java -version

Se vedi una versione di Java, va bene.
Se il comando non viene trovato, devi installare Java prima di continuare.

## 2. Aprire la cartella del progetto
Apri Prompt dei comandi.
Vai nella cartella src del progetto.
Esempio:

cd C:\Users\NomeUtente\Desktop\blackjack\src

Sostituisci il percorso con quello corretto sul tuo computer.

## 3. Compilare il progetto
Sempre nella cartella src, scrivi:

javac blackjack\gioco\*.java

Se non compaiono errori, la compilazione è completata.

## 4. Avviare il server
Dal Prompt dei comandi, nella cartella src:

java blackjack.gioco.ServerMain

Se tutto funziona, vedrai:

Server started...

Lascia questa finestra aperta.

## 5. Avviare un client (giocatore)
Apri un nuovo Prompt dei comandi.
Vai di nuovo nella cartella src:

cd C:\Users\NomeUtente\Desktop\blackjack\src

Avvia il client:

java blackjack.gioco.ClientMain

Ora il client è collegato al server.
Per più giocatori, apri altre finestre e ripeti il comando.

## Regole importanti
-Avvia prima il server, poi i client
-Ogni client usa una finestra diversa
-I comandi vanno eseguiti dalla cartella src
-Non entrare nella cartella gioco per eseguire java

## Problemi comuni
Errore: Could not find or load main class
Il comando è stato eseguito dalla cartella sbagliata.

Torna in src e riprova.
