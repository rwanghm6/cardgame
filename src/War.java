import processing.core.PApplet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class War extends CardGame {
    private Card lastComputerCard = null;
    private ArrayList<Card> playerChoices = new ArrayList<>();
    private ArrayList<Card> computerChoices = new ArrayList<>();
    private int playerScore = 0;
    private int computerScore = 0;
    private int choiceCount = 3; // number of choices shown to each side
    public War() {
        // Ensure the game is initialized and cards are dealt for War
        resetGame();
    }
    
    //Create method to deal cards//
    public void dealChoices(int count) {
        playerChoices.clear();
        computerChoices.clear();
        this.choiceCount = count;
        // Use parent class dealing to actually populate the Hand objects
        dealCards(count);
        // Mirror the hands into the choice lists so UI/logic can access them
        for (int i = 0; i < playerOneHand.getSize(); i++) {
            playerChoices.add(playerOneHand.getCard(i));
        }
        for (int i = 0; i < playerTwoHand.getSize(); i++) {
            computerChoices.add(playerTwoHand.getCard(i));
        }
    }

    //Scoring mech using compare//
    public int compareValues(Card a, Card b) {
        // Match the values produced by the default deck ("2".."10","J","Q","K","A").
        String[] ranks = {"2","3","4","5","6","7","8","9","10","J","Q","K","A"};
        int ra = -1, rb = -1;
        for (int i = 0; i < ranks.length; i++) {
            if (ranks[i].equals(a.value)) ra = i;
            if (ranks[i].equals(b.value)) rb = i;
        }
        if (ra > rb) return 1;
        if (ra < rb) return -1;
        return 0;
    }
    
    public ArrayList<Card> getPlayerChoices() {
        return playerChoices;
    }

    public ArrayList<Card> getComputerChoices() {
        return computerChoices;
    }

    public int getPlayerScore() {
        return playerScore;
    }

    public int getComputerScore() {
        return computerScore;
    }

    public int playRound(int playerIndex) {
        if (playerIndex < 0 || playerIndex >= playerChoices.size()) {
            throw new IllegalArgumentException("Invalid player index");
        }
        // Remove from both the choice list and the player's hand so UI stays consistent
        Card p = playerChoices.remove(playerIndex);
        playerOneHand.removeCard(p);

        if (computerChoices.isEmpty()) {
            return 0;
        }
        Random rnd = new Random();
        int compIndex = rnd.nextInt(computerChoices.size());
        Card c = computerChoices.remove(compIndex);
        playerTwoHand.removeCard(c);

        // remember played cards for UI
        lastPlayedCard = p;
        lastComputerCard = c;
        lastComputerCard.setTurned(false);

        int cmp = compareValues(p, c);
        if (cmp > 0) playerScore++;
        else if (cmp < 0) computerScore++;

        // Refill choices from the hands by drawing into the hands and mirroring
        while (playerChoices.size() < choiceCount && getDeckSize() > 0) {
            drawCard(playerOneHand);
            Card added = playerOneHand.getCard(playerOneHand.getSize() - 1);
            playerChoices.add(added);
        }
        while (computerChoices.size() < choiceCount && getDeckSize() > 0) {
            drawCard(playerTwoHand);
            Card added = playerTwoHand.getCard(playerTwoHand.getSize() - 1);
            computerChoices.add(added);
        }

        return cmp;
    }

    @Override
    public void handleCardClick(int mouseX, int mouseY) {
        // Only allow player clicks
        if (!playerOneTurn) return;

        // find clicked card index in playerOneHand
        int clickedIndex = -1;
        for (int i = playerOneHand.getSize() - 1; i >= 0; i--) {
            Card card = playerOneHand.getCard(i);
            if (card != null && card.isClicked(mouseX, mouseY)) {
                clickedIndex = i;
                break;
            }
        }
        if (clickedIndex == -1) return;

        // Play the round with the selected index
        playRound(clickedIndex);
        // After playing a round, ensure positions are updated and it's still player's turn according to War rules
        playerOneHand.positionCards(50, 450, 80, 120, 20);
        playerTwoHand.positionCards(50, 50, 80, 120, 20);
    }

    @Override
    public boolean hasDrawButton() {
        return false;
    }

    public Card getLastComputerCard() {
        return lastComputerCard;
    }

    public void resetGame() {
        playerChoices.clear();
        computerChoices.clear();
        playerScore = 0;
        computerScore = 0;
        deck.clear();
        createDeck();
        Collections.shuffle(deck);
        // Deal the full deck split between the two players for War
        dealFullDeckSplit();
    }

    //deal
    public void dealFullDeckSplit() {
        playerChoices.clear();
        computerChoices.clear();
        // clear existing hands
        playerOneHand.getCards().clear();
        playerTwoHand.getCards().clear();

        Collections.shuffle(deck);
        int half = deck.size() / 2;
        for (int i = 0; i < half; i++) {
            if (deck.isEmpty()) break;
            playerOneHand.addCard(deck.remove(0));
            if (deck.isEmpty()) break;
            Card c = deck.remove(0);
            c.setTurned(true);
            playerTwoHand.addCard(c);
        }

        // position card
        playerOneHand.positionCards(50, 450, 80, 120, 20);
        playerTwoHand.positionCards(50, 50, 80, 120, 20);

       
        for (int i = 0; i < playerOneHand.getSize(); i++) {
            playerChoices.add(playerOneHand.getCard(i));
        }
        for (int i = 0; i < playerTwoHand.getSize(); i++) {
            computerChoices.add(playerTwoHand.getCard(i));
        }
    }
    
}
