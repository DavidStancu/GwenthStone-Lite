package org.poo.main.Cards;

import java.util.List;
import java.util.Objects;

/**
 * Represents a minion card in a card-based game. Minions extend the {@link Card} class
 * and include additional attributes and abilities unique to minion cards.
 * This class provides functionality for specific minion abilities,
 * as well as states like being frozen, attacked, or tank/druid classification.
 */
public class Minion extends Card {
    private boolean isFrozen;
    private boolean isAttacked;
    private boolean hasAttacked;
    private boolean isTank;
    private boolean isDruid;

    /**
     * Constructs a {@code Minion} instance with the specified attributes.
     * Certain attributes like tank or druid status are determined based on the minion's name.
     *
     * @param name         the name of the minion
     * @param manaCost     the mana cost required to use the minion
     * @param attackDamage the attack damage of the minion
     * @param healthPoints the health points of the minion
     * @param description  the description or lore of the minion
     * @param colors       a list of colors associated with the minion
     */
    public Minion(final String name, final int manaCost, final int attackDamage,
                  final int healthPoints, final String description, final List<String> colors) {
        super(name, manaCost, attackDamage, healthPoints, description, colors);
        isFrozen = false;
        isAttacked = false;
        hasAttacked = false;

        isTank = name.equals("Goliath") || name.equals("Warden");
        isDruid = name.equals("The Ripper") || name.equals("Miraj");
    }

    /**
     * Reduces the attack damage of the target minion by 2.
     * This ability is only available to minions named "The Ripper."
     *
     * @param target the target minion whose attack damage is to be reduced
     */
    public void weakKnees(final Minion target) {
        if (Objects.equals(this.getName(), "The Ripper")) {
            int newAttack = Math.max(0, target.getAttackDamage() - 2);
            target.setAttackDamage(newAttack);
        }
    }

    /**
     * Swaps health points between this minion and the target minion.
     * This ability is only available to minions named "Miraj."
     *
     * @param target the target minion to swap health with
     */
    public void skyjack(final Minion target) {
        if (Objects.equals(this.getName(), "Miraj")) {
            int tempHealth = this.getHealthPoints();
            this.setHealthPoints(target.getHealthPoints());
            target.setHealthPoints(tempHealth);
        }
    }

    /**
     * Swaps the attack damage and health points of the target minion.
     * If the resulting attack damage is 0, the target's health points are set to 0.
     * This ability is only available to minions named "The Cursed One."
     *
     * @param target the target minion to transform
     */
    public void shapeshift(final Minion target) {
        if (Objects.equals(this.getName(), "The Cursed One")) {
            int tempAttack = target.getAttackDamage();
            target.setAttackDamage(target.getHealthPoints());
            target.setHealthPoints(tempAttack);

            if (target.getAttackDamage() == 0) {
                target.setHealthPoints(0);
            }
        }
    }

    /**
     * Increases the health points of the target minion by 2.
     * This ability is only available to minions named "Disciple."
     *
     * @param target the target minion to heal
     */
    public void godsPlan(final Minion target) {
        if (Objects.equals(this.getName(), "Disciple")) {
            target.setHealthPoints(target.getHealthPoints() + 2);
        }
    }

    /**
     * Sets the frozen state of this minion.
     *
     * @param newFrozenState {@code true} if the minion should be frozen; {@code false} otherwise
     */
    public void setFrozen(final boolean newFrozenState) {
        isFrozen = newFrozenState;
    }

    /**
     * Checks if this minion is frozen.
     *
     * @return {@code true} if the minion is frozen; {@code false} otherwise
     */
    public boolean isFrozen() {
        return isFrozen;
    }

    /**
     * Checks if this minion has been attacked.
     *
     * @return {@code true} if the minion has been attacked; {@code false} otherwise
     */
    public boolean isAttacked() {
        return isAttacked;
    }

    /**
     * Sets the attacked state of this minion.
     *
     * @param isMinionAttacked {@code true} if the minion has been attacked; {@code false} otherwise
     */
    public void setAttacked(final boolean isMinionAttacked) {
        isAttacked = isMinionAttacked;
    }

    /**
     * Checks if this minion has attacked during the current turn.
     *
     * @return {@code true} if the minion has attacked; {@code false} otherwise
     */
    public boolean hasAttacked() {
        return hasAttacked;
    }

    /**
     * Sets whether this minion has attacked during the current turn.
     *
     * @param hasAttacked {@code true} if the minion has attacked; {@code false} otherwise
     */
    public void setHasAttacked(final boolean hasAttacked) {
        this.hasAttacked = hasAttacked;
    }

    /**
     * Checks if this minion is a tank.
     *
     * @return {@code true} if the minion is a tank; {@code false} otherwise
     */
    public boolean getTank() {
        return isTank;
    }

    /**
     * Checks if this minion is a druid.
     *
     * @return {@code true} if the minion is a druid; {@code false} otherwise
     */
    public boolean getDruid() {
        return isDruid;
    }

}
