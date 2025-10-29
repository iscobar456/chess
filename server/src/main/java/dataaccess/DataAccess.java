package dataaccess;

import java.sql.SQLException;
import java.util.ArrayList;

public interface DataAccess {
    UserData getUser(String username) throws DataAccessException, SQLException;
    void saveUser(UserData data) throws DataAccessException, SQLException;
    AuthData getAuth(String authToken);
    void saveAuth(AuthData data);
    void deleteAuth(String authToken);
    ArrayList<GameData> getGames();
    GameData getGame(int gameID);
    void saveGame(GameData data);
    void clear();
}
