package org.poo.main.GameMechanics;

import org.poo.main.Cards.*;
import java.util.*;
import org.poo.fileio.*;
import java.util.ArrayList;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import static java.util.Collections.shuffle;

/**
 * Represents a player in the game, including their mana, deck, hero, hand, and rows of cards.
 * Provides methods for managing the player's deck, hand, and game state.
 */
public class Player {
    private int mana;
    private ArrayList<Minion> deck;
    private Hero hero;
    private ArrayList<Minion> hand;
    private int gamesWon;
    private ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Constructs a new player with default values for mana, deck, hand, front row, and back row.
     */
    public Player() {
        mana = 0;
        deck = new ArrayList<>();
        hand = new ArrayList<>();
        gamesWon = 0;
    }

    /**
     * Adds a specified amount of mana to the player's current mana.
     *
     * @param amount The amount of mana to add.
     */
    public void addMana(final int amount) {
        mana += amount;
    }

    /**
     * Uses a specified amount of mana, if the player has enough mana.
     *
     * @param cost The cost of the mana to use.
     * @return true if mana was successfully used, false otherwise.
     */
    public boolean useMana(final int cost) {
        if (mana >= cost) {
            mana -= cost;
            return true;
        }
        return false;
    }

    /**
     * Resets the player's state, clearing mana, deck, and hand.
     */
    public void resetPlayer() {
        mana = 0;
        deck.clear();
        hand.clear();
    }

    /**
     * Sets the player's deck based on the specified deck index and shuffle seed.
     *
     * @param decksInput The input containing available decks.
     * @param deckIdx The index of the deck to set.
     * @param shuffleSeed The seed to use for shuffling the deck.
     */
    public void setDeck(final DecksInput decksInput, final int deckIdx, final long shuffleSeed) {
        if (deckIdx >= 0 && deckIdx < decksInput.getDecks().size()) {
            ArrayList<Minion> selectedDeck = new ArrayList<>();
            List<CardInput> cardsInput = decksInput.getDecks().get(deckIdx);

            for (CardInput cardInput : cardsInput) {
                Minion card = new Minion(cardInput.getName(), cardInput.getMana(),
                        cardInput.getAttackDamage(), cardInput.getHealth(),
                        cardInput.getDescription(), cardInput.getColors());
                selectedDeck.add(card);
            }
            Random random = new Random(shuffleSeed);
            shuffle(selectedDeck, random);
            deck = selectedDeck;
        }
    }

    /**
     * Sets the player's hero based on the provided hero input.
     *
     * @param heroInput The input containing hero information.
     */
    public void setHero(final CardInput heroInput) {
        this.hero = new Hero(heroInput.getName(), heroInput.getMana(),
                MagicNumbers.STARTING_HELO_HP, heroInput.getDescription(),
                heroInput.getColors());
    }

    /**
     * Prints the player's deck as a JSON array.
     *
     * @return A JSON array representing the player's deck.
     */
    public ArrayNode printDeck() {
        ArrayNode arrayNode = objectMapper.createArrayNode();
        for (Card card : deck) {
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

            arrayNode.add(cardNode);
        }
        return arrayNode;
    }

    /**
     * Gets the player's hero as a JSON object.
     *
     * @return A JSON object representing the player's hero.
     */
    public ObjectNode getHero() {
        ObjectNode heroNode = objectMapper.createObjectNode();

        heroNode.put("mana", hero.getManaCost());
        heroNode.put("health", hero.getHealthPoints());
        heroNode.put("description", hero.getDescription());

        ArrayNode colorsArray = objectMapper.createArrayNode();
        for (String color : hero.getColors()) {
            colorsArray.add(color);
        }
        heroNode.set("colors", colorsArray);
        heroNode.put("name", hero.getName());

        return heroNode;
    }

    /**
     * Gets the player's hero as a Hero object.
     *
     * @return The player's hero.
     */
    public Hero getHeroNormal() {
        return hero;
    }

    /**
     * Adds a specified card to the player's hand.
     *
     * @param card The card to add to the hand.
     */
    public void addCardToHand(final Minion card) {
        hand.add(card);
    }

    /**
     * Removes a card from the player's hand by its index.
     *
     * @param index The index of the card to remove.
     */
    public void removeCardFromHand(final int index) {
        if (index >= 0 && index < hand.size()) {
            hand.remove(index);
        }
    }

    /**
     * Draws a card from the player's deck.
     *
     * @return The drawn card, or null if the deck is empty.
     */
    public Minion drawCard() {
        if (!deck.isEmpty()) {
            return deck.remove(0);
        }
        return null;
    }

    /**
     * Gets the player's hand of cards.
     *
     * @return The player's hand.
     */
    public ArrayList<Minion> getHand() {
        return hand;
    }

    /**
     * Gets the player's current mana.
     *
     * @return The current mana of the player.
     */
    public int getMana() {
        return this.mana;
    }

    /**
     * Sets the player's mana to a specified value.
     *
     * @param mana The new mana value.
     */
    public void setMana(final int mana) {
        this.mana = mana;
    }

    /**
     * Gets a card from the player's hand by its index.
     *
     * @param handIdx The index of the card in the hand.
     * @return The card at the specified index, or null if the index is invalid.
     */
    public Minion getCardFromHand(final int handIdx) {
        if (handIdx >= 0 && handIdx < hand.size()) {
            return hand.get(handIdx);
        }
        return null;
    }

    /**
     * Sets the number of games the player has won.
     *
     * @param gamesWon The number of games the player has won.
     */
    public void setGamesWon(final int gamesWon) {
        this.gamesWon = gamesWon;
    }

    /**
     * Gets the number of games the player has won.
     *
     * @return The number of games the player has won.
     */
    public int getGamesWon() {
        return gamesWon;
    }
}
