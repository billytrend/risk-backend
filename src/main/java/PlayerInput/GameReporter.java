package PlayerInput;

import GameUtils.Results.FightResult;
import GameEngine.PlayerChoice.ArmySelection;
import GameEngine.PlayerChoice.CardSelection;
import GameEngine.PlayerChoice.CountrySelection;
import GameEngine.PlayerChoice.DiceSelection;

public interface GameReporter {
	
    public void report(ArmySelection s);
    public void report(CardSelection s);
    public void report(CountrySelection s);
    public void report(DiceSelection s);
   
    public void report(FightResult f);
    
}
