package dataaccess;

import java.sql.SQLException;
import java.util.ArrayList;

public interface DataAccess {
    UserData getUser(String username) throws DataAccessException, SQLException;
    void saveUser(UserData data) throws DataAccessException, SQLException;
    AuthData getAuth(String authToken) throws DataAccessException, SQLException;
    void saveAuth(AuthData data) throws DataAccessException, SQLException;
    void deleteAuth(String authToken) throws DataAccessException, SQLException;
    ArrayList<GameData> getGames() throws DataAccessException, SQLException;
    GameData getGame(int gameID) throws DataAccessException, SQLException;
    void saveGame(GameData data) throws DataAccessException, SQLException;
    void clear() throws DataAccessException, SQLException;
}
