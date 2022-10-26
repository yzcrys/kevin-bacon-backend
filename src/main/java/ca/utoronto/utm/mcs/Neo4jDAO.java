package ca.utoronto.utm.mcs;

import org.json.JSONObject;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.Transaction;

import javax.inject.Inject;

// All your database transactions or queries should
// go in this class
public class Neo4jDAO {
    // TODO Complete This Class

    private Driver driver;
    private JSONObject responseBody;

    @Inject
    public Neo4jDAO(Driver driver) {
        this.driver = driver;
    }

    public boolean actorExists(String actorId) {
        try(Session session = driver.session()){
            Transaction tx = session.beginTransaction();
            String query = "MATCH (n: Actor) WHERE n.actorId = '%s' RETURN n".formatted(actorId);
            Result res = tx.run(query);
            Boolean exists = res.hasNext();
            tx.commit();
            return exists;
        }
    }

    public int addActor(String name, String actorId) {
        try(Session session = driver.session()){
            Transaction tx = session.beginTransaction();

            if(actorExists(actorId)) {
                System.out.println("Neo4jDAO: addActor(): Actor " + name + " already exists");
                return 400;
            }
            else {
                System.out.println("Neo4jDAO: addActor(): Adding Actor " + name);
                String query = "CREATE (n: Actor {name:'%s', actorId:'%s'})".formatted(name, actorId);
                tx.run(query);
                tx.commit();
                return 200;
            }
        }
        catch(Exception e) {
            e.printStackTrace();
            return 500;
        }
    }

    public boolean movieExists(String movieId) {
        try(Session session = driver.session()){
            Transaction tx = session.beginTransaction();
            String query = "MATCH (n: Movie) WHERE n.movieId = '%s' RETURN n".formatted(movieId);
            Result res = tx.run(query);
            Boolean exists = res.hasNext();
            tx.commit();
            return exists;
        }
    }

    public int addMovie(String name, String movieId) {
        try(Session session = driver.session()){
            Transaction tx = session.beginTransaction();

            if(movieExists(movieId)) {
                System.out.println("Neo4jDAO: addMovie(): Movie " + name + " already exists");
                return 400;
            }
            else {
                System.out.println("Neo4jDAO: addMovie(): Adding movie " + name);
                String query = "CREATE (n: Movie {name:'%s', movieId:'%s'})".formatted(name, movieId);
                tx.run(query);
                tx.commit();
                return 200;
            }
        }
        catch(Exception e) {
            e.printStackTrace();
            return 500;
        }
    }
}
