package chess.MovesCalc;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class pawnCalculator implements movesCalculator {
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        ChessGame.TeamColor color = board.getPiece(myPosition).getTeamColor();
        int move;
        if (color.equals(ChessGame.TeamColor.WHITE)) {
            move = 1;
        } else { move = -1;}

        if ((color.equals(ChessGame.TeamColor.WHITE)&&row!=8)||(color.equals(ChessGame.TeamColor.BLACK)&&row!=1)) {
            ChessPosition potential = new ChessPosition(row+move , col);
            if (board.getPiece(potential)==null) {
                moves.add(new ChessMove(myPosition, potential, null));
                if ((color.equals(ChessGame.TeamColor.WHITE)&&row==2)||(color.equals(ChessGame.TeamColor.BLACK)&&row==7)) {
                    ChessPosition potential2 = new ChessPosition(row+move*2,col);
                    if (board.getPiece(potential2)==null) {
                        moves.add(new ChessMove(myPosition, potential2, null));
                    }
                }
            }
        }
        for (int i=-1;i<2;i+=2) {
            ChessPosition potential = new ChessPosition(row+move,col+i);
            if (col+i<9&&col+i>0&&board.getPiece(potential)!=null&&board.getPiece(potential).getTeamColor()!=color) {
                moves.add(new ChessMove(myPosition, potential, null));
            }
        }
        Collection<ChessMove> moreMoves = new ArrayList<>();
        Collection<ChessMove> temp = new ArrayList<>();
        for (ChessMove place: moves) {
            if ((color.equals(ChessGame.TeamColor.WHITE)&&place.getEndPosition().getRow()==8)||(color.equals(ChessGame.TeamColor.BLACK)&&place.getEndPosition().getRow()==1)) {
                ChessPosition end = place.getEndPosition();
                moreMoves.add(new ChessMove(myPosition, end, ChessPiece.PieceType.QUEEN));
                moreMoves.add(new ChessMove(myPosition, end, ChessPiece.PieceType.ROOK));
                moreMoves.add(new ChessMove(myPosition, end, ChessPiece.PieceType.BISHOP));
                moreMoves.add(new ChessMove(myPosition, end, ChessPiece.PieceType.KNIGHT));
                temp.add(place);
            }
        }
        moves.addAll(moreMoves);
        if (!temp.isEmpty()) {
            for (ChessMove rm: temp) {
                moves.remove(rm);
            }
        }


        return moves;
    }
}
