package server;

import chess.ChessGame;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import dataaccess.*;
import data.GameData;
import io.javalin.*;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.HttpResponseException;

import server.websocket.WebSocketHandler;
import service.Service;

import java.util.ArrayList;

public class Server {

    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));
        DataAccess dataAccess = new SQLDataAccess();
        Service service = new Service(dataAccess);
        Gson gson = new GsonBuilder().serializeNulls().create();

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
            service.validateToken(ctx.header("authorization"));
            service.deleteAuth(ctx.header("authorization"));
        });
        javalin.get("/game", ctx -> {
            service.validateToken(ctx.header("authorization"));
            ArrayList<GameData> games = service.getGames();
            ctx.json(gson.toJson(new GamesResponse(games)));
        });
        javalin.post("/game", ctx -> {
            service.validateToken(ctx.header("authorization"));
            CreateGameBody createGameBody = gson.fromJson(ctx.body(), CreateGameBody.class);
            int gameID = service.createGame(createGameBody.gameName);
            ctx.json(gson.toJson(new CreateGameResponse(gameID)));
        });
        javalin.put("/game", ctx -> {
            String username = service.validateToken(ctx.header("authorization"));
            JoinGameBody joinGameBody = gson.fromJson(ctx.body(), JoinGameBody.class);
            service.joinGame(joinGameBody.gameID(), username, joinGameBody.playerColor());
        });
        javalin.delete("/db", ctx -> {
            service.clear();
        });
        WebSocketHandler wsHandler = new WebSocketHandler(service);
        javalin.ws("/ws", ws -> {
            ws.onConnect(wsHandler);
            ws.onMessage(wsHandler);
            ws.onClose(wsHandler);
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
    record JoinGameBody(int gameID, ChessGame.TeamColor playerColor) {}
    record CreateGameBody(String gameName){}
    record GamesResponse(ArrayList<GameData> games) {}
    record CreateGameResponse(int gameID) {}
    record ErrorResponse(String message) {}

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
