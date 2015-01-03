package GameEngine;

import GameState.*;
import PlayerInput.TextInterface;

/**
 * Class responsible for handling battles.
 */
public class AttackEngine {
    


    	
        /**
         * The method used after a territory was taken and the attacker
         * decided how many armies they want to move to the newly
         * obtained territory. It checks whether they specified
         * a sufficient number of armies to move and not more
         * than they actually have. 
         * 
         * @param numOfArmies
         * @param numOfDice
         * @param territory
         * @return
         */
        static boolean checkNumOfMovedArmies(int numOfArmies, int numOfDice, Territory territory){
            if((numOfArmies < numOfDice) || (territory.armies.amount - 1 < numOfArmies)){
            	System.out.println("\nYOU CANNOT MOVE THIS NUMBER OF ARMIES.\n");
            	return false;
            }
            return true;
        }
        
        /**
         * Removes the given amount of armies from a given territory.
         * The method additionally checks whether after removing the 
         * territory has some armies left. If it doesn't the territory
         * is unassigned - a player looses it.
         * 
         * @param numOfArmies
         * @param territory
         */
        private static void removeArmies(int numOfArmies, Territory territory){
            territory.armies.amount = territory.armies.amount - numOfArmies; 
            if(territory.armies.amount == 0){
            	GameEngine.unassignTerritory(territory.player, territory);
            }
        }
        
        
        
        /**
         * Called at the end of each attack. Checks whether
         * the given territory was defeated.
         * 
         * @param defender
         * @return
         */
        private static boolean checkDefeated(Territory defender){
            if(defender.player == null){
                // set giveCard to true (in takeTurn method)
            	return true;
            }
            else 
                return false;
        }
        
        
        // check whether this player should get a card first
        private void giveCard(Player player){}
        
        
        
        /**
         * Method called each time the player decides to attack or is attacker.
         * Responsible for checking the validity of the attack, 
         * throwing dice, conducting the attack and removing the appropriate
         * amount of armies.
         * 
         * @param attacker
         * @param defender
         * @return
         */
        static boolean attack(Territory attacker, Territory defender){
            
            if(!checkAttack(State.players.get(State.currentPlayer),
            		attacker, defender)){
                return false;
            }
            
            // Players specify how many dice do they want to throw
            int attackerDiceNum;
            do{
            	System.out.println("\nAttacker, " + attacker.player.id);
                attackerDiceNum = TextInterface.getNumOfDice(attacker.player);
            } while(!checkNumberOfDice(attacker, attackerDiceNum, true));
            
            int defenderDiceNum;
            do{
            	System.out.println("\nDefender, " + defender.player.id);
                defenderDiceNum = TextInterface.getNumOfDice(defender.player);
            } while(!checkNumberOfDice(defender, defenderDiceNum,  false));
            
            
            int[] attackerResult = multipleDiceThrow(attackerDiceNum);
            int[] defenderResult = multipleDiceThrow(defenderDiceNum);
            
            int[] armiesToRemove = compareDice(attackerResult, defenderResult);
            
            System.out.println("\nDefender won: " + armiesToRemove[0] + " times");
            System.out.println("Attacker won: " + armiesToRemove[1] + " times\n");
            
            removeArmies(armiesToRemove[0], attacker);
            removeArmies(armiesToRemove[1], defender);
            
            if(checkDefeated(defender)){
                int armies;        
                System.out.println("\n" + attacker.player.id + " won a territory!");
                
                // adding the defenders territory to attackers territory
            	GameEngine.assignTerritory(attacker.player, defender);
              
            	// the attacker needs to move their armies
            	do{
            		armies = TextInterface.getNumOfArmies(attacker.player);
                } while(!checkNumOfMovedArmies(armies, attackerDiceNum, attacker));
                
            	// moving attackers armies to its new territory
                GameEngine.moveArmy(armies, attacker, defender);
                
               	State.checkWin(attacker.player);
                return true;
            }
            else
                return true;
        }

}
