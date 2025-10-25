package dataaccess;

import java.util.ArrayList;

public interface DataAccess {
    UserData getUser(String username);
    void saveUser(UserData data);
    AuthData getAuth(String authToken);
    void saveAuth(AuthData data);
    void deleteAuth(String authToken);
    ArrayList<GameData> getGames();
    GameData getGame(int gameID);
    void saveGame(GameData data);
    void clear();
}
