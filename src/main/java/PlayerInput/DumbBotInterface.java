package PlayerInput;

import GameState.Card;
import GameState.Player;
import GameState.Territory;
import PlayerInput.PlayerChoice.ArmySelection;
import PlayerInput.PlayerChoice.Choice;
import PlayerInput.PlayerChoice.CountrySelection;
import PlayerInput.PlayerChoice.DiceSelection;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;

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
    
    private Choice lastChoice = null;
    private CountDownLatch waiter = null;

    @Override
    public Choice getChoice() {
        return lastChoice;
    }

    @Override
    public Choice await() {
        return lastChoice;
    }

    @Override
    public void setChoice(Choice ch) {

    }

    public DumbBotInterface getNumberOfDice(Player player, int max) {
        emit(player, " how many dice do you want to throw? Max " + max);
        emit(player, "Chose " + max);
        this.lastChoice = new DiceSelection(max);
        return this;
    }

    public DumbBotInterface getTerritory(Player player, HashSet<Territory> possibles, 
    		boolean canResign) {
    	System.out.println("possibles: " + possibles.size());
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
        this.lastChoice = (choice == 0) ? null : 
        				new CountrySelection(posList.get(choice - 1));
        
        return this;
    }

    public DumbBotInterface getNumberOfArmies(Player player, int max) {
        emit(player, "How many armies would you like to move? Max " + max);
        emit(player, "Chose " + max);
        this.lastChoice = new ArmySelection(ran.nextInt(max + 1));
        return this;
    }

    public DumbBotInterface giveCard(Player player, Card card) {
        return this;
    }

    public DumbBotInterface getCardOptions() {
        return this;
    }

}
