package chess;

import java.util.Locale;
import java.util.Objects;

/**
 * Represents a single square position on a chess board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPosition {

    private final int row;
    private final int col;

    public ChessPosition(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public boolean isInBounds() {
        return row > 0 && row < 9 && col > 0 && col < 9;
    }

    /**
     * @return which row this position is in
     * 1 codes for the bottom row
     */
    public int getRow() {
        return row;
    }

    /**
     * @return which column this position is in
     * 1 codes for the left row
     */
    public int getColumn() {
        return col;
    }

    private int charToPosInt(char c) throws Exception {
        if (c > 48 && c < 57) {
            return c-48;
        } else if (c > 64 && c < 73) {
            return c-64;
        } else {
            throw new Exception("Invalid position");
        }
    }

    public ChessPosition(String pos) throws Exception {
        if (pos.length() != 2) {
            throw new Exception("Invalid position");
        }
        pos = pos.toUpperCase(Locale.ROOT);
        row = charToPosInt(pos.charAt(1));
        col = charToPosInt(pos.charAt(0));
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPosition that = (ChessPosition) o;
        return row == that.row && col == that.col;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }

    @Override
    public String toString() {
        return "ChessPosition{" +
                "row=" + row +
                ", col=" + col +
                '}';
    }
}