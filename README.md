# Multithread Blackjack Simulator

## Descrizione del progetto

Questo progetto implementa un simulatore di **Blackjack** in Java con architettura **client–server** e supporto **multithread**.
Ogni giocatore e il banco sono gestiti tramite thread separati, coordinati da una logica centrale di gioco.

Il sistema rispetta le regole classiche del Blackjack:

* gestione degli assi (1 o 11)
* blackjack naturale
* sballo
* puntate
* logica del banco standard

### Caratteristiche principali

* Supporto da **1 a 4 giocatori**
* Banco automatico controllato dal sistema
* Thread dedicati per giocatori e banco
* Gestione dei turni centralizzata
* Mazzo sincronizzato per evitare accessi concorrenti
* Gestione del denaro e condizioni di fine partita
* Architettura modulare, facilmente estendibile (GUI, split, raddoppio, assicurazione)

---

## Struttura del progetto

* `Carta` → rappresenta una singola carta (seme e valore)
* `Mazzo` → gestisce il mazzo, il mescolamento e la pesca
* `Mano` → rappresenta la mano di un giocatore e ne calcola il valore
* `Giocatore` → thread che gestisce le azioni del giocatore
* `Banco` → thread che gestisce il comportamento del banco
* `GameManager` → coordina la partita, i turni e la logica di gioco
* `ServerMain` → avvia il server
* `ClientMain` → avvia un client (giocatore)

---

# Avvio del gioco (Linux / macOS)

Il gioco è composto da due parti:

* **Server** → gestisce la partita
* **Client** → rappresenta un giocatore

Il server va avviato **una sola volta**.
Ogni giocatore avvia il proprio client.

---

## Requisiti

* Java JDK **8 o superiore**
* Un terminale

Verifica che Java sia installato:

```bash
java -version
```

---

## 1. Compilazione del progetto

Apri il terminale ed entra nella cartella `src` del progetto:

```bash
cd src
```

Compila tutti i file Java:

```bash
javac blackjack/gioco/*.java
```

Se non vengono mostrati errori, la compilazione è riuscita.

---

## 2. Avvio del server

Dalla cartella `src`:

```bash
java blackjack.gioco.ServerMain
```

Output atteso:

```text
Server started...
```

Lascia questo terminale **aperto**.

---

## 3. Avvio di un client (giocatore)

Apri un **nuovo terminale**.
Entra di nuovo nella cartella `src`:

```bash
cd src
```

Avvia il client:

```bash
java blackjack.gioco.ClientMain
```

Per più giocatori, apri altri terminali e ripeti il comando.



# Avvio del gioco (Windows 10 / 11)

---

## 1. Verificare Java

Apri **Prompt dei comandi** e scrivi:

```bat
java -version
```

Se il comando non viene riconosciuto, installa Java prima di continuare.

---

## 2. Aprire la cartella del progetto

Dal Prompt dei comandi, entra nella cartella `src`.
Esempio:

```bat
cd C:\Users\NomeUtente\Desktop\blackjack\src
```

---

## 3. Compilare il progetto

```bat
javac blackjack\gioco\*.java
```

Se non compaiono errori, la compilazione è completata.

---

## 4. Avviare il server

```bat
java blackjack.gioco.ServerMain
```

Output atteso:

```text
Server started...
```

Lascia la finestra aperta.

---

## 5. Avviare un client (giocatore)

Apri un **nuovo Prompt dei comandi**.
Torna nella cartella `src` e avvia il client:

```bat
java blackjack.gioco.ClientMain
```

Ripeti per ogni giocatore aggiuntivo.

---



## Regole importanti

* Il server deve essere avviato **prima** dei client
* Ogni client usa un terminale separato
* I comandi vanno eseguiti sempre dalla cartella `src`
* Non entrare nella cartella `gioco` per eseguire `java`

---
  
## Problemi comuni

**Errore:**

```text
Could not find or load main class
```

**Causa:** il comando `java` è stato eseguito dalla cartella sbagliata.

**Soluzione:** torna nella cartella `src` e riprova.
