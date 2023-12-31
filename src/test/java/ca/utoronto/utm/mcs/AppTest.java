package ca.utoronto.utm.mcs;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

// TODO Please Write Your Tests For CI/CD In This Class. You will see
// these tests pass/fail on github under github actions.
public class AppTest {

    public HttpResponse<String> sendPutReq(HttpClient client, String endPoint, String body) throws URISyntaxException, IOException, InterruptedException {
        return client.send(HttpRequest.newBuilder()
                .uri(new URI("http://localhost:8080/api/v1/" + endPoint))
                .PUT(HttpRequest.BodyPublishers.ofString(body))
                .build(), HttpResponse.BodyHandlers.ofString());
    }

    public HttpResponse<String> sendGetReq(HttpClient client, String endPoint, String body) throws URISyntaxException, IOException, InterruptedException {
        return client.send(HttpRequest.newBuilder()
                .uri(new URI("http://localhost:8080/api/v1/" + endPoint))
                .method("GET", HttpRequest.BodyPublishers.ofString(body))
                .build(), HttpResponse.BodyHandlers.ofString());
    }

    @Test
    public void exampleTest() {
        assertTrue(true);
    }

    @Test
    public void addActorPass() throws IOException, URISyntaxException, InterruptedException {

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = sendPutReq(client, "addActor", "{ \"name\": \"John Pass\", \"actorId\": \"actorpass\" }");

//        System.out.println("addActorPass: The response status is " + response.statusCode());

        assertTrue(response.statusCode() == 200);
    }

    // Test for duplicate actor, invalid format, no response body (Response status 400, 400)
    @Test
    public void addActorFail() throws JSONException, IOException, URISyntaxException, InterruptedException {

        HttpClient client = HttpClient.newHttpClient();
        sendPutReq(client, "addActor", "{ \"name\": \"John Fail 1\", \"actorId\": \"actorfail\" }");
        HttpResponse<String> response = sendPutReq(client,"addActor", "{ \"name\": \"John Fail 2\", \"actorId\": \"actorfail\" }");

        HttpResponse<String> response2 = sendPutReq(client, "addActor", "{ \"name\": \"John Fail 1\" }");

//        System.out.println("addActorFail: The response status is " + response.statusCode());

        assertTrue(response.statusCode() == 400 && response2.statusCode() == 400 && response.body().isEmpty() && response2.body().isEmpty());
    }

    @Test
    public void addMoviePass() throws IOException, URISyntaxException, InterruptedException {

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = sendPutReq(client, "addMovie", "{ \"name\": \"Parasite Pass\", \"movieId\": \"moviepass\" }");

//        System.out.println("addMoviePass: The response status is " + response.statusCode());

        assertTrue(response.statusCode() == 200);
    }

    // Test for duplicate movie, invalid format, no response body (Response status 400, 400)
    @Test
    public void addMovieFail() throws IOException, URISyntaxException, InterruptedException {

        HttpClient client = HttpClient.newHttpClient();
        sendPutReq(client, "addMovie", "{ \"name\": \"Parasite Fail 1\", \"movieId\": \"moviefail\" }");

        HttpResponse<String> response = sendPutReq(client, "addMovie", "{ \"name\": \"Parasite Fail 2\", \"movieId\": \"moviefail\" }");
        HttpResponse<String> response2 = sendPutReq(client, "addMovie", "{ \"name\": \"Parasite Fail 2\" } ");

//        System.out.println();

        assertTrue(response.statusCode() == 400 && response2.statusCode() == 400 && response.body().isEmpty() && response2.body().isEmpty());
    }

    // realtionship check
    @Test
    public void addRelationshipPass() throws IOException, URISyntaxException, InterruptedException {

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest requestAddActor = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:8080/api/v1/addActor"))
                .PUT(HttpRequest.BodyPublishers.ofString("{ \"name\": \"Actor Relationship Pass\", \"actorId\": \"arp\" }"))
                .build();

        HttpRequest requestAddMovie = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:8080/api/v1/addMovie"))
                .PUT(HttpRequest.BodyPublishers.ofString("{ \"name\": \"Movie Relationship Pass\", \"movieId\": \"mrp\" }"))
                .build();

        HttpRequest requestAddRelationship = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:8080/api/v1/addRelationship"))
                .PUT(HttpRequest.BodyPublishers.ofString("{ \"actorId\": \"arp\", \"movieId\": \"mrp\" }"))
                .build();

        client.send(requestAddActor, HttpResponse.BodyHandlers.ofString());
        client.send(requestAddMovie, HttpResponse.BodyHandlers.ofString());

        HttpResponse<String> response = client.send(requestAddRelationship, HttpResponse.BodyHandlers.ofString());
//        System.out.println("addRelationshipPass: The response status is " + response.statusCode());

        assertTrue(response.statusCode() == 200);
    }

