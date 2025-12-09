package serverfacade;

import data.GameData;

public interface UpdateListener {
    public void onNotification(String notification);
    public void onLoadGame(GameData gameData);
}
