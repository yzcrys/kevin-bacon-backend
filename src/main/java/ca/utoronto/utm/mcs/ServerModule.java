package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpServer;
import dagger.Module;
import dagger.Provides;

import java.io.IOException;

@Module
public class ServerModule {
    // TODO Complete This Module

    /**
     * This method provides the <code>HttpServer</code> object injected into <code>Server</code>
     * @return Return the <code>Server</code> object
     */
    @Provides
    public HttpServer provideServer(){
        try {
            return HttpServer.create();

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
