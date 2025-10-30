package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import io.javalin.http.InternalServerErrorResponse;

import java.sql.SQLException;
import java.util.ArrayList;

public class SQLDataAccess implements DataAccess {
    private final Gson gson = new Gson();

    @Override
    public UserData getUser(String username) {
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("SELECT * FROM users WHERE username = ?")) {
                preparedStatement.setString(1, username);
                var result = preparedStatement.executeQuery();
                if (!result.next()) {
                    return null;
                }
                return new UserData(
                        result.getString("username"),
                        result.getString("email"),
                        result.getString("password"));
            }
        } catch (Exception e) {
            throw new InternalServerErrorResponse("internal error");
        }
    }

    @Override
    public void saveUser(UserData data) {
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("INSERT INTO users VALUES (?,?,?)")) {
                preparedStatement.setString(1, data.username());
                preparedStatement.setString(2, data.password());
                preparedStatement.setString(3, data.email());
                preparedStatement.executeUpdate();
            }
        } catch (Exception e) {
            throw new InternalServerErrorResponse("internal error");
        }
    }

    @Override
    public AuthData getAuth(String authToken) {
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("SELECT * FROM auths WHERE authtoken = ?")) {
                preparedStatement.setString(1, authToken);
                var result = preparedStatement.executeQuery();
                if (!result.next()) {
                    return null;
                }
                return new AuthData(result.getString(1), result.getString(2));
            }
        } catch (Exception e) {
            throw new InternalServerErrorResponse("internal error");
        }
    }

    @Override
    public void saveAuth(AuthData data) {
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("INSERT INTO auths VALUES (?,?)")) {
                preparedStatement.setString(1, data.authToken());
                preparedStatement.setString(2, data.username());
                preparedStatement.executeUpdate();
            }
        } catch (Exception e) {
            throw new InternalServerErrorResponse("internal error");
        }
    }

    @Override
    public void deleteAuth(String authToken) {
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("DELETE FROM auths WHERE authtoken = ?")) {
                preparedStatement.setString(1, authToken);
                preparedStatement.executeUpdate();
            }
        } catch (Exception e) {
            throw new InternalServerErrorResponse("internal error");
        }
    }

    @Override
    public ArrayList<GameData> getGames() {
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("SELECT * FROM games")) {
                var result = preparedStatement.executeQuery();
                ArrayList<GameData> games = new ArrayList<>();
                while (result.next()) {
                    games.add(new GameData(
                            result.getInt(1),
                            result.getString(2),
                            result.getString(3),
                            result.getString(4),
                            (ChessGame) gson.fromJson(result.getString(5), ChessGame.class)));
                }
                return games;
            }
        } catch (Exception e) {
            throw new InternalServerErrorResponse("internal error");
        }
    }

    @Override
    public GameData getGame(int gameID) {
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("SELECT * FROM games WHERE id = ?")) {
                preparedStatement.setInt(1, gameID);
                var result = preparedStatement.executeQuery();
                if (!result.next()) {
                    return null;
                }
                return new GameData(
                        result.getInt(1),
                        result.getString(2),
                        result.getString(3),
                        result.getString(4),
                        (ChessGame) gson.fromJson(result.getString(5), ChessGame.class));
            }
        } catch (Exception e) {
            throw new InternalServerErrorResponse("internal error");
        }
    }

    @Override
    public void saveGame(GameData data) {
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("INSERT INTO games VALUES (?,?,?,?,?)")) {
                preparedStatement.setInt(1, data.gameID());
                preparedStatement.setString(2, data.whiteUsername());
                preparedStatement.setString(3, data.blackUsername());
                preparedStatement.setString(4, data.gameName());
                preparedStatement.setString(5, gson.toJson(data.game()));
                preparedStatement.executeUpdate();
            }
        } catch (Exception e) {
            throw new InternalServerErrorResponse("internal error");
        }
    }

    @Override
    public void clear() {
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("DELETE FROM games")) {
                preparedStatement.executeUpdate();
            }
            try (var preparedStatement = conn.prepareStatement("DELETE FROM auths")) {
                preparedStatement.executeUpdate();
            }
            try (var preparedStatement = conn.prepareStatement("DELETE FROM users")) {
                preparedStatement.executeUpdate();
            }
        } catch (Exception e) {
            throw new InternalServerErrorResponse("internal error");
        }
    }
}