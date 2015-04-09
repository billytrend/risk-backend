/**
 * 
 */
package PeerServer.protocol.dice;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

/**
 * Sent by the host/a player to request a dice roll for a number of dice with a number of faces. 
 * The roll(s) require input from all players to avoid any player being able to influence the game. 
 * "player_id" may be null in the case of a non-player host requesting 
 * a dice roll to determine the starting player.
 * @author 120011995
 *
 */
public class roll {

	int dice_count; //number of dice to roll
	int dice_faces; //number of faces on each die
	int player_id;


	/**
	 * Creates a random 256 bit number to be
	 * converted to hex
	 */
	public void generateRandomNumber(){
		convertToHex(new BigInteger(256, new Random()));
	}

	/**
	 * @param randomBigInt
	 * @return the random 256 bit integer in hexadecimal 
	 */
	public void convertToHex(BigInteger randomBigInt){
		String randomNumber = randomBigInt.toString(10);
		System.out.println("The value in Hex is: "+ randomNumber);
		generateRollHash(randomNumber);

	}

	/**
	 * @param randomNumber
	 * @return SHA-256 hash digest of the random number
	 */
	public byte[] generateRollHash(String randomNumber){
		MessageDigest md;
		byte[] digest = null;
		try {
			md = MessageDigest.getInstance("SHA-256");
			md.update(randomNumber.getBytes("UTF-8")); 
			digest = md.digest();

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return digest;
	}

	//XOR the byte arrays then convert to int array
	public static byte[] xor(byte[] a, byte[] b) {
		byte[] result = new byte[Math.min(a.length, b.length)];

		for (int i = 0; i < result.length; i++) {
			result[i] = (byte) (((int) a[i]) ^ ((int) b[i]));
		}

		return result;
	}

	public void mersenneTwisterGeneration(){
		int numberOfFaces = 6; // 6 sided dice
		MersenneTwisterFast mer = new MersenneTwisterFast();
		//int[] seed = /* XOR byte arrays, then convert to int array */
		//mer.setSeed(seed);
		/* necessary because nextInt produces zero as well */
		//int number = mer.nextInt(numberOfFaces - 1) + 1; 
	}
}


