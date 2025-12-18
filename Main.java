import static java.lang.Math.ceil;
import java.util.*;

class Process {
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


    
    Process(String name, int arrivalTime, int burstTime, int priority, int quantum) {
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

    void setRemainingBurstTime(int remainingBurstTime) { this.remainingBurstTime = remainingBurstTime; }
    void setCompletionTime(int completionTime) { this.completionTime = completionTime; }
    void setWaitingTime(int waitingTime) { this.waitingTime = waitingTime; }
    void setTurnaroundTime(int turnaroundTime) { this.turnaroundTime = turnaroundTime; }

   void setQuantum(int q) { 
        this.quantum = q;
        this.quantumHistory.add(q);
    }
    void setRemainingQuantum(int rq) { this.remainingQuantum = rq; }

    int getArrivalTime() {return arrivalTime;}
    int getBurstTime() {return burstTime;}
    int getPriority() {return priority;}
    int getRemainingBurstTime() {return remainingBurstTime;}
    String getName() {return name;}
    int getCompletionTime() { return completionTime; }
    int getWaitingTime() { return waitingTime; }
    int getTurnaroundTime() {return turnaroundTime;}
    int getQuantum() { return quantum; }
    int getRemainingQuantum() { return remainingQuantum; }
    int getInputQuantum() { return inputQuantum; }
    List<Integer> getQuantumHistory() { return quantumHistory; }
   
}





class CPUsimulator {
    // preemptive Shortest-Job First (SJF) Scheduling with context switching
    public void preemptiveSJF(List<Process> processes, int contextSwitch) {
        int currentT = 0;
        int completedP = 0;
        int numProcesses = processes.size();
        Process currentP = null;
        Process previousP = null;

        List<String> executionOrder = new ArrayList<>();

        while (completedP < numProcesses) {
            // Find shortest job
            Process shortest = null;
            int minimum = Integer.MAX_VALUE;
            // loop through processes 
            for (Process proc : processes) {
                // Check if process has arrived and has remaining time
                if (proc.getArrivalTime() <= currentT && proc.getRemainingBurstTime() > 0) {
                    if (proc.getRemainingBurstTime() < minimum) {
                        minimum = proc.getRemainingBurstTime();
                        shortest = proc;
                    }
                }
            }
            //  if no process found
            if (shortest == null) {
                currentT++;
                continue;
            }

            // context switch handling when switching processes
            if (currentP != null && !previousP.getName().equals(shortest.getName())) {
                currentT += contextSwitch;
                System.out.println(
                        "Context Switch at time: " + previousP.getName() + " to " + shortest.getName() + " at time "
                                + currentT);
            }
            currentP = shortest;

            // execute process for 1 time unit
            // to avoid duplicates in execution order
            int lastIndex = executionOrder.size() - 1;

            if (executionOrder.isEmpty() || !executionOrder.get(lastIndex).equals(currentP.getName())) {
                // add current process to execution order
                executionOrder.add(currentP.getName());
            }

            currentP.setRemainingBurstTime(currentP.getRemainingBurstTime() - 1);
            currentT++;

            // check if process is completed and calculate times
            if (currentP.getRemainingBurstTime() == 0) {
                completedP++;
                currentP.setCompletionTime(currentT);
                currentP.setTurnaroundTime(currentP.getCompletionTime() - currentP.getArrivalTime());
                currentP.setWaitingTime(currentP.getTurnaroundTime() - currentP.getBurstTime());
                System.out.println("Process " + currentP.getName() + " completed at time " + currentT);
            }

            previousP = currentP;

        }
        System.out.println("Execution Order: " + executionOrder);
        System.out.println("\nProcess\tArrival\tBurst\tCompletion\tTAT\tWT");
        System.out.println("---------------------------------------------------");
        double totalWaitingTime = 0;
        double totalTurnaroundTime = 0;
        for (Process proc : processes) {
            proc.setTurnaroundTime(proc.getCompletionTime() - proc.getArrivalTime());
            proc.setWaitingTime(proc.getTurnaroundTime() - proc.getBurstTime());
            System.out.println(proc.getName() + "\t" +
                    proc.getArrivalTime() + "\t" +
                    proc.getBurstTime() + "\t" +
                    proc.getCompletionTime() + "\t\t" +
                    proc.getTurnaroundTime() + "\t" +
                    proc.getWaitingTime());

            totalWaitingTime += proc.getWaitingTime();
            totalTurnaroundTime += proc.getTurnaroundTime();
        }
        System.out.println("---------------------------------------------------");
        System.out.println("Average Waiting Time: " + (totalWaitingTime / numProcesses));
        System.out.println("Average Turnaround Time: " + (totalTurnaroundTime / numProcesses));
    }

