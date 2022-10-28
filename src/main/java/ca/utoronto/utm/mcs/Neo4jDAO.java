package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpExchange;
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

    /**
     * This field is final, following the Neo4j documentation examples.
     */
    private final Driver driver;

    /**
     * A Driver (org.neo4j.driver) object is injected into this constructor as stated in <code>A1Handout.pdf</code>.
     * @param driver The driver injected
     */
    @Inject
    public Neo4jDAO(Driver driver) {
        this.driver = driver;
    }

    /**
     * This method checks if the actor corresponding to the given actorId exists in the database.
     * @param actorId The actorId corresponding to the actor being checked.
     * @return Return true if the actor exist, false otherwise.
     * @see Neo4jDAO#addActor(String, String) addActor(String) in NEO4jDAO for example usage
     */
    public boolean actorExists(String actorId) {
        try(Session session = driver.session()){
            Transaction tx = session.beginTransaction();
            String query = "MATCH (n: actor{id: \"%s\"}) RETURN n".formatted(actorId);
            Result res = tx.run(query);
            boolean exists = res.hasNext();
            tx.commit();
            return exists;
        }
    }

    /**
     * This endpoint is to add an actor node into the database.
     * If an actor with the same actorId already exists in the database, no node is added.
     * @param name The name of the actor being added.
     * @param actorId The actorId of the actor being added.
     * @return Return 200 if the actor was successfully added, 400 if the request body is improperly formatted,
     * missing required information, or an actor with the same actorId already exists, and 500 if save or add was unsuccessful (Java Exception Thrown).
     * @see Neo4jDAO#actorExists(String)
     */
    public int addActor(String name, String actorId) {
        try(Session session = driver.session()){
            Transaction tx = session.beginTransaction();

            if(actorExists(actorId)) {
//                System.out.println("Neo4jDAO: addActor(): actor " + name + " already exists");
                return 400;
            }
            else {
//                System.out.println("Neo4jDAO: addActor(): Adding actor " + name);
                String query = "CREATE (n: actor {Name:'%s', id:'%s'})".formatted(name, actorId);
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

    /**
     * This method checks if the movie corresponding to the given movieId exists in the database.
     * @param movieId The movieId corresponding to the movie being checked.
     * @return Return true if the movie exist, false otherwise.
     * @see Neo4jDAO#addMovie(String, String) addMovie(String) in NEO4jDAO for example usage
     */
    public boolean movieExists(String movieId) {
        try(Session session = driver.session()){
            Transaction tx = session.beginTransaction();
            String query = "MATCH (n: movie{id: \"%s\"}) RETURN n".formatted(movieId);
            Result res = tx.run(query);
            boolean exists = res.hasNext();
            tx.commit();
            return exists;
        }
    }

    /**
     * This endpoint is to add a movie node into the database.
     * If a movie with the same movieId already exists in the database, no node is added.
     * @param name The name of the movie being added.
     * @param movieId The movieId of the movie being added.
     * @return Return 200 if the movie was successfully added, 400 if the request body is improperly formatted,
     * missing required information, or a movie with the same movieId already exists, and 500 if save or add was unsuccessful (Java Exception Thrown).
     * @see Neo4jDAO#movieExists(String)
     */
    public int addMovie(String name, String movieId) {
        try(Session session = driver.session()){
            Transaction tx = session.beginTransaction();

            if(movieExists(movieId)) {
//                System.out.println("Neo4jDAO: addMovie(): Movie " + name + " already exists");
                return 400;
            }
            else {
//                System.out.println("Neo4jDAO: addMovie(): Adding movie " + name);
                String query = "CREATE (n: movie {Name:'%s', id:'%s'})".formatted(name, movieId);
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

    /**
     * This method checks if an <code>ACTED_IN</code> relationship between the given actor and movie exists in the database.
     * @param actorId The actorId corresponding to the actor being checked.
     * @param movieId The movieId corresponding to the movie being checked.
     * @return Return true if the relationship exists, false otherwise.
     * @see Neo4jDAO#addRelationship(String, String)  addRelationship(String) in NEO4jDAO for example usage
     */
    public boolean relationshipExists(String actorId, String movieId) {
        try(Session session = driver.session()){
            Transaction tx = session.beginTransaction();
            String query = "MATCH (a:actor {id: '%s'})-[r:ACTED_IN]->(b:movie {id: '%s'}) RETURN a".formatted(actorId, movieId);
            Result res = tx.run(query);
            boolean exists = res.hasNext();
            tx.commit();
            return exists;
        }
    }

    /**
     *  This endpoint is to add an ACTED_IN relationship between an actor
     * and a movie in the database.
     * If an ACTED_IN relationship between the actor and movie already exists in the database, no relationship is added.
     * If the actor or movie does not exist in the database, no relationship is added.
     * @param actorId The actorId of the actor in question.
     * @param movieId The movieId of the movie in question.
     * @return Return 200 if the relationship was successfully added, 400 if the request body is improperly formatted,
     * missing required information, or a relationship with the same relationshipId already exists,
     * 404 if the actor or movie does not exist, and 500 if save or add was unsuccessful (Java Exception Thrown).
     * @see Neo4jDAO#relationshipExists(String, String)
     */
    public int addRelationship(String actorId, String movieId) {
        try(Session session = driver.session()){
            Transaction tx = session.beginTransaction();

            if (!actorExists(actorId) || !movieExists(movieId)) {
//                System.out.println("Neo4jDAO: addRelationship():  " + actorId + " or " + movieId + "does not exist");
                return 404;
            }
            else if (relationshipExists(actorId, movieId)) {
//                System.out.println("Neo4jDAO: addRelationship(): relationship " + actorId + " ACTED_IN " + movieId + "already exists");
                return 400;
            }
            else {
//                System.out.println("Neo4jDAO: addMovie(): Adding relationship " + actorId + " ACTED_IN " + movieId);
                String query = ("MATCH (a:actor), (b:movie) WHERE a.id = '%s' AND b.id = '%s'" +
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

    /**
     * This endpoint is to check if an actor exists in the database.
     * If the actor does not exist in the database, or an internal server error occurs,
     * a string containing response code is returned.
     * Otherwise, a string containing the response body JSONObject is returned.
     * <p>Response body string format example:</p>
     * <p><code>
     *     { "actorId": "nm1111891", "name": "John Doe", "movies": ["nm8911231", "nm1991341", "nm2005431"]}
     * </code></p>
     * @param actorId The actorId of the actor in question.
     * @return Return a string containing the response body JSONObject if the relationship was successfully added,
     * "404" if the actor or movie does not exist, and "500" if save or add was unsuccessful (Java Exception Thrown).
     * @see ReqHandler#getActor(HttpExchange) getActor(HttpExchange) in ReqHandler for example usage
     */
    public String getActor(String actorId) {
        String obj = "";

        try(Session session = driver.session()){
            Transaction tx = session.beginTransaction();

            if (!actorExists(actorId)) {
//                System.out.println("Neo4jDAO: getActor():  " + actorId + "does not exist");
                return "404";
            }
            else {
//                System.out.println("Neo4jDAO: getActor(): Getting actor with id " + actorId);
                String query = ("MATCH (n:actor {id: '%s'})" +
                        " RETURN collect({actorId: n.id, name: n.Name, movies: [(n)-->(m:movie) | m.id]})[0] AS obj").formatted(actorId);

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

    /**
     * This endpoint is to check if a movie exists in the database.
     * If the movie does not exist in the database, or an internal server error occurs,
     * a string containing response code is returned.
     * Otherwise, a string containing the response body JSONObject is returned.
     * <p>Response body string format example:</p>
     * <p><code>
     *     { "movieId": "nm1111891", "name": "Groundhog Day", "actors": ["nm8911231", "nm1991341", "nm2005431"]}
     * </code></p>
     * @param movieId The movieId of the actor in question.
     * @return Return a string containing the response body JSONObject if the query was successful,
     * "404" if the movie does not exist, and "500" if save or add was unsuccessful (Java Exception Thrown).
     * @see ReqHandler#getMovie(HttpExchange)  getMovie(HttpExchange) in ReqHandler for example usage
     */
    public String getMovie(String movieId) {
        String obj = "";

        try(Session session = driver.session()){
            Transaction tx = session.beginTransaction();

            if (!movieExists(movieId)) {
//                System.out.println("Neo4jDAO: getMovie():  " + movieId + "does not exist");
                return "404";
            }
            else {
//                System.out.println("Neo4jDAO: getActor(): Getting movie with id " + movieId);
                String query = ("MATCH (n:movie {id: '%s'})" +
                        " RETURN collect({movieId: n.id, name: n.Name, actors: [(m:actor)-->(n) | m.id]})[0] AS obj").formatted(movieId);

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

    /**
     * This endpoint is to check if a relationship between an actor and a movie exists in the database.
     * If the relationship does not exist in the database, or an internal server error occurs,
     * a string containing response code is returned.
     * Otherwise, a string containing the response body JSONObject is returned.
     * <p>Response body string format example:</p>
     * <p><code>
     *     { "actorId": "nm1111891", "movieId": "nm234923", "hasRelationship": false }
     * </code></p>
     * @param actorId The actorId of the actor in question.
     * @param movieId The movieId of the movie in question.
     * @return Return a string containing the response body JSONObject if the query was successful,
     * "404" if the actor or movie does not exist, and "500" if save or add was unsuccessful (Java Exception Thrown).
     * @see ReqHandler#hasRelationship(HttpExchange)  hasRelationship(HttpExchange) in ReqHandler for example usage
     */
    public String hasRelationship(String actorId, String movieId) {
        String obj = "";

        try(Session session = driver.session()){
            Transaction tx = session.beginTransaction();

            if (!actorExists(actorId) || !movieExists(movieId)) {
//                System.out.println("Neo4jDAO: hasRelationship():  " + actorId + " or " + movieId + "does not exist");
                return "404";
            }
            else {
//                System.out.println("Neo4jDAO: hasRelationship(): Checking for relationship between " + actorId + " and " + movieId);
                String query = ("""
                        OPTIONAL MATCH (n: actor{id: '%1$s'})-[r:ACTED_IN]->(m: movie{id: '%2$s'}) 
                        WITH COUNT(r) > 0 AS hasRel 
                        MATCH (n: actor{id: '%1$s'}), (m: movie{id:  '%2$s'}) 
                        RETURN collect({ actorId: n.id, movieId: m.id, hasRelationship: hasRel})[0] AS obj""").formatted(actorId, movieId);
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

    /**
     * This endpoint is to check the bacon number of an actor.
     * If the path does not exist in the database, or an internal server error occurs,
     * a string containing response code is returned.
     * Otherwise, a string containing the response body JSONObject is returned.
     * If the actor is Kevin Bacon, the baconNumber is 0.
     * <p>Response body string format example:</p>
     * <p><code>
     *     { "baconNumber": 3 }
     * </code></p>
     * @param actorId The actorId of the actor in question.
     * @return Return a string containing the response body JSONObject if the query was successful,
     * "404" if the actor or a path to Kevin Bacon does not exist, and "500" if save or add was unsuccessful (Java Exception Thrown).
     * @see ReqHandler#computeBaconNumber(HttpExchange) computeBaconNumber(HttpExchange) in ReqHandler for example usage
     */
    public String computeBaconNumber(String actorId) {
        String kevinBaconId = "nm0000102";
        String obj = "";

        try(Session session = driver.session()){
            Transaction tx = session.beginTransaction();

            if (!actorExists(kevinBaconId) || !actorExists(actorId)) {
//                System.out.println("Neo4jDAO: computeBaconNumber():  Kevin Bacon or " + actorId + " does not exist");
                return "404";
            }
            else if (actorId.equals(kevinBaconId)) {
                return new JSONObject().put("baconNumber", 0).toString();
            }
            else {
//                System.out.println("Neo4jDAO: computeBaconNumber(): Computing bacon number for " + actorId);
                String query = ("MATCH (a:actor {id: '%s'} ), (b:actor {id: '%s'}),\n" +
                                    "p = shortestPath((a)-[:ACTED_IN*]-(b))\n" +
                                    "WITH (length(p) / 2) as baconNumber\n" +
                                    "RETURN collect({ baconNumber: baconNumber })[0] as obj").formatted(actorId, kevinBaconId);
                Result res = tx.run(query);

                if (res.hasNext()) {
                    org.neo4j.driver.Record record = res.next();
                    obj = record.values().get(0).toString();
                    if (obj.equals("NULL")) {
//                        System.out.println("Neo4jDAO: computeBaconNumber():  There is no path from Kevin Bacon to " + actorId);
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

    /**
     * This endpoint returns the shortest Bacon Path in order from the actor given to Kevin Bacon.
     * If the actor or path does not exist in the database, or an internal server error occurs,
     * a string containing response code is returned.
     * Otherwise, a string containing the response body JSONObject is returned.
     * If the actor is Kevin Bacon, the list of interchanging actors and movies is just Kevin Bacon's id.
     * <p>Response body string format example:</p>
     * <p><code>
     *     { "baconPath": [ "nm1991271", "nm9112231", "nm9191136", "nm9894331", "nm0000102" ] }
     * </code></p>
     * @param actorId The actorId of the actor in question.
     * @return Return a string containing the response body JSONObject of with a list of interchanging actors and movies beginning with the
     * inputted actorId and ending with Kevin Bacon’s actorId if the query was successful,
     * "404" if the actor or a path to Kevin Bacon does not exist, and "500" if save or add was unsuccessful (Java Exception Thrown).
     * @see ReqHandler#computeBaconPath(HttpExchange)  computeBaconPath(HttpExchange) in ReqHandler for example usage
     */
    public String computeBaconPath(String actorId) {
        String kevinBaconId = "nm0000102";
        String obj = "";

        try(Session session = driver.session()){
            Transaction tx = session.beginTransaction();

            if (!actorExists(kevinBaconId) || !actorExists(actorId)) {
//                System.out.println("Neo4jDAO: computeBaconPath():  Kevin Bacon or " + actorId + " does not exist");
                return "404";
            }
            else if (actorId.equals(kevinBaconId)) {
                return new JSONObject().put("baconPath", new String[] {kevinBaconId}).toString();
            }
            else {
//                System.out.println("Neo4jDAO: computeBaconPath(): Computing bacon number for " + actorId);
                String query = ("""
                        MATCH (a:actor {id: '%s'} ),   (b:actor {id: '%s'}),
                            p = shortestPath((a)-[:ACTED_IN*]-(b))
                            WITH p as p, (length(p) / 2) as baconNumber, nodes(p) as n
                            RETURN collect({baconPath: [a in n | CASE WHEN a:actor THEN a.id ELSE a.id END]})[0] as obj""").formatted(actorId, kevinBaconId);
                Result res = tx.run(query);

                if (res.hasNext()) {
                    org.neo4j.driver.Record record = res.next();
                    obj = record.values().get(0).toString();
                    if (obj.equals("NULL")) {
//                        System.out.println("Neo4jDAO: computeBaconNumber():  There is no path from Kevin Bacon to " + actorId);
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
