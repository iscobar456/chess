package chess;

import java.util.ArrayList;

public class ChessMoveCalculator {

    private final int[][] diagonalVectors = {{1,1},{1,-1},{-1,-1},{-1,1}};
    private final int[][] orthogonalVectors = {{-1,0},{1,0},{0,1},{0,-1}};
    private final int[][] LVectors = {{1,2},{2,1},{2,-1},{1,-2},{-1,-2},{-2,-1},{-2,1},{-1,2}};

    public ChessMoveCalculator() {

    }

    private boolean isInBounds(int[] pos) {
        return pos[0] > 0 && pos[0] < 9 && pos[1] > 0 && pos[1] < 9;
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

            if (isInBounds(dest)) {
                ChessPosition movePos = new ChessPosition(dest[0], dest[1]);
                if (board.getPiece(movePos) != null && board.getPiece(movePos).getTeamColor() == board.getPiece(startPos).getTeamColor()) {
                    continue;
                }
                moves.add(new ChessMove(startPos, movePos, null));
                continue;
            }
        }

        return moves;
    }

    public ArrayList<ChessMove> rookMoves(ChessPosition startPos, ChessBoard board) {
        ArrayList<ChessMove> moves = new ArrayList<>();

        for (var vec : orthogonalVectors) {
            int[] dest = {startPos.getRow() + vec[0], startPos.getColumn() + vec[1]};
            while (isInBounds(dest)) {
                ChessPosition destPos = new ChessPosition(dest[0], dest[1]);
                ChessPiece destPiece = board.getPiece(destPos);
                if (destPiece != null) {
                    if (destPiece.getTeamColor() != board.getPiece(startPos).getTeamColor()) {
                        moves.add(new ChessMove(startPos, destPos, null));
                    }
                    break;
                }
                moves.add(new ChessMove(startPos, destPos, null));
                dest[0] += vec[0];
                dest[1] += vec[1];
            }
        }

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