    // Test for duplicate relationship (Response status 400)
    @Test
    public void addRelationshipFail() throws IOException, URISyntaxException, InterruptedException {

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest requestAddActor = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:8080/api/v1/addActor"))
                .PUT(HttpRequest.BodyPublishers.ofString("{ \"name\": \"Actor Relationship Fail\", \"actorId\": \"arf\" }"))
                .build();

        HttpRequest requestAddMovie = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:8080/api/v1/addMovie"))
                .PUT(HttpRequest.BodyPublishers.ofString("{ \"name\": \"Movie Relationship Fail\", \"movieId\": \"mrf\" }"))
                .build();

        HttpRequest requestAddRelationship = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:8080/api/v1/addRelationship"))
                .PUT(HttpRequest.BodyPublishers.ofString("{ \"actorId\": \"arf\", \"movieId\": \"mrf\" }"))
                .build();

        HttpRequest requestAddDuplicateRelationship = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:8080/api/v1/addRelationship"))
                .PUT(HttpRequest.BodyPublishers.ofString("{ \"actorId\": \"arf\", \"movieId\": \"mrf\" }"))
                .build();

        client.send(requestAddActor, HttpResponse.BodyHandlers.ofString());
        client.send(requestAddMovie, HttpResponse.BodyHandlers.ofString());
        client.send(requestAddRelationship, HttpResponse.BodyHandlers.ofString());

        HttpResponse<String> response = client.send(requestAddDuplicateRelationship, HttpResponse.BodyHandlers.ofString());
//        System.out.println("addRelationshipFail: The response status is " + response.statusCode());

        assertTrue(response.statusCode() == 400);
    }

    @Test
    public void getActorPass() throws IOException, URISyntaxException, InterruptedException, JSONException {

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest requestAddActor = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:8080/api/v1/addActor"))
                .PUT(HttpRequest.BodyPublishers.ofString("{ \"name\": \"Actor GetActor Pass\", \"actorId\": \"aap\" }"))
                .build();

        HttpRequest requestAddMovie = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:8080/api/v1/addMovie"))
                .PUT(HttpRequest.BodyPublishers.ofString("{ \"name\": \"Movie GetActor Pass\", \"movieId\": \"map\" }"))
                .build();

        HttpRequest requestAddRelationship = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:8080/api/v1/addRelationship"))
                .PUT(HttpRequest.BodyPublishers.ofString("{ \"actorId\": \"aap\", \"movieId\": \"map\" }"))
                .build();

        HttpRequest requestGetActor = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:8080/api/v1/getActor"))
                .method("GET", HttpRequest.BodyPublishers.ofString("{ \"actorId\": \"aap\" }"))
                .build();

        client.send(requestAddActor, HttpResponse.BodyHandlers.ofString());
        client.send(requestAddMovie, HttpResponse.BodyHandlers.ofString());
        client.send(requestAddRelationship, HttpResponse.BodyHandlers.ofString());

        HttpResponse<String> response = client.send(requestGetActor, HttpResponse.BodyHandlers.ofString());
//        System.out.println("getActorPass: The response status is " + response.statusCode() + " with response body " + response.body());

        JSONObject obj = null;
        try {
            obj = new JSONObject(response.body());
        }
        catch (JSONException e)
        {
            assertTrue(false);
        }

        boolean correctBody = false;

        if (obj == null || !obj.has("actorId") | !obj.has("name") || !obj.has("movies"))
            correctBody = false;
        else if (obj.getString("actorId").equals("aap") && obj.getString("name").equals("Actor GetActor Pass") && obj.getJSONArray("movies").toString().equals("[\"map\"]"))
            correctBody = true;

        assertTrue(response.statusCode() == 200 && correctBody);
    }

    // Test for actor does not exist and no response body (Response status 404)
    @Test
    public void getActorFail() throws IOException, URISyntaxException, InterruptedException, JSONException {

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest requestGetActor = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:8080/api/v1/getActor"))
                .method("GET", HttpRequest.BodyPublishers.ofString("{ \"actorId\": \"aaf\" }"))
                .build();

        HttpResponse<String> response = client.send(requestGetActor, HttpResponse.BodyHandlers.ofString());
//        System.out.println("getActorFail: The response status is " + response.statusCode() + " with response body " + response.body());

        assertTrue(response.statusCode() == 404 && response.body().isEmpty());
    }

