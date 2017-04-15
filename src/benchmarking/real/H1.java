package benchmarking.real;

import benchmarking.real.RealFunctionsAbstractFactory.RealFunctionFactory;

public class H1 extends GenericRealFunction  {
	
	static {
		RealFunctionsAbstractFactory.registerRealFunction("h1", new RealFunctionFactory() {
			@Override
			public GenericRealFunction create(int n) {
				return new H1();
			}
		});
	}

	public H1() {
		super(2);
		initGenericInterval(-100.0, 100.0);
	}

	@Override
	protected double evaluateFunction(Double[] x) {
		double x1 = x[0];
		double x2 = x[1];
		
		return (Math.pow(Math.sin(x1 - x2/8.0),2.0) + Math.sin(x2 + x1/8.0))/
			   (Math.sqrt( Math.pow(x1-8.6998,2.0) + Math.pow((x2-6.7665),2.0)) + 1);
	}

}
