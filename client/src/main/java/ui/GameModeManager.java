package ui;

import chess.ChessGame;

public class GameModeManager {
    final String ERASE_SCREEN = "\u001b[2J";
    final String ERASE_LINE = "\u001b[2K";
    final String MOVE_TO_STATUS = "\u001b[H";
    final String MOVE_TO_BOARD = "\u001b[3;0H";
    final String SAVE_CURSOR = "\u001b 7";
    final String MOVE_TO_SAVED_CURSOR = "\u001b 8";
    final String MOVE_TO_PROMPT = "\u001b[18;0H";
    ChessGame.TeamColor perspective;
    boolean isObserver;

    public GameModeManager(ChessGame.TeamColor perspective, boolean isObserver) {
        this.perspective = perspective;
        this.isObserver = isObserver;

    }

    public void renderGame(ChessGame game) {
        System.out.print(SAVE_CURSOR);
        BoardView boardView = new BoardView();
        String boardString = boardView.render(game, perspective);
        System.out.print(MOVE_TO_BOARD);
        System.out.print(ERASE_LINE);
        System.out.print(boardString);
        System.out.flush();
        System.out.print(MOVE_TO_SAVED_CURSOR);
    }

    public void notify(String message) {
        System.out.print(SAVE_CURSOR);
        System.out.print(MOVE_TO_STATUS);
        System.out.print(ERASE_LINE);
        System.out.print(message);
        System.out.print(MOVE_TO_PROMPT);
        System.out.flush();
        System.out.print(MOVE_TO_SAVED_CURSOR);
    }

    public void exitGameMode() {
        System.out.print(ERASE_SCREEN);
    }
}
