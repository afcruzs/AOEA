package experiments;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.StringTokenizer;

import benchmarking.real.Ackley;
import benchmarking.real.Bohachevsky;
import benchmarking.real.CentralTwoPeakTrap;
import benchmarking.real.Cigar;
import benchmarking.real.GenericRealFunction;
import benchmarking.real.Griewangk;
import benchmarking.real.H1;
import benchmarking.real.Himmelblau;
import benchmarking.real.Jong1;
import benchmarking.real.Jong2;
import benchmarking.real.Jong3;
import benchmarking.real.Plane;
import benchmarking.real.Rastrigin;
import benchmarking.real.RealFunctionsAbstractFactory;
import benchmarking.real.RosenbrockSaddle;
import benchmarking.real.Schaffer;
import benchmarking.real.Schwefel;
import benchmarking.real.Shubert2D;
import benchmarking.real.TwoPeakTrap;

/*
 * This is the main entry point to reproduce the experiments of
 * Self-adaptation of Genetic Operators Through Genetic Programming Techniques.
 * This class must be executed with two parameters as follows:
 * 
 * java MainRealExperiment CONFIG_FILE EXPERIMENTS_PER_THREAD
 * 
 * CONFIG_FILE : Defines the file on which the experiments configuration is stored 
 * EXPERIMENTS_PER_THREAD : Defines how many experiments are going to be executed sequentally
 * 							on a single thread.
 */

public class MainRealExperiment {
	
	public static void main(String[] args) throws IOException, ClassNotFoundException {

		Class.forName(Ackley.class.getName());
		Class.forName(Bohachevsky.class.getName());
		Class.forName(CentralTwoPeakTrap.class.getName());
		Class.forName(Griewangk.class.getName());
		Class.forName(Jong1.class.getName());
		Class.forName(Jong2.class.getName());
		Class.forName(Jong3.class.getName());
		Class.forName(Rastrigin.class.getName());
		Class.forName(RosenbrockSaddle.class.getName());
		Class.forName(Schwefel.class.getName());
		Class.forName(Shubert2D.class.getName());
		Class.forName(TwoPeakTrap.class.getName());
		Class.forName(Cigar.class.getName());
		Class.forName(H1.class.getName());
		Class.forName(Himmelblau.class.getName());
		Class.forName(Plane.class.getName());
		Class.forName(Schaffer.class.getName());
		
		if (args.length < 2) {
			System.out.println("Not enough arguments " + Arrays.toString(args));
			System.exit(0);
		}
		String experimentsFileName = args[0];
		int experimentsPerThread = Integer.parseInt(args[1]);

		FileReader reader = new FileReader(new File(experimentsFileName));
		BufferedReader in = new BufferedReader(reader);
		String line;
		MultithreadingExperiment multiExperiment = new MultithreadingExperiment();
		SingleThreadExperiment singleExperiment = new SingleThreadExperiment();
		String algorithmName = in.readLine();
		boolean added = false;
		while ((line = in.readLine()) != null) {
			StringTokenizer tok = new StringTokenizer(line, ";");
			int idx = 0;
			int dimension = -1;
			String outFileName = "nofilee";
			String functionName = "noFunction";
			int popSize = -1, iterations = -1, operatorsPopSize = -1, numberOfExperiments = -1;

			while (tok.hasMoreTokens()) {
				String token = tok.nextToken().trim();
				switch (idx) {
				case 0:
					functionName = token;
					break;
				case 1:
					dimension = Integer.parseInt(token);
					break;

				case 2:
					outFileName = algorithmName + "_" + token;
					break;
				case 3:
					popSize = Integer.parseInt(token);
					break;
				case 4:
					iterations = Integer.parseInt(token);
					break;
				case 5:
					operatorsPopSize = Integer.parseInt(token);
					break;
				case 6:
					numberOfExperiments = Integer.parseInt(token);
					break;

				default:
					break;
				}

				idx++;
			}

			GenericRealFunction function = RealFunctionsAbstractFactory.getFunction(functionName, dimension);
			RealExperiment experiment = getExperiment(algorithmName, popSize, iterations, 
													  operatorsPopSize, numberOfExperiments,
													  dimension, function, outFileName, functionName);

			singleExperiment.addExperiment(experiment);
			if (singleExperiment.countExperiments() == experimentsPerThread) {
				multiExperiment.addExperiment(singleExperiment);
				singleExperiment = new SingleThreadExperiment();
				added = true;
			} else {
				added = false;

			}

		}

		if (!added) {
			multiExperiment.addExperiment(singleExperiment);
		}

		in.close();

		for (SingleThreadExperiment ex : multiExperiment.experiments) {
			System.out.println(ex);
		}

		multiExperiment.runAll();

	}

	private static RealExperiment getExperiment(String algorithmName, int popSize, int iterations,
			int operatorsPopSize, int numberOfExperiments, int dimension, GenericRealFunction function,
			String outFileName, String functionName) {
		
		if(algorithmName.equals("AOEA")){
			RealExperimentAOEA ex = new RealExperimentAOEA(popSize, iterations, operatorsPopSize, numberOfExperiments, dimension, function, outFileName);
			if(numberOfExperiments == 1){
				try {
					ex.setOperatorsOutputStream(new PrintStream(new File("AOEA_bestOperators_" + functionName + "_" + dimension + "pop_" + popSize)));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
			return ex;
		}else if(algorithmName.equals("GA"))
			return new RealExperimentGA(popSize, iterations, numberOfExperiments, dimension, function, outFileName);
		else if(algorithmName.equals("HAEA")){
			RealExperimentHaea ex = new RealExperimentHaea(popSize, iterations, numberOfExperiments, dimension, function, outFileName);
			try {
				ex.setOperatorsOutputStream(new PrintStream(new File("HAEA_bestOperators_" + functionName + "_" + dimension + "pop_" + popSize)));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			return ex;
		}
		
		throw new RuntimeException(algorithmName + " is not a valid name ");
	}
}
