package ea.operators;

import java.util.ArrayList;

import ea.FitnessFunction;

public interface CrossoverOperator<T> {
	void recombine(ArrayList<T[]> population, FitnessFunction<T> function);
}
