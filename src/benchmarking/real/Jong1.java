package benchmarking.real;

import benchmarking.real.RealFunctionsAbstractFactory.RealFunctionFactory;

/**
 * M1 Function as defined by De Jong
 */
public class Jong1 extends GenericRealFunction {
	
	static {
		RealFunctionsAbstractFactory.registerRealFunction("Jong1", new RealFunctionFactory() {
			@Override
			public GenericRealFunction create(int n) {
				return new Jong1();
			}
		});
	}

	protected Jong1() {
		super(1);
		initGenericInterval(-5.12, 5.12);
	}

	@Override
	public double evaluateFunction(Double[] ind) {
		double ans = 0.0;
		for(int i=0; i<ind.length; i++)
			ans += ind[i] * ind[i];
		return -ans;
	}
	
	@Override
	public String toString(){
		return "Jong1";
	}
}
