package ea.operators.real;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import ea.FitnessFunction;
import ea.operators.MutationOperator;
import grammar.OneDimensionOperator;

public class SingleBitMutationGaussian implements MutationOperator<Double>, OneDimensionOperator<Double> {
	
	private double standardDeviation, mean;

	public SingleBitMutationGaussian(double standardDeviation, double mean) {
		super();
		this.standardDeviation = standardDeviation;
		this.mean = mean;
	}
	
	private double gaussian() {
		return ThreadLocalRandom.current().nextGaussian() * standardDeviation + mean;
	}

	@Override
	public Double[] operate(Double[] chromosome, FitnessFunction<Double> function) {
		Double[] copy = new Double[chromosome.length];
		for(int i=0; i<copy.length; i++)
			copy[i] = chromosome[i];
		
		copy[ ThreadLocalRandom.current().nextInt(copy.length) ] += gaussian();
		return copy;
	}

	@Override
	public void mutate(ArrayList<Double[]> population, FitnessFunction<Double> function) {
		double aux = 1.0 / (double) population.size();
		for (int i = 0; i < population.size(); i++) {
			if (ThreadLocalRandom.current().nextDouble() <= aux) {
				Double ind[] = population.get(ThreadLocalRandom.current().nextInt(population.size()));
				ind[ ThreadLocalRandom.current().nextInt(ind.length) ] += gaussian();
			}
		}
	}

	@Override
	public String getName() {
		return "SingleBitMutationGaussian_" + standardDeviation + "_" + mean;
	}


}
