package ui;

import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import static ui.EscapeSequences.*;

public class ChessBoard {
    private static final int BOARD_SIZE_IN_SQUARES = 8;
    private static final String EMPTY = "   ";
    private static boolean flip;

    public static void drawChessBoard(chess.ChessBoard board, boolean flip) {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        ChessBoard.flip = flip;
        out.print(ERASE_SCREEN);
        out.println();

        drawHeaders(out);
        drawBoard(out, board);
        drawHeaders(out);

        setDefault(out);
        out.println();

    }

    private static void drawHeaders(PrintStream out) {
        setGray(out);
        String[] headers = { " a ", " b ", " c ", " d ", " e ", " f ", " g ", " h " };
        if (flip) {
            headers = new String[]{" h ", " g ", " f ", " e ", " d ", " c ", " b ", " a "};
        }

        out.print(EMPTY);
        for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; ++boardCol) {
            printHeaderText(out, headers[boardCol]);
        }
        out.print(EMPTY);

        setDefault(out);
        out.println();
    }

    private static void printHeaderText(PrintStream out, String player) {
        out.print(SET_BG_COLOR_LIGHT_GREY);
        out.print(SET_TEXT_COLOR_BLACK);

        out.print(player);
        setGray(out);
    }

    private static void drawBoard(PrintStream out, chess.ChessBoard board) {
        if (flip) {
            for (int boardRow = 1; boardRow < 9; ++boardRow) {
                drawRowOfSquares(out, boardRow, board);
            }
        } else {
            for (int boardRow = 8; boardRow > 0; --boardRow) {
                drawRowOfSquares(out, boardRow, board);
            }
        }
    }

    private static void drawRowOfSquares(PrintStream out, int rowNumber, chess.ChessBoard board) {
        drawEdgeSquare(out, rowNumber);
        boolean flag = rowNumber % 2 > 0;

        for (int col = 0; col < BOARD_SIZE_IN_SQUARES; ++col) {
            // check if it's flipped or not
            int column;
            if (flip) {
                column = 9 - (col + 1);
            } else {
                column = col + 1;
            }

            ChessPosition position = new ChessPosition(rowNumber, column);
            String piece = determinePiece(board, position);
            String color = determineColor(board, position);

            if (flag) {
                if (col % 2 > 0) {
                    drawLightBrownSquare(out, color, piece);
                } else {
                    drawBrownSquare(out, color, piece);
                }
            } else {
                if (col % 2 > 0) {
                    drawBrownSquare(out, color, piece);
                } else {
                    drawLightBrownSquare(out, color, piece);
                }
            }
        }

        drawEdgeSquare(out, rowNumber);
        setDefault(out);
        out.println();
    }

    private static String determinePiece(chess.ChessBoard board, ChessPosition position) {
        ChessPiece piece = board.getPiece(position);
        if (piece != null) {
            return switch (piece.getPieceType()) {
                case ChessPiece.PieceType.KING ->
                        piece.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_KING : BLACK_KING;
                case ChessPiece.PieceType.QUEEN ->
                        piece.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_QUEEN : BLACK_QUEEN;
                case ChessPiece.PieceType.ROOK ->
                        piece.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_ROOK : BLACK_ROOK;
                case ChessPiece.PieceType.BISHOP ->
                        piece.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_BISHOP : BLACK_BISHOP;
                case ChessPiece.PieceType.PAWN ->
                        piece.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_PAWN : BLACK_PAWN;
                case ChessPiece.PieceType.KNIGHT ->
                        piece.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_KNIGHT : BLACK_KNIGHT;
            };
        }
        return null;
    }

    private static String determineColor(chess.ChessBoard board, ChessPosition position) {
        ChessPiece piece = board.getPiece(position);
        if (piece!=null) {
            if (piece.getTeamColor().equals(ChessGame.TeamColor.WHITE)) {
                return SET_TEXT_COLOR_WHITE;
            } else {
                return SET_TEXT_COLOR_BLACK;
            }
        }
        return null;
    }

    private static void drawLightBrownSquare(PrintStream out, String teamColor, String piece) {
        printPiece(out, SET_BG_COLOR_LIGHT_BROWN, teamColor, piece);
    }

    private static void drawBrownSquare(PrintStream out, String teamColor, String piece) {
        printPiece(out, SET_BG_COLOR_BROWN, teamColor, piece);
    }

    private static void drawEdgeSquare(PrintStream out, int rowNumber) {
        setBlack(out);
        String row = " " + rowNumber + " ";
        printHeaderText(out, row);
    }

    private static void printPiece(PrintStream out, String bgColor, String teamColor, String piece) {
        out.print(bgColor);
        out.print(Objects.requireNonNullElse(teamColor, bgColor));
        out.print(Objects.requireNonNullElse(piece, EMPTY));
    }

    private static void setBlack(PrintStream out) {
        out.print(SET_BG_COLOR_DARK_GREY);
        out.print(SET_TEXT_COLOR_BLACK);
    }

    private static void setGray(PrintStream out) {
        out.print(SET_BG_COLOR_LIGHT_GREY);
        out.print(SET_TEXT_COLOR_LIGHT_GREY);
    }

    private static void setDefault(PrintStream out) {
        out.print("\u001B[0m");
    }

}
