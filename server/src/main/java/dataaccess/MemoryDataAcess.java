package dataaccess;

import java.util.HashMap;

public class MemoryDataAcess implements DataAccess {
    HashMap<String, UserData> users;
    HashMap<String, AuthData> auths;
    HashMap<String, GameData> games;

    MemoryDataAcess() {
        users = new HashMap<>();
        auths = new HashMap<>();
        games = new HashMap<>();
    }

    @Override
    public UserData getUser(String username) {
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
    public GameData[] getGames() {
        return new GameData[0];
    }

    @Override
    public GameData getGame(int gameID) {
        return null;
    }

    @Override
    public void saveGame(GameData data) {

    }

    @Override
    public void updateGame(GameData data) {

    }

    @Override
    public void clear() {

    }
}
