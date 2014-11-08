import java.util.Arrays;
import java.util.Random;


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
    

        
        //check adjacent, check 2 armies 
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
        
    	
        static boolean checkNumOfMovedArmies(int numOfArmies, int numOfDice, Territory territory){
            if((numOfArmies < numOfDice) || (territory.armies.amount - 1 < numOfArmies)){
            	System.out.println("\nYOU CANNOT MOVE THIS NUMBER OF ARMIES.\n");
            	return false;
            }
            return true;
        }
        
        // might think about removing boolean field
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
        
        // returns the array of 2 ints which gives the amount taken of each player
        private static int[] compareDice(int[] attacker, int[] defender){
            Arrays.sort(attacker);
            Arrays.sort(defender);
            
            System.out.println("\nComparing dice throws:");
            System.out.print("Attacker: ");
            for(int i = 0; i < attacker.length; i++){
                System.out.print(attacker[i]);
            }
            System.out.print("\nDefender: ");
            for(int i = 0; i < defender.length; i++){
                System.out.print(defender[i]);
            }
            System.out.println();
            
            int numOfCompared = (attacker.length > defender.length) 
                    ? defender.length : attacker.length;
            
            // 1st int is number of units taken of attacker
            // 2nd is the number taken of defender
            int[] unitsTakenOfPlayers = {0, 0};
            
            for(int i = 1; i <= numOfCompared; i++){
                if(attacker[attacker.length - i] > defender[defender.length - i])
                    unitsTakenOfPlayers[1]++;
                else
                    unitsTakenOfPlayers[0]++;
            }
            
            return unitsTakenOfPlayers;
        }
        
        
        // called within attack
        private static void removeArmies(int numOfArmies, Territory territory){
            territory.armies.amount = territory.armies.amount - numOfArmies; 
            if(territory.armies.amount == 0){
            	GameEngine.unassignTerritory(territory.player, territory);
            }
        }
        
        // called within attack 
        // set giveCard to true (in takeTurn method)
        // checks for the end of the game?
        private static boolean checkDefeated(Territory defender){
            if(defender.armies.amount == 0){
            	return true;
            }
            else 
                return false;
            // THE END OF GAME?
        }
        
        // check whether this player should get a card first
       // abstract void giveCard(Player player);
        
        
        
        // throwing dice, conducting attack, returning result 
        // needs to save number of dice thrown - for later moving units
        static boolean attack(Player player, Territory attacker, Territory defender){
            
            if(!checkAttack(player, attacker, defender)){
                return false;
                // do sth else?
            }
            
            int attackerDiceNum;
            do{
            	System.out.println("\nAttacker, " + attacker.player.id);
                attackerDiceNum = UIEngine.getNumOfDice(true);
            } while(!checkNumberOfDice(attacker, attackerDiceNum, true));
            
            int defenderDiceNum;
            do{
            	System.out.println("\nDefender, " + defender.player.id);
                defenderDiceNum = UIEngine.getNumOfDice(false);
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
            	GameEngine.assignTerritory(attacker.player, defender);
                do{
                    armies = UIEngine.getNumOfArmies();
                } while(!checkNumOfMovedArmies(armies, attackerDiceNum, attacker));

                GameEngine.moveArmy(armies, attacker, defender);
               	GameState.checkWin(attacker.player);
                return true;
            }
            else
                return true;
        }

}
