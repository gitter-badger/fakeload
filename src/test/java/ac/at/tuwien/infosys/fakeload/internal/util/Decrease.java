package ac.at.tuwien.infosys.fakeload.internal.util;

public final class Decrease extends Instruction {

    public Decrease(LoadType loadType, int offSet, long change) {
        super(loadType, offSet, change);
    }

}
