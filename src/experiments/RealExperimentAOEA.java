package experiments;

import java.io.PrintStream;
import java.util.ArrayList;

import aoea.AdaptiveOperatorsEvolutionaryAlgorithm;
import benchmarking.real.GenericRealFunction;
import ea.FitnessFunction;
import ea.operators.SinglePointCrossover;
import ea.operators.SwapMutationOperator;
import ea.operators.UniformCrossover;
import ea.operators.real.AverageCrossover;
import ea.operators.real.LinearCrossover;
import ea.operators.real.SingleBitMutationGaussian;
import grammar.OneDimensionOperator;
import grammar.OneDimensionOperatorFactory;
import grammar.OperatorGrammar;
import grammar.TwoDimensionsOperator;
import grammar.TwoDimensionsOperatorFactory;

public class RealExperimentAOEA extends RealExperiment {
	
	private AdaptiveOperatorsEvolutionaryAlgorithm<Double> GGHAEA;
	private ArrayList<Double[]> population;
	private ArrayList<OperatorGrammar<Double>.GrammarNode> operatorsPopulation;
	private OperatorGrammar<Double> operator;
	private int operatorsPopSize;
	private PrintStream operatorsOutputStream;
	
	
	public RealExperimentAOEA(int popSize, int iterations, int operatorsPopSize, int numberOfExperiments,
			int dimension, GenericRealFunction function, String outFileName) {
		
		super(popSize, iterations, numberOfExperiments, dimension, function, outFileName);
		
		this.operatorsPopSize = operatorsPopSize;
	}
	
	public void setOperatorsOutputStream(PrintStream stream){
		operatorsOutputStream = stream;
	}
	
	@Override
	protected void initPopulation(int popSize, GenericRealFunction function, int n){
		operator = new OperatorGrammar<Double>(oned, twod, function);
		population = new ArrayList<Double[]>();
		for(int i=0; i<popSize; ++i){
			population.add( function.randomIndividual(n) );
		}
		
		operatorsPopulation = new ArrayList<>();
		for (int i = 0; i < operatorsPopSize; i++) {
			operatorsPopulation.add(operator.randomTree(4));
		}
	}
	
	@Override
	protected ArrayList<Double[]> getPopulation(){
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
	
	
	  ArrayList<OperatorGrammar<Double>.GrammarNode> getOperatorsPopulation(){
		  ArrayList<OperatorGrammar<Double>.GrammarNode> deepCopy = new ArrayList<>();
		
		for(int i=0; i<operatorsPopulation.size(); ++i){
			deepCopy.add(operatorsPopulation.get(i).clone());
		}
		
		return deepCopy;
	}
	
	@SuppressWarnings("rawtypes")
	OneDimensionOperatorFactory oned[] = { 
	new OneDimensionOperatorFactory<Double>() {
		@Override
		public OneDimensionOperator<Double> create() {
			return new SwapMutationOperator<Double>();
		}
	}, new OneDimensionOperatorFactory<Double>() {

		@Override
		public OneDimensionOperator<Double> create() {
			double standardDeviation = 1.0, mean = 0.0;
			return new SingleBitMutationGaussian(standardDeviation, mean);
		}
	} };

	@SuppressWarnings("rawtypes")
	TwoDimensionsOperatorFactory twod[] = { 
	new TwoDimensionsOperatorFactory<Double>() {
		@Override
		public TwoDimensionsOperator<Double> create() {
			return new SinglePointCrossover<Double>(false);
		}
	}, new TwoDimensionsOperatorFactory<Double>() {
		@Override
		public TwoDimensionsOperator<Double> create() {
			return new UniformCrossover<Double>(false);
		}
	}, new TwoDimensionsOperatorFactory<Double>() {
		@Override
		public TwoDimensionsOperator<Double> create() {
			return new AverageCrossover(false);
		}
	}, new TwoDimensionsOperatorFactory<Double>() {
		@Override
		public TwoDimensionsOperator<Double> create() {
			return new LinearCrossover();
		}
	} };

	@Override
	protected void initAlgorithm(FitnessFunction<Double> function, boolean hasNegativeValues, PrintStream outputStream,
			int iterations) {
		
		
		this.GGHAEA = new AdaptiveOperatorsEvolutionaryAlgorithm<Double>(
		function, 
		operator, 
		hasNegativeValues, 
		outputStream, 
		iterations);
		
		if(operatorsOutputStream != null)
			this.GGHAEA.setOperatorsOutputStream(operatorsOutputStream);
		
	}

	@Override
	protected Double[] optimize(int popSize, int iterations, int n) {
		return GGHAEA.optimize(popSize, iterations, n, operatorsPopSize, getPopulation(), getOperatorsPopulation() );
	}

	@Override
	protected double getData(int i, int j) {
		return GGHAEA.getData()[i][j];
	}

	@Override
	protected String getLogInfo() {
		return "_opp_" + operatorsPopSize;
	}

	@Override
	protected void logInfo(PrintStream stream) {
		stream.println("Operators pop size: " + operatorsPopSize);
	}

}
