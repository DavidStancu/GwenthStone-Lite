package org.poo.main.GameMechanics;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.*;
import org.poo.main.Cards.*;

/**
 * The {@code ErrorHandler} class provides methods to handle various
 * game-related errors during gameplay.
 * It ensures that the game rules are followed by validating the actions
 * of players and providing appropriate error messages.
 */
public class ErrorHandler {
    private String command;
    private Board board;
    private Player[] player;
    private int currentPlayer;

    /**
     * Handles errors related to placing a card on the board.
     *
     * @param output the output node where the error message will be added
     * @param action the action containing the command and card information
     * @param player the array of players in the game
     * @param currentPlayer the index of the current player
     * @param board the current game board
     * @return {@code true} if there was an error, {@code false} otherwise
     */
    public static boolean placeErrorHandler(final ArrayNode output, final ActionsInput action,
                                            final Player[] player, final int currentPlayer,
                                            final Board board) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode actionNode = objectMapper.createObjectNode();
        actionNode.put("command", action.getCommand());

        int handIdx = action.getHandIdx();
        Minion cardToPlace = player[currentPlayer].getCardFromHand(handIdx);

        if (cardToPlace == null) {
            actionNode.put("error", "Invalid hand index");
            actionNode.put("handIdx", action.getHandIdx());
            output.add(actionNode);
            return true;
        }