    // Round Robin (RR) with context switching
    public void RRContextSwitch(List<Process> processes, int timeQuantum, int contextSwitch) {
        int currentT = 0;
        int completedP = 0;
        int numProcesses = processes.size();
        int nextP = 0; // Track next process to execute

        Process currentP = null;
        Process previousP = null;

        Queue<Process> queue = new LinkedList<>();
        List<String> executionOrder = new ArrayList<>();

        // First sort processes by arrival time
        processes.sort(Comparator.comparingInt(Process::getArrivalTime));

        while (completedP < numProcesses) {
            // Add newly arrived processes to the queue
            while (nextP < numProcesses && processes.get(nextP).getArrivalTime() <= currentT) {
                queue.add(processes.get(nextP));
                System.out.println(
                        "Process " + processes.get(nextP).getName() + " added to the queue at time " + currentT);
                nextP++;
            }

            // If queue is empty, increment time
            if (queue.isEmpty()) {
                currentT++;
                continue;
            }

            // Dequeue the next process
            currentP = queue.poll();

            // Context switch handling when switching processes
            if (currentP != null && previousP != null && !previousP.getName().equals(currentP.getName())) {
                currentT += contextSwitch;
                System.out.println("Context Switch Consumed: " + contextSwitch + " from " + previousP.getName() + " to "
                        + currentP.getName()
                        + " at time " + currentT);
            }
            // Execute process for time quantum or remaining time
            int execTime = Math.min(timeQuantum, currentP.getRemainingBurstTime());

            // Add current process to execution order
            executionOrder.add(currentP.getName());

            // Update current execution time
            currentP.setRemainingBurstTime(currentP.getRemainingBurstTime() - execTime);
            currentT += execTime;
            // Insert newly arrived processes during execution
            while (nextP < numProcesses && processes.get(nextP).getArrivalTime() <= currentT) {
                queue.add(processes.get(nextP));
                System.out.println(
                        "Process " + processes.get(nextP).getName() + " added to the queue at time " + currentT);
                nextP++;
            }

            if (currentP.getRemainingBurstTime() > 0) {
                // Reinsert process to the end of the queue if not completed
                queue.add(currentP);
                System.out
                        .println("Process " + currentP.getName() + " re-added to the queue with remaining burst time: "
                                + currentP.getRemainingBurstTime() + " at time " + currentT);
            } else {
                // Process completed
                completedP++;
                currentP.setCompletionTime(currentT);
                currentP.setTurnaroundTime(currentP.getCompletionTime() - currentP.getArrivalTime());
                currentP.setWaitingTime(currentP.getTurnaroundTime() - currentP.getBurstTime());
                System.out.println("Process " + currentP.getName() + " completed at time " + currentT);
            }
            previousP = currentP;

        }
        System.out.println("Execution Order: " + executionOrder);
        System.out.println("\nProcess\tArrival\tBurst\tCompletion\tTAT\tWT");
        System.out.println("---------------------------------------------------");
        double totalWaitingTime = 0;
        double totalTurnaroundTime = 0;
        for (Process proc : processes) {
            proc.setTurnaroundTime(proc.getCompletionTime() - proc.getArrivalTime());
            proc.setWaitingTime(proc.getTurnaroundTime() - proc.getBurstTime());
            System.out.println(proc.getName() + "\t" +
                    proc.getArrivalTime() + "\t" +
                    proc.getBurstTime() + "\t" +
                    proc.getCompletionTime() + "\t\t" +
                    proc.getTurnaroundTime() + "\t" +
                    proc.getWaitingTime());

            totalWaitingTime += proc.getWaitingTime();
            totalTurnaroundTime += proc.getTurnaroundTime();
        }
        System.out.println("---------------------------------------------------");
        System.out.println("Average Waiting Time: " + (totalWaitingTime / numProcesses));
        System.out.println("Average Turnaround Time: " + (totalTurnaroundTime / numProcesses));

    }

}

class AGScheduler {
    Queue<Process> readyQueue = new LinkedList<>();
    Process current;
    List<Process> processes = new ArrayList<>();
    int currentTime = 0;
    int counterCompletedProcesses = 0;
    boolean endTurn = false;

