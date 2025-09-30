package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private TeamColor activeTeam;
    private ChessBoard board;

    public ChessGame() {

    }

    public TeamColor getTeamTurn() {
        return activeTeam;
    }

    /**
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        activeTeam = team;
    }

    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessBoard originalBoard = board.copy();
        ChessPiece piece = board.getPiece(startPosition);

        if (piece == null) {
            return Collections.emptyList();
        }

        var moves = piece.pieceMoves(board, startPosition);
        var validMoves = new ArrayList<ChessMove>();
        for (var move : moves) {
            movePiece(move);
            if (!isInCheck(activeTeam)) {
                validMoves.add(move);
            }
            setBoard(originalBoard);
        }

        return validMoves;
    }

    private void movePiece(ChessMove move) {
        board.setPiece(board.getPiece(move.getStartPosition()), move.getEndPosition());
        board.removePiece(move.getStartPosition());
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        movePiece(move);
        if (activeTeam == TeamColor.BLACK) {
            setTeamTurn(TeamColor.WHITE);
        } else {
            setTeamTurn(TeamColor.BLACK);
        }
    }

    private ChessPosition getKingPosition() {
        ChessPosition kingPosition = null;
        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                ChessPosition pos = new ChessPosition(i,j);
                if (board.getPiece(pos).getPieceType() == ChessPiece.PieceType.KING && board.getPiece(pos).getTeamColor() == activeTeam) {
                    kingPosition = pos;
                }
            }
        }
        return kingPosition;
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPosition = getKingPosition();
        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                ChessPosition pos = new ChessPosition(i,j);
                ChessPiece piece = board.getPiece(pos);
                for (var move : piece.pieceMoves(board, pos)) {
                    if (move.getEndPosition() == kingPosition) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        boolean hasValidMoves = false;
        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                ChessPosition pos = new ChessPosition(i, j);
                if (!validMoves(pos).isEmpty()) {
                    hasValidMoves = true;
                }
            }
        }
        return isInCheck(activeTeam) && !hasValidMoves;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        boolean hasValidMoves = false;
        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                ChessPosition pos = new ChessPosition(i, j);
                if (!validMoves(pos).isEmpty()) {
                    hasValidMoves = true;
                }
            }
        }
        return !isInCheck(activeTeam) && !hasValidMoves;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }
}