    @Test
    public void getMoviePass() throws IOException, URISyntaxException, InterruptedException, JSONException {

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest requestAddActor = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:8080/api/v1/addActor"))
                .PUT(HttpRequest.BodyPublishers.ofString("{ \"name\": \"Actor GetMovie Pass\", \"actorId\": \"amp\" }"))
                .build();

        HttpRequest requestAddMovie = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:8080/api/v1/addMovie"))
                .PUT(HttpRequest.BodyPublishers.ofString("{ \"name\": \"Movie GetMovie Pass\", \"movieId\": \"mmp\" }"))
                .build();

        HttpRequest requestAddRelationship = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:8080/api/v1/addRelationship"))
                .PUT(HttpRequest.BodyPublishers.ofString("{ \"actorId\": \"amp\", \"movieId\": \"mmp\" }"))
                .build();

        HttpRequest requestGetMovie = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:8080/api/v1/getMovie"))
                .method("GET", HttpRequest.BodyPublishers.ofString("{ \"movieId\": \"mmp\" }"))
                .build();

        client.send(requestAddActor, HttpResponse.BodyHandlers.ofString());
        client.send(requestAddMovie, HttpResponse.BodyHandlers.ofString());
        client.send(requestAddRelationship, HttpResponse.BodyHandlers.ofString());

        HttpResponse<String> response = client.send(requestGetMovie, HttpResponse.BodyHandlers.ofString());
//        System.out.println("getMoviePass: The response status is " + response.statusCode() + " with response body " + response.body());

        JSONObject obj = null;
        try {
            obj = new JSONObject(response.body());
        }
        catch (JSONException e)
        {
            assertTrue(false);
        }

        boolean correctBody = false;

        if (obj == null || !obj.has("movieId") | !obj.has("name") || !obj.has("actors"))
            correctBody = false;
        else if (obj.getString("movieId").equals("mmp") && obj.getString("name").equals("Movie GetMovie Pass") && obj.getJSONArray("actors").toString().equals("[\"amp\"]"))
            correctBody = true;

        assertTrue(response.statusCode() == 200 && correctBody);
    }

    // Test for movie does not exist and no response body(Response status 404)
    @Test
    public void getMovieFail() throws IOException, URISyntaxException, InterruptedException, JSONException {

        HttpClient client = HttpClient.newHttpClient();

        HttpResponse<String> response = sendGetReq(client, "getMovie", "{ \"movieId\": \"mmf\" }");
//        System.out.println("getMovieFail: The response status is " + response.statusCode() + " with response body " + response.body());

        assertTrue(response.statusCode() == 404 && response.body().isEmpty());
    }

    @Test
    public void hasRelationshipPass() throws IOException, URISyntaxException, InterruptedException, JSONException {

        HttpClient client = HttpClient.newHttpClient();
        sendPutReq(client, "addActor", "{ \"name\": \"Actor HasRelationship Pass\", \"actorId\": \"ahrp\" }");
        sendPutReq(client, "addMovie", "{ \"name\": \"Movie HasRelationship Pass\", \"movieId\": \"mhrp\" }");
        sendPutReq(client, "addRelationship", "{ \"actorId\": \"ahrp\", \"movieId\": \"mhrp\" }");

        HttpResponse<String> response = sendGetReq(client, "hasRelationship", "{ \"actorId\": \"ahrp\", \"movieId\": \"mhrp\" }");
//        System.out.println("hasRelationshipPass: The response status is " + response.statusCode() + " with response body " + response.body());

        JSONObject obj = null;
        try {
            obj = new JSONObject(response.body());
        }
        catch (JSONException e)
        {
            assertTrue(false);
        }

        boolean correctBody = false;

        if (obj == null || !obj.has("actorId") | !obj.has("movieId") || !obj.has("hasRelationship"))
            correctBody = false;
        else if (obj.getString("actorId").equals("ahrp") && obj.getString("movieId").equals("mhrp") && obj.getBoolean("hasRelationship") == true)
            correctBody = true;

        assertTrue(response.statusCode() == 200 && correctBody);
    }

