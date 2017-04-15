package benchmarking.real;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

import ea.FitnessFunction;

public abstract class GenericRealFunction implements FitnessFunction<Double> {
	
	protected double minInterval[], maxInterval[];
	private int dimension;
	public final static double infinity = Double.MAX_VALUE/3;
			

	protected GenericRealFunction(int dimension) {
		this.dimension = dimension;
	}
	
	/*
	 * Sets the same minvalue and maxvalue for every dimension
	 */
	protected void initGenericInterval(double minValue, double maxValue){
		this.minInterval = new double[dimension];
		this.maxInterval = new double[dimension];
		Arrays.fill(minInterval, minValue);
		Arrays.fill(maxInterval, maxValue);
	}
	
//	protected void initWithNoInterval(){
//		initGenericInterval(-1e9, 1e9);
//	}

	private boolean satisfy(double x, int i) {
		return minInterval[i] <= x && x <= maxInterval[i];
	}
	
	protected double satisfy(Double x[]){
		int count = 0;
		for(int i=0; i<x.length; i++)
			if( !satisfy(x[i],i) )
				count++;
		
		return ((double)count) / ((double)x.length); 
	}
	

	/*
	 * Generic reparing strategy, put the nearest boundary value.
	 */
	@Override
	public void repair(Double[] ind) {
		for (int i = 0; i < ind.length; i++) {
			if (!satisfy(ind[i], i)) {
				if (Math.abs(ind[i] - minInterval[i]) <= Math.abs(ind[i] - maxInterval[i]))
					ind[i] = minInterval[i];
				else
					ind[i] = maxInterval[i];
			}
		}
	}

	@Override
	public Double[] randomIndividual(int n) {
		Double[] x = new Double[dimension];
		for (int i = 0; i < x.length; i++) {
			x[i] = ThreadLocalRandom.current().
					nextDouble(minInterval[i], 
							   maxInterval[i]);
		}
		return x;
	}
	
	protected abstract double evaluateFunction(Double[] x);
	
	@Override
	public double evaluate(Double[] x) {
		double sat = satisfy(x); 
		if(sat > 0.0)
			return -infinity * sat;
		
		return evaluateFunction(x);
	}
}
