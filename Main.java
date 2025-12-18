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

    public void priorityScheduling(List<Process> processes, int agingInterval) {
        int currentT = 0;
        int completedP = 0;
        int numProcesses = processes.size();

        List<String> executionOrder = new ArrayList<>();

        // track when each process last executed
        Map<Process, Integer> lastExecutedTime = new HashMap<>();
        for (Process p : processes) {
            lastExecutedTime.put(p, 0);
        }

        // store original priorities
        Map<Process, Integer> originalPriority = new HashMap<>();
        for (Process p : processes) {
            originalPriority.put(p, p.getPriority());
        }

        while (completedP < numProcesses) {
            // find processes that have arrived and still have burst time
            List<Process> readyQueue = new ArrayList<>();
            for (Process p : processes) {
                if (p.getArrivalTime() <= currentT && p.getRemainingBurstTime() > 0) {
                    readyQueue.add(p);
                }
            }

            // if no ready process, advance time
            if (readyQueue.isEmpty()) {
                currentT++;
                continue;
            }

            // apply aging : increase priority of waiting processes
            for (Process p : readyQueue) {
                int waitingTime = currentT - lastExecutedTime.get(p);
                // every agingInterval time units, improve priority by 1
                int priorityBoost = waitingTime / agingInterval;
                int adjustedPriority = Math.max(0, originalPriority.get(p) - priorityBoost);
                p.priority = adjustedPriority;
            }

            // select process with lowest priority number
            Process selected = readyQueue.get(0);
            for (Process p : readyQueue) {
                if (p.priority < selected.priority) {
                    selected = p;
                }
            }

            // add to execution order
            if (executionOrder.isEmpty() || !executionOrder.get(executionOrder.size() - 1).equals(selected.getName())) {
                executionOrder.add(selected.getName());
            }

            // execute for 1 time unit
            selected.setRemainingBurstTime(selected.getRemainingBurstTime() - 1);
            currentT++;
            lastExecutedTime.put(selected, currentT);

            // check if process completed
            if (selected.getRemainingBurstTime() == 0) {
                completedP++;
                selected.setCompletionTime(currentT);
                selected.setTurnaroundTime(selected.getCompletionTime() - selected.getArrivalTime());
                selected.setWaitingTime(selected.getTurnaroundTime() - selected.getBurstTime());
                System.out.println("Process " + selected.getName() + " completed at time " + currentT);
            }

            // restore original priority
            selected.priority = originalPriority.get(selected);
        }

        // print results
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

    enum PickMode { FCFS, PRIORITY, SJF }

    public void AGScheduling(List<Process> processes) {

    Queue<Process> readyQueue = new LinkedList<>();
    List<String> executionOrder = new ArrayList<>();

    PickMode nextPick = PickMode.FCFS;

    int currentTime = 0;
    int completed = 0;

    // reset process state (important if reused)
    for (Process p : processes) {
        p.addedToQueue = false;
        p.setRemainingBurstTime(p.getBurstTime());
        p.setRemainingQuantum(p.getQuantum());
    }

    while (completed < processes.size()) {

        // add arrived processes
        for (Process p : processes) {
            if (p.getArrivalTime() <= currentTime && !p.addedToQueue) {
                readyQueue.add(p);
                p.addedToQueue = true;
            }
        }

        if (readyQueue.isEmpty()) {
            currentTime++;
            continue;
        }

        // pick process
        Process current = null;

        if (nextPick == PickMode.FCFS) {
            current = readyQueue.poll();
        } else {
            Process best = null;

            if (nextPick == PickMode.PRIORITY) {
                int bestPr = Integer.MAX_VALUE;
                for (Process p : readyQueue) {
                    if (p.getPriority() < bestPr) {
                        bestPr = p.getPriority();
                        best = p;
                    }
                }
            } else { // SJF
                int bestRem = Integer.MAX_VALUE;
                for (Process p : readyQueue) {
                    if (p.getRemainingBurstTime() < bestRem) {
                        bestRem = p.getRemainingBurstTime();
                        best = p;
                    }
                }
            }

            current = best;
            readyQueue.remove(best);
            nextPick = PickMode.FCFS;
        }

        // record execution
        if (executionOrder.isEmpty() ||
            !executionOrder.get(executionOrder.size() - 1).equals(current.getName())) {
            executionOrder.add(current.getName());
        }

        current.setRemainingQuantum(current.getQuantum());

        // Phase 1: FCFS (25%)
        int q1 = (int) ceil(0.25 * current.getQuantum());
        int c1 = 0;

        while (current.getRemainingBurstTime() > 0 &&
               current.getRemainingQuantum() > 0 &&
               c1 < q1) {

            current.setRemainingBurstTime(current.getRemainingBurstTime() - 1);
            current.setRemainingQuantum(current.getRemainingQuantum() - 1);
            currentTime++;
            c1++;

            for (Process p : processes) {
                if (p.getArrivalTime() <= currentTime && !p.addedToQueue) {
                    readyQueue.add(p);
                    p.addedToQueue = true;
                }
            }
        }

        if (current.getRemainingBurstTime() == 0) {
            current.setCompletionTime(currentTime);
            current.setQuantum(0);
            completed++;
            continue;
        }

        if (current.getRemainingQuantum() == 0) {
            current.setQuantum(current.getQuantum() + 2);
            readyQueue.add(current);
            continue;
        }

        //Phase 2: Priority (25%) 
        int q2 = (int) ceil(0.25 * current.getQuantum());
        int c2 = 0;

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
            continue;
        }

        while (current.getRemainingBurstTime() > 0 &&
               current.getRemainingQuantum() > 0 &&
               c2 < q2) {

            current.setRemainingBurstTime(current.getRemainingBurstTime() - 1);
            current.setRemainingQuantum(current.getRemainingQuantum() - 1);
            currentTime++;
            c2++;
        }

        if (current.getRemainingBurstTime() == 0) {
            current.setCompletionTime(currentTime);
            current.setQuantum(0);
            completed++;
            continue;
        }

        if (current.getRemainingQuantum() == 0) {
            current.setQuantum(current.getQuantum() + 2);
            readyQueue.add(current);
            continue;
        }

        // Phase 3: SJF
        while (current.getRemainingBurstTime() > 0 &&
               current.getRemainingQuantum() > 0) {

            Process bestSJF = null;
            int bestRem = Integer.MAX_VALUE;
            for (Process p : readyQueue) {
                if (p.getRemainingBurstTime() < bestRem) {
                    bestRem = p.getRemainingBurstTime();
                    bestSJF = p;
                }
            }

            if (bestSJF != null &&
                bestSJF.getRemainingBurstTime() < current.getRemainingBurstTime()) {

                current.setQuantum(current.getQuantum() + current.getRemainingQuantum());
                readyQueue.add(current);
                nextPick = PickMode.SJF;
                break;
            }

            current.setRemainingBurstTime(current.getRemainingBurstTime() - 1);
            current.setRemainingQuantum(current.getRemainingQuantum() - 1);
            currentTime++;
        }

        if (current.getRemainingBurstTime() == 0) {
            current.setCompletionTime(currentTime);
            current.setQuantum(0);
            completed++;
        } else if (current.getRemainingQuantum() == 0) {
            current.setQuantum(current.getQuantum() + 2);
            readyQueue.add(current);
        }
    }

    //Results
    double totalWaitingTime = 0, totalTurnaroundTime = 0;

    System.out.println("Execution Order = " + executionOrder);

    for (Process p : processes) {
        p.setTurnaroundTime(p.getCompletionTime() - p.getArrivalTime());
        p.setWaitingTime(p.getTurnaroundTime() - p.getBurstTime());

        totalWaitingTime += p.getWaitingTime();
        totalTurnaroundTime += p.getTurnaroundTime();

        System.out.println(
            p.getName() +
            " Waiting Time=" + p.getWaitingTime() +
            " Turnaround Time=" + p.getTurnaroundTime() +
            " Quantum History=" + p.getQuantumHistory()
        );
    }

    System.out.printf("Average WT = %.2f%n", totalWaitingTime / processes.size()); //approximate to 2 decimal places
    System.out.printf("Average TAT = %.2f%n", totalTurnaroundTime / processes.size());
 }

}


