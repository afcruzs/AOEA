package benchmarking.real;

import benchmarking.real.RealFunctionsAbstractFactory.RealFunctionFactory;

public class Cigar extends GenericRealFunction {
	
	static {
		RealFunctionsAbstractFactory.registerRealFunction("cigar", new RealFunctionFactory() {
			@Override
			public GenericRealFunction create(int n) {
				return new Cigar(n);
			}
		});
	}

	protected Cigar(int dimension) {
		super(dimension);
		initGenericInterval(-100.0, 100.0);
	}

	@Override
	protected double evaluateFunction(Double[] x) {
		double x0 = x[0];
		double sum = 0.0;
		for(int i=0; i<x.length; ++i)
			sum += x[i] * x[i];
		
		return -(x0 * x0 + 1e6 * sum);
	}

}
