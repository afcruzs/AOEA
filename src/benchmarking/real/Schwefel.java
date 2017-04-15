package benchmarking.real;

import benchmarking.real.RealFunctionsAbstractFactory.RealFunctionFactory;

public class Schwefel extends GenericRealFunction {
	
	static {
		RealFunctionsAbstractFactory.registerRealFunction("Schwefel", new RealFunctionFactory() {
			@Override
			public GenericRealFunction create(int n) {
				return new Schwefel(n);
			}
		});
	}

	protected Schwefel(int dimension) {
		super(dimension);
		initGenericInterval(-500.0, 500.0);
	}
	
	double apply( double x ){
	    return ( -x * Math.sin(Math.sqrt(Math.abs(x))) );
	  }

	@Override
	public double evaluateFunction(Double[] x) {
		int n = x.length;
	    double f = 0.0;
	    for( int i=0; i<n; i++ ){
	      f += apply(x[i]);
	    }
	    return -(418.9829101*n + f);
	}
	
	@Override
	public String toString(){
		return "Schwefel";
	}

}
