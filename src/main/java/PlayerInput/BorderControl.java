package PlayerInput;

import java.util.ArrayList;
import java.util.HashSet;

import org.javatuples.Triplet;

import GameEngine.RequestReason;
import GameState.Card;
import GameState.Player;
import GameState.State;
import GameState.Territory;
import GameUtils.ArmyUtils;
import GameUtils.PlayerUtils;
import GameUtils.TerritoryUtils;
import GameUtils.Results.Change;

public class BorderControl implements PlayerInterface {
	public State state;

	public BorderControl(State state) {
		this.state = state;
	}

	public int getNumberOfDice(Player player, int max, RequestReason reason,
			Territory attacking, Territory defending) {
		HashSet<Territory> neighbours = TerritoryUtils.getNeighbours(state,
				attacking);
		for (Territory t : neighbours) {
			// if the current territory is not protected all round then don't
			// want to leave it defenseless
			if (PlayerUtils.getTerritoryOwner(state, t) != player
					&& t != defending) {
				// +1 so rounds up on /2
				int numberOfArmies = ArmyUtils.getNumberOfArmiesOnTerritory(
						player, t) + 1;
				// only want to move half to leave the current territory with
				// some defense
				if (numberOfArmies / 2 > max) {
					return max;
				} else
					return numberOfArmies / 2;

				// think about the difference in the size of the threats?
			}
		}
		// if protected all around
		return max;

	}

	public Territory getTerritory(Player player, HashSet<Territory> possibles,
			boolean canResign, RequestReason reason) {

		ArrayList<HashSet<Territory>> clusters = TerritoryUtils.getAllClusters(
				state, player);
		switch (reason) {
		case PLACING_ARMIES_SET_UP:
			// look to place in clusters - choose random territory if can't
			// (maybe adjust to finding territory close to cluster?)
			HashSet<Territory> options = new HashSet<Territory>();
			// for each cluster (starting with the largest) looks at surrounding
			// and checks if possible move
			for (HashSet<Territory> cluster : clusters) {
				for (Territory territory : cluster) {
					HashSet<Territory> neighbours = TerritoryUtils
							.getNeighbours(state, territory);
					for (Territory neighbour : neighbours) {
						if (possibles.contains(neighbour))
							return neighbour;
					}
				}
			}
			return TerritoryUtils.getRandomTerritory(state, possibles);
		case PLACING_REMAINING_ARMIES_PHASE:
			// reinforce outer edges of clusters
		case PLACING_ARMIES_PHASE:
			// is this for reinforcing at the start of a go?
			// will it ever need to be different to above?
			Territory weakest = null;
			int lowestDefense = 100;
			for (HashSet<Territory> cluster : clusters) {
				for (Territory territory : cluster) {
					HashSet<Territory> enemies = TerritoryUtils
							.getEnemyNeighbours(state, territory, player);
					if (!enemies.isEmpty()) {
						int enemyArmies = 0;
						int armies = ArmyUtils.getArmiesOnTerritory(player,
								territory).size();
						for (Territory enemyTerritory : enemies) {
							Player enemy = PlayerUtils.getTerritoryOwner(state,
									enemyTerritory);
							enemyArmies += ArmyUtils.getArmiesOnTerritory(
									enemy, enemyTerritory).size();
						}
						if (armies / enemyArmies < lowestDefense) {
							lowestDefense = armies / enemyArmies;
							weakest = territory;
						}
					}
				}
				if (weakest != null) return weakest;
			}
		case ATTACK_CHOICE_FROM:
		case ATTACK_CHOICE_TO:
			// look for largest cluster and choose outer territory that has 2:1
			// advantage over another
			for (HashSet<Territory> cluster : clusters) {
				for (Territory territory : cluster) {
					int armiesOnTerritory = ArmyUtils
							.getNumberOfArmiesOnTerritory(player, territory);
					HashSet<Territory> enemyNeighbours = TerritoryUtils
							.getEnemyNeighbours(state, territory, player);
					for (Territory enemyTerritory : enemyNeighbours) {
						Player enemy = PlayerUtils.getTerritoryOwner(state,
								enemyTerritory);
						int armiesOnEnemyTerritory = ArmyUtils
								.getArmiesOnTerritory(enemy, enemyTerritory)
								.size();
						if (armiesOnTerritory >= 2 * armiesOnEnemyTerritory) {
							if (reason == RequestReason.ATTACK_CHOICE_FROM)
								return territory;
							else
								return enemyTerritory; // should probably find
														// weakest enemy
														// neighbour
						}
					}
				}
			}

		case REINFORCEMENT_PHASE:
			// move armies outwards - look for weakest, unprotected territories
		
		}

		return null;

	}
	//need to and from
	public int getNumberOfArmies(Player player, int max, RequestReason reason, Territory to, Territory from) {
	//case POST_ATTACK_MOVEMENT:
		// if all neighbours are friendly move all but 1
		// else move half - or look at difference in threats and decide
		// accordingly
		int armiesFrom = ArmyUtils.getArmiesOnTerritory(player, from).size();
		if(TerritoryUtils.getEnemyNeighbours(state, from, player).size() == 0) return armiesFrom -1;
		else return armiesFrom/2;

	}

	public Triplet<Card, Card, Card> getCardChoice(Player player,
			ArrayList<Triplet<Card, Card, Card>> possibleCombinations) {
		return null;

	}

	@Override
	public int getNumberOfDice(Player player, int max, RequestReason reason) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void reportStateChange(Change change) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void createResponse() {
		// TODO Auto-generated method stub
		
	}


}
