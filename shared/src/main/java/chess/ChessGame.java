package chess;

import java.util.ArrayList;
import java.util.Objects;

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
        activeTeam = TeamColor.WHITE;
        board = new ChessBoard();
        board.resetBoard();
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

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public ArrayList<ChessMove> validMoves(ChessPosition startPosition) {
        var validMoves = new ArrayList<ChessMove>();
        ChessPiece piece = board.getPiece(startPosition);
        if (piece == null) {
            return validMoves;
        }

        ChessBoard originalBoard = board.copy();
        TeamColor originalColor = activeTeam;
        setTeamTurn(piece.getTeamColor());

        var moves = piece.pieceMoves(board, startPosition);
        for (var move : moves) {
            movePiece(move);
            if (!isInCheck(activeTeam)) {
                validMoves.add(move);
            }
            setBoard(originalBoard.copy());
        }

        activeTeam = originalColor;
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
        if (board.getPiece(move.getStartPosition()) == null) {
            throw new InvalidMoveException("No piece");
        }
        if (!isTeam(move.getStartPosition(), activeTeam)) {
            throw new InvalidMoveException("Out of turn move");
        }
        var valMoves = validMoves(move.getStartPosition());
        if (valMoves.contains(move)) {
            movePiece(move);
            if (move.getPromotionPiece() != null) {
                board.setPiece(new ChessPiece(activeTeam, move.getPromotionPiece()), move.getEndPosition());
            }
        } else {
            throw new InvalidMoveException(move.toString());
        }
        setTeamTurn(activeTeam == TeamColor.BLACK ? TeamColor.WHITE : TeamColor.BLACK);
    }

    private boolean isTeam(ChessPosition pos, TeamColor color) {
        return board.getPiece(pos).getTeamColor() == color;
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPosition = board.getKingPosition(teamColor);
        for (var pos : board.getPiecePositions()) {
            ChessPiece piece = board.getPiece(pos);
            for (var move : piece.pieceMoves(board, pos)) {
                if (kingPosition.equals(move.getEndPosition())) {
                    return true;
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
        for (var pos : board.getPiecePositions()) {
            if (!isTeam(pos, teamColor)) {
                continue;
            }
            if (!validMoves(pos).isEmpty()) {
                hasValidMoves = true;
                break;
            }
        }
        return isInCheck(teamColor) && !hasValidMoves;
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
        for (var pos : board.getPiecePositions()) {
            if (!isTeam(pos, teamColor)) {
                continue;
            }
            if (!validMoves(pos).isEmpty()) {
                hasValidMoves = true;
                break;
            }
        }
        return !isInCheck(activeTeam) && !hasValidMoves;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return activeTeam == chessGame.activeTeam && Objects.equals(board, chessGame.board);
    }

    @Override
    public int hashCode() {
        return Objects.hash(activeTeam, board);
    }

    public enum TeamColor {
        WHITE,
        BLACK
    }
}