    // for output
    List<String> executionOrder = new ArrayList<>();

    enum PickMode { FCFS, PRIORITY, SJF }
    PickMode nextPick = PickMode.FCFS;

    AGScheduler(List<Process> processes) {
        this.processes = processes;
    }

    void addProcess() { // add process upon arrival
        for (Process p : processes) {
            if (p.getArrivalTime() <= currentTime && !p.addedToQueue) {
                readyQueue.add(p);
                p.addedToQueue = true;
            }
        }
    }

    void recordExecution(Process p) {
        if (executionOrder.isEmpty() || !executionOrder.get(executionOrder.size() - 1).equals(p.getName())) {
            executionOrder.add(p.getName());
        }
    }

    // pick according to the mode, removing the chosen one from the queue
    Process pickNextProcess() {
        if (readyQueue.isEmpty()) return null;

        if (nextPick == PickMode.FCFS) {
            return readyQueue.poll();
        }

        Process best = null;

        if (nextPick == PickMode.PRIORITY) {
            int bestPriority = Integer.MAX_VALUE;
            for (Process p : readyQueue) {
                if (p.getPriority() < bestPriority) {
                    bestPriority = p.getPriority();
                    best = p;
                }
            }
        } else if (nextPick == PickMode.SJF) {
            int bestRem = Integer.MAX_VALUE;
            for (Process p : readyQueue) {
                if (p.getRemainingBurstTime() < bestRem) {
                    bestRem = p.getRemainingBurstTime();
                    best = p;
                }
            }
        }

        // remove chosen from queue
        if (best != null) {
            readyQueue.remove(best);
        }

        // after choosing once, go back to FCFS unless a new preemption sets it again
        nextPick = PickMode.FCFS;
        return best;
    }

    void startProcess() {
        current.setRemainingQuantum(current.getQuantum());
        recordExecution(current);
    }

    void caseiCheck() {
        // i) used all quantum and still not finished, add to end, quantum += 2
        if (current.getRemainingQuantum() == 0 && current.getRemainingBurstTime() > 0) {
            current.setQuantum(current.getQuantum() + 2);
            readyQueue.add(current);
            endTurn = true;
        }
    }

    void caseivCheck() {
        // iv) completion time
        if (current.getRemainingBurstTime() == 0) {
            current.setCompletionTime(currentTime);
            current.setQuantum(0); // also pushes 0 into quantumHistory
            counterCompletedProcesses++;
            endTurn = true;
        }
    }

