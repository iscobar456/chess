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
            try (var preparedStatement = conn.prepareStatement("SELECT * FROM users WHERE username = ?")) {

            }
        }
    }

    @Override
    public AuthData getAuth(String authToken) {
        return null;
    }

    @Override
    public void saveAuth(AuthData data) {

    }

    @Override
    public void deleteAuth(String authToken) {

    }

    @Override
    public ArrayList<GameData> getGames() {
        return null;
    }

    @Override
    public GameData getGame(int gameID) {
        return null;
    }

    @Override
    public void saveGame(GameData data) {

    }

    @Override
    public void clear() {

    }
}