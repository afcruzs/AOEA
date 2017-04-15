package benchmarking.real;

import benchmarking.real.RealFunctionsAbstractFactory.RealFunctionFactory;

public class CentralTwoPeakTrap extends TwoPeakTrap {
	
	static {
		RealFunctionsAbstractFactory.registerRealFunction("CentralTwoPeakTrap", new RealFunctionFactory() {
			@Override
			public GenericRealFunction create(int n) {
				return new TwoPeakTrap();
			}
		});
	}
	
	public CentralTwoPeakTrap() {
		super();
	}
	
	/*
	 * The currentEvaluations is handled by the 
	 * parent class.
	 */
	@Override
	public double evaluateFunction(Double[] ind) {
		double x = ind[0];
		if( x < 10.0 ){
	      return -(160.0*x/15.0);
	    }else{
	      return (super.evaluateFunction(ind));
	    }
	}
	
	@Override
	public String toString(){
		return "CentralTwoPeakTrap";
	}
}
