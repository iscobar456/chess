package chess;

import java.util.ArrayList;

public class ChessMoveCalculator {

    private final int[][] diagonalVectors = {{1,1},{1,-1},{-1,-1},{-1,1}};
    private final int[][] orthogonalVectors = {{0,1},{1,0},{0,-1},{-1,0}};
    private final int[][] LVectors = {{1,2},{2,1},{2,-1},{1,-2},{-1,-2},{-2,-1},{-2,1},{-1,2}};

    public ChessMoveCalculator() {
    }

    public ArrayList<ChessMove> kingMoves(ChessPosition startPos, ChessBoard board) {
        ArrayList<ChessMove>moves = new ArrayList<>();

        int[][] kingVectors = new int[diagonalVectors.length + orthogonalVectors.length][2];
        System.arraycopy(diagonalVectors, 0, kingVectors, 0, diagonalVectors.length);
        System.arraycopy(orthogonalVectors,0,kingVectors,diagonalVectors.length, orthogonalVectors.length);

        for (var vec : kingVectors) {
            int[] dest = {
                    startPos.getRow() + vec[0],
                    startPos.getColumn() + vec[1]
            };
            // check bounds.
            if (dest[0] < 1 || dest[0] > 8 || dest[1] < 1 || dest[1] > 8) {
                continue;
            }
            // check for team pieces.
            ChessPosition movePos = new ChessPosition(dest[0], dest[1]);
            if (board.getPiece(movePos).getTeamColor() == board.getPiece(startPos).getTeamColor()) {
                continue;
            }
            moves.add(new ChessMove(startPos, movePos, null));
        }

        return moves;
    }

    public ArrayList<ChessMove> rookMoves(ChessPosition startPos, ChessBoard board) {
        ArrayList<ChessMove> moves = new ArrayList<>();


        return moves;
    }
    public ArrayList<ChessMove> knightMoves(ChessPosition startPos, ChessBoard board) {
        ArrayList<ChessMove> moves = new ArrayList<>();


        return moves;
    }

    public ArrayList<ChessMove> bishopMoves(ChessPosition startPos, ChessBoard board) {
        ArrayList<ChessMove> moves = new ArrayList<>();


        return moves;
    }

    public ArrayList<ChessMove> queenMoves(ChessPosition startPos, ChessBoard board) {
        ArrayList<ChessMove> moves = new ArrayList<>();


        return moves;
    }

    public ArrayList<ChessMove> pawnMoves(ChessPosition startPos, ChessBoard board) {
        ArrayList<ChessMove> moves = new ArrayList<>();


        return moves;
    }

}
