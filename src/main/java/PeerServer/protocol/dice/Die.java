package PeerServer.protocol.dice;

import java.util.Random;
import java.util.Map;
import java.util.HashMap;
import java.security.MessageDigest;
import java.security.SecureRandom;

/**
 * Die Class
 * Wraps around cryptographic dice rolling
 */
public class Die {

	private int faceValue = 1;

	private Map<Integer, byte[]> playerhash = new HashMap<Integer, byte[]>();
	private Map<Integer, byte[]> playernum = new HashMap<Integer, byte[]>();

	private byte[] mixednumber = new byte[32];
	private int ptr = 0;

	private SecureRandom sr;

	private boolean finalised = false;

	public Die() throws HashMismatchException
	{
		try {
			sr = SecureRandom.getInstance("SHA1PRNG", "SUN");
		} catch (Exception e) {
			throw new HashMismatchException("Couldn't initialise random number generator");
		}
	}
	
	public void setFaceValue(int value){
		faceValue = value;
	}
	
	public int getNumberOfReceivedHashes(){
		return playerhash.size();
	}
	
	public int getNumberOfReceivedNumbers(){
		return playernum.size();
	}

	/**
	 * Converts a hexadecimal string to a byte array
	 * @param	str	The string to convert
	 */
	private byte[] hexToByte(String str)
	{
		int len = str.length();
		byte[] data = new byte[len >> 1];

		for (int i = 0; i < len; i += 2)
			data[i >> 1] = (byte)((Character.digit(str.charAt(i), 16) << 4) +
						  Character.digit(str.charAt(i + 1), 16));

		return data;
	}

	/**
	 * Abstracts over random number generation for sending to other hosts
	 * @return The generated number
	 */
	public byte[] generateNumber()
	{
		byte[] res = new byte[mixednumber.length];
		sr.nextBytes(res);
		return res;
	}

	/**
	 * Hashes a byte array
	 * @param	arr Byte array to hash
	 * @return		Byte array containing hash
	 */
	public byte[] hashByteArr(byte[] arr) throws HashMismatchException
	{
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");

			md.update(arr);
			return md.digest();
		} catch (Exception e) {
			throw new HashMismatchException("Couldn't find the SHA-256 hashing algorithm!");
		}
	}

	
	/**
	 * Hash some source data and compare with a hash to see if they match
	 * @param	src		The source data
	 * @param	hash	The hash to compare hash(src) with
	 * @return			Did the comparison succeed
	 */
	private boolean hashMatch(byte[] src, byte[] hash) throws HashMismatchException
	{
		byte[] calchash = hashByteArr(src);
		int len = calchash.length;

		if (len != hash.length)
			return false;

		for (int i = 0; i < len; i++)
			if (calchash[i] != hash[i]) // No need for constant time comparisons
				return false;

		return true;
	}

	/**
	 * Adds a hash sent by a player for a dice roll
	 * @param	playerid	The player id sending the number
	 * @param	number		The number itself
	 */
	public void addHash(int playerid, String hash) throws HashMismatchException
	{
		if (finalised)
			throw new HashMismatchException("Already finalised, cannot retroactively add hash");

		if (hash.length() != mixednumber.length * 1)
			throw new HashMismatchException("Invalid hash length provided");

		if (playernum.size() != 0)
			throw new HashMismatchException("Refusing to receive a hash after receiving a number");

		if (playerhash.get(playerid) != null)
			throw new HashMismatchException("Already received a hash from this player");

		playerhash.put(playerid, hexToByte(hash));
	}
	
	/**
	 * Adds and verifies a number sent by a player after its corresponding hash was sent
	 * @param	playerid	The player id sending the number
	 * @param	number		The number itself
	 */
	public void addNumber(int playerid, String number) throws HashMismatchException
	{
		byte[] numberbyte;
		byte[] hashbyte;

		if (finalised)
			throw new HashMismatchException("Already finalised, cannot retroactively add number");

		if (playernum.get(playerid) != null)
			throw new HashMismatchException("Already received a number from this player");

		if (number.length() != mixednumber.length * 2)
			throw new HashMismatchException("Invalid number length provided");

		hashbyte = playerhash.get(playerid);

		if (hashbyte == null)
			throw new HashMismatchException("No hash provided before number");

		numberbyte = hexToByte(number);

		if (!hashMatch(numberbyte, hashbyte))
			throw new HashMismatchException("Hash of number and received number do not match");
		
		playernum.put(playerid, numberbyte);

		for (int i = 0; i < mixednumber.length; i++)
			mixednumber[i] ^= numberbyte[i];
	}

	/**
	 * Performs final checks before results from the hash can be returned
	 */
	private void finalise() throws HashMismatchException
	{
		if (finalised)
			throw new HashMismatchException("Already finalised");

		for (Map.Entry<Integer, byte[]> entry : playerhash.entrySet())
			if (playernum.get(entry.getKey()) == null)
				throw new HashMismatchException("Haven't received all numbers");

		mixednumber = hashByteArr(mixednumber); // So all seeds create a pseudo-random sequence of numbers
	}

	/**
	 * Returns a number between 0 and 255
	 * @return	The resulting byte
	 */
	public int getByte() throws HashMismatchException, OutOfEntropyException
	{
		int res;

		if (!finalised) {
			finalise();
			finalised = true;
		}

		if (ptr >= mixednumber.length)
			throw new OutOfEntropyException("Exhausted entropy available");

		res = (int)mixednumber[ptr++] & 0xFF; // Bit hack to force remove sign extension of MSB
		return res;
	}
	
	
	
	public class OutOfEntropyException extends Exception {

		private static final long serialVersionUID = 1L;

		public OutOfEntropyException(String message)
		{
			super(message);
		}

	}

	public class HashMismatchException extends Exception {

		private static final long serialVersionUID = 1L;
	
		public HashMismatchException(String message)
		{
			super(message);
		}

	}


}

