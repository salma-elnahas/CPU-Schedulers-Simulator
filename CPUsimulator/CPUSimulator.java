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
    private void record(List<String> executionOrder, Process p) {
        if (executionOrder.isEmpty() ||
            !executionOrder.get(executionOrder.size() - 1).equals(p.getName())) {
            executionOrder.add(p.getName());
        }
    }

    // priority Scheduling
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

            // apply aging: increase priority of waiting processes
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
    enum PickMode { FCFS, PRIORITY, SJF } //to switch between picking modes

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
 