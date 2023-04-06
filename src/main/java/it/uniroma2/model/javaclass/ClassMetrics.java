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
    private int indPaths;


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
        this.indPaths = 0;
    }


    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getnFix() {
        return nFix;
    }

    public void setnFix(int nFix) {
        this.nFix = nFix;
    }

    public int getnAuthors() {
        return nAuthors;
    }

    public void setnAuthors(int nAuthors) {
        this.nAuthors = nAuthors;
    }

    public double getAvgLocAdded() {
        return avgLocAdded;
    }

    public void setAvgLocAdded(double avgLocAdded) {
        this.avgLocAdded = avgLocAdded;
    }

    public int getChurn() {
        return churn;
    }

    public void setChurn(int churn) {
        this.churn = churn;
    }

    public int getAvgChurn() {
        return avgChurn;
    }

    public void setAvgChurn(int avgChurn) {
        this.avgChurn = avgChurn;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public double getAvgTimeFix() {
        return avgTimeFix;
    }

    public void setAvgTimeFix(double avgTimeFix) {
        this.avgTimeFix = avgTimeFix;
    }

    public int getFanOut() {
        return fanOut;
    }

    public void setFanOut(int fanOut) {
        this.fanOut = fanOut;
    }

    public int getIndPaths() {
        return indPaths;
    }

    public void setIndPaths(int indPaths) {
        this.indPaths = indPaths;
    }
}
