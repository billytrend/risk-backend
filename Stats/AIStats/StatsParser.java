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
		double[] bControlStats = new double[17];
		double[] comDefStats = new double[17];
		double[] comAggStats = new double[17];
		double[] bossStats = new double[17];
		double[] swapperStats = new double[17];

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
				case "Swapper":
					parseLine(line, swapperStats);
					break;	
				default:
					break;
				}
			}
			writeCollectedStats(bControlStats, comAggStats, comDefStats, bossStats, swapperStats);
			scanner.close();
		} catch (FileNotFoundException e) {
			System.out.println("\"Statistics.csv file\" not found.");
		}
	}
	
	public String statsString(double[] stats, String s){
		for(int i =0; i< 8; i++) s += stats[i] + ",";
		//avg turns on win
		s += stats[8]/stats[0] + ",";
		//avg attacks per turn when win
		s += stats[9]/stats[8] + ",";
		//avg position with 6 players
		s += stats[12]/stats[13] + ",";
		//win percentage
		s += stats[0]/stats[14] *100 + ",";
		//count
		s += stats[14] + ",";
		//win percentage vs losers
		s+= stats[1]/stats[15]*100 + ",";
		//win percentage vs dumb bots
		s += stats[2]/stats[16]*100;
		return s;
	}
	
	public void writeCollectedStats(double[] bControlStats, double[] comAggStats, double[] comDefStats, double[] bossStats, double[] swapperStats){
		File collectedStatsFile = new File("CollectedStats.csv");
		try {
			PrintWriter writer = new PrintWriter(new FileWriter(collectedStatsFile));
			String titles = "AI,wins,wins V losers,wins V dumbbots,wins V 1, wins V 2,wins V 3,wins V 4,wins V 5, avg turns taken to win,avg attacks per win,avg position -6 players, win percentage,count,losercount,dbcount";
			String bControl = "BorderControl" +",";
			bControl = statsString(bControlStats, bControl);
			String comAgg = "CommunistAggressive" +",";
			comAgg = statsString(comAggStats, comAgg);
			String comDef = "CommunistDefensive" +",";
			comDef = statsString(comDefStats, comDef);
			String boss = "Boss" +",";
			boss = statsString(bossStats, boss);
			String swapper = "Swapper" +",";
			swapper = statsString(swapperStats, swapper);
			
			writer.println(titles);
			writer.println(bControl);
			writer.println(comAgg);
			writer.println(comDef);
			writer.println(boss);
			writer.println(swapper);
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
			//attacks when won
			stats[9] +=Integer.parseInt(line[7]);
			
		}
		//total turns
		stats[10] += Integer.parseInt(line[6]);
		//total attacks
		stats[11] += Integer.parseInt(line[7]);
		if(line[5] != ""){
		//position when 6 players
		stats[12] += Integer.parseInt(line[8]);
		stats[13] ++;
		}
		//games recorded
		stats[14]++;
		//count of loser games
		if(line[1].equals("l0")){
			stats[15] ++;
		}
		//count of dumbbot games
		else stats[16] ++;
		return stats;
	}

}
