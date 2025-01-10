package rip.pixie.jvmproxy;

import com.google.common.primitives.Bytes;
import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class UpstreamClient extends WebSocketClient {
    private final WebSocket clientSocket;
    private final ArrayList<ByteBuffer> toReceive = new ArrayList<>();

    public UpstreamClient(WebSocket clientSocket) {
        super(URI.create("wss://connect.essential.gg/v1"));
        this.clientSocket = clientSocket;

        this.setTcpNoDelay(true);
        this.setReuseAddr(true);
        this.setConnectionLostTimeout(0);
        Authenticator.setDefault(
                new Authenticator() {
                    @Override
                    public PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(System.getenv("WS_PROXY_USERNAME"), System.getenv("WS_PROXY_PASSWORD").toCharArray());
                    }
                }
        );

        System.setProperty("jdk.http.auth.tunneling.disabledSchemes", "");

        this.setProxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(System.getenv("WS_PROXY_HOST"), Integer.parseInt(System.getenv("WS_PROXY_PORT")))));
    }

    public void connect(String incomingAuth, String maxProtoVersion) {
        this.addHeader("Authorization", incomingAuth);
        this.addHeader("Essential-Max-Protocol-Version", maxProtoVersion);
        // this.addHeader("Essential-Protocol-Version", "6");
        this.connect();
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        for (ByteBuffer buffer : toReceive) {
            send(buffer);
        }
        toReceive.clear();
    }

    @Override
    public void onMessage(String msg) {
        this.clientSocket.send(msg);
    }

    @Override
    public void onMessage(ByteBuffer bytes) {
        this.clientSocket.send(bytes);
    }

    @Override
    public void onClose(int i, String s, boolean b) {
        this.clientSocket.close();
        System.out.printf("Connection closed: %s%n", s);
    }

    @Override
    public void onError(Exception e) {
        e.printStackTrace();
    }

    public void sendQueue(ByteBuffer buffer) {
        if (isOpen()) {
            send(buffer);
            return;
        }
        toReceive.add(buffer);
    }
}