    // Test for actor or movie does not exist and no response body (Response status 404)
    @Test
    public void hasRelationshipFail() throws IOException, URISyntaxException, InterruptedException, JSONException {

        HttpClient client = HttpClient.newHttpClient();
        sendPutReq(client, "addActor", "{ \"name\": \"Actor HasRelationship Pass\", \"actorId\": \"ahrf\" }");

        HttpResponse<String> response = sendGetReq(client, "hasRelationship", "{ \"actorId\": \"ahrf\", \"movieId\": \"mhrf\" }");
//        System.out.println("hasRelationshipFail: The response status is " + response.statusCode() + " with response body " + response.body());

        assertTrue(response.statusCode() == 404 && response.body().isEmpty());
    }

    @Test
    public void computeBaconNumberPass() throws IOException, URISyntaxException, InterruptedException, JSONException {

        HttpClient client = HttpClient.newHttpClient();

        sendPutReq(client, "addActor", "{ \"name\": \"Kevin Bacon\", \"actorId\": \"nm0000102\" }");
        sendPutReq(client, "addActor", "{ \"name\": \"Actor ComputeBaconNum Pass 1\", \"actorId\": \"acbnp1\" }");
        sendPutReq(client, "addActor", "{ \"name\": \"Actor ComputeBaconNum Pass 2\", \"actorId\": \"acbnp2\" }");
        sendPutReq(client, "addActor", "{ \"name\": \"Actor ComputeBaconNum Pass 3\", \"actorId\": \"acbnp3\" }");
        sendPutReq(client, "addActor", "{ \"name\": \"Actor ComputeBaconNum Pass 4\", \"actorId\": \"acbnp4\" }");
        sendPutReq(client, "addMovie", "{ \"name\": \"Movie ComputeBaconNum Pass 1\", \"movieId\": \"mcbnp1\" }");
        sendPutReq(client, "addMovie", "{ \"name\": \"Movie ComputeBaconNum Pass 2\", \"movieId\": \"mcbnp2\" }");
        sendPutReq(client, "addMovie", "{ \"name\": \"Movie ComputeBaconNum Pass 3\", \"movieId\": \"mcbnp3\" }");
        sendPutReq(client, "addMovie", "{ \"name\": \"Movie ComputeBaconNum Pass 4\", \"movieId\": \"mcbnp4\" }");
        sendPutReq(client, "addRelationship", "{ \"actorId\": \"acbnp1\", \"movieId\": \"mcbnp1\" }");
        sendPutReq(client, "addRelationship", "{ \"actorId\": \"acbnp2\", \"movieId\": \"mcbnp1\" }");
        sendPutReq(client, "addRelationship", "{ \"actorId\": \"acbnp2\", \"movieId\": \"mcbnp2\" }");
        sendPutReq(client, "addRelationship", "{ \"actorId\": \"acbnp3\", \"movieId\": \"mcbnp2\" }");
        sendPutReq(client, "addRelationship", "{ \"actorId\": \"acbnp3\", \"movieId\": \"mcbnp3\" }");
        sendPutReq(client, "addRelationship", "{ \"actorId\": \"acbnp4\", \"movieId\": \"mcbnp3\" }");
        sendPutReq(client, "addRelationship", "{ \"actorId\": \"acbnp4\", \"movieId\": \"mcbnp4\" }");
        sendPutReq(client, "addRelationship", "{ \"actorId\": \"acbnp1\", \"movieId\": \"mcbnp4\" }");
        sendPutReq(client, "addRelationship", "{ \"actorId\": \"nm0000102\", \"movieId\": \"mcbnp3\" }");

        HttpResponse<String> response = sendGetReq(client, "computeBaconNumber", "{ \"actorId\": \"acbnp1\" }");
//        System.out.println("computeBaconNumberPass: The response status is " + response.statusCode() + " with response body " + response.body());

        JSONObject obj = null;
        try {
            obj = new JSONObject(response.body());
        }
        catch (JSONException e)
        {
            assertTrue(false);
        }

        boolean correctBody = false;

        if (obj == null || !obj.has("baconNumber"))
            correctBody = false;
        else if (obj.getInt("baconNumber") == 2)
            correctBody = true;

        assertTrue(response.statusCode() == 200 && correctBody);
    }

    // Test for actor exists, path does not exist, no response body (Response status 404)
    @Test
    public void computeBaconNumberFail() throws IOException, URISyntaxException, InterruptedException, JSONException {

        HttpClient client = HttpClient.newHttpClient();

        sendPutReq(client, "addActor", "{ \"name\": \"Kevin Bacon\", \"actorId\": \"nm0000102\" }");
        sendPutReq(client, "addActor", "{ \"name\": \"Actor ComputeBaconNum Fail\", \"actorId\": \"acbnf1\" }");

        HttpResponse<String> response = sendGetReq(client, "computeBaconNumber", "{ \"actorId\": \"acbnf1\" }");
//        System.out.println("computeBaconNumberFail: The response status is " + response.statusCode() + " with response body " + response.body());

        assertTrue(response.statusCode() == 404 && response.body().isEmpty());
    }

