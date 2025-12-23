package test;
// JUnit imports
import java.util.List;

 
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

// Gson imports
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

// CPU Simulator imports
import CPUsimulator.CPUSimulator;
import CPUsimulator.Process;
 
// File handling imports
import java.io.FileReader;
import java.util.ArrayList;
import java.io.File;


// Main test class
public class CpuSimulatorTest {
    // Initialize CPU simulator and Gson
    private CPUSimulator simulator;
    private Gson gson;
    // Helper method to get all JSON files from folder
    private List<String> getJsonFiles(String path) {
    List<String> filePaths = new ArrayList<>();
    File folder = new File(path);
    File[] filesList = folder.listFiles();
    if (filesList != null) {
        for (File file : filesList) {
            if (file.isFile() && file.getName().endsWith(".json")) {
                filePaths.add(file.getPath());
            }
        }
    }
    return filePaths;
}
    // Before each test, set up the simulator and Gson
    @BeforeEach
    public void setUp() throws Exception {
        simulator = new CPUSimulator();
        gson = new GsonBuilder().create();
    }

   // SJF testing method
    @Test
    public void testPreemptiveSJF() throws Exception {
        List<String> jsonFiles = getJsonFiles("test_cases_v5/test_cases_v5/Other_Schedulers");
        assertFalse(jsonFiles.isEmpty(), "No test files found in test_cases_v5");
        
        // Loop through each test file and run the SJF test
        for (String file : jsonFiles) {
            System.out.println(" Testing SJF from: " + file);
            runSchedulerTest(file, "SJF");
        }
    }

    // RR testing method
    @Test
    public void testRoundRobin() throws Exception {
        List<String> jsonFiles = getJsonFiles("test_cases_v5/test_cases_v5/Other_Schedulers");
        assertFalse(jsonFiles.isEmpty(), "No test files found in test_cases_v5");

        // Loop through each test file and run the RR test
        for (String file : jsonFiles) {
            System.out.println(" Testing Round Robin from: " + file);
            runSchedulerTest(file, "RR");
        }
    }

     // Priority testing method
     @Test
     public void testPreemptivePriority() throws Exception {
          List<String> jsonFiles = getJsonFiles("test_cases_v5/test_cases_v5/Other_Schedulers");
        assertFalse(jsonFiles.isEmpty(), "No test files found in test_cases_v5");

         // Loop through each test file and run the RR test
         for (String file : jsonFiles) {
             System.out.println(" Testing Preemptive priority from: " + file);
             runSchedulerTest(file, "Priority");
         }
     }

    // AG testing method
    @Test
    public void testAGScheduling() throws Exception {
        List<String> jsonFiles = getJsonFiles("test_cases_v5/test_cases_v5/AG");
        assertFalse(jsonFiles.isEmpty(), "No test files found in test_cases_v5");

        for (String filePath : jsonFiles) {
            System.out.println(" Testing AG Scheduler from: " + filePath);
            runAGSchedulerTest(filePath);
        }
    }

