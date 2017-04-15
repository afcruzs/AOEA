package aoea;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.ThreadLocalRandom;

import ea.FitnessFunction;
import grammar.OperatorGrammar;

/*
 * AOEA Algorithm implementation as proposed in 
 * Self-adaptation of Genetic Operators Through Genetic Programming Techniques
 * GECCO 2017.
 * 
 * Author: Andres Felipe Cruz Salinas
 * afcruzs@unal.edu.co
 * */

public class AdaptiveOperatorsEvolutionaryAlgorithm<T> {
	private FitnessFunction<T> function;
	private OperatorGrammar<T> operator;
	private boolean hasNegativeValues;
	private PrintStream outputStream;
	private double data[][];
	private PrintStream operatorsOutputStream;
	
	/*
	 * function: Is the objective function to optimize
	 * 
	 * operator: The grammar to sample the operators, this is by default
	 * 			 the simple binary tree based grammar with atomic operators
	 * 
	 * hasNegativeValues: Flag to indicate if the objective function has 
	 * 					  negative values.
	 * 
	 * outputStream: An stream to print the values of the algorithm per iteration.
	 * 
	 * iterations: Number of iterations to run the algorithm, this is used in order to
	 * 			   reserve space to store statistics, it must be consistent when calling
	 * 			   the optimize method.
	 * */
	public AdaptiveOperatorsEvolutionaryAlgorithm(FitnessFunction<T> function,
			OperatorGrammar<T> operator, boolean hasNegativeValues, 
			PrintStream outputStream, int iterations) {

		this.function = function;
		this.operator = operator;
		this.hasNegativeValues = hasNegativeValues;
		this.outputStream = outputStream;
		this.data = new double[iterations][4];
		this.operatorsOutputStream = null;
	}
	
