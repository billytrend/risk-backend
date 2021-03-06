/**
 * 
 */
package PeerServer.protocol;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import static com.esotericsoftware.minlog.Log.debug;

/**
 * @author 120011995
 * @category JSON parsing used to translate messages recieved from other implementations into internal moves 
 */
public class Parser {

	//testing will be done with a local file
	private static final String filePath = "somewhere in my file system lol";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//creates a Parser object and invokes parsing
		new Parser().parseJSON();

	}

	/**
	 * Reads in JSON file from disk (eventually network) and
	 * begins to parse it for translation into internal moves
	 * by Serialiser class
	 */
	public void parseJSON(){

		// read the json file
		try {
			FileReader reader = new FileReader(filePath);
			JSONParser jsonParser = new JSONParser();
			JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);

			//get command from JSON
			String command = (String) jsonObject.get("command");
			debug("The command : " + command);

			//get payload which is a JSON object :( 
			JSONObject payloadStructure = (JSONObject) jsonObject.get("payload");
			String payload = (String) payloadStructure.get("type");
			debug("Payload is: " + payload);

			//get player_id
			String player_id = (String) jsonObject.get("player_id");
			debug("The player_id : " + player_id);

			//get (optional) acknowledgment request
			String ack_req = (String) jsonObject.get("ack_req");
			debug("The ack_req : " + ack_req);

		} catch (FileNotFoundException e) {
			debug("File not found");
			e.printStackTrace();
		} catch (IOException e) {
			debug("I/O Exception");
			e.printStackTrace();
		} catch (ParseException e) {
			debug("Parse Exception");
			e.printStackTrace();
		}

	}

}
