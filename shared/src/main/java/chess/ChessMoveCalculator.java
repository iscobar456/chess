package chess;

import java.util.ArrayList;

public class ChessMoveCalculator {

    private final int[][] diagonalVectors = {{1,1},{1,-1},{-1,-1},{-1,1}};
    private final int[][] orthogonalVectors = {{-1,0},{1,0},{0,1},{0,-1}};
    private final int[][] knightVectors = {{1,2},{2,1},{2,-1},{1,-2},{-1,-2},{-2,-1},{-2,1},{-1,2}};

    public ChessMoveCalculator() {

    }

    private boolean isInBounds(int[] pos) {
        return pos[0] > 0 && pos[0] < 9 && pos[1] > 0 && pos[1] < 9;
    }

    private ChessPosition addPositionVector(ChessPosition position, int[] vector) {
        int[] destination = {position.getRow() + vector[0], position.getColumn() + vector[1]};
        if (!isInBounds(destination)) return null;
        return new ChessPosition(destination[0], destination[1]);
    }

    /**
     * Takes direction vectors and calculates moves bounded by range.
     * @param vectors  The directions in which a piece can move.
     * @param startPos The starting position of the piece.
     * @param board    The game board.
     * @param range    How far a piece can move. O means unbounded.
     * @return         An ArrayList of moves.
     */
    private ArrayList<ChessMove> vectorMoves(int[][] vectors, ChessPosition startPos, ChessBoard board, int range) {
        ArrayList<ChessMove>moves = new ArrayList<>();
        for (var vec : vectors) {
            int[] dest = {startPos.getRow() + vec[0], startPos.getColumn() + vec[1]};
            range = range == 0 ? -1 : range;
            int distance = 0;
            while (isInBounds(dest) && range != distance) {
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
                distance += 1;
            }
        }
        return moves;
    }

    public ArrayList<ChessMove> kingMoves(ChessPosition startPos, ChessBoard board) {
        int[][] kingVectors = new int[diagonalVectors.length + orthogonalVectors.length][2];
        System.arraycopy(diagonalVectors, 0, kingVectors, 0, diagonalVectors.length);
        System.arraycopy(orthogonalVectors,0,kingVectors,diagonalVectors.length, orthogonalVectors.length);

        return vectorMoves(kingVectors, startPos, board, 1);
    }

    public ArrayList<ChessMove> rookMoves(ChessPosition startPos, ChessBoard board) {
        return vectorMoves(orthogonalVectors, startPos, board, 0);
    }
    public ArrayList<ChessMove> knightMoves(ChessPosition startPos, ChessBoard board) {
        return vectorMoves(knightVectors, startPos, board, 1);
    }

    public ArrayList<ChessMove> bishopMoves(ChessPosition startPos, ChessBoard board) {
        return vectorMoves(diagonalVectors, startPos, board, 0);
    }

    public ArrayList<ChessMove> queenMoves(ChessPosition startPos, ChessBoard board) {
        int[][] queenVectors = new int[diagonalVectors.length + orthogonalVectors.length][2];
        System.arraycopy(diagonalVectors, 0, queenVectors, 0, diagonalVectors.length);
        System.arraycopy(orthogonalVectors,0,queenVectors,diagonalVectors.length, orthogonalVectors.length);

        return vectorMoves(queenVectors, startPos, board, 0);
    }

    public ArrayList<ChessMove> pawnMoves(ChessPosition startPos, ChessBoard board) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        ChessPiece piece = board.getPiece(startPos);

        int[][] killVectors = {{1, -1}, {1,1}};
        int[] forwardVector = {1,0};
        if (piece.getTeamColor() == ChessGame.TeamColor.BLACK) {
            killVectors[0] = new int[]{-1,-1};
            killVectors[1] = new int[]{-1,1};
            forwardVector[0] = -1;
        }

        /* Check kill spots for opposing pieces */
        for (var vec : killVectors) {
            ChessPosition dest = addPositionVector(startPos, vec);
            if (dest != null
                    && board.getPiece(dest) != null
                    && board.getPiece(dest).getTeamColor() != piece.getTeamColor()) {
                moves.add(new ChessMove(startPos, dest, null));
            }
        }

        /* Add forward moves */
        ChessPosition ahead = addPositionVector(startPos, forwardVector);
        if (board.getPiece(ahead) == null) {
            moves.add(new ChessMove(startPos, ahead, null));

            if ((piece.getTeamColor() == ChessGame.TeamColor.BLACK && startPos.getRow() == 7)
                    || (piece.getTeamColor() == ChessGame.TeamColor.WHITE && startPos.getRow() == 2)) {
                ChessPosition nextAhead = addPositionVector(ahead, forwardVector);
                if (board.getPiece(nextAhead) == null) {
                    moves.add(new ChessMove(startPos, nextAhead, null));
                }
            }
        }

        /* Check for promotions */
        ArrayList<ChessMove> promoMoves = new ArrayList<>();
        for (var move : moves) {
            if (move.getEndPosition().getRow() == 1 || move.getEndPosition().getRow() == 8) {
                ChessMove promoRookMove = new ChessMove(move.getStartPosition(), move.getEndPosition(), ChessPiece.PieceType.ROOK);
                ChessMove promoKnightMove = new ChessMove(move.getStartPosition(), move.getEndPosition(), ChessPiece.PieceType.KNIGHT);
                ChessMove promoBishopMove = new ChessMove(move.getStartPosition(), move.getEndPosition(), ChessPiece.PieceType.BISHOP);
                ChessMove promoQueenMove = new ChessMove(move.getStartPosition(), move.getEndPosition(), ChessPiece.PieceType.QUEEN);
                promoMoves.add(promoRookMove);
                promoMoves.add(promoKnightMove);
                promoMoves.add(promoBishopMove);
                promoMoves.add(promoQueenMove);
            }
        }
        if (!promoMoves.isEmpty()) {
            return promoMoves;
        }

        return moves;
    }

}
