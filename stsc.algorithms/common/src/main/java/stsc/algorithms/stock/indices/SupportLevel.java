package stsc.algorithms.stock.indices;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.Optional;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import stsc.common.BadSignalException;
import stsc.common.Day;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.algorithms.StockAlgorithm;
import stsc.common.algorithms.StockAlgorithmInit;
import stsc.common.signals.SerieSignal;
import stsc.common.signals.SignalsSerie;
import stsc.signals.DoubleSignal;
import stsc.signals.series.LimitSignalsSerie;

import com.google.common.collect.TreeMultiset;

public class SupportLevel extends StockAlgorithm {

	private static final class PairComparator implements Comparator<Pair<Integer, Double>> {
		@Override
		public int compare(Pair<Integer, Double> arg0, Pair<Integer, Double> arg1) {
			if (arg0.getRight().compareTo(arg1.getRight()) == 0) {
				return arg0.getLeft().compareTo(arg1.getLeft());
			} else
				return arg0.getRight().compareTo(arg1.getRight());
		}
	};

	private static final PairComparator PAIR_COMPARATOR = new PairComparator();

	private final String subExecutionName;

	private final int N;
	private final int M;

	private int currentIndex = 0;
	private double sumOfNMininalValues = 0.0;

	private final LinkedList<Pair<Integer, Double>> elements = new LinkedList<Pair<Integer, Double>>();

	// by second value, order: 1, 6, 9, 12, 14, 16, 19, 24, 27
	private final TreeMultiset<Pair<Integer, Double>> mElementsSorted = TreeMultiset.create(PAIR_COMPARATOR);
	private final TreeMultiset<Pair<Integer, Double>> nElementsSorted = TreeMultiset.create(PAIR_COMPARATOR);

	public SupportLevel(StockAlgorithmInit init) throws BadAlgorithmException {
		super(init);
		if (init.getSettings().getSubExecutions().size() <= 0) {
			throw new BadAlgorithmException("sub executions settings for " + SupportLevel.class.toString()
					+ " should have at least one algorithm");
		}
		this.subExecutionName = init.getSettings().getSubExecutions().get(0);
		this.N = init.getSettings().getIntegerSetting("N", 8).getValue();
		this.M = init.getSettings().getIntegerSetting("M", 66).getValue();
	}

	@Override
	public Optional<SignalsSerie<SerieSignal>> registerSignalsClass(StockAlgorithmInit initialize) throws BadAlgorithmException {
		final int size = initialize.getSettings().getIntegerSetting("size", 2).getValue().intValue();
		return Optional.of(new LimitSignalsSerie<>(DoubleSignal.class, size));
	}

	@Override
	public void process(Day day) throws BadSignalException {
		final double value = getSignal(subExecutionName, day.getDate()).getContent(DoubleSignal.class).getValue();
		final Pair<Integer, Double> element = new ImmutablePair<Integer, Double>(currentIndex, value);
		final double averageFromLastNMinValues = getAvFromMins(element);
		addSignal(day.getDate(), new DoubleSignal(averageFromLastNMinValues));
		currentIndex += 1;
	}

	private double getAvFromMins(final Pair<Integer, Double> newE) {
		while (!elements.isEmpty() && elements.getLast().getLeft() < currentIndex - M) {
			final Pair<Integer, Double> v = elements.pollLast();
			if (nElementsSorted.contains(v)) {
				nElementsSorted.remove(v);
				if (!mElementsSorted.isEmpty()) {
					final Pair<Integer, Double> el = mElementsSorted.pollFirstEntry().getElement();
					sumOfNMininalValues += el.getRight();
					nElementsSorted.add(el);
				}
				sumOfNMininalValues -= v.getRight();
			} else if (mElementsSorted.contains(v)) {
				mElementsSorted.remove(v);
			}
		}
		if (nElementsSorted.size() < N) {
			elements.addFirst(newE);
			nElementsSorted.add(newE);
			sumOfNMininalValues += newE.getRight();
		} else {
			elements.addFirst(newE);
			final double maxValue = nElementsSorted.lastEntry().getElement().getRight();
			if (newE.getRight() < maxValue) {
				nElementsSorted.add(newE);
				final Pair<Integer, Double> v = nElementsSorted.pollLastEntry().getElement();
				mElementsSorted.add(v);
				sumOfNMininalValues += newE.getRight();
				sumOfNMininalValues -= v.getRight();
			} else {
				mElementsSorted.add(newE);
			}
		}
		return sumOfNMininalValues / (nElementsSorted.size());
	}
}
