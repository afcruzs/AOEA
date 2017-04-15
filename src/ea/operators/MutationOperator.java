package ea.operators;

import java.util.ArrayList;

import ea.FitnessFunction;

public interface MutationOperator<T> {
	void mutate( ArrayList<T[]> population, FitnessFunction<T> function );
}
