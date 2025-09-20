package org.poo.main.GameMechanics;

import org.poo.main.Cards.*;

/**
 * Represents the game board where players' cards (minions) are placed and interact.
 * The board consists of four rows:
 *     Front row 1 and back row 1 for Player 1.
 *     Front row 2 and back row 2 for Player 2.
 * Provides methods to manipulate rows and cards, such as adding, removing, and applying effects.
 */
public class Board {
    private Minion[] frontRow1;
    private Minion[] frontRow2;
    private Minion[] backRow1;
    private Minion[] backRow2;

    /**
     * Constructs a new {@code Board} with all rows initialized to empty arrays of {@code Minion}.
     */
    public Board() {
        frontRow1 = new Minion[MagicNumbers.ROW_LENGTH];
        frontRow2 = new Minion[MagicNumbers.ROW_LENGTH];
        backRow1 = new Minion[MagicNumbers.ROW_LENGTH];
        backRow2 = new Minion[MagicNumbers.ROW_LENGTH];
    }

    /**
     * Retrieves a card from a specified row and position.
     *
     * @param x the index of the row (0-3)
     * @param y the index of the position in the row (0-4)
     * @return the {@code Minion} at the specified position, or {@code null} if invalid
     */
    public Minion getCardFromRow(final int x, final int y) {
        Minion[] row;
        switch (x) {
            case MagicNumbers.BACK_ROW_1_ID:
                row = backRow1;
                break;
            case MagicNumbers.FRONT_ROW_1_ID:
                row = frontRow1;
                break;
            case MagicNumbers.FRONT_ROW_2_ID:
                row = frontRow2;
                break;
            case MagicNumbers.BACK_ROW_2_ID:
                row = backRow2;
                break;
            default:
                return null;
        }
        if (y < 0 || y >= row.length) {
            return null;
        }
        return row[y];
    }

    /**
     * Retrieves a row for a specific player and row index.
     *
     * @param playerIdx the index of the player (0 or 1)
     * @param rowIdx    the index of the row (0 for back, 1 for front)
     * @return the array representing the row, or {@code null} if invalid
     */
    public Minion[] getRowForPlayer(final int playerIdx, final int rowIdx) {
        switch (playerIdx) {
            case 1:
                switch (rowIdx) {
                    case MagicNumbers.BACK_ROW_1_ID:
                        return backRow1;
                    case MagicNumbers.FRONT_ROW_1_ID:
                        return frontRow1;
                    default:
                        return null;
                }

            case 0:
                switch (rowIdx) {
                    case MagicNumbers.FRONT_ROW_2_ID:
                        return frontRow2;
                    case MagicNumbers.BACK_ROW_2_ID:
                        return backRow2;
                    default:
                        return null;
                }

            default:
                return null;
        }
    }

    /**
     * Determines which player owns a given row.
     *
     * @param rowIdx the index of the row (0-3)
     * @return the player index (0 or 1)
     */
    public int getPlayerForRow(final int rowIdx) {
        if (rowIdx == 0 || rowIdx == 1) {
            return 1;
        } else {
            return 0;
        }
    }

    /**
     * Adds a card to a specified row for a player. Places the card in the first available slot.
     *
     * @param playerIdx the index of the player (0 or 1)
     * @param rowIdx    the index of the row (0 or 1)
     * @param card      the {@code Minion} to be added
     */
    public void addCardToRow(final int playerIdx, final int rowIdx, final Minion card) {
        Minion[] row = getRowForPlayer(playerIdx, rowIdx);

        if (row != null) {
            for (int i = 0; i < row.length; i++) {
                if (row[i] == null) {
                    row[i] = card;
                    return;
                }
            }
        }
    }

    /**
     * Removes a card from a specified row and shifts subsequent cards to the left.
     *
     * @param row   the array representing the row
     * @param index the index of the card to remove
     */
    public void removeCardFromRow(final Minion[] row, final int index) {
        if (index >= 0 && index < row.length) {
            for (int i = index; i < row.length - 1; i++) {
                row[i] = row[i + 1];
            }
            row[row.length - 1] = null;
        }
    }

