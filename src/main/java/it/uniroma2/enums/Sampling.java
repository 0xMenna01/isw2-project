package it.uniroma2.enums;

public enum Sampling {
    NO_SAMPLING,
    OVER_SAMPLING,
    UNDER_SAMPLING;

    //This is only for Over Sampling
    private int majorityClassSize;
    private int minorityClassSize;

    //
    public void setSize(int majorityClassSize, int minorityClassSize) {
        this.majorityClassSize = majorityClassSize;
        this.minorityClassSize = minorityClassSize;
    }

    public int getMajorityClassSize() {
        check();
        return majorityClassSize;
    }

    public int getMinorityClassSize() {
        check();
        return minorityClassSize;
    }

    private void check() {
        if (!this.equals(OVER_SAMPLING)) throw new IllegalArgumentException("Only for over sampling");
    }
}