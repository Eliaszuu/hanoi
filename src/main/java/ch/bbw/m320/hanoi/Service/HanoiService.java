package ch.bbw.m320.hanoi.Service;

import ch.bbw.m320.hanoi.HanoiBoard;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HanoiService {

    private HanoiBoard board = HanoiBoard.initializeWithSize(5);

    public HanoiBoard getHanoiBoard() {
        return board;
    }

    public HanoiBoard reset(int size) {
        return HanoiBoard.initializeWithSize(size);
    }

    public HanoiBoard move(HanoiBoard.Move move) {
        board.move(move);
        return board;
    }

    public HanoiBoard.Move getHint() {
        return getNextMove(board);
    }

    private HanoiBoard.Move getNextMove(HanoiBoard board) {
        if (board.isSolved()) {
            throw new IllegalStateException("Board is solved. No hint available.");
        }

        HanoiBoard.Peg largestDisk = board.getPegOfDiskWithIndex(board.getNumberOfPieces() - 1);

        if (largestDisk == HanoiBoard.Peg.C) {
            return getNextMove(removeLargestDisk(board));
        }

        if (largestDisk == HanoiBoard.Peg.B) {
            HanoiBoard.Move move = getNextMove(new HanoiBoard(board.pegB(), board.pegA(), board.pegC()));
            return new HanoiBoard.Move(
                    swapPeg(move.from(), HanoiBoard.Peg.A, HanoiBoard.Peg.B),
                    swapPeg(move.to(), HanoiBoard.Peg.A, HanoiBoard.Peg.B)
            );
        }

        if (board.pegC().isEmpty() && board.pegA().size() == 1) {
            return new HanoiBoard.Move(HanoiBoard.Peg.A, HanoiBoard.Peg.C);
        }

        HanoiBoard.Move move = getNextMove(removeLargestDisk(new HanoiBoard(board.pegA(), board.pegC(), board.pegB())));

        return new HanoiBoard.Move(
                swapPeg(move.from(), HanoiBoard.Peg.B, HanoiBoard.Peg.C),
                swapPeg(move.to(), HanoiBoard.Peg.B, HanoiBoard.Peg.C)
        );
    }

    private HanoiBoard.Peg swapPeg(HanoiBoard.Peg peg, HanoiBoard.Peg p1, HanoiBoard.Peg p2) {
        return (peg == p1) ? p2 : (peg == p2) ? p1 : peg;
    }

    private HanoiBoard removeLargestDisk(HanoiBoard board) {
        int largestDiskIndex = board.getNumberOfPieces() - 1;
        return new HanoiBoard(
                filterDisk(board.pegA(), largestDiskIndex),
                filterDisk(board.pegB(), largestDiskIndex),
                filterDisk(board.pegC(), largestDiskIndex)
        );
    }

    private List<Integer> filterDisk(List<Integer> peg, int disk) {
        return peg.stream().filter(d -> d != disk).toList();
    }


}
