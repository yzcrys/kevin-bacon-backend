package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpServer;
import io.github.cdimascio.dotenv.Dotenv;

import javax.inject.Inject;
import java.io.IOException;
import java.net.InetSocketAddress;

public class Server {
    // TODO Complete This Class

    private HttpServer server;

    @Inject
    public Server(HttpServer server) {
        this.server = server;
    }

    public void start() {
        this.server.start();
    }

    public void bind(int port) throws IOException {
        this.server.bind(new InetSocketAddress("0.0.0.0", port), 0);
    }

    public void setContext(String uri, ReqHandler handler) {
        server.createContext(uri, handler);
    }
}
