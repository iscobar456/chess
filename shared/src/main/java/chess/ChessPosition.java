package chess;

/**
 * Represents a single square position on a chess board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPosition {

    public enum Row {
        A, B, C, D, E, F, G, H
    }
    private final Row row;
    private final int col;

    public ChessPosition(int row, int col) {
        /* Check position bounds */
        if (row > 7 || col > 7 || row < 0 || col < 0) {
            throw new RuntimeException(String.format("Invalid position: row %d, column %d", row, col));
        }
        this.row = Row.values()[row];
        this.col = col;
    }

    /**
     * @return which row this position is in
     * 1 codes for the bottom row
     */
    public int getRow() {
        return row.ordinal();
    }

    /**
     * @return which column this position is in
     * 1 codes for the left row
     */
    public int getColumn() {
        return col;
    }

    public String toString() {
        return String.format("%s%d", row.toString(), col + 1);
    }
}
