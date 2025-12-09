package ui;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.ArrayList;

public class BoardView {
    ChessGame game;
    Tile[][] board;
    ChessGame.TeamColor perspective;
    private final char whiteBg = (char) 252;

    public BoardView() {
        board = new Tile[10][10];
    }

    private void setBorderLabels() {
        for (int i = 0; i < 8; i++) {
            board[0][1 + i].setContent(String.valueOf((char) (65 + i)));
            board[9][1 + i].setContent(String.valueOf((char) (65 + i)));
            board[1 + i][0].setContent(Integer.toString(8 - i));
            board[1 + i][9].setContent(Integer.toString(8 - i));
        }
    }

    private void constructBorders() {
        for (int i = 0; i < 9; i++) {
            board[0][i] = new Tile((char) 254, (char) 232, " ");
            board[i][9] = new Tile((char) 254, (char) 232, " ");
            board[9][9 - i] = new Tile((char) 254, (char) 232, " ");
            board[9 - i][0] = new Tile((char) 254, (char) 232, " ");
        }
        setBorderLabels();
    }

    private void constructGameTiles() {
        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                if ((i + j) % 2 == 0) {
                    board[i][j] = new Tile(whiteBg, (char) 0, " ");
                } else {
                    char blackBg = (char) 243;
                    board[i][j] = new Tile(blackBg, (char) 0, " ");
                }
            }
        }
    }

    private void constructEmptyBoard() {
        constructBorders();
        constructGameTiles();
    }

    private void placePiece(ChessPiece piece, ChessPosition pos) {
        if (piece == null) {
            return;
        }
        Tile tile = board[pos.getRow()][pos.getColumn()];
        tile.setContent(piece.toString());
        char whiteFg = (char) 255;
        char blackFg = (char) 232;
        tile.setFg(piece.getTeamColor() == ChessGame.TeamColor.WHITE
                ? whiteFg
                : blackFg);
    }

    private void placePieces() {
        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                ChessPiece piece = game.getBoard().getBoard()[i-1][j-1];
                ChessPosition pos = new ChessPosition(9 - i, j);
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

    public void constructBoard(ChessGame game, ChessGame.TeamColor perspective) {
        this.game = game;
        this.perspective = perspective;
        constructEmptyBoard();
        placePieces();
    }

    public String render() {
        if (perspective == ChessGame.TeamColor.BLACK) {
            invertBoard();
        }
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                output.append(board[i][j].toString());
            }
            output.append('\n');
        }
        return output.toString();
    }

    public void highlightMoves(ArrayList<ChessMove> moves) {
        for (var move : moves) {
            int row = 9 - move.getEndPosition().getRow();
            int col = move.getEndPosition().getColumn();
            var currentTile = board[row][col];
            var currentBg = currentTile.getBg();
            if (currentBg == whiteBg) {
                currentTile.setBg((char) 194);
            } else {
                currentTile.setBg((char) 113);
            }
        }
    }
}
