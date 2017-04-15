package benchmarking.real;

import benchmarking.real.RealFunctionsAbstractFactory.RealFunctionFactory;

public class Ackley extends GenericRealFunction {

	static {
		RealFunctionsAbstractFactory.registerRealFunction("ackley", new RealFunctionFactory() {
			@Override
			public GenericRealFunction create(int n) {
				return new Ackley(n);
			}
		});
	}

	public Ackley(int n) {
		super(n);
		initGenericInterval(-5.0, 5.0);
	}

	@Override
	public double evaluateFunction(Double[] x) {
		int n = x.length;
		double sum1 = 0.0;
		double sum2 = 0.0;
		for (int i = 0; i < n; i++) {
			sum1 += x[i] * x[i];
			sum2 += Math.cos(2.0 * Math.PI * x[i]);
		}
		sum1 /= n;
		sum2 /= n;

		return -(20.0 + Math.exp(1.0) - 20.0 * Math.exp(-0.2 * Math.sqrt(sum1)) - Math.exp(sum2));
	}
	
	@Override
	public String toString(){
		return "Ackley";
	}
}
