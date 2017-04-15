package experiments;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class SingleThreadExperiment extends Thread{
	private List<GenericExperiment> allExperiments;
	
	
	public SingleThreadExperiment() {
		this.allExperiments = new ArrayList<>();
	}
	
	public int countExperiments(){
		return allExperiments.size();
	}
	
	public void addExperiment(GenericExperiment experiment){
		this.allExperiments.add(experiment);
	}
	
	@Override
	public void run() {
		for(GenericExperiment experiment : allExperiments){
			try {
				experiment.runExperiment();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public String toString() {
		return allExperiments.toString();
	}
	
}