    private void runSchedulerTest(String filePath, String schedulerType) throws Exception {
    // Read Json file and parse it into Testcase object
    try (FileReader reader = new FileReader(filePath)) {
        Testcase testCase = gson.fromJson(reader, Testcase.class);
        assertNotNull(testCase, "Failed to parse JSON file:" + filePath);

        System.out.println(" Running test: " + testCase.getName());
 
        testInput input = testCase.getInput();   
        expectedOutput output = testCase.getExpectedOutput();   
        
        List<Process> processes = convertToProcessObject(input.getProcesses(), false);
        List<String> actualExecutionOrder = new ArrayList<>();
         // Switch based on scheduler type
        switch (schedulerType) {
            case "SJF":
                // call each algorithm method from simulator
                simulator.preemptiveSJF(processes, input.getContextSwitch());
                actualExecutionOrder  = simulator.getExecutionOrder();
                validateResults(processes, output.getPreemptiveSJF(), actualExecutionOrder, testCase.getName());
                break;
            case "RR":
                simulator.RRContextSwitch(processes, input.getRrQuantum(), input.getContextSwitch());
                actualExecutionOrder  = simulator.getExecutionOrder();
                validateResults(processes, output.getRoundRobin(), actualExecutionOrder, testCase.getName());
                break;
             case "Priority":
                 simulator.priorityScheduling(processes, input.getAgingInterval(), input.getContextSwitch());
                 actualExecutionOrder = simulator.getExecutionOrder();
                 validateResults(processes, output.getPreemptivePriority(), actualExecutionOrder, testCase.getName());
                 break;
        }
    }
}
    private void runAGSchedulerTest(String filePath) throws Exception {
        // Read Json file and parse it into Testcase object
        try (FileReader reader = new FileReader(filePath)) {
             AGTestcase testCase = gson.fromJson(reader, AGTestcase.class);
             

            assertNotNull(testCase, "Failed to parse AG JSON: " + filePath);
            System.out.println(" Running AG test: " + filePath);

            testInput input = testCase.getInput();  
            expectedOutput output = testCase.getExpectedOutput();  
    
            List<Process> processes = convertToProcessObject(input.getProcesses(), true);
            List<String> actualExecutionOrder = new ArrayList<>();
            // Call AG scheduler algorithm
            simulator.AGScheduling(processes);
            actualExecutionOrder = simulator.getExecutionOrder(); 
            // Validate results
            validateAGResults(processes, output,actualExecutionOrder,filePath);
         }
        }

