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
            String query = "MATCH (n: actor) WHERE n.actorId = '%s' RETURN n".formatted(actorId);
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
                System.out.println("Neo4jDAO: addActor(): actor " + name + " already exists");
                return 400;
            }
            else {
                System.out.println("Neo4jDAO: addActor(): Adding actor " + name);
                String query = "CREATE (n: actor {name:'%s', actorId:'%s'})".formatted(name, actorId);
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
            String query = "MATCH (n: movie) WHERE n.movieId = '%s' RETURN n".formatted(movieId);
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
                String query = "CREATE (n: movie {name:'%s', movieId:'%s'})".formatted(name, movieId);
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

    public boolean relationshipExists(String actorId, String movieId) {
        try(Session session = driver.session()){
            Transaction tx = session.beginTransaction();
            String query = "MATCH (a:actor {actorId: '%s'})-[r:ACTED_IN]->(b:movie {movieId: '%s'}) RETURN a".formatted(actorId, movieId);
            Result res = tx.run(query);
            Boolean exists = res.hasNext();
            tx.commit();
            return exists;
        }
    }

    public int addRelationship(String actorId, String movieId) {
        try(Session session = driver.session()){
            Transaction tx = session.beginTransaction();

            if (!actorExists(actorId) || !movieExists(movieId)) {
                System.out.println("Neo4jDAO: addRelationship():  " + actorId + " or " + movieId + "does not exist");
                return 404;
            }
            else if (relationshipExists(actorId, movieId)) {
                System.out.println("Neo4jDAO: addRelationship(): relationship " + actorId + " ACTED_IN " + movieId + "already exists");
                return 400;
            }
            else {
                System.out.println("Neo4jDAO: addMovie(): Adding relationship " + actorId + " ACTED_IN " + movieId);
                String query = ("MATCH (a:actor), (b:movie) WHERE a.actorId = '%s' AND b.movieId = '%s'" +
                        " CREATE (a)-[r:ACTED_IN]->(b)").formatted(actorId, movieId);
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

    public String getActor(String actorId) {
        String actor = "null";

        try(Session session = driver.session()){
            Transaction tx = session.beginTransaction();

            if (!actorExists(actorId)) {
                System.out.println("Neo4jDAO: getActor():  " + actorId + "does not exist");
                return "404";
            }
            else {
                System.out.println("Neo4jDAO: getActor(): Getting actor with id " + actorId);
                String query = ("MATCH (n:actor {actorId: '%s'})" +
                        " RETURN collect({actorId: n.actorId, name: n.name, movies: [(n)-->(m:movie) | m.movieId]}) AS obj").formatted(actorId);

                Result res = tx.run(query);

                if (res.hasNext()) {
                    org.neo4j.driver.Record record = res.next();
                    actor= record.values().get(0).get(0).toString();
                }

                tx.commit();
                return actor;
            }
        }
        catch(Exception e) {
            e.printStackTrace();
            return "500";
        }
    }

    public String getMovie(String movieId) {
        String movie = "null";

        try(Session session = driver.session()){
            Transaction tx = session.beginTransaction();

            if (!movieExists(movieId)) {
                System.out.println("Neo4jDAO: getMovie():  " + movieId + "does not exist");
                return "404";
            }
            else {
                System.out.println("Neo4jDAO: getActor(): Getting movie with id " + movieId);
                String query = ("MATCH (n:movie {movieId: '%s'})" +
                        " RETURN collect({movieId: n.movieId, name: n.name, actors: [(m:actor)-->(n) | m.actorId]}) AS obj").formatted(movieId);

                Result res = tx.run(query);

                if (res.hasNext()) {
                    org.neo4j.driver.Record record = res.next();
                    movie = record.values().get(0).get(0).toString();
                }

                tx.commit();
                return movie;
            }
        }
        catch(Exception e) {
            e.printStackTrace();
            return "500";
        }
    }
}
