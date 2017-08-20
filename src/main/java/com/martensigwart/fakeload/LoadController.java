package com.martensigwart.fakeload;

import com.sun.management.OperatingSystemMXBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.management.ManagementFactory;
import java.util.Collections;
import java.util.List;

/**
 * This class acts as the controlling entity of the simulation infrastructure.
 *
 * When the load simulation infrastructure is currently active this class runs as a separate thread to monitor the
 * actual load generated by the simulator threads. Whenever a significant deviation between target and actual load is
 * detected, the class adjusts the load slightly in the direction of target. This way the load generated by the
 * simulation actually reaches the desired level.
 *
 * The {@code SimulationControl} retrieves all load changes via a shared instance of type
 * {@link SystemLoad}. These changes are then propagated to the respective simulator threads.
 *
 * @author Marten Sigwart
 * @since 1.8
 */
public final class SimulationControl implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(SimulationControl.class);
    private static final OperatingSystemMXBean operatingSystem = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
    private static final int SLEEP_PERIOD = 2000;
    private static final int CPU_CONTROL_THRESHOLD = 1;


    private final SystemLoad systemLoad;
    private final List<CpuSimulator> cpuSimulators;
    private final MemorySimulator memorySimulator;
    private final double stepSize;
    private final Object lock;

    private long lastCpu = 0L;

    SimulationControl(List<CpuSimulator> cpuSimulators, MemorySimulator memorySimulator) {
        this.systemLoad = new SystemLoad(); //TODO pass as constructor parameter
        this.cpuSimulators = Collections.unmodifiableList(cpuSimulators);
        this.memorySimulator = memorySimulator;
        this.stepSize = 1.0 / Runtime.getRuntime().availableProcessors();
        this.lock = new Object();
    }


    @Override
    public void run() {
        log.trace("SimulationControl - Started");

        boolean running = true;
        operatingSystem.getProcessCpuLoad();    // the first value reported is always zero
        while(running) {
            try {
                synchronized (lock) {
                    while (systemLoad.getCpu() == 0) {
                        log.trace("SimulationControl - Waiting...");
                        lock.wait();
                        log.trace("SimulationControl - Woke Up");
                    }
                }
                Thread.sleep(SLEEP_PERIOD);
                controlCpuLoad();


            } catch (InterruptedException e) {
                log.warn("SimulationControl - Interrupted");
                running = false;
            }
        }
    }



    public void increaseSystemLoadBy(FakeLoad load) throws MaximumLoadExceededException {
        systemLoad.increaseBy(load);

        for (CpuSimulator cpuSim: cpuSimulators) {
            cpuSim.setLoad(systemLoad.getCpu());
        }

        synchronized (lock) {
            lock.notify();
        }

        memorySimulator.setLoad(systemLoad.getMemory());
        //TODO propagate changes to simulators
    }

    public void decreaseSystemLoadBy(FakeLoad load) {
        systemLoad.decreaseBy(load);

        for (CpuSimulator cpuSim: cpuSimulators) {
            cpuSim.setLoad(systemLoad.getCpu());
        }
        memorySimulator.setLoad(systemLoad.getMemory());
        // TODO propagate changes to simulators
    }



    private void controlCpuLoad() {
        long actualCpu = (long)(operatingSystem.getProcessCpuLoad() * 100);
        long desiredCpu = systemLoad.getCpu();

        log.trace("Desired CPU: {}, Actual CPU: {}, Last CPU: {}", desiredCpu, actualCpu, lastCpu);

        long difference = actualCpu - desiredCpu;

        if (    Math.abs(difference) > CPU_CONTROL_THRESHOLD &&
                Math.abs(lastCpu - actualCpu) <= CPU_CONTROL_THRESHOLD) {

            int noOfSteps = (int)(Math.abs(difference) / stepSize);
            log.trace("{}",noOfSteps);
            if (difference < 0) {   // actual load smaller than desired load
                log.trace("Increasing CPU load, difference {}", difference);
                increaseCpuSimulatorLoads(1, noOfSteps);
            } else {
                log.trace("Decreasing CPU load, difference {}", difference);
                decreaseCpuSimulatorLoads(1, noOfSteps);
            }

        }

        lastCpu = actualCpu;

    }


    private void increaseCpuSimulatorLoads(int delta, int noOfSteps) {
        for (int i=0; i<noOfSteps; i++) {
            cpuSimulators.get(i % cpuSimulators.size()).increaseLoad(delta);
        }
    }

    private void decreaseCpuSimulatorLoads(int delta, int noOfSteps) {
        for (int i=0; i<noOfSteps; i++) {
            cpuSimulators.get(i % cpuSimulators.size()).decreaseLoad(delta);
        }
    }


}