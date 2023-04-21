package it.uniroma2;

import it.uniroma2.controller.ExecuteDataCollection;

public class Main {
    public static void main(String[] args) {

        new ExecuteDataCollection("BOOKKEEPER", "https://github.com/0xMenna01/bookkeeper.git").collectData();
    }

}