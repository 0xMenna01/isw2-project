package it.uniroma2.controller;

import java.util.List;

import it.uniroma2.model.ReleaseMeta;

public class ExecuteDataCollection {
    
    //Class with only static methods does not have to be instanciated
    private ExecuteDataCollection(){
        throw new IllegalStateException("The class does not have to be instanciated");
    }

    public static void collectData(String projName) {
        CollectJiraData collectJiraInfo = new CollectJiraData(projName);
        List<ReleaseMeta> releasesList = null;
        try {
            releasesList = collectJiraInfo.retrieveReleasesMeta();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
