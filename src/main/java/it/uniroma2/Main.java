package it.uniroma2;

import it.uniroma2.controller.ExecuteDataCollection;

public class Main {
    public static void main(String[] args) {
        new ExecuteDataCollection("bookkeeper").collectData();
    }

}