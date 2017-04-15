package benchmarking.real;

import benchmarking.real.RealFunctionsAbstractFactory.RealFunctionFactory;

public class Plane extends GenericRealFunction  {
	
	static {
		RealFunctionsAbstractFactory.registerRealFunction("plane", new RealFunctionFactory() {
			@Override
			public GenericRealFunction create(int n) {
				return new Plane(n);
			}
		});
	}

	protected Plane(int dimension) {
		super(dimension);
		initGenericInterval(-100.0, 100.0);
	}

	@Override
	protected double evaluateFunction(Double[] x) {
		return -x[0];
	}

}
