package gui;

import java.util.ArrayList;
import java.util.HashMap;
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
        computerLatencies.put(computer, new ArrayList<Double>());
    }
    
    public void addLatencyValue(String computer, double value){
        computerLatencies.get(computer).add(value);
    }
    
    public void setLatencyList(String computer, List<Double> latencies){
        computerLatencies.put(computer, latencies);
    }
    
    public double getAvgLatency(String computer){
        List<Double> latencies = computerLatencies.get(computer);
        double sum = 0;
        for (double d : latencies){
            sum += d;
        }
        return sum/latencies.size();
    }
    
    public Set<String> getComputers(){
        //TODO: return list of all computer names/IDs/IPs
        /*ArrayList<String> computerNames = new ArrayList<String>();
        computerNames.addAll(computerLatencies.keySet());
        return computerNames;*/
        return computerLatencies.keySet();
    }
    
}
