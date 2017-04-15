package ea;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import ea.operators.HaeaOperator;
import grammar.OneDimensionOperator;
import grammar.OperatorGrammar;
import grammar.TwoDimensionsOperator;

/*
 * Implementation of HAEA (Hybrid Adaptive Evolutionary Algorithm) by J. Gomez
 * 
 * https://link.springer.com/chapter/10.1007/978-3-540-24854-5_113
 * 
 * Author: Andres Felipe Cruz Salinas
 * afcruzs@unal.edu.co
 */

public class PlainHaea<T> {
	private FitnessFunction<T> function;
	private List<HaeaOperator> operators;
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
	public PlainHaea(FitnessFunction<T> function,
			List<HaeaOperator> operators, boolean hasNegativeValues, 
			PrintStream outputStream, int iterations) {

		this.function = function;
		this.operators = operators;
		this.hasNegativeValues = hasNegativeValues;
		this.outputStream = outputStream;
		this.data = new double[iterations][4];
		this.operatorsOutputStream = null;
	}
	
	public void setOperatorsOutputStream(PrintStream stream){
		operatorsOutputStream = stream;
	}
	
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
	
	/*
	 * Operates an individual with a Haea operator, if needed, an exta
	 * individual is retrieved applying selective pressure.
	 * 
	 *  Note: In order to use the same classes of the other algorithms 
	 *  the HaeaOperator wrapper is introduced. 
	 */
	private T[] operate(HaeaOperator operator, T individual[], double quality[], ArrayList<T[]> population, FitnessFunction<T> function){
		if(operator instanceof OneDimensionOperator){
			OneDimensionOperator<T> oneDimensionOperator = (OneDimensionOperator<T>) operator;
			return oneDimensionOperator.operate(individual, function);
		}
		
		if(operator instanceof TwoDimensionsOperator){
			TwoDimensionsOperator<T> twoDimensionsOperator = (TwoDimensionsOperator<T>) operator;
			int extraIndividualIdx = rouletteSelection(population, quality);
			return twoDimensionsOperator.operate(individual, population.get(extraIndividualIdx), function);
		}
		
		throw new RuntimeException("Operator is not either OneDimension or TwoDimension");
		
	}

	public T[] optimize(int popSize, int iterations, int n, 
			ArrayList<T[]> population, 
			List<HaeaOperator> operators) {
		
		
		double quality[] = new double[population.size()];
		double rates[] = new double[operators.size()];
		
		for (int i = 0; i < popSize; i++) {
			quality[i] = function.evaluate(population.get(i));
		}

		for (int i = 0; i < operators.size(); ++i)
				rates[i] = ThreadLocalRandom.current().nextDouble();
		
		normalizeRates(rates);

		int bestIdx = 0;
		for (int it = 0; it < iterations; it++) {
			for (int i = 0; i < popSize; ++i) {
				T[] ind = population.get(i);
				double delta = ThreadLocalRandom.current().nextDouble();

				int idx = selectOperator(operators, rates);
				T[] child = operate(operators.get(idx), ind, quality, population, function);
				double childFitness = function.evaluate(child);
				if (childFitness > quality[i]) {
					reward(rates, idx, delta);
					
				} else {
					punish(rates, idx, delta);
				}
				
				if( childFitness >= quality[i] ){
					population.set(i, child);
					quality[i] = childFitness;
				}

				normalizeRates(rates);
				
			}
			
			
			for (int i = 0; i < popSize; i++) {
				if (quality[bestIdx] <= quality[i])
					bestIdx = i;
			}
			
			double best = quality[bestIdx];
			double worst = worst( quality );
			double median = median(quality);
			
			if(operatorsOutputStream != null && it == iterations-1){
				operatorsOutputStream.println("Generation: " + it);
				int i = 0;
				for(HaeaOperator operator : operators){
					operatorsOutputStream.println(operator.getName());
					operatorsOutputStream.println(rates[i]);
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


	private <G> int rouletteSelection(List<G> objects, double[] measure) {
		double a[] = new double[objects.size()];
		for (int i = 0; i < a.length; i++)
			a[i] = measure[i] /** ( maximization ? 1.0 : -1.0 ) */
		;

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

	private int selectOperator(List<HaeaOperator> operators, 
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

	void setRandom(int delta, ArrayList<T[]> population,
			FitnessFunction<T> function, int n) {
		for (int k = 0; k < delta; k++) {
			int idx = ThreadLocalRandom.current().nextInt(population.size());
			population.set(idx, function.randomIndividual(n));
		}
	}

	private void reScaleData(double minValue, double maxValue, double data[]) {
		double min = Double.MAX_VALUE;
		double max = Double.MIN_NORMAL;

		for (int i = 0; i < data.length; i++) {
			min = Math.min(min, data[i]);
			max = Math.max(max, data[i]);
		}

		// x := (c - a) * (z - y) / (b - a) + y
		for (int i = 0; i < data.length; i++)
			data[i] = (data[i] - min) * (maxValue - minValue) / (max - min)
					+ minValue;
	}

	public void select(ArrayList<T[]> population,
			ArrayList<OperatorGrammar<T>.GrammarNode> operators,
			FitnessFunction<T> function) {

		double a[] = new double[population.size()];
		for (int i = 0; i < a.length; i++)
			a[i] = function.evaluate(population.get(i)) /**
		 * ( maximization ? 1.0 :
		 * -1.0 )
		 */
		;

		if (hasNegativeValues)
			reScaleData(0.0, 1.0, a);

		for (int i = 1; i < a.length; i++)
			a[i] = a[i - 1] + a[i];

		ArrayList<T[]> buffer = new ArrayList<>();
		ArrayList<OperatorGrammar<T>.GrammarNode> buffer2 = new ArrayList<>();
		for (int k = 0; k < a.length; k++) {
			int low = 0, high = population.size() - 1;
			int mid;

			double p = ThreadLocalRandom.current().nextDouble()
					* a[a.length - 1];
			while (low < high && high - low > 1) {
				mid = (low + high) / 2;
				if (p <= a[mid])
					high = mid;
				else
					low = mid;
			}

			if (a[low] > p) {
				buffer.add(population.get(low));
				buffer2.add(operators.get(low));
			} else {
				buffer.add(population.get(high));
				buffer2.add(operators.get(high));
			}
		}

		population.clear();
		population.addAll(buffer);
		buffer.clear();
		buffer = null;
		a = null;

		operators.clear();
		operators.addAll(buffer2);
		buffer2.clear();
		buffer2 = null;
	}

	public T[] optimize(int popSize, int iterations, int n) {

		ArrayList<T[]> population = new ArrayList<>();
		for (int i = 0; i < popSize; i++) {
			T ind[] = function.randomIndividual(n);

			function.repair(ind);
			population.add(ind);
		}
		

		return optimize(popSize, iterations, n, 
						population, operators);

	}

}
