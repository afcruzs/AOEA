package benchmarking.real;

import benchmarking.real.RealFunctionsAbstractFactory.RealFunctionFactory;

public class TwoPeakTrap extends GenericRealFunction {
	
	static {
		RealFunctionsAbstractFactory.registerRealFunction("TwoPeakTrap", new RealFunctionFactory() {
			@Override
			public GenericRealFunction create(int n) {
				return new TwoPeakTrap();
			}
		});
	}

	/*
	 * One dimensional version.
	 */
	protected TwoPeakTrap() {
		super(1);
		initGenericInterval(-15.0, 
				 			 15.0);
		
	}

	@Override
	public double evaluateFunction(Double[] ind) {
		double x = ind[0];
		if( x < 15.0 ){
	      return -(160.0*(15.0-x)/15.0);
	    }else{
	      return -(200.0*(x-15.0)/5.0);
	    }
	}
	
	@Override
	public String toString(){
		return "TwoPeakTrap";
	}

}
