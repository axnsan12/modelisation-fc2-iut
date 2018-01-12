package modelisation.tree;

public class ThresholdSplit extends Split {
    private final double threshold;

    public ThresholdSplit(double threshold) {
        this.threshold = threshold;
    }

    @Override
    protected String[] getBranchLabels(int[] column) {
        // < threshold goes into leaf 0, >= threshold goes into leaf 1
        return new String[] {"<" + threshold, ">=" + threshold};
    }

    @Override
    protected int getBranchIndex(int value) {
        if (value < threshold) {
            return 0;
        }
        else {
            return 1;
        }
    }
}
