package ea.operators.real;

import java.util.ArrayList;

import ea.FitnessFunction;
import ea.operators.CrossoverOperator;
import grammar.TwoDimensionsOperator;

public class AverageCrossover implements CrossoverOperator<Double>, TwoDimensionsOperator<Double> {
	
	private boolean pressure;

	public AverageCrossover(boolean pressure) {
		this.pressure = pressure;
	}

	@Override
	public Double[] operate(Double[] parent1, Double[] parent2, FitnessFunction<Double> function) {
		Double child[] = new Double[parent1.length];
		for(int i=0; i<child.length; i++)
			child[i] = (parent1[i]+parent2[i])/2.0;
		
		return child;
	}

	@Override
	public void recombine(ArrayList<Double[]> population, FitnessFunction<Double> function) {
		for(int k=0; k<population.size(); k+=2){
			Double p1[] = population.get(k);
			Double p2[] = population.get(k+1);
			
			Double c1[] = new Double[p1.length];
			Double c2[] = new Double[p1.length];
			
			for(int i=0; i<p1.length; i++){
				c1[i] = (p1[i]+p2[i])/2.0;
				c2[i] = -(p1[i]+p2[i])/2.0;
			}
			
			function.repair(c1);
			function.repair(c2);
			
			if(pressure && function.evaluate(c1) >= function.evaluate(p1))
				population.set(k, c1);
			
			if(pressure && function.evaluate(c2) >= function.evaluate(p2))
				population.set(k+1, c2);
		}
	}

	@Override
	public String getName() {
		return "AverageCrossover_" + (pressure ? "pressure" : "noPressure");
	}
}
