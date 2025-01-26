package ch.bbw.m320.hanoi;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * A mutable representation of a state of the game Tower of Hanoi.
 * Each disk is represented as a unique integer from 0 (smallest) to N (largest).
 * Each peg is organized as a list, where the first item represents the disk lowest in the stack.
 *
 * @param pegA usually the starting location
 * @param pegB usually the auxiliary pole
 * @param pegC usually the target location
 */
public record HanoiBoard(List<Integer> pegA, List<Integer> pegB, List<Integer> pegC) {

	public HanoiBoard {
		// Verify this to be a valid board
		Stream.of(pegA, pegB, pegC)
				.forEach(peg -> {
					if (!peg.stream().sorted(Comparator.reverseOrder()).toList().equals(peg)) {
						throw new IllegalStateException("Invalid peg: cannot stack large disks on small ones");
					}
				});

		var duplicates = Stream.concat(Stream.concat(pegA.stream(), pegB.stream()), pegC.stream())
				.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
				.entrySet()
				.stream()
				.filter(e -> e.getValue() > 1)
				.map(Map.Entry::getKey)
				.toList();

		if (!duplicates.isEmpty()) {
			throw new IllegalStateException("Duplicate disks found: " + duplicates);
		}
	}

	/**
	 * Create a new board with randomly distributed disks.
	 *
	 * @param numberOfPieces amount of disks
	 * @return a new random board
	 */
	public static HanoiBoard randomWithSize(int numberOfPieces) {
		var board = initializeWithSize(numberOfPieces);

		for (var i = board.pegA.iterator(); i.hasNext(); ) {
			var disk = i.next();
			var target = Peg.values()[new Random().nextInt(3)];
			if (target != Peg.A) {
				i.remove();
				board.byPeg(target).add(disk);
			}
		}
		return board;
	}

	/**
	 * Create a new board with all disks on peg A.
	 *
	 * @param numberOfPieces amount of disks
	 * @return a new default board
	 */
	public static HanoiBoard initializeWithSize(int numberOfPieces) {
		if (numberOfPieces <= 0) {
			throw new IllegalArgumentException("Must use at least one disk");
		}

		// Fill peg A from largest disk first to the smallest last: [N, ..., 2, 1, 0]
		var pegA = IntStream.iterate(numberOfPieces - 1, i -> i - 1)
				.limit(numberOfPieces)
				.boxed()
				.toList();

		return new HanoiBoard(pegA, List.of(), List.of()).copy();
	}

	/**
	 * @return true in case all disks are on peg C.
	 */
	public boolean isSolved() {
		return pegA.isEmpty() && pegB.isEmpty();
	}

	/**
	 * @return the total amount of disks.
	 */
	public int getNumberOfPieces() {
		return pegA.size() + pegB.size() + pegC.size();
	}

	/**
	 * @param diskSize the index of the disk to search
	 * @return on which peg the disk currently resides (or an exception if no such disk exists)
	 */
	public Peg getPegOfDiskWithIndex(int diskSize) {
		if (pegA.contains(diskSize)) {
			return Peg.A;
		}
		if (pegB.contains(diskSize)) {
			return Peg.B;
		}
		if (pegC.contains(diskSize)) {
			return Peg.C;
		}
		throw new IllegalStateException("There is no disk with size " + diskSize);
	}

	/**
	 * @param peg index of the peg (A, B, C).
	 * @return the corresponding list of disks.
	 */
	private List<Integer> byPeg(Peg peg) {
		return switch (peg) {
			case A -> pegA;
			case B -> pegB;
			case C -> pegC;
		};
	}

	/**
	 * @return a fresh (mutable) copy of this board.
	 */
	public HanoiBoard copy() {
		return new HanoiBoard(new ArrayList<>(pegA), new ArrayList<>(pegB), new ArrayList<>(pegC));
	}

	/**
	 * Modify a board by moving a disk.
	 *
	 * @param move from which peg to which peg a disk has to be moved
	 * @throws InvalidMoveException if this move is not allowed on the current board
	 */
	public void move(Move move) throws InvalidMoveException {
		List<Integer> fromPeg = byPeg(move.from());
		List<Integer> toPeg = byPeg(move.to());

		if (fromPeg.isEmpty()) {
			throw new InvalidMoveException("Cannot move from an empty peg: " + move.from());
		}

		int diskToMove = fromPeg.removeLast();

		if (!toPeg.isEmpty() && toPeg.getLast() < diskToMove) {
			fromPeg.add(diskToMove);

			throw new InvalidMoveException("Cannot place larger disk (" + diskToMove + ") on smaller disk ("
					+ toPeg.getLast() + ") on peg: " + move.to());
		}

		toPeg.add(diskToMove);
	}

	@Override
	public String toString() {
		return String.format("A:%s, B:%s, C:%s", pegA, pegB, pegC);
	}

	/**
	 * Identifies one of three pegs (rods/poles/sticks).
	 */
	public enum Peg {
		A, B, C
	}

	/**
	 * A potentially valid move of a single disk, oblivious of the actual board.
	 *
	 * @param from starting peg
	 * @param to   target peg
	 */
	public record Move(Peg from, Peg to) {
		public Move {
			if (from == to) {
				throw new InvalidMoveException("Cannot move to self");
			}
			if (from == null || to == null) {
				throw new InvalidMoveException("Invalid move");
			}
		}
	}
}