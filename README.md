README - Tema0-OOP: "GwentStone Lite"
-

This is the readme that goes through the functionalities of each class and method used in implementing a card-based game simulator. This implementetion of the code is made out of three chapters:


Nr. 1, the Cards Pack
-
- This pack holds the contents of various placeable cards and units, as well as the Hero of each player and the static variable class, called MagicNumbers

I. Parent Class Cards
-
Overview:
- The Card class represents a card. Each card has several attributes such as a name, mana cost, attack damage, health points, description, and colors. This class is designed to encapsulate all the properties and behaviors associated with an individual card, making it a fundamental component for gameplay mechanics.

This class has the following parameters:
- name: holds the name of the card.
- manaCost: holds the mana value of the card.
- attackDamage: holds the attack value of the card.
- healthPoints: holds the health value of the card.
- description: holds a short description of the card.
- colors: holds a list of colors the card has.

Getters and Setters:
- Each attribute has its corresponding getter and setter method for retrieving or modifying the value.

Summary:
- This class is mostly filled with getters and setters, its only reason is to be used as parent class for other children classes, such as Minion and Hero.

II. Child Class Minion
-
Overview:
- The Minion class represents the placeable entity in the game. It extends the base Card class and introduces additional attributes and functionalities specific to minions. 

Attributes:
- attributes inherited from Card: name, manaCost, attackDamage, healthPoints, description, colors

Specialized parameters for the Minion Class:
- isFrozen: special attribute which checks if the minion is frozen. If the statement is true, the minion cannot act.
- isAttacked: indicates if the minion has been attacked during the current turn. Helps with logic in the Game handler.
- hasAttacked: indicates if the minion has attacked during the current turn. If the Minion has already attacked, he cannot perform other actions in some cases, attacked targets allied Minions and doesn't harm them).
- isTank: special attribute of some Minions. Minions with the Tank attribute must sit in the front row and be targeted first by enemy attacks of abilities.
- isDruid: special attribute of some Minions. Druid cards sit in the front line, but they don't need to be targeted specifically.

Getters and setters:
- makes the necessary modifiers to have the logic of the game set-up. used for checking frozen cards, and if they are attacked or attacking.

Special Abilities:
- weakKnees: reduces the target Minion's attack damage by 2. Available to Minions named "The Ripper".
- skyjack: swaps health points with the target Minion. If the resulting attack damage is 0, the target's health is set to 0.Available to Minions named "Miraj".
- shapeshift: swaps the attack damage and health points of the target Minion. If the attack damage in question is 0, the Minion's health is set to 0 and therefore die. Available to minions named "The Cursed One".
- godsPlan: Increases the health points of the target minion by 2. Available to minions named "Disciple".

Summary:
- in short, this class represents the actual "card" part of the game. Ensuring this class works well means that we will be able to get reliable information about a Minion's state, abilities, and characteristics.

III. Child Class Hero
-

Overview:
- the Hero class represents the, well, hero, who acts as a target to the enemy player. This class holds certain attributes and functionalities specific to Hero.

Attributes:
- this class inherited all the characteristics the Minion class inherited from the parent class Card, which are all of them.

