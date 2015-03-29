package PeerServer.protocol.ProtocolObjects;

public class SuperProtocolObject {
    
    final String requestType;
    final Object payload;


    protected SuperProtocolObject(String requestType, Object payload) {
        this.requestType = requestType;
        this.payload = payload;
    }
}