        if (player[currentPlayer].getMana() < cardToPlace.getManaCost()) {
            actionNode.put("error", "Not enough mana to place card on table.");
            actionNode.put("handIdx", action.getHandIdx());
            output.add(actionNode);
            return true;
        }

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
            return true;
        }

        return false;
    }

    /**
     * Handles errors related to fetching a card at a specific position on the board.
     *
     * @param output the output node where the error message will be added
     * @param action the action containing the command and position
     * @param board the current game board
     * @return {@code true} if there was an error, {@code false} otherwise
     */
    public static boolean getCardAtPositionErrorHandler(final ArrayNode output,
                                                        final ActionsInput action,
                                                        final Board board) {
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
            output.add(actionNode);
            return true;
        }
        return false;
    }

    /**
     * Handles errors related to the use of a card's attack or ability.
     *
     * @param output the output node where the error message will be added
     * @param action the action containing the command and card coordinates
     * @param board the current game board
     * @param currentPlayer the index of the current player
     * @return {@code true} if there was an error, {@code false} otherwise
     */
    public static boolean carduseAttackOrAbilityErrorHandler(final ArrayNode output,
                                                             final ActionsInput action,
                                                             final Board board,
                                                             final int currentPlayer) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode actionNode = objectMapper.createObjectNode();

        String command = action.getCommand();
        actionNode.put("command", command);

        Coordinates attackerCoords = action.getCardAttacker();
        Coordinates attackedCoords = action.getCardAttacked();

        if (attackedCoords != null) {
            ObjectNode attackedNode = objectMapper.createObjectNode();
            attackedNode.put("x", attackedCoords.getX());
            attackedNode.put("y", attackedCoords.getY());
            actionNode.set("cardAttacked", attackedNode);
        }

        if (attackerCoords != null) {
            ObjectNode attackerNode = objectMapper.createObjectNode();
            attackerNode.put("x", attackerCoords.getX());
            attackerNode.put("y", attackerCoords.getY());
            actionNode.set("cardAttacker", attackerNode);
        }

        Minion cardAttacker = board.getCardFromRow(attackerCoords.getX(),
                attackerCoords.getY());
        Minion cardAttacked = board.getCardFromRow(attackedCoords.getX(),
                attackedCoords.getY());

        if (cardAttacker == null || cardAttacked == null) {
            return true;
        }

        if (command.equals("cardUsesAttack")) {
            if (getAttackedPlayer(attackedCoords.getX()) == currentPlayer) {
                actionNode.put("error", "Attacked card does not belong to the enemy.");
                output.add(actionNode);
                return true;
            }

            if (cardAttacker.hasAttacked()) {
                actionNode.put("error", "Attacker card has already attacked this turn.");
                output.add(actionNode);
                return true;
            }

            if (board.hasTankOnRow(getAttackedPlayer(attackedCoords.getX()))
                    && !cardAttacked.getTank()) {
                actionNode.put("error", "Attacked card is not of type 'Tank'.");
                output.add(actionNode);
                return true;
            }

            if (cardAttacker.isFrozen()) {
                actionNode.put("error", "Attacker card is frozen.");
                output.add(actionNode);
                return true;
            }
        } else if (command.equals("cardUsesAbility")) {
            if (getAttackedPlayer(attackedCoords.getX()) == currentPlayer
                    && !"Disciple".equals(cardAttacker.getName())) {

                actionNode.put("error", "Attacked card does not belong to the enemy.");
                output.add(actionNode);
                return true;
            }

            if (getAttackedPlayer(attackedCoords.getX()) != currentPlayer
                    && "Disciple".equals(cardAttacker.getName())) {
                actionNode.put("error", "Attacked card does not belong to the current player.");
                output.add(actionNode);
                return true;
            }

            if (cardAttacker.isFrozen()) {
                actionNode.put("error", "Attacker card is frozen.");
                output.add(actionNode);
                return true;
            }

            if (cardAttacker.hasAttacked()) {
                actionNode.put("error", "Attacker card has already attacked this turn.");
                output.add(actionNode);
                return true;
            }

            if (board.hasTankOnRow(getAttackedPlayer(attackedCoords.getX()))
                    && !cardAttacked.getTank() && !"Disciple".equals(cardAttacker.getName())) {
                actionNode.put("error", "Attacked card is not of type 'Tank'.");
                output.add(actionNode);
                return true;
            }
        }
        return false;
    }

    /**
     * Determines which player the attacked card belongs to based on the row index.
     *
     * @param rowIndex the index of the row where the attacked card is located
     * @return {@code 0} if the card belongs to player 1, {@code 1} if the card belongs to player 2
     */
    private static int getAttackedPlayer(final int rowIndex) {
        if (rowIndex == 0 || rowIndex == 1) {
            return 1;
        }
        return 0;

    }

    /**
     * Handles errors related to attacking the hero.
     *
     * @param output the output node where the error message will be added
     * @param action the action containing the command and card coordinates
     * @param board the current game board
     * @return {@code true} if there was an error, {@code false} otherwise
     */
    public static boolean cardAttackHeroErrorHandler(final ArrayNode output,
                                                     final ActionsInput action,
                                                     final Board board) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode actionNode = objectMapper.createObjectNode();

        String command = action.getCommand();
        actionNode.put("command", command);

        Coordinates attackerCoords = action.getCardAttacker();

        if (attackerCoords != null) {
            ObjectNode attackerNode = objectMapper.createObjectNode();
            attackerNode.put("x", attackerCoords.getX());
            attackerNode.put("y", attackerCoords.getY());
            actionNode.set("cardAttacker", attackerNode);
        }

        if (attackerCoords == null) {
            actionNode.put("error", "Invalid coordinates for attacker.");
            output.add(actionNode);
            return true;
        }

        Minion cardAttacker = board.getCardFromRow(attackerCoords.getX(),
                attackerCoords.getY());
        if (cardAttacker == null) {
            actionNode.put("error", "Invalid attacker card.");
            output.add(actionNode);
            return true;
        }

        if (cardAttacker.isFrozen()) {
            actionNode.put("error", "Attacker card is frozen.");
            output.add(actionNode);
            return true;
        }

        if (cardAttacker.hasAttacked()) {
            actionNode.put("error", "Attacker card has already attacked this turn.");
            output.add(actionNode);
            return true;
        }

        if (board.hasTankOnRow(getAttackedPlayer(3 - attackerCoords.getX()))) {
            actionNode.put("error", "Attacked card is not of type 'Tank'.");
            output.add(actionNode);
            return true;
        }

        return false;
    }

    /**
     * Handles errors related to using a hero's ability.
     *
     * @param output the output node where the error message will be added
     * @param action the action containing the command and affected row
     * @param player the array of players in the game
     * @param currentPlayer the index of the current player
     * @param board the current game board
     * @return {@code true} if there was an error, {@code false} otherwise
     */
    public static boolean heroUseAbilityErrorHandler(final ArrayNode output,
                                                     final ActionsInput action,
                                                     final Player[] player,
                                                     final int currentPlayer,
                                                     final Board board) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode actionNode = objectMapper.createObjectNode();

        String command = action.getCommand();
        int affectedRow = action.getAffectedRow();
        actionNode.put("command", command);
        actionNode.put("affectedRow", affectedRow);

        Hero hero = player[currentPlayer].getHeroNormal();
        if (player[currentPlayer].getMana() < hero.getManaCost()) {
            actionNode.put("error", "Not enough mana to use hero's ability.");
            output.add(actionNode);
            return true;
        }

        if (hero.hasAttacked()) {
            actionNode.put("error", "Hero has already attacked this turn.");
            output.add(actionNode);
            return true;
        }

        if ((hero.getName().equals("Lord Royce") || hero.getName().equals("Empress Thorina"))
                && board.getPlayerForRow(affectedRow) == currentPlayer) {
            actionNode.put("error", "Selected row does not belong to the enemy.");
            output.add(actionNode);
            return true;
        }

        if ((hero.getName().equals("General Kocioraw") || hero.getName().equals("King Mudface"))
                && board.getPlayerForRow(affectedRow) != currentPlayer) {
            actionNode.put("error", "Selected row does not belong to the current player.");
            output.add(actionNode);
            return true;
        }
        return false;
    }
}
