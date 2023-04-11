package it.uniroma2.model.javaclass;

public class ClassMeta {

    private String pathName;
    private ClassMetrics metrics;
    private boolean isBuggy;

    public ClassMeta(String pathName, ClassMetrics metrics) {
        this.pathName = pathName;
        this.metrics = metrics;
        this.isBuggy = false;
    }

    public String getPathName() {
        return pathName;
    }

    public void setPathName(String pathName) {
        this.pathName = pathName;
    }

    public ClassMetrics getMetrics() {
        return metrics;
    }

    public void setMetrics(ClassMetrics metrics) {
        this.metrics = metrics;
    }

    public boolean isBuggy() {
        return isBuggy;
    }

    public void setBuggy(boolean buggy) {
        isBuggy = buggy;
    }
}
