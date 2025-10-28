package dataaccess;

import java.util.ArrayList;

public class SQLDataAccess implements DataAccess {
    private DatabaseManager db;

    public SQLDataAccess() {
        db = new DatabaseManager();
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