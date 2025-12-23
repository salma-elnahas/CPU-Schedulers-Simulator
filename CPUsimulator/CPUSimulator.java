package CPUsimulator;
import java.util.*;


public class CPUSimulator {

    List<String> executionOrder = new ArrayList<>();
    public List<String> getExecutionOrder() {return executionOrder;}
    // preemptive Shortest-Job First (SJF) Scheduling with context switching
    public void preemptiveSJF(List<Process> processes, int contextSwitch) {
        // clear previous state
        this.executionOrder.clear();
        int currentT = 0;
        int completedP = 0;
        int numProcesses = processes.size();
        Process currentP = null;
        Process previousP = null;

 
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
        // clear previous state
        this.executionOrder.clear();
        int currentT = 0;
        int completedP = 0;
        int numProcesses = processes.size();
        int nextP = 0; // Track next process to execute

        Process currentP = null;
        Process previousP = null;

        Queue<Process> queue = new LinkedList<>();
 
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

    private void addArrivals(List<Process> processes, Queue<Process> readyQueue, int currentTime) {
        for (Process p : processes) {
            if (!p.addedToQueue && p.getArrivalTime() <= currentTime) {
                readyQueue.add(p);
                p.addedToQueue = true;
            }
        }
    }
 
    public void priorityScheduling(List<Process> processes, int agingInterval, int contextSwitch) {
    // clear previous state
    this.executionOrder.clear();

    // Create working copies
    List<Process> Processes = new ArrayList<>();
    for (Process p : processes) {
        Process copy = new Process( p.getName(),p.getArrivalTime(),p.getBurstTime(), p.getPriority(),0);
        copy.setRemainingBurstTime(p.getBurstTime());
        Processes.add(copy);
    }
     // Sort by arrival time, then by name for consistency
    Processes.sort(Comparator.comparingInt(Process::getArrivalTime).thenComparing(Process::getName));

    List<Process> readyQueue = new ArrayList<>();
    List<Process> pending = new ArrayList<>(Processes);
    List<Process> finished = new ArrayList<>();
    //Track effective priority after aging adjustments
    Map<String, Integer> effectivePriority = new HashMap<>();
    // Track when each process entered ready queue
    Map<String, Integer> waitStartTime = new HashMap<>();
    // Initialize effective priorities and wait start times
    for (Process p : Processes) {
        effectivePriority.put(p.getName(), p.getPriority());
        waitStartTime.put(p.getName(), -1);
    }
    // Simulation loop
    int currentTime = 0;
    Process currentP= null;
    String lastExecutedP = null;

    while (true) {
        // Add newly arrived processes to ready queue
        checkArrivals(readyQueue, pending, waitStartTime, currentTime);
        // Apply aging to waiting processes
        applyAging(readyQueue, currentP, currentTime, agingInterval, effectivePriority, waitStartTime);

        // Handle preemptiion (check if current process should be interrupted)
        if (currentP != null && !readyQueue.isEmpty()) {
            // Only check if current process is still in ready queue
            if (readyQueue.contains(currentP)) {
                // Find highest priority process
                Process candidate = selectHighestPriority(
                    readyQueue, effectivePriority);

                int currentPrio = effectivePriority.get(currentP.getName());
                int candidatePrio= effectivePriority.get(candidate.getName());
                // Preempt if candidate has higher or equal priority
                if (candidate != currentP && candidatePrio <= currentPrio) {
                    // Add context switch time if switching
                    if (!currentP.getName().equals(candidate.getName())) {
                        currentTime += contextSwitch;
                        record(executionOrder, currentP);
                        // Recheck again after context switch delay
                        checkArrivals(readyQueue, pending, waitStartTime, currentTime);
                        applyAging(readyQueue, currentP, currentTime, agingInterval, effectivePriority, waitStartTime);
                    }

                    // Mark when preempted process started waiting
                    waitStartTime.put(currentP.getName(), currentTime - contextSwitch);
                    currentP = selectHighestPriority(readyQueue, effectivePriority);    
                    if (candidate != currentP) {
                        currentTime += contextSwitch;
                    }
                }
            }
        }

        // Select process if CPU idle
        if (currentP == null && !readyQueue.isEmpty()) {
            Process candidate = selectHighestPriority(readyQueue, effectivePriority);

            if (lastExecutedP != null && !lastExecutedP.equals(candidate.getName())) {
                currentTime += contextSwitch;
                record(executionOrder, candidate);
                checkArrivals(readyQueue, pending, waitStartTime, currentTime);
                applyAging(readyQueue, currentP, currentTime, agingInterval, effectivePriority, waitStartTime);
            }

            currentP = selectHighestPriority(readyQueue, effectivePriority);

            if (candidate != currentP) {
                currentTime += contextSwitch;
            }

            checkArrivals(readyQueue, pending, waitStartTime, currentTime);
            applyAging(readyQueue, currentP, currentTime, agingInterval, effectivePriority, waitStartTime);
        }

        // Check for termination
        if (currentP == null) {
            // exit if all processes finished 
            if (finished.size() == Processes.size()) break;

            int nextTime = Integer.MAX_VALUE;
            if (!pending.isEmpty()) {
                nextTime = pending.get(0).getArrivalTime();
            }

            if (nextTime != Integer.MAX_VALUE && nextTime > currentTime) {
                currentTime = nextTime;
            } else {
                currentTime++;
            }

            continue;
        }

        // Execute current process
        record(executionOrder, currentP);
        currentP.setRemainingBurstTime(currentP.getRemainingBurstTime() - 1);
        // Update it with -1
        waitStartTime.put(currentP.getName(), -1);
        currentTime++;
        
        // Check if process completed
        if (currentP.getRemainingBurstTime() == 0) {
            currentP.setCompletionTime(currentTime);
            finished.add(currentP);
            readyQueue.remove(currentP);
            lastExecutedP = currentP.getName();
            currentP = null;
        } else {
            lastExecutedP = currentP.getName();
        }
    }

    printInfo(processes, finished);
    }
    // Helper method to check for newly arrived processes and adds them to queue
    private void checkArrivals(List<Process> readyQueue, List<Process> pending, 
                              Map<String, Integer> waitStartTime, int time) {
        Iterator<Process> iter = pending.iterator();
        while (iter.hasNext()) {
            Process p = iter.next();
            if (p.getArrivalTime() <= time) {
                if (!readyQueue.contains(p)) {
                    // Add process to queue if not there
                    readyQueue.add(p);
                    waitStartTime.put(p.getName(), p.getArrivalTime());
                }
                iter.remove();
            }
        }
    }
    // Helper method to apply aging mechanism to prevent starvation
    private void applyAging(List<Process> readyQueue, Process current, int time, 
                           int agingInterval, Map<String, Integer> effectivePriority, 
            Map<String, Integer> waitStartTime) {
        if (agingInterval <= 0)
            return;

        for (Process p : readyQueue) {
            if (p == current)
                continue;

            int start = waitStartTime.get(p.getName());
            // -1 means process is executing
            if (start == -1)
                continue;
            // Calculate how long process has been waiting 
            int queueTime = time - start;

            // If waited long enough, boost priority
            if (queueTime >= agingInterval) {
                int priorityBoost = queueTime / agingInterval;
                int currentPrio = effectivePriority.get(p.getName());
                int newPrio = Math.max(1, currentPrio - priorityBoost);
                effectivePriority.put(p.getName(), newPrio);
                waitStartTime.put(p.getName(), time);
            }
        }
    }
    
    // Helper method to select highest process ( lower number = higher priority)
    private Process selectHighestPriority(List<Process> readyQueue, Map<String, Integer> effectivePriority) {
        if (readyQueue.isEmpty()) return null;
        
        Process highest = readyQueue.get(0);
        for (Process p : readyQueue) {
            int highPriority = effectivePriority.get(highest.getName());
            int processPriority = effectivePriority.get(p.getName());
            // Check for lower . If same , earlier arrival wins
            if (processPriority < highPriority || (processPriority == highPriority && p.getArrivalTime() < highest.getArrivalTime())) {
                highest = p;
            }
        }
        return highest;
    }
    // Helper method to record processes in execution order
    private void record(List<String> executionOrder, Process p) {
        if (executionOrder.isEmpty() ||
                !executionOrder.get(executionOrder.size() - 1).equals(p.getName())) {
            executionOrder.add(p.getName());
        }
    }

    private void printInfo(List<Process> originalProcesses, List<Process> finished) {
        System.out.println("Execution Order: " + executionOrder);
        System.out.println("\nProcess\tArrival\tBurst\tPriority\tCompletion\tTAT\tWT");
        System.out.println("---------------------------------------------------------------------------");
    
        double totalWT = 0;
        double totalTAT = 0;
        int n = originalProcesses.size();
    
        for (Process original : originalProcesses) {
            for (Process worked : finished) {
                if (original.getName().equals(worked.getName())) {
                    int compTime = worked.getCompletionTime();
                    int arrival = original.getArrivalTime();
                    int burst = original.getBurstTime();
    
                    int tat = compTime - arrival;
                    int wt = tat - burst;
    
                    original.setCompletionTime(compTime);
                    original.setTurnaroundTime(tat);
                    original.setWaitingTime(wt);
    
                    System.out.println(original.getName() + "\t" +
                            arrival + "\t" +
                            burst + "\t" +
                            original.getPriority() + "\t\t" +
                            compTime + "\t\t" +
                            tat + "\t" + wt);
    
                    totalWT += wt;
                    totalTAT += tat;
                    break;
                }
            }
        }
    
        System.out.println("---------------------------------------------------------------------------");
        System.out.printf("Average Waiting Time: %.2f\n", (totalWT / n));
        System.out.printf("Average Turnaround Time: %.2f\n", (totalTAT / n));
    }

    enum PickMode { FCFS, PRIORITY, SJF } //to switch between picking modes
    // AG Scheduling
    public void AGScheduling() {
    AGScheduling(this.processes);
    }

    public void AGScheduling(List<Process> processes) {
     // clear previous state
    this.executionOrder.clear();

    Queue<Process> readyQueue = new LinkedList<>();
 
    PickMode nextPick = PickMode.FCFS;
    int currentTime = 0;
    int completed = 0;

    // reset state
    for (Process p : processes) {
        p.addedToQueue = false; // process not yet in ready queu
        p.setRemainingBurstTime(p.getBurstTime()); // reset remaining CPU time
        p.setRemainingQuantum(p.getQuantum()); // reset remaining quantum

    }

    // Continue until all processes finish
    while (completed < processes.size()) {

         // Add newly arrived processes to the ready queue
        addArrivals(processes, readyQueue, currentTime);

        // If no process is ready, CPU stays idle
        if (readyQueue.isEmpty()) {
            currentTime++;
            continue;
        }

        // pick process
        Process current;
        if (nextPick == PickMode.FCFS) {
            // Take the first process in the ready queue
            current = readyQueue.poll();
        } else {
            Process best = null;
            // Priority-based selection (lower value = higher priority)
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
            // Select and remove chosen process from queue
            current = best;
            readyQueue.remove(best);
            nextPick = PickMode.FCFS;
        }

        // Reset the remaining quantum at the start of this process’s turn
        current.setRemainingQuantum(current.getQuantum());
        record(executionOrder, current);

        // phase 1: FCFS 25%
        int q1 = (int) Math.ceil(0.25 * current.getQuantum());
        int c1 = 0; // Counter to track how much of Phase 1 has been used

        while (c1 < q1 &&
               current.getRemainingBurstTime() > 0 &&
               current.getRemainingQuantum() > 0) {
            // Execute process for 1 time unit
            current.setRemainingBurstTime(current.getRemainingBurstTime() - 1);
            // Consume 1 unit of quantum
            current.setRemainingQuantum(current.getRemainingQuantum() - 1);
            currentTime++;
            c1++;
            
            // Add any newly arrived processes to the ready queue
            addArrivals(processes, readyQueue, currentTime);
        }

        // Case IV: finished
        if (current.getRemainingBurstTime() == 0) {
            current.setCompletionTime(currentTime);
            current.setQuantum(0);
            completed++;
            continue;  // Move to next scheduling cycle
        }

        // Case I: quantum finished => Q += 2
        if (current.getRemainingQuantum() == 0) {
            current.setQuantum(current.getQuantum() + 2);
            readyQueue.add(current); // Send process to the end of the ready queue
            continue;
        }

        // Phase 2, priority check only, no execution
        Process bestPriority = null;
        int bestPr = Integer.MAX_VALUE; 
        for (Process p : readyQueue) {
            if (p.getPriority() < bestPr) {
                bestPr = p.getPriority();
                bestPriority = p;
            }
        }

        // Case II
        if (bestPriority != null && bestPriority.getPriority() < current.getPriority()) {
            int addQ = (int) Math.ceil(current.getRemainingQuantum() / 2.0);
            current.setQuantum(current.getQuantum() + addQ);
            // Put the current process back at the end of the ready queue
            readyQueue.add(current);
            // We don’t immediately run the priority process , we set nextPick so the next scheduling decision picks by priority.
            nextPick = PickMode.PRIORITY;
            continue;
        }

        int q2 = (int) Math.ceil(0.25 * current.getQuantum());
        int c2 = 0; // Counter to track how many time units used in Phase 2

        while (c2 < q2 &&  current.getRemainingBurstTime() > 0 && current.getRemainingQuantum() > 0) {

            current.setRemainingBurstTime(current.getRemainingBurstTime() - 1);
            current.setRemainingQuantum(current.getRemainingQuantum() - 1);
            currentTime++;
            c2++;

            addArrivals(processes, readyQueue, currentTime);
        }

        // Case IV: finished
        if (current.getRemainingBurstTime() == 0) {
            current.setCompletionTime(currentTime);
            current.setQuantum(0);
            completed++;
            continue;
        }

        // Case I: quantum finished
        if (current.getRemainingQuantum() == 0) {
            current.setQuantum(current.getQuantum() + 2);
            readyQueue.add(current);
            continue;
        }

        // phase 3: SJF (preemptive) 
        while (current.getRemainingBurstTime() > 0 &&
               current.getRemainingQuantum() > 0) {

            addArrivals(processes, readyQueue, currentTime);

             // Find the process with the smallest remaining burst time in the ready queue
            Process bestSJF = null; 
            int bestRem = Integer.MAX_VALUE;
            for (Process p : readyQueue) {
                if (p.getRemainingBurstTime() < bestRem) {
                    bestRem = p.getRemainingBurstTime();
                    bestSJF = p;
                }
            }

            // case III
            if (bestSJF != null && bestSJF.getRemainingBurstTime() < current.getRemainingBurstTime()) {
                // Restore the unused quantum back to the process's total quantum
                current.setQuantum(current.getQuantum() + current.getRemainingQuantum());
                readyQueue.add(current);
                nextPick = PickMode.SJF;
                break;
            }

            current.setRemainingBurstTime(current.getRemainingBurstTime() - 1); // Decrement the remaining burst time
            current.setRemainingQuantum(current.getRemainingQuantum() - 1);
            currentTime++;
        }

        if (current.getRemainingBurstTime() == 0) {
            current.setCompletionTime(currentTime); // Record the completion time for turnaround time calculation
            current.setQuantum(0); // Reset quantum to 0 as the process is done
            completed++;
        } else if (current.getRemainingQuantum() == 0) { 
            current.setQuantum(current.getQuantum() + 2);
            readyQueue.add(current);
        }
    }

    // Results
    double totalwaiting = 0, totalTurnaround = 0;

    System.out.println("Execution Order = " + executionOrder);
    for (Process p : processes) {
        p.setTurnaroundTime(p.getCompletionTime() - p.getArrivalTime());
        p.setWaitingTime(p.getTurnaroundTime() - p.getBurstTime());
        totalwaiting += p.getWaitingTime();
        totalTurnaround += p.getTurnaroundTime();

        System.out.println(p.getName() +
                " Waiting Time=" + p.getWaitingTime() +
                " Turnaround Time=" + p.getTurnaroundTime() +
                " Quantum History=" + p.getQuantumHistory());
    }

    System.out.printf("Average WT = %.2f%n", totalwaiting / processes.size()); // approximate to 2 decimal places
    System.out.printf("Average TAT = %.2f%n", totalTurnaround / processes.size());
    }

    //for testing purposes
    List<Process> processes = new ArrayList<>();

    public void add(Process p) {
    processes.add(p);
    }

}
 
 