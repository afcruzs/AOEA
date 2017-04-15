package benchmarking.real;

import benchmarking.real.RealFunctionsAbstractFactory.RealFunctionFactory;

public class RosenbrockSaddle extends GenericRealFunction {
	
	static {
		RealFunctionsAbstractFactory.registerRealFunction("RosenbrockSaddle", new RealFunctionFactory() {
			@Override
			public GenericRealFunction create(int n) {
				return new RosenbrockSaddle(n);
			}
		});
	}

	protected RosenbrockSaddle(int dimension) {
		super(dimension);
		initGenericInterval(-2.048, 2.048);
	}
	
	double apply( double x1, double x2 ){
	    double y = x1*x1 - x2;
	    return (100.0*y*y + (1.0-x1)*(1.0-x1));
	  }

	@Override
	public double evaluateFunction(Double[] x) {
		double f = 0.0;
	    int n = x.length - 1;
	    for( int i=0; i<n; i++ ){
	      f += apply( x[i], x[i+1] );
	    }
	    return -f;
	}
	
	@Override
	public String toString(){
		return "RosenbrockSaddle";
	}
	
}
