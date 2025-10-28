package dataaccess;

import java.sql.SQLException;
import java.util.ArrayList;

public class SQLDataAccess implements DataAccess {
    @Override
    public UserData getUser(String username) throws DataAccessException, SQLException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("SELECT 1+1")) {
                var rs = preparedStatement.executeQuery();
                rs.next();
                System.out.println(rs.getInt(1));
            }
        }
        return null;
    }

    @Override
    public void saveUser(UserData data) {

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