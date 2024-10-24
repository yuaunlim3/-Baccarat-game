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
import java.net.ServerSocket;
import java.net.Socket;

public class ServerApp {

    private static ServerSocket socket;
    private static Socket sock;

    public static void main(String[] args) {
        try{
            int portNum = 3000;
            int deckNum = 1;
            if(args.length > 0){
                portNum = Integer.parseInt(args[0]);
                deckNum = Integer.parseInt(args[1]);
            }
            socket = new ServerSocket(portNum);

            BaccaratEngine game = new BaccaratEngine(deckNum);

            System.out.printf("Waiting for connection on port %d\n", portNum);
            sock = socket.accept();
            System.out.println("Got a new connection\n");

            //to read from client
            InputStream is = sock.getInputStream();
            Reader reader = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(reader);

            
            // Get the write to client
            OutputStream os = sock.getOutputStream();
            Writer writer = new OutputStreamWriter(os);
            BufferedWriter bw = new BufferedWriter(writer);

            String fromClient;
            String toClient;

            while((fromClient = br.readLine()) != null){
                String[] inputs = fromClient.split("\\|");
                if(inputs[0].equalsIgnoreCase("login")){
                    game.createPlayer(inputs[1], Integer.parseInt(inputs[2]));
                }else if(inputs[0].equalsIgnoreCase("bet")){
                    int bet = Integer.parseInt(inputs[1]);
                    game.betMade(bet);
                }else if(inputs[0].equalsIgnoreCase("deal")){
                    toClient = game.deal(inputs[1]);
                    game.amountLeft();
                    bw.write(toClient);
                    bw.newLine();
                    bw.flush();
                }else if(inputs[0].equalsIgnoreCase("close")){
                    System.exit(0);
                }
            }
            bw.close();
            br.close();



        }catch(IOException ex){
            ex.printStackTrace();
        }finally{
            try {
                if (socket != null && !socket.isClosed()) {
                    socket.close();
                }
                System.out.println("Server socket closed.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
