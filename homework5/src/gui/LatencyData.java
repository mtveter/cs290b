package gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Varun
 */
public class LatencyData {
    
    private HashMap<String, List<Double>> computerLatencies;
    
    public LatencyData(){
        computerLatencies = new HashMap<String, List<Double>>();
    }
    
    public void addComputer(String computer){
        computerLatencies.put(computer, Collections.synchronizedList(new ArrayList<Double>()));
    }
    
    public void addLatencyValue(String computer, double value){
    	computerLatencies.get(computer).add(value);
    	/*List<Double> latencies = computerLatencies.get(computer);
        synchronized(latencies){        	
        	latencies.add(value);
        }*/
    }
    
    /*public void setLatencyList(String computer, List<Double> latencies){
        computerLatencies.put(computer, latencies);
    }*/
    
    /*public double getAvgLatency(String computer){
        List<Double> latencies = computerLatencies.get(computer);
        double sum = 0;
        for (double d : latencies){
            sum += d;
        }
        return sum/latencies.size();
    }*/
    
    public static double getAverage(List<Double> list){
    	list = new ArrayList<Double>(list);
		double sum = 0.0;
		for (double d : list){
			sum += d;
		}
		return sum/list.size();
    }
    
    public List<Double> getMostRecentLatencies(String computer, int n){
    	List<Double> latencies = computerLatencies.get(computer);
    	int size;
    	if (latencies == null || (size = latencies.size()) <= n) return latencies;
    	else return Collections.synchronizedList(latencies.subList(size-n, size));
    }
    
    public Set<String> getComputers(){
        return computerLatencies.keySet();
    }
    
}
