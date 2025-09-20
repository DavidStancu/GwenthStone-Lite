package org.poo.main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.*;
import org.poo.main.Cards.*;
import org.poo.main.GameMechanics.*;
import java.util.ArrayList;
import java.util.Objects;

/**
 * The Game class manages the overall logic and state of the card game,
 * including players, board state, rounds, turns, and game actions.
 */
public class Game {
    private Player[] player = new Player[2];
    private int currentPlayer;
    private Board board;
    private int round;
    private int turn;
    private int totalGames;

    /**
     * Initializes players, board, and other game variables.
     */
    public Game() {
        player[0] = new Player();
        player[1] = new Player();
        player[0].setMana(0);
        player[1].setMana(0);
        this.board = new Board();
        this.currentPlayer = 0;
        this.round = 0;
        this.turn = 0;
    }

    /**
     * Starts a new game by resetting the game state, initializing player decks and heroes,
     * and setting the starting player and first round.
     *
     * @param inputData      The input data containing player deck information.
     * @param startGameInput The starting configurations for the game.
     */
    private void startGame(final Input inputData, final StartGameInput startGameInput) {
        resetGame();
        player[0].setDeck(inputData.getPlayerOneDecks(), startGameInput.getPlayerOneDeckIdx(),
                startGameInput.getShuffleSeed());
        player[0].setHero(startGameInput.getPlayerOneHero());

        player[1].setDeck(inputData.getPlayerTwoDecks(), startGameInput.getPlayerTwoDeckIdx(),
                startGameInput.getShuffleSeed());
        player[1].setHero(startGameInput.getPlayerTwoHero());

        currentPlayer = startGameInput.getStartingPlayer() - 1;
        turn = 0;
        round = 0;
        startRound();
    }

    /**
     * Executes the game logic, processing a sequence of actions provided as input.
     *
     * @param inputData The input data containing player and game configurations.
     * @param output    The JSON output node for recording game results and errors.
     * @param gameInput The sequence of actions to execute during the game.
     */
    public void playGame(final Input inputData, final ArrayNode output, final GameInput gameInput) {
        startGame(inputData, gameInput.getStartGame());

        ArrayList<ActionsInput> actions = gameInput.getActions();
        for (ActionsInput action : actions) {
            String command = action.getCommand();
            switch (command) {
                case "getPlayerDeck" -> getPlayerDeck(output, action);
                case "getPlayerHero" -> getPlayerHero(output, action);
                case "getPlayerTurn" -> getPlayerTurn(output, action);
                case "getCardsInHand" -> getCardsInHand(output, action);
                case "endPlayerTurn" -> endPlayerTurn();
                case "getPlayerMana" -> getPlayerMana(output, action);
                case "getCardsOnTable" -> getCardsOnTable(output, action);
                case "placeCard" -> placeCard(output, action);
                case "cardUsesAttack" -> attackCard(output, action);
                case "cardUsesAbility" -> cardUseAbility(output, action);
                case "useAttackHero" -> useAttackHero(output, action);
                case "useHeroAbility" -> useHeroAbility(output, action);
                case "getCardAtPosition" -> getCardAtPosition(output, action);
                case "getFrozenCardsOnTable" -> getFrozenCardsOnTable(output, action);
                case "getTotalGamesPlayed" -> getTotalGamesPlayed(output, action);
                case "getPlayerOneWins" -> getPlayerOneWins(output, action);
                case "getPlayerTwoWins" -> getPlayerTwoWins(output, action);
                default -> { }
            }
        }
    }

    /**
     * Retrieves and outputs the deck of a specified player.
     *
     * @param output The JSON output node for storing the result.
     * @param action The action containing the player index.
     */
    private void getPlayerDeck(final ArrayNode output, final ActionsInput action) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode actionNode = objectMapper.createObjectNode();
        actionNode.put("command", action.getCommand());
        actionNode.put("playerIdx", action.getPlayerIdx());
        ArrayNode arrayNode = player[action.getPlayerIdx() - 1].printDeck();
        actionNode.set("output", arrayNode);

