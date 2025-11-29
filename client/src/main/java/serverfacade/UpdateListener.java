package serverfacade;

import chess.ChessGame;
import data.GameData;
import data.Update;

public interface UpdateListener {
    public void onNotification(String notification);
    public void onLoadGame(GameData gameData);
    public void onDisconnect();
}
