package AIStats;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class StatsParser {

	public static void main(String[] args){
		File statsFile = new File("Statistics.csv");
		StatsParser parser = new StatsParser();
		parser.parseFile(statsFile);
	}

	public void parseFile(File statsFile) {
		
		String[] line;
		double[] bControlStats = new double[13];
		double[] comDefStats = new double[13];
		double[] comAggStats = new double[13];
		double[] bossStats = new double[13];

		try {
			Scanner scanner = new Scanner(new FileReader(statsFile));
			while (scanner.hasNextLine()) {
				line = scanner.nextLine().split(",");
				switch (line[0]) {
				case "Boss":
					bossStats = parseLine(line, bossStats);
					break;
				case "CommunistDefensive":
					comDefStats = parseLine(line, comDefStats);
					break;
				case "CommunistAggressive":
					comAggStats = parseLine(line, comAggStats);
					break;
				case "BorderControl":
					parseLine(line, bControlStats);
					break;
				default:
					break;
				}
			}
			writeCollectedStats(bControlStats, comAggStats, comDefStats, bossStats);
			scanner.close();
		} catch (FileNotFoundException e) {
			System.out.println("\"Statistics.csv file\" not found.");
		}
	}
	
	public String statsString(double[] stats, String s){
		for(int i =0; i< 8; i++) s += stats[i] + ",";
		//avg turns on win
		s += stats[8]/stats[0] + ",";
		//avg attacks per turn
		s += stats[10]/stats[9] + ",";
		//avg position
		s += stats[11]/stats[12];
		return s;
	}
	
	public void writeCollectedStats(double[] bControlStats, double[] comAggStats, double[] comDefStats, double[] bossStats){
		File collectedStatsFile = new File("CollectedStats.csv");
		try {
			PrintWriter writer = new PrintWriter(new FileWriter(collectedStatsFile));
			String bControl = "BorderControl" +",";
			bControl = statsString(bControlStats, bControl);
			String comAgg = "CommunistAggressive" +",";
			comAgg = statsString(comAggStats, comAgg);
			String comDef = "CommunistDefensive" +",";
			comDef = statsString(comDefStats, comDef);
			String boss = "Boss" +",";
			boss = statsString(bossStats, boss);
			
			writer.println(bControl);
			writer.println(comAgg);
			writer.println(comDef);
			writer.println(boss);
			writer.close();
		} catch (IOException e) {
			System.out.println("\"CollectedStats.csv file\" not found.");
		}
	}

	public double[] parseLine(String[] line, double[] stats) {
		if (line[8].equals("1")) {
			// overall wins
			stats[0]++;
			// against losers
			if (line[1].equals("l0"))
				stats[1]++;
			// against dumbbots
			if (line[1].equals("d0"))
				stats[2]++;
			// against 1
			if (line[2].equals(""))
				stats[3]++;
			// against 2
			else if (line[3].equals(""))
				stats[4]++;
			// against 3
			else if (line[4].equals(""))
				stats[5]++;
			// against 4
			else if (line[5].equals(""))
				stats[6]++;
			// against 5
			else
				stats[7]++;
			//turns when won
			stats[8] += Integer.parseInt(line[6]);
		}
		//total turns
		stats[9] += Integer.parseInt(line[6]);
		//total attacks
		stats[10] += Integer.parseInt(line[7]);
		//position
		stats[11] += Integer.parseInt(line[8]);
		//games recorded
		stats[12]++;
		return stats;
	}

}
