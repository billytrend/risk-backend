package LobbyServer;

import GeneralUtils.Jsonify;
import LobbyServer.LobbyState.ObjectFromClient.ClientMessage;
import LobbyServer.LobbyState.ObjectFromClient.GenericTypingObject;

public class WebServerUtils {

	public static ClientMessage getMessageObject(String s) {
		GenericTypingObject o = (GenericTypingObject) Jsonify
				.getJsonStringAsObject(s, GenericTypingObject.class);
		if (o.getClassType() == null) {
			return null;
		}
		return (ClientMessage) Jsonify.getJsonStringAsObject(s,
				o.getClassType());
	}

}
