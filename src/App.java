import processing.core.PApplet;

public class App extends PApplet {

    CardGame cardGame = new War();
    private int timer;

    public static void main(String[] args) {
        PApplet.main("App");
    }
    @Override
    public void settings() {
        size(600, 600);   
    }

    @Override
    public void draw() {
        background(255);
        // Draw player hands
        for (int i = 0; i < cardGame.playerOneHand.getSize(); i++) {
            Card card = cardGame.playerOneHand.getCard(i);
            if (card != null) {
                card.draw(this);
            }
        }
        // Draw computer hand
        for (int i = 0; i < cardGame.playerTwoHand.getSize(); i++) {
            Card card = cardGame.playerTwoHand.getCard(i);
            if (card != null) {
                card.draw(this);
            }
        }
        
        // Draw draw button if the current game uses it
        if (cardGame.hasDrawButton()) {
            fill(200);
            cardGame.drawButton.draw(this);
            fill(0);
            textAlign(CENTER, CENTER);
            text("Draw", cardGame.drawButton.x + cardGame.drawButton.width / 2, cardGame.drawButton.y + cardGame.drawButton.height / 2);
        }

        // Display current player
        fill(0);
        textSize(16);
        text("Current Player: " + cardGame.getCurrentPlayer(), width / 2, 20);

        // Display deck size
        text("Deck Size: " + cardGame.getDeckSize(), width / 2,
                height - 20);
        // Display last played card
        if (cardGame.getLastPlayedCard() != null) {
            cardGame.getLastPlayedCard().setPosition(width / 2 - 40, height / 2 - 60, 80, 120);
            cardGame.getLastPlayedCard().draw(this);
        }
        // For games that use the draw-button turn flow, show computer thinking and auto-act.
        if (cardGame.hasDrawButton() && cardGame.getCurrentPlayer().equals("Player Two")) {
            fill(0);
            textSize(16);
            text("Computer is thinking...", width / 2, height / 2 + 80);
            timer++;
            if (timer == 100) {
                cardGame.handleComputerTurn();
                timer = 0;
            }
        }

        cardGame.drawChoices(this);

        // If this is a War game, draw the computer's last played card beside the player's last played card
        if (cardGame instanceof War) {
            War war = (War) cardGame;
            Card comp = war.getLastComputerCard();
            if (comp != null) {
                comp.setPosition(width / 2 + 60, height / 2 - 60, 80, 120);
                comp.draw(this);
            }
        }
    }

    
    @Override
    public void mousePressed() {
        if (cardGame.hasDrawButton()) {
            cardGame.handleDrawButtonClick(mouseX, mouseY);
        }
        cardGame.handleCardClick(mouseX, mouseY);
    }

}
