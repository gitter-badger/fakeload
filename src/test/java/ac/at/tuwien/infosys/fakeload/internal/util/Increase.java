package ac.at.tuwien.infosys.fakeload.internal.util;

public final class Increase extends Instruction {

    public Increase(LoadType loadType, int offSet, long change) {
        super(loadType, offSet, change);
    }

}