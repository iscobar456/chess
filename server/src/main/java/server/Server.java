package server;

import io.javalin.*;

public class Server {

    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        javalin.post("/user", ctx -> {

        });
        javalin.post("/session", ctx -> {

        });
        javalin.delete("/session", ctx -> {

        });
        javalin.get("/game", ctx -> {

        });
        javalin.post("/game", ctx -> {

        });
        javalin.put("/game", ctx -> {

        });
        javalin.delete("/db", ctx -> {

        });

    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
