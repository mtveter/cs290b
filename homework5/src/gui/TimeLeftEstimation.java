/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

/**
 *
 * @author Varun
 */
public class TimeLeftEstimation {
    
    private long timeLeft = -1;
    private long startTime;
    
    public void start(){
        startTime = System.currentTimeMillis();
    }
    
    public void finished(){
    	timeLeft = 0;
    }
    
    public void updateEstimation(int progress){
        estimateTimeLeft(progress, System.currentTimeMillis()-startTime);
    }
    
    private void estimateTimeLeft(int progress, long timeSpent){
        if (progress > 0){
            long timePrPercent = timeSpent / progress;
            timeLeft = (100-progress) * timePrPercent;
        }
    }
    
    public String toString(){
        long seconds = timeLeft / 1000 + (timeLeft % 1000 == 0 ? 0 : 1); //Rounds up
        if (seconds < 60) {
            return seconds == 1 ? seconds+" second" : seconds+" seconds";
        } else {
            long minutes = (seconds/60);
            return minutes == 1 ? minutes+" minute" : minutes+" minutes";
        }
        
        //return seconds < 60 ? seconds+" seconds" : (seconds/60)+" minutes";
    }
    
    public static void main(String[] args){
        TimeLeftEstimation e = new TimeLeftEstimation();
        e.estimateTimeLeft(50, 415500);
    }
    
}
