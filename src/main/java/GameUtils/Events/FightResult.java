package GameUtils.Events;

import GameState.Player;
import GameState.Territory;

public class FightResult {

    private int defendersLoss;
    private int attackersLoss;
    private Integer[] attackDiceRolled;
    private Integer[] defendDiceRolled;
    private final Player attacker;
    private final Player defender;
    private final Territory attackingTerritory;
    private final Territory defendingTerritory;

    public FightResult(Player attacker, Player defender, Territory attackingTerritory, Territory defendingTerritory) {
        this.attacker = attacker;
        this.defender = defender;
        this.attackingTerritory = attackingTerritory;
        this.defendingTerritory = defendingTerritory;
    }

    public int getDefendersLoss() {
        return defendersLoss;
    }

    public void setDefendersLoss(int defendersLoss) {
        this.defendersLoss = defendersLoss;
    }

    public int getAttackersLoss() {
        return attackersLoss;
    }

    public void setAttackersLoss(int attackersLoss) {
        this.attackersLoss = attackersLoss;
    }

    public Integer[] getAttackDiceRolled() {
        return attackDiceRolled;
    }

    public void setAttackDiceRolled(Integer[] attackDiceRolled) {
        this.attackDiceRolled = attackDiceRolled;
    }

    public Integer[] getDefendDiceRolled() {
        return defendDiceRolled;
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

    public Player getAttacker() {
        return attacker;
    }

    public Player getDefender() {
        return defender;
    }

    public Territory getAttackingTerritory() {
        return attackingTerritory;
    }

    public Territory getDefendingTerritory() {
        return defendingTerritory;
    }
}

