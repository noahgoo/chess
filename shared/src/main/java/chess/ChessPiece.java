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
        } else if (piece.equals(PieceType.BISHOP)) {
            moves = BishopMoves(board, myPosition);
        } else if (piece.equals(PieceType.ROOK)) {
            moves = RookMoves(board,myPosition);
        } else if (piece.equals(PieceType.QUEEN)) {
            moves = QueenMoves(board, myPosition);
        } else if (piece.equals(PieceType.KING)) {
            moves = KingMoves(board, myPosition);
        } else if (piece.equals(PieceType.KNIGHT)) {
            moves = KnightMoves(board, myPosition);
        } else { throw new RuntimeException("not a valid piece");}
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
    }

    public Collection<ChessPosition> BishopMoves(ChessBoard board, ChessPosition myPosition) {
        // create an array for possible moves and set variable for the piece, set row/col
        Collection<ChessPosition> moves = new ArrayList<>();
        ChessPiece piece = board.getPiece(myPosition);
        ChessGame.TeamColor color = piece.getTeamColor();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        // check up and right diagonal
        for (int i = 1; row+i < 9 && col+i < 9; i++) {
            if (board.getPiece(new ChessPosition(row+i,col+i))== null) {
                moves.add(new ChessPosition(row+i,col+i));
            } else if (board.getPiece(new ChessPosition(row+i,col+i)).getTeamColor()!=color) {
                    moves.add(new ChessPosition(row+i,col+i));
                    break;
            } else { break;}
        }

        // check up and left diagonal
        for (int i = 1; row+i < 9 && col-i > 0; i++) {
            if (board.getPiece(new ChessPosition(row + i, col - i)) == null) {
                moves.add(new ChessPosition(row + i, col - i));
            } else if (board.getPiece(new ChessPosition(row+i,col-i)).getTeamColor()!=color) {
                    moves.add(new ChessPosition(row+i,col-i));
                    break;
            } else { break;}
        }

        // check down and right diagonal
        for (int i = 1; row-i > 0 && col+i < 9; i++) {
            if (board.getPiece(new ChessPosition(row-i,col+i))== null) {
                moves.add(new ChessPosition(row-i,col+i));
            } else if (board.getPiece(new ChessPosition(row-i,col+i)).getTeamColor()!=color) {
                moves.add(new ChessPosition(row - i, col + i));
                break;
            } else { break;}
        }

        // check down and left diagonal
        for (int i = 1; row-i > 0 && col-i > 0; i++) {
            if (board.getPiece(new ChessPosition(row - i, col - i)) == null) {
                moves.add(new ChessPosition(row - i, col - i));
            } else if (board.getPiece(new ChessPosition(row-i,col-i)).getTeamColor()!=color) {
                moves.add(new ChessPosition(row-i,col-i));
                break;
            } else { break;}
        }
        return moves;
    }

    public Collection<ChessPosition> RookMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessPosition> moves = new ArrayList<>();
        ChessGame.TeamColor color = board.getPiece(myPosition).getTeamColor();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        // check spaces in front
        for (int i = 1; row+i<9; i++) {
            if (board.getPiece(new ChessPosition(row+i,col))==null) {
                moves.add(new ChessPosition(row+i,col));
            } else if (board.getPiece(new ChessPosition(row+i,col)).getTeamColor()!=color) {
                moves.add(new ChessPosition(row+i,col));
                break;
            } else { break;}
        }
        // check behind
        for (int i = 1; row-i>0; i++) {
            if (board.getPiece(new ChessPosition(row-i,col))==null) {
                moves.add(new ChessPosition(row-i,col));
            } else if (board.getPiece(new ChessPosition(row-i,col)).getTeamColor()!=color) {
                moves.add(new ChessPosition(row-i,col));
                break;
            } else { break;}
        }
        // check right
        for (int i=1;col+i<9;i++) {
            if (board.getPiece(new ChessPosition(row,col+i))==null) {
                moves.add(new ChessPosition(row,col+i));
            } else if (board.getPiece(new ChessPosition(row,col+i)).getTeamColor()!=color) {
                moves.add(new ChessPosition(row,col+i));
                break;
            } else {break;}
        }
        // check left
        for (int i=1;col-i>0;i++) {
            if (board.getPiece(new ChessPosition(row,col-i))==null) {
                moves.add(new ChessPosition(row,col-i));
            } else if (board.getPiece(new ChessPosition(row,col-i)).getTeamColor()!=color) {
                moves.add(new ChessPosition(row,col-i));
                break;
            } else {break;}
        }

        return moves;
    }

    public Collection<ChessPosition> QueenMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessPosition> moves = new ArrayList<>();
        moves = BishopMoves(board, myPosition);
        moves.addAll(RookMoves(board, myPosition));
        return moves;
    }

    public Collection<ChessPosition> KingMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessPosition> moves = new ArrayList<>();
        ChessGame.TeamColor color = board.getPiece(myPosition).getTeamColor();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        
        // check all squares around King
        for (int i=-1;i<=1;i++) {
            for (int j=-1;j<=1;j++) {
                if (i != 0 || j != 0) {
                    if (checkSquare(board, new ChessPosition(row+i,col+j), color)) {
                        moves.add(new ChessPosition(row+i,col+j));
                    }
                }
            }
        }
        
        return moves;
    }

    public Collection<ChessPosition> KnightMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessPosition> moves = new ArrayList<>();
        ChessGame.TeamColor color = board.getPiece(myPosition).getTeamColor();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        // check up and down, left and right
        for (int i = -2;i<3;i+=4) {
            for (int j = -1;j<2;j+=2) {
                if (checkSquare(board, new ChessPosition(row+i,col+j), color)) {
                    moves.add(new ChessPosition(row+i,col+j));
                }
                if (checkSquare(board, new ChessPosition(row+j,col+i),color)) {
                    moves.add(new ChessPosition(row+j,col+i));
                }
            }
        }

        return moves;
    }

    private boolean checkSquare(ChessBoard board, ChessPosition position, ChessGame.TeamColor color) {
        if (position.getRow() > 0 && position.getRow() < 9 && position.getColumn() > 0 && position.getColumn() < 9) {
            if (board.getPiece(position) == null) {
                return true;
            } else {
                return board.getPiece(position).getTeamColor() != color;
            }
        }
        return false;
    }
}
//
