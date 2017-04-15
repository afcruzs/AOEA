package benchmarking.real;

import benchmarking.real.RealFunctionsAbstractFactory.RealFunctionFactory;

public class Jong2 extends GenericRealFunction {
	
	static {
		RealFunctionsAbstractFactory.registerRealFunction("Jong2", new RealFunctionFactory() {
			@Override
			public GenericRealFunction create(int n) {
				return new Jong2();
			}
		});
	}

	protected Jong2() {
		super(1);
		initGenericInterval(-5.12, 5.12);
	}

	@Override
	public double evaluateFunction(Double[] ind) {
		double ans = 0.0;
		for(int i=0; i<ind.length; i++)
			ans += ( (double) (i+1) ) *ind[i] * ind[i];
		return -ans;
	}
	
	@Override
	public String toString(){
		return "Jong2";
	}
}
