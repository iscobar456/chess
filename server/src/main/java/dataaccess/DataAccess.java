package dataaccess;

public interface DataAccess {
    UserData getUser(String username);
    void saveUser(UserData data);
    AuthData getAuth(String authToken);
    void saveAuth(AuthData data);
    void deleteAuth(String authToken);
    GameData[] getGames();
    GameData getGame(int gameID);
    void saveGame(GameData data);
    void updateGame(GameData data);
    void clear();
}
