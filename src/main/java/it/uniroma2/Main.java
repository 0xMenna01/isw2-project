package it.uniroma2;

import it.uniroma2.controller.ExecuteDataCollection;
import it.uniroma2.exception.ProjectNameException;

//Remember to remove all print statements before submitting
// and delete the MainView class

public class Main {
    public static void main(String[] args) throws ProjectNameException, Exception {

        new ExecuteDataCollection("BOOKKEEPER", "https://github.com/0xMenna01/bookkeeper.git").collectData();
    }
}