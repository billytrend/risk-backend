import java.util.Arrays;
import java.util.Random;


public abstract class AttackEngine {
    
        private int oneDiceThrow(){
            Random ran = new Random();
            return ran.nextInt(6);
        }
        
        private int[] multipleDiceThrow(int numOfDice){
            int[] result = new int[numOfDice];
            for(int i = 0; i < numOfDice; i++){
                result[0] = oneDiceThrow();
            }
            return result;
        }
    
        private boolean checkNumOfMovedArmies(int numOfArmies, int numOfDice, Territory territory){
            if((numOfArmies < numOfDice) || (territory.armies.amount - 1 < numOfArmies))
                return false;
            return true;
        }
        
        //check adjacent, check 2 armies 
        private boolean checkAttack(Player player, Territory attacker, Territory defender){
            if((!player.territories.contains(attacker)) || 
                    (!attacker.neighbours.contains(defender))
                    || (attacker.armies.amount <= 1))
                return false;
            else
                return true;
        }
        
        // might think about removing boolean field
        private boolean checkNumberOfDice(Territory territory, int numOfDice, boolean attacker){            
            if(numOfDice == 0)
                return false;
            else if(attacker && ((territory.armies.amount <= numOfDice) || (numOfDice > 3)))
                return false;
            else if(!attacker && ((territory.armies.amount < numOfDice) || (numOfDice > 2)))
                return false;
            else
                return true;
        }
        
        // returns the array of 2 ints which gives the amount taken of each player
        private int[] compareDice(int[] attacker, int[] defender){
            Arrays.sort(attacker);
            Arrays.sort(defender);
            
            System.out.println("Comparing dice throws:");
            System.out.println("Attacker:");
            for(int i = 0; i < attacker.length; i++){
                System.out.print(attacker[i]);
            }
            System.out.println("\nDefender:");
            for(int i = 0; i < defender.length; i++){
                System.out.print(defender[i]);
            }
            System.out.println();
            
            int numOfCompared = (attacker.length > defender.length) 
                    ? defender.length : attacker.length;
            
            // 1st int is number of units taken of attacker
            // 2nd is the number taken of defender
            int[] unitsTakenOfPlayers = {0, 0};
            
            for(int i = 0; i < numOfCompared; i++){
                if(attacker[i] > defender[i])
                    unitsTakenOfPlayers[1]++;
                else
                    unitsTakenOfPlayers[0]++;
            }
            
            return unitsTakenOfPlayers;
        }
        
        
        // called within attack
        private void removeArmies(int numOfArmies, Territory territory){
            territory.armies.amount -= numOfArmies; 
        }
        
        // called within attack
        // set giveCard to true (in takeTurn method)
        // checks for the end of the game?
        private boolean checkDefeated(Territory defender){
            if(defender.armies.amount == 0)
                return true;
            else 
                return false;
            // THE END OF GAME?
        }
        
        // check whether this player should get a card first
        abstract void giveCard(Player player);
        
        
        
        // throwing dice, conducting attack, returning result 
        // needs to save number of dice thrown - for later moving units
        boolean attack(Player player, Territory attacker, Territory defender){
            
            if(!checkAttack(player, attacker, defender)){
                return false;
                // do sth else?
            }
            
            int attackerDiceNum;
            do{
                attackerDiceNum = UIEngine.getNumOfDice(true);
            } while(!checkNumberOfDice(attacker, attackerDiceNum, true));
            
            int defenderDiceNum;
            do{
                defenderDiceNum = UIEngine.getNumOfDice(false);
            } while(!checkNumberOfDice(defender, defenderDiceNum,  false));
            
            
            int[] attackerResult = multipleDiceThrow(attackerDiceNum);
            int[] defenderResult = multipleDiceThrow(defenderDiceNum);
            
            int[] armiesToRemove = compareDice(attackerResult, defenderResult);
            
            removeArmies(armiesToRemove[0], attacker);
            removeArmies(armiesToRemove[1], defender);
            
            if(checkDefeated(defender)){
                int armies;        
                do{
                    armies = UIEngine.getNumOfArmies();
                } while(!checkNumOfMovedArmies(armies, attackerDiceNum, attacker));

                GameState.map.moveArmy(armies, attacker, defender);
                return true;
            }
            else
                return true;
        }

}
