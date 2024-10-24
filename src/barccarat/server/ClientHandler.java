package barccarat.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private final Socket sock;
    private BaccaratEngine game;

    public ClientHandler(Socket s, BaccaratEngine game) {
        this.sock = s;
        this.game = game;
    }

    @Override
    public void run() {
        String threadName = Thread.currentThread().getName();
        System.out.printf("===================%s===============\n",threadName);
        try {
            // to read from client
            InputStream is = sock.getInputStream();
            Reader reader = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(reader);

            // Get the write to client
            OutputStream os = sock.getOutputStream();
            Writer writer = new OutputStreamWriter(os);
            BufferedWriter bw = new BufferedWriter(writer);

            String fromClient;
            String toClient;

            while ((fromClient = br.readLine()) != null) {
                String[] inputs = fromClient.split("\\|");
                if (inputs[0].equalsIgnoreCase("login")) {
                    game.createPlayer(inputs[1], Integer.parseInt(inputs[2]));
                } else if (inputs[0].equalsIgnoreCase("bet")) {
                    int bet = Integer.parseInt(inputs[1]);
                    game.betMade(bet);
                } else if (inputs[0].equalsIgnoreCase("deal")) {
                    toClient = game.deal(inputs[1]);
                    game.amountLeft();
                    bw.write(toClient);
                    bw.newLine();
                    bw.flush();
                } else if (inputs[0].equalsIgnoreCase("close")) {
                    ThreadedServerApp.stopServer();
                }
            }
            bw.close();
            br.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                System.out.println("Socket close");
                sock.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
