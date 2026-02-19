package blackjack.gioco;

import java.util.*;

public class Deck {

    // Valori delle carte: 2-10 valore nominale, J/Q/K = 10, Asso = 11 (gestito in handValue)
    private List<Integer> cards;

    public Deck() {
        cards = new ArrayList<>();
        reset();
    }

    /** Rigenera e mescola il mazzo (4 semi per ogni valore). */
    public synchronized void reset() {
        cards.clear();
        // Valori 2-10
        for (int v = 2; v <= 10; v++) {
            for (int s = 0; s < 4; s++) cards.add(v);
        }
        // J, Q, K → valore 10
        for (int s = 0; s < 4; s++) cards.add(10);
        for (int s = 0; s < 4; s++) cards.add(10);
        for (int s = 0; s < 4; s++) cards.add(10);
        // Asso → valore 11 (può essere ridotto a 1 in handValue)
        for (int s = 0; s < 4; s++) cards.add(11);

        Collections.shuffle(cards);
    }

    /** Pesca una carta; rigenera il mazzo se esaurito. */
    public synchronized int draw() {
        if (cards.isEmpty()) reset();
        return cards.remove(0);
    }
}
