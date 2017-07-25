package ac.at.tuwien.infosys.fakeload.internal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;

/**
 * This class acts as the controlling entity of the simulation infrastructure.
 *
 * When the load simulation infrastructure is currently active this class runs as a separate thread to monitor the
 * actual load generated by the simulator threads. Whenever a significant deviation between desired and actual load is
 * detected, the class adjusts the load slightly in the direction of target. This way the load generated by the
 * simulation actually reaches the desired level.
 *
 * Further this class retrieves all load requests from the {@link DefaultFakeLoadDispatcher} via a shared instance of type
 * {@link Connection}. The requests are then propagated to the respective simulator threads.
 *
 * @author Marten Sigwart
 * @since 1.8
 */
public final class ControlTask implements Callable<Void> {

    private static final Logger log = LoggerFactory.getLogger(ControlTask.class);


    private final Connection connection;
    private final LoadControl cpuControl;
    private final LoadControl memoryControl;

    ControlTask(Connection connection) {
        this.connection = connection;
        this.cpuControl = new LoadControl();
        this.memoryControl = new LoadControl();
    }


    @Override
    public Void call() throws Exception {
        log.debug("Started");

        while(true) {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                log.warn("ControlTask was interrupted", e);
                break;
            }
        }
        return null;
    }


    public LoadControl getCpuControl() {
        return cpuControl;
    }

    public LoadControl getMemoryControl() {
        return memoryControl;
    }
}
