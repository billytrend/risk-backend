package AIStats;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;

public class ParserTests {

	@Test
	public void testLine() {
		StatsParser parser = new StatsParser();
		String[] line = {"BorderControl", "l0", "l1","","","","20","87","1"};
		double[] test = new double[13];
		test = parser.parseLine(line, test);
		assertEquals(1, test[0],0);
		assertEquals(1, test[1],0);
		assertEquals(0, test[2],0);
		assertEquals(0, test[3],0);
		assertEquals(1, test[4],0);
		assertEquals(0, test[5],0);
		assertEquals(0, test[6],0);
		assertEquals(0, test[7],0);
		assertEquals(20, test[8],0);
		assertEquals(20, test[9],0);
		assertEquals(87, test[10],0);
		assertEquals(1, test[11],0);
		assertEquals(1, test[12],0);
	}
}
