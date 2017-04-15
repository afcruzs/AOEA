package experiments;

import java.io.PrintStream;
import java.util.ArrayList;

import benchmarking.real.GenericRealFunction;
import ea.FitnessFunction;
import ea.GeneticAlgorithm;
import ea.operators.RouletteSelection;
import ea.operators.real.LinearCrossover;
import ea.operators.real.SingleBitMutationGaussian;

public class RealExperimentGA extends RealExperiment {
	
	private GeneticAlgorithm<Double> GA;
	private ArrayList<Double[]> population;
	
	public RealExperimentGA(
			int popSize, int iterations, int numberOfExperiments,
			int dimension, GenericRealFunction function, String outFileName) {
		
		super(popSize, iterations, numberOfExperiments, dimension, function, outFileName);
		
	}


	@Override
	protected Double[] optimize(int popSize, int iterations, int n) {
		return GA.optimize(popSize, iterations, n, getPopulation());
	}

	@Override
	protected double getData(int i, int j) {
		return GA.getData()[i][j];
	}


	@Override
	protected void initAlgorithm(
			FitnessFunction<Double> function,
			boolean hasNegativeValues, PrintStream outputStream, int iterations) {
		
		this.GA = new GeneticAlgorithm<Double>(function, 
				new LinearCrossover(), 
				new SingleBitMutationGaussian(1.0, 0.0), 
				new RouletteSelection<Double>(hasNegativeValues));
		
	}


	@Override
	protected void initPopulation(int popSize, GenericRealFunction function, int n) {
		population = new ArrayList<Double[]>();
		for(int i=0; i<popSize; ++i){
			population.add( function.randomIndividual(n) );
		}
	}


	@Override
	protected ArrayList<Double[]> getPopulation() {
		ArrayList<Double[]> deepCopy = new ArrayList<Double[]>();
		for(int i=0; i<population.size(); i++){
			Double ind[] = new Double[ population.get(i).length ];
			for (int j = 0; j < ind.length; j++) {
				ind[j] = population.get(i)[j];
			}
			deepCopy.add(ind);
		}
		
		return deepCopy;
	}


	@Override
	protected String getLogInfo() {
		return "";
	}


	@Override
	protected void logInfo(PrintStream stream) {/* No additional info to add */}

}
