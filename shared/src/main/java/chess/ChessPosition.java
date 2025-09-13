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
        if (row > 8 || col > 8 || row < 1 || col < 1) {
            throw new RuntimeException(String.format("Invalid position: row %d, column %d", row, col));
        }
        this.row = Row.values()[row-1];
        this.col = col;
    }

    /**
     * @return which row this position is in
     * 1 codes for the bottom row
     */
    public int getRow() {
        return row.ordinal()+1;
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
