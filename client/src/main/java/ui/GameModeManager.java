package ui;

import chess.ChessGame;
import chess.ChessPosition;

import java.util.Locale;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class GameModeManager {
    ChessGame.TeamColor perspective;
    boolean isObserver;
    BoardView boardView;

    public GameModeManager(ChessGame.TeamColor perspective, boolean isObserver) {
        this.perspective = perspective;
        this.isObserver = isObserver;
        this.boardView = new BoardView();
    }

    private void drawPrompt() {
        System.out.print(moveCursorToLocation(1, 1));
        System.out.print(ERASE_LINE);
        System.out.print("--> ");
    }

    public void renderGame(ChessGame game) {
        boardView.constructBoard(game, perspective);
        String boardString = boardView.render();

        System.out.print(ERASE_SCREEN);
        System.out.print(moveCursorToLocation(5, 1));
        System.out.print(boardString);
        drawPrompt();
        System.out.flush();
    }

    public void renderGameWithHighlights(ChessGame game, ChessPosition pos) {
        BoardView boardView = new BoardView();
        boardView.constructBoard(game, perspective);
        boardView.highlightMoves(game.validMoves(pos));
        String boardString = boardView.render();

        System.out.print(ERASE_SCREEN);
        System.out.print(moveCursorToLocation(5, 1));
        System.out.print(boardString);
        drawPrompt();
        System.out.flush();
    }

    public void notify(String message) {
        System.out.print(moveCursorToLocation(4, 1));
        System.out.print(ERASE_LINE);
        System.out.print(moveCursorToLocation(3, 1));
        System.out.print(ERASE_LINE);
        System.out.print(message);
        drawPrompt();
        System.out.flush();
    }

    public void printHelp() {
        System.out.print(ERASE_SCREEN);
        System.out.print(moveCursorToLocation(2,1));
        String playerHelpString = """
                |-----------COMMAND------------:-----------INFO-----------|
                | redraw                       : Redraw the board         |
                | leave                        : Leave the game           |
                | move <start> <end> <promote> : Move a piece             |
                | resign                       : Forfeit the game         |
                | highlight                    : Highlight legal moves    |
                | help                         : Display help message     |""";
        String observerHelpString = """
                |-----------COMMAND------------:-----------INFO-----------|
                | redraw                       : Redraw the board         |
                | leave                        : Leave the game           |
                | highlight                    : Highlight legal moves    |
                | help                         : Display help message     |""";
        System.out.print(isObserver ? observerHelpString : playerHelpString);
        drawPrompt();
        System.out.flush();
    }

    public boolean confirmResign() {
        System.out.print(moveCursorToLocation(1,1));
        System.out.print(ERASE_LINE);
        System.out.print("Are you sure you want to resign? (y/n): ");
        System.out.flush();
        Scanner scanner = new Scanner(System.in);
        String confirm = scanner.nextLine().toLowerCase(Locale.ROOT);
        drawPrompt();
        System.out.flush();
        return confirm.equals("y");
    }

    public void exitGameMode() {
        System.out.print(ERASE_SCREEN);
    }
}
