package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;

import java.sql.SQLException;
import java.util.ArrayList;

public class SQLDataAccess implements DataAccess {
    private final Gson gson = new Gson();

    @Override
    public UserData getUser(String username) throws DataAccessException, SQLException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("SELECT * FROM users WHERE username = ?")) {
                preparedStatement.setString(1, username);
                var result = preparedStatement.executeQuery();
                result.next();
                UserData data = new UserData(
                        result.getString("username"),
                        result.getString("email"),
                        result.getString("password"));
                return data;
            }
        }
    }

    @Override
    public void saveUser(UserData data) throws DataAccessException, SQLException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("INSERT (?,?,?) INTO users")) {
                preparedStatement.setString(1, data.username());
                preparedStatement.setString(2, data.password());
                preparedStatement.setString(3, data.email());
                preparedStatement.executeUpdate();
            }
        }
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException, SQLException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("SELECT * FROM auths WHERE authtoken = ?")) {

            }
        }
    }

    @Override
    public void saveAuth(AuthData data) throws DataAccessException, SQLException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("INSERT (?,?) INTO auths")) {
                preparedStatement.setString(1, data.authToken());
                preparedStatement.setString(2, data.username());
                preparedStatement.executeQuery();
            }
        }
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException, SQLException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("DELETE FROM auths WHERE authtoken = ?")) {
                preparedStatement.setString(1, authToken);
                preparedStatement.executeQuery();

            }
        }
    }

    @Override
    public ArrayList<GameData> getGames() throws DataAccessException, SQLException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("SELECT * FROM games")) {

            }
        }
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException, SQLException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("SELECT * FROM games WHERE id = ?")) {
                preparedStatement.setInt(1, gameID);
                var result = preparedStatement.executeQuery();
                result.next();
                return new GameData(
                        result.getInt(1),
                        result.getString(2),
                        result.getString(3),
                        result.getString(4),
                        (ChessGame) gson.fromJson(result.getString(5), ChessGame.class)
                );
            }
        }
    }

    @Override
    public void saveGame(GameData data) throws DataAccessException, SQLException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("INSERT (?,?,?,?,?) INTO games")) {
                preparedStatement.setInt(1, data.gameID());
                preparedStatement.setString(2, data.whiteUsername());
                preparedStatement.setString(3, data.blackUsername());
                preparedStatement.setString(4, data.gameName());
                preparedStatement.setString(5, gson.toJson(data.game()));
                preparedStatement.executeQuery();
            }
        }
    }

    @Override
    public void clear() throws DataAccessException, SQLException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("TRUNCATE TABLE users; TRUNCATE TABLE auths; TRUNCATE TABLE games;")) {
                preparedStatement.executeQuery();
            }
        }
    }
}