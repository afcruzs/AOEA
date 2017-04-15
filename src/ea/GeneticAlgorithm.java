package ea;

import java.util.ArrayList;
import java.util.Arrays;

import ea.operators.CrossoverOperator;
import ea.operators.MutationOperator;
import ea.operators.SelectionOperator;

/*
 * This is a vainilla Genetic Algorithm implementation
 * with real coding.
 * */
public class GeneticAlgorithm<T>{
	private FitnessFunction<T> function;
	private CrossoverOperator<T> crossover;
	private MutationOperator<T> mutation;
	private SelectionOperator<T> selection;
	private double data[][];

	public GeneticAlgorithm(
			FitnessFunction<T> function,
			CrossoverOperator<T> crossover, MutationOperator<T> mutation,
			SelectionOperator<T> selection) {
		this.function = function;
		this.crossover = crossover;
		this.mutation = mutation;
		this.selection = selection;
	}
	
	public T[] optimize(int popSize, int iterations, int n,ArrayList<T[]> population) {
		if(this.data == null)
			this.data = new double[iterations][4];
		
		double quality[] = new double[population.size()];
		
		int bestIdx = 0;
		for(int it=0; it<iterations; it++){
			
			selection.select(population, function);
			crossover.recombine(population, function);
			mutation.mutate(population, function);
			
			for(int i=0; i<popSize; i++){
				function.repair(population.get(i));
				quality[i] = function.evaluate(population.get(i));
			}
			
			for(int i=0; i<popSize; i++){
				if( quality[bestIdx] <= quality[i] )
					bestIdx = i;
			}
			
			double best = quality[bestIdx];
			double worst = worst(quality);
			double median = median(quality);
			double stdev = stdev( quality );
			data[it][0] = best;
			data[it][1] = median;
			data[it][2] = worst;
			data[it][3] = stdev;
		}

		return population.get(bestIdx);
	}
	
	public T[] optimize(int popSize, int iterations, int n) {
		
		ArrayList<T[]> population = new ArrayList<>();
		for (int i = 0; i < popSize; i++) {
			T ind[] = function.randomIndividual(n);
			
			function.repair(ind);
			population.add(ind);
		}
		
		return optimize(popSize,iterations,n,population);
		
	}

	public double[][] getData() {
		return data;
	}
	
	private double avg(double t[]) {
		double avg = 0;
		for (double a : t)
			avg += a;
		return avg / (double) t.length;
	}
	
	private double worst(double[] quality) {
		double worst = quality[0];
		for(int i=0; i<quality.length; i++)
			worst = Math.min(worst, quality[i]);
		return worst;
	}

	private double median(double[] quality) {
		double xd[] = new double[quality.length];
		System.arraycopy(quality, 0, xd, 0, quality.length-1);
		Arrays.sort(xd);
		return xd[ xd.length/2 ];
	}
	
	private double stdev(double t[]) {
		double std = 0.0, avg = avg(t);
		for (double a : t) {
			std += (a - avg) * (a - avg);
		}

		std /= (double) (t.length - 1);
		return Math.sqrt(std);
	}
}
