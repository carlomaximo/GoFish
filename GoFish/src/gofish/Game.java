package gofish;

import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class Game {
    private ArrayList<Player> players;
    public Deck deck;
    private Player user = null;
    private Player computer = null;
    private ArrayList<Card.Rank> computerMemory;
    private boolean gameOver = false;
    private Random rand;
    private Scanner in;

    public void displayMenu() {
        System.out.println("Go Fish! (Created by Carlo Maximo)");
        System.out.println("Main Menu");
        System.out.println("1. Play Game");
        System.out.println("2. Exit");
    }

    public void createProfile() {
        System.out.println("Enter your name: ");
        String name = in.nextLine();
        players.add(new Player(name));
        players.add(new Player("Computer"));
        user = players.get(0);
        computer = players.get(1);
    }

    public void dealCards() {
        for (int i = 0; i < 8; i++) {
            for (Player player : players) {
                player.addCard(deck.deal());
            }
        }
    }

    public void checkBeginningDeal() {
        for (Player player : players) {
            hasFour(player);
        }
    }

    public void displayScores() {
        for (Player player : players) {
            System.out.println(player.getName() + "'s score: " + player.getScore());
        }
        System.out.println();
    }

    public boolean allPlayersHaveCards() {
        for (Player player : players) {
            if (player.getHand().isEmpty()) {
                return true;
            }
        }
        return false;
    }

    public boolean isCardInHand(Card.Rank rank, Player player) {
        for (Card card : player.getHand()) {
            if (card.getRank() == rank) {
                return true;
            }
        }
        return false;
    }

    public void hasFour(Player player) {
        for (Card.Rank rank : Card.Rank.values()) {
            int count = 0;
            ArrayList<Card> toRemove = new ArrayList<>();
            for (Card card : player.getHand()) {
                if (card.getRank() == rank) {
                    count++;
                    toRemove.add(card);
                }
            }
            if (count == 4) {
                computerMemory.remove(rank);
                System.out.println(player.getName() + " got a set of " + rank + "s!");
                for (Card card : toRemove) {
                    player.removeCard(card);
                }
                player.incrementScore();
            }
        }
    }

    public void interlude() {
        System.out.println("\nPress enter to continue...");
        in.nextLine();
    }

    public void userTurn() {
        boolean validTurn = false;

        while (!validTurn) {
            System.out.println("Your turn!");
            System.out.println("Your hand: ");
            if (user.getHand().isEmpty()) {
                System.out.println("You don't have any cards.");
                Card card = deck.deal();
                if (card != null) {
                    user.addCard(card);
                    System.out.println("You drew a card: " + card);
                }
                break;
            }
            else {
                displayHand(user);
            }

            System.out.println();
            System.out.println("Enter the rank you want to ask for: ");
            try {
                Card.Rank rank = Card.Rank.valueOf(in.nextLine());
                System.out.println("You asked for: " + rank);
                if (isCardInHand(rank, user)) {
                    user.checkForCard(computer, rank, deck, true);
                    if (!computerMemory.contains(rank)) {
                        computerMemory.add(rank);
                    }
                    validTurn = true;
                } else {
                    System.out.println("You don't have that rank.");
                    System.out.println();
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid rank.");
                System.out.println();
            }
        }
    }
    
    public void computerTurn() {
        boolean fromMemory = false;

        System.out.println("Computer's turn!");
        if (computer.getHand().isEmpty()) {
            System.out.println("Computer doesn't have any cards.");
            Card card = deck.deal();
            if (card != null) {
                computer.addCard(card);
            }
        }
        else {
            Card.Rank targetRank = computer.getHand().get(rand.nextInt(computer.getHand().size())).getRank();
            // overwrites targetRank if a rank is in computerMemory and in hand
            for (Card.Rank rank : computerMemory) {
                if (isCardInHand(rank, computer)) {
                    targetRank = rank;
                    fromMemory = true;
                    break;
                }
            }
            System.out.println("Computer asked for: " + targetRank);
            computer.checkForCard(user, targetRank, deck, false);

            // removes rank from computerMemory if it was in memory
            if (fromMemory) {
                computerMemory.remove(targetRank);
            }
        }
    }

    public void displayHand(Player player) {
        System.out.println(player.getName() + "'s hand: ");
        for (Card card : player.getHand()) {
            System.out.println(card);
        }
        System.out.println();
    }

    public void displayMemory() {
        System.out.println("Computer's memory: ");
        for (Card.Rank rank : computerMemory) {
            System.out.println(rank);
        }
        System.out.println();
    }

    public void displayDeck() {
        System.out.println();
        System.out.println("Number of cards left in deck: " + deck.getDeck().size());
        // for (Card card : deck.getDeck()) {
        //     System.out.println(card);
        // }
        System.out.println();
    }

    public void displayAll() {
        displayHand(user);
        displayHand(computer);
        displayMemory();
        displayDeck();
    }

    public void isGameOver() {
        if (deck.isEmpty() && allPlayersHaveCards()) {
            gameOver = true;
        }
    }
    
    public void whoWon() {
        displayScores();
        if (user.getScore() > computer.getScore()) {
            System.out.println("You won!");
        } else if (user.getScore() < computer.getScore()) {
            System.out.println("Computer won!");
        } else {
            System.out.println("It's a tie!");
        }
    }

    public void gameLogic() {
        checkBeginningDeal();
        do {
            // User turn
            displayScores();
            userTurn();
            hasFour(user);
            isGameOver();
            // displayAll(); // display all info, for debugging
            interlude();

            // Computer turn
            displayScores();
            computerTurn();
            hasFour(computer);
            isGameOver();
            // displayAll(); // display all info, for debugging
            interlude();

            displayDeck();
        } while (!gameOver);
        whoWon();
    }

    public void play() {
        boolean validInput = false;
        while (!validInput) {
            displayMenu();
            try {
                String choice = in.nextLine();
                if (choice.equals("1")) {
                    break;
                } else if (choice.equals("2")) {
                    System.out.println("Goodbye!");
                    in.close();
                    System.exit(0);
                } else {
                    System.out.println("Invalid input. Please try again.");
                }
            } catch (Exception e) {
                System.out.println("Exception: Invalid input. Please try again.");
                in.nextLine();
            }
        }
        createProfile();
        dealCards();
        gameLogic();
        validInput = true;
        System.out.println("Goodbye!");
        in.close();
    }

    public Game() {
        players = new ArrayList<Player>();
        deck = new Deck();
        deck.shuffle();
        computerMemory = new ArrayList<Card.Rank>();
        in = new Scanner(System.in);
        rand = new Random();
    }
}
