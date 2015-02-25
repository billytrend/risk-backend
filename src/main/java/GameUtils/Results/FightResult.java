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
    private final Player attacker;
    private final Player defender;
    private final Territory attackingTerritory;
    private final Territory defendingTerritory;

    public FightResult(Player attacker, Player defender, Territory attackingTerritory, Territory defendingTerritory) {
    	super(attacker, PlayState.PLAYER_INVADING_COUNTRY);
    	this.attacker = attacker;
        this.defender = defender;
        this.attackingTerritory = attackingTerritory;
        this.defendingTerritory = defendingTerritory;
    }


    /**
    * The method destroys the suitable amount of armies both
    * on attackers and defenders territory. If the attacker won
    * then the method moves the minimum amount of armies to the
    * defeated territory.
    * 
    */
	public void applyChange() {
		ArmyUtils.destroyArmies(attacker, attackingTerritory, attackersLoss);
        ArmyUtils.destroyArmies(defender, defendingTerritory, defendersLoss);
        
        // if country defeated then move minimum armies across
        if (ArmyUtils.getNumberOfArmiesOnTerritory(defender, defendingTerritory) == 0) {
        	ArmyUtils.moveArmies(attacker, attackingTerritory, defendingTerritory,
        			(attackDiceRolled.length - attackersLoss));
        }
	}
	
	public String toString(){
		return super.toString() + "\n\tFIGHT RESULT:\n\t" + attacker.getId() + " attacks " + defender.getId() +"'s territory " + 
				defendingTerritory.getId() + " from " + attackingTerritory.getId() + 
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
    public Player getDefender() {
        return defender;
    }


	public int getDefendersLoss() {
		return defendersLoss;
	}
	public int getAttackersLoss(){
		return attackersLoss;
	}
	public Territory getAttackingTerritory(){
		return attackingTerritory;
	}

	public Territory getDefendingTerritory(){
		return defendingTerritory;
	}


	public Integer[] getAttackDiceRolled() {
		return attackDiceRolled;
	}
	
	public Integer[] getDefendDiceRolled(){
		return defendDiceRolled;
	}

}

