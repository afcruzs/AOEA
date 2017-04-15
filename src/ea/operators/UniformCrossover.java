package ea.operators;

import grammar.TwoDimensionsOperator;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import ea.FitnessFunction;

public class UniformCrossover<T> implements CrossoverOperator<T>, TwoDimensionsOperator<T> {
	
	private boolean pressure;
	
	public UniformCrossover(boolean pressure) {
		this.pressure = pressure;
	}

	@Override
	public void recombine(ArrayList<T[]> population, FitnessFunction<T> function) {
		for(int k=0; k<population.size(); k+=2){
			T p1[] = population.get(k);
			T p2[] = population.get(k+1);
			
			T child1[] = function.randomIndividual(p1.length);
			T child2[] = function.randomIndividual(p1.length);
			
			for(int i=0; i<p1.length; i++){
				child1[i] = ThreadLocalRandom.current().nextBoolean() ? p1[i] : p2[i];
				child2[i] = ThreadLocalRandom.current().nextBoolean() ? p1[i] : p2[i];
			}
			
			
			function.repair(child1);
			function.repair(child2);
			
			
			if( pressure ){
				if( function.evaluate(child1) >= function.evaluate(p1) ){
					population.set(k, child1);
//					System.arraycopy(child1, 0, p1, 0, p1.length);
				}
				
				if( function.evaluate(child2) >= function.evaluate(p2) ){
					population.set(k+1, child2);
//					System.arraycopy(child2, 0, p2, 0, p2.length);
				}
			}else{
				population.set(k, child1);
//				System.arraycopy(child1, 0, p1, 0, p1.length);
				population.set(k+1, child2);
//				System.arraycopy(child2, 0, p2, 0, p2.length);
			}
			
		}
	}

	@Override
	public T[] operate(T[] p1, T[] p2, FitnessFunction<T> function) {
		T child1[] = function.randomIndividual(p1.length);
		
		for(int i=0; i<p1.length; i++){
			child1[i] = ThreadLocalRandom.current().nextBoolean() ? p1[i] : p2[i];
		}
		
		return child1;
	}

	@Override
	public String getName() {
		return "UniformCrossover";
	}

}
