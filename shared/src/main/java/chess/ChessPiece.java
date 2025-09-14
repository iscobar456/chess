package chess;

import java.util.Collection;

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
        throw new RuntimeException("Not implemented");
    }

    public boolean equals(ChessPiece p) {
        if (p == null) {
            return false;
        }
        return p.getPieceType() == type && p.getTeamColor() == color;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = hash * 31 + type.hashCode();
        hash = hash * 31 + color.hashCode();
        return hash;
    }

    public String toString() {
        return String.valueOf(
            type.toString().charAt(0) + color.compareTo(ChessGame.TeamColor.BLACK) * 32
        );
    }
}
