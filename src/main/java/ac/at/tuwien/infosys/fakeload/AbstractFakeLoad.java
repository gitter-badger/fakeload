package ac.at.tuwien.infosys.fakeload;

import ac.at.tuwien.infosys.fakeload.internal.DefaultFakeLoadDispatcher;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;


public abstract class AbstractFakeLoad implements FakeLoad {

    /**
     *  {@inheritDoc}
     *  This method submits the load pattern of the AbstractFakeLoad instance to the {@Link DefaultFakeLoadDispatcher} singleton instance.
     *  The method blocks until the requested load simulation completes.
     */
    @Override
    public void execute() {
        try {

            // wait until simulation completes
            Future<String> future = DefaultFakeLoadDispatcher.INSTANCE.submitLoad(this);
            String response = future.get();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean contains(FakeLoad load) {
        if (this == load) {
            return true;
        }

        // check if pattern is contained in child patterns
        for (FakeLoad l : this.getLoads()) {
            return l.contains(load);
        }

        // if not found anywhere return false
        return false;
    }

}
