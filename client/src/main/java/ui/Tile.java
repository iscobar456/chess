package ui;

public class Tile {
    private char bg;
    private char fg;
    private String content;

    public Tile(char bg, char fg, String content) {
        this.bg = bg;
        this.fg = fg;
        this.content = content;
    }

    @Override
    public String toString() {
        return String.format("\u001B[48;5;%d;38;5;%dm %s \u001B[0m", (int) bg, (int) fg, content == null ? "" : content.toString());
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
