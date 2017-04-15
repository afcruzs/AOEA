package ea.operators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

import ea.FitnessFunction;

public class RouletteSelection<T> implements SelectionOperator<T> {
	
	private boolean hasNegativeValues;
	
	public RouletteSelection(boolean hasNegativeValues) {
		this.hasNegativeValues = hasNegativeValues;
	}
	
	private void reScaleData(double minValue, double maxValue, double data[]){
		double min = Double.MAX_VALUE;
		double max = Double.MIN_NORMAL;
		
		for(int i=0; i<data.length; i++){
			min = Math.min(min, data[i]);
			max = Math.max(max, data[i]);
		}
				
		//x := (c - a) * (z - y) / (b - a) + y
		for(int i=0; i<data.length; i++)
				data[i] = (data[i] - min) * (maxValue - minValue) / (max - min) + minValue;
	}

	@Override
	public void select(ArrayList<T[]> population, FitnessFunction<T> function) {

		double a [] = new double[population.size()];
        for(int i=0; i<a.length; i++)
            a[i] =  function.evaluate(population.get(i)) /** ( maximization ? 1.0 : -1.0 ) */;
       
        if(hasNegativeValues)
        	reScaleData( 0.0, 1.0, a );
        
        
//        if( !maximization ){
//        	double maxm = a[0];
//            for(int i=0; i<a.length; i++)
//            	if( a[i] < maxm )
//            		maxm = a[i];
//            
//            maxm *= 2;
//            for(int i=0; i<a.length; i++)
//            	a[i] += -maxm;
//            
//        }
        
        
        for(int i=1; i<a.length; i++)
            a[i] = a[i-1] + a[i];
        
//        System.out.println(Arrays.toString(a));
        
        
        ArrayList<T[]> buffer = new ArrayList<>();
        for(int k=0; k<a.length; k++){
            int low = 0, high = population.size()-1;
                 int mid;

                 double p = ThreadLocalRandom.current().nextDouble() * a[a.length-1];
                 while( low < high && high-low > 1 ){
                     mid = (low+high) / 2;
                     if( p <= a[mid] )
                         high = mid;
                     else
                         low = mid;
                 }

                 if( a[low] > p )
                     buffer.add( population.get(low) );
                 else
                     buffer.add( population.get(high) );
        }

        population.clear();
        population.addAll(buffer);
        buffer.clear();
        buffer = null;
        a = null;
	}

}
