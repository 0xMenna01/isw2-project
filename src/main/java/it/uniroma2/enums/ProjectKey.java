package it.uniroma2.enums;

import it.uniroma2.exception.ProjectNameException;

public enum ProjectKey {

    BOOKEEPER("BOOKKEEPER"),
    SYNCOPE("SYNCOPE"),
    FALCON("FALCON"),
    IVY("IVY"),
    OPENJPA("OPENJPA"),
    STORM("STORM"),
    TAJO("TAJO");

    private final String projKey;

    private ProjectKey(String projKey) {
        this.projKey = projKey;
    }

    public static ProjectKey fromString(String projKey) throws ProjectNameException {
        for (ProjectKey key : values()) {
            if (key.toString() == projKey) {
                return key;
            }
        }
        throw new ProjectNameException("Invalid name for the project");
    }

    public String toString() {
        return projKey;
    }

    public void setColdStartState(ColdStartState state) {
        if (this == BOOKEEPER || this == SYNCOPE) {
            state = ColdStartState.INACTIVE;
        } else {
            state = ColdStartState.EXECUTING;
        }
    }
}
