package benchmarking.real;

import benchmarking.real.RealFunctionsAbstractFactory.RealFunctionFactory;

/**
 * Constructor: Creates a Bohachevsky function
 * @param _one True if it is the Bohachevsky I function, false if it is the Bohachevsky II function
 */
public class Bohachevsky extends GenericRealFunction {
	
	static {
		RealFunctionsAbstractFactory.registerRealFunction("Bohachevsky1", new RealFunctionFactory() {
			@Override
			public GenericRealFunction create(int n) {
				return new Bohachevsky(n,true);
			}
		});
		RealFunctionsAbstractFactory.registerRealFunction("Bohachevsky2", new RealFunctionFactory() {
			@Override
			public GenericRealFunction create(int n) {
				return new Bohachevsky(n,false);
			}
		});
	}
	
	private boolean one;
	public Bohachevsky(int n, boolean one) {
		super(n);
		initGenericInterval(-100.0,100.0);
		this.one = one;
	}
	
	/**
	   * Evaluates the Bohachevsky I function over two real values
	   * @param x1 the first real value argument of the Bohachevsky function
	   * @param x2 the second real value argument of the Bohachevsky function
	   * @return the Bohachevsky value for the given values
	   */
	  public double evalI( double x1, double x2 ){
	    return ( x1*x1 - 2*x2*x2 - 0.3*Math.cos(3.0*Math.PI*x1)
	                   - 0.4*Math.cos(4.0*Math.PI*x2) + 0.7 );
	  }

	  /**
	   * Evaluates the Bohachevsky II function over two real values
	   * @param x1 the first real value argument of the Bohachevsky function
	   * @param x2 the second real value argument of the Bohachevsky function
	   * @return the Bohachevsky value for the given values
	   */
	  public double evalII( double x1, double x2 ){
	    return ( x1*x1 + 2*x2*x2 - 0.12*Math.cos(3.0*Math.PI*x1)*Math.cos(4.0*Math.PI*x2) + 0.3 );
	  }


	@Override
	public double evaluateFunction(Double[] x) {
		double f = 0.0;
	    int n = x.length - 1;
	    if( one ){
	      for( int i=0; i<n; i++ ){
	        f += evalI( x[i], x[i+1] );
	      }
	    }else{
	      for( int i=0; i<n; i++ ){
	        f += evalII( x[i], x[i+1] );
	      }
	    }
	    
	    return f;
	}
	
	@Override
	public String toString(){
		return "Bohachevsky " + (one ? "1" : "2");
	}
}
