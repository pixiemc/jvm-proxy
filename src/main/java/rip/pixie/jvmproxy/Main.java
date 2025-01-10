package rip.pixie.jvmproxy;

public class Main {
    public static void main(String[] args) {
        new ProxyServer(8002).run();
    }
}