package GameUtils.Results;

import GameEngine.PlayState;
import GameState.Player;
import GameState.State;
import GameState.Territory;
import GameUtils.ArmyUtils;
import GameUtils.RuleUtils;

/**
 * A class that provides all the details about a fight
 * including the loss on both sides, the information 
 * about both players, the territories involved etc.
 *
 */
public class FightResult extends Change {

    private int defendersLoss;
    private int attackersLoss;
    private Integer[] attackDiceRolled;
    private Integer[] defendDiceRolled;
    private final String attackerId;
    private final String defenderId;
    private final String attackingTerritoryId;
    private final String defendingTerritoryId;

    public FightResult(String attackerId, String defenderId, String attackingTerritoryId, String defendingTerritoryId) {
    	super(attackerId, PlayState.PLAYER_INVADING_COUNTRY);
    	this.attackerId = attackerId;
        this.defenderId = defenderId;
        this.attackingTerritoryId = attackingTerritoryId;
        this.defendingTerritoryId = defendingTerritoryId;
    }


    /**
    * The method destroys the suitable amount of armies both
    * on attackers and defenders territory. If the attacker won
    * then the method moves the minimum amount of armies to the
    * defeated territory.
    * 
    */
	public void applyChange(State state) {
		Player attacker = state.lookUpPlayer(attackerId);
		Player defender = state.lookUpPlayer(defenderId);
		Territory attackingTerritory = state.lookUpTerritory(attackingTerritoryId);
		Territory defendingTerritory = state.lookUpTerritory(defendingTerritoryId);
		
		ArmyUtils.destroyArmies(attacker, attackingTerritory, attackersLoss);
        ArmyUtils.destroyArmies(defender, defendingTerritory, defendersLoss);
        
        // if country defeated then move minimum armies across
        if (ArmyUtils.getNumberOfArmiesOnTerritory(defender, defendingTerritory) == 0) {
        	ArmyUtils.moveArmies(attacker, attackingTerritory, defendingTerritory,
        			(attackDiceRolled.length - attackersLoss));
        }
	}
	
	public String toString(){
		return super.toString() + "\n\tFIGHT RESULT:\n\t" + attackerId + " attacks " + defenderId +"'s territory " + 
				defendingTerritoryId + " from " + attackingTerritoryId + 
				".\n\tAttacker looses " + attackersLoss + " armies. Defender looses " + defendersLoss +  " armies.";
	}
    

    public void setDefendersLoss(int defendersLoss) {
        this.defendersLoss = defendersLoss;
    }
    public void setAttackersLoss(int attackersLoss) {
        this.attackersLoss = attackersLoss;
    }
    public void setAttackDiceRolled(Integer[] attackDiceRolled) {
        this.attackDiceRolled = attackDiceRolled;
    }
    public void setDefendDiceRolled(Integer[] defendDiceRolled) {
        this.defendDiceRolled = defendDiceRolled;
    }
    public void addDefendLoss() {
        this.defendersLoss++;
    }
    public void addAttackLoss() {
        this.attackersLoss++;
    }
    public String getDefenderId() {
        return defenderId;
    }


	public int getDefendersLoss() {
		return defendersLoss;
	}
	public int getAttackersLoss(){
		return attackersLoss;
	}
	public String getAttackingTerritoryId(){
		return attackingTerritoryId;
	}

	public String getDefendingTerritoryId(){
		return defendingTerritoryId;
	}


	public Integer[] getAttackDiceRolled() {
		return attackDiceRolled;
	}
	
	public Integer[] getDefendDiceRolled(){
		return defendDiceRolled;
	}

}

