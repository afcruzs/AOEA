package grammar;

import ea.FitnessFunction;
import ea.operators.HaeaOperator;

public interface OneDimensionOperator<T> extends HaeaOperator {
	T[] operate( T chromosome[], FitnessFunction<T> function );
}
