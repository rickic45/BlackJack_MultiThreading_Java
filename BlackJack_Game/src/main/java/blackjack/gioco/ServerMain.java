package blackjack.gioco;

import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ServerMain {

    // Lista thread-safe dei giocatori connessi
    private static final List<PlayerHandler> players    = new CopyOnWriteArrayList<>();
    private static final List<Integer>       dealerHand = new ArrayList<>();
    private static final Deck                deck       = new Deck();

    // Contatore per nomi identificativi univoci: Player1, Player2, …
    private static final AtomicInteger playerCounter = new AtomicInteger(1);

    public static void main(String[] args) throws Exception {
        ServerSocket serverSocket = new ServerSocket(12345);
        System.out.println("Server Blackjack avviato sulla porta 12345...");

        // Thread di accettazione nuovi client (non blocca il loop di gioco)
        Thread acceptThread = new Thread(() -> {
            while (!serverSocket.isClosed()) {
                try {
                    Socket s          = serverSocket.accept();
                    String name       = "Player" + playerCounter.getAndIncrement();
                    PlayerHandler ph  = new PlayerHandler(s, deck, name);
                    players.add(ph);
                    new Thread(ph).start();
                    System.out.println("[SERVER] " + name + " si è connesso.");
                } catch (Exception e) {
                    if (!serverSocket.isClosed()) e.printStackTrace();
                }
            }
        });
        acceptThread.setDaemon(true);
        acceptThread.start();

        // ──────────────────────────────────────────
        //  Loop principale di gioco
        // ──────────────────────────────────────────
        while (true) {

            // Attendi finché ci sono giocatori
            if (players.isEmpty()) {
                Thread.sleep(500);
                continue;
            }

            // ── 1. Snapshot giocatori attivi per questo round ──
            List<PlayerHandler> activePlayers = new ArrayList<>();
            for (PlayerHandler p : players) {
                if (p.isActive()) activePlayers.add(p);
            }

            if (activePlayers.isEmpty()) {
                // Rimuovi giocatori non attivi e aspetta
                players.removeIf(p -> !p.isActive());
                Thread.sleep(500);
                continue;
            }

            // ── 2. Avvio turni di TUTTI i giocatori IN PARALLELO ──
            // Ogni giocatore riceve startTurn() nello stesso momento e gioca
            // autonomamente; il server aspetta che tutti abbiano concluso.
            CountDownLatch latch = new CountDownLatch(activePlayers.size());

            for (PlayerHandler p : activePlayers) {
                if (!p.isActive()) {
                    latch.countDown();
                    continue;
                }
                // Passa il latch al giocatore così può segnalare quando ha finito
                p.startTurn(latch);
            }

            // Attendi al massimo 5 minuti che tutti completino il turno
            boolean completed = latch.await(5, TimeUnit.MINUTES);
            if (!completed) {
                // Forza la fine del turno per chi non ha risposto
                for (PlayerHandler p : activePlayers) {
                    if (p.isMyTurn()) {
                        p.send("Tempo scaduto. Turno saltato.");
                        p.endTurn();
                    }
                }
            }

            // Controlla disconnessioni avvenute durante il turno
            for (PlayerHandler p : activePlayers) {
                if (!p.isActive()) {
                    System.out.println("[SERVER] " + p.getName() + " si è disconnesso durante il turno.");
                    notifyDisconnection(p, activePlayers);
                }
            }

            // ── 3. Il banco gioca (rivelato a tutti DOPO i turni) ──
            // Filtra ancora: qualcuno potrebbe essersi disconnesso
            List<PlayerHandler> stillActive = new ArrayList<>();
            for (PlayerHandler p : activePlayers) {
                if (p.isActive()) stillActive.add(p);
            }

            if (!stillActive.isEmpty()) {
                int dealerValue = dealerPlay();
                String dealerStr = dealerHandToString();
                System.out.println("[SERVER] Mano del banco: " + dealerHand + " → " + dealerValue);

                // ── 4. Risoluzione del round per ciascun giocatore ──
                for (PlayerHandler p : stillActive) {
                    p.resolveRound(dealerValue, dealerStr);
                }
            }

            // ── 5. Pulizia giocatori senza fiches / disconnessi ──
            for (PlayerHandler p : players) {
                if (!p.isActive()) {
                    System.out.println("[SERVER] " + p.getName() + " rimosso dalla partita (fiches esaurite o disconnesso).");
                }
            }
            players.removeIf(p -> !p.isActive());

            // Breve pausa prima del prossimo round
            Thread.sleep(2000);
        }
    }

    // ──────────────────────────────────────────────
    //  Dealer: pesca finché < 17 (regola standard)
    // ──────────────────────────────────────────────
    private static int dealerPlay() {
        dealerHand.clear();
        while (dealerHandValue() < 17) {
            dealerHand.add(deck.draw());
        }
        return dealerHandValue();
    }

    /** Valore mano dealer con gestione Assi (identico a PlayerHandler). */
    private static int dealerHandValue() {
        return PlayerHandler.calcHandValue(dealerHand);
    }

    private static String dealerHandToString() {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < dealerHand.size(); i++) {
            int v = dealerHand.get(i);
            sb.append(v == 11 ? "A" : String.valueOf(v));
            if (i < dealerHand.size() - 1) sb.append(", ");
        }
        sb.append("]");
        return sb.toString();
    }

    // ──────────────────────────────────────────────
    //  Notifica disconnessione agli altri giocatori
    // ──────────────────────────────────────────────
    private static void notifyDisconnection(PlayerHandler disconnected, List<PlayerHandler> group) {
        String msg = "[INFO] " + disconnected.getName() + " si è disconnesso dalla partita.";
        for (PlayerHandler p : group) {
            if (p != disconnected && p.isActive()) {
                p.send(msg);
            }
        }
    }
}
