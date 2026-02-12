# Multithread BlackJack Simulator

## Descrizione del progetto
Questo progetto implementa un simulatore di BlackJack in Java, con supporto multithread per giocatori e banco controllati dal computer.  
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

## Come avviare il progetto
1. Clonare il repository:
   ```bash
   git clone https://github.com/tuo-username/BlackJackSimulator.git
