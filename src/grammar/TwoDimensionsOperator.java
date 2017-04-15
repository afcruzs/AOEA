package grammar;

import ea.FitnessFunction;
import ea.operators.HaeaOperator;

public interface TwoDimensionsOperator<T> extends HaeaOperator {
	T[] operate( T parent1[], T parent2[], FitnessFunction<T> function );
}
