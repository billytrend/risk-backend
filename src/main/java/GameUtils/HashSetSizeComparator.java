package GameUtils;

import java.util.Comparator;
import java.util.HashSet;

import GameState.Territory;

public class HashSetSizeComparator implements Comparator<HashSet<Territory>> {

	@Override
	public int compare(HashSet<Territory> set1, HashSet<Territory> set2) {
		// TODO Auto-generated method stub
		return set1.size()<set2.size() ? 1: set1.size() == set2.size()? 0:-1;
	
	}
}