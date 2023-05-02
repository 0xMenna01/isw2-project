package it.uniroma2.enums;

import it.uniroma2.exception.EnumException;

public enum ClassifierName {
    RANDOM_FOREST("Random Forest"),
    NAIVE_BAYES("Naive Bayes"),
    IBK("IBk");

    private final String className;
    
    private ClassifierName(String className) {
        this.className = className;
    }

    public static ClassifierName fromString(String className) throws EnumException {
        for (ClassifierName key : values()) {
            if (key.toString().equals(className)) {
                return key;
            }
        }
        throw new EnumException("Invalid name for the project");
    }

    @Override
    public String toString() {
        return className;
    }

}
