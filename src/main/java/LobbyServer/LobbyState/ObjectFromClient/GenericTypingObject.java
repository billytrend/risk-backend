package LobbyServer.LobbyState.ObjectFromClient;

/**
 * This is used to get the type of the message received from a client. It means
 * every message from a client must be sent with a commandType field.* *
 */
public class GenericTypingObject {

	private String commandType;

	public Class getClassType() {
		try {
			return Class.forName(this.commandType);
		} catch (ClassNotFoundException e) {
			return null;
		}
	}

}
