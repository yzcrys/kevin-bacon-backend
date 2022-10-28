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

    private final Driver driver;

    @Inject
    public Neo4jDAO(Driver driver) {
        this.driver = driver;
    }

    public boolean actorExists(String actorId) {
        try(Session session = driver.session()){
            Transaction tx = session.beginTransaction();
            String query = "MATCH (n: actor{actorId: \"%s\"}) RETURN n".formatted(actorId);
            Result res = tx.run(query);
            boolean exists = res.hasNext();
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
            String query = "MATCH (n: movie{movieId: \"%s\"}) RETURN n".formatted(movieId);
            Result res = tx.run(query);
            boolean exists = res.hasNext();
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
            boolean exists = res.hasNext();
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
        String obj = "";

        try(Session session = driver.session()){
            Transaction tx = session.beginTransaction();

            if (!actorExists(actorId)) {
                System.out.println("Neo4jDAO: getActor():  " + actorId + "does not exist");
                return "404";
            }
            else {
                System.out.println("Neo4jDAO: getActor(): Getting actor with id " + actorId);
                String query = ("MATCH (n:actor {actorId: '%s'})" +
                        " RETURN collect({actorId: n.actorId, name: n.name, movies: [(n)-->(m:movie) | m.movieId]})[0] AS obj").formatted(actorId);

                Result res = tx.run(query);

                if (res.hasNext()) {
                    org.neo4j.driver.Record record = res.next();
                    obj = record.values().get(0).toString();
                }

                tx.commit();
                return obj;
            }
        }
        catch(Exception e) {
            e.printStackTrace();
            return "500";
        }
    }

    public String getMovie(String movieId) {
        String obj = "";

        try(Session session = driver.session()){
            Transaction tx = session.beginTransaction();

            if (!movieExists(movieId)) {
                System.out.println("Neo4jDAO: getMovie():  " + movieId + "does not exist");
                return "404";
            }
            else {
                System.out.println("Neo4jDAO: getActor(): Getting movie with id " + movieId);
                String query = ("MATCH (n:movie {movieId: '%s'})" +
                        " RETURN collect({movieId: n.movieId, name: n.name, actors: [(m:actor)-->(n) | m.actorId]})[0] AS obj").formatted(movieId);

                Result res = tx.run(query);

                if (res.hasNext()) {
                    org.neo4j.driver.Record record = res.next();
                    obj = record.values().get(0).toString();
                }

                tx.commit();
                return obj;
            }
        }
        catch(Exception e) {
            e.printStackTrace();
            return "500";
        }
    }

    public String hasRelationship(String actorId, String movieId) {
        String obj = "";

        try(Session session = driver.session()){
            Transaction tx = session.beginTransaction();

            if (!actorExists(actorId) || !movieExists(movieId)) {
                System.out.println("Neo4jDAO: hasRelationship():  " + actorId + " or " + movieId + "does not exist");
                return "404";
            }
            else {
                System.out.println("Neo4jDAO: hasRelationship(): Checking for relationship between " + actorId + " and " + movieId);
                String query = ("""
                        OPTIONAL MATCH (n: actor{actorId: '%1$s'})-[r:ACTED_IN]->(m: movie{movieId: '%2$s'}) 
                        WITH COUNT(r) > 0 AS hasRel 
                        MATCH (n: actor{actorId: '%1$s'}), (m: movie{movieId:  '%2$s'}) 
                        RETURN collect({ actorId: n.actorId, movieId: m.movieId, hasRelationship: hasRel})[0] AS obj""").formatted(actorId, movieId);
                Result res = tx.run(query);

                if (res.hasNext()) {
                    org.neo4j.driver.Record record = res.next();
                    obj = record.values().get(0).toString();
                }

                obj = obj.replace("TRUE", "true").replace("FALSE", "false");

                tx.commit();
                return obj;
            }
        }
        catch(Exception e) {
            e.printStackTrace();
            return "500";
        }
    }

    public String computeBaconNumber(String actorId) {
        String kevinBaconId = "nm0000102";
        String obj = "";

        try(Session session = driver.session()){
            Transaction tx = session.beginTransaction();

            if (!actorExists(kevinBaconId) || !actorExists(actorId)) {
                System.out.println("Neo4jDAO: computeBaconNumber():  Kevin Bacon or " + actorId + " does not exist");
                return "404";
            }
            else if (actorId.equals(kevinBaconId)) {
                return new JSONObject().put("baconNumber", 0).toString();
            }
            else {
                System.out.println("Neo4jDAO: computeBaconNumber(): Computing bacon number for " + actorId);
                String query = ("MATCH (a:actor {actorId: '%s'} ), (b:actor {actorId: '%s'}),\n" +
                                    "p = shortestPath((a)-[:ACTED_IN*]-(b))\n" +
                                    "WITH (length(p) / 2) as baconNumber\n" +
                                    "RETURN collect({ baconNumber: baconNumber })[0] as obj").formatted(actorId, kevinBaconId);
                Result res = tx.run(query);

                if (res.hasNext()) {
                    org.neo4j.driver.Record record = res.next();
                    obj = record.values().get(0).toString();
                    if (obj.equals("NULL")) {
                        System.out.println("Neo4jDAO: computeBaconNumber():  There is no path from Kevin Bacon to " + actorId);
                        obj = "404";
                    }
                }

                tx.commit();
                return obj;
            }
        }
        catch(Exception e) {
            e.printStackTrace();
            return "500";
        }
    }

    public String computeBaconPath(String actorId) {
        String kevinBaconId = "nm0000102";
        String obj = "";

        try(Session session = driver.session()){
            Transaction tx = session.beginTransaction();

            if (!actorExists(kevinBaconId) || !actorExists(actorId)) {
                System.out.println("Neo4jDAO: computeBaconPath():  Kevin Bacon or " + actorId + " does not exist");
                return "404";
            }
            else if (actorId.equals(kevinBaconId)) {
                return new JSONObject().put("baconPath", new String[] {kevinBaconId}).toString();
            }
            else {
                System.out.println("Neo4jDAO: computeBaconPath(): Computing bacon number for " + actorId);
                String query = ("""
                        MATCH (a:actor {actorId: '%s'} ),   (b:actor {actorId: '%s'}),
                            p = shortestPath((a)-[:ACTED_IN*]-(b))
                            WITH p as p, (length(p) / 2) as baconNumber, nodes(p) as n
                            RETURN collect({baconPath: [a in n | CASE WHEN a:actor THEN a.actorId ELSE a.movieId END]})[0] as obj""").formatted(actorId, kevinBaconId);
                Result res = tx.run(query);

                if (res.hasNext()) {
                    org.neo4j.driver.Record record = res.next();
                    obj = record.values().get(0).toString();
                    if (obj.equals("NULL")) {
                        System.out.println("Neo4jDAO: computeBaconNumber():  There is no path from Kevin Bacon to " + actorId);
                        obj = "404";
                    }
                }

                tx.commit();
                return obj;
            }
        }
        catch(Exception e) {
            e.printStackTrace();
            return "500";
        }
    }
}
