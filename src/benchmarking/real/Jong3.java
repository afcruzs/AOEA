package benchmarking.real;

import benchmarking.real.RealFunctionsAbstractFactory.RealFunctionFactory;

public class Jong3 extends GenericRealFunction {
	
	static {
		RealFunctionsAbstractFactory.registerRealFunction("Jong3", new RealFunctionFactory() {
			@Override
			public GenericRealFunction create(int n) {
				return new Jong3();
			}
		});
	}
	
	protected Jong3() {
		super(1);
		initGenericInterval(-1.0, 1.0);
	}

	@Override
	public double evaluateFunction(Double[] ind) {
		double ans = 0.0;
		for(int i=0; i<ind.length; i++)
			ans += Math.pow(Math.abs(ind[i]), (double)(i+1));
		return -ans;
	}
	
	@Override
	public String toString(){
		return "Jong3";
	}
}
