package chess;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    private ChessPiece[][] board = new ChessPiece[8][8];

    public ChessBoard() {
        
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param pos where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition pos, ChessPiece piece) {
        board[pos.getRow()][pos.getColumn()] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return board[position.getRow()][position.getColumn()];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        board = new ChessPiece[8][8];
    }

    public String toString() {
        StringBuilder boardStr = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            boardStr.append("|");
           for (int j = 0; j < 7; j++) {
               boardStr.append(" ");
               boardStr.append(board[i][j].toString());
               boardStr.append(" |");
           }
           boardStr.append('\n');
           if (i != 7) {
               boardStr.append("-------------------------------");
           }
        }

        return boardStr.toString();
    }
}
