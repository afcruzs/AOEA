package ea.operators;

import grammar.OneDimensionOperator;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import ea.FitnessFunction;

public class SwapMutationOperator<T> implements MutationOperator<T>, OneDimensionOperator<T> {

	@Override
	public void mutate(ArrayList<T[]> population, FitnessFunction<T> function) {
		double tmp = 1.0 / ((double)population.size());
		for(int i=0; i<population.size(); i++){
			if( ThreadLocalRandom.current().nextDouble() <= tmp ){
				T aux[] = population.get(i);
				int idx1 = ThreadLocalRandom.current().nextInt(aux.length);
				int idx2 = ThreadLocalRandom.current().nextInt(aux.length);
				
				T xd = aux[idx1];
				aux[idx1] = aux[idx2];
				aux[idx2] = xd;
			}
		}
		
	}

	@Override
	public T[] operate(T[] chromosome, FitnessFunction<T> function) {
		int idx1 = ThreadLocalRandom.current().nextInt(chromosome.length);
		int idx2 = ThreadLocalRandom.current().nextInt(chromosome.length);
		
		T child[] = function.randomIndividual(chromosome.length);
		for(int i=0; i<child.length; i++)
			child[i] = chromosome[i];
		
		T xd = child[idx1];
		child[idx1] = child[idx2];
		child[idx2] = xd;
		return child;
	}

	@Override
	public String getName() {
		return "SwapMutation";
	}

}
