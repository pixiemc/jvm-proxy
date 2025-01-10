package rip.pixie.jvmproxy;

import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.exceptions.InvalidDataException;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.handshake.ServerHandshake;
import org.java_websocket.handshake.ServerHandshakeBuilder;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.HashMap;

public class ProxyServer extends WebSocketServer {
    private final HashMap<WebSocket, UpstreamClient> upstreamClients = new HashMap<>();

    public ProxyServer(int port) {
        super(new InetSocketAddress(port));
    }

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
        UpstreamClient upstreamClient = new UpstreamClient(webSocket);
        upstreamClients.put(webSocket, upstreamClient);
        upstreamClient.connect(clientHandshake.getFieldValue("Authorization"), clientHandshake.getFieldValue("Essential-Max-Protocol-Version"));
    }

    @Override
    public void onClose(WebSocket webSocket, int i, String s, boolean b) {
        upstreamClients.get(webSocket).close();
        upstreamClients.remove(webSocket);
    }

    @Override
    public void onMessage(WebSocket webSocket, String s) {
        upstreamClients.get(webSocket).send(s);
    }

    @Override
    public void onMessage(WebSocket conn, ByteBuffer message) {
        upstreamClients.get(conn).sendQueue(message);
    }

    @Override
    public void onError(WebSocket webSocket, Exception e) {
        e.printStackTrace();
    }

    @Override
    public void onStart() {

    }

    @Override
    public ServerHandshakeBuilder onWebsocketHandshakeReceivedAsServer(WebSocket conn, Draft draft, ClientHandshake request) throws InvalidDataException {
        ServerHandshakeBuilder builder = super
                .onWebsocketHandshakeReceivedAsServer(conn, draft, request);
        builder.put("Essential-Protocol-Version", "6");
        return builder;
    }
}
