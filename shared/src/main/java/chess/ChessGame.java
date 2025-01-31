package chess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private TeamColor teamTurn = TeamColor.WHITE;
    private ChessBoard board = new ChessBoard();

    public ChessGame() {
        this.board.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return this.teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        // Check if there is a piece in the startPosition first
        try {
        if (board.getPiece(startPosition)==null) {
            return null;
        } else {
            Collection<ChessMove> moves = board.getPiece(startPosition).pieceMoves(board, startPosition);
            Collection<ChessMove> invalidMoves = new ArrayList<>();
            ChessPiece piece = board.getPiece(startPosition);
            TeamColor teamColor = piece.getTeamColor();
            for (ChessMove move: moves) {
                ChessBoard clone = (ChessBoard) board.clone(); // clone the board
                ChessBoard temp = board;
                board = clone;
                // make the move and test for check
                board.addPiece(move.getStartPosition(), null);
                board.addPiece(move.getEndPosition(), piece);
                if (isInCheck(teamColor)) {
                    invalidMoves.add(move);
                }
                board = temp;
            }

            moves.removeAll(invalidMoves);
            return moves;
        }
    } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        // find King's position
        ChessPosition king = null;
        for (int row = 1; row<9 ;row++) {
            for (int col = 1; col<9 ;col++) {
                ChessPiece piece = board.getPiece(new ChessPosition(row,col));
                if (piece!=null&&piece.getPieceType().equals(ChessPiece.PieceType.KING)&&piece.getTeamColor().equals(teamColor)) {
                    king = new ChessPosition(row, col);
                }
            }
        }

        // make sure no opposing players can move on the king
        for (int row = 1; row<9;row++) {
            for (int col = 1; col<9;col++) {
                ChessPiece piece = board.getPiece(new ChessPosition(row, col));
                if (piece!=null&&piece.getTeamColor()!=teamColor) {
                    Collection<ChessMove> potentialMoves = piece.pieceMoves(board, new ChessPosition(row,col));
                    for (ChessMove move: potentialMoves) {
                        if (move.getEndPosition().equals(king)) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return this.board;
    }
}
