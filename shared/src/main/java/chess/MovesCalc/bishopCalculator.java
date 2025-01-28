package chess.MovesCalc;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class bishopCalculator implements movesCalculator {

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        ChessGame.TeamColor color = board.getPiece(myPosition).getTeamColor();

        for (int i=1;row+i<9&&col+i<9;i++) {
            ChessPosition potential = new ChessPosition(row+i,col+i);
            if (board.getPiece(potential)==null) {
                moves.add(new ChessMove(myPosition, potential, null));
            } else if (board.getPiece(potential).getTeamColor()!=color) {
                moves.add(new ChessMove(myPosition, potential, null));
                break;
            } else {break;}
        }
        for (int i=1;row-i>0&&col+i<9;i++) {
            ChessPosition potential = new ChessPosition(row-i,col+i);
            if (board.getPiece(potential)==null) {
                moves.add(new ChessMove(myPosition, potential, null));
            } else if (board.getPiece(potential).getTeamColor()!=color) {
                moves.add(new ChessMove(myPosition, potential, null));
                break;
            } else {break;}
        }
        for (int i=1;row+i<9&&col-i>0;i++) {
            ChessPosition potential = new ChessPosition(row+i,col-i);
            if (board.getPiece(potential)==null) {
                moves.add(new ChessMove(myPosition, potential, null));
            } else if (board.getPiece(potential).getTeamColor()!=color) {
                moves.add(new ChessMove(myPosition, potential, null));
                break;
            } else {break;}
        }
        for (int i=1;row-i>0&&col-i>0;i++) {
            ChessPosition potential = new ChessPosition(row-i,col-i);
            if (board.getPiece(potential)==null) {
                moves.add(new ChessMove(myPosition, potential, null));
            } else if (board.getPiece(potential).getTeamColor()!=color) {
                moves.add(new ChessMove(myPosition, potential, null));
                break;
            } else {break;}
        }

        return moves;
    }
}