    /**
     * Checks if a "Tank" type card exists on a specific player's row.
     *
     * @param enemyPlayerIdx the index of the enemy player (0 or 1)
     * @return {@code true} if a "Tank" exists; {@code false} otherwise
     */
    public boolean hasTankOnRow(final int enemyPlayerIdx) {
        Minion[] row = getRowForPlayer(enemyPlayerIdx, 2 - enemyPlayerIdx);
        if (row == null) {
            return false;
        }

        for (Minion card : row) {
            if (card != null && card.getTank()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if a row is full (contains no empty slots).
     *
     * @param rowIdx the index of the row (0-3)
     * @return {@code true} if the row is full; {@code false} otherwise
     */
    public boolean isRowFull(final int rowIdx) {
        Minion[] row = null;

        if (rowIdx == MagicNumbers.BACK_ROW_1_ID) {
            row = backRow1;
        } else if (rowIdx == MagicNumbers.FRONT_ROW_1_ID) {
            row = frontRow1;
        } else if (rowIdx == MagicNumbers.FRONT_ROW_2_ID) {
            row = frontRow2;
        } else if (rowIdx == MagicNumbers.BACK_ROW_2_ID) {
            row = backRow2;
        }
        for (Minion card : row) {
            if (card == null) {
                return false;
            }
        }
        return true;
    }

    /**
     * Resets the attack state of all cards on the board.
     */
    public void resetHasAttacked() {
        resetRowAttacked(frontRow1);
        resetRowAttacked(frontRow2);
        resetRowAttacked(backRow1);
        resetRowAttacked(backRow2);
    }

    /**
     * Unfreezes all cards for a specific player.
     *
     * @param playerIdx the index of the player (0 or 1)
     */
    public void unfreezeAll(final int playerIdx) {
        if (playerIdx == 0) {
            unfreezeRow(frontRow2);
            unfreezeRow(backRow2);
        } else if (playerIdx == 1) {
            unfreezeRow(frontRow1);
            unfreezeRow(backRow1);
        }
    }

    /**
     * Unfreezes all cards in a specific row.
     *
     * @param row the array representing the row of cards
     */
    private void unfreezeRow(final Minion[] row) {
        for (Minion card : row) {
            if (card != null) {
                card.setFrozen(false);
            }
        }
    }

    /**
     * Resets the attack state for all cards in a specific row.
     *
     * @param row the array representing the row of cards
     */
    private void resetRowAttacked(final Minion[] row) {
        for (Minion minion : row) {
            if (minion != null) {
                minion.setHasAttacked(false);
            }
        }
    }

    /**
     * Freezes all cards in a specific row.
     *
     * @param row the array representing the row of cards
     */
    public void freezeAllCards(final Minion[] row) {
        for (Minion card : row) {
            if (card != null) {
                card.setFrozen(true);
            }
        }
    }

    /**
     * Destroys the card with the highest health in a specific row.
     *
     * @param row the array representing the row of cards
     */
    public void destroyHighestHealthCard(final Minion[] row) {
        Minion maxHealthCard = null;
        int maxHealthIndex = -1;
        for (int i = 0; i < row.length; i++) {
            Minion card = row[i];
            if (card != null && (maxHealthCard == null
                    || card.getHealthPoints() > maxHealthCard.getHealthPoints())) {
                maxHealthCard = card;
                maxHealthIndex = i;
            }
        }
        if (maxHealthIndex != -1) {
            removeCardFromRow(row, maxHealthIndex);
        }
    }

    /**
     * Boosts the health of all cards in a specific row by 1.
     *
     * @param row the array representing the row of cards
     */
    public void boostHealth(final Minion[] row) {
        for (Minion card : row) {
            if (card != null) {
                card.setHealthPoints(card.getHealthPoints() + 1);
            }
        }
    }

    /**
     * Boosts the attack damage of all cards in a specific row by 1.
     *
     * @param row the array representing the row of cards
     */
    public void boostAttack(final Minion[] row) {
        for (Minion card : row) {
            if (card != null) {
                card.setAttackDamage(card.getAttackDamage() + 1);
            }
        }
    }

    /**
     * Retrieves the front row for Player 1.
     *
     * @return the array representing Player 1's front row
     */
    public Minion[] getFrontRow1() {
        return frontRow1;
    }

    /**
     * Retrieves the back row for Player 1.
     *
     * @return the array representing Player 1's back row
     */
    public Minion[] getBackRow1() {
        return backRow1;
    }

    /**
     * Retrieves the front row for Player 2.
     *
     * @return the array representing Player 2's front row
     */
    public Minion[] getFrontRow2() {
        return frontRow2;
    }

    /**
     * Retrieves the back row for Player 2.
     *
     * @return the array representing Player 2's back row
     */
    public Minion[] getBackRow2() {
        return backRow2;
    }
}
