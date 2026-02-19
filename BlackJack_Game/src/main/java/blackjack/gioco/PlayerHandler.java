package blackjack.gioco;

import java.io.*;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.CountDownLatch;

public class PlayerHandler implements Runnable {

    private final Socket     socket;
    private final Deck       deck;
    private final String     playerName;

    private BufferedReader in;
    private PrintWriter    out;

    private int            chips      = 100;
    private int            currentBet = 0;
    private boolean        busted     = false;
    private boolean        standing   = false;   // true dopo STAY
    private volatile boolean active   = true;
    private volatile boolean myTurn   = false;
    private volatile boolean inRound  = false;   // true dal BET fino a resolveRound

    private final List<Integer> hand = new ArrayList<>();

    // Latch fornito dal server per sincronizzare la fine del turno
    private volatile CountDownLatch turnLatch;

    public PlayerHandler(Socket socket, Deck deck, String playerName) {
        this.socket     = socket;
        this.deck       = deck;
        this.playerName = playerName;
    }

    // ─────────────────────────────────────────────
    //  Runnable
    // ─────────────────────────────────────────────
    @Override
    public void run() {
        try {
            in  = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            send("=== Benvenuto nel Blackjack Server! ===");
            send("Il tuo nome identificativo è: " + playerName);
            send("Fiches iniziali: " + chips);
            send("Attendi che il turno inizi...");

            while (active) {
                String line = in.readLine();
                if (line == null) {          // client disconnesso
                    disconnect();
                    break;
                }
                if (!myTurn) {
                    send("Aspetta il tuo turno.");
                    continue;
                }
                handleCommand(line.trim().toUpperCase());
            }
        } catch (IOException e) {
            disconnect();
        }
    }

    // ─────────────────────────────────────────────
    //  Gestione comandi
    // ─────────────────────────────────────────────
    private void handleCommand(String cmd) {
        if (cmd.startsWith("BET")) {
            handleBet(cmd);
        } else if (cmd.equals("HIT")) {
            handleHit();
        } else if (cmd.equals("STAY")) {
            handleStay();
        } else if (cmd.equals("QUIT")) {
            disconnect();
        } else {
            send("Comando non riconosciuto. Usa: BET <n> | HIT | STAY | QUIT");
        }
    }

    private void handleBet(String cmd) {
        // Se la mano è già iniziata, ignora un secondo BET
        if (!hand.isEmpty()) {
            send("Hai già puntato. Usa HIT o STAY.");
            return;
        }
        try {
            int bet = Integer.parseInt(cmd.split(" ")[1]);
            if (bet <= 0 || bet > chips) {
                send("Puntata non valida. Hai " + chips + " fiches disponibili.");
                return;
            }
            currentBet = bet;
            chips     -= bet;
            busted     = false;
            standing   = false;
            inRound    = true;
            dealInitialHand();
            send("Fiches rimanenti: " + chips);
            send("Digita HIT per pescare o STAY per fermarti.");
        } catch (NumberFormatException e) {
            send("Formato errato. Usa: BET <numero>");
        }
    }

    private void handleHit() {
        if (hand.isEmpty()) {
            send("Prima fai una puntata: BET <n>");
            return;
        }
        hand.add(deck.draw());
        send("La tua mano: " + handToString() + "  [Valore: " + handValue() + "]");
        if (handValue() > 21) {
            busted = true;
            send("BUST! Hai sforato 21.");
            endTurn();
        }
    }

    private void handleStay() {
        if (hand.isEmpty()) {
            send("Prima fai una puntata: BET <n>");
            return;
        }
        standing = true;
        send("Hai deciso di fermarti con " + handValue() + ".");
        endTurn();
    }

    // ─────────────────────────────────────────────
    //  Logica di gioco
    // ─────────────────────────────────────────────

