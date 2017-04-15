package experiments;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import benchmarking.real.GenericRealFunction;
import ea.FitnessFunction;

public abstract class RealExperiment implements GenericExperiment {
	
	private int popSize = 100, 
			   iterations = 500, 
			   numberOfExperiments = 3, dimension = 1000;
	
	private String outFileName;
	private GenericRealFunction function;
	private int n;
	private static int BEST = 0, MEDIAN = 1, WORST = 2, STDEV = 3;
	
	public RealExperiment(int popSize, int iterations,
			int numberOfExperiments, 
			int dimension, GenericRealFunction function, String outFileName) {
		this.popSize = popSize;
		this.iterations = iterations;
		this.numberOfExperiments = numberOfExperiments;
		this.dimension = dimension;
		this.outFileName = outFileName + "_pop_" + popSize;
		this.function = function;
	}
	
	protected abstract void initPopulation(int popSize, GenericRealFunction function, int n);
	
	protected abstract ArrayList<Double[]> getPopulation();
	
	protected abstract void initAlgorithm(
			FitnessFunction<Double> function,
			boolean hasNegativeValues, 
			PrintStream outputStream, int iterations);
	
	protected abstract Double[] optimize(int popSize, int iterations, int n);
	
	protected abstract double getData(int i, int j);
	
	protected abstract String getLogInfo();
	
	protected abstract void logInfo(PrintStream stream);
	
	/*
	 * Arguments:
	 * InputfilePath dimension outFilePath popSize iterations operatorsPerIndividual numberOfExperiments
	 * */
	@Override
	public void runExperiment() throws FileNotFoundException {
	    Format formatter = new SimpleDateFormat("YYYY-MM-dd_hh-mm-ss");
	    
	    
	    String logFileName = "Log_" + function.toString() + "_dimension_" + dimension + 
	    	"_pop_" + popSize + getLogInfo() + "_date_" + formatter.format(new Date()) + ".log";
//		String logFileName = "Dimension " + dimension + " " + fileName + "_" + formatter.format(new Date()) + ".log";
		PrintStream log = new PrintStream(new File(logFileName));
//		log = System.out;
		
		try{
				n = dimension;
				
				log.println( "Running " + function.toString() );
				log.println("Degree: " + dimension);
				log.println("Outfile name: " + outFileName);
				log.println("Population size: " + popSize);
				log.println("Iterations: " + iterations);
				logInfo(log);
				log.println("Number of experiments: " + numberOfExperiments);
				log.println("Dimension: " + dimension);
				log.println("----------------- STARTING ---------------------");
				
				
				System.out.println( "Running " + function.toString() );
				System.out.println("Degree: " + dimension);
				System.out.println("Outfile name: " + outFileName);
				System.out.println("Population size: " + popSize);
				System.out.println("Iterations: " + iterations);
				logInfo(System.out);
				System.out.println("Number of experiments: " + numberOfExperiments);
				log.println("Dimension: " + dimension);
				System.out.println("----------------- STARTING ---------------------");
				
				
				initPopulation(popSize, function, n);
				
				double data[][] = new double[iterations][4];
				double rawData[][] = new double[iterations][numberOfExperiments];
				
				initAlgorithm(
						function, 
						true, 
						null, 
						iterations);
				
//				GrammarGeneticAlgorithmHaEa<Double> GGHAEA = new GrammarGeneticAlgorithmHaEa<Double>(
//						function, 
//						operator, 
//						true, 
//						null, 
//						iterations);
				
				for( int i=0; i < data.length; i++ ){
					data[i][BEST] = Double.NEGATIVE_INFINITY;
					data[i][WORST] = Double.POSITIVE_INFINITY;
				}
				
				Double bestEver = null;
				double lastGenerationFitness[] = new double[numberOfExperiments];
				for( int ex = 0; ex < numberOfExperiments; ex++ ){
					
					log.println("Running experiment #" + ex + " " + new Date());
					System.out.println("Running experiment #" + ex + " " + new Date());
					
					Double ans[] = optimize(popSize, iterations, n);
					double fitness = function.evaluate(ans);
					lastGenerationFitness[ex] = fitness;
					
					if( bestEver == null || fitness > bestEver )
						bestEver = fitness;
					
					
					for( int i=0; i < data.length; i++ ){
						rawData[i][ex] = getData(i, BEST);
 					}
				}
				
				log.println("Finished, now computing statistics..." +  new Date());
				System.out.println("Finished, now computing statistics..." + new Date());
				
				PrintStream outputStreamLastGen = new PrintStream(new File(outFileName + "_lastgeneration" ));
				for(int i=0; i<lastGenerationFitness.length; i++)
					outputStreamLastGen.println(lastGenerationFitness[i]);
				
				
				
				for(int i=0; i<rawData.length; i++){
					for(int j=0; j<rawData[i].length; j++){
						data[i][BEST] = Math.max(data[i][BEST], rawData[i][j]);
						data[i][WORST] = Math.min(data[i][WORST], rawData[i][j]);
					}
					
					data[i][MEDIAN] = median(rawData[i]);
					data[i][STDEV] = stdev(rawData[i], data[i][MEDIAN]);
				}
				
				log.println("Computing statistics... Finished" +  new Date());
				System.out.println("Computing statistics... Finished" + new Date());
				
				PrintStream outputStream = new PrintStream(new File(outFileName));
				outputStream.println("best\tmedian\tworst\tstdev");
				
				for( int i=0; i < data.length; i++ ){
					for( int j = 0; j < data[i].length; j++ ){
						outputStream.print( String.valueOf(data[i][j]) );
						if( j != data[i].length - 1 )
							outputStream.print("\t");
					}
					outputStream.println();
				}
				
				outputStream.close();
				log.println("Best reported value for " + function.toString() + " deg: " + dimension + " : " + bestEver );
				log.println("Check " + outFileName + " for data." );
				
				System.out.println("Best reported value for " + function.toString() + " deg: " + dimension + " : " + bestEver +
						"\nCheck " + outFileName + " for data."  );
				log.flush();
				log.close();
		}catch(Exception ex){
			ex.printStackTrace(log);			
			ex.printStackTrace(System.out);
		}
	}
	
	private double median(double[] quality) {
		double xd[] = new double[quality.length];
		System.arraycopy(quality, 0, xd, 0, quality.length-1);
		Arrays.sort(xd);
		return xd[ xd.length/2 ];
	}
	
	private double stdev(double[] quality, double estimator){
		double std = 0.0;
		for (double a : quality) {
			std += (a - estimator) * (a - estimator);
		}

		std /= (double) (quality.length - 1);
		return Math.sqrt(std);
	}

	

	@Override
	public String toString() {
		return "RealExperiment [popSize=" + popSize + ", iterations="
				+ iterations + ", operatorsPerIndividual="
				+ getLogInfo() + ", numberOfExperiments="
				+ numberOfExperiments + ", dimension=" + dimension + 
				", outFileName=" + outFileName +
				", function=" + function.toString();
	}
}
