
public abstract class AttackEngine {
	
		abstract int oneDiceThrow();
		
		int[] multipleDiceThrow(int numOfDice){
			int[] result = new int[numOfDice];
			for(int i = 0; i < numOfDice; i++){
				result[0] = oneDiceThrow();
			}
			return result;
		}
	
		abstract boolean checkNumOfMovedArmies(int numOfArmies, int numOfDice);
		
		//check adjacent, check 2 armies 
		abstract boolean checkAttack(Player player, Territory attacker, Territory defender);
		
		// might think about removing boolean field
		abstract boolean checkNumberOfDice(Territory territory, int numOfDice, boolean attacker);
		
		// returns the array of 2 ints which gives the amount taken of each player
		abstract int[] compareDice(int[] attacker, int[] defender);
		
		
		// called within attack
		abstract void removeArmies(int numOfArmies, Territory territory);
		
		// called within attack
		// set giveCard to true (in takeTurn method)
		// checks for the end of the game?
		abstract boolean checkDefeated(Territory defender);
		
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
				} while(!checkNumOfMovedArmies(armies, attackerDiceNum));

				GameState.map.moveArmy(armies, attacker, defender);
				return true;
			}
			else
				return true;
		}

}