    /** Distribuisce le due carte iniziali. */
    private void dealInitialHand() {
        hand.clear();
        hand.add(deck.draw());
        hand.add(deck.draw());
        send("Mano iniziale: " + handToString() + "  [Valore: " + handValue() + "]");
    }

    /**
     * Calcola il valore della mano gestendo gli Assi:
     * ogni Asso (valore 11) può essere abbassato a 1 se si sfora 21.
     */
    public static int calcHandValue(List<Integer> h) {
        int total = 0;
        int aces  = 0;
        for (int c : h) {
            total += c;
            if (c == 11) aces++;
        }
        while (total > 21 && aces > 0) {
            total -= 10;   // Asso da 11 → 1
            aces--;
        }
        return total;
    }

    private int handValue() {
        return calcHandValue(hand);
    }

    /** Rappresentazione leggibile della mano. */
    private String handToString() {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < hand.size(); i++) {
            sb.append(cardLabel(hand.get(i)));
            if (i < hand.size() - 1) sb.append(", ");
        }
        sb.append("]");
        return sb.toString();
    }

    /** Etichetta human-friendly per il valore numerico della carta. */
    private String cardLabel(int v) {
        return switch (v) {
            case 11 -> "A";
            case 10 -> "10";   // potrebbe essere J/Q/K ma li trattiamo tutti come 10
            default -> String.valueOf(v);
        };
    }

    // ─────────────────────────────────────────────
    //  Risoluzione round (chiamata dal server)
    // ─────────────────────────────────────────────
    public void resolveRound(int dealerValue, String dealerHandStr) {
        send("-----------------------------");
        send("Mano del banco: " + dealerHandStr + "  [Valore: " + dealerValue + "]");

        if (busted) {
            send("Risultato: HAI PERSO (bust).");
        } else {
            int pv = handValue();
            if (dealerValue > 21) {
                chips += currentBet * 2;
                send("Il banco ha sforato! HAI VINTO " + currentBet + " fiches.");
            } else if (pv > dealerValue) {
                chips += currentBet * 2;
                send("HAI VINTO " + currentBet + " fiches.");
            } else if (pv == dealerValue) {
                chips += currentBet;     // restituzione puntata
                send("PAREGGIO – la tua puntata ti viene restituita.");
            } else {
                send("HAI PERSO " + currentBet + " fiches.");
            }
        }
        send("Fiches attuali: " + chips);
        send("-----------------------------");
        inRound = false;

        if (chips <= 0) {
            send("Hai esaurito le fiches. Game over!");
            disconnect();
        }
    }

    // ─────────────────────────────────────────────
    //  Controllo turno
    // ─────────────────────────────────────────────
    public void startTurn(CountDownLatch latch) {
        this.turnLatch = latch;
        myTurn = true;
        hand.clear();
        send("=============================");
        send("È IL TUO TURNO, " + playerName + "!");
        send("Fiches disponibili: " + chips);
        send("Comandi: BET <n> | QUIT");
    }

    public synchronized void endTurn() {
        myTurn = false;
        notifyAll();
        if (turnLatch != null) {
            turnLatch.countDown();
            turnLatch = null;
        }
    }

    // ─────────────────────────────────────────────
    //  Disconnessione
    // ─────────────────────────────────────────────
    public void disconnect() {
        active = false;
        myTurn = false;
        // Se il giocatore si disconnette durante il turno, sblocca il latch
        if (turnLatch != null) {
            turnLatch.countDown();
            turnLatch = null;
        }
        try { socket.close(); } catch (IOException ignored) {}
    }

    // ─────────────────────────────────────────────
    //  Getter
    // ─────────────────────────────────────────────
    // Attivo = connesso E (ha chips oppure è ancora in un round in corso)
    public boolean isActive()  { return active && (chips > 0 || inRound); }
    public boolean isMyTurn()  { return myTurn; }
    public String  getName()   { return playerName; }

    // Messaggio uscente verso il client
    public void send(String msg) {
        if (out != null) out.println(msg);
    }
}

