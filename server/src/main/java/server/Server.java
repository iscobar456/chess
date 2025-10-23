package server;

import chess.ChessGame;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import dataaccess.AuthData;
import dataaccess.GameData;
import dataaccess.UserData;
import io.javalin.*;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.HttpResponseException;

import service.Service;

import java.util.ArrayList;

public class Server {

    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));
        Service service = new Service();
        Gson gson = new Gson();

        javalin.post("/user", ctx -> {
            UserData registerBody = gson.fromJson(ctx.body(), UserData.class);
            AuthData result = service.register(registerBody.username(), registerBody.password(), registerBody.email());
            ctx.json(gson.toJson(result));
        });
        javalin.post("/session", ctx -> {
            LoginBody loginBody = gson.fromJson(ctx.body(), LoginBody.class);
            AuthData result = service.login(loginBody.username(), loginBody.password());
            ctx.json(gson.toJson(result));
        });
        javalin.delete("/session", ctx -> {
            service.validateToken(ctx.header("authToken"));
            service.deleteAuth(ctx.header("authToken"));
        });
        javalin.get("/game", ctx -> {
            service.validateToken(ctx.header("authToken"));
            ArrayList<GameData> games = service.getGames();
            ctx.json(gson.toJson(new GamesResponse(games)));
        });
        javalin.post("/game", ctx -> {
            service.validateToken(ctx.header("authToken"));
            JoinGameBody joinGameBody = gson.fromJson(ctx.body(), JoinGameBody.class);
            service.joinGame(joinGameBody.gameID(), joinGameBody.username(), joinGameBody.playerColor());
        });
        javalin.put("/game", ctx -> {

        });
        javalin.delete("/db", ctx -> {

        });

        javalin.exception(HttpResponseException.class, (e,ctx) -> {
            ctx.status(e.getStatus());
            String errorString = String.format("Error: %s", e.getMessage());
            ctx.json(gson.toJson(new ErrorResponse(errorString)));
        });

        javalin.exception(JsonParseException.class, (e,ctx) -> {
            throw new BadRequestResponse("bad request");
        });

        javalin.exception(JsonSyntaxException.class, (e,ctx) -> {
            throw new BadRequestResponse("bad request");
        });
    }

    record LoginBody(String username, String password) {}
    record JoinGameBody(int gameID, String username, ChessGame.TeamColor playerColor) {}
    record GamesResponse(ArrayList<GameData> games) {}
    record ErrorResponse(String message) {}

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