    public void AGScheduling() {
        while (counterCompletedProcesses < processes.size()) {
            addProcess();

            if (readyQueue.isEmpty()) {
                currentTime++;
                continue;
            }

            current = pickNextProcess();
            if (current == null) continue;

            endTurn = false;
            startProcess();

            // phase 1 (FCFS) for ceil(25%Q)
            int q1 = (int) ceil(0.25 * current.getQuantum());
            int phaseOneCounter = 0;

            while (current.getRemainingBurstTime() > 0 &&
                   current.getRemainingQuantum() > 0 &&
                   phaseOneCounter < q1) {

                current.setRemainingBurstTime(current.getRemainingBurstTime() - 1);
                current.setRemainingQuantum(current.getRemainingQuantum() - 1);
                currentTime++;
                phaseOneCounter++;
                addProcess();
            }

            caseivCheck();
            if (endTurn) continue;
            caseiCheck();
            if (endTurn) continue;

            // phase 2: non-preemptive priority for next ceil(25%Q)
            int q2 = (int) ceil(0.25 * current.getQuantum());
            int phaseTwoCounter = 0;

            // case ii check (priority preemption before running phase2)
            Process bestPriority = null;
            int bestPr = Integer.MAX_VALUE;
            for (Process p : readyQueue) {
                if (p.getPriority() < bestPr) {
                    bestPr = p.getPriority();
                    bestPriority = p;
                }
            }

            if (bestPriority != null && bestPriority.getPriority() < current.getPriority()) {
                int addQ = (int) ceil(current.getRemainingQuantum() / 2.0);
                current.setQuantum(current.getQuantum() + addQ);
                readyQueue.add(current);

                nextPick = PickMode.PRIORITY;
                endTurn = true;
                continue;
            }

            while (current.getRemainingBurstTime() > 0 &&
                   current.getRemainingQuantum() > 0 &&
                   phaseTwoCounter < q2) {

                current.setRemainingBurstTime(current.getRemainingBurstTime() - 1);
                current.setRemainingQuantum(current.getRemainingQuantum() - 1);
                currentTime++;
                phaseTwoCounter++;
                addProcess();
            }

            caseivCheck();
            if (endTurn) continue;
            caseiCheck();
            if (endTurn) continue;

            // phase 3: preemptive SJF for the remaining quantum
            while (current.getRemainingBurstTime() > 0 && current.getRemainingQuantum() > 0) {
                addProcess();

                // case iii check (shorter remaining time exists)
                Process bestSJF = null;
                int bestRem = Integer.MAX_VALUE;
                for (Process p : readyQueue) {
                    if (p.getRemainingBurstTime() < bestRem) {
                        bestRem = p.getRemainingBurstTime();
                        bestSJF = p;
                    }
                }

                if (bestSJF != null && bestSJF.getRemainingBurstTime() < current.getRemainingBurstTime()) {
                    current.setQuantum(current.getQuantum() + current.getRemainingQuantum());
                    readyQueue.add(current);

                    nextPick = PickMode.SJF;
                    endTurn = true;
                    break;
                }

                // execute 1 unit
                current.setRemainingBurstTime(current.getRemainingBurstTime() - 1);
                current.setRemainingQuantum(current.getRemainingQuantum() - 1);
                currentTime++;
            }

            if (endTurn) continue;

            caseivCheck();
            if (endTurn) continue;
            caseiCheck();
        }
    }

    public void printResults() {
        double totalWaiting = 0;
        double totalTurnaround = 0;

        // compute waiting/turnaround
        for (Process p : processes) {
            p.setTurnaroundTime(p.getCompletionTime() - p.getArrivalTime());
            p.setWaitingTime(p.getTurnaroundTime() - p.getBurstTime());
            totalWaiting += p.getWaitingTime();
            totalTurnaround += p.getTurnaroundTime();
        }

        System.out.println("execution Order = " + executionOrder);

        for (Process p : processes) {
            System.out.println(
                p.getName() +
                " Waiting Time=" + p.getWaitingTime() +
                " Turnaround Time=" + p.getTurnaroundTime() +
                " quantum History=" + p.getQuantumHistory()
            );
        }

        System.out.printf("averageWaitingTime = %.2f%n", totalWaiting / processes.size()); //approximate to 2 decimal places
        System.out.printf("averageTurnaroundTime = %.2f%n", totalTurnaround / processes.size());
    }
}




