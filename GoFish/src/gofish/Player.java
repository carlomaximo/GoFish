package gofish;

import java.util.ArrayList;
import java.util.Iterator;

public class Player {
    // Player attributes
    private String name;
    private ArrayList<Card> hand;
    private Integer score;

    // Player constructor
    public Player(String name) {
        this.name = name;
        hand = new ArrayList<>();
        score = 0;
    }

    // Player methods
    public String getName() {
        return name;
    }

    public void addCard(Card card) {
        hand.add(card);
    }

    public void removeCard(Card card) {
        hand.remove(card);
    }


    public ArrayList<Card> getHand() {
        sortHand(hand);
        return hand;
    }

    public Integer getScore() {
        return score;
    }

    public void incrementScore() {
        score++;
    }
    
    // Sorts the hand of the player so it looks prettier
    public void sortHand(ArrayList<Card> hand) {
        for (int i = 0; i < hand.size(); i++) {
            for (int j = i + 1; j < hand.size(); j++) {
                if (hand.get(i).getRank().ordinal() > hand.get(j).getRank().ordinal()) {
                    Card temp = hand.get(i);
                    hand.set(i, hand.get(j));
                    hand.set(j, temp);
                }
            }
        }
    }

    // Checks if the opposing player has cards of the provided rank
    public void checkForCard(Player oppPlayer, Card.Rank rank, Deck deck, boolean isPlayer) {
        boolean hasCard = false;
        int cardsTaken = 0;
        Iterator<Card> iterator = oppPlayer.hand.iterator();
        while (iterator.hasNext()) {
            Card card = iterator.next();
            if (card.getRank() == rank) {
                if (!hasCard) {
                    System.out.println("Card found!");
                    hasCard = true;
                }
                hand.add(card);
                iterator.remove();
                cardsTaken++;
            }
        }
        if (!hasCard) {
            System.out.println("Go fish!");
            Card card = deck.deal();
            if (card != null) {
                if (isPlayer) {
                    System.out.println("Card taken: " + card);
                }
                hand.add(card);
            }
        } else {
            System.out.println("Number of cards taken: " + cardsTaken);
        }
    }
}
