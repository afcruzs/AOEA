package benchmarking.real;

import benchmarking.real.RealFunctionsAbstractFactory.RealFunctionFactory;

public class Griewangk extends GenericRealFunction {
	
	static {
		RealFunctionsAbstractFactory.registerRealFunction("Griewangk", new RealFunctionFactory() {
			@Override
			public GenericRealFunction create(int n) {
				return new Griewangk(n);
			}
		});
	}

	protected Griewangk(int dimension) {
		super(dimension);
		initGenericInterval(-600.0, 600.0);
	}

	@Override
	public double evaluateFunction(Double[] x) {
		int n = x.length;
	    double sum = 0.0;
	    double prod = 1.0;
	    for( int i=0; i<n; i++ ){
	      sum += x[i]*x[i]/4000.0;
	      prod *= Math.cos(x[i]/Math.sqrt(i+1.0));
	    }
	    return -(1.0 + sum - prod);
	}
	
	@Override
	public String toString(){
		return "Griewangk";
	}
}