Specialized parameters to the Hero Class:
- ability: an int code in which a hero's special ability is stored.
- hasAttacked: indicates if the Hero has attacked already in the current turn. Helps with stopping the Player attacking multiple times (in some cases, attacked targets allied Minions and doesn't harm them).

Heroes:
- "Lord Royce": offensive hero. Has the ability named "Sub-Zero", which freezes the designated enemy row.
- "Empress Thorina": offensive hero. Has the ability named "Low Blow", which instantly kills the highest-health Minion from the board.
- "King Mudface": defensive hero. Has the ability named "Earth Born", which increases the health value (of all allied Minions from the designated row) by 1.
- "General Kociraw": defensive hero. Has the ability named "Blood Thirst", which increases the attack value (of all allied Minions from the designated row) by 1.

Summary:
- the Child Class Hero is essentially the glorified health bar of the Player. They also have special powerful abilities that can change the course of the game. And, if the Hero's health drops to 0, the Hero dies.

IV. Static Class MagicNumbers
-

Summary:
- this class holds only the static values of some variables that help with errors . It holds nothing special.


Nr 2, the GameMechanics Pack
-
- This pack holds the logic of the Game, its Errors, and the Board.


I. Class Board
-
Overview:
- This class holds the Board info. It holds information about the cards, card order, as well as methods to use the board, such as adding, removing, checking if rows are full, etc.

Parameters:
- this class has only 4 arrays, the rows. This creates the Board. Each specific row has an index.


Methods used:
- getCardFromRow: returns the row based on index.
- getRowForPlayer: returns the row based on player index and the row index.
- getRowForPlayer: returns the owner player of the row based on index.
- addCardToRow: adds the card to the targeted row.
- removeCardFromRow: removes the target card from the target row.
- hasTankOnRow: verifies if the row has a Minion which holds the Tank tag. Helps with logic inside the Game Handler.
- isRowFull: checks if the target row is full.
- resetHasAttacked: resets all Minions with hasAttacked tag on all rows. Helps when it comes to new turn logic.
- unfreezeAll: resets all Minions with isFrozen tag on all rows. Helps when it comes to new turn logic.
- unfreezeRow: resets all Minions with isFrozen tag on the target row.
- resetRowAttacked: resets all Minions with hasAttacked tag on target row.
- freezeAllCards: freezes all cards from the target row. Is the code logic behind the "Sub-Zero" ability.
- destroyHighestHealthCard: destroys the highest health value Minion from the target row. Is the code logic behind the "Low Blow" ability.
- boostHealth: adds 1 to the health value of all Minions from the target row. Is the code logic behind the "Earth Born" ability.
- boostAttack: adds 1 to the attack value of all Minions from the target row. Is the code logic behind the "Blood Thirst" ability.

Summary:
- this Class is the board of where the game will happen. Players will attack each other's Minions and Heroes, and the Board will make the specific calculations for the whole adding/removing/ability influencing logic to happen.


II. Class Player
-
Overview:
- This class holds the entire information about the player, from its held mana to its hand, hero and games won.

Parameters:
- mana: this variable holds its total mana.
- deck: this variable holds the player's assigned deck at the start of the game.
- hero: this variable is the player's assigned hero at the start of the game.
- hand: holds the player's current cards drawn from the deck.
- gamesWon; holds the total games won since the test started.

Methods used:
- addMana: adds mana to the player. used at start of rounds.
- useMana: deducts mana from the player based on the int cost.
- resetPlayer: resets the player at the start of the game, preparing him for the next one.
- setDeck: gives the player its assigned deck, as well as shuffling it using a designated seed.
- setHero: assigns the hero to the player.
- printDeck: prints the deck held my the player. Used in test outputs.
- getHero: prints the hero held by the player. Used in test outputs.
- getHeroNormal: returns the hero info. Used in calculations in the Game handler.
- addCardToHand: draws the card and puts it in the player's hand.
- removeCardFromHand: removes the target card from the player's hand. Used when it comes to placing cards.
- drawCard: removes the top card from the deck.

Summary:
- This Class helps with the actions the player takes throughout the game, as well as game logic based operations such as drawing and removing cards from one's hand.


III. ErrorHandler
-
Summary:
- This Class helps sort out Errors inside the Game handler.


Nr. 3, the Big Game Class
-

- this Class holds the info about all game logistics, and is behind all commands given by the server. it gets its own chapter because its, well, the biggest cog in the machine.

Parameters:
- player: holds both players in an array of two.
- currentPlayer: an index representing the current player.
- board: the board of the game.
- round: the current round of the game.
- turn: the current turn of the game. a round can have two turns, one for each player.
- totalGames: used to count how many games have been played.

Methods used:
- startGame: starts the actual game. This means resetting both players and rounds/turns, giving them new decks and heross, and assigning the new starting player. Starts the first round.
- playGame: a big switch case which scans every command that the server could possibily send.
- getPlayerDeck: prints the target player's deck info.
- getPlayerHero: prints the target player's hero info.
- getPlayerTurn: prints the target player's turn.
- getCardsInHand: prints the held hand of the target player.
- getPlayerMana: prints the current mana of the target player.
- getCardsOnTable: Prints the cards on the board. This is done in the following order: back row 1 -> front row 1 -> front row 2 -> back row 2.
- placeCard: places the target card from the player's hand on the designated row.
- getCardAtPosition: prints the card info at the target location.
- attackCard: executes a Minion-to-Minion attack between the target attacker Minion and the target attacked Minion.
- cardUseAbility: executes a Minion-to-Minion ability use between the target attacker Minion and its targeted Minion.
- useAttackHero: executes a Minion-to-Hero attack between the attacker Minion and the attacked Hero.
- useHeroAbility: executes a Hero ability on the target row.
- getFrozenCardsOnTable: prints all cards from the board that are frozen.
- getTotalGamesPlayed: prints the games played in the test.
- getPlayerOneWins: prints the total wins of the assigned "player 1".
- getPLayerTwoWIns: prints the total wins of the assigned "player 2".
- startRound: does the logic necessary to start the round. increases round, resets all hasAttacked tags, and draws each Player a new card from their respective decks.
- endPlayerTurn: increases turn and sets all isFrozen tags from the current player.
- resetGame: resets the current game, preparing it for the next one.
