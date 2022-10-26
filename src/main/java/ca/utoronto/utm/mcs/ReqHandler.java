package ca.utoronto.utm.mcs;

import java.io.IOException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONException;
import org.json.JSONObject;

import javax.inject.Inject;

public class ReqHandler implements HttpHandler {

    // TODO Complete This Class

    public Neo4jDAO dao;

    @Inject
    public ReqHandler(Neo4jDAO dao) {
        this.dao = dao;
    }

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
                exchange.sendResponseHeaders(400, -1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleGet(HttpExchange exchange) throws IOException, JSONException {

        String endPoint = exchange.getRequestURI().toString();

        System.out.println("ReqHandler: handleGet() Error - NOT IMPLEMENTED");
        exchange.sendResponseHeaders(404, -1);
    }

    public void handlePut(HttpExchange exchange) throws IOException, JSONException {

        String endPoint = exchange.getRequestURI().toString();

        if (endPoint.equals("/api/v1/addActor")) {
            addActor(exchange);
        } else {
            System.out.println("ReqHandler: handlePut() Error");
            exchange.sendResponseHeaders(404, -1);
        }
    }

    public void addActor(HttpExchange r) throws IOException, JSONException {

        String body = Utils.convert(r.getRequestBody());
        JSONObject obj = new JSONObject(body);

        int status = 400;

        if (obj.has("name") && obj.has("actorId")) {

            String name, actorId;

            name = obj.getString("name");
            actorId = obj.getString("actorId");
            status = dao.addActor(name, actorId);
            r.sendResponseHeaders(status, -1);

        } else {
            r.sendResponseHeaders(status, -1);
        }
    }
}