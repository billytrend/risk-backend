package PeerServer.protocol;

//{
//        “command” : “setup”,
//        “payload” : <Territory ID>,
//        “ack_id” : <ack_id>
//}

public class HandShake extends JSON_Command {
    String command;
    String payload;
    String ack_id;
}
