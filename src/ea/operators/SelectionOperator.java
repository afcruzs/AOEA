package ea.operators;

import java.util.ArrayList;

import ea.FitnessFunction;

public interface SelectionOperator<T> {
	void select(ArrayList<T[]> population, FitnessFunction<T> function);
}
