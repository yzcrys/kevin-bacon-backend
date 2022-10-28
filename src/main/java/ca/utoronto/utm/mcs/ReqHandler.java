package ca.utoronto.utm.mcs;

import java.io.IOException;
import java.io.OutputStream;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONException;
import org.json.JSONObject;

import javax.inject.Inject;

public class ReqHandler implements HttpHandler {

    // TODO Complete This Class

    public Neo4jDAO dao;

    /**
     * A Neo4jDAO object is injected into this constructor to be passed to specific endpoint handlers,
     *  as stated in <code>A1Handout.pdf</code>.
     * @param dao The Neo4jDAO object injected
     */
    @Inject
    public ReqHandler(Neo4jDAO dao) {
        this.dao = dao;
    }

    /**
     * This method handles the exchange by calling <code>handlePut</code> or <code>handleGet</code>
     * depending on the request method.
     * @param exchange the exchange containing the request from the
     *                 client and used to send the response
     * @throws IOException
     */
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String request = exchange.getRequestMethod();

        try {
            if (request.equals("PUT")) {
                handlePut(exchange);

            } else if (request.equals("GET")) {
                handleGet(exchange);
            } else {
                System.out.println("ReqHandler: handle() Error");
                exchange.sendResponseHeaders(500, -1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method handles the GET request by calling the corresponding GET endpoint method in <code>ReqHandler</code>.
     * @param exchange the exchange containing the request from the
     *                 client and used to send the response
     * @throws IOException
     * @throws JSONException
     */
    public void handleGet(HttpExchange exchange) throws IOException, JSONException {

        String endPoint = exchange.getRequestURI().toString();

        switch (endPoint) {
            case "/api/v1/getActor" -> getActor(exchange);
            case "/api/v1/getMovie" -> getMovie(exchange);
            case "/api/v1/hasRelationship" -> hasRelationship(exchange);
            case "/api/v1/computeBaconNumber" -> computeBaconNumber(exchange);
            case "/api/v1/computeBaconPath" -> computeBaconPath(exchange);
            default -> {
                System.out.println("ReqHandler: handleGet() Error");
                exchange.sendResponseHeaders(500, -1);
            }
        }
    }

    /**
     * This method handles the PUT request by calling the corresponding PUT endpoint method in <code>ReqHandler</code>.
     * @param exchange the exchange containing the request from the
     *                 client and used to send the response
     * @throws IOException
     * @throws JSONException
     */
    public void handlePut(HttpExchange exchange) throws IOException, JSONException {

        String endPoint = exchange.getRequestURI().toString();

        switch (endPoint) {
            case "/api/v1/addActor" -> addActor(exchange);
            case "/api/v1/addMovie" -> addMovie(exchange);
            case "/api/v1/addRelationship" -> addRelationship(exchange);
            default -> {
                System.out.println("ReqHandler: handlePut() Error");
                exchange.sendResponseHeaders(404, -1);
            }
        }
    }

    /**
     * This method handles the addActor PUT request by calling the 
     * <code>addActor</code> method in <code>NEO4jDAO</code> and setting
     * the appropriate response code.
     * @param exchange the exchange containing the request from the
     *                 client and used to send the response
     * @throws IOException
     * @throws JSONException
     * @see Neo4jDAO#addActor(String, String) 
     */
    public void addActor(HttpExchange exchange) throws IOException, JSONException {

        int status = 400;

        String body = Utils.convert(exchange.getRequestBody());
        JSONObject obj = null;

        try {
            obj = new JSONObject(body);
        }
        catch (JSONException e)
        {
            status = 500;
        }

        if (obj != null && obj.has("name") && obj.has("actorId")) {

            String name, actorId;

            name = obj.getString("name");
            actorId = obj.getString("actorId");
            status = dao.addActor(name, actorId);
            exchange.sendResponseHeaders(status, -1);

        } else {
            exchange.sendResponseHeaders(status, -1);
        }
    }

    /**
     * This method handles the addMovie PUT request by calling the 
     * <code>addMovie</code> method in <code>NEO4jDAO</code> and setting
     * the appropriate response code.
     * @param exchange the exchange containing the request from the
     *                 client and used to send the response
     * @throws IOException
     * @throws JSONException
     * @see Neo4jDAO#addMovie(String, String) 
     */
    public void addMovie(HttpExchange exchange) throws IOException, JSONException {

        int status = 400;

        String body = Utils.convert(exchange.getRequestBody());
        JSONObject obj = null;

        try {
            obj = new JSONObject(body);
        }
        catch (JSONException e)
        {
            status = 500;
        }

        if (obj != null && obj.has("name") && obj.has("movieId")) {

            String name, movieId;

            name = obj.getString("name");
            movieId = obj.getString("movieId");
            status = dao.addMovie(name, movieId);
            exchange.sendResponseHeaders(status, -1);

        } else {
            exchange.sendResponseHeaders(status, -1);
        }
    }

    /**
     * This method handles the addRelationship PUT request by calling the 
     * <code>addRelationship</code> method in <code>NEO4jDAO</code> and setting
     * the appropriate response code.
     * @param exchange the exchange containing the request from the
     *                 client and used to send the response
     * @throws IOException
     * @throws JSONException
     * @see Neo4jDAO#addRelationship(String, String) 
     */
    public void addRelationship(HttpExchange exchange) throws IOException, JSONException {

        int status = 400;

        String body = Utils.convert(exchange.getRequestBody());
        JSONObject obj = null;

        try {
            obj = new JSONObject(body);
        }
        catch (JSONException e)
        {
            status = 500;
        }

        if (obj != null && obj.has("actorId") && obj.has("movieId")) {

            String actorId, movieId;

            actorId = obj.getString("actorId");
            movieId = obj.getString("movieId");
            status = dao.addRelationship(actorId, movieId);
            exchange.sendResponseHeaders(status, -1);

        } else {
            exchange.sendResponseHeaders(status, -1);
        }
    }

    /**
     * This method handles the getActor GET request by calling the 
     * <code>getActor</code> method in <code>NEO4jDAO</code> and setting
     * the appropriate response code and response body.
     * @param exchange the exchange containing the request from the
     *                 client and used to send the response
     * @throws IOException
     * @throws JSONException
     * @see Neo4jDAO#getActor(String) 
     */
    public void getActor(HttpExchange exchange) throws IOException, JSONException {

        int status = 400;

        String body = Utils.convert(exchange.getRequestBody());
        JSONObject obj = null;

        try {
            obj = new JSONObject(body);
        }
        catch (JSONException e)
        {
            status = 500;
        }

        if (obj != null && obj.has("actorId")) {

            String actorId;
            String res;

            actorId = obj.getString("actorId");
            res = dao.getActor(actorId);

            if (res.length() == 3) {
                status = Integer.parseInt(res);
                exchange.sendResponseHeaders(status, -1);
            }
            else {
                status = 200;
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(status, res.length());
                OutputStream os = exchange.getResponseBody();
                os.write(res.getBytes());
                os.close();
            }
        } else {
            exchange.sendResponseHeaders(status, -1);
        }
    }

    /**
     * This method handles the getMovie GET request by calling the 
     * <code>getMovie</code> method in <code>NEO4jDAO</code> and setting
     * the appropriate response code and response body.
     * @param exchange the exchange containing the request from the
     *                 client and used to send the response
     * @throws IOException
     * @throws JSONException
     * @see Neo4jDAO#getMovie(String) 
     */
    public void getMovie(HttpExchange exchange) throws IOException, JSONException {

        int status = 400;

        String body = Utils.convert(exchange.getRequestBody());
        JSONObject obj = null;

        try {
            obj = new JSONObject(body);
        }
        catch (JSONException e)
        {
            status = 500;
        }

        if (obj != null && obj.has("movieId")) {

            String movieId;
            String res;

            movieId = obj.getString("movieId");
            res = dao.getMovie(movieId);

            if (res.length() == 3) {
                status = Integer.parseInt(res);
                exchange.sendResponseHeaders(status, -1);
            }
            else {
                status = 200;
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(status, res.length());
                OutputStream os = exchange.getResponseBody();
                os.write(res.getBytes());
                os.close();
            }
        } else {
            exchange.sendResponseHeaders(status, -1);
        }
    }

    /**
     * This method handles the hasRelationship GET request by calling the 
     * <code>hasRelationship</code> method in <code>NEO4jDAO</code> and setting
     * the appropriate response code and response body.
     * @param exchange the exchange containing the request from the
     *                 client and used to send the response
     * @throws IOException
     * @throws JSONException
     * @see Neo4jDAO#hasRelationship(String, String) 
     */
    public void hasRelationship(HttpExchange exchange) throws IOException, JSONException {

        int status = 400;

        String body = Utils.convert(exchange.getRequestBody());
        JSONObject obj = null;

        try {
            obj = new JSONObject(body);
        }
        catch (JSONException e)
        {
            status = 500;
        }

        if (obj != null && obj.has("actorId") && obj.has("movieId")) {

            String actorId, movieId;
            String res;

            actorId = obj.getString("actorId");
            movieId = obj.getString("movieId");
            res = dao.hasRelationship(actorId, movieId);

            if (res.length() == 3) {
                status = Integer.parseInt(res);
                exchange.sendResponseHeaders(status, -1);
            }
            else {
                status = 200;
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(status, res.length());
                OutputStream os = exchange.getResponseBody();
                os.write(res.getBytes());
                os.close();
            }
        } else {
            exchange.sendResponseHeaders(status, -1);
        }
    }

    /**
     * This method handles the computeBaconNumber GET request by calling the 
     * <code>computeBaconNumber</code> method in <code>NEO4jDAO</code> and setting
     * the appropriate response code and response body.
     * @param exchange the exchange containing the request from the
     *                 client and used to send the response
     * @throws IOException
     * @throws JSONException
     * @see Neo4jDAO#computeBaconNumber(String) 
     */
    public void computeBaconNumber(HttpExchange exchange) throws IOException, JSONException {

        int status = 400;

        String body = Utils.convert(exchange.getRequestBody());
        JSONObject obj = null;

        try {
            obj = new JSONObject(body);
        }
        catch (JSONException e)
        {
            status = 500;
        }

        if (obj != null && obj.has("actorId")) {

            String actorId;
            String res;

            actorId = obj.getString("actorId");

            res = dao.computeBaconNumber(actorId);

            if (res.length() == 3) {
                status = Integer.parseInt(res);
                exchange.sendResponseHeaders(status, -1);
            }
            else {
                status = 200;
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(status, res.length());
                OutputStream os = exchange.getResponseBody();
                os.write(res.getBytes());
                os.close();
            }
        } else {
            exchange.sendResponseHeaders(status, -1);
        }
    }

    /**
     * This method handles the computeBaconPath GET request by calling the 
     * <code>computeBaconPath</code> method in <code>NEO4jDAO</code> and setting
     * the appropriate response code and response body.
     * @param exchange the exchange containing the request from the
     *                 client and used to send the response
     * @throws IOException
     * @throws JSONException
     * @see Neo4jDAO#computeBaconPath(String)
     */
    public void computeBaconPath(HttpExchange exchange) throws IOException, JSONException {

        int status = 400;

        String body = Utils.convert(exchange.getRequestBody());
        JSONObject obj = null;

        try {
            obj = new JSONObject(body);
        }
        catch (JSONException e)
        {
            status = 500;
        }

        if (obj != null && obj.has("actorId")) {

            String actorId;
            String res;

            actorId = obj.getString("actorId");

            res = dao.computeBaconPath(actorId);

            if (res.length() == 3) {
                status = Integer.parseInt(res);
                exchange.sendResponseHeaders(status, -1);
            }
            else {
                status = 200;
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(status, res.length());
                OutputStream os = exchange.getResponseBody();
                os.write(res.getBytes());
                os.close();
            }
        } else {
            exchange.sendResponseHeaders(status, -1);
        }
    }
}