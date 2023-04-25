package it.uniroma2.model.javaclass;

public class ClassMeta extends ClassMetrics {

    protected String pathName;
    protected boolean isBuggy;

    public ClassMeta(String pathName) {
        this.pathName = pathName;
        this.isBuggy = false;
    }

    public String getPathName() {
        return pathName;
    }

    public void setPathName(String pathName) {
        this.pathName = pathName;
    }

    public boolean isBuggy() {
        return isBuggy;
    }

    public void setBuggy(boolean isBuggy) {
        this.isBuggy = isBuggy;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setnFix(int nFix) {
        this.nFix = nFix;
    }

    public void setnAuthors(int nAuthors) {
        this.nAuthors = nAuthors;
    }

    public void setAvgLocAdded(double avgLocAdded) {
        this.avgLocAdded = avgLocAdded;
    }

    public void setChurn(int churn) {
        this.churn = churn;
    }

    public void setAvgChurn(double avgChurn) {
        this.avgChurn = avgChurn;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setAvgTimeFix(double avgTimeFix) {
        this.avgTimeFix = avgTimeFix;
    }

    public void setFanOut(int fanOut) {
        this.fanOut = fanOut;
    }

    public void setChangeSetSize(int changeSetSize) {
        this.changeSetSize = changeSetSize;
    }

    


}
