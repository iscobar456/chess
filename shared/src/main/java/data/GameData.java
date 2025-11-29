package data;

import chess.ChessGame;

public record GameData(String gameName, int gameID, String whiteUsername, String blackUsername, ChessGame game) {}
