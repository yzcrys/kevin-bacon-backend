package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpServer;
import io.github.cdimascio.dotenv.Dotenv;

import javax.inject.Inject;
import java.io.IOException;
import java.net.InetSocketAddress;

public class Server {
    // TODO Complete This Class

    private HttpServer server;

    /**
     * A HttpServer object is injected into this constructor as stated in <code>A1Handout.pdf</code>.
     * @param server The Server object injected
     */
    @Inject
    public Server(HttpServer server) {
        this.server = server;
    }

    /**
     * This method starts the given server.
     * @see App#main(String[]) <code>main(String[])</code> in <code>App</code> for example usage
     */
    public void start() {
        this.server.start();
    }

    /**
     * This method binds the server to <code>localhost</code> and the given port.
     * @param port The port on to start the server on
     * @throws IOException
     * @see App#main(String[]) <code>main(String[])</code> in <code>App</code> for example usage
     */
    public void bind(int port) throws IOException {
        this.server.bind(new InetSocketAddress("localhost", port), 0);
    }

    /**
     * This method creates a server context for the given uri and handler
     * @param uri The uri used to create the server context.
     * @param handler The handler used to create the server context.
     * @see App#main(String[]) <code>main(String[])</code> in <code>App</code> for example usage
     */
    public void setContext(String uri, ReqHandler handler) {
        server.createContext(uri, handler);
    }
}