    @Test
    public void computeBaconPathPass() throws IOException, URISyntaxException, InterruptedException, JSONException {

        HttpClient client = HttpClient.newHttpClient();

        sendPutReq(client, "addActor", "{ \"name\": \"Kevin Bacon\", \"actorId\": \"nm0000102\" }");
        sendPutReq(client, "addActor", "{ \"name\": \"Actor ComputeBaconNum Pass 1\", \"actorId\": \"acbpp1\" }");
        sendPutReq(client, "addActor", "{ \"name\": \"Actor ComputeBaconNum Pass 2\", \"actorId\": \"acbpp2\" }");
        sendPutReq(client, "addActor", "{ \"name\": \"Actor ComputeBaconNum Pass 3\", \"actorId\": \"acbpp3\" }");
        sendPutReq(client, "addActor", "{ \"name\": \"Actor ComputeBaconNum Pass 4\", \"actorId\": \"acbpp4\" }");
        sendPutReq(client, "addMovie", "{ \"name\": \"Movie ComputeBaconNum Pass 1\", \"movieId\": \"mcbpp1\" }");
        sendPutReq(client, "addMovie", "{ \"name\": \"Movie ComputeBaconNum Pass 2\", \"movieId\": \"mcbpp2\" }");
        sendPutReq(client, "addMovie", "{ \"name\": \"Movie ComputeBaconNum Pass 3\", \"movieId\": \"mcbpp3\" }");
        sendPutReq(client, "addMovie", "{ \"name\": \"Movie ComputeBaconNum Pass 4\", \"movieId\": \"mcbpp4\" }");
        sendPutReq(client, "addRelationship", "{ \"actorId\": \"acbpp1\", \"movieId\": \"mcbpp1\" }");
        sendPutReq(client, "addRelationship", "{ \"actorId\": \"acbpp2\", \"movieId\": \"mcbpp1\" }");
        sendPutReq(client, "addRelationship", "{ \"actorId\": \"acbpp2\", \"movieId\": \"mcbpp2\" }");
        sendPutReq(client, "addRelationship", "{ \"actorId\": \"acbpp3\", \"movieId\": \"mcbpp2\" }");
        sendPutReq(client, "addRelationship", "{ \"actorId\": \"acbpp3\", \"movieId\": \"mcbpp3\" }");
        sendPutReq(client, "addRelationship", "{ \"actorId\": \"acbpp4\", \"movieId\": \"mcbpp3\" }");
        sendPutReq(client, "addRelationship", "{ \"actorId\": \"acbpp4\", \"movieId\": \"mcbpp4\" }");
        sendPutReq(client, "addRelationship", "{ \"actorId\": \"acbpp1\", \"movieId\": \"mcbpp4\" }");
        sendPutReq(client, "addRelationship", "{ \"actorId\": \"nm0000102\", \"movieId\": \"mcbpp3\" }");

        HttpResponse<String> response = sendGetReq(client, "computeBaconPath", "{ \"actorId\": \"acbpp1\" }");
//        System.out.println("computeBaconPathPass: The response status is " + response.statusCode() + " with response body " + response.body());

        JSONObject obj = null;
        try {
            obj = new JSONObject(response.body());
        }
        catch (JSONException e)
        {
            assertTrue(false);
        }

        boolean correctBody = false;
        ArrayList<String> correctPath = new ArrayList<String>();
        Collections.addAll(correctPath, "acbpp1", "mcbpp1", "acbpp2", "mcbpp2", "nm0000102");

        if (obj == null || !obj.has("baconPath"))
            correctBody = false;
        else if (obj.get("baconPath").toString().equals("['acbpp1', 'mcbpp4', 'acbpp4', 'mcbpp3', 'nm0000102']}"));
            correctBody = true;

        assertTrue(response.statusCode() == 200 && correctBody);
    }