	public void setOperatorsOutputStream(PrintStream stream){
		operatorsOutputStream = stream;
	}
	
	
	/*
	 * This is the actual algorithm implementation.
	 * 
	 * n : the dimension of the objective function
	 * operatorsPopSize: The population size
	 * population: A previously initialized population
	 * operators: An initialized operators population
	 * 
	 * Returns the best individual at the end of the algorithm
	 * */
	public T[] optimize(int popSize, int iterations, 
			int n, int operatorsPopSize, 
			ArrayList<T[]> population, 
			ArrayList<OperatorGrammar<T>.GrammarNode> operators) {
		
		//Stores the fitness of the population
		double quality[] = new double[population.size()];

		//Stores the quality measure of the operators
		double rates[] = new double[operatorsPopSize];
		
		//Auxiliary array to count the votes
		int auxRates[] = new int[operatorsPopSize];
		
		//Keeps track of the operators id
		int opId[] = new int[operatorsPopSize];
		for(int i=0; i<operatorsPopSize; i++)
			opId[i] = i;
		
		for (int i = 0; i < popSize; i++)
			quality[i] = function.evaluate(population.get(i));
		/*
		 * Random initialization of the operators quality measure.
		 * It does not impact too much if the initialization is 
		 * deterministic.
		 * */
		for (int i = 0; i < operatorsPopSize; ++i)
				rates[i] = ThreadLocalRandom.current().nextDouble();
		
		normalizeRates(rates);
		
		
		//Actual algorithm
		int bestIdx = 0;
		for (int it = 0; it < iterations; it++) {
			/*
			 * CROSSOVER POPULATION (On paper: Algorithm 2)
			 */
			Arrays.fill(auxRates, 0);
			for (int i = 0; i < popSize; ++i) {
				T[] ind = population.get(i);
				int idx = selectOperator(operators, rates);
				OperatorGrammar<T>.GrammarNode operator = operators.get(idx);
				T extraInd[] = selectIndividual(population, quality);
				T[] child1 = operator.operate(ind, extraInd);
				T[] child2 = operator.operate(extraInd, ind);
				T[] child = best(child1, child2, function);
				double childFitness = function.evaluate(child);
				if (childFitness > quality[i]) {
					auxRates[idx] += 1;
				} else {
					auxRates[idx] -= 1;
				}
				
				if( childFitness >= quality[i] ){
					population.set(i, child);
					quality[i] = childFitness;
				}
			}
			
			for(int i=0; i<operatorsPopSize; i++){
				if(auxRates[i] > 0)
					reward(rates, i, ThreadLocalRandom.current().nextDouble());
				else if(auxRates[i] < 0)
					punish(rates, i, ThreadLocalRandom.current().nextDouble());
				
				normalizeRates(rates);
			}
			
			/*
			 * CROSSOVER OPERATORS (On paper: Algorithm 3)
			 * The operators population is shuffled so that the crossover
			 * is not so elitist. 
			 */
			
			for(int k = 0; k < operatorsPopSize; k++){
				int r = k + (int)(ThreadLocalRandom.current().nextDouble()*(operatorsPopSize - k));
				Collections.swap(operators, k, r);
				double tmp = rates[k];
				rates[k] = rates[r];
				rates[r] = tmp;
				
				
				int aux = opId[k];
				opId[k] = opId[r];
				opId[r] = aux;
			}
			
			for(int k=0; k < operatorsPopSize; k+=2){
				OperatorGrammar<T>.GrammarNode op1 = operators.get(k);
				OperatorGrammar<T>.GrammarNode op2 = operators.get(k + 1);
				ArrayList<OperatorGrammar<T>.GrammarNode> operatorsChildren = op1.recombine(op2);
				operators.set(k, operatorsChildren.get(0));
				operators.set(k + 1, operatorsChildren.get(1));
			}
			
			/*
			 * OPERATORS MUTATION (On paper: Algorithm 3)
			 */
			
			double prob = 1.0 / (double) popSize;
			
			for(OperatorGrammar<T>.GrammarNode operator : operators)
				if (ThreadLocalRandom.current().nextDouble() <= prob) 
					operator.mutate();
			
			/*
			 * From here there is only statistical measures and logging. 
			 */
			
			for (int i = 0; i < popSize; i++) {
				if (quality[bestIdx] <= quality[i])
					bestIdx = i;
			}
			
			double best = quality[bestIdx];
			double worst = worst( quality );
			double median = median(quality);
			
			if(operatorsOutputStream != null){
				operatorsOutputStream.println("Generation: " + it);
				int i = 0;
				for(OperatorGrammar<T>.GrammarNode operator : operators){
					StringBuilder sb = new StringBuilder();
					
					operatorsOutputStream.println(opId[i]);
					operatorsOutputStream.println(rates[i]);
					
					for(String node : operator.computePreorder())
						sb.append(node).append(" ");
					
					operatorsOutputStream.println(sb.toString().trim());
					
					sb.setLength(0);
					for(String node : operator.computeInOrder())
						sb.append(node).append(" ");
					
					operatorsOutputStream.println(sb.toString().trim());
					
					
					operatorsOutputStream.println();
					i++;
				}
			}
			
			
			double stdev = stdev( quality );
			if(outputStream!=null)
				outputStream.println(
					String.valueOf(best) + "\t" + 
					String.valueOf(median) + "\t" +
					String.valueOf(worst) + "\t" +
					String.valueOf(stdev)
				);
			
			data[it][0] = best;
			data[it][1] = median;
			data[it][2] = worst;
			data[it][3] = stdev;
		}
			
		if(operatorsOutputStream != null){
			 operatorsOutputStream.flush();
			 operatorsOutputStream.close();
		}
		return population.get(bestIdx);
	}
	