        output.add(actionNode);
    }

    /**
     * Retrieves and outputs the hero of a specified player.
     *
     * @param output The JSON output node for storing the result.
     * @param action The action containing the player index.
     */
    private void getPlayerHero(final ArrayNode output, final ActionsInput action) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode actionNode = objectMapper.createObjectNode();
        actionNode.put("command", action.getCommand());
        actionNode.put("playerIdx", action.getPlayerIdx());
        ObjectNode heroNode = player[action.getPlayerIdx() - 1].getHero();
        actionNode.set("output", heroNode);
        output.add(actionNode);
    }

    /**
     * Retrieves and outputs the current player's turn.
     *
     * @param output The JSON output node for storing the result.
     * @param action The action containing the command details.
     */
    private void getPlayerTurn(final ArrayNode output, final ActionsInput action) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode actionNode = objectMapper.createObjectNode();
        actionNode.put("command", action.getCommand());
        actionNode.put("output", currentPlayer + 1);
        output.add(actionNode);
    }

    /**
     * Retrieves the cards in the hand of the specified player and adds the output
     * to the provided JSON array.
     *
     * @param output the JSON array to which the hand information will be added.
     * @param action the action input containing the player's index and command.
     */
    private void getCardsInHand(final ArrayNode output, final ActionsInput action) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode actionNode = objectMapper.createObjectNode();
        actionNode.put("command", action.getCommand());
        actionNode.put("playerIdx", action.getPlayerIdx());

        ArrayList<Minion> hand = player[action.getPlayerIdx() - 1].getHand();
        ArrayNode handArray = objectMapper.createArrayNode();

        for (Minion card : hand) {
            ObjectNode cardNode = objectMapper.createObjectNode();
            cardNode.put("name", card.getName());
            cardNode.put("mana", card.getManaCost());
            cardNode.put("attackDamage", card.getAttackDamage());
            cardNode.put("health", card.getHealthPoints());
            cardNode.put("description", card.getDescription());

            ArrayNode colorsNode = objectMapper.createArrayNode();
            for (String color : card.getColors()) {
                colorsNode.add(color);
            }
            cardNode.set("colors", colorsNode);

            handArray.add(cardNode);
        }

        actionNode.set("output", handArray);
        output.add(actionNode);
    }

    /**
     * Retrieves the current mana of the specified player and adds the output
     * to the provided JSON array.
     *
     * @param output the JSON array to which the player's mana information will be added.
     * @param action the action input containing the player's index and command.
     */
    private void getPlayerMana(final ArrayNode output, final ActionsInput action) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode actionNode = objectMapper.createObjectNode();
        actionNode.put("command", action.getCommand());

        int playerMana = player[action.getPlayerIdx() - 1].getMana();
        actionNode.put("output", playerMana);
        actionNode.put("playerIdx", action.getPlayerIdx());

        output.add(actionNode);
    }

    /**
     * Retrieves all cards currently on the board, grouped by rows,
     * and adds the output to the provided JSON array.
     *
     * @param output the JSON array to which the board state will be added.
     * @param action the action input containing the command.
     */
    private void getCardsOnTable(final ArrayNode output, final ActionsInput action) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode actionNode = objectMapper.createObjectNode();
        actionNode.put("command", action.getCommand());

        ArrayNode cardsOnTable = objectMapper.createArrayNode();
        for (int i = 0; i < MagicNumbers.TABLE_ROWS; i++) {
            Minion[] row;
            ArrayNode rowArray = objectMapper.createArrayNode();
            if (i == 0) {
                row = board.getBackRow1();
            } else if (i == 1) {
                row = board.getFrontRow1();
            } else if (i == 2) {
                row = board.getFrontRow2();
            } else {
                row = board.getBackRow2();
            }

            for (Minion card : row) {
                if (card != null) {
                    ObjectNode cardNode = objectMapper.createObjectNode();
                    cardNode.put("mana", card.getManaCost());
                    cardNode.put("attackDamage", card.getAttackDamage());
                    cardNode.put("health", card.getHealthPoints());
                    cardNode.put("description", card.getDescription());
                    ArrayNode colorsNode = objectMapper.createArrayNode();
                    for (String color : card.getColors()) {
                        colorsNode.add(color);
                    }
                    cardNode.set("colors", colorsNode);
                    cardNode.put("name", card.getName());

                    rowArray.add(cardNode);
                }
            }
            cardsOnTable.add(rowArray);
        }

        actionNode.set("output", cardsOnTable);
        output.add(actionNode);
    }

    /**
     * Places a card from the current player's hand onto the board at the
     * appropriate row based on the card's type.
     * Validates placement rules, such as mana availability and row capacity,
     * before placing the card.
     * Updates the player's mana and removes the card from their hand if
     * placement is successful.
     *
     * @param output the JSON array to which the result or errors will be added.
     * @param action the action input containing the hand index of the card to
     *               place and the command details.
     */
    public void placeCard(final ArrayNode output, final ActionsInput action) {
        if (ErrorHandler.placeErrorHandler(output, action, player, currentPlayer, board)) {
            return;
        }
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode actionNode = objectMapper.createObjectNode();
        actionNode.put("command", action.getCommand());

        int handIdx = action.getHandIdx();
        Minion cardToPlace = player[currentPlayer].getCardFromHand(handIdx);

        int rowIdx;
        if (currentPlayer == 0) {
            if (player[currentPlayer].getCardFromHand(handIdx).getTank()
                    || player[currentPlayer].getCardFromHand(handIdx).getDruid()) {
                rowIdx = MagicNumbers.FRONT_ROW_2_ID;
            } else {
                rowIdx = MagicNumbers.BACK_ROW_2_ID;
            }
        } else {
            if (player[currentPlayer].getCardFromHand(handIdx).getTank()
                    || player[currentPlayer].getCardFromHand(handIdx).getDruid()) {
                rowIdx = MagicNumbers.FRONT_ROW_1_ID;
            } else {
                rowIdx = MagicNumbers.BACK_ROW_1_ID;
            }
        }

        if (board.isRowFull(rowIdx)) {
            actionNode.put("error", "Cannot place card on table since row is full");
            output.add(actionNode);
            return;
        }
        board.addCardToRow(currentPlayer, rowIdx, cardToPlace);
        player[currentPlayer].useMana(cardToPlace.getManaCost());
        player[currentPlayer].removeCardFromHand(handIdx);
    }

    /**
     * Retrieves the card at a specific position on the board and adds its details to the output.
     * If no card is found at the given position, an appropriate message is added to the output.
     *
     * @param output the JSON array to which the card details or error message will be added.
     * @param action the action input containing the position coordinates (x, y) and
     *               the command details.
     */
    public void getCardAtPosition(final ArrayNode output, final ActionsInput action) {
        if (ErrorHandler.getCardAtPositionErrorHandler(output, action, board)) {
            return;
        }

        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode actionNode = objectMapper.createObjectNode();
        actionNode.put("command", action.getCommand());

        int x = action.getX();
        int y = action.getY();

        Minion card = board.getCardFromRow(x, y);

        if (card == null) {
            actionNode.put("output", "No card available at that position.");
            actionNode.put("x", action.getX());
            actionNode.put("y", action.getY());
        } else {
            ObjectNode cardNode = objectMapper.createObjectNode();
            cardNode.put("mana", card.getManaCost());
            cardNode.put("attackDamage", card.getAttackDamage());
            cardNode.put("health", card.getHealthPoints());
            cardNode.put("description", card.getDescription());

            ArrayNode colorsNode = objectMapper.createArrayNode();
            for (String color : card.getColors()) {
                colorsNode.add(color);
            }
            cardNode.set("colors", colorsNode);

            cardNode.put("name", card.getName());

            actionNode.set("output", cardNode);
        }

        actionNode.put("x", action.getX());
        actionNode.put("y", action.getY());
        output.add(actionNode);
    }

    /**
     * Executes an attack by a card at the specified attacker coordinates against a card
     * at the attacked coordinates.
     * Reduces the health points of the attacked card by the attacker's attack damage.
     * Marks the attacking card as having attacked and removes the attacked card if
     * its health drops to 0 or below.
     *
     * @param output the JSON array to which any errors or status updates will be added.
     * @param action the action input containing the attacker and attacked card coordinates,
     *               as well as the command details.
     */
    public void attackCard(final ArrayNode output, final ActionsInput action) {
        if (ErrorHandler.carduseAttackOrAbilityErrorHandler(output, action, board, currentPlayer)) {
            return;
        }

        Coordinates attackerCoords = action.getCardAttacker();
        Coordinates attackedCoords = action.getCardAttacked();

        Minion cardAttacker = board.getCardFromRow(attackerCoords.getX(), attackerCoords.getY());
        Minion cardAttacked = board.getCardFromRow(attackedCoords.getX(), attackedCoords.getY());

        if (cardAttacker == null || cardAttacked == null) {
            System.out.println("Invalid cards involved in attack.");
            return;
        }

        int attackDamage = cardAttacker.getAttackDamage();
        cardAttacked.setHealthPoints(cardAttacked.getHealthPoints() - attackDamage);

        cardAttacker.setHasAttacked(true);

        if (cardAttacked.getHealthPoints() <= 0) {
            board.removeCardFromRow(board.getRowForPlayer(1 - currentPlayer,
                                    attackedCoords.getX()), attackedCoords.getY());
        }
    }

    /**
     * Executes the ability of a card at the specified attacker coordinates on the card
     * at the attacked coordinates.
     * Handles specific abilities based on the attacking card's name.
     * Marks the attacking card as having attacked. If the attacked card's health drops to 0,
     * it is removed from the board.
     *
     * @param output the JSON array to which any errors or status updates will be added.
     * @param action the action input containing the attacker and attacked card coordinates,
     *               as well as the command details.
     */
    public void cardUseAbility(final ArrayNode output, final ActionsInput action) {
        if (ErrorHandler.carduseAttackOrAbilityErrorHandler(output, action, board, currentPlayer)) {
            return;
        }

        Coordinates attackerCoords = action.getCardAttacker();
        Coordinates attackedCoords = action.getCardAttacked();

        Minion cardAttacker = board.getCardFromRow(attackerCoords.getX(), attackerCoords.getY());
        Minion cardAttacked = board.getCardFromRow(attackedCoords.getX(), attackedCoords.getY());

        if (Objects.equals(cardAttacker.getName(), "Disciple")) {
            cardAttacker.godsPlan(cardAttacked);
            cardAttacker.setHasAttacked(true);
            return;
        }

        switch (cardAttacker.getName()) {
            case "The Ripper":
                cardAttacker.weakKnees(cardAttacked);
                break;

            case "Miraj":
                cardAttacker.skyjack(cardAttacked);
                break;

            case "The Cursed One":
                cardAttacker.shapeshift(cardAttacked);

                if (cardAttacked.getHealthPoints() <= 0) {
                    board.removeCardFromRow(board.getRowForPlayer(1 - currentPlayer,
                            attackedCoords.getX()), attackedCoords.getY());
                }
                break;

            default:
                return;
        }
        cardAttacker.setHasAttacked(true);
    }

    /**
     * Executes an attack by a card on the enemy hero. Reduces the hero's health points
     * based on the attacker's damage.
     * Marks the attacker as having attacked and handles game-ending conditions
     * if the hero's health drops to 0 or below.
     * Adds a game-ending message to the output if applicable.
     *
     * @param output the JSON array to which the game status or errors will be added.
     * @param action the action input containing the attacker's coordinates and
     *               the command details.
     */
    public void useAttackHero(final ArrayNode output, final ActionsInput action) {
        if (ErrorHandler.cardAttackHeroErrorHandler(output, action, board)) {
            return;
        }

        Coordinates attackerCoords = action.getCardAttacker();
        Minion cardAttacker = board.getCardFromRow(attackerCoords.getX(), attackerCoords.getY());

        if (cardAttacker == null) {
            System.out.println("Invalid card attacker at position: "
                    + attackerCoords.getX() + " " + attackerCoords.getY());
            return;
        }

        Hero enemyHero = player[1 - currentPlayer].getHeroNormal();

        int attackDamage = cardAttacker.getAttackDamage();
        enemyHero.setHealthPoints(enemyHero.getHealthPoints() - attackDamage);

        cardAttacker.setHasAttacked(true);

        if (enemyHero.getHealthPoints() <= 0) {
            String message = currentPlayer == 0
                    ? "Player one killed the enemy hero."
                    : "Player two killed the enemy hero.";
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode resultNode = objectMapper.createObjectNode();
            resultNode.put("gameEnded", message);
            output.add(resultNode);
            totalGames++;
            player[currentPlayer].setGamesWon(player[currentPlayer].getGamesWon() + 1);

        }
    }

    /**
     * Activates the current player's hero ability on the specified row.
     * Adjusts mana cost and marks the hero as having used its ability.
     * The affected row is determined based on the hero's name and ability type.
     *
     * @param output the JSON array to which any errors or status updates will be added.
     * @param action the action input containing the affected row and the command details.
     */
    private void useHeroAbility(final ArrayNode output, final ActionsInput action) {
        if (ErrorHandler.heroUseAbilityErrorHandler(output, action, player, currentPlayer, board)) {
            return;
        }

        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode actionNode = objectMapper.createObjectNode();

        String command = action.getCommand();
        int affectedRow = action.getAffectedRow();
        actionNode.put("command", command);
        actionNode.put("affectedRow", affectedRow);

        Hero hero = player[currentPlayer].getHeroNormal();

        Minion[] row = null;
        if (hero.getName().equals("General Kocioraw") || hero.getName().equals("King Mudface")) {
            row = board.getRowForPlayer(currentPlayer, affectedRow);
        } else if (hero.getName().equals("Lord Royce")
                   || hero.getName().equals("Empress Thorina")) {
            row = board.getRowForPlayer(1 - currentPlayer, affectedRow);
        }

        hero.useAbility(board, row);
        hero.setHasAttacked(true);
        player[currentPlayer].setMana(player[currentPlayer].getMana() - hero.getManaCost());
    }

    /**
     * Retrieves all frozen cards currently on the table and adds them to the output JSON.
     * The method checks all rows for frozen cards and creates a detailed JSON representation
     * for each frozen card found, including its stats, description, colors, and name.
     *
     * @param output the JSON array to which the frozen cards data will be added.
     * @param action the action input containing the command details.
     */
    public void getFrozenCardsOnTable(final ArrayNode output, final ActionsInput action) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode actionNode = objectMapper.createObjectNode();

        String command = action.getCommand();
        actionNode.put("command", command);

        ArrayNode frozenCards = objectMapper.createArrayNode();

        Minion[][] rows = {
                board.getBackRow1(),
                board.getFrontRow1(),
                board.getFrontRow2(),
                board.getBackRow2()
        };

        for (Minion[] row : rows) {
            for (Minion card : row) {
                if (card != null && card.isFrozen()) {
                    ObjectNode frozenCard = objectMapper.createObjectNode();
                    frozenCard.put("attackDamage", card.getAttackDamage());
                    ArrayNode colorsNode = objectMapper.createArrayNode();
                    for (String color : card.getColors()) {
                        colorsNode.add(color);
                    }
                    frozenCard.set("colors", colorsNode);
                    frozenCard.put("description", card.getDescription());
                    frozenCard.put("health", card.getHealthPoints());
                    frozenCard.put("mana", card.getManaCost());
                    frozenCard.put("name", card.getName());
                    frozenCards.add(frozenCard);
                }
            }
        }

        actionNode.set("output", frozenCards);
        output.add(actionNode);
    }

    /**
     * Retrieves the total number of games played and adds it to the output JSON.
     *
     * @param output the JSON array to which the total games count will be added.
     * @param action the action input containing the command details.
     */
    private void getTotalGamesPlayed(final ArrayNode output, final ActionsInput action) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode actionNode = objectMapper.createObjectNode();
        actionNode.put("command", action.getCommand());
        actionNode.put("output", getTotalGames());
        output.add(actionNode);
    }

    /**
     * Retrieves the total number of games won by Player One and adds it to the output JSON.
     *
     * @param output the JSON array to which Player One's win count will be added.
     * @param action the action input containing the command details.
     */
    private void getPlayerOneWins(final ArrayNode output, final ActionsInput action) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode actionNode = objectMapper.createObjectNode();
        actionNode.put("command", action.getCommand());
        actionNode.put("output", player[0].getGamesWon());
        output.add(actionNode);
    }

    /**
     * Retrieves the total number of games won by Player Two and adds it to the output JSON.
     *
     * @param output the JSON array to which Player Two's win count will be added.
     * @param action the action input containing the command details.
     */
    private void getPlayerTwoWins(final ArrayNode output, final ActionsInput action) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode actionNode = objectMapper.createObjectNode();
        actionNode.put("command", action.getCommand());
        actionNode.put("output", player[1].getGamesWon());
        output.add(actionNode);
    }

    /**
     * Starts a new round of the game. Resets attack statuses for all cards and heroes,
     * draws new cards for both players, adds mana based on the round, and
     *switches the current player.
     * Ensures mana does not exceed the maximum allowed.
     */
    private void startRound() {
        round++;
        board.resetHasAttacked();
        player[currentPlayer].getHeroNormal().setHasAttacked(false);
        player[1 - currentPlayer].getHeroNormal().setHasAttacked(false);

        Minion nextCard1 = player[currentPlayer].drawCard();
        if (nextCard1 != null) {
            player[currentPlayer].addCardToHand(nextCard1);
        }

        player[currentPlayer].addMana(Math.min(round, MagicNumbers.MAX_MANA));

        currentPlayer = 1 - currentPlayer;
        Minion nextCard2 = player[currentPlayer].drawCard();
        if (nextCard2 != null) {
            player[currentPlayer].addCardToHand(nextCard2);
        }

        player[currentPlayer].addMana(Math.min(round, MagicNumbers.MAX_MANA));

        currentPlayer = 1 - currentPlayer;
    }

    /**
     * Ends the current player's turn. Unfreezes all cards for the current player,
     * switches to the next player, and starts a new round if applicable.
     * Outputs the current player's turn start message.
     */
    private void endPlayerTurn() {
        turn++;
        board.unfreezeAll(currentPlayer);
        currentPlayer = 1 - currentPlayer;
        if (turn % 2 == 0) {
            startRound();
        }
    }

    /**
     * Resets the game state, including the board, players, turn, and current player,
     * to prepare for a new game.
     */
    private void resetGame() {
        board = new Board();
        player[0].resetPlayer();
        player[1].resetPlayer();
        currentPlayer = 0;
        turn = 0;
    }

    /**
     * Retrieves the current turn number.
     *
     * @return the current turn number.
     */
    public int getTurn() {
        return this.turn;
    }

    /**
     * Sets the current turn number to a specified value.
     *
     * @param turn the turn number to set.
     */
    public void setTurn(final int turn) {
        this.turn = turn;
    }

    /**
     * Retrieves the total number of games played.
     *
     * @return the total games played.
     */
    public int getTotalGames() {
        return this.totalGames;
    }
}
