package org.poo.main.Cards;

import java.util.List;

/**
 * Represents a card with various attributes such as name, mana cost, attack damage,
 * health points, description, and colors. This class is used in card-based games to
 * encapsulate the properties of a single card.
 */
public class Card {
    private String name;
    private int manaCost;
    private int attackDamage;
    private int healthPoints;
    private String description;
    private List<String> colors;

    /**
     * Constructs a new {@code Card} instance with the specified attributes.
     *
     * @param name         the name of the card
     * @param manaCost     the mana cost required to use the card
     * @param attackDamage the attack damage value of the card
     * @param healthPoints the health points of the card
     * @param description  the description or lore of the card
     * @param colors       a list of colors associated with the card
     */
    public Card(final String name, final int manaCost, final int attackDamage,
                final int healthPoints, final String description, final List<String> colors) {
        this.name = name;
        this.manaCost = manaCost;
        this.attackDamage = attackDamage;
        this.healthPoints = healthPoints;
        this.description = description;
        this.colors = colors;
    }

    /**
     * Gets the name of the card.
     *
     * @return the name of the card
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the card.
     *
     * @param name the new name of the card
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Gets the mana cost of the card.
     *
     * @return the mana cost of the card
     */
    public int getManaCost() {
        return manaCost;
    }

    /**
     * Sets the mana cost of the card.
     *
     * @param manaCost the new mana cost of the card
     */
    public void setManaCost(final int manaCost) {
        this.manaCost = manaCost;
    }

    /**
     * Gets the attack damage value of the card.
     *
     * @return the attack damage value of the card
     */
    public int getAttackDamage() {
        return attackDamage;
    }

    /**
     * Sets the attack damage value of the card.
     *
     * @param attackDamage the new attack damage value of the card
     */
    public void setAttackDamage(final int attackDamage) {
        this.attackDamage = attackDamage;
    }

    /**
     * Gets the health points of the card.
     *
     * @return the health points of the card
     */
    public int getHealthPoints() {
        return healthPoints;
    }

    /**
     * Sets the health points of the card.
     *
     * @param healthPoints the new health points of the card
     */
    public void setHealthPoints(final int healthPoints) {
        this.healthPoints = healthPoints;
    }

    /**
     * Gets the description or lore of the card.
     *
     * @return the description of the card
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description or lore of the card.
     *
     * @param description the new description of the card
     */
    public void setDescription(final String description) {
        this.description = description;
    }

    /**
     * Gets the list of colors associated with the card.
     *
     * @return the list of colors associated with the card
     */
    public List<String> getColors() {
        return colors;
    }

    /**
     * Sets the list of colors associated with the card.
     *
     * @param colors the new list of colors associated with the card
     */
    public void setColors(final List<String> colors) {
        this.colors = colors;
    }
}
