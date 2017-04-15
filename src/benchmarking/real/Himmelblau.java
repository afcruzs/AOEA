package benchmarking.real;

import benchmarking.real.RealFunctionsAbstractFactory.RealFunctionFactory;

public class Himmelblau extends GenericRealFunction {
	
	static {
		RealFunctionsAbstractFactory.registerRealFunction("himmelblau", new RealFunctionFactory() {
			@Override
			public GenericRealFunction create(int n) {
				return new Himmelblau(n);
			}
		});
	}

	protected Himmelblau(int dimensions) {
		super(2);
		initGenericInterval(-6.0, 6.0);
	}

	@Override
	protected double evaluateFunction(Double[] x) {
		double x1 = x[0];
		double x2 = x[1];
		
		return -( Math.pow(x1 * x1 + x2 -11.0, 2.0) + Math.pow(x1 + x2 * x2 - 7 , 2.0) );
	}

}
