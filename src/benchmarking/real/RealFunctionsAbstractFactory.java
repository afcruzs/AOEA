package benchmarking.real;

import java.util.HashMap;
import java.util.Map;

public class RealFunctionsAbstractFactory {

	static{
		factory = new HashMap<>();
	}

	public static interface RealFunctionFactory {
		GenericRealFunction create(int dimension);
	}

	private RealFunctionsAbstractFactory() {
	}

	private static Map<String, RealFunctionFactory> factory;

	public static void registerRealFunction(String functionName, RealFunctionFactory factory) {
		RealFunctionsAbstractFactory.factory.put(functionName.toLowerCase(), factory);
	}

	public static GenericRealFunction getFunction(String functionName, int dimension) {
		functionName = functionName.toLowerCase();
		if (!factory.containsKey(functionName))
			throw new IllegalArgumentException(functionName + " has not a registred factory");

		return factory.get(functionName).create(dimension);
	}
}
