package it.uniroma2.model.javaclass;

public class ClassMetrics {

    protected int size;
    protected int nFix;
    protected int nAuthors;
    protected double avgLocAdded;
    protected int churn;
    protected double avgChurn;
    protected int age;
    protected double avgFixChurn;
    protected int fanOut;
    protected int numOfRevisions;

    public ClassMetrics() {
        this.size = 0;
        this.nFix = 0;
        this.nAuthors = 0;
        this.avgLocAdded = 0;
        this.churn = 0;
        this.avgChurn = 0;
        this.age = 0;
        this.avgFixChurn = 0;
        this.fanOut = 0;
        this.numOfRevisions = 0;
    }

    public int getSize() {
        return size;
    }

    public int getnFix() {
        return nFix;
    }

    public int getnAuthors() {
        return nAuthors;
    }

    public double getAvgLocAdded() {
        return avgLocAdded;
    }

    public int getChurn() {
        return churn;
    }

    public double getAvgChurn() {
        return avgChurn;
    }

    public int getAge() {
        return age;
    }

    public double getAvgFixChurn() {
        return avgFixChurn;
    }

    public int getFanOut() {
        return fanOut;
    }

    public int getNumOfRevisions() {
        return numOfRevisions;
    }

    

}
