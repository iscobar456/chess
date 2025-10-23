package dataaccess;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MemoryDataAccess implements DataAccess {
    public HashMap<String, UserData> users;
    public HashMap<String, AuthData> auths;
    public HashMap<Integer, GameData> games;

    public MemoryDataAccess() {
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
        auths.remove(authToken);
    }

    @Override
    public ArrayList<GameData> getGames() {
        return new ArrayList<GameData>(games.values());
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
        users.clear();
        auths.clear();
        games.clear();
    }
}
