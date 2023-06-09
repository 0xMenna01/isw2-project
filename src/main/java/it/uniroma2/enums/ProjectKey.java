package it.uniroma2.enums;

import it.uniroma2.exception.ProjectNameException;

public enum ProjectKey {

    BOOKEEPER("BOOKKEEPER"),
    SYNCOPE("SYNCOPE"),
    AVRO("AVRO"),
    FALCON("FALCON"),
    OPENJPA("OPENJPA"),
    ZOOKEEPER("ZOOKEEPER"),
    STORM("STORM");

    private final String projKey;

    private ProjectKey(String projKey) {
        this.projKey = projKey;
    }

    public static ProjectKey fromString(String projKey) throws ProjectNameException {
        for (ProjectKey key : values()) {
            if (key.toString().equals(projKey)) {
                return key;
            }
        }
        throw new ProjectNameException("Invalid name for the project");
    }

    @Override
    public String toString() {
        return projKey;
    }

    public ColdStartState getColdStartState() {
        ColdStartState state = ColdStartState.INACTIVE;
        if (this != BOOKEEPER && this != SYNCOPE)
            state = ColdStartState.EXECUTING;

        return state;
    }
}
