package benchmarking.real;

import benchmarking.real.RealFunctionsAbstractFactory.RealFunctionFactory;

public class Rastrigin extends GenericRealFunction {
	
	static {
		RealFunctionsAbstractFactory.registerRealFunction("Rastrigin", new RealFunctionFactory() {
			@Override
			public GenericRealFunction create(int n) {
				return new Rastrigin(n);
			}
		});
	}

	protected Rastrigin(int dimension) {
		super(dimension);
		initGenericInterval(-5.12, 5.12);
	}

	double apply(double x) {
		return (x * x - 10.0 * Math.cos(2.0 * Math.PI * x));
	}

	@Override
	public double evaluateFunction(Double[] x) {
		int n = x.length;
		double f = 0.0;
		for (int i = 0; i < n; i++) {
			f += apply(x[i]);
		}
		return -(10.0 * n + f);
	}
	
	@Override
	public String toString(){
		return "Rastrigin";
	}

}
