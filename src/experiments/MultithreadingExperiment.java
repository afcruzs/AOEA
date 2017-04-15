package experiments;

import java.util.ArrayList;
import java.util.List;

public class MultithreadingExperiment {
	List<SingleThreadExperiment> experiments;
	
	public MultithreadingExperiment() {
		this.experiments = new ArrayList<>();
	}
	
	public void addExperiment(SingleThreadExperiment experiment){
		experiments.add(experiment);
	}
	
	public void runAll(){
		for(SingleThreadExperiment ex : experiments){
			ex.start();
		}
	}
	
	public String toString(){
		return experiments + "";
	}
}
