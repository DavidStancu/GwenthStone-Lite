package org.poo.main.Cards;

import org.poo.main.GameMechanics.Board;

import java.util.List;

/**
 * Represents a hero card in a card-based game. Heroes extend the {@link Card} class
 * and possess unique abilities that can impact the board state or other cards.
 * This class includes functionality for setting and using hero abilities, as well as
 * tracking whether the hero has attacked during a turn.
 */
public class Hero extends Card {
    private int ability;
    private boolean hasAttacked;

    /**
     * Constructs a {@code Hero} instance with the specified attributes.
     * The hero's ability is determined based on their name.
     *
     * @param name         the name of the hero
     * @param manaCost     the mana cost required to use the hero
     * @param healthPoints the health points of the hero
     * @param description  the description or lore of the hero
     * @param colors       a list of colors associated with the hero
     */
    public Hero(final String name, final int manaCost, final int healthPoints,
                final String description, final List<String> colors) {
        super(name, manaCost, 0, healthPoints, description, colors);
        setAbility(name);
        hasAttacked = false;
    }

    /**
     * Assigns an ability to the hero based on their name.
     *
     * @param name the name of the hero
     */
    private void setAbility(final String name) {
        switch (name) {
            case "Lord Royce":
                this.ability = MagicNumbers.LORD_ROYCE_ID;
                break;
            case "Empress Thorina":
                this.ability = MagicNumbers.EMP_THORINA_ID;
                break;
            case "King Mudface":
                this.ability = MagicNumbers.KING_MUDFACE_ID;
                break;
            case "General Kocioraw":
                this.ability = MagicNumbers.GEN_KOCIRAW_ID;
                break;
            default:
                break;
        }
    }

    /**
     * Uses the hero's ability on a specific row of minions.
     * The effect of the ability depends on the hero's type:
     * <ul>
     *     <li>Lord Royce freezes all cards in the row.</li>
     *     <li>Empress Thorina destroys the card with the highest health in the row.</li>
     *     <li>King Mudface boosts the health of all cards in the row.</li>
     *     <li>General Kocioraw boosts the attack of all cards in the row.</li>
     * </ul>
     *
     * @param board       the board where the ability is applied
     * @param affectedRow the row of minions affected by the ability
     */
    public void useAbility(final Board board, final Minion[] affectedRow) {
        switch (ability) {
            case MagicNumbers.LORD_ROYCE_ID:
                board.freezeAllCards(affectedRow);
                break;
            case MagicNumbers.EMP_THORINA_ID:
                board.destroyHighestHealthCard(affectedRow);
                break;
            case MagicNumbers.KING_MUDFACE_ID:
                board.boostHealth(affectedRow);
                break;
            case MagicNumbers.GEN_KOCIRAW_ID:
                board.boostAttack(affectedRow);
                break;
            default:
                break;
        }
    }

    /**
     * Checks if the hero has attacked during the current turn.
     *
     * @return {@code true} if the hero has attacked; {@code false} otherwise
     */
    public boolean hasAttacked() {
        return hasAttacked;
    }

    /**
     * Sets whether the hero has attacked during the current turn.
     *
     * @param hasAttacked {@code true} if the hero has attacked; {@code false} otherwise
     */
    public void setHasAttacked(final boolean hasAttacked) {
        this.hasAttacked = hasAttacked;
    }
}
