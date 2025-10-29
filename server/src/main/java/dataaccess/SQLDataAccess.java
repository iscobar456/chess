package dataaccess;

import java.sql.SQLException;
import java.util.ArrayList;

public class SQLDataAccess implements DataAccess {
    @Override
    public UserData getUser(String username) throws DataAccessException, SQLException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("SELECT * FROM users WHERE username = ?")) {
                preparedStatement.setString(1, username);
                var rs = preparedStatement.executeQuery();
                rs.next();
                UserData data = new UserData(
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("password"));
                return data;
            }
        }
    }

    @Override
    public void saveUser(UserData data) throws DataAccessException, SQLException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("INSERT (?,?,?) INTO users")) {
                preparedStatement.executeQuery();
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
                preparedStatement.executeQuery();
            }
        }
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException, SQLException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("DELETE FROM auths WHERE authtoken = ?")) {
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

            }
        }
    }

    @Override
    public void saveGame(GameData data) throws DataAccessException, SQLException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("INSERT (?,?,?,?,?) INTO games")) {
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