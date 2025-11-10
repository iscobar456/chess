package ui;

import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

public class BoardView {
    ChessGame game;
    Tile[][] board;
    ChessGame.TeamColor perspective;

    public BoardView(ChessGame game, ChessGame.TeamColor perspective) {
        this.game = game;
        this.perspective = perspective;
        board = new Tile[10][10];
    }

    private void setBorderLabels() {
        for (int i = 0; i < 8; i++) {
            board[0][1 + i].setContent((char) (65 + i));
            board[9][1 + i].setContent((char) (65 + i));
            board[1 + i][0].setContent(Integer.toString(8 - i).charAt(0));
            board[1 + i][9].setContent(Integer.toString(8 - i).charAt(0));
        }
    }

    private void constructBorders() {
        for (int i = 0; i < 9; i++) {
            board[0][0 + i] = new Tile((char) 254, (char) 232, (char) 0);
            board[0 + i][9] = new Tile((char) 254, (char) 232, (char) 0);
            board[9][9 - i] = new Tile((char) 254, (char) 232, (char) 0);
            board[9 - i][0] = new Tile((char) 254, (char) 232, (char) 0);
        }
        setBorderLabels();
    }

    private void constructGameTiles() {
        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                if ((i + j) % 2 == 0) {
                    board[i][j] = new Tile((char) 252, (char) 0, (char) 0);
                } else {
                    board[i][j] = new Tile((char) 243, (char) 0, (char) 0);
                }
            }
        }
    }

    private void constructEmptyBoard() {
        constructBorders();
        constructGameTiles();
    }

    private void placePiece(ChessPiece piece, ChessPosition pos) {
        Tile tile = board[pos.getRow()][pos.getColumn()];
        tile.setContent(piece.toString().charAt(0));
        tile.setFg(piece.getTeamColor() == ChessGame.TeamColor.WHITE
                ? (char) 255
                : (char) 232);
    }

    private void placePieces() {
        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                ChessPiece piece = game.getBoard().getBoard()[i][j];
                ChessPosition pos = new ChessPosition(i, j);
                placePiece(piece, pos);
            }
        }
    }

    private void invertBoard() {
        Tile[][] newBoard = new Tile[10][10];
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                Tile tile = board[i][j];
                newBoard[9-i][9-j] = new Tile(tile.getBg(), tile.getFg(), tile.getContent());
            }
        }
        board = newBoard;
    }

    public String render() {
        constructEmptyBoard();
        placePieces();

        StringBuilder output = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                output.append(board[i][j].toString());
            }
            output.append('\n');
        }

        if (perspective == ChessGame.TeamColor.BLACK) {
            invertBoard();
        }

        return output.toString();
    }

    ;
}
