package blackjack.gioco;

import java.io.*;
import java.net.*;

public class Client {
    public static void main(String[] args) throws Exception {
        Socket socket = new Socket("localhost", 12345);

        BufferedReader in   = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        BufferedReader user = new BufferedReader(new InputStreamReader(System.in));
        PrintWriter    out  = new PrintWriter(socket.getOutputStream(), true);

        // Thread dedicato alla lettura dei messaggi dal server
        Thread reader = new Thread(() -> {
            try {
                String msg;
                while ((msg = in.readLine()) != null) {
                    System.out.println(msg);
                }
            } catch (IOException ignored) {}
            System.out.println("[Connessione al server chiusa]");
            System.exit(0);
        });
        reader.setDaemon(true);
        reader.start();

        // Lettura input utente e invio al server
        String input;
        while ((input = user.readLine()) != null) {
            out.println(input);
        }
    }
}
