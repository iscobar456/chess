package chess;

import java.util.ArrayList;
import java.util.Arrays;

public class ChessMoveCalculator {
    private static final int[][] orthoVects = {{1,0},{-1,0},{0,1},{0,-1}};
    private static final int[][] diagVects = {{1,1},{1,-1},{-1,1},{-1,-1}};
    private static final int[][] LVects = {{2,1},{2,-1},{-2,1},{-2,-1},{1,2},{1,-2},{-1,2},{-1,-2}};

    public static ArrayList<ChessMove> getPieceMoves(ChessBoard board, ChessPosition pos, ChessPiece.PieceType type, ChessGame.TeamColor color) {
        return switch (type) {
            case ROOK -> getRookMoves(board, pos, type, color);
            case KNIGHT -> getKnightMoves(board, pos, type, color);
            case BISHOP -> getBishopMoves(board, pos, type, color);
            case QUEEN -> getQueenMoves(board, pos, type, color);
            case KING -> getKingMoves(board, pos, type, color);
            case PAWN -> getPawnMoves(board, pos, type, color);
        };
    }

    private static boolean isEmpty(ChessBoard board, ChessPosition pos) {
        return board.getPiece(pos) == null;
    }

    private static boolean isEnemy(ChessBoard board, ChessPosition pos, ChessGame.TeamColor color) {
        return !isEmpty(board, pos) && board.getPiece(pos).getTeamColor() != color;
    }

    private static boolean isEmptyOrEnemy(ChessBoard board, ChessPosition pos, ChessGame.TeamColor color) {
        return isEmpty(board, pos) || isEnemy(board, pos, color);
    }

    private static ChessPosition addVectPosition(int[] vector, ChessPosition pos) {
        return new ChessPosition(pos.getRow() + vector[0], pos.getColumn() + vector[1]);
    }

    private static ArrayList<ChessMove> calculateMoves(ChessBoard board, ChessPosition pos, ArrayList<int[]> vectors, boolean isBounded, ChessPiece.PieceType type, ChessGame.TeamColor color) {
        ArrayList<ChessMove> moves = new ArrayList<>();

        for (var vec : vectors) {
            ChessPosition end = addVectPosition(vec, pos);
            while (end.isInBounds() && isEmptyOrEnemy(board, end, color)) {
                moves.add(new ChessMove(pos, end, null));
                if (isEnemy(board, end, color) || isBounded) break;
                end = addVectPosition(vec, end);
            }
        }

        return moves;
    }

    private static ArrayList<ChessMove> getRookMoves(ChessBoard board, ChessPosition pos, ChessPiece.PieceType type, ChessGame.TeamColor color) {
        ArrayList<int[]> vectors = new ArrayList<>(Arrays.asList(orthoVects));
        return calculateMoves(board, pos, vectors, false, type, color);
    }

    private static ArrayList<ChessMove> getKnightMoves(ChessBoard board, ChessPosition pos, ChessPiece.PieceType type, ChessGame.TeamColor color) {
        ArrayList<int[]> vectors = new ArrayList<>(Arrays.asList(LVects));
        return calculateMoves(board, pos, vectors,true,  type, color);
    }

    private static ArrayList<ChessMove> getBishopMoves(ChessBoard board, ChessPosition pos, ChessPiece.PieceType type, ChessGame.TeamColor color) {
        ArrayList<int[]> vectors = new ArrayList<>(Arrays.asList(diagVects));
        return calculateMoves(board, pos, vectors,false,  type, color);
    }

    private static ArrayList<ChessMove> getQueenMoves(ChessBoard board, ChessPosition pos, ChessPiece.PieceType type, ChessGame.TeamColor color) {
        ArrayList<int[]> vectors = new ArrayList<>(Arrays.asList(orthoVects));
        vectors.addAll(Arrays.asList(diagVects));
        return calculateMoves(board, pos, vectors,false,  type, color);
    }

    private static ArrayList<ChessMove> getKingMoves(ChessBoard board, ChessPosition pos, ChessPiece.PieceType type, ChessGame.TeamColor color) {
        ArrayList<int[]> vectors = new ArrayList<>(Arrays.asList(orthoVects));
        vectors.addAll(Arrays.asList(diagVects));
        return calculateMoves(board, pos, vectors,true,  type, color);
    }

    private static ArrayList<ChessMove> getPawnMoves(ChessBoard board, ChessPosition pos, ChessPiece.PieceType type, ChessGame.TeamColor color) {
        ArrayList<ChessMove> moves = new ArrayList<>();

        int[] whiteForward = {1,0};
        int[][] whiteCaptures = {{1,1},{1,-1}};
        int[] blackForward = {-1,0};
        int[][] blackCaptures = {{-1,1},{-1,-1}};

        if (color == ChessGame.TeamColor.WHITE) {
            ChessPosition forward = addVectPosition(whiteForward, pos);
            if (forward.isInBounds() && isEmpty(board, forward)) {
                moves.add(new ChessMove(pos, forward, null));
                if (pos.getRow() == 2) {
                    ChessPosition doubleForward = addVectPosition(whiteForward, forward);
                    if (isEmpty(board, doubleForward)) {
                        moves.add(new ChessMove(pos, doubleForward, null));
                    }
                }
            }
            for (var captureVect : whiteCaptures) {
                ChessPosition capture = addVectPosition(captureVect, pos);
                if (capture.isInBounds() && isEnemy(board, capture, color)) {
                    moves.add(new ChessMove(pos, capture, null));
                }
            }
        } else {
            ChessPosition forward = addVectPosition(blackForward, pos);
            if (forward.isInBounds() && isEmpty(board, forward)) {
                moves.add(new ChessMove(pos, forward, null));
                if (pos.getRow() == 7) {
                    ChessPosition doubleForward = addVectPosition(blackForward, forward);
                    if (isEmpty(board, doubleForward)) {
                        moves.add(new ChessMove(pos, doubleForward, null));
                    }
                }
            }
            for (var captureVect : blackCaptures) {
                ChessPosition capture = addVectPosition(captureVect, pos);
                if (capture.isInBounds() && isEnemy(board, capture, color)) {
                    moves.add(new ChessMove(pos, capture, null));
                }
            }
        }


        ArrayList<ChessMove> promoMoves = new ArrayList<>();
        for (var move : moves) {
            if (move.getEndPosition().getRow() == 1 || move.getEndPosition().getRow() == 8) {
                promoMoves.add(new ChessMove(pos, move.getEndPosition(), ChessPiece.PieceType.ROOK));
                promoMoves.add(new ChessMove(pos, move.getEndPosition(), ChessPiece.PieceType.KNIGHT));
                promoMoves.add(new ChessMove(pos, move.getEndPosition(), ChessPiece.PieceType.BISHOP));
                promoMoves.add(new ChessMove(pos, move.getEndPosition(), ChessPiece.PieceType.QUEEN));
            }
        }
        if (!promoMoves.isEmpty()) {
            return promoMoves;
        }

        return moves;
    }
}
