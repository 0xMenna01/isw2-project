package it.uniroma2;

import it.uniroma2.controller.ExecuteDataCollection;

public class Main {
    public static void main(String[] args) throws Exception {

        new ExecuteDataCollection("BOOKKEEPER", "https://github.com/0xMenna01/bookkeeper.git").collectData();
        
        new ExecuteDataCollection("SYNCOPE", "https://github.com/0xMenna01/syncope.git").collectData();
    }

}