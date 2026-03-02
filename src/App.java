import processing.core.PApplet;

public class App extends PApplet {

    CardGame cardGame = new War();
    private int timer;
    private ClickableRectangle playAgainButton;  // UI element to restart the game
    private boolean endScreenStarted = false;      // has the end-game timer started?
    private boolean showEndScreen = false;         // whether overlay is visible
    private long endStartTime = 0;                 // millis when game ended

    public static void main(String[] args) {
        PApplet.main("App");
    }
    @Override
    public void settings() {
        size(600, 600);   
    }

    @Override
    public void setup() {
        // create the play-again button after size is known
        playAgainButton = new ClickableRectangle();
        playAgainButton.x = width - 120;
        playAgainButton.y = 10;
        playAgainButton.width = 100;
        playAgainButton.height = 30;
    }

    @Override
    public void draw() {
        background(255);
        // check for game over and manage end-screen timing
        if (cardGame.isGameOver()) {
            if (!endScreenStarted) {
                endStartTime = millis();
                endScreenStarted = true;
            } else if (!showEndScreen && millis() - endStartTime >= 2000) {
                showEndScreen = true;
            }
        }

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

        // Draw score panel
        drawScorePanel();

        // Draw play-again button
        drawPlayAgainButton();

        // Calculating score screeen
        if (cardGame.isGameOver() && endScreenStarted && !showEndScreen) {
            fill(255);
            rect(0, 0, width, height);
            fill(0);
            textSize(36);
            textAlign(CENTER, CENTER);
            text("Calculating", width / 2, height / 2);
        }

        // if end screen should be shown, render the overlay last
        if (showEndScreen) {
            drawEndScreen();
        }
    }

    
    @Override
    public void mousePressed() {
        // check play again first so it takes priority
        if (playAgainButton != null && playAgainButton.isClicked(mouseX, mouseY)) {
            cardGame.resetGame();
            // clear any end-screen state
            endScreenStarted = false;
            showEndScreen = false;
            return;
        }

        if (cardGame.hasDrawButton()) {
            cardGame.handleDrawButtonClick(mouseX, mouseY);
        }
        cardGame.handleCardClick(mouseX, mouseY);
    }

    private void drawScorePanel() {
        //dimensions and position
        int panelX = 10;
        int panelY = 10;
        int panelWidth = 150;
        int panelHeight = 110;

        //background
        fill(220, 240, 255);
        stroke(100);
        strokeWeight(2);
        rect(panelX, panelY, panelWidth, panelHeight, 8);

        //border
        noFill();
        stroke(50);
        strokeWeight(2);
        rect(panelX, panelY, panelWidth, panelHeight, 8);

        //text
        fill(0);
        textAlign(LEFT);
        textSize(13);
        textFont(createFont("Arial", 13, true));

        int textX = panelX + 12;
        int lineHeight = 25;
        int startY = panelY + 20;

        text("Your score: " + cardGame.getPlayerScore(), textX, startY);
        text("Their score: " + cardGame.getComputerScore(), textX, startY + lineHeight);
        text("Cards left: " + cardGame.getPlayerCardsRemaining(), textX, startY + lineHeight * 2);
    }

    private void drawPlayAgainButton() {
        if (playAgainButton == null) return;

        fill(200, 220, 200);
        stroke(80);
        strokeWeight(2);
        rect(playAgainButton.x, playAgainButton.y, playAgainButton.width, playAgainButton.height, 6);

        fill(0);
        textSize(14);
        textAlign(CENTER, CENTER);
        text("Play Again", playAgainButton.x + playAgainButton.width / 2,
                playAgainButton.y + playAgainButton.height / 2);
    }

    private void drawEndScreen() {
        // semi-transparent dark overlay
        fill(0, 0, 0, 150);
        rect(0, 0, width, height);

        // determine scores and outcome
        int playerScore = cardGame.getPlayerScore();
        int compScore = cardGame.getComputerScore();
        String scoreText = playerScore + " - " + compScore;
        String result;
        if (playerScore > compScore) result = "You win!";
        else if (playerScore < compScore) result = "You lose!";
        else result = "It's a tie!";

        fill(255);
        textAlign(CENTER, CENTER);
        textSize(32);
        text(scoreText, width / 2, height / 2 - 20);
        textSize(24);
        text(result, width / 2, height / 2 + 30);
    }
}
