package test;

import java.util.List;

// Data models to map JSON files
class TCJsonFile {
    private List<Testcase> testcases;

    public List<Testcase> getTestcases() {
        return testcases;
    }
}

class Testcase {
    private String name;
    private testInput input;
    private expectedOutput expectedOutput;

    public String getName() {
        return name;
    }

    public testInput getInput() {
        return input;
    }

    public expectedOutput getExpectedOutput() {
        return expectedOutput;
    }
}
// Custom data format for AG test cases
class AGTestcase {
    private testInput input;
    private expectedOutput expectedOutput;
    public testInput getInput() {
        return input;
    }
    public expectedOutput getExpectedOutput() {
        return expectedOutput;
    }
}

class testInput {
    private int contextSwitch;
    private int rrQuantum;
    private int agingInterval;
    private List<ProcessInput> processes;

    public int getContextSwitch() { return contextSwitch; }
    public int getRrQuantum() { return rrQuantum; }
    public int getAgingInterval() { return agingInterval; }
    public List<ProcessInput> getProcesses() { return processes; }
}

class ProcessInput {
    private String name;
    private int arrival;
    private int burst;
    private int priority;
    private int quantum;  // Used for AG scheduler 
    
    public String getName() { return name; }
    public int getArrival() { return arrival; }
    public int getBurst() { return burst; }
    public int getPriority() { return priority; }
    public int getQuantum() { return quantum; }
}

class expectedOutput {
    private schedularAlgorithm SJF;   
    private schedularAlgorithm RR;  
    private schedularAlgorithm Priority;  
    private List<String> executionOrder;
    private List<ProcessResult> processResults;
    private double averageWaitingTime;
    private double averageTurnaroundTime;

    public schedularAlgorithm getPreemptiveSJF() { return SJF;}

    public schedularAlgorithm getRoundRobin() { return RR;}

    public schedularAlgorithm getPreemptivePriority() {return Priority;}

    // For AG scheduler direct access
    public List<String> getExecutionOrder() { return executionOrder; }
    public List<ProcessResult> getProcessResults() { return processResults; }
    public double getAverageWaitingTime() { return averageWaitingTime; }
    public double getAverageTurnaroundTime() { return averageTurnaroundTime; }
}

class schedularAlgorithm {
    private List<String> executionOrder;
    private List<ProcessResult> processResults;
    private double averageWaitingTime;
    private double averageTurnaroundTime;

    public List<String> getExecutionOrder() { return executionOrder; }
    public List<ProcessResult> getProcessResults() { return processResults; }
    public double getAverageWaitingTime() { return averageWaitingTime; }
    public double getAverageTurnaroundTime() { return averageTurnaroundTime; }
}

class ProcessResult {
    private String name;
    private int waitingTime;
    private int turnaroundTime;
    private List<Integer> quantumHistory;  // Used for AG scheduler 
    
    public String getName() { return name; }
    public int getWaitingTime() { return waitingTime; }
    public int getTurnaroundTime() { return turnaroundTime; }
    public List<Integer> getQuantumHistory() { return quantumHistory; }
}
