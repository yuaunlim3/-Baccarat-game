package barccarat.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Console;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.Socket;

public class ClientApp {
    public static void main(String[] args) throws IOException {
        int port = 3000;
        String host = "";
        if (args.length > 0) {
            String[] inputs = args[0].split(":");
            host = inputs[0];
            port = Integer.parseInt(inputs[1]);

        }

        Console cons = System.console();
        String cmd = "a";
        Socket sock = null;
        BufferedWriter bw = null;
        BufferedReader br = null;

        try {
            // Create the socket once before the loop
            sock = new Socket(host, port);
            System.out.println("Connected!");

            // Set up output and input streams
            OutputStream os = sock.getOutputStream();
            Writer writer = new OutputStreamWriter(os);
            bw = new BufferedWriter(writer);

            InputStream is = sock.getInputStream();
            Reader reader = new InputStreamReader(is);
            br = new BufferedReader(reader);

            while (!cmd.equals("close")){
                System.out.println("========================");
                cmd = cons.readLine(">>> ");
                bw.write(cmd);
                bw.newLine();
                bw.flush();
                if(cmd.equals("close")){
                    break;
                }
                String[] inputs = cmd.split("\\|");
                if(inputs[0].equalsIgnoreCase("deal")){
                    String fromServer = br.readLine();
                    System.out.println(fromServer);  
                }
            }


        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // Close resources in the finally block
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (sock != null && !sock.isClosed()) {
                try {
                    sock.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}