package barccarat.server.decks;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

public class Deck {
    protected Queue<String> deck;
    private List<String> cards;
    private File file;
    private FileWriter fw;
    private BufferedWriter bw;


    public Deck() throws IOException{
        this.deck = new LinkedList<>();
        this.cards = new ArrayList<>();
        this.file = new File("deck.db");
    }
    
    
    public void create() throws IOException{
        for(double cardType = 1; cardType < 14;cardType++){
            for(double suit = 0.1;suit < 0.5; suit += 0.1){
                double value = cardType + suit;
                String card = Double.toString(value);
                this.deck.add(card);
                this.cards.add(card);
            }
        }
        createFile();
        shuffle();

    }

    public void createFile() throws IOException{
        if(file.createNewFile()){
            System.out.println("New file created");
        }
        update();
    }

    public void update() throws IOException{
        this.fw = new FileWriter(this.file);
        this.bw = new BufferedWriter(this.fw);
        for(String card: this.deck){
            this.bw.write(card);
            this.bw.newLine();
        }
        this.bw.flush();
        this.bw.close();
    }


    public void updateDeck(){
        for(String card:this.cards){
            this.deck.add(card);
        }
    }

    public void shuffle(){
        this.deck = new LinkedList<String>();
        Random rand = new SecureRandom();
        for (int idx = this.cards.size() - 1; idx > 0; idx--) {
            int pos = rand.nextInt(idx + 1);
            String temp = this.cards.get(idx);
            this.cards.set(idx, this.cards.get(pos));
            this.cards.set(pos, temp);
        }
        updateDeck();
    }

    public String draw() throws IOException{
        if(this.deck.size() > 4){
            String cardDraw = this.deck.poll();
            update();
            return cardDraw;
        }else{
            System.err.println("Not enough cards to draw");
            return null;
        }
    }


    public Queue<String> getDeck() {
        return this.deck;
    }


    public int getSize() {
        return this.deck.size();
    }
}
