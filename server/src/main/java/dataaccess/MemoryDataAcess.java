package dataaccess;

import java.util.HashMap;

public class MemoryDataAcess implements DataAccess {
    HashMap<String, UserData> users;
    HashMap<String, AuthData> auths;
    HashMap<Integer, GameData> games;

    MemoryDataAcess() {
        users = new HashMap<>();
        auths = new HashMap<>();
        games = new HashMap<>();
    }

    @Override
    public UserData getUser(String username) {
        return users.get(username);
    }

    @Override
    public void saveUser(UserData data) {
        users.put(data.username(), data);
    }

    @Override
    public AuthData getAuth(String authToken) {
        return auths.get(authToken);
    }

    @Override
    public void saveAuth(AuthData data) {
        auths.put(data.authToken(), data);
    }

    @Override
    public void deleteAuth(String authToken) {

    }

    @Override
    public GameData[] getGames() {
        return new GameData[0];
    }

    @Override
    public GameData getGame(int gameID) {
        return games.get(gameID);
    }

    @Override
    public void saveGame(GameData data) {
        games.put(data.gameID(), data);
    }

    @Override
    public void updateGame(GameData data) {
        games.put(data.gameID(), data);
    }

    @Override
    public void clear() {
        users = null;
        auths = null;
        games = null;
    }
}
