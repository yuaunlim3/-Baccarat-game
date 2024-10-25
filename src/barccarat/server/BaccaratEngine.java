package barccarat.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import barccarat.server.decks.Deck;

public class BaccaratEngine {
    private File readFile;
    private String name;
    private int amount;
    private int betAmt;
    private String bet;
    protected volatile Deck deck;
    protected String results;
    protected int rounds;

    public static final String[] pictureCards = {"11","12","13"};


    public BaccaratEngine( int numDeck) throws IOException{
        this.deck = new Deck();
        for(int num = 0; num < numDeck ;num++){
            this.deck.create();
        }
        this.results = "";
        this.rounds = 0;
    }

    public int getAmount() {
        return amount;
    }

    public void createPlayer(String name, int amount){
        try{
            clearCSV();
            this.name = name;
            this.amount = amount;
            this.readFile = new File(name + ".db");
            FileWriter fw = new FileWriter(readFile);
            BufferedWriter bw = new BufferedWriter(fw);
            if(this.readFile.createNewFile()){
                System.out.println("New file created");
            }
            bw.write(name +" " + Integer.toString(amount));
            bw.newLine();
            bw.flush();
            bw.close();
        }catch(IOException ex){
            ex.printStackTrace();
        }
    }


    public void betMade(int betAmt){
        System.out.printf("Bet of %d made\n",betAmt);
        this.betAmt = betAmt;
    }

    public synchronized String deal(String target) throws IOException{
        String result = "";
        if(this.betAmt <= this.amount && this.deck.getSize() > 4){
            this.bet = target;
            int clientTotal = 0;
            int dealerTotal = 0;
            String clientCards = "P";
            String dealerCards ="B";


            //Client draw first
            String card = this.deck.draw();
            String cardValue = card.substring(0, card.indexOf("."));
            if(Arrays.binarySearch(pictureCards, cardValue)>= 0){
                cardValue = "10";
            }
            clientCards += "|" + cardValue;
            clientTotal += Integer.parseInt(cardValue);

            //Dealer draw
            card = this.deck.draw();
            cardValue = card.substring(0, card.indexOf("."));
            if(Arrays.binarySearch(pictureCards, cardValue)>= 0){
                cardValue = "10";
            }
            dealerCards += "|" + cardValue;
            dealerTotal += Integer.parseInt(cardValue);

            //client draw again
            card = this.deck.draw();
            cardValue = card.substring(0, card.indexOf("."));
            if(Arrays.binarySearch(pictureCards, cardValue)>= 0){
                cardValue = "10";
            }
            clientCards += "|" + cardValue;
            clientTotal += Integer.parseInt(cardValue);

            //Dealer draw again
            card = this.deck.draw();
            cardValue = card.substring(0, card.indexOf("."));
            if(Arrays.binarySearch(pictureCards, cardValue)>= 0){
                cardValue = "10";
            }
            dealerCards += "|" + cardValue;
            dealerTotal += Integer.parseInt(cardValue);

            if((clientTotal % 10) < 5){
                card = this.deck.draw();
                cardValue = card.substring(0, card.indexOf("."));
                if(Arrays.binarySearch(pictureCards, cardValue)>= 0){
                    cardValue = "10";
                }
                clientCards += "|" + cardValue;
                clientTotal += Integer.parseInt(cardValue); 
            }

            if((dealerTotal % 10) < 5){
                card = this.deck.draw();
                cardValue = card.substring(0, card.indexOf("."));
                if(Arrays.binarySearch(pictureCards, cardValue)>= 0){
                    cardValue = "10";
                }
                dealerCards += "|" + cardValue;
                dealerTotal += Integer.parseInt(cardValue);
            }
            
            String win = winner(dealerTotal % 10, clientTotal % 10 );
            System.out.printf("<==================%s==============>",this.name);
            System.out.printf("Round %d=================\n",this.rounds);
            System.out.printf("The winner is %s, your bet is %s\n",win,this.bet);
            if(win.equalsIgnoreCase(this.bet)){
                System.out.printf("%s amount won\n",this.betAmt);
                this.amount += this.betAmt;
            }else if(win.equalsIgnoreCase("D")){
                System.out.println("Its a draw"
                );
            }else{
                System.out.printf("%s amount lose\n",this.betAmt);
                this.amount -= this.betAmt;
            }
            result = String.join(",",clientCards,dealerCards);
            if(this.results.equals("")){
                this.results = win;
            }
            this.results = this.results +"," + win;
            this.rounds++;
            if(this.rounds == 6){
                updateCSV();
                this.results="";
                this.rounds = 0;
                System.out.println("<==================>");
            }
        }
        else{
            updateCSV();
            result = "Insufficient amount";
            
        }
        System.out.println(result);
        return result;
    }

    public String winner(int dealer , int player){
        String win = "";
        if(dealer > player){
            win = "B";
        }else if(dealer < player){
            win = "P";
        }else if(dealer == player){
            win = "D";
        }
        return win;
            
        }

    public void amountLeft(){
        System.out.printf("Amount left: %d\n", this.amount);
    }

    public void clearCSV() throws IOException{
        File readFile = new File("gameHistory.csv");
        if(readFile.createNewFile()){
            System.out.println("File created");
        }
        
        FileWriter fw = new FileWriter(readFile);
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write("");
        bw.flush();
        bw.close();
    }

    public void updateCSV(){
        try{
            
            File readFile = new File("gameHistory.csv");
            FileReader fr = new FileReader(readFile);
            BufferedReader br = new BufferedReader(fr);

            ArrayList<String> inputs = new ArrayList<>();

            String line = "a";
            while(line != null){
                line = br.readLine();
                if(line == null){
                    break;
                }
                System.out.println(line);
                inputs.add(line);
            }
            br.close();
            inputs.add(this.results);
            inputs.add(this.name);

            FileWriter fw = new FileWriter(readFile);
            BufferedWriter bw = new BufferedWriter(fw);
            for(String input:inputs){
                bw.write(input);
                bw.newLine();
            }
            bw.flush();
            bw.close();
        }
        catch(IOException ex){
            ex.printStackTrace();
        }
    }
}
