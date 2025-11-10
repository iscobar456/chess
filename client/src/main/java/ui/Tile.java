package ui;

public class Tile {
    private char bg;
    private char fg;
    private Character content;

    public Tile(char bg, char fg, Character content) {
        this.bg = bg;
        this.fg = fg;
        this.content = content;
    }

    @Override
    public String toString() {
        return String.format("\033[48;5;%d;38;5;%dm%s\033[0m", getBg(), getFg(), getContent());
    }

    public char getBg() {
        return bg;
    }

    public void setBg(char bg) {
        this.bg = bg;
    }

    public char getFg() {
        return fg;
    }

    public void setFg(char fg) {
        this.fg = fg;
    }

    public Character getContent() {
        return content;
    }

    public void setContent(Character content) {
        this.content = content;
    }
}
