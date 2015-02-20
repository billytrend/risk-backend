package PeerServer.protocol;

//{
//        “command” : “setup”,
//        “payload” : <Territory ID>,
//        “ack_id” : <ack_id>
//}

public class HandShake {
    String command;
    String payload;
    String ack_id;
}
