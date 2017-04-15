package ea.operators;

import grammar.TwoDimensionsOperator;

import java.util.ArrayList;

import ea.FitnessFunction;

public class SinglePointCrossover<T> implements CrossoverOperator<T>, TwoDimensionsOperator<T> {
	
	private boolean pressure;
	
	public SinglePointCrossover(boolean pressure) {
		this.pressure = pressure;
	}

	@Override
	public void recombine(ArrayList<T[]> population, FitnessFunction<T> function) {
		for(int k=0; k<population.size(); k+=2){
			T p1[] = population.get(k);
			T p2[] = population.get(k+1);
			
			T child1[] = function.randomIndividual(p1.length);
			T child2[] = function.randomIndividual(p1.length);
			
			int pivot = p1.length/2;
			for(int i=0; i<=pivot; i++){
				child1[i] = p1[i];
				child2[i] = p2[i];
			}
			
			for(int i=pivot; i<p1.length; i++){
				child1[i] = p2[i];
				child2[i] = p1[i];
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
	public T[] operate(T[] parent1, T[] parent2, FitnessFunction<T> function) {
		
		T child1[] = function.randomIndividual(parent1.length);
		
		int pivot = parent1.length/2;
		for(int i=0; i<=pivot; i++){
			child1[i] = parent1[i];
		}
		
		for(int i=pivot; i<parent1.length; i++){
			child1[i] = parent2[i];
		}
		
		return child1;
	}

	@Override
	public String getName() {
		return "SinglePointCrossover_" + (pressure ? "pressure" : "noPressure");
	}

}
