package benchmarking.real;

import benchmarking.real.RealFunctionsAbstractFactory.RealFunctionFactory;

public class Shubert2D  extends GenericRealFunction{
	
	static {
		RealFunctionsAbstractFactory.registerRealFunction("Shubert2D", new RealFunctionFactory() {
			@Override
			public GenericRealFunction create(int n) {
				return new Shubert2D();
			}
		});
	}

	protected Shubert2D() {
		super(2);
		initGenericInterval(-5.12, 5.12);
	}

	@Override
	public double evaluateFunction(Double[] ind) {
		double x1 = ind[0];
		double x2 = ind[1];
		double sum1 = 0.0, sum2 = 0.0;
		for(int i=1; i<=5; i++){
			sum1 += ((double)i)*Math.cos((i+1)*x1+i);
		}
		
		for(int i=1; i<=5; i++){
			sum2 += ((double)i)*Math.cos((i+1)*x2+i);
		}
		return -(sum1 * sum2);
	}
	
	@Override
	public String toString(){
		return "Shubert2D";
	}

}
