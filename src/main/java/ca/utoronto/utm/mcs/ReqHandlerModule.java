package ca.utoronto.utm.mcs;

import dagger.Module;
import dagger.Provides;
import io.github.cdimascio.dotenv.Dotenv;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;

@Module
public class ReqHandlerModule {
    // TODO Complete This Module

    /**
     * This method provides the Neo4jDAO object.
     * @return Return the Neo4jDAO object
     */
    @Provides
    public Neo4jDAO provideNeo4jDAO()
    {
        String username = "neo4j";
        String password = "123456";
        Dotenv dotenv = Dotenv.load();
        String addr = dotenv.get("NEO4J_ADDR");
        String uriDb = "bolt://" + addr + ":7687";

        Driver driver = GraphDatabase.driver(uriDb, AuthTokens.basic(username, password));

        return new Neo4jDAO(driver);
    }
}
