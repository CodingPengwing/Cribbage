# Cribbage #
This is a solution to project 2 of SWEN30006-Software modelling and Design, the 
University of Melbourne. The specifications are provided in the repo.

This is a Cribbage trainer! Cribbage is a turn-based card game, usually for 2 players,
where you try to play and group combinations of cards so that you get more points than
the other player. Here's the [link](https://en.wikipedia.org/wiki/Cribbage) to read up 
about what Cribbage actually is...

This program accommodates a full game of Cribbage, given 2 players (human or computer). 
The game also logs all the moves and scores at every step, and calculates a total at the
end for each player. There is a UI that pops up to show how the game is progressing and
allow human players to interact. Since the game is mostly for training, players will
get to see each other's cards, however, future implementation may hide other players
cards to simulate a serious game of Cribbage.

Run instructions:  
- IntelliJ: The repository has been set up for running in IntelliJ, the user can simply 
clone the repo and build/run. 
- Other environments: The src, sprites folders and cribbage.properties file may need to
be extracted to run in other environments.  

Modify settings:  
Settings can be safely changed through the cribbage.properties file. Examples of 
possible settings have been left commented out. Main things to note are:  
- Players: HumanPlayer/RandomPlayer
- Animate: animations turned on/off for moving cards in the UI. Turning on will animate
the motion of delivering a card from a hand to a pile, turning off will run the game
faster.
- Scoring: can be changed at the bottom of the file to try out different variations
of scoring rules.
