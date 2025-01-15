package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    final private ChessGame.TeamColor teamColor;
    private ChessPiece.PieceType pieceType;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.teamColor = pieceColor;
        this.pieceType = type;
    }

    @Override
    public String toString() {
        return "ChessPiece{" +
                "teamColor=" + teamColor +
                ", pieceType=" + pieceType +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return teamColor == that.teamColor && pieceType == that.pieceType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamColor, pieceType);
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
        return teamColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
         return pieceType;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        // initialize arrays for moves and potential moves
        Collection<ChessMove> validMoves = new ArrayList<>();
        Collection<ChessPosition> potentialMoves = pieceMovesCalculator(board, myPosition);

        // put all potential moves into actual moves
        for (ChessPosition potentialMove : potentialMoves) {
            validMoves.add(new ChessMove(myPosition, potentialMove, null));
        }
        return validMoves;
    }

    public Collection<ChessPosition> pieceMovesCalculator(ChessBoard board, ChessPosition myPosition) {
        // determine pieceType and initialize arrayList for potential moves
        PieceType piece = board.getPiece(myPosition).getPieceType();
        Collection<ChessPosition> moves = new ArrayList<>();
        if (piece.equals(PieceType.PAWN)) {
            moves = PawnMoves(board, myPosition);
        }
        return moves;
    }

    public Collection<ChessPosition> PawnMoves(ChessBoard board, ChessPosition myPosition) {
        // create an array for possible moves and determine if piece is white or black, set mv accordingly
        Collection<ChessPosition> moves = new ArrayList<>();
        ChessPiece piece = board.getPiece(myPosition);

        // optimized movement
        // determine if black or white
        int i;
        ChessGame.TeamColor color;
        if (piece.getTeamColor().equals(ChessGame.TeamColor.WHITE)) {
            i = 1;
            color = ChessGame.TeamColor.WHITE;
        } else {
            i = -1;
            color = ChessGame.TeamColor.BLACK;
        }
        int row = myPosition.getRow();

        // check front space and two ahead if in starting position
        if (((row!=8 && color.equals(ChessGame.TeamColor.WHITE)) || (row!=1 && color.equals(ChessGame.TeamColor.BLACK))) && board.getPiece(new ChessPosition(myPosition.getRow() + i, myPosition.getColumn())) == null) {
            moves.add(new ChessPosition(myPosition.getRow() + i, myPosition.getColumn()));
            if (((row==2 && color.equals(ChessGame.TeamColor.WHITE)) || (row==7 && color.equals(ChessGame.TeamColor.BLACK))) && board.getPiece(new ChessPosition(myPosition.getRow() + i*2, myPosition.getColumn())) == null) {
                moves.add(new ChessPosition(myPosition.getRow() + i*2, myPosition.getColumn()));
            } // check for diagonal enemies to capture
        } else if (board.getPiece(new ChessPosition(myPosition.getRow() + i, myPosition.getColumn() + 1)).getTeamColor() != color) {
            moves.add(new ChessPosition(myPosition.getRow() + i, myPosition.getColumn() + 1));
        } else if (board.getPiece(new ChessPosition(myPosition.getRow() + i, myPosition.getColumn() - 1)).getTeamColor() != color) {
            moves.add(new ChessPosition(myPosition.getRow() + i, myPosition.getColumn() - 1));
        }
        return moves;

        // moves for WHITE
        // check for space in front and two spaces in front if in starting row
//        if (piece.getTeamColor().equals(ChessGame.TeamColor.WHITE)) {
//            if (myPosition.getRow()!=8 && board.getPiece(new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn())) == null) {
//                moves.add(new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn()));
//                if (myPosition.getRow() == 2 && board.getPiece(new ChessPosition(myPosition.getRow() + 2, myPosition.getColumn())) == null) {
//                    moves.add(new ChessPosition(myPosition.getRow() + 2, myPosition.getColumn()));
//                }
//                // check for diagonal enemies to capture
//            } else if (board.getPiece(new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() + 1)).getTeamColor().equals(ChessGame.TeamColor.BLACK)) {
//                moves.add(new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() + 1));
//            } else if (board.getPiece(new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() - 1)).getTeamColor().equals(ChessGame.TeamColor.BLACK)) {
//                moves.add(new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() - 1));
//            }
//            return moves;
//            // moves for BLACK
//            // check space in front and starting position
//        } else if (piece.getTeamColor().equals(ChessGame.TeamColor.BLACK)) {
//            if (myPosition.getRow()!=1 && board.getPiece(new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn())) == null) {
//                moves.add(new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn()));
//                if (myPosition.getRow() == 7 && board.getPiece(new ChessPosition(myPosition.getRow() - 2, myPosition.getColumn())) == null) {
//                    moves.add(new ChessPosition(myPosition.getRow() - 2, myPosition.getColumn()));
//                }
//                // check for diagonal enemies to capture
//            } else if (board.getPiece(new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() + 1)).getTeamColor().equals(ChessGame.TeamColor.BLACK)) {
//                moves.add(new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() + 1));
//            } else if (board.getPiece(new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() - 1)).getTeamColor().equals(ChessGame.TeamColor.BLACK)) {
//                moves.add(new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() - 1));
//            }
//            return moves;
//        }



    }

}
//
