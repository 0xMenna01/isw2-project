package it.uniroma2.model.weka;

import it.uniroma2.enums.ProjectKey;

public class ClassifierMeta {

    private ProjectKey proj;
    private int walkForwardIterationIndex;
    private double trainingPercent;
    private ClassifierMethod method;
    
    
    public ClassifierMeta(ProjectKey proj, int walkForwardIterationIndex, double trainingPercent,
            ClassifierMethod method) {
        this.proj = proj;
        this.walkForwardIterationIndex = walkForwardIterationIndex;
        this.trainingPercent = trainingPercent;
        this.method = method;
    }


    public ProjectKey getProj() {
        return proj;
    }


    public int getWalkForwardIterationIndex() {
        return walkForwardIterationIndex;
    }


    public double getTrainingPercent() {
        return trainingPercent;
    }


    public ClassifierMethod getMethod() {
        return method;
    }

    

    

}
