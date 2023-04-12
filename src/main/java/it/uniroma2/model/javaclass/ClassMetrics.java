package it.uniroma2.model.javaclass;

public class ClassMetrics {

    private int size;
    private int nFix;
    private int nAuthors;
    private double avgLocAdded;
    private int churn;
    private int avgChurn;
    private int age;
    private double avgTimeFix;
    private int fanOut;
    private int changeSetSize;

    public ClassMetrics() {
        this.size = 0;
        this.nFix = 0;
        this.nAuthors = 0;
        this.avgLocAdded = 0;
        this.churn = 0;
        this.avgChurn = 0;
        this.age = 0;
        this.avgTimeFix = 0;
        this.fanOut = 0;
        this.changeSetSize = 0;
    }

    
}