    // helper method
    public void validateResults(List<Process> actual, schedularAlgorithm expected, List<String> actExecOrder, String testName) {
        // Ensure expected is not null
        assertNotNull(expected, "Expected output is null for test: " + testName);
        List<String> expectedExecOrder = expected.getExecutionOrder();
        if(expectedExecOrder != null) {
            assertEquals(expectedExecOrder, actExecOrder, "Execution order mismatch for test: " + testName);
            for (int i = 0; i < expectedExecOrder.size(); i++) {
                assertEquals(expectedExecOrder.get(i), actExecOrder.get(i),
                    String.format("Execution order mismatch at index %d in test: %s (expected: %s, actual: %s)",
                        i, testName, expectedExecOrder.get(i), actExecOrder.get(i)));
            }
           
        }
         System.out.println(" Execution order matches");
        List<ProcessResult> expectedProcesses = expected.getProcessResults();
        
        assertEquals(expectedProcesses.size(), actual.size(), testName);

        for (ProcessResult exp : expectedProcesses) {
            Process Actual = findProcessByName(actual, exp.getName());
            assertNotNull(Actual,
                    String.format("Process %s not found in actual results for test: %s", exp.getName(), testName));
            // Assert waiting time and turnaround time
            assertEquals(exp.getWaitingTime(), Actual.getWaitingTime(),
                    String.format("Waiting time mismatch for %s in test: %s (expected: %d, actual: %d)",
                            exp.getName(), testName, exp.getWaitingTime(), Actual.getWaitingTime()));
            assertEquals(exp.getTurnaroundTime(), Actual.getTurnaroundTime(),
                    String.format("Turnaround time mismatch for %s in test: %s (expected: %d, actual: %d)",
                            exp.getName(), testName, exp.getTurnaroundTime(), Actual.getTurnaroundTime()));
        }
        System.out.println(" Process Results match ");

        double expAvgWaiting = expected.getAverageWaitingTime();
        double expAvgTurnaround = expected.getAverageTurnaroundTime();
        double actAvgWaiting = calculateAverageWaitingTime(actual);
        double actAvgTurnaround = calculateAverageTurnaroundTime(actual);
        double delta = 0.15; // Safe margin for floating-point precision
        assertEquals(expAvgWaiting, actAvgWaiting, delta, "Average waiting time mismatch for test: " + testName);
        assertEquals(expAvgTurnaround, actAvgTurnaround, delta, "Average turnaround time mismatch for test: " + testName);
        System.out.println(" Average Waiting and Turnaround Time matches: " );
       
        System.out.println(" Test PASSED: " + testName + " for " + expected);
        
    }

     
    public void validateAGResults(List<Process> actual, expectedOutput expected, List<String> actExecOrder, String testName) {
        // Ensure expected is not null
        assertNotNull(expected, "Expected output is null for test: " + testName);

        List<String> expectedExecOrder = expected.getExecutionOrder();
        if (expectedExecOrder != null) {
            assertEquals(expectedExecOrder, actExecOrder, "Execution order mismatch for test: " + testName);
            for (int i = 0; i < expectedExecOrder.size(); i++) {
                assertEquals(expectedExecOrder.get(i), actExecOrder.get(i),
                        "Execution order mismatch at index %d in test: %s (expected: %s, actual: %s)" + testName);

            }

        }
         System.out.println(" AG Execution order matches");
        List<ProcessResult> expectedProcesses = expected.getProcessResults();
        
        assertEquals(expectedProcesses.size(), actual.size(), testName);

        for (ProcessResult exp : expectedProcesses) {
            Process Actual = findProcessByName(actual, exp.getName());
            assertNotNull(Actual, String.format("Process %s not found in actual results ", exp.getName(), testName));

            assertEquals(exp.getWaitingTime(), Actual.getWaitingTime(), String.format("Waiting time mismatch for %s",
                    exp.getName(), testName, exp.getWaitingTime(), Actual.getWaitingTime()));
            assertEquals(exp.getTurnaroundTime(), Actual.getTurnaroundTime(),
                    String.format("Turnaround time mismatch for %s ", exp.getName(), testName, exp.getTurnaroundTime(),
                            Actual.getTurnaroundTime()));

            // Validate quantum history if available in your Process class
            if (exp.getQuantumHistory() != null) {
                List<Integer> actQuantumHistory = Actual.getQuantumHistory();
                if (actQuantumHistory != null) {
                    assertEquals(exp.getQuantumHistory(), actQuantumHistory,
                            String.format("Quantum history mismatch for %s ", exp.getName(), testName));
                }
            }
        }

        double expAvgWaiting = expected.getAverageWaitingTime();
        double expAvgTurnaround = expected.getAverageTurnaroundTime();
        double actAvgWaiting = calculateAverageWaitingTime(actual);
        double actAvgTurnaround = calculateAverageTurnaroundTime(actual);
        double delta = 0.01; // to handle floating point precision
        assertEquals(expAvgWaiting, actAvgWaiting, delta, "Average waiting time mismatch for test: " + testName);
        assertEquals(expAvgTurnaround, actAvgTurnaround, delta, "Average turnaround time mismatch for test: " + testName);
        System.out.println(" Average Waiting and Turnaround Time matches: " );
        
        System.out.println(" AG Test PASSED: " + testName);
    }

    // Helper method to find a process by name in a list
    public Process findProcessByName(List<Process> processes, String name) {
        for (Process proc : processes) {
            if (proc.getName().equals(name)) {
                return proc;
            }
        }
        return null;
    }

    // Convert ProcessInput to Process objects
    public List<Process> convertToProcessObject(List<ProcessInput> inputs, boolean includeQuantum) {
        List<Process> processes = new ArrayList<>();
        for (ProcessInput p : inputs) {
            // For non-AG schedulers, set quantum to 0
            int quantum = includeQuantum ? p.getQuantum() : 0;
            processes.add(new Process(p.getName(), p.getArrival(), p.getBurst(), p.getPriority(), quantum));
        }
        return processes;
    }

    public double calculateAverageWaitingTime(List<Process> processes) {
        double total = 0;
        for (Process p : processes) {
            total += p.getWaitingTime();
        }
        return total / processes.size();
    }
    public double calculateAverageTurnaroundTime(List<Process> processes) {
        double total = 0;
        for (Process p : processes) {
            total += p.getTurnaroundTime();
        }
        return total / processes.size();
    }
    
}