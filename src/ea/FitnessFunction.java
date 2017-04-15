package ea;

public interface FitnessFunction<T> {
	double evaluate(T[] ind);

	void repair(T[] ind);

	T[] randomIndividual(int n);
}
