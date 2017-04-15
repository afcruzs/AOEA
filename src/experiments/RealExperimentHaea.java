package experiments;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import benchmarking.real.GenericRealFunction;
import ea.FitnessFunction;
import ea.PlainHaea;
import ea.operators.HaeaOperator;
import ea.operators.SinglePointCrossover;
import ea.operators.SwapMutationOperator;
import ea.operators.UniformCrossover;
import ea.operators.real.AverageCrossover;
import ea.operators.real.LinearCrossover;
import ea.operators.real.SingleBitMutationGaussian;

public class RealExperimentHaea extends RealExperiment  {
	private PlainHaea<Double> GA;
	private ArrayList<Double[]> population;
	private List<HaeaOperator> operators;
	private PrintStream operatorsOutputStream;
	
	public RealExperimentHaea(
			int popSize, int iterations, int numberOfExperiments,
			int dimension, GenericRealFunction function, String outFileName) {
		
		super(popSize, iterations, numberOfExperiments, dimension, function, outFileName);
		
	}
	
	public void setOperatorsOutputStream(PrintStream stream){
		operatorsOutputStream = stream;
	}


	@Override
	protected Double[] optimize(int popSize, int iterations, int n) {
		return GA.optimize(popSize, iterations, n, getPopulation(), getPopulationOperators());
	}

	private List<HaeaOperator> getPopulationOperators() {
		return operators;
	}


	@Override
	protected double getData(int i, int j) {
		return GA.getData()[i][j];
	}


	@Override
	protected void initAlgorithm(
			FitnessFunction<Double> function,
			boolean hasNegativeValues, PrintStream outputStream, int iterations) {
		
		this.GA = new PlainHaea<>(function, operators, hasNegativeValues, outputStream, iterations);
		
		if(operatorsOutputStream != null)
			this.GA.setOperatorsOutputStream(operatorsOutputStream);
	}


	@Override
	protected void initPopulation(int popSize, GenericRealFunction function, int n) {
		population = new ArrayList<Double[]>();
		for(int i=0; i<popSize; ++i){
			population.add( function.randomIndividual(n) );
		}
		
		operators = new ArrayList<>();

		double standardDeviation = 1.0, mean = 0.0;
		
		operators.add(new LinearCrossover());
		operators.add(new AverageCrossover(false));
		operators.add(new UniformCrossover<Double>(false));
		operators.add(new SinglePointCrossover<Double>(false));
		operators.add(new SingleBitMutationGaussian(standardDeviation, mean));
		operators.add(new SwapMutationOperator<Double>());
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