    // Test for actor does not exist and no response body (Response status 404)
    @Test
    public void computeBaconPathFail() throws IOException, URISyntaxException, InterruptedException, JSONException {

        HttpClient client = HttpClient.newHttpClient();

        sendPutReq(client, "addActor", "{ \"name\": \"Kevin Bacon\", \"actorId\": \"nm0000102\" }");
        sendPutReq(client, "addActor", "{ \"name\": \"Actor ComputeBaconNum Fail\", \"actorId\": \"acbpf1\" }");

        HttpResponse<String> response = sendGetReq(client, "computeBaconNumber", "{ \"actorId\": \"acbpf1\" }");
//        System.out.println("computeBaconPathFail: The response status is " + response.statusCode() + " with response body " + response.body());

        assertTrue(response.statusCode() == 404 && response.body().isEmpty());
    }

    // ********** EDGE CASES TESTS **********

    //  actor with no movies (200, empty list of movies)
    @Test
    public void getActorNoMovies() throws IOException, URISyntaxException, InterruptedException, JSONException {

        HttpClient client = HttpClient.newHttpClient();
        sendPutReq(client, "addActor", "{ \"name\": \"Bad Actor\", \"actorId\": \"badactor\" }");
        HttpResponse<String> response = sendGetReq(client, "getActor", "{ \"actorId\": \"badactor\" }");

        JSONObject obj = null;
        try {
            obj = new JSONObject(response.body());
        }
        catch (JSONException e)
        {
            assertTrue(false);
        }

        boolean correctBody = false;

        if (obj == null || !obj.has("actorId") | !obj.has("name") || !obj.has("movies"))
            correctBody = false;
        else if (obj.getString("actorId").equals("badactor") && obj.getString("name").equals("Bad Actor") && obj.getJSONArray("movies").toString().equals("[]"))
            correctBody = true;


        assertTrue(response.statusCode() == 200 && correctBody);
    }

    //  movie with no actors (200, empty list of actors)
    @Test
    public void getMovieNoActors() throws IOException, URISyntaxException, InterruptedException, JSONException {

        HttpClient client = HttpClient.newHttpClient();
        sendPutReq(client, "addMovie", "{ \"name\": \"Bad Movie\", \"movieId\": \"badmovie\" }");
        HttpResponse<String> response = sendGetReq(client, "getMovie", "{ \"movieId\": \"badmovie\" }");

        JSONObject obj = null;
        try {
            obj = new JSONObject(response.body());
        }
        catch (JSONException e)
        {
            assertTrue(false);
        }

        boolean correctBody = false;

        if (obj == null || !obj.has("movieId") | !obj.has("name") || !obj.has("actors"))
            correctBody = false;
        else if (obj.getString("movieId").equals("badmovie") && obj.getString("name").equals("Bad Movie") && obj.getJSONArray("actors").toString().equals("[]"))
            correctBody = true;

        assertTrue(response.statusCode() == 200 && correctBody);
    }

    //  compute Kevin Bacon's bacon number (200, baconNumber : 0)
    @Test
    public void computeBaconNumberKevinBacon() throws IOException, URISyntaxException, InterruptedException, JSONException {

        HttpClient client = HttpClient.newHttpClient();

        sendPutReq(client, "addActor", "{ \"name\": \"Kevin Bacon\", \"actorId\": \"nm0000102\" }");

        HttpResponse<String> response = sendGetReq(client, "computeBaconNumber", "{ \"actorId\": \"nm0000102\" }");

        JSONObject obj = null;
        try {
            obj = new JSONObject(response.body());
        }
        catch (JSONException e)
        {
            assertTrue(false);
        }

        boolean correctBody = false;

        if (obj == null || !obj.has("baconNumber"))
            correctBody = false;
        else if (obj.getInt("baconNumber") == 0)
            correctBody = true;

        assertTrue(response.statusCode() == 200 && correctBody);
    }

    //  compute Kevin Bacon's bacon path (200, baconPath: ["nm0000102"])
    @Test
    public void computeBaconPathKevinBacon() throws IOException, URISyntaxException, InterruptedException, JSONException {

        HttpClient client = HttpClient.newHttpClient();

        sendPutReq(client, "addActor", "{ \"name\": \"Kevin Bacon\", \"actorId\": \"nm0000102\" }");

        HttpResponse<String> response = sendGetReq(client, "computeBaconPath", "{ \"actorId\": \"nm0000102\" }");

        JSONObject obj = null;
        try {
            obj = new JSONObject(response.body());
        }
        catch (JSONException e)
        {
            assertTrue(false);
        }

        boolean correctBody = false;

        if (obj == null || !obj.has("baconPath"))
            correctBody = false;
        else if (obj.get("baconPath").toString() == "[\"nm0000102\"]");
            correctBody = true;

        assertTrue(response.statusCode() == 200 && correctBody);
    }

}
