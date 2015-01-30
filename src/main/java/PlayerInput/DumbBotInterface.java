package PlayerInput;

import GameEngine.PlayerChoice.ArmySelection;
import GameEngine.PlayerChoice.CardSelection;
import GameEngine.PlayerChoice.CountrySelection;
import GameEngine.PlayerChoice.DiceSelection;
import GameState.Card;
import GameState.Player;
import GameState.Territory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Scanner;

/**
 * Used for a very simple AI acting as a player
 *
 */
public class DumbBotInterface implements PlayerInterface {

    Random ran = new Random();
	
    protected void emit(Player p, String message) {
        System.out.print("[" + p.getId() + "]" + "\t\t");
        System.out.println(message);
    }

    /**
     * Getting an integer from the user
     * @return
     */
    protected static int easyIn() {
        // ADDITIONAL CHECKS?
        int a;
        System.out.print("Please enter your selection: ");
        Scanner in = new Scanner(System.in);
        a = in.nextInt();
        return a;
    }
    

    /**
     * 
     */
    public DiceSelection getNumberOfDice(Player player, int max) {
        emit(player, " how many dice do you want to throw? Max " + max);
        emit(player, "Chose " + max);
        return new DiceSelection(max);
    }

    /**
     * 
     */
    public CountrySelection getTerritory(Player player, HashSet<Territory> possibles,
                                         boolean canResign) {
        ArrayList<Territory> posList = new ArrayList<Territory>(possibles);
        emit(player, "Please choose a territory");
       
        // the player can decide not to make a choice
        // in case of starting an attack or moving armies
        if(canResign){
        	emit(player, "\t0. Don't choose");
        }

        for(int i = 0; i < possibles.size(); i++) {
            emit(player,  "\t" + (i + 1) + ". " + posList.get(i).getId());
        }
        
        // random choice
        Integer choice = canResign ? ran.nextInt(posList.size() + 1) : 
        				(ran.nextInt(posList.size()) + 1);
        
        emit(player, "Chose " + choice);
        return  (choice == 0) ? null :
        				new CountrySelection(posList.get(choice - 1));

    }
    
    /**
     * 
     */
    public ArmySelection getNumberOfArmies(Player player, int max) {
        emit(player, "How many armies would you like to move? Max " + max);
        emit(player, "Chose " + max);
        return new ArmySelection(ran.nextInt(max + 1));
    }

    /**
     * 
     */
    public void giveCard(Player player, Card card) {
        return;
    }

    /**
     * 
     */
    public CardSelection getCardOptions() {
        return null;
    }

}
