import java.util.Arrays;
import java.util.Random;

/**
 * Class responsible for handling battles.
 */
public class AttackEngine {
    
	
        private static int oneDiceThrow(){
            Random ran = new Random();
            int result = ran.nextInt(6) + 1;
            return result;
        }
        
        private static int[] multipleDiceThrow(int numOfDice){
            int[] result = new int[numOfDice];
            for(int i = 0; i < numOfDice; i++){
                result[i] = oneDiceThrow();
            }
            return result;
        }
    

        /**
         * The method checks whether the given attack is valid and 
         * returns false if it's not valid. 
         *  
         * @param player
         * @param attacker
         * @param defender
         * @return
         */
        private static boolean checkAttack(Player player, Territory attacker, Territory defender){
            if(!player.territories.contains(attacker)){
            	System.out.println("\nYOU DON'T OWN THIS TERRITORY!\n");
            	return false;
            }
            else if (!attacker.neighbours.contains(defender)){
            	System.out.println("\nTHIS IS NOT YOUR NEIGHBOUR.\n");
            	return false;
            }
            else if (attacker.armies.amount <= 1){
            	System.out.println("\nYOU DONT HAVE ENOUGHT ARMIES.\n");
                return false;
            }
            if(player.territories.contains(defender)){
            	System.out.println("\nYOU CANNOT ATTACK YOUR OWN TERRITORY.\n");
            	return false;
            }
            else
                return true;
        }
        
    	
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
         * Checks whether the number of dice to be thrown specified by a player
         * is a valid number. The method differenciates between attackers choice
         * and defenders choice to apply appropriate rules.
         * 
         * @param territory
         * @param numOfDice
         * @param attacker
         * @return
         */
        private static boolean checkNumberOfDice(Territory territory, int numOfDice, boolean attacker){            
            boolean result;
        	if(numOfDice == 0)
                result = false;
            else if(attacker && ((territory.armies.amount <= numOfDice) || (numOfDice > 3)))
                result = false;
            else if(!attacker && ((territory.armies.amount < numOfDice) || (numOfDice > 2)))
                result = false;
            else
                result = true;
        	
        	if(result == false)
        		System.out.println("\nYOU SPECIFIED AND INVALID NUMBER OF DICE\n");
        	
        	return result;
        }
        
        /**
         * The method takes the result of dice throws and compares then
         * to specify how many armies should be taken of each player. 
         * The returned value is an array of ints where i[0] is an
         * amount taken of attacker and i[1] is an amount taken of
         * defender
         * 
         * @param attacker
         * @param defender
         * @return
         */
        private static int[] compareDice(int[] attacker, int[] defender){
            Arrays.sort(attacker);
            Arrays.sort(defender);
            
            System.out.print("Attackers dice: ");
            for(int i = 0; i < attacker.length; i++){
                System.out.print(attacker[i]);
            }
            
            System.out.print("\nDefenders dice: ");
            for(int i = 0; i < defender.length; i++){
                System.out.print(defender[i]);
            }
            System.out.println();
           
            // the amount compared is the smaller number of dice
            // that were thrown
            int numOfCompared = (attacker.length > defender.length) 
                    ? defender.length : attacker.length;
            
            int[] unitsTakenOfPlayers = {0, 0};
            for(int i = 1; i <= numOfCompared; i++){
                if(attacker[attacker.length - i] > defender[defender.length - i])
                    unitsTakenOfPlayers[1]++;
                else
                	// the defender wins when their dice result was greater
                	// or when there was a draw
                    unitsTakenOfPlayers[0]++;
            }
            
            return unitsTakenOfPlayers;
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
         * @param player
         * @param attacker
         * @param defender
         * @return
         */
        static boolean attack(Territory attacker, Territory defender){
            
            if(!checkAttack(GameState.players.get(GameState.currentPlayer), 
            		attacker, defender)){
                return false;
            }
            
            // Players specify how many dice do they want to throw
            int attackerDiceNum;
            do{
            	System.out.println("\nAttacker, " + attacker.player.id);
                attackerDiceNum = UIEngine.getNumOfDice(attacker.player);
            } while(!checkNumberOfDice(attacker, attackerDiceNum, true));
            
            int defenderDiceNum;
            do{
            	System.out.println("\nDefender, " + defender.player.id);
                defenderDiceNum = UIEngine.getNumOfDice(defender.player);
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
            		armies = UIEngine.getNumOfArmies(attacker.player);
                } while(!checkNumOfMovedArmies(armies, attackerDiceNum, attacker));
                
            	// moving attackers armies to its new territory
                GameEngine.moveArmy(armies, attacker, defender);
                
               	GameState.checkWin(attacker.player);
                return true;
            }
            else
                return true;
        }

}
