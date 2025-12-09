package chess;

import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    static final String WHITE_KING = "♔";
    static final String WHITE_QUEEN = "♕";
    static final String WHITE_BISHOP = "♗";
    static final String WHITE_KNIGHT = "♘";
    static final String WHITE_ROOK = "♖";
    static final String WHITE_PAWN = "♙";
    static final String BLACK_KING = "♚";
    static final String BLACK_QUEEN = "♛";
    public static final String BLACK_BISHOP = "♝";
    static final String BLACK_KNIGHT = "♞";
    static final String BLACK_ROOK = "♜";
    static final String BLACK_PAWN = "♟";

    private final ChessGame.TeamColor color;
    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        color = pieceColor;
        this.type = type;
    }

    public ChessPiece copy() {
        return new ChessPiece(this.color, this.type);
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
        return ChessMoveCalculator.getPieceMoves(board, myPosition, type, color);
    }

    public static PieceType stringToType(String type) throws Exception {
        if (type.equalsIgnoreCase("king")) {
            return PieceType.KING;
        } else if (type.equalsIgnoreCase("queen")) {
            return PieceType.QUEEN;
        } else if (type.equalsIgnoreCase("bishop")) {
            return PieceType.BISHOP;
        } else if (type.equalsIgnoreCase("knight")) {
            return PieceType.KNIGHT;
        } else if (type.equalsIgnoreCase("rook")) {
            return PieceType.ROOK;
        } else if (type.equalsIgnoreCase("pawn")) {
            return PieceType.PAWN;
        } else {
            throw new Exception("Invalid piece type");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece piece = (ChessPiece) o;
        return color == piece.color && type == piece.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(color, type);
    }

    public String toString() {
        return switch(type) {
            case KING -> color == ChessGame.TeamColor.WHITE ? WHITE_KING : BLACK_KING;
            case QUEEN -> color == ChessGame.TeamColor.WHITE ? WHITE_QUEEN : BLACK_QUEEN;
            case ROOK -> color == ChessGame.TeamColor.WHITE ? WHITE_ROOK : BLACK_ROOK;
            case BISHOP -> color == ChessGame.TeamColor.WHITE ? WHITE_BISHOP : BLACK_BISHOP;
            case KNIGHT -> color == ChessGame.TeamColor.WHITE ? WHITE_KNIGHT : BLACK_KNIGHT;
            case PAWN -> color == ChessGame.TeamColor.WHITE ? WHITE_PAWN : BLACK_PAWN;
        };
    }
}