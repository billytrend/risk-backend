package GameUtils;

import GameBuilders.DemoGameBuilder;
import GameState.State;
import GameState.Territory;
import junit.framework.TestCase;
import org.javatuples.Pair;

import java.util.HashSet;

public class TerritoryUtilsTest extends TestCase {

    public void testGetAllTerritories() throws Exception {

    }

    public void testGetAllBorders() throws Exception {
        State st = DemoGameBuilder.buildGame(4, 4);
        HashSet<Pair<Territory, Territory>> terrs = TerritoryUtils.getAllBorders(st);
        assertEquals(terrs.size(), 5);
    }

    public void testAreNeighbours() throws Exception {

    }

    public void testGetUnownedTerritories() throws Exception {

    }

    public void testGetPlayersTerritories() throws Exception {

    }

    public void testHasEmptyTerritories() throws Exception {

    }

    public void testGetNeighbours() throws Exception {

    }

    public void testGetEnemyNeighbours() throws Exception {

    }

    public void testGetFriendlyNeighbours() throws Exception {

    }

    public void testGetTerritoriesWithMoreThanOneArmy() throws Exception {

    }

    public void testGetPossibleAttackingTerritories() throws Exception {

    }

    public void testGetDeployable() throws Exception {

    }

    public void testAddTerritory() throws Exception {

    }

    public void testAddBorder() throws Exception {

    }

    public void testCountTerritories() throws Exception {

    }
}