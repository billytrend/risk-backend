package PlayerInput;

import GameState.Card;
import GameState.Player;
import GameState.Territory;
import GameEngine.PlayerChoice.ArmySelection;
import GameEngine.PlayerChoice.Choice;
import GameEngine.PlayerChoice.CountrySelection;
import GameEngine.PlayerChoice.DiceSelection;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;

public class DumbBotInterface implements PlayerInterface {

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
        emit(player, " how many dice do you want to throw?");
        emit(player, "Chose " + max);
        this.lastChoice = new DiceSelection(max);
        return this;
    }

    public DumbBotInterface getTerritory(Player player, HashSet<Territory> possibles) {
        ArrayList<Territory> posList = new ArrayList<Territory>(possibles);
        emit(player, "Please choose a territory");
        for(int i = 0; i < possibles.size(); i++) {
            emit(player,  "\t" + i + ". " + posList.get(i).getId());
        }
        emit(player, "Chose 0");
        this.lastChoice = new CountrySelection(posList.get(0));
        return this;
    }

    public DumbBotInterface getNumberOfArmies(Player player, int max) {
        emit(player, "How many armies would you like to move? Max " + max);
        emit(player, "Chose " + max);
        this.lastChoice = new ArmySelection(max);
        return this;
    }

    public DumbBotInterface giveCard(Player player, Card card) {
        return this;
    }

    public DumbBotInterface getCardOptions() {
        return this;
    }

}
