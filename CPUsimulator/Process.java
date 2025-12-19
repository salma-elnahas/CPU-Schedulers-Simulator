package CPUsimulator;

import java.util.ArrayList;
import java.util.List;

public class Process {
    private String name;
    private int arrivalTime;
    private int burstTime;
    private int priority = 0;
    private int remainingBurstTime; // for preemptive scheduling
    // results variables
    int completionTime;
    int turnaroundTime;
    int waitingTime;

    //AG scheduling variables
    private int quantum;           // current quantum 
    private int remainingQuantum;  // remaining quantum
    private int inputQuantum;   // for output
    boolean addedToQueue = false;
    List<Integer> quantumHistory = new ArrayList<>();


    public Process(String name, int arrivalTime, int burstTime, int priority, int quantum) {
        this.name = name;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.priority = priority;
        this.remainingBurstTime = burstTime; // initially remaining time is equal to burst time
        this.quantum = quantum;
        this.remainingQuantum = quantum;
        this.inputQuantum = quantum;
        this.quantumHistory.add(quantum);
    }

    public void setRemainingBurstTime(int remainingBurstTime) { this.remainingBurstTime = remainingBurstTime; }
    public void setCompletionTime(int completionTime) { this.completionTime = completionTime; }
    public void setWaitingTime(int waitingTime) { this.waitingTime = waitingTime; }

    public void setTurnaroundTime(int turnaroundTime) {
        this.turnaroundTime = turnaroundTime;
    }

    public void setPriority(int p) {
        this.priority = p;
    }

   public void setQuantum(int q) { 
        this.quantum = q;
        this.quantumHistory.add(q);
    }
    public void setRemainingQuantum(int rq) { this.remainingQuantum = rq; }
    public int getArrivalTime() {return arrivalTime;}
    public int getBurstTime() {return burstTime;}
    public int getPriority() {return priority;}
    public int getRemainingBurstTime() {return remainingBurstTime;}
    public String getName() {return name;}
    public int getCompletionTime() { return completionTime; }
    public int getWaitingTime() { return waitingTime; }
    public int getTurnaroundTime() {return turnaroundTime;}
    public int getQuantum() { return quantum; }
    public int getRemainingQuantum() { return remainingQuantum; }
    public int getInputQuantum() { return inputQuantum; }
    public List<Integer> getQuantumHistory() { return quantumHistory; }
   
}  