	public double[][] getData(){
		return data;
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
	
	
	private void reward(double[] rates, int idx, double delta) {
		rates[idx] = (1.0 + delta) * rates[idx];
	}

	private void punish(double[] rates, int idx, double delta) {
		rates[idx] = (1.0 - delta) * rates[idx];
	}

	private void normalizeRates(double[] rates) {
		
		double sum = 0.0;
		for (int i = 0; i < rates.length; i++) {
			sum += rates[i];
		}

		for (int i = 0; i < rates.length; i++) {
			rates[i] /= sum;
		}
	}

	static double min(double d[]) {
		double minm = d[0];
		for (int i = 1; i < d.length; i++)
			if (d[i] < minm)
				minm = d[i];

		return minm;
	}

	static double max(double a[]) {
		double ans = a[0];
		for (int i = 1; i < a.length; i++)
			ans = Math.max(ans, a[i]);

		return ans;
	}

	private T[] best(T[] child1, T[] child2, FitnessFunction<T> function) {
		return function.evaluate(child1) > function.evaluate(child2) ? child1 : child2;
	}
	
	/*
	 * Performs a roulette selection and returns the index of the 
	 * selected individual. It performs binary search to find the 
	 * selected index.
	 * 
	 * If the objective function does have negative numbers, it first
	 * re scale the data in the [0,1] interval.
	 */
	private <G> int rouletteSelection(ArrayList<G> objects, double[] measure) {
		double a[] = new double[objects.size()];
		for (int i = 0; i < a.length; i++)
			a[i] = measure[i];

		if (hasNegativeValues)
			reScaleData(0.0, 1.0, a);

		for (int i = 1; i < a.length; i++)
			a[i] = a[i - 1] + a[i];

		int low = 0, high = objects.size() - 1;
		int mid;

		double p = ThreadLocalRandom.current().nextDouble() * a[a.length - 1];
		while (low < high && high - low > 1) {
			mid = (low + high) / 2;
			if (p <= a[mid])
				high = mid;
			else
				low = mid;
		}

		int idx = -1;
		if (a[low] > p) {
			idx = low;
		} else {
			idx = high;
		}

		return idx;
	}

	private T[] selectIndividual(ArrayList<T[]> population, double[] quality) {
		return population.get(rouletteSelection(population, quality));
	}

	private int selectOperator(ArrayList<OperatorGrammar<T>.GrammarNode> operators, 
							   double[] rates) {
		
		return rouletteSelection(operators, rates);

	}

	private static double avg(double t[]) {
		double avg = 0;
		for (double a : t)
			avg += a;
		return avg / (double) t.length;
	}

	private static double stdev(double t[]) {
		double std = 0.0, avg = avg(t);
		for (double a : t) {
			std += (a - avg) * (a - avg);
		}

		std /= (double) (t.length - 1);
		return Math.sqrt(std);
	}
	
	/*
	 * Random population initialization. 
	 */
	void setRandom(int delta, ArrayList<T[]> population,
			FitnessFunction<T> function, int n) {
		for (int k = 0; k < delta; k++) {
			int idx = ThreadLocalRandom.current().nextInt(population.size());
			population.set(idx, function.randomIndividual(n));
		}
	}
	
	/*
	 * Re scales the data in a range. 
	 */
	private void reScaleData(double minValue, double maxValue, double data[]) {
		double min = Double.MAX_VALUE;
		double max = Double.MIN_NORMAL;

		for (int i = 0; i < data.length; i++) {
			min = Math.min(min, data[i]);
			max = Math.max(max, data[i]);
		}

		for (int i = 0; i < data.length; i++)
			data[i] = (data[i] - min) * (maxValue - minValue) / (max - min) + minValue;
	}
	
	/*
	 * Optimizes the objective function of this object.
	 * 
	 * popSize: The size of the population
	 * iterations: The number of iterations of the evolutionary algorithm
	 * n : The dimension of the objective function
	 * operatorsPopulation : The size of the operators population
	 * 
	 *  Returns the best individual at the end of the algorithm
	 */
	public T[] optimize(int popSize, int iterations, int n,
			int operatorsPopulation) {

		ArrayList<T[]> population = new ArrayList<>();
		for (int i = 0; i < popSize; i++) {
			T ind[] = function.randomIndividual(n);

			function.repair(ind);
			population.add(ind);
		}
		
		ArrayList<OperatorGrammar<T>.GrammarNode> operators = new ArrayList<>();
		for(int i=0; i<operatorsPopulation; i++){
			operators.add(operator.randomTree(4));
		}

		return optimize(popSize, iterations, n, 
						operatorsPopulation, population, operators);

	}
	
	/*
	 * Just an auxiliary method to shuffle 
	 * */
	public static void shuffle(int[] a) {
        int n = a.length;
        for (int i = 0; i < n; i++) {
            // choose index uniformly in [i, n-1]
            int r = i + (int) (ThreadLocalRandom.current().nextDouble() * (n - i));
            int swap = a[r];
            a[r] = a[i];
            a[i] = swap;
        }
    }
}
