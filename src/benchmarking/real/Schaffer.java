package benchmarking.real;

import benchmarking.real.RealFunctionsAbstractFactory.RealFunctionFactory;

public class Schaffer extends GenericRealFunction {
	
	static {
		RealFunctionsAbstractFactory.registerRealFunction("schaffer", new RealFunctionFactory() {
			@Override
			public GenericRealFunction create(int n) {
				return new Schaffer(n);
			}
		});
	}

	protected Schaffer(int dimension) {
		super(dimension);
		initGenericInterval(-100.0, 100.0);
	}

	@Override
	protected double evaluateFunction(Double[] x) {
		int n = x.length;
		double f = 0.0;
		for(int i=0; i<n-1; i++){
			f += Math.pow(x[i]*x[i] + x[i+1] * x[i+1], 0.25) * 
					(Math.sin(50*Math.pow(x[i]*x[i] + x[i+1]*x[i+1], 0.25)) + 1.0);
		}
		
		return -f;
	}

}
