package ui;

import chess.ChessGame;
import static ui.EscapeSequences.*;

public class GameModeManager {
    ChessGame.TeamColor perspective;
    boolean isObserver;

    public GameModeManager(ChessGame.TeamColor perspective, boolean isObserver) {
        this.perspective = perspective;
        this.isObserver = isObserver;

    }

    public void renderGame(ChessGame game) {
        System.out.print(ERASE_SCREEN);
        BoardView boardView = new BoardView();
        String boardString = boardView.render(game, perspective);
        System.out.print(moveCursorToLocation(1,5));
        System.out.print(boardString);
        System.out.print(moveCursorToLocation(1, 1));
        System.out.print(">> ");
        System.out.flush();
    }

    public void notify(String message) {
        System.out.print(moveCursorToLocation(1,3));
        System.out.print(ERASE_LINE);
        System.out.print(message);
        System.out.print(moveCursorToLocation(1,1));
        System.out.print(ERASE_LINE);
        System.out.print(">> ");
        System.out.flush();
    }

    public void exitGameMode() {
        System.out.print(ERASE_SCREEN);
    }
}
