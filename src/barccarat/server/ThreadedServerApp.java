package barccarat.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ThreadedServerApp {
    private static boolean running = true;
    private static ServerSocket socket; // Keep a reference to the server socket

    public static void stopServer() {
        running = false; // Set the flag to false
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close(); // Close the server socket to stop accepting new connections
            }
            System.out.println("Server is stopping...");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        ExecutorService thrPool = Executors.newFixedThreadPool(2); // Moved outside the try block
        try {
            int portNum = 3000;
            int deckNum = 1;
            if (args.length > 0) {
                portNum = Integer.parseInt(args[0]);
                deckNum = Integer.parseInt(args[1]);
            }
            socket = new ServerSocket(portNum);
            try {
                while (running) {
                    System.out.printf("Waiting for connection on port %d\n", portNum);
                    Socket sock = socket.accept();
                    System.out.println("Got a new connection\n");
                    BaccaratEngine game = new BaccaratEngine(deckNum);
                    
                    ClientHandler handler = new ClientHandler(sock,game);
                    thrPool.submit(handler);
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            } finally {
                // Wait for existing tasks to terminate
                thrPool.shutdown();
                try {
                    if (!thrPool.awaitTermination(60, TimeUnit.SECONDS)) {
                        thrPool.shutdownNow();
                    }
                } catch (InterruptedException ex) {
                    thrPool.shutdownNow();
                    Thread.currentThread().interrupt();
                }
            }
        } catch (IOException ex) {
            ex.getMessage();
        } finally {
            try {
                if (socket != null && !socket.isClosed()) {
                    socket.close();
                }
                System.out.println("Server socket closed.");
            } catch (IOException e) {
                e.getMessage();
            }
        }
    }
}
