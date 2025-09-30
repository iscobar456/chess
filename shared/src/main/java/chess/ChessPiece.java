package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        color = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    private final ChessGame.TeamColor color;
    private final PieceType type;

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return color;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ChessMoveCalculator moveCalculator = new ChessMoveCalculator();

        System.out.println(board.toString());
        System.out.println(myPosition.toString());
        return switch (type) {
            case KING -> moveCalculator.kingMoves(myPosition, board);
            case ROOK -> moveCalculator.rookMoves(myPosition, board);
            case KNIGHT -> moveCalculator.knightMoves(myPosition, board);
            case BISHOP -> moveCalculator.bishopMoves(myPosition, board);
            case QUEEN -> moveCalculator.queenMoves(myPosition, board);
            case PAWN -> moveCalculator.pawnMoves(myPosition, board);
        };
    }

    public ChessPiece copy() {
        return new ChessPiece(this.color, this.type);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return color == that.color && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(color, type);
    }

    public String toString() {
        return String.valueOf(
                (char) (type.toString().charAt(0) + ((color == ChessGame.TeamColor.BLACK) ? 1 : 0) * 32)
        );
    }
}
