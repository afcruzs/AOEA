package ea.operators.real;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import ea.FitnessFunction;
import ea.operators.CrossoverOperator;
import grammar.TwoDimensionsOperator;

public class LinearCrossover implements CrossoverOperator<Double>, TwoDimensionsOperator<Double>  {

	@Override
	public Double[] operate(Double[] parent1, Double[] parent2, FitnessFunction<Double> function) {
		double alpha0 = ThreadLocalRandom.current().nextDouble();
		double negAlpha0 = 1.0 - alpha0;
		Double child[] = new Double[parent1.length];
		for(int i=0; i<parent1.length; i++){
			Double t1 = new Double(parent1[i]);
			Double t2 = new Double(parent2[i]);
			child[i] = t1 * alpha0 + t2 * negAlpha0;
		}
		
		return child;
	}

	@Override
	public void recombine(ArrayList<Double[]> population, FitnessFunction<Double> function) {
		for(int k=0; k<population.size(); k+=2){
			Double p1[] = population.get(k);
			Double p2[] = population.get(k+1);
			
			Double c1[] = new Double[p1.length];
			Double c2[] = new Double[p1.length];
			
			double alpha1 = ThreadLocalRandom.current().nextDouble();
			double alpha2 = ThreadLocalRandom.current().nextDouble();
			double negAlpha1 = 1.0 - alpha1;
			double negAlpha2 = 1.0 - alpha2;
			
			for(int i=0; i<p1.length; i++){
				c1[i] = alpha1 * p1[i] + negAlpha1 * p2[i];
				c2[i] = alpha2 * p1[i] + negAlpha2 * p2[i];
			}
			
			function.repair(c1);
			function.repair(c2);
			
			if(function.evaluate(c1) >= function.evaluate(p1))
				population.set(k, c1);
			
			if(function.evaluate(c2) >= function.evaluate(p2))
				population.set(k+1, c2);
		}
	}

	@Override
	public String getName() {
		return "LinearCrossover";
	}

}